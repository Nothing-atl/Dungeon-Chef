package CMPT276.Group22;

/**
 * Represents a regular ingredient within the game.
 * Regular ingredients provide standard rewards upon collection.
 */
public class RegularIngredient extends Ingredient {
    public RegularIngredient(Coordinate position, int value, String name, Board board) {
        super(position, value, name, board);
    }
    
    @Override
    public void onCollected(Board board) {
        board.increasePlayerScore(value);

        board.addCollectedIngredient(this.getName());
    }
}



