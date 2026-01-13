package mygame;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class Ghost {
    private int x, y; // x=col, y=row
    private Color color;
    private String name;
    private boolean isAlive = true;
    private Random rand = new Random();

    public Ghost(int startX, int startY, Color color, String name) {
        this.x = startX;
        this.y = startY;
        this.color = color;
        this.name = name;
    }

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

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 12));
        StdDraw.text(px, py + r + 0.02, this.name);
    }

    // הפונקציה המעודכנת שמקבלת Board
    public void move(Board board) {
        if (!isAlive) return;

        int[] dx = {0, 1, 0, -1}; // שינוי ב-X (עמודות)
        int[] dy = {-1, 0, 1, 0}; // שינוי ב-Y (שורות)

        int dir = rand.nextInt(4);
        for (int i = 0; i < 4; i++) {
            int nextDir = (dir + i) % 4;
            int nextCol = x + dx[nextDir];
            int nextRow = y + dy[nextDir];

            // בדיקה שהתנועה בגבולות הלוח
            if (nextRow >= 0 && nextRow < board.getHeight() &&
                    nextCol >= 0 && nextCol < board.getWidth()) {

                // שימוש ב-getCell של ה-Board כדי לבדוק אם יש קיר (1)
                if (board.getCell(nextCol, nextRow) != 1) {
                    this.x = nextCol;
                    this.y = nextRow;
                    return;
                }
            }
        }
    }

    public void die() {
        this.isAlive = false;
    }

    public boolean isAlive() { return isAlive; }
    public int getX() { return x; }
    public int getY() { return y; }
}