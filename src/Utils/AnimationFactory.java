package Utils;

import Render.Animation;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import Objects.PowerUps.PowerUpType;
import Objects.GameEntities.PaddleState;

/**
 * FACTORY LAYER - Factory tạo Animation từ các sprite sequence.
 *
 * Kiến trúc:
 * AssetLoader (load từ disk) → SpriteCache (cache sprites) → **AnimationFactory** (tạo animations)
 *
 * Factory này sử dụng:
 * - SpriteCache để lấy cached sprites (không load trực tiếp từ disk)
 * - Constants.Animation để định nghĩa frame duration
 * 
 * Workflow:
 * AnimationFactory.createXXX() → SpriteCache.get() → (nếu chưa có) → AssetLoader.loadImage()
 *
 * @author SteveHoang aka BoizSocSon
 */
public final class AnimationFactory {

    private AnimationFactory() {
        throw new UnsupportedOperationException("Utility class - cannot instantiate");
    }

    /**
     * Tạo animation cho gạch bạc bị nứt (crack effect).
     *
     * Spec:
     * - Frames: brick_silver_1.png đến brick_silver_10.png
     * - Duration: 50ms/frame (Constants.Animation.CRACK_ANIMATION_DURATION)
     * - Loop: false (chạy 1 lần)
     *
     * @return Animation object cho brick crack effect
     */
    public static Animation createBrickCrackAnimation() {
        List<Image> frames = loadSequence("brick_silver_%d.png", 1, 10);
        // Sử dụng CRACK_ANIMATION_DURATION từ Constants
        return new Animation(frames, Constants.Animation.CRACK_ANIMATION_DURATION, false);
    }

    /**
     * Tạo animation cho power-up dựa trên loại power-up.
     *
     * Spec:
     * - Frames: powerup_{type}_1.png đến powerup_{type}_8.png
     * - Duration: 100ms/frame (Constants.Animation.POWERUP_ANIMATION_DURATION)
     * - Loop: true (animation liên tục khi rơi)
     *
     * @param type loại power-up (CATCH, EXPAND, LASER, v.v.)
     * @return Animation object cho power-up
     */
    public static Animation createPowerUpAnimation(PowerUpType type) {
        // Sử dụng spritePrefix từ PowerUpType để build tên file
        String pattern = type.getSpritePrefix() + "_%d.png";
        List<Image> frames = loadSequence(pattern, 1, 8);
        // Sử dụng POWERUP_ANIMATION_DURATION từ Constants
        return new Animation(frames, Constants.Animation.POWERUP_ANIMATION_DURATION, true);
    }

    /**
     * Tạo animation cho paddle dựa trên trạng thái.
     *
     * Spec từ requirements:
     * - NORMAL: paddle.png (static)
     * - WIDE: paddle_wide_1.png...9.png (60ms/frame, loop)
     * - LASER: paddle_laser_1.png...16.png (40ms/frame, loop)
     * - PULSATE: paddle_pulsate_1.png...4.png (80ms/frame, loop)
     * - MATERIALIZE: paddle_materialize_1.png...15.png (45ms/frame, once)
     * - EXPLODE: paddle_explode_1.png...8.png (50ms/frame, once)
     *
     * @param state trạng thái paddle
     * @return Animation object tương ứng
     */
    public static Animation createPaddleAnimation(PaddleState state) {
        switch (state) {
            case NORMAL: {
                // Static sprite - single frame, long duration, loop true
                List<Image> single = new ArrayList<>();
                single.add(loadSingle("paddle.png"));
                return new Animation(single, 1000L, true);
            }
            case WIDE: {
                // 9 frames, 60ms each, loop
                List<Image> frames = loadSequence("paddle_wide_%d.png", 1, 9);
                return new Animation(frames, 60L, true);
            }
            case LASER: {
                // 16 frames, 40ms each, loop
                List<Image> frames = loadSequence("paddle_laser_%d.png", 1, 16);
                return new Animation(frames, 40L, true);
            }
            case PULSATE: {
                // 4 frames, 80ms each, loop
                List<Image> frames = loadSequence("paddle_pulsate_%d.png", 1, 4);
                return new Animation(frames, 80L, true);
            }
            case MATERIALIZE: {
                // 15 frames, 45ms each, once
                List<Image> frames = loadSequence("paddle_materialize_%d.png", 1, 15);
                return new Animation(frames, 45L, false);
            }
            case EXPLODE: {
                // 8 frames, 50ms each, once
                List<Image> frames = loadSequence("paddle_explode_%d.png", 1, 8);
                return new Animation(frames, 50L, false);
            }
            default: {
                // Fallback to normal
                List<Image> single = new ArrayList<>();
                single.add(loadSingle("paddle.png"));
                return new Animation(single, 1000L, true);
            }
        }
    }

    /**
     * Tạo animation cho laser bullet.
     *
     * Spec:
     * - Frame: laser_bullet.png (single frame)
     * - Loop: true (để render liên tục)
     * - Note: Có thể thêm glow effect trong tương lai
     *
     * @return Animation object cho laser bullet
     */
    public static Animation createLaserBulletAnimation() {
        // Single-frame bullet; loop true để renderer vẽ liên tục
        List<Image> frames = new ArrayList<>();
        frames.add(loadSingle("laser_bullet.png"));
        return new Animation(frames, 1000L, true);
    }

    // ==================== HELPER METHODS ====================
    // Các phương thức phụ trợ để lấy sprites từ SpriteCache (không load trực tiếp).

    /**
     * Load một sequence sprites theo pattern với số thứ tự.
     * 
     * Workflow: AnimationFactory → SpriteCache.getInstance().get() → (cache miss) → AssetLoader
     *
     * @param patternWithPercentD pattern chứa %d (ví dụ: "brick_%d.png")
     * @param from số thứ tự bắt đầu (inclusive)
     * @param to số thứ tự kết thúc (inclusive)
     * @return danh sách Image objects
     */
    private static List<Image> loadSequence(String patternWithPercentD, int from, int to) {
        List<Image> frames = new ArrayList<>();
        SpriteCache cache = SpriteCache.getInstance();
        
        for (int i = from; i <= to; i++) {
            String filename = String.format(patternWithPercentD, i);
            // Lấy từ cache (SpriteCache sẽ gọi AssetLoader nếu chưa có)
            frames.add(cache.get(filename));
        }
        return frames;
    }

    /**
     * Load một sprite đơn từ SpriteCache.
     *
     * Workflow: AnimationFactory → SpriteCache.get() → (nếu chưa có) → AssetLoader.loadImage()
     *
     * @param filename tên file sprite (ví dụ: "paddle.png")
     * @return Image object (có thể là placeholder nếu load failed)
     */
    private static Image loadSingle(String filename) {
        // Lấy từ SpriteCache thay vì load trực tiếp
        return SpriteCache.getInstance().get(filename);
    }

    /**
     * Tạo placeholder image (1x1 transparent pixel) khi sprite không load được.
     * 
     * DEPRECATED: Sử dụng AssetLoader.createPlaceholderImage() thay thế.
     * Method này giữ lại để backwards compatibility.
     *
     * @return WritableImage 1x1 pixel
     */
    @Deprecated
    private static Image createPlaceholder() {
        return new WritableImage(1, 1);
    }
}
