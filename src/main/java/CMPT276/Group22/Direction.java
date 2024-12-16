package CMPT276.Group22;

/**
 * Enum representing possible movement directions within the game.
 * Provides a set of constants to define the direction of entity movement.
 */
public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public Direction oppositeDirection() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
        }
        return null;
    }
}