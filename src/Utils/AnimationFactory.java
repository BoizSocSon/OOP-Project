package Utils;

import Render.Animation;
import javafx.scene.image.Image;
import java.util.List;
import Objects.PowerUps.PowerUpType;
import Objects.GameEntities.PaddleState;

/**
 * Lớp nhà máy (Factory) tĩnh chịu trách nhiệm tạo ra các đối tượng
 * {@link Render.Animation} từ các tài nguyên (sprites) được cung cấp.
 * Lớp này đảm bảo rằng tất cả các animation được tạo ra đều sử dụng
 * cùng một nguồn tài nguyên ảnh (SpriteProvider).
 */
public final class AnimationFactory {

    /**
     * Constructor private để ngăn việc tạo ra các instance của lớp tiện ích (utility class) này.
     */
    private AnimationFactory() {
    }

    // Biến tĩnh để lưu trữ đối tượng cung cấp tài nguyên ảnh (sprite provider).
    private static SpriteProvider sprites;

    /**
     * Khởi tạo nhà máy bằng cách cung cấp đối tượng {@link SpriteProvider}.
     * Phương thức này phải được gọi một lần trong quá trình khởi tạo ứng dụng.
     *
     * @param spriteProvider Đối tượng cung cấp các khung hình ảnh (sprites) cho các animation.
     */
    public static void initialize(SpriteProvider spriteProvider) {
        sprites = spriteProvider;
    }

    /**
     * Phương thức tiện ích để đảm bảo rằng {@link SpriteProvider} đã được thiết lập
     * trước khi tạo bất kỳ animation nào.
     *
     * @return Đối tượng SpriteProvider đã được thiết lập.
     * @throws IllegalStateException Nếu SpriteProvider chưa được thiết lập (chưa gọi initialize).
     */
    private static SpriteProvider requireProvider() {
        // Kiểm tra xem SpriteProvider đã được thiết lập chưa.
        if (sprites == null) {
            // Ném ngoại lệ nếu chưa thiết lập, hướng dẫn người dùng gọi initialize.
            throw new IllegalStateException(
                    "AnimationFactory: SpriteProvider not set. Call AnimationFactory.initialize(...) during init.");
        }
        return sprites;
    }

    /**
     * Tạo animation cho hiệu ứng gạch bị nứt (crack).
     *
     * @return Một đối tượng {@link Animation} mô tả hiệu ứng nứt của gạch.
     */
    public static Animation createBrickCrackAnimation() {
        // Lấy danh sách các khung hình ảnh cho hiệu ứng gạch nứt từ nhà cung cấp.
        List<Image> frames = requireProvider().getSilverCrackFrames();
        // Tạo đối tượng Animation mới với danh sách khung hình, thời lượng và không lặp lại (false).
        return new Animation(frames, Constants.Animation.CRACK_ANIMATION_DURATION, false);
    }

    /**
     * Tạo animation cho một loại PowerUp cụ thể.
     *
     * @param type Loại PowerUp cần tạo animation.
     * @return Một đối tượng {@link Animation} mô tả chuyển động của PowerUp.
     */
    public static Animation createPowerUpAnimation(PowerUpType type) {
        // Lấy danh sách các khung hình ảnh cho PowerUp theo loại.
        List<Image> frames = requireProvider().getPowerUpFrames(type);
        // Tạo đối tượng Animation mới với danh sách khung hình, thời lượng và lặp lại (true).
        return new Animation(frames, Constants.Animation.POWERUP_ANIMATION_DURATION, true);
    }

    /**
     * Tạo animation cho thanh trượt (Paddle) dựa trên trạng thái hiện tại của nó.
     *
     * @param state Trạng thái của thanh trượt (ví dụ: LASER, FIREBALL).
     * @return Một đối tượng {@link Animation} mô tả trạng thái của thanh trượt.
     * @throws IllegalArgumentException Nếu trạng thái là NORMAL vì trạng thái NORMAL không có animation.
     */
    public static Animation createPaddleAnimation(PaddleState state) {
        // Kiểm tra nếu trạng thái là NORMAL thì ném ngoại lệ vì trạng thái này không có animation riêng.
        if (state == PaddleState.NORMAL) {
            throw new IllegalArgumentException("PaddleState.NORMAL does not have animation frames.");
        }
        // Lấy danh sách các khung hình ảnh cho Paddle theo trạng thái.
        List<Image> frames = requireProvider().getPaddleFrames(state);
        // Tạo đối tượng Animation mới, quyết định có lặp lại hay không dựa trên trạng thái của Paddle.
        return new Animation(frames, Constants.Animation.PADDLE_ANIMATION_DURATION, state.shouldLoop());
    }
}