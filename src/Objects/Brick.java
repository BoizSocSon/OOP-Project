package Objects;

import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;
import Render.Renderer;

/**
 * Brick is the base class for destructible blocks. It stores a hit point counter
 * (hp). Each call to {@link #takeHit()} reduces hp, and when hp reaches zero the
 * brick is marked destroyed.
 */
public abstract class Brick implements GameObject {
    protected double x, y, width, height;
    protected int hitPoints;
    protected boolean alive = true;

    public Brick(double x, double y, double width, double height, int hp) {
        this.x = x; this.y = y; this.width = width; this.height = height; this.hitPoints = hp;
    }

    /** Decrease hit points by one; destroy when hp <= 0. */
    public void takeHit() {
        hitPoints--;
        if (hitPoints <= 0) destroy();
    }

    /** Returns true if the brick has been destroyed. */
    public boolean isDestroyed() { return !alive; }

    @Override
    public Rectangle getBounds() { return new Rectangle(new Point(x,y), width, height); }

    @Override
    public boolean isAlive() { return alive; }

    @Override
    public void destroy() { alive = false; }
}
