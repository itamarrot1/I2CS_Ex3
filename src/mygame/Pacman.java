package mygame;

import java.util.List;
import assignments.Ex3.Map2D;

/**
 * Represents the Pacman character, focusing on state management and rendering.
 * This class uses a delegated AI "brain" to handle movement logic.
 */
public class Pacman implements PacmanInterface {
    private int row, col;
    private long superModeStartTime = 0;
    private static final long SUPER_DURATION = 8000;
    private static final String IMAGE_PATH = "src/mygame/my_photo.png";
    private final PacmanAI brain;


    /**
     * Constructs a new Pacman at the specified starting coordinates.
     * @param row The starting row index.
     * @param col The starting column index.
     */
    public Pacman(int row, int col) {
        this.row = row;
        this.col = col;
        this.brain = new PacmanAI();
    }

    /**
     * Renders the Pacman image on the game board.
     * Displays a visual orange circle when in super mode.
     * @param board The board object used for coordinate mapping.
     */
    @Override
    public void draw(Board board) {
        double cellW = 1.0 / board.getWidth();
        double cellH = 1.0 / board.getHeight();
        double px = col * cellW + cellW / 2;
        double py = 1 - (row * cellH + cellH / 2);
        double size = Math.min(cellW, cellH) * 0.8;

        if (isSuper()) {
            StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
            StdDraw.setPenRadius(0.01);
            StdDraw.circle(px, py, size / 1.5);
        }
        StdDraw.picture(px, py, IMAGE_PATH, size, size);
    }

    /**
     * Executes a move by delegating pathfinding to the PacmanAI.
     * @param board  The board object to update visual state.
     * @param map    The logical 2D map for environment checks.
     * @param ghosts The list of ghosts to interact with.
     */
    @Override
    public void move(Board board, Map2D map, List<Ghost> ghosts) {
        int[] nextPos = brain.computeNextMove(this.row, this.col, map, ghosts, isSuper());

        this.row = nextPos[0];
        this.col = nextPos[1];

        updateGameState(board, map, ghosts);
    }

    /**
     * Handles food consumption, super mode activation, and ghost collisions.
     * @param board  The board to clear consumed items.
     * @param map    The map to update pixel values.
     * @param ghosts The list of ghosts for collision checks.
     */
    private void updateGameState(Board board, Map2D map, List<Ghost> ghosts) {
        int cellValue = map.getPixel(this.row, this.col);
        if (cellValue > 1) {
            // Value 3 represents super food
            if (cellValue == 3) this.superModeStartTime = System.currentTimeMillis();
            map.setPixel(this.row, this.col, 0);
            board.setCell(this.col, this.row, 0);
        }

        for (Ghost g : ghosts) {
            if (g.isAlive() && g.getY() == this.row && g.getX() == this.col) {
                if (isSuper()) {
                    g.die();
                } else {
                    System.out.println("GAME OVER!");
                }
            }
        }
    }

    /**
     * Checks if the super mode duration is still active.
     * @return true if Pacman is currently in super mode.
     */
    @Override
    public boolean isSuper() {
        return (System.currentTimeMillis() - superModeStartTime < SUPER_DURATION);
    }

    /** @return The current row position. */
    public int getRow() { return row; }

    /** @return The current column position. */
    public int getCol() { return col; }
}