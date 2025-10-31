package UI.Menu;

import Engine.GameState;
import Engine.StateManager;
import Objects.PowerUps.PowerUpType;
import UI.Button;
import UI.PowerUpDisplay;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Menu chính của game.
 * Hiển thị logo, buttons, và preview PowerUps.
 */
public class MainMenu implements Screen {
    private final StateManager stateManager;
    private final SpriteProvider sprites;
    private final HighScoreDisplay highScoreDisplay;
    private final SettingsScreen settingsScreen;

    // UI Components
    private List<Button> buttons;
    private List<PowerUpDisplay> leftPowerUps;
    private List<PowerUpDisplay> rightPowerUps;
    private Image logo;

    // Selection state
    private int selectedButtonIndex;

    // Layout constants
    private static final double WINDOW_WIDTH = Constants.Window.WINDOW_WIDTH;
    private static final double WINDOW_HEIGHT = Constants.Window.WINDOW_HEIGHT;
    private static final double BUTTON_WIDTH = 200;
    private static final double BUTTON_HEIGHT = 50;
    private static final double BUTTON_SPACING = 20;
    private static final double POWERUP_SIZE = 60;
    private static final double LOGO_WIDTH = Constants.UISprites.LOGO_WIDTH; // 400x145
    private static final double LOGO_HEIGHT = Constants.UISprites.LOGO_HEIGHT;

    // State
    private boolean showingHighScore = false;
    private boolean showingSettings = false;

    /**
     * Constructor.
     * @param stateManager StateManager để chuyển state
     * @param sprites SpriteProvider để lấy sprites
     */
    public MainMenu(StateManager stateManager, SpriteProvider sprites) {
        this.stateManager = stateManager;
        this.sprites = sprites;
        this.highScoreDisplay = new HighScoreDisplay(sprites);
        this.settingsScreen = new SettingsScreen(stateManager.getAudioManager(), sprites, this::onBackFromSettings);
        this.buttons = new ArrayList<>();
        this.leftPowerUps = new ArrayList<>();
        this.rightPowerUps = new ArrayList<>();
        this.selectedButtonIndex = 0;

        initializeComponents();
    }

    /**
     * Khởi tạo các UI components.
     */
    private void initializeComponents() {
        // Load logo
        logo = sprites.get("logo.png"); // Giả sử có logo.png

        // Tính toán vị trí center cho buttons
        double centerX = WINDOW_WIDTH / 2;
        double centerY = WINDOW_HEIGHT / 2;
        double buttonX = centerX - BUTTON_WIDTH / 2;
        double buttonY = centerY - (BUTTON_HEIGHT * 4 + BUTTON_SPACING * 3) / 2; // 4 buttons now

        // Tạo buttons
        buttons.add(new Button(
                buttonX, buttonY,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                "START GAME",
                this::onStartGame
        ));

        buttons.add(new Button(
                buttonX, buttonY + BUTTON_HEIGHT + BUTTON_SPACING,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                "HIGH SCORE",
                this::onHighScore
        ));

        buttons.add(new Button(
                buttonX, buttonY + (BUTTON_HEIGHT + BUTTON_SPACING) * 2,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                "SETTINGS",
                this::onSettings
        ));

        buttons.add(new Button(
                buttonX, buttonY + (BUTTON_HEIGHT + BUTTON_SPACING) * 3,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                "QUIT GAME",
                this::onQuitGame
        ));

        // Set button đầu tiên là selected
        buttons.get(0).setSelected(true);

        // Tạo PowerUp displays - Left side
        double leftX = centerX - 200;
        double startY = centerY - POWERUP_SIZE;

        leftPowerUps.add(new PowerUpDisplay(
                PowerUpType.DUPLICATE, leftX, startY,
                POWERUP_SIZE, POWERUP_SIZE * 0.5, sprites
        ));

        leftPowerUps.add(new PowerUpDisplay(
                PowerUpType.CATCH, leftX, startY + POWERUP_SIZE * 2.5,
                POWERUP_SIZE, POWERUP_SIZE * 0.5, sprites
        ));

        leftPowerUps.add(new PowerUpDisplay(
                PowerUpType.SLOW, leftX, startY + POWERUP_SIZE * 2.5 * 2,
                POWERUP_SIZE, POWERUP_SIZE * 0.5, sprites
        ));

        // Tạo PowerUp displays - Right side
        double rightX = centerX + 200;

        rightPowerUps.add(new PowerUpDisplay(
                PowerUpType.EXPAND, rightX, startY,
                POWERUP_SIZE, POWERUP_SIZE * 0.5, sprites
        ));

        rightPowerUps.add(new PowerUpDisplay(
                PowerUpType.LASER, rightX, startY + POWERUP_SIZE * 2.5,
                POWERUP_SIZE, POWERUP_SIZE * 0.5, sprites
        ));

        rightPowerUps.add(new PowerUpDisplay(
                PowerUpType.WARP, rightX, startY + POWERUP_SIZE * 2.5 * 2,
                POWERUP_SIZE, POWERUP_SIZE * 0.5, sprites
        ));
    }

    @Override
    public void render(GraphicsContext gc) {
        if (showingHighScore) {
            highScoreDisplay.render(gc);
            return;
        }

        if (showingSettings) {
            settingsScreen.render(gc);
            return;
        }

        // Draw gradient background
        UIHelper.drawGradientBackground(gc, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT,
                Color.rgb(10, 10, 30), Color.rgb(30, 10, 50));

        // Draw logo
        double logoY = 100;
        UIHelper.drawLogo(gc, logo, WINDOW_WIDTH / 2, logoY, LOGO_WIDTH, LOGO_HEIGHT);

        // Draw PowerUps
        for (PowerUpDisplay powerUp : leftPowerUps) {
            powerUp.render(gc);
        }
        for (PowerUpDisplay powerUp : rightPowerUps) {
            powerUp.render(gc);
        }

        // Draw buttons
        for (Button button : buttons) {
            button.render(gc);
        }

        // Draw instruction text
        UIHelper.drawCenteredText(gc, "Use Arrow Keys or Mouse to Navigate",
                WINDOW_WIDTH / 2, WINDOW_HEIGHT - 50,
                Font.font("Courier New", 14), Color.LIGHTGRAY);
    }

    @Override
    public void update(long deltaTime) {
        if (showingHighScore) {
            highScoreDisplay.update(deltaTime);
            return;
        }

        if (showingSettings) {
            settingsScreen.update(deltaTime);
            return;
        }

        // Update PowerUp animations
        long currentTime = System.currentTimeMillis();
        for (PowerUpDisplay powerUp : leftPowerUps) {
            powerUp.update(currentTime);
        }
        for (PowerUpDisplay powerUp : rightPowerUps) {
            powerUp.update(currentTime);
        }
    }

    @Override
    public void handleKeyPressed(KeyCode keyCode) {
        if (showingHighScore) {
            if (keyCode == KeyCode.ESCAPE) {
                showingHighScore = false;
            }
            return;
        }

        if (showingSettings) {
            settingsScreen.handleKeyPressed(keyCode);
            return;
        }

        switch (keyCode) {
            case UP:
                navigateUp();
                break;
            case DOWN:
                navigateDown();
                break;
            case ENTER:
            case SPACE:
                buttons.get(selectedButtonIndex).click();
                break;
            case ESCAPE:
                onQuitGame();
                break;
        }
    }

    @Override
    public void handleKeyReleased(KeyCode keyCode) {
        // Not used
    }

    @Override
    public void handleMouseClicked(MouseEvent event) {
        if (showingHighScore) {
            return;
        }

        if (showingSettings) {
            settingsScreen.handleMouseClicked(event);
            return;
        }

        double mouseX = event.getX();
        double mouseY = event.getY();

        for (Button button : buttons) {
            if (button.contains(mouseX, mouseY)) {
                button.click();
                break;
            }
        }
    }

    @Override
    public void handleMouseMoved(MouseEvent event) {
        if (showingHighScore) {
            return;
        }

        if (showingSettings) {
            settingsScreen.handleMouseMoved(event);
            return;
        }

        double mouseX = event.getX();
        double mouseY = event.getY();

        // Update hover state
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            boolean isHovered = button.contains(mouseX, mouseY);
            button.setHovered(isHovered);

            if (isHovered && selectedButtonIndex != i) {
                buttons.get(selectedButtonIndex).setSelected(false);
                selectedButtonIndex = i;
                button.setSelected(true);
            }
        }
    }

    @Override
    public void onEnter() {
        showingHighScore = false;
        showingSettings = false;
        selectedButtonIndex = 0;
        updateButtonSelection();
    }

    @Override
    public void onExit() {
        // Cleanup if needed
    }

    /**
     * Navigate lên button phía trên.
     */
    private void navigateUp() {
        buttons.get(selectedButtonIndex).setSelected(false);
        selectedButtonIndex--;
        if (selectedButtonIndex < 0) {
            selectedButtonIndex = buttons.size() - 1;
        }
        updateButtonSelection();
    }

    /**
     * Navigate xuống button phía dưới.
     */
    private void navigateDown() {
        buttons.get(selectedButtonIndex).setSelected(false);
        selectedButtonIndex++;
        if (selectedButtonIndex >= buttons.size()) {
            selectedButtonIndex = 0;
        }
        updateButtonSelection();
    }

    /**
     * Update trạng thái selected của buttons.
     */
    private void updateButtonSelection() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setSelected(i == selectedButtonIndex);
        }
    }

    // Button callbacks
    private void onStartGame() {
        stateManager.setState(GameState.PLAYING);
    }

    private void onHighScore() {
        showingHighScore = true;
        highScoreDisplay.onEnter();
    }

    private void onSettings() {
        showingSettings = true;
        settingsScreen.onEnter();
    }

    private void onBackFromSettings() {
        showingSettings = false;
    }

    private void onQuitGame() {
        System.exit(0);
    }
}
