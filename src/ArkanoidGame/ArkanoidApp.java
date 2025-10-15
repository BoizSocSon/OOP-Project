package ArkanoidGame;

import Engine.GameManager;
import Engine.GameState;
import Objects.GameEntities.Ball;
import Objects.GameEntities.Laser;
import Objects.PowerUps.PowerUp;
import Objects.Bricks.Brick;
import Render.CanvasRenderer;
import Render.Renderer;
import Utils.FileManager;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Arkanoid Application - Main entry point (TUẦN 2 REFACTORED).
 *
 * New Features:
 * - Multi-ball support (renders all balls)
 * - Laser shooting system (Space bar)
 * - State management integration
 * - Round progression display
 * - Pause functionality (ESC)
 * - Score multiplier display
 *
 * Controls:
 * - LEFT/RIGHT: Move paddle
 * - SPACE: Launch ball OR Shoot laser (if laser enabled)
 * - ESC: Pause/Unpause
 * - R: Restart (when game over)
 *
 * @author SteveHoang aka BoizSocSon
 */
public class ArkanoidApp extends Application {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;
    private static final int UI_BAR_HEIGHT = 150; // UI bar phía trên
    private static final int PLAY_AREA_HEIGHT = HEIGHT - UI_BAR_HEIGHT; // 650px

    private GameManager gameManager;
    private Renderer renderer;
    private boolean spacePressed = false; // Prevent key repeat

    /**
     * JavaFX entry point - initializes UI, event handlers, and game loop.
     *
     * NEW FEATURES (Tuần 2):
     * - Space bar: Launch ball OR shoot laser (if enabled)
     * - ESC: Pause/unpause game
     * - Multi-ball rendering
     * - Laser rendering
     * - Round info display
     *
     * @param stage Main JavaFX window
     */
    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        renderer = new CanvasRenderer(canvas);
        
        // GameManager with PLAY_AREA dimensions (excluding UI bar)
        gameManager = new GameManager(WIDTH, PLAY_AREA_HEIGHT);
        
        // Start in PLAYING state for testing
        gameManager.getStateManager().setState(GameState.PLAYING);

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);

        // Keyboard input handlers
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            
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
                
                if (gameManager.ballAttached) {
                    // Launch ball if attached
                    gameManager.launchBall();
                } else if (gameManager.paddle.isLaserEnabled()) {
                    // Shoot laser if laser power-up active
                    gameManager.shootLaser();
                }
            }
            
            // ESC: Pause/unpause
            if (code == KeyCode.ESCAPE) {
                GameState currentState = gameManager.getStateManager().getState();
                if (currentState == GameState.PLAYING) {
                    gameManager.getStateManager().setState(GameState.PAUSED);
                    System.out.println("Game PAUSED");
                } else if (currentState == GameState.PAUSED) {
                    gameManager.getStateManager().setState(GameState.PLAYING);
                    System.out.println("Game RESUMED");
                }
            }
            
            // R: Restart game
            if (code == KeyCode.R) {
                if (gameManager.isGameOver()) {
                    gameManager.reset();
                    gameManager.getStateManager().setState(GameState.PLAYING);
                    System.out.println("Game RESTARTED");
                }
            }
        });
        
        scene.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();
            
            // Stop paddle movement
            if (code == KeyCode.LEFT || code == KeyCode.RIGHT) {
                gameManager.paddle.stop();
            }
            
            // Reset space key state
            if (code == KeyCode.SPACE) {
                spacePressed = false;
            }
        });

        stage.setScene(scene);
        stage.setTitle("Arkanoid");
        stage.setResizable(false);
        stage.show();

        // Main game loop (60 FPS)
        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update game logic
                gameManager.update();
                
                // Clear canvas
                renderer.clear();
                
                // ====== UI LAYER (Top) ======
                if (renderer instanceof CanvasRenderer) {
                    CanvasRenderer cr = (CanvasRenderer) renderer;
                    
                    // Top UI bar (logo, score, high score)
                    int highScore = FileManager.loadHighscore();
                    cr.drawTopUI(gameManager.getScore(), highScore);
                    
                    // Draw borders around play area
                    cr.drawBorders();
                }
                
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
                        laser.render(renderer);
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
                
                // ====== HUD LAYER ======
                if (renderer instanceof CanvasRenderer) {
                    CanvasRenderer cr = (CanvasRenderer) renderer;
                    
                    // Lives display (bottom left)
                    cr.drawLivesDisplay(gameManager.getLives());
                    
                    // Round info (top of play area)
//                    drawRoundInfo(cr);
                    
                    // Debug info (optional)
//                    drawDebugInfo(cr);
                }
                
                // ====== OVERLAY LAYER ======
                drawStateOverlays();
                
                // Present frame
                renderer.present();
            }
        };
        loop.start();
    }
    
    /**
     * Draws round information at top of play area.
     */
    private void drawRoundInfo(CanvasRenderer cr) {
        String roundInfo = gameManager.getRoundsManager().getRoundInfo();
        renderer.drawText(roundInfo, 20, UI_BAR_HEIGHT + 20);
    }
    
    /**
     * Draws debug information (ball count, laser count, etc.).
     */
    private void drawDebugInfo(CanvasRenderer cr) {
        int yOffset = UI_BAR_HEIGHT + 40;
        
        // Ball count
        renderer.drawText("Balls: " + gameManager.balls.size(), 20, yOffset);
        
        // Laser count
        renderer.drawText("Lasers: " + gameManager.getLasers().size(), 20, yOffset + 15);
        
        // Laser shots remaining
        if (gameManager.paddle.isLaserEnabled()) {
            renderer.drawText("Laser Shots: " + gameManager.paddle.getLaserShots(), 20, yOffset + 30);
        }
        
        // Score multiplier
        int multiplier = gameManager.getScoreManager().getMultiplier();
        if (multiplier > 1) {
            renderer.drawText("Multiplier: " + multiplier + "x", 20, yOffset + 45);
        }
    }
    
    /**
     * Draws state-specific overlays (pause, game over, win).
     */
    private void drawStateOverlays() {
        GameState state = gameManager.getStateManager().getState();
        
        switch (state) {
            case PAUSED:
                drawPauseOverlay();
                break;
                
            case GAME_OVER:
                drawGameOverOverlay();
                break;
                
            case WIN:
                drawWinOverlay();
                break;
                
            case LEVEL_COMPLETE:
                drawLevelCompleteOverlay();
                break;
                
            default:
                // No overlay for MENU, PLAYING
                break;
        }
    }
    
    /**
     * Draws pause overlay.
     */
    private void drawPauseOverlay() {
        renderer.drawText("════════════════════", 210, 350);
        renderer.drawText("PAUSED", 270, 380);
        renderer.drawText("Press ESC to resume", 220, 410);
        renderer.drawText("════════════════════", 210, 440);
    }
    
    /**
     * Draws game over overlay.
     */
    private void drawGameOverOverlay() {
        renderer.drawText("════════════════════", 210, 300);
        renderer.drawText("GAME OVER", 250, 330);
        renderer.drawText("Final Score: " + gameManager.getScore(), 230, 360);
        renderer.drawText("Press 'R' to restart", 220, 390);
        renderer.drawText("════════════════════", 210, 420);
    }
    
    /**
     * Draws win overlay.
     */
    private void drawWinOverlay() {
        renderer.drawText("════════════════════", 210, 300);
        renderer.drawText("★ YOU WIN! ★", 240, 330);
        renderer.drawText("Final Score: " + gameManager.getScore(), 230, 360);
        renderer.drawText("All rounds completed!", 210, 390);
        renderer.drawText("Press 'R' to restart", 220, 420);
        renderer.drawText("════════════════════", 210, 450);
    }
    
    /**
     * Draws level complete overlay.
     */
    private void drawLevelCompleteOverlay() {
        renderer.drawText("════════════════════", 210, 350);
        renderer.drawText("LEVEL COMPLETE!", 230, 380);
        renderer.drawText("Loading next round...", 215, 410);
        renderer.drawText("════════════════════", 210, 440);
    }

    /**
     * Hàm main tiêu chuẩn để khởi chạy ứng dụng JavaFX.
     * @param args đối số dòng lệnh (nếu có)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
