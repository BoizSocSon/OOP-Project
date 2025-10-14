package Objects.PowerUps;

import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;
import GeometryPrimitives.Velocity;
import Objects.Core.GameObject;
import Objects.GameEntities.Paddle;
import Render.Animation;
import Render.Renderer;
import Utils.AnimationFactory;
import Utils.Constants;

/**
 * Base class for power-ups that fall from destroyed bricks.
 *
 * Design:
 * - Each powerup has a type (PowerUpType enum)
 * - Falls down with constant velocity
 * - Has animated sprite (8 frames, looping)
 * - Abstract methods for applying/removing effects
 * 
 * Lifecycle:
 * 1. Spawned from brick at (x, y)
 * 2. Falls down each frame
 * 3. Collected by paddle → apply effect
 * 4. If timed effect → schedule removal
 * 5. Destroyed when off-screen or collected
 * 
 * @author SteveHoang aka BoizSocSon
 */
public abstract class PowerUp implements GameObject {
    protected double x;
    protected double y;
    protected final double width;
    protected final double height;
    protected final PowerUpType type;
    protected final Velocity velocity;
    protected final Animation animation;
    protected boolean active = true;

    /**
     * Creates a PowerUp at specified position.
     * 
     * @param x X-coordinate (top-left)
     * @param y Y-coordinate (top-left)
     * @param type PowerUpType enum value
     */
    public PowerUp(double x, double y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.width = Constants.PowerUps.POWERUP_WIDTH;
        this.height = Constants.PowerUps.POWERUP_HEIGHT;
        
        // Constant downward velocity
        this.velocity = new Velocity(0, Constants.PowerUps.POWERUP_FALL_SPEED);
        
        // Load animated sprite
        this.animation = AnimationFactory.createPowerUpAnimation(type);
        this.animation.play();
    }

    /**
     * Updates powerup state each frame.
     * 
     * Actions:
     * - Move down
     * - Update animation
     */
    public void update() {
        // Move down
        Point currentPos = new Point(x, y);
        Point newPos = velocity.applyToPoint(currentPos);
        this.x = newPos.getX();
        this.y = newPos.getY();
        
        // Update animation
        if (animation != null) {
            animation.update();
        }
    }

    /**
     * Renders the powerup.
     * 
     * @param renderer The renderer to use
     */
    public void render(Renderer renderer) {
        if (animation != null && active) {
            renderer.drawImage(animation.getCurrentFrame(), x, y);
        }
    }

    // Accessors
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public PowerUpType getType() { return type; }
    public boolean isActive() { return active; }

    /** Apply effect to paddle or game state. */
    public abstract void applyEffect(Paddle paddle);

    /** Remove effect when expired. */
    public abstract void removeEffect(Paddle paddle);

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

