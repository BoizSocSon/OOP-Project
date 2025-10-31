package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

public class SlowBallPowerUp extends PowerUp{
    public SlowBallPowerUp(double x, double y) {
        super(x, y, PowerUpType.SLOW);
    }

    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("SlowBallPowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Slow all balls by multiplier (0.7x = 30% reduction)
        gameManager.slowBalls(Constants.PowerUps.SLOW_MULTIPLIER);

        System.out.println("SlowBallPowerUp: Balls slowed to " +
                (Constants.PowerUps.SLOW_MULTIPLIER * 100) + "% speed for " +
                Constants.PowerUps.SLOW_DURATION / 1000.0 + " seconds");
    }

    @Override
    public void removeEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("SlowBallPowerUp: GameManager is null, cannot remove effect");
            return;
        }

        // Restore ball speed (divide by slow multiplier)
        gameManager.restoreBallSpeed();

        System.out.println("SlowBallPowerUp: Ball speed restored (slow expired)");
    }
}
