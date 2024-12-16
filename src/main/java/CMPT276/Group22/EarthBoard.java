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
 * Earth-themed game board implementation.
 * Features vegetable ingredients (dal, potato, carrot, onion, drumstick).
 * Includes nature-themed obstacles and earthy environment challenges.
 */
public class EarthBoard extends Board {
    public static final int DAL_TARGET = 2;
    public static final int POTATO_TARGET = 3;
    public static final int CARROT_TARGET = 4;
    public static final int ONION_TARGET = 2;

    private boolean bonusRiceCollected = false;

    /**
     * Loads and initializes earth-themed game assets.
     * Sets up board-specific images for entities and background.
     */
    @Override
    protected void loadBoardSpecificImages() {
        loadImage("background", "/assets/earth/background.png");
        loadImage("wall", "/assets/earth/wall.jpg");
        loadImage("barrier", "/assets/earth/wall.jpg");
        loadImage("staticEnemy", "/assets/earth/enemy.png");
        loadImage("movingEnemy", "/assets/earth/enemy.png");
        loadImage("regularIngredient_dal", "/assets/earth/dal.png");
        loadImage("regularIngredient_potato", "/assets/earth/potato.png");
        loadImage("regularIngredient_carrot", "/assets/earth/carrot.png");
        loadImage("regularIngredient_onion", "/assets/earth/onion.png");
        //loadImage("regularIngredient_drumstick", "/assets/earth/drumstick.png");
        loadImage("bonusIngredient", "/assets/earth/rice.png");
        loadImage("player", "/assets/earth/player.png");
    }

    /**
     * Places initial ingredients on the board.
     * Spawns dal, potato, carrot, onion, and drumstick at random locations.
     */
    @Override
    protected void initializeIngredients() {
        // Initialize Herbs (3 needed)
        addRegularIngredients("dal", DAL_TARGET, 6); // Value of 6

        // Initialize Mushrooms (2 needed)
        addRegularIngredients("potato", POTATO_TARGET, 2); // Value of 8

        // Initialize Roots (4 needed)
        addRegularIngredients("carrot", CARROT_TARGET, 4); // Value of 5

        addRegularIngredients("onion", ONION_TARGET, 5);

        //addRegularIngredients("drumstick", DRUMSTICK_TARGET, 5);
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
        return Arrays.asList("dal", "potato", "carrot", "onion");
    }


    @Override
    protected void initializeEnemies() {
        // Initialize static enemies
        int numStaticEnemies = 6;
        for (int i = 0; i < numStaticEnemies; i++) {
            int x, y;
            do {
                x = new Random().nextInt(GRID_WIDTH);
                y = new Random().nextInt(GRID_HEIGHT);
            } while (grid[y][x] != null || isPlayerPosition(x, y));

            StaticEnemy enemy = new StaticEnemy(new Coordinate(y, x), 12); // Penalty of 12
            staticEnemies.add(enemy);
            grid[y][x] = enemy;
        }

        // Initialize moving enemies
        int numMovingEnemies = 3;
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

        BonusIngredient bonus = new BonusIngredient(new Coordinate(y, x), bonusValue, "rice", bonusDuration, this);
        ingredients.add(bonus);
        grid[y][x] = bonus;
        repaint();
    }
    
    /**
     * Processes rice bonus collection.
     * Updates score and disables further bonus spawns.
     * @param ingredient the collected bonus ingredient
     */
    @Override
    protected void handleBonusCollection(Ingredient ingredient) {
        if (ingredient instanceof BonusIngredient && !bonusCollected) {
            bonusCollected = true;
            bonusRiceCollected = true;
            playerScore += ingredient.getValue();
            if (bonusSpawnTimer != null) {
                bonusSpawnTimer.stop();
            }
        }
    }

    @Override
    protected void initializeBarriers() {
        // Initialize barriers specific to the Earth level
        int numBarriers = 18; // Adjust as needed
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
        Map<String, Integer> collectedCounts = new HashMap<>();
        for (String ingredient : collectedIngredients) {
            collectedCounts.merge(ingredient, 1, Integer::sum);
        }

        boolean hasEnoughDal = collectedCounts.getOrDefault("dal", 0) >= DAL_TARGET;
        boolean hasEnoughPotato = collectedCounts.getOrDefault("potato", 0) >= POTATO_TARGET;
        boolean hasEnoughCarrot = collectedCounts.getOrDefault("carrot", 0) >= CARROT_TARGET;
        boolean hasEnoughOnion = collectedCounts.getOrDefault("onion", 0) >= ONION_TARGET;
        //boolean hasEnoughDrumstick = collectedCounts.getOrDefault("drumstick", 0) >= DRUMSTICK_TARGET;

        if (hasEnoughDal && hasEnoughPotato && hasEnoughCarrot && hasEnoughOnion) {
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
            bonusRiceCollected = true;
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
            case "dal": return DAL_TARGET;
            case "potato": return POTATO_TARGET;
            case "carrot": return CARROT_TARGET;
            case "onion": return ONION_TARGET;
            default: return 0;
        }
    }

    public int getDalCollected() { return getIngredientCount("dal"); }
    public int getPotatoCollected() { return getIngredientCount("potato"); }
    public int getCarrotCollected() { return getIngredientCount("carrot"); }
    public int getOnionCollected() { return getIngredientCount("onion"); }
    public boolean hasBonusRice() { return bonusRiceCollected; }

}