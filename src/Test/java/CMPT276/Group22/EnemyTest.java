package CMPT276.Group22;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class EnemyTest {
    private class TestEnemy extends Enemy {
        public TestEnemy(Coordinate position) {
            super(position);
        }

        @Override
        public void move(Board board, Character character) {
        }
    }

    private Enemy enemy;
    private Coordinate initialPosition;

    @Before
    public void setUp() {
        initialPosition = new Coordinate(5, 5);
        enemy = new TestEnemy(initialPosition);
    }

    @Test
    public void testEnemyInitialization() {
        assertNotNull("Enemy should not be null", enemy);
        assertNotNull("Enemy position should not be null", enemy.getPosition());
    }

    @Test
    public void testInitialPosition() {
        assertEquals("Initial position row should match", 5, enemy.getPosition().getRow());
        assertEquals("Initial position column should match", 5, enemy.getPosition().getCol());
    }

    @Test
    public void testSetPosition() {
        Coordinate newPosition = new Coordinate(3, 3);
        enemy.setPosition(newPosition);
        assertEquals("New position should be updated", newPosition, enemy.getPosition());
    }

    @Test
    public void testPositionIndependence() {
        Coordinate originalPos = enemy.getPosition();
        Coordinate newPos = new Coordinate(7, 7);
        enemy.setPosition(newPos);
        
        // Verify original position hasn't changed
        assertEquals("Original position row should remain unchanged", 5, originalPos.getRow());
        assertEquals("Original position column should remain unchanged", 5, originalPos.getCol());
    }

    @Test
    public void testMultiplePositionUpdates() {
        Coordinate pos1 = new Coordinate(1, 1);
        Coordinate pos2 = new Coordinate(2, 2);
        Coordinate pos3 = new Coordinate(3, 3);

        enemy.setPosition(pos1);
        assertEquals("First position update should work", pos1, enemy.getPosition());

        enemy.setPosition(pos2);
        assertEquals("Second position update should work", pos2, enemy.getPosition());

        enemy.setPosition(pos3);
        assertEquals("Third position update should work", pos3, enemy.getPosition());
    }

    @Test
    public void testBoundaryPositions() {
        // Test setting position at boundaries
        Coordinate zeroPos = new Coordinate(0, 0);
        enemy.setPosition(zeroPos);
        assertEquals("Should handle zero position", zeroPos, enemy.getPosition());

        Coordinate maxPos = new Coordinate(Integer.MAX_VALUE, Integer.MAX_VALUE);
        enemy.setPosition(maxPos);
        assertEquals("Should handle maximum integer position", maxPos, enemy.getPosition());
    }
}