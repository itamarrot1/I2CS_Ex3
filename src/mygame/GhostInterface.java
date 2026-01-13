package mygame;

/**
 * Defines the contract for any ghost entity in the game.
 */
public interface GhostInterface {

    /**
     * Renders the ghost on the game screen.
     * @param boardWidth Number of columns.
     * @param boardHeight Number of rows.
     */
    void draw(int boardWidth, int boardHeight);

    /**
     * Updates the ghost's position based on its specific movement AI.
     * @param board The board to check for walls and boundaries.
     */
    void move(Board board);

    /**
     * Changes the ghost's state to dead/inactive.
     */
    void die();

    /**
     * @return true if the ghost is alive and active on the board.
     */
    boolean isAlive();

    /** @return current X (column) coordinate. */
    int getX();

    /** @return current Y (row) coordinate. */
    int getY();
}