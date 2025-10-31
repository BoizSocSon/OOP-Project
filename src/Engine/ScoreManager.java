package Engine;

import Objects.Bricks.BrickType;
import Utils.Constants;

public class ScoreManager {
    private int score = 0;
    private int scoreMultiplier = 1;

    public void resetScore() {
        this.score = 0;
    }

    public void resetMultiplier() {
        this.scoreMultiplier = 1;
    }


    public void addDestroyBrickScore(BrickType brickType) {
        this.score = this.score
                + brickType.getBaseScore()
                + this.scoreMultiplier * Constants.Scoring.SCORE_BRICK_INCREMENT;
        this.scoreMultiplier++;
    }

    public void addRoundCompleteScore() {
        this.score = this.score
                + Constants.Scoring.SCORE_LEVEL_COMPLETE_BONUS;
    }

    public void addLifeBonusScore(int livesRemaining) {
        this.score = this.score
                + (livesRemaining * Constants.Scoring.SCORE_LIFE_BONUS);
    }

    public void applyLoseLifePenalty() {
        this.score = Math.max(0, this.score + Constants.Scoring.SCORE_LOSE_LIFE_PENALTY);
        resetMultiplier();
    }

    public int getScore() {
        return score;
    }

    public int getMultiplier() {
        return scoreMultiplier;
    }
}
