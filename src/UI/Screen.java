package UI;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

/**
 * Interface cơ bản cho tất cả các màn hình UI trong game.
 * Mỗi màn hình (Menu, Pause, GameOver, Win) sẽ implement interface này.
 */
public interface Screen {
    /**
     * Render màn hình lên GraphicsContext.
     * @param gc GraphicsContext để vẽ
     */
    void render(GraphicsContext gc);

    /**
     * Xử lý sự kiện phím được nhấn.
     * @param keyCode KeyCode của phím được nhấn
     */
    void handleKeyPressed(KeyCode keyCode);

    /**
     * Xử lý sự kiện phím được thả.
     * @param keyCode KeyCode của phím được thả
     */
    void handleKeyReleased(KeyCode keyCode);

    /**
     * Xử lý sự kiện chuột được nhấn.
     * @param event MouseEvent chứa thông tin chuột
     */
    void handleMouseClicked(MouseEvent event);

    /**
     * Xử lý sự kiện chuột di chuyển.
     * @param event MouseEvent chứa thông tin chuột
     */
    void handleMouseMoved(MouseEvent event);

    /**
     * Update logic của màn hình (animations, timers, etc.).
     * @param deltaTime Thời gian trôi qua kể từ frame trước (ms)
     */
    void update(long deltaTime);

    /**
     * Được gọi khi màn hình được kích hoạt.
     * Sử dụng để khởi tạo hoặc reset trạng thái.
     */
    void onEnter();

    /**
     * Được gọi khi màn hình bị vô hiệu hóa.
     * Sử dụng để cleanup resources nếu cần.
     */
    void onExit();
}
