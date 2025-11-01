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

/**
 * Lớp chính khởi chạy ứng dụng Arkanoid, thiết lập môi trường JavaFX,
 * vòng lặp game, và quản lý các sự kiện input cũng như chuyển đổi trạng thái (GameState).
 */
public class ArkanoidApp extends Application {
    // Hằng số kích thước cửa sổ
    private static final int WIDTH = Constants.Window.WINDOW_WIDTH;
    private static final int HEIGHT = Constants.Window.WINDOW_HEIGHT;
    private static final int PLAY_AREA_WIDTH = Constants.PlayArea.PLAY_AREA_WIDTH;
    private static final int PLAY_AREA_HEIGHT = Constants.PlayArea.PLAY_AREA_HEIGHT;

    private GameManager gameManager;
    private CanvasRenderer renderer;
    private GraphicsContext gc;
    private boolean spacePressed = false; // Ngăn chặn lặp lại phím

    // Các màn hình UI
    private MainMenu mainMenu;
    private PauseScreen pauseScreen;
    private GameOverScreen gameOverScreen;
    private WinScreen winScreen;
    private HighScoreManager highScoreManager;

    // Tham chiếu Scene để xử lý input
    private Scene scene;

    /**
     * Phương thức khởi tạo chính của ứng dụng JavaFX.
     * @param stage Stage chính của ứng dụng.
     */
    @Override
    public void start(Stage stage) {
        // Khởi tạo AudioManager đầu tiên
        AudioManager audioManager = AudioManager.getInstance();
        try {
            audioManager.initialize();
            System.out.println("AudioManager initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize AudioManager: " + e.getMessage());
            e.printStackTrace();
        }

        Canvas canvas = new Canvas(WIDTH, HEIGHT);

        // Khởi tạo Sprite Cache và Provider
        SpriteCache spriteCache = SpriteCache.getInstance();
        if (!spriteCache.isInitialized()) {
            spriteCache.initialize();
        }
        SpriteProvider sprites = new SpriteCacheProvider(spriteCache);

        // Khởi tạo AnimationFactory
        AnimationFactory.initialize(sprites);

        renderer = new CanvasRenderer(canvas, sprites);

        gc = canvas.getGraphicsContext2D();

        // Khởi tạo quản lý game và High Score
        gameManager = new GameManager();
        highScoreManager = new HighScoreManager();

        // Khởi tạo các màn hình UI
        mainMenu = new MainMenu(gameManager.getStateManager(), sprites);
        pauseScreen = new PauseScreen(sprites);
        gameOverScreen = new GameOverScreen(sprites, highScoreManager);
        winScreen = new WinScreen(sprites, highScoreManager);

        Pane root = new Pane(canvas);
        scene = new Scene(root);

        // ====== Xử lý Input Keyboard ======
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            GameState currentState = gameManager.getStateManager().getState();

            // Định tuyến input dựa trên trạng thái hiện tại
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
                        // Trở về menu và reset game
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
                // Dừng di chuyển paddle khi nhả phím
                if (code == KeyCode.LEFT || code == KeyCode.RIGHT) {
                    gameManager.paddle.stop();
                }

                // Đặt lại trạng thái phím cách
                if (code == KeyCode.SPACE) {
                    spacePressed = false;
                }
            }
        });

        // ====== Xử lý Input Mouse ======
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

        // ====== Vòng Lặp Game Chính (60 FPS) ======
        AnimationTimer loop = new AnimationTimer() {
            private long lastUpdateTime = 0;
            private GameState previousState = GameState.MENU;

            @Override
            public void handle(long now) {
                // Tính toán delta time (thời gian trôi qua)
                long deltaTime = (now - lastUpdateTime) / 1_000_000; // Chuyển sang ms
                lastUpdateTime = now;

                GameState currentState = gameManager.getStateManager().getState();

                // Xử lý chuyển đổi trạng thái
                if (currentState != previousState) {
                    onStateChange(previousState, currentState);
                    previousState = currentState;
                }

                // Cập nhật logic dựa trên trạng thái
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
                }

                // Render dựa trên trạng thái
                renderer.clear();

                switch (currentState) {
                    case MENU:
                        mainMenu.render(gc);
                        break;

                    case PLAYING:
                        renderGameplay();
                        break;

                    case PAUSED:
                        // Render gameplay trước, sau đó là lớp phủ Pause
                        renderGameplay();
                        pauseScreen.render(gc);
                        break;

                    case GAME_OVER:
                        gameOverScreen.render(gc);
                        break;

                    case WIN:
                        winScreen.render(gc);
                        break;

                    case LEVEL_COMPLETE:
                        // Render gameplay và thông báo hoàn thành màn
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
     * Phương thức được gọi khi trạng thái game thay đổi.
     * Xử lý logic vào/ra (onEnter/onExit) cho các màn hình.
     *
     * @param from Trạng thái cũ.
     * @param to Trạng thái mới.
     */
    private void onStateChange(GameState from, GameState to) {
        System.out.println("State changed: " + from + " -> " + to);

        // Xử lý thoát khỏi trạng thái cũ
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

        // Xử lý vào trạng thái mới
        switch (to) {
            case MENU:
                mainMenu.onEnter();
                break;

            case GAME_OVER:
            case WIN:
                // Logic chung cho GAME_OVER và WIN
                int finalScore = gameManager.getScore();
                int currentRound = gameManager.getRoundsManager().getCurrentRoundNumber();

                // Kiểm tra và thêm vào High Score
                if (highScoreManager.isHighScore(finalScore)) {
                    // TODO: Hiển thị dialog nhập tên người chơi
                    String playerName = "PLAYER"; // Tạm thời dùng tên mặc định
                    highScoreManager.addScore(playerName, finalScore, LocalDate.now());
                }

                if (to == GameState.GAME_OVER) {
                    gameOverScreen.setGameResult(finalScore, currentRound);
                    gameOverScreen.onEnter();
                } else { // GameState.WIN
                    winScreen.setGameResult(finalScore, currentRound);
                    winScreen.onEnter();
                }
                break;
        }
    }

    /**
     * Render tất cả các thành phần gameplay (thực thể game và UI).
     */
    private void renderGameplay() {
        // ====== Lớp UI (trên cùng) ======
        int highScore = highScoreManager.getHighestScore();
        renderer.drawUI(gameManager.getScore(), highScore, gameManager.getLives());

        // ====== Lớp Đối Tượng Game ======
        // Vẽ paddle
        renderer.drawPaddle(gameManager.paddle);

        // Vẽ tất cả các quả bóng
        for (Ball ball : gameManager.balls) {
            renderer.drawBall(ball);
        }

        // Vẽ tất cả các tia laser đang hoạt động
        for (Laser laser : gameManager.getLasers()) {
            if (laser.isAlive()) {
                renderer.drawLaser(laser);
            }
        }

        // Vẽ gạch
        for (Brick brick : gameManager.bricks) {
            if (brick.isAlive()) {
                renderer.drawBrick(brick);
            }
        }

        // Vẽ PowerUps
        for (PowerUp powerUp : gameManager.getPowerUpManager().getActivePowerUps()) {
            renderer.drawPowerUp(powerUp);
        }
    }

    /**
     * Xử lý input bàn phím khi game đang ở trạng thái PLAYING.
     */
    private void handlePlayingInput(KeyCode code) {
        // Điều khiển di chuyển
        if (code == KeyCode.LEFT) {
            gameManager.paddle.moveLeft();
        }
        if (code == KeyCode.RIGHT) {
            gameManager.paddle.moveRight();
        }

        // Phím Space: Phóng bóng HOẶC bắn laser
        if (code == KeyCode.SPACE && !spacePressed) {
            spacePressed = true;

            if (gameManager.isAttached()) {
                gameManager.launchBall(); // Phóng bóng
            } else if (gameManager.paddle.isLaserEnabled()) {
                gameManager.shootLaser(); // Bắn laser
            }
        }

        // ESC: Tạm dừng game
        if (code == KeyCode.ESCAPE) {
            // Cài đặt thông tin game cho màn hình Pause
            pauseScreen.setGameInfo(
                    gameManager.getRoundsManager().getCurrentRoundNumber(),
                    gameManager.getRoundsManager().getCurrentRoundName(),
                    gameManager.getScore(),
                    gameManager.getLives()
            );
            gameManager.getStateManager().setState(GameState.PAUSED);
            pauseScreen.onEnter();
        }

        // R: Khởi động lại game (Debug)
        if (code == KeyCode.R) {
            gameManager.resetGame();
            System.out.println("Game RESTARTED");
        }
    }

    /**
     * Xử lý input bàn phím khi game đang ở trạng thái PAUSED.
     */
    private void handlePausedInput(KeyCode code) {
        if (code == KeyCode.SPACE) {
            // Tiếp tục game
            gameManager.getStateManager().setState(GameState.PLAYING);
            pauseScreen.onExit();
        } else if (code == KeyCode.ESCAPE) {
            // Trở về menu
            gameManager.resetGame();
            gameManager.getStateManager().setState(GameState.MENU);
            pauseScreen.onExit();
            mainMenu.onEnter();
        }
    }

    /**
     * Phương thức main để khởi chạy ứng dụng JavaFX.
     * @param args Tham số dòng lệnh.
     */
    public static void main(String[] args) {
        launch(args);
    }
}