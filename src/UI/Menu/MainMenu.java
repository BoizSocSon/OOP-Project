package UI.Menu;

import Engine.GameState;
import Engine.StateManager;
import Objects.PowerUps.PowerUpType;
import UI.Button;
import UI.PowerUpDisplay;
import UI.Screen;
import UI.UIHelper;
import Utils.AssetLoader;
import Utils.Constants;
import Utils.SpriteProvider;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;


/**
 * Lớp màn hình Menu chính (Main Menu).
 * Chịu trách nhiệm hiển thị các tùy chọn chính của game (Bắt đầu, Điểm cao, Cài đặt, Thoát),
 * xử lý tương tác của người dùng và chuyển đổi trạng thái game.
 */
public class MainMenu implements Screen {
    private final StateManager stateManager; // Quản lý trạng thái game, dùng để chuyển từ MENU sang PLAYING.
    private final SpriteProvider sprites; // Nguồn cung cấp sprite (hình ảnh) cho các thành phần UI.
    private final HighScoreDisplay highScoreDisplay; // Màn hình hiển thị điểm cao (màn hình con).
    private final SettingsScreen settingsScreen; // Màn hình cài đặt (màn hình con).

    // Các thành phần UI
    private List<Button> buttons; // Danh sách các nút bấm chính trong menu.
    private List<PowerUpDisplay> leftPowerUps; // Danh sách hiển thị PowerUp bên trái (trang trí chuyển động).
    private List<PowerUpDisplay> rightPowerUps; // Danh sách hiển thị PowerUp bên phải (trang trí chuyển động).
    private PowerUpDisplay middlePowerUp; // PowerUp hiển thị ở giữa (nếu cần).
    private Image logo; // Logo game được hiển thị ở trên cùng.

    // Trạng thái lựa chọn
    private int selectedButtonIndex; // Chỉ số (index) của nút đang được chọn bằng phím điều hướng.

    // Các hằng số layout
    private static final double WINDOW_WIDTH = Constants.Window.WINDOW_WIDTH;
    private static final double WINDOW_HEIGHT = Constants.Window.WINDOW_HEIGHT;
    private static final double BUTTON_WIDTH = 180;
    private static final double BUTTON_HEIGHT = 60;
    private static final double BUTTON_SPACING = 20; // Khoảng cách giữa các nút.
    private static final double POWERUP_SIZE_HEIGHT = Constants.PowerUps.POWERUP_HEIGHT * 1.3;
    private static final double POWERUP_SIZE_WIDTH = Constants.PowerUps.POWERUP_WIDTH * 1.3;
    private static final double POWERUP_DISPLAY_OFFSET_Y = 60; // Khoảng cách từ trung tâm đến vị trí PowerUp.
    private static final double POWERUP_DISPLAY_OFFSET_X = 60; // Khoảng cách từ trung tâm đến vị trí PowerUp.
    private static final double LOGO_WIDTH = Constants.UISprites.LOGO_WIDTH; // 400x145
    private static final double LOGO_HEIGHT = Constants.UISprites.LOGO_HEIGHT;

    // Trạng thái màn hình con
    private boolean showingHighScore = false; // Cờ kiểm tra đang hiển thị màn hình điểm cao.
    private boolean showingSettings = false; // Cờ kiểm tra đang hiển thị màn hình cài đặt.

    // Font
    private String fontFamily; // Lưu tên font family để tái sử dụng
    /**
     * Constructor.
     * @param stateManager StateManager để chuyển trạng thái game.
     * @param sprites SpriteProvider để lấy tài nguyên hình ảnh.
     */
    public MainMenu(StateManager stateManager, SpriteProvider sprites) {
        this.stateManager = stateManager;
        this.sprites = sprites;
        // Khởi tạo màn hình điểm cao.
        this.highScoreDisplay = new HighScoreDisplay(sprites);

        // Khởi tạo màn hình cài đặt, truyền callback onBackFromSettings để khi ESC sẽ quay lại menu chính.
        this.settingsScreen = new SettingsScreen(stateManager.getAudioManager(), sprites, this::onBackFromSettings);

        // Khởi tạo danh sách trống cho các thành phần UI.
        this.buttons = new ArrayList<>();
        this.leftPowerUps = new ArrayList<>();
        this.rightPowerUps = new ArrayList<>();
        this.selectedButtonIndex = 0;

        initializeComponents(); // Thiết lập các thành phần UI (buttons và trang trí).
    }

    /**
     * Khởi tạo các thành phần UI (buttons, power-up displays).
     */
    private void initializeComponents() {
        // Tải logo từ SpriteProvider
        logo = sprites.get("logo.png");

        // Tính toán vị trí center cho khối buttons
        double centerX = WINDOW_WIDTH / 2;
        double centerY = WINDOW_HEIGHT / 2;
        double buttonX = centerX - BUTTON_WIDTH / 2; // Vị trí X bắt đầu của nút (căn giữa)

        // Tính vị trí Y bắt đầu để khối 4 nút được căn giữa theo chiều dọc.
        double buttonY = centerY - (BUTTON_HEIGHT * 4 + BUTTON_SPACING * 3) / 2;

        // --- Tạo buttons và định nghĩa hành động (callbacks) ---
        // Nút START GAME
        buttons.add(new Button(
                buttonX, buttonY,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                "START GAME",
                this::onStartGame // Callback: chuyển state sang PLAYING
        ));

        // Nút HIGH SCORE
        buttons.add(new Button(
                buttonX, buttonY + BUTTON_HEIGHT + BUTTON_SPACING,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                "HIGH SCORE",
                this::onHighScore // Callback: bật cờ hiển thị màn hình điểm cao
        ));

        // Nút SETTINGS
        buttons.add(new Button(
                buttonX, buttonY + (BUTTON_HEIGHT + BUTTON_SPACING) * 2,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                "SETTINGS",
                this::onSettings // Callback: bật cờ hiển thị màn hình cài đặt
        ));

        // Nút QUIT GAME
        buttons.add(new Button(
                buttonX, buttonY + (BUTTON_HEIGHT + BUTTON_SPACING) * 3,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                "QUIT GAME",
                this::onQuitGame // Callback: thoát ứng dụng
        ));

        // Đặt button đầu tiên là selected mặc định
        buttons.get(0).setSelected(true);

        // --- Tạo PowerUp displays (Trang trí chuyển động) ---
        double leftX = centerX - POWERUP_DISPLAY_OFFSET_X * 4.3;
        double startY = centerY - POWERUP_DISPLAY_OFFSET_Y * 2;

        // PowerUps bên trái
        leftPowerUps.add(new PowerUpDisplay(
                PowerUpType.SLOW, leftX, startY,
                POWERUP_SIZE_WIDTH, POWERUP_SIZE_HEIGHT, sprites
        ));

        leftPowerUps.add(new PowerUpDisplay(
                PowerUpType.CATCH, leftX, startY + POWERUP_DISPLAY_OFFSET_Y * 2.5,
                POWERUP_SIZE_WIDTH, POWERUP_SIZE_HEIGHT, sprites
        ));

        leftPowerUps.add(new PowerUpDisplay(
                PowerUpType.DUPLICATE, leftX, startY + POWERUP_DISPLAY_OFFSET_Y * 2.5 * 2,
                POWERUP_SIZE_WIDTH, POWERUP_SIZE_HEIGHT, sprites
        ));

        // PowerUps bên phải
        double rightX = centerX + POWERUP_DISPLAY_OFFSET_X * 4.3;

        rightPowerUps.add(new PowerUpDisplay(
                PowerUpType.EXPAND, rightX, startY,
                POWERUP_SIZE_WIDTH, POWERUP_SIZE_HEIGHT, sprites
        ));

        rightPowerUps.add(new PowerUpDisplay(
                PowerUpType.LASER, rightX, startY + POWERUP_DISPLAY_OFFSET_Y * 2.5,
                POWERUP_SIZE_WIDTH, POWERUP_SIZE_HEIGHT, sprites
        ));

        rightPowerUps.add(new PowerUpDisplay(
                PowerUpType.LIFE, rightX, startY + POWERUP_DISPLAY_OFFSET_Y * 2.5 * 2,
                POWERUP_SIZE_WIDTH, POWERUP_SIZE_HEIGHT, sprites
        ));

        // PowerUp giữa (nếu cần)
        middlePowerUp = new PowerUpDisplay(
                PowerUpType.WARP, centerX - POWERUP_DISPLAY_OFFSET_X, 580,
                POWERUP_SIZE_WIDTH, POWERUP_SIZE_HEIGHT, sprites
        );

        // Tải font UI
        try {
            // Chỉ load font một lần, lưu tên font family
            Font baseFont = AssetLoader.loadFont("optimus.otf", 24);
            fontFamily = baseFont.getFamily();
        } catch (Exception e) {
            // Sử dụng font mặc định nếu không tải được
            fontFamily = "Monospaced";
            System.out.println("MainMenu: Failed to load custom font, using default.");
        }
    }

    /**
     * Vẽ màn hình Menu chính hoặc ủy quyền vẽ cho màn hình con (Điểm cao/Cài đặt).
     *
     * @param gc GraphicsContext để vẽ.
     */
    @Override
    public void render(GraphicsContext gc) {
        // Kiểm tra if: Nếu đang hiển thị màn hình điểm cao, vẽ màn hình con và dừng.
        if (showingHighScore) {
            highScoreDisplay.render(gc);
            return;
        }

        // Kiểm tra if: Nếu đang hiển thị màn hình cài đặt, vẽ màn hình con và dừng.
        if (showingSettings) {
            settingsScreen.render(gc);
            return;
        }

        // --- Bắt đầu vẽ Menu chính ---

        // 1. Vẽ nền
        UIHelper.drawGradientBackground(gc, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT,
                Color.rgb(10, 10, 30), Color.rgb(30, 10, 50));

        // 2. Vẽ logo
        double logoY = 100;
        UIHelper.drawLogo(gc, logo, WINDOW_WIDTH / 2, logoY, LOGO_WIDTH, LOGO_HEIGHT);

        // 3. Vẽ các PowerUp trang trí (animation)
        for (PowerUpDisplay powerUp : leftPowerUps) {
            powerUp.render(gc); // Lặp qua danh sách PowerUp bên trái để vẽ từng cái.
        }
        for (PowerUpDisplay powerUp : rightPowerUps) {
            powerUp.render(gc); // Lặp qua danh sách PowerUp bên phải để vẽ từng cái.
        }
        middlePowerUp.render(gc); // Vẽ PowerUp giữa (nếu cần).

        // 3.1. Vẽ text tên cho PowerUp
        gc.setFont(Font.font(fontFamily, 24)); // Sử dụng font family với size 24
        gc.setFill(Color.WHITE);

        // Bên trái
        double leftNameX = POWERUP_DISPLAY_OFFSET_X + 15;
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Slow", leftNameX, leftPowerUps.get(0).getY());
        gc.fillText("Catch", leftNameX, leftPowerUps.get(1).getY());
        gc.fillText("Duplicate", leftNameX, leftPowerUps.get(2).getY());

        // Bên phải
        double rightNameX = WINDOW_WIDTH - POWERUP_DISPLAY_OFFSET_X - 15;
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("Expand", rightNameX, rightPowerUps.get(0).getY());
        gc.fillText("Laser", rightNameX, rightPowerUps.get(1).getY());
        gc.fillText("Extra Life", rightNameX, rightPowerUps.get(2).getY());

        // Ở giữa (nếu cần)
        double middleNameX = WINDOW_WIDTH / 2 - POWERUP_SIZE_WIDTH + 23;
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Warp",middleNameX, middlePowerUp.getY());

        // 3.2. Vẽ mô tả ngắn cho PowerUps
        gc.setFont(Font.font(fontFamily, 18)); // Sử dụng font family với size 18

        // Bên trái
        double leftDescX = leftPowerUps.get(0).getX() - POWERUP_SIZE_WIDTH / 2;
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Slows down\nball speed", leftDescX, leftPowerUps.get(0).getY() + POWERUP_SIZE_WIDTH);
        gc.fillText("Catch and\nrelease the ball", leftDescX, leftPowerUps.get(1).getY() + POWERUP_SIZE_WIDTH);
        gc.fillText("Duplicates\nthe ball", leftDescX, leftPowerUps.get(2).getY() + POWERUP_SIZE_WIDTH);

        // Bên phải
        double rightDescX = rightPowerUps.get(0).getX() + POWERUP_SIZE_WIDTH / 2;
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("Expands the\npaddle size", rightDescX, rightPowerUps.get(0).getY() + POWERUP_SIZE_WIDTH);
        gc.fillText("Enables laser\nshooting", rightDescX, rightPowerUps.get(1).getY() + POWERUP_SIZE_WIDTH);
        gc.fillText("Grants an\nextra life", rightDescX, rightPowerUps.get(2).getY() + POWERUP_SIZE_WIDTH);

        // Ở giữa (nếu cần)
        double middleDescX = middlePowerUp.getX() - POWERUP_SIZE_WIDTH / 2;
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Jump to\nthe next level", middleDescX, middlePowerUp.getY() + POWERUP_SIZE_WIDTH);

        // 4. Vẽ các nút bấm
        for (Button button : buttons) {
            button.render(gc); // Lặp qua danh sách nút để vẽ từng nút.
        }

        // 5. Vẽ hướng dẫn sử dụng
        UIHelper.drawCenteredText(gc, "Use Arrow Keys or Mouse to Navigate",
                WINDOW_WIDTH / 2, WINDOW_HEIGHT - 50,
                Font.font(fontFamily, 14), Color.LIGHTGRAY);
    }

    /**
     * Cập nhật logic màn hình (cập nhật animation PowerUp hoặc ủy quyền cho màn hình con).
     *
     * @param deltaTime Thời gian trôi qua giữa các frame.
     */
    @Override
    public void update(long deltaTime) {
        // Kiểm tra if: Nếu đang hiển thị màn hình con, cập nhật màn hình con và dừng.
        if (showingHighScore) {
            highScoreDisplay.update(deltaTime);
            return;
        }

        // Kiểm tra if: Nếu đang hiển thị màn hình con, cập nhật màn hình con và dừng.
        if (showingSettings) {
            settingsScreen.update(deltaTime);
            return;
        }

        // Cập nhật animation PowerUp trang trí
        long currentTime = System.currentTimeMillis();
        for (PowerUpDisplay powerUp : leftPowerUps) {
            powerUp.update(currentTime); // Lặp và cập nhật animation cho PowerUp bên trái.
        }
        for (PowerUpDisplay powerUp : rightPowerUps) {
            powerUp.update(currentTime); // Lặp và cập nhật animation cho PowerUp bên phải.
        }
        middlePowerUp.update(currentTime);
    }

    /**
     * Xử lý sự kiện nhấn phím để điều hướng hoặc kích hoạt chức năng.
     *
     * @param keyCode Mã phím được nhấn.
     */
    @Override
    public void handleKeyPressed(KeyCode keyCode) {
        // --- Xử lý màn hình con trước ---
        // Kiểm tra if: Nếu đang ở màn hình điểm cao, chỉ xử lý phím ESC.
        if (showingHighScore) {
            if (keyCode == KeyCode.ESCAPE) {
                showingHighScore = false; // Nhấn ESC thoát màn hình điểm cao
            }
            return;
        }

        // Kiểm tra if: Nếu đang ở màn hình cài đặt, ủy quyền xử lý phím cho màn hình cài đặt.
        if (showingSettings) {
            settingsScreen.handleKeyPressed(keyCode);
            return;
        }

        // --- Xử lý điều hướng Menu chính ---
        switch (keyCode) {
            case UP:
                navigateUp(); // Phím Mũi tên Lên: chọn nút trước đó
                break;
            case DOWN:
                navigateDown(); // Phím Mũi tên Xuống: chọn nút tiếp theo
                break;
            case ENTER:
            case SPACE:
                buttons.get(selectedButtonIndex).click(); // Phím ENTER/SPACE: Kích hoạt nút đang chọn
                break;
            case ESCAPE:
                onQuitGame(); // Phím ESC: Thoát game
                break;
        }
    }

    /**
     * Xử lý sự kiện nhả phím (không sử dụng trong Menu).
     *
     * @param keyCode Mã phím được nhả.
     */
    @Override
    public void handleKeyReleased(KeyCode keyCode) {
        // Không sử dụng
    }

    /**
     * Xử lý sự kiện nhấp chuột.
     *
     * @param event Sự kiện chuột.
     */
    @Override
    public void handleMouseClicked(MouseEvent event) {
        // Kiểm tra if: Nếu đang ở màn hình con, bỏ qua nhấp chuột.
        if (showingHighScore) {
            return;
        }

        // Kiểm tra if: Nếu đang ở màn hình cài đặt, ủy quyền xử lý nhấp chuột.
        if (showingSettings) {
            settingsScreen.handleMouseClicked(event);
            return;
        }

        double mouseX = event.getX();
        double mouseY = event.getY();

        // Kiểm tra và kích hoạt nút bấm khi nhấp chuột
        for (Button button : buttons) {
            // Kiểm tra if: Nếu chuột nằm trong phạm vi nút
            if (button.contains(mouseX, mouseY)) {
                button.click(); // Kích hoạt nút
                break; // Chỉ xử lý một nút duy nhất
            }
        }
    }

    /**
     * Xử lý sự kiện di chuyển chuột (dùng để cập nhật trạng thái hover và selected).
     *
     * @param event Sự kiện chuột.
     */
    @Override
    public void handleMouseMoved(MouseEvent event) {
        // Kiểm tra if: Nếu đang ở màn hình con, bỏ qua di chuyển chuột.
        if (showingHighScore) {
            return;
        }

        // Kiểm tra if: Nếu đang ở màn hình cài đặt, ủy quyền xử lý di chuyển chuột.
        if (showingSettings) {
            settingsScreen.handleMouseMoved(event);
            return;
        }

        double mouseX = event.getX();
        double mouseY = event.getY();

        // Cập nhật trạng thái hover và đồng bộ với trạng thái selected
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            boolean isHovered = button.contains(mouseX, mouseY);
            button.setHovered(isHovered); // Thiết lập trạng thái hover

            // Kiểm tra if: Nếu chuột di chuyển qua một nút khác nút đang chọn
            if (isHovered && selectedButtonIndex != i) {
                buttons.get(selectedButtonIndex).setSelected(false); // Bỏ chọn nút cũ
                selectedButtonIndex = i; // Cập nhật chỉ số nút đang được chọn
                button.setSelected(true); // Chọn nút mới
            }
        }
    }

    /**
     * Xử lý khi màn hình được kích hoạt (vào Menu).
     */
    @Override
    public void onEnter() {
        // Đặt lại các cờ và lựa chọn khi vào menu
        showingHighScore = false;
        showingSettings = false;
        selectedButtonIndex = 0;
        updateButtonSelection(); // Đảm bảo nút đầu tiên được chọn
    }

    /**
     * Xử lý khi màn hình bị vô hiệu hóa (thoát Menu).
     */
    @Override
    public void onExit() {
        // Dọn dẹp tài nguyên hoặc dừng nhạc nền nếu cần thiết.
    }

    /**
     * Điều hướng lên nút phía trên.
     */
    private void navigateUp() {
        buttons.get(selectedButtonIndex).setSelected(false); // Bỏ chọn nút hiện tại
        selectedButtonIndex--; // Giảm chỉ số
        // Kiểm tra if: Nếu vượt quá giới hạn trên (nhỏ hơn 0)
        if (selectedButtonIndex < 0) {
            selectedButtonIndex = buttons.size() - 1; // Quay vòng lên nút cuối cùng
        }
        updateButtonSelection(); // Cập nhật trạng thái hiển thị
    }

    /**
     * Điều hướng xuống nút phía dưới.
     */
    private void navigateDown() {
        buttons.get(selectedButtonIndex).setSelected(false); // Bỏ chọn nút hiện tại
        selectedButtonIndex++; // Tăng chỉ số
        // Kiểm tra if: Nếu vượt quá giới hạn dưới (lớn hơn hoặc bằng tổng số nút)
        if (selectedButtonIndex >= buttons.size()) {
            selectedButtonIndex = 0; // Quay vòng xuống nút đầu tiên
        }
        updateButtonSelection(); // Cập nhật trạng thái hiển thị
    }

    /**
     * Cập nhật trạng thái selected của tất cả buttons (đảm bảo chỉ có một nút được chọn).
     */
    private void updateButtonSelection() {
        for (int i = 0; i < buttons.size(); i++) {
            // Kiểm tra if: Chỉ đặt selected cho nút có chỉ số trùng với selectedButtonIndex
            buttons.get(i).setSelected(i == selectedButtonIndex);
        }
    }

    // --- Button callbacks (Hành động khi nút được nhấn) ---

    /**
     * Chuyển trạng thái game sang PLAYING để bắt đầu game mới.
     */
    private void onStartGame() {
        stateManager.setState(GameState.PLAYING);
    }

    /**
     * Hiển thị màn hình điểm cao.
     */
    private void onHighScore() {
        showingHighScore = true;
        highScoreDisplay.onEnter(); // Xử lý logic khi vào màn hình điểm cao
    }

    /**
     * Hiển thị màn hình cài đặt.
     */
    private void onSettings() {
        showingSettings = true;
        settingsScreen.onEnter(); // Xử lý logic khi vào màn hình cài đặt
    }

    /**
     * Callback được gọi khi thoát khỏi màn hình cài đặt (SettingsScreen).
     */
    private void onBackFromSettings() {
        showingSettings = false;
    }

    /**
     * Thoát ứng dụng (gọi System.exit(0)).
     */
    private void onQuitGame() {
        System.exit(0);
    }
}