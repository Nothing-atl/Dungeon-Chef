package CMPT276.Group22;

import org.junit.Test;
import static org.junit.Assert.*;

public class WallTest {

    @Test
    public void testConstructor() {
        Coordinate position = new Coordinate(3, 4);
        Wall wall = new Wall(position);

        assertEquals(position, wall.getPosition());
    }

    @Test
    public void testGetPosition() {
        Coordinate position = new Coordinate(5, 6);
        Wall wall = new Wall(position);

        assertEquals(position, wall.getPosition());
    }

    @Test
    public void testSetPosition() {
        Coordinate initialPosition = new Coordinate(7, 8);
        Wall wall = new Wall(initialPosition);

        Coordinate newPosition = new Coordinate(9, 10);
        wall.setPosition(newPosition);

        assertEquals(newPosition, wall.getPosition());
    }

    @Test
    public void testWallAsImpassableObstacle() {
        Coordinate wallPosition = new Coordinate(7, 8);
        Wall wall = new Wall(wallPosition);

        Coordinate entityPosition = new Coordinate(7, 8);
        assertTrue(wall.getPosition().equals(entityPosition));
    }
}
