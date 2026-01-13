package mygame;

import assignments.Ex3.Map2D;

import java.awt.Color;

public class Board {
    private int[][] grid;
    private int rows;
    private int cols;

    public Board(int[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
    }

    public int getWidth() { return cols; }
    public int getHeight() { return rows; }

    // תיקון: תמיד שורה (row) אז עמודה (col)
    public int getCell(int col, int row) {
        return grid[row][col];
    }

    // תיקון: תמיד שורה (row) אז עמודה (col)
    public void setCell(int col, int row, int value) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            grid[row][col] = value;
        }
    }

    public void draw() {
        StdDraw.clear(Color.BLACK);
        double cellWidth = 1.0 / cols;
        double cellHeight = 1.0 / rows;

        for (int i = 0; i < rows; i++) { // i רץ על השורות
            for (int j = 0; j < cols; j++) { // j רץ על העמודות
                double x = j * cellWidth + cellWidth / 2;
                double y = 1 - (i * cellHeight + cellHeight / 2);

                // שימוש עקבי ב-grid[i][j] שזה [row][col]
                switch (grid[i][j]) {
                    case 0:
                        StdDraw.setPenColor(Color.BLACK);
                        StdDraw.filledSquare(x, y, cellWidth / 2);
                        break;
                    case 1: // קיר
                        StdDraw.setPenColor(Color.BLUE);
                        StdDraw.filledSquare(x, y, cellWidth / 2);
                        break;
                    case 2: // אוכל
                        StdDraw.setPenColor(Color.YELLOW);
                        StdDraw.filledCircle(x, y, Math.min(cellWidth, cellHeight) * 0.1);
                        break;
                    case 3: // פרי
                        StdDraw.setPenColor(Color.ORANGE);
                        StdDraw.filledCircle(x, y, Math.min(cellWidth, cellHeight) * 0.25);
                        break;
                }

            }
        }
    }
    public void debugPrint() {
        System.out.println("--- Board Grid Debug (Rows: " + rows + ", Cols: " + cols + ") ---");
        for (int i = 0; i < rows; i++) {
            System.out.print("Row " + i + ": ");
            for (int j = 0; j < cols; j++) {
                int val = grid[i][j];
                if (val == 1) System.out.print("# "); // קיר
                else if (val == 2) System.out.print(". "); // אוכל
                else if (val == 3) System.out.print("* "); // פרי
                else System.out.print("  "); // ריק (0)
            }
            System.out.println();
        }
        System.out.println("-----------------------------------------------------");
    }
    public boolean isGameOver(Map2D map) {
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                int cell = map.getPixel(x, y);
                // אם מצאנו לפחות נקודה אחת (2) או פרי אחד (3)
                if (cell == 2 || cell == 3) {
                    return false; // המשחק לא נגמר
                }
            }
        }
        return true; // לא נמצא אוכל, הניצחון!
    }
}