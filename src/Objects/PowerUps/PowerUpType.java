package Objects.PowerUps;

import Utils.Constants;

/**
 * <p>Enum đại diện cho các **Loại PowerUp** khác nhau trong trò chơi.
 * Mỗi loại PowerUp được xác định bởi tiền tố tên sprite và xác suất xuất hiện (spawn chance).</p>
 */
public enum PowerUpType {

    // Các loại PowerUp và xác suất xuất hiện tương ứng

    /** PowerUp Bắt bóng (Catch Mode). Xác suất xuất hiện: 15%. */
    CATCH("powerup_catch", 0.15),

    /** PowerUp Nhân bản bóng (Duplicate Balls). Xác suất xuất hiện: 12%. */
    DUPLICATE("powerup_duplicate", 0.12),

    /** PowerUp Mở rộng thanh đỡ (Expand Paddle). Xác suất xuất hiện: 15%. */
    EXPAND("powerup_expand", 0.15),

    /** PowerUp Laser (Laser Paddle). Xác suất xuất hiện: 15%. */
    LASER("powerup_laser", 0.15),

    /** PowerUp Cộng mạng (Extra Life). Xác suất xuất hiện: 5% (thấp nhất). */
    LIFE("powerup_life", 0.05),

    /** PowerUp Làm chậm bóng (Slow Balls). Xác suất xuất hiện: 15%. */
    SLOW("powerup_slow", 0.15),

    /** PowerUp Dịch chuyển tức thời (Warp/Next Level - Hiếm). Xác suất xuất hiện: 1% (rất hiếm). */
    WARP("powerup_warp", 0.01);


    /** Tiền tố tên sprite (ví dụ: "powerup_catch") được sử dụng để tải hoạt ảnh. */
    private final String powerupPrefix;

    /** Xác suất xuất hiện (từ 0.0 đến 1.0) của PowerUp này khi gạch bị phá hủy. */
    private final double spawnChance;

    /**
     * <p>Constructor cho enum PowerUpType.</p>
     *
     * @param powerupPrefix Tiền tố tên sprite.
     * @param spawnChance Xác suất xuất hiện (từ 0.0 đến 1.0).
     */
    PowerUpType(String powerupPrefix, double spawnChance) {
        this.powerupPrefix = powerupPrefix;
        this.spawnChance = spawnChance;
    }

    /**
     * <p>Tạo đường dẫn tệp hình ảnh (sprite path) cho một khung hình hoạt ảnh cụ thể.</p>
     *
     * @param frameNumber Số thứ tự của khung hình.
     * @return Chuỗi đường dẫn tệp sprite (ví dụ: "powerup_catch_1.png").
     */
    public String getFramePath(int frameNumber) {
        return powerupPrefix + "_" + frameNumber + ".png";
    }

    /**
     * <p>Kiểm tra xem PowerUp này có phải là hiệu ứng **tức thời** (Instant Effect) hay không.</p>
     * <p>Các hiệu ứng tức thời không cần quản lý thời gian hết hạn (ví dụ: LIFE, WARP).</p>
     *
     * @return {@code true} nếu là LIFE hoặc WARP, ngược lại {@code false}.
     */
    public boolean isInstant() {
        return this == LIFE || this == WARP;
    }

    /**
     * <p>Chọn ngẫu nhiên một loại PowerUp dựa trên **trọng số xác suất xuất hiện** (spawnChance).</p>
     *
     * @return Một {@link PowerUpType} được chọn ngẫu nhiên.
     */
    public static PowerUpType randomWeighted() {
        double totalWeight = 0.0;
        // Tính tổng trọng số (tổng xác suất)
        for (PowerUpType type : PowerUpType.values()) {
            totalWeight += type.spawnChance;
        }

        // Tạo giá trị ngẫu nhiên trong phạm vi tổng trọng số
        double randomValue = Math.random() * totalWeight;
        double cumulativeWeight = 0.0;
        // Lặp qua các loại và chọn loại tương ứng với trọng số tích lũy
        for (PowerUpType type : PowerUpType.values()) {
            cumulativeWeight += type.spawnChance;
            if (randomValue <= cumulativeWeight) {
                return type;
            }
        }

        // Trường hợp dự phòng (thường không xảy ra nếu tổng trọng số đúng)
        return EXPAND;

//        //Only return duplicate and catch for testing purposes
//        // Viết mã để spawn 50% duplicate và 50% catch // Chú thích cũ giữa nguyên
//        if (Math.random() < 0.5) {
//            return EXPAND;
//        } else {
//            return LASER;
//        }
//
//        return LASER;
    }

    /**
     * <p>Trả về thời gian kéo dài (duration) của hiệu ứng PowerUp (tính bằng milliseconds).</p>
     *
     * @return Thời gian kéo dài của hiệu ứng (0L nếu là hiệu ứng tức thời).
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
                // Hiệu ứng tức thời (Instant effect) hoặc không có thời gian
                return 0L;
        }
    }

    /**
     * <p>Trả về tiền tố tên sprite (giống với {@link #powerupPrefix}).</p>
     *
     * @return Chuỗi tiền tố sprite.
     */
    public String getSpritePrefix() {
        return powerupPrefix;
    }

    /**
     * <p>Trả về xác suất xuất hiện của PowerUp này.</p>
     *
     * @return Giá trị xác suất (double).
     */
    public double getSpawnChance() {
        return spawnChance;
    }
}