package Objects.PowerUps;

import Utils.Constants;

/**
 * PowerUpType là một enum định nghĩa tất cả các loại Power-up có thể xuất hiện trong game.
 * Mỗi loại chứa thông tin cấu hình cần thiết, bao gồm tiền tố sprite và xác suất xuất hiện.
 */
public enum PowerUpType {
    // Tên (Prefix, Xác suất xuất hiện)
    CATCH("powerup_catch", 0.15),      // Bắt bóng (Catch)
    DUPLICATE("powerup_duplicate", 0.12),  // Nhân đôi bóng (Duplicate)
    EXPAND("powerup_expand", 0.15),    // Mở rộng Paddle (Expand)
    LASER("powerup_laser", 0.15),      // Laser
    LIFE("powerup_life", 0.05),        // Thêm mạng sống (Life) - Xác suất thấp hơn vì quan trọng
    SLOW("powerup_slow", 0.15),        // Làm chậm bóng (Slow)
    WARP("powerup_warp", 0.01);        // Chuyển cấp (Warp) - Xác suất rất thấp vì mạnh

    // Tiền tố (prefix) được sử dụng để tìm kiếm các file sprite liên quan
    private final String powerupPrefix;
    // Xác suất Power-up này xuất hiện khi một viên gạch rơi ra Power-up
    private final double spawnChance;

    /**
     * Constructor cho PowerUpType.
     *
     * @param powerupPrefix Tiền tố tên file sprite (ví dụ: "powerup_catch").
     * @param spawnChance Xác suất xuất hiện (trọng số) của Power-up này.
     */
    PowerUpType(String powerupPrefix, double spawnChance) {
        this.powerupPrefix = powerupPrefix;
        this.spawnChance = spawnChance;
    }

    /**
     * Tạo đường dẫn file sprite hoàn chỉnh cho một frame animation cụ thể.
     *
     * @param frameNumber Số thứ tự của frame (ví dụ: 0, 1, 2...).
     * @return Chuỗi đường dẫn file sprite (ví dụ: "powerup_catch_2.png").
     */
    public String getFramePath(int frameNumber) {
        return powerupPrefix + "_" + frameNumber + ".png";
    }

    /**
     * Kiểm tra xem Power-up này có phải là loại tức thời (instant) hay không.
     * Power-up tức thời không có thời gian hết hạn (ví dụ: LIFE, WARP).
     *
     * @return true nếu hiệu ứng là tức thời, ngược lại là false.
     */
    public boolean isInstant() {
        return this == LIFE || this == WARP || this == DUPLICATE; // Cập nhật: DUPLICATE cũng là tức thời
    }

    /**
     * Lựa chọn ngẫu nhiên một loại Power-up dựa trên xác suất (trọng số) đã định.
     *
     * @return Một loại PowerUpType được chọn ngẫu nhiên.
     */
    public static PowerUpType randomWeighted() {
        // 1. Tính tổng trọng số (tổng xác suất)
        double totalWeight = 0.0;
        for (PowerUpType type : PowerUpType.values()) {
            totalWeight += type.spawnChance;
        }

        // 2. Chọn một giá trị ngẫu nhiên trong khoảng [0, totalWeight)
        double randomValue = Math.random() * totalWeight;

        // 3. Xác định loại Power-up tương ứng
        double cumulativeWeight = 0.0;
        for (PowerUpType type : PowerUpType.values()) {
            cumulativeWeight += type.spawnChance;
            // Nếu giá trị ngẫu nhiên nằm trong phạm vi trọng số tích lũy của loại này
            if (randomValue <= cumulativeWeight) {
                return type;
            }
        }

        // Trường hợp dự phòng (nên hiếm khi xảy ra nếu logic tính toán trọng số đúng)
        return EXPAND;
    }

    /**
     * Lấy thời gian hiệu lực (duration) của Power-up.
     * Đối với Power-up tức thời, trả về 0L.
     *
     * @return Thời gian hiệu lực tính bằng milliseconds.
     */
    public long getDuration() {
        switch (this) {
            case CATCH:
                return Constants.PowerUps.CATCH_DURATION;
            case EXPAND:
                return Constants.PowerUps.EXPAND_DURATION;
            case LASER:
                return Constants.PowerUps.LASER_DURATION;
            case SLOW:
                return Constants.PowerUps.SLOW_DURATION;
            default:
                // Các loại tức thời (LIFE, DUPLICATE, WARP) có thời gian bằng 0
                return 0L;
        }
    }

    /**
     * Lấy tiền tố sprite của Power-up.
     *
     * @return Tiền tố tên file sprite.
     */
    public String getSpritePrefix() {
        return powerupPrefix;
    }

    /**
     * Lấy xác suất xuất hiện (trọng số) của Power-up.
     *
     * @return Xác suất xuất hiện (double).
     */
    public double getSpawnChance() {
        return spawnChance;
    }
}