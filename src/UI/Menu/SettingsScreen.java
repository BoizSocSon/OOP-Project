package UI.Menu;

import Engine.AudioManager;
import UI.Button;
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
import javafx.scene.text.TextAlignment;

/**
 * Màn hình Settings để điều chỉnh âm thanh.
 * Cho phép user thay đổi volume bằng phím LEFT/RIGHT và mute/unmute bằng phím UP/DOWN.
 */
public class SettingsScreen implements Screen {
    private final AudioManager audioManager;
    private final Runnable onBack;
    private final SpriteProvider sprites;
    private final Image logo;

    // UI Components
    private Button muteButton;
    private Button unmuteButton;

    // Selection state
    private int selectedOption = 0; // 0 = volume, 1 = mute/unmute

    // Volume control
    private static final double VOLUME_STEP = 0.05; // 5% mỗi lần tăng/giảm

    // Layout constants
    private static final double WINDOW_WIDTH = Constants.Window.WINDOW_WIDTH;
    private static final double WINDOW_HEIGHT = Constants.Window.WINDOW_HEIGHT;
    private static final double BUTTON_WIDTH = 150;
    private static final double BUTTON_HEIGHT = 50;
    private static final double LOGO_WIDTH = Constants.UISprites.LOGO_WIDTH;
    private static final double LOGO_HEIGHT = Constants.UISprites.LOGO_HEIGHT;

    /**
     * Constructor.
     * @param audioManager AudioManager để điều khiển âm thanh
     * @param sprites SpriteProvider để lấy sprites
     * @param onBack Callback khi nhấn BACK
     */
    public SettingsScreen(AudioManager audioManager, SpriteProvider sprites, Runnable onBack) {
        this.audioManager = audioManager;
        this.sprites = sprites;
        this.onBack = onBack;
        this.logo = sprites.get("logo.png");
        initializeComponents();
    }

    /**
     * Khởi tạo các UI components.
     */
    private void initializeComponents() {
        double centerX = WINDOW_WIDTH / 2;

        // Mute/Unmute buttons - Điều chỉnh vị trí Y để không đè lên chữ SOUND
        double buttonY = 490; // Tăng từ 400 lên 490
        double buttonSpacing = 20;
        double totalButtonWidth = BUTTON_WIDTH * 2 + buttonSpacing;
        double leftButtonX = centerX - totalButtonWidth / 2;

        muteButton = new Button(
                leftButtonX, buttonY,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                "MUTE",
                () -> audioManager.setMuted(true)
        );

        unmuteButton = new Button(
                leftButtonX + BUTTON_WIDTH + buttonSpacing, buttonY,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                "UNMUTE",
                () -> audioManager.setMuted(false)
        );
    }

    @Override
    public void render(GraphicsContext gc) {
        // Draw gradient background
        UIHelper.drawGradientBackground(gc, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT,
                Color.rgb(10, 10, 30), Color.rgb(30, 10, 50));

        // Draw logo at top
        double logoY = 100;
        UIHelper.drawLogo(gc, logo, WINDOW_WIDTH / 2, logoY, LOGO_WIDTH, LOGO_HEIGHT);

        // Draw title
        gc.setFill(Color.GOLD);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Courier New", 36));
        gc.fillText("AUDIO SETTINGS", WINDOW_WIDTH / 2, 220);

        // Draw volume section
        gc.setFill(selectedOption == 0 ? Color.YELLOW : Color.WHITE);
        gc.setFont(Font.font("Courier New", 24));
        gc.fillText("VOLUME", WINDOW_WIDTH / 2, 280);

        // Draw volume bar
        drawVolumeBar(gc);

        // Draw volume percentage
        int volumePercent = (int) (audioManager.getVolume() * 100);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Courier New", 20));
        gc.fillText(volumePercent + "%", WINDOW_WIDTH / 2, 370);

        // Draw volume control hint
        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Courier New", 14));
        gc.fillText("Use LEFT/RIGHT arrows to adjust", WINDOW_WIDTH / 2, 395);

        // Draw sound toggle label - Điều chỉnh vị trí để không đè lên buttons
        gc.setFill(selectedOption == 1 ? Color.YELLOW : Color.WHITE);
        gc.setFont(Font.font("Courier New", 24));
        gc.fillText("SOUND", WINDOW_WIDTH / 2, 450);

        // Update button states based on muted
        boolean isMuted = audioManager.isMuted();
        muteButton.setSelected(isMuted && selectedOption == 1);
        unmuteButton.setSelected(!isMuted && selectedOption == 1);

        // Highlight selected buttons only when on sound option
        if (selectedOption == 1) {
            muteButton.setHovered(false);
            unmuteButton.setHovered(false);
        }

        // Draw buttons
        muteButton.render(gc);
        unmuteButton.render(gc);

        // Draw sound control hint
        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Courier New", 14));
        gc.fillText("Use UP/DOWN arrows to select, ENTER to toggle", WINDOW_WIDTH / 2, 565);

        // Draw instruction at bottom
        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Courier New", 16));
        gc.fillText("Press ESC to return to menu", WINDOW_WIDTH / 2, WINDOW_HEIGHT - 50);
    }

    /**
     * Vẽ thanh volume bar (không phải slider).
     */
    private void drawVolumeBar(GraphicsContext gc) {
        double barWidth = 400;
        double barHeight = 20;
        double barX = (WINDOW_WIDTH - barWidth) / 2;
        double barY = 330; // Điều chỉnh từ 300 lên 330

        // Draw background track
        gc.setFill(Color.rgb(80, 80, 80));
        gc.fillRect(barX, barY, barWidth, barHeight);

        // Draw fill based on volume
        double fillWidth = barWidth * audioManager.getVolume();
        gc.setFill(Color.rgb(100, 200, 100));
        gc.fillRect(barX, barY, fillWidth, barHeight);

        // Draw border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(barX, barY, barWidth, barHeight);
    }

    @Override
    public void update(long deltaTime) {
        // Nothing to update
    }

    @Override
    public void handleKeyPressed(KeyCode keyCode) {
        switch (keyCode) {
            case ESCAPE:
                onBack.run();
                break;

            case UP:
                // Navigate up (volume -> sound)
                selectedOption = 0;
                break;

            case DOWN:
                // Navigate down (sound -> volume)
                selectedOption = 1;
                break;

            case LEFT:
                // Decrease volume when on volume option
                if (selectedOption == 0) {
                    double newVolume = Math.max(0.0, audioManager.getVolume() - VOLUME_STEP);
                    audioManager.setVolume(newVolume);
                }
                break;

            case RIGHT:
                // Increase volume when on volume option
                if (selectedOption == 0) {
                    double newVolume = Math.min(1.0, audioManager.getVolume() + VOLUME_STEP);
                    audioManager.setVolume(newVolume);
                }
                break;

            case ENTER:
            case SPACE:
                // Toggle mute/unmute when on sound option
                if (selectedOption == 1) {
                    audioManager.setMuted(!audioManager.isMuted());
                }
                break;
        }
    }

    @Override
    public void handleKeyReleased(KeyCode keyCode) {
        // Not used
    }

    @Override
    public void handleMouseClicked(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        // Check button clicks
        if (muteButton.contains(mouseX, mouseY)) {
            selectedOption = 1;
            muteButton.click();
        } else if (unmuteButton.contains(mouseX, mouseY)) {
            selectedOption = 1;
            unmuteButton.click();
        }
    }

    @Override
    public void handleMouseMoved(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        // Update hover states (chỉ để visual feedback)
        if (selectedOption != 1) {
            muteButton.setHovered(muteButton.contains(mouseX, mouseY));
            unmuteButton.setHovered(unmuteButton.contains(mouseX, mouseY));
        }
    }

    @Override
    public void onEnter() {
        selectedOption = 0; // Start with volume selected
    }

    @Override
    public void onExit() {
        // Nothing to do
    }
}
