package Objects.GameEntities;

/**
 * Enum định nghĩa các trạng thái hoạt ảnh của Paddle.
 *
 * Mỗi state tương ứng với một bộ sprite animation hoặc sprite đơn.
 * AnimationFactory sử dụng enum này để tạo Animation phù hợp.
 */
public enum PaddleState {
    /**
     * Trạng thái bình thường (mặc định)
     * Sprite: paddle.png (static, không animation)
     */
    NORMAL,

    /**
     * Trạng thái paddle được mở rộng (EXPAND power-up)
     * Animation: paddle_wide_1.png ... paddle_wide_9.png
     */
    WIDE,

    /**
     * Trạng thái paddle có laser (LASER power-up)
     * Animation: paddle_laser_1.png ... paddle_laser_16.png
     */
    LASER,

    /**
     * Trạng thái paddle nhấp nháy (effect khi sắp hết power-up)
     * Animation: paddle_pulsate_1.png ... paddle_pulsate_4.png
     */
    PULSATE,

    /**
     * Trạng thái paddle xuất hiện/hiện thân (spawn animation)
     * Animation: paddle_materialize_1.png ... paddle_materialize_15.png
     * Chạy 1 lần (loop=false)
     */
    MATERIALIZE,

    /**
     * Trạng thái paddle nổ (khi mất mạng)
     * Animation: paddle_explode_1.png ... paddle_explode_8.png
     * Chạy 1 lần (loop=false)
     */
    EXPLODE
}
