package Objects.Bricks;

/**
 * Lớp {@code GoldBrick} đại diện cho loại gạch vàng trong trò chơi.
 *
 * <p>Gạch vàng là loại gạch đặc biệt có độ bền cực cao (gần như không thể phá hủy).
 * Vì vậy, các phương thức {@link #takeHit()} và {@link #update()} không làm gì cả
 * — gạch vàng không bị ảnh hưởng bởi va chạm hoặc đòn đánh.</p>
 *
 * <p>Thông tin về loại gạch này được định nghĩa trong {@link BrickType#GOLD}.</p>
 */
public class GoldBrick extends Brick {

    /**
     * Tạo một đối tượng {@code GoldBrick} mới tại vị trí và kích thước xác định.
     *
     * @param x      tọa độ X của gạch (góc trên bên trái)
     * @param y      tọa độ Y của gạch (góc trên bên trái)
     * @param width  chiều rộng của gạch
     * @param height chiều cao của gạch
     */
    public GoldBrick(double x, double y, double width, double height) {
        // Gọi constructor cha với số lần chịu đòn lấy từ BrickType.GOLD
        super(x, y, width, height, BrickType.GOLD.getHitPoints());
    }

    /**
     * Ghi đè phương thức {@link Brick#takeHit()}.
     * <p>Gạch vàng không thể bị phá, nên phương thức này được để trống.</p>
     */
    @Override
    public void takeHit() {
        // Không làm gì — gạch vàng không thể bị phá
    }

    /**
     * Cập nhật trạng thái gạch.
     * <p>Gạch vàng không có hành vi đặc biệt nên phương thức này để trống.</p>
     */
    @Override
    public void update() {
        // Không có hành vi đặc biệt cho gạch vàng
    }

    /**
     * Trả về loại gạch tương ứng, là {@link BrickType#GOLD}.
     *
     * @return loại gạch vàng
     */
    @Override
    public BrickType getBrickType() {
        return BrickType.GOLD;
    }
}
