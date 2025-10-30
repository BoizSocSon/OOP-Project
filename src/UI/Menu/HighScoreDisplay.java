package UI.Menu;

import Engine.HighScoreManager;
import Engine.HighScoreManager.HighScoreEntry;
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

import java.util.List;

/**
 * Màn hình hiển thị High Scores.
 * Hiển thị top 10 scores với Rank, Name, Score, Date.
 */
public class HighScoreDisplay implements Screen {
    private final SpriteProvider sprites;
    private final HighScoreManager highScoreManager;
    private Image logo;

    // Layout constants
    private static final double WINDOW_WIDTH = Constants.Window.WINDOW_WIDTH;
    private static final double WINDOW_HEIGHT = Constants.Window.WINDOW_HEIGHT;
    private static final double LOGO_WIDTH = 320; // Scale down cho HighScore screen
    private static final double LOGO_HEIGHT = 116; // Giữ tỷ lệ 400:145
    private static final double TABLE_START_Y = 250;
    private static final double ROW_HEIGHT = 40;
    private static final double COL_RANK_X = 100;
    private static final double COL_NAME_X = 200;
    private static final double COL_SCORE_X = 350;
    private static final double COL_DATE_X = 480;

    /**
     * Constructor.
     * @param sprites SpriteProvider để lấy logo
     */
    public HighScoreDisplay(SpriteProvider sprites) {
        this.sprites = sprites;
        this.highScoreManager = new HighScoreManager();
        loadAssets();
    }

    /**
     * Load assets.
     */
    private void loadAssets() {
        logo = sprites.get("logo.png");
    }

    @Override
    public void render(GraphicsContext gc) {
        // Draw gradient background
        UIHelper.drawGradientBackground(gc, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT,
                Color.rgb(10, 10, 30), Color.rgb(30, 10, 50));

        // Draw logo
        UIHelper.drawLogo(gc, logo, WINDOW_WIDTH / 2, 120, LOGO_WIDTH, LOGO_HEIGHT);

        // Draw title
        UIHelper.drawCenteredText(gc, "HIGH SCORES",
                WINDOW_WIDTH / 2, 200,
                Font.font("Courier New", 30), Color.WHITE);

        // Draw table header
        Font headerFont = Font.font("Courier New", 18);
        Font dataFont = Font.font("Courier New", 16);
        Color headerColor = Color.YELLOW;
        Color dataColor = Color.WHITE;

        double headerY = TABLE_START_Y;

        UIHelper.drawLeftAlignedText(gc, "RANK", COL_RANK_X, headerY, headerFont, headerColor);
        UIHelper.drawLeftAlignedText(gc, "NAME", COL_NAME_X, headerY, headerFont, headerColor);
        UIHelper.drawLeftAlignedText(gc, "SCORE", COL_SCORE_X, headerY, headerFont, headerColor);
        UIHelper.drawLeftAlignedText(gc, "DATE", COL_DATE_X, headerY, headerFont, headerColor);

        // Draw separator line
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        gc.strokeLine(COL_RANK_X, headerY + 25, WINDOW_WIDTH - 100, headerY + 25);

        // Draw scores
        List<HighScoreEntry> scores = highScoreManager.getAllScores();
        double rowY = TABLE_START_Y + 40;

        for (HighScoreEntry entry : scores) {
            // Alternate row colors
            if (entry.getRank() % 2 == 0) {
                gc.setFill(Color.rgb(20, 20, 40, 0.5));
                gc.fillRect(COL_RANK_X - 10, rowY - 5, WINDOW_WIDTH - 180, ROW_HEIGHT - 5);
            }

            UIHelper.drawLeftAlignedText(gc, String.valueOf(entry.getRank()),
                    COL_RANK_X, rowY, dataFont, dataColor);
            UIHelper.drawLeftAlignedText(gc, entry.getPlayerName(),
                    COL_NAME_X, rowY, dataFont, dataColor);
            UIHelper.drawLeftAlignedText(gc, String.format("%,d", entry.getScore()),
                    COL_SCORE_X, rowY, dataFont, dataColor);
            UIHelper.drawLeftAlignedText(gc, entry.getFormattedDate(),
                    COL_DATE_X, rowY, dataFont, dataColor);

            rowY += ROW_HEIGHT;
        }

        // Draw instruction
        UIHelper.drawCenteredText(gc, "Press ESC to return to menu",
                WINDOW_WIDTH / 2, WINDOW_HEIGHT - 50,
                Font.font("Courier New", 14), Color.LIGHTGRAY);
    }

    @Override
    public void update(long deltaTime) {
        // No animation needed
    }

    @Override
    public void handleKeyPressed(KeyCode keyCode) {
        // Handled by MainMenu
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
        // Reload scores khi vào màn hình
        // highScoreManager sẽ tự động load từ file
    }

    @Override
    public void onExit() {
        // Cleanup if needed
    }

    /**
     * Lấy HighScoreManager.
     * @return HighScoreManager instance
     */
    public HighScoreManager getHighScoreManager() {
        return highScoreManager;
    }
}
