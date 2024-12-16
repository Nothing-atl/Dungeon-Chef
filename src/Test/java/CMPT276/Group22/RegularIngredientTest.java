package CMPT276.Group22;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RegularIngredientTest {

    @Test
    public void testConstructor() {
        Coordinate position = new Coordinate(3, 4);
        int value = 10;
        String name = "Regular Ingredient";
        Board board = mock(Board.class);

        RegularIngredient ingredient = new RegularIngredient(position, value, name, board);

        assertEquals(position, ingredient.getPosition());
        assertEquals(value, ingredient.getValue());
        assertEquals(name, ingredient.getName());
        assertEquals(board, ingredient.board);
    }

    @Test
    public void testGetValue() {
        Coordinate position = new Coordinate(5, 6);
        int value = 20;
        String name = "Test Ingredient";
        Board board = mock(Board.class);

        RegularIngredient ingredient = new RegularIngredient(position, value, name, board);

        assertEquals(value, ingredient.getValue());
    }

    @Test
    public void testGetName() {
        Coordinate position = new Coordinate(7, 8);
        int value = 15;
        String name = "Regular Ingredient Name";
        Board board = mock(Board.class);

        RegularIngredient ingredient = new RegularIngredient(position, value, name, board);

        assertEquals(name, ingredient.getName());
    }

    @Test
    public void testSetPosition() {
        Coordinate initialPosition = new Coordinate(9, 10);
        int value = 25;
        String name = "Test Regular Ingredient";
        Board board = mock(Board.class);

        RegularIngredient ingredient = new RegularIngredient(initialPosition, value, name, board);

        Coordinate newPosition = new Coordinate(11, 12);
        ingredient.setPosition(newPosition);

        assertEquals(newPosition, ingredient.getPosition());
    }

    @Test
    public void testOnCollected() {
        Coordinate position = new Coordinate(1, 1);
        int value = 30;
        String name = "Collected Ingredient";
        Board board = mock(Board.class);

        RegularIngredient ingredient = new RegularIngredient(position, value, name, board);

        ingredient.onCollected(board);

        // Verify that the board methods are called with the correct parameters
        verify(board, times(1)).increasePlayerScore(value);
        verify(board, times(1)).addCollectedIngredient(name);
    }
}
