package CMPT276.Group22;

/**
 * Represents a base enemy in the game.
 * Enemies serve as obstacles that the player must avoid.
 */
public abstract class Enemy extends Entity {

    public Enemy(Coordinate position) {
        super(position);
    }

    public abstract void move(Board board, Character character);  
}
