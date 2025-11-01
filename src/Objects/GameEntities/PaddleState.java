package Objects.GameEntities;

/**
 * PaddleState là một enum định nghĩa tất cả các trạng thái hình ảnh và animation
 * có thể có của thanh đỡ (Paddle) trong game.
 *
 * Mỗi trạng thái chứa thông tin cần thiết để tạo và quản lý animation tương ứng,
 * bao gồm tiền tố tên sprite, số lượng frame, và cờ lặp (loop).
 */
public enum PaddleState {
    // Trạng thái mặc định, không có animation đặc biệt
    NORMAL("paddle", 1, false),

    // Trạng thái Paddle mở rộng (WIDE)
    WIDE("paddle_wide", 9, false),                 // Animation chuyển đổi (chạy một lần, không lặp)

    // Trạng thái WIDE nhưng sắp hết hạn (cảnh báo)
    WIDE_PULSATE("paddle_wide_pulsate", 4, true),  // Animation cảnh báo (lặp)

    // Trạng thái Paddle có laser
    LASER("paddle_laser", 16, false),              // Animation chuyển đổi (chạy một lần, không lặp)

    // Trạng thái LASER nhưng sắp hết hạn (cảnh báo)
    LASER_PULSATE("paddle_laser_pulsate", 4, true), // Animation cảnh báo (lặp)

    // Trạng thái nhấp nháy chung cho các hiệu ứng độc lập với hình dạng (Catch/Slow) sắp hết hạn
    PULSATE("paddle_pulsate", 4, true),            // Animation cảnh báo (lặp)

    // Animation khi paddle mới xuất hiện
    MATERIALIZE("paddle_materialize", 15, false),  // Animation xuất hiện (chạy một lần, không lặp)

    // Animation khi paddle bị phá hủy
    EXPLODE("paddle_explode", 8, false);           // Animation phá hủy (chạy một lần, không lặp)

    // Tiền tố (prefix) được sử dụng để tìm kiếm các file sprite liên quan
    private final String paddlePrefix;
    // Tổng số frame trong animation của trạng thái này
    private final int frameCount;
    // Cờ chỉ định liệu animation có nên lặp lại hay chỉ chạy một lần
    private final boolean shouldLoop;

    /**
     * Constructor cho PaddleState.
     *
     * @param paddlePrefix Tiền tố tên file sprite (ví dụ: "paddle_wide").
     * @param frameCount Số lượng frame animation.
     * @param shouldLoop true nếu animation nên lặp lại (ví dụ: trạng thái cảnh báo), false nếu chỉ chạy một lần (ví dụ: trạng thái chuyển đổi).
     */
    PaddleState(String paddlePrefix, int frameCount, boolean shouldLoop) {
        this.paddlePrefix = paddlePrefix;
        this.frameCount = frameCount;
        this.shouldLoop = shouldLoop;
    }

    /**
     * Lấy tiền tố tên file sprite cho trạng thái này.
     *
     * @return Tiền tố sprite.
     */
    public String getPaddlePrefix() {
        return paddlePrefix;
    }

    /**
     * Lấy số lượng frame animation cho trạng thái này.
     *
     * @return Số lượng frame.
     */
    public int getFrameCount() {
        return frameCount;
    }

    /**
     * Kiểm tra xem animation có nên lặp lại hay không.
     *
     * @return true nếu animation nên lặp (loop), false nếu chỉ chạy một lần (one-shot).
     */
    public boolean shouldLoop() {
        return shouldLoop;
    }
}