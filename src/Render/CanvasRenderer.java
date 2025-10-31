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

public class CanvasRenderer {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final SpriteRenderer spriteRenderer;
    private final BorderRenderer borderRenderer;
    private final SpriteProvider sprites;

    private Font scoreFont;
    private Font uiFont;

    public CanvasRenderer(Canvas canvas, SpriteProvider sprites) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.spriteRenderer = new SpriteRenderer(gc, sprites);
        this.borderRenderer = new BorderRenderer(gc, sprites);
        this.sprites = sprites;
        this.loadUIAssets();
    }

    public void clear() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void loadUIAssets() {
        try {
            scoreFont = AssetLoader.loadFont("generation.ttf", 24);
            uiFont = AssetLoader.loadFont("emulogic.ttf", 18);
        } catch (Exception e) {
            scoreFont = Font.font("Monospaced", 24);
            uiFont = Font.font("Monospaced", 18);
            System.out.println("CanvasRenderer: Failed to load custom fonts, using default.");
        }
    }

    public void drawUI(int score, int highScore, int lives) {
        gc.drawImage(sprites.get("logo.png"),0,0);

        gc.setFont(uiFont);
        gc.setFill(Color.RED);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("1UP", canvas.getWidth() * 0.82, 30);

        gc.setFont(scoreFont);
        gc.setFill(Color.GOLD);
        gc.fillText(String.valueOf(score), canvas.getWidth() * 0.82, 60);

        gc.setFont(uiFont);
        gc.setFill(Color.RED);
        gc.fillText("HIGH SCORE", canvas.getWidth() * 0.82, 100);

        gc.setFont(scoreFont);
        gc.setFill(Color.GOLD);
        gc.fillText(String.valueOf(highScore), canvas.getWidth() * 0.82, 130);

        borderRenderer.render();

        for (int i = 0; i < lives; i++) {
            double lifeX = Constants.Window.WINDOW_SIDE_OFFSET + i * (Constants.Paddle.PADDLE_LIFE_WIDTH + 10);
            double lifeY = Constants.Window.WINDOW_HEIGHT - Constants.Paddle.PADDLE_LIFE_HEIGHT - 10;
            gc.drawImage(sprites.get("paddle_life.png"), lifeX, lifeY);
        }
    }

    public void drawPauseOverlay() {
        double cx = Constants.Window.WINDOW_WIDTH / 2.0;
        // translucent backdrop
        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRoundRect(120, 320, 360, 160, 10, 10);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);

        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("════════════════════", cx, 350);

        gc.setFont(Font.font("Monospaced", 36));
        gc.fillText("PAUSED", cx, 390);

        gc.setFont(Font.font("Monospaced", 16));
        gc.fillText("Press ESC to resume", cx, 420);

        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("════════════════════", cx, 450);
    }

    public void drawGameOverOverlay(int score) {
        double cx = Constants.Window.WINDOW_WIDTH / 2.0;
        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRoundRect(120, 260, 360, 200, 10, 10);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);

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

    public void drawWinOverlay(int score) {
        double cx = Constants.Window.WINDOW_WIDTH / 2.0;
        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRoundRect(120, 260, 360, 220, 10, 10);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);

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

    public void drawLevelCompleteOverlay() {
        double cx = Constants.Window.WINDOW_WIDTH / 2.0;
        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRoundRect(120, 320, 360, 160, 10, 10);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);

        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("════════════════════", cx, 350);

        gc.setFont(Font.font("Monospaced", 28));
        gc.fillText("LEVEL COMPLETE!", cx, 390);

        gc.setFont(Font.font("Monospaced", 16));
        gc.fillText("Loading next round...", cx, 420);

        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText("════════════════════", cx, 450);
    }

    public void drawBall(Ball ball) {
        spriteRenderer.drawBall(ball);
    }

    public void drawLaser(Laser laser) {
        spriteRenderer.drawLaser(laser);
    }

    public void drawPaddle(Paddle paddle) {
        spriteRenderer.drawPaddle(paddle);
    }

    public void drawBrick(Brick brick) {
        spriteRenderer.drawBrick(brick);
    }

    public void drawPowerUp(PowerUp powerUp) {
        spriteRenderer.drawPowerUp(powerUp);
    }

    public void present() {
        // In JavaFX, the canvas is automatically presented, so no action is needed here.
    }


}
