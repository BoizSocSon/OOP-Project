package UI;

import Objects.PowerUps.PowerUpType;
import Utils.SpriteProvider;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

/**
 * Component để hiển thị preview của PowerUp trong Menu.
 * Hiển thị icon PowerUp với animation idle.
 */
public class PowerUpDisplay {
    private PowerUpType type;
    private double x, y;
    private double width, height;
    private SpriteProvider sprites;

    // Animation
    private int currentFrame;
    private long lastFrameTime;
    private static final long FRAME_DURATION = 100; // 100ms mỗi frame
    private static final int TOTAL_FRAMES = 8; // PowerUps có 8 frames

    /**
     * Constructor cho PowerUpDisplay.
     * @param type Loại PowerUp
     * @param x Tọa độ X (center)
     * @param y Tọa độ Y (center)
     * @param width Chiều rộng hiển thị
     * @param height Chiều cao hiển thị
     * @param sprites SpriteProvider để lấy sprites
     */
    public PowerUpDisplay(PowerUpType type, double x, double y, double width, double height, SpriteProvider sprites) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprites = sprites;
        this.currentFrame = 0;
        this.lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Update animation frame.
     * @param currentTime Thời gian hiện tại (ms)
     */
    public void update(long currentTime) {
        if (currentTime - lastFrameTime >= FRAME_DURATION) {
            currentFrame = (currentFrame + 1) % TOTAL_FRAMES;
            lastFrameTime = currentTime;
        }
    }

    /**
     * Render PowerUp icon lên canvas.
     * @param gc GraphicsContext để vẽ
     */
    public void render(GraphicsContext gc) {
        List<Image> frames = sprites.getPowerUpFrames(type);

        if (frames != null && !frames.isEmpty() && currentFrame < frames.size()) {
            Image sprite = frames.get(currentFrame);
            // Draw centered
            double drawX = x - width / 2;
            double drawY = y - height / 2;
            gc.drawImage(sprite, drawX, drawY, width, height);
        }
    }

    // Getters
    public PowerUpType getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
