package mygame;

import java.util.List;
import assignments.Ex3.*;

/**
 * Detailed interface for Pacman's decision-making algorithms.
 */
public interface PacmanLogic {

    /**
     * The primary method to get the next coordinates.
     */
    int[] computeNextMove(int curRow, int curCol, Map2D map, List<Ghost> ghosts, boolean isSuper);

    /**
     * Evaluates a specific position and returns a quality score.
     */
    double evaluate(Pixel2D pos, Map2D map, double[][] dangerMap, boolean isSuper);

    /**
     * Identifies the safest and most dangerous areas on the map.
     */
    double[][] buildDangerMap(Map2D map, List<Ghost> ghosts);

    /**
     * Locates the nearest objective (food/fruit).
     */
    Pixel2D findClosestFood(Map2D map, Map2D distMap);
}