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
 * Lớp màn hình Chiến thắng (Win Screen) khi người chơi hoàn thành tất cả các cấp độ.
 * Hiển thị điểm cuối cùng, điểm cao nhất (high score), và các thông báo chiến thắng.
 */
public class WinScreen implements Screen {
    private final SpriteProvider sprites; // Nguồn cung cấp sprite để tải hình ảnh.
    private final HighScoreManager highScoreManager; // Quản lý điểm cao để lấy và kiểm tra high score.
    private Image logo; // Sprite logo game.

    // Font families
    private String fontFamilyOptimus; // Lưu tên font family để tái sử dụng
    private String fontFamilyGeneration; // Lưu tên font family để tái sử dụng
    private String fontFamilyEmulogic; // Lưu tên font family để tái sử dụng

    // Thông tin kết quả game được thiết lập từ GameManager
    private int finalScore; // Điểm số cuối cùng đạt được.
    private int highScore; // Điểm cao nhất hiện tại.
    private int totalRounds; // Tổng số vòng chơi đã hoàn thành (tất cả các vòng).
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
     * @param highScoreManager HighScoreManager để kiểm tra high score.
     */
    public WinScreen(SpriteProvider sprites, HighScoreManager highScoreManager) {
        this.sprites = sprites;
        this.highScoreManager = highScoreManager;
        loadAssets(); // Tải tài nguyên cần thiết.
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
            System.out.println("WinScreen: Failed to load custom font, using default.");
        }
    }

    /**
     * Thiết lập kết quả game để hiển thị trên màn hình.
     * Phương thức này được gọi bởi GameManager khi chuyển sang trạng thái WIN.
     *
     * @param score Điểm cuối cùng người chơi đạt được.
     * @param rounds Tổng số vòng chơi đã hoàn thành (tất cả các vòng).
     */
    public void setGameResult(int score, int rounds) {
        this.finalScore = score;
        this.totalRounds = rounds;
        this.highScore = highScoreManager.getHighestScore(); // Lấy điểm cao nhất hiện tại từ manager.
        this.isNewHighScore = highScoreManager.isHighScore(score); // Kiểm tra xem điểm mới có lọt vào top không.
    }

    /**
     * Vẽ tất cả các thành phần UI lên màn hình, bao gồm nền, hộp thông báo, và thống kê.
     *
     * @param gc GraphicsContext để vẽ.
     */
    @Override
    public void render(GraphicsContext gc) {
        // Vẽ nền gradient (màu lễ hội/chiến thắng)
        UIHelper.drawGradientBackground(gc, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT,
                Color.rgb(10, 20, 40), Color.rgb(40, 10, 60));

        // --- Thiết lập và vẽ hộp thông báo chính ---
        double boxWidth = 450;
        double boxHeight = 450;
        double boxX = (WINDOW_WIDTH - boxWidth) / 2; // Căn giữa theo chiều ngang.
        double boxY = (WINDOW_HEIGHT - boxHeight) / 2; // Căn giữa theo chiều dọc.

        // Vẽ hộp và viền màu Vàng (GOLD)
        UIHelper.drawBox(gc, boxX, boxY, boxWidth, boxHeight,
                Color.rgb(20, 40, 60, 0.95), Color.GOLD, 3);

        // Vẽ logo bên trong hộp
        UIHelper.drawLogo(gc, logo, WINDOW_WIDTH / 2, boxY + 60, LOGO_WIDTH, LOGO_HEIGHT);

        // --- Vẽ tiêu đề và thông báo chiến thắng ---

        // Vẽ tiêu đề chính
        UIHelper.drawCenteredText(gc, "CONGRATULATIONS!",
                WINDOW_WIDTH / 2, boxY + 140,
                Font.font(fontFamilyEmulogic, 27), Color.GOLD);

        // Vẽ tiêu đề phụ
        UIHelper.drawCenteredText(gc, "You Won!",
                WINDOW_WIDTH / 2, boxY + 180,
                Font.font(fontFamilyGeneration, 24), Color.YELLOW);

        // --- Vẽ thống kê game ---
        Font statsFont = Font.font(fontFamilyOptimus, 22);
        Color statsColor = Color.WHITE;
        double statsY = boxY + 230;
        double lineSpacing = 40;

        // Hiển thị Điểm số của bạn
        UIHelper.drawCenteredText(gc, String.format("Your Score: %,d", finalScore),
                WINDOW_WIDTH / 2, statsY,
                statsFont, statsColor);

        // Hiển thị Điểm cao nhất
        UIHelper.drawCenteredText(gc, String.format("High Score: %,d", highScore),
                WINDOW_WIDTH / 2, statsY + lineSpacing,
                statsFont, statsColor);

        // Hiển thị Tổng số vòng đã hoàn thành
        UIHelper.drawCenteredText(gc, String.format("Rounds Completed: %d", totalRounds),
                WINDOW_WIDTH / 2, statsY + lineSpacing * 2,
                statsFont, statsColor);

        // --- Vẽ thông báo High Score mới (kèm animation) ---
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
                    Font.font(fontFamilyEmulogic, 21), Color.GOLD);

            gc.restore(); // Khôi phục trạng thái GC
        }

        // --- Vẽ hướng dẫn thoát màn hình ---
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
        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Arial", 30));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.setTextBaseline(javafx.geometry.VPos.CENTER);
        gc.fillText("★", 0, 0); // Vẽ ngôi sao tại (0, 0) của hệ tọa độ đã được translate và rotate

        gc.restore(); // Khôi phục trạng thái GC về trước khi biến đổi
    }

    /**
     * Cập nhật logic màn hình cho mỗi frame (chủ yếu dùng để cập nhật animation).
     *
     * @param deltaTime Thời gian trôi qua giữa các frame (miligiây).
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
     * Xử lý sự kiện nhấn phím (logic chuyển trạng thái được xử lý ở MainGameLoop).
     *
     * @param keyCode Mã phím được nhấn.
     */
    @Override
    public void handleKeyPressed(KeyCode keyCode) {
        // Ví dụ: Nhấn ENTER sẽ chuyển trạng thái về MENU.
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
     * Xử lý khi màn hình được kích hoạt (vào trạng thái Win).
     */
    @Override
    public void onEnter() {
        // Đặt lại animation khi màn hình được hiển thị
        starRotation = 0;
        // Có thể thêm logic phát nhạc chiến thắng tại đây.
    }

    /**
     * Xử lý khi màn hình bị vô hiệu hóa (thoát Win Screen).
     */
    @Override
    public void onExit() {
        // Dọn dẹp tài nguyên hoặc dừng nhạc nếu cần thiết.
    }
}