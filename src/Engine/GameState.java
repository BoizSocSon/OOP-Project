package Engine;

/**
 * Enum định nghĩa các trạng thái của game.
 *
 * Lưu đồ chuyển trạng thái (State machine flow):
 * MENU → PLAYING ↔ PAUSED
 * PLAYING → LEVEL_COMPLETE → PLAYING (next level)
 * PLAYING → GAME_OVER (no lives left)
 * PLAYING → WIN (all levels completed)
 * GAME_OVER/WIN → MENU (restart)
 */
public enum GameState {
    /**
     * Menu chính.
     * Hiển thị tiêu đề, điểm cao (high scores), và các tùy chọn.
     */
    MENU,

    /**
     * Trò chơi đang được chơi tích cực.
     * Bóng di chuyển, gạch bị phá hủy, v.v.
     */
    PLAYING,

    /**
     * Trò chơi đang tạm dừng (thường do nhấn ESC).
     * Tất cả đối tượng game bị đóng băng, hiển thị menu tạm dừng.
     */
    PAUSED,

    /**
     * Cấp độ (Level) hiện tại đã hoàn thành thành công.
     * Hiển thị màn hình chuyển tiếp, chuẩn bị tải cấp độ tiếp theo.
     */
    LEVEL_COMPLETE,

    /**
     * Trò chơi kết thúc (Game Over) - hết mạng.
     * Hiển thị điểm số cuối cùng, cho phép nhập điểm cao.
     */
    GAME_OVER,

    /**
     * Hoàn thành tất cả các cấp độ - người chơi chiến thắng!
     * Hiển thị màn hình chiến thắng, điểm số cuối cùng.
     */
    WIN
}