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
    private Double originalWidth = null;

    public ExpandPaddlePowerUp(double x, double y, double width, double height, double duration, double expandAmount) {
        super(x,y,width,height,duration);
        this.expandAmount = expandAmount;
    }

    /** Áp dụng hiệu ứng lên paddle: tăng chiều rộng paddle. */
    @Override
    public void applyEffect(Paddle paddle) {
        // store original width so it can be restored later
        if (originalWidth == null) {
            originalWidth = paddle.getWidth();
        }
        paddle.setWidth(paddle.getWidth() + expandAmount);
    }

    /** Bỏ hiệu ứng: trả lại chiều rộng ban đầu. */
    @Override
    public void removeEffect(Paddle paddle) {
        if (originalWidth != null) {
            paddle.setWidth(originalWidth);
            originalWidth = null;
        } else {
            // fallback: decrement by amount
            paddle.setWidth(Math.max(0, paddle.getWidth() - expandAmount));
        }
    }

    @Override
    public void update() { /* falls through as it just falls */ }

    @Override
    public void render(Render.Renderer renderer) { renderer.drawPowerUp(this); }
}
