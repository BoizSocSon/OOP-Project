package UI;

import Objects.PowerUps.PowerUpType;
import Utils.SpriteProvider;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

/**
 * Lớp dùng để hiển thị biểu tượng PowerUp trên giao diện (UI).
 * Biểu tượng này thường được dùng để chỉ báo PowerUp nào đang hoạt động
 * và nó chạy một animation nhỏ.
 */
public class PowerUpDisplay {
    // Loại PowerUp mà đối tượng này đại diện
    private PowerUpType type;
    // Tọa độ trung tâm để vẽ (sử dụng để căn giữa)
    private double x, y;
    // Kích thước vẽ
    private double width, height;
    // Đối tượng cung cấp sprite
    private SpriteProvider sprites;

    // Chỉ số khung hình hiện tại của animation
    private int currentFrame;
    // Thời điểm lần cuối chuyển khung hình
    private long lastFrameTime;
    private static final long FRAME_DURATION = 100; // 100ms mỗi frame
    private static final int TOTAL_FRAMES = 8; // PowerUps có 8 frames

    /**
     * Constructor khởi tạo PowerUpDisplay.
     * * @param type Loại PowerUp.
     * @param x Tọa độ trung tâm X.
     * @param y Tọa độ trung tâm Y.
     * @param width Chiều rộng hiển thị.
     * @param height Chiều cao hiển thị.
     * @param sprites Đối tượng SpriteProvider để lấy frames.
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
     * Cập nhật chỉ số khung hình để tạo hiệu ứng animation.
     * * @param currentTime Thời gian hệ thống hiện tại (mili giây).
     */
    public void update(long currentTime) {
        // Kiểm tra xem đã đủ thời gian để chuyển frame chưa
        if (currentTime - lastFrameTime >= FRAME_DURATION) {
            // Chuyển sang frame tiếp theo và lặp lại khi đạt TOTAL_FRAMES
            currentFrame = (currentFrame + 1) % TOTAL_FRAMES;
            lastFrameTime = currentTime;
        }
    }

    /**
     * Vẽ khung hình animation hiện tại lên canvas.
     * * @param gc GraphicsContext để vẽ.
     */
    public void render(GraphicsContext gc) {
        // Lấy danh sách frames tương ứng với loại PowerUp
        List<Image> frames = sprites.getPowerUpFrames(type);

        // Kiểm tra tính hợp lệ của frames
        if (frames != null && !frames.isEmpty() && currentFrame < frames.size()) {
            Image sprite = frames.get(currentFrame);

            // Tính toán tọa độ vẽ để căn giữa
            double drawX = x - width / 2;
            double drawY = y - height / 2;

            // Vẽ sprite
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