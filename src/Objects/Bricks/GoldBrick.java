package Objects.Bricks;

import Render.Renderer;

/**
 * GoldBrick - viên gạch vàng đặc biệt.
 *
 * Chức năng:
 * - Không thể bị phá hủy.
 * - Chỉ phản xạ bóng, không thay đổi trạng thái.
 */
public class GoldBrick extends Brick {

    /**
     * Constructor: GoldBrick không thể bị phá hủy.
     */
    public GoldBrick(double x, double y, double width, double height) {
        super(x, y, width, height, Integer.MAX_VALUE);
    }

    /**
     * Không làm gì khi bị hit.
     */
    @Override
    public void takeHit() {
        // Không giảm hp, không thay đổi trạng thái.
        return;
    }
    
    /**
     * Update method - không cần làm gì cho gold brick.
     */
    @Override
    public void update() {
        // Gold brick không có animation hoặc state changes
    }

    /**
     * Render gold brick sprite.
     */
    @Override
    public void render(Renderer renderer) {
        renderer.drawSprite(BrickType.GOLD.getSpriteName(), getX(), getY());
    }
}
