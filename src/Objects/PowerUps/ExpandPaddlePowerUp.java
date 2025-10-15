package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * ExpandPaddlePowerUp - Power-up mở rộng kích thước paddle.
 * 
 * Functionality:
 * - Paddle width tăng lên 1.5x (50% wider)
 * - Dễ dàng hơn trong việc đỡ ball
 * - Paddle sprite thay đổi để hiển thị expanded state
 * - Timed effect: Duration 10 seconds
 * 
 * Mechanics:
 * - applyEffect(): 
 *   1. Store original width
 *   2. Set paddle width = currentWidth * 1.5
 *   3. Update paddle sprite to expanded version
 *   4. Update collision bounds
 * - removeEffect(): 
 *   1. Restore original width
 *   2. Revert sprite to normal
 * 
 * Visual changes:
 * - Normal paddle: 64px width
 * - Expanded paddle: 96px width (1.5x)
 * - Sprite: paddle_wide (stretched horizontally)
 * - Smooth transition animation (optional)
 * 
 * Balance considerations:
 * - Không stack (multiple pickups reset timer to 10s)
 * - Max width = 1.5x (even if collected multiple times)
 * - If paddle already expanded: timer resets only
 * 
 * Edge cases:
 * - Paddle at screen edge: Width expansion stays within bounds
 * - Expansion centers on paddle (extends equally both sides)
 * - Ball on paddle when expanding: Position adjusts proportionally
 * 
 * @author SteveHoang aka BoizSocSon
 */
public class ExpandPaddlePowerUp extends PowerUp {
    
    /**
     * Creates an ExpandPaddlePowerUp at the specified position.
     * 
     * @param x X-coordinate (center of destroyed brick)
     * @param y Y-coordinate (center of destroyed brick)
     */
    public ExpandPaddlePowerUp(double x, double y) {
        super(x, y, PowerUpType.EXPAND);
    }
    
    /**
     * Applies the expand effect to the paddle.
     * 
     * Actions:
     * 1. Get paddle from GameManager
     * 2. Calculate new width = currentWidth * 1.5
     * 3. Store original width for later restoration
     * 4. Apply new width to paddle
     * 5. Center expanded paddle at current position
     * 6. Change sprite to expanded version (paddle_wide)
     * 7. Update collision bounds
     * 8. Effect duration: 10 seconds
     * 
     * Implementation details:
     * - Expansion centers on paddle's current X position
     * - Formula: newX = currentX - (newWidth - oldWidth) / 2
     * - Collision rectangle automatically updates with new bounds
     * - Sprite scaling handled by SpriteRenderer
     * 
     * Multiple pickups behavior:
     * - If already expanded: Reset timer to 10s (no further expansion)
     * - Maximum expansion = 1.5x original size
     * - Timer in PowerUpManager handles expiry
     * 
     * @param gameManager The game manager to apply effects to
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("ExpandPaddlePowerUp: GameManager is null, cannot apply effect");
            return;
        }
        
        // Expand paddle width through GameManager
//        gameManager.expandPaddle(Constants.PowerUps.EXPAND_MULTIPLIER);
        gameManager.expandPaddle();

        System.out.println("ExpandPaddlePowerUp: Paddle expanded to " + 
                         (Constants.PowerUps.EXPAND_MULTIPLIER * 100) + "% for " +
                         Constants.PowerUps.EXPAND_DURATION / 1000.0 + " seconds");
    }
    
    /**
     * Removes the expand effect when expired.
     * 
     * Actions:
     * 1. Get paddle from GameManager
     * 2. Restore original width
     * 3. Re-center paddle at current position
     * 4. Revert sprite to normal paddle
     * 5. Update collision bounds
     * 
     * Edge cases:
     * - If paddle at screen edge: Ensure it stays within bounds after shrinking
     * - If ball on paddle: Maintain relative position
     * - Smooth transition (optional shrink animation)
     * 
     * Implementation notes:
     * - Original width stored in Paddle instance
     * - GameManager coordinates with Paddle to revert
     * - Timer automatically triggers after 10 seconds
     * 
     * @param gameManager The game manager to remove effects from
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("ExpandPaddlePowerUp: GameManager is null, cannot remove effect");
            return;
        }
        
        // Revert paddle to original width
        gameManager.revertPaddleSize();
        
        System.out.println("ExpandPaddlePowerUp: Paddle reverted to normal size (expired)");
    }
}
