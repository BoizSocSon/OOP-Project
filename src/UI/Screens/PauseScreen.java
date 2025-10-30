package UI.Screens;

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
 * Màn hình Pause khi game bị tạm dừng.
 * Hiển thị thông tin round hiện tại, điểm, số mạng.
 */
public class PauseScreen implements Screen {
    private final SpriteProvider sprites;
    private Image logo;

    // Game state info
    private int currentRound;
    private String roundName;
    private int currentScore;
    private int currentLives;

    // Layout constants
    private static final double WINDOW_WIDTH = Constants.Window.WINDOW_WIDTH;
    private static final double WINDOW_HEIGHT = Constants.Window.WINDOW_HEIGHT;
    private static final double LOGO_WIDTH = 320; // Scale down cho Pause screen
    private static final double LOGO_HEIGHT = 116; // Giữ tỷ lệ 400:145

    /**
     * Constructor.
     * @param sprites SpriteProvider để lấy sprites
     */
    public PauseScreen(SpriteProvider sprites) {
        this.sprites = sprites;
        loadAssets();
    }

    /**
     * Load assets.
     */
    private void loadAssets() {
        logo = sprites.get("logo.png");
    }

    /**
     * Set thông tin game để hiển thị.
     * @param round Round hiện tại
     * @param roundName Tên round
     * @param score Điểm hiện tại
     * @param lives Số mạng hiện tại
     */
    public void setGameInfo(int round, String roundName, int score, int lives) {
        this.currentRound = round;
        this.roundName = roundName;
        this.currentScore = score;
        this.currentLives = lives;
    }

    @Override
    public void render(GraphicsContext gc) {
        // Draw semi-transparent overlay
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Draw box
        double boxWidth = 400;
        double boxHeight = 350;
        double boxX = (WINDOW_WIDTH - boxWidth) / 2;
        double boxY = (WINDOW_HEIGHT - boxHeight) / 2;

        UIHelper.drawBox(gc, boxX, boxY, boxWidth, boxHeight,
                Color.rgb(20, 20, 40, 0.95), Color.CYAN, 3);

        // Draw logo
        UIHelper.drawLogo(gc, logo, WINDOW_WIDTH / 2, boxY + 60, LOGO_WIDTH, LOGO_HEIGHT);

        // Draw "GAME PAUSED" title
        UIHelper.drawCenteredText(gc, "GAME PAUSED",
                WINDOW_WIDTH / 2, boxY + 130,
                Font.font("Courier New", 32), Color.YELLOW);

        // Draw game info
        Font infoFont = Font.font("Courier New", 20);
        Color infoColor = Color.WHITE;
        double infoY = boxY + 180;
        double lineSpacing = 35;

        UIHelper.drawCenteredText(gc, String.format("Round: %d - \"%s\"", currentRound, roundName),
                WINDOW_WIDTH / 2, infoY,
                infoFont, infoColor);

        UIHelper.drawCenteredText(gc, String.format("Score: %,d", currentScore),
                WINDOW_WIDTH / 2, infoY + lineSpacing,
                infoFont, infoColor);

        // Draw lives as hearts
        String livesStr = "Lives: " + getHeartString(currentLives);
        UIHelper.drawCenteredText(gc, livesStr,
                WINDOW_WIDTH / 2, infoY + lineSpacing * 2,
                infoFont, Color.RED);

        // Draw instructions
        Font instructionFont = Font.font("Courier New", 14);
        Color instructionColor = Color.LIGHTGRAY;
        double instructionY = boxY + boxHeight - 60;

        UIHelper.drawCenteredText(gc, "Press SPACE to continue",
                WINDOW_WIDTH / 2, instructionY,
                instructionFont, instructionColor);

        UIHelper.drawCenteredText(gc, "Press ESC to return to menu",
                WINDOW_WIDTH / 2, instructionY + 25,
                instructionFont, instructionColor);
    }

    /**
     * Tạo chuỗi hearts từ số mạng.
     * @param lives Số mạng
     * @return Chuỗi hearts
     */
    private String getHeartString(int lives) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lives; i++) {
            sb.append("♥ ");
        }
        return sb.toString().trim();
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
        // Called when entering pause state
    }

    @Override
    public void onExit() {
        // Cleanup if needed
    }
}
