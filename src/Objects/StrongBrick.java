package Objects;

/**
 * StrongBrick requires multiple hits (hp &gt; 1) to be destroyed.
 */
public class StrongBrick extends Brick {
    public StrongBrick(double x, double y, double width, double height, int hp) {
        super(x, y, width, height, hp);
    }

    @Override
    public void update() { }

    @Override
    public void render(Render.Renderer renderer) { renderer.drawBrick(this); }
}
