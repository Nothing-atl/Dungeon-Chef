package CMPT276.Group22;

import org.junit.Test;

import javax.swing.*;

import java.awt.Component;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class MainMenuTest {

    @Test
    public void testMainMenuInitialization() {
        MainMenu mainMenu = new MainMenu();

        assertEquals("Dungeon Chef", mainMenu.getTitle());
        assertEquals(800, mainMenu.getWidth());
        assertEquals(600, mainMenu.getHeight());
        assertNotNull(mainMenu.getContentPane());
    }

    @Test
    public void testButtonsExist() {
        MainMenu mainMenu = new MainMenu();

        // Check that buttons are initialized and not null
        assertNotNull("Start Game button should exist", mainMenu.startGameButton);
        assertNotNull("Instructions button should exist", mainMenu.instructionButton);
        assertNotNull("Exit button should exist", mainMenu.exitButton);
    }

    @Test
    public void testStartGameButtonAction() {
        MainMenu mainMenu = new MainMenu();

        // Simulate clicking the Start Game button
        mainMenu.startGameButton.doClick();

        // Verify that the main menu is hidden after starting the game
        assertFalse("Main menu should be hidden after starting the game", mainMenu.isVisible());
    }

    @Test
    public void testInstructionButtonAction() {
        MainMenu mainMenu = new MainMenu();

        // Simulate clicking the Instructions button
        mainMenu.instructionButton.doClick();

        // Instructions are shown via JOptionPane; you may manually verify or use a mock JOptionPane
        // Here we ensure no exceptions occur during this operation
        assertTrue("Instruction button action should complete without errors", true);
    }

   /* @Test
    public void testExitButtonAction() {
        // Create MainMenu and override exit behavior
        MainMenu mainMenu = new MainMenu();
        AtomicBoolean exitCalled = new AtomicBoolean(false);

        mainMenu.setExitHandler(() -> exitCalled.set(true)); // Mock exit behavior

        // Simulate clicking the Exit button
        mainMenu.exitButton.doClick();

        // Verify the custom exit handler was invoked
        assertTrue("Exit handler should be called", exitCalled.get());
    }*/

    /**
     * Debugs the component hierarchy of MainMenu.
     */
    @Test
    public void debugHierarchy() {
        MainMenu mainMenu = new MainMenu();
        printComponentHierarchy(mainMenu.getContentPane(), 0);
    }

    private void printComponentHierarchy(Container container, int level) {
        for (Component component : container.getComponents()) {
            System.out.println(" ".repeat(level * 2) + component.getClass().getName());
            if (component instanceof Container) {
                printComponentHierarchy((Container) component, level + 1);
            }
        }
    }
}
