package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * LifePowerUp - Power-up thêm 1 mạng cho player.
 * 
 * Functionality:
 * - Adds 1 extra life to player's life count
 * - Maximum 5 lives (prevents infinite stacking)
 * - Most valuable powerup (spawn rate only 5%)
 * - Instant effect: No duration, applies immediately
 * 
 * Mechanics:
 * - applyEffect():
 *   1. Check current life count
 *   2. If < 5: Add 1 life
 *   3. If = 5: Ignore (already at max)
 *   4. Update HUD display
 *   5. Play life_gain sound effect
 * - removeEffect(): Not applicable (instant)
 * 
 * Visual:
 * - Animation: life_powerup (8 frames, green/heart theme)
 * - Collection effect: Green sparkle + heart icon float
 * - HUD update: Life counter increases with animation
 * 
 * Balance considerations:
 * - Rarest powerup (5% spawn chance)
 * - Maximum 5 lives prevents exploitation
 * - Strategic value: Players prioritize this over others
 * - No penalty for collecting at max lives (just ignored)
 * 
 * Game design rationale:
 * - Gives players comeback opportunity
 * - Rewards aggressive brick destruction
 * - Encourages risk-taking to collect powerups
 * - Max cap prevents game becoming too easy
 * 
 * @author SteveHoang aka BoizSocSon
 */
public class LifePowerUp extends PowerUp {
    
    /**
     * Creates a LifePowerUp at the specified position.
     * 
     * @param x X-coordinate (center of destroyed brick)
     * @param y Y-coordinate (center of destroyed brick)
     */
    public LifePowerUp(double x, double y) {
        super(x, y, PowerUpType.LIFE);
    }
    
    /**
     * Applies the life effect to the player.
     * 
     * Actions:
     * 1. Get current life count from GameManager
     * 2. Check if lives < MAX_LIVES (5)
     * 3. If yes:
     *    - Increment life counter
     *    - Update HUD display (heart icons)
     *    - Play "1UP" sound effect
     *    - Show "+1 LIFE" text effect
     * 4. If no:
     *    - Log "Max lives reached"
     *    - Still collect powerup (no effect)
     *    - Optional: Show "MAX" text effect
     * 
     * Implementation details:
     * - Lives stored in GameManager state
     * - HUD observes life changes and updates display
     * - Sound effect: "1up.wav" (classic arcade sound)
     * - Text effect: Green floating text "+1 LIFE"
     * 
     * Edge cases:
     * - Lives = 5 before collection: No change (capped)
     * - Lives = 0 (game over state): Should not spawn
     * - Multiple life powerups: Each adds 1 until cap
     * 
     * Strategic value:
     * - Most important powerup to collect
     * - Players should take risks to get it
     * - Can save a difficult run
     * 
     * @param gameManager The game manager to apply effects to
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("LifePowerUp: GameManager is null, cannot apply effect");
            return;
        }
        
        // Add 1 life (capped at MAX_LIVES)
        int livesBeforeAdd = gameManager.getLives();
        gameManager.addLife();
        int livesAfter = gameManager.getLives();
        
        if (livesAfter > livesBeforeAdd) {
            System.out.println("LifePowerUp: Life added! Lives: " + 
                             livesBeforeAdd + " → " + livesAfter);
        } else {
            System.out.println("LifePowerUp: Max lives reached (" + 
                             Constants.GameRules.MAX_LIVES + "), no effect");
        }
    }
    
    /**
     * Removes the life effect (not applicable for instant effects).
     * 
     * LifePowerUp is a permanent instant effect that doesn't need removal.
     * Once a life is added:
     * - It persists until player loses it (ball falls off screen)
     * - No expiry timer
     * - No revert mechanism
     * 
     * Lives can only be lost by:
     * - Ball falling off bottom of screen
     * - All balls lost in multi-ball situations
     * - Game over when lives reach 0
     * 
     * @param gameManager The game manager (not used)
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        // Instant permanent effect - no removal needed
        // Lives persist until lost through normal gameplay
    }
}
