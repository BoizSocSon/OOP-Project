package UI.Screens;

import Engine.HighScoreManager;
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
 * Lớp màn hình Game Over khi người chơi thua.
 * Hiển thị điểm cuối cùng, điểm cao nhất (high score), vòng chơi (round) đạt được.
 */
public class GameOverScreen implements Screen {
    private final SpriteProvider sprites; // Nguồn cung cấp sprite.
    private final HighScoreManager highScoreManager; // Quản lý điểm cao.
    private Image logo; // Sprite logo game.

    // Font families
    private String fontFamilyOptimus; // Lưu tên font family để tái sử dụng
    private String fontFamilyGeneration; // Lưu tên font family để tái sử dụng
    private String fontFamilyEmulogic; // Lưu tên font family để tái sử dụng

    // Thông tin kết quả game
    private int finalScore; // Điểm số cuối cùng đạt được.
    private int highScore; // Điểm cao nhất hiện tại.
    private int roundReached; // Vòng chơi đã đạt được.
    private boolean isNewHighScore; // Cờ kiểm tra xem điểm cuối cùng có phải là high score mới không.

    // Animation
    private double starRotation = 0; // Góc quay hiện tại của các ngôi sao trang trí (dùng cho hiệu ứng animation).

    // Các hằng số layout
    private static final double WINDOW_WIDTH = Constants.Window.WINDOW_WIDTH;
    private static final double WINDOW_HEIGHT = Constants.Window.WINDOW_HEIGHT;
    private static final double LOGO_WIDTH = 320; // Chiều rộng logo được điều chỉnh.
    private static final double LOGO_HEIGHT = 116; // Chiều cao logo (giữ tỷ lệ).

    /**
     * Constructor.
     * @param sprites SpriteProvider để lấy tài nguyên hình ảnh.
     * @param highScoreManager HighScoreManager để kiểm tra điểm cao nhất.
     */
    public GameOverScreen(SpriteProvider sprites, HighScoreManager highScoreManager) {
        this.sprites = sprites;
        this.highScoreManager = highScoreManager;
        loadAssets(); // Tải tài nguyên.
    }

    /**
     * Tải các sprite cần thiết cho màn hình.
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
            System.out.println("GameOverScreen: Failed to load custom font, using default.");
        }
    }

    /**
     * Thiết lập kết quả game để hiển thị trên màn hình.
     * @param score Điểm cuối cùng người chơi đạt được.
     * @param round Vòng chơi đã đạt được.
     */
    public void setGameResult(int score, int round) {
        this.finalScore = score;
        this.roundReached = round;
        // Lấy điểm cao nhất hiện tại.
        this.highScore = highScoreManager.getHighestScore();
        // Kiểm tra xem điểm cuối cùng có phải là high score mới không.
        this.isNewHighScore = highScoreManager.isHighScore(score);
    }

    /**
     * Vẽ tất cả các thành phần UI lên màn hình.
     *
     * @param gc GraphicsContext để vẽ.
     */
    @Override
    public void render(GraphicsContext gc) {
        // Vẽ nền gradient (màu đỏ đậm/đen)
        UIHelper.drawGradientBackground(gc, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT,
                Color.rgb(20, 0, 0), Color.rgb(50, 10, 10));

        // --- Khu vực hộp thông báo chính ---
        double boxWidth = 450;
        double boxHeight = 400;
        double boxX = (WINDOW_WIDTH - boxWidth) / 2;
        double boxY = (WINDOW_HEIGHT - boxHeight) / 2;

        // Vẽ hộp và viền đỏ
        UIHelper.drawBox(gc, boxX, boxY, boxWidth, boxHeight,
                Color.rgb(30, 10, 10, 0.95), Color.RED, 3);

        // Vẽ logo bên trong hộp
        UIHelper.drawLogo(gc, logo, WINDOW_WIDTH / 2, boxY + 60, LOGO_WIDTH, LOGO_HEIGHT);

        // Vẽ tiêu đề "GAME OVER"
        UIHelper.drawCenteredText(gc, "GAME OVER",
                WINDOW_WIDTH / 2, boxY + 140,
                Font.font(fontFamilyEmulogic, 40), Color.RED);

        // --- Vẽ thống kê game ---
        Font statsFont = Font.font(fontFamilyOptimus, 22);
        Color statsColor = Color.WHITE;
        double statsY = boxY + 200;
        double lineSpacing = 40;

        // Điểm cuối cùng
        UIHelper.drawCenteredText(gc, String.format("Your Score: %,d", finalScore),
                WINDOW_WIDTH / 2, statsY,
                statsFont, statsColor);

        // Điểm cao nhất
        UIHelper.drawCenteredText(gc, String.format("High Score: %,d", highScore),
                WINDOW_WIDTH / 2, statsY + lineSpacing,
                statsFont, statsColor);

        // Vòng chơi đạt được
        UIHelper.drawCenteredText(gc, String.format("Round Reached: %d", roundReached),
                WINDOW_WIDTH / 2, statsY + lineSpacing * 2,
                statsFont, statsColor);

        // Vẽ thông báo "NEW HIGH SCORE!" nếu có
        if (isNewHighScore) {
            gc.save(); // Lưu trạng thái GC hiện tại

            // Vị trí Y cho ngôi sao và chữ "NEW HIGH SCORE!"
            double starY = statsY + lineSpacing * 3;

            // Vẽ các ngôi sao quay trang trí hai bên
            drawRotatingStar(gc, WINDOW_WIDTH / 2 - 180, starY);
            drawRotatingStar(gc, WINDOW_WIDTH / 2 + 180, starY);

            // Vẽ thông báo chính
            UIHelper.drawCenteredText(gc, "NEW HIGH SCORE!",
                    WINDOW_WIDTH / 2, starY,
                    Font.font(fontFamilyEmulogic, 21), Color.YELLOW);

            gc.restore(); // Khôi phục trạng thái GC
        }

        // --- Vẽ hướng dẫn ---
        Font instructionFont = Font.font(fontFamilyOptimus, 16);
        Color instructionColor = Color.LIGHTGRAY;

        UIHelper.drawCenteredText(gc, "Press ENTER to return to menu",
                WINDOW_WIDTH / 2, boxY + boxHeight - 40,
                instructionFont, instructionColor);
    }

    /**
     * Vẽ một ngôi sao quay tại tọa độ xác định để tạo hiệu ứng animation.
     * Phương thức này sử dụng phép biến đổi (translate, rotate) của GraphicsContext.
     *
     * @param gc GraphicsContext để vẽ.
     * @param x Tọa độ X của tâm ngôi sao.
     * @param y Tọa độ Y của tâm ngôi sao.
     */
    private void drawRotatingStar(GraphicsContext gc, double x, double y) {
        gc.save(); // Lưu trạng thái GC trước khi biến đổi
        gc.translate(x, y); // Di chuyển hệ tọa độ về tâm ngôi sao
        gc.rotate(starRotation); // Xoay GC theo góc quay hiện tại

        // Vẽ hình ngôi sao (sử dụng ký tự Unicode cho đơn giản)
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", 30));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.setTextBaseline(javafx.geometry.VPos.CENTER);
        gc.fillText("★", 0, 0); // Vẽ ngôi sao tại (0, 0) của hệ tọa độ đã được translate và rotate

        gc.restore(); // Khôi phục trạng thái GC về trước khi biến đổi
    }

    /**
     * Cập nhật logic màn hình (không có animation).
     *
     * @param deltaTime Thời gian trôi qua giữa các frame.
     */
    @Override
    public void update(long deltaTime) {
        // Cập nhật góc quay ngôi sao
        starRotation += 2.0; // Quay 2 độ mỗi frame
        if (starRotation >= 360) {
            starRotation -= 360; // Đặt lại về 0 sau mỗi vòng quay đầy đủ
        }
    }

    /**
     * Xử lý sự kiện nhấn phím (được xử lý bởi lớp GameManager/MainGameLoop).
     *
     * @param keyCode Mã phím được nhấn.
     */
    @Override
    public void handleKeyPressed(KeyCode keyCode) {
        // Logic chuyển trạng thái được xử lý ở lớp ngoài.
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
     * Xử lý khi màn hình được kích hoạt (vào trạng thái Game Over).
     */
    @Override
    public void onEnter() {
        // Đặt lại animation khi màn hình được hiển thị
        starRotation = 0;
        // Có thể thêm logic lưu điểm cao vào file tại đây nếu chưa làm ở GameManager.
    }

    /**
     * Xử lý khi màn hình bị vô hiệu hóa (thoát Game Over).
     */
    @Override
    public void onExit() {
        // Dọn dẹp nếu cần thiết.
    }
}