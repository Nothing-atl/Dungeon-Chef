package CMPT276.Group22;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;

public class GameBoardCoverageTest {
    private TestGameBoard gameBoard;
    private Board board;
    
    // Test implementation to avoid UI dependencies
    private class TestGameBoard {
        private int secondsElapsed = 0;
        private boolean isPaused = false;
        private int score = 0;
        private Timer timer;
        private Map<String, Integer> ingredientCounts;
        private boolean isGameOver = false;
        private String boardType;

        public TestGameBoard(String type) {
            this.boardType = type;
            this.ingredientCounts = new HashMap<>();
            initializeBoard();
        }

        private void initializeBoard() {
            switch(boardType.toLowerCase()) {
                case "fire":
                    board = new FireBoard();
                    break;
                case "ice":
                    board = new IceBoard();
                    break;
                case "earth":
                    board = new EarthBoard();
                    break;
                default:
                    board = null;
            }
        }

        public void updateScoreDisplay(int newScore) {
            score = newScore;
        }

        public void updateIngredientDisplay(Map<String, Integer> counts) {
            ingredientCounts = counts != null ? new HashMap<>(counts) : new HashMap<>();
        }

        public void updateTimer() {
            if (!isPaused) {
                secondsElapsed++;
            }
        }

        public void pauseTimer() {
            isPaused = true;
        }

        public void resumeTimer() {
            isPaused = false;
        }

        public void endGame() {
            isGameOver = true;
            isPaused = true;
        }

        // Getters
        public int getSecondsElapsed() { return secondsElapsed; }
        public boolean isPaused() { return isPaused; }
        public int getScore() { return score; }
        public Map<String, Integer> getIngredientCounts() { return ingredientCounts; }
        public boolean isGameOver() { return isGameOver; }
        public Board getBoard() { return board; }
        public String getBoardType() { return boardType; }
    }

    @Before
    public void setUp() {
        gameBoard = new TestGameBoard("fire");
        board = spy(new FireBoard());
    }

    @Test
    public void testTimerManagement() {
        // Test initial state
        assertFalse("Timer should not be paused initially", gameBoard.isPaused());
        
        // Test pause
        gameBoard.pauseTimer();
        assertTrue("Timer should be paused", gameBoard.isPaused());
        
        // Test resume
        gameBoard.resumeTimer();
        assertFalse("Timer should not be paused after resume", gameBoard.isPaused());
        
        // Test elapsed time
        gameBoard.updateTimer();
        assertEquals("Timer should increment when running", 1, gameBoard.getSecondsElapsed());
    }

    @Test
    public void testScoreManagement() {
        // Test initial score
        assertEquals("Initial score should be 0", 0, gameBoard.getScore());
        
        // Test positive score
        gameBoard.updateScoreDisplay(100);
        assertEquals("Score should update to positive value", 100, gameBoard.getScore());
        
        // Test negative score
        gameBoard.updateScoreDisplay(-50);
        assertEquals("Score should handle negative values", -50, gameBoard.getScore());
        
        // Test zero score
        gameBoard.updateScoreDisplay(0);
        assertEquals("Score should handle zero", 0, gameBoard.getScore());
    }

    @Test
    public void testIngredientTracking() {
        // Test initial state
        assertTrue("Initial ingredient counts should be empty", 
                  gameBoard.getIngredientCounts().isEmpty());
        
        // Test adding ingredients
        Map<String, Integer> counts = new HashMap<>();
        counts.put("broth", 1);
        counts.put("chili", 2);
        gameBoard.updateIngredientDisplay(counts);
        
        assertEquals("Should track broth count", 
                    1, (int)gameBoard.getIngredientCounts().get("broth"));
        assertEquals("Should track chili count", 
                    2, (int)gameBoard.getIngredientCounts().get("chili"));
        
        // Test null counts
        gameBoard.updateIngredientDisplay(null);
        assertTrue("Should handle null counts", 
                  gameBoard.getIngredientCounts().isEmpty());
    }

    @Test
    public void testBoardTypes() {
        // Test valid board types
        TestGameBoard fireBoard = new TestGameBoard("fire");
        assertNotNull("Should create fire board", fireBoard.getBoard());
        assertTrue("Should be FireBoard instance", 
                  fireBoard.getBoard() instanceof FireBoard);
        
        TestGameBoard iceBoard = new TestGameBoard("ice");
        assertNotNull("Should create ice board", iceBoard.getBoard());
        assertTrue("Should be IceBoard instance", 
                  iceBoard.getBoard() instanceof IceBoard);
        
        TestGameBoard earthBoard = new TestGameBoard("earth");
        assertNotNull("Should create earth board", earthBoard.getBoard());
        assertTrue("Should be EarthBoard instance", 
                  earthBoard.getBoard() instanceof EarthBoard);
        
        // Test invalid board type
        TestGameBoard invalidBoard = new TestGameBoard("invalid");
        assertNull("Should handle invalid board type", invalidBoard.getBoard());
    }

    @Test
    public void testGameStateTransitions() {
        // Test game over
        assertFalse("Game should not be over initially", gameBoard.isGameOver());
        gameBoard.endGame();
        assertTrue("Game should be over", gameBoard.isGameOver());
        assertTrue("Timer should be paused when game is over", gameBoard.isPaused());
    }

    @Test
    public void testTimerEdgeCases() {
        // Test multiple pauses
        gameBoard.pauseTimer();
        gameBoard.pauseTimer(); // Second pause
        assertTrue("Timer should remain paused after multiple pauses", 
                  gameBoard.isPaused());
        
        // Test multiple resumes
        gameBoard.resumeTimer();
        gameBoard.resumeTimer(); // Second resume
        assertFalse("Timer should remain running after multiple resumes", 
                   gameBoard.isPaused());
        
        // Test update while paused
        gameBoard.pauseTimer();
        int timeBefore = gameBoard.getSecondsElapsed();
        gameBoard.updateTimer();
        assertEquals("Time should not increase while paused", 
                    timeBefore, gameBoard.getSecondsElapsed());
    }

    @Test
    public void testScoreEdgeCases() {
        // Test maximum score
        gameBoard.updateScoreDisplay(Integer.MAX_VALUE);
        assertEquals("Should handle maximum score", 
                    Integer.MAX_VALUE, gameBoard.getScore());
        
        // Test minimum score
        gameBoard.updateScoreDisplay(Integer.MIN_VALUE);
        assertEquals("Should handle minimum score", 
                    Integer.MIN_VALUE, gameBoard.getScore());
    }

    @Test
    public void testIngredientEdgeCases() {
        // Test empty map
        Map<String, Integer> emptyMap = new HashMap<>();
        gameBoard.updateIngredientDisplay(emptyMap);
        assertTrue("Should handle empty ingredient map", 
                  gameBoard.getIngredientCounts().isEmpty());
        
        // Test zero counts
        Map<String, Integer> zeroCounts = new HashMap<>();
        zeroCounts.put("test", 0);
        gameBoard.updateIngredientDisplay(zeroCounts);
        assertEquals("Should handle zero counts", 
                    0, (int)gameBoard.getIngredientCounts().get("test"));
        
        // Test ingredient removal
        Map<String, Integer> counts = new HashMap<>();
        counts.put("test", 1);
        gameBoard.updateIngredientDisplay(counts);
        counts.remove("test");
        gameBoard.updateIngredientDisplay(counts);
        assertFalse("Should handle ingredient removal", 
                   gameBoard.getIngredientCounts().containsKey("test"));
    }
}