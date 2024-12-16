package CMPT276.Group22;

/**
 * Represents a static enemy within the game.
 * Static enemies remain fixed in place, creating obstacles for the player.
 */
public class StaticEnemy extends Entity {
    private int penaltyAmount;

    public StaticEnemy(Coordinate position, int penaltyAmount) {
        super(position);
        this.penaltyAmount = penaltyAmount;
    }

    public int getPenaltyAmount() {
        return penaltyAmount;
    }
}

