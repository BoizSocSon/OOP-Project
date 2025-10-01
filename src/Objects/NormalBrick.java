package Objects;

/**
 * NormalBrick is destroyed with a single hit (hp = 1).
 */
/**
 * Viên gạch thông thường: chỉ cần 1 hit để phá hủy.
 */
public class NormalBrick extends Brick {
    public NormalBrick(double x, double y, double width, double height) {
        super(x, y, width, height, 1);
    }

    @Override
    public void update() { /* static by default */ }

    @Override
    public void render(Render.Renderer renderer) {
        renderer.drawBrick(this);
    }
}
