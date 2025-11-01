package UI;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Lớp chứa tất cả các hằng số (constants) liên quan đến giao diện người dùng (UI)
 * của trò chơi, bao gồm màu sắc, font chữ và kích thước.
 */
public final class UIConstants {

    // Ngăn chặn khởi tạo đối tượng utility class này
    private UIConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // === COLORS ===
    /** Chứa các hằng số màu sắc được sử dụng trong các màn hình UI. */
    public static class Colors {
        // Màu nền (Gradient giả định)
        public static final Color BG_MENU_TOP = Color.rgb(10, 10, 30);
        public static final Color BG_MENU_BOTTOM = Color.rgb(30, 10, 50);

        public static final Color BG_PAUSE_TOP = Color.rgb(0, 0, 20);
        public static final Color BG_PAUSE_BOTTOM = Color.rgb(20, 0, 40);

        public static final Color BG_GAMEOVER_TOP = Color.rgb(20, 0, 0);
        public static final Color BG_GAMEOVER_BOTTOM = Color.rgb(50, 10, 10);

        public static final Color BG_WIN_TOP = Color.rgb(10, 20, 40);
        public static final Color BG_WIN_BOTTOM = Color.rgb(40, 10, 60);

        // Màu nền cho các hộp thoại (Box) với độ trong suốt nhất định
        public static final Color BOX_FILL_MENU = Color.rgb(20, 20, 40, 0.95);
        public static final Color BOX_FILL_PAUSE = Color.rgb(20, 20, 40, 0.95);
        public static final Color BOX_FILL_GAMEOVER = Color.rgb(30, 10, 10, 0.95);
        public static final Color BOX_FILL_WIN = Color.rgb(20, 40, 60, 0.95);

        // Màu viền (Border)
        public static final Color BORDER_MENU = Color.CYAN;
        public static final Color BORDER_PAUSE = Color.CYAN;
        public static final Color BORDER_GAMEOVER = Color.RED;
        public static final Color BORDER_WIN = Color.GOLD;

        // Màu chữ
        public static final Color TEXT_TITLE = Color.WHITE;
        public static final Color TEXT_NORMAL = Color.WHITE;
        public static final Color TEXT_HIGHLIGHT = Color.YELLOW; // Màu chữ nổi bật (ví dụ: điểm số)
        public static final Color TEXT_INSTRUCTION = Color.LIGHTGRAY;
        public static final Color TEXT_SUCCESS = Color.GOLD;
        public static final Color TEXT_ERROR = Color.RED;

        // Màu sắc cho Button
        public static final Color BUTTON_NORMAL = Color.rgb(100, 100, 100);
        public static final Color BUTTON_HOVER = Color.rgb(150, 150, 200);
        public static final Color BUTTON_SELECTED = Color.rgb(200, 200, 255);
        public static final Color BUTTON_BORDER = Color.WHITE;
        public static final Color BUTTON_TEXT = Color.WHITE;

        // Màu lớp phủ tối (Overlay)
        public static final Color OVERLAY_DARK = Color.rgb(0, 0, 0, 0.7);
    }

    // === FONTS ===
    /** Chứa các đối tượng Font với kích thước khác nhau. */
    public static class Fonts {
        private static final String FONT_FAMILY = "Courier New";

        // Font cho tiêu đề
        public static final Font TITLE_LARGE = Font.font(FONT_FAMILY, 40);
        public static final Font TITLE_MEDIUM = Font.font(FONT_FAMILY, 32);
        public static final Font TITLE_SMALL = Font.font(FONT_FAMILY, 24);

        // Font cho nội dung chính
        public static final Font CONTENT_LARGE = Font.font(FONT_FAMILY, 22);
        public static final Font CONTENT_MEDIUM = Font.font(FONT_FAMILY, 20);
        public static final Font CONTENT_SMALL = Font.font(FONT_FAMILY, 18);

        // Font cho các thành phần UI cụ thể
        public static final Font BUTTON = Font.font(FONT_FAMILY, 20);
        public static final Font INSTRUCTION = Font.font(FONT_FAMILY, 14);
        public static final Font TABLE_HEADER = Font.font(FONT_FAMILY, 18);
        public static final Font TABLE_DATA = Font.font(FONT_FAMILY, 16);
    }

    // === SIZES ===
    /** Chứa các hằng số kích thước (pixel hoặc độ rộng/cao). */
    public static class Sizes {
        // Kích thước Button
        public static final double BUTTON_WIDTH = 200;
        public static final double BUTTON_HEIGHT = 50;
        public static final double BUTTON_SPACING = 20;
        public static final double BUTTON_BORDER_WIDTH = 2.0;

        // Kích thước Logo
        public static final double LOGO_WIDTH_LARGE = 400;
        public static final double LOGO_HEIGHT_LARGE = 145;
        public static final double LOGO_WIDTH_MEDIUM = 320;
        public static final double LOGO_HEIGHT_MEDIUM = 116;

        // Kích thước hiển thị PowerUp
        public static final double POWERUP_SIZE = 60;

        // Độ rộng viền cho các Box/Hộp thoại
        public static final double BOX_BORDER_WIDTH = 3.0;

        // Khoảng cách giữa các dòng/phần tử
        public static final double LINE_SPACING_LARGE = 40;
        public static final double LINE_SPACING_MEDIUM = 35;
        public static final double LINE_SPACING_SMALL = 25;
    }

    // === LAYOUT ===
    /** Chứa các hằng số tọa độ và vị trí layout cụ thể. */
    public static class Layout {
        // Vị trí cho bảng High Score
        public static final double HIGHSCORE_TABLE_START_Y = 250;
        public static final double HIGHSCORE_ROW_HEIGHT = 40;
        public static final double HIGHSCORE_COL_RANK_X = 100;
        public static final double HIGHSCORE_COL_NAME_X = 200;
        public static final double HIGHSCORE_COL_SCORE_X = 350;
        public static final double HIGHSCORE_COL_DATE_X = 480;
    }

    // === ANIMATION ===
    /** Chứa các hằng số liên quan đến thời gian và tốc độ animation UI. */
    public static class Animation {
        public static final long POWERUP_FRAME_DURATION = 100; // Thời gian hiển thị mỗi frame (ms)
        public static final int POWERUP_TOTAL_FRAMES = 8; // Tổng số frame của PowerUp animation
        public static final double STAR_ROTATION_SPEED = 2.0; // Tốc độ quay của các ngôi sao (độ/frame)
    }
}