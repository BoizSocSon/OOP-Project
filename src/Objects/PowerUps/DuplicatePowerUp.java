package Objects.PowerUps;

import Engine.GameManager;

/**
 * DuplicatePowerUp - Power-up nhân đôi số lượng ball trong game.
 * 
 * Functionality:
 * - Clone tất cả balls hiện tại
 * - Mỗi ball gốc tạo 2 balls mới với góc +45° và -45°
 * - Tăng cơ hội phá brick và survive
 * - Instant effect: Không có duration, kích hoạt ngay lập tức
 * 
 * Mechanics:
 * - applyEffect(): 
 *   1. Get all current balls
 *   2. For each ball:
 *      - Create ball1 with angle +45° từ current direction
 *      - Create ball2 with angle -45° từ current direction
 *      - Keep original ball
 *   3. Result: 3x số lượng balls
 * - removeEffect(): Không cần (instant effect)
 * 
 * Balance considerations:
 * - Maximum balls limit: 20 (to prevent performance issues)
 * - If at max: effect still applies but capped at 20 total
 * - All new balls inherit speed của original ball
 * 
 * Visual:
 * - Animation: duplicate_powerup (8 frames, orange color theme)
 * - Spawn effect: White flash khi balls duplicate
 * 
 * @author SteveHoang aka BoizSocSon
 */
public class DuplicatePowerUp extends PowerUp {
    
    /**
     * Creates a DuplicatePowerUp at the specified position.
     * 
     * @param x X-coordinate (center of destroyed brick)
     * @param y Y-coordinate (center of destroyed brick)
     */
    public DuplicatePowerUp(double x, double y) {
        super(x, y, PowerUpType.DUPLICATE);
    }
    
    /**
     * Applies the duplicate effect to the game.
     * 
     * Actions:
     * 1. Get all active balls from GameManager
     * 2. For each ball:
     *    - Calculate current velocity angle
     *    - Create 2 new balls:
     *      * Ball 1: angle + 45° (right fork)
     *      * Ball 2: angle - 45° (left fork)
     *    - Same speed as original
     *    - Same position as original
     * 3. Add new balls to game
     * 4. Keep original balls (total = 3x)
     * 
     * Implementation details:
     * - Uses Vector2D math for angle calculation
     * - New balls start at exact same position
     * - Separation happens naturally from different angles
     * - Maximum 20 balls enforced by GameManager
     * 
     * Edge cases:
     * - If 1 ball exists: Result = 3 balls
     * - If 2 balls exist: Result = 6 balls
     * - If 7+ balls exist: Capped at 20 balls
     * - If no balls (edge case): No effect
     * 
     * @param gameManager The game manager to apply effects to
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("DuplicatePowerUp: GameManager is null, cannot apply effect");
            return;
        }
        
        // Duplicate all balls with ±45° angle offsets
        int originalCount = gameManager.getBallCount();
        gameManager.duplicateBalls();
        int newCount = gameManager.getBallCount();
        
        System.out.println("DuplicatePowerUp: Balls duplicated from " + 
                         originalCount + " to " + newCount);
    }
    
    /**
     * Removes the duplicate effect (not applicable for instant effects).
     * 
     * DuplicatePowerUp is an instant effect that doesn't need removal.
     * Once balls are created, they exist independently until:
     * - They fall off screen
     * - Player loses a life
     * - Level completes
     * 
     * @param gameManager The game manager (not used)
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        // Instant effect - no removal needed
        // Balls created persist until they die naturally
    }
}
