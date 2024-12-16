package CMPT276.Group22;

/**
 * Represents an ingredient that can be collected within the game.
 * Ingredients contribute to the player's score and progress when collected.
 */
public abstract class Ingredient extends Entity {
    protected int value;
    protected String name;
    protected Board board; // Reference to the Board instance

    public Ingredient(Coordinate position, int value, String name, Board board) {
        super(position);
        this.value = value;
        this.name = name;
        this.board = board;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    /**
     * Defines behavior when the ingredient is collected by the player.
     * This method should be implemented by subclasses.
     */
    public abstract void onCollected(Board board);
}




