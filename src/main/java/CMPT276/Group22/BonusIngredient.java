package CMPT276.Group22;

import javax.swing.Timer;

/**
 * Represents a bonus ingredient in the game.
 * Bonus ingredients provide extra points to the player upon collection.
 */
public class BonusIngredient extends Ingredient {
    private javax.swing.Timer expirationTimer;
    private final int duration;

    public BonusIngredient(Coordinate position, int value, String name, int duration, Board board) {
        super(position, value, name, board);
        this.duration = duration;
        startExpirationTimer();
    }

    private void startExpirationTimer() {
        expirationTimer = new javax.swing.Timer(duration, e -> {
            System.out.println("Timer expired, calling removeIngredient");
            if (board != null) {
                board.removeIngredient(BonusIngredient.this);
            }
            expirationTimer.stop();
        });
        expirationTimer.setRepeats(false);
        expirationTimer.start();
    }

    @Override
    public void onCollected(Board board) {
        if (expirationTimer != null) {
            expirationTimer.stop();
        }
        board.handleBonusCollection(this);  // First handle bonus collection
        board.collectIngredient(this);      // Then collect the ingredient
        
        // Update visualization panel
        GameBoard gameBoard = board.getGameBoard();
        if (gameBoard != null) {
            RecipeVisualizationPanel visPanel = gameBoard.getRecipeVisualizationPanel();
            if (visPanel != null) {
                visPanel.updateBonusState(true);
            }
        }
    }
}