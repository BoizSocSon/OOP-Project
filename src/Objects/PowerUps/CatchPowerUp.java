package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

public class CatchPowerUp extends PowerUp{
    public CatchPowerUp(double x, double y) {
        super(x, y, PowerUpType.CATCH);
    }

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
