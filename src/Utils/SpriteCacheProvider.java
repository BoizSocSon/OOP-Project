package Utils;

import Objects.GameEntities.PaddleState;
import Objects.PowerUps.PowerUpType;
import javafx.scene.image.Image;
import java.util.List;

/**
 * Lớp triển khai giao diện {@link SpriteProvider}, sử dụng {@link SpriteCache}
 * làm nguồn cung cấp tài nguyên hình ảnh thực tế.
 * Lớp này hoạt động như một cầu nối, cho phép các lớp khác truy cập vào
 * các sprite và animation frames đã được lưu trong cache một cách dễ dàng
 * và rõ ràng theo giao diện đã định nghĩa.
 */
public final class SpriteCacheProvider implements SpriteProvider {
    // Tham chiếu đến đối tượng SpriteCache chứa tất cả tài nguyên đã được tải.
    private final SpriteCache cache;

    /**
     * Khởi tạo một đối tượng SpriteCacheProvider mới.
     *
     * @param cache Đối tượng SpriteCache đã được khởi tạo và chứa tài nguyên.
     */
    public SpriteCacheProvider(SpriteCache cache) {
        this.cache = cache;
    }

    /**
     * Lấy một sprite tĩnh từ cache bằng tên file.
     *
     * @param filename Tên file của sprite.
     * @return Đối tượng {@link Image} tương ứng.
     */
    @Override
    public Image get(String filename) {
        return cache.getImage(filename);
    }

    /**
     * Lấy danh sách các khung hình animation cho một loại PowerUp cụ thể.
     *
     * @param type Loại PowerUp.
     * @return Danh sách các khung hình {@link Image} của PowerUp đó.
     */
    @Override
    public List<Image> getPowerUpFrames(PowerUpType type) {
        // Sử dụng switch expression để trả về đúng danh sách cache dựa trên loại PowerUp.
        return switch (type) {
            case CATCH -> cache.getPowerUpCatchCache();
            case EXPAND -> cache.getPowerUpExpandCache();
            case LASER -> cache.getPowerUpLaserCache();
            case DUPLICATE -> cache.getPowerUpDuplicateCache();
            case SLOW -> cache.getPowerUpSlowCache();
            case LIFE -> cache.getPowerUPLifeCache();
            case WARP -> cache.getPowerUpWarpCache();
        };
    }

    /**
     * Lấy danh sách các khung hình animation cho một trạng thái Paddle cụ thể.
     *
     * @param state Trạng thái của Paddle.
     * @return Danh sách các khung hình {@link Image} của trạng thái Paddle đó.
     * @throws IllegalStateException Nếu trạng thái là NORMAL vì đây là sprite tĩnh.
     */
    @Override
    public List<Image> getPaddleFrames(PaddleState state) {
        // Sử dụng switch expression để trả về đúng danh sách cache dựa trên trạng thái Paddle.
        return switch (state) {
            case NORMAL -> throw new IllegalStateException("NORMAL is static image"); // State NORMAL sử dụng sprite tĩnh, không có animation frames.
            case WIDE -> cache.getPaddleWideCache();
            case WIDE_PULSATE -> cache.getPaddleWidePulsateCache();
            case LASER -> cache.getPaddleLaserCache();
            case LASER_PULSATE -> cache.getPaddleLaserPulsateCache();
            case PULSATE -> cache.getPaddlePulsateCache();
            case MATERIALIZE -> cache.getPaddleMaterializeCache();
            case EXPLODE -> cache.getPaddleExplodeCache();
        };
    }

    /**
     * Lấy danh sách các khung hình animation cho hiệu ứng gạch bạc bị nứt (Silver Crack).
     *
     * @return Danh sách các khung hình {@link Image} của hiệu ứng gạch nứt.
     */
    @Override
    public List<Image> getSilverCrackFrames() {
        return cache.getSilverCrackCache();
    }

    /**
     * Kiểm tra xem các tài nguyên đã sẵn sàng để sử dụng chưa (SpriteCache đã được khởi tạo chưa).
     *
     * @return {@code true} nếu tài nguyên đã sẵn sàng, ngược lại là {@code false}.
     */
    @Override
    public boolean isReady() {
        return cache.isInitialized();
    }
}