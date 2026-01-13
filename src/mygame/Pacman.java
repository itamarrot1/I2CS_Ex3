package mygame;
import java.util.List;
import assignments.Ex3.Pixel2D;
import assignments.Ex3.Index2D;
import assignments.Ex3.Map2D;

public class Pacman {
    private int row;
    private int col;
    private int lastDir = -1;
    private long superModeStartTime = 0;
    private static final long SUPER_DURATION = 8000;
    private static final String IMAGE_PATH = "src/mygame/my_photo.png";

    public Pacman(int row, int col) {
        this.row = row;
        this.col = col;
    }

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

    public void move(Board board, Map2D map, List<Ghost> ghosts) {
        Pixel2D me = new Index2D(this.row, this.col);
        double[][] dangerMap = buildDangerMap(map, ghosts);

        int bestDir = -1;
        double maxScore = Double.NEGATIVE_INFINITY;

        for (int dir = 0; dir < 4; dir++) {
            Pixel2D next = getNeighbor(me, dir, map);
            if (map.getPixel(next.getX(), next.getY()) == 1) continue;

            double score = evaluate(next, map, dangerMap);
            if (dir == lastDir) score += 20;

            if (score > maxScore) {
                maxScore = score;
                bestDir = dir;
            }
        }

        if (bestDir != -1) {
            lastDir = bestDir;
            Pixel2D finalPos = getNeighbor(me, bestDir, map);
            this.row = finalPos.getX();
            this.col = finalPos.getY();

            int cellValue = map.getPixel(this.row, this.col);
            if (cellValue > 1) {
                if (cellValue == 3) this.superModeStartTime = System.currentTimeMillis();
                map.setPixel(this.row, this.col, 0);
                board.setCell(this.col, this.row, 0);
            }

            // לוגיקת אכילת רוחות
            for (Ghost g : ghosts) {
                if (g.isAlive() && g.getY() == this.row && g.getX() == this.col) {
                    if (isSuper()) {
                        g.die();
                    } else {
                        System.out.println("GAME OVER!");
                        // כאן אפשר להוסיף System.exit(0) לסיום המשחק
                    }
                }
            }
        }
    }

    private double evaluate(Pixel2D pos, Map2D map, double[][] dangerMap) {
        double score = 0;
        double distToGhost = dangerMap[pos.getX()][pos.getY()];
        boolean superActive = isSuper();

        if (superActive) {
            // רדיפה אחרי רוחות חיות בלבד
            if (distToGhost <= 5.1) score += (250000.0 / (distToGhost + 1));
        } else {
            if (distToGhost <= 1.1) return -999999.0;
            if (distToGhost <= 3.1) score -= 30000.0;
        }

        Map2D distToFoodMap = map.allDistance(pos, 1);
        Pixel2D closestFood = findClosestFood(map, distToFoodMap);
        if (closestFood != null) {
            double d = distToFoodMap.getPixel(closestFood.getX(), closestFood.getY());
            score += 15000.0 / (d + 1);
        }

        int cell = map.getPixel(pos.getX(), pos.getY());
        if (cell == 2) score += 5000;
        if (cell == 3) score += 60000;

        return score;
    }

    public boolean isSuper() {
        return (System.currentTimeMillis() - superModeStartTime < SUPER_DURATION);
    }

    private Pixel2D getNeighbor(Pixel2D p, int dir, Map2D map) {
        int r = p.getX(), c = p.getY();
        if (dir == 0) r--; else if (dir == 1) c++; else if (dir == 2) r++; else if (dir == 3) c--;
        int h = map.getHeight(), w = map.getWidth();
        return new Index2D((r + h) % h, (c + w) % w);
    }

    private double[][] buildDangerMap(Map2D map, List<Ghost> ghosts) {
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

    private Pixel2D findClosestFood(Map2D map, Map2D distMap) {
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

    public int getRow() { return row; }
    public int getCol() { return col; }
}