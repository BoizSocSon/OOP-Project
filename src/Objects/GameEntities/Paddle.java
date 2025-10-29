package Objects.GameEntities;

import Utils.AnimationFactory;
import Utils.Constants;
import GeometryPrimitives.Velocity;
import Render.Animation;
import Objects.Core.MovableObject;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>Lớp đại diện cho **Thanh đỡ (Paddle)** của người chơi.
 * Thanh đỡ là một {@link MovableObject} có thể di chuyển ngang và quản lý các
 * hiệu ứng nâng cấp (PowerUp) như mở rộng, bắt bóng, và bắn laser,
 * cùng với các trạng thái hoạt ảnh (animation state) tương ứng.</p>
 */
public class Paddle extends MovableObject{

    /** Chế độ bắt bóng (catch mode) đang được kích hoạt. */
    private boolean catchMode = false;

    /** Số lần bắn laser còn lại. */
    private int laserShots = 0;

    /** Thời điểm tia laser tiếp theo có thể được bắn (dùng để quản lý cooldown). */
    private long laserCooldown = 0;

    /** Trạng thái hiện tại của thanh đỡ (ví dụ: NORMAL, WIDE, LASER). */
    private PaddleState currentState = PaddleState.NORMAL;

    /** Hoạt ảnh (animation) đang được chơi. */
    private Animation currentAnimation = null;

    /** Cờ báo hiệu hoạt ảnh có đang được chơi hay không. */
    private boolean animationPlaying = false;

    // Timers for effects
    /** Thời điểm hiệu ứng mở rộng (WIDE) hết hạn. */
    private long expandExpiryTime = 0; // When wide paddle should shrink

    /** Thời điểm hiệu ứng laser hết hạn. */
    private long laserExpiryTime = 0; // When laser effect should expire

    /** Thời điểm hiệu ứng bắt bóng (CATCH) hết hạn. */
    private long catchExpiryTime = 0; // When catch effect should expire

    /** Thời điểm hiệu ứng làm chậm (SLOW) hết hạn. */
    private long slowExpiryTime = 0; // When slow effect should expire


    /**
     * <p>Constructor khởi tạo thanh đỡ.</p>
     *
     * @param x Tọa độ x ban đầu.
     * @param y Tọa độ y ban đầu.
     * @param width Chiều rộng ban đầu.
     * @param height Chiều cao ban đầu.
     */
    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    /**
     * <p>Cập nhật trạng thái của thanh đỡ trong mỗi vòng lặp game.
     * Xử lý di chuyển, cập nhật hoạt ảnh và kiểm tra thời gian hết hạn của các hiệu ứng PowerUp.</p>
     */
    @Override
    public void update() {
        // Di chuyển thanh đỡ theo vận tốc đã thiết lập
        move();

        // Update animation if playing
        // Cập nhật hoạt ảnh nếu đang chơi
        if (animationPlaying && currentAnimation != null) {
            currentAnimation.update();

            // Check if one-shot animations finished
            // Kiểm tra xem hoạt ảnh một lần chơi (one-shot) đã hoàn tất chưa
            if (currentAnimation.isFinished() && !currentAnimation.isPlaying()) {
                if (currentState == PaddleState.MATERIALIZE) {
                    // After materialize, go to NORMAL
                    // Sau khi hoạt ảnh xuất hiện kết thúc, chuyển sang trạng thái NORMAL
                    System.out.println("Paddle: MATERIALIZE animation finished, switching to NORMAL");
                    setState(PaddleState.NORMAL);
                } else if (currentState == PaddleState.EXPLODE) {
                    // After explode, stop animation but keep state
                    // Sau khi hoạt ảnh nổ kết thúc, dừng chơi animation
                    // The paddle will be reset or respawned by GameManager
                    System.out.println("Paddle: EXPLODE animation finished");
                    animationPlaying = false;
                    currentAnimation = null;
                } else if (currentState == PaddleState.WIDE || currentState == PaddleState.LASER) {
                    // After transition animation, stop playing but keep state
                    // Sau hoạt ảnh chuyển đổi (vd: mở rộng), dừng chơi animation nhưng giữ trạng thái
                    // Paddle will show static sprite for this state
                    System.out.println("Paddle: " + currentState + " transition animation finished");
                    animationPlaying = false;
                } else if (currentAnimation.isReversed()) {
                    // Reversed animation finished, switch to NORMAL
                    // Hoạt ảnh đảo ngược kết thúc, chuyển sang NORMAL
                    System.out.println("Paddle: Reversed animation finished, switching to NORMAL");
                    setState(PaddleState.NORMAL); // SetState sẽ tự tắt animationPlaying
                    animationPlaying = false;
                    currentAnimation = null;
                }
            }
        }

        long currentTime = System.currentTimeMillis();
        long warningThreshold = Constants.PowerUps.WARNING_THRESHOLD;

        // Check for WIDE pulsate warning
        // Kiểm tra cảnh báo (WIDE pulsate) cho hiệu ứng mở rộng
        if (expandExpiryTime > 0) {
            long timeRemaining = expandExpiryTime - currentTime;

            if (timeRemaining <= 0) {
                // Effect expired, shrink back to normal
                // Hiệu ứng hết hạn, thu nhỏ lại
                shrinkToNormal();
                expandExpiryTime = 0;
            } else if (timeRemaining <= warningThreshold && currentState == PaddleState.WIDE) {
                // Warning: switch to WIDE_PULSATE
                // Cảnh báo: chuyển sang trạng thái nhấp nháy WIDE_PULSATE
                setState(PaddleState.WIDE_PULSATE);
                System.out.println("Paddle: WIDE effect expiring soon, switching to WIDE_PULSATE");
            }
        }

        // Check for LASER pulsate warning
        // Kiểm tra cảnh báo (LASER pulsate) cho hiệu ứng laser
        if (laserExpiryTime > 0) {
            long timeRemaining = laserExpiryTime - currentTime;

            if (timeRemaining <= 0) {
                // Effect expired, disable laser
                // Hiệu ứng hết hạn, vô hiệu hóa laser
                disableLaser();
                laserExpiryTime = 0;
            } else if (timeRemaining <= warningThreshold && currentState == PaddleState.LASER) {
                // Warning: switch to LASER_PULSATE
                // Cảnh báo: chuyển sang trạng thái nhấp nháy LASER_PULSATE
                setState(PaddleState.LASER_PULSATE);
                System.out.println("Paddle: LASER effect expiring soon, switching to LASER_PULSATE");
            }
        }

        // Check for CATCH/SLOW pulsate warning (shape-independent effects)
        // Kiểm tra cảnh báo nhấp nháy cho các hiệu ứng không liên quan đến hình dạng (CATCH/SLOW)
        boolean hasCatchEffect = catchExpiryTime > 0 && (catchExpiryTime - currentTime) > 0;
        boolean hasSlowEffect = slowExpiryTime > 0 && (slowExpiryTime - currentTime) > 0;
        boolean hasShapeIndependentEffect = hasCatchEffect || hasSlowEffect;

        if (hasShapeIndependentEffect && currentState == PaddleState.NORMAL) {
            // Has active effects, check if warning needed
            // Có hiệu ứng đang hoạt động, kiểm tra xem có cần cảnh báo không
            long catchTimeRemaining = hasCatchEffect ? catchExpiryTime - currentTime : Long.MAX_VALUE;
            long slowTimeRemaining = hasSlowEffect ? slowExpiryTime - currentTime : Long.MAX_VALUE;
            long minTimeRemaining = Math.min(catchTimeRemaining, slowTimeRemaining);

            if (minTimeRemaining <= warningThreshold) {
                // Warning: switch to PULSATE
                // Cảnh báo: chuyển sang trạng thái nhấp nháy PULSATE
                setState(PaddleState.PULSATE);
                System.out.println("Paddle: Shape-independent effect expiring soon, switching to PULSATE");
            }
        } else if (!hasShapeIndependentEffect && currentState == PaddleState.PULSATE) {
            // No more shape-independent effects active and currently in PULSATE state
            // Không còn hiệu ứng độc lập nào đang hoạt động và đang ở trạng thái PULSATE
            // Switch back to NORMAL
            // Chuyển về NORMAL
            setState(PaddleState.NORMAL);
            System.out.println("Paddle: Shape-independent effects expired, switching back to NORMAL");
        }

        // Clean up expired timers
        // Xóa các bộ đếm giờ đã hết hạn
        if (catchExpiryTime > 0 && (catchExpiryTime - currentTime) <= 0) {
            catchExpiryTime = 0;
            // Cần vô hiệu hóa catchMode ở đây nếu không được thực hiện ở nơi khác
            this.catchMode = false;
        }
        if (slowExpiryTime > 0 && (slowExpiryTime - currentTime) <= 0) {
            slowExpiryTime = 0;
        }
    }

    /**
     * <p>Thiết lập vận tốc di chuyển sang trái.</p>
     */
    public void moveLeft() {
        setVelocity(new Velocity(-Constants.Paddle.PADDLE_SPEED, 0));
    }

    /**
     * <p>Thiết lập vận tốc di chuyển sang phải.</p>
     */
    public void moveRight() {
        setVelocity(new Velocity(Constants.Paddle.PADDLE_SPEED, 0));
    }

    /**
     * <p>Dừng di chuyển (thiết lập vận tốc bằng 0).</p>
     */
    public void stop() {
        setVelocity(new Velocity(0,0));
    }

    // ============================================================
    // Animation System (Hệ thống Hoạt ảnh)
    // ============================================================

    /**
     * <p>Chuyển đổi trạng thái của thanh đỡ và bắt đầu hoạt ảnh tương ứng.</p>
     *
     * @param newState Trạng thái mới (ví dụ: WIDE, LASER).
     */
    public void setState(PaddleState newState) {
        if (this.currentState == newState && animationPlaying) {
            return;
        }

        this.currentState = newState;

        // NORMAL state doesn't have animation, just a static sprite
        // Trạng thái NORMAL không có hoạt ảnh, chỉ là một sprite tĩnh
        if (newState == PaddleState.NORMAL) {
            this.currentAnimation = null;
            this.animationPlaying = false;
            return;
        }

        // Tạo hoạt ảnh mới cho trạng thái mới
        this.currentAnimation = AnimationFactory.createPaddleAnimation(newState);

        if (currentAnimation != null) {
            currentAnimation.play();
            animationPlaying = true;
        }
    }

    /**
     * <p>Trả về trạng thái hiện tại của thanh đỡ.</p>
     *
     * @return {@link PaddleState} hiện tại.
     */
    public PaddleState getState() {
        return currentState;
    }

    /**
     * <p>Trả về đối tượng hoạt ảnh hiện tại.</p>
     *
     * @return Đối tượng {@link Animation}.
     */
    public Animation getAnimation() {
        return currentAnimation;
    }

    /**
     * <p>Kiểm tra xem hoạt ảnh có đang được chơi (và chưa kết thúc) hay không.</p>
     *
     * @return {@code true} nếu hoạt ảnh đang hoạt động.
     */
    public boolean isAnimationPlaying() {
        return animationPlaying && currentAnimation != null && currentAnimation.isPlaying();
    }

    // ============================================================
    // PowerUp Effects (Các Hiệu ứng PowerUp)
    // ============================================================

    /**
     * <p>Kích hoạt hiệu ứng Laser trên thanh đỡ.</p>
     * <p>Nếu đang ở chế độ WIDE, sẽ thu nhỏ về kích thước thường trước khi kích hoạt Laser.</p>
     */
    public void enableLaser() {
        // If paddle is currently expanded, shrink it first (immediate width change)
        // Nếu đang ở trạng thái mở rộng, thu nhỏ ngay lập tức
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
        // Đặt số lượng bắn và thời gian hết hạn
        laserShots = Constants.Laser.LASER_SHOTS;
        laserExpiryTime = System.currentTimeMillis() + Constants.PowerUps.LASER_DURATION;
    }

    /**
     * <p>Vô hiệu hóa hiệu ứng Laser.</p>
     * <p>Chơi hoạt ảnh đảo ngược từ LASER về NORMAL.</p>
     */
    public void disableLaser() {
        if (currentState == PaddleState.LASER || currentState == PaddleState.LASER_PULSATE) {
            // Play reversed animation back to NORMAL
            playReversedAnimation(PaddleState.LASER);
            // Xóa hết số lần bắn và thời gian hết hạn
            laserShots = 0;
            laserExpiryTime = 0;
            System.out.println("Paddle: Laser disabled with reversed animation");
        }
    }

    /**
     * <p>Bắn hai tia laser từ hai bên của thanh đỡ.</p>
     * <p>Việc bắn bị giới hạn bởi số lần bắn còn lại và thời gian cooldown.</p>
     *
     * @return Danh sách các đối tượng {@link Laser} đã được tạo ra.
     */
    public List<Laser> shootLaser() {
        List<Laser> lasers = new ArrayList<>();

        // Kiểm tra số lần bắn còn lại
        if (laserShots <= 0) {
            return lasers;
        }

        // Kiểm tra thời gian cooldown
        long now = System.currentTimeMillis();
        if (now < laserCooldown) {
            return lasers;
        }

        laserShots--; // Giảm số lần bắn

        // Thiết lập cooldown cho lần bắn tiếp theo
        laserCooldown = now + Constants.Laser.LASER_COOLDOWN;

        double paddleLeft = getX();
        double paddleRight = getX() + getWidth();
        double paddleTop = getY();

        // Tạo tia laser bên trái (cách lề 10 pixel)
        lasers.add(new Laser(paddleLeft + 10, paddleTop));
        // Tạo tia laser bên phải (cách lề 10 pixel)
        lasers.add(new Laser(paddleRight - 10 - Constants.Laser.LASER_WIDTH, paddleTop));

        // Don't disable laser when shots depleted, only when time expires
        // Không vô hiệu hóa laser khi hết đạn, chỉ khi thời gian hết hạn (laserExpiryTime)
        // Paddle will stay in LASER state until laserExpiryTime

        return lasers;
    }

    /**
     * <p>Kích hoạt hiệu ứng mở rộng (WIDE).</p>
     * <p>Nếu đang ở trạng thái LASER, hiệu ứng LASER sẽ bị hủy bỏ.</p>
     */
    public void expand() {
        // Nếu đã ở trạng thái WIDE, chỉ gia hạn thời gian
        if (getState() == PaddleState.WIDE || getState() == PaddleState.WIDE_PULSATE) {
            expandExpiryTime = System.currentTimeMillis() + Constants.PowerUps.EXPAND_DURATION;
            return; // chỉ gia hạn thời gian, không mở rộng thêm
        }

        // If paddle has laser, cancel laser effect (don't play animation, just clear state)
        // Nếu đang ở trạng thái LASER, hủy hiệu ứng laser
        if (currentState == PaddleState.LASER || currentState == PaddleState.LASER_PULSATE) {
            laserShots = 0;
            laserExpiryTime = 0;
            System.out.println("Paddle: Laser cancelled by EXPAND powerup");
        }

        setState(PaddleState.WIDE);

        // Thay đổi kích thước và điều chỉnh vị trí để giữ nguyên tâm
        double centerX = getX() + getWidth() / 2.0;
        double newWidth = Constants.Paddle.PADDLE_WIDE_WIDTH;
        setWidth(newWidth);
        setX(centerX - newWidth / 2.0);
        expandExpiryTime = System.currentTimeMillis() + Constants.PowerUps.EXPAND_DURATION;
    }

    /**
     * <p>Thu nhỏ thanh đỡ về kích thước bình thường (NORMAL).</p>
     * <p>Chơi hoạt ảnh đảo ngược từ WIDE về NORMAL.</p>
     */
    public void shrinkToNormal() {
        if (getState() != PaddleState.WIDE && getState() != PaddleState.WIDE_PULSATE) {
            return; // Chỉ thu nhỏ nếu đang ở trạng thái WIDE hoặc WIDE_PULSATE
        }

        // Play reversed animation back to NORMAL
        playReversedAnimation(PaddleState.WIDE);

        // Thay đổi kích thước và điều chỉnh vị trí để giữ nguyên tâm
        double centerX = getX() + getWidth() / 2.0;
        double normalWidth = Constants.Paddle.PADDLE_WIDTH;
        setWidth(normalWidth);

        setX(centerX - getWidth() / 2.0);
        expandExpiryTime = 0;
    }

    /**
     * <p>Kích hoạt chế độ bắt bóng (Catch Mode).</p>
     */
    public void enableCatch() {
        this.catchMode = true;
        catchExpiryTime = System.currentTimeMillis() + Constants.PowerUps.CATCH_DURATION;
    }

    /**
     * <p>Vô hiệu hóa chế độ bắt bóng (Catch Mode).</p>
     */
    public void disableCatch() {
        this.catchMode = false;
        catchExpiryTime = 0;
    }

    // ============================================================
    // PowerUp State Getters/Setters (Truy xuất/Thiết lập trạng thái PowerUp)
    // ============================================================

    /**
     * <p>Kiểm tra xem chế độ bắt bóng có đang được kích hoạt hay không.</p>
     *
     * @return {@code true} nếu chế độ bắt bóng đang hoạt động.
     */
    public boolean isCatchModeEnabled() {
        return catchMode;
    }

    /**
     * <p>Thiết lập trạng thái kích hoạt của chế độ bắt bóng.</p>
     *
     * @param enabled {@code true} để kích hoạt, {@code false} để vô hiệu hóa.
     */
    public void setCatchModeEnabled(boolean enabled) {
        this.catchMode = enabled;
        if (enabled) {
            enableCatch();
        } else {
            disableCatch();
        }
    }

    /**
     * <p>Kiểm tra xem hiệu ứng Laser có đang được kích hoạt hay không.</p>
     * <p>Hiệu ứng chỉ được coi là kích hoạt nếu còn đạn và ở trạng thái LASER/LASER_PULSATE.</p>
     *
     * @return {@code true} nếu Laser đang hoạt động.
     */
    public boolean isLaserEnabled() {
        return laserShots > 0 && (currentState == PaddleState.LASER || currentState == PaddleState.LASER_PULSATE);
    }

    /**
     * <p>Thiết lập trạng thái kích hoạt của Laser.</p>
     *
     * @param enabled {@code true} để kích hoạt, {@code false} để vô hiệu hóa (chuyển về NORMAL nếu cần).
     */
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

    /**
     * <p>Trả về số lần bắn laser còn lại.</p>
     *
     * @return Số lần bắn laser.
     */
    public int getLaserShots() {
        return laserShots;
    }

    /**
     * <p>Thiết lập thời gian hết hạn cho hiệu ứng làm chậm (Slow).</p>
     *
     * @param expiryTime Thời điểm hết hạn (milliseconds).
     */
    public void setSlowEffectExpiry(long expiryTime) {
        this.slowExpiryTime = expiryTime;
    }

    /**
     * <p>Xóa hiệu ứng làm chậm.</p>
     */
    public void clearSlowEffect() {
        this.slowExpiryTime = 0;
    }

    /**
     * <p>Bắt đầu hoạt ảnh xuất hiện (Materialize) của thanh đỡ.</p>
     */
    public void playMaterializeAnimation() {
        setState(PaddleState.MATERIALIZE);
    }

    /**
     * <p>Bắt đầu hoạt ảnh nổ (Explode) của thanh đỡ.</p>
     */
    public void playExplodeAnimation() {
        setState(PaddleState.EXPLODE);
    }

    /**
     * <p>Chơi một hoạt ảnh đảo ngược từ trạng thái PowerUp về trạng thái NORMAL.</p>
     * <p>Được sử dụng khi các hiệu ứng (WIDE, LASER) hết hạn.</p>
     *
     * @param fromState Trạng thái PowerUp hiện tại (ví dụ: WIDE, LASER).
     */
    private void playReversedAnimation(PaddleState fromState) {
        if (fromState == PaddleState.NORMAL) {
            return;
        }

        // Create reversed animation
        // Tạo hoạt ảnh tương ứng từ trạng thái PowerUp
        this.currentAnimation = AnimationFactory.createPaddleAnimation(fromState);

        if (currentAnimation != null) {
            currentAnimation.playReversed();
            animationPlaying = true;

            // Schedule state change to NORMAL after animation completes
            // Lên lịch chuyển trạng thái sang NORMAL sau khi hoạt ảnh kết thúc
            // This will be handled in update() when animation finishes
            System.out.println("Paddle: Playing reversed animation from " + fromState + " to NORMAL");
        } else {
            // Fallback if animation not available
            // Trường hợp dự phòng nếu không có hoạt ảnh
            setState(PaddleState.NORMAL);
        }
    }
}