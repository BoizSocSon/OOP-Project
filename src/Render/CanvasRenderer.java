package Render;

import Objects.GameEntities.Ball;
import Objects.GameEntities.Laser;
import Objects.GameEntities.Paddle;
import Objects.Bricks.Brick;
import Objects.PowerUps.PowerUp;
import Utils.AssetLoader;
import Utils.Constants;
import Utils.SpriteProvider;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Lớp chịu trách nhiệm render (vẽ) tất cả các thành phần lên Canvas
 * của trò chơi, bao gồm cả các thực thể game và giao diện người dùng (UI).
 */
public class CanvasRenderer {
    // Canvas chính của game
    private final Canvas canvas;
    // Context đồ họa, dùng để vẽ
    private final GraphicsContext gc;
    // Renderer chuyên dụng để vẽ các sprite của thực thể game
    private final SpriteRenderer spriteRenderer;
    // Renderer chuyên dụng để vẽ viền (border)
    private final BorderRenderer borderRenderer;
    // Đối tượng cung cấp các sprite (hình ảnh)
    private final SpriteProvider sprites;

    // Font cho điểm số (Score)
    private Font scoreFont;
    // Font cho các phần tử UI khác
    private Font uiFont;

    /**
     * Khởi tạo CanvasRenderer.
     *
     * @param canvas Canvas của game.
     * @param sprites Đối tượng cung cấp sprite.
     */
    public CanvasRenderer(Canvas canvas, SpriteProvider sprites) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        // Khởi tạo các Renderer phụ
        this.spriteRenderer = new SpriteRenderer(gc, sprites);
        this.borderRenderer = new BorderRenderer(gc, sprites);
        this.sprites = sprites;
        // Tải font UI khi khởi tạo
        this.loadUIAssets();
    }

    /**
     * Xóa toàn bộ Canvas, tô màu nền đen.
     */
    public void clear() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Tải các font chữ tùy chỉnh cho UI. Nếu lỗi, sử dụng font mặc định.
     */
    private void loadUIAssets() {
        try {
            scoreFont = AssetLoader.loadFont("generation.ttf", 24);
            uiFont = AssetLoader.loadFont("emulogic.ttf", 18);
        } catch (Exception e) {
            // Sử dụng font mặc định nếu không tải được
            scoreFont = Font.font("Monospaced", 24);
            uiFont = Font.font("Monospaced", 18);
            System.out.println("CanvasRenderer: Failed to load custom fonts, using default.");
        }
    }

    /**
     * Vẽ giao diện người dùng (UI), bao gồm logo, điểm số, điểm cao nhất và mạng sống.
     *
     * @param score Điểm số hiện tại.
     * @param highScore Điểm cao nhất.
     * @param lives Số mạng sống còn lại.
     */
    public void drawUI(int score, int highScore, int lives) {
        // Vẽ Logo
        gc.drawImage(sprites.get("logo.png"),0,0);

        // Vẽ Score (1UP)
        gc.setFont(uiFont);
        gc.setFill(Color.RED);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("1UP", canvas.getWidth() * 0.82, 30); // Vị trí góc phải
        gc.setFont(scoreFont);
        gc.setFill(Color.GOLD);
        gc.fillText(String.valueOf(score), canvas.getWidth() * 0.82, 60);

        // Vẽ High Score
        gc.setFont(uiFont);
        gc.setFill(Color.RED);
        gc.fillText("HIGH SCORE", canvas.getWidth() * 0.82, 100);
        gc.setFont(scoreFont);
        gc.setFill(Color.GOLD);
        gc.fillText(String.valueOf(highScore), canvas.getWidth() * 0.82, 130);

        // Vẽ các cạnh viền
        borderRenderer.render();

        // Vẽ biểu tượng mạng sống (lives)
        for (int i = 0; i < lives; i++) {
            // Tính toán vị trí X cho mỗi biểu tượng
            double lifeX = Constants.Window.WINDOW_SIDE_OFFSET + i * (Constants.Paddle.PADDLE_LIFE_WIDTH + 10);
            // Vị trí Y cố định ở dưới cùng
            double lifeY = Constants.Window.WINDOW_HEIGHT - Constants.Paddle.PADDLE_LIFE_HEIGHT - 10;
            gc.drawImage(sprites.get("paddle_life.png"), lifeX, lifeY);
        }
    }

    /**
     * Vẽ overlay (lớp phủ) khi game tạm dừng (PAUSED).
     */
    public void drawPauseOverlay() {
        double cx = Constants.Window.WINDOW_WIDTH / 2.0;

        // Vẽ hình chữ nhật trong suốt làm nền
        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRoundRect(120, 320, 360, 160, 10, 10);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);

        // Vẽ chữ "PAUSED" và hướng dẫn
        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("════════════════════", cx, 350);
        gc.setFont(Font.font("Monospaced", 36));
        gc.fillText("PAUSED", cx, 390);
        gc.setFont(Font.font("Monospaced", 16));
        gc.fillText("Press ESC to resume", cx, 420);
        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("════════════════════", cx, 450);
    }

    /**
     * Vẽ overlay khi game kết thúc (GAME OVER).
     *
     * @param score Điểm số cuối cùng.
     */
    public void drawGameOverOverlay(int score) {
        double cx = Constants.Window.WINDOW_WIDTH / 2.0;
        // Vẽ nền overlay
        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRoundRect(120, 260, 360, 200, 10, 10);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);

        // Vẽ chữ "GAME OVER" và điểm số
        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("════════════════════", cx, 300);
        gc.setFont(Font.font("Monospaced", 36));
        gc.fillText("GAME OVER", cx, 340);
        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("Final Score: " + score, cx, 370);
        gc.fillText("Press 'R' to restart", cx, 400);
        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("════════════════════", cx, 430);
    }

    /**
     * Vẽ overlay khi người chơi chiến thắng (thắng tất cả các màn).
     *
     * @param score Điểm số cuối cùng.
     */
    public void drawWinOverlay(int score) {
        double cx = Constants.Window.WINDOW_WIDTH / 2.0;
        // Vẽ nền overlay
        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRoundRect(120, 260, 360, 220, 10, 10);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);

        // Vẽ chữ "YOU WIN!"
        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("════════════════════", cx, 300);
        gc.setFont(Font.font("Monospaced", 30));
        gc.fillText("★ YOU WIN! ★", cx, 340);
        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("Final Score: " + score, cx, 370);
        gc.fillText("All rounds completed!", cx, 400);
        gc.fillText("Press 'R' to restart", cx, 430);
        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("════════════════════", cx, 460);
    }

    /**
     * Vẽ overlay khi hoàn thành một màn chơi (chuyển sang màn tiếp theo).
     */
    public void drawLevelCompleteOverlay() {
        double cx = Constants.Window.WINDOW_WIDTH / 2.0;
        // Vẽ nền overlay
        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRoundRect(120, 320, 360, 160, 10, 10);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);

        // Vẽ thông báo "LEVEL COMPLETE!"
        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("════════════════════", cx, 350);
        gc.setFont(Font.font("Monospaced", 28));
        gc.fillText("LEVEL COMPLETE!", cx, 390);
        gc.setFont(Font.font("Monospaced", 16));
        gc.fillText("Loading next round...", cx, 420);
        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("════════════════════", cx, 450);
    }

    // Các phương thức vẽ thực thể game sử dụng SpriteRenderer

    /**
     * Vẽ quả bóng.
     * @param ball Đối tượng Ball.
     */
    public void drawBall(Ball ball) {
        spriteRenderer.drawBall(ball);
    }

    /**
     * Vẽ tia laser.
     * @param laser Đối tượng Laser.
     */
    public void drawLaser(Laser laser) {
        spriteRenderer.drawLaser(laser);
    }

    /**
     * Vẽ thanh trượt (paddle).
     * @param paddle Đối tượng Paddle.
     */
    public void drawPaddle(Paddle paddle) {
        spriteRenderer.drawPaddle(paddle);
    }

    /**
     * Vẽ viên gạch.
     * @param brick Đối tượng Brick.
     */
    public void drawBrick(Brick brick) {
        spriteRenderer.drawBrick(brick);
    }

    /**
     * Vẽ PowerUp.
     * @param powerUp Đối tượng PowerUp.
     */
    public void drawPowerUp(PowerUp powerUp) {
        spriteRenderer.drawPowerUp(powerUp);
    }

    /**
     * Phương thức này có thể được sử dụng để hoàn tất việc render.
     */
    public void present() {
        // Hiện tại không cần thêm code ở đây, nhưng giữ lại cho kiến trúc render
    }
}