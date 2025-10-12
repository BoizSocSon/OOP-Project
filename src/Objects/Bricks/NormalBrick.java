package Objects.Bricks;

import Graphics.Renderer;
import Graphics.SpriteCache;

/**
 * NormalBrick - loại gạch thông thường.
 * Destroyed sau 1 hit.
 */
public class NormalBrick extends Brick {

    /**
     * Constructor nhận BrickType.
     * Mỗi màu BrickType khác nhau sẽ có điểm và sprite riêng.
     */
    public NormalBrick(BrickType type) {
        super(type);
        this.hp = 1; // Vỡ sau 1 hit
    }

    /**
     * Render sprite tương ứng với BrickType.
     */
    @Override
    public void render(Renderer renderer) {
        renderer.drawSprite(SpriteCache.getBrickSprite(type), x, y);
    }

    /**
     * Trả về điểm dựa trên BrickType.
     */
    @Override
    public int getScore() {
        return type.getBaseScore();
    }
}
