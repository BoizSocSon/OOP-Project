package Objects.GameEntities;

import GeometryPrimitives.Velocity;
import Render.Animation;
import Render.Renderer;
import Objects.Core.MovableObject;
import Utils.AnimationFactory;

/**
 * Thanh điều khiển (paddle) do người chơi điều khiển.
 *
 * Tính năng:
 * - Di chuyển ngang (left/right/stop) bằng cách thay đổi vận tốc theo trục x.
 * - PowerUp effects với animations (WIDE, LASER, MATERIALIZE, EXPLODE)
 * - Animation state management
 */
public class Paddle extends MovableObject {
    private double speed; // pixels per frame
    private double originalWidth; // Store original width for expand/contract
    
    // PowerUp state flags (managed by GameManager)
    private boolean catchModeEnabled = false;
    private boolean laserEnabled = false;
    private int laserShots = 0;
    
    // Animation system
    private PaddleState currentState = PaddleState.NORMAL;
    private Animation currentAnimation = null;
    private boolean animationPlaying = false;

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
    // PowerUp State Getters/Setters (for GameManager)
    // ============================================================
    
    public boolean isCatchModeEnabled() {
        return catchModeEnabled;
    }
    
    public void setCatchModeEnabled(boolean enabled) {
        this.catchModeEnabled = enabled;
    }
    
    public boolean isLaserEnabled() {
        return laserEnabled;
    }
    
    public void setLaserEnabled(boolean enabled) {
        this.laserEnabled = enabled;
        if (enabled) {
            setState(PaddleState.LASER);
        } else {
            setState(PaddleState.NORMAL);
        }
    }
    
    public int getLaserShots() {
        return laserShots;
    }
    
    public void setLaserShots(int shots) {
        this.laserShots = shots;
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
