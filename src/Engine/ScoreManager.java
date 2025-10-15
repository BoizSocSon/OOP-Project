package Engine;

/**
 * ScoreManager - Manages scoring and score multipliers.
 * 
 * Responsibilities:
 * - Track current score
 * - Calculate score increments with multipliers
 * - Handle penalties (lose life, etc.)
 * - Track high scores
 * 
 * @author SteveHoang aka BoizSocSon
 */
public class ScoreManager {
    private int score;
    private int scoreMultiplier;
    private int consecutiveHits;
    
    public ScoreManager() {
        reset();
    }
    
    /**
     * Resets score to zero.
     */
    public void reset() {
        this.score = 0;
        this.scoreMultiplier = 1;
        this.consecutiveHits = 0;
    }
    
    /**
     * Adds points to score.
     * @param points Points to add (can be negative for penalties)
     */
    public void addScore(int points) {
        if (points > 0) {
            // Apply multiplier for positive scores
            score += points * scoreMultiplier;
            consecutiveHits++;
            
            // Increase multiplier every 10 consecutive hits
            if (consecutiveHits % 10 == 0) {
                scoreMultiplier = Math.min(scoreMultiplier + 1, 5); // Max 5x
            }
        } else {
            // Penalty - no multiplier
            score = Math.max(0, score + points);
            resetMultiplier();
        }
    }
    
    /**
     * Resets score multiplier.
     */
    public void resetMultiplier() {
        scoreMultiplier = 1;
        consecutiveHits = 0;
    }
    
    /**
     * Gets current score.
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Gets current multiplier.
     */
    public int getMultiplier() {
        return scoreMultiplier;
    }
    
    /**
     * Gets consecutive hits count.
     */
    public int getConsecutiveHits() {
        return consecutiveHits;
    }
}
