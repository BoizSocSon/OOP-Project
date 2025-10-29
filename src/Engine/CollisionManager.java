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
 * {@code CollisionManager} — lớp chịu trách nhiệm quản lý toàn bộ quá trình
 * phát hiện và xử lý va chạm trong trò chơi (Breakout hoặc tương tự Arkanoid).
 *
 * <p>Chức năng chính bao gồm:
 * <ul>
 *     <li>Phát hiện va chạm giữa bóng và tường</li>
 *     <li>Phát hiện va chạm giữa bóng và paddle (bao gồm tính góc bật)</li>
 *     <li>Phát hiện va chạm giữa bóng và gạch</li>
 *     <li>Phát hiện va chạm giữa tia laser và gạch</li>
 *     <li>Phát hiện va chạm giữa PowerUp và paddle</li>
 * </ul>
 *
 * <p>Thiết kế hướng module giúp tách logic va chạm khỏi {@code GameManager},
 * dễ bảo trì và mở rộng (ví dụ: spatial partitioning hoặc swept collision trong tương lai).
 */
public class CollisionManager {

    /** Chiều rộng vùng chơi */
    private int playAreaWidth;

    /** Chiều cao vùng chơi */
    private int playAreaHeight;

    /** Góc bật tối đa của bóng khi va vào mép paddle */
    private static final double MAX_BOUNCE_ANGLE = Constants.Paddle.PADDLE_MAX_ANGLE;

    /**
     * Khởi tạo một {@code CollisionManager} cho khu vực chơi cụ thể.
     *
     * @param width  chiều rộng vùng chơi
     * @param height chiều cao vùng chơi
     */
    public CollisionManager(int width, int height) {
        this.playAreaWidth = width;
        this.playAreaHeight = height;
    }

    /**
     * Phát hiện và xử lý va chạm giữa bóng và các tường (trái, phải, trên).
     * <p>Khi bóng chạm tường, thành phần vận tốc tương ứng bị đảo chiều.
     *
     * @param ball        đối tượng bóng
     * @param leftBorder  tọa độ biên trái (thường = 0)
     * @param rightBorder tọa độ biên phải (thường = playAreaWidth)
     * @param topBorder   tọa độ biên trên (thường = 0)
     */
    public void checkBallWallCollisions(Ball ball, double leftBorder, double rightBorder, double topBorder) {
        boolean collided = false;

        // Va chạm tường bên trái
        if (ball.getX() <= leftBorder) {
            ball.setX(leftBorder); // chỉnh lại vị trí hợp lệ
            ball.setVelocity(new Velocity(
                    Math.abs(ball.getVelocity().getDx()), // đảo hướng X sang phải
                    ball.getVelocity().getDy()
            ));
            collided = true;
        }

        // Va chạm tường bên phải
        if (ball.getX() + ball.getWidth() >= rightBorder) {
            ball.setX(rightBorder - ball.getWidth());
            ball.setVelocity(new Velocity(
                    -Math.abs(ball.getVelocity().getDx()), // đảo hướng X sang trái
                    ball.getVelocity().getDy()
            ));
            collided = true;
        }

        // Va chạm trần (trên cùng)
        if (ball.getY() <= topBorder) {
            ball.setY(topBorder);
            ball.setVelocity(new Velocity(
                    ball.getVelocity().getDx(),
                    Math.abs(ball.getVelocity().getDy()) // đảo hướng Y để bóng nảy xuống
            ));
            collided = true;
        }

        // Nếu có va chạm, có thể phát hiệu ứng âm thanh hoặc animation
        if (collided) {
            // AudioManager.playSFX(WALL_HIT);
        }
    }

    /**
     * Phát hiện và xử lý va chạm giữa bóng và paddle.
     * <p>Tính toán góc bật dựa theo vị trí va chạm trên paddle:
     * trung tâm -> bóng bật thẳng đứng, mép -> bóng bật xiên.
     *
     * @param ball   đối tượng bóng
     * @param paddle paddle của người chơi
     * @return {@code true} nếu có va chạm
     */
    public boolean checkBallPaddleCollision(Ball ball, Paddle paddle) {
        // Nếu không giao nhau thì không có va chạm
        if (!ball.getBounds().intersects(paddle.getBounds())) {
            return false;
        }

        // Tính vị trí tâm để xác định vị trí va chạm tương đối
        double ballCenterX = ball.getCenter().getX();
        double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
        double paddleHalfWidth = paddle.getWidth() / 2.0;

        // hitPosition trong [-1, 1], 0 là giữa paddle
        double hitPosition = (ballCenterX - paddleCenterX) / paddleHalfWidth;
        hitPosition = Math.max(-1.0, Math.min(1.0, hitPosition)); // giới hạn giá trị

        // Nếu paddle đang bật chế độ “catch” → bóng dính lại, không bật nảy
        if (paddle.isCatchModeEnabled()) {
            return true;
        }

        // Tính toán lại vận tốc dựa vào góc bật
        calculateBallAngleFromPaddle(ball, paddle, hitPosition);

        // Đặt bóng ngay trên paddle để tránh "kẹt" khi chồng bounding box
        ball.setY(paddle.getY() - ball.getHeight() - 1);

        return true;
    }

    /**
     * Tính toán góc bật của bóng khi va vào paddle.
     * <p>Trung tâm paddle: bóng bật thẳng lên; càng xa trung tâm → góc bật càng lớn.
     *
     * @param ball        đối tượng bóng
     * @param paddle      paddle va chạm
     * @param hitPosition vị trí va chạm (-1 trái, +1 phải)
     */
    private void calculateBallAngleFromPaddle(Ball ball, Paddle paddle, double hitPosition) {
        // Tính tốc độ hiện tại (căn bậc 2 của dx² + dy²)
        double speed = Math.hypot(ball.getVelocity().getDx(), ball.getVelocity().getDy());

        // Chuyển vị trí va chạm thành góc
        double angle = hitPosition * MAX_BOUNCE_ANGLE;
        double angleRad = Math.toRadians(angle);

        // Tính lại dx, dy theo góc bật
        double dx = speed * Math.sin(angleRad);
        double dy = -speed * Math.cos(angleRad); // hướng lên → âm

        ball.setVelocity(new Velocity(dx, dy));
    }

    /**
     * Kiểm tra va chạm giữa bóng và danh sách gạch.
     * <p>Gạch vàng (Gold) sẽ không bị phá, chỉ phản xạ bóng.
     *
     * @param ball   bóng cần kiểm tra
     * @param bricks danh sách gạch
     * @return danh sách gạch bị phá vỡ
     */
    public List<Brick> checkBallBrickCollisions(Ball ball, List<Brick> bricks) {
        List<Brick> destroyedBricks = new ArrayList<>();

        for (Brick brick : bricks) {
            if (!brick.isAlive()) continue; // bỏ qua gạch đã vỡ

            // Gạch vàng chỉ phản xạ
            if (brick.getBrickType() == BrickType.GOLD) {
                ignoreGoldBricksCollision(brick, ball);
                continue;
            }

            // Nếu bóng chạm gạch
            if (ball.checkCollisionWithRect(brick.getBounds())) {
                brick.takeHit(); // giảm độ bền

                if (brick.isDestroyed()) {
                    destroyedBricks.add(brick);
                }
            }
        }

        return destroyedBricks;
    }

    /**
     * Xử lý riêng cho va chạm giữa bóng và gạch vàng (Gold Brick).
     * <p>Không bị phá, chỉ phản xạ vận tốc bóng.
     *
     * @param brick gạch vàng
     * @param ball  bóng va chạm
     */
    private void ignoreGoldBricksCollision(Brick brick, Ball ball) {
        if (ball.getBounds().intersects(brick.getBounds())) {
            // So sánh vị trí tâm để xác định hướng phản xạ
            double dx = ball.getCenter().getX() - (brick.getX() + brick.getWidth() / 2.0);
            double dy = ball.getCenter().getY() - (brick.getY() + brick.getHeight() / 2.0);

            // Xác định hướng va chạm trội
            if (Math.abs(dx) > Math.abs(dy)) {
                // Va chạm ngang → đảo trục X
                ball.setVelocity(new Velocity(-ball.getVelocity().getDx(), ball.getVelocity().getDy()));
            } else {
                // Va chạm dọc → đảo trục Y
                ball.setVelocity(new Velocity(ball.getVelocity().getDx(), -ball.getVelocity().getDy()));
            }
        }
    }

    /**
     * Kiểm tra va chạm giữa các tia laser và gạch.
     * <p>Mỗi laser chỉ có thể trúng một gạch duy nhất.
     *
     * @param lasers danh sách laser đang hoạt động
     * @param bricks danh sách gạch
     * @return Map (laser, brick) các cặp va chạm
     */
    public Map<Laser, Brick> checkLaserBrickCollisions(List<Laser> lasers, List<Brick> bricks) {
        Map<Laser, Brick> collisions = new HashMap<>();

        for (Laser laser : lasers) {
            if (!laser.isAlive()) continue;

            for (Brick brick : bricks) {
                if (!brick.isAlive()) continue;

                if (laser.getBounds().intersects(brick.getBounds())) {
                    brick.takeHit(); // gạch nhận sát thương
                    collisions.put(laser, brick);
                    break; // laser chỉ phá 1 gạch
                }
            }
        }

        return collisions;
    }

    /**
     * Kiểm tra va chạm giữa PowerUp đang rơi và paddle.
     *
     * @param powerUps danh sách PowerUp đang rơi
     * @param paddle   paddle người chơi
     * @return danh sách PowerUp được thu thập
     */
    public List<PowerUp> checkPowerUpPaddleCollisions(List<PowerUp> powerUps, Paddle paddle) {
        List<PowerUp> collected = new ArrayList<>();

        if (paddle == null) return collected;

        for (PowerUp powerUp : powerUps) {
            if (!powerUp.isAlive()) continue;

            if (powerUp.checkPaddleCollision(paddle)) {
                collected.add(powerUp);
            }
        }

        return collected;
    }

    /**
     * Cập nhật lại kích thước vùng chơi (thường dùng khi thay đổi độ phân giải màn hình).
     *
     * @param width  chiều rộng mới
     * @param height chiều cao mới
     */
    public void setPlayAreaSize(int width, int height) {
        this.playAreaWidth = width;
        this.playAreaHeight = height;
    }
}
