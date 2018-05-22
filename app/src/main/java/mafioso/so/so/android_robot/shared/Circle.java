package mafioso.so.so.android_robot.shared;

import org.opencv.core.Point;

public class Circle {

    private Point mCenter;
    private double mDiameter;

    public Circle(Point mCenter, double mDiameter) {
        this.mCenter = mCenter;
        this.mDiameter = mDiameter;
    }

    public Point getCenter() {
        return mCenter;
    }


    public double getDiameter() {
        return mDiameter;
    }
}
