package UI.Menu;

import Engine.AudioManager;
import UI.Button;
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

/**
 * Lớp màn hình Cài đặt (Settings Screen).
 * Cho phép người chơi điều chỉnh âm lượng nhạc nền và bật/tắt tiếng.
 */
public class SettingsScreen implements Screen {
    private final AudioManager audioManager; // Quản lý và điều khiển âm thanh game (nhạc nền, SFX).
    private final Runnable onBack; // Callback được gọi khi người dùng chọn thoát màn hình cài đặt (quay lại Menu chính).
    private final SpriteProvider sprites; // Cung cấp các tài nguyên hình ảnh (sprites).
    private final Image logo; // Sprite logo game được hiển thị trên màn hình.

    // Các thành phần UI
    private Button muteButton; // Nút để tắt toàn bộ âm thanh (MUTE).
    private Button unmuteButton; // Nút để bật lại âm thanh (UNMUTE).

    // Trạng thái lựa chọn bằng phím điều hướng
    private int selectedOption = 0; // 0 = Điều chỉnh âm lượng (VOLUME), 1 = Bật/Tắt tiếng (SOUND).

    // Điều khiển âm lượng
    private static final double VOLUME_STEP = 0.01; // Bước điều chỉnh âm lượng: 5% mỗi lần nhấn phím.

    // Các hằng số layout
    private static final double WINDOW_WIDTH = Constants.Window.WINDOW_WIDTH;
    private static final double WINDOW_HEIGHT = Constants.Window.WINDOW_HEIGHT;
    private static final double BUTTON_WIDTH = 150;
    private static final double BUTTON_HEIGHT = 50;
    private static final double LOGO_WIDTH = Constants.UISprites.LOGO_WIDTH;
    private static final double LOGO_HEIGHT = Constants.UISprites.LOGO_HEIGHT;

    private String fontFamilyOptimus; // Lưu tên font family để tái sử dụng
    private String fontFamilyGeneration; // Lưu tên font family để tái sử dụng
    private String fontFamilyEmulogic; // Lưu tên font family để tái sử dụng
    /**
     * Constructor.
     * @param audioManager AudioManager để điều khiển âm thanh.
     * @param sprites SpriteProvider để lấy tài nguyên hình ảnh.
     * @param onBack Callback được gọi khi nhấn nút BACK (ESC) để quay lại màn hình trước.
     */
    public SettingsScreen(AudioManager audioManager, SpriteProvider sprites, Runnable onBack) {
        this.audioManager = audioManager;
        this.sprites = sprites;
        this.onBack = onBack;
        this.logo = sprites.get("logo.png"); // Tải logo.
        initializeComponents(); // Khởi tạo các thành phần UI.
    }

    /**
     * Khởi tạo các UI components (chủ yếu là nút MUTE/UNMUTE) và tính toán vị trí.
     */
    private void initializeComponents() {
        double centerX = WINDOW_WIDTH / 2;

        // Vị trí Y cho khối nút Mute/Unmute
        double buttonY = 490;
        double buttonSpacing = 20;
        double totalButtonWidth = BUTTON_WIDTH * 2 + buttonSpacing;
        double leftButtonX = centerX - totalButtonWidth / 2; // Vị trí X bắt đầu để căn giữa cả khối.

        // Khởi tạo nút MUTE
        muteButton = new Button(
                leftButtonX, buttonY,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                "MUTE",
                () -> audioManager.setMuted(true) // Hành động: Tắt tiếng nhạc nền và SFX.
        );

        // Khởi tạo nút UNMUTE
        unmuteButton = new Button(
                leftButtonX + BUTTON_WIDTH + buttonSpacing, buttonY,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                "UNMUTE",
                () -> audioManager.setMuted(false) // Hành động: Bật tiếng trở lại.
        );

        try {
            // Chỉ load font một lần, lưu tên font family
            Font baseFontEmulogic = AssetLoader.loadFont("emulogic.ttf", 24);
            Font baseFontGeneration = AssetLoader.loadFont("generation.ttf", 24);
            Font baseFontOptimus = AssetLoader.loadFont("optimus.otf", 24);
            fontFamilyEmulogic = baseFontEmulogic.getFamily();
            fontFamilyGeneration = baseFontGeneration.getFamily();
            fontFamilyOptimus = baseFontOptimus.getFamily();
        } catch (Exception e) {
            // Sử dụng font mặc định nếu không tải được
            fontFamilyEmulogic = "Courier New";
            fontFamilyGeneration = "Courier New";
            fontFamilyOptimus = "Monospaced";
            System.out.println("MainMenu: Failed to load custom font, using default.");
        }
    }

    /**
     * Vẽ tất cả các thành phần UI của màn hình cài đặt lên GraphicsContext.
     *
     * @param gc GraphicsContext để vẽ.
     */
    @Override
    public void render(GraphicsContext gc) {
        // Vẽ nền gradient (tương tự Menu chính).
        UIHelper.drawGradientBackground(gc, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT,
                Color.rgb(10, 10, 30), Color.rgb(30, 10, 50));

        // Vẽ logo ở trên cùng
        double logoY = 100;
        UIHelper.drawLogo(gc, logo, WINDOW_WIDTH / 2, logoY, LOGO_WIDTH, LOGO_HEIGHT);

        // Vẽ tiêu đề chính của màn hình
        gc.setFill(Color.GOLD);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font(fontFamilyEmulogic, 30));
        gc.fillText("AUDIO SETTINGS", WINDOW_WIDTH / 2, 220);

        // --- Khu vực điều chỉnh VOLUME ---
        // Highlight chữ VOLUME nếu đang được chọn (selectedOption == 0)
        gc.setFill(selectedOption == 0 ? Color.YELLOW : Color.WHITE);
        gc.setFont(Font.font(fontFamilyOptimus, 24));
        gc.fillText("VOLUME", WINDOW_WIDTH / 2, 280);

        // Vẽ thanh volume bar trực quan
        drawVolumeBar(gc);

        // Vẽ phần trăm âm lượng hiện tại
        int volumePercent = (int) (audioManager.getVolume() * 100);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(fontFamilyEmulogic, 20));
        gc.fillText(volumePercent + "%", WINDOW_WIDTH / 2, 370);

        // Vẽ hướng dẫn điều chỉnh âm lượng
        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font(fontFamilyOptimus, 14));
        gc.fillText("Use LEFT/RIGHT arrows to adjust", WINDOW_WIDTH / 2, 395);

        // --- Khu vực BẬT/TẮT tiếng (MUTE/UNMUTE) ---
        // Highlight chữ SOUND nếu đang được chọn (selectedOption == 1)
        gc.setFill(selectedOption == 1 ? Color.YELLOW : Color.WHITE);
        gc.setFont(Font.font(fontFamilyOptimus, 24));
        gc.fillText("SOUND", WINDOW_WIDTH / 2, 450);

        // Cập nhật trạng thái selected của các nút Mute/Unmute
        boolean isMuted = audioManager.isMuted();

        // Nút MUTE sáng nếu đang tắt tiếng VÀ đang chọn mục SOUND
        muteButton.setSelected(isMuted && selectedOption == 1);
        // Nút UNMUTE sáng nếu đang bật tiếng VÀ đang chọn mục SOUND
        unmuteButton.setSelected(!isMuted && selectedOption == 1);

        // Nếu đang chọn mục SOUND, hủy trạng thái hover để tránh xung đột với trạng thái selected.
        if (selectedOption == 1) {
            muteButton.setHovered(false);
            unmuteButton.setHovered(false);
        }

        // Vẽ các nút MUTE/UNMUTE
        muteButton.render(gc);
        unmuteButton.render(gc);

        // Vẽ hướng dẫn điều khiển nút
        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font(fontFamilyOptimus, 14));
        gc.fillText("Use UP/DOWN arrows to select, ENTER to toggle", WINDOW_WIDTH / 2, 565);

        // Vẽ hướng dẫn thoát màn hình
        gc.setFill(Color.GOLD);
        gc.setFont(Font.font(fontFamilyOptimus, 16));
        gc.fillText("Press ESC to return to menu", WINDOW_WIDTH / 2, WINDOW_HEIGHT - 50);
    }

    /**
     * Vẽ thanh volume bar (biểu thị mức âm lượng) với nền và phần tô đầy.
     *
     * @param gc GraphicsContext để vẽ.
     */
    private void drawVolumeBar(GraphicsContext gc) {
        double barWidth = 400;
        double barHeight = 20;
        double barX = (WINDOW_WIDTH - barWidth) / 2;
        double barY = 330;

        // Vẽ phần nền (track) của thanh bar (màu xám tối)
        gc.setFill(Color.rgb(80, 80, 80));
        gc.fillRect(barX, barY, barWidth, barHeight);

        // Vẽ phần tô đầy dựa trên mức âm lượng hiện tại
        double fillWidth = barWidth * audioManager.getVolume();
        gc.setFill(Color.rgb(100, 200, 100)); // Màu xanh lá cây sáng
        gc.fillRect(barX, barY, fillWidth, barHeight);

        // Vẽ viền thanh bar (màu trắng)
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(barX, barY, barWidth, barHeight);
    }

    /**
     * Cập nhật logic màn hình (hiện tại không có animation phức tạp).
     *
     * @param deltaTime Thời gian trôi qua giữa các frame.
     */
    @Override
    public void update(long deltaTime) {
        // Không có logic cập nhật animation trong màn hình này.
    }

    /**
     * Xử lý sự kiện nhấn phím để điều hướng và thay đổi cài đặt.
     *
     * @param keyCode Mã phím được nhấn.
     */
    @Override
    public void handleKeyPressed(KeyCode keyCode) {
        switch (keyCode) {
            case ESCAPE:
                onBack.run(); // Thoát màn hình cài đặt
                break;

            case UP:
                // Chuyển lên mục VOLUME (selectedOption = 0)
                selectedOption = 0;
                break;

            case DOWN:
                // Chuyển xuống mục SOUND (selectedOption = 1)
                selectedOption = 1;
                break;

            case LEFT:
                // Giảm âm lượng khi đang chọn mục VOLUME (selectedOption == 0)
                if (selectedOption == 0) {
                    double newVolume = Math.max(0.0, audioManager.getVolume() - VOLUME_STEP); // Giới hạn dưới là 0.0
                    audioManager.setVolume(newVolume);
                }
                break;

            case RIGHT:
                // Tăng âm lượng khi đang chọn mục VOLUME (selectedOption == 0)
                if (selectedOption == 0) {
                    double newVolume = Math.min(1.0, audioManager.getVolume() + VOLUME_STEP); // Giới hạn trên là 1.0
                    audioManager.setVolume(newVolume);
                }
                break;

            case ENTER:
            case SPACE:
                // Chuyển đổi trạng thái Mute/Unmute khi đang chọn mục SOUND (selectedOption == 1)
                if (selectedOption == 1) {
                    audioManager.setMuted(!audioManager.isMuted());
                }
                break;
        }
    }

    /**
     * Xử lý sự kiện nhả phím (không sử dụng).
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
        double mouseX = event.getX();
        double mouseY = event.getY();

        // Kiểm tra nhấp vào nút MUTE/UNMUTE
        if (muteButton.contains(mouseX, mouseY)) {
            selectedOption = 1; // Chuyển sang mục SOUND
            muteButton.click();
        } else if (unmuteButton.contains(mouseX, mouseY)) {
            selectedOption = 1; // Chuyển sang mục SOUND
            unmuteButton.click();
        }
        // Lưu ý: Nhấp chuột không ảnh hưởng đến thanh volume bar (phải dùng phím LEFT/RIGHT).
    }

    /**
     * Xử lý sự kiện di chuyển chuột (dùng để cập nhật trạng thái hover).
     *
     * @param event Sự kiện chuột.
     */
    @Override
    public void handleMouseMoved(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        // Cập nhật trạng thái hover (chỉ để phản hồi trực quan)
        // Chỉ cập nhật hover nếu đang không ở chế độ chọn nút bằng phím (selectedOption != 1)
        if (selectedOption != 1) {
            muteButton.setHovered(muteButton.contains(mouseX, mouseY));
            unmuteButton.setHovered(unmuteButton.contains(mouseX, mouseY));
        }
    }

    /**
     * Xử lý khi màn hình được kích hoạt (vào màn hình).
     */
    @Override
    public void onEnter() {
        selectedOption = 0; // Luôn bắt đầu với mục Volume được chọn
    }

    /**
     * Xử lý khi màn hình bị vô hiệu hóa (thoát màn hình).
     */
    @Override
    public void onExit() {
        // Không có hành động dọn dẹp cần thiết.
    }
}