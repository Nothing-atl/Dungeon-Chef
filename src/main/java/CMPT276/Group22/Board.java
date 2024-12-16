package CMPT276.Group22;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

/**
 * Abstract base class for game boards implementing core game mechanics.
 * Manages entity positioning, collision detection, and game loop functionality.
 */
public abstract class Board extends JPanel {
    protected static final int CELL_SIZE = 40; // Adjusted for visibility
    protected static final int GRID_WIDTH = 800 / CELL_SIZE;
    protected static final int GRID_HEIGHT = 600 / CELL_SIZE;

    protected int playerX, playerY;
    protected int playerHealth;
    protected Entity[][] grid;
    protected List<Ingredient> ingredients;
    protected List<String> collectedIngredients;
    protected List<Barrier> barriers;
    protected List<StaticEnemy> staticEnemies;
    protected List<MovingEnemy> movingEnemies;
    protected boolean isGameOver;
    protected boolean isGameWon;
    protected boolean isPaused;
    protected GameBoard gameBoard;
    protected HashMap<String, Image> images;
    protected javax.swing.Timer enemyTimer;
    protected int playerScore;
    protected javax.swing.Timer bonusSpawnTimer;
    protected boolean bonusCollected = false;
    protected Map<String, Integer> ingredientCounts = new HashMap<>();
    
    /**
     * Initializes a new game board with default size 800x600.
     * Sets up game state, collections for entities, and input handling.
     */
    public Board() {
        this.setPreferredSize(new Dimension(800, 600));
        ingredients = new ArrayList<>();
        staticEnemies = new ArrayList<>();
        movingEnemies = new ArrayList<>();
        barriers = new ArrayList<>();
        images = new HashMap<>();
        collectedIngredients = new ArrayList<>();
        playerScore = 0;
        initializeBoard();
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow(); // Ensure the board has focus to receive key events
        setupKeyListener();
    }

    private void initializeBoard() {
        initializeGrid();
        loadBoardSpecificImages();
        initializePlayer();
        initializeWalls();
        initializeIngredients();
        initializeEnemies();
        initializeEnemyTimer();
        initializeBonusIngredientSpawner();
        initializeBarriers();
    }
    
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    protected void initializeGrid() {
        grid = new Entity[GRID_HEIGHT][GRID_WIDTH];
    }

    protected void initializePlayer() {
        playerX = 1;
        playerY = 1;
        playerHealth = 3; // Example starting health
    }

    protected void initializeWalls() {
        // Add walls around the border
        for (int i = 0; i < GRID_WIDTH; i++) {
            grid[0][i] = new Wall(new Coordinate(0, i));
            grid[GRID_HEIGHT - 1][i] = new Wall(new Coordinate(GRID_HEIGHT - 1, i));
        }
        for (int i = 0; i < GRID_HEIGHT; i++) {
            grid[i][0] = new Wall(new Coordinate(i, 0));
            grid[i][GRID_WIDTH - 1] = new Wall(new Coordinate(i, GRID_WIDTH - 1));
        }
    }

    /**
     * Processes movement of enemy entities towards the player.
     * Enemies move one cell per tick in the direction that minimizes distance to player.
     * Checks for collisions with player after movement.
     */
    private void moveEnemies() {
        if (isGameOver || isGameWon) {
            enemyTimer.stop();
            return;
        }

        for (MovingEnemy enemy : movingEnemies) {
            // Store old position
            int oldRow = enemy.getPosition().getRow();
            int oldCol = enemy.getPosition().getCol();
            
            // Clear old position temporarily
            grid[oldRow][oldCol] = null;
            
            // Get possible moves towards player
            List<Coordinate> possibleMoves = getPossibleMovesForEnemy(enemy, oldRow, oldCol);
            
            if (!possibleMoves.isEmpty()) {
                // Randomly select one of the possible moves that get closer to the player
                Random rand = new Random();
                Coordinate newPos = possibleMoves.get(rand.nextInt(possibleMoves.size()));
                enemy.setPosition(newPos);
                grid[newPos.getRow()][newPos.getCol()] = enemy;
            } else {
                // If no moves available, stay in current position
                enemy.setPosition(new Coordinate(oldRow, oldCol));
                grid[oldRow][oldCol] = enemy;
            }
    
            // Check for collision with player
            if (enemy.getPosition().equals(new Coordinate(playerY, playerX))) {
                handleEnemyCollision();
            }
        }
    }

    private List<Coordinate> getPossibleMovesForEnemy(MovingEnemy enemy, int currentRow, int currentCol) {
        List<Coordinate> possibleMoves = new ArrayList<>();
        int playerRow = playerY;
        int playerCol = playerX;
        
        // Check all four directions
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // up, down, left, right
        
        for (int[] dir : directions) {
            int newRow = currentRow + dir[0];
            int newCol = currentCol + dir[1];
            
            // Check if move is valid and gets closer to player
            if (isValidEnemyMove(newRow, newCol)) {
                // Calculate distances
                double currentDistance = getDistance(currentRow, currentCol, playerRow, playerCol);
                double newDistance = getDistance(newRow, newCol, playerRow, playerCol);
                
                // Only add moves that get closer to the player
                if (newDistance < currentDistance) {
                    possibleMoves.add(new Coordinate(newRow, newCol));
                }
            }
        }
        
        // If no moves get closer to player, consider all valid moves
        if (possibleMoves.isEmpty()) {
            for (int[] dir : directions) {
                int newRow = currentRow + dir[0];
                int newCol = currentCol + dir[1];
                if (isValidEnemyMove(newRow, newCol)) {
                    possibleMoves.add(new Coordinate(newRow, newCol));
                }
            }
        }
        
        return possibleMoves;
    }

    private double getDistance(int row1, int col1, int row2, int col2) {
        return Math.sqrt(Math.pow(row2 - row1, 2) + Math.pow(col2 - col1, 2));
    }

    /**
     * Checks for valid moves in the given direction for enemies.
     * @param row current row position
     * @param col current column position
     * @return true if the move is valid (no walls/barriers/other entities)
     */
    private boolean isValidEnemyMove(int row, int col) {
        return row >= 0 && row < GRID_HEIGHT &&
               col >= 0 && col < GRID_WIDTH &&
               !(grid[row][col] instanceof Wall) &&
               !(grid[row][col] instanceof Barrier) &&
               !(grid[row][col] instanceof StaticEnemy) &&
               !(grid[row][col] instanceof MovingEnemy) &&
               !(grid[row][col] instanceof Ingredient);
    }

    private void handleEnemyCollision() {
        SoundManager.getInstance().playSound("enemy_hit");
        isGameOver = true;
        if (gameBoard != null) {
            gameBoard.showGameOverDialog();
        }
    }

    private void stopGame() {
        // Stop enemy timer
        if (enemyTimer != null) {
            enemyTimer.stop();
        }
        // Stop bonus ingredient spawner
        if (bonusSpawnTimer != null) {
            bonusSpawnTimer.stop();
        }
        // Additional cleanup if necessary
    }

    private void initializeEnemyTimer() {
        enemyTimer = new javax.swing.Timer(500, e -> {
            if (!isPaused && !isGameOver) {  // Only move if not paused
                moveEnemies();
                repaint();
            }
        });
        enemyTimer.start();
    }
    

    protected void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    isPaused = !isPaused;
                    if (gameBoard != null) {
                        if (isPaused) {
                            gameBoard.pauseTimer();
                        } else {
                            gameBoard.resumeTimer();
                        }
                    }
                    return;
                }
                if (isGameOver || isPaused) return;

                int newX = playerX;
                int newY = playerY;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        newX--;
                        break;
                    case KeyEvent.VK_RIGHT:
                        newX++;
                        break;
                    case KeyEvent.VK_UP:
                        newY--;
                        break;
                    case KeyEvent.VK_DOWN:
                        newY++;
                        break;
                }

                if (isValidMove(newX, newY)) {
                    playerX = newX;
                    playerY = newY;
                    checkCollisions();
                    checkWinCondition();
                    if (gameBoard != null) {
                        gameBoard.updateScoreDisplay();
                    }
                    repaint();
                }
            }
        });
    }

    protected boolean isValidMove(int x, int y) {
        return x >= 0 && x < GRID_WIDTH &&
                y >= 0 && y < GRID_HEIGHT &&
                !(grid[y][x] instanceof Wall)&&
                !(grid[y][x] instanceof Barrier);
    }

    /**
     * Handles collision detection between player and game entities.
     * Processes ingredient collection and enemy encounters.
     * Updates score and checks win conditions.
     */
    protected void checkCollisions() {
        // Create safe copies of the collections to iterate over
        List<Ingredient> ingredientsCopy = new ArrayList<>(ingredients);
        List<StaticEnemy> staticEnemiesCopy = new ArrayList<>(staticEnemies);
        
        // Create lists to store items to be removed
        List<Ingredient> ingredientsToRemove = new ArrayList<>();
        List<StaticEnemy> enemiesToRemove = new ArrayList<>();
        
        // Check ingredient collisions
        for (Ingredient ingredient : ingredientsCopy) {
            if (ingredient.getPosition().equals(new Coordinate(playerY, playerX))) {
                // Play sound before handling collection
                if (ingredient instanceof BonusIngredient) {
                    SoundManager.getInstance().playSound("collect_bonus");
                } else {
                    SoundManager.getInstance().playSound("collect_ingredient");
                }
                ingredient.onCollected(this);
                ingredientsToRemove.add(ingredient);
                // Clear the grid cell
                grid[ingredient.getPosition().getRow()][ingredient.getPosition().getCol()] = null;
            }
        }
        
        // Check static enemy collisions
        for (StaticEnemy enemy : staticEnemiesCopy) {
            if (enemy.getPosition().equals(new Coordinate(playerY, playerX))) {
                SoundManager.getInstance().playSound("enemy_hit");  // Add this line
                applyPenalty(enemy);
                enemiesToRemove.add(enemy);
                // Clear the grid cell
                grid[enemy.getPosition().getRow()][enemy.getPosition().getCol()] = null;
            }
        }
        
        // Synchronize access to the original lists when removing items
        synchronized(this) {
            ingredients.removeAll(ingredientsToRemove);
            staticEnemies.removeAll(enemiesToRemove);
        }
        
        // Update ingredients panel
        if (gameBoard != null) {
            // Update ingredient counts display
            Map<String, Integer> collectedCounts = new HashMap<>();
            for (String ingredient : collectedIngredients) {
                collectedCounts.merge(ingredient, 1, Integer::sum);
            }
            gameBoard.updateIngredientDisplay(collectedCounts);
            
            // Update recipe visualization
            RecipeVisualizationPanel visPanel = gameBoard.getRecipeVisualizationPanel();
            if (visPanel != null) {
                // Check each ingredient type for completion
                List<String> requiredIngredients = getRequiredIngredients();
                for (String ingredient : requiredIngredients) {
                    int collected = collectedCounts.getOrDefault(ingredient.toLowerCase(), 0);
                    int target = getTargetForIngredient(ingredient);
                    visPanel.updateIngredientState(ingredient.toLowerCase(), collected >= target);
                }
                
                // Update bonus state
                visPanel.updateBonusState(bonusCollected);
            }
        }
        
        checkWinCondition();
    }

    protected abstract int getTargetForIngredient(String ingredient);
    

    private void applyPenalty(StaticEnemy enemy) {
        playerScore -= enemy.getPenaltyAmount();
        if (gameBoard != null) {
            gameBoard.updateScoreDisplay();
        }
        if (playerScore < 0) {
            isGameOver = true;
            if (gameBoard != null) {
                gameBoard.showGameOverDialog();
            }
        }
    }

    /**
     * Spawns a bonus ingredient at random location on the board.
     * Bonus ingredients have higher value but disappear after a time limit.
     * Only one bonus ingredient can exist at a time.
     */
    protected abstract void spawnBonusIngredient();
    protected abstract void handleBonusCollection(Ingredient ingredient);

    private void initializeBonusIngredientSpawner() {
        int bonusSpawnInterval = 10000; // Every 10 seconds
        bonusSpawnTimer = new javax.swing.Timer(bonusSpawnInterval, e -> {
            if (!isPaused && !isGameOver) {  // Only spawn if not paused
                spawnBonusIngredient();
            }
        });
        bonusSpawnTimer.start();
    }

    public void removeIngredientFromGrid(Ingredient ingredient) {
        grid[ingredient.getPosition().getRow()][ingredient.getPosition().getCol()] = null;
        repaint();
    }
    

    protected void loadImage(String key, String path) {
        try (InputStream imgStream = getClass().getResourceAsStream(path)) {
            if (imgStream != null) {
                Image img = ImageIO.read(imgStream);
                images.put(key, img);
                // System.out.println("Loaded image: " + path);
            } 
            // else {
            //     System.err.println("Failed to load image: " + path);
            // }
        } catch (IOException e) {
            System.err.println("IOException when loading image: " + path);
            e.printStackTrace();
        }
    }
    

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }

    protected void drawBoard(Graphics g) {
        // Draw background
        g.drawImage(images.get("background"), 0, 0, getWidth(), getHeight(), this);
    
        // Draw all grid entities (includes ingredients, enemies, barriers, walls)
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                if (grid[row][col] != null) {
                    drawEntity(g, grid[row][col], row, col);
                }
            }
        }
    
        // Draw player
        g.drawImage(images.get("player"),
                playerX * CELL_SIZE,
                playerY * CELL_SIZE,
                CELL_SIZE,
                CELL_SIZE,
                this);
    }

    protected abstract void initializeBarriers();
    

    protected abstract void loadBoardSpecificImages();
    protected abstract void initializeIngredients();
    protected abstract void initializeEnemies();
    
    /**
     * Checks if current game state satisfies win conditions.
     * Win conditions vary by board type and required ingredients.
     */
    protected abstract void checkWinCondition();

    protected abstract List<String> getRequiredIngredients();

    public synchronized void increasePlayerScore(int value) {
        playerScore += value;
    }    

    public synchronized void addCollectedIngredient(String ingredientName) {
        collectedIngredients.add(ingredientName.toLowerCase());
        updateIngredientProgress();
        checkWinCondition();
    }

    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
        grid[ingredient.getPosition().getRow()][ingredient.getPosition().getCol()] = null;
        repaint();
    }

    protected void collectIngredient(Ingredient ingredient) {
        playerScore += ingredient.getValue();
        
        if (ingredient instanceof BonusIngredient) {
            handleBonusCollection(ingredient);  // Make sure this is called
            RecipeVisualizationPanel visPanel = gameBoard.getRecipeVisualizationPanel();
            if (visPanel != null) {
                visPanel.updateBonusState(true);
            }
            SoundManager.getInstance().playSound("collect_bonus");
        } else {
            SoundManager.getInstance().playSound("collect_ingredient");
        }
        
        ingredients.remove(ingredient);
        grid[ingredient.getPosition().getRow()][ingredient.getPosition().getCol()] = null;
        repaint();
    }

    protected void updateIngredientProgress() {
        if (gameBoard != null) {
            Map<String, Integer> collectedCounts = new HashMap<>();
            for (String ingredient : collectedIngredients) {
                collectedCounts.merge(ingredient, 1, Integer::sum);
            }
            gameBoard.updateIngredientDisplay(collectedCounts);
        }
    }

    protected void drawEntity(Graphics g, Entity entity, int row, int col) {
        Image image = null;
        if (entity instanceof Wall) {
            image = images.get("wall");
        } else if (entity instanceof Barrier) {
            image = images.get("barrier");
        } else if (entity instanceof StaticEnemy) {
            image = images.get("staticEnemy");
        } else if (entity instanceof MovingEnemy) {
            image = images.get("movingEnemy");
        } else if (entity instanceof RegularIngredient) {
            String name = ((RegularIngredient) entity).getName();
            image = images.get("regularIngredient_" + name.toLowerCase());
        } else if (entity instanceof BonusIngredient) {
            image = images.get("bonusIngredient");
        }
    
        if (image != null) {
            g.drawImage(image, col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        } else {
            System.err.println("Image for entity is null at (" + col + ", " + row + ")");
        }
    }

    protected void incrementIngredientCount(String ingredient) {
        ingredientCounts.merge(ingredient.toLowerCase(), 1, Integer::sum);
    }

    protected int getIngredientCount(String ingredient) {
        return ingredientCounts.getOrDefault(ingredient.toLowerCase(), 0);
    }

}