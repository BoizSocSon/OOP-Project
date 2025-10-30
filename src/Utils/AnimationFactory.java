package Utils;

import Render.Animation;
import javafx.scene.image.Image;
import java.util.List;
import Objects.PowerUps.PowerUpType;
import Objects.GameEntities.PaddleState;

/**
 * Lớp tiện ích (factory) chịu trách nhiệm tạo ra các đối tượng {@link Animation}
 * cho các thành phần trong game như gạch, power-up và thanh đỡ (paddle).
 *
 * <p>Đây là một lớp dạng singleton không cho phép khởi tạo trực tiếp.
 * Mọi phương thức đều là static, giúp quản lý và tái sử dụng hiệu quả các animation.</p>
 *
 * <p>Cần gọi {@link #initialize(SpriteProvider)} để cung cấp nguồn sprite
 * trước khi gọi các hàm tạo animation.</p>
 */
public final class AnimationFactory {

    /**
     * Constructor private để tránh việc tạo đối tượng ngoài ý muốn.
     * Chỉ được sử dụng thông qua các phương thức static.
     */
    private AnimationFactory() {
    }

    /**
     * Đối tượng SpriteProvider dùng để truy xuất ảnh động (sprite frames) từ bộ tài nguyên game.
     * Biến này được chia sẻ toàn cục trong lớp.
     */
    private static SpriteProvider sprites;

    /**
     * Khởi tạo SpriteProvider cho factory.
     *
     * Phải gọi hàm này trong giai đoạn khởi động (init) của game để đảm bảo
     * các phương thức tạo animation có thể truy cập được dữ liệu ảnh.
     *
     * @param spriteProvider đối tượng cung cấp ảnh động (sprite frames) cho game
     */
    public static void initialize(SpriteProvider spriteProvider) {
        sprites = spriteProvider;
    }

    /**
     * Đảm bảo SpriteProvider đã được khởi tạo trước khi sử dụng.
     * Nếu chưa, ném ra ngoại lệ để tránh lỗi NullPointerException.
     *
     * @return đối tượng SpriteProvider hiện tại
     * @throws IllegalStateException nếu chưa được gọi initialize()
     */
    private static SpriteProvider requireProvider() {
        if (sprites == null) {
            throw new IllegalStateException(
                    "AnimationFactory: SpriteProvider not set. Call AnimationFactory.initialize(...) during init.");
        }
        return sprites;
    }

    /**
     * Tạo animation cho hiệu ứng gạch bạc bị nứt (silver brick crack).
     *
     * Animation này thường được kích hoạt khi viên gạch bị va chạm.
     *
     * @return animation chỉ chạy một lần (không lặp)
     */
    public static Animation createBrickCrackAnimation() {
        // Lấy danh sách khung hình (frames) cho hiệu ứng nứt gạch từ SpriteProvider
        List<Image> frames = requireProvider().getSilverCrackFrames();

        // Tạo animation với thời lượng được định nghĩa trong Constants, không lặp lại
        return new Animation(frames, Constants.Animation.CRACK_ANIMATION_DURATION, false);
    }

    /**
     * Tạo animation cho vật phẩm (power-up) rơi xuống.
     *
     * Mỗi loại power-up có tập khung hình riêng biệt.
     *
     * @param type loại power-up (ví dụ: mở rộng paddle, làm chậm bóng, v.v.)
     * @return animation lặp vô hạn cho đến khi vật phẩm bị thu thập
     */
    public static Animation createPowerUpAnimation(PowerUpType type) {
        // Lấy các khung hình (frames) tương ứng với loại power-up truyền vào
        List<Image> frames = requireProvider().getPowerUpFrames(type);

        // Power-up thường hiển thị liên tục trên màn hình nên cần animation lặp (loop = true)
        return new Animation(frames, Constants.Animation.POWERUP_ANIMATION_DURATION, true);
    }

    /**
     * Tạo animation cho các trạng thái đặc biệt của paddle (ví dụ: MATERIALIZE, EXPLODE).
     *
     * Các animation này chỉ xuất hiện khi paddle thay đổi trạng thái, ví dụ khi xuất hiện hoặc phát nổ.
     *
     * @param state trạng thái hiện tại của paddle
     * @return animation tương ứng với trạng thái đó
     * @throws IllegalArgumentException nếu state là NORMAL (vì trạng thái này không có animation)
     */
    public static Animation createPaddleAnimation(PaddleState state) {
        // Kiểm tra: nếu paddle ở trạng thái bình thường thì không có animation để hiển thị
        if (state == PaddleState.NORMAL) {
            throw new IllegalArgumentException("PaddleState.NORMAL does not have animation frames.");
        }

        // Lấy danh sách khung hình tương ứng với trạng thái paddle
        List<Image> frames = requireProvider().getPaddleFrames(state);

        // Fallback: if frames list is empty (missing assets or not initialized),
        // try to load a single static image as a placeholder so the Animation
        // constructor doesn't throw. This makes the app more robust to init order
        // or missing files.
        //
        // (Nếu danh sách khung hình bị thiếu, có thể nạp ảnh tĩnh thay thế để tránh lỗi runtime)
        // if (frames == null || frames.isEmpty()) {
        //     Image fallback = requireProvider().get(state.getPaddlePrefix() + ".png");
        //     frames = java.util.List.of(fallback);
        // }

        // Use the loop setting from PaddleState (MATERIALIZE and EXPLODE are one-shot)
        // Sử dụng cờ lặp (loop) được định nghĩa sẵn trong state:
        // MATERIALIZE hoặc EXPLODE là animation một lần, còn các loại khác có thể lặp.
        return new Animation(frames, Constants.Animation.PADDLE_ANIMATION_DURATION, state.shouldLoop());
    }
}
