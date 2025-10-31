package Engine;

import Audio.MusicTrack;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Lớp quản lý trạng thái (StateManager) chịu trách nhiệm theo dõi và quản lý
 * các trạng thái hiện tại của trò chơi (GameState) và đảm bảo các quá trình
 * chuyển đổi giữa các trạng thái là hợp lệ.
 */
public class StateManager {
    private GameState currentState; // Trạng thái hiện tại của game.
    private GameState previousState; // Trạng thái trước đó.
    private final AudioManager audioManager; // Tham chiếu đến AudioManager để xử lý nhạc nền khi chuyển trạng thái.

    // Map định nghĩa các quy tắc chuyển đổi hợp lệ: Key (Trạng thái BẮT ĐẦU) -> Value (Set các Trạng thái ĐÍCH hợp lệ).
    private final Map<GameState, Set<GameState>> validTransitions;

    /**
     * Khởi tạo StateManager.
     */
    public StateManager() {
        this.currentState = GameState.MENU; // Trạng thái ban đầu là MENU.
        this.previousState = null;
        this.audioManager = AudioManager.getInstance();
        this.validTransitions = new HashMap<>();

        initializeTransitionRules(); // Thiết lập các quy tắc chuyển đổi.

        onStateEnter(GameState.MENU); // Thực hiện các hành động khi vào trạng thái MENU.
    }

    /**
     * Khởi tạo các quy tắc chuyển đổi hợp lệ giữa các trạng thái game.
     */
    private void initializeTransitionRules() {
        // Từ MENU chỉ có thể chuyển sang PLAYING
        validTransitions.put (GameState.MENU,
                EnumSet.of(GameState.PLAYING));

        // Từ PLAYING có thể chuyển sang PAUSED, LEVEL_COMPLETE, GAME_OVER, WIN
        validTransitions.put(GameState.PLAYING,
                EnumSet.of(GameState.PAUSED, GameState.LEVEL_COMPLETE, GameState.GAME_OVER, GameState.WIN));

        // Từ PAUSED có thể chuyển về PLAYING (tiếp tục) hoặc MENU
        validTransitions.put(GameState.PAUSED,
                EnumSet.of(GameState.PLAYING, GameState.MENU));

        // Từ LEVEL_COMPLETE có thể chuyển sang PLAYING (vòng tiếp theo) hoặc WIN (nếu đó là vòng cuối)
        validTransitions.put(GameState.LEVEL_COMPLETE,
                EnumSet.of(GameState.PLAYING, GameState.WIN));

        // Từ GAME_OVER chỉ có thể chuyển về MENU
        validTransitions.put(GameState.GAME_OVER,
                EnumSet.of(GameState.MENU));

        // Từ WIN chỉ có thể chuyển về MENU
        validTransitions.put(GameState.WIN,
                EnumSet.of(GameState.MENU));
    }

    /**
     * Thực hiện chuyển đổi trạng thái game sang trạng thái mới.
     *
     * @param newState Trạng thái game mới.
     * @return {@code true} nếu chuyển đổi hợp lệ và thành công, ngược lại là {@code false}.
     */
    public boolean setState(GameState newState) {
        if (newState == null) {
            System.err.println("StateManager: Cannot transition to null state");
            return false;
        }

        // Nếu trạng thái mới trùng với trạng thái hiện tại, không làm gì.
        if (currentState == newState) {
            return true;
        }

        // Kiểm tra tính hợp lệ của chuyển đổi.
        if (!canTransitionTo(currentState, newState)) {
            System.err.println("StateManager: Invalid transition from " + currentState + " to " + newState);
            return false;
        }

        System.out.println("StateManager: Transitioning from " + currentState + " to " + newState);

        onStateExit(currentState); // Xử lý khi thoát trạng thái cũ.
        previousState = currentState; // Lưu trạng thái cũ.
        currentState = newState; // Cập nhật trạng thái mới.
        onStateEnter(currentState); // Xử lý khi vào trạng thái mới.
        return true;
    }

    /**
     * Kiểm tra xem việc chuyển đổi từ trạng thái {@code from} sang trạng thái {@code to} có hợp lệ không.
     *
     * @param from Trạng thái bắt đầu.
     * @param to Trạng thái đích.
     * @return {@code true} nếu chuyển đổi hợp lệ.
     */
    public boolean canTransitionTo(GameState from, GameState to) {
        Set<GameState> allowed = validTransitions.get(from);
        // Kiểm tra xem trạng thái đích có trong Set các trạng thái được phép chuyển đến từ trạng thái bắt đầu không.
        return allowed != null && allowed.contains(to);
    }

    /**
     * Xử lý các hành động cần thiết khi game VÀO một trạng thái mới.
     *
     * @param state Trạng thái vừa được vào.
     */
    private void onStateEnter(GameState state) {
        switch (state) {
            case MENU:
                System.out.println("Returned to menu.");
                audioManager.playMusic(MusicTrack.MENU); // Phát nhạc menu.
                break;
            case PLAYING:
                System.out.println("Game resumed/started.");
                // Nếu quay lại từ PAUSED, tiếp tục nhạc.
                if (previousState == GameState.PAUSED) {
                    audioManager.resumeMusic();
                }
                // Ngược lại (bắt đầu mới hoặc từ LEVEL_COMPLETE), phát nhạc vòng chơi.
                else {
                    audioManager.playMusic(MusicTrack.ROUNDS);
                }
                break;
            case PAUSED:
                System.out.println("Game paused.");
                audioManager.pauseMusic(); // Tạm dừng nhạc.
                break;
            case LEVEL_COMPLETE:
                System.out.println("Level completed!");
                // (Thêm logic xử lý điểm thưởng khi qua màn nếu cần)
                break;
            case GAME_OVER:
                System.out.println("Game over!");
                audioManager.playMusic(MusicTrack.GAME_OVER); // Phát nhạc Game Over.
                break;
            case WIN:
                System.out.println("You win!");
                audioManager.playMusic(MusicTrack.VICTORY); // Phát nhạc chiến thắng.
                break;
            default:
                break;
        }
    }

    /**
     * Xử lý các hành động cần thiết khi game THOÁT khỏi một trạng thái.
     *
     * @param state Trạng thái vừa được thoát.
     */
    private void onStateExit(GameState state) {
        switch (state) {
            case PAUSED:
                System.out.println("Exiting pause.");
                break;
            case PLAYING:
                System.out.println("Exiting playing state.");
                // (Có thể dừng các timer không cần thiết khi thoát PLAYING)
                break;
            case LEVEL_COMPLETE:
                System.out.println("Exiting level complete state.");
                break;
            case GAME_OVER:
                System.out.println("Exiting game over state.");
                // (Có thể xử lý lưu điểm cao tại đây)
                break;
            case WIN:
                System.out.println("Exiting win state.");
                break;
            case MENU:
                System.out.println("Exiting menu.");
                // (Có thể dừng nhạc MENU tại đây)
                break;
            default:
                break;
        }
    }

    /**
     * Lấy trạng thái game hiện tại.
     *
     * @return {@link GameState} hiện tại.
     */
    public GameState getState() {
        return currentState;
    }

    /**
     * Lấy trạng thái game trước đó.
     *
     * @return {@link GameState} trước đó, hoặc {@code null} nếu chưa có.
     */
    public GameState getPreviousState() {
        return previousState;
    }

    /**
     * Kiểm tra xem game có đang ở trạng thái PLAYING không.
     *
     * @return {@code true} nếu đang chơi.
     */
    public boolean isPlaying() {
        return currentState == GameState.PLAYING;
    }

    /**
     * Kiểm tra xem game có đang ở trạng thái PAUSED không.
     *
     * @return {@code true} nếu đang tạm dừng.
     */
    public boolean isPaused() {
        return currentState == GameState.PAUSED;
    }

    /**
     * Kiểm tra xem game có đang ở trạng thái GAME_OVER không.
     *
     * @return {@code true} nếu game kết thúc (hết mạng).
     */
    public boolean isGameOver() {
        return currentState == GameState.GAME_OVER;
    }

    /**
     * Lấy AudioManager instance.
     *
     * @return {@link AudioManager} instance.
     */
    public AudioManager getAudioManager() {
        return audioManager;
    }

}