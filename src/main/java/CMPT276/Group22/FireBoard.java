package CMPT276.Group22;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.SwingUtilities;

/**
 * Fire-themed game board implementation.
 * Features specific ingredients (broth, chili, meat) and unique enemy behaviors.
 * Players must collect ingredients while avoiding enemies in a heated environment.
 */
public class FireBoard extends Board {
    public static final int CHILI_TARGET = 3;
    public static final int BROTH_TARGET = 1;
    public static final int MEAT_TARGET = 5;
    
    private boolean bonusIceTeaCollected = false;

    /**
     * Loads and initializes fire-themed game assets.
     * Sets up board-specific images for entities and background.
     */
    @Override
    protected void loadBoardSpecificImages() {
        loadImage("background", "/assets/fire/background.png");
        loadImage("wall", "/assets/fire/wall.jpg");
        loadImage("barrier", "/assets/fire/wall.jpg");
        loadImage("staticEnemy", "/assets/fire/enemy.png");
        loadImage("movingEnemy", "/assets/fire/enemy.png");
        loadImage("regularIngredient_broth", "/assets/fire/broth.png");
        loadImage("regularIngredient_chili", "/assets/fire/chili.png");
        loadImage("regularIngredient_meat", "/assets/fire/meat.png");
        loadImage("bonusIngredient", "/assets/fire/ice-tea.png");
        loadImage("player", "/assets/fire/player.png");
    }

    /**
     * Places initial ingredients on the board.
     * Spawns broth, chili, and meat at random valid locations.
     */
    @Override
    protected void initializeIngredients() {
        // Initialize broth (1 needed)
        addRegularIngredients("broth", BROTH_TARGET, 10); // Value of 10

        // Initialize chili (3 needed)
        addRegularIngredients("chili", CHILI_TARGET, 5); // Value of 5

        // Initialize meat (5 needed)
        addRegularIngredients("meat", MEAT_TARGET, 7); // Value of 7
    }

    private void addRegularIngredients(String name, int quantity, int value) {
        for (int i = 0; i < quantity; i++) {
            int x, y;
            do {
                x = new Random().nextInt(GRID_WIDTH);
                y = new Random().nextInt(GRID_HEIGHT);
            } while (grid[y][x] != null || isPlayerPosition(x, y));
    
            RegularIngredient ingredient = new RegularIngredient(new Coordinate(y, x), value, name, this);
            ingredients.add(ingredient);
            grid[y][x] = ingredient;
        }
    }
    

    @Override
    protected List<String> getRequiredIngredients() {
        return Arrays.asList("broth", "chili", "meat");
    }

    @Override
    protected void initializeEnemies() {
        // Initialize static enemies
        int numStaticEnemies = 4;
        for (int i = 0; i < numStaticEnemies; i++) {
            int x, y;
            do {
                x = new Random().nextInt(GRID_WIDTH);
                y = new Random().nextInt(GRID_HEIGHT);
            } while (grid[y][x] != null || isPlayerPosition(x, y));

            StaticEnemy enemy = new StaticEnemy(new Coordinate(y, x), 10); // Penalty of 10
            staticEnemies.add(enemy);
            grid[y][x] = enemy;
        }

        // Initialize moving enemies
        int numMovingEnemies = 1;
        for (int i = 0; i < numMovingEnemies; i++) {
            int x, y;
            do {
                x = new Random().nextInt(GRID_WIDTH);
                y = new Random().nextInt(GRID_HEIGHT);
            } while (grid[y][x] != null || isPlayerPosition(x, y));

            MovingEnemy enemy = new MovingEnemy(new Coordinate(y, x));
            movingEnemies.add(enemy);
            grid[y][x] = enemy;
        }
    }

    @Override
    protected void spawnBonusIngredient() {
        if (isGameOver || isGameWon || bonusCollected) {
            if (bonusSpawnTimer != null) {
                bonusSpawnTimer.stop();
            }
            return;
        }

        int x, y;
        do {
            x = new Random().nextInt(GRID_WIDTH);
            y = new Random().nextInt(GRID_HEIGHT);
        } while (grid[y][x] != null);

        int bonusValue = 20;
        int bonusDuration = 5000;

        // Play spawn sound effect
        SoundManager.getInstance().playSound("bonus_spawned");

        BonusIngredient bonus = new BonusIngredient(new Coordinate(y, x), bonusValue, "ice-tea", bonusDuration, this);
        ingredients.add(bonus);
        grid[y][x] = bonus;
        repaint();
    }

    /**
     * Processes bonus ice tea collection.
     * Updates score and disables further bonus spawns.
     * @param ingredient the collected bonus ingredient
     */
    @Override
    protected void handleBonusCollection(Ingredient ingredient) {
        if (ingredient instanceof BonusIngredient && !bonusCollected) {
            bonusCollected = true;
            bonusIceTeaCollected = true;
            playerScore += ingredient.getValue();
            if (bonusSpawnTimer != null) {
                bonusSpawnTimer.stop();
            }
        }
    }

    @Override
    protected void initializeBarriers() {
        // Initialize barriers specific to the Fire level
        int numBarriers = 30; // Adjust as needed
        for (int i = 0; i < numBarriers; i++) {
            int x, y;
            do {
                x = new Random().nextInt(GRID_WIDTH);
                y = new Random().nextInt(GRID_HEIGHT);
            } while (grid[y][x] != null || isPlayerPosition(x, y));

            Barrier barrier = new Barrier(new Coordinate(y, x));
            barriers.add(barrier);
            grid[y][x] = barrier;
        }
    }

    @Override
    protected void checkWinCondition() {
        // Count collected ingredients
        Map<String, Integer> collectedCounts = new HashMap<>();
        for (String ingredient : collectedIngredients) {
            collectedCounts.merge(ingredient, 1, Integer::sum);
        }

        // Check if we have enough of each required ingredient
        boolean hasEnoughBroth = collectedCounts.getOrDefault("broth", 0) >= BROTH_TARGET;
        boolean hasEnoughChili = collectedCounts.getOrDefault("chili", 0) >= CHILI_TARGET;
        boolean hasEnoughMeat = collectedCounts.getOrDefault("meat", 0) >= MEAT_TARGET;

        if (hasEnoughBroth && hasEnoughChili && hasEnoughMeat) {
            isGameOver = true;
            isGameWon = true;
            // Stop enemy movement and bonus spawning
            if (enemyTimer != null) {
                enemyTimer.stop();
            }
            if (bonusSpawnTimer != null) {
                bonusSpawnTimer.stop();
            }
            SoundManager.getInstance().playSound("win");
            if (gameBoard != null) {
                SwingUtilities.invokeLater(() -> {
                    gameBoard.showGameOverDialog();
                });
            }
        }
    }

    private boolean isPlayerPosition(int x, int y) {
        return x == playerX && y == playerY;
    }

    @Override
    protected void collectIngredient(Ingredient ingredient) {
        playerScore += ingredient.getValue();
        if (ingredient instanceof BonusIngredient) {
            bonusIceTeaCollected = true;
        } else {
            incrementIngredientCount(ingredient.getName());
        }
        ingredients.remove(ingredient);
        grid[ingredient.getPosition().getRow()][ingredient.getPosition().getCol()] = null;
    }

    public int getIngredientCount(String ingredient) {
        return ingredientCounts.getOrDefault(ingredient.toLowerCase(), 0);
    }

    @Override
    protected int getTargetForIngredient(String ingredient) {
        switch (ingredient.toLowerCase()) {
            case "broth": return BROTH_TARGET;
            case "chili": return CHILI_TARGET;
            case "meat": return MEAT_TARGET;
            default: return 0;
        }
    }

    //Getters for score display
    public int getChiliCollected() { return getIngredientCount("chili"); }
    public int getBrothCollected() { return getIngredientCount("broth"); }
    public int getMeatCollected() { return getIngredientCount("meat"); }
    public boolean hasBonusIceTea() { return bonusIceTeaCollected; }
}