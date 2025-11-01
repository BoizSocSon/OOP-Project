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
 * Lớp trừu tượng PowerUp đại diện cho tất cả các vật phẩm Power-up có thể rơi và được nhặt trong game.
 * Lớp này quản lý các thuộc tính cơ bản như vị trí, kích thước, vận tốc, animation,
 * và logic va chạm với Paddle.
 */
public abstract class PowerUp implements GameObject {
    // Vị trí và kích thước
    private double x;
    private double y;
    private final double width;
    private final double height;

    // Loại Power-up (ví dụ: LASER, EXPAND, CATCH)
    private final PowerUpType type;
    // Vận tốc rơi (luôn hướng xuống)
    private final Velocity velocity;
    // Animation hiển thị của Power-up
    private final Animation animation;

    // Trạng thái: đã được người chơi nhặt chưa
    private boolean collected;
    // Trạng thái: Power-up có còn hoạt động/hiển thị trong game không (chưa nhặt hoặc chưa ra khỏi màn hình)
    private boolean active;

    /**
     * Khởi tạo một đối tượng PowerUp.
     * Thiết lập vị trí, kích thước, loại, vận tốc rơi, và bắt đầu animation.
     *
     * @param x Tọa độ x ban đầu.
     * @param y Tọa độ y ban đầu.
     * @param type Loại PowerUp (để xác định sprite/animation).
     */
    public PowerUp(double x, double y, PowerUpType type) {
        this.type = type;
        this.x = x;
        this.y = y;
        // Lấy kích thước cố định từ Constants
        this.width = Constants.PowerUps.POWERUP_WIDTH;
        this.height = Constants.PowerUps.POWERUP_HEIGHT;

        this.collected = false;
        this.active = true; // Ban đầu Power-up luôn hoạt động

        // Vận tốc rơi thẳng đứng xuống dưới
        this.velocity = new Velocity(0, Constants.PowerUps.POWERUP_FALL_SPEED);
        // Tạo animation dựa trên loại PowerUp
        this.animation = AnimationFactory.createPowerUpAnimation(type);
        this.animation.play(); // Bắt đầu chơi animation
    }

    /**
     * Cập nhật trạng thái của Power-up trong mỗi frame.
     * Cập nhật vị trí dựa trên vận tốc và cập nhật animation.
     */
    public void update() {
        // Cập nhật vị trí
        Point currentPos = new Point(x, y);
        Point newPos = velocity.applyToPoint(currentPos);
        this.x = newPos.getX();
        this.y = newPos.getY();

        // Cập nhật animation
        if (animation != null) {
            animation.update();
        }
    }

    /**
     * Lấy animation hiện tại của Power-up.
     *
     * @return Đối tượng Animation.
     */
    public Animation getAnimation() {
        return animation;
    }

    /**
     * Kiểm tra va chạm giữa Power-up và thanh đỡ (Paddle).
     *
     * @param paddle Thanh đỡ của người chơi.
     * @return true nếu Power-up va chạm với Paddle, ngược lại là false.
     */
    public boolean checkPaddleCollision(Paddle paddle) {
        // Chỉ kiểm tra va chạm nếu paddle hợp lệ và Power-up còn hoạt động
        if (paddle == null || !active) {
            return false;
        }

        // Sử dụng phương thức intersects() của Rectangle để kiểm tra va chạm hình học
        return getBounds().intersects(paddle.getBounds());
    }

    // --- Getters ---
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public PowerUpType getType() { return type; }
    public boolean isActive() { return active; }
    public boolean isCollected() { return collected; }

    /**
     * Đánh dấu Power-up là đã được nhặt (collected).
     * Đặt trạng thái collected thành true và active thành false (ngừng cập nhật/vẽ).
     */
    public void collect() {
        this.collected = true;
        this.active = false; // Ngay khi nhặt, Power-up biến mất khỏi màn hình
    }

    /**
     * Phương thức trừu tượng để áp dụng hiệu ứng cụ thể của Power-up.
     * Phải được triển khai bởi các lớp con.
     *
     * @param gameManager Đối tượng GameManager để thay đổi trạng thái game.
     */
    public abstract void applyEffect(GameManager gameManager);

    /**
     * Phương thức trừu tượng để loại bỏ hiệu ứng cụ thể của Power-up (nếu có thời gian hết hạn).
     * Phải được triển khai bởi các lớp con.
     *
     * @param gameManager Đối tượng GameManager.
     */
    public abstract void removeEffect(GameManager gameManager);

    // --- Triển khai interface GameObject ---

    /**
     * Trả về hộp giới hạn (Bounding Box) của Power-up dưới dạng Rectangle.
     *
     * @return Đối tượng Rectangle đại diện cho vị trí và kích thước.
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point(x, y), width, height);
    }

    /**
     * Kiểm tra xem Power-up có còn "sống" (cần được cập nhật/vẽ) hay không.
     *
     * @return true nếu active là true, ngược lại là false.
     */
    @Override
    public boolean isAlive() {
        return active;
    }

    /**
     * Vô hiệu hóa Power-up (thường được gọi khi nó rơi ra khỏi màn hình).
     */
    @Override
    public void destroy() {
        active = false;
    }
}