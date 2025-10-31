package Engine;

import Audio.MusicTrack;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StateManager {
    private GameState currentState;
    private GameState previousState;
    private final AudioManager audioManager;

    private final Map<GameState, Set<GameState>> validTransitions;

    public StateManager() {
        this.currentState = GameState.MENU;
        this.previousState = null;
        this.audioManager = AudioManager.getInstance();
        this.validTransitions = new HashMap<>();

        initializeTransitionRules();

        // Phát nhạc menu lúc khởi động
        onStateEnter(GameState.MENU);
    }

    private void initializeTransitionRules() {
        validTransitions.put (GameState.MENU,
                EnumSet.of(GameState.PLAYING));

        validTransitions.put(GameState.PLAYING,
                EnumSet.of(GameState.PAUSED, GameState.LEVEL_COMPLETE, GameState.GAME_OVER, GameState.WIN));

        validTransitions.put(GameState.PAUSED,
                EnumSet.of(GameState.PLAYING, GameState.MENU));

        validTransitions.put(GameState.LEVEL_COMPLETE,
                EnumSet.of(GameState.PLAYING, GameState.WIN));

        validTransitions.put(GameState.GAME_OVER,
                EnumSet.of(GameState.MENU));

        validTransitions.put(GameState.WIN,
                EnumSet.of(GameState.MENU));
    }

    public boolean setState(GameState newState) {
        if (newState == null) {
            System.err.println("StateManager: Cannot transition to null state");
            return false;
        }

        if (currentState == newState) {
            return true;
        }
        if (!canTransitionTo(currentState, newState)) {
            System.err.println("StateManager: Invalid transition from " + currentState + " to " + newState);
            return false;
        }

        System.out.println("StateManager: Transitioning from " + currentState + " to " + newState);

        onStateExit(currentState);
        previousState = currentState;
        currentState = newState;
        onStateEnter(currentState);
        return true;
    }

    public boolean canTransitionTo(GameState from, GameState to) {
        Set<GameState> allowed = validTransitions.get(from);
        return allowed != null && allowed.contains(to);
    }

    private void onStateEnter(GameState state) {
        switch (state) {
            case MENU:
                System.out.println("Returned to menu.");
                audioManager.playMusic(MusicTrack.MENU);
                break;
            case PLAYING:
                System.out.println("Game resumed/started.");
                // Nếu từ PAUSED sang PLAYING thì resume, còn không thì play mới
                if (previousState == GameState.PAUSED) {
                    audioManager.resumeMusic();
                } else {
                    audioManager.playMusic(MusicTrack.ROUNDS);
                }
                break;
            case PAUSED:
                System.out.println("Game paused.");
                audioManager.pauseMusic();
                break;
            case LEVEL_COMPLETE:
                System.out.println("Level completed!");
                // Tiếp tục phát nhạc ROUNDS
                break;
            case GAME_OVER:
                System.out.println("Game over!");
                audioManager.playMusic(MusicTrack.GAME_OVER);
                break;
            case WIN:
                System.out.println("You win!");
                audioManager.playMusic(MusicTrack.VICTORY);
                break;
            default:
                break;
        }
    }

    private void onStateExit(GameState state) {
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

    public GameState getState() {
        return currentState;
    }

    public GameState getPreviousState() {
        return previousState;
    }

    public boolean isPlaying() {
        return currentState == GameState.PLAYING;
    }

    public boolean isPaused() {
        return currentState == GameState.PAUSED;
    }

    public boolean isGameOver() {
        return currentState == GameState.GAME_OVER;
    }

    /**
     * Lấy reference đến AudioManager.
     * @return AudioManager instance
     */
    public AudioManager getAudioManager() {
        return audioManager;
    }

}
