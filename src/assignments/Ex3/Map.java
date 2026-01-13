package assignments.Ex3;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class represents a 2D map as a "screen" or a raster matrix or maze over integers.
 * @author boaz.benmoshe
 *
 */
public class Map implements Map2D {
    private int _w;
    private int _h;
    private int _v;
    private int[][] _map;
    private boolean _cyclicFlag = true;


    /**
     * Constructs a w*h 2D raster map with an init value v.
     *
     * @param w
     * @param h
     * @param v
     */
    public Map(int w, int h, int v) {
        if (w < 0 || h < 0) {
            throw new RuntimeException("Coordinates cannot be negative: " + w + "," + h);
        }
        init(w, h, v);
    }


    /**
     * Constructs a square map (size*size).
     */
    public Map(int size) {
        this(size, size, 0);
    }


    /**
     * Constructs a map from a given 2D array.
     *
     * @param data
     */
    public Map(int[][] data) {
        if (data == null) {
            throw new RuntimeException("data must include values");
        }
        init(data);
    }


    /**
     * This constructor create a map form width , height and color .
     *
     * @param w the width of the map.
     * @param h the height of the map.
     * @param v the init value of all the entries in the map.
     */
    @Override
    public void init(int w, int h, int v) {
        this._w = w;
        this._h = h;
        this._map = new int[w][h];
        this._v = v;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                _map[x][y] = v;
            }
        }
    }


    /**
     * This constructor create the map from a given 2D array .
     * the width is calculated by the length of the main array.
     * the height is calculated by the length of inside array.
     * Note 1: the constructor is working only if all inner arrays have the same length.
     * Note 2: the color is declared by the value of every cell inside the inner arrays.
     *
     * @param arr a 2D int array.
     */
    @Override
    public void init(int[][] arr) {
        if (arr == null) {
            throw new RuntimeException("array must include values");
        }
        this._w = arr.length;
        this._h = arr[0].length;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].length != _h) {
                throw new RuntimeException("the array is not ragged");
            }
        }
        this._map = new int[_w][_h];
        for (int x = 0; x < _w; x++) {
            for (int y = 0; y < _h; y++) {
                _map[x][y] = arr[x][y];
            }
        }
    }


    /**
     * This is a public function that creates a copy of the current map.
     *
     * @return ans [][] - return a 2D arrays represent the map.
     */
    @Override
    public int[][] getMap() {
        int[][] ans = null;
        int width = this._w;
        int height = this._h;
        ans = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                ans[x][y] = this._map[x][y];
            }
        }
        return ans;
    }


    /**
     * This function get the width of the map.
     *
     * @return ans - map width.
     */
    @Override
    public int getWidth() {
        int ans = -1;
        ans = this._w;
        return ans;
    }


    @Override
    public int getHeight() {
        int ans = -1;
        ans = this._h;
        return ans;
    }

    @Override
    public int getPixel(int x, int y) {
        int ans = -1;
        ans = this._map[x][y];
        return ans;
    }

    @Override
    public int getPixel(Pixel2D p) {
        return this.getPixel(p.getX(), p.getY());
    }

    @Override
    /////// add your code below ///////
    public void setPixel(int x, int y, int v) {
        this._map[x][y] = v;
    }


    @Override
    public void setPixel(Pixel2D p, int v) {
        this._map[p.getX()][p.getY()] = v;
    }


    @Override
    /**
     * Fills a continuous area of the same color with a new color, starting from a given pixel.
     * The function uses the Flood Fill algorithm (BFS) to traverse connected pixels.
     * @param xy - the starting pixel coordinate.
     * @param new_v - the new color value to apply.
     * @param cyclic - if true, the fill will "wrap around" the map edges (top to bottom, left to right).
     * @return the total number of pixels that were repainted.
     */
    public int fill(Pixel2D xy, int new_v) {
        int oldColor = getPixel(xy);
        if (oldColor == new_v) return 0;

        Queue<Pixel2D> q = new LinkedList<>();
        q.add(xy);

        setPixel(xy, new_v);
        int counter = 1;

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (!q.isEmpty()) {
            Pixel2D current = q.poll();

            for (int i = 0; i < 4; i++) {
                int nx = current.getX() + dx[i];
                int ny = current.getY() + dy[i];

                if (this.isCyclic()) {
                    nx = (nx + getWidth()) % getWidth();
                    ny = (ny + getHeight()) % getHeight();
                }

                if (nx >= 0 && nx < getWidth() && ny >= 0 && ny < getHeight()) {
                    if (getPixel(nx, ny) == oldColor) {
                        setPixel(nx, ny, new_v);
                        q.add(new Index2D(nx, ny));
                        counter++;
                    }
                }
            }
        }
        return counter;
    }


    @Override
    /**
     * Finds the shortest path between two points on the map using the BFS algorithm.
     * The path avoids pixels with the specified obstacle color.
     * If a path exists, it returns an array of pixels from start to end.
     * @param p1 the starting point.
     * @param p2 the target destination point.
     * @param obsColor the color value that represents an obstacle (impassable).
     * @return an array of Pixel2D representing the shortest path, or null if no path exists.
     */
    public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor) {
        Pixel2D[] ans = null;
        if (getPixel(p1) == obsColor || getPixel(p2) == obsColor) {
            return null;
        }
        if (p1.equals(p2)) {
            return new Pixel2D[]{p1};
        }

        Queue<Pixel2D> q = new LinkedList<>();
        q.add(p1);

        Pixel2D[][] prev = new Pixel2D[getWidth()][getHeight()];
        prev[p1.getX()][p1.getY()] = p1;

        while (!q.isEmpty()) {
            Pixel2D current = q.poll();
            if (current.equals(p2)) {
                return reconstructPath(p1, p2, prev);
            }
            int x = current.getX();
            int y = current.getY();
            boolean cyclic = _cyclicFlag;
            checkNeighbor(x + 1, y, current, q, prev, obsColor, cyclic);
            checkNeighbor(x - 1, y, current, q, prev, obsColor, cyclic);
            checkNeighbor(x, y + 1, current, q, prev, obsColor, cyclic);
            checkNeighbor(x, y - 1, current, q, prev, obsColor, cyclic);
        }
        return ans;
    }

    @Override
    public boolean isInside(Pixel2D p) {
        boolean ans = true;
        if (p == null) {
            return false;
        }
        int width = p.getX();
        int height = p.getY();
        if (width < 0 || width >= _w || height < 0 || height >= _h) {
            ans = false;
        }
        return ans;
    }

    @Override
    public boolean isCyclic() {
        boolean ans = false;
        if (this._cyclicFlag) {
            ans = true;
        }
        return ans;
    }


    @Override
    public void setCyclic(boolean cy) {
        this._cyclicFlag = cy;
    }


    /**
     * Calculates the shortest distance from a starting point to all reachable pixels on the map.
     * This method returns a Map2D where each pixel contains its distance from the start point.
     * Pixels that are unreachable or contain obstacles are marked with -1.
     *
     * @param start    the starting point for the distance calculation.
     * @param obsColor the color value representing an obstacle.
     * @return a Map2D where each pixel's value is its distance from the start point, or -1 if unreachable.
     */
    @Override
    public Map2D allDistance(Pixel2D start, int obsColor) {
        Map2D ans = new Map(getWidth(), getHeight(), -1);

        if (start == null || !isInside(start) || this.getPixel(start) == obsColor) {
            return ans;
        }
        Queue<Pixel2D> q = new LinkedList<>();
        q.add(start);
        ans.setPixel(start, 0);

        while (!q.isEmpty()) {
            Pixel2D current = q.poll();
            int dist = ans.getPixel(current);

            int[] dx = {1, -1, 0, 0};
            int[] dy = {0, 0, 1, -1};

            for (int i = 0; i < 4; i++) {
                int nx = current.getX() + dx[i];
                int ny = current.getY() + dy[i];

                if (_cyclicFlag) {
                    nx = ((nx % getWidth()) + getWidth()) % getWidth();
                    ny = ((ny % getHeight()) + getHeight()) % getHeight();
                }

                if (nx >= 0 && nx < getWidth() && ny >= 0 && ny < getHeight()) {

                    Pixel2D neighbor = new Index2D(nx, ny);

                    if (this.getPixel(neighbor) != obsColor && ans.getPixel(neighbor) == -1) {
                        ans.setPixel(neighbor, dist + 1);
                        q.add(neighbor);
                    }
                }
            }
        }

        return ans;
    }

//////////////// private methods //////////////////////////////////////////////////////////


    /**
     * Reconstructs the shortest path from start to end by backtracking through the 'prev' matrix.
     *
     * @return an array of Pixel2D representing the path in correct order (start to end).
     */
    private Pixel2D[] reconstructPath(Pixel2D p1, Pixel2D p2, Pixel2D[][] prev) {
        ArrayList<Pixel2D> pathList = new ArrayList<>();

        Pixel2D curr = p2;

        while (curr != null) {
            pathList.add(curr);

            if (curr.equals(p1)) break;
            curr = prev[curr.getX()][curr.getY()];
        }
        Collections.reverse(pathList);
        return pathList.toArray(new Pixel2D[0]);
    }



    /**
     * Helper function for BFS that validates a neighbor pixel and adds it to the queue.
     * Handles cyclic boundaries and ensures the neighbor is not an obstacle and hasn't been visited.
     */
    private void checkNeighbor(int nx, int ny, Pixel2D current, Queue<Pixel2D> q, Pixel2D[][] prev, int obsColor, boolean cyclic) {
        int actualX = nx;
        int actualY = ny;

        if (cyclic) {
            actualX = (nx + getWidth()) % getWidth();
            actualY = (ny + getHeight()) % getHeight();
        }


        if (actualX >= 0 && actualX < getWidth() && actualY >= 0 && actualY < getHeight()) {

            Pixel2D neighbor = new Index2D(actualX, actualY);

            if (getPixel(actualX, actualY) != obsColor && prev[actualX][actualY] == null) {
                prev[actualX][actualY] = current;
                q.add(neighbor);
            }
        }
    }
}



