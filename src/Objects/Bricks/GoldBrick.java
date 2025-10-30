package Objects.Bricks;

/**
 * <p>Lớp đại diện cho loại gạch Vàng (Gold Brick) trong trò chơi.
 * Đây là loại gạch gần như **không thể phá hủy**
 * (thể hiện qua việc phương thức {@link #takeHit()} không làm gì cả)
 * và thường được dùng làm chướng ngại vật cố định hoặc biên giới.</p>
 */
public class GoldBrick extends Brick{

    /**
     * <p>Constructor khởi tạo một viên gạch Vàng.</p>
     *
     * @param x Tọa độ x của góc trên bên trái viên gạch.
     * @param y Tọa độ y của góc trên bên trái viên gạch.
     * @param width Chiều rộng của viên gạch.
     * @param height Chiều cao của viên gạch.
     */
    public GoldBrick(double x, double y, double width, double height) {
        // Gọi constructor của lớp cha (Brick) với hitPoints lấy từ BrickType.GOLD
        // Mặc dù hitPoints là 999 nhưng GoldBrick vẫn override takeHit() để đảm bảo không bị phá hủy.
        super(x, y, width, height, BrickType.GOLD.getHitPoints());
    }

    /**
     * <p>Xử lý sự kiện khi gạch bị va chạm (hit).
     * Gạch Vàng là **không thể phá hủy**, nên phương thức này không thực hiện hành động nào
     * (không giảm hit points) để giữ viên gạch luôn tồn tại.</p>
     */
    @Override
    public void takeHit() {
        // Gold bricks are indestructible; do nothing on hit
        // Gạch vàng không thể phá hủy; không làm gì khi bị va chạm.
    }

    /**
     * <p>Cập nhật trạng thái của gạch Vàng trong mỗi vòng lặp game.
     * Gạch Vàng không có hành vi đặc biệt nào cần cập nhật.</p>
     */
    @Override
    public void update() {
        // GoldBrick does not have special behavior in update
        // GoldBrick không có hành vi đặc biệt nào cần cập nhật trong phương thức này.
    }

    /**
     * <p>Trả về loại gạch cụ thể (BrickType) của đối tượng này.</p>
     *
     * @return Luôn trả về {@link BrickType#GOLD}.
     */
    @Override
    public BrickType getBrickType() {
        return BrickType.GOLD;
    }
}