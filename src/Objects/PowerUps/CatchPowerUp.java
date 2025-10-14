package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * CatchPowerUp - Power-up cho phép paddle "bắt" ball khi chạm.
 * 
 * Functionality:
 * - Ball sẽ dính vào paddle khi chạm thay vì bounce
 * - Player nhấn SPACE để launch ball ra với góc mới
 * - Cho phép player kiểm soát hướng bắn tốt hơn
 * - Timed effect: Duration 15 seconds
 * 
 * Mechanics:
 * - applyEffect(): Enable catch mode trên paddle
 * - Ball velocity = 0 khi caught
 * - Ball position locked relative to paddle
 * - Space key → release ball với góc tùy vị trí trên paddle
 * - removeEffect(): Disable catch mode sau 15s
 * 
 * Visual:
 * - Animation: catch_powerup (8 frames, cyan color theme)
 * - Paddle sprite changes to catch_paddle khi active
 * 
 * @author SteveHoang aka BoizSocSon
 */
public class CatchPowerUp extends PowerUp {
    
    /**
     * Creates a CatchPowerUp at the specified position.
     * 
     * @param x X-coordinate (center of destroyed brick)
     * @param y Y-coordinate (center of destroyed brick)
     */
    public CatchPowerUp(double x, double y) {
        super(x, y, PowerUpType.CATCH);
    }
    
    /**
     * Applies the catch effect to the game.
     * 
     * Actions:
     * 1. Enable catch mode on paddle
     * 2. Paddle sprite changes to show catch indicators
     * 3. Next ball collision will trigger catch mechanism
     * 4. Effect duration: 15 seconds (defined in Constants)
     * 
     * Implementation details:
     * - GameManager delegates to paddle.enableCatchMode()
     * - Paddle stores catch state and handles ball sticking logic
     * - Ball collision detection checks paddle.isCatchModeActive()
     * - Caught ball position updates relative to paddle movement
     * 
     * @param gameManager The game manager to apply effects to
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("CatchPowerUp: GameManager is null, cannot apply effect");
            return;
        }
        
        // Enable catch mode on paddle through GameManager
        gameManager.enableCatchMode();
        
        System.out.println("CatchPowerUp: Catch mode enabled for " + 
                         Constants.PowerUps.CATCH_DURATION / 1000.0 + " seconds");
    }
    
    /**
     * Removes the catch effect when expired.
     * 
     * Actions:
     * 1. Disable catch mode on paddle
     * 2. Revert paddle sprite to normal
     * 3. Any currently caught ball is released
     * 4. Effect automatically expires after 15 seconds
     * 
     * Edge cases:
     * - If ball is caught when effect expires, ball is released with default angle
     * - Multiple catch powerups don't stack (timer resets to 15s)
     * 
     * @param gameManager The game manager to remove effects from
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("CatchPowerUp: GameManager is null, cannot remove effect");
            return;
        }
        
        // Disable catch mode through GameManager
        gameManager.disableCatchMode();
        
        System.out.println("CatchPowerUp: Catch mode disabled (expired)");
    }
}
