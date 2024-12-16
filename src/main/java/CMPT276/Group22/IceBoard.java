package CMPT276.Group22;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Ice-themed game board implementation.
 * Features frozen treats as ingredients (banana, ice cream, cherry, whip cream).
 * Includes slippery movement mechanics and ice-themed obstacles.
 */
public class IceBoard extends Board {
    public static final int BANANA_TARGET = 2;
    public static final int ICE_CREAM_TARGET = 3;
    public static final int CHERRY_TARGET = 3;
    public static final int WHIP_CREAM_TARGET = 1;
    
    private boolean bonusMilkshakeCollected = false;

    /**
     * Loads and initializes ice-themed game assets.
     * Sets up board-specific images for entities and background.
     */
    @Override
    protected void loadBoardSpecificImages() {
        loadImage("background", "/assets/ice/background.png");
        loadImage("wall", "/assets/ice/wall.jpg");
        loadImage("barrier", "/assets/ice/wall.jpg");
        loadImage("staticEnemy", "/assets/ice/enemy.png");
        loadImage("movingEnemy", "/assets/ice/enemy.png");
        loadImage("regularIngredient_icecream", "/assets/ice/ice-cream.png");
        loadImage("regularIngredient_cherry", "/assets/ice/cherry.png");
        loadImage("regularIngredient_banana", "/assets/ice/banana.png");
        loadImage("regularIngredient_whipcream", "/assets/ice/whip-cream.png");
        loadImage("bonusIngredient", "/assets/ice/milkshake.png");
        loadImage("player", "/assets/ice/player.png");
    }

    /**
     * Places initial ingredients on the board.
     * Spawns ice cream, cherry, banana, and whip cream at random locations.
     */
    @Override
    protected void initializeIngredients() {
        // Initialize Ice (2 needed)
        addRegularIngredients("icecream", ICE_CREAM_TARGET, 4); // Value of 5

        // Initialize Mint (3 needed)
        addRegularIngredients("whipcream", WHIP_CREAM_TARGET, 7); // Value of 7

        addRegularIngredients("banana", BANANA_TARGET, 5); // Value of 7

        // Initialize Sugar (1 needed)
        addRegularIngredients("cherry", CHERRY_TARGET, 3); // Value of 10
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
        return Arrays.asList("icecream", "whipcream", "banana", "cherry");
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

            StaticEnemy enemy = new StaticEnemy(new Coordinate(y, x), 15); // Penalty of 15
            staticEnemies.add(enemy);
            grid[y][x] = enemy;
        }

        // Initialize moving enemies
        int numMovingEnemies = 2;
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

        BonusIngredient bonus = new BonusIngredient(new Coordinate(y, x), bonusValue, "milkshake", bonusDuration, this);
        ingredients.add(bonus);
        grid[y][x] = bonus;
        repaint();
    }

     /**
     * Processes milkshake bonus collection.
     * Updates score and disables further bonus spawns.
     * @param ingredient the collected bonus ingredient
     */
    @Override
    protected void handleBonusCollection(Ingredient ingredient) {
        if (ingredient instanceof BonusIngredient && !bonusCollected) {
            bonusCollected = true;
            bonusMilkshakeCollected = true;
            playerScore += ingredient.getValue();
            if (bonusSpawnTimer != null) {
                bonusSpawnTimer.stop();
            }
        }
    }

    @Override
    protected void initializeBarriers() {
        // Initialize barriers specific to the Ice level
        int numBarriers = 12; // Adjust as needed
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

        boolean hasEnoughBanana = collectedCounts.getOrDefault("banana", 0) >= BANANA_TARGET;
        boolean hasEnoughIceCream = collectedCounts.getOrDefault("icecream", 0) >= ICE_CREAM_TARGET;
        boolean hasEnoughCherry = collectedCounts.getOrDefault("cherry", 0) >= CHERRY_TARGET;
        boolean hasEnoughWhipCream = collectedCounts.getOrDefault("whipcream", 0) >= WHIP_CREAM_TARGET;

        if (hasEnoughBanana && hasEnoughIceCream && hasEnoughCherry && hasEnoughWhipCream) {
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
            bonusMilkshakeCollected = true;
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
            case "banana": return BANANA_TARGET;
            case "icecream": return ICE_CREAM_TARGET;
            case "cherry": return CHERRY_TARGET;
            case "whipcream": return WHIP_CREAM_TARGET;
            default: return 0;
        }
    }

    // Getters for score display
    public int getBananaCollected() { return getIngredientCount("banana"); }
    public int getIceCreamCollected() { return getIngredientCount("icecream"); }
    public int getCherryCollected() { return getIngredientCount("cherry"); }
    public int getWhipCreamCollected() { return getIngredientCount("whipcream"); }
    public boolean hasBonusMilkshake() { return bonusMilkshakeCollected; }
}
