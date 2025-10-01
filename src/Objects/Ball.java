package Objects;

import GeometryPrimitives.Line;
import GeometryPrimitives.Point;
import GeometryPrimitives.Velocity;
import GeometryPrimitives.Rectangle;
import Render.Renderer;

/**
 * Ball represents the projectile in Arkanoid. It stores a radius and a per-frame
 * velocity. Collision detection uses a simple swept-line approach: the ball's
 * center traces a line from its current center to the next center (center + velocity)
 * and intersections with rectangle edges are used to compute collisions.
 *
 * The class exposes {@link #checkCollisionWithRect(Rectangle)} which returns true
 * and updates the ball's velocity and position when a collision is detected.
 */
public class Ball extends MovableObject {
    private double radius;

    public Ball(double centerX, double centerY, double radius, Velocity initialVelocity) {
        super(centerX - radius, centerY - radius, radius * 2, radius * 2);
        this.radius = radius;
        this.velocity = initialVelocity;
    }

    public Point getCenter() {
        return new Point(x + radius, y + radius);
    }

    public void setCenter(Point p) {
        this.x = p.getX() - radius;
        this.y = p.getY() - radius;
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public void render(Renderer renderer) {
        renderer.drawBall(this);
    }

    /**
     * Check collision against a rectangle (e.g., wall/paddle/brick) using swept line.
     * If collision occurs, reflect velocity across the collided side normal.
     */
    public boolean checkCollisionWithRect(Rectangle rect) {
        Point center = getCenter();
        Point next = new Point(center.getX() + velocity.getDx(), center.getY() + velocity.getDy());
        Line traj = new Line(center, next);
        Point hit = traj.closestIntersectionToStartOfLine(rect);
        if (hit == null) return false;

        // Determine which side was hit by comparing hit to rectangle edges
        double ux = rect.getUpperLeft().getX();
        double uy = rect.getUpperLeft().getY();
        double w = rect.getWidth();
        double h = rect.getHeight();

        double eps = 1e-6;
        boolean hitTop = Math.abs(hit.getY() - uy) < eps;
        boolean hitBottom = Math.abs(hit.getY() - (uy + h)) < eps;
        boolean hitLeft = Math.abs(hit.getX() - ux) < eps;
        boolean hitRight = Math.abs(hit.getX() - (ux + w)) < eps;

        double dx = velocity.getDx();
        double dy = velocity.getDy();

        if (hitTop || hitBottom) {
            dy = -dy;
        }
        if (hitLeft || hitRight) {
            dx = -dx;
        }

        this.velocity = new Velocity(dx, dy);
        // push the ball slightly out of the collision to avoid immediate re-collision (epsilon)
        double push = 0.5; // pixels
        double newCenterX = hit.getX();
        double newCenterY = hit.getY();
        // adjust according to which side(s) were hit
        if (hitTop) newCenterY = uy - radius - push;
        if (hitBottom) newCenterY = uy + h + radius + push;
        if (hitLeft) newCenterX = ux - radius - push;
        if (hitRight) newCenterX = ux + w + radius + push;
        // corner case: if both axes were hit, push diagonally from the corner
        setCenter(new Point(newCenterX, newCenterY));
        return true;
    }
}
