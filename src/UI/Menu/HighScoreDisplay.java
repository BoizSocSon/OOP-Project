package UI.Menu;

import Engine.HighScoreManager;
import Engine.HighScoreManager.HighScoreEntry;
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

import java.util.List;

/**
 * Lớp màn hình hiển thị điểm cao (High Score Display).
 * Chịu trách nhiệm tải, hiển thị danh sách top scores và các thành phần UI liên quan.
 */
public class HighScoreDisplay implements Screen {
    private final SpriteProvider sprites; // Nguồn cung cấp sprite (hình ảnh).
    private final HighScoreManager highScoreManager; // Quản lý điểm cao.
    private Image logo; // Sprite logo game.
    private String fontFamilyOptimus; // Lưu tên font family để tái sử dụng
    private String fontFamilyGeneration; // Lưu tên font family để tái sử dụng
    private String fontFamilyEmulogic; // Lưu tên font family để tái sử dụng
    // Các hằng số kích thước và vị trí UI.
    private static final double WINDOW_WIDTH = Constants.Window.WINDOW_WIDTH;
    private static final double WINDOW_HEIGHT = Constants.Window.WINDOW_HEIGHT;
    private static final double LOGO_WIDTH = 320;
    private static final double LOGO_HEIGHT = 116;
    private static final double TABLE_START_Y = 250; // Vị trí Y bắt đầu bảng điểm.
    private static final double ROW_HEIGHT = 40; // Chiều cao mỗi hàng trong bảng.
    private static final double COL_RANK_X = 65; // Vị trí X cột Hạng.
    private static final double COL_NAME_X = COL_RANK_X + 100; // Vị trí X cột Tên.
    private static final double COL_SCORE_X = COL_NAME_X + 150; // Vị trí X cột Điểm.
    private static final double COL_DATE_X = COL_SCORE_X + 130; // Vị trí X cột Ngày.

    /**
     * Constructor.
     *
     * @param sprites Đối tượng SpriteProvider để tải tài nguyên.
     */
    public HighScoreDisplay(SpriteProvider sprites) {
        this.sprites = sprites;
        // Khởi tạo HighScoreManager, manager sẽ tự động tải scores từ file.
        this.highScoreManager = new HighScoreManager();
        loadAssets(); // Tải các tài nguyên cần thiết.
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
            System.out.println("MainMenu: Failed to load custom font, using default.");
        }
    }

    /**
     * Vẽ tất cả các thành phần UI lên màn hình.
     *
     * @param gc GraphicsContext để vẽ.
     */
    @Override
    public void render(GraphicsContext gc) {
        // Vẽ nền gradient
        UIHelper.drawGradientBackground(gc, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT,
                Color.rgb(10, 10, 30), Color.rgb(30, 10, 50));

        // Vẽ logo
        UIHelper.drawLogo(gc, logo, WINDOW_WIDTH / 2, 120, LOGO_WIDTH, LOGO_HEIGHT);

        // Vẽ tiêu đề màn hình
        UIHelper.drawCenteredText(gc, "HIGH SCORES",
                WINDOW_WIDTH / 2, 200,
                Font.font(fontFamilyEmulogic, 30), Color.WHITE);

        // --- Cấu hình và vẽ tiêu đề bảng (Header) ---
        Font headerFont = Font.font(fontFamilyOptimus, 18);
        Font dataFont = Font.font(fontFamilyOptimus, 16);
        Color headerColor = Color.YELLOW;
        Color dataColor = Color.WHITE;

        double headerY = TABLE_START_Y;

        // Vẽ tên các cột
        UIHelper.drawLeftAlignedText(gc, "RANK", COL_RANK_X, headerY, headerFont, headerColor);
        UIHelper.drawLeftAlignedText(gc, "NAME", COL_NAME_X, headerY, headerFont, headerColor);
        UIHelper.drawLeftAlignedText(gc, "SCORE", COL_SCORE_X, headerY, headerFont, headerColor);
        UIHelper.drawLeftAlignedText(gc, "DATE", COL_DATE_X, headerY, headerFont, headerColor);

        // Vẽ đường phân cách (Separator line)
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        gc.strokeLine(COL_RANK_X, headerY + 25, WINDOW_WIDTH - 60, headerY + 25);

        // Vẽ dữ liệu điểm số (Scores Data)
        List<HighScoreEntry> scores = highScoreManager.getAllScores();
        double rowY = TABLE_START_Y + 40; // Bắt đầu hàng dữ liệu đầu tiên

        for (HighScoreEntry entry : scores) {
            // Đổi màu nền hàng xen kẽ
            if (entry.getRank() % 2 == 0) {
                gc.setFill(Color.rgb(20, 20, 40, 0.5)); // Màu tối mờ
                gc.fillRect(COL_RANK_X - 10, rowY - 5, WINDOW_WIDTH - 180, ROW_HEIGHT - 5);
            }

            // Vẽ dữ liệu từng cột
            UIHelper.drawLeftAlignedText(gc, String.valueOf(entry.getRank()),
                    COL_RANK_X, rowY, dataFont, dataColor);
            UIHelper.drawLeftAlignedText(gc, entry.getPlayerName(),
                    COL_NAME_X, rowY, dataFont, dataColor);
            // Định dạng điểm số có dấu phẩy ngăn cách hàng nghìn
            UIHelper.drawLeftAlignedText(gc, String.format("%,d", entry.getScore()),
                    COL_SCORE_X, rowY, dataFont, dataColor);
            UIHelper.drawLeftAlignedText(gc, entry.getFormattedDate(),
                    COL_DATE_X, rowY, dataFont, dataColor);

            rowY += ROW_HEIGHT; // Chuyển sang hàng tiếp theo
        }

        // Vẽ hướng dẫn thoát màn hình
        UIHelper.drawCenteredText(gc, "Press ESC to return to menu",
                WINDOW_WIDTH / 2, WINDOW_HEIGHT - 50,
                Font.font(fontFamilyOptimus, 14), Color.LIGHTGRAY);
    }

    /**
     * Cập nhật logic màn hình (không có animation đặc biệt).
     *
     * @param deltaTime Thời gian trôi qua giữa các frame (miligiây).
     */
    @Override
    public void update(long deltaTime) {
        // Không cần animation phức tạp.
    }

    /**
     * Xử lý sự kiện nhấn phím (được xử lý bởi MainMenu để quay lại).
     *
     * @param keyCode Mã phím được nhấn.
     */
    @Override
    public void handleKeyPressed(KeyCode keyCode) {
        // Logic quay lại Menu được xử lý ở lớp ngoài.
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
     * Xử lý khi màn hình được kích hoạt (vào màn hình).
     */
    @Override
    public void onEnter() {
        // HighScoreManager tự động load từ file khi khởi tạo, không cần reload thủ công.
    }

    /**
     * Xử lý khi màn hình bị vô hiệu hóa (thoát màn hình).
     */
    @Override
    public void onExit() {
        // Dọn dẹp nếu cần thiết (hiện tại không cần).
    }

    /**
     * Lấy HighScoreManager instance để truy cập điểm số.
     * @return Instance của HighScoreManager.
     */
    public HighScoreManager getHighScoreManager() {
        return highScoreManager;
    }
}