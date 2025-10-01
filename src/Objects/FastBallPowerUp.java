package Objects;

/**
 * FastBallPowerUp is intended to increase the ball speed. The current skeleton
 * leaves the application logic to the GameManager (so this class records the
 * multiplier but doesn't directly mutate the ball).
 */
public class FastBallPowerUp extends PowerUp {
    private double speedMultiplier;

    public FastBallPowerUp(double x, double y, double width, double height, double duration, double speedMultiplier) {
        super(x,y,width,height,duration);
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public void applyEffect(Paddle paddle) {
        // not applicable to paddle; this powerup should be applied to a Ball via GameManager
    }

    @Override
    public void removeEffect(Paddle paddle) { }

    @Override
    public void update() { }

    @Override
    public void render(Render.Renderer renderer) { renderer.drawPowerUp(this); }
}
