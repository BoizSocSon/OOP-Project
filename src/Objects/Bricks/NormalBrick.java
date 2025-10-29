package Objects.Bricks;

//import Render.Renderer;

/**
 * <p>Lớp đại diện cho các loại gạch thông thường (Normal Brick) trong trò chơi.
 * Các viên gạch này có thể bị phá hủy khi **hit points** giảm về 0.
 * Loại gạch cụ thể (màu sắc, điểm số) được xác định bởi {@link BrickType} được truyền vào.</p>
 */
public class NormalBrick extends Brick {

    /** Loại gạch cụ thể (ví dụ: BLUE, RED, GREEN) của viên gạch này. */
    private final BrickType brickType;

    /**
     * <p>Constructor khởi tạo một viên gạch thông thường.</p>
     *
     * @param x Tọa độ x của góc trên bên trái viên gạch.
     * @param y Tọa độ y của góc trên bên trái viên gạch.
     * @param width Chiều rộng của viên gạch.
     * @param height Chiều cao của viên gạch.
     * @param brickType Loại gạch cụ thể mà viên gạch này đại diện (ví dụ: BrickType.BLUE).
     */
    public NormalBrick(double x, double y, double width, double height, BrickType brickType) {
        // Gọi constructor của lớp cha (Brick) và thiết lập hit points dựa trên BrickType.
        super(x, y, width, height, brickType.getHitPoints());
        this.brickType = brickType;
    }

    /**
     * <p>Cập nhật trạng thái của gạch thông thường trong mỗi vòng lặp game.
     * Gạch thông thường không có hành vi đặc biệt nào cần được cập nhật.</p>
     */
    @Override
    public void update() {
        // NormalBrick không có hành vi đặc biệt trong phương thức update
        // (Không có chuyển động, không có hiệu ứng đặc biệt).
    }

    /**
     * <p>Trả về loại gạch cụ thể (BrickType) của đối tượng này.</p>
     *
     * @return {@link BrickType} của viên gạch này (ví dụ: BLUE, RED).
     */
    @Override
    public BrickType getBrickType() {
        return brickType;
    }

    /**
     * <p>Trả về giá trị điểm số cơ bản mà người chơi nhận được khi phá hủy viên gạch này.</p>
     *
     * @return Giá trị điểm số cơ bản lấy từ {@link BrickType#getBaseScore()}.
     */
    public int getScoreValue() {
        return brickType.getBaseScore();
    }
}