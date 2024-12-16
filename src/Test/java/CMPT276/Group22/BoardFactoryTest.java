package CMPT276.Group22;

import org.junit.Test;
import static org.junit.Assert.*;

public class BoardFactoryTest {

    @Test
    public void testCreateFireBoard() {
        Board board = BoardFactory.createBoard("fire");
        assertNotNull(board);
        assertTrue(board instanceof FireBoard);
    }

    @Test
    public void testCreateIceBoard() {
        Board board = BoardFactory.createBoard("ice");
        assertNotNull(board);
        assertTrue(board instanceof IceBoard);
    }

    @Test
    public void testCreateEarthBoard() {
        Board board = BoardFactory.createBoard("earth");
        assertNotNull(board);
        assertTrue(board instanceof EarthBoard);
    }

    @Test
    public void testCaseInsensitiveInput() {
        Board fireBoard = BoardFactory.createBoard("FIRE");
        assertNotNull(fireBoard);
        assertTrue(fireBoard instanceof FireBoard);

        Board iceBoard = BoardFactory.createBoard("Ice");
        assertNotNull(iceBoard);
        assertTrue(iceBoard instanceof IceBoard);

        Board earthBoard = BoardFactory.createBoard("EaRtH");
        assertNotNull(earthBoard);
        assertTrue(earthBoard instanceof EarthBoard);
    }

    @Test
    public void testInvalidInput() {
        Board board = BoardFactory.createBoard("water");
        assertNull(board);
    }

    @Test
    public void testNullInput() {
        Board board = BoardFactory.createBoard(null);
        assertNull(board);
    }

    @Test
    public void testEmptyStringInput() {
        Board board = BoardFactory.createBoard("");
        assertNull(board);
    }
}
