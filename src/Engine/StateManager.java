package Engine;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Lớp StateManager quản lý trạng thái hiện tại của trò chơi và kiểm soát
 * các chuyển đổi hợp lệ giữa các trạng thái (Finite State Machine).
 */
public class StateManager {
    /** Trạng thái hiện tại của trò chơi. */
    private GameState currentState;
    /** Trạng thái trước đó của trò chơi. */
    private GameState previousState;

    /** Bản đồ lưu trữ các quy tắc chuyển trạng thái hợp lệ. */
    private final Map<GameState, Set<GameState>> validTransitions;

    /**
     * Khởi tạo StateManager.
     * Thiết lập trạng thái ban đầu là MENU và khởi tạo các quy tắc chuyển đổi.
     */
    public StateManager() {
        this.currentState = GameState.MENU; // Trạng thái mặc định khi khởi tạo
        this.previousState = null;
        this.validTransitions = new HashMap<>();

        initializeTransitionRules();
    }

    /**
     * Thiết lập các quy tắc chuyển đổi hợp lệ giữa các trạng thái.
     * (Ví dụ: từ PLAYING có thể chuyển sang PAUSED, LEVEL_COMPLETE, GAME_OVER, WIN).
     */
    private void initializeTransitionRules() {
        // Từ MENU chỉ có thể bắt đầu trò chơi (PLAYING)
        validTransitions.put (GameState.MENU,
                EnumSet.of(GameState.PLAYING));

        // Từ PLAYING có thể chuyển sang: Tạm dừng, Hoàn thành Level, Game Over, Thắng
        validTransitions.put(GameState.PLAYING,
                EnumSet.of(GameState.PAUSED, GameState.LEVEL_COMPLETE, GameState.GAME_OVER, GameState.WIN));

        // Từ PAUSED có thể chuyển sang: Chơi tiếp, hoặc Quay lại Menu (Thoát)
        validTransitions.put(GameState.PAUSED,
                EnumSet.of(GameState.PLAYING, GameState.MENU));

        // Từ LEVEL_COMPLETE có thể chuyển sang: Chơi tiếp (Next Level), hoặc Thắng (Nếu hết Level)
        validTransitions.put(GameState.LEVEL_COMPLETE,
                EnumSet.of(GameState.PLAYING, GameState.WIN));

        // Từ GAME_OVER chỉ có thể chuyển sang MENU (để khởi động lại)
        validTransitions.put(GameState.GAME_OVER,
                EnumSet.of(GameState.MENU));

        // Từ WIN chỉ có thể chuyển sang MENU (để khởi động lại)
        validTransitions.put(GameState.WIN,
                EnumSet.of(GameState.MENU));
    }

    /**
     * Cố gắng chuyển trạng thái trò chơi sang trạng thái mới.
     * Kiểm tra tính hợp lệ của việc chuyển đổi theo quy tắc đã định nghĩa.
     * @param newState Trạng thái mới muốn chuyển đến.
     * @return true nếu chuyển trạng thái thành công, false nếu chuyển đổi không hợp lệ.
     */
    public boolean setState(GameState newState) {
        if (newState == null) {
            System.err.println("StateManager: Cannot transition to null state");
            return false;
        }

        // Nếu trạng thái mới giống trạng thái hiện tại, không làm gì cả
        if (currentState == newState) {
            return true;
        }

        // Kiểm tra tính hợp lệ của chuyển đổi
        if (!canTransitionTo(currentState, newState)) {
            System.err.println("StateManager: Invalid transition from " + currentState + " to " + newState);
            return false;
        }

        System.out.println("StateManager: Transitioning from " + currentState + " to " + newState);

        onStateExit(currentState); // Xử lý logic khi thoát trạng thái cũ
        previousState = currentState; // Lưu trạng thái hiện tại làm trạng thái trước đó
        currentState = newState; // Cập nhật trạng thái mới
        onStateEnter(currentState); // Xử lý logic khi vào trạng thái mới
        return true;
    }

    /**
     * Kiểm tra xem việc chuyển từ trạng thái 'from' sang trạng thái 'to' có hợp lệ không.
     * @param from Trạng thái bắt đầu.
     * @param to Trạng thái đích.
     * @return true nếu chuyển đổi hợp lệ, ngược lại là false.
     */
    public boolean canTransitionTo(GameState from, GameState to) {
        Set<GameState> allowed = validTransitions.get(from);
        // Kiểm tra xem trạng thái bắt đầu có quy tắc và trạng thái đích có nằm trong tập hợp cho phép không
        return allowed != null && allowed.contains(to);
    }

    /**
     * Xử lý các hành động cần thực hiện khi vào một trạng thái mới.
     * @param state Trạng thái vừa được chuyển đến.
     */
    private void onStateEnter(GameState state) {
        // Logic cụ thể có thể được mở rộng ở đây (ví dụ: hiển thị menu, tải level, phát nhạc)
        switch (state) {
            case PAUSED:
                System.out.println("Game paused.");
                break;
            case PLAYING:
                System.out.println("Game resumed/started.");
                break;
            case LEVEL_COMPLETE:
                System.out.println("Level completed!");
                break;
            case GAME_OVER:
                System.out.println("Game over!");
                break;
            case WIN:
                System.out.println("You win!");
                break;
            case MENU:
                System.out.println("Returned to menu.");
                break;
            default:
                break;
        }
    }

    /**
     * Xử lý các hành động cần thực hiện khi thoát khỏi một trạng thái.
     * @param state Trạng thái vừa bị thoát.
     */
    private void onStateExit(GameState state) {
        // Logic cụ thể có thể được mở rộng ở đây (ví dụ: ẩn menu, lưu điểm số, dừng animation)
        switch (state) {
            case PAUSED:
                System.out.println("Exiting pause.");
                break;
            case PLAYING:
                System.out.println("Exiting playing state.");
                break;
            case LEVEL_COMPLETE:
                System.out.println("Exiting level complete state.");
                break;
            case GAME_OVER:
                System.out.println("Exiting game over state.");
                break;
            case WIN:
                System.out.println("Exiting win state.");
                break;
            case MENU:
                System.out.println("Exiting menu.");
                break;
            default:
                break;
        }
    }

    /**
     * Lấy trạng thái hiện tại của trò chơi.
     * @return Trạng thái GameState hiện tại.
     */
    public GameState getState() {
        return currentState;
    }

    /**
     * Lấy trạng thái trước đó của trò chơi.
     * @return Trạng thái GameState trước đó.
     */
    public GameState getPreviousState() {
        return previousState;
    }

    /**
     * Kiểm tra xem trò chơi có đang ở trạng thái PLAYING (đang chơi) không.
     * @return true nếu currentState là PLAYING.
     */
    public boolean isPlaying() {
        return currentState == GameState.PLAYING;
    }

    /**
     * Kiểm tra xem trò chơi có đang ở trạng thái PAUSED (tạm dừng) không.
     * @return true nếu currentState là PAUSED.
     */
    public boolean isPaused() {
        return currentState == GameState.PAUSED;
    }

    /**
     * Kiểm tra xem trò chơi có đang ở trạng thái GAME_OVER (kết thúc) không.
     * @return true nếu currentState là GAME_OVER.
     */
    public boolean isGameOver() {
        // Lưu ý: Có thể xem xét thêm trạng thái WIN vào đây nếu muốn kiểm tra điều kiện kết thúc game chung
        return currentState == GameState.GAME_OVER;
    }
}