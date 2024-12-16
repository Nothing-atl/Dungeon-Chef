package CMPT276.Group22;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.awt.event.KeyEvent;

import java.util.List;
import java.util.Arrays;
import java.awt.Graphics;

public class BoardTest {
    
    // Concrete implementation of Board for testing
    private class TestBoard extends Board {
        @Override
        protected void loadBoardSpecificImages() {}
        
        @Override
        protected void initializeIngredients() {}
        
        @Override
        protected void initializeEnemies() {}
        
        @Override
        protected void initializeBarriers() {}
        
        @Override
        protected void checkWinCondition() {}
        
        @Override
        protected List<String> getRequiredIngredients() {
            return Arrays.asList("test_ingredient");
        }
        
        @Override
        protected void spawnBonusIngredient() {}
        
        @Override
        protected void handleBonusCollection(Ingredient ingredient) {}
        
        @Override
        protected void drawEntity(Graphics g, Entity entity, int row, int col) {}
        
        @Override
        protected int getTargetForIngredient(String ingredient) {
            return 1;
        }
    }
    
    private TestBoard board;
    
    @Before
    public void setUp() {
        board = new TestBoard();
    }
    
    @Test
    public void testInitialPlayerPosition() {
        assertEquals("Initial player X position should be 1", 1, board.playerX);
        assertEquals("Initial player Y position should be 1", 1, board.playerY);
    }
    
    @Test
    public void testInitialGameState() {
        assertFalse("Game should not be over initially", board.isGameOver);
        assertFalse("Game should not be won initially", board.isGameWon);
        assertFalse("Game should not be paused initially", board.isPaused);
        assertEquals("Initial score should be 0", 0, board.playerScore);
    }
    
    @Test
    public void testValidMove() {
        assertTrue("Move to empty space should be valid", 
                  board.isValidMove(2, 2));
        assertFalse("Move outside grid should be invalid", 
                    board.isValidMove(-1, 1));
        assertFalse("Move outside grid should be invalid", 
                    board.isValidMove(board.GRID_WIDTH + 1, 1));
    }
    
    @Test
    public void testWallCollision() {
        // Walls are initialized on the borders
        assertFalse("Cannot move into wall", board.isValidMove(0, 0));
        assertFalse("Cannot move into wall", 
                    board.isValidMove(board.GRID_WIDTH - 1, board.GRID_HEIGHT - 1));
    }
    
    @Test
    public void testScoreIncrement() {
        int initialScore = board.playerScore;
        int increment = 10;
        board.increasePlayerScore(increment);
        assertEquals("Score should increase by correct amount", 
                    initialScore + increment, board.playerScore);
    }
    
    @Test
    public void testIngredientCollection() {
        String ingredientName = "test_ingredient";
        board.addCollectedIngredient(ingredientName);
        assertTrue("Ingredient should be in collected list", 
                  board.collectedIngredients.contains(ingredientName.toLowerCase()));
    }
    
    @Test
    public void testPauseGame() {
        board.isPaused = false;
        // Simulate pause key press
        KeyEvent pauseEvent = new KeyEvent(board, KeyEvent.KEY_PRESSED, 
                                         System.currentTimeMillis(), 0, 
                                         KeyEvent.VK_P, 'P');
        board.getKeyListeners()[0].keyPressed(pauseEvent);
        assertTrue("Game should be paused after pressing P", board.isPaused);
    }
    
    @Test
    public void testGridInitialization() {
        assertNotNull("Grid should be initialized", board.grid);
        assertEquals("Grid height should match constant", 
                    board.GRID_HEIGHT, board.grid.length);
        assertEquals("Grid width should match constant", 
                    board.GRID_WIDTH, board.grid[0].length);
    }
    
    @Test
    public void testCollectionInitialization() {
        assertNotNull("Ingredients list should be initialized", board.ingredients);
        assertNotNull("Static enemies list should be initialized", board.staticEnemies);
        assertNotNull("Moving enemies list should be initialized", board.movingEnemies);
        assertNotNull("Barriers list should be initialized", board.barriers);
        assertNotNull("Collected ingredients list should be initialized", 
                     board.collectedIngredients);
    }
}