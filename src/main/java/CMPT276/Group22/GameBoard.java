package CMPT276.Group22;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Main game board container managing game state, UI elements, and player interaction.
 * Handles score tracking, timer management, and game completion states.
 */
public class GameBoard extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color TOP_PANEL_COLOR = new Color(50, 50, 50);
    private static final int STATS_PANEL_WIDTH = 200;
    private static final int STATS_PANEL_HEIGHT = 100;
    private static final int TOP_PANEL_HEIGHT = 40;
    private static final Font STATS_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font INGREDIENT_FONT = new Font("Arial", Font.PLAIN, 11);

    private Board currentBoard;
    private JPanel ingredientsPanel;
    private JPanel gameStatsPanel;   
    private JPanel topPanel;          
    private JButton retryButton;
    private JButton menuButton;
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private javax.swing.Timer gameTimer;
    private JPanel ingredientTrackingPanel;
    private int secondsElapsed;
    private Map<String, JLabel> ingredientLabels;
    private long startTime;
    private boolean gameOverDialogShown = false;

    /**
     * Creates new game window with specified board type.
     * Initializes UI components, timer, and score tracking.
     * @param boardType type of board to create ("fire", "ice", or "earth")
     */
    public GameBoard(String boardType) {
        setTitle("Dungeon Chef");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
    
        ingredientLabels = new HashMap<>();
        
        currentBoard = BoardFactory.createBoard(boardType);
        if (currentBoard == null) {
            throw new IllegalStateException("Failed to create board of type: " + boardType);
        }
        currentBoard.setGameBoard(this);
    
        initializeTopPanel();
        initializeGameStatsPanel();
        initializeIngredientsPanel();
        initializeTimer();
    
        startTime = System.currentTimeMillis();
    
        JPanel rightPanel = createRightPanel();
    
        add(topPanel, BorderLayout.NORTH);
        add(currentBoard, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        SwingUtilities.invokeLater(() -> currentBoard.requestFocusInWindow());
    }
    
    private void initializeTopPanel() {
        topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(TOP_PANEL_COLOR);
        topPanel.setPreferredSize(new Dimension(getWidth(), TOP_PANEL_HEIGHT));
    
        // Menu and retry buttons
        menuButton = new JButton("Pause");
        menuButton.setFocusable(false);
        menuButton.addActionListener(e -> showMenu());
    
        retryButton = new JButton("Retry");
        retryButton.setFocusable(false);
        retryButton.addActionListener(e -> resetGame());
        retryButton.setVisible(false);
    
        // Add point information labels
        JLabel pointsInfo = new JLabel();
        pointsInfo.setForeground(Color.WHITE);
        
        // Set text based on board type
        if (currentBoard instanceof FireBoard) {
            pointsInfo.setText("  Points: Broth +10 | Chili +5 | Meat +7 | BONUS: Ice Tea +20 | Enemy -10");
        } else if (currentBoard instanceof IceBoard) {
            pointsInfo.setText("  Points: Banana +5 | Ice Cream +10 | Cherry +7 | Whip Cream +8 | BONUS: Milk Shake +20 | Enemy -10");
        } else if (currentBoard instanceof EarthBoard) {
            pointsInfo.setText("  Points: Dal +7 | Potato +5 | Carrot +5 | Onion +5 | BONUS: Rice +20| Enemy -10");
        }
    
        // Add all components to the panel
        topPanel.add(menuButton);
        topPanel.add(retryButton);
        topPanel.add(pointsInfo);
    }
    
    private void initializeGameStatsPanel() {
        gameStatsPanel = new JPanel();
        gameStatsPanel.setLayout(new BoxLayout(gameStatsPanel, BoxLayout.Y_AXIS));
        gameStatsPanel.setBorder(BorderFactory.createTitledBorder("Game Stats"));
        gameStatsPanel.setBackground(BACKGROUND_COLOR);

        scoreLabel = new JLabel("Score: 0");
        timerLabel = new JLabel("Time: 0:00");

        scoreLabel.setFont(STATS_FONT);
        timerLabel.setFont(STATS_FONT);
        
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        gameStatsPanel.add(Box.createVerticalStrut(5));
        gameStatsPanel.add(scoreLabel);
        gameStatsPanel.add(Box.createVerticalStrut(10));
        gameStatsPanel.add(timerLabel);
        gameStatsPanel.add(Box.createVerticalStrut(5));

        gameStatsPanel.setPreferredSize(new Dimension(STATS_PANEL_WIDTH, STATS_PANEL_HEIGHT));
    }


    private void initializeIngredientsPanel() {
        ingredientsPanel = new JPanel();
        ingredientsPanel.setLayout(new BoxLayout(ingredientsPanel, BoxLayout.Y_AXIS));
        ingredientsPanel.setBorder(BorderFactory.createTitledBorder("Required Ingredients"));
        ingredientsPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel ingredientTrackingPanel = new JPanel();
        ingredientTrackingPanel.setLayout(new BoxLayout(ingredientTrackingPanel, BoxLayout.Y_AXIS));
        ingredientTrackingPanel.setBackground(BACKGROUND_COLOR);
        
        this.ingredientTrackingPanel = ingredientTrackingPanel;
        
        RecipeVisualizationPanel recipeVisPanel = new RecipeVisualizationPanel(currentBoard);
        
        ingredientTrackingPanel.setPreferredSize(new Dimension(STATS_PANEL_WIDTH, 150));
        ingredientTrackingPanel.setMaximumSize(new Dimension(STATS_PANEL_WIDTH, 150));
        ingredientsPanel.add(ingredientTrackingPanel);
        
        ingredientsPanel.add(Box.createVerticalStrut(10));
        ingredientsPanel.add(recipeVisPanel);
        
        ingredientsPanel.setPreferredSize(new Dimension(STATS_PANEL_WIDTH, 400));
    }

    private void updateTimerDisplay() {
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        timerLabel.setText(String.format("Time: %d:%02d", minutes, seconds));
    }
    
    public void updateScoreDisplay() {
        scoreLabel.setText("Score: " + currentBoard.playerScore);
        // Remove the timer update from here since it's handled by the timer itself
    }
    
    private void initializeTimer() {
        secondsElapsed = 0;
        gameTimer = new javax.swing.Timer(1000, e -> {
            secondsElapsed++;
            updateTimerDisplay();
        });
        gameTimer.start();
    }

    private int elapsedTimeInSeconds() {
        return secondsElapsed;
    }

    /**
     * Shows game completion dialog with final score and time.
     * Stops game timer and prevents multiple dialog displays.
     */
    public void showGameOverDialog() {
        if (!gameOverDialogShown) {
            gameOverDialogShown = true;
            // Stop the game timer
            if (gameTimer != null) {
                gameTimer.stop();
            }

            // Create a custom dialog
            JDialog dialog = new JDialog(this, "Game Over", true);
            dialog.setLayout(new BorderLayout());

            // Create message panel
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
            
            // Add game over message and stats
            String status = currentBoard.isGameWon ? "You Win!" : "Game Over!";
            JLabel statusLabel = new JLabel(status);
            JLabel scoreLabel = new JLabel("Final Score: " + currentBoard.playerScore);
            JLabel timeLabel = new JLabel("Time: " + elapsedTimeInSeconds() + " seconds");
            
            // Center align the labels
            statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Add some padding
            messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
            
            messagePanel.add(statusLabel);
            messagePanel.add(Box.createRigidArea(new Dimension(0, 10)));
            messagePanel.add(scoreLabel);
            messagePanel.add(Box.createRigidArea(new Dimension(0, 10)));
            messagePanel.add(timeLabel);

            // Create button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            
            // Create buttons
            JButton restartButton = new JButton("Restart");
            JButton menuButton = new JButton("Main Menu");
            
            // Add action listeners
            restartButton.addActionListener(e -> {
                dialog.dispose();
                resetGame();
                gameOverDialogShown = false;
            });
            
            menuButton.addActionListener(e -> {
                dialog.dispose();
                dispose(); // Close the game window
                new MainMenu().setVisible(true);
            });
            
            // Add buttons to panel
            buttonPanel.add(restartButton);
            buttonPanel.add(menuButton);
            
            // Add panels to dialog
            dialog.add(messagePanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            // Set dialog properties
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.setSize(300, 200);
            dialog.setLocationRelativeTo(this);
            dialog.setResizable(false);
            
            // Show dialog
            dialog.setVisible(true);
        }
    }

    public void showRetryButton() {
        gameTimer.stop();
        retryButton.setVisible(true);

        // Show game over message with time
        showGameOverDialog();
    }

    private void resetGame() {
        // Remove current board
        remove(currentBoard);
    
        // Create new board of same type
        String boardType = getCurrentBoardType();
        switch (boardType) {
            case "fire":
                SoundManager.getInstance().playBackgroundMusic("fire_music");
                break;
            case "ice":
                SoundManager.getInstance().playBackgroundMusic("ice_music");
                break;
            case "earth":
                SoundManager.getInstance().playBackgroundMusic("earth_music");
                break;
        }
        currentBoard = BoardFactory.createBoard(boardType);
        currentBoard.setGameBoard(this);
    
        // Add new board
        add(currentBoard, BorderLayout.CENTER);
    
        // Clear and reinitialize tracking panel
        ingredientTrackingPanel.removeAll();
    
        // Reset game stats
        secondsElapsed = 0;
        updateTimerDisplay();
        gameTimer.restart();
    
        // Hide retry button
        retryButton.setVisible(false);
    
        // Create new recipe visualization panel
        RecipeVisualizationPanel newRecipeVisPanel = new RecipeVisualizationPanel(currentBoard);
        
        // Rebuild ingredients panel
        ingredientsPanel.removeAll();
        ingredientsPanel.add(ingredientTrackingPanel);
        ingredientsPanel.add(Box.createVerticalStrut(10));
        ingredientsPanel.add(newRecipeVisPanel);
    
        // Reset displays
        updateScoreDisplay();
        
        // Initialize ingredient display with empty counts
        Map<String, Integer> emptyCollectedCounts = new HashMap<>();
        updateIngredientDisplay(emptyCollectedCounts);
    
        // Update layout
        revalidate();
        repaint();
    
        // Make sure board has focus
        currentBoard.requestFocusInWindow();
    }

    private void showMenu() {
        gameTimer.stop(); 
        pauseTimer();
        
        if (currentBoard != null) {
            currentBoard.isPaused = true;
        }
    
        String[] options = {"Resume", "Restart", "Main Menu", "Exit"};
        int choice = JOptionPane.showOptionDialog(this,
                "Game Menu",
                "Menu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
    
        switch (choice) {
            case 0: // Resume
                if (currentBoard != null) {
                    currentBoard.isPaused = false;  // Unpause the board
                }
                gameTimer.start();
                resumeTimer();  // This will resume time counting
                break;
            case 1: // Restart
                resetGame();
                break;
            case 2: // Main Menu
                dispose(); // Close current game window
                new MainMenu().setVisible(true);
                break;
            case 3: // Exit
                System.exit(0);
                break;
            default:  // If they close the dialog without selecting (X button)
                if (currentBoard != null) {
                    currentBoard.isPaused = false;  // Unpause the board
                }
                gameTimer.start();
                resumeTimer();
                break;
        }
    }

    private String getCurrentBoardType() {
        return currentBoard instanceof FireBoard ? "fire" :
               currentBoard instanceof IceBoard ? "ice" :
               currentBoard instanceof EarthBoard ? "earth" : "fire";
    }

    public void pauseTimer() {
        gameTimer.stop();
        SoundManager.getInstance().pauseBackgroundMusic();
    }

    public void resumeTimer() {
        gameTimer.start();
        SoundManager.getInstance().resumeBackgroundMusic();
    }

    @Override
    public void dispose() {
        SoundManager.getInstance().stopBackgroundMusic();
        super.dispose();
    }

    /**
     * Updates ingredient collection display with current counts.
     * Shows progress towards required ingredient targets.
     * @param collectedCounts map of ingredient names to collected quantities
     */
    public void updateIngredientDisplay(Map<String, Integer> collectedCounts) {
        // Clear only the ingredient tracking panel, not the entire ingredients panel
        ingredientTrackingPanel.removeAll();
        
        // Add ingredient progress based on board type
        if (currentBoard instanceof FireBoard) {
            addIngredientLabel("Broth", collectedCounts.getOrDefault("broth", 0), FireBoard.BROTH_TARGET);
            addIngredientLabel("Chili", collectedCounts.getOrDefault("chili", 0), FireBoard.CHILI_TARGET);
            addIngredientLabel("Meat", collectedCounts.getOrDefault("meat", 0), FireBoard.MEAT_TARGET);
        } else if (currentBoard instanceof IceBoard) {
            addIngredientLabel("Banana", collectedCounts.getOrDefault("banana", 0), IceBoard.BANANA_TARGET);
            addIngredientLabel("Ice Cream", collectedCounts.getOrDefault("icecream", 0), IceBoard.ICE_CREAM_TARGET);
            addIngredientLabel("Cherry", collectedCounts.getOrDefault("cherry", 0), IceBoard.CHERRY_TARGET);
            addIngredientLabel("Whip Cream", collectedCounts.getOrDefault("whipcream", 0), IceBoard.WHIP_CREAM_TARGET);
        } else if (currentBoard instanceof EarthBoard) {
            addIngredientLabel("Dal", collectedCounts.getOrDefault("dal", 0), EarthBoard.DAL_TARGET);
            addIngredientLabel("Potato", collectedCounts.getOrDefault("potato", 0), EarthBoard.POTATO_TARGET);
            addIngredientLabel("Carrot", collectedCounts.getOrDefault("carrot", 0), EarthBoard.CARROT_TARGET);
            addIngredientLabel("Onion", collectedCounts.getOrDefault("onion", 0), EarthBoard.ONION_TARGET);
            // addIngredientLabel("Drumstick", collectedCounts.getOrDefault("drumstick", 0), EarthBoard.DRUMSTICK_TARGET);
        }
        
        // Revalidate and repaint only the tracking panel
        ingredientTrackingPanel.revalidate();
        ingredientTrackingPanel.repaint();
    }
    
    private void addIngredientLabel(String name, int collected, int target) {
        JLabel label = new JLabel(String.format("%s: %d/%d", name, collected, target));
        label.setFont(INGREDIENT_FONT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        ingredientTrackingPanel.add(label);
        ingredientTrackingPanel.add(Box.createVerticalStrut(3));
    }

    public RecipeVisualizationPanel getRecipeVisualizationPanel() {
        for (Component c : ingredientsPanel.getComponents()) {
            if (c instanceof RecipeVisualizationPanel) {
                return (RecipeVisualizationPanel) c;
            }
        }
        return null;
    }
    
    public JPanel getIngredientTrackingPanel() {
        return ingredientTrackingPanel;
    }

    public int getSecondsElapsed() {
        return secondsElapsed;
    }

    public javax.swing.Timer getGameTimer() {
        return gameTimer;
    }
   
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        rightPanel.add(gameStatsPanel, BorderLayout.NORTH);
        rightPanel.add(ingredientsPanel, BorderLayout.CENTER);
        
        return rightPanel;
    }

}