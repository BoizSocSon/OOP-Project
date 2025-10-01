package Objects;

/**
 * ExpandPaddlePowerUp increases the paddle width by a fixed amount while active.
 * Note: this simple implementation mutates the paddle width directly; a safer
 * approach would store the original width and restore it in removeEffect.
 */
public class ExpandPaddlePowerUp extends PowerUp {
    private double expandAmount;

    public ExpandPaddlePowerUp(double x, double y, double width, double height, double duration, double expandAmount) {
        super(x,y,width,height,duration);
        this.expandAmount = expandAmount;
    }

    @Override
    public void applyEffect(Paddle paddle) {
        paddle.setSpeed(paddle.getSpeed());
        paddle.width += expandAmount; // package-visible field
    }

    @Override
    public void removeEffect(Paddle paddle) {
        paddle.width -= expandAmount;
    }

    @Override
    public void update() { /* falls through as it just falls */ }

    @Override
    public void render(Render.Renderer renderer) { renderer.drawPowerUp(this); }
}
