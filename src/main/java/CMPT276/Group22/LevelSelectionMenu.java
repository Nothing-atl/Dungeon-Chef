package CMPT276.Group22;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Level selection interface allowing players to choose between Fire, Ice, and Earth boards.
 * Presents visual buttons for each level type and handles level initialization.
 */
public class LevelSelectionMenu extends JFrame {
    private static final String BACKGROUND_IMAGE = "/assets/character/MainMenu.png";
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 60;
    private static final int SPACING = 10;

    private final Map<String, String> boardTypeMap = new LinkedHashMap<>(Map.of(
            "Level 1", "fire",
            "Level 2", "ice",
            "Level 3", "earth"
    ));

    private final Map<String, String> musicMap = new LinkedHashMap<>(Map.of(
            "Level 1", "fire_music",
            "Level 2", "ice_music",
            "Level 3", "earth_music"
    ));


    private JPanel levelPanel;

    public LevelSelectionMenu() {
        initializeFrame();
        initializeBackground();
        initializeLevelPanel();
    }

    private void initializeFrame() {
        setTitle("Select Level");
        setResizable(false);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void initializeBackground() {
        JLabel background = new JLabel(new ImageIcon(getClass().getResource(BACKGROUND_IMAGE)));
        background.setLayout(new BorderLayout());
        add(background);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false); // Transparent to show background
        background.add(centerPanel, BorderLayout.CENTER);

        levelPanel = new JPanel();
        levelPanel.setLayout(new BoxLayout(levelPanel, BoxLayout.Y_AXIS));
        levelPanel.setOpaque(false); // Transparent to show background
        centerPanel.add(levelPanel, new GridBagConstraints());
    }
    
    

    private void initializeLevelPanel() {
        boardTypeMap.keySet().forEach(level -> {
            JButton button = createLevelButton(level);
            levelPanel.add(button);
            levelPanel.add(Box.createRigidArea(new Dimension(0, SPACING))); // Add spacing
        });
    }

    private JButton createLevelButton(String level) {
        String imagePath = switch (level) {
            case "Level 1" -> "/assets/character/fire.png";
            case "Level 2" -> "/assets/character/ice1.png";
            case "Level 3" -> "/assets/character/earth.png";
            default -> throw new IllegalArgumentException("Unknown level: " + level);
        };

        JButton button = createButtonWithImage(imagePath);
        button.addActionListener(e -> startGame(level));
        return button;
    }

    private JButton createButtonWithImage(String imagePath) {
        ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
        Image scaledImage = icon.getImage().getScaledInstance(BUTTON_WIDTH, BUTTON_HEIGHT, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaledImage));
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setFocusPainted(false); // Remove focus border
        button.setContentAreaFilled(false); // Transparent background
        button.setBorderPainted(false); // Remove button border
        return button;
    }

    private void startGame(String level) {
        SoundManager.getInstance().playSound("button_click");

        SwingUtilities.invokeLater(() -> {
            String boardType = boardTypeMap.getOrDefault(level, "fire");
            String music = musicMap.getOrDefault(level, "fire_music");

            SoundManager.getInstance().playBackgroundMusic(music);
            GameBoard gameBoard = new GameBoard(boardType);
            gameBoard.setVisible(true);
            dispose();  // Close the level selection menu
        });
    }

    public JPanel getLevelPanel() {
        return levelPanel;
    }
}
