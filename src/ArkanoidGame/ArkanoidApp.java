package ArkanoidGame;

import Engine.AudioManager;
import Engine.GameManager;
import Engine.GameState;
import Engine.HighScoreManager;
import Objects.GameEntities.Ball;
import Objects.GameEntities.Laser;
import Objects.PowerUps.PowerUp;
import Objects.Bricks.Brick;
import Render.CanvasRenderer;
import UI.Menu.MainMenu;
import UI.Screens.PauseScreen;
import UI.Screens.GameOverScreen;
import UI.Screens.WinScreen;
import Utils.Constants;
import Utils.SpriteCache;
import Utils.SpriteCacheProvider;
import Utils.SpriteProvider;
import Utils.AnimationFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.time.LocalDate;

public class ArkanoidApp extends Application {
    private static final int WIDTH = Constants.Window.WINDOW_WIDTH;
    private static final int HEIGHT = Constants.Window.WINDOW_HEIGHT;
    private static final int PLAY_AREA_WIDTH = Constants.PlayArea.PLAY_AREA_WIDTH; // UI bar phía trên
    private static final int PLAY_AREA_HEIGHT = Constants.PlayArea.PLAY_AREA_HEIGHT; // 650px

    private GameManager gameManager;
    private CanvasRenderer renderer;
    private GraphicsContext gc;
    private boolean spacePressed = false; // Prevent key repeat

    // UI Screens
    private MainMenu mainMenu;
    private PauseScreen pauseScreen;
    private GameOverScreen gameOverScreen;
    private WinScreen winScreen;
    private HighScoreManager highScoreManager;

    // Scene reference for input handling
    private Scene scene;

    @Override
    public void start(Stage stage) {
        // Initialize AudioManager FIRST
        AudioManager audioManager = AudioManager.getInstance();
        try {
            audioManager.initialize();
            System.out.println("AudioManager initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize AudioManager: " + e.getMessage());
            e.printStackTrace();
        }

        Canvas canvas = new Canvas(WIDTH, HEIGHT);

        // Use global sprite cache + provider and pass to CanvasRenderer
        SpriteCache spriteCache = SpriteCache.getInstance();

        // Ensure sprite cache is loaded before creating game objects/animations
        if (!spriteCache.isInitialized()) {
            spriteCache.initialize();
        }

        SpriteProvider sprites = new SpriteCacheProvider(spriteCache);

        // Initialize AnimationFactory with the sprite provider so animations
        // can be created during GameManager initialization.
        AnimationFactory.initialize(sprites);

        renderer = new CanvasRenderer(canvas, sprites);

        gc = canvas.getGraphicsContext2D();

        // GameManager with PLAY_AREA dimensions (excluding UI bar)
        gameManager = new GameManager();

        // Initialize HighScoreManager and UI Screens
        highScoreManager = new HighScoreManager();
        mainMenu = new MainMenu(gameManager.getStateManager(), sprites);
        pauseScreen = new PauseScreen(sprites);
        gameOverScreen = new GameOverScreen(sprites, highScoreManager);
        winScreen = new WinScreen(sprites, highScoreManager);

        Pane root = new Pane(canvas);
        scene = new Scene(root);

        // Keyboard input handlers
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            GameState currentState = gameManager.getStateManager().getState();

            // Route input based on current state
            switch (currentState) {
                case MENU:
                    mainMenu.handleKeyPressed(code);
                    break;

                case PLAYING:
                    handlePlayingInput(code);
                    break;

                case PAUSED:
                    handlePausedInput(code);
                    break;

                case GAME_OVER:
                case WIN:
                    if (code == KeyCode.ENTER) {
                        // Return to menu and reset game
                        gameManager.resetGame();
                        gameManager.getStateManager().setState(GameState.MENU);
                        mainMenu.onEnter();
                    }
                    break;
            }
        });

        scene.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();
            GameState currentState = gameManager.getStateManager().getState();

            if (currentState == GameState.MENU) {
                mainMenu.handleKeyReleased(code);
            } else if (currentState == GameState.PLAYING) {
                // Stop paddle movement
                if (code == KeyCode.LEFT || code == KeyCode.RIGHT) {
                    gameManager.paddle.stop();
                }

                // Reset space key state
                if (code == KeyCode.SPACE) {
                    spacePressed = false;
                }
            }
        });

        // Mouse input handlers
        scene.setOnMouseClicked(e -> {
            GameState currentState = gameManager.getStateManager().getState();
            if (currentState == GameState.MENU) {
                mainMenu.handleMouseClicked(e);
            }
        });

        scene.setOnMouseMoved(e -> {
            GameState currentState = gameManager.getStateManager().getState();
            if (currentState == GameState.MENU) {
                mainMenu.handleMouseMoved(e);
            }
        });

        stage.setScene(scene);
        stage.setTitle("Arkanoid");
        stage.setResizable(false);
        stage.show();

        // Main game loop (60 FPS)
        AnimationTimer loop = new AnimationTimer() {
            private long lastUpdateTime = 0;
            private GameState previousState = GameState.MENU;

            @Override
            public void handle(long now) {
                // Calculate delta time
                long deltaTime = (now - lastUpdateTime) / 1_000_000; // Convert to ms
                lastUpdateTime = now;

                GameState currentState = gameManager.getStateManager().getState();

                // Handle state transitions
                if (currentState != previousState) {
                    onStateChange(previousState, currentState);
                    previousState = currentState;
                }

                // Update based on state
                switch (currentState) {
                    case MENU:
                        mainMenu.update(deltaTime);
                        break;

                    case PLAYING:
                        gameManager.update();
                        break;

                    case PAUSED:
                        pauseScreen.update(deltaTime);
                        break;

                    case GAME_OVER:
                        gameOverScreen.update(deltaTime);
                        break;

                    case WIN:
                        winScreen.update(deltaTime);
                        break;

                    case LEVEL_COMPLETE:
                        // Optional: could add delay here
                        break;
                }

                // Render based on state
                renderer.clear();

                switch (currentState) {
                    case MENU:
                        // Render menu directly on canvas
                        mainMenu.render(gc);
                        break;

                    case PLAYING:
                        renderGameplay();
                        break;

                    case PAUSED:
                        // Render gameplay in background, then pause overlay
                        renderGameplay();
                        pauseScreen.render(gc);
                        break;

                    case GAME_OVER:
                        // Render game over screen
                        gameOverScreen.render(gc);
                        break;

                    case WIN:
                        // Render win screen
                        winScreen.render(gc);
                        break;

                    case LEVEL_COMPLETE:
                        renderGameplay();
                        renderer.drawLevelCompleteOverlay();
                        break;
                }

                renderer.present();
            }
        };
        loop.start();
    }

    /**
     * Called when game state changes.
     */
    private void onStateChange(GameState from, GameState to) {
        System.out.println("State changed: " + from + " -> " + to);

        // Handle exit from previous state
        switch (from) {
            case MENU:
                mainMenu.onExit();
                break;
            case PAUSED:
                pauseScreen.onExit();
                break;
            case GAME_OVER:
                gameOverScreen.onExit();
                break;
            case WIN:
                winScreen.onExit();
                break;
        }

        // Handle enter to new state
        switch (to) {
            case MENU:
                mainMenu.onEnter();
                break;

            case GAME_OVER:
                // Set game result info
                int finalScore = gameManager.getScore();
                int currentRound = gameManager.getRoundsManager().getCurrentRoundNumber();

                // Add to high scores if qualified
                if (highScoreManager.isHighScore(finalScore)) {
                    // TODO: Show name input dialog

                    String playerName = "PLAYER"; // For now, use default name
                    highScoreManager.addScore(playerName, finalScore, LocalDate.now());
                }

                gameOverScreen.setGameResult(finalScore, currentRound);
                gameOverScreen.onEnter();
                break;

            case WIN:
                // Set game result info
                int winScore = gameManager.getScore();
                int totalRounds = gameManager.getRoundsManager().getCurrentRoundNumber();

                // Add to high scores if qualified
                if (highScoreManager.isHighScore(winScore)) {
                    // TODO: Show name input dialog
                    String playerName = "PLAYER"; // For now, use default name
                    highScoreManager.addScore(playerName, winScore, LocalDate.now());
                }

                winScreen.setGameResult(winScore, totalRounds);
                winScreen.onEnter();
                break;
        }
    }

    /**
     * Render gameplay elements (called when in PLAYING or PAUSED state).
     */
    private void renderGameplay() {
        // ====== UI LAYER (Top) ======
        int highScore = highScoreManager.getHighestScore();
        renderer.drawUI(gameManager.getScore(), highScore, gameManager.getLives());

        // ====== GAME OBJECTS LAYER ======
        // Draw paddle
        renderer.drawPaddle(gameManager.paddle);

        // Draw ALL balls (multi-ball support)
        for (Ball ball : gameManager.balls) {
            renderer.drawBall(ball);
        }

        // Draw ALL lasers
        for (Laser laser : gameManager.getLasers()) {
            if (laser.isAlive()) {
                renderer.drawLaser(laser);
            }
        }

        // Draw bricks
        for (Brick brick : gameManager.bricks) {
            if (brick.isAlive()) {
                renderer.drawBrick(brick);
            }
        }

        // Draw PowerUps with animation
        for (PowerUp powerUp : gameManager.getPowerUpManager().getActivePowerUps()) {
            renderer.drawPowerUp(powerUp);
        }
    }

    /**
     * Handle input during PLAYING state.
     */
    private void handlePlayingInput(KeyCode code) {
        // Movement controls
        if (code == KeyCode.LEFT) {
            gameManager.paddle.moveLeft();
        }
        if (code == KeyCode.RIGHT) {
            gameManager.paddle.moveRight();
        }

        // Space bar: Launch ball OR shoot laser
        if (code == KeyCode.SPACE && !spacePressed) {
            spacePressed = true;

            if (gameManager.isAttached()) {
                // Launch ball if attached
                gameManager.launchBall();
            } else if (gameManager.paddle.isLaserEnabled()) {
                // Shoot laser if laser power-up active
                gameManager.shootLaser();
            }
        }

        // ESC: Pause game
        if (code == KeyCode.ESCAPE) {
            // Set pause screen info before pausing
            pauseScreen.setGameInfo(
                    gameManager.getRoundsManager().getCurrentRoundNumber(),
                    gameManager.getRoundsManager().getCurrentRoundName(),
                    gameManager.getScore(),
                    gameManager.getLives()
            );
            gameManager.getStateManager().setState(GameState.PAUSED);
            pauseScreen.onEnter();
        }

        // R: Restart game (debug)
        if (code == KeyCode.R) {
            gameManager.resetGame();
            System.out.println("Game RESTARTED");
        }
    }

    /**
     * Handle input during PAUSED state.
     */
    private void handlePausedInput(KeyCode code) {
        if (code == KeyCode.SPACE) {
            // Resume game
            gameManager.getStateManager().setState(GameState.PLAYING);
            pauseScreen.onExit();
        } else if (code == KeyCode.ESCAPE) {
            // Return to menu
            gameManager.resetGame();
            gameManager.getStateManager().setState(GameState.MENU);
            pauseScreen.onExit();
            mainMenu.onEnter();
        }
    }

    /**
     * Draws round information at top of play area.
     */
    // Removed - now using UI screens

    /**
     * Draws debug information (ball count, laser count, etc.).
     */
    // Removed - can add back if needed

    // Old overlay methods removed - now using UI screens

    public static void main(String[] args) {
        launch(args);
    }
}
