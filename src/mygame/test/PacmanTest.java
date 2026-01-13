package mygame;

import static org.junit.jupiter.api.Assertions.*;

import assignments.Ex3.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import assignments.Ex3.Map2D;


public class PacmanTest {
    private Pacman pacman;
    private Board board;
    private Map2D map;
    private List<Ghost> ghosts;

    @BeforeEach
    void setUp() {
        int[][] grid = {
                {1, 1, 1, 1, 1},
                {1, 0, 0, 0, 1},
                {1, 0, 1, 0, 1},
                {1, 0, 0, 0, 1},
                {1, 1, 1, 1, 1}
        };
        board = new Board(grid);
        map = new Map(grid);
        ghosts = new ArrayList<>();
        pacman = new Pacman(1, 1);
    }

    @Test
    void testSuperModeActivation() {
        map.setPixel(1, 2, 3);
        board.setCell(2, 1, 3);

        assertFalse(pacman.isSuper(), "Should start in normal mode");

        pacman.move(board, map, ghosts);

        assertEquals(1, pacman.getRow());
        assertEquals(2, pacman.getCol(), "Pacman should move to (1,2)");
        assertTrue(pacman.isSuper(), "Super Mode should be active after eating fruit");
        assertEquals(0, map.getPixel(1, 2), "Fruit should be removed from map");
    }

    @Test
    void testGhostAvoidance() {
        map.setPixel(2, 1, 2);
        ghosts.add(new Ghost(2, 1, Color.RED, "Blinky"));

        pacman.move(board, map, ghosts);

        assertNotEquals(2, pacman.getCol(), "Pacman should NOT move into the ghost");
    }

    @Test
    void testGhostEatingInSuperMode() {
        map.setPixel(1, 2, 3);
        pacman.move(board, map, ghosts);
        assertTrue(pacman.isSuper());

        map.setPixel(1, 3, 2);
        Ghost blinky = new Ghost(3, 1, Color.RED, "Blinky"); // col=3, row=1
        ghosts.add(blinky);

        pacman.move(board, map, ghosts);

        assertFalse(blinky.isAlive(), "Ghost should be dead after being eaten in Super Mode");
    }

    @Test
    void testEatLastDot() {
        for(int r=0; r<5; r++) for(int c=0; c<5; c++) if(map.getPixel(r,c)!=1) map.setPixel(r,c,0);
        map.setPixel(2, 1, 2);

        pacman.move(board, map, ghosts);

        assertEquals(2, pacman.getRow());
        assertEquals(1, pacman.getCol());
        assertEquals(0, map.getPixel(2, 1), "Last dot should be eaten");
    }
}