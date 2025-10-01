package Objects;

import GeometryPrimitives.Velocity;
import Render.Renderer;

/**
 * Paddle is the player's controllable bar. It moves only horizontally and
 * can accept power-ups that modify its properties.
 *
 * Movement is controlled by setting per-frame velocity via moveLeft/moveRight/stop.
 */
public class Paddle extends MovableObject {
    private double speed; // pixels per frame
    private PowerUp currentPowerUp;

    public Paddle(double x, double y, double width, double height, double speed) {
        super(x, y, width, height);
        this.speed = speed;
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public void render(Renderer renderer) {
        renderer.drawPaddle(this);
    }

    public void moveLeft() {
        setVelocity(new Velocity(-speed, 0));
    }

    public void moveRight() {
        setVelocity(new Velocity(speed, 0));
    }

    public void stop() { setVelocity(new Velocity(0,0)); }

    public void applyPowerUp(PowerUp p) {
        this.currentPowerUp = p;
        p.applyEffect(this);
    }

    public void removePowerUp() {
        if (this.currentPowerUp != null) {
            this.currentPowerUp.removeEffect(this);
            this.currentPowerUp = null;
        }
    }

    public double getSpeed() { return speed; }
    public void setSpeed(double s) { this.speed = s; }
}
