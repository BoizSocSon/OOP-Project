package Engine;

import Objects.Bricks.BrickType;
import Utils.Constants;

/**
 * Lớp quản lý điểm số (ScoreManager) chịu trách nhiệm tính toán, cập nhật,
 * và theo dõi điểm số cũng như hệ số nhân điểm (multiplier) của người chơi.
 */
public class ScoreManager {
    private int score = 0; // Điểm số hiện tại của người chơi.
    private int scoreMultiplier = 1; // Hệ số nhân điểm (bắt đầu từ 1).

    /**
     * Đặt lại điểm số về 0.
     */
    public void resetScore() {
        this.score = 0;
    }

    /**
     * Đặt lại hệ số nhân điểm về 1.
     */
    public void resetMultiplier() {
        this.scoreMultiplier = 1;
    }


    /**
     * Cộng điểm khi phá hủy một viên gạch.
     * Điểm được tính dựa trên điểm cơ bản của gạch cộng với điểm thưởng từ hệ số nhân.
     * Sau khi cộng điểm, hệ số nhân sẽ tăng thêm 1.
     *
     * @param brickType Loại gạch bị phá hủy.
     */
    public void addDestroyBrickScore(BrickType brickType) {
        // Cộng điểm: Điểm cơ bản + (Hệ số nhân * Điểm tăng thêm)
        this.score = this.score
                + brickType.getBaseScore()
                + this.scoreMultiplier * Constants.Scoring.SCORE_BRICK_INCREMENT;
        // Tăng hệ số nhân điểm cho lần phá gạch tiếp theo.
        this.scoreMultiplier++;
    }

    /**
     * Cộng điểm thưởng khi hoàn thành một vòng chơi (Level Complete Bonus).
     */
    public void addRoundCompleteScore() {
        this.score = this.score
                + Constants.Scoring.SCORE_LEVEL_COMPLETE_BONUS;
    }

    /**
     * Cộng điểm thưởng dựa trên số mạng còn lại khi hoàn thành cấp độ.
     *
     * @param livesRemaining Số mạng còn lại của người chơi.
     */
    public void addLifeBonusScore(int livesRemaining) {
        // Cộng điểm: Số mạng còn lại * Điểm thưởng cho mỗi mạng.
        this.score = this.score
                + (livesRemaining * Constants.Scoring.SCORE_LIFE_BONUS);
    }

    /**
     * Áp dụng điểm phạt khi người chơi mất mạng.
     * Điểm số không được phép nhỏ hơn 0.
     */
    public void applyLoseLifePenalty() {
        // Trừ điểm phạt và đảm bảo điểm không nhỏ hơn 0.
        this.score = Math.max(0, this.score + Constants.Scoring.SCORE_LOSE_LIFE_PENALTY);
        // Đặt lại hệ số nhân điểm sau khi mất mạng.
        resetMultiplier();
    }

    /**
     * Lấy điểm số hiện tại.
     *
     * @return Điểm số hiện tại.
     */
    public int getScore() {
        return score;
    }

    /**
     * Lấy hệ số nhân điểm hiện tại.
     *
     * @return Hệ số nhân điểm.
     */
    public int getMultiplier() {
        return scoreMultiplier;
    }
}