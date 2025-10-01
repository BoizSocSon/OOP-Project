package Objects;
/**
 * Power-up dạng mở rộng chiều ngang của paddle khi người chơi nhặt được.
 *
 * Cơ chế đơn giản:
 * - Khi áp dụng, tăng trực tiếp kích thước {@code width} của paddle.
 * - Khi hết hiệu lực (hoặc bị xoá), giảm lại kích thước tương ứng.
 *
 * Lưu ý: cài đặt hiện tại thay đổi trực tiếp field package-visible của Paddle;
 * trong hệ thống lớn hơn nên dùng API an toàn (getter/setter) để tránh lỗi.
 */
public class ExpandPaddlePowerUp extends PowerUp {
    private double expandAmount;

    public ExpandPaddlePowerUp(double x, double y, double width, double height, double duration, double expandAmount) {
        super(x,y,width,height,duration);
        this.expandAmount = expandAmount;
    }

    /** Áp dụng hiệu ứng lên paddle: tăng chiều rộng paddle. */
    @Override
    public void applyEffect(Paddle paddle) {
        paddle.setSpeed(paddle.getSpeed());
        paddle.width += expandAmount; // package-visible field
    }

    /** Bỏ hiệu ứng: trả lại chiều rộng ban đầu. */
    @Override
    public void removeEffect(Paddle paddle) {
        paddle.width -= expandAmount;
    }

    @Override
    public void update() { /* falls through as it just falls */ }

    @Override
    public void render(Render.Renderer renderer) { renderer.drawPowerUp(this); }
}
