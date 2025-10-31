package Objects.PowerUps;

import Engine.GameManager;
import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;
import GeometryPrimitives.Velocity;
import Objects.Core.GameObject;
import Objects.GameEntities.Paddle;
import Render.Animation;
import Utils.AnimationFactory;
import Utils.Constants;

public abstract class PowerUp implements GameObject {
    private double x;
    private double y;
    private final double width;
    private final double height;
    private final PowerUpType type;
    private final Velocity velocity;
    private final Animation animation;
    private boolean collected;
    private boolean active;

    public PowerUp(double x, double y, PowerUpType type) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = Constants.PowerUps.POWERUP_WIDTH;
        this.height = Constants.PowerUps.POWERUP_HEIGHT;

        this.collected = false;
        this.active = true;

        this.velocity = new Velocity(0, Constants.PowerUps.POWERUP_FALL_SPEED);
        this.animation = AnimationFactory.createPowerUpAnimation(type);
        this.animation.play();
    }

    public void update() {
        Point currentPos = new Point(x, y);
        Point newPos = velocity.applyToPoint(currentPos);
        this.x = newPos.getX();
        this.y = newPos.getY();

        if (animation != null) {
            animation.update();
        }
    }

    public Animation getAnimation() {
        return animation;
    }

    public boolean checkPaddleCollision(Paddle paddle) {
        if (paddle == null || !active) {
            return false;
        }

        return getBounds().intersects(paddle.getBounds());
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public PowerUpType getType() { return type; }
    public boolean isActive() { return active; }
    public boolean isCollected() { return collected; }

    public void collect() {
        this.collected = true;
        this.active = false;
    }

    public abstract void applyEffect(GameManager gameManager);

    public abstract void removeEffect(GameManager gameManager);

    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point(x, y), width, height);
    }

    @Override
    public boolean isAlive() {
        return active;
    }

    @Override
    public void destroy() {
        active = false;
    }
}
