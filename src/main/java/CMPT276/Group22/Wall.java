package CMPT276.Group22;

/**
 * Represents a wall entity on the game board.
 * Acts as an impassable obstacle for player and enemy movement.
 */
public class Wall extends Entity {

    // Constructor to set the position of the wall
    public Wall(Coordinate position) {
        super(position);
    }
}
