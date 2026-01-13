package mygame;

import java.util.List;
import assignments.Ex3.Map2D;

/**
 * Defines the essential behaviors for a Pacman entity in the game.
 */
public interface PacmanInterface {

    /**
     * Renders the Pacman character.
     * @param board The game board for coordinate mapping.
     */
    void draw(Board board);

    /**
     * Updates Pacman's position and state based on game logic.
     * @param board The visual board.
     * @param map The logical map.
     * @param ghosts List of active ghosts.
     */
    void move(Board board, Map2D map, List<Ghost> ghosts);

    /**
     * @return true if Pacman is currently in powered-up super mode.
     */
    boolean isSuper();

    /**
     * @return The current row (Y-coordinate) of Pacman.
     */
    int getRow();

    /**
     * @return The current column (X-coordinate) of Pacman.
     */
    int getCol();
}
