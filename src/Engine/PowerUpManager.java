package Engine;

import Objects.PowerUps.*;
import Objects.Bricks.BrickType;
import Objects.GameEntities.Paddle;
import Utils.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class PowerUpManager {
    private static PowerUpManager instance;
    private final List<PowerUp> activePowerUps;
    private final Map<PowerUpType, Long> activeEffects;
    private GameManager gameManager;

    private PowerUpManager() {
        this.activePowerUps = new ArrayList<>();
        this.activeEffects = new HashMap<>();
    }

    public static PowerUpManager getInstance() {
        if (instance == null) {
            instance = new PowerUpManager();
        }
        return instance;
    }

    public static void reset() {
        if (instance != null) {
            instance.activePowerUps.clear();
            instance.activeEffects.clear();
        }
        instance = null;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void spawnFromBrick(double x, double y, BrickType brickType) {
        // 30% chance to spawn powerup
        if (Math.random() > Constants.GameRules.POWERUP_SPAWN_CHANCE) {
            return; // No spawn
        }

        // Select random powerup type with weighted probability
        PowerUpType type = PowerUpType.randomWeighted();
        if (type == null) {
            return; // Failed to select (shouldn't happen)
        }

        PowerUp powerUp = createPowerUp(x, y, type);
        activePowerUps.add(powerUp);

        System.out.println("PowerUp spawned: " + type + " at (" + x + ", " + y + ")");
    }

    private PowerUp createPowerUp(double x, double y, PowerUpType type) {
        switch (type) {
            case CATCH:
                return new CatchPowerUp(x, y);
            case DUPLICATE:
                return new DuplicatePowerUp(x, y);
            case EXPAND:
                return new ExpandPaddlePowerUp(x, y);
            case LASER:
                return new LaserPowerUp(x, y);
            case LIFE:
                return new LifePowerUp(x, y);
            case SLOW:
                return new SlowBallPowerUp(x, y);
            case WARP:
                return new WarpPowerUp(x, y);
            default:
                System.err.println("Unknown PowerUpType: " + type);
                return new ExpandPaddlePowerUp(x, y);
        }
    }

    public void update(Paddle paddle) {
        if (paddle == null) {
            return;
        }

        // Create a copy to avoid ConcurrentModificationException
        // This prevents issues when applyPowerUpEffect triggers clearAllPowerUps
        List<PowerUp> powerUpsCopy = new ArrayList<>(activePowerUps);

        for (PowerUp powerUp : powerUpsCopy) {
            // Skip if this powerup was already removed (e.g., by clearAllPowerUps)
            if (!activePowerUps.contains(powerUp)) {
                continue;
            }

            powerUp.update();

            if (powerUp.checkPaddleCollision(paddle)) {
                powerUp.collect();
                applyPowerUpEffect(powerUp);
                scheduleEffectExpiry(powerUp.getType());
                activePowerUps.remove(powerUp);

                System.out.println("PowerUp collected: " + powerUp.getType());
            } else if (powerUp.getY() > Constants.Window.WINDOW_HEIGHT) {
                activePowerUps.remove(powerUp);
                System.out.println("PowerUp missed and removed: " + powerUp.getType());
            }
        }

        updateActiveEffects();
    }

    private void applyPowerUpEffect(PowerUp powerUp) {
        if (gameManager == null) {
            System.err.println("PowerUpManager: GameManager is null, cannot apply effect");
            return;
        }

        powerUp.applyEffect(gameManager);
    }

    private void removePowerUpEffect(PowerUpType type) {
        if (gameManager == null) {
            System.err.println("PowerUpManager: GameManager is null, cannot remove effect");
            return;
        }

        PowerUp tempPowerUp = createPowerUp(0, 0, type);
        tempPowerUp.removeEffect(gameManager);
    }

    private void scheduleEffectExpiry(PowerUpType type) {
        long duration = type.getDuration();
        if (duration > 0) {
            long expiryTime = System.currentTimeMillis() + duration;
            activeEffects.put(type, expiryTime);

            System.out.println("PowerUpManager: Scheduled expiry for " + type + " at " + expiryTime);
        }
    }

    private void updateActiveEffects() {
        if (activeEffects.isEmpty()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<PowerUpType, Long>> iterator = activeEffects.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<PowerUpType, Long> entry = iterator.next();
            PowerUpType type = entry.getKey();
            long expiryTime = entry.getValue();

            if (currentTime >= expiryTime) {
                removePowerUpEffect(type);
                iterator.remove();

                System.out.println("PowerUpManager: Effect expired for " + type);
            }
        }
    }

    public List<PowerUp> getActivePowerUps() {
        return new ArrayList<>(activePowerUps);
    }

    public void clearAllPowerUps() {
        activePowerUps.clear();
        activeEffects.clear();
        System.out.println("PowerUpManager: Cleared all power-ups and effects");
    }
}
