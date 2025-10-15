package Engine;

/**
 * Enum định nghĩa các trạng thái của game.
 * 
 * State machine flow:
 * MENU → PLAYING ↔ PAUSED
 * PLAYING → LEVEL_COMPLETE → PLAYING (next level)
 * PLAYING → GAME_OVER (no lives left)
 * PLAYING → WIN (all levels completed)
 * GAME_OVER/WIN → MENU (restart)
 * 
 * @author SteveHoang aka BoizSocSon
 */
public enum GameState {
    /**
     * Main menu - game not started yet.
     * Show title, high scores, options.
     */
    MENU,
    
    /**
     * Game is actively being played.
     * Ball moving, bricks being destroyed, etc.
     */
    PLAYING,
    
    /**
     * Game is paused (ESC pressed).
     * All game objects frozen, show pause menu.
     */
    PAUSED,
    
    /**
     * Level completed successfully.
     * Show transition screen, prepare next level.
     */
    LEVEL_COMPLETE,
    
    /**
     * Game over - no lives remaining.
     * Show final score, high score entry.
     */
    GAME_OVER,
    
    /**
     * All levels completed - player wins!
     * Show victory screen, final score.
     */
    WIN
}
