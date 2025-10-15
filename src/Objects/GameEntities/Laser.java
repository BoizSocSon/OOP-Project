package Objects.GameEntities;

import GeometryPrimitives.Velocity;
import Objects.Core.MovableObject;
import Render.Renderer;

/**
 * Laser bullet shot from paddle with laser power-up.
 * 
 * Features:
 * - Moves straight up
 * - Destroys bricks on collision
 * - Auto-destroys when off-screen
 * 
 * Physics:
 * - Width: 4px
 * - Height: 16px
 * - Velocity: (0, -8) - fast upward movement
 * 
 * @author SteveHoang aka BoizSocSon
 */
public class Laser extends MovableObject {
    private static final double LASER_WIDTH = 4.0;
    private static final double LASER_HEIGHT = 16.0;
    private static final double LASER_SPEED = -8.0; // Negative = upward
    
    private boolean destroyed = false;

    /**
     * Creates a new laser at specified position.
     * @param x X coordinate (left edge)
     * @param y Y coordinate (top edge)
     */
    public Laser(double x, double y) {
        super(x, y, LASER_WIDTH, LASER_HEIGHT);
        setVelocity(new Velocity(0, LASER_SPEED));
    }

    @Override
    public void update() {
        if (destroyed) return;
        
        // Move upward
        move();
    }

    @Override
    public void render(Renderer renderer) {
        if (destroyed) return;
        
        // Use sprite rendering - renderer will handle loading from cache
        renderer.drawSprite("laser_bullet.png", getX(), getY());
    }

    /**
     * Checks if laser has moved off screen (top edge).
     * @return true if laser is above screen
     */
    public boolean isOffScreen() {
        return getY() + getHeight() < 0;
    }

    /**
     * Marks laser for destruction.
     */
    public void destroy() {
        this.destroyed = true;
    }

    /**
     * Checks if laser is destroyed.
     */
    @Override
    public boolean isAlive() {
        return !destroyed;
    }
}
