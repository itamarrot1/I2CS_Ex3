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

    public int getCell(int col, int row) {
        return grid[row][col];
    }

    public void setCell(int col, int row, int value) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            grid[row][col] = value;
        }
    }

    public void draw() {
        StdDraw.clear(Color.BLACK);
        double cellWidth = 1.0 / cols;
        double cellHeight = 1.0 / rows;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j * cellWidth + cellWidth / 2;
                double y = 1 - (i * cellHeight + cellHeight / 2);

                switch (grid[i][j]) {
                    case 0:
                        StdDraw.setPenColor(Color.BLACK);
                        StdDraw.filledSquare(x, y, cellWidth / 2);
                        break;
                    case 1: // wall
                        StdDraw.setPenColor(Color.BLUE);
                        StdDraw.filledSquare(x, y, cellWidth / 2);
                        break;
                    case 2: // food
                        StdDraw.setPenColor(Color.YELLOW);
                        StdDraw.filledCircle(x, y, Math.min(cellWidth, cellHeight) * 0.1);
                        break;
                    case 3: // fruit
                        StdDraw.setPenColor(Color.ORANGE);
                        StdDraw.filledCircle(x, y, Math.min(cellWidth, cellHeight) * 0.25);
                        break;
                }

            }
        }
    }

    public boolean isGameOver(Map2D map) {
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                int cell = map.getPixel(x, y);
                if (cell == 2 || cell == 3) {
                    return false;
                }
            }
        }
        return true;
    }
}