package Objects.Bricks;

import Utils.Constants;

/**
 * <p>Đại diện cho các loại gạch (brick) khác nhau trong trò chơi.
 * Mỗi loại gạch có các thuộc tính riêng biệt như
 * **điểm máu (hit points)**, **tên sprite (hình ảnh)**, và **điểm cơ bản (base score)**.</p>
 */
public enum BrickType {
    // Các loại gạch thông thường, chỉ cần 1 điểm va chạm để phá vỡ (hitPoints = 1)

    /** Gạch màu Xanh dương. */
    BLUE(1, "brick_blue", Constants.Scoring.SCORE_BRICK_BASE + 10),
    /** Gạch màu Đỏ. */
    RED(1, "brick_red", Constants.Scoring.SCORE_BRICK_BASE + 20),
    /** Gạch màu Xanh lá. */
    GREEN(1, "brick_green", Constants.Scoring.SCORE_BRICK_BASE + 30),
    /** Gạch màu Vàng. */
    YELLOW(1, "brick_yellow", Constants.Scoring.SCORE_BRICK_BASE + 40),
    /** Gạch màu Cam. */
    ORANGE(1, "brick_orange", Constants.Scoring.SCORE_BRICK_BASE + 50),
    /** Gạch màu Hồng. */
    PINK(1, "brick_pink", Constants.Scoring.SCORE_BRICK_BASE + 60),
    /** Gạch màu Xanh ngọc (Cyan). */
    CYAN(1, "brick_cyan", Constants.Scoring.SCORE_BRICK_BASE + 70),
    /** Gạch màu Trắng. */
    WHITE(1, "brick_white", Constants.Scoring.SCORE_BRICK_BASE + 80),

    // Các loại gạch đặc biệt

    /** * Gạch Bạc. Cần 2 điểm va chạm (hitPoints = 2) để phá vỡ.
     * Điểm thưởng bằng điểm cơ bản của gạch.
     */
    SILVER(2, "brick_silver", Constants.Scoring.SCORE_BRICK_BASE),

    /** * Gạch Vàng. Gần như không thể phá vỡ (hitPoints = 999),
     * thường được dùng để tạo biên hoặc chướng ngại vật cố định.
     * Điểm thưởng bằng điểm cơ bản của gạch.
     */
    GOLD(999, "brick_gold", Constants.Scoring.SCORE_BRICK_BASE + 0);

    /** Điểm máu (số lần va chạm) cần thiết để phá hủy gạch. */
    private final int hitPoints;

    /** Tên của tài nguyên hình ảnh (sprite) đại diện cho gạch. */
    private final String spriteName;

    /** Điểm cơ bản mà người chơi nhận được khi phá hủy gạch. */
    private final int baseScore;

    /**
     * <p>Constructor cho enum BrickType.</p>
     *
     * @param hitPoints Số lần va chạm tối thiểu cần thiết để phá hủy gạch.
     * @param spriteName Tên tệp hình ảnh (sprite) của gạch.
     * @param baseScore Điểm thưởng cơ bản khi phá hủy gạch.
     */
    BrickType(int hitPoints, String spriteName, int baseScore) {
        this.hitPoints = hitPoints;
        this.spriteName = spriteName;
        this.baseScore = baseScore;
    }

    /**
     * <p>Trả về số điểm máu (hit points) của loại gạch.</p>
     *
     * @return Số lần va chạm cần thiết để phá hủy gạch.
     */
    public int getHitPoints() {
        return hitPoints;
    }

    /**
     * <p>Trả về tên sprite (hình ảnh) của loại gạch.</p>
     *
     * @return Tên tệp hình ảnh của gạch.
     */
    public String getSpriteName() {
        return spriteName;
    }

    /**
     * <p>Trả về điểm cơ bản (base score) của loại gạch.</p>
     *
     * @return Điểm thưởng khi gạch bị phá hủy.
     */
    public int getBaseScore() {
        return baseScore;
    }
}