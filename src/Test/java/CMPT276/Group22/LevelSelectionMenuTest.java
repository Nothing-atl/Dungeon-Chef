package CMPT276.Group22;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Component;

import javax.swing.*;

public class LevelSelectionMenuTest {

    @Test
    public void testLevelSelectionMenuInitialization() {
        LevelSelectionMenu menu = new LevelSelectionMenu();

        assertEquals("Select Level", menu.getTitle());
        assertEquals(800, menu.getWidth());
        assertEquals(600, menu.getHeight());
        assertNotNull(menu.getContentPane());
    }

    @Test
    public void testButtonCreation() {
        LevelSelectionMenu menu = new LevelSelectionMenu();
        JPanel levelPanel = menu.getLevelPanel(); // Ensure the levelPanel is accessible
        assertNotNull("Level panel should not be null", levelPanel);

        int buttonCount = 0; // Track the number of buttons
        for (Component component : levelPanel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;

                // Verify button properties
                assertNotNull(button);
                assertFalse(button.isFocusPainted());
                assertFalse(button.isContentAreaFilled());
                assertFalse(button.isBorderPainted());

                buttonCount++;
            }
        }

        // Verify the correct number of buttons
        assertEquals("The level panel should contain 3 buttons", 3, buttonCount);
    }


    @Test
    public void testStartGameAction() {
        LevelSelectionMenu menu = new LevelSelectionMenu();

        // Ensure the levelPanel is accessible
        JPanel levelPanel = menu.getLevelPanel();
        assertNotNull("Level panel should not be null", levelPanel);

        int buttonIndex = 0;

        // Loop through components and find JButton instances
        for (Component component : levelPanel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;

                // Simulate button click
                button.doClick();

                // Verify action based on button index (adjust assertions for your logic)
                switch (buttonIndex) {
                    case 0:
                        // Verify behavior for Level 1 button
                       // System.out.println("Level 1 button clicked.");
                        break;
                    case 1:
                        // Verify behavior for Level 2 button
                        //System.out.println("Level 2 button clicked.");
                        break;
                    case 2:
                        // Verify behavior for Level 3 button
                        //System.out.println("Level 3 button clicked.");
                        break;
                    default:
                        fail("Unexpected button index.");
                }
                buttonIndex++;
            }
        }

        // Verify the total number of buttons tested
        assertEquals("All level buttons should be tested", 3, buttonIndex);
    }

}
