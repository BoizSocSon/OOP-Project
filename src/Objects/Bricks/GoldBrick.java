package Objects.Bricks;

import Graphics.Renderer;
import Graphics.SpriteCache;

/**
 * GoldBrick - viên gạch vàng đặc biệt.
 * 
 * Chức năng:
 * - Không thể bị phá hủy.
 * - Chỉ phản xạ bóng, không thay đổi trạng thái.
 */
public class GoldBrick extends Brick {

    /**
     * Constructor.
     * Gán hp = Integer.MAX_VALUE để không bao giờ bị phá hủy.
     */
    public GoldBrick() {
        super(BrickType.GOLD);
        this.hp = Integer.MAX_VALUE;
    }

    /**
     * Khi bị bóng va chạm - không làm gì cả.
     */
    @Override
    public void takeHit() {
        // Không giảm hp, không thay đổi trạng thái.
    }

    /**
     * Hiển thị viên gạch vàng.
     */
    @Override
    public void render(Renderer renderer) {
        renderer.drawSprite(SpriteCache.getBrickSprite(BrickType.GOLD), x, y);
    }
}
