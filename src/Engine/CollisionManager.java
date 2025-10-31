package Engine;

import Objects.Bricks.BrickType;
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
 * Lớp quản lý va chạm (CollisionManager) chịu trách nhiệm phát hiện và xử lý
 * các tương tác va chạm giữa các thực thể game, bao gồm bóng, thanh đỡ, gạch,
 * viền tường và tia laser.
 */
public class CollisionManager {
    private int playAreaWidth; // Chiều rộng khu vực chơi.
    private int playAreaHeight; // Chiều cao khu vực chơi.
    // Góc phản xạ tối đa của bóng khi chạm thanh đỡ, lấy từ hằng số.
    private static final double MAX_BOUNCE_ANGLE = Constants.Paddle.PADDLE_MAX_ANGLE;

    /**
     * Khởi tạo CollisionManager.
     *
     * @param width Chiều rộng khu vực chơi.
     * @param height Chiều cao khu vực chơi.
     */
    public CollisionManager(int width, int height) {
        this.playAreaWidth = width;
        this.playAreaHeight = height;
    }

    /**
     * Kiểm tra và xử lý va chạm của bóng với các biên giới hạn cố định (tường trên, trái, phải).
     *
     * @param ball Đối tượng bóng.
     * @param leftBorder Tọa độ X của biên trái.
     * @param rightBorder Tọa độ X của biên phải.
     * @param topBorder Tọa độ Y của biên trên.
     */
    public void checkBallWallCollisions(Ball ball, double leftBorder, double rightBorder, double topBorder) {
        boolean collided = false;

        // Kiểm tra va chạm biên trái
        if (ball.getX() <= leftBorder) {
            ball.setX(leftBorder); // Đặt lại vị trí bóng sát biên.
            // Đảo hướng vận tốc theo trục X (đảm bảo dx luôn dương).
            ball.setVelocity(new Velocity(
                    Math.abs(ball.getVelocity().getDx()),
                    ball.getVelocity().getDy()
            ));
            collided = true;
        }

        // Kiểm tra va chạm biên phải
        if (ball.getX() + ball.getWidth() >= rightBorder) {
            ball.setX(rightBorder - ball.getWidth()); // Đặt lại vị trí bóng sát biên.
            // Đảo hướng vận tốc theo trục X (đảm bảo dx luôn âm).
            ball.setVelocity(new Velocity(
                    -Math.abs(ball.getVelocity().getDx()),
                    ball.getVelocity().getDy()
            ));
            collided = true;
        }

        // Kiểm tra va chạm biên trên
        if (ball.getY() <= topBorder) {
            ball.setY(topBorder); // Đặt lại vị trí bóng sát biên.
            // Đảo hướng vận tốc theo trục Y (đảm bảo dy luôn dương).
            ball.setVelocity(new Velocity(
                    ball.getVelocity().getDx(),
                    Math.abs(ball.getVelocity().getDy())
            ));
            collided = true;
        }

        // Logic xử lý SFX va chạm tường sẽ được thêm vào đây
        if (collided) {

        }
    }

    /**
     * Kiểm tra va chạm của bóng với thanh đỡ (Paddle).
     *
     * @param ball Đối tượng bóng.
     * @param paddle Đối tượng thanh đỡ.
     * @return {@code true} nếu va chạm xảy ra, ngược lại là {@code false}.
     */
    public boolean checkBallPaddleCollision(Ball ball, Paddle paddle) {
        // Kiểm tra va chạm AABB đơn giản trước.
        if (!ball.getBounds().intersects(paddle.getBounds())) {
            return false;
        }

        // Tính toán vị trí va chạm trên thanh đỡ.
        double ballCenterX = ball.getCenter().getX();
        double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
        double paddleHalfWidth = paddle.getWidth() / 2.0;

        // hitPosition: tỉ lệ từ -1.0 (cực trái) đến 1.0 (cực phải)
        double hitPosition = (ballCenterX - paddleCenterX) / paddleHalfWidth;
        // Giới hạn giá trị trong khoảng [-1, 1].
        hitPosition = Math.max(-1.0, Math.min(1.0, hitPosition));


        // Nếu chế độ bắt bóng (Catch Mode) đang bật.
        if (paddle.isCatchModeEnabled()) {
            return true;
        }

        // Tính toán hướng bay mới của bóng dựa trên vị trí va chạm.
        calculateBallAngleFromPaddle(ball, paddle, hitPosition);
        // Đặt lại vị trí Y của bóng để đảm bảo nó bật lên trên thanh đỡ.
        ball.setY(paddle.getY() - ball.getHeight() - 1);
        return true;
    }

    /**
     * Tính toán góc phản xạ mới của bóng dựa trên vị trí va chạm trên thanh đỡ.
     *
     * @param ball Đối tượng bóng.
     * @param paddle Đối tượng thanh đỡ.
     * @param hitPosition Vị trí va chạm chuẩn hóa [-1, 1].
     */
    private void calculateBallAngleFromPaddle(Ball ball, Paddle paddle, double hitPosition) {
        // Lấy tốc độ hiện tại của bóng (độ lớn vector vận tốc).
        double speed = Math.hypot(ball.getVelocity().getDx(), ball.getVelocity().getDy());

        // Tính toán góc phản xạ (độ) từ -MAX_BOUNCE_ANGLE đến +MAX_BOUNCE_ANGLE.
        double angle = hitPosition * MAX_BOUNCE_ANGLE;

        // Chuyển đổi góc sang radian.
        double angleRad = Math.toRadians(angle);

        // Tính toán thành phần vận tốc mới (dx, dy).
        // Góc 0 độ: bắn thẳng lên (dy = -speed, dx = 0).
        // Góc dương: hướng sang phải, Góc âm: hướng sang trái.
        double dx = speed * Math.sin(angleRad);
        // dy âm vì hướng lên là hướng âm trong hệ tọa độ JavaFX.
        double dy = -speed * Math.cos(angleRad);

        // Đặt vận tốc mới cho bóng.
        ball.setVelocity(new Velocity(dx, dy));
    }

    /**
     * Kiểm tra va chạm của bóng với tất cả gạch trong màn chơi.
     *
     * @param ball Đối tượng bóng.
     * @param bricks Danh sách các gạch cần kiểm tra.
     * @return Danh sách các gạch đã bị phá hủy trong lần va chạm này (dùng cho tính điểm).
     */
    public List<Brick> checkBallBrickCollisions(Ball ball, List<Brick> bricks) {
        List<Brick> destroyedBricks = new ArrayList<>();

        for (Brick brick : bricks) {
            // Chỉ kiểm tra va chạm với gạch còn sống.
            if (!brick.isAlive()) {
                continue;
            }

            // Bỏ qua va chạm cho gạch vàng (không thể phá hủy).
            if (brick.getBrickType() == BrickType.GOLD){
                ignoreGoldBricksCollision(brick, ball);
                continue;
            }


            // Kiểm tra va chạm bằng phương pháp "swept collision" tích hợp của Ball.
            if (ball.checkCollisionWithRect(brick.getBounds())) {
                // Gạch nhận sát thương.
                brick.takeHit();

                // Logic phát SFX chạm gạch

                // Nếu gạch bị phá hủy, thêm vào danh sách.
                if (brick.isDestroyed()) {
                    destroyedBricks.add(brick);
                }
            }
        }

        return destroyedBricks;
    }

    /**
     * Xử lý va chạm giữa bóng và gạch vàng (Gold Bricks),
     * gạch vàng không bị phá hủy và va chạm như tường.
     *
     * @param brick Gạch vàng.
     * @param ball Đối tượng bóng.
     */
    private void ignoreGoldBricksCollision(Brick brick, Ball ball) {
        // Gạch vàng là không thể phá hủy - va chạm tương tự như tường.
        if (brick.getBrickType() == BrickType.GOLD) {
            // Kiểm tra va chạm AABB.
            if (ball.getBounds().intersects(brick.getBounds())) {
                // Phản hồi đơn giản: đảo hướng vận tốc dựa trên cạnh va chạm.
                double ballCenterX = ball.getCenter().getX();
                double ballCenterY = ball.getCenter().getY();
                double brickCenterX = brick.getX() + brick.getWidth() / 2.0;
                double brickCenterY = brick.getY() + brick.getHeight() / 2.0;

                double dx = ballCenterX - brickCenterX;
                double dy = ballCenterY - brickCenterY;

                // Nếu chênh lệch theo X lớn hơn chênh lệch theo Y -> Va chạm ngang.
                if (Math.abs(dx) > Math.abs(dy)) {
                    // Đảo vận tốc X.
                    ball.setVelocity(new Velocity(-ball.getVelocity().getDx(), ball.getVelocity().getDy()));
                } else {
                    // Va chạm dọc.
                    // Đảo vận tốc Y.
                    ball.setVelocity(new Velocity(ball.getVelocity().getDx(), -ball.getVelocity().getDy()));
                }

                // Logic phát SFX va chạm tường
            }
        }

    }

    /**
     * Kiểm tra va chạm của tia laser với gạch.
     * Mỗi tia laser chỉ có thể bắn trúng một gạch.
     *
     * @param lasers Danh sách các tia laser đang hoạt động.
     * @param bricks Danh sách các gạch.
     * @return Map chứa các cặp va chạm laser-gạch (laser: gạch bị trúng).
     */
    public Map<Laser, Brick> checkLaserBrickCollisions(List<Laser> lasers, List<Brick> bricks) {
        Map<Laser, Brick> collisions = new HashMap<>();

        for (Laser laser : lasers) {
            // Chỉ kiểm tra tia laser đang hoạt động.
            if (!laser.isAlive()) {
                continue;
            }

            for (Brick brick : bricks) {
                // Chỉ kiểm tra gạch còn sống.
                if (!brick.isAlive()) {
                    continue;
                }

                // Kiểm tra va chạm AABB.
                if (laser.getBounds().intersects(brick.getBounds())) {
                    // Gạch nhận sát thương.
                    brick.takeHit();

                    // Ghi lại cặp va chạm.
                    collisions.put(laser, brick);

                    // Logic phát SFX chạm laser

                    // Tia laser chỉ có thể bắn trúng một gạch, nên dừng vòng lặp bricks.
                    break;
                }
            }
        }

        return collisions;
    }

    /**
     * Kiểm tra va chạm của vật phẩm bổ trợ (PowerUp) với thanh đỡ.
     *
     * @param powerUps Danh sách các vật phẩm đang rơi.
     * @param paddle Thanh đỡ của người chơi.
     * @return Danh sách các vật phẩm đã được thu thập.
     */
    public List<PowerUp> checkPowerUpPaddleCollisions(List<PowerUp> powerUps, Paddle paddle) {
        List<PowerUp> collected = new ArrayList<>();

        // Nếu thanh đỡ không tồn tại, trả về danh sách trống.
        if (paddle == null) return collected;

        for (PowerUp powerUp : powerUps) {
            // Chỉ kiểm tra vật phẩm còn sống.
            if (!powerUp.isAlive()) {
                continue;
            }

            // Sử dụng kiểm tra va chạm của chính PowerUp (nguyên tắc single source of truth).
            if (powerUp.checkPaddleCollision(paddle)) {
                collected.add(powerUp);

                // Logic phát SFX thu thập vật phẩm
            }
        }

        return collected;
    }

    /**
     * Cập nhật kích thước khu vực chơi (hữu ích cho việc thay đổi độ phân giải).
     *
     * @param width Chiều rộng mới.
     * @param height Chiều cao mới.
     */
    public void setPlayAreaSize(int width, int height) {
        this.playAreaWidth = width;
        this.playAreaHeight = height;
    }
}