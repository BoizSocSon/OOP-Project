package Objects.GameEntities;

/**
 * <p>Enum đại diện cho các trạng thái hoạt ảnh (animation state) khác nhau của **Thanh đỡ (Paddle)**.
 * Mỗi trạng thái xác định tên sprite cơ sở, số lượng khung hình, và liệu hoạt ảnh có lặp lại hay không.</p>
 */
public enum PaddleState {

    // Các trạng thái hoạt ảnh của thanh đỡ

    /** Trạng thái bình thường, sprite tĩnh. */
    NORMAL("paddle", 1, false),

    /** Trạng thái mở rộng, hoạt ảnh chuyển đổi (chơi một lần). */
    WIDE("paddle_wide", 9, false),                 // Transition animation (one-shot)

    /** Trạng thái mở rộng và nhấp nháy (cảnh báo hết hạn hiệu ứng), hoạt ảnh lặp. */
    WIDE_PULSATE("paddle_wide_pulsate", 4, true),  // Warning animation (loop)

    /** Trạng thái Laser, hoạt ảnh chuyển đổi (chơi một lần). */
    LASER("paddle_laser", 16, false),              // Transition animation (one-shot)

    /** Trạng thái Laser và nhấp nháy (cảnh báo hết hạn hiệu ứng), hoạt ảnh lặp. */
    LASER_PULSATE("paddle_laser_pulsate", 4, true), // Warning animation (loop)

    /** Trạng thái nhấp nháy chung (cảnh báo hết hạn hiệu ứng CATCH/SLOW), hoạt ảnh lặp. */
    PULSATE("paddle_pulsate", 4, true),            // Warning animation (loop)

    /** Trạng thái xuất hiện/khởi tạo, hoạt ảnh chơi một lần. */
    MATERIALIZE("paddle_materialize", 15, false),  // Spawn animation (one-shot)

    /** Trạng thái bị phá hủy/nổ, hoạt ảnh chơi một lần. */
    EXPLODE("paddle_explode", 8, false);           // Death animation (one-shot)

    /** Tiền tố (prefix) được sử dụng để tìm kiếm các tệp sprite tương ứng. */
    private final String paddlePrefix;

    /** Tổng số khung hình (frame count) trong hoạt ảnh của trạng thái này. */
    private final int frameCount;

    /** Xác định liệu hoạt ảnh có nên lặp lại (loop) hay không. */
    private final boolean shouldLoop;

    /**
     * <p>Constructor cho enum PaddleState.</p>
     *
     * @param paddlePrefix Tiền tố tên sprite.
     * @param frameCount Tổng số khung hình.
     * @param shouldLoop Liệu hoạt ảnh có lặp lại không.
     */
    PaddleState(String paddlePrefix, int frameCount, boolean shouldLoop) {
        this.paddlePrefix = paddlePrefix;
        this.frameCount = frameCount;
        this.shouldLoop = shouldLoop;
    }

    /**
     * <p>Trả về tiền tố tên sprite của trạng thái này.</p>
     *
     * @return Chuỗi tiền tố sprite (ví dụ: "paddle_wide").
     */
    public String getPaddlePrefix() {
        return paddlePrefix;
    }

    /**
     * <p>Trả về tổng số khung hình của hoạt ảnh.</p>
     *
     * @return Số lượng khung hình.
     */
    public int getFrameCount() {
        return frameCount;
    }

    /**
     * <p>Kiểm tra xem hoạt ảnh có nên lặp lại (loop) sau khi chơi hết hay không.</p>
     *
     * @return {@code true} nếu hoạt ảnh nên lặp lại (ví dụ: trạng thái cảnh báo), ngược lại {@code false}.
     */
    public boolean shouldLoop() {
        return shouldLoop;
    }
}