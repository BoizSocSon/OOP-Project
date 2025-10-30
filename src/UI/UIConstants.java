package UI;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Constants cho UI System.
 * Chứa colors, fonts, sizes sử dụng trong các màn hình UI.
 */
public final class UIConstants {

    // Prevent instantiation
    private UIConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // === COLORS ===
    public static class Colors {
        // Background colors
        public static final Color BG_MENU_TOP = Color.rgb(10, 10, 30);
        public static final Color BG_MENU_BOTTOM = Color.rgb(30, 10, 50);

        public static final Color BG_PAUSE_TOP = Color.rgb(0, 0, 20);
        public static final Color BG_PAUSE_BOTTOM = Color.rgb(20, 0, 40);

        public static final Color BG_GAMEOVER_TOP = Color.rgb(20, 0, 0);
        public static final Color BG_GAMEOVER_BOTTOM = Color.rgb(50, 10, 10);

        public static final Color BG_WIN_TOP = Color.rgb(10, 20, 40);
        public static final Color BG_WIN_BOTTOM = Color.rgb(40, 10, 60);

        // Box colors
        public static final Color BOX_FILL_MENU = Color.rgb(20, 20, 40, 0.95);
        public static final Color BOX_FILL_PAUSE = Color.rgb(20, 20, 40, 0.95);
        public static final Color BOX_FILL_GAMEOVER = Color.rgb(30, 10, 10, 0.95);
        public static final Color BOX_FILL_WIN = Color.rgb(20, 40, 60, 0.95);

        // Border colors
        public static final Color BORDER_MENU = Color.CYAN;
        public static final Color BORDER_PAUSE = Color.CYAN;
        public static final Color BORDER_GAMEOVER = Color.RED;
        public static final Color BORDER_WIN = Color.GOLD;

        // Text colors
        public static final Color TEXT_TITLE = Color.WHITE;
        public static final Color TEXT_NORMAL = Color.WHITE;
        public static final Color TEXT_HIGHLIGHT = Color.YELLOW;
        public static final Color TEXT_INSTRUCTION = Color.LIGHTGRAY;
        public static final Color TEXT_SUCCESS = Color.GOLD;
        public static final Color TEXT_ERROR = Color.RED;

        // Button colors
        public static final Color BUTTON_NORMAL = Color.rgb(100, 100, 100);
        public static final Color BUTTON_HOVER = Color.rgb(150, 150, 200);
        public static final Color BUTTON_SELECTED = Color.rgb(200, 200, 255);
        public static final Color BUTTON_BORDER = Color.WHITE;
        public static final Color BUTTON_TEXT = Color.WHITE;

        // Overlay
        public static final Color OVERLAY_DARK = Color.rgb(0, 0, 0, 0.7);
    }

    // === FONTS ===
    public static class Fonts {
        private static final String FONT_FAMILY = "Courier New";

        // Title fonts
        public static final Font TITLE_LARGE = Font.font(FONT_FAMILY, 40);
        public static final Font TITLE_MEDIUM = Font.font(FONT_FAMILY, 32);
        public static final Font TITLE_SMALL = Font.font(FONT_FAMILY, 24);

        // Content fonts
        public static final Font CONTENT_LARGE = Font.font(FONT_FAMILY, 22);
        public static final Font CONTENT_MEDIUM = Font.font(FONT_FAMILY, 20);
        public static final Font CONTENT_SMALL = Font.font(FONT_FAMILY, 18);

        // UI fonts
        public static final Font BUTTON = Font.font(FONT_FAMILY, 20);
        public static final Font INSTRUCTION = Font.font(FONT_FAMILY, 14);
        public static final Font TABLE_HEADER = Font.font(FONT_FAMILY, 18);
        public static final Font TABLE_DATA = Font.font(FONT_FAMILY, 16);
    }

    // === SIZES ===
    public static class Sizes {
        // Button sizes
        public static final double BUTTON_WIDTH = 200;
        public static final double BUTTON_HEIGHT = 50;
        public static final double BUTTON_SPACING = 20;
        public static final double BUTTON_BORDER_WIDTH = 2.0;

        // Logo sizes (từ Constants.UISprites)
        public static final double LOGO_WIDTH_LARGE = 400;
        public static final double LOGO_HEIGHT_LARGE = 145;
        public static final double LOGO_WIDTH_MEDIUM = 320;
        public static final double LOGO_HEIGHT_MEDIUM = 116; // Giữ tỷ lệ 400:145

        // PowerUp display sizes
        public static final double POWERUP_SIZE = 60;

        // Box sizes
        public static final double BOX_BORDER_WIDTH = 3.0;

        // Spacing
        public static final double LINE_SPACING_LARGE = 40;
        public static final double LINE_SPACING_MEDIUM = 35;
        public static final double LINE_SPACING_SMALL = 25;
    }

    // === LAYOUT ===
    public static class Layout {
        // High Score table
        public static final double HIGHSCORE_TABLE_START_Y = 250;
        public static final double HIGHSCORE_ROW_HEIGHT = 40;
        public static final double HIGHSCORE_COL_RANK_X = 100;
        public static final double HIGHSCORE_COL_NAME_X = 200;
        public static final double HIGHSCORE_COL_SCORE_X = 350;
        public static final double HIGHSCORE_COL_DATE_X = 480;
    }

    // === ANIMATION ===
    public static class Animation {
        public static final long POWERUP_FRAME_DURATION = 100; // ms
        public static final int POWERUP_TOTAL_FRAMES = 8;
        public static final double STAR_ROTATION_SPEED = 2.0; // degrees per frame
    }
}
