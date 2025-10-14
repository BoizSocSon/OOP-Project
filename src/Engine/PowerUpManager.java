package Engine;

import Objects.PowerUps.PowerUp;
import Objects.PowerUps.PowerUpType;
import Objects.Bricks.BrickType;
import Objects.GameEntities.Paddle;
import Utils.Constants;
import GeometryPrimitives.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * PowerUpManager - Singleton quản lý spawn, update và collision của power-ups.
 *
 * Responsibilities:
 * - Spawn powerups khi bricks destroyed (30% chance)
 * - Update active powerups (falling animation)
 * - Detect collision với paddle
 * - Track active effects với expiry timer
 * - Remove expired effects
 *
 * Design patterns:
 * - Singleton: chỉ có 1 instance trong game
 * - Observer: notify GameManager khi effects expire
 *
 * @author SteveHoang aka BoizSocSon
 */
public class PowerUpManager {
    private static PowerUpManager instance;

    // Active powerups đang rơi xuống
    private final List<PowerUp> activePowerUps;

    // Active effects hiện đang có hiệu lực (type → expiry timestamp)
    private final Map<PowerUpType, Long> activeEffects;

    // Weighted probabilities cho random spawn
    // Total weight = 1.0 (100%)
    private final Map<PowerUpType, Double> spawnProbabilities;

    // Reference tới GameManager để apply effects
    private GameManager gameManager;

    /**
     * Private constructor for Singleton pattern.
     */
    private PowerUpManager() {
        this.activePowerUps = new ArrayList<>();
        this.activeEffects = new HashMap<>();
        this.spawnProbabilities = new HashMap<>();

        initializeSpawnProbabilities();
    }

    /**
     * Gets the singleton instance.
     *
     * @return PowerUpManager instance
     */
    public static PowerUpManager getInstance() {
        if (instance == null) {
            instance = new PowerUpManager();
        }
        return instance;
    }

    /**
     * Resets the manager (used for new game or level restart).
     */
    public static void reset() {
        if (instance != null) {
            instance.activePowerUps.clear();
            instance.activeEffects.clear();
        }
        instance = null;
    }

    /**
     * Sets the GameManager reference for applying effects.
     *
     * @param gameManager The game manager instance
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Initializes weighted spawn probabilities for each powerup type.
     *
     * Distribution (from PowerUpType enum):
     * - CATCH: 15%
     * - DUPLICATE: 12%
     * - EXPAND: 15%
     * - LASER: 15%
     * - LIFE: 5%
     * - SLOW: 15%
     * - WARP: 3%
     * Total: 80% (remaining 20% = no spawn)
     */
    private void initializeSpawnProbabilities() {
        // Use probabilities from PowerUpType enum
        for (PowerUpType type : PowerUpType.values()) {
            spawnProbabilities.put(type, type.getSpawnChance());
        }
    }

    /**
     * Spawns a powerup from a destroyed brick with 30% chance.
     *
     * Logic:
     * 1. Roll 30% spawn chance
     * 2. If success, select random PowerUpType (weighted)
     * 3. Create PowerUp instance at brick position
     * 4. Add to activePowerUps list
     *
     * @param x X-coordinate (brick center)
     * @param y Y-coordinate (brick center)
     * @param brickType Type of brick destroyed (for potential special rules)
     */
    public void spawnFromBrick(double x, double y, BrickType brickType) {
        // 30% chance to spawn powerup
        if (Math.random() > Constants.GameRules.POWERUP_SPAWN_CHANCE) {
            return; // No spawn
        }

        // Select random powerup type with weighted probability
        PowerUpType type = selectWeightedRandomType();
        if (type == null) {
            return; // Failed to select (shouldn't happen)
        }

        // Create powerup at brick center (adjust for powerup size)
        double powerUpX = x - Constants.PowerUps.POWERUP_WIDTH / 2.0;
        double powerUpY = y - Constants.PowerUps.POWERUP_HEIGHT / 2.0;

        PowerUp powerUp = createPowerUp(type, powerUpX, powerUpY);
        activePowerUps.add(powerUp);

        System.out.println("PowerUp spawned: " + type + " at (" + x + ", " + y + ")");
    }

    /**
     * Selects a random PowerUpType using weighted probabilities.
     *
     * Algorithm:
     * 1. Calculate total weight
     * 2. Roll random number [0, totalWeight)
     * 3. Iterate through types, subtracting weights
     * 4. Return type when cumulative weight exceeds roll
     *
     * @return Random PowerUpType or null if failed
     */
    private PowerUpType selectWeightedRandomType() {
        double totalWeight = 0.0;
        for (double weight : spawnProbabilities.values()) {
            totalWeight += weight;
        }

        double roll = Math.random() * totalWeight;
        double cumulative = 0.0;

        for (Map.Entry<PowerUpType, Double> entry : spawnProbabilities.entrySet()) {
            cumulative += entry.getValue();
            if (roll <= cumulative) {
                return entry.getKey();
            }
        }

        // Fallback to EXPAND if something goes wrong
        return PowerUpType.EXPAND;
    }

    /**
     * Creates a PowerUp instance of the specified type.
     *
     * Uses simple placeholder implementation until actual PowerUp subclasses
     * (CatchPowerUp, ExpandPowerUp, etc.) are implemented.
     *
     * @param type PowerUpType to create
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return PowerUp instance
     */
    private PowerUp createPowerUp(PowerUpType type, double x, double y) {
        // Temporary placeholder implementation
        // TODO: Replace with actual subclass instantiation when implemented:
        // case CATCH: return new CatchPowerUp(x, y);
        // case EXPAND: return new ExpandPaddlePowerUp(x, y);
        // etc.

        return new PowerUp(x, y, type) {
            @Override
            public void applyEffect(Paddle paddle) {
                // Placeholder - will be implemented in subclasses
            }

            @Override
            public void removeEffect(Paddle paddle) {
                // Placeholder - will be implemented in subclasses
            }
        };
    }

    /**
     * Updates all active powerups each frame.
     *
     * Actions per powerup:
     * 1. Update position (fall down)
     * 2. Update animation
     * 3. Check if out of bounds → remove
     * 4. Check collision with paddle → apply effect & remove
     *
     * Also updates active effects and removes expired ones.
     *
     * @param paddle The player's paddle for collision detection
     */
    public void update(Paddle paddle) {
        if (paddle == null) {
            return;
        }

        // Use iterator for safe removal during iteration
        Iterator<PowerUp> iterator = activePowerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();

            // Update powerup (movement + animation)
            powerUp.update();

            // Check if powerup fell off screen
            if (powerUp.getY() > Constants.Window.WINDOW_HEIGHT) {
                iterator.remove();
                continue;
            }

            // Check collision with paddle
            if (checkPaddleCollision(powerUp, paddle)) {
                // Apply effect to game
                applyPowerUpEffect(powerUp, paddle);

                // Schedule removal if it's a timed effect
                if (!powerUp.getType().isInstant()) {
                    scheduleRemoval(powerUp);
                }

                // Remove from active list
                iterator.remove();

                System.out.println("PowerUp collected: " + powerUp.getType());
            }
        }

        // Update and remove expired effects
        updateActiveEffects(paddle);
    }

    /**
     * Checks collision between powerup and paddle.
     *
     * Uses Rectangle.intersects() for AABB collision detection.
     *
     * @param powerUp The powerup to check
     * @param paddle The paddle
     * @return true if collision detected
     */
    private boolean checkPaddleCollision(PowerUp powerUp, Paddle paddle) {
        Rectangle powerUpBounds = powerUp.getBounds();
        Rectangle paddleBounds = paddle.getBounds();

        // Simple AABB collision
        return !(powerUpBounds.getUpperLeft().getX() + powerUpBounds.getWidth() < paddleBounds.getUpperLeft().getX() ||
                 powerUpBounds.getUpperLeft().getX() > paddleBounds.getUpperLeft().getX() + paddleBounds.getWidth() ||
                 powerUpBounds.getUpperLeft().getY() + powerUpBounds.getHeight() < paddleBounds.getUpperLeft().getY() ||
                 powerUpBounds.getUpperLeft().getY() > paddleBounds.getUpperLeft().getY() + paddleBounds.getHeight());
    }

    /**
     * Applies the powerup effect to the game.
     *
     * For instant effects (LIFE, DUPLICATE, WARP):
     * - Apply immediately
     *
     * For timed effects (CATCH, EXPAND, LASER, SLOW):
     * - Apply effect
     * - Schedule removal after duration
     *
     * @param powerUp The collected powerup
     * @param paddle The player's paddle
     */
    private void applyPowerUpEffect(PowerUp powerUp, Paddle paddle) {
        PowerUpType type = powerUp.getType();

        // TODO: Implement actual effect application through GameManager
        // For now, just log the effect

        if (gameManager == null) {
            System.err.println("PowerUpManager: GameManager not set, cannot apply effect");
            return;
        }

        switch (type) {
            case CATCH:
                // Enable catch mode on paddle
                // paddle.enableCatchMode(Constants.PowerUps.CATCH_DURATION);
                System.out.println("Applied CATCH effect");
                break;

            case DUPLICATE:
                // Duplicate ball(s)
                // gameManager.duplicateBalls();
                System.out.println("Applied DUPLICATE effect");
                break;

            case EXPAND:
                // Expand paddle width
                // paddle.expand(Constants.PowerUps.EXPAND_MULTIPLIER, Constants.PowerUps.EXPAND_DURATION);
                System.out.println("Applied EXPAND effect");
                break;

            case LASER:
                // Enable laser shooting
                // paddle.enableLaser(Constants.PowerUps.LASER_DURATION);
                System.out.println("Applied LASER effect");
                break;

            case LIFE:
                // Add extra life
                // gameManager.addLife();
                System.out.println("Applied LIFE effect");
                break;

            case SLOW:
                // Slow down ball speed
                // gameManager.slowBalls(Constants.PowerUps.SLOW_MULTIPLIER, Constants.PowerUps.SLOW_DURATION);
                System.out.println("Applied SLOW effect");
                break;

            case WARP:
                // Warp to next level
                // gameManager.warpToNextLevel();
                System.out.println("Applied WARP effect");
                break;
        }
    }

    /**
     * Schedules a timed effect for removal after its duration.
     *
     * Adds entry to activeEffects map:
     * - Key: PowerUpType
     * - Value: expiry timestamp (currentTime + duration)
     *
     * @param powerUp The powerup with timed effect
     */
    private void scheduleRemoval(PowerUp powerUp) {
        PowerUpType type = powerUp.getType();
        long duration = type.getDuration();

        if (duration > 0) {
            long expiryTime = System.currentTimeMillis() + duration;
            activeEffects.put(type, expiryTime);

            System.out.println("Scheduled " + type + " removal at " + expiryTime);
        }
    }

    /**
     * Updates active effects and removes expired ones.
     *
     * Checks each active effect:
     * - If current time > expiry time → remove effect
     * - Notify GameManager to revert effect
     *
     * @param paddle The player's paddle (for reverting effects)
     */
    private void updateActiveEffects(Paddle paddle) {
        if (activeEffects.isEmpty()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<PowerUpType, Long>> iterator = activeEffects.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<PowerUpType, Long> entry = iterator.next();
            PowerUpType type = entry.getKey();
            long expiryTime = entry.getValue();

            // Check if effect has expired
            if (currentTime >= expiryTime) {
                // Remove effect
                removeEffect(type, paddle);
                iterator.remove();

                System.out.println("Effect expired: " + type);
            }
        }
    }

    /**
     * Removes/reverts a powerup effect.
     *
     * For each effect type:
     * - Revert changes made by the powerup
     * - Notify GameManager
     *
     * @param type The effect type to remove
     * @param paddle The player's paddle
     */
    private void removeEffect(PowerUpType type, Paddle paddle) {
        if (gameManager == null || paddle == null) {
            return;
        }

        // TODO: Implement actual effect removal through GameManager
        switch (type) {
            case CATCH:
                // paddle.disableCatchMode();
                System.out.println("Removed CATCH effect");
                break;

            case EXPAND:
                // paddle.revertToNormalSize();
                System.out.println("Removed EXPAND effect");
                break;

            case LASER:
                // paddle.disableLaser();
                System.out.println("Removed LASER effect");
                break;

            case SLOW:
                // gameManager.restoreBallSpeed();
                System.out.println("Removed SLOW effect");
                break;

            default:
                // Instant effects don't need removal
                break;
        }
    }

    /**
     * Gets all active powerups (for rendering).
     *
     * @return List of active powerups
     */
    public List<PowerUp> getActivePowerUps() {
        return new ArrayList<>(activePowerUps); // Defensive copy
    }

    /**
     * Checks if a specific effect is currently active.
     *
     * @param type PowerUpType to check
     * @return true if effect is active
     */
    public boolean isEffectActive(PowerUpType type) {
        return activeEffects.containsKey(type);
    }

    /**
     * Gets remaining time for an active effect.
     *
     * @param type PowerUpType to check
     * @return Remaining milliseconds, or 0 if not active
     */
    public long getRemainingTime(PowerUpType type) {
        if (!activeEffects.containsKey(type)) {
            return 0;
        }

        long expiryTime = activeEffects.get(type);
        long currentTime = System.currentTimeMillis();
        return Math.max(0, expiryTime - currentTime);
    }

    /**
     * Gets count of active powerups (for debugging).
     *
     * @return Number of powerups currently falling
     */
    public int getActivePowerUpCount() {
        return activePowerUps.size();
    }

    /**
     * Gets count of active effects (for debugging).
     *
     * @return Number of effects currently active
     */
    public int getActiveEffectCount() {
        return activeEffects.size();
    }

    /**
     * Clears all powerups and effects (used for level restart).
     */
    public void clearAll() {
        activePowerUps.clear();
        activeEffects.clear();
        System.out.println("PowerUpManager: Cleared all powerups and effects");
    }
}
