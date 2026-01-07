package assignments.Ex3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MapTest {

    private Map _m;
    private Map _m1;
    private final int[][] DEFAULT_DATA = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};

    @BeforeEach
    public void setUp() {
        _m = new Map(DEFAULT_DATA);
        _m1 = new Map(10, 10, 0);
    }

    @Test
    public void initTest1() {
        int x = 10;
        int y = 12;
        int z = 0;
        Map m1 = new Map(x, y, z);
        assertEquals(x, m1.getWidth());
        assertEquals(y, m1.getHeight());
        for (int i = 0; i < x; i++) {
            for (int w = 0; w < y; w++) {
                assertEquals(z, m1.getPixel(i, w));
            }
        }
    }

    @Test
    public void initTest2() {
        int size = 10;
        Map m2 = new Map(size);
        assertEquals(size, m2.getWidth());
        assertEquals(size, m2.getHeight());
    }

    @Test
    public void initTest3() {
        int[][] data = {{1, 2, 3}, {4, 5, 6}};
        Map m3 = new Map(data);
        assertEquals(2, m3.getWidth());
        assertEquals(3, m3.getHeight());
    }

    @Test
    public void getMapTest() {
        int[][] temp = _m.getMap();
        for (int x = 0; x < _m.getWidth(); x++) {
            for (int y = 0; y < _m.getHeight(); y++) {
                assertEquals(temp[x][y], _m.getPixel(x, y));
            }
        }
        // בדיקת Deep Copy
        int ogpixel = _m.getPixel(0, 1);
        temp[0][1] = 55; // שינוי במערך החיצוני
        assertEquals(_m.getPixel(0, 1), ogpixel); // הערך במפה לא אמור להשתנות
    }

    @Test
    public void getPixelTest1() {
        int temp = _m.getPixel(0, 2);
        assertEquals(3, temp);
    }

    @Test
    public void getPixelTest2() {
        Index2D test = new Index2D(1, 1);
        int temp1 = _m.getPixel(test);
        assertEquals(5, temp1);
    }

    @Test
    public void setPixelTest1() {
        int v = 8;
        int x = 1;
        int y = 2;
        _m.setPixel(x, y, v);
        assertEquals(8, _m.getPixel(1, 2));
    }

    @Test
    public void isInsideTest() {
        assertTrue(_m.isInside(new Index2D(0, 2)));
        assertTrue(_m.isInside(new Index2D(0, 0)));
        // במפה של 3X3, אינדקס 3 הוא כבר בחוץ
        assertFalse(_m.isInside(new Index2D(3, 3)));
        assertFalse(_m.isInside(null));
    }

    @Test
    void testAllDistanceBasic() {
        Map myMap = new Map(3, 3, 255);
        Pixel2D start = new Index2D(0, 0);
        int obsColor = 0;
        myMap.setPixel(1, 1, obsColor);

        // שינוי כאן: הסרתי את ה-boolean כי ב-Map שלך הוא לא קיים בחתימה
        Map2D res = myMap.allDistance(start, obsColor);

        assertNotNull(res);
        assertEquals(0, res.getPixel(0, 0));
        assertEquals(1, res.getPixel(1, 0));
        assertEquals(1, res.getPixel(0, 1));
        assertEquals(-1, res.getPixel(1, 1)); // מכשול
    }

    @Test
    public void testShortestPathCyclic() {
        Map cyclicMap = new Map(10, 10, 255);
        cyclicMap.setCyclic(true); // מגדיר מחזוריות דרך המתודה של האובייקט

        Pixel2D p1 = new Index2D(0, 5);
        Pixel2D p2 = new Index2D(9, 5);

        // ב-Map שלך הפונקציה מקבלת רק 3 פרמטרים
        Pixel2D[] path = cyclicMap.shortestPath(p1, p2, 0);

        assertNotNull(path, "Path should be found");
        assertEquals(2, path.length, "Cyclic path should only take 2 steps (0,5 -> 9,5)");
    }

    @Test
    public void testFillOnM1() {
        _m1.setCyclic(false);
        // נצבע "קיר" באמצע המפה
        for(int i=0; i<10; i++) _m1.setPixel(5, i, 255);

        // נמלא צד אחד
        int pixelsFilled = _m1.fill(new Index2D(0, 0), 10);

        assertEquals(10, _m1.getPixel(0, 0));
        assertEquals(255, _m1.getPixel(5, 5));
        assertEquals(0, _m1.getPixel(6, 5)); // הצד השני לא אמור להשתנות
        assertEquals(50, pixelsFilled); // חצי מפה (5*10)
    }

    @Test
    public void testShortestPathNoPath() {
        Map blockedMap = new Map(3, 3, 255);
        blockedMap.setCyclic(false);
        // חסימה מוחלטת של הגישה ל-(2,2)
        for(int i=0; i<3; i++) blockedMap.setPixel(1, i, 0);

        Pixel2D[] path = blockedMap.shortestPath(new Index2D(0,0), new Index2D(2,2), 0);
        assertNull(path, "Path should be null when blocked");
    }
}