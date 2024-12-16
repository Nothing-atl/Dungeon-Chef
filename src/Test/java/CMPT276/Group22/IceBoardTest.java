package CMPT276.Group22;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IceBoardTest {
    private IceBoard iceBoard;

    @Before
    public void setUp() {
        iceBoard = new IceBoard();
    }

    @Test
    public void testInitializeIngredients() {
        int totalIngredients = iceBoard.ingredients.size();
        int expectedTotal = IceBoard.BANANA_TARGET + IceBoard.ICE_CREAM_TARGET +
                          IceBoard.CHERRY_TARGET + IceBoard.WHIP_CREAM_TARGET;
        
        assertEquals("Should have correct number of ingredients", expectedTotal, totalIngredients);
        
        long bananaCount = countIngredientsOfType("banana");
        long iceCreamCount = countIngredientsOfType("icecream");
        long cherryCount = countIngredientsOfType("cherry");
        long whipCreamCount = countIngredientsOfType("whipcream");
        
        assertEquals("Should have correct number of bananas", IceBoard.BANANA_TARGET, bananaCount);
        assertEquals("Should have correct number of ice cream", IceBoard.ICE_CREAM_TARGET, iceCreamCount);
        assertEquals("Should have correct number of cherries", IceBoard.CHERRY_TARGET, cherryCount);
        assertEquals("Should have correct number of whip cream", IceBoard.WHIP_CREAM_TARGET, whipCreamCount);
    }

    private long countIngredientsOfType(String type) {
        return iceBoard.ingredients.stream()
            .filter(i -> i instanceof RegularIngredient)
            .map(i -> (RegularIngredient) i)
            .filter(i -> i.getName().equals(type))
            .count();
    }

    @Test
    public void testGetRequiredIngredients() {
        List<String> required = iceBoard.getRequiredIngredients();
        
        assertNotNull("Required ingredients list should not be null", required);
        assertEquals("Should have 4 required ingredients", 4, required.size());
        assertTrue("Should contain banana", required.contains("banana"));
        assertTrue("Should contain icecream", required.contains("icecream"));
        assertTrue("Should contain cherry", required.contains("cherry"));
        assertTrue("Should contain whipcream", required.contains("whipcream"));
    }

    @Test
    public void testInitializeEnemies() {
        assertEquals("Should have correct number of static enemies", 4, iceBoard.staticEnemies.size());
        assertEquals("Should have correct number of moving enemies", 2, iceBoard.movingEnemies.size());
        
        // Verify enemy positions
        for (StaticEnemy enemy : iceBoard.staticEnemies) {
            assertTrue("Static enemy should be within grid bounds", isPositionValid(enemy.getPosition()));
        }
        
        for (MovingEnemy enemy : iceBoard.movingEnemies) {
            assertTrue("Moving enemy should be within grid bounds", isPositionValid(enemy.getPosition()));
        }
    }

    private boolean isPositionValid(Coordinate pos) {
        return pos.getRow() >= 0 && pos.getRow() < IceBoard.GRID_HEIGHT &&
               pos.getCol() >= 0 && pos.getCol() < IceBoard.GRID_WIDTH;
    }

    @Test
    public void testInitializeBarriers() {
        assertEquals("Should have correct number of barriers", 12, iceBoard.barriers.size());
        
        for (Barrier barrier : iceBoard.barriers) {
            assertTrue("Barrier should be within grid bounds", isPositionValid(barrier.getPosition()));
            assertEquals("Grid should contain barrier", 
                        barrier, 
                        iceBoard.grid[barrier.getPosition().getRow()][barrier.getPosition().getCol()]);
        }
    }

    @Test
    public void testBonusIngredientLifecycle() {
        iceBoard.isGameOver = false;
        iceBoard.isGameWon = false;
        iceBoard.bonusCollected = false;

        iceBoard.spawnBonusIngredient();
        
        boolean hasBonusIngredient = iceBoard.ingredients.stream()
            .anyMatch(i -> i instanceof BonusIngredient && "milkshake".equals(i.getName()));
        assertTrue("Should spawn bonus milkshake", hasBonusIngredient);
        
        BonusIngredient bonus = (BonusIngredient) iceBoard.ingredients.stream()
            .filter(i -> i instanceof BonusIngredient)
            .findFirst()
            .orElseThrow();
        
        iceBoard.handleBonusCollection(bonus);
        
        assertTrue("Should mark bonus as collected", iceBoard.bonusCollected);
        assertTrue("Should mark milkshake as collected", iceBoard.hasBonusMilkshake());
        assertEquals("Should add bonus value to score", bonus.getValue(), iceBoard.playerScore);
    }

    @Test
    public void testIngredientCollection() {
        RegularIngredient banana = new RegularIngredient(
            new Coordinate(1, 1), 5, "banana", iceBoard);
        iceBoard.collectIngredient(banana);
        assertEquals("Should collect banana", 1, iceBoard.getBananaCollected());
        
        RegularIngredient iceCream = new RegularIngredient(
            new Coordinate(2, 2), 10, "iceCream", iceBoard);
        iceBoard.collectIngredient(iceCream);
        assertEquals("Should collect ice cream", 1, iceBoard.getIceCreamCollected());
        
        assertEquals("Should update total score", 15, iceBoard.playerScore);
    }

    @Test
    public void testInitialState() {
        assertEquals("Should start with 0 bananas", 0, iceBoard.getBananaCollected());
        assertEquals("Should start with 0 ice cream", 0, iceBoard.getIceCreamCollected());
        assertEquals("Should start with 0 cherries", 0, iceBoard.getCherryCollected());
        assertEquals("Should start with 0 whip cream", 0, iceBoard.getWhipCreamCollected());
        assertFalse("Should start with no bonus milkshake", iceBoard.hasBonusMilkshake());
        assertEquals("Should start with 0 score", 0, iceBoard.playerScore);
        assertFalse("Should not start as game over", iceBoard.isGameOver);
        assertFalse("Should not start as won", iceBoard.isGameWon);
    }

    @Test
    public void testCheckWinCondition() {
        iceBoard.checkWinCondition();
        assertFalse(iceBoard.isGameWon);
        
        for (int i = 0; i < IceBoard.BANANA_TARGET; i++) iceBoard.collectedIngredients.add("banana");
        for (int i = 0; i < IceBoard.ICE_CREAM_TARGET; i++) iceBoard.collectedIngredients.add("icecream");
        for (int i = 0; i < IceBoard.CHERRY_TARGET; i++) iceBoard.collectedIngredients.add("cherry");
        for (int i = 0; i < IceBoard.WHIP_CREAM_TARGET; i++) iceBoard.collectedIngredients.add("whipcream");
        
        iceBoard.checkWinCondition();
        assertTrue(iceBoard.isGameWon);
        assertTrue(iceBoard.isGameOver);
    }

    @Test
    public void testGetTargetForIngredient() {
        assertEquals(IceBoard.BANANA_TARGET, iceBoard.getTargetForIngredient("banana"));
        assertEquals(IceBoard.ICE_CREAM_TARGET, iceBoard.getTargetForIngredient("icecream"));
        assertEquals(IceBoard.CHERRY_TARGET, iceBoard.getTargetForIngredient("cherry"));
        assertEquals(IceBoard.WHIP_CREAM_TARGET, iceBoard.getTargetForIngredient("whipcream"));
        assertEquals(0, iceBoard.getTargetForIngredient("invalid"));
    }

    @Test
    public void testCollectIngredient() {
        Ingredient[] ingredients = {
            new RegularIngredient(new Coordinate(1, 1), 5, "banana", iceBoard),
            new RegularIngredient(new Coordinate(1, 2), 4, "icecream", iceBoard),
            new RegularIngredient(new Coordinate(1, 3), 3, "cherry", iceBoard),
            new RegularIngredient(new Coordinate(1, 4), 7, "whipcream", iceBoard),
            new BonusIngredient(new Coordinate(1, 5), 20, "milkshake", 5000, iceBoard)
        };

        int expectedScore = 0;
        for (Ingredient ingredient : ingredients) {
            iceBoard.collectIngredient(ingredient);
            expectedScore += ingredient.getValue();
        }

        assertEquals(1, iceBoard.getBananaCollected());
        assertEquals(1, iceBoard.getIceCreamCollected());
        assertEquals(1, iceBoard.getCherryCollected());
        assertEquals(1, iceBoard.getWhipCreamCollected());
        assertTrue(iceBoard.hasBonusMilkshake());
        assertEquals(expectedScore, iceBoard.playerScore);
    }

    @Test
    public void testHandleBonusCollection() {
        BonusIngredient bonus = new BonusIngredient(new Coordinate(1, 1), 20, "milkshake", 5000, iceBoard);
        RegularIngredient regular = new RegularIngredient(new Coordinate(1, 1), 10, "banana", iceBoard);

        iceBoard.handleBonusCollection(regular);
        assertFalse(iceBoard.bonusCollected);

        iceBoard.handleBonusCollection(bonus);
        assertTrue(iceBoard.bonusCollected);
        assertTrue(iceBoard.hasBonusMilkshake());
        assertEquals(20, iceBoard.playerScore);
    }

    @Test
    public void testSpawnBonusIngredient() {
        iceBoard.isGameOver = true;
        iceBoard.spawnBonusIngredient();
        assertFalse(iceBoard.ingredients.stream().anyMatch(i -> i instanceof BonusIngredient));

        iceBoard.isGameOver = false;
        iceBoard.bonusCollected = false;
        iceBoard.spawnBonusIngredient();
        assertTrue(iceBoard.ingredients.stream().anyMatch(i -> i instanceof BonusIngredient));
    }
}