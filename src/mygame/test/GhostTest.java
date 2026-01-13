package mygame;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Color;

public class GhostTest {
    private Ghost ghost;
    private Board board;
    private int[][] grid;

    @BeforeEach
    void setUp() {
        // יצירת לוח פשוט 3x3 לבדיקות
        grid = new int[][]{
                {1, 1, 1},
                {1, 0, 1},
                {1, 1, 1}
        };
        board = new Board(grid);
        // יצירת רוח במרכז (1,1)
        ghost = new Ghost(1, 1, Color.RED, "Blinky");
    }

    @Test
    void testInitialStatus() {
        // בדיקה שהרוח נוצרת חיה ובמיקום הנכון
        assertTrue(ghost.isAlive(), "Ghost should be alive upon creation");
        assertEquals(1, ghost.getX(), "Initial X (col) should be 1");
        assertEquals(1, ghost.getY(), "Initial Y (row) should be 1");
    }

    @Test
    void testDie() {
        // בדיקה שפעולת die מעדכנת את הסטטוס
        ghost.die();
        assertFalse(ghost.isAlive(), "Ghost should be dead after calling die()");
    }

    @Test
    void testMoveBlockedByWalls() {
        // בלוח שיצרנו ב-setUp, הרוח מוקפת בקירות (1)
        // היא נמצאת ב-(1,1) וכל השאר קירות. היא לא אמורה לזוז.
        int initialX = ghost.getX();
        int initialY = ghost.getY();

        ghost.move(board);

        assertEquals(initialX, ghost.getX(), "Ghost should not move if surrounded by walls");
        assertEquals(initialY, ghost.getY(), "Ghost should not move if surrounded by walls");
    }

    @Test
    void testMoveDeadGhost() {
        // רוח מתה לא אמורה לזוז
        ghost.die();
        int initialX = ghost.getX();

        // נשנה את הלוח שיהיה פתוח
        grid[0][1] = 0;
        board = new Board(grid);

        ghost.move(board);
        assertEquals(initialX, ghost.getX(), "Dead ghost should not be able to move");
    }

    @Test
    void testMovementWithinBounds() {
        // בדיקה שהרוח לא יוצאת מגבולות המערך בלוח קטן
        for (int i = 0; i < 50; i++) { // נריץ הרבה פעמים כי התנועה רנדומלית
            ghost.move(board);
            assertTrue(ghost.getX() >= 0 && ghost.getX() < board.getWidth());
            assertTrue(ghost.getY() >= 0 && ghost.getY() < board.getHeight());
        }
    }
}