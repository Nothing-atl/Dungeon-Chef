package CMPT276.Group22;

import org.junit.Test;
import static org.junit.Assert.*;

public class EntityTest {

    // Concrete subclass for testing purposes
    static class TestEntity extends Entity {
        private boolean interacted = false;

        public TestEntity(Coordinate position) {
            super(position);
        }

        @Override
        public void interact() {
            interacted = true;
        }

        public boolean isInteracted() {
            return interacted;
        }
    }

    @Test
    public void testConstructor() {
        Coordinate position = new Coordinate(3, 4);
        TestEntity entity = new TestEntity(position);

        assertEquals(position, entity.getPosition());
    }

    @Test
    public void testGetPosition() {
        Coordinate position = new Coordinate(5, 6);
        TestEntity entity = new TestEntity(position);

        assertEquals(position, entity.getPosition());
    }

    @Test
    public void testSetPosition() {
        Coordinate initialPosition = new Coordinate(7, 8);
        TestEntity entity = new TestEntity(initialPosition);

        Coordinate newPosition = new Coordinate(9, 10);
        entity.setPosition(newPosition);

        assertEquals(newPosition, entity.getPosition());
    }

    @Test
    public void testInteractDefaultBehavior() {
        TestEntity entity = new TestEntity(new Coordinate(1, 2));
        assertFalse(entity.isInteracted());

        entity.interact();
        assertTrue(entity.isInteracted());
    }
}
