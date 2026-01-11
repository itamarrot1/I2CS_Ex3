package assignments.Ex3;

import exe.ex3.game.*;
import java.awt.*;
import java.util.*;

public class Ex3Algo implements PacManAlgo {

    private int step = 0;
    private int BLUE, PINK, GREEN;
    private int lastDir = -1;

    @Override
    public String getInfo() {
        return "ID: " + GameInfo.MY_ID + " | Smart Survival AI (DEBUG)";
    }

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

        System.out.println("Step " + step + " | Pacman at " + me.getX() + "," + me.getY());

        int bestDir = -1; // התחלה ללא כיוון
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int dir : new int[]{Game.UP, Game.DOWN, Game.LEFT, Game.RIGHT}) {
            Pixel2D next = neighbor(me, dir, map);
            if (!isLegal(next, board)) continue;

            // אם זה הכיוון החוקי הראשון, נשמור אותו כברירת מחדל
            if (bestDir == -1) bestDir = dir;

            double score = evaluate(next, map, board, danger);
            // ... שאר הלוגיקה
        }

        for (int dir : new int[]{Game.UP, Game.DOWN, Game.LEFT, Game.RIGHT}) {
            Pixel2D next = neighbor(me, dir, map);
            if (!isLegal(next, board)) continue;

            double score = evaluate(next, map, board, danger);

            Map2D d2 = map.allDistance(next, BLUE);
            score += 0.6 * futureScore(d2, board, danger);

            if (dir == lastDir) score += 200;

            System.out.println("  Dir " + dir + " -> next " + next.getX() + "," + next.getY() +
                    " | score=" + score);

            if (score > bestScore) {
                bestScore = score;
                bestDir = dir;
            }
        }

        System.out.println("Chosen dir: " + bestDir + " | Score: " + bestScore);
        lastDir = bestDir;
        step++;
        return bestDir;
    }

    private double[][] buildDangerMap(Map map, int[][] board, GhostCL[] ghosts) {
        int w = board.length, h = board[0].length;
        double[][] danger = new double[w][h];
        for (double[] r : danger) Arrays.fill(r, Double.POSITIVE_INFINITY);

        for (int i = 0; i < ghosts.length; i++) {
            if (ghosts[i].remainTimeAsEatable(i) > 2) continue;

            Pixel2D gp = new Index2D(ghosts[i].getPos(i));
            Map2D dist = map.allDistance(gp, BLUE);

            for (int x = 0; x < w; x++)
                for (int y = 0; y < h; y++) {
                    double d = dist.getPixel(x, y);
                    if (d != -1)
                        danger[x][y] = Math.min(danger[x][y], d);
                }
            System.out.println("  Ghost " + i + " at " + gp.getX() + "," + gp.getY());
        }
        return danger;
    }

    private double evaluate(Pixel2D pos, Map map, int[][] board, double[][] danger) {
        double score = 0;
        int x = pos.getX();
        int y = pos.getY();
        double ghostDist = danger[x][y];

        // 1. הישרדות מעל הכל - אם הרוח במרחק נגיעה, אל תלך לשם
        if (ghostDist <= 1) return -1e10;

        // 2. בדיקת שטח בטוח - שלא ניכנס לסימטה ללא מוצא
        // בתוך evaluate
        int safeSpace = countSafeSpace(pos, map, board, danger, 12);
// במקום לפסול, פשוט תוריד ציון. ככה אם אין ברירה, הוא יכנס למבוי סתום כדי לאכול נקודה.
        score += safeSpace * 1000;

        // 3. המטרה: הנקודה הוורודה הקרובה ביותר
        Map2D distMap = map.allDistance(pos, BLUE);
        Pixel2D pink = closest(board, distMap, PINK);

        if (pink != null) {
            double dToPink = distMap.getPixel(pink.getX(), pink.getY());
            // ככל שהמרחק קטן יותר, הציון גבוה יותר משמעותית
            score += 100000.0 / (dToPink + 1);
        } else {
            // אם אין נקודות וורודות - ניצחנו! (או שמחפשים ירוק לקינוח)
            return 1e11;
        }

        // 4. בונוס על הדרך: אם יש נקודה וורודה ממש במיקום הזה
        if (board[x][y] == PINK) {
            score += 50000;
        }

        // 5. עונש קטן על קרבה לרוחות (גם אם הן לא בטווח סכנה מיידי)
        if (ghostDist < 5) {
            score -= (10 - ghostDist) * 2000;
        }

        return score;
    }

    private double futureScore(Map2D dist, int[][] board, double[][] danger) {
        double best = 0;
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                if (board[i][j] == PINK && danger[i][j] > dist.getPixel(i, j) + 2)
                    best = Math.max(best, 200000 / (dist.getPixel(i, j) + 1));
        return best;
    }

    private int countSafeSpace(Pixel2D start, Map map, int[][] board,
                               double[][] danger, int limit) {
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
                String k = key(n);

                if (!isLegal(n, board) || dist.containsKey(k)) continue;
                if (danger[n.getX()][n.getY()] <= d + 2) continue;

                dist.put(k, d + 1);
                q.add(n);
            }
        }
        System.out.println("  Safe space from " + start.getX() + "," + start.getY() + " = " + count);
        return count;
    }

    private Pixel2D closest(int[][] board, Map2D dist, int color) {
        Pixel2D best = null;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                if (board[i][j] == color && dist.getPixel(i, j) != -1 &&
                        dist.getPixel(i, j) < min) {
                    min = dist.getPixel(i, j);
                    best = new Index2D(i, j);
                }
        return best;
    }

    private Pixel2D neighbor(Pixel2D p, int dir, Map map) {
        int x = p.getX(), y = p.getY();
        if (dir == Game.UP) y++;
        if (dir == Game.DOWN) y--;
        if (dir == Game.LEFT) x--;
        if (dir == Game.RIGHT) x++;

        int w = map.getMap().length, h = map.getMap()[0].length;
        return new Index2D((x + w) % w, (y + h) % h);
    }

    private boolean isLegal(Pixel2D p, int[][] board) {
        return board[p.getX()][p.getY()] != BLUE && !isGhostHouse(p, board);
    }

    private boolean isGhostHouse(Pixel2D p, int[][] board) {
        int mx = board.length / 2, my = board[0].length / 2;
        return Math.abs(p.getX() - mx) < 3 &&
                Math.abs(p.getY() - my) < 3 &&
                board[p.getX()][p.getY()] == 0;
    }

    private String key(Pixel2D p) {
        return p.getX() + "," + p.getY();
    }
}
