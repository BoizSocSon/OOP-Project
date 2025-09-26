/**
 * @author SteveHoang aka BoizSocSon
 * Student ID: 23020845
 */
package GeometryPrimitives;

import java.util.ArrayList;
import java.util.List;

public class Rectangle {
    private Point upperLeft;
    private double width;
    private double height;

    public Rectangle(Point upperLeft, double width, double height) {
        this.upperLeft = upperLeft;
        this.width = width;
        this.height = height;
    }

    public List<Point> intersectionPoints(Line line) {
        List<Point> intersections = new ArrayList<>();
        Line[] sides = new Line[4];

        // Define the sides of the rectangle
        sides[0] = new Line(this.upperLeft, new Point(upperLeft.getX() + width, upperLeft.getY()));
        sides[1] = new Line(new Point(upperLeft.getX(), upperLeft.getY() + height),
                new Point(upperLeft.getX() + width, upperLeft.getY() + height));
        sides[2] = new Line(upperLeft, new Point(upperLeft.getX(), upperLeft.getY() + height));
        sides[3] = new Line(new Point(upperLeft.getX() + width, upperLeft.getY()),
                new Point(upperLeft.getX() + width, upperLeft.getY() + height));

        boolean enter = true;
        for (Line side : sides) {
            Point intersection = line.intersectionWith(side);
            for (Point point : intersections) {
                if (point.equals(intersection)) {
                    enter = false;
                }
            }
            if (intersection != null && enter) {
                intersections.add(intersection);
            }
            enter = true;
        }
        return intersections;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public Point getUpperLeft() {
        return this.upperLeft;
    }

    public boolean equals(Rectangle rectangle) {
        double epsilon = 0.001;
        return this.upperLeft.equals(rectangle.upperLeft)
                && Math.abs(this.width - rectangle.width) < epsilon
                && Math.abs(this.height - rectangle.height) < epsilon;
    }

}