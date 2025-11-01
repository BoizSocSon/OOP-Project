package UI.Screens;

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

/**
 * Lớp màn hình Tạm dừng (Pause Screen) khi game bị tạm dừng.
 * Hiển thị thông tin vòng chơi hiện tại, điểm số và số mạng để người chơi tiếp tục hoặc thoát.
 */
public class PauseScreen implements Screen {
    private final SpriteProvider sprites; // Nguồn cung cấp sprite.
    private Image logo; // Sprite logo game.

    // Font families
    private String fontFamilyOptimus; // Lưu tên font family để tái sử dụng
    private String fontFamilyGeneration; // Lưu tên font family để tái sử dụng
    private String fontFamilyEmulogic; // Lưu tên font family để tái sử dụng

    // Thông tin trạng thái game
    private int currentRound; // Vòng chơi hiện tại.
    private String roundName; // Tên vòng chơi.
    private int currentScore; // Điểm số hiện tại.
    private int currentLives; // Số mạng hiện tại.

    // Các hằng số layout
    private static final double WINDOW_WIDTH = Constants.Window.WINDOW_WIDTH;
    private static final double WINDOW_HEIGHT = Constants.Window.WINDOW_HEIGHT;
    private static final double LOGO_WIDTH = 320; // Chiều rộng logo được điều chỉnh.
    private static final double LOGO_HEIGHT = 116; // Chiều cao logo (giữ tỷ lệ).

    /**
     * Constructor.
     * @param sprites SpriteProvider để lấy tài nguyên hình ảnh.
     */
    public PauseScreen(SpriteProvider sprites) {
        this.sprites = sprites;
        loadAssets(); // Tải tài nguyên.
    }

    /**
     * Tải các sprite cần thiết cho màn hình (chủ yếu là logo).
     */
    private void loadAssets() {
        logo = sprites.get("logo.png");
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
            System.out.println("PauseScreen: Failed to load custom font, using default.");
        }
    }

    /**
     * Thiết lập thông tin game để màn hình hiển thị.
     *
     * @param round Vòng chơi hiện tại (số).
     * @param roundName Tên vòng chơi.
     * @param score Điểm hiện tại.
     * @param lives Số mạng hiện tại.
     */
    public void setGameInfo(int round, String roundName, int score, int lives) {
        this.currentRound = round;
        this.roundName = roundName;
        this.currentScore = score;
        this.currentLives = lives;
    }

    /**
     * Vẽ tất cả các thành phần UI lên màn hình.
     *
     * @param gc GraphicsContext để vẽ.
     */
    @Override
    public void render(GraphicsContext gc) {
        // Vẽ lớp phủ bán trong suốt (làm tối màn hình nền game)
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // --- Khu vực hộp thông báo chính ---
        double boxWidth = 400;
        double boxHeight = 350;
        double boxX = (WINDOW_WIDTH - boxWidth) / 2;
        double boxY = (WINDOW_HEIGHT - boxHeight) / 2;

        // Vẽ hộp và viền (màu xanh cyan)
        UIHelper.drawBox(gc, boxX, boxY, boxWidth, boxHeight,
                Color.rgb(20, 20, 40, 0.95), Color.CYAN, 3);

        // Vẽ logo bên trong hộp
        UIHelper.drawLogo(gc, logo, WINDOW_WIDTH / 2, boxY + 60, LOGO_WIDTH, LOGO_HEIGHT);

        // Vẽ tiêu đề "GAME PAUSED"
        UIHelper.drawCenteredText(gc, "GAME PAUSED",
                WINDOW_WIDTH / 2, boxY + 130,
                Font.font(fontFamilyEmulogic, 32), Color.YELLOW);

        // --- Vẽ thông tin game ---
        Font infoFont = Font.font(fontFamilyOptimus, 20);
        Color infoColor = Color.WHITE;
        double infoY = boxY + 180;
        double lineSpacing = 35;

        // Thông tin vòng chơi
        UIHelper.drawCenteredText(gc, String.format("Round: %d - \"%s\"", currentRound, roundName),
                WINDOW_WIDTH / 2, infoY,
                infoFont, infoColor);

        // Thông tin điểm số (định dạng có dấu phẩy)
        UIHelper.drawCenteredText(gc, String.format("Score: %,d", currentScore),
                WINDOW_WIDTH / 2, infoY + lineSpacing,
                infoFont, infoColor);

        // Thông tin số mạng (hiển thị bằng biểu tượng trái tim)
        String livesStr = "Lives: " + getHeartString(currentLives);
        UIHelper.drawCenteredText(gc, livesStr,
                WINDOW_WIDTH / 2, infoY + lineSpacing * 2,
                infoFont, Color.RED);

        // --- Vẽ hướng dẫn ---
        Font instructionFont = Font.font(fontFamilyOptimus, 14);
        Color instructionColor = Color.LIGHTGRAY;
        double instructionY = boxY + boxHeight - 60;

        // Hướng dẫn tiếp tục game
        UIHelper.drawCenteredText(gc, "Press SPACE to continue",
                WINDOW_WIDTH / 2, instructionY,
                instructionFont, instructionColor);

        // Hướng dẫn quay lại menu
        UIHelper.drawCenteredText(gc, "Press ESC to return to menu",
                WINDOW_WIDTH / 2, instructionY + 25,
                instructionFont, instructionColor);
    }

    /**
     * Tạo chuỗi biểu tượng trái tim (♥) tương ứng với số mạng.
     *
     * @param lives Số mạng hiện tại.
     * @return Chuỗi ký tự trái tim.
     */
    private String getHeartString(int lives) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lives; i++) {
            sb.append("♥ ");
        }
        return sb.toString().trim();
    }

    /**
     * Cập nhật logic màn hình (không có animation).
     *
     * @param deltaTime Thời gian trôi qua giữa các frame.
     */
    @Override
    public void update(long deltaTime) {
        // Không có animation cần cập nhật
    }

    /**
     * Xử lý sự kiện nhấn phím (được xử lý bởi lớp GameManager/MainGameLoop).
     *
     * @param keyCode Mã phím được nhấn.
     */
    @Override
    public void handleKeyPressed(KeyCode keyCode) {
        // Logic chuyển trạng thái được xử lý ở lớp ngoài (GameManager/MainGameLoop).
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
     * Xử lý sự kiện nhấp chuột (không sử dụng).
     *
     * @param event Sự kiện chuột.
     */
    @Override
    public void handleMouseClicked(MouseEvent event) {
        // Không sử dụng
    }

    /**
     * Xử lý sự kiện di chuyển chuột (không sử dụng).
     *
     * @param event Sự kiện chuột.
     */
    @Override
    public void handleMouseMoved(MouseEvent event) {
        // Không sử dụng
    }

    /**
     * Xử lý khi màn hình được kích hoạt (vào trạng thái Pause).
     */
    @Override
    public void onEnter() {
        // Có thể thêm logic lưu trạng thái game tạm thời tại đây.
    }

    /**
     * Xử lý khi màn hình bị vô hiệu hóa (thoát trạng thái Pause).
     */
    @Override
    public void onExit() {
        // Dọn dẹp nếu cần thiết.
    }
}