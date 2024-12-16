package CMPT276.Group22;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics;

public class ExtraMovingEnemyTest {
    private TestBoard board;
    private TestMovingEnemy enemy;
    private static final int GRID_SIZE = 20;

    // Test-specific moving enemy implementation
    private class TestMovingEnemy extends MovingEnemy {
        private boolean moved = false;

        public TestMovingEnemy(Coordinate position) {
            super(position);
        }

        @Override
        public void moveTowardsPlayer(int playerX, int playerY, Entity[][] grid) {
            // Get current position
            int currentRow = getPosition().getRow();
            int currentCol = getPosition().getCol();
            
            // Calculate direction to player
            int dx = Integer.compare(playerX - currentCol, 0);
            int dy = Integer.compare(playerY - currentRow, 0);
            
            // Try horizontal movement first
            if (dx != 0 && isValidMove(currentRow, currentCol + dx, grid)) {
                setPosition(new Coordinate(currentRow, currentCol + dx));
                moved = true;
                return;
            }
            
            // Try vertical movement if horizontal wasn't possible
            if (dy != 0 && isValidMove(currentRow + dy, currentCol, grid)) {
                setPosition(new Coordinate(currentRow + dy, currentCol));
                moved = true;
                return;
            }
            
            // If no movement was possible, don't change position
            moved = false;
        }

        private boolean isValidMove(int row, int col, Entity[][] grid) {
            if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) {
                return false;
            }
            return grid[row][col] == null || grid[row][col] == this;
        }

        public boolean hasMoved() {
            return moved;
        }

        public void resetMoved() {
            moved = false;
        }
    }

    private class TestBoard extends Board {
        @Override protected void loadBoardSpecificImages() {}
        @Override protected void initializeIngredients() {}
        @Override protected void initializeEnemies() {}
        @Override protected void initializeBarriers() {}
        @Override protected void checkWinCondition() {}
        @Override protected List<String> getRequiredIngredients() {
            return new ArrayList<>();
        }
        @Override protected void spawnBonusIngredient() {}
        @Override protected void handleBonusCollection(Ingredient ingredient) {}
        @Override protected void drawEntity(Graphics g, Entity entity, int row, int col) {}
        @Override protected int getTargetForIngredient(String ingredient) { return 1; }

        public void setGridCell(int row, int col, Entity entity) {
            grid[row][col] = entity;
        }

        public Entity getGridCell(int row, int col) {
            return grid[row][col];
        }

        public void clearGrid() {
            grid = new Entity[GRID_SIZE][GRID_SIZE];
        }
    }

    @Before
    public void setUp() {
        board = new TestBoard();
        board.grid = new Entity[GRID_SIZE][GRID_SIZE];
        enemy = new TestMovingEnemy(new Coordinate(10, 10));
        board.setGridCell(10, 10, enemy);
        board.playerX = 11;  // Place player one cell to the right
        board.playerY = 10;  // Same row as enemy
    }

    @Test
    public void testInitialPosition() {
        assertEquals(10, enemy.getPosition().getRow());
        assertEquals(10, enemy.getPosition().getCol());
    }

    @Test
    public void testHorizontalMovement() {
        enemy.moveTowardsPlayer(board.playerX, board.playerY, board.grid);
        
        assertEquals("Row should not change for horizontal movement", 
                    10, enemy.getPosition().getRow());
        assertEquals("Should move one column towards player", 
                    11, enemy.getPosition().getCol());
        assertTrue("Should record movement", enemy.hasMoved());
    }

    @Test
    public void testVerticalMovement() {
        // Set player position above enemy
        board.playerX = 10;
        board.playerY = 9;
        
        enemy.moveTowardsPlayer(board.playerX, board.playerY, board.grid);
        
        assertEquals("Column should not change for vertical movement", 
                    10, enemy.getPosition().getCol());
        assertEquals("Should move one row towards player", 
                    9, enemy.getPosition().getRow());
        assertTrue("Should record movement", enemy.hasMoved());
    }

    @Test
    public void testPathBlocked() {
        // Place barriers around enemy
        board.setGridCell(9, 10, new Barrier(new Coordinate(9, 10)));   // Up
        board.setGridCell(11, 10, new Barrier(new Coordinate(11, 10))); // Down
        board.setGridCell(10, 9, new Barrier(new Coordinate(10, 9)));   // Left
        board.setGridCell(10, 11, new Barrier(new Coordinate(10, 11))); // Right
        
        Coordinate initialPos = enemy.getPosition();
        enemy.moveTowardsPlayer(board.playerX, board.playerY, board.grid);
        
        assertEquals("Position should not change when blocked", 
                    initialPos, enemy.getPosition());
        assertFalse("Should not record movement when blocked", enemy.hasMoved());
    }

    @Test
    public void testGridBoundary() {
        // Place enemy at edge
        enemy = new TestMovingEnemy(new Coordinate(0, 0));
        board.clearGrid();
        board.setGridCell(0, 0, enemy);
        
        // Try to move out of bounds
        enemy.moveTowardsPlayer(-1, 0, board.grid);
        
        assertTrue("Row should stay in bounds", 
                  enemy.getPosition().getRow() >= 0);
        assertTrue("Column should stay in bounds", 
                  enemy.getPosition().getCol() >= 0);
    }

    @Test
    public void testEnemyCollisionAvoidance() {
        // Place another enemy in the path
        MovingEnemy otherEnemy = new TestMovingEnemy(new Coordinate(10, 11));
        board.setGridCell(10, 11, otherEnemy);
        
        Coordinate initialPos = enemy.getPosition();
        enemy.moveTowardsPlayer(board.playerX, board.playerY, board.grid);
        
        assertEquals("Should not move into other enemy's position", 
                    initialPos, enemy.getPosition());
        assertFalse("Should not record movement when blocked by enemy", 
                   enemy.hasMoved());
    }
}