package Objects.PowerUps;

import Engine.GameManager;
import Utils.Constants;

public class LifePowerUp extends PowerUp{
    public LifePowerUp(double x, double y) {
        super(x, y, PowerUpType.LIFE);
    }

    @Override
    public void applyEffect(GameManager gameManager) {
        if (gameManager == null) {
            System.err.println("LifePowerUp: GameManager is null, cannot apply effect");
            return;
        }

        // Add 1 life (capped at MAX_LIVES)
        int livesBeforeAdd = gameManager.getLives();
        gameManager.addLife();
        int livesAfter = gameManager.getLives();

        if (livesAfter > livesBeforeAdd) {
            System.out.println("LifePowerUp: Life added! Lives: " +
                    livesBeforeAdd + " → " + livesAfter);
        } else {
            System.out.println("LifePowerUp: Max lives reached (" +
                    Constants.GameRules.MAX_LIVES + "), no effect");
        }
    }

    @Override
    public void removeEffect(GameManager gameManager) {
        // Instant permanent effect - no removal needed
        // Lives persist until lost through normal gameplay
    }
}
