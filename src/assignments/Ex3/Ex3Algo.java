package assignments.Ex3;

import exe.ex3.game.*;
import java.awt.*;
import java.util.*;

/**
 * Ex3Algo provides an AI implementation for Pacman.
 * It uses distance mapping, danger zones, and safe space evaluation
 * to determine the optimal move for survival and score maximization.
 */
public class Ex3Algo implements PacManAlgo {

    private int step = 0;
    public int BLUE, PINK, GREEN;
    private int lastDir = -1;

    /**
     * Provides basic info about the algorithm.
     * @return A string containing the ID and name of the AI.
     */
    @Override
    public String getInfo() {
        return "ID: " + GameInfo.MY_ID + " | Ultimate Survival AI";
    }

    /**
     * Main control loop for the Pacman move.
     * Evaluates all 4 directions and chooses the one with the highest calculated score.
     * @param game The current game status.
     * @return The integer representing the chosen direction.
     */
    @Override
    public int move(PacmanGame game) {
        int pac = 0;
        int[][] board = game.getGame(pac);
        Map map = new Map(board);
        map.setCyclic(GameInfo.CYCLIC_MODE);
        Pixel2D me = new Index2D(game.getPos(pac));

        if (step == 0) {
            BLUE  = Game.getIntColor(Color.BLUE, 0);
            PINK  = Game.getIntColor(Color.PINK, 0);
            GREEN = Game.getIntColor(Color.GREEN, 0);
        }

        GhostCL[] ghosts = game.getGhosts(pac);
        double[][] danger = buildDangerMap(map, board, ghosts);

        int bestDir = -1;
        double bestScore = Double.NEGATIVE_INFINITY;

        // לולאה אחת מאוחדת - קריטי למניעת "קיפאון"
        for (int dir : new int[]{Game.UP, Game.DOWN, Game.LEFT, Game.RIGHT}) {
            Pixel2D next = neighbor(me, dir, map);
            if (!isLegal(next, board)) continue;

            double score = evaluate(next, map, board, danger);

            // חישוב עומק - עוזר להחליט בין נתיבים
            Map2D d2 = map.allDistance(next, BLUE);
            score += 0.5 * futureScore(d2, board, danger);

            // בונוס התמדה חזק יותר - מונע רעידות מול רוח
            if (dir == lastDir) score += 500;

            if (score > bestScore) {
                bestScore = score;
                bestDir = dir;
            }
        }

        if (bestDir == -1) { // מוצא אחרון אם הכל מסוכן
            for (int d=0; d<4; d++) if (isLegal(neighbor(me, d, map), board)) return d;
        }

        lastDir = bestDir;
        step++;
        return bestDir;
    }

    /**
     * Builds a 2D map of the distance to the nearest non-eatable ghost.
     * @param map The map object for distance calculations.
     * @param board The current game board.
     * @param ghosts Array of ghosts current positions and status.
     * @return A 2D array of doubles representing danger distance.
     */
    public double[][] buildDangerMap(Map map, int[][] board, GhostCL[] ghosts) {
        int w = board.length, h = board[0].length;
        double[][] danger = new double[w][h];
        for (double[] r : danger) Arrays.fill(r, 99.0);

        if (ghosts != null && ghosts.length > 0) {
            for (int i = 0; i < ghosts.length; i++) {
                if (ghosts[i].remainTimeAsEatable(i) > 3.0) continue;
                Pixel2D gp = new Index2D(ghosts[i].getPos(i));
                Map2D dist = map.allDistance(gp, BLUE);
                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        double d = dist.getPixel(x, y);
                        if (d != -1) danger[x][y] = Math.min(danger[x][y], d);
                    }
                }
            }
        } else {
            // אין רוחות → בנה סכנה לפי צבעים בלוח בלבד
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    if (board[x][y] < 0) { // PINK/BLUE/GREEN
                        danger[x][y] = 0;
                    }
                }
            }
        }

        return danger;
    }

    /**
     * Calculates a heuristic score for a specific position.
     * @param pos The position to evaluate.
     * @param map Map helper object.
     * @param board Current board state.
     * @param danger Pre-calculated danger map.
     * @return The evaluation score.
     */
    public double evaluate(Pixel2D pos, Map map, int[][] board, double[][] danger) {
        double score = 0;
        int x = pos.getX(), y = pos.getY();
        double ghostDist = danger[x][y];

        // 1. הגנה אגרסיבית - בורח כבר ממרחק 2-3
        if (ghostDist <= 1.1) return -10000000.0;
        if (ghostDist <= 2.1) score -= 500000.0;
        if (ghostDist <= 3.1) score -= 100000.0;

        // 2. שטח בטוח - מונע כניסה למלכודות
        int safeSpace = countSafeSpace(pos, map, board, danger, 15);
        score += safeSpace * 2000;

        // 3. חיפוש אוכל - עם עדיפות חזקה לקרוב
        Map2D distMap = map.allDistance(pos, BLUE);
        Pixel2D pink = closest(board, distMap, PINK);

        if (pink != null) {
            double d = distMap.getPixel(pink.getX(), pink.getY());
            score += 200000.0 / (d + 1); // הגדלת המשקל של האוכל
        } else {
            score += ghostDist * 5000; // אם אין אוכל, פשוט תתרחק מהרוח
        }

        if (board[x][y] == PINK) score += 10000;

        return score;
    }

    /**
     * Counts reachable safe tiles using BFS.
     * @param start Starting position.
     * @param map Map object.
     * @param board Board state.
     * @param danger Danger map.
     * @param limit BFS depth limit.
     * @return Number of safe reachable tiles.
     */
    public int countSafeSpace(Pixel2D start, Map map, int[][] board, double[][] danger, int limit) {
        Queue<Pixel2D> q = new LinkedList<>();
        java.util.Map<String, Integer> dist = new HashMap<>();
        q.add(start);
        dist.put(key(start), 0);
        int count = 0;
        while (!q.isEmpty() && count < limit) {
            Pixel2D cur = q.poll();
            int d = dist.get(key(cur));
            count++;
            for (int dir : new int[]{0,1,2,3}) {
                Pixel2D n = neighbor(cur, dir, map);
                if (!isLegal(n, board) || dist.containsKey(key(n))) continue;
                // משבצת נחשבת בטוחה רק אם הרוח לא יכולה להגיע אליה לפנינו (או איתנו)
                if (danger[n.getX()][n.getY()] <= d + 1) continue;
                dist.put(key(n), d + 1);
                q.add(n);
            }
        }
        return count;
    }

    /**
     * Calculates future potential based on food density.
     * @param dist Distance map from a specific point.
     * @param board The board.
     * @param danger The danger map.
     * @return A double representing the potential future score.
     */
    public double futureScore(Map2D dist, int[][] board, double[][] danger) {
        double best = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == PINK) {
                    double d = dist.getPixel(i, j);
                    if (d != -1 && danger[i][j] > d + 2) {
                        best = Math.max(best, 100000 / (d + 1));
                    }
                }
            }
        }
        return best;
    }

    /**
     * Finds the closest Pixel2D of a specific color.
     * @param board The board.
     * @param dist Pre-calculated distances.
     * @param color The target color ID.
     * @return The closest Pixel2D of that color.
     */
    public Pixel2D closest(int[][] board, Map2D dist, int color) {
        Pixel2D best = null; double min = Double.MAX_VALUE;
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                if (board[i][j] == color && dist.getPixel(i, j) != -1 && dist.getPixel(i, j) < min) {
                    min = dist.getPixel(i, j); best = new Index2D(i, j);
                }
        return best;
    }

    /**
     * Logic for determining neighbor coordinates, including cyclic board handling.
     * @param p Current position.
     * @param dir Direction to check.
     * @param map Map helper.
     * @return The neighbor's position.
     */
    public Pixel2D neighbor(Pixel2D p, int dir, Map map) {
        int x = p.getX(), y = p.getY();
        if (dir == Game.UP) y++; else if (dir == Game.DOWN) y--;
        else if (dir == Game.LEFT) x--; else if (dir == Game.RIGHT) x++;
        int w = map.getMap().length, h = map.getMap()[0].length;
        return new Index2D((x + w) % w, (y + h) % h);
    }

    /**
     * Validates if a tile is walkable.
     * @param p Position to check.
     * @param board The game board.
     * @return true if legal, false if it's a wall or ghost house.
     */
    public boolean isLegal(Pixel2D p, int[][] board) {
        if (p.getX() < 0 || p.getX() >= board.length || p.getY() < 0 || p.getY() >= board[0].length) return false;
        return board[p.getX()][p.getY()] != BLUE && !isGhostHouse(p, board);
    }

    /**
     * Checks if a pixel is inside the ghost house area.
     * @param p Position to check.
     * @param board The board.
     * @return true if inside, false otherwise.
     */
    public boolean isGhostHouse(Pixel2D p, int[][] board) {
        int mx = board.length / 2, my = board[0].length / 2;
        return Math.abs(p.getX() - mx) < 3 && Math.abs(p.getY() - my) < 3 && board[p.getX()][p.getY()] == 0;
    }

    /**
     * Generates a string key for Pixel2D objects to use in Maps.
     * @param p The pixel.
     * @return A "x,y" string.
     */
    public String key(Pixel2D p) { return p.getX() + "," + p.getY(); }
}