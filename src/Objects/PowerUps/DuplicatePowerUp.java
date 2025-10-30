package Objects.PowerUps;

import Engine.GameManager;

public class DuplicatePowerUp extends  PowerUp{
    public DuplicatePowerUp(double x, double y) {
        super(x, y, PowerUpType.DUPLICATE);
    }

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

    @Override
    public void removeEffect(GameManager gameManager) {
        // Instant effect - no removal needed
        // Balls created persist until they die naturally
    }
}
