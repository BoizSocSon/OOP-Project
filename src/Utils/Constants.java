package Utils;

import javafx.scene.paint.Color;

/**
 * Lớp chứa tất cả các hằng số dùng chung trong trò chơi.
 * <p>
 * Các hằng số này bao gồm thông số cửa sổ, quy tắc trò chơi, vật lý,
 * hoạt ảnh, âm thanh, và đường dẫn tài nguyên.
 * </p>
 */
public final class Constants {

    // ==================== 0. GENERAL PARAMETERS ====================

    /**
     * Các hằng số chung cho toàn bộ chương trình
     */
    public static class General {
        /** Hằng số nhỏ dùng để so sánh số thực, tránh lỗi sai số */
        public static final double EPSILON = 1e-10;
    }

    // ==================== 1. WINDOW SETTINGS ====================

    /**
     * Các thông số cấu hình cửa sổ game
     */
    public static class Window {
        public static final int WINDOW_WIDTH = 600; // Chiều ngang cửa sổ game (pixel)
        public static final int WINDOW_HEIGHT = 800; // Chiều dọc cửa sổ game (pixel)
        public static final int WINDOW_TOP_OFFSET = 150; // Khoảng cách từ mép trên cửa sổ đến khu vực chơi
        public static final int WINDOW_SIDE_OFFSET = 22; // Khoảng cách từ mép bên đến khu vực chơi
        public static final String WINDOW_TITLE = "Arkanoid"; // Tiêu đề cửa sổ game
        public static final int FPS = 60; // Số khung hình/giây
    }

    // ==================== 1.1 PLAY AREA ====================

    /**
     * Xác định khu vực chơi chính của game
     */
    public static class PlayArea {
        public static final int PLAY_AREA_X = Borders.BORDER_SIDE_WIDTH; // Tọa độ X bắt đầu khu vực chơi

        // Giải thích: Y = offset UI trên + chiều cao viền trên (top border)
        // Vì gốc tọa độ nằm ở góc trên bên trái, nếu tính sai sẽ khiến khu vực chơi bị lệch
        public static final int PLAY_AREA_Y =
                Window.WINDOW_TOP_OFFSET + Borders.BORDER_TOP_HEIGHT;

        public static final int PLAY_AREA_WIDTH =
                Window.WINDOW_WIDTH - (Borders.BORDER_SIDE_WIDTH * 2); // Chiều rộng khu vực chơi

        public static final int PLAY_AREA_HEIGHT =
                Window.WINDOW_HEIGHT - Window.WINDOW_TOP_OFFSET - Borders.BORDER_TOP_HEIGHT; // Chiều cao khu vực chơi
    }

    // ==================== 2. GAME RULES ====================

    /**
     * Quy tắc và giới hạn cơ bản của trò chơi
     */
    public static class GameRules {
        public static final int INITIAL_LIVES = 3; // Số mạng ban đầu
        public static final int MAX_LIVES = 5; // Số mạng tối đa
        public static final int LIFE_LOST_PENALTY = 500; // Điểm bị trừ khi mất mạng
        public static final double POWERUP_SPAWN_CHANCE = 0.3; // Xác suất rơi vật phẩm khi phá gạch
    }

    // ==================== 3. PHYSICS & SPRITES ====================

    /**
     * Thông số vật lý và kích thước sprite của bóng
     */
    public static class Ball {
        public static final double BALL_SIZE = 10.0; // Kích thước sprite bóng (10x10px)
        public static final double BALL_RADIUS = BALL_SIZE / 2.0; // Bán kính bóng
        public static final double BALL_INITIAL_SPEED = 3.0; // Vận tốc ban đầu
        public static final double BALL_MIN_SPEED = 1.5; // Vận tốc tối thiểu
        public static final double BALL_MAX_SPEED = 6.0; // Vận tốc tối đa
        public static final double BALL_SPEED_INCREMENT = 0.1; // Mức tăng tốc mỗi lần bóng chạm gạch
    }

    /**
     * Thông số của thanh đỡ (Paddle)
     */
    public static class Paddle {
        public static final double PADDLE_WIDTH = 79.0; // Kích thước mặc định
        public static final double PADDLE_HEIGHT = 20.0;
        public static final double PADDLE_WIDE_WIDTH = 119.0; // Khi mở rộng (vật phẩm EXPAND)
        public static final double PADDLE_LIFE_WIDTH = 43.0; // Kích thước hiển thị số mạng
        public static final double PADDLE_LIFE_HEIGHT = 17.0;
        public static final double PADDLE_SPEED = 6.0; // Vận tốc di chuyển
        public static final double PADDLE_MAX_ANGLE = 60.0; // Góc phản xạ tối đa (độ)
    }

    // ==================== 4. BRICKS ====================

    /**
     * Cấu hình gạch (Brick)
     */
    public static class Bricks {
        public static final double BRICK_WIDTH = 32.0; // Chiều rộng gạch
        public static final double BRICK_HEIGHT = 21.0; // Chiều cao gạch
        public static final double BRICK_H_SPACING = 0.0; // Khoảng cách ngang giữa gạch
        public static final double BRICK_V_SPACING = 0.0; // Khoảng cách dọc
        public static final double BRICK_START_Y = 100.0; // Vị trí hàng đầu tiên của gạch
    }

    // ==================== 5. POWERUPS ====================

    /**
     * Thông số vật phẩm tăng sức mạnh (Power-ups)
     */
    public static class PowerUps {
        public static final double POWERUP_WIDTH = 38.0;
        public static final double POWERUP_HEIGHT = 19.0;
        public static final double POWERUP_FALL_SPEED = 2.0; // Tốc độ rơi vật phẩm
        public static final double EXPAND_MULTIPLIER = 1.5; // Hệ số mở rộng thanh đỡ
        public static final long EXPAND_DURATION = 10_000L; // Thời gian hiệu lực vật phẩm EXPAND
        public static final long CATCH_DURATION = 8_000L;
        public static final long LASER_DURATION = 10_000L;
        public static final double SLOW_MULTIPLIER = 0.7; // Làm chậm bóng
        public static final long SLOW_DURATION = 8_000L;
        public static final long WARNING_THRESHOLD = 2_000L; // Hiện cảnh báo khi sắp hết hiệu lực
    }

    // ==================== 6. LASER ====================

    /**
     * Thông số của tia laser từ vật phẩm LASER
     */
    public static class Laser {
        public static final double LASER_WIDTH = 4.0;
        public static final double LASER_HEIGHT = 16.0;
        public static final double LASER_SPEED = 8.0; // Tốc độ bay của tia laser
        public static final int LASER_SHOTS = 5; // Số lần bắn tối đa
        public static final long LASER_COOLDOWN = 300L; // Thời gian hồi giữa các lần bắn
    }

    // ==================== 7. SCORING ====================

    /**
     * Cấu hình tính điểm trong trò chơi
     */
    public static class Scoring {
        public static final int SCORE_BRICK_BASE = 50;
        public static final int SCORE_BRICK_INCREMENT = 10;
        public static final int SCORE_LEVEL_COMPLETE_BONUS = 1000;
        public static final int SCORE_LIFE_BONUS = 500;
        public static final int SCORE_LOSE_LIFE_PENALTY = -500;
    }

    // ==================== 8. ANIMATION ====================

    /**
     * Thông số điều khiển hoạt ảnh (animation)
     */
    public static class Animation {
        public static final long ANIMATION_FRAME_DURATION = 100L;
        public static final long CRACK_ANIMATION_DURATION = 20L;
        public static final long PADDLE_ANIMATION_DURATION = 80L;
        public static final long POWERUP_ANIMATION_DURATION = 100L;
    }

    // ==================== 9. AUDIO ====================

    /**
     * Cấu hình âm thanh và nhạc nền
     */
    public static class Audio {
        public static final double DEFAULT_MUSIC_VOLUME = 0.7; // Âm lượng nhạc nền mặc định
        public static final double DEFAULT_SFX_VOLUME = 1.0; // Âm lượng hiệu ứng
        public static final int MAX_SIMULTANEOUS_SOUNDS = 8; // Giới hạn số hiệu ứng phát cùng lúc
    }

    // ==================== 10. PATHS ====================

    /**
     * Đường dẫn đến các tài nguyên trong thư mục Resources
     */
    public static class Paths {
        public static final String RESOURCES_PATH = "/Resources/";
        public static final String GRAPHICS_PATH = RESOURCES_PATH + "Graphics/";
        public static final String AUDIO_PATH = RESOURCES_PATH + "Audio/";
        public static final String FONTS_PATH = RESOURCES_PATH + "Fonts/";
        public static final String HIGHSCORE_FILE = "highscore.dat";
    }

    // ==================== 11. BORDERS ====================

    /**
     * Kích thước viền của khu vực chơi (Sprites)
     */
    public static class Borders {
        public static final int BORDER_TOP_WIDTH = 556;
        public static final int BORDER_TOP_HEIGHT = 22;
        public static final int BORDER_SIDE_WIDTH = 22;
        public static final int BORDER_SIDE_HEIGHT = 650;
    }

    // ==================== 12. UI SPRITES ====================

    /**
     * Thông số giao diện người dùng (logo, biểu tượng, đạn laser)
     */
    public static class UISprites {
        public static final double LOGO_WIDTH = 400.0;
        public static final double LOGO_HEIGHT = 145.0;
        public static final double LASER_BULLET_WIDTH = 6.0;
        public static final double LASER_BULLET_HEIGHT = 15.0;
    }

    // ==================== 13. COLORS ====================

    /**
     * Màu sắc mặc định nếu không tải được hình ảnh
     */
    public static class Colors {
        public static final Color COLOR_BACKGROUND = Color.rgb(20, 20, 40);
        public static final Color COLOR_TEXT_TITLE = Color.GOLD;
        public static final Color COLOR_TEXT_BODY = Color.WHITE;
        public static final Color COLOR_TEXT_SUBTITLE = Color.LIGHTGRAY;
    }

    /**
     * Hàm dựng bị cấm gọi — lớp này chỉ chứa hằng số, không nên khởi tạo.
     */
    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
