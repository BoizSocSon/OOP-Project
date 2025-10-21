package Engine;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * StateManager - Manages game state transitions and lifecycle callbacks.
 *
 * Responsibilities:
 * - Validate state transitions (state machine rules)
 * - Trigger onStateEnter/onStateExit callbacks
 * - Track current state
 * - Prevent invalid transitions
 *
 * Design:
 * - Uses state machine pattern
 * - Defines valid transitions in transition map
 * - Callbacks for music, UI, game logic changes
 *
 */
public class StateManager {
    private GameState currentState;
    private GameState previousState;

    // Valid transitions map (from state → set of allowed destination states)
    private final Map<GameState, Set<GameState>> validTransitions;

    /**
     * Creates a new StateManager starting in MENU state.
     */
    public StateManager() {
        this.currentState = GameState.MENU;
        this.previousState = null;
        this.validTransitions = new HashMap<>();

        initializeTransitionRules();
    }

    /**
     * Initializes the state machine transition rules.
     * Defines which states can transition to which other states.
     */
    private void initializeTransitionRules() {
        // MENU can go to PLAYING
        validTransitions.put(GameState.MENU,
                EnumSet.of(GameState.PLAYING));

        // PLAYING can go to PAUSED, LEVEL_COMPLETE, GAME_OVER
        validTransitions.put(GameState.PLAYING,
                EnumSet.of(GameState.PAUSED, GameState.LEVEL_COMPLETE, GameState.GAME_OVER));

        // PAUSED can only go back to PLAYING or MENU (quit)
        validTransitions.put(GameState.PAUSED,
                EnumSet.of(GameState.PLAYING, GameState.MENU));

        // LEVEL_COMPLETE can go to PLAYING (next level) or WIN (if last level)
        validTransitions.put(GameState.LEVEL_COMPLETE,
                EnumSet.of(GameState.PLAYING, GameState.WIN));

        // GAME_OVER can go to MENU (restart)
        validTransitions.put(GameState.GAME_OVER,
                EnumSet.of(GameState.MENU));

        // WIN can go to MENU (restart)
        validTransitions.put(GameState.WIN,
                EnumSet.of(GameState.MENU));
    }

    /**
     * Attempts to transition to a new state.
     * Validates transition, calls lifecycle callbacks.
     *
     * @param newState The state to transition to
     * @return true if transition successful, false if invalid
     */
    public boolean setState(GameState newState) {
        if (newState == null) {
            System.err.println("StateManager: Cannot transition to null state");
            return false;
        }

        // Check if already in this state
        if (currentState == newState) {
            return true; // Not an error, just no-op
        }

        // Validate transition
        if (!canTransition(currentState, newState)) {
            System.err.printf("StateManager: Invalid transition from %s to %s%n",
                    currentState, newState);
            return false;
        }

        // Perform transition
        System.out.printf("StateManager: Transitioning from %s to %s%n",
                currentState, newState);

        onStateExit(currentState);

        previousState = currentState;
        currentState = newState;

        onStateEnter(newState);

        return true;
    }

    /**
     * Checks if transition from one state to another is valid.
     *
     * @param from Source state
     * @param to Destination state
     * @return true if transition is allowed
     */
    public boolean canTransition(GameState from, GameState to) {
        Set<GameState> allowed = validTransitions.get(from);
        return allowed != null && allowed.contains(to);
    }

    /**
     * Called when entering a new state.
     * Handles state-specific initialization.
     *
     * @param state The state being entered
     */
    private void onStateEnter(GameState state) {
        switch (state) {
            case MENU:
                System.out.println("StateManager: Entering MENU");
                // AudioManager.playMusic(MENU) - to be implemented
                // Show menu UI
                break;

            case PLAYING:
                System.out.println("StateManager: Entering PLAYING");
                // AudioManager.playMusic(ROUND_MUSIC) - to be implemented
                // Hide menu UI, show game HUD
                break;

            case PAUSED:
                System.out.println("StateManager: Entering PAUSED");
                // Pause game loop (handled by GameManager)
                // Show pause overlay
                break;

            case LEVEL_COMPLETE:
                System.out.println("StateManager: Entering LEVEL_COMPLETE");
                // Show "Level Complete!" transition screen
                // Schedule next level load
                break;

            case GAME_OVER:
                System.out.println("StateManager: Entering GAME_OVER");
                // AudioManager.playMusic(GAME_OVER) - to be implemented
                // Show game over screen, final score
                break;

            case WIN:
                System.out.println("StateManager: Entering WIN");
                // AudioManager.playMusic(VICTORY) - to be implemented
                // Show victory screen, congratulations
                break;
        }
    }

    /**
     * Called when exiting a state.
     * Handles state-specific cleanup.
     *
     * @param state The state being exited
     */
    private void onStateExit(GameState state) {
        switch (state) {
            case MENU:
                System.out.println("StateManager: Exiting MENU");
                // Cleanup menu resources
                break;

            case PLAYING:
                System.out.println("StateManager: Exiting PLAYING");
                // Save game state if needed
                break;

            case PAUSED:
                System.out.println("StateManager: Exiting PAUSED");
                // Resume game loop
                break;

            case LEVEL_COMPLETE:
                System.out.println("StateManager: Exiting LEVEL_COMPLETE");
                // Cleanup transition screen
                break;

            case GAME_OVER:
                System.out.println("StateManager: Exiting GAME_OVER");
                // Cleanup game over screen
                break;

            case WIN:
                System.out.println("StateManager: Exiting WIN");
                // Cleanup victory screen
                break;
        }
    }

    /**
     * Gets the current game state.
     * @return Current state
     */
    public GameState getState() {
        return currentState;
    }

    /**
     * Gets the previous game state.
     * @return Previous state (null if none)
     */
    public GameState getPreviousState() {
        return previousState;
    }

    /**
     * Checks if game is in playing state (not paused, not in menu).
     * @return true if actively playing
     */
    public boolean isPlaying() {
        return currentState == GameState.PLAYING;
    }

    /**
     * Checks if game is paused.
     * @return true if paused
     */
    public boolean isPaused() {
        return currentState == GameState.PAUSED;
    }

    /**
     * Checks if game is over (either GAME_OVER or WIN).
     * @return true if game ended
     */
    public boolean isGameOver() {
        return currentState == GameState.GAME_OVER || currentState == GameState.WIN;
    }
}
