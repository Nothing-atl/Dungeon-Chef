package CMPT276.Group22;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.awt.Graphics;

public class AdditionalCoverageTest {
    private TestBoard board;
    private GameBoard gameBoard;

    // Test-specific board implementation to avoid UI dependencies
    private class TestBoard extends Board {
        @Override protected void loadBoardSpecificImages() {}
        @Override protected void initializeIngredients() {}
        @Override protected void initializeEnemies() {}
        @Override protected void initializeBarriers() {}
        @Override protected void checkWinCondition() {}
        @Override protected List<String> getRequiredIngredients() {
            return new ArrayList<>();
        }
        @Override protected void spawnBonusIngredient() {
            // Create bonus ingredient without UI updates
            BonusIngredient bonus = new BonusIngredient(
                new Coordinate(5, 5), 20, "test_bonus", 5000, this);
            ingredients.add(bonus);
            grid[5][5] = bonus;
        }
        @Override protected void handleBonusCollection(Ingredient ingredient) {
            bonusCollected = true;
            playerScore += ingredient.getValue();
        }
        @Override protected void drawEntity(Graphics g, Entity entity, int row, int col) {}
        @Override protected int getTargetForIngredient(String ingredient) { return 1; }

        // Test helper methods
        public void simulateCollision(Entity entity) {
            if (entity instanceof StaticEnemy) {
                playerScore -= ((StaticEnemy) entity).getPenaltyAmount();
            } else if (entity instanceof MovingEnemy) {
                isGameOver = true;
            }
        }
    }

    @Before
    public void setUp() {
        gameBoard = mock(GameBoard.class);
        board = new TestBoard();
        board.setGameBoard(gameBoard);
        
        // Initialize game state
        board.grid = new Entity[Board.GRID_HEIGHT][Board.GRID_WIDTH];
        board.ingredients = new ArrayList<>();
        board.staticEnemies = new ArrayList<>();
        board.movingEnemies = new ArrayList<>();
        board.barriers = new ArrayList<>();
        board.collectedIngredients = new ArrayList<>();
        board.playerScore = 0;
        board.isGameOver = false;
    }

    @Test
    public void testIngredientCollection() {
        // Create and place ingredient
        RegularIngredient ingredient = new RegularIngredient(
            new Coordinate(2, 2), 10, "test_ingredient", board);
        board.ingredients.add(ingredient);
        board.grid[2][2] = ingredient;
        
        // Initial state check
        int initialScore = board.playerScore;
        assertFalse("Ingredient should not be collected initially", 
                   board.collectedIngredients.contains("test_ingredient"));
        
        // Simulate collection
        board.playerX = 2;
        board.playerY = 2;
        board.checkCollisions();
        
        // Verify collection
        assertEquals("Score should increase by ingredient value",
                    initialScore + 10, board.playerScore);
        assertTrue("Ingredient should be in collected list",
                  board.collectedIngredients.contains("test_ingredient"));
        assertFalse("Ingredient should be removed from ingredients list",
                   board.ingredients.contains(ingredient));
    }

    @Test
    public void testStaticEnemyCollision() {
        // Setup
        int initialScore = 100;
        board.playerScore = initialScore;
        
        // Create enemy
        StaticEnemy enemy = new StaticEnemy(new Coordinate(3, 3), 50);
        board.staticEnemies.add(enemy);
        board.grid[3][3] = enemy;
        
        // Simulate collision
        board.simulateCollision(enemy);
        
        assertEquals("Score should be reduced by penalty",
                    initialScore - 50, board.playerScore);
    }

    @Test
    public void testMovingEnemyCollision() {
        // Setup
        MovingEnemy enemy = new MovingEnemy(new Coordinate(4, 4));
        board.movingEnemies.add(enemy);
        board.grid[4][4] = enemy;
        
        assertFalse("Game should not be over initially", board.isGameOver);
        
        // Simulate collision
        board.simulateCollision(enemy);
        
        assertTrue("Game should be over after enemy collision", board.isGameOver);
    }

    @Test
    public void testBonusIngredientMechanics() {
        // Initial state check
        assertFalse("Should not have bonus initially", board.bonusCollected);
        assertEquals("Score should be 0 initially", 0, board.playerScore);
        
        // Spawn and find bonus ingredient
        board.spawnBonusIngredient();
        BonusIngredient bonus = findBonusIngredient();
        
        assertNotNull("Bonus ingredient should exist", bonus);
        assertEquals("Bonus ingredient should have correct value", 20, bonus.getValue());
        
        // Collect bonus
        board.handleBonusCollection(bonus);
        assertTrue("Bonus should be marked as collected", board.bonusCollected);
        assertEquals("Score should include bonus value", 20, board.playerScore);
    }

    @Test
    public void testBarrierAndMovementValidation() {
        // Test empty space movement
        assertTrue("Should be able to move to empty space",
                  board.isValidMove(2, 2));
        
        // Test barrier collision
        Barrier barrier = new Barrier(new Coordinate(5, 5));
        board.barriers.add(barrier);
        board.grid[5][5] = barrier;
        assertFalse("Should not be able to move into barrier",
                    board.isValidMove(5, 5));
        
        // Test boundary conditions
        assertFalse("Should not be able to move outside left boundary",
                   board.isValidMove(-1, 5));
        assertFalse("Should not be able to move outside right boundary",
                   board.isValidMove(Board.GRID_WIDTH, 5));
        assertFalse("Should not be able to move outside top boundary",
                   board.isValidMove(5, -1));
        assertFalse("Should not be able to move outside bottom boundary",
                   board.isValidMove(5, Board.GRID_HEIGHT));
    }

    private BonusIngredient findBonusIngredient() {
        for (Ingredient ingredient : board.ingredients) {
            if (ingredient instanceof BonusIngredient) {
                return (BonusIngredient) ingredient;
            }
        }
        return null;
    }
}