package Render;

import Objects.Bricks.Brick;
import Objects.Bricks.GoldBrick;
import Objects.Bricks.NormalBrick;
import Objects.Bricks.SilverBrick;
import Objects.GameEntities.Ball;
import Objects.GameEntities.Laser;
import Objects.GameEntities.Paddle;
import Objects.GameEntities.PaddleState;
import Objects.PowerUps.PowerUp;
import Utils.Constants;
import Utils.SpriteProvider;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Lớp chịu trách nhiệm vẽ các sprite (hình ảnh) của tất cả các thực thể
 * trong game (Ball, Paddle, Brick, v.v.) lên Canvas.
 * Nó quản lý logic vẽ phức tạp như hoạt ảnh (animations) và trạng thái (states).
 */
public class SpriteRenderer {
    // Context đồ họa để thực hiện các thao tác vẽ
    private final GraphicsContext gc;
    // Đối tượng cung cấp các hình ảnh sprite
    private final SpriteProvider sprites;

    // Các biến thực thể này được khai báo nhưng không dùng để giữ trạng thái.
    private Ball ball;
    private Paddle paddle;
    private Brick brick;
    private NormalBrick normalBrick;
    private SilverBrick silverBrick;
    private GoldBrick goldBrick;
    private PowerUp powerUp;

    /**
     * Khởi tạo SpriteRenderer.
     *
     * @param gc Context đồ họa.
     * @param sprites Đối tượng cung cấp các sprite.
     */
    public SpriteRenderer(GraphicsContext gc, SpriteProvider sprites) {
        this.gc = gc;
        this.sprites = sprites;
    }

    /**
     * Vẽ quả bóng (Ball).
     * @param ball Đối tượng Ball.
     */
    public void drawBall(Ball ball) {
        gc.drawImage(sprites.get("ball.png"), ball.getX(), ball.getY());
    }

    /**
     * Vẽ tia laser (Laser).
     * @param laser Đối tượng Laser.
     */
    public void drawLaser(Laser laser) {
        gc.drawImage(sprites.get("laser_bullet.png"), laser.getX(), laser.getY());
    }

    /**
     * Vẽ thanh trượt (Paddle). Ưu tiên vẽ animation nếu đang chạy, nếu không
     * thì vẽ sprite tĩnh dựa trên trạng thái (NORMAL, WIDE, LASER).
     *
     * @param paddle Đối tượng Paddle.
     */
    public void drawPaddle(Paddle paddle) {
        // Ưu tiên 1: Vẽ khung hình animation nếu đang chạy (dùng cho hiệu ứng chuyển trạng thái)
        if (paddle.isAnimationPlaying()) {
            Animation animation = paddle.getAnimation();
            if (animation != null) {
                Image frame = animation.getCurrentFrame();
                if (frame != null) {
                    // Tính toán vị trí X để căn giữa frame theo chiều ngang của paddle
                    // Điều này đảm bảo animation mở rộng/thu nhỏ đều từ tâm
                    double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
                    double frameWidth = frame.getWidth();
                    double drawX = paddleCenterX - frameWidth / 2.0;
                    
                    gc.drawImage(frame, drawX, paddle.getY());
                    return;
                }
            }
        }

        // Ưu tiên 2: Vẽ sprite tĩnh dựa trên trạng thái
        PaddleState state = paddle.getState();

        if (state == PaddleState.NORMAL) {
            gc.drawImage(sprites.get("paddle.png"), paddle.getX(), paddle.getY());
        } else if (state == PaddleState.WIDE || state == PaddleState.WIDE_PULSATE) {
            // Vẽ thanh trượt rộng
            gc.drawImage(sprites.get("paddle_wide.png"), paddle.getX(), paddle.getY());
        } else if (state == PaddleState.LASER || state == PaddleState.LASER_PULSATE) {
            // Vẽ thanh trượt laser
            gc.drawImage(sprites.get("paddle_laser.png"), paddle.getX(), paddle.getY());
        } else {
            // Fallback: Mặc định vẽ paddle thường
            gc.drawImage(sprites.get("paddle.png"), paddle.getX(), paddle.getY());
        }
    }

    /**
     * Vẽ viên gạch (Brick). Xử lý logic phức tạp cho Gạch Bạc (vết nứt).
     *
     * @param brick Đối tượng Brick.
     */
    public void drawBrick(Brick brick) {
        double x = brick.getX();
        double y = brick.getY();
        double w = Constants.Bricks.BRICK_WIDTH;
        double h = Constants.Bricks.BRICK_HEIGHT;

        if (brick instanceof NormalBrick) {
            // Gạch thường: Vẽ sprite tương ứng với màu gạch
            NormalBrick normalBrick = (NormalBrick) brick;
            String spriteName = "brick_" + normalBrick.getBrickType().name().toLowerCase() + ".png";
            gc.drawImage(sprites.get(spriteName), x, y, w, h);
        } else if (brick instanceof SilverBrick) {
            // Gạch Bạc: Xử lý animation vết nứt
            SilverBrick silverBrick = (SilverBrick) brick;
            Animation crackAnimation = silverBrick.getCrackAnimation();

            if (silverBrick.isCrackAnimationPlaying() && crackAnimation != null) {
                // Vẽ sprite gạch bạc làm nền
                gc.drawImage(sprites.get("brick_silver.png"), x, y, w, h);

                // Vẽ frame nứt đè lên trên
                Image crackFrame = crackAnimation.getCurrentFrame();
                if (crackFrame != null) {
                    gc.drawImage(crackFrame, x, y, w, h);
                }
            } else {
                // Chỉ vẽ gạch bạc (chưa nứt)
                gc.drawImage(sprites.get("brick_silver.png"), x, y, w, h);
            }
        } else if (brick instanceof GoldBrick) {
            // Gạch Vàng: Chỉ vẽ sprite gạch vàng
            GoldBrick goldBrick = (GoldBrick) brick;
            gc.drawImage(sprites.get("brick_gold.png"), x, y, w, h);
        } else {
            // Fallback: Vẽ hình chữ nhật màu xám
            gc.setFill(Color.GRAY);
            gc.fillRect(x, y, brick.getWidth(), brick.getHeight());
        }
    }

    /**
     * Vẽ PowerUp đang rơi. Ưu tiên vẽ animation (nếu có).
     *
     * @param powerUp Đối tượng PowerUp.
     */
    public void drawPowerUp(PowerUp powerUp) {
        // Chỉ vẽ nếu PowerUp đang hoạt động
        if (powerUp == null || !powerUp.isActive()) {
            return;
        }

        double x = powerUp.getX();
        double y = powerUp.getY();

        // Vẽ animation của PowerUp
        Animation animation = powerUp.getAnimation();
        if (animation != null && animation.isPlaying()) {
            Image frame = animation.getCurrentFrame();
            if (frame != null) {
                gc.drawImage(frame, x, y);
                return;
            }
        }

        // Fallback: Vẽ hình tròn màu vàng nếu không có sprite
        gc.setFill(Color.YELLOW);
        gc.fillOval(x, y, powerUp.getWidth(), powerUp.getHeight());
    }
}
