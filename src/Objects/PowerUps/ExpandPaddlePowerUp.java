package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

public class ExpandPaddlePowerUp extends PowerUp{
    public ExpandPaddlePowerUp(double x, double y) {
        super(x, y, PowerUpType.EXPAND);
    }

    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("ExpandPaddlePowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Expand paddle width through GameManager
        gameManager.expandPaddle();

        System.out.println("ExpandPaddlePowerUp: Paddle expanded to " +
                (Constants.PowerUps.EXPAND_MULTIPLIER * 100) + "% for " +
                Constants.PowerUps.EXPAND_DURATION / 1000.0 + " seconds");
    }

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
