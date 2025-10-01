package Objects;

/**
 * Power-up tăng tốc độ của bóng khi được áp dụng.
 *
 * Lưu ý: cài đặt hiện tại lưu hệ số tăng tốc nhưng không tự động áp dụng lên Ball;
 * GameManager hoặc mã gọi đến phải lấy thông tin này và thay đổi vận tốc bóng theo ý muốn.
 */
public class FastBallPowerUp extends PowerUp {
    private double speedMultiplier;

    public FastBallPowerUp(double x, double y, double width, double height, double duration, double speedMultiplier) {
        super(x,y,width,height,duration);
        this.speedMultiplier = speedMultiplier;
    }

    /**
     * Ghi chú: phương thức này không áp dụng trực tiếp trên {@link Paddle};
     * để áp dụng lên {@link Objects.Ball}, GameManager cần đọc instance này
     * và điều chỉnh {@code Ball.velocity} tương ứng.
     */
    @Override
    public void applyEffect(Paddle paddle) {
        // not applicable to paddle; this powerup should be applied to a Ball via GameManager
    }

    @Override
    public void removeEffect(Paddle paddle) { }

    @Override
    public void update() { }

    @Override
    public void render(Render.Renderer renderer) { renderer.drawPowerUp(this); }
}
