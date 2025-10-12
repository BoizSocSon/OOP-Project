package Objects.Bricks;

/**
 * Enum representing different types of bricks in the game.
 * Special bricks include:
 * - SILVER: Takes 2 hits to destroy
 * - GOLD: Indestructible (999 hit points)
 */
public enum BrickType {
    BLUE(1, "brick_blue.png", 60),
    RED(1, "brick_red.png", 70),
    GREEN(1, "brick_green.png", 80),
    YELLOW(1, "brick_yellow.png", 90),
    ORANGE(1, "brick_orange.png", 100),
    PINK(1, "brick_pink.png", 110),
    CYAN(1, "brick_cyan.png", 120),
    WHITE(1, "brick_white.png", 150),
    SILVER(2, "brick_silver.png", 50),
    GOLD(999, "brick_gold.png", 0);

    private final int hitPoints;
    private final String spriteName;
    private final int baseScore;

    /**
     * Constructor for BrickType enum.
     * @param hitPoints Number of hits required to destroy the brick
     * @param spriteName Name of the sprite file for the brick
     * @param baseScore Base score value when the brick is destroyed
     */
    BrickType(int hitPoints, String spriteName, int baseScore) {
        this.hitPoints = hitPoints;
        this.spriteName = spriteName;
        this.baseScore = baseScore;
    }

    /**
     * Get the number of hits required to destroy this brick.
     * @return Number of hits required
     */
    public int getHitPoints() {
        return hitPoints;
    }

    /**
     * Get the sprite file name for this brick type.
     * @return Sprite file name
     */
    public String getSpriteName() {
        return spriteName;
    }

    /**
     * Get the base score value for destroying this brick.
     * @return Base score value
     */
    public int getBaseScore() {
        return baseScore;
    }

    /**
     * Check if this brick type is breakable.
     * @return true if the brick can be destroyed, false for indestructible bricks (GOLD)
     */
    public boolean isBreakable() {
        return this != GOLD;
    }

    /**
     * Check if this brick type is a special brick (SILVER or GOLD).
     * @return true if this is a special brick, false otherwise
     */
    public boolean isSpecial() {
        return this == SILVER || this == GOLD;
    }
}
