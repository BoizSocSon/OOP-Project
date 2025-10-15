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
<<<<<<< HEAD
 *
=======
 * 
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
 * Responsibilities:
 * - Ball vs Wall collisions
 * - Ball vs Paddle collisions (with angle adjustment)
 * - Ball vs Brick collisions
 * - Laser vs Brick collisions
 * - PowerUp vs Paddle collisions
<<<<<<< HEAD
 *
=======
 * 
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
 * Design:
 * - Separates collision logic from GameManager
 * - Uses AABB (Axis-Aligned Bounding Box) for broad phase
 * - Swept collision for accurate detection
 * - Optimized with spatial partitioning (future enhancement)
<<<<<<< HEAD
=======
 * 
 * @author SteveHoang aka BoizSocSon
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
 */
public class CollisionManager {
    private int playAreaWidth;
    private int playAreaHeight;
    private static final double MAX_BOUNCE_ANGLE = Constants.Physics.PADDLE_MAX_ANGLE;
<<<<<<< HEAD

=======
    
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
    /**
     * Creates collision manager for specified play area.
     * @param width Play area width
     * @param height Play area height
     */
    public CollisionManager(int width, int height) {
        this.playAreaWidth = width;
        this.playAreaHeight = height;
    }
<<<<<<< HEAD

    /**
     * Checks and handles ball collision with walls.
     * Reverses velocity and plays SFX on collision.
     *
=======
    
    /**
     * Checks and handles ball collision with walls.
     * Reverses velocity and plays SFX on collision.
     * 
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
     * @param ball Ball to check
     * @param leftBorder Left boundary (usually 0)
     * @param rightBorder Right boundary (usually playAreaWidth)
     * @param topBorder Top boundary (usually 0)
     */
    public void checkBallWallCollisions(Ball ball, double leftBorder, double rightBorder, double topBorder) {
        boolean collided = false;
<<<<<<< HEAD

=======
        
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
        // Left wall collision
        if (ball.getX() <= leftBorder) {
            ball.setX(leftBorder);
            ball.setVelocity(new Velocity(
<<<<<<< HEAD
                    Math.abs(ball.getVelocity().getDx()),
                    ball.getVelocity().getDy()
            ));
            collided = true;
        }

=======
                Math.abs(ball.getVelocity().getDx()), 
                ball.getVelocity().getDy()
            ));
            collided = true;
        }
        
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
        // Right wall collision
        if (ball.getX() + ball.getWidth() >= rightBorder) {
            ball.setX(rightBorder - ball.getWidth());
            ball.setVelocity(new Velocity(
<<<<<<< HEAD
                    -Math.abs(ball.getVelocity().getDx()),
                    ball.getVelocity().getDy()
            ));
            collided = true;
        }

=======
                -Math.abs(ball.getVelocity().getDx()), 
                ball.getVelocity().getDy()
            ));
            collided = true;
        }
        
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
        // Top wall collision
        if (ball.getY() <= topBorder) {
            ball.setY(topBorder);
            ball.setVelocity(new Velocity(
<<<<<<< HEAD
                    ball.getVelocity().getDx(),
                    Math.abs(ball.getVelocity().getDy())
            ));
            collided = true;
        }

=======
                ball.getVelocity().getDx(), 
                Math.abs(ball.getVelocity().getDy())
            ));
            collided = true;
        }
        
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
        // Play SFX if collision occurred
        if (collided) {
            // AudioManager.playSFX(WALL_HIT) - to be implemented
        }
    }
<<<<<<< HEAD

=======
    
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
    /**
     * Checks and handles ball collision with paddle.
     * Adjusts ball angle based on hit position.
     * Handles catch mode (ball sticks to paddle).
<<<<<<< HEAD
     *
=======
     * 
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
     * @param ball Ball to check
     * @param paddle Paddle to check against
     * @return true if collision occurred
     */
    public boolean checkBallPaddleCollision(Ball ball, Paddle paddle) {
        // Check if bounds intersect
        if (!ball.getBounds().intersects(paddle.getBounds())) {
            return false;
        }
<<<<<<< HEAD

=======
        
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
        // Calculate hit position on paddle (-1 to 1, where 0 is center)
        double ballCenterX = ball.getCenter().getX();
        double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
        double paddleHalfWidth = paddle.getWidth() / 2.0;
<<<<<<< HEAD

        double hitPosition = (ballCenterX - paddleCenterX) / paddleHalfWidth;
        hitPosition = Math.max(-1.0, Math.min(1.0, hitPosition)); // Clamp to [-1, 1]

=======
        
        double hitPosition = (ballCenterX - paddleCenterX) / paddleHalfWidth;
        hitPosition = Math.max(-1.0, Math.min(1.0, hitPosition)); // Clamp to [-1, 1]
        
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
        // Check if catch mode is enabled
        if (paddle.isCatchModeEnabled()) {
            // Ball sticks to paddle - handled by GameManager
            // Just signal collision occurred
            // AudioManager.playSFX(PADDLE_HIT_CATCH) - to be implemented
            return true;
        }
<<<<<<< HEAD

        // Calculate new angle based on hit position
        calculateBallAngleFromPaddle(ball, paddle, hitPosition);

        // Push ball above paddle to prevent repeated collisions
        ball.setY(paddle.getY() - ball.getHeight() - 1);

        // Play paddle hit SFX
        // AudioManager.playSFX(PADDLE_HIT) - to be implemented

        return true;
    }

=======
        
        // Calculate new angle based on hit position
        calculateBallAngleFromPaddle(ball, paddle, hitPosition);
        
        // Push ball above paddle to prevent repeated collisions
        ball.setY(paddle.getY() - ball.getHeight() - 1);
        
        // Play paddle hit SFX
        // AudioManager.playSFX(PADDLE_HIT) - to be implemented
        
        return true;
    }
    
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
    /**
     * Adjusts ball velocity based on where it hit the paddle.
     * Hit at center = straight up
     * Hit at edges = angled bounce (up to MAX_BOUNCE_ANGLE degrees)
<<<<<<< HEAD
     *
=======
     * 
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
     * @param ball Ball to adjust
     * @param paddle Paddle that was hit
     * @param hitPosition Position on paddle (-1 to 1)
     */
    private void calculateBallAngleFromPaddle(Ball ball, Paddle paddle, double hitPosition) {
        // Get current speed (magnitude)
        double speed = Math.hypot(ball.getVelocity().getDx(), ball.getVelocity().getDy());
<<<<<<< HEAD

        // Calculate angle in degrees (-MAX_BOUNCE_ANGLE to +MAX_BOUNCE_ANGLE)
        double angle = hitPosition * MAX_BOUNCE_ANGLE;

        // Convert to radians
        double angleRad = Math.toRadians(angle);

=======
        
        // Calculate angle in degrees (-MAX_BOUNCE_ANGLE to +MAX_BOUNCE_ANGLE)
        double angle = hitPosition * MAX_BOUNCE_ANGLE;
        
        // Convert to radians
        double angleRad = Math.toRadians(angle);
        
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
        // Calculate new velocity components
        // Angle 0 = straight up (dy = -speed, dx = 0)
        // Positive angle = right, negative angle = left
        double dx = speed * Math.sin(angleRad);
        double dy = -speed * Math.cos(angleRad); // Negative because up is negative Y
<<<<<<< HEAD

        ball.setVelocity(new Velocity(dx, dy));
    }

    /**
     * Checks ball collisions with all bricks.
     * Returns list of destroyed bricks for scoring.
     *
=======
        
        ball.setVelocity(new Velocity(dx, dy));
    }
    
    /**
     * Checks ball collisions with all bricks.
     * Returns list of destroyed bricks for scoring.
     * 
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
     * @param ball Ball to check
     * @param bricks List of bricks to check against
     * @return List of bricks that were destroyed
     */
    public List<Brick> checkBallBrickCollisions(Ball ball, List<Brick> bricks) {
        List<Brick> destroyedBricks = new ArrayList<>();
<<<<<<< HEAD

=======
        
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
        for (Brick brick : bricks) {
            if (!brick.isAlive()) {
                continue;
            }
<<<<<<< HEAD

=======
            
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
            // Check collision using Ball's built-in swept collision
            if (ball.checkCollisionWithRect(brick.getBounds())) {
                // Brick takes damage
                brick.takeHit();
<<<<<<< HEAD

                // Play hit SFX
                // AudioManager.playSFX(BRICK_HIT) - to be implemented

=======
                
                // Play hit SFX
                // AudioManager.playSFX(BRICK_HIT) - to be implemented
                
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
                // If brick was destroyed, add to list
                if (brick.isDestroyed()) {
                    destroyedBricks.add(brick);
                }
            }
        }
<<<<<<< HEAD

        return destroyedBricks;
    }

    /**
     * Checks laser collisions with bricks.
     * Each laser can only hit one brick.
     *
=======
        
        return destroyedBricks;
    }
    
    /**
     * Checks laser collisions with bricks.
     * Each laser can only hit one brick.
     * 
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
     * @param lasers List of active lasers
     * @param bricks List of bricks
     * @return Map of laser-brick collision pairs
     */
    public Map<Laser, Brick> checkLaserBrickCollisions(List<Laser> lasers, List<Brick> bricks) {
        Map<Laser, Brick> collisions = new HashMap<>();
<<<<<<< HEAD

=======
        
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
        for (Laser laser : lasers) {
            if (!laser.isAlive()) {
                continue;
            }
<<<<<<< HEAD

=======
            
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
            for (Brick brick : bricks) {
                if (!brick.isAlive()) {
                    continue;
                }
<<<<<<< HEAD

=======
                
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
                // Check AABB intersection
                if (laser.getBounds().intersects(brick.getBounds())) {
                    // Brick takes damage
                    brick.takeHit();
<<<<<<< HEAD

                    // Record collision
                    collisions.put(laser, brick);

                    // Play laser hit SFX
                    // AudioManager.playSFX(LASER_HIT) - to be implemented

=======
                    
                    // Record collision
                    collisions.put(laser, brick);
                    
                    // Play laser hit SFX
                    // AudioManager.playSFX(LASER_HIT) - to be implemented
                    
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
                    // Laser can only hit one brick
                    break;
                }
            }
        }
<<<<<<< HEAD

        return collisions;
    }

    /**
     * Checks powerup collisions with paddle.
     * Returns list of collected powerups.
     *
=======
        
        return collisions;
    }
    
    /**
     * Checks powerup collisions with paddle.
     * Returns list of collected powerups.
     * 
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
     * @param powerUps List of active powerups
     * @param paddle Player's paddle
     * @return List of powerups that were collected
     */
    public List<PowerUp> checkPowerUpPaddleCollisions(List<PowerUp> powerUps, Paddle paddle) {
        List<PowerUp> collected = new ArrayList<>();
<<<<<<< HEAD

=======
        
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
        for (PowerUp powerUp : powerUps) {
            if (!powerUp.isAlive()) {
                continue;
            }
<<<<<<< HEAD

            // Check AABB intersection
            if (powerUp.getBounds().intersects(paddle.getBounds())) {
                collected.add(powerUp);

=======
            
            // Check AABB intersection
            if (powerUp.getBounds().intersects(paddle.getBounds())) {
                collected.add(powerUp);
                
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
                // Play collection SFX
                // AudioManager.playSFX(POWERUP_COLLECT) - to be implemented
            }
        }
<<<<<<< HEAD

        return collected;
    }

=======
        
        return collected;
    }
    
>>>>>>> 8656814 (feat: Implement rounds classes and round manger. Build up Engine Manager)
    /**
     * Updates play area dimensions (useful for resolution changes).
     */
    public void setPlayAreaSize(int width, int height) {
        this.playAreaWidth = width;
        this.playAreaHeight = height;
    }
}
