package Engine;

import Objects.GameEntities.Ball;
import Objects.GameEntities.Paddle;
import Objects.GameEntities.Laser;
import Objects.Bricks.Brick;
import Objects.PowerUps.PowerUp;
import GeometryPrimitives.Velocity;
import Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CollisionManager - Centralized collision detection and response system.
 * 
 * Responsibilities:
 * - Ball vs Wall collisions
 * - Ball vs Paddle collisions (with angle adjustment)
 * - Ball vs Brick collisions
 * - Laser vs Brick collisions
 * - PowerUp vs Paddle collisions
 * 
 * Design:
 * - Separates collision logic from GameManager
 * - Uses AABB (Axis-Aligned Bounding Box) for broad phase
 * - Swept collision for accurate detection
 * - Optimized with spatial partitioning (future enhancement)
 * 
 * @author SteveHoang aka BoizSocSon
 */
public class CollisionManager {
    private int playAreaWidth;
    private int playAreaHeight;
    private static final double MAX_BOUNCE_ANGLE = Constants.Physics.PADDLE_MAX_ANGLE;
    
    /**
     * Creates collision manager for specified play area.
     * @param width Play area width
     * @param height Play area height
     */
    public CollisionManager(int width, int height) {
        this.playAreaWidth = width;
        this.playAreaHeight = height;
    }
    
    /**
     * Checks and handles ball collision with walls.
     * Reverses velocity and plays SFX on collision.
     * 
     * @param ball Ball to check
     * @param leftBorder Left boundary (usually 0)
     * @param rightBorder Right boundary (usually playAreaWidth)
     * @param topBorder Top boundary (usually 0)
     */
    public void checkBallWallCollisions(Ball ball, double leftBorder, double rightBorder, double topBorder) {
        boolean collided = false;
        
        // Left wall collision
        if (ball.getX() <= leftBorder) {
            ball.setX(leftBorder);
            ball.setVelocity(new Velocity(
                Math.abs(ball.getVelocity().getDx()), 
                ball.getVelocity().getDy()
            ));
            collided = true;
        }
        
        // Right wall collision
        if (ball.getX() + ball.getWidth() >= rightBorder) {
            ball.setX(rightBorder - ball.getWidth());
            ball.setVelocity(new Velocity(
                -Math.abs(ball.getVelocity().getDx()), 
                ball.getVelocity().getDy()
            ));
            collided = true;
        }
        
        // Top wall collision
        if (ball.getY() <= topBorder) {
            ball.setY(topBorder);
            ball.setVelocity(new Velocity(
                ball.getVelocity().getDx(), 
                Math.abs(ball.getVelocity().getDy())
            ));
            collided = true;
        }
        
        // Play SFX if collision occurred
        if (collided) {
            // AudioManager.playSFX(WALL_HIT) - to be implemented
        }
    }
    
    /**
     * Checks and handles ball collision with paddle.
     * Adjusts ball angle based on hit position.
     * Handles catch mode (ball sticks to paddle).
     * 
     * @param ball Ball to check
     * @param paddle Paddle to check against
     * @return true if collision occurred
     */
    public boolean checkBallPaddleCollision(Ball ball, Paddle paddle) {
        // Check if bounds intersect
        if (!ball.getBounds().intersects(paddle.getBounds())) {
            return false;
        }
        
        // Calculate hit position on paddle (-1 to 1, where 0 is center)
        double ballCenterX = ball.getCenter().getX();
        double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
        double paddleHalfWidth = paddle.getWidth() / 2.0;
        
        double hitPosition = (ballCenterX - paddleCenterX) / paddleHalfWidth;
        hitPosition = Math.max(-1.0, Math.min(1.0, hitPosition)); // Clamp to [-1, 1]
        
        // Check if catch mode is enabled
        if (paddle.isCatchModeEnabled()) {
            // Ball sticks to paddle - handled by GameManager
            // Just signal collision occurred
            // AudioManager.playSFX(PADDLE_HIT_CATCH) - to be implemented
            return true;
        }
        
        // Calculate new angle based on hit position
        calculateBallAngleFromPaddle(ball, paddle, hitPosition);
        
        // Push ball above paddle to prevent repeated collisions
        ball.setY(paddle.getY() - ball.getHeight() - 1);
        
        // Play paddle hit SFX
        // AudioManager.playSFX(PADDLE_HIT) - to be implemented
        
        return true;
    }
    
    /**
     * Adjusts ball velocity based on where it hit the paddle.
     * Hit at center = straight up
     * Hit at edges = angled bounce (up to MAX_BOUNCE_ANGLE degrees)
     * 
     * @param ball Ball to adjust
     * @param paddle Paddle that was hit
     * @param hitPosition Position on paddle (-1 to 1)
     */
    private void calculateBallAngleFromPaddle(Ball ball, Paddle paddle, double hitPosition) {
        // Get current speed (magnitude)
        double speed = Math.hypot(ball.getVelocity().getDx(), ball.getVelocity().getDy());
        
        // Calculate angle in degrees (-MAX_BOUNCE_ANGLE to +MAX_BOUNCE_ANGLE)
        double angle = hitPosition * MAX_BOUNCE_ANGLE;
        
        // Convert to radians
        double angleRad = Math.toRadians(angle);
        
        // Calculate new velocity components
        // Angle 0 = straight up (dy = -speed, dx = 0)
        // Positive angle = right, negative angle = left
        double dx = speed * Math.sin(angleRad);
        double dy = -speed * Math.cos(angleRad); // Negative because up is negative Y
        
        ball.setVelocity(new Velocity(dx, dy));
    }
    
    /**
     * Checks ball collisions with all bricks.
     * Returns list of destroyed bricks for scoring.
     * 
     * @param ball Ball to check
     * @param bricks List of bricks to check against
     * @return List of bricks that were destroyed
     */
    public List<Brick> checkBallBrickCollisions(Ball ball, List<Brick> bricks) {
        List<Brick> destroyedBricks = new ArrayList<>();
        
        for (Brick brick : bricks) {
            if (!brick.isAlive()) {
                continue;
            }
            
            // Check collision using Ball's built-in swept collision
            if (ball.checkCollisionWithRect(brick.getBounds())) {
                // Brick takes damage
                brick.takeHit();
                
                // Play hit SFX
                // AudioManager.playSFX(BRICK_HIT) - to be implemented
                
                // If brick was destroyed, add to list
                if (brick.isDestroyed()) {
                    destroyedBricks.add(brick);
                }
            }
        }
        
        return destroyedBricks;
    }
    
    /**
     * Checks laser collisions with bricks.
     * Each laser can only hit one brick.
     * 
     * @param lasers List of active lasers
     * @param bricks List of bricks
     * @return Map of laser-brick collision pairs
     */
    public Map<Laser, Brick> checkLaserBrickCollisions(List<Laser> lasers, List<Brick> bricks) {
        Map<Laser, Brick> collisions = new HashMap<>();
        
        for (Laser laser : lasers) {
            if (!laser.isAlive()) {
                continue;
            }
            
            for (Brick brick : bricks) {
                if (!brick.isAlive()) {
                    continue;
                }
                
                // Check AABB intersection
                if (laser.getBounds().intersects(brick.getBounds())) {
                    // Brick takes damage
                    brick.takeHit();
                    
                    // Record collision
                    collisions.put(laser, brick);
                    
                    // Play laser hit SFX
                    // AudioManager.playSFX(LASER_HIT) - to be implemented
                    
                    // Laser can only hit one brick
                    break;
                }
            }
        }
        
        return collisions;
    }
    
    /**
     * Checks powerup collisions with paddle.
     * Returns list of collected powerups.
     * 
     * @param powerUps List of active powerups
     * @param paddle Player's paddle
     * @return List of powerups that were collected
     */
    public List<PowerUp> checkPowerUpPaddleCollisions(List<PowerUp> powerUps, Paddle paddle) {
        List<PowerUp> collected = new ArrayList<>();
        
        for (PowerUp powerUp : powerUps) {
            if (!powerUp.isAlive()) {
                continue;
            }
            
            // Check AABB intersection
            if (powerUp.getBounds().intersects(paddle.getBounds())) {
                collected.add(powerUp);
                
                // Play collection SFX
                // AudioManager.playSFX(POWERUP_COLLECT) - to be implemented
            }
        }
        
        return collected;
    }
    
    /**
     * Updates play area dimensions (useful for resolution changes).
     */
    public void setPlayAreaSize(int width, int height) {
        this.playAreaWidth = width;
        this.playAreaHeight = height;
    }
}
