package CMPT276.Group22;

import org.junit.Test;
import static org.junit.Assert.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import static org.mockito.Mockito.*;

public class RecipeVisualizationPanelExtendedTest {

    @Test
    public void testConstructorInitialization() {
        Board mockBoard = mock(FireBoard.class); // Use FireBoard mock for testing
        RecipeVisualizationPanel panel = new RecipeVisualizationPanel(mockBoard);

        assertNotNull(panel);
        assertNotNull(panel.getPreferredSize());
        assertEquals(new Dimension(180, panel.getPreferredSize().height), panel.getPreferredSize());
    }

    @Test
    public void testLoadImagesForFireBoard() {
        FireBoard mockBoard = mock(FireBoard.class);
        RecipeVisualizationPanel panel = new RecipeVisualizationPanel(mockBoard);

        // Access loaded images and validate
        assertNotNull(panel.baseImage);
        assertNotNull(panel.overlayImages);
        assertTrue(panel.overlayImages.size() > 0); // Ensure at least one overlay is loaded
    }

    @Test
    public void testUpdateIngredientState() {
        FireBoard mockBoard = mock(FireBoard.class);
        RecipeVisualizationPanel panel = new RecipeVisualizationPanel(mockBoard);

        panel.updateIngredientState("chili", true);

        // Validate ingredient state
        Map<String, Boolean> ingredientState = panel.ingredientCompleted;
        assertTrue(ingredientState.getOrDefault("chili", false));
    }

    @Test
    public void testUpdateBonusState() {
        FireBoard mockBoard = mock(FireBoard.class);
        RecipeVisualizationPanel panel = new RecipeVisualizationPanel(mockBoard);

        panel.updateBonusState(true);

        // Validate bonus state
        assertTrue(panel.bonusCollected);
    }

    @Test
    public void testPaintComponent() {
        FireBoard mockBoard = mock(FireBoard.class);
        RecipeVisualizationPanel panel = new RecipeVisualizationPanel(mockBoard);

        // Create a graphics context for testing
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        // Call paintComponent and ensure no exceptions are thrown
        panel.paintComponent(g);
    }
}