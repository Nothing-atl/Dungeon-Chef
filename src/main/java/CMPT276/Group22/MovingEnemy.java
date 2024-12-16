package CMPT276.Group22;

/**
 * Represents an enemy that moves within the game.
 * Moving enemies follow specific patterns to challenge the player.
 */
public class MovingEnemy extends Entity {
    private Coordinate previousPosition;

    public MovingEnemy(Coordinate position) {
        super(position);
        this.previousPosition = position;
    }

    public void moveTowardsPlayer(int playerX, int playerY, Entity[][] grid) {
        int dx = playerX - position.getCol();
        int dy = playerY - position.getRow();
        int stepX = Integer.signum(dx);
        int stepY = Integer.signum(dy);

        // Try diagonal movement first
        if (Math.abs(dx) > 0 && Math.abs(dy) > 0) {
            attemptMove(stepX, stepY, grid);
        } else if (Math.abs(dx) > Math.abs(dy)) {
            attemptMove(stepX, 0, grid); // Horizontal movement
        } else {
            attemptMove(0, stepY, grid); // Vertical movement
        }
    }


    private void attemptMove(int deltaX, int deltaY, Entity[][] grid) {
        int newX = position.getCol() + deltaX;
        int newY = position.getRow() + deltaY;
        if (isValidMove(newX, newY, grid)) {
            position = new Coordinate(newY, newX);
        }
    }

    private boolean isValidMove(int x, int y, Entity[][] grid) {
        return x >= 0 && x < 800 / Board.CELL_SIZE &&  // Use board's cell size constant
               y >= 0 && y < 600 / Board.CELL_SIZE &&
               !(grid[y][x] instanceof Wall) &&
               !(grid[y][x] instanceof Barrier) &&
               !(grid[y][x] instanceof MovingEnemy) &&  // Prevent enemies from combining
               !(grid[y][x] instanceof StaticEnemy) &&  // Prevent moving into static enemies
               !(grid[y][x] instanceof Ingredient);     // Prevent moving onto ingredients
    }
    

    public void revertMove() {
        position = previousPosition;
    }
}

