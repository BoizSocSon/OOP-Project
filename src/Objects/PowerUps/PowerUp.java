package Objects.PowerUps;

import Engine.GameManager;
import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;
import GeometryPrimitives.Velocity;
import Objects.Core.GameObject;
import Objects.GameEntities.Paddle;
import Render.Animation;
import Utils.AnimationFactory;
import Utils.Constants;

/**
 * <p>Lớp trừu tượng (abstract class) cơ sở cho tất cả các đối tượng **PowerUp** trong trò chơi.
 * PowerUp là các vật phẩm rơi xuống từ gạch bị phá hủy, cung cấp các hiệu ứng
 * tạm thời hoặc vĩnh viễn cho người chơi khi được thu thập.</p>
 */
public abstract class PowerUp implements GameObject {

    /** Tọa độ x của góc trên bên trái PowerUp. */
    private double x;

    /** Tọa độ y của góc trên bên trái PowerUp. */
    private double y;

    /** Chiều rộng cố định của PowerUp. */
    private final double width;

    /** Chiều cao cố định của PowerUp. */
    private final double height;

    /** Loại PowerUp cụ thể (ví dụ: CATCH, LASER, LIFE). */
    private final PowerUpType type;

    /** Vận tốc di chuyển (rơi xuống) của PowerUp. */
    private final Velocity velocity;

    /** Hoạt ảnh (animation) trực quan của PowerUp. */
    private final Animation animation;

    /** Cờ báo hiệu PowerUp đã được thanh đỡ thu thập hay chưa. */
    private boolean collected;

    /** Cờ báo hiệu PowerUp còn hoạt động trong game hay không (đã rơi khỏi màn hình hoặc đã thu thập). */
    private boolean active;

    /**
     * <p>Constructor khởi tạo một PowerUp.</p>
     *
     * @param x Tọa độ x ban đầu.
     * @param y Tọa độ y ban đầu.
     * @param type Loại PowerUp cụ thể.
     */
    public PowerUp(double x, double y, PowerUpType type) {
        this.type = type;
        this.x = x;
        this.y = y;
        // Thiết lập kích thước cố định từ Constants
        this.width = Constants.PowerUps.POWERUP_WIDTH;
        this.height = Constants.PowerUps.POWERUP_HEIGHT;

        this.collected = false;
        this.active = true;

        // Thiết lập vận tốc mặc định (rơi thẳng đứng xuống dưới)
        this.velocity = new Velocity(0, Constants.PowerUps.POWERUP_FALL_SPEED);
        // Tạo và bắt đầu hoạt ảnh
        this.animation = AnimationFactory.createPowerUpAnimation(type);
        this.animation.play();
    }

    /**
     * <p>Cập nhật vị trí (di chuyển) và trạng thái hoạt ảnh của PowerUp.</p>
     */
    public void update() {
        // Cập nhật vị trí bằng cách áp dụng vận tốc
        Point currentPos = new Point(x, y);
        Point newPos = velocity.applyToPoint(currentPos);
        this.x = newPos.getX();
        this.y = newPos.getY();

        // Cập nhật hoạt ảnh
        if (animation != null) {
            animation.update();
        }
    }

    /**
     * <p>Trả về đối tượng hoạt ảnh của PowerUp.</p>
     *
     * @return Đối tượng {@link Animation}.
     */
    public Animation getAnimation() {
        return animation;
    }

    /**
     * <p>Kiểm tra va chạm giữa PowerUp và thanh đỡ (Paddle).</p>
     *
     * @param paddle Đối tượng thanh đỡ cần kiểm tra va chạm.
     * @return {@code true} nếu có va chạm với thanh đỡ và PowerUp đang hoạt động, ngược lại {@code false}.
     */
    public boolean checkPaddleCollision(Paddle paddle) {
        if (paddle == null || !active) {
            return false;
        }

        // Kiểm tra xem hình chữ nhật bao quanh có giao nhau hay không
        return getBounds().intersects(paddle.getBounds());
    }

    // ============================================================
    // Getters and Status methods
    // ============================================================

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    /** Trả về loại PowerUp (ví dụ: CATCH, LASER). */
    public PowerUpType getType() { return type; }
    /** Kiểm tra xem PowerUp có còn hoạt động (chưa bị thu thập/hủy) hay không. */
    public boolean isActive() { return active; }
    /** Kiểm tra xem PowerUp đã được thu thập hay chưa. */
    public boolean isCollected() { return collected; }

    /**
     * <p>Thiết lập trạng thái đã được thu thập và vô hiệu hóa PowerUp.</p>
     */
    public void collect() {
        this.collected = true;
        this.active = false;
    }

    /**
     * <p>Phương thức trừu tượng: Áp dụng hiệu ứng của PowerUp lên trò chơi.</p>
     * <p>Phải được triển khai bởi các lớp PowerUp cụ thể.</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi để thay đổi trạng thái game.
     */
    public abstract void applyEffect(GameManager gameManager);

    /**
     * <p>Phương thức trừu tượng: Gỡ bỏ hiệu ứng của PowerUp khỏi trò chơi.</p>
     * <p>Phải được triển khai bởi các lớp PowerUp cụ thể (thường dùng cho hiệu ứng tạm thời).</p>
     *
     * @param gameManager Đối tượng quản lý trò chơi để hoàn tác trạng thái game.
     */
    public abstract void removeEffect(GameManager gameManager);

    /**
     * <p>Triển khai từ {@link GameObject}: Trả về hình chữ nhật giới hạn của PowerUp.</p>
     *
     * @return Đối tượng {@link Rectangle} đại diện cho giới hạn va chạm.
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point(x, y), width, height);
    }

    /**
     * <p>Triển khai từ {@link GameObject}: Kiểm tra xem PowerUp còn hoạt động (active) hay không.</p>
     *
     * @return Giá trị của {@link #active}.
     */
    @Override
    public boolean isAlive() {
        return active;
    }

    /**
     * <p>Triển khai từ {@link GameObject}: Đặt trạng thái PowerUp là bị phá hủy (hủy hoạt).</p>
     */
    @Override
    public void destroy() {
        active = false;
    }
}