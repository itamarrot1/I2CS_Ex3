package mygame;

import assignments.Ex3.Map2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Ex3MainCustom {
    public static void main(String[] args) throws InterruptedException {
        String mapFile = "src/mygame/map1.txt";
        int[][] rawMap = MapLoader.loadMap(mapFile);
        List<Ghost> ghostList = new ArrayList<>();



        Map2D mapObj = new assignments.Ex3.Map(rawMap);
        Board board = new Board(rawMap);

        int startRow = 1;
        int startCol = 1;
        if (mapObj.getPixel(startCol, startRow) == 1) {
            outer:
            for (int r = 0; r < mapObj.getHeight(); r++) {
                for (int c = 0; c < mapObj.getWidth(); c++) {
                    if (mapObj.getPixel(c, r) != 1) {
                        startCol = c;
                        startRow = r;
                        break outer;
                    }
                }
            }
        }

        Pacman pacman = new Pacman(startRow, startCol);



        Ghost ghost1 = new Ghost(5, 5, Color.RED,"מבוא לחישוב" );
        Ghost ghost2 = new Ghost(2, 2, Color.PINK,"אינפי");
        Ghost ghost3 = new Ghost(2, 2, Color.cyan,"לוגיקה");

        ghostList.add(ghost1);
        ghostList.add(ghost2);
        ghostList.add(ghost3);

        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.enableDoubleBuffering();

        while (true) {
            if (board.isGameOver(mapObj)) {
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.GREEN);
                Font font = new Font("Arial", Font.BOLD, 30);
                StdDraw.setFont(font);
                StdDraw.text(0.5, 0.5, "YOU WON");
                StdDraw.show();
                Thread.sleep(3000);
                System.exit(0);
            }
            for (Ghost g : ghostList) {
                g.move(board);
            }
            pacman.move(board, mapObj, ghostList);

            StdDraw.clear(Color.BLACK);
            board.draw();
            pacman.draw(board);
            for (Ghost g : ghostList) {
                g.draw(board.getWidth(), board.getHeight());
            }

            StdDraw.show();
            Thread.sleep(150);
        }
    }
}
