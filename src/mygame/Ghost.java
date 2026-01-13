package mygame;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

/**
 * Represents an enemy Ghost in the game.
 * Ghosts move semi-randomly and have a respawn mechanic after being defeated.
 */
public class Ghost implements GhostInterface {
    private int x, y;
    private final int startX, startY; // Store initial position for respawn
    private Color color;
    private String name;
    private boolean isAlive = true;
    private long deathTime = 0;
    private static final long RESPAWN_TIME = 3000; // 5 seconds in milliseconds
    private Random rand = new Random();

    /**
     * Constructs a new Ghost.
     * @param startX Initial column index.
     * @param startY Initial row index.
     * @param color The color of the ghost's body.
     * @param name The name displayed above the ghost.
     */
    public Ghost(int startX, int startY, Color color, String name) {
        this.x = startX;
        this.y = startY;
        this.startX = startX; // Save for respawn
        this.startY = startY; // Save for respawn
        this.color = color;
        this.name = name;
    }

    /**
     * Renders the ghost on the screen if it is alive.
     * @param boardWidth The number of columns in the grid.
     * @param boardHeight The number of rows in the grid.
     */
    public void draw(int boardWidth, int boardHeight) {
        if (!isAlive) return;

        double cellW = 1.0 / boardWidth;
        double cellH = 1.0 / boardHeight;
        double px = x * cellW + cellW / 2;
        double py = 1 - (y * cellH + cellH / 2);

        StdDraw.setPenColor(this.color);
        double r = Math.min(cellW, cellH) * 0.4;
        StdDraw.filledCircle(px, py, r);
        StdDraw.filledRectangle(px, py - r/2, r, r/2);

        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 16));
        StdDraw.text(px, py + r + 0.02, this.name);
    }

    /**
     * Updates the ghost status. If dead, checks if it's time to respawn.
     * If alive, moves randomly to an adjacent cell.
     * @param board The game board used for collision detection.
     */
    public void move(Board board) {
        if (!isAlive) {
            checkRespawn();
            return;
        }

        int[] dx = {0, 1, 0, -1};
        int[] dy = {-1, 0, 1, 0};

        int dir = rand.nextInt(4);
        for (int i = 0; i < 4; i++) {
            int nextDir = (dir + i) % 4;
            int nextCol = x + dx[nextDir];
            int nextRow = y + dy[nextDir];

            if (nextRow >= 0 && nextRow < board.getHeight() &&
                    nextCol >= 0 && nextCol < board.getWidth()) {

                if (board.getCell(nextCol, nextRow) != 1) {
                    this.x = nextCol;
                    this.y = nextRow;
                    return;
                }
            }
        }
    }

    /**
     * Checks if the required time has passed since death to bring the ghost back.
     */
    private void checkRespawn() {
        if (System.currentTimeMillis() - deathTime >= RESPAWN_TIME) {
            this.isAlive = true;
            this.x = startX; // Reset to start position
            this.y = startY;
        }
    }

    /**
     * Sets the ghost's status to dead and records the time of death.
     */
    public void die() {
        this.isAlive = false;
        this.deathTime = System.currentTimeMillis();
    }

    /** @return true if the ghost is alive. */
    public boolean isAlive() { return isAlive; }
    /** @return current X. */
    public int getX() { return x; }
    /** @return current Y. */
    public int getY() { return y; }
}