package mygame;

import java.util.List;
import assignments.Ex3.*;

/**
 * Logic-only class for Pacman pathfinding.
 * This class calculates the best moves based on environmental factors
 * such as ghost proximity and food distance.
 */
public class PacmanAI implements PacmanLogic {
    private int lastDir = -1;

    /**
     * Computes the next move for Pacman by evaluating all possible directions.
     * * @param curRow  Current row index of Pacman.
     * @param curCol  Current column index of Pacman.
     * @param map     The logical game map.
     * @param ghosts  List of all active ghosts in the game.
     * @param isSuper Whether Pacman is currently in super mode.
     * @return An integer array [nextRow, nextCol] representing the chosen move.
     */
    public int[] computeNextMove(int curRow, int curCol, Map2D map, List<Ghost> ghosts, boolean isSuper) {
        Pixel2D me = new Index2D(curRow, curCol);
        double[][] dangerMap = buildDangerMap(map, ghosts);

        int bestDir = -1;
        double maxScore = Double.NEGATIVE_INFINITY;

        for (int dir = 0; dir < 4; dir++) {
            Pixel2D next = getNeighbor(me, dir, map);
            if (map.getPixel(next.getX(), next.getY()) == 1) continue;

            double score = evaluate(next, map, dangerMap, isSuper);
            if (dir == lastDir) score += 20;

            if (score > maxScore) {
                maxScore = score;
                bestDir = dir;
            }
        }

        if (bestDir != -1) {
            lastDir = bestDir;
            Pixel2D finalPos = getNeighbor(me, bestDir, map);
            return new int[]{finalPos.getX(), finalPos.getY()};
        }
        return new int[]{curRow, curCol};
    }

    /**
     * Assigns a heuristic score to a potential position.
     * Higher scores indicate more desirable moves.
     * * @param pos       The target position to evaluate.
     * @param map       The logical game map.
     * @param dangerMap A 2D array representing distances to the nearest ghost.
     * @param isSuper   True if Pacman can eat ghosts, false otherwise.
     * @return A double representing the calculated score of the position.
     */
    public double evaluate(Pixel2D pos, Map2D map, double[][] dangerMap, boolean isSuper) {
        double score = 0;
        double distToGhost = dangerMap[pos.getX()][pos.getY()];

        if (isSuper) {
            // In Super Mode, chase ghosts
            if (distToGhost <= 5.1) score += (250000.0 / (distToGhost + 1));
        } else {
            // In Normal Mode, avoid ghosts
            if (distToGhost <= 1.1) return -999999.0;
            if (distToGhost <= 3.1) score -= 30000.0;
        }

        // Distance to the nearest food source
        Map2D distToFoodMap = map.allDistance(pos, 1);
        Pixel2D closestFood = findClosestFood(map, distToFoodMap);
        if (closestFood != null) {
            double d = distToFoodMap.getPixel(closestFood.getX(), closestFood.getY());
            score += 15000.0 / (d + 1);
        }

        // Direct point bonuses for the target cell
        int cell = map.getPixel(pos.getX(), pos.getY());
        if (cell == 2) score += 5000;  // Normal food
        if (cell == 3) score += 60000; // Super fruit

        return score;
    }

    /**
     * Calculates the neighboring cell based on the specified direction.
     * Supports wrap-around (teleporting) logic.
     * * @param p   The starting pixel.
     * @param dir Direction index (0: Up, 1: Right, 2: Down, 3: Left).
     * @param map The logical map for boundary dimensions.
     * @return A Pixel2D object representing the neighbor.
     */
    private Pixel2D getNeighbor(Pixel2D p, int dir, Map2D map) {
        int r = p.getX(), c = p.getY();
        if (dir == 0) r--; else if (dir == 1) c++; else if (dir == 2) r++; else if (dir == 3) c--;
        int h = map.getHeight(), w = map.getWidth();
        return new Index2D((r + h) % h, (c + w) % w);
    }

    /**
     * Builds a distance matrix indicating the distance from every cell to the nearest ghost.
     * * @param map    The logical game map.
     * @param ghosts List of active ghosts.
     * @return A 2D array where each cell contains the minimum distance to a ghost.
     */
    public double[][] buildDangerMap(Map2D map, List<Ghost> ghosts) {
        int h = map.getHeight(), w = map.getWidth();
        double[][] danger = new double[h][w];
        for (int i = 0; i < h; i++) for (int j = 0; j < w; j++) danger[i][j] = 99.0;

        for (Ghost g : ghosts) {
            if (!g.isAlive()) continue;
            Pixel2D ghostPos = new Index2D(g.getY(), g.getX());
            Map2D distFromGhost = map.allDistance(ghostPos, 1);
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    double d = distFromGhost.getPixel(i, j);
                    if (d != -1 && d < danger[i][j]) danger[i][j] = d;
                }
            }
        }
        return danger;
    }

    /**
     * Iterates through the map to find the nearest food item or super fruit.
     * * @param map     The logical game map.
     * @param distMap A map containing calculated distances from the current position.
     * @return The Pixel2D position of the closest food, or null if no food is available.
     */
    public Pixel2D findClosestFood(Map2D map, Map2D distMap) {
        Pixel2D closest = null;
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < map.getHeight(); i++) {
            for (int j = 0; j < map.getWidth(); j++) {
                int val = map.getPixel(i, j);
                double d = distMap.getPixel(i, j);
                if ((val == 2 || val == 3) && d >= 0 && d < minDist) {
                    minDist = d;
                    closest = new Index2D(i, j);
                }
            }
        }
        return closest;
    }
}