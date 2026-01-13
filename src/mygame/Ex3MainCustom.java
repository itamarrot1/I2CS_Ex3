package mygame;

import assignments.Ex3.Map2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Ex3MainCustom {
    public static void main(String[] args) throws InterruptedException {
        String mapFile = "src/mygame/map1.txt";
        // בתוך ה-Main, לפני יצירת המפה:
        int[][] rawMap = MapLoader.loadMap(mapFile); // המערך מהקובץ (שורות/עמודות)
        List<Ghost> ghostList = new ArrayList<>();



        Map2D mapObj = new assignments.Ex3.Map(rawMap);
        Board board = new Board(rawMap);
        board.debugPrint();

        // בחר תא התחלה חוקי
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

        // מיקום התחלתי של הרוחות (לא נוגעות לפקמן אוטומטי)


        Ghost ghost1 = new Ghost(5, 5, Color.RED,"מבוא לחישוב" );
        Ghost ghost2 = new Ghost(2, 2, Color.PINK,"אינפי");

        ghostList.add(ghost1);
        ghostList.add(ghost2);

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
                Thread.sleep(3000); // מחכה 3 שניות לפני סגירה
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
