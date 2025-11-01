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
 * Lớp màn hình Game Over khi người chơi thua.
 * Hiển thị điểm cuối cùng, điểm cao nhất (high score), vòng chơi (round) đạt được.
 */
public class GameOverScreen implements Screen {
    private final SpriteProvider sprites; // Nguồn cung cấp sprite.
    private final HighScoreManager highScoreManager; // Quản lý điểm cao.
    private Image logo; // Sprite logo game.

    // Thông tin kết quả game
    private int finalScore; // Điểm số cuối cùng đạt được.
    private int highScore; // Điểm cao nhất hiện tại.
    private int roundReached; // Vòng chơi đã đạt được.
    private boolean isNewHighScore; // Cờ kiểm tra xem điểm cuối cùng có phải là high score mới không.

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
                Font.font("Courier New", 40), Color.RED);

        // --- Vẽ thống kê game ---
        Font statsFont = Font.font("Courier New", 22);
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
            UIHelper.drawCenteredText(gc, "★ NEW HIGH SCORE! ★",
                    WINDOW_WIDTH / 2, statsY + lineSpacing * 3,
                    Font.font("Courier New", 24), Color.YELLOW);
        }

        // --- Vẽ hướng dẫn ---
        Font instructionFont = Font.font("Courier New", 16);
        Color instructionColor = Color.LIGHTGRAY;

        UIHelper.drawCenteredText(gc, "Press ENTER to return to menu",
                WINDOW_WIDTH / 2, boxY + boxHeight - 40,
                instructionFont, instructionColor);
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