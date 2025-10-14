package Objects.Bricks;

import Render.Animation;
import Render.Renderer;
import Utils.AnimationFactory;
import Utils.Constants;
import Engine.PowerUpManager;
import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;

/**
 * Silver Brick - takes 2 hits to destroy with crack animation.
 *
 * Behavior:
 * - HP = 2 (requires 2 hits)
 * - First hit: starts crack animation (10 frames, 50ms each)
 * - Second hit: destroys brick and spawns powerup
 * - Collision remains active during animation
 *
 * @author SteveHoang aka BoizSocSon
 */
public class SilverBrick extends Brick {
    private final Animation crackAnimation;
    private int currentHP;

    /**
     * Creates a new SilverBrick at the specified position.
     *
     * @param x X-coordinate (top-left corner)
     * @param y Y-coordinate (top-left corner)
     * @param width Brick width (typically Constants.Bricks.BRICK_WIDTH)
     * @param height Brick height (typically Constants.Bricks.BRICK_HEIGHT)
     */
    public SilverBrick(double x, double y, double width, double height) {
        super(x, y, width, height, 2); // HP = 2
        this.currentHP = 2;
        this.crackAnimation = AnimationFactory.createBrickCrackAnimation();
    }

    /**
     * Handles the brick being hit by a ball.
     *
     * Logic:
     * - Decrements HP
     * - HP == 1: starts crack animation (does NOT loop)
     * - HP == 0: destroys brick
     *
     * Thread-safe for rapid consecutive hits.
     */
    @Override
    public void takeHit() {
        if (currentHP <= 0) {
            return; // Already destroyed, ignore additional hits
        }

        currentHP--;

        if (currentHP == 1) {
            // Start crack animation on first hit
            crackAnimation.play();
        } else if (currentHP == 0) {
            // Destroy on second hit
            destroy();
        }
    }

    /**
     * Updates the brick's state each frame.
     *
     * Must be called every frame to advance crack animation.
     */
    @Override
    public void update() {
        // Update crack animation if it's playing
        if (crackAnimation != null && crackAnimation.isPlaying()) {
            crackAnimation.update();
        }
    }

    /**
     * Gets the collision bounds of this brick.
     *
     * Collision remains active during crack animation to prevent
     * ball from passing through.
     *
     * @return Rectangle representing the brick's collision box
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point(getX(), getY()), getWidth(), getHeight());
    }

    /**
     * Renders the brick with appropriate sprite.
     *
     * Rendering logic:
     * - HP == 2: normal silver brick sprite
     * - HP == 1: current frame of crack animation
     * - HP == 0: don't render (brick is destroyed)
     *
     * @param renderer The renderer to use for drawing
     */
    @Override
    public void render(Renderer renderer) {
        if (currentHP == 2) {
            // Full health: render normal silver brick sprite
            renderer.drawSprite(BrickType.SILVER.getSpriteName(), getX(), getY());
        } else if (currentHP == 1 && crackAnimation != null) {
            // Damaged: render crack animation's current frame
            renderer.drawImage(crackAnimation.getCurrentFrame(), getX(), getY());
        }
        // currentHP == 0: don't render (destroyed)
    }

    /**
     * Handles brick destruction.
     *
     * Actions:
     * 1. Spawns random powerup at brick's center position
     * 2. Marks brick as not alive (for removal from game)
     */
    @Override
    public void destroy() {
        // Spawn powerup at brick's center
        double centerX = getX() + getWidth() / 2.0;
        double centerY = getY() + getHeight() / 2.0;

        PowerUpManager manager = PowerUpManager.getInstance();
        if (manager != null) {
            manager.spawnFromBrick(centerX, centerY, BrickType.SILVER);
        }

        // Mark as destroyed (parent class handles alive flag)
        super.destroy();
    }

    /**
     * Checks if this brick has been destroyed.
     *
     * @return true if HP <= 0, false otherwise
     */
    @Override
    public boolean isDestroyed() {
        return currentHP <= 0 || !isAlive();
    }

    /**
     * Gets current hit points (for debugging/testing).
     *
     * @return Current HP (2, 1, or 0)
     */
    public int getCurrentHP() {
        return currentHP;
    }

    /**
     * Checks if crack animation is currently playing.
     *
     * @return true if animation is active
     */
    public boolean isCrackAnimationPlaying() {
        return crackAnimation != null && crackAnimation.isPlaying();
    }
    
    /**
     * Gets the crack animation for rendering.
     * Used by CanvasRenderer to render animated crack effect.
     *
     * @return Crack animation object, or null if not available
     */
    public Animation getCrackAnimation() {
        return crackAnimation;
    }
}