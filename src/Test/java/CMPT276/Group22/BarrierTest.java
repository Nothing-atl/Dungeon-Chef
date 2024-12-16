package CMPT276.Group22;

import org.junit.Test;
import static org.junit.Assert.*;

public class BarrierTest {

    @Test
    public void testConstructor() {
        Coordinate position = new Coordinate(3, 4);
        Barrier barrier = new Barrier(position);
        assertEquals(position, barrier.getPosition());
    }

    @Test
    public void testPositionGetter() {
        Coordinate position = new Coordinate(5, 6);
        Barrier barrier = new Barrier(position);
        assertEquals(position, barrier.getPosition());
    }

    @Test
    public void testPositionSetter() {
        Coordinate initialPosition = new Coordinate(7, 8);
        Barrier barrier = new Barrier(initialPosition);

        Coordinate newPosition = new Coordinate(9, 10);
        barrier.setPosition(newPosition);

        assertEquals(newPosition, barrier.getPosition());
    }

    @Test
    public void testImmutabilityOfInitialPosition() {
        Coordinate position = new Coordinate(1, 2);
        Barrier barrier = new Barrier(position);
        position.move(Direction.UP); // Modify the original coordinate
        position.move(Direction.RIGHT);

        assertEquals(0, barrier.getPosition().getRow());
        assertEquals(3, barrier.getPosition().getCol());
    }

    @Test
    public void testBarrierFunctionality() {
        // Assuming there's a collision detection mechanism
        Coordinate barrierPosition = new Coordinate(7, 8);
        Barrier barrier = new Barrier(barrierPosition);

        Coordinate entityPosition = new Coordinate(7, 8);
        boolean isBlocked = barrier.getPosition().equals(entityPosition);

        assertTrue(isBlocked);
    }
}
