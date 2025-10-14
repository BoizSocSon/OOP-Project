package Objects.PowerUps;

import Engine.GameManager;

/**
 * WarpPowerUp - Power-up skip level hiện tại và warp tới level tiếp theo.
 * 
 * Functionality:
 * - Instantly complete current level
 * - Advance to next round without destroying all bricks
 * - Keep current score and lives
 * - Rarest powerup (3% spawn chance)
 * - Instant effect: No duration
 * 
 * Mechanics:
 * - applyEffect():
 *   1. Mark current round as completed
 *   2. Keep current score (no reset)
 *   3. Keep current lives (no reset)
 *   4. Keep current powerup effects (CATCH, EXPAND, etc.)
 *   5. Trigger level transition animation
 *   6. Load next round
 *   7. Reset paddle/ball positions
 * - removeEffect(): Not applicable (instant)
 * 
 * Level progression:
 * - Round 1 → Round 2
 * - Round 2 → Round 3
 * - Round 3 → Win screen (game complete)
 * - Score bonus: None (you skip scoring from remaining bricks)
 * 
 * Visual:
 * - Animation: warp_powerup (8 frames, purple/portal theme)
 * - Collection effect: Screen warp/distortion
 * - Transition: Fade out → Load next level → Fade in
 * - Portal sound effect
 * 
 * Balance considerations:
 * - Extremely rare (3% spawn chance)
 * - High risk/reward: Skip bricks = skip points
 * - Useful if stuck on hard level
 * - Strategic decision: Collect or ignore for score?
 * - Cannot warp beyond last level (shows win screen)
 * 
 * Game design rationale:
 * - Provides escape from unwinnable situations
 * - Adds strategic choice (points vs progression)
 * - Exciting "oh wow" moment when found
 * - Rarity makes it special
 * 
 * @author SteveHoang aka BoizSocSon
 */
public class WarpPowerUp extends PowerUp {
    
    /**
     * Creates a WarpPowerUp at the specified position.
     * 
     * @param x X-coordinate (center of destroyed brick)
     * @param y Y-coordinate (center of destroyed brick)
     */
    public WarpPowerUp(double x, double y) {
        super(x, y, PowerUpType.WARP);
    }
    
    /**
     * Applies the warp effect to the game.
     * 
     * Actions:
     * 1. Get current round from RoundsManager
     * 2. Check if next round exists:
     *    - If yes: Proceed with warp
     *    - If no: Trigger win screen (last level completed)
     * 3. Preserve game state:
     *    - score → carry over
     *    - lives → carry over
     *    - active powerup effects → carry over (optional)
     * 4. Play warp transition:
     *    - Screen distortion effect
     *    - Portal sound
     *    - Fade out
     * 5. Load next round:
     *    - Clear all bricks
     *    - Load new brick layout
     *    - Reset ball position
     *    - Reset paddle position (center)
     *    - Keep ball count (multi-ball persists)
     * 6. Fade in new level
     * 7. Resume gameplay
     * 
     * State preservation:
     * - Score: Kept (no bonus for skipped bricks)
     * - Lives: Kept (no penalty)
     * - Balls: Reset to 1 ball (or keep multi-ball - design choice)
     * - Powerup effects: Optional (CATCH, EXPAND may persist or reset)
     * - Paddle size: Reset to normal (design choice)
     * 
     * Edge cases:
     * - Warp from Round 3 → Win screen
     * - Warp with 0 bricks remaining → Same as normal completion
     * - Warp with active effects → Effects reset on level load
     * - Multi-ball during warp → Reset to 1 ball
     * 
     * Implementation details:
     * - GameManager.warpToNextLevel() handles all transitions
     * - RoundsManager.nextRound() loads new level
     * - StateManager handles save/restore of score/lives
     * - Transition animation runs before level load
     * 
     * Strategic considerations:
     * - Skip hard levels but lose potential points
     * - Useful when low on lives
     * - Risk: Next level might be harder
     * - Speedrun potential (skip levels for faster completion)
     * 
     * @param gameManager The game manager to apply effects to
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("WarpPowerUp: GameManager is null, cannot apply effect");
            return;
        }
        
        // Warp to next level
        boolean hasNextLevel = gameManager.warpToNextLevel();
        
        if (hasNextLevel) {
            System.out.println("WarpPowerUp: Warping to next level! " +
                             "Score and lives preserved.");
        } else {
            System.out.println("WarpPowerUp: No more levels! " +
                             "Triggering win screen.");
        }
    }
    
    /**
     * Removes the warp effect (not applicable for instant effects).
     * 
     * WarpPowerUp is an instant irreversible effect that doesn't need removal.
     * Once warped:
     * - New level is loaded
     * - Cannot undo
     * - Previous level state is lost
     * 
     * Level state:
     * - Old bricks: Cleared (no score recovery)
     * - Old ball positions: Lost
     * - Old powerup positions: Cleared
     * - Cannot return to previous level
     * 
     * @param gameManager The game manager (not used)
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        // Instant irreversible effect - no removal needed
        // Once warped, the transition is permanent
    }
}
