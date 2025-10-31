package Objects.PowerUps;

import Engine.GameManager;

public class WarpPowerUp extends PowerUp{
    public WarpPowerUp(double x, double y) {
        super(x, y, PowerUpType.WARP);
    }

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

    @Override
    public void removeEffect(GameManager gameManager) {
        // Instant irreversible effect - no removal needed
        // Once warped, the transition is permanent
    }
}
