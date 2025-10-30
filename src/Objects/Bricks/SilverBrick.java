package Objects.Bricks;

import Render.Animation;
import Utils.AnimationFactory;
import Utils.Constants;
import Engine.PowerUpManager;

import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;

/**
 * <p>Lớp đại diện cho loại **Gạch Bạc (Silver Brick)** trong trò chơi.
 * Loại gạch này có **nhiều hơn 1 điểm máu (hit point)**,
 * yêu cầu nhiều lần va chạm để phá hủy, và sẽ hiển thị hiệu ứng nứt
 * (crack animation) khi còn 1 điểm máu.</p>
 */
public class SilverBrick extends Brick{

    /** Điểm máu hiện tại của viên gạch. */
    private int currentHP;

    /** Hoạt ảnh (animation) hiển thị vết nứt trên gạch khi nó gần bị phá hủy. */
    private Animation crackAnimation;

    /**
     * <p>Constructor khởi tạo một viên gạch Bạc.</p>
     *
     * @param x Tọa độ x của góc trên bên trái viên gạch.
     * @param y Tọa độ y của góc trên bên trái viên gạch.
     * @param width Chiều rộng của viên gạch.
     * @param height Chiều cao của viên gạch.
     */
    public SilverBrick(double x, double y, double width, double height) {
        // Khởi tạo lớp cha (Brick) với tổng hit points ban đầu từ BrickType.SILVER
        super(x, y, width, height, BrickType.SILVER.getHitPoints());

        // Thiết lập điểm máu hiện tại bằng điểm máu tối đa
        this.currentHP = BrickType.SILVER.getHitPoints();

        // Tạo và khởi tạo hiệu ứng nứt
        this.crackAnimation = AnimationFactory.createBrickCrackAnimation();
    }

    /**
     * <p>Xử lý sự kiện khi gạch bị va chạm (hit).</p>
     * <p>Phương thức này giảm {@link #currentHP}. Nếu HP giảm xuống 1, nó bắt đầu
     * hiệu ứng nứt. Nếu HP giảm xuống 0, nó gọi {@link #destroy()} để phá hủy gạch.</p>
     */
    @Override
    public void takeHit() {
        if (currentHP <= 0) {
            return;
        }

        currentHP--;

        if (currentHP == 1) {
            // Hiển thị hiệu ứng nứt khi còn 1 HP
            crackAnimation.play();
        } else if (currentHP == 0) {
            // Phá hủy gạch khi HP về 0
            destroy();
        }
    }

    /**
     * <p>Cập nhật trạng thái của gạch Bạc trong mỗi vòng lặp game.
     * Chủ yếu dùng để cập nhật trạng thái của hiệu ứng nứt (nếu nó đang chạy).</p>
     */
    @Override
    public void update() {
        // Cập nhật hiệu ứng nứt nếu cần
        if (crackAnimation != null && crackAnimation.isPlaying()) {
            crackAnimation.update();
        }
    }

    /**
     * <p>Trả về hình chữ nhật bao quanh (bounding box) của viên gạch.</p>
     *
     * @return Đối tượng Rectangle đại diện cho giới hạn va chạm của gạch.
     */
    @Override
    public Rectangle getBounds() {
        // Tạo và trả về một đối tượng Rectangle mới dựa trên vị trí và kích thước của gạch.
        return new Rectangle(new Point(getX(), getY()), getWidth(), getHeight());
    }


    /**
     * <p>Thực hiện các hành động khi viên gạch bị phá hủy (HP bằng 0).</p>
     * <p>Hiện tại, nó chỉ gọi phương thức {@code destroy()} của lớp cha.</p>
     */
    @Override
    public void destroy() {
        super.destroy();
    }

    /**
     * <p>Kiểm tra xem viên gạch đã bị phá hủy hay chưa.</p>
     *
     * @return {@code true} nếu điểm máu hiện tại nhỏ hơn hoặc bằng 0, hoặc nếu đối tượng không còn "sống" (alive), ngược lại {@code false}.
     */
    @Override
    public boolean isDestroyed() {
        return currentHP <= 0 || !isAlive();
    }

    /**
     * <p>Trả về loại gạch cụ thể (BrickType) của đối tượng này.</p>
     *
     * @return Luôn trả về {@link BrickType#SILVER}.
     */
    @Override
    public BrickType getBrickType() {
        return BrickType.SILVER;
    }

    /**
     * <p>Trả về điểm máu hiện tại (current HP) của viên gạch.</p>
     *
     * @return Số điểm máu còn lại.
     */
    public int getCurrentHP() {
        return currentHP;
    }

    /**
     * <p>Kiểm tra xem hiệu ứng nứt (crack animation) có đang chạy hay không.</p>
     *
     * @return {@code true} nếu hiệu ứng nứt đang phát, ngược lại {@code false}.
     */
    public boolean isCrackAnimationPlaying() {
        return crackAnimation != null && crackAnimation.isPlaying();
    }

    /**
     * <p>Trả về đối tượng Hoạt ảnh nứt (crack animation).</p>
     *
     * @return Đối tượng {@link Animation} của hiệu ứng nứt.
     */
    public Animation getCrackAnimation() {
        return crackAnimation;
    }

    /**
     * <p>Thiết lập đối tượng Hoạt ảnh nứt mới cho viên gạch.</p>
     *
     * @param animation Đối tượng Animation mới.
     */
    public void setCrackAnimation(Animation animation) {
        this.crackAnimation = animation;
    }
}