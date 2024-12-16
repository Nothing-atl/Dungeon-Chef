package CMPT276.Group22;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BonusIngredientTest {

    @Test
    public void testConstructor() {
        Coordinate position = new Coordinate(3, 4);
        int value = 10;
        String name = "Bonus Ingredient";
        int duration = 5000; // 5 seconds
        Board board = mock(Board.class);

        BonusIngredient ingredient = new BonusIngredient(position, value, name, duration, board);

        assertEquals(position, ingredient.getPosition());
        assertEquals(value, ingredient.getValue());
        assertEquals(name, ingredient.getName());
        assertNotNull(ingredient);
    }

    @Test
    public void testExpirationTimer() throws InterruptedException {
        Board board = mock(Board.class);
        BonusIngredient ingredient = new BonusIngredient(
                new Coordinate(5, 6), 20, "Expiring Bonus", 100, board); // 100ms duration

        Thread.sleep(200); // Wait for the timer to expire

        verify(board, times(1)).removeIngredient(ingredient);
    }

    @Test
    public void testOnCollectedBehavior() {
        // Create mocks
        Board board = mock(Board.class);
        GameBoard gameBoard = mock(GameBoard.class);
        RecipeVisualizationPanel visPanel = mock(RecipeVisualizationPanel.class);

        // Stub methods
        when(board.getGameBoard()).thenReturn(gameBoard);
        when(gameBoard.getRecipeVisualizationPanel()).thenReturn(visPanel);

        // Create BonusIngredient
        BonusIngredient ingredient = new BonusIngredient(
                new Coordinate(1, 1), 30, "Collected Bonus", 5000, board);

        // Call onCollected
        ingredient.onCollected(board);

        // Verify interactions
        verify(board, times(1)).collectIngredient(ingredient);
        verify(gameBoard, times(1)).getRecipeVisualizationPanel();
        verify(visPanel, times(1)).updateBonusState(true);
    }





    @Test
    public void testTimerNonRepetition() throws InterruptedException {
        Board board = mock(Board.class);
        BonusIngredient ingredient = new BonusIngredient(
                new Coordinate(7, 8), 50, "Non-Repeating Timer", 100, board); // 100ms duration

        Thread.sleep(200); // Wait for the timer to expire

        verify(board, times(1)).removeIngredient(ingredient);

        // Simulate additional wait to confirm no further calls
        Thread.sleep(100);
        verify(board, times(1)).removeIngredient(ingredient); // No additional calls
    }
}
