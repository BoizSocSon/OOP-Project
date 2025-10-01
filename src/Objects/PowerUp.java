package Objects;

import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;

/**
 * Base class for power-ups that fall from destroyed bricks and apply temporary
 * effects to the paddle or ball. Concrete implementations must implement
 * applyEffect and removeEffect to mutate the target object.
 */
public abstract class PowerUp implements GameObject {
    protected double x, y, width, height;
    protected double duration; // frames
    protected boolean active = true;

    public PowerUp(double x, double y, double width, double height, double duration) {
        this.x = x; this.y = y; this.width = width; this.height = height; this.duration = duration;
    }

    /** Apply effect to the given paddle (or other target). */
    public abstract void applyEffect(Paddle paddle);

    /** Remove effect when duration expires or power-up is cleared. */
    public abstract void removeEffect(Paddle paddle);

    @Override
    public Rectangle getBounds() { return new Rectangle(new Point(x,y), width, height); }

    @Override
    public boolean isAlive() { return active; }

    @Override
    public void destroy() { active = false; }
}
