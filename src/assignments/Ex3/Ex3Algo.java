package assignments.Ex3;
import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;


/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 *
 * This code is a very simple example (random-walk algorithm).
 * Your task is to implement (here) your PacMan algorithm.
 */
public class Ex3Algo implements PacManAlgo{
	private int _count;
    private int _blue, _pink, _black, _green;
	public Ex3Algo() {
        _count=0;
    }


	@Override
	/**
	 *  Add a short description for the algorithm as a String.
	 */
	public String getInfo() {
		String info =
                "ID: " + GameInfo.MY_ID +
                        ", Scenario: " + GameInfo.CASE_SCENARIO +
                        ", Cyclic: " + GameInfo.CYCLIC_MODE +
                        ", DT : " + GameInfo.DT +
                        ", RESOLUTION_NORM : " + GameInfo.RESOLUTION_NORM +
                        ", RANDOM_SEED :  " + GameInfo.RANDOM_SEED +
                        ", ALGO :  " + GameInfo.ALGO ;
        return info;
	}


    @Override
    public int move(PacmanGame game) {
        if(_count==0 || _count==300) {
            int code = 0;
            int[][] board = game.getGame(0);
            printBoard(board);
             _blue = Game.getIntColor(Color.BLUE, code);
             _pink = Game.getIntColor(Color.PINK, code);
             _black = Game.getIntColor(Color.BLACK, code);
             _green = Game.getIntColor(Color.GREEN, code);
            System.out.println("Blue=" + _blue + ", Pink=" + _pink + ", Black=" + _black + ", Green=" + _green);
            String pos = game.getPos(code).toString();
            System.out.println("Pacman coordinate: "+pos);
            GhostCL[] ghosts = game.getGhosts(code);
            printGhosts(ghosts);
        }

        int code = 0;
        int[][] board = game.getGame(code);

        Map myMap = new Map(board);
        myMap.setCyclic(GameInfo.CYCLIC_MODE);

        Pixel2D current = new Index2D(game.getPos(code));
        Map2D dist = myMap.allDistance(current, _blue);

        double ghostDist = getClosestGhostDistance(game, dist);
        if (ghostDist < 5.0) { // אם רוח קרובה מדי
            System.out.println("--- סכנה! רוח במרחק: " + ghostDist + " ---");
            // כאן בעתיד נחזיר פונקציית escape. כרגע נחזור לרנדומלי
            _count++;
            return randomDir();
        }

        Pixel2D target = findClosestGreen(board, dist, _green);

        if (target != null) {
            Pixel2D[] path = myMap.shortestPath(current, target, _blue);
            if (path != null && path.length > 1) {
                Pixel2D nextStep = path[1];
                _count++;
                return getDirFromPixels(current, nextStep, myMap);
            }
        }

        // אם הגענו לכאן, סימן שלא נמצא מסלול חכם
        _count++;
        int dir = randomDir();
        return dir;
    }


	private static void printBoard(int[][] b) {
		for(int y =0;y<b[0].length;y++){
			for(int x =0;x<b.length;x++){
				int v = b[x][y];
				System.out.print(v+"\t");
			}
			System.out.println();
		}
	}


	private static void printGhosts(GhostCL[] gs) {
		for(int i=0;i<gs.length;i++){
			GhostCL g = gs[i];
			System.out.println(i+") status: "+g.getStatus()+",  type: "+g.getType()+",  pos: "+g.getPos(0)+",  time: "+g.remainTimeAsEatable(0));
		}
	}


	private static int randomDir() {
		int[] dirs = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
		int ind = (int)(Math.random()*dirs.length);
		return dirs[ind];
	}

    private Pixel2D findClosestGreen(int[][] board, Map2D distMap, int targetColor) {
        Pixel2D target = null;
        int minDistance = Integer.MAX_VALUE;

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[0].length; y++) {
                if (board[x][y] == targetColor) {
                    int d = distMap.getPixel(x, y);
                    if (d != -1 && d < minDistance) {
                        minDistance = d;
                        target = new Index2D(x, y);
                    }
                }
            }
        }
        return target;
    }

    private double getClosestGhostDistance(PacmanGame game, Map2D dist) {
        GhostCL[] ghosts = game.getGhosts(0);
        double minSafeDist = Double.MAX_VALUE;

        for (GhostCL ghost : ghosts) {
            // אנחנו בודקים רק רוחות שהן לא ורודות (כלומר מסוכנות)
            if (ghost.getType() != _pink) {
                Pixel2D ghostPos = new Index2D(ghost.getPos(0));

                // אנחנו שואלים את המפה: "כמה צעדים יש מהפקמן לרוח הזו?"
                double d = dist.getPixel(ghostPos);

                // אם המרחק חוקי (לא קיר) והוא קטן ממה שמצאנו עד כה
                if (d != -1 && d < minSafeDist) {
                    minSafeDist = d;
                }
            }
        }
        return minSafeDist;
    }


    private int getDirFromPixels(Pixel2D src, Pixel2D dest, Map map) {
        int dx = dest.getX() - src.getX();
        int dy = dest.getY() - src.getY();
         System.out.println("From: " + src + " To: " + dest + " dx: " + dx + " dy: " + dy);
        if (dx > 1) return Game.LEFT;
        if (dx < -1) return Game.RIGHT;
        if (dy > 1) return Game.UP;
        if (dy < -1) return Game.DOWN;

        // מקרה רגיל (ללא קפיצה)
        if (dx == 1) return Game.RIGHT;
        if (dx == -1) return Game.LEFT;
        if (dy == -1) return Game.DOWN;
        if (dy == 1) return Game.UP;

        return Game.UP; // ברירת מחדל
    }
}