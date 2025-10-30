package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

public class LaserPowerUp extends PowerUp{
    public LaserPowerUp(double x, double y) {
        super(x, y, PowerUpType.LASER);
    }

    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("LaserPowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Enable laser mode with 5 shots
        gameManager.enableLaser();

        System.out.println("LaserPowerUp: Laser enabled with " +
                Constants.Laser.LASER_SHOTS + " shots for " +
                Constants.PowerUps.LASER_DURATION / 1000.0 + " seconds");
    }

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
