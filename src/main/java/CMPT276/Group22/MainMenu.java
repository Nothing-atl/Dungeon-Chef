package CMPT276.Group22;

import javax.swing.*;
import java.awt.*;

/**
 * The main menu interface for the Dungeon Chef game.
 * Provides options to start game, view instructions, and exit.
 */
public class MainMenu extends JFrame {
    private static final String BACKGROUND_IMAGE = "/assets/character/Designer.jpeg";
    private static final String START_GAME_IMAGE = "/assets/character/start-game.png";
    private static final String INSTRUCTIONS_IMAGE = "/assets/character/instructions.png";
    private static final String EXIT_GAME_IMAGE = "/assets/character/exit-game.png";
    private static final String INSTRUCTION_SCREEN_IMAGE = "/assets/character/instruction.png";
    private static final String MENU_MUSIC = "menu_music";
    private static final String BUTTON_CLICK_SOUND = "button_click";

    private final SoundManager soundManager;
    private final JPanel menuPanel = new JPanel();
    private final JLabel titleLabel = new JLabel("DUNGEON CHEF");

    JButton startGameButton;
    JButton instructionButton;
    JButton exitButton;

    private ExitHandler exitHandler = () -> System.exit(0); // Default behavior

    public MainMenu() {
        // Sound Manager Initialization
        soundManager = SoundManager.getInstance();

        initializeFrame();
        initializeBackground();
        initializeMenuPanel();
        initializeButtons();

        soundManager.playBackgroundMusic(MENU_MUSIC);
    }

    private void initializeFrame() {
        setTitle("Dungeon Chef");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initializeBackground() {
        JLabel background = createBackgroundImage(BACKGROUND_IMAGE);
        background.setLayout(new BorderLayout());
        add(background);
        background.add(menuPanel, BorderLayout.CENTER);
    }

    private JLabel createBackgroundImage(String imagePath) {
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource(imagePath));
        JLabel background = new JLabel(backgroundImage);
        background.setLayout(new BorderLayout());
        return background;
    }

    private void initializeMenuPanel() {
        menuPanel.setOpaque(false); // Make the panel transparent
        menuPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        menuPanel.add(titleLabel, gbc);
    }

    private void initializeButtons() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Start Game Button
        startGameButton = createButton(START_GAME_IMAGE, 200, 60, this::startGame);
        gbc.gridy = 1;
        menuPanel.add(startGameButton, gbc);

        // Instructions Button
        instructionButton = createButton(INSTRUCTIONS_IMAGE, 200, 60, this::showInstructions);
        gbc.gridy = 2;
        menuPanel.add(instructionButton, gbc);

        // Exit Game Button
        exitButton = createButton(EXIT_GAME_IMAGE, 200, 60, exitHandler::exit);
        gbc.gridy = 3;
        menuPanel.add(exitButton, gbc);
    }

    private JButton createButton(String imagePath, int width, int height, Runnable action) {
        ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaledImage));
        button.setPreferredSize(new Dimension(width, height));
        button.setFocusPainted(false); // Remove focus border
        button.setContentAreaFilled(false); // Transparent background
        button.setBorderPainted(false); // Remove button border
        button.addActionListener(e -> {
            soundManager.playSound(BUTTON_CLICK_SOUND);
            action.run();
        });
        return button;
    }

    public void setExitHandler(ExitHandler handler) {
        this.exitHandler = handler;
    }

    private void startGame() {
        this.setVisible(false);
        LevelSelectionMenu levelSelectionMenu = new LevelSelectionMenu();
        levelSelectionMenu.setVisible(true);
    }

    private void showInstructions() {
        ImageIcon instructionImage = new ImageIcon(getClass().getResource(INSTRUCTION_SCREEN_IMAGE));
        JOptionPane.showMessageDialog(
                this,
                new JLabel(instructionImage),
                "Instructions",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void dispose() {
        soundManager.stopBackgroundMusic();
        super.dispose();
    }

    /**
     * Functional interface for handling exit behavior.
     */
    @FunctionalInterface
    public interface ExitHandler {
        void exit();
    }
    
    
}

