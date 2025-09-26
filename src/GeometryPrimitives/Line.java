/**
 * @author SteveHoang aka BoizSocSon
 * Student ID: 23020845
 */
package GeometryPrimitives;

import java.util.List;

public class Line {
    private static final double EPSILON = 1e-6;
    private final Point start;
    private final Point end;

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Point closestIntersectionToStartOfLine(Rectangle rect) {
        List<Point> intersections = rect.intersectionPoints(this);
        if (intersections.isEmpty()) {
            return null;
        }

        Point closestPoint = intersections.get(0);
        double minDistance = start.distance(closestPoint);

        // finds the closest point to the start of the line
        for (Point point : intersections) {
            double distance = start.distance(point);
            if (distance < minDistance) {
                closestPoint = point;
                minDistance = distance;
            }
        }

        return closestPoint;
    }

    public Point start() {
        return this.start;
    }

    public Point end() {
        return this.end;
    }

    public double length() {
        return start.distance(end);
    }

    public boolean isIntersecting(Line other) {
        int d1 = this.directionPointRelToLine(other.start());
        int d2 = this.directionPointRelToLine(other.end());
        int d3 = other.directionPointRelToLine(this.start());
        int d4 = other.directionPointRelToLine(this.end());

        if (d1 != d2 && d3 != d4) {
            return true;
        }

        return ((d1 == 0) && this.isPointOnLine(other.start()))
                || ((d2 == 0) && this.isPointOnLine(other.end()))
                || ((d3 == 0) && other.isPointOnLine(this.start()))
                || ((d4 == 0) && other.isPointOnLine(this.end()))
                || this.start.equals(other.end())
                || this.start.equals(other.start)
                || this.end.equals(other.end())
                || this.end.equals(other.start());

    }

    private int directionPointRelToLine(Point point) {
        double pointX = point.getX();
        double pointY = point.getY();
        double startX = this.start.getX();
        double startY = this.start.getY();
        double endX = this.end.getX();
        double endY = this.end.getY();

        double slopeCalc = (endY - startY) * (pointX - startX) - (endX - startX) * (pointY - startY);

        if (slopeCalc == 0) {
            return 0; // Nằm trên đường thẳng
        }
        if (slopeCalc > 0) {
            return 1; // Nằm bên phải (theo chiều start → end)
        } else {
            return 2; // Nằm bên trái (theo chiều start → end)
        }
    }

    private boolean isPointOnLine(Point point) {
        double pointX = point.getX();
        double pointY = point.getY();
        double startX = this.start.getX();
        double startY = this.start.getY();
        double endX = this.end.getX();
        double endY = this.end.getY();

        if (point.equals(this.start) || point.equals(this.end)) {
            return true;
        }

        if (pointX > Math.max(startX, endX) + EPSILON || pointX < Math.min(startX, endX) - EPSILON) {
            return false;
        }
        if (pointY > Math.max(startY, endY) + EPSILON || pointY < Math.min(startY, endY) - EPSILON) {
            return false;
        }

        double slopeCalc = (endY - startY) * (pointX - startX) - (endX - startX) * (pointY - startY);

        return Math.abs(slopeCalc) < EPSILON;
    }

    private boolean isOverlapping(Line other) {
        if (this.start.equals(other.start)) {
            return this.isPointOnLine(other.end()) || other.isPointOnLine(this.end);
        }
        if (this.start.equals(other.end)) {
            return this.isPointOnLine(other.start()) || other.isPointOnLine(this.end);
        }
        if (this.end.equals(other.start)) {
            return this.isPointOnLine(other.end()) || other.isPointOnLine(this.start);
        }
        if (this.end.equals(other.end)) {
            return this.isPointOnLine(other.start()) || other.isPointOnLine(this.start);
        }
        return this.isPointOnLine(other.start) || this.isPointOnLine(other.end)
                || other.isPointOnLine(this.start) || other.isPointOnLine(this.end);
    }


    public Point intersectionWith(Line other) {
        if (!this.isIntersecting(other)) {
            return null;
        }

        if (this.start.equals(this.end)) {
            return other.isPointOnLine(this.start) ? this.start : null;
        }
        if (other.start.equals(other.end)) {
            return this.isPointOnLine(other.start) ? other.start : null;
        }

        if (this.start.getX() != this.end.getX() && other.start.getX() != other.end.getX()) {
            double m1 = (this.end.getY() - this.start.getY()) / (this.end.getX() - this.start.getX());
            double b1 = this.start.getY() - m1 * this.start.getX();
            double m2 = (other.end.getY() - other.start.getY()) / (other.end.getX() - other.start.getX());
            double b2 = other.start.getY() - m2 * other.start.getX();

            if (Math.abs(m1 - m2) < EPSILON) {
                if (Math.abs(b1 - b2) < EPSILON) {
                    if (this.isOverlapping(other)) {
                        return null;
                    }
                    if (this.start.equals(other.start)) {
                        return this.start;
                    }
                    if (this.end.equals(other.end)) {
                        return this.end;
                    }
                    if (this.start.equals(other.end)) {
                        return this.start;
                    }
                    if (this.end.equals(other.start)) {
                        return this.end;
                    }
                }
                return null;
            }

            double x = (b2 - b1) / (m1 - m2);
            double y = m1 * x + b1;

            return new Point(x, y);
        }

        if (Math.abs(this.start.getX() - this.end.getX()) < EPSILON && other.start.getX() != other.end.getX()) {
            double m2 = (other.end.getY() - other.start.getY()) / (other.end.getX() - other.start.getX());
            double b2 = other.start.getY() - m2 * other.start.getX();
            double x = this.start.getX();
            double y = m2 * x + b2;

            Point intersection = new Point(x, y);
            return this.isPointOnLine(intersection) && other.isPointOnLine(intersection) ? intersection : null;
        }

        if (Math.abs(other.start.getX() - other.end.getX()) < EPSILON && this.start.getX() != this.end.getX()) {
            double m1 = (this.end.getY() - this.start.getY()) / (this.end.getX() - this.start.getX());
            double b1 = this.start.getY() - m1 * this.start.getX();
            double x = other.start.getX();
            double y = m1 * x + b1;
            Point intersection = new Point(x, y);
            return this.isPointOnLine(intersection) && other.isPointOnLine(intersection) ? intersection : null;
        }

        if (Math.abs(this.start.getX() - this.end.getX()) < EPSILON && other.start.getX() == other.end.getX()) {
            if (this.start.getX() != other.start.getX()) {
                return null;
            }
            if (this.isOverlapping(other)) {
                return null;
            }
            if (this.start.equals(other.start)) {
                return this.start;
            }
            if (this.end.equals(other.end)) {
                return this.end;
            }
            if (this.start.equals(other.end)) {
                return this.start;
            }
            if (this.end.equals(other.start)) {
                return this.end;
            }
        }
        return null;
    }
}
