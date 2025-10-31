package Objects.PowerUps;

import Utils.Constants;

public enum PowerUpType {
    CATCH("powerup_catch", 0.15),
    DUPLICATE("powerup_duplicate", 0.12),
    EXPAND("powerup_expand", 0.15),
    LASER("powerup_laser", 0.15),
    LIFE("powerup_life", 0.05),
    SLOW("powerup_slow", 0.15),
    WARP("powerup_warp", 0.01);


    private final String powerupPrefix;
    private final double spawnChance;

    PowerUpType(String powerupPrefix, double spawnChance) {
        this.powerupPrefix = powerupPrefix;
        this.spawnChance = spawnChance;
    }

    public String getFramePath(int frameNumber) {
        return powerupPrefix + "_" + frameNumber + ".png";
    }

    public boolean isInstant() {
        return this == LIFE || this == WARP;
    }

    public static PowerUpType randomWeighted() {
        double totalWeight = 0.0;
        for (PowerUpType type : PowerUpType.values()) {
            totalWeight += type.spawnChance;
        }

        double randomValue = Math.random() * totalWeight;
        double cumulativeWeight = 0.0;
        for (PowerUpType type : PowerUpType.values()) {
            cumulativeWeight += type.spawnChance;
            if (randomValue <= cumulativeWeight) {
                return type;
            }
        }

        return EXPAND;
    }

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
                return 0L;
        }
    }
    public String getSpritePrefix() {
        return powerupPrefix;
    }
    public double getSpawnChance() {
        return spawnChance;
    }
}
