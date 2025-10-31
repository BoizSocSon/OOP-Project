package Objects.GameEntities;

import Utils.AnimationFactory;
import Utils.Constants;
import GeometryPrimitives.Velocity;
import Render.Animation;
import Objects.Core.MovableObject;
import java.util.List;
import java.util.ArrayList;

/**
 * Lớp Paddle đại diện cho thanh đỡ (paddle) của người chơi trong game Arkanoid/Breakout.
 * Nó kế thừa từ MovableObject và quản lý các trạng thái, hiệu ứng (power-ups)
 * như mở rộng, laser, bắt bóng (catch), và làm chậm (slow), cùng với các animation tương ứng.
 */
public class Paddle extends MovableObject{
    // Biến trạng thái cho hiệu ứng Catch
    private boolean catchMode = false;
    // Số lần bắn laser còn lại
    private int laserShots = 0;
    // Thời điểm cooldown laser kết thúc (để kiểm soát tốc độ bắn)
    private long laserCooldown = 0;

    // Trạng thái hiện tại của Paddle (NORMAL, WIDE, LASER, v.v.)
    private PaddleState currentState = PaddleState.NORMAL;
    // Animation hiện tại đang được phát
    private Animation currentAnimation = null;
    // Cờ báo hiệu animation đang được phát
    private boolean animationPlaying = false;

    // Thời điểm các hiệu ứng hết hạn (milliseconds)
    private long expandExpiryTime = 0;
    private long laserExpiryTime = 0;
    private long catchExpiryTime = 0;
    private long slowExpiryTime = 0;


    /**
     * Khởi tạo một Paddle mới.
     *
     * @param x Tọa độ x ban đầu của paddle.
     * @param y Tọa độ y ban đầu của paddle.
     * @param width Chiều rộng của paddle.
     * @param height Chiều cao của paddle.
     */
    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    /**
     * Cập nhật trạng thái của paddle trong mỗi frame game.
     * Xử lý di chuyển, animation, và kiểm tra/hết hạn của các hiệu ứng power-up.
     */
    @Override
    public void update() {
        // 1. Di chuyển paddle
        move();

        // 2. Cập nhật và kiểm tra animation
        if (animationPlaying && currentAnimation != null) {
            currentAnimation.update();

            // Kiểm tra xem animation đã kết thúc chưa
            if (currentAnimation.isFinished() && !currentAnimation.isPlaying()) {
                if (currentState == PaddleState.MATERIALIZE) {
                    // Sau khi animation MATERIALIZE (xuất hiện) kết thúc, chuyển về trạng thái NORMAL
                    System.out.println("Paddle: MATERIALIZE animation finished, switching to NORMAL");
                    setState(PaddleState.NORMAL);
                } else if (currentState == PaddleState.EXPLODE) {
                    // Sau khi animation EXPLODE (nổ) kết thúc
                    System.out.println("Paddle: EXPLODE animation finished");
                    animationPlaying = false;
                    currentAnimation = null;
                } else if (currentState == PaddleState.WIDE || currentState == PaddleState.LASER) {
                    // Animation chuyển trạng thái (WIDE/LASER) kết thúc
                    System.out.println("Paddle: " + currentState + " transition animation finished");
                    animationPlaying = false;
                } else if (currentAnimation.isReversed()) {
                    // Animation đảo ngược (chuyển về NORMAL) kết thúc
                    System.out.println("Paddle: Reversed animation finished, switching to NORMAL");
                    setState(PaddleState.NORMAL); // Gọi setState(NORMAL) để dọn dẹp
                    animationPlaying = false;
                    currentAnimation = null;
                }
            }
        }

        // 3. Kiểm tra thời gian hết hạn của các Power-up
        long currentTime = System.currentTimeMillis();
        long warningThreshold = Constants.PowerUps.WARNING_THRESHOLD;

        // Xử lý hiệu ứng EXPAND (mở rộng)
        if (expandExpiryTime > 0) {
            long timeRemaining = expandExpiryTime - currentTime;

            if (timeRemaining <= 0) {
                // Hết hạn: thu nhỏ về NORMAL
                shrinkToNormal();
                expandExpiryTime = 0;
            } else if (timeRemaining <= warningThreshold && currentState == PaddleState.WIDE) {
                // Gần hết hạn: chuyển sang trạng thái nhấp nháy (PULSATE)
                setState(PaddleState.WIDE_PULSATE);
                System.out.println("Paddle: WIDE effect expiring soon, switching to WIDE_PULSATE");
            }
        }

        // Xử lý hiệu ứng LASER
        if (laserExpiryTime > 0) {
            long timeRemaining = laserExpiryTime - currentTime;

            if (timeRemaining <= 0) {
                // Hết hạn: tắt laser
                disableLaser();
                laserExpiryTime = 0;
            } else if (timeRemaining <= warningThreshold && currentState == PaddleState.LASER) {
                // Gần hết hạn: chuyển sang trạng thái nhấp nháy (PULSATE)
                setState(PaddleState.LASER_PULSATE);
                System.out.println("Paddle: LASER effect expiring soon, switching to LASER_PULSATE");
            }
        }

        // Kiểm tra xem có hiệu ứng độc lập với hình dạng (Catch hoặc Slow) đang hoạt động không
        boolean hasCatchEffect = catchExpiryTime > 0 && (catchExpiryTime - currentTime) > 0;
        boolean hasSlowEffect = slowExpiryTime > 0 && (slowExpiryTime - currentTime) > 0;
        boolean hasShapeIndependentEffect = hasCatchEffect || hasSlowEffect;

        // Xử lý hiệu ứng độc lập với hình dạng (Catch, Slow) trên Paddle NORMAL
        if (hasShapeIndependentEffect && currentState == PaddleState.NORMAL) {
            long catchTimeRemaining = hasCatchEffect ? catchExpiryTime - currentTime : Long.MAX_VALUE;
            long slowTimeRemaining = hasSlowEffect ? slowExpiryTime - currentTime : Long.MAX_VALUE;
            // Lấy thời gian còn lại ngắn nhất
            long minTimeRemaining = Math.min(catchTimeRemaining, slowTimeRemaining);

            if (minTimeRemaining <= warningThreshold) {
                // Gần hết hạn: chuyển sang trạng thái nhấp nháy (PULSATE)
                setState(PaddleState.PULSATE);
                System.out.println("Paddle: Shape-independent effect expiring soon, switching to PULSATE");
            }
        } else if (!hasShapeIndependentEffect && currentState == PaddleState.PULSATE) {
            // Tất cả hiệu ứng độc lập đã hết hạn: chuyển về NORMAL
            setState(PaddleState.NORMAL);
            System.out.println("Paddle: Shape-independent effects expired, switching back to NORMAL");
        }

        // Dọn dẹp thời gian hết hạn cho Catch và Slow (nếu chưa được xử lý ở các phần trên)
        if (catchExpiryTime > 0 && (catchExpiryTime - currentTime) <= 0) {
            catchExpiryTime = 0;
        }
        if (slowExpiryTime > 0 && (slowExpiryTime - currentTime) <= 0) {
            slowExpiryTime = 0;
        }
    }

    /**
     * Đặt vận tốc để paddle di chuyển sang trái.
     */
    public void moveLeft() {
        setVelocity(new Velocity(-Constants.Paddle.PADDLE_SPEED, 0));
    }

    /**
     * Đặt vận tốc để paddle di chuyển sang phải.
     */
    public void moveRight() {
        setVelocity(new Velocity(Constants.Paddle.PADDLE_SPEED, 0));
    }

    /**
     * Dừng di chuyển của paddle (vận tốc bằng 0).
     */
    public void stop() {
        setVelocity(new Velocity(0,0));
    }

    /**
     * Thay đổi trạng thái hiện tại của paddle và bắt đầu animation mới nếu có.
     *
     * @param newState Trạng thái PaddleState mới.
     */
    public void setState(PaddleState newState) {
        // Bỏ qua nếu trạng thái không đổi và animation đang chạy
        if (this.currentState == newState && animationPlaying) {
            return;
        }

        this.currentState = newState;
        if (newState == PaddleState.NORMAL) {
            // Trạng thái NORMAL không có animation
            this.currentAnimation = null;
            this.animationPlaying = false;
            return;
        }

        // Tạo animation mới cho trạng thái mới
        this.currentAnimation = AnimationFactory.createPaddleAnimation(newState);

        if (currentAnimation != null) {
            // Bắt đầu phát animation
            currentAnimation.play();
            animationPlaying = true;
        }
    }

    /**
     * Lấy trạng thái hiện tại của paddle.
     *
     * @return Trạng thái PaddleState hiện tại.
     */
    public PaddleState getState() {
        return currentState;
    }

    /**
     * Lấy animation hiện tại.
     *
     * @return Đối tượng Animation hiện tại.
     */
    public Animation getAnimation() {
        return currentAnimation;
    }

    /**
     * Kiểm tra xem animation có đang được phát hay không.
     *
     * @return true nếu animation đang chạy và chưa kết thúc, ngược lại là false.
     */
    public boolean isAnimationPlaying() {
        return animationPlaying && currentAnimation != null && currentAnimation.isPlaying();
    }

    /**
     * Kích hoạt hiệu ứng laser.
     * Nếu paddle đang ở trạng thái WIDE, nó sẽ thu nhỏ về kích thước chuẩn trước khi bật laser.
     */
    public void enableLaser() {
        // Nếu đang ở trạng thái WIDE, phải thu nhỏ về kích thước chuẩn trước khi bật LASER
        if (currentState == PaddleState.WIDE || currentState == PaddleState.WIDE_PULSATE) {
            double centerX = getX() + getWidth() / 2.0;
            double normalWidth = Constants.Paddle.PADDLE_WIDTH;

            setWidth(normalWidth);
            setX(centerX - normalWidth / 2.0);

            expandExpiryTime = 0; // Hủy bỏ hiệu ứng EXPAND

            System.out.println("Paddle: Shrunk from WIDE to enable LASER");
        }

        setState(PaddleState.LASER);
        laserShots = Constants.Laser.LASER_SHOTS;
        // Thiết lập thời gian hết hạn
        laserExpiryTime = System.currentTimeMillis() + Constants.PowerUps.LASER_DURATION;
    }

    /**
     * Vô hiệu hóa hiệu ứng laser.
     * Chơi animation đảo ngược để chuyển từ trạng thái LASER về NORMAL.
     */
    public void disableLaser() {
        if (currentState == PaddleState.LASER || currentState == PaddleState.LASER_PULSATE) {
            // Chơi animation đảo ngược
            playReversedAnimation(PaddleState.LASER);
            laserShots = 0;
            laserExpiryTime = 0;
            System.out.println("Paddle: Laser disabled with reversed animation");
        }
    }

    /**
     * Bắn laser.
     * Kiểm tra số lần bắn và cooldown.
     *
     * @return Một danh sách chứa 0 hoặc 2 đối tượng Laser mới.
     */
    public List<Laser> shootLaser() {
        List<Laser> lasers = new ArrayList<>();

        // Không thể bắn nếu hết đạn
        if (laserShots <= 0) {
            return lasers;
        }

        long now = System.currentTimeMillis();
        // Không thể bắn nếu đang trong thời gian cooldown
        if (now < laserCooldown) {
            return lasers;
        }

        laserShots--; // Giảm số lần bắn còn lại

        // Thiết lập thời gian cooldown tiếp theo
        laserCooldown = now + Constants.Laser.LASER_COOLDOWN;

        double paddleLeft = getX();
        double paddleRight = getX() + getWidth();
        double paddleTop = getY();

        // Tạo 2 viên laser ở hai bên mép paddle
        lasers.add(new Laser(paddleLeft + 10, paddleTop));
        lasers.add(new Laser(paddleRight - 10 - Constants.Laser.LASER_WIDTH, paddleTop));

        return lasers;
    }

    /**
     * Kích hoạt hiệu ứng mở rộng (EXPAND).
     * Nếu đã WIDE, chỉ gia hạn thời gian. Nếu đang LASER, hủy LASER.
     */
    public void expand() {
        // Nếu đã ở trạng thái WIDE, chỉ gia hạn thời gian
        if (getState() == PaddleState.WIDE || getState() == PaddleState.WIDE_PULSATE) {
            expandExpiryTime = System.currentTimeMillis() + Constants.PowerUps.EXPAND_DURATION;
            return;
        }
        // Nếu đang ở trạng thái LASER, hủy LASER
        if (currentState == PaddleState.LASER || currentState == PaddleState.LASER_PULSATE) {
            laserShots = 0;
            laserExpiryTime = 0;
            System.out.println("Paddle: Laser cancelled by EXPAND powerup");
        }

        setState(PaddleState.WIDE);

        // Tính toán lại vị trí để giữ paddle ở giữa
        double centerX = getX() + getWidth() / 2.0;
        double newWidth = Constants.Paddle.PADDLE_WIDE_WIDTH;
        setWidth(newWidth);
        setX(centerX - newWidth / 2.0);
        // Thiết lập thời gian hết hạn
        expandExpiryTime = System.currentTimeMillis() + Constants.PowerUps.EXPAND_DURATION;
    }

    /**
     * Thu nhỏ paddle về kích thước NORMAL.
     * Chơi animation đảo ngược từ WIDE về NORMAL.
     */
    public void shrinkToNormal() {
        if (getState() != PaddleState.WIDE && getState() != PaddleState.WIDE_PULSATE) {
            return;
        }

        // Chơi animation đảo ngược
        playReversedAnimation(PaddleState.WIDE);

        // Đặt lại kích thước và vị trí để giữ paddle ở giữa
        double centerX = getX() + getWidth() / 2.0;
        double normalWidth = Constants.Paddle.PADDLE_WIDTH;
        setWidth(normalWidth);

        setX(centerX - getWidth() / 2.0);
        expandExpiryTime = 0;
    }

    /**
     * Kích hoạt hiệu ứng bắt bóng (CATCH).
     */
    public void enableCatch() {
        this.catchMode = true;
        catchExpiryTime = System.currentTimeMillis() + Constants.PowerUps.CATCH_DURATION;
    }

    /**
     * Vô hiệu hóa hiệu ứng bắt bóng (CATCH).
     */
    public void disableCatch() {
        this.catchMode = false;
        catchExpiryTime = 0;
    }

    /**
     * Kiểm tra xem hiệu ứng bắt bóng (CATCH) có đang được kích hoạt hay không.
     *
     * @return true nếu CATCH mode đang bật, ngược lại là false.
     */
    public boolean isCatchModeEnabled() {
        return catchMode;
    }

    /**
     * Đặt trạng thái bắt bóng (CATCH).
     *
     * @param enabled true để bật, false để tắt.
     */
    public void setCatchModeEnabled(boolean enabled) {
        if (enabled) {
            enableCatch();
        } else {
            disableCatch();
        }
    }

    /**
     * Kiểm tra xem hiệu ứng laser có đang hoạt động và còn đạn không.
     *
     * @return true nếu laser đang hoạt động, ngược lại là false.
     */
    public boolean isLaserEnabled() {
        return laserShots > 0 && (currentState == PaddleState.LASER || currentState == PaddleState.LASER_PULSATE);
    }

    /**
     * Đặt trạng thái laser.
     * LƯU Ý: Đây là một hàm thiết lập đơn giản. Hàm `enableLaser()` phức tạp hơn nên được ưu tiên dùng.
     *
     * @param enabled true để bật, false để tắt.
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
     * Lấy số lần bắn laser còn lại.
     *
     * @return Số lần bắn laser.
     */
    public int getLaserShots() {
        return laserShots;
    }

    /**
     * Đặt thời gian hết hạn cho hiệu ứng làm chậm (SLOW).
     *
     * @param expiryTime Thời điểm hết hạn (milliseconds).
     */
    public void setSlowEffectExpiry(long expiryTime) {
        this.slowExpiryTime = expiryTime;
    }

    /**
     * Xóa hiệu ứng làm chậm (SLOW).
     */
    public void clearSlowEffect() {
        this.slowExpiryTime = 0;
    }

    /**
     * Bắt đầu animation xuất hiện (MATERIALIZE).
     */
    public void playMaterializeAnimation() {
        setState(PaddleState.MATERIALIZE);
    }

    /**
     * Bắt đầu animation nổ (EXPLODE).
     */
    public void playExplodeAnimation() {
        setState(PaddleState.EXPLODE);
    }

    /**
     * Chơi animation đảo ngược để chuyển từ một trạng thái hiệu ứng về trạng thái NORMAL.
     *
     * @param fromState Trạng thái ban đầu trước khi chuyển về NORMAL.
     */
    private void playReversedAnimation(PaddleState fromState) {
        if (fromState == PaddleState.NORMAL) {
            return;
        }

        // Tạo animation cho trạng thái ban đầu
        this.currentAnimation = AnimationFactory.createPaddleAnimation(fromState);

        if (currentAnimation != null) {
            // Chơi animation đảo ngược
            currentAnimation.playReversed();
            animationPlaying = true;

            System.out.println("Paddle: Playing reversed animation from " + fromState + " to NORMAL");
        } else {
            // Nếu không có animation, chuyển trực tiếp về NORMAL
            setState(PaddleState.NORMAL);
        }
    }
}