package Objects.GameEntities;

import GeometryPrimitives.Velocity;
import Render.Animation;
import Render.Renderer;
import Objects.Core.MovableObject;
import Utils.AnimationFactory;
import java.util.List;
import java.util.ArrayList;

/**
 * Thanh điều khiển (paddle) do người chơi điều khiển.
 *
 * Tính năng:
 * - Di chuyển ngang (left/right/stop) bằng cách thay đổi vận tốc theo trục x.
 * - PowerUp effects với animations (WIDE, LASER, MATERIALIZE, EXPLODE)
 * - Animation state management
 * - Laser shooting system với cooldown
 */
public class Paddle extends MovableObject {
    private double speed; // pixels per frame
    private double originalWidth; // Store original width for expand/contract
    
    // PowerUp state flags
    private boolean catchMode = false;
    private int laserShots = 0;
    private long laserCooldown = 0; // Timestamp when next laser can be fired
    
    // Animation system
    private PaddleState currentState = PaddleState.NORMAL;
    private Animation currentAnimation = null;
    private boolean animationPlaying = false;
    
    // Timers for effects
    private long expandExpiryTime = 0; // When wide paddle should shrink

    public Paddle(double x, double y, double width, double height, double speed) {
        super(x, y, width, height);
        this.speed = speed;
        this.originalWidth = width; // Store for expand/contract
    }

    @Override
    public void update() {
        move();
        
        // Update animation if playing
        if (animationPlaying && currentAnimation != null) {
            currentAnimation.update();
            
            // Check if one-shot animations finished (MATERIALIZE, EXPLODE)
            if (currentAnimation.isFinished()) {
                if (currentState == PaddleState.MATERIALIZE) {
                    // After materialize, go to NORMAL
                    setState(PaddleState.NORMAL);
                } else if (currentState == PaddleState.EXPLODE) {
                    // After explode, stay hidden or reset
                    animationPlaying = false;
                }
            }
        }
        
        // Check if expand effect should expire
        if (expandExpiryTime > 0 && System.currentTimeMillis() >= expandExpiryTime) {
            shrinkToNormal();
            expandExpiryTime = 0;
        }
    }

    @Override
    public void render(Renderer renderer) {
        renderer.drawPaddle(this);
    }

    /** Bắt đầu di chuyển sang trái bằng cách đặt vận tốc âm. */
    public void moveLeft() {
        setVelocity(new Velocity(-speed, 0));
    }

    /** Bắt đầu di chuyển sang phải bằng cách đặt vận tốc dương. */
    public void moveRight() {
        setVelocity(new Velocity(speed, 0));
    }

    /** Dừng chuyển động ngang. */
    public void stop() {
        setVelocity(new Velocity(0,0));
    }

    // ============================================================
    // Animation System
    // ============================================================
    
    /**
     * Sets paddle state and loads corresponding animation.
     * @param state New paddle state
     */
    public void setState(PaddleState state) {
        if (this.currentState == state && animationPlaying) {
            return; // Already in this state
        }
        
        this.currentState = state;
        this.currentAnimation = AnimationFactory.createPaddleAnimation(state);
        
        if (currentAnimation != null) {
            currentAnimation.play();
            animationPlaying = true;
        }
    }
    
    /**
     * Gets current paddle state.
     */
    public PaddleState getState() {
        return currentState;
    }
    
    /**
     * Gets current animation for rendering.
     */
    public Animation getAnimation() {
        return currentAnimation;
    }
    
    /**
     * Checks if animation is currently playing.
     */
    public boolean isAnimationPlaying() {
        return animationPlaying && currentAnimation != null && currentAnimation.isPlaying();
    }

    // ============================================================
    // PowerUp Effects
    // ============================================================
    
    /**
     * Enables laser mode and sets number of shots.
     * @param shots Number of laser shots to grant
     */
    public void enableLaser(int shots) {
        setState(PaddleState.LASER);
        this.laserShots = shots;
        // AudioManager.playSFX(LASER_ACTIVATE) - to be implemented
    }
    
    /**
     * Shoots lasers from paddle sides.
     * @return List of newly created Laser objects (2 lasers - left & right)
     */
    public List<Laser> shootLaser() {
        List<Laser> lasers = new ArrayList<>();
        
        // Check if can shoot
        if (laserShots <= 0) {
            return lasers; // Empty list
        }
        
        long now = System.currentTimeMillis();
        if (now < laserCooldown) {
            return lasers; // Still on cooldown
        }
        
        // Consume one shot
        laserShots--;
        
        // Set cooldown (300ms)
        laserCooldown = now + 300;
        
        // Create 2 lasers (left & right side of paddle)
        double paddleLeft = getX();
        double paddleRight = getX() + getWidth();
        double paddleTop = getY();
        
        // Left laser (offset 10px from left edge)
        lasers.add(new Laser(paddleLeft + 10, paddleTop));
        
        // Right laser (offset 10px from right edge)
        lasers.add(new Laser(paddleRight - 14, paddleTop)); // -14 to account for laser width
        
        // AudioManager.playSFX(LASER_SHOOT) - to be implemented
        
        // If no more shots, disable laser mode
        if (laserShots <= 0) {
            setState(PaddleState.NORMAL);
        }
        
        return lasers;
    }
    
    /**
     * Expands paddle to wide size.
     */
    public void expand() {
        setState(PaddleState.WIDE);
        
        // Expand width by 1.5x
        double centerX = getX() + getWidth() / 2.0;
        setWidth(originalWidth * 1.5);
        
        // Adjust X to keep center position
        setX(centerX - getWidth() / 2.0);
        
        // Schedule shrink after 10 seconds
        expandExpiryTime = System.currentTimeMillis() + 10000;
    }
    
    /**
     * Shrinks paddle back to normal size.
     */
    private void shrinkToNormal() {
        setState(PaddleState.NORMAL);
        
        // Restore original width
        double centerX = getX() + getWidth() / 2.0;
        setWidth(originalWidth);
        
        // Adjust X to keep center position
        setX(centerX - getWidth() / 2.0);
    }
    
    /**
     * Enables catch mode (ball sticks to paddle).
     */
    public void enableCatch() {
        setState(PaddleState.PULSATE);
        this.catchMode = true;
    }
    
    /**
     * Disables catch mode.
     */
    public void disableCatch() {
        this.catchMode = false;
        if (currentState == PaddleState.PULSATE) {
            setState(PaddleState.NORMAL);
        }
    }

    // ============================================================
    // PowerUp State Getters/Setters
    // ============================================================
    
    public boolean isCatchModeEnabled() {
        return catchMode;
    }
    
    public void setCatchModeEnabled(boolean enabled) {
        this.catchMode = enabled;
        if (enabled) {
            enableCatch();
        } else {
            disableCatch();
        }
    }
    
    public boolean isLaserEnabled() {
        return laserShots > 0 && currentState == PaddleState.LASER;
    }
    
    public void setLaserEnabled(boolean enabled) {
        if (enabled) {
            enableLaser(5); // Default 5 shots
        } else {
            this.laserShots = 0;
            if (currentState == PaddleState.LASER) {
                setState(PaddleState.NORMAL);
            }
        }
    }
    
    public int getLaserShots() {
        return laserShots;
    }
    
    public void setLaserShots(int shots) {
        this.laserShots = shots;
        if (shots > 0) {
            setState(PaddleState.LASER);
        } else if (currentState == PaddleState.LASER) {
            setState(PaddleState.NORMAL);
        }
    }
    
    public double getOriginalWidth() {
        return originalWidth;
    }

    public double getSpeed() {
        return speed;
    }
    
    public void setSpeed(double s) {
        this.speed = s;
    }
    
    /**
     * Triggers materialize animation (spawn effect).
     */
    public void playMaterializeAnimation() {
        setState(PaddleState.MATERIALIZE);
    }
    
    /**
     * Triggers explode animation (death effect).
     */
    public void playExplodeAnimation() {
        setState(PaddleState.EXPLODE);
    }
}
