package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * SlowBallPowerUp - Power-up làm chậm tốc độ ball.
 * 
 * Functionality:
 * - Reduces ball speed by 30% (multiplier 0.7x)
 * - Gives player more time to react
 * - Minimum speed: 1.5px/frame (prevents too slow)
 * - Can stack multiple times (cumulative slow)
 * - Timed effect: Duration 8 seconds
 * 
 * Mechanics:
 * - applyEffect():
 *   1. Get all active balls
 *   2. For each ball:
 *      - currentSpeed *= 0.7
 *      - Check minimum speed (1.5px/frame)
 *      - If below minimum: clamp to 1.5
 *   3. Store original speeds for restoration
 * - removeEffect():
 *   1. Restore original speed for all balls
 *   2. If multiple slows were active: Remove 1 layer
 * 
 * Stacking behavior:
 * - Slow #1: Speed = 100% → 70%
 * - Slow #2: Speed = 70% → 49%
 * - Slow #3: Speed = 49% → 34%
 * - Each slow has independent 8s timer
 * - When one expires: Reverse its multiplier (÷ 0.7 = × 1.43)
 * 
 * Visual:
 * - Animation: slow_powerup (8 frames, blue color theme)
 * - Ball effect: Blue trail/glow when slowed
 * - HUD indicator: "SLOW" text with countdown timer
 * 
 * Balance considerations:
 * - Minimum speed prevents game from being trivial
 * - 8 second duration shorter than other timed effects
 * - Stacking allows cumulative slowdown but capped by minimum
 * - Very helpful for difficult levels/patterns
 * 
 * @author SteveHoang aka BoizSocSon
 */
public class SlowBallPowerUp extends PowerUp {
    
    /**
     * Creates a SlowBallPowerUp at the specified position.
     * 
     * @param x X-coordinate (center of destroyed brick)
     * @param y Y-coordinate (center of destroyed brick)
     */
    public SlowBallPowerUp(double x, double y) {
        super(x, y, PowerUpType.SLOW);
    }
    
    /**
     * Applies the slow effect to all balls.
     * 
     * Actions:
     * 1. Get all active balls from GameManager
     * 2. For each ball:
     *    a. Get current speed (magnitude of velocity)
     *    b. Calculate new speed = currentSpeed * 0.7
     *    c. Check minimum speed threshold (1.5px/frame)
     *    d. If newSpeed < minimum: Clamp to minimum
     *    e. Apply new speed to ball (maintain direction)
     * 3. Store ball states for reversal
     * 4. Effect duration: 8 seconds
     * 
     * Speed calculation:
     * - Current velocity = (vx, vy)
     * - Current speed = sqrt(vx² + vy²)
     * - New speed = currentSpeed * 0.7
     * - New velocity = (vx, vy) * (newSpeed / currentSpeed)
     * 
     * Minimum speed enforcement:
     * - Prevents ball from becoming too slow
     * - MIN_SPEED = 1.5px/frame
     * - If calculated speed < MIN_SPEED: Use MIN_SPEED
     * - Direction (angle) always preserved
     * 
     * Stacking behavior:
     * - Multiple slow effects multiply: 0.7 × 0.7 = 0.49
     * - Each effect has independent timer
     * - PowerUpManager tracks multiple SLOW effects
     * - Expiry: Each slow removes its layer
     * 
     * Implementation details:
     * - GameManager delegates to Ball.multiplySpeed(0.7)
     * - Ball stores speed history for proper restoration
     * - Direction vector normalized and rescaled
     * 
     * @param gameManager The game manager to apply effects to
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("SlowBallPowerUp: GameManager is null, cannot apply effect");
            return;
        }
        
        // Slow all balls by multiplier (0.7x = 30% reduction)
        gameManager.slowBalls(Constants.PowerUps.SLOW_MULTIPLIER);
        
        System.out.println("SlowBallPowerUp: Balls slowed to " + 
                         (Constants.PowerUps.SLOW_MULTIPLIER * 100) + "% speed for " +
                         Constants.PowerUps.SLOW_DURATION / 1000.0 + " seconds");
    }
    
    /**
     * Removes the slow effect when expired.
     * 
     * Actions:
     * 1. Get all active balls
     * 2. Reverse the slow multiplier (÷ 0.7 = × 1.428)
     * 3. For each ball:
     *    a. currentSpeed ÷= 0.7 (restore previous speed)
     *    b. Check maximum speed cap (if exists)
     *    c. Apply restored speed
     * 4. Effect automatically expires after 8 seconds
     * 
     * Stacking reversal:
     * - If 2 slows active: Removing 1 leaves 1 slow still active
     * - If 1 slow active: Removing it restores original speed
     * - Formula: newSpeed = currentSpeed / 0.7
     * 
     * Edge cases:
     * - Ball destroyed before expiry: No action needed
     * - New balls spawned during slow: Inherit current slow state
     * - All balls gone when expired: No error (safe check)
     * 
     * Implementation notes:
     * - GameManager tracks slow stack count
     * - Each expiry decrements count
     * - When count = 0: Full speed restored
     * 
     * @param gameManager The game manager to remove effects from
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("SlowBallPowerUp: GameManager is null, cannot remove effect");
            return;
        }
        
        // Restore ball speed (divide by slow multiplier)
        gameManager.restoreBallSpeed();
        
        System.out.println("SlowBallPowerUp: Ball speed restored (slow expired)");
    }
}
