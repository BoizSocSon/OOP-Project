package Objects.Bricks;

import Engine.PowerUpManager;
import Render.Animation;
import Render.Renderer;
import Utils.AnimationFactory;
import Objects.Core.GameObject;
import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;

/**
 * Represents a Silver brick that takes 2 hits to destroy and shows a crack animation.
 */
public class SilverBrick extends Brick {
    private final Animation crackAnimation;
    private int currentHP;
    private double x;
    private double y;

    /**
     * Creates a new SilverBrick at the specified position.
     * @param x X-coordinate of the brick
     * @param y Y-coordinate of the brick
     */
    public SilverBrick(double x, double y) {
        this.x = x;
        this.y = y;
        this.currentHP = 2;
        this.crackAnimation = AnimationFactory.createBrickCrackAnimation();
    }

    /**
     * Handles the brick being hit by a ball.
     * First hit starts crack animation, second hit destroys the brick.
     */
    @Override
    public void takeHit() {
        currentHP--;
        
        if (currentHP == 1) {
            crackAnimation.start();
        }
        
        if (currentHP == 0) {
            destroy();
        }
    }

    /**
     * Updates the brick's state, including crack animation if playing.
     */
    @Override
    public void update() {
        if (crackAnimation != null && crackAnimation.isPlaying()) {
            crackAnimation.update();
        }
    }

    /**
     * Gets the collision bounds of this brick.
     * @return Rectangle representing the brick's bounds
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 60, 20); // Assuming standard brick size
    }

    /**
     * Renders the brick.
     * Shows normal sprite at full health, crack animation when damaged.
     * @param renderer The renderer to use
     */
    @Override
    public void render(Renderer renderer) {
        if (currentHP == 2) {
            renderer.drawSprite(BrickType.SILVER.getSpriteName(), x, y);
        } else if (currentHP == 1 && crackAnimation != null) {
            renderer.drawSprite(crackAnimation.getCurrentFrame(), x, y);
        }
    }

    /**
     * Handles brick destruction.
     * Spawns a random power-up when destroyed.
     */
    @Override
    public void destroy() {
        if (PowerUpManager.getInstance() != null) {
            PowerUpManager.getInstance().spawnRandomPowerUp(x, y);
        }
        setDestroyed(true);
    }

    /**
     * Checks if this brick has been destroyed.
     * @return true if the brick is destroyed, false otherwise
     */
    @Override
    public boolean isDestroyed() {
        return currentHP <= 0;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    private void setDestroyed(boolean destroyed) {
        if (destroyed) {
            currentHP = 0;
        }
    }
}