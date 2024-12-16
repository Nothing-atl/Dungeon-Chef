package CMPT276.Group22;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class StaticEnemyTest {
    
    private StaticEnemy enemy;
    private Coordinate initialPosition;
    private int initialPenalty;

    @Before
    public void setUp() {
        initialPosition = new Coordinate(5, 5);
        initialPenalty = 10;
        enemy = new StaticEnemy(initialPosition, initialPenalty);
    }

    @Test
    public void testConstructorAndInitialization() {
        assertNotNull("Enemy should not be null", enemy);
        assertNotNull("Enemy position should not be null", enemy.getPosition());
        assertEquals("Initial position should match constructor argument", initialPosition, enemy.getPosition());
        assertEquals("Initial penalty should match constructor argument", initialPenalty, enemy.getPenaltyAmount());
    }

    @Test
    public void testPositionUpdate() {
        Coordinate newPosition = new Coordinate(3, 3);
        enemy.setPosition(newPosition);
        assertEquals("Position should be updated correctly", newPosition, enemy.getPosition());
        assertEquals("Penalty should remain unchanged after position update", 
                    initialPenalty, enemy.getPenaltyAmount());
    }

    @Test
    public void testMultiplePositionUpdates() {
        Coordinate[] positions = {
            new Coordinate(1, 1),
            new Coordinate(2, 2),
            new Coordinate(3, 3),
            new Coordinate(4, 4)
        };

        for (Coordinate pos : positions) {
            enemy.setPosition(pos);
            assertEquals("Position should be updated correctly", pos, enemy.getPosition());
            assertEquals("Penalty should remain unchanged", initialPenalty, enemy.getPenaltyAmount());
        }
    }

    @Test
    public void testDifferentPenaltyValues() {
        int[] penalties = {0, 5, 10, 20, 50, 100};
        
        for (int penalty : penalties) {
            StaticEnemy testEnemy = new StaticEnemy(initialPosition, penalty);
            assertEquals("Penalty amount should match constructor argument", 
                        penalty, testEnemy.getPenaltyAmount());
        }
    }

    @Test
    public void testNegativePenalty() {
        StaticEnemy negativeEnemy = new StaticEnemy(initialPosition, -10);
        assertEquals("Should handle negative penalty", -10, negativeEnemy.getPenaltyAmount());
    }

    @Test
    public void testPositionIndependence() {
        Coordinate originalPos = enemy.getPosition();
        Coordinate newPos = new Coordinate(7, 7);
        
        enemy.setPosition(newPos);
        
        // Verify original position object hasn't changed
        assertEquals("Original position should not be modified", 5, originalPos.getRow());
        assertEquals("Original position should not be modified", 5, originalPos.getCol());
    }

    @Test
    public void testInheritanceFromEntity() {
        assertTrue("StaticEnemy should be an instance of Entity", enemy instanceof Entity);
    }

    @Test
    public void testBoundaryPositions() {
        // Test minimum boundary
        Coordinate minPos = new Coordinate(0, 0);
        enemy.setPosition(minPos);
        assertEquals("Should handle minimum position", minPos, enemy.getPosition());

        // Test maximum boundary
        Coordinate maxPos = new Coordinate(Integer.MAX_VALUE, Integer.MAX_VALUE);
        enemy.setPosition(maxPos);
        assertEquals("Should handle maximum position", maxPos, enemy.getPosition());
    }

    @Test
    public void testSeparateInstances() {
        StaticEnemy enemy1 = new StaticEnemy(new Coordinate(1, 1), 10);
        StaticEnemy enemy2 = new StaticEnemy(new Coordinate(2, 2), 20);

        assertNotSame("Different instances should not be the same", enemy1, enemy2);
        assertNotEquals("Different instances should have different positions", 
                       enemy1.getPosition(), enemy2.getPosition());
        assertNotEquals("Different instances can have different penalties",
                       enemy1.getPenaltyAmount(), enemy2.getPenaltyAmount());
    }
}