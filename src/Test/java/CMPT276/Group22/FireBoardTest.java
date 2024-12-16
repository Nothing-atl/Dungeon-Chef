package CMPT276.Group22;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.awt.Image;
import javax.swing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FireBoardTest {
    private FireBoard fireBoard;

    @Before
    public void setUp() {
        fireBoard = new FireBoard();
    }

    @Test
    public void testInitializeIngredients() {
        int totalIngredients = fireBoard.ingredients.size();
        int expectedTotal = FireBoard.BROTH_TARGET + FireBoard.CHILI_TARGET + FireBoard.MEAT_TARGET;
        
        assertEquals("Should have correct number of ingredients", expectedTotal, totalIngredients);
        
        long brothCount = countIngredientsOfType("broth");
        long chiliCount = countIngredientsOfType("chili");
        long meatCount = countIngredientsOfType("meat");
        
        assertEquals("Should have correct number of broth", FireBoard.BROTH_TARGET, brothCount);
        assertEquals("Should have correct number of chili", FireBoard.CHILI_TARGET, chiliCount);
        assertEquals("Should have correct number of meat", FireBoard.MEAT_TARGET, meatCount);
    }

    private long countIngredientsOfType(String type) {
        return fireBoard.ingredients.stream()
            .filter(i -> i instanceof RegularIngredient)
            .map(i -> (RegularIngredient) i)
            .filter(i -> i.getName().equals(type))
            .count();
    }

    @Test
    public void testGetRequiredIngredients() {
        List<String> required = fireBoard.getRequiredIngredients();
        
        assertNotNull("Required ingredients list should not be null", required);
        assertEquals("Should have 3 required ingredients", 3, required.size());
        assertTrue("Should contain broth", required.contains("broth"));
        assertTrue("Should contain chili", required.contains("chili"));
        assertTrue("Should contain meat", required.contains("meat"));
    }

    @Test
    public void testInitializeEnemies() {
        assertEquals("Should have correct number of static enemies", 4, fireBoard.staticEnemies.size());
        assertEquals("Should have correct number of moving enemies", 1, fireBoard.movingEnemies.size());
        
        for (StaticEnemy enemy : fireBoard.staticEnemies) {
            assertTrue("Static enemy should be within grid bounds", isPositionValid(enemy.getPosition()));
        }
        
        for (MovingEnemy enemy : fireBoard.movingEnemies) {
            assertTrue("Moving enemy should be within grid bounds", isPositionValid(enemy.getPosition()));
        }
    }

    private boolean isPositionValid(Coordinate pos) {
        return pos.getRow() >= 0 && pos.getRow() < FireBoard.GRID_HEIGHT &&
               pos.getCol() >= 0 && pos.getCol() < FireBoard.GRID_WIDTH;
    }

    @Test
    public void testInitializeBarriers() {
        assertEquals("Should have correct number of barriers", 30, fireBoard.barriers.size());
        
        for (Barrier barrier : fireBoard.barriers) {
            assertTrue("Barrier should be within grid bounds", isPositionValid(barrier.getPosition()));
            assertEquals("Grid should contain barrier", 
                        barrier, 
                        fireBoard.grid[barrier.getPosition().getRow()][barrier.getPosition().getCol()]);
        }
    }

    @Test
    public void testBonusIngredientLifecycle() {
        fireBoard.isGameOver = false;
        fireBoard.isGameWon = false;
        fireBoard.bonusCollected = false;

        fireBoard.spawnBonusIngredient();
        
        boolean hasBonusIngredient = fireBoard.ingredients.stream()
            .anyMatch(i -> i instanceof BonusIngredient && "ice-tea".equals(i.getName()));
        assertTrue("Should spawn bonus ice tea", hasBonusIngredient);
        
        BonusIngredient bonus = (BonusIngredient) fireBoard.ingredients.stream()
            .filter(i -> i instanceof BonusIngredient)
            .findFirst()
            .orElseThrow();
        
        fireBoard.handleBonusCollection(bonus);
        
        assertTrue("Should mark bonus as collected", fireBoard.bonusCollected);
        assertTrue("Should mark ice tea as collected", fireBoard.hasBonusIceTea());
        assertEquals("Should add bonus value to score", bonus.getValue(), fireBoard.playerScore);
    }

    @Test
    public void testIngredientCollection() {
        RegularIngredient broth = new RegularIngredient(
            new Coordinate(1, 1), 10, "broth", fireBoard);
        fireBoard.collectIngredient(broth);
        assertEquals("Should collect broth", 1, fireBoard.getBrothCollected());
        
        RegularIngredient chili = new RegularIngredient(
            new Coordinate(2, 2), 5, "chili", fireBoard);
        fireBoard.collectIngredient(chili);
        assertEquals("Should collect chili", 1, fireBoard.getChiliCollected());
        
        assertEquals("Should update total score", 15, fireBoard.playerScore);
    }

    @Test
    public void testInitialState() {
        assertEquals("Should start with 0 broth", 0, fireBoard.getBrothCollected());
        assertEquals("Should start with 0 chili", 0, fireBoard.getChiliCollected());
        assertEquals("Should start with 0 meat", 0, fireBoard.getMeatCollected());
        assertFalse("Should start with no bonus ice tea", fireBoard.hasBonusIceTea());
        assertEquals("Should start with 0 score", 0, fireBoard.playerScore);
        assertFalse("Should not start as game over", fireBoard.isGameOver);
        assertFalse("Should not start as won", fireBoard.isGameWon);
    }

    @Test
    public void testCheckWinCondition() {
        // Test not winning case
        fireBoard.checkWinCondition();
        assertFalse(fireBoard.isGameWon);
        
        // Test winning case
        for (int i = 0; i < FireBoard.BROTH_TARGET; i++) {
            fireBoard.collectedIngredients.add("broth");
        }
        for (int i = 0; i < FireBoard.CHILI_TARGET; i++) {
            fireBoard.collectedIngredients.add("chili");
        }
        for (int i = 0; i < FireBoard.MEAT_TARGET; i++) {
            fireBoard.collectedIngredients.add("meat");
        }
        
        fireBoard.checkWinCondition();
        assertTrue(fireBoard.isGameWon);
        assertTrue(fireBoard.isGameOver);
    }

    @Test
    public void testGetTargetForIngredient() {
        assertEquals(FireBoard.BROTH_TARGET, fireBoard.getTargetForIngredient("broth"));
        assertEquals(FireBoard.CHILI_TARGET, fireBoard.getTargetForIngredient("chili"));
        assertEquals(FireBoard.MEAT_TARGET, fireBoard.getTargetForIngredient("meat"));
        assertEquals(0, fireBoard.getTargetForIngredient("invalid"));
    }

    @Test
    public void testHandleBonusCollection() {
        BonusIngredient bonus = new BonusIngredient(new Coordinate(1, 1), 20, "ice-tea", 5000, fireBoard);
        
        // Test with regular ingredient
        RegularIngredient regular = new RegularIngredient(new Coordinate(1, 1), 10, "broth", fireBoard);
        fireBoard.handleBonusCollection(regular);
        assertFalse(fireBoard.bonusCollected);
        
        // Test with bonus ingredient
        fireBoard.handleBonusCollection(bonus);
        assertTrue(fireBoard.bonusCollected);
        assertTrue(fireBoard.hasBonusIceTea());
        assertEquals(20, fireBoard.playerScore);
    }

    @Test
    public void testCollectIngredient() {
        RegularIngredient broth = new RegularIngredient(new Coordinate(1, 1), 10, "broth", fireBoard);
        fireBoard.grid[1][1] = broth;
        fireBoard.ingredients.add(broth);
        
        fireBoard.collectIngredient(broth);
        assertEquals(1, fireBoard.getBrothCollected());
        assertEquals(10, fireBoard.playerScore);
        assertNull(fireBoard.grid[1][1]);
        assertFalse(fireBoard.ingredients.contains(broth));
    }

    @Test
    public void testSpawnBonusIngredient() {
        // Test when game is over
        fireBoard.isGameOver = true;
        fireBoard.spawnBonusIngredient();
        assertFalse(fireBoard.ingredients.stream().anyMatch(i -> i instanceof BonusIngredient));
        
        // Test normal spawn
        fireBoard.isGameOver = false;
        fireBoard.bonusCollected = false;
        fireBoard.spawnBonusIngredient();
        assertTrue(fireBoard.ingredients.stream().anyMatch(i -> i instanceof BonusIngredient));
    }

}