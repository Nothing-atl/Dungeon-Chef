package CMPT276.Group22;

import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;

public class MovingEnemyTest {

    @Test
    public void testMoveTowardsPlayerDiagonally() {
        Entity[][] grid = new Entity[20][20];
        Coordinate start = new Coordinate(10, 10);
        MovingEnemy enemy = new MovingEnemy(start);

        enemy.moveTowardsPlayer(12, 12, grid);

        Coordinate newPosition = enemy.getPosition();
        assertEquals(11, newPosition.getRow());
        assertEquals(11, newPosition.getCol());
    }

    @Test
    public void testMoveTowardsPlayerHorizontally() {
        Entity[][] grid = new Entity[20][20];
        Coordinate start = new Coordinate(10, 10);
        MovingEnemy enemy = new MovingEnemy(start);

        enemy.moveTowardsPlayer(15, 10, grid);

        Coordinate newPosition = enemy.getPosition();
        assertEquals(10, newPosition.getRow());
        assertEquals(11, newPosition.getCol());
    }

    @Test
    public void testMoveTowardsPlayerVertically() {
        Entity[][] grid = new Entity[20][20];
        Coordinate start = new Coordinate(10, 10);
        MovingEnemy enemy = new MovingEnemy(start);

        enemy.moveTowardsPlayer(10, 5, grid);

        Coordinate newPosition = enemy.getPosition();
        assertEquals(9, newPosition.getRow());
        assertEquals(10, newPosition.getCol());
    }

    @Test
    public void testMoveBlockedByWall() {
        Entity[][] grid = new Entity[20][20];
        Coordinate start = new Coordinate(10, 10);
        grid[11][11] = Mockito.mock(Wall.class); // Place a wall at the target diagonal position

        MovingEnemy enemy = new MovingEnemy(start);
        enemy.moveTowardsPlayer(12, 12, grid);

        Coordinate newPosition = enemy.getPosition();
        assertEquals(10, newPosition.getRow()); // Position should not change
        assertEquals(10, newPosition.getCol());
    }

    @Test
    public void testMoveBlockedByAnotherEnemy() {
        Entity[][] grid = new Entity[20][20];
        Coordinate start = new Coordinate(10, 10);
        grid[11][11] = Mockito.mock(MovingEnemy.class); // Place another enemy at the target diagonal position

        MovingEnemy enemy = new MovingEnemy(start);
        enemy.moveTowardsPlayer(12, 12, grid);

        Coordinate newPosition = enemy.getPosition();
        assertEquals(10, newPosition.getRow()); // Position should not change
        assertEquals(10, newPosition.getCol());
    }

    @Test
    public void testRevertMove() {
        Entity[][] grid = new Entity[20][20];
        Coordinate start = new Coordinate(10, 10);
        MovingEnemy enemy = new MovingEnemy(start);

        enemy.moveTowardsPlayer(12, 12, grid); // Move diagonally
        enemy.revertMove(); // Revert to previous position

        Coordinate revertedPosition = enemy.getPosition();
        assertEquals(10, revertedPosition.getRow());
        assertEquals(10, revertedPosition.getCol());
    }

    @Test
    public void testStayWithinBounds() {
        Entity[][] grid = new Entity[20][20];
        Coordinate start = new Coordinate(0, 0); // Enemy starts at the top-left corner
        MovingEnemy enemy = new MovingEnemy(start);

        enemy.moveTowardsPlayer(-5, -5, grid); // Attempt to move outside bounds

        Coordinate newPosition = enemy.getPosition();
        assertEquals(0, newPosition.getRow()); // Should stay within bounds
        assertEquals(0, newPosition.getCol());
    }

    @Test
    public void testBlockedByIngredient() {
        Entity[][] grid = new Entity[20][20];
        Coordinate start = new Coordinate(10, 10);
        grid[11][11] = Mockito.mock(Ingredient.class); // Place an ingredient at the diagonal target

        MovingEnemy enemy = new MovingEnemy(start);
        enemy.moveTowardsPlayer(12, 12, grid);

        Coordinate newPosition = enemy.getPosition();
        assertEquals(10, newPosition.getRow()); // Should not move
        assertEquals(10, newPosition.getCol());
    }
}
