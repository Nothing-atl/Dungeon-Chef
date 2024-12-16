package CMPT276.Group22;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays a visual representation of a recipe within the game.
 * Used for showing the ingredients and steps needed to complete a recipe.
 */
public class RecipeVisualizationPanel extends JPanel {
    private static final int VERTICAL_GAP = 0;
    private static final int BONUS_OFFSET = 15;
    private static final int MAX_PANEL_WIDTH = 180;

    private final Board currentBoard;
    final Map<String, Image> overlayImages = new HashMap<>();
    final Map<String, Boolean> ingredientCompleted = new HashMap<>();
    Image baseImage;
    private Image bonusImage;
    boolean bonusCollected;
    private int imageWidth;
    private int imageHeight;

    public RecipeVisualizationPanel(Board board) {
        this.currentBoard = board;
        this.bonusCollected = false;

        setBackground(new Color(240, 240, 240));
        loadImages();
        setPreferredSize(new Dimension(MAX_PANEL_WIDTH, calculatePanelHeight()));
        setMaximumSize(new Dimension(MAX_PANEL_WIDTH, calculatePanelHeight()));
    }

    /**
     * Loads images for the current board's recipe.
     */
    private void loadImages() {
        if (currentBoard instanceof FireBoard) {
            loadRecipeAssets("/assets/recipes/fire/", "hotpot",
                    List.of("broth", "chili", "meat"), "ice_tea");
        } else if (currentBoard instanceof IceBoard) {
            loadRecipeAssets("/assets/recipes/ice/", "bananasplit",
                    List.of("banana", "icecream", "cherry", "whipcream"), "milkshake");
        } else if (currentBoard instanceof EarthBoard) {
            loadRecipeAssets("/assets/recipes/earth/", "sambar",
                    List.of("dal", "potato", "carrot", "onion", "drumstick"), "rice");
        }
    }

    /**
     * Loads and scales all images for a given recipe.
     */
    private void loadRecipeAssets(String path, String recipeName, List<String> ingredients, String bonusName) {
        baseImage = loadAndScaleImage(path + recipeName + "_base.png");

        for (String ingredient : ingredients) {
            Image overlay = loadAndScaleImage(path + recipeName + "_" + ingredient + ".png");
            if (overlay != null) {
                overlayImages.put(ingredient, overlay);
                ingredientCompleted.put(ingredient, false);
            }
        }

        bonusImage = loadAndScaleImage(path + bonusName + ".png");
    }

    /**
     * Loads and scales an image from the specified path.
     */
    private Image loadAndScaleImage(String path) {
        Image image = loadImage(path);
        if (image != null) {
            double scale = (double) (MAX_PANEL_WIDTH - BONUS_OFFSET) / image.getWidth(null);
            imageWidth = (int) (image.getWidth(null) * scale);
            imageHeight = (int) (image.getHeight(null) * scale);
            return image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
        }
        return null;
    }

    /**
     * Loads an image from the specified path.
     */
    private Image loadImage(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) {
                return ImageIO.read(is);
            }
        } catch (IOException e) {
            System.err.println("Failed to load image: " + path);
        }
        return null;
    }

    /**
     * Updates the completion state of an ingredient.
     */
    public void updateIngredientState(String ingredient, boolean completed) {
        ingredientCompleted.put(ingredient.toLowerCase(), completed);
        revalidate();
        repaint();
    }

    /**
     * Updates the bonus collection state.
     */
    public void updateBonusState(boolean collected) {
        this.bonusCollected = collected;
        revalidate();
        repaint();
    }

    /**
     * Calculates the height of the panel based on image dimensions and gaps.
     */
    private int calculatePanelHeight() {
        return imageHeight * 2 + VERTICAL_GAP;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int x = (getWidth() - imageWidth) / 2;

        // Draw the base recipe image
        if (baseImage != null) {
            g.drawImage(baseImage, x, -20, imageWidth + 10, imageHeight + 10, this);
        }

        // Draw ingredient overlays
        overlayImages.forEach((ingredient, image) -> {
            if (ingredientCompleted.getOrDefault(ingredient, false)) {
                g.drawImage(image, x, -20, imageWidth + 10, imageHeight + 10, this);
            }
        });

        // Draw bonus image if collected
        if (bonusCollected && bonusImage != null) {
            g.drawImage(bonusImage, x, -20 + imageHeight + 5, imageWidth + 5, imageHeight, this);
        }
    }
}
