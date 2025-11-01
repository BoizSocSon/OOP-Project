package Objects.Bricks;

/**
 * Lớp {@code NormalBrick} đại diện cho loại gạch thông thường trong trò chơi.
 *
 * <p>Gạch thường có thể bị phá sau một số lần va chạm nhất định (tùy theo {@link BrickType}).
 * Khi bị phá, người chơi nhận được số điểm tương ứng với {@code baseScore} của loại gạch đó.</p>
 *
 * <p>Ví dụ:
 * {@code new NormalBrick(100, 200, 64, 32, BrickType.RED);}
 * sẽ tạo một viên gạch đỏ ở vị trí (100, 200), kích thước 64×32 pixel.</p>
 */
public class NormalBrick extends Brick {

    /** Loại gạch (màu sắc, độ bền, điểm số) được xác định bởi {@link BrickType} */
    private final BrickType brickType;

    /**
     * Khởi tạo một gạch thông thường với vị trí, kích thước và loại gạch xác định.
     *
     * @param x          tọa độ X của gạch (góc trên bên trái)
     * @param y          tọa độ Y của gạch (góc trên bên trái)
     * @param width      chiều rộng của gạch
     * @param height     chiều cao của gạch
     * @param brickType  loại gạch (ví dụ: RED, BLUE, GREEN,...)
     */
    public NormalBrick(double x, double y, double width, double height, BrickType brickType) {
        // Gọi constructor lớp cha với số hitPoints tương ứng từ BrickType
        super(x, y, width, height, brickType.getHitPoints());
        this.brickType = brickType;
    }

    /**
     * Phương thức cập nhật trạng thái gạch (nếu cần).
     * <p>Hiện tại, {@code NormalBrick} không có hành vi đặc biệt,
     * nhưng có thể được mở rộng trong tương lai (ví dụ: hiệu ứng nhấp nháy, rung,...).</p>
     */
    @Override
    public void update() {
        // Không có hành vi đặc biệt cho gạch thường
    }

    /**
     * Trả về loại gạch hiện tại.
     *
     * @return {@link BrickType} của viên gạch này
     */
    @Override
    public BrickType getBrickType() {
        return brickType;
    }

    /**
     * Lấy điểm số người chơi nhận được khi phá hủy viên gạch này.
     *
     * @return điểm tương ứng với loại gạch
     */
    public int getScoreValue() {
        return brickType.getBaseScore();
    }
}
