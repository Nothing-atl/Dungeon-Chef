package CMPT276.Group22;

/**
 * Represents a general entity within the game.
 * Base class for different types of objects, including enemies, barriers, and items.
 */
public abstract class Entity {

    protected Coordinate position;

    // Constructor to set the initial position of the entity
    public Entity(Coordinate position) {
        this.position = position;
    }

    // Getter for the entity's position
    public Coordinate getPosition() {
        return position;
    }

    // Setter for the entity's position
    public void setPosition(Coordinate position) {
        this.position = position;
    }

    // This method can be overridden by child classes for custom behavior
    public void interact() {
        // Default interaction behavior (can be empty or customized in subclasses)
    }
}
