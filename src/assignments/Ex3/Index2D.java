package assignments.Ex3;




/**
 * This class represents a 2D point (pixel) on a grid.
 * It implements the Pixel2D interface and provides methods for
 * coordinate retrieval, distance calculation, and equality checks.
 */

public class Index2D implements Pixel2D {
    private int _x, _y;



    public Index2D(String s) {
        try {
            String[] a = s.split(",");
            double x_double = Double.parseDouble(a[0]);
            double y_double = Double.parseDouble(a[1]);
            this._x = (int) x_double;
            this._y = (int) y_double;
        } catch (Exception e) {
            System.err.println("Error parsing position string: " + s);
            this._x = 0;
            this._y = 0;
        }
    }

    public Index2D(int x, int y) {
        _x=x;
        _y=y;
    }

    public Index2D(Pixel2D t) {
        this(t.getX(), t.getY());
    }


    @Override
    public int getX() {
        return _x;
    }

    @Override
    public int getY()
    {
        return _y;
    }


    /**
     * Calculates the Euclidean distance between this pixel and another.
     * @param p2 the other pixel to measure distance to.
     * @return the distance as a double.
     * @throws RuntimeException if p2 is null.
     */
    @Override
    public double distance2D(Pixel2D p2) {
        if (p2 == null){
            throw new RuntimeException("p2  have to include a value");
        }
        double ans = 0;
        double dx = this._x - p2.getX();
        double dy = this._y - p2.getY();
        ans  = Math.sqrt(dx*dx + dy*dy);
        return ans;
    }


    @Override
    public String toString() {

        return getX()+","+getY();
    }


    @Override
    public boolean equals(Object t) {
        boolean ans = false;
       /////// you do NOT need to add your code below ///////
        if(t instanceof Pixel2D) {
            Pixel2D p = (Pixel2D) t;
            ans = (this.distance2D(p)==0);
        }
       ///////////////////////////////////
        return ans;
    }
}
