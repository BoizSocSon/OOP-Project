package Objects.Bricks;

import Render.Renderer;

/**
 * NormalBrick - loại gạch thông thường.
 * Destroyed sau 1 hit.
 */
public class NormalBrick extends Brick {
    private final BrickType type;
    /**
     * Constructor nhận BrickType, vị trí và kích thước.
     */
    public NormalBrick(BrickType type, double x, double y, double width, double height) {
        super(x, y, width, height, 1); // 1 hit để phá hủy
        this.type = type;
    }
    
    /**
     * Update method - không cần làm gì cho normal brick.
     */
    @Override
    public void update() {
        // Normal brick không có animation
    }

    /**
     * Render sprite tương ứng với BrickType.
     */
    @Override
    public void render(Renderer renderer) {
        renderer.drawSprite(type.getSpriteName(), getX(), getY());
    }

    /**
     * Trả về điểm dựa trên BrickType.
     */
    public int getScore() {
        return type.getBaseScore();
    }
    
    /**
     * Gets the brick type for rendering.
     * @return BrickType enum value
     */
    public BrickType getType() {
        return type;
    }
}
