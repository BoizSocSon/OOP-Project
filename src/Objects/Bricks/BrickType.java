package Objects.Bricks;

import Utils.Constants;

/**
 * Enum representing different types of bricks in the game.
 * Special bricks include:
 * - SILVER: Takes 2 hits to destroy
 * - GOLD: Indestructible (999 hit points)
 */
public enum BrickType {
    BLUE(1, "brick_blue.png", Constants.Scoring.SCORE_BRICK_BASE + 10),
    RED(1, "brick_red.png", Constants.Scoring.SCORE_BRICK_BASE + 20),
    GREEN(1, "brick_green.png", Constants.Scoring.SCORE_BRICK_BASE + 30),
    YELLOW(1, "brick_yellow.png", Constants.Scoring.SCORE_BRICK_BASE + 40),
    ORANGE(1, "brick_orange.png", Constants.Scoring.SCORE_BRICK_BASE + 50),
    PINK(1, "brick_pink.png", Constants.Scoring.SCORE_BRICK_BASE + 60),
    CYAN(1, "brick_cyan.png", Constants.Scoring.SCORE_BRICK_BASE + 70),
    WHITE(1, "brick_white.png", Constants.Scoring.SCORE_BRICK_BASE + 80),
    SILVER(2, "brick_silver.png", Constants.Scoring.SCORE_BRICK_BASE),
    GOLD(999, "brick_gold.png", Constants.Scoring.SCORE_BRICK_BASE + 0);

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
