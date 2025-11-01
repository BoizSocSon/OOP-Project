package Objects.Bricks;

import Utils.Constants;

/**
 * Enum {@code BrickType} định nghĩa các loại gạch khác nhau trong trò chơi.
 * <p>Mỗi loại gạch có ba thuộc tính chính:
 * <ul>
 *     <li>{@code hitPoints} – số lần gạch có thể chịu đòn trước khi bị phá hủy</li>
 *     <li>{@code spriteName} – tên hình ảnh (sprite) dùng để hiển thị gạch</li>
 *     <li>{@code baseScore} – số điểm mà người chơi nhận được khi phá gạch</li>
 * </ul>
 *
 * <p>Ví dụ:
 * <pre>{@code
 * BrickType.RED.getBaseScore(); // trả về điểm của gạch đỏ
 * }</pre>
 *
 * Các hằng số điểm được lấy từ {@link Constants.Scoring}.
 */
public enum BrickType {

    /** Gạch màu xanh dương – dễ phá, điểm cộng thấp */
    BLUE(1, "brick_blue", Constants.Scoring.SCORE_BRICK_BASE + 10),

    /** Gạch màu đỏ – điểm cao hơn xanh */
    RED(1, "brick_red", Constants.Scoring.SCORE_BRICK_BASE + 20),

    /** Gạch màu xanh lá cây */
    GREEN(1, "brick_green", Constants.Scoring.SCORE_BRICK_BASE + 30),

    /** Gạch màu vàng */
    YELLOW(1, "brick_yellow", Constants.Scoring.SCORE_BRICK_BASE + 40),

    /** Gạch màu cam */
    ORANGE(1, "brick_orange", Constants.Scoring.SCORE_BRICK_BASE + 50),

    /** Gạch màu hồng */
    PINK(1, "brick_pink", Constants.Scoring.SCORE_BRICK_BASE + 60),

    /** Gạch màu cyan (xanh ngọc) */
    CYAN(1, "brick_cyan", Constants.Scoring.SCORE_BRICK_BASE + 70),

    /** Gạch màu trắng */
    WHITE(1, "brick_white", Constants.Scoring.SCORE_BRICK_BASE + 80),

    /** Gạch bạc – bền hơn, cần 2 lần đánh mới phá được */
    SILVER(2, "brick_silver", Constants.Scoring.SCORE_BRICK_BASE),

    /** Gạch vàng – gần như không thể phá, có thể chỉ xuất hiện để trang trí hoặc bonus */
    GOLD(999, "brick_gold", Constants.Scoring.SCORE_BRICK_BASE + 0);

    // --- Thuộc tính của từng loại gạch ---

    /** Số lần có thể chịu đòn (hit) trước khi bị phá */
    private final int hitPoints;

    /** Tên hình ảnh (sprite) tương ứng với loại gạch trong game assets */
    private final String spriteName;

    /** Điểm cơ bản mà người chơi nhận được khi phá gạch */
    private final int baseScore;

    /**
     * Hàm khởi tạo enum {@code BrickType}.
     *
     * @param hitPoints  số lần có thể chịu đòn
     * @param spriteName tên hình ảnh tương ứng
     * @param baseScore  điểm cơ bản khi phá gạch
     */
    BrickType(int hitPoints, String spriteName, int baseScore) {
        this.hitPoints = hitPoints;
        this.spriteName = spriteName;
        this.baseScore = baseScore;
    }

    /**
     * Lấy số lần gạch có thể chịu đòn trước khi bị phá hủy.
     *
     * @return số hitPoints
     */
    public int getHitPoints() {
        return hitPoints;
    }

    /**
     * Lấy tên sprite (tên file hình ảnh) tương ứng với gạch.
     *
     * @return tên sprite
     */
    public String getSpriteName() {
        return spriteName;
    }

    /**
     * Lấy điểm cơ bản mà người chơi nhận được khi phá gạch.
     *
     * @return điểm cơ bản
     */
    public int getBaseScore() {
        return baseScore;
    }
}
