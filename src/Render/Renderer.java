package Render;

import Objects.GameEntities.Ball;
import Objects.Bricks.Brick;
import Objects.GameEntities.Paddle;
import Objects.PowerUps.PowerUp;

/**
 * Giao diện trừu tượng chịu trách nhiệm vẽ các đối tượng game lên màn hình.
 *
 * Mục đích:
 * - Tách riêng phần render khỏi logic game (ví dụ: JavaFX Canvas hoặc engine khác).
 * - Cung cấp các phương thức cần thiết để các đối tượng game gọi khi render.
 */
public interface Renderer {
    /** Xoá màn hình trước khi vẽ lại */
    void clear();

    void drawBall(Ball b);
    void drawPaddle(Paddle p);
    void drawBrick(Brick b);
    void drawPowerUp(PowerUp p);
    void drawText(String text, double x, double y);

    /** Gọi khi hoàn tất các lệnh vẽ cho frame (Canvas vẽ ngay nên thường trống) */
    void present();
}
