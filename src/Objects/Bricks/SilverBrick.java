package Objects.Bricks;

import Render.Animation;
import Utils.AnimationFactory;
import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;

/**
 * Lớp {@code SilverBrick} đại diện cho loại gạch bạc trong trò chơi.
 *
 * <p>Gạch bạc có độ bền cao hơn gạch thường, cần bị đánh trúng nhiều lần
 * (theo giá trị {@link BrickType#SILVER#getHitPoints()}) mới bị phá hủy hoàn toàn.</p>
 *
 * <p>Khi chỉ còn 1 HP, gạch bạc sẽ hiển thị hiệu ứng nứt thông qua {@link Animation}
 * được tạo bởi {@link AnimationFactory#createBrickCrackAnimation()}.</p>
 *
 * <p>Trạng thái và hiệu ứng của gạch được cập nhật liên tục trong phương thức {@link #update()}.</p>
 */
public class SilverBrick extends Brick {

    /** Số máu hiện tại của gạch bạc */
    private int currentHP;

    /** Hiệu ứng nứt của gạch bạc khi gần bị phá hủy */
    private Animation crackAnimation;

    /**
     * Khởi tạo một đối tượng {@code SilverBrick} tại vị trí và kích thước xác định.
     *
     * @param x      tọa độ X (góc trên bên trái)
     * @param y      tọa độ Y (góc trên bên trái)
     * @param width  chiều rộng gạch
     * @param height chiều cao gạch
     */
    public SilverBrick(double x, double y, double width, double height) {
        // Gọi constructor lớp cha với thông tin từ BrickType.SILVER
        super(x, y, width, height, BrickType.SILVER.getHitPoints());
        this.currentHP = BrickType.SILVER.getHitPoints();
        // Tạo hiệu ứng nứt từ AnimationFactory
        this.crackAnimation = AnimationFactory.createBrickCrackAnimation();
    }

    /**
     * Xử lý khi gạch bị đánh trúng.
     * <p>Giảm HP mỗi khi va chạm, phát hiệu ứng nứt nếu còn 1 HP,
     * và phá hủy gạch khi HP = 0.</p>
     */
    @Override
    public void takeHit() {
        if (currentHP <= 0) {
            return; // Nếu đã vỡ thì bỏ qua
        }

        currentHP--;

        if (currentHP == 1) {
            // Khi chỉ còn 1 HP → hiển thị hiệu ứng nứt
            crackAnimation.play();
        } else if (currentHP == 0) {
            // Khi HP = 0 → phá hủy gạch
            destroy();
        }
    }

    /**
     * Cập nhật hiệu ứng nứt (nếu đang phát).
     * <p>Phương thức này được gọi mỗi frame để đảm bảo animation hoạt động mượt mà.</p>
     */
    @Override
    public void update() {
        if (crackAnimation != null && crackAnimation.isPlaying()) {
            crackAnimation.update();
        }
    }

    /**
     * Trả về vùng bao (hitbox) của gạch bạc.
     *
     * @return đối tượng {@link Rectangle} biểu diễn vùng va chạm.
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point(getX(), getY()), getWidth(), getHeight());
    }

    /**
     * Phá hủy gạch bằng cách gọi phương thức {@link Brick#destroy()} của lớp cha.
     */
    @Override
    public void destroy() {
        super.destroy();
    }

    /**
     * Kiểm tra xem gạch đã bị phá hủy hay chưa.
     *
     * @return {@code true} nếu HP ≤ 0 hoặc gạch đã bị đánh dấu là "chết".
     */
    @Override
    public boolean isDestroyed() {
        return currentHP <= 0 || !isAlive();
    }

    /**
     * Trả về loại gạch tương ứng.
     *
     * @return {@link BrickType#SILVER}
     */
    @Override
    public BrickType getBrickType() {
        return BrickType.SILVER;
    }

    /**
     * Lấy số HP hiện tại của gạch.
     *
     * @return số HP còn lại
     */
    public int getCurrentHP() {
        return currentHP;
    }

    /**
     * Kiểm tra xem hiệu ứng nứt có đang được phát hay không.
     *
     * @return {@code true} nếu hiệu ứng đang chạy
     */
    public boolean isCrackAnimationPlaying() {
        return crackAnimation != null && crackAnimation.isPlaying();
    }

    /**
     * Lấy hiệu ứng nứt hiện tại của gạch bạc.
     *
     * @return đối tượng {@link Animation} của hiệu ứng nứt
     */
    public Animation getCrackAnimation() {
        return crackAnimation;
    }

    /**
     * Gán hiệu ứng nứt mới cho gạch bạc.
     *
     * @param animation đối tượng {@link Animation} mới
     */
    public void setCrackAnimation(Animation animation) {
        this.crackAnimation = animation;
    }
}
