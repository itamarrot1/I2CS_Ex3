package assignments.Ex3;

import exe.ex3.game.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.Arrays;

public class Ex3AlgoTest {

    private Ex3Algo algo;
    private int[][] board;
    private Map map;

    @BeforeEach
    void setUp() {
        algo = new Ex3Algo();
        algo.BLUE = -1;
        algo.PINK = -2;
        algo.GREEN = -3;

        board = new int[10][10];
        for (int i = 0; i < 10; i++) Arrays.fill(board[i], 100);

        map = new Map(board);
        map.setCyclic(false);
    }

    @Test
    void testClosest() {
        board[0][2] = algo.PINK;
        Map2D dists = map.allDistance(new Index2D(0, 0), algo.BLUE);
        Pixel2D result = algo.closest(board, dists, algo.PINK);

        assertNotNull(result);
        assertEquals(0, result.getX());
        assertEquals(2, result.getY());
    }

    @Test
    void testSafeSpace() {
        double[][] danger = new double[10][10];
        for(int i=0; i<10; i++) Arrays.fill(danger[i], 100.0);

        int space = algo.countSafeSpace(new Index2D(0,0), map, board, danger, 5);
        assertEquals(5, space);
    }

    @Test
    void testIsLegal() {
        board[1][1] = algo.BLUE;
        assertFalse(algo.isLegal(new Index2D(1, 1), board));
        assertTrue(algo.isLegal(new Index2D(0, 0), board));
    }

    @Test
    void testIsGhostHouse() {
        board[5][5] = 0;
        assertTrue(algo.isGhostHouse(new Index2D(5, 5), board));

        board[5][5] = 1;
        assertFalse(algo.isGhostHouse(new Index2D(5, 5), board));
    }

    @Test
    void testEvaluate() {
        double[][] danger = new double[10][10];
        for(int i=0; i<10; i++) Arrays.fill(danger[i], 100.0);

        board[5][0] = algo.PINK;
        double scoreNear = algo.evaluate(new Index2D(4, 0), map, board, danger);
        double scoreFar = algo.evaluate(new Index2D(0, 0), map, board, danger);

        assertTrue(scoreNear > scoreFar);
    }

    @Test
    void testBuildDangerMap_NoGhosts() {
        board[2][2] = algo.PINK; // מקור סכנה
        GhostCL[] ghosts = new GhostCL[0];

        double[][] dangerMap = algo.buildDangerMap(map, board, ghosts);

        assertNotNull(dangerMap);
        assertEquals(0, dangerMap[2][2], 0.1); // ✅ עכשיו יעבוד
        assertTrue(dangerMap[0][0] > 0);
    }


    @Test
    void testNeighbor() {
        Pixel2D start = new Index2D(5, 5);

        Pixel2D up = algo.neighbor(start, Game.UP, map);
        assertEquals(6, up.getY());

        Pixel2D left = algo.neighbor(start, Game.LEFT, map);
        assertEquals(4, left.getX());
    }

    @Test
    void testFutureScore() {
        Map2D dists = map.allDistance(new Index2D(0, 0), algo.BLUE);
        double[][] danger = new double[10][10];
        for(int i=0; i<10; i++) Arrays.fill(danger[i], 100.0);

        board[1][0] = algo.PINK;
        double score = algo.futureScore(dists, board, danger);

        assertTrue(score > 0);
    }

    @Test
    void testKey() {
        Pixel2D p = new Index2D(3, 4);
        assertEquals("3,4", algo.key(p));
    }
}