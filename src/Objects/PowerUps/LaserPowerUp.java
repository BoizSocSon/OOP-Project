package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

/**
 * LaserPowerUp - Power-up cho phép paddle bắn laser để phá brick.
 * 
 * Functionality:
 * - Paddle có thể bắn laser beam lên phía trên
 * - Mỗi laser có thể phá 1 brick (hoặc damage Silver/Gold)
 * - Player nhấn SPACE để shoot (limited shots)
 * - Timed effect: 10 seconds hoặc hết 5 shots
 * 
 * Mechanics:
 * - applyEffect():
 *   1. Enable laser mode on paddle
 *   2. Set shot count = 5
 *   3. Change paddle sprite to paddle_laser (with cannons)
 *   4. Space key → fire laser
 * - Laser behavior:
 *   1. Spawns at paddle position (2 lasers from both sides)
 *   2. Travels upward at constant speed
 *   3. On brick hit: Damage brick + destroy laser
 *   4. On edge hit: Destroy laser
 * - removeEffect():
 *   1. Disable laser mode
 *   2. Revert paddle sprite
 *   3. Clear remaining lasers
 * 
 * Visual:
 * - Animation: laser_powerup (8 frames, red color theme)
 * - Paddle sprite: paddle_laser (with cannons on sides)
 * - Laser sprite: laser_beam (vertical red line)
 * - Muzzle flash effect when firing
 * 
 * Balance:
 * - 5 shots total (can destroy up to 5 bricks)
 * - Duration: 10 seconds
 * - Effect ends when time expires OR all shots used
 * - Laser speed: 8px/frame (faster than ball)
 * - Laser damage: 1 hit point per shot
 * 
 * @author SteveHoang aka BoizSocSon
 */
public class LaserPowerUp extends PowerUp {
    
    /**
     * Creates a LaserPowerUp at the specified position.
     * 
     * @param x X-coordinate (center of destroyed brick)
     * @param y Y-coordinate (center of destroyed brick)
     */
    public LaserPowerUp(double x, double y) {
        super(x, y, PowerUpType.LASER);
    }
    
    /**
     * Applies the laser effect to the paddle.
     * 
     * Actions:
     * 1. Enable laser mode on paddle
     * 2. Set remaining shots = 5
     * 3. Change paddle sprite to paddle_laser
     * 4. Add laser cannons visual to paddle sides
     * 5. Register SPACE key for firing
     * 6. Effect duration: 10 seconds OR until shots depleted
     * 
     * Firing mechanics:
     * - Player presses SPACE → spawn 2 Laser objects
     * - Left laser: spawns at paddle.x + 5
     * - Right laser: spawns at paddle.x + paddle.width - 5
     * - Both travel upward at LASER_SPEED
     * - Decrement shot counter each fire
     * 
     * Laser collision:
     * - Check intersection with all bricks
     * - On hit: brick.takeDamage(1) + laser.destroy()
     * - Normal brick: Destroyed in 1 shot
     * - Silver brick: Requires 2 shots (first shot cracks it)
     * - Gold brick: Indestructible (laser passes through or bounces)
     * 
     * Implementation details:
     * - GameManager tracks active Laser objects
     * - Laser extends MovableObject (velocity = (0, -8))
     * - Collision detection same as Ball vs Brick
     * - Shot counter managed by Paddle instance
     * 
     * @param gameManager The game manager to apply effects to
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("LaserPowerUp: GameManager is null, cannot apply effect");
            return;
        }
        
        // Enable laser mode with 5 shots
        gameManager.enableLaser(Constants.PowerUps.LASER_SHOTS);
        
        System.out.println("LaserPowerUp: Laser enabled with " + 
                         Constants.PowerUps.LASER_SHOTS + " shots for " +
                         Constants.PowerUps.LASER_DURATION / 1000.0 + " seconds");
    }
    
    /**
     * Removes the laser effect when expired or shots depleted.
     * 
     * Actions:
     * 1. Disable laser mode on paddle
     * 2. Revert paddle sprite to normal
     * 3. Remove laser cannons visual
     * 4. Destroy all active lasers
     * 5. Unregister SPACE key for firing
     * 
     * Expiry conditions:
     * - Timer reaches 10 seconds (automatic)
     * - All 5 shots used (manual trigger)
     * - Player loses a life (game state reset)
     * 
     * Edge cases:
     * - If lasers mid-flight when expired: They finish their trajectory
     * - If player holding SPACE when expired: No more lasers spawn
     * - Multiple laser powerups: Reset timer + refill to 5 shots
     * 
     * @param gameManager The game manager to remove effects from
     */
    @Override
    public void removeEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("LaserPowerUp: GameManager is null, cannot remove effect");
            return;
        }
        
        // Disable laser mode
        gameManager.disableLaser();
        
        System.out.println("LaserPowerUp: Laser disabled (expired or shots depleted)");
    }
}
