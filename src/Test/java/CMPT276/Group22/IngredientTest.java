package CMPT276.Group22;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IngredientTest {

    // Concrete subclass for testing purposes
    static class TestIngredient extends Ingredient {
        private boolean collected = false;

        public TestIngredient(Coordinate position, int value, String name, Board board) {
            super(position, value, name, board);
        }

        @Override
        public void onCollected(Board board) {
            this.collected = true;
        }

        public boolean isCollected() {
            return collected;
        }
    }

    @Test
    public void testConstructor() {
        Coordinate position = new Coordinate(3, 4);
        int value = 10;
        String name = "Test Ingredient";
        Board board = mock(Board.class);

        Ingredient ingredient = new TestIngredient(position, value, name, board);

        assertEquals(position, ingredient.getPosition());
        assertEquals(value, ingredient.getValue());
        assertEquals(name, ingredient.getName());
        assertEquals(board, ingredient.board);
    }

    @Test
    public void testGetValue() {
        Coordinate position = new Coordinate(5, 6);
        int value = 15;
        String name = "Test Ingredient";
        Board board = mock(Board.class);

        Ingredient ingredient = new TestIngredient(position, value, name, board);

        assertEquals(value, ingredient.getValue());
    }

    @Test
    public void testGetName() {
        Coordinate position = new Coordinate(7, 8);
        int value = 20;
        String name = "Ingredient Name";
        Board board = mock(Board.class);

        Ingredient ingredient = new TestIngredient(position, value, name, board);

        assertEquals(name, ingredient.getName());
    }

    @Test
    public void testSetPosition() {
        Coordinate initialPosition = new Coordinate(9, 10);
        int value = 25;
        String name = "Test Ingredient";
        Board board = mock(Board.class);

        Ingredient ingredient = new TestIngredient(initialPosition, value, name, board);

        Coordinate newPosition = new Coordinate(11, 12);
        ingredient.setPosition(newPosition);

        assertEquals(newPosition, ingredient.getPosition());
    }

    @Test
    public void testOnCollected() {
        Coordinate position = new Coordinate(1, 1);
        int value = 30;
        String name = "Test Ingredient";
        Board board = mock(Board.class);

        TestIngredient ingredient = new TestIngredient(position, value, name, board);

        assertFalse(ingredient.isCollected());

        ingredient.onCollected(board);

        assertTrue(ingredient.isCollected());
    }
}
