package Render;

import Objects.Ball;
import Objects.Paddle;
import Objects.Brick;
import Objects.PowerUp;

/**
 * Abstraction cho lớp responsible vẽ các đối tượng game lên màn hình.
 *
 * Mục đích:
 * - Tách rời logic game khỏi chi tiết vẽ (ví dụ JavaFX Canvas hoặc engine khác).
 * - Cung cấp các phương thức cần thiết để đối tượng gọi khi render.
 */
public interface Renderer {
    void clear();
    void drawBall(Ball b);
    void drawPaddle(Paddle p);
    void drawBrick(Brick b);
    void drawPowerUp(PowerUp p);
    void drawText(String text, double x, double y);
    void present();
}
