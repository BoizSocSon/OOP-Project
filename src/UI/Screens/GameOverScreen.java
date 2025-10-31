package UI.Screens;

import Engine.HighScoreManager;
import UI.Screen;
import UI.UIHelper;
import Utils.Constants;
import Utils.SpriteProvider;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Màn hình Game Over khi người chơi thua.
 * Hiển thị điểm cuối cùng, high score, round đạt được.
 */
public class GameOverScreen implements Screen {
    private final SpriteProvider sprites;
    private final HighScoreManager highScoreManager;
    private Image logo;

    // Game result info
    private int finalScore;
    private int highScore;
    private int roundReached;
    private boolean isNewHighScore;

    // Layout constants
    private static final double WINDOW_WIDTH = Constants.Window.WINDOW_WIDTH;
    private static final double WINDOW_HEIGHT = Constants.Window.WINDOW_HEIGHT;
    private static final double LOGO_WIDTH = 320; // Scale down cho GameOver screen
    private static final double LOGO_HEIGHT = 116; // Giữ tỷ lệ 400:145

    /**
     * Constructor.
     * @param sprites SpriteProvider để lấy sprites
     * @param highScoreManager HighScoreManager để kiểm tra high score
     */
    public GameOverScreen(SpriteProvider sprites, HighScoreManager highScoreManager) {
        this.sprites = sprites;
        this.highScoreManager = highScoreManager;
        loadAssets();
    }

    /**
     * Load assets.
     */
    private void loadAssets() {
        logo = sprites.get("logo.png");
    }

    /**
     * Set kết quả game để hiển thị.
     * @param score Điểm cuối cùng
     * @param round Round đạt được
     */
    public void setGameResult(int score, int round) {
        this.finalScore = score;
        this.roundReached = round;
        this.highScore = highScoreManager.getHighestScore();
        this.isNewHighScore = highScoreManager.isHighScore(score);
    }

    @Override
    public void render(GraphicsContext gc) {
        // Draw gradient background
        UIHelper.drawGradientBackground(gc, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT,
                Color.rgb(20, 0, 0), Color.rgb(50, 10, 10));

        // Draw box
        double boxWidth = 450;
        double boxHeight = 400;
        double boxX = (WINDOW_WIDTH - boxWidth) / 2;
        double boxY = (WINDOW_HEIGHT - boxHeight) / 2;

        UIHelper.drawBox(gc, boxX, boxY, boxWidth, boxHeight,
                Color.rgb(30, 10, 10, 0.95), Color.RED, 3);

        // Draw logo
        UIHelper.drawLogo(gc, logo, WINDOW_WIDTH / 2, boxY + 60, LOGO_WIDTH, LOGO_HEIGHT);

        // Draw "GAME OVER" title
        UIHelper.drawCenteredText(gc, "GAME OVER",
                WINDOW_WIDTH / 2, boxY + 140,
                Font.font("Courier New", 40), Color.RED);

        // Draw game stats
        Font statsFont = Font.font("Courier New", 22);
        Color statsColor = Color.WHITE;
        double statsY = boxY + 200;
        double lineSpacing = 40;

        UIHelper.drawCenteredText(gc, String.format("Your Score: %,d", finalScore),
                WINDOW_WIDTH / 2, statsY,
                statsFont, statsColor);

        UIHelper.drawCenteredText(gc, String.format("High Score: %,d", highScore),
                WINDOW_WIDTH / 2, statsY + lineSpacing,
                statsFont, statsColor);

        UIHelper.drawCenteredText(gc, String.format("Round Reached: %d", roundReached),
                WINDOW_WIDTH / 2, statsY + lineSpacing * 2,
                statsFont, statsColor);

        // Draw NEW HIGH SCORE if applicable
        if (isNewHighScore) {
            UIHelper.drawCenteredText(gc, "★ NEW HIGH SCORE! ★",
                    WINDOW_WIDTH / 2, statsY + lineSpacing * 3,
                    Font.font("Courier New", 24), Color.YELLOW);
        }

        // Draw instruction
        Font instructionFont = Font.font("Courier New", 16);
        Color instructionColor = Color.LIGHTGRAY;

        UIHelper.drawCenteredText(gc, "Press ENTER to return to menu",
                WINDOW_WIDTH / 2, boxY + boxHeight - 40,
                instructionFont, instructionColor);
    }

    @Override
    public void update(long deltaTime) {
        // No animation needed
    }

    @Override
    public void handleKeyPressed(KeyCode keyCode) {
        // Handled by GameManager
    }

    @Override
    public void handleKeyReleased(KeyCode keyCode) {
        // Not used
    }

    @Override
    public void handleMouseClicked(MouseEvent event) {
        // Not used
    }

    @Override
    public void handleMouseMoved(MouseEvent event) {
        // Not used
    }

    @Override
    public void onEnter() {
        // Called when entering game over state
    }

    @Override
    public void onExit() {
        // Cleanup if needed
    }
}
