package Utils;

public final class Constants {

    public static class General {
        public static final double EPSILON = 1e-10; // Hằng số nhỏ để so sánh số thực
    }

    public static class Window {
        public static final int WINDOW_WIDTH = 600; // Chiều ngang cửa sổ game (pixel)
        public static final int WINDOW_HEIGHT = 800; // Chiều dọc cửa sổ game (pixel)
        public static final int WINDOW_TOP_OFFSET = 150; // Khoảng cách từ mép trên cửa sổ đến khu vực chơi (pixel)
        public static final int WINDOW_SIDE_OFFSET = 22; // Khoảng cách từ mép bên cửa sổ đến khu vực chơi (pixel)
        public static final String WINDOW_TITLE = "Arkanoid"; // Tiêu đề cửa sổ game
        public static final int FPS = 60; // Số khung hình trên một giây
    }

    public static class PlayArea {
        public static final int PLAY_AREA_X = Borders.BORDER_SIDE_WIDTH; // Vị trí X bắt đầu khu vực chơi (pixel)
        public static final int PLAY_AREA_Y = Window.WINDOW_TOP_OFFSET + Borders.BORDER_TOP_HEIGHT; // Vị trí Y bắt đầu khu vực chơi (pixel)
        public static final int PLAY_AREA_WIDTH = Window.WINDOW_WIDTH - (Borders.BORDER_SIDE_WIDTH * 2); // Chiều ngang khu vực chơi (pixel)
        public static final int PLAY_AREA_HEIGHT = Window.WINDOW_HEIGHT - Window.WINDOW_TOP_OFFSET - Borders.BORDER_TOP_HEIGHT; // Chiều cao khu vực chơi (pixel)
    }

    public static class GameRules {
        public static final int INITIAL_LIVES = 3; // Số mạng ban đầu
        public static final int MAX_LIVES = 5; // Số mạng tối đa
        public static final int LIFE_LOST_PENALTY = 500; // Điểm trừ khi mất mạng
        public static final double POWERUP_SPAWN_CHANCE = 0.3; // Tỉ lệ rơi ra vật phẩm khi phá gạch
    }

    public static class Ball {
        public static final double BALL_SIZE = 10.0; // 10x10px sprite
        public static final double BALL_RADIUS = BALL_SIZE / 2.0; // 5.0px radius
        public static final double BALL_INITIAL_SPEED = 3.0; // Vận tốc ban đầu của bóng (pixel/frame)
        public static final double BALL_MIN_SPEED = 1.5; // Vận tốc tối thiểu của bóng (pixel/frame)
        public static final double BALL_MAX_SPEED = 6.0; // Vận tốc tối đa của bóng (pixel/frame)
        public static final double BALL_SPEED_INCREMENT = 0.1; // Tăng vận tốc mỗi khi bóng chạm gạch (pixel/frame)
    }

    public static class Paddle {
        public static final double PADDLE_WIDTH = 79.0; // 79x20px sprite (normal)
        public static final double PADDLE_HEIGHT = 20.0;
        public static final double PADDLE_WIDE_WIDTH = 119.0; // 119x20px sprite (expanded)
        public static final double PADDLE_LIFE_WIDTH = 43.0; // 43x17px sprite (lives display)
        public static final double PADDLE_LIFE_HEIGHT = 17.0;
        public static final double PADDLE_SPEED = 6.0; // Vận tốc di chuyển của thanh đỡ (pixel/frame)
        public static final double PADDLE_MAX_ANGLE = 60.0; // Góc đỡ tối đa của thanh đỡ (độ)
    }

    public static class Bricks {
        public static final double BRICK_WIDTH = 32.0; // 32x21px sprite (all brick types)
        public static final double BRICK_HEIGHT = 21.0;
        public static final double BRICK_H_SPACING = 0.0; // Khoảng cách ngang giữa các gạch (pixel)
        public static final double BRICK_V_SPACING = 0.0; // Khoảng cách dọc giữa các gạch (pixel)
        public static final double BRICK_START_Y = 100.0; // Vị trí Y bắt đầu của hàng gạch đầu tiên (pixel)
    }

    public static class PowerUps {
        public static final double POWERUP_WIDTH = 38.0; // Chiều rộng vật phẩm (pixel)
        public static final double POWERUP_HEIGHT = 19.0; // Chiều cao vật
        public static final double POWERUP_FALL_SPEED = 2.0; // Vận tốc rơi của vật phẩm (pixel/frame)
        public static final double EXPAND_MULTIPLIER = 1.5; // Hệ số mở rộng thanh đỡ
        public static final long EXPAND_DURATION = 10_000L; // Thời gian hiệu lực vật phẩm EXPAND (ms)
        public static final long CATCH_DURATION = 8_000L; // Thời gian hiệu lực vật phẩm CATCH (ms)
        public static final long LASER_DURATION = 10_000L; // Thời gian hiệu lực vật phẩm LASER (ms)
        public static final double SLOW_MULTIPLIER = 0.7; // Hệ số làm chậm tốc độ bóng của vật phẩm SLOW
        public static final long SLOW_DURATION = 8_000L; // Thời gian hiệu lực vật phẩm SLOW (ms)
        public static final long WARNING_THRESHOLD = 2_000L; // Thời gian trước khi hết hạn để hiển thị cảnh báo (ms)
    }

    public static class Laser {
        public static final double LASER_WIDTH = 4.0; // Chiều rộng tia laser (pixel)
        public static final double LASER_HEIGHT = 16.0; // Chiều cao tia laser (pixel)
        public static final double LASER_SPEED = 8.0; // Vận tốc tia laser (pixel/frame)
        public static final int LASER_SHOTS = 5; // Số lần bắn tối đa của thanh đỡ khi có vật phẩm LASER
        public static final long LASER_COOLDOWN = 300L; // Thời gian hồi chiêu giữa các lần bắn (ms)
    }

    public static class Scoring {
        public static final int SCORE_BRICK_BASE = 50; // Điểm cơ bản khi phá gạch
        public static final int SCORE_BRICK_INCREMENT = 10; // Tăng điểm mỗi khi phá thêm gạch liên tiếp
        public static final int SCORE_LEVEL_COMPLETE_BONUS = 1000; // Điểm thưởng khi qua màn 1 chơi
        public static final int SCORE_LIFE_BONUS = 500; // Điểm thưởng khi còn mạng
        public static final int SCORE_LOSE_LIFE_PENALTY = -500; // Điểm trừ khi mất mạng
    }

    public static class Animation {
        public static final long ANIMATION_FRAME_DURATION = 100L; // Thời gian mỗi khung hình trong hoạt ảnh (ms)
        public static final long CRACK_ANIMATION_DURATION = 20L; // Thời gian hoạt ảnh nứt gạch (ms)
        public static final long PADDLE_ANIMATION_DURATION = 80L; // Thời gian hoạt ảnh thanh đỡ (ms)
        public static final long POWERUP_ANIMATION_DURATION = 100L; // Thời gian hoạt ảnh vật phẩm (ms)
    }

    public static class Audio {
        public static final double DEFAULT_MUSIC_VOLUME = 0.7; // Âm lượng nhạc nền
        public static final double DEFAULT_SFX_VOLUME = 1.0; // Âm lượng hiệu ứng âm thanh
        public static final int MAX_SIMULTANEOUS_SOUNDS = 8; // Số hiệu ứng âm thanh tối đa có thể phát cùng lúc
    }

    public static class Paths {
        public static final String RESOURCES_PATH = "/Resources/";
        public static final String GRAPHICS_PATH = RESOURCES_PATH + "Graphics/";
        public static final String AUDIO_PATH = RESOURCES_PATH + "Audio/";
        public static final String FONTS_PATH = RESOURCES_PATH + "Fonts/";
        public static final String HIGHSCORE_FILE = "highscore.dat";
    }

    public static class Borders {
        public static final int BORDER_TOP_WIDTH = 556; // edge_top: 556x22px sprite
        public static final int BORDER_TOP_HEIGHT = 22;
        public static final int BORDER_SIDE_WIDTH = 22; // edge_left/right: 22x650px sprite
        public static final int BORDER_SIDE_HEIGHT = 650;
    }

    public static class UISprites {
        public static final double LOGO_WIDTH = 400.0; // logo: 400x145px sprite
        public static final double LOGO_HEIGHT = 145.0;
        public static final double LASER_BULLET_WIDTH = 6.0; // laser_bullet: 6x15px sprite
        public static final double LASER_BULLET_HEIGHT = 15.0;
    }

    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
