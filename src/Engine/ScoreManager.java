package Engine;

import Objects.Bricks.Brick;
import Objects.Bricks.BrickType;
import Utils.Constants;

/**
 * Lớp ScoreManager chịu trách nhiệm quản lý điểm số và hệ số nhân điểm (multiplier)
 * của người chơi trong trò chơi.
 */
public class ScoreManager {
    /** Điểm số hiện tại của người chơi. */
    private int score = 0;
    /** Hệ số nhân điểm hiện tại. Tăng lên mỗi khi phá hủy gạch liên tiếp. */
    private int scoreMultiplier = 1;

    /**
     * Đặt lại điểm số về 0.
     */
    public void resetScore() {
        this.score = 0;
    }

    /**
     * Đặt lại hệ số nhân điểm về giá trị ban đầu (1).
     */
    public void resetMultiplier() {
        this.scoreMultiplier = 1;
    }

    /**
     * Thêm điểm khi một viên gạch bị phá hủy.
     * Điểm được tính dựa trên điểm cơ bản của loại gạch cộng với điểm tăng dần
     * nhân với hệ số nhân hiện tại. Sau đó, hệ số nhân được tăng lên.
     * @param brickType Loại gạch vừa bị phá hủy.
     */
    public void addDestroyBrickScore(BrickType brickType) {
        // Công thức tính điểm khi phá gạch:
        // Điểm = Điểm hiện tại + Điểm cơ bản loại gạch + (Hệ số nhân * Điểm tăng thêm)
        this.score = this.score
                + brickType.getBaseScore()
                + this.scoreMultiplier * Constants.Scoring.SCORE_BRICK_INCREMENT;

        // Tăng hệ số nhân cho lần phá gạch tiếp theo
        this.scoreMultiplier++;
    }

    /**
     * Thêm điểm thưởng khi hoàn thành một vòng chơi (level).
     */
    public void addRoundCompleteScore() {
        this.score = this.score
                + Constants.Scoring.SCORE_LEVEL_COMPLETE_BONUS;
    }

    /**
     * Thêm điểm thưởng mạng sống khi kết thúc game hoặc hoàn thành tất cả các level.
     * @param livesRemaining Số mạng sống còn lại.
     */
    public void addLifeBonusScore(int livesRemaining) {
        // Điểm thưởng = Số mạng còn lại * Điểm thưởng cho mỗi mạng
        this.score = this.score
                + (livesRemaining * Constants.Scoring.SCORE_LIFE_BONUS);
    }

    /**
     * Áp dụng hình phạt điểm khi người chơi mất một mạng.
     * Đồng thời đặt lại hệ số nhân điểm về 1.
     */
    public void applyLoseLifePenalty() {
        // Trừ điểm, đảm bảo điểm số không xuống dưới 0
        this.score = Math.max(0, this.score + Constants.Scoring.SCORE_LOSE_LIFE_PENALTY);
        // Đặt lại hệ số nhân điểm (chuỗi combo bị ngắt)
        resetMultiplier();
    }

    /**
     * Lấy điểm số hiện tại của người chơi.
     * @return Điểm số hiện tại.
     */
    public int getScore() {
        return score;
    }

    /**
     * Lấy hệ số nhân điểm hiện tại.
     * @return Hệ số nhân điểm.
     */
    public int getMultiplier() {
        return scoreMultiplier;
    }

}