package Objects.Core;

import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;
import GeometryPrimitives.Velocity;

public abstract class MovableObject implements GameObject {
    private double x;
    private double y;
    private double width;
    private double height;
    private Velocity velocity; // per-frame velocity dx/dy
    private boolean alive;

    public MovableObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocity = new Velocity(0, 0);
        this.alive = true;
    }

    public void move() {
        this.x += this.velocity.getDx();
        this.y += this.velocity.getDy();
    }

    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Velocity getVelocity() {
        return velocity;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point(x, y), width, height);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void destroy() {
        alive = false;
    }
}
