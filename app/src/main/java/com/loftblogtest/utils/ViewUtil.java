package com.loftblogtest.utils;

import android.graphics.Point;

public class ViewUtil {

    public static boolean isNearTouch(int touchX,
                                      int touchY,
                                      int rad,
                                      Point customCenter,
                                      int customRadius) {
        return customRadius + rad >= (Math.sqrt(Math.pow(touchX - customCenter.x, 2) + Math.pow(touchY - customCenter.y, 2)));
    }

    public static int pointToAngle(int x, int y, Point center) {
        if (x >= center.x && y < center.y) {
            double opp = x - center.x;
            double adj = center.y - y;
            return 270 + (int) Math.toDegrees(Math.atan(opp / adj));
        } else if (x > center.x) {
            double opp = y - center.y;
            double adj = x - center.x;
            return (int) Math.toDegrees(Math.atan(opp / adj));
        } else if (y > center.y) {
            double opp = center.x - x;
            double adj = y - center.y;
            return 90 + (int) Math.toDegrees(Math.atan(opp / adj));
        } else if (x < center.x) {
            double opp = center.y - y;
            double adj = center.x - x;
            return 180 + (int) Math.toDegrees(Math.atan(opp / adj));
        }

        throw new IllegalArgumentException();
    }
}
