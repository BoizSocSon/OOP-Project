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
 * Màn hình Win khi người chơi thắng (hoàn thành tất cả rounds).
 * Hiển thị điểm cuối cùng, high score.
 */
public class WinScreen implements Screen {
    private final SpriteProvider sprites;
    private final HighScoreManager highScoreManager;
    private Image logo;

    // Game result info
    private int finalScore;
    private int highScore;
    private int totalRounds;
    private boolean isNewHighScore;

    // Animation
    private double starRotation = 0;

    // Layout constants
    private static final double WINDOW_WIDTH = Constants.Window.WINDOW_WIDTH;
    private static final double WINDOW_HEIGHT = Constants.Window.WINDOW_HEIGHT;
    private static final double LOGO_WIDTH = 320; // Scale down cho Win screen
    private static final double LOGO_HEIGHT = 116; // Giữ tỷ lệ 400:145

    /**
     * Constructor.
     * @param sprites SpriteProvider để lấy sprites
     * @param highScoreManager HighScoreManager để kiểm tra high score
     */
    public WinScreen(SpriteProvider sprites, HighScoreManager highScoreManager) {
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
     * @param rounds Tổng số rounds đã hoàn thành
     */
    public void setGameResult(int score, int rounds) {
        this.finalScore = score;
        this.totalRounds = rounds;
        this.highScore = highScoreManager.getHighestScore();
        this.isNewHighScore = highScoreManager.isHighScore(score);
    }

    @Override
    public void render(GraphicsContext gc) {
        // Draw gradient background (celebration colors)
        UIHelper.drawGradientBackground(gc, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT,
                Color.rgb(10, 20, 40), Color.rgb(40, 10, 60));

        // Draw box
        double boxWidth = 450;
        double boxHeight = 450;
        double boxX = (WINDOW_WIDTH - boxWidth) / 2;
        double boxY = (WINDOW_HEIGHT - boxHeight) / 2;

        UIHelper.drawBox(gc, boxX, boxY, boxWidth, boxHeight,
                Color.rgb(20, 40, 60, 0.95), Color.GOLD, 3);

        // Draw logo
        UIHelper.drawLogo(gc, logo, WINDOW_WIDTH / 2, boxY + 60, LOGO_WIDTH, LOGO_HEIGHT);

        // Draw "CONGRATULATIONS!" title
        UIHelper.drawCenteredText(gc, "CONGRATULATIONS!",
                WINDOW_WIDTH / 2, boxY + 140,
                Font.font("Courier New", 32), Color.GOLD);

        // Draw subtitle
        UIHelper.drawCenteredText(gc, "You Won!",
                WINDOW_WIDTH / 2, boxY + 180,
                Font.font("Courier New", 24), Color.YELLOW);

        // Draw game stats
        Font statsFont = Font.font("Courier New", 22);
        Color statsColor = Color.WHITE;
        double statsY = boxY + 230;
        double lineSpacing = 40;

        UIHelper.drawCenteredText(gc, String.format("Your Score: %,d", finalScore),
                WINDOW_WIDTH / 2, statsY,
                statsFont, statsColor);

        UIHelper.drawCenteredText(gc, String.format("High Score: %,d", highScore),
                WINDOW_WIDTH / 2, statsY + lineSpacing,
                statsFont, statsColor);

        UIHelper.drawCenteredText(gc, String.format("Rounds Completed: %d", totalRounds),
                WINDOW_WIDTH / 2, statsY + lineSpacing * 2,
                statsFont, statsColor);

        // Draw NEW HIGH SCORE if applicable with rotating stars
        if (isNewHighScore) {
            gc.save();

            // Draw rotating stars
            double starY = statsY + lineSpacing * 3;
            drawRotatingStar(gc, WINDOW_WIDTH / 2 - 150, starY);
            drawRotatingStar(gc, WINDOW_WIDTH / 2 + 150, starY);

            UIHelper.drawCenteredText(gc, "NEW HIGH SCORE!",
                    WINDOW_WIDTH / 2, starY,
                    Font.font("Courier New", 26), Color.GOLD);

            gc.restore();
        }

        // Draw instruction
        Font instructionFont = Font.font("Courier New", 16);
        Color instructionColor = Color.LIGHTGRAY;

        UIHelper.drawCenteredText(gc, "Press ENTER to return to menu",
                WINDOW_WIDTH / 2, boxY + boxHeight - 40,
                instructionFont, instructionColor);
    }

    /**
     * Vẽ một ngôi sao quay.
     * @param gc GraphicsContext
     * @param x Tọa độ X
     * @param y Tọa độ Y
     */
    private void drawRotatingStar(GraphicsContext gc, double x, double y) {
        gc.save();
        gc.translate(x, y);
        gc.rotate(starRotation);

        // Draw star shape (simple version using text)
        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Arial", 30));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.setTextBaseline(javafx.geometry.VPos.CENTER);
        gc.fillText("★", 0, 0);

        gc.restore();
    }

    @Override
    public void update(long deltaTime) {
        // Update star rotation
        starRotation += 2.0; // Rotate 2 degrees per frame
        if (starRotation >= 360) {
            starRotation -= 360;
        }
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
        // Reset animation
        starRotation = 0;
    }

    @Override
    public void onExit() {
        // Cleanup if needed
    }
}
