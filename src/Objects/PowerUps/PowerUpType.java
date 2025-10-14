package Objects.PowerUps;

import Utils.Constants;

/**
 * Enum định nghĩa tất cả loại power-ups với metadata.
 *
 * Thay vì dùng literals cho thời lượng, enum này tham chiếu tới các hằng số trong
 * `Utils.Constants.PowerUps` để đảm bảo nhất quán khi cần điều chỉnh các thông số game.
 */
public enum PowerUpType {
    CATCH("powerup_catch", 0.15, Constants.PowerUps.CATCH_DURATION),
    DUPLICATE("powerup_duplicate", 0.12, 0L),
    EXPAND("powerup_expand", 0.15, Constants.PowerUps.EXPAND_DURATION),
    LASER("powerup_laser", 0.15, Constants.PowerUps.LASER_DURATION),
    LIFE("powerup_life", 0.05, 0L),
    SLOW("powerup_slow", 0.15, Constants.PowerUps.SLOW_DURATION),
    WARP("powerup_warp", 0.03, 0L);

    private final String spritePrefix;
    private final double spawnChance;
    private final long duration;

    PowerUpType(String spritePrefix, double spawnChance, long duration) {
        this.spritePrefix = spritePrefix;
        this.spawnChance = spawnChance;
        this.duration = duration;
    }

    // Getters
    public String getSpritePrefix() {
        return spritePrefix;
    }
    public double getSpawnChance() {
        return spawnChance;
    }
    public long getDuration() {
        return duration;
    }

    /**
     * @return Path pattern cho animation frames
     * Example: "powerup_catch" → "powerup_catch_1.png" ... "powerup_catch_8.png"
     */
    public String getFramePath(int frameNumber) {
        return spritePrefix + "_" + frameNumber + ".png";
    }

    /**
     * @return Có phải instant effect không (duration = 0)
     */
    public boolean isInstant() {
        return duration == 0L;
    }

    /**
     * Random weighted powerup type dựa trên spawn chances.
     * @return Random PowerUpType
     */
    public static PowerUpType randomWeighted() {
        double totalWeight = 0.0;
        for (PowerUpType type : values()) totalWeight += type.spawnChance;

        double r = Math.random() * totalWeight;
        double cumulative = 0.0;
        for (PowerUpType type : values()) {
            cumulative += type.spawnChance;
            if (r <= cumulative) return type;
        }
        return EXPAND; // fallback
    }
}