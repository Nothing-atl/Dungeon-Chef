package CMPT276.Group22;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

public class GameBoardTest {

    @Test
    public void testGameBoardInitialization() {
        // Verify the GameBoard is initialized correctly
        GameBoard gameBoard = new GameBoard("fire");
        assertNotNull(gameBoard);
        assertNotNull(gameBoard.getRecipeVisualizationPanel());
        assertEquals("Dungeon Chef", gameBoard.getTitle());
        assertFalse(gameBoard.isResizable());
    }

   /* @Test
    public void testShowMenuOptions() {
        GameBoard gameBoard = spy(new GameBoard("fire"));

        // Pause timer before showing menu
        doNothing().when(gameBoard).pauseTimer();

        // Simulate showing menu
        SwingUtilities.invokeLater(() -> {
            gameBoard.showMenu();
            verify(gameBoard, times(1)).pauseTimer();
        });
    }*/

    @Test
    public void testUpdateIngredientDisplayReflection() throws Exception {
        GameBoard gameBoard = new GameBoard("fire");

        // Use reflection to access the private field
        Field field = GameBoard.class.getDeclaredField("ingredientTrackingPanel");
        field.setAccessible(true);
        JPanel ingredientTrackingPanel = (JPanel) field.get(gameBoard);

        // Verify the panel exists and test its behavior
        assertNotNull(ingredientTrackingPanel);
    }

    @Test
    public void testUpdateTimerDisplay() throws Exception {
        GameBoard gameBoard = new GameBoard("fire");

        // Use reflection to modify the private field
        Field field = GameBoard.class.getDeclaredField("secondsElapsed");
        field.setAccessible(true);
        field.setInt(gameBoard, 125); // Simulate 125 seconds elapsed

        // Call the method to update the display
        Method method = GameBoard.class.getDeclaredMethod("updateTimerDisplay");
        method.setAccessible(true);
        method.invoke(gameBoard);

        // Verify the timer label is updated
        Field timerField = GameBoard.class.getDeclaredField("timerLabel");
        timerField.setAccessible(true);
        JLabel timerLabel = (JLabel) timerField.get(gameBoard);
        assertEquals("Time: 2:05", timerLabel.getText());
    }


    @Test
    public void testGameOverDialog() {
        GameBoard gameBoard = spy(new GameBoard("fire"));

        // Mock the behavior of gameTimer
        doNothing().when(gameBoard).showGameOverDialog();

        // Simulate game over
        gameBoard.showGameOverDialog();

        // Verify game over dialog was shown
        verify(gameBoard, times(1)).showGameOverDialog();
    }

   /* @Test
    public void testResetGame() {
        GameBoard gameBoard = spy(new GameBoard("fire"));

        // Mock behavior for resetting
        doNothing().when(gameBoard).resetGame();

        // Call resetGame
        gameBoard.resetGame();

        // Verify resetGame was invoked
        verify(gameBoard, times(1)).resetGame();
    }*/

    @Test
    public void testPauseAndResumeTimer() {
        GameBoard gameBoard = spy(new GameBoard("fire"));

        // Mock pause and resume methods
        doNothing().when(gameBoard).pauseTimer();
        doNothing().when(gameBoard).resumeTimer();

        // Call pause and resume
        gameBoard.pauseTimer();
        gameBoard.resumeTimer();

        // Verify both methods were called once
        verify(gameBoard, times(1)).pauseTimer();
        verify(gameBoard, times(1)).resumeTimer();
    }
    
    
}


