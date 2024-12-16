package CMPT276.Group22;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class RecipeVisualizationPanelTest {

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
    public void testLoadImagesForIceBoard() {
        IceBoard mockBoard = mock(IceBoard.class);
        RecipeVisualizationPanel panel = new RecipeVisualizationPanel(mockBoard);

        // Validate base and overlay images
        assertNotNull(panel.baseImage);
        assertNotNull(panel.overlayImages);
        assertTrue(panel.overlayImages.size() > 0);
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

        // Create a BufferedImage to simulate Graphics
        BufferedImage bufferedImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bufferedImage.getGraphics();

        try {
            panel.paintComponent(graphics); // Test the painting logic
            assertTrue(true); // If no exception, the test passes
        } catch (Exception e) {
            fail("paintComponent threw an exception: " + e.getMessage());
        } finally {
            graphics.dispose();
        }
    }

}
