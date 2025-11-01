package UI;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

/**
 * Giao diện đại diện cho một màn hình hoặc trạng thái (state) trong trò chơi.
 * Mọi màn hình (ví dụ: Menu, GamePlay, GameOver) cần triển khai giao diện này
 * để xử lý việc render, cập nhật logic và tương tác người dùng.
 */
public interface Screen {
    /**
     * Vẽ nội dung của màn hình lên GraphicsContext.
     * @param gc Context đồ họa để vẽ.
     */
    void render(GraphicsContext gc);

    /**
     * Xử lý sự kiện khi một phím được nhấn xuống.
     * @param keyCode Mã phím được nhấn.
     */
    void handleKeyPressed(KeyCode keyCode);

    /**
     * Xử lý sự kiện khi một phím được nhả ra.
     * @param keyCode Mã phím được nhả.
     */
    void handleKeyReleased(KeyCode keyCode);

    /**
     * Xử lý sự kiện click chuột.
     * @param event Chi tiết sự kiện chuột.
     */
    void handleMouseClicked(MouseEvent event);

    /**
     * Xử lý sự kiện di chuyển chuột.
     * @param event Chi tiết sự kiện chuột.
     */
    void handleMouseMoved(MouseEvent event);

    /**
     * Cập nhật logic màn hình theo thời gian.
     * @param deltaTime Thời gian đã trôi qua kể từ lần cập nhật trước.
     */
    void update(long deltaTime);

    /**
     * Được gọi khi màn hình được chuyển đến (bắt đầu hoạt động).
     */
    void onEnter();

    /**
     * Được gọi khi màn hình bị rời đi (dừng hoạt động).
     */
    void onExit();
}