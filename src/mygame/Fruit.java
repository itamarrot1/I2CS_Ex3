package mygame;

public class Fruit {
    // משך הזמן שהפקמן נשאר "סופר" (במילישניות)
    private static final long SUPER_DURATION = 8000;

    public static boolean isSuperMode(long superModeStartTime) {
        if (superModeStartTime == 0) return false;
        return System.currentTimeMillis() - superModeStartTime < SUPER_DURATION;
    }
}