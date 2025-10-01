package Objects;

import GeometryPrimitives.Velocity;
import Render.Renderer;

/**
 * Thanh điều khiển (paddle) do người chơi điều khiển.
 *
 * Tính năng:
 * - Di chuyển ngang (left/right/stop) bằng cách thay đổi vận tốc theo trục x.
 * - Có thể nhận power-up (apply/remove) và lưu một power-up đang hoạt động.
 */
public class Paddle extends MovableObject {
    private double speed; // pixels per frame
    private PowerUp currentPowerUp;

    public Paddle(double x, double y, double width, double height, double speed) {
        super(x, y, width, height);
        this.speed = speed;
    }

    @Override
    public void update() {
        move();
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

    /**
     * Áp dụng power-up lên paddle (lưu tham chiếu và gọi applyEffect).
     * @param p power-up được áp dụng
     */
    public void applyPowerUp(PowerUp p) {
        this.currentPowerUp = p;
        p.applyEffect(this);
    }

    /** Gỡ power-up hiện tại nếu có. */
    public void removePowerUp() {
        if (this.currentPowerUp != null) {
            this.currentPowerUp.removeEffect(this);
            this.currentPowerUp = null;
        }
    }

    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double s) {
        this.speed = s;
    }
}
