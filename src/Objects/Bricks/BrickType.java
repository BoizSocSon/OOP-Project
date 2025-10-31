package Objects.Bricks;

import Utils.Constants;

public enum BrickType {
    BLUE(1, "brick_blue", Constants.Scoring.SCORE_BRICK_BASE + 10),
    RED(1, "brick_red", Constants.Scoring.SCORE_BRICK_BASE + 20),
    GREEN(1, "brick_green", Constants.Scoring.SCORE_BRICK_BASE + 30),
    YELLOW(1, "brick_yellow", Constants.Scoring.SCORE_BRICK_BASE + 40),
    ORANGE(1, "brick_orange", Constants.Scoring.SCORE_BRICK_BASE + 50),
    PINK(1, "brick_pink", Constants.Scoring.SCORE_BRICK_BASE + 60),
    CYAN(1, "brick_cyan", Constants.Scoring.SCORE_BRICK_BASE + 70),
    WHITE(1, "brick_white", Constants.Scoring.SCORE_BRICK_BASE + 80),
    SILVER(2, "brick_silver", Constants.Scoring.SCORE_BRICK_BASE),
    GOLD(999, "brick_gold", Constants.Scoring.SCORE_BRICK_BASE + 0);

    private final int hitPoints;
    private final String spriteName;
    private final int baseScore;

    BrickType(int hitPoints, String spriteName, int baseScore) {
        this.hitPoints = hitPoints;
        this.spriteName = spriteName;
        this.baseScore = baseScore;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public String getSpriteName() {
        return spriteName;
    }

    public int getBaseScore() {
        return baseScore;
    }
}
