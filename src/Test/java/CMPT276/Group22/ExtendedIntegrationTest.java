package CMPT276.Group22;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ExtendedIntegrationTest {

    private GameBoard gameBoard;
    private Board board;
    private RecipeVisualizationPanel recipeVisualizationPanel;

    @Before
    public void setUp() {
        gameBoard = mock(GameBoard.class);
        board = mock(Board.class);
        recipeVisualizationPanel = mock(RecipeVisualizationPanel.class);

        // Stub methods
        when(board.getGameBoard()).thenReturn(gameBoard);
        when(gameBoard.getRecipeVisualizationPanel()).thenReturn(recipeVisualizationPanel);
    }

    @Test
    public void testBonusIngredientCollectionAndVisualizationUpdate() {
        // Create mocks with proper behavior
        GameBoard gameBoard = mock(GameBoard.class);
        Board board = mock(Board.class);
        RecipeVisualizationPanel visPanel = mock(RecipeVisualizationPanel.class);
        
        // Set up mock chain
        when(board.getGameBoard()).thenReturn(gameBoard);
        when(gameBoard.getRecipeVisualizationPanel()).thenReturn(visPanel);
        
        // Create and collect bonus ingredient
        BonusIngredient ingredient = new BonusIngredient(
            new Coordinate(1, 1), 50, "Bonus Ingredient", 5000, board);
        ingredient.onCollected(board);

        // Verify all the steps occurred
        verify(board, times(1)).handleBonusCollection(ingredient);
        verify(board, times(1)).collectIngredient(ingredient);
        verify(board, times(1)).getGameBoard();
        verify(gameBoard, times(1)).getRecipeVisualizationPanel();
        verify(visPanel, times(1)).updateBonusState(true);
    }

    @Test
    public void testRegularIngredientCollectionAndVisualizationUpdate() {
        // Create a RegularIngredient
        Coordinate position = new Coordinate(2, 2);
        int value = 20;
        String name = "Regular Ingredient";
        RegularIngredient ingredient = new RegularIngredient(position, value, name, board);

        // Print the state of ingredientCompleted before collection
      
        // Collect the ingredient
        ingredient.onCollected(board);

        // Simulate the interaction with the RecipeVisualizationPanel
        recipeVisualizationPanel.updateIngredientState(name.toLowerCase(), true);

        // Print the state of ingredientCompleted after collection

        // Verify that the ingredient was collected
        verify(board, times(1)).increasePlayerScore(value);
        verify(board, times(1)).addCollectedIngredient(name);

        // Verify that the visualization panel was updated
        verify(recipeVisualizationPanel, times(1)).updateIngredientState(name.toLowerCase(), true);
    }
}