package Utils;

/**
 * Lớp chứa tất cả các hằng số (constants) được sử dụng trong game Arkanoid.
 * Các hằng số được phân loại và nhóm thành các lớp tĩnh lồng nhau để dễ quản lý.
 * Lớp này không thể được khởi tạo.
 */
public final class Constants {

    /**
     * Chứa các hằng số chung, cơ bản.
     */
    public static class General {
        public static final double EPSILON = 1e-10; // Hằng số nhỏ để so sánh số thực, dùng để xử lý lỗi dấu chấm động (floating-point error)
    }

    /**
     * Chứa các hằng số liên quan đến cửa sổ ứng dụng (JavaFX Stage/Scene).
     */
    public static class Window {
        public static final int WINDOW_WIDTH = 600; // Chiều ngang cửa sổ game (pixel)
        public static final int WINDOW_HEIGHT = 800; // Chiều dọc cửa sổ game (pixel)
        public static final int WINDOW_TOP_OFFSET = 150; // Khoảng cách từ mép trên cửa sổ đến khu vực hiển thị game/score (pixel)
        public static final int WINDOW_SIDE_OFFSET = 22; // Khoảng cách từ mép bên cửa sổ đến khu vực chơi (pixel)
        public static final String WINDOW_TITLE = "Arkanoid"; // Tiêu đề cửa sổ game
        public static final int FPS = 60; // Số khung hình trên một giây (Frames Per Second)
    }

    /**
     * Chứa các hằng số xác định khu vực chơi game thực tế, không tính khung viền và UI trên cùng.
     */
    public static class PlayArea {
        // Vị trí X bắt đầu khu vực chơi, bằng chiều rộng viền bên.
        public static final int PLAY_AREA_X = Borders.BORDER_SIDE_WIDTH;
        // Vị trí Y bắt đầu khu vực chơi, bằng độ lệch trên cùng + chiều cao viền trên.
        public static final int PLAY_AREA_Y = Window.WINDOW_TOP_OFFSET + Borders.BORDER_TOP_HEIGHT;
        // Chiều ngang khu vực chơi, bằng Chiều ngang cửa sổ - (chiều rộng viền bên * 2).
        public static final int PLAY_AREA_WIDTH = Window.WINDOW_WIDTH - (Borders.BORDER_SIDE_WIDTH * 2);
        // Chiều cao khu vực chơi, bằng Chiều cao cửa sổ - độ lệch trên cùng - chiều cao viền trên.
        public static final int PLAY_AREA_HEIGHT = Window.WINDOW_HEIGHT - Window.WINDOW_TOP_OFFSET - Borders.BORDER_TOP_HEIGHT;
    }

    /**
     * Chứa các quy tắc và thông số cơ bản của trò chơi.
     */
    public static class GameRules {
        public static final int INITIAL_LIVES = 3; // Số mạng ban đầu khi bắt đầu game
        public static final int MAX_LIVES = 5; // Số mạng tối đa người chơi có thể giữ
        public static final int LIFE_LOST_PENALTY = 500; // Điểm trừ khi mất mạng
        public static final double POWERUP_SPAWN_CHANCE = 0.3; // Tỉ lệ (0.0 đến 1.0) rơi ra vật phẩm khi phá gạch
    }

    /**
     * Chứa các hằng số liên quan đến đối tượng bóng (Ball).
     */
    public static class Ball {
        public static final double BALL_SIZE = 10.0; // Kích thước sprite bóng: 10x10px
        public static final double BALL_RADIUS = BALL_SIZE / 2.0; // Bán kính bóng: 5.0px (sử dụng cho tính toán va chạm)
        public static final double BALL_INITIAL_SPEED = 3.0; // Vận tốc ban đầu của bóng (pixel/frame)
        public static final double BALL_MIN_SPEED = 1.5; // Vận tốc tối thiểu của bóng (pixel/frame)
        public static final double BALL_MAX_SPEED = 6.0; // Vận tốc tối đa của bóng (pixel/frame)
        public static final double BALL_SPEED_INCREMENT = 0.1; // Tăng vận tốc mỗi khi bóng chạm gạch (pixel/frame)
    }

    /**
     * Chứa các hằng số liên quan đến đối tượng thanh đỡ (Paddle).
     */
    public static class Paddle {
        public static final double PADDLE_WIDTH = 79.0; // Chiều rộng sprite thanh đỡ (trạng thái bình thường)
        public static final double PADDLE_HEIGHT = 20.0; // Chiều cao sprite thanh đỡ
        public static final double PADDLE_WIDE_WIDTH = 119.0; // Chiều rộng sprite thanh đỡ (trạng thái mở rộng)
        public static final double PADDLE_LIFE_WIDTH = 43.0; // Chiều rộng sprite hiển thị mạng (lives display)
        public static final double PADDLE_LIFE_HEIGHT = 17.0; // Chiều cao sprite hiển thị mạng
        public static final double PADDLE_SPEED = 6.0; // Vận tốc di chuyển của thanh đỡ (pixel/frame)
        public static final double PADDLE_MAX_ANGLE = 60.0; // Góc đỡ tối đa của thanh đỡ (độ), xác định góc phản xạ của bóng
    }

    /**
     * Chứa các hằng số liên quan đến gạch (Bricks).
     */
    public static class Bricks {
        public static final double BRICK_WIDTH = 32.0; // Chiều rộng sprite gạch (tất cả các loại)
        public static final double BRICK_HEIGHT = 21.0; // Chiều cao sprite gạch
        public static final double BRICK_H_SPACING = 0.0; // Khoảng cách ngang giữa các gạch (pixel)
        public static final double BRICK_V_SPACING = 0.0; // Khoảng cách dọc giữa các gạch (pixel)
        public static final double BRICK_START_Y = 100.0; // Vị trí Y bắt đầu của hàng gạch đầu tiên (pixel)
    }

    /**
     * Chứa các hằng số liên quan đến vật phẩm bổ trợ (PowerUps).
     */
    public static class PowerUps {
        public static final double POWERUP_WIDTH = 38.0; // Chiều rộng vật phẩm (pixel)
        public static final double POWERUP_HEIGHT = 19.0; // Chiều cao vật phẩm (pixel)
        public static final double POWERUP_FALL_SPEED = 2.0; // Vận tốc rơi của vật phẩm (pixel/frame)
        public static final double EXPAND_MULTIPLIER = 1.5; // Hệ số mở rộng thanh đỡ
        public static final long EXPAND_DURATION = 10_000L; // Thời gian hiệu lực vật phẩm EXPAND (ms - 10 giây)
        public static final long CATCH_DURATION = 8_000L; // Thời gian hiệu lực vật phẩm CATCH (ms - 8 giây)
        public static final long LASER_DURATION = 10_000L; // Thời gian hiệu lực vật phẩm LASER (ms - 10 giây)
        public static final double SLOW_MULTIPLIER = 0.7; // Hệ số làm chậm tốc độ bóng của vật phẩm SLOW (70% tốc độ ban đầu)
        public static final long SLOW_DURATION = 8_000L; // Thời gian hiệu lực vật phẩm SLOW (ms - 8 giây)
        public static final long WARNING_THRESHOLD = 2_000L; // Thời gian trước khi hết hạn để hiển thị cảnh báo (ms)
    }

    /**
     * Chứa các hằng số liên quan đến tia laser do thanh đỡ bắn ra.
     */
    public static class Laser {
        public static final double LASER_WIDTH = 4.0; // Chiều rộng tia laser (pixel)
        public static final double LASER_HEIGHT = 16.0; // Chiều cao tia laser (pixel)
        public static final double LASER_SPEED = 8.0; // Vận tốc tia laser (pixel/frame)
        public static final int LASER_SHOTS = 5; // Số lần bắn tối đa của thanh đỡ khi có vật phẩm LASER
        public static final long LASER_COOLDOWN = 300L; // Thời gian hồi chiêu giữa các lần bắn (ms)
    }

    /**
     * Chứa các hằng số liên quan đến hệ thống tính điểm.
     */
    public static class Scoring {
        public static final int SCORE_BRICK_BASE = 50; // Điểm cơ bản khi phá gạch
        public static final int SCORE_BRICK_INCREMENT = 10; // Tăng điểm mỗi khi phá thêm gạch liên tiếp
        public static final int SCORE_LEVEL_COMPLETE_BONUS = 1000; // Điểm thưởng khi qua màn chơi
        public static final int SCORE_LIFE_BONUS = 500; // Điểm thưởng cho mỗi mạng còn lại khi qua màn
        public static final int SCORE_LOSE_LIFE_PENALTY = -500; // Điểm trừ khi mất mạng
    }

    /**
     * Chứa các hằng số liên quan đến thời gian và tốc độ của các hoạt ảnh (Animations).
     */
    public static class Animation {
        public static final long ANIMATION_FRAME_DURATION = 100L; // Thời gian hiển thị mỗi khung hình trong hoạt ảnh (ms)
        public static final long CRACK_ANIMATION_DURATION = 20L; // Thời gian hoạt ảnh nứt gạch (ms)
        public static final long PADDLE_ANIMATION_DURATION = 80L; // Thời gian hoạt ảnh thanh đỡ (ms)
        public static final long POWERUP_ANIMATION_DURATION = 100L; // Thời gian hoạt ảnh vật phẩm (ms)
    }

    /**
     * Chứa các hằng số liên quan đến âm thanh và nhạc nền.
     */
    public static class Audio {
        public static final double DEFAULT_MUSIC_VOLUME = 0.7; // Âm lượng nhạc nền mặc định (từ 0.0 đến 1.0)
        public static final double DEFAULT_SFX_VOLUME = 1.0; // Âm lượng hiệu ứng âm thanh mặc định (từ 0.0 đến 1.0)
        public static final int MAX_SIMULTANEOUS_SOUNDS = 8; // Số hiệu ứng âm thanh tối đa có thể phát cùng lúc để tránh quá tải
    }

    /**
     * Chứa các hằng số liên quan đến đường dẫn file tài nguyên.
     */
    public static class Paths {
        public static final String RESOURCES_PATH = "/Resources/";
        public static final String GRAPHICS_PATH = RESOURCES_PATH + "Graphics/";
        public static final String AUDIO_PATH = RESOURCES_PATH + "Audio/";
        public static final String FONTS_PATH = RESOURCES_PATH + "Fonts/";
        public static final String HIGHSCORE_FILE = "highscore.dat"; // Tên file lưu điểm cao
    }

    /**
     * Chứa các hằng số liên quan đến kích thước của khung viền game.
     */
    public static class Borders {
        public static final int BORDER_TOP_WIDTH = 556; // Chiều rộng sprite viền trên (edge_top)
        public static final int BORDER_TOP_HEIGHT = 22; // Chiều cao sprite viền trên
        public static final int BORDER_SIDE_WIDTH = 22; // Chiều rộng sprite viền bên (edge_left/right)
        public static final int BORDER_SIDE_HEIGHT = 650; // Chiều cao sprite viền bên
    }

    /**
     * Chứa các hằng số liên quan đến kích thước của các sprite Giao diện người dùng (UI).
     */
    public static class UISprites {
        public static final double LOGO_WIDTH = 400.0; // Chiều rộng sprite logo
        public static final double LOGO_HEIGHT = 145.0; // Chiều cao sprite logo
        public static final double LASER_BULLET_WIDTH = 6.0; // Chiều rộng sprite đạn laser
        public static final double LASER_BULLET_HEIGHT = 15.0; // Chiều cao sprite đạn laser
    }

    /**
     * Constructor private để ngăn việc tạo ra các instance của lớp tiện ích này.
     *
     * @throws UnsupportedOperationException Luôn ném ngoại lệ vì đây là lớp tiện ích.
     */
    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}