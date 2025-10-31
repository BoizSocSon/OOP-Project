package Objects.GameEntities;

import Utils.AnimationFactory;
import Utils.Constants;
import GeometryPrimitives.Velocity;
import Render.Animation;
import Objects.Core.MovableObject;
import java.util.List;
import java.util.ArrayList;

public class Paddle extends MovableObject{
    private boolean catchMode = false;
    private int laserShots = 0;
    private long laserCooldown = 0;

    private PaddleState currentState = PaddleState.NORMAL;
    private Animation currentAnimation = null;
    private boolean animationPlaying = false;

    // Timers for effects
    private long expandExpiryTime = 0; // When wide paddle should shrink
    private long laserExpiryTime = 0; // When laser effect should expire
    private long catchExpiryTime = 0; // When catch effect should expire
    private long slowExpiryTime = 0; // When slow effect should expire


    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public void update() {
        move();

        // Update animation if playing
        if (animationPlaying && currentAnimation != null) {
            currentAnimation.update();

            // Check if one-shot animations finished
            if (currentAnimation.isFinished() && !currentAnimation.isPlaying()) {
                if (currentState == PaddleState.MATERIALIZE) {
                    // After materialize, go to NORMAL
                    System.out.println("Paddle: MATERIALIZE animation finished, switching to NORMAL");
                    setState(PaddleState.NORMAL);
                } else if (currentState == PaddleState.EXPLODE) {
                    // After explode, stop animation but keep state
                    // The paddle will be reset or respawned by GameManager
                    System.out.println("Paddle: EXPLODE animation finished");
                    animationPlaying = false;
                    currentAnimation = null;
                } else if (currentState == PaddleState.WIDE || currentState == PaddleState.LASER) {
                    // After transition animation, stop playing but keep state
                    // Paddle will show static sprite for this state
                    System.out.println("Paddle: " + currentState + " transition animation finished");
                    animationPlaying = false;
                } else if (currentAnimation.isReversed()) {
                    // Reversed animation finished, switch to NORMAL
                    System.out.println("Paddle: Reversed animation finished, switching to NORMAL");
                    setState(PaddleState.NORMAL);
                    animationPlaying = false;
                    currentAnimation = null;
                }
            }
        }

        long currentTime = System.currentTimeMillis();
        long warningThreshold = Constants.PowerUps.WARNING_THRESHOLD;

        // Check for WIDE pulsate warning
        if (expandExpiryTime > 0) {
            long timeRemaining = expandExpiryTime - currentTime;

            if (timeRemaining <= 0) {
                // Effect expired, shrink back to normal
                shrinkToNormal();
                expandExpiryTime = 0;
            } else if (timeRemaining <= warningThreshold && currentState == PaddleState.WIDE) {
                // Warning: switch to WIDE_PULSATE
                setState(PaddleState.WIDE_PULSATE);
                System.out.println("Paddle: WIDE effect expiring soon, switching to WIDE_PULSATE");
            }
        }

        // Check for LASER pulsate warning
        if (laserExpiryTime > 0) {
            long timeRemaining = laserExpiryTime - currentTime;

            if (timeRemaining <= 0) {
                // Effect expired, disable laser
                disableLaser();
                laserExpiryTime = 0;
            } else if (timeRemaining <= warningThreshold && currentState == PaddleState.LASER) {
                // Warning: switch to LASER_PULSATE
                setState(PaddleState.LASER_PULSATE);
                System.out.println("Paddle: LASER effect expiring soon, switching to LASER_PULSATE");
            }
        }

        // Check for CATCH/SLOW pulsate warning (shape-independent effects)
        boolean hasCatchEffect = catchExpiryTime > 0 && (catchExpiryTime - currentTime) > 0;
        boolean hasSlowEffect = slowExpiryTime > 0 && (slowExpiryTime - currentTime) > 0;
        boolean hasShapeIndependentEffect = hasCatchEffect || hasSlowEffect;

        if (hasShapeIndependentEffect && currentState == PaddleState.NORMAL) {
            // Has active effects, check if warning needed
            long catchTimeRemaining = hasCatchEffect ? catchExpiryTime - currentTime : Long.MAX_VALUE;
            long slowTimeRemaining = hasSlowEffect ? slowExpiryTime - currentTime : Long.MAX_VALUE;
            long minTimeRemaining = Math.min(catchTimeRemaining, slowTimeRemaining);

            if (minTimeRemaining <= warningThreshold) {
                // Warning: switch to PULSATE
                setState(PaddleState.PULSATE);
                System.out.println("Paddle: Shape-independent effect expiring soon, switching to PULSATE");
            }
        } else if (!hasShapeIndependentEffect && currentState == PaddleState.PULSATE) {
            // No more shape-independent effects active and currently in PULSATE state
            // Switch back to NORMAL
            setState(PaddleState.NORMAL);
            System.out.println("Paddle: Shape-independent effects expired, switching back to NORMAL");
        }

        // Clean up expired timers
        if (catchExpiryTime > 0 && (catchExpiryTime - currentTime) <= 0) {
            catchExpiryTime = 0;
        }
        if (slowExpiryTime > 0 && (slowExpiryTime - currentTime) <= 0) {
            slowExpiryTime = 0;
        }
    }

    public void moveLeft() {
        setVelocity(new Velocity(-Constants.Paddle.PADDLE_SPEED, 0));
    }

    public void moveRight() {
        setVelocity(new Velocity(Constants.Paddle.PADDLE_SPEED, 0));
    }

    public void stop() {
        setVelocity(new Velocity(0,0));
    }

    // ============================================================
    // Animation System
    // ============================================================

    public void setState(PaddleState newState) {
        if (this.currentState == newState && animationPlaying) {
            return;
        }

        this.currentState = newState;

        // NORMAL state doesn't have animation, just a static sprite
        if (newState == PaddleState.NORMAL) {
            this.currentAnimation = null;
            this.animationPlaying = false;
            return;
        }

        this.currentAnimation = AnimationFactory.createPaddleAnimation(newState);

        if (currentAnimation != null) {
            currentAnimation.play();
            animationPlaying = true;
        }
    }

    public PaddleState getState() {
        return currentState;
    }

    public Animation getAnimation() {
        return currentAnimation;
    }

    public boolean isAnimationPlaying() {
        return animationPlaying && currentAnimation != null && currentAnimation.isPlaying();
    }

    // ============================================================
    // PowerUp Effects
    // ============================================================

    public void enableLaser() {
        // If paddle is currently expanded, shrink it first (immediate width change)
        if (currentState == PaddleState.WIDE || currentState == PaddleState.WIDE_PULSATE) {
            // Calculate center before changing width
            double centerX = getX() + getWidth() / 2.0;
            double normalWidth = Constants.Paddle.PADDLE_WIDTH;

            // Immediately shrink width
            setWidth(normalWidth);
            setX(centerX - normalWidth / 2.0);

            // Cancel expand effect timer
            expandExpiryTime = 0;

            System.out.println("Paddle: Shrunk from WIDE to enable LASER");
        }

        setState(PaddleState.LASER);
        laserShots = Constants.Laser.LASER_SHOTS;
        laserExpiryTime = System.currentTimeMillis() + Constants.PowerUps.LASER_DURATION;
    }

    public void disableLaser() {
        if (currentState == PaddleState.LASER || currentState == PaddleState.LASER_PULSATE) {
            // Play reversed animation back to NORMAL
            playReversedAnimation(PaddleState.LASER);
            laserShots = 0;
            laserExpiryTime = 0;
            System.out.println("Paddle: Laser disabled with reversed animation");
        }
    }

    public List<Laser> shootLaser() {
        List<Laser> lasers = new ArrayList<>();

        if (laserShots <= 0) {
            return lasers;
        }

        long now = System.currentTimeMillis();
        if (now < laserCooldown) {
            return lasers;
        }

        laserShots--;

        laserCooldown = now + Constants.Laser.LASER_COOLDOWN;

        double paddleLeft = getX();
        double paddleRight = getX() + getWidth();
        double paddleTop = getY();

        lasers.add(new Laser(paddleLeft + 10, paddleTop));
        lasers.add(new Laser(paddleRight - 10 - Constants.Laser.LASER_WIDTH, paddleTop));

        // Don't disable laser when shots depleted, only when time expires
        // Paddle will stay in LASER state until laserExpiryTime

        return lasers;
    }

    public void expand() {
        if (getState() == PaddleState.WIDE || getState() == PaddleState.WIDE_PULSATE) {
            expandExpiryTime = System.currentTimeMillis() + Constants.PowerUps.EXPAND_DURATION;
            return; // chỉ gia hạn thời gian, không mở rộng thêm
        }

        // If paddle has laser, cancel laser effect (don't play animation, just clear state)
        if (currentState == PaddleState.LASER || currentState == PaddleState.LASER_PULSATE) {
            laserShots = 0;
            laserExpiryTime = 0;
            System.out.println("Paddle: Laser cancelled by EXPAND powerup");
        }

        setState(PaddleState.WIDE);

        double centerX = getX() + getWidth() / 2.0;
        double newWidth = Constants.Paddle.PADDLE_WIDE_WIDTH;
        setWidth(newWidth);
        setX(centerX - newWidth / 2.0);
        expandExpiryTime = System.currentTimeMillis() + Constants.PowerUps.EXPAND_DURATION;
    }

    public void shrinkToNormal() {
        if (getState() != PaddleState.WIDE && getState() != PaddleState.WIDE_PULSATE) {
            return; // Chỉ thu nhỏ nếu đang ở trạng thái WIDE hoặc WIDE_PULSATE
        }

        // Play reversed animation back to NORMAL
        playReversedAnimation(PaddleState.WIDE);

        double centerX = getX() + getWidth() / 2.0;
        double normalWidth = Constants.Paddle.PADDLE_WIDTH;
        setWidth(normalWidth);

        setX(centerX - getWidth() / 2.0);
        expandExpiryTime = 0;
    }

    public void enableCatch() {
        this.catchMode = true;
        catchExpiryTime = System.currentTimeMillis() + Constants.PowerUps.CATCH_DURATION;
    }

    public void disableCatch() {
        this.catchMode = false;
        catchExpiryTime = 0;
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
        return laserShots > 0 && (currentState == PaddleState.LASER || currentState == PaddleState.LASER_PULSATE);
    }

    public void setLaserEnabled(boolean enabled) {
        if (enabled) {
            enableLaser();
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

    public void setSlowEffectExpiry(long expiryTime) {
        this.slowExpiryTime = expiryTime;
    }

    public void clearSlowEffect() {
        this.slowExpiryTime = 0;
    }

    public void playMaterializeAnimation() {
        setState(PaddleState.MATERIALIZE);
    }

    public void playExplodeAnimation() {
        setState(PaddleState.EXPLODE);
    }

    /**
     * Plays a reversed animation from the specified state back to NORMAL.
     * Used when effects expire (WIDE -> NORMAL, LASER -> NORMAL).
     */
    private void playReversedAnimation(PaddleState fromState) {
        if (fromState == PaddleState.NORMAL) {
            return;
        }

        // Create reversed animation
        this.currentAnimation = AnimationFactory.createPaddleAnimation(fromState);

        if (currentAnimation != null) {
            currentAnimation.playReversed();
            animationPlaying = true;

            // Schedule state change to NORMAL after animation completes
            // This will be handled in update() when animation finishes
            System.out.println("Paddle: Playing reversed animation from " + fromState + " to NORMAL");
        } else {
            // Fallback if animation not available
            setState(PaddleState.NORMAL);
        }
    }
}
