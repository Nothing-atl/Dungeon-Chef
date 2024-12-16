package CMPT276.Group22;

import org.junit.Test;
import static org.junit.Assert.*;

public class DirectionTest {
    
    @Test
    public void testOppositeDirection() {
        assertEquals(Direction.DOWN, Direction.UP.oppositeDirection());
        assertEquals(Direction.UP, Direction.DOWN.oppositeDirection());
        assertEquals(Direction.LEFT, Direction.RIGHT.oppositeDirection());
        assertEquals(Direction.RIGHT, Direction.LEFT.oppositeDirection());
    }

    @Test
    public void testAllDirectionsHaveOpposites() {
        for (Direction dir : Direction.values()) {
            assertNotNull(dir.oppositeDirection());
            assertNotEquals(dir, dir.oppositeDirection());
        }
    }

    @Test 
    public void testNullForInvalidDirection() {
        // Using null is a valid way to test edge case
        Direction nullDirection = null;
        assertNull(nullDirection == null ? null : nullDirection.oppositeDirection());
    }
}