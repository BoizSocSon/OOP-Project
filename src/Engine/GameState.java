package Engine;

/**
 * Enum định nghĩa các **trạng thái** khác nhau của trò chơi.
 *
 * Sơ đồ luồng trạng thái (State machine flow):
 * MENU → PLAYING ↔ PAUSED
 * PLAYING → LEVEL_COMPLETE → PLAYING (màn tiếp theo)
 * PLAYING → GAME_OVER (hết mạng)
 * PLAYING → WIN (hoàn thành tất cả các màn)
 * GAME_OVER/WIN → MENU (khởi động lại).
 */
public enum GameState {
    /**
     * **Menu chính** - Trò chơi chưa bắt đầu.
     * Hiển thị tiêu đề, điểm cao, tùy chọn.
     */
    MENU,

    /**
     * **Đang chơi** - Trò chơi đang hoạt động.
     * Bóng di chuyển, gạch bị phá hủy, v.v.
     */
    PLAYING,

    /**
     * **Tạm dừng** - Trò chơi đã được tạm dừng (ví dụ: nhấn ESC).
     * Tất cả các đối tượng trò chơi bị đóng băng, hiển thị menu tạm dừng.
     */
    PAUSED,

    /**
     * **Hoàn thành màn chơi** - Màn chơi hiện tại đã hoàn thành thành công.
     * Hiển thị màn hình chuyển tiếp, chuẩn bị cho màn chơi tiếp theo.
     */
    LEVEL_COMPLETE,

    /**
     * **Kết thúc trò chơi (Thua)** - Không còn mạng sống.
     * Hiển thị điểm số cuối cùng, màn hình nhập điểm cao.
     */
    GAME_OVER,

    /**
     * **Thắng cuộc** - Tất cả các màn chơi đã được hoàn thành.
     * Hiển thị màn hình chiến thắng, điểm số cuối cùng.
     */
    WIN
}