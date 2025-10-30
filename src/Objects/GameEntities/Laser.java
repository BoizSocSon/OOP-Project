package Objects.GameEntities;

import GeometryPrimitives.Velocity;
import Objects.Core.MovableObject;
import Utils.Constants;

public class Laser extends MovableObject{
    private boolean destroyed;

    public Laser(double x, double y) {
        super(x, y, Constants.Laser.LASER_WIDTH, Constants.Laser.LASER_HEIGHT);
        setVelocity(new Velocity(0, -Constants.Laser.LASER_SPEED));
        this.destroyed = false;
    }

    @Override
    public void update() {
        if (destroyed) {
            return;
        }
        move();
    }

    public boolean isOffScreen() {
        return getY() + getHeight() < Constants.Window.WINDOW_TOP_OFFSET + Constants.Borders.BORDER_TOP_HEIGHT;
    }

    public void destroy() {
        this.destroyed = true;
    }

    @Override
    public boolean isAlive() {
        return !destroyed;
    }
}
