package Objects.PowerUps;

import Engine.GameManager;
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
    protected boolean collected = false;
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
     * - Update animation frames (flipbook)
     */
    public void update() {
        // Move down
        Point currentPos = new Point(x, y);
        Point newPos = velocity.applyToPoint(currentPos);
        this.x = newPos.getX();
        this.y = newPos.getY();
        
        // Update animation (flipbook frames)
        if (animation != null) {
            animation.update();
        }
    }

    /**
     * Renders the powerup using its current animation frame.
     * 
     * PowerUp animation is a flipbook (8 frames) that simulates
     * rotation around horizontal axis.
     * 
     * @param renderer The renderer to use
     */
    public void render(Renderer renderer) {
        if (animation != null && active) {
            renderer.drawPowerUp(this);
        }
    }

    /**
     * Gets the animation object for rendering.
     * @return Animation containing 8 frames of powerup sprite
     */
    public Animation getAnimation() {
        return animation;
    }

    /**
     * Checks collision between this powerup and the paddle.
     * Uses AABB (Axis-Aligned Bounding Box) collision detection.
     * 
     * Algorithm:
     * - PowerUp falls and paddle moves horizontally
     * - Collision occurs when rectangles overlap on both X and Y axes
     * - No collision if any gap exists between rectangles
     * 
     * @param paddle The paddle to check collision with
     * @return true if powerup intersects with paddle bounds
     */
    public boolean checkPaddleCollision(Paddle paddle) {
        if (paddle == null || !active) {
            return false;
        }
        
        Rectangle powerUpBounds = getBounds();
        Rectangle paddleBounds = paddle.getBounds();
        
        // AABB collision detection
        // No collision if:
        // - PowerUp right edge is left of paddle left edge
        // - PowerUp left edge is right of paddle right edge
        // - PowerUp bottom edge is above paddle top edge
        // - PowerUp top edge is below paddle bottom edge
        boolean noCollision = 
            (powerUpBounds.getUpperLeft().getX() + powerUpBounds.getWidth() < paddleBounds.getUpperLeft().getX()) ||
            (powerUpBounds.getUpperLeft().getX() > paddleBounds.getUpperLeft().getX() + paddleBounds.getWidth()) ||
            (powerUpBounds.getUpperLeft().getY() + powerUpBounds.getHeight() < paddleBounds.getUpperLeft().getY()) ||
            (powerUpBounds.getUpperLeft().getY() > paddleBounds.getUpperLeft().getY() + paddleBounds.getHeight());
        
        return !noCollision;
    }

    // Accessors
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public PowerUpType getType() { return type; }
    public boolean isActive() { return active; }
    public boolean isCollected() { return collected; }
    
    /**
     * Marks this powerup as collected by the paddle.
     * Once collected, it stops falling and will be removed from active list.
     */
    public void collect() {
        this.collected = true;
        this.active = false;
    }

    /**
     * Applies the powerup effect to the game.
     * This method must be implemented by each specific powerup subclass.
     * 
     * Examples:
     * - CatchPowerUp: Enable catch mode on paddle
     * - ExpandPaddlePowerUp: Increase paddle width
     * - LifePowerUp: Add extra life to player
     * 
     * @param gameManager The game manager to apply effects to
     */
    public abstract void applyEffect(GameManager gameManager);

    /**
     * Removes/reverts the powerup effect when it expires.
     * Only relevant for timed effects (CATCH, EXPAND, LASER, SLOW).
     * Instant effects (LIFE, DUPLICATE, WARP) don't need removal.
     * 
     * @param gameManager The game manager to remove effects from
     */
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

