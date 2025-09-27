package GeometryPrimitives;

import java.util.List;

/**
 * Tiny test runner for the GeometryPrimitives package.
 * Run with: javac -d out src\GeometryPrimitives\*.java && java -cp out GeometryPrimitives.TestGeometryPrimitives
 */
public class TestGeometryPrimitives {
    private static int tests = 0;
    private static int failures = 0;

    public static void main(String[] args) {
        testPointDistance();
        testPointEquals();
        testLineLength();
        testIntersectingLines();
        testNonIntersectingLines();
        testRectangleIntersections();
        testVelocityFromAngleAndSpeed();
        testVelocityApplyToPoint();
        testPointNullEquals();
        testOverlappingCollinearLines();
        testCollinearTouchAtEndpoint();
        testLinePointIntersectionWhenOneIsPoint();
        testClosestIntersectionToStartOfLine();
        testRectangleEqualsAndGetters();

        System.out.println();
        System.out.println("Tests run: " + tests + ", Failures: " + failures);
        if (failures == 0) {
            System.out.println("ALL TESTS PASSED");
        } else {
            System.out.println("SOME TESTS FAILED");
            System.exit(1);
        }
    }

    private static void ok(String name) {
        tests++;
        System.out.println("[PASS] " + name);
    }

    private static void fail(String name, String reason) {
        tests++;
        failures++;
        System.out.println("[FAIL] " + name + " -> " + reason);
    }

    private static boolean approxEqual(double a, double b, double eps) {
        return Math.abs(a - b) <= eps;
    }

    private static void assertTrue(boolean cond, String name) {
        if (cond) ok(name); else fail(name, "condition was false");
    }

    private static void assertEquals(double a, double b, double eps, String name) {
        if (approxEqual(a, b, eps)) ok(name); else fail(name, a + " != " + b);
    }

    private static void assertEquals(Point p, Point q, double eps, String name) {
        if (p == null && q == null) { ok(name); return; }
        if (p == null || q == null) { fail(name, "one of points is null"); return; }
        if (approxEqual(p.getX(), q.getX(), eps) && approxEqual(p.getY(), q.getY(), eps)) ok(name); else fail(name, p.getX() + "," + p.getY() + " != " + q.getX() + "," + q.getY());
    }

    // --- tests ---
    private static void testPointDistance() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(3, 4);
        assertEquals(p1.distance(p2), 5.0, 1e-6, "Point.distance: 3-4-5");
    }

    private static void testPointEquals() {
        Point p1 = new Point(1.000000, 2.000000);
        Point p2 = new Point(1.0000001, 1.9999999);
        assertTrue(p1.equals(p2), "Point.equals uses epsilon");
    }

    private static void testLineLength() {
        Line l = new Line(new Point(0, 0), new Point(0, 5));
        assertEquals(l.length(), 5.0, 1e-6, "Line.length vertical");
    }

    private static void testIntersectingLines() {
        Line a = new Line(new Point(0, 0), new Point(4, 4));
        Line b = new Line(new Point(0, 4), new Point(4, 0));
        Point inter = a.intersectionWith(b);
        assertEquals(inter, new Point(2, 2), 1e-6, "Line.intersectionWith crossing");
        assertTrue(a.isIntersecting(b), "Line.isIntersecting crossing");
    }

    private static void testNonIntersectingLines() {
        Line a = new Line(new Point(0, 0), new Point(1, 0));
        Line b = new Line(new Point(0, 1), new Point(1, 1));
        Point inter = a.intersectionWith(b);
        assertTrue(inter == null, "Line.intersectionWith parallel -> null");
        assertTrue(!a.isIntersecting(b), "Line.isIntersecting parallel -> false");
    }

    private static void testRectangleIntersections() {
        Rectangle r = new Rectangle(new Point(1, 1), 2, 2); // corners (1,1)-(3,3)
        Line horizontal = new Line(new Point(0, 2), new Point(4, 2));
        List<Point> pts = r.intersectionPoints(horizontal);
        if (pts.size() == 2) ok("Rectangle.intersectionPoints count == 2"); else fail("Rectangle.intersectionPoints count", "expected 2, got " + pts.size());
        // check points roughly
        if (pts.size() >= 2) {
            assertEquals(pts.get(0), new Point(1, 2), 1e-6, "Rectangle intersection pt1");
            assertEquals(pts.get(1), new Point(3, 2), 1e-6, "Rectangle intersection pt2");
        }
    }

    private static void testVelocityFromAngleAndSpeed() {
        Velocity v = Velocity.fromAngleAndSpeed(0, 1);
        assertEquals(v.getDx(), 0.0, 1e-6, "Velocity.fromAngleAndSpeed dx for angle 0");
        assertEquals(v.getDy(), -1.0, 1e-6, "Velocity.fromAngleAndSpeed dy for angle 0");
    }

    private static void testVelocityApplyToPoint() {
        Velocity v = new Velocity(1.5, -2.5);
        Point p = new Point(3.0, 4.0);
        Point res = v.applyToPoint(p);
        assertEquals(res, new Point(4.5, 1.5), 1e-6, "Velocity.applyToPoint moves point correctly");
    }

    private static void testPointNullEquals() {
        Point p = new Point(1.0, 1.0);
        assertTrue(!p.equals(null), "Point.equals should return false for null");
    }

    private static void testOverlappingCollinearLines() {
        Line a = new Line(new Point(0, 0), new Point(4, 0));
        Line b = new Line(new Point(2, 0), new Point(6, 0));
        // They overlap (infinite intersection points), isIntersecting should be true, intersectionWith should return null
        assertTrue(a.isIntersecting(b), "Overlapping collinear lines considered intersecting");
        assertTrue(b.isIntersecting(a), "Overlapping collinear lines considered intersecting (commutative)");
        assertTrue(a.intersectionWith(b) == null, "Overlapping collinear lines -> intersectionWith returns null (infinite points)");
    }

    private static void testCollinearTouchAtEndpoint() {
        Line a = new Line(new Point(0, 0), new Point(2, 0));
        Line b = new Line(new Point(2, 0), new Point(3, 0));
        assertTrue(a.isIntersecting(b), "Collinear touching at endpoint -> isIntersecting true");
        Point inter = a.intersectionWith(b);
        assertEquals(inter, new Point(2, 0), 1e-6, "Collinear touching -> intersectionWith returns shared endpoint");
    }

    private static void testLinePointIntersectionWhenOneIsPoint() {
        Line pointLine = new Line(new Point(2, 0), new Point(2, 0));
        Line other = new Line(new Point(0, 0), new Point(4, 0));
        assertTrue(pointLine.isIntersecting(other), "A degenerate line (point) on another line -> intersecting");
        Point inter = pointLine.intersectionWith(other);
        assertEquals(inter, new Point(2, 0), 1e-6, "Degenerate line intersection returns that point when on the other line");
    }

    private static void testClosestIntersectionToStartOfLine() {
        Rectangle r = new Rectangle(new Point(1, 1), 2, 2); // corners (1,1)-(3,3)
        Line diag = new Line(new Point(0, 0), new Point(4, 4));
        Point closest = diag.closestIntersectionToStartOfLine(r);
        assertEquals(closest, new Point(1, 1), 1e-6, "closestIntersectionToStartOfLine returns nearest corner intersection");
    }

    private static void testRectangleEqualsAndGetters() {
        Rectangle r1 = new Rectangle(new Point(1, 1), 2.0, 3.0);
        Rectangle r2 = new Rectangle(new Point(1.0000001, 1.0000001), 2.0, 3.0);
        assertTrue(r1.equals(r2), "Rectangle.equals uses epsilon for upper-left coordinates");
        assertEquals(r1.getWidth(), 2.0, 1e-6, "Rectangle.getWidth");
        assertEquals(r1.getHeight(), 3.0, 1e-6, "Rectangle.getHeight");
        assertEquals(r1.getUpperLeft(), new Point(1, 1), 1e-6, "Rectangle.getUpperLeft");
    }

}
