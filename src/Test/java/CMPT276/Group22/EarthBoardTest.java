package CMPT276.Group22;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;


public class EarthBoardTest {
    private EarthBoard earthBoard;

    @Before
    public void setUp() {
        earthBoard = new EarthBoard();
    }

    @Test
    public void testInitializeIngredients() {
        // Count initial ingredients after board initialization
        int totalIngredients = earthBoard.ingredients.size();
        int expectedTotal = EarthBoard.DAL_TARGET + EarthBoard.POTATO_TARGET +
                          EarthBoard.CARROT_TARGET + EarthBoard.ONION_TARGET;
        
        assertEquals("Should have correct number of ingredients", 
                    expectedTotal, totalIngredients);
        
        // Check if each type of ingredient exists
        long dalCount = countIngredientsOfType("dal");
        long potatoCount = countIngredientsOfType("potato");
        long carrotCount = countIngredientsOfType("carrot");
        long onionCount = countIngredientsOfType("onion");
        
        assertEquals("Should have correct number of dal", EarthBoard.DAL_TARGET, dalCount);
        assertEquals("Should have correct number of potato", EarthBoard.POTATO_TARGET, potatoCount);
        assertEquals("Should have correct number of carrot", EarthBoard.CARROT_TARGET, carrotCount);
        assertEquals("Should have correct number of onion", EarthBoard.ONION_TARGET, onionCount);
    }

    private long countIngredientsOfType(String type) {
        return earthBoard.ingredients.stream()
            .filter(i -> i instanceof RegularIngredient)
            .map(i -> (RegularIngredient) i)
            .filter(i -> i.getName().equals(type))
            .count();
    }

    @Test
    public void testGetRequiredIngredients() {
        List<String> required = earthBoard.getRequiredIngredients();
        
        assertNotNull("Required ingredients list should not be null", required);
        assertEquals("Should have 4 required ingredients", 4, required.size());
        assertTrue("Should contain dal", required.contains("dal"));
        assertTrue("Should contain potato", required.contains("potato"));
        assertTrue("Should contain carrot", required.contains("carrot"));
        assertTrue("Should contain onion", required.contains("onion"));
    }

    @Test
    public void testInitializeEnemies() {
        // Count enemies after board initialization
        assertEquals("Should have correct number of static enemies", 
                    6, earthBoard.staticEnemies.size());
        assertEquals("Should have correct number of moving enemies", 
                    3, earthBoard.movingEnemies.size());
        
        // Verify static enemy positions are valid
        for (StaticEnemy enemy : earthBoard.staticEnemies) {
            assertTrue("Static enemy should be within grid bounds", 
                      isPositionValid(enemy.getPosition()));
        }
        
        // Verify moving enemy positions are valid
        for (MovingEnemy enemy : earthBoard.movingEnemies) {
            assertTrue("Moving enemy should be within grid bounds", 
                      isPositionValid(enemy.getPosition()));
        }
    }

    private boolean isPositionValid(Coordinate pos) {
        return pos.getRow() >= 0 && pos.getRow() < EarthBoard.GRID_HEIGHT &&
               pos.getCol() >= 0 && pos.getCol() < EarthBoard.GRID_WIDTH;
    }

    @Test
    public void testInitializeBarriers() {
        assertEquals("Should have correct number of barriers", 
                    18, earthBoard.barriers.size());
        
        // Verify barrier positions
        for (Barrier barrier : earthBoard.barriers) {
            assertTrue("Barrier should be within grid bounds", 
                      isPositionValid(barrier.getPosition()));
            assertEquals("Grid should contain barrier", 
                        barrier, earthBoard.grid[barrier.getPosition().getRow()]
                                                [barrier.getPosition().getCol()]);
        }
    }

    @Test
    public void testBonusIngredientLifecycle() {
        earthBoard.isGameOver = false;
        earthBoard.isGameWon = false;
        earthBoard.bonusCollected = false;

        // Test spawning
        earthBoard.spawnBonusIngredient();
        
        boolean hasBonusIngredient = earthBoard.ingredients.stream()
            .anyMatch(i -> i instanceof BonusIngredient && "rice".equals(i.getName()));
        assertTrue("Should spawn bonus rice", hasBonusIngredient);
        
        // Test collection
        BonusIngredient bonus = (BonusIngredient) earthBoard.ingredients.stream()
            .filter(i -> i instanceof BonusIngredient)
            .findFirst()
            .orElseThrow();
        
        earthBoard.handleBonusCollection(bonus);
        
        assertTrue("Should mark bonus as collected", earthBoard.bonusCollected);
        assertTrue("Should mark rice as collected", earthBoard.hasBonusRice());
        assertEquals("Should add bonus value to score", bonus.getValue(), earthBoard.playerScore);
    }

    @Test
    public void testIngredientCollection() {
        RegularIngredient dal = new RegularIngredient(
            new Coordinate(1, 1), 7, "dal", earthBoard);
        earthBoard.collectIngredient(dal);
        assertEquals("Should collect dal", 1, earthBoard.getDalCollected());
        
        RegularIngredient potato = new RegularIngredient(
            new Coordinate(2, 2), 5, "potato", earthBoard);
        earthBoard.collectIngredient(potato);
        assertEquals("Should collect potato", 1, earthBoard.getPotatoCollected());
        
        assertEquals("Should update total score", 12, earthBoard.playerScore);
    }

    @Test
    public void testInitialState() {
        assertEquals("Should start with 0 dal", 0, earthBoard.getDalCollected());
        assertEquals("Should start with 0 potato", 0, earthBoard.getPotatoCollected());
        assertEquals("Should start with 0 carrot", 0, earthBoard.getCarrotCollected());
        assertEquals("Should start with 0 onion", 0, earthBoard.getOnionCollected());
        assertFalse("Should start with no bonus rice", earthBoard.hasBonusRice());
        assertEquals("Should start with 0 score", 0, earthBoard.playerScore);
        assertFalse("Should not start as game over", earthBoard.isGameOver);
        assertFalse("Should not start as won", earthBoard.isGameWon);
    }

    @Test
    public void testCheckWinCondition() {
        // Test not winning
        earthBoard.checkWinCondition();
        assertFalse(earthBoard.isGameWon);
        
        // Test winning
        for (int i = 0; i < EarthBoard.DAL_TARGET; i++) {
            earthBoard.collectedIngredients.add("dal");
        }
        for (int i = 0; i < EarthBoard.POTATO_TARGET; i++) {
            earthBoard.collectedIngredients.add("potato");
        }
        for (int i = 0; i < EarthBoard.CARROT_TARGET; i++) {
            earthBoard.collectedIngredients.add("carrot");
        }
        for (int i = 0; i < EarthBoard.ONION_TARGET; i++) {
            earthBoard.collectedIngredients.add("onion");
        }
        
        earthBoard.checkWinCondition();
        assertTrue(earthBoard.isGameWon);
        assertTrue(earthBoard.isGameOver);
    }

    @Test
    public void testGetTargetForIngredient() {
        assertEquals(EarthBoard.DAL_TARGET, earthBoard.getTargetForIngredient("dal"));
        assertEquals(EarthBoard.POTATO_TARGET, earthBoard.getTargetForIngredient("potato"));
        assertEquals(EarthBoard.CARROT_TARGET, earthBoard.getTargetForIngredient("carrot"));
        assertEquals(EarthBoard.ONION_TARGET, earthBoard.getTargetForIngredient("onion"));
        assertEquals(0, earthBoard.getTargetForIngredient("invalid"));
    }

    @Test
    public void testCollectIngredient() {
        Ingredient[] testIngredients = {
            new RegularIngredient(new Coordinate(1, 1), 6, "dal", earthBoard),
            new RegularIngredient(new Coordinate(1, 2), 2, "potato", earthBoard),
            new RegularIngredient(new Coordinate(1, 3), 4, "carrot", earthBoard),
            new RegularIngredient(new Coordinate(1, 4), 5, "onion", earthBoard),
            new BonusIngredient(new Coordinate(1, 5), 20, "rice", 5000, earthBoard)
        };

        for (Ingredient ingredient : testIngredients) {
            earthBoard.collectIngredient(ingredient);
        }

        assertEquals(1, earthBoard.getDalCollected());
        assertEquals(1, earthBoard.getPotatoCollected());
        assertEquals(1, earthBoard.getCarrotCollected());
        assertEquals(1, earthBoard.getOnionCollected());
        assertTrue(earthBoard.hasBonusRice());
    }

    @Test
    public void testSpawnBonusIngredient() {
        // Test game over case
        earthBoard.isGameOver = true;
        earthBoard.spawnBonusIngredient();
        assertFalse(earthBoard.ingredients.stream().anyMatch(i -> i instanceof BonusIngredient));

        // Test normal spawn
        earthBoard.isGameOver = false;
        earthBoard.bonusCollected = false;
        earthBoard.spawnBonusIngredient();
        assertTrue(earthBoard.ingredients.stream().anyMatch(i -> i instanceof BonusIngredient));
    }

    @Test 
    public void testHandleBonusCollection() {
        BonusIngredient bonus = new BonusIngredient(new Coordinate(1, 1), 20, "rice", 5000, earthBoard);
        RegularIngredient regular = new RegularIngredient(new Coordinate(1, 1), 10, "dal", earthBoard);

        // Test regular ingredient
        earthBoard.handleBonusCollection(regular);
        assertFalse(earthBoard.bonusCollected);

        // Test bonus ingredient
        earthBoard.handleBonusCollection(bonus);
        assertTrue(earthBoard.bonusCollected);
        assertTrue(earthBoard.hasBonusRice());
        assertEquals(20, earthBoard.playerScore);
    }
}