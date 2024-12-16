package CMPT276.Group22;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

public class ComprehensiveGameTest {

    private Board board;
    private GameBoard gameBoard;
    private Coordinate playerPosition;

    @Before
    public void setUp() {
        gameBoard = mock(GameBoard.class);
        board = spy(new FireBoard());
        board.setGameBoard(gameBoard);
        
        // Initialize game state
        board.grid = new Entity[Board.GRID_HEIGHT][Board.GRID_WIDTH];
        board.ingredients = new ArrayList<>();
        board.staticEnemies = new ArrayList<>();
        board.movingEnemies = new ArrayList<>();
        board.barriers = new ArrayList<>();
        board.collectedIngredients = new ArrayList<>();
        
        // Reset game state
        board.isGameOver = false;
        board.playerScore = 0;
        
        // Set initial player position
        board.playerX = 1;
        board.playerY = 1;
        playerPosition = new Coordinate(1, 1);
    }

    @Test
    public void testValidPlayerMovement() {
        // Test moving to empty space
        boolean validMove = board.isValidMove(2, 2);
        assertTrue("Should be able to move to empty space", validMove);
        
        // Test moving outside grid
        assertFalse("Should not be able to move outside grid", 
                   board.isValidMove(-1, 1));
    }

    @Test
    public void testIngredientCollection() {
        // Create and place ingredient
        RegularIngredient ingredient = new RegularIngredient(
            new Coordinate(2, 2), 10, "testIngredient", board);
        board.ingredients.add(ingredient);
        board.grid[2][2] = ingredient;
        
        int initialScore = board.playerScore;
        
        // Move player to ingredient position
        board.playerX = 2;
        board.playerY = 2;
        board.checkCollisions();
        
        // Verify collection
        assertEquals("Score should increase by ingredient value",
                    initialScore + 10, board.playerScore);
        assertTrue("Ingredient should be added to collected list",
                  board.collectedIngredients.contains("testingredient"));
    }

    @Test
    public void testBarrierCollision() {
        // Place barrier
        Barrier barrier = new Barrier(new Coordinate(3, 3));
        board.grid[3][3] = barrier;
        board.barriers.add(barrier);
        
        // Try to move to barrier position
        assertFalse("Should not be able to move into barrier", 
                   board.isValidMove(3, 3));
    }

    @Test
    public void testStaticEnemyPenalty() {
        // Initial score setup
        board.playerScore = 100;
        
        // Create and place static enemy
        StaticEnemy enemy = new StaticEnemy(new Coordinate(4, 4), 50);
        board.staticEnemies.add(enemy);
        board.grid[4][4] = enemy;
        
        // Move player to enemy
        board.playerX = 4;
        board.playerY = 4;
        
        // Force collision check
        board.checkCollisions();
        
        // Verify penalty application
        assertEquals("Score should be reduced by penalty amount",
                    50, board.playerScore);
        verify(gameBoard, times(1)).updateScoreDisplay();
    }


    public void testMovingEnemyCollision() {
        board.isGameOver = false; // Ensure game starts not over
        
        // Create and place moving enemy
        MovingEnemy enemy = new MovingEnemy(new Coordinate(5, 5));
        board.movingEnemies.add(enemy);
        board.grid[5][5] = enemy;
        
        assertFalse("Game should not be over before collision", board.isGameOver);
        
        // Move player to enemy position and update game state
        board.playerX = 5;
        board.playerY = 5;
        
        // Create collision coordinates that match the enemy's position
        Coordinate collisionPosition = new Coordinate(5, 5);
        
        // Force collision check
        board.checkCollisions();
        
        // Additional verification that the positions match
        assertEquals("Player should be at enemy position", 
                    enemy.getPosition().getRow(), board.playerY);
        assertEquals("Player should be at enemy position", 
                    enemy.getPosition().getCol(), board.playerX);
        
        assertTrue("Game should be over after enemy collision", board.isGameOver);
        verify(gameBoard, times(1)).showGameOverDialog();
        
        // Print debug information
        System.out.println("Enemy position: " + enemy.getPosition().getRow() + 
                         "," + enemy.getPosition().getCol());
        System.out.println("Player position: " + board.playerY + "," + board.playerX);
        System.out.println("Game over state: " + board.isGameOver);
    }
}