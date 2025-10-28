package Utils;

import Objects.GameEntities.PaddleState;
import Objects.PowerUps.PowerUpType;
import javafx.scene.image.Image;

import java.util.List;

/**
 * Lớp cung cấp giao diện truy cập các sprite (hình ảnh) đã được lưu trữ sẵn trong {@link SpriteCache}.
 * <p>
 * Mục đích chính: tách biệt logic quản lý bộ nhớ đệm hình ảnh (SpriteCache)
 * khỏi phần mã sử dụng chúng, giúp chương trình tuân thủ nguyên tắc "Separation of Concerns".
 * </p>
 */
public final class SpriteCacheProvider implements SpriteProvider {
    /** Tham chiếu đến đối tượng SpriteCache chứa tất cả các sprite của trò chơi. */
    private final SpriteCache cache;

    /**
     * Khởi tạo một đối tượng {@code SpriteCacheProvider} với bộ nhớ đệm hình ảnh được truyền vào.
     *
     * @param cache đối tượng {@link SpriteCache} đã được khởi tạo và chứa dữ liệu hình ảnh.
     */
    public SpriteCacheProvider(SpriteCache cache) {
        this.cache = cache;
    }

    /**
     * Lấy ra một hình ảnh tĩnh từ bộ nhớ đệm dựa trên tên file.
     *
     * @param filename tên file ảnh (ví dụ: "ball.png")
     * @return đối tượng {@link Image} tương ứng, hoặc null nếu không tồn tại.
     */
    @Override
    public Image get(String filename) {
        // Truy cập ảnh tĩnh trực tiếp từ SpriteCache
        return cache.getImage(filename);
    }

    /**
     * Lấy danh sách các frame ảnh tương ứng với loại Power-Up.
     * Mỗi Power-Up thường có nhiều frame để tạo hiệu ứng động.
     *
     * @param type loại Power-Up (CATCH, EXPAND, LASER, v.v.)
     * @return danh sách các frame {@link Image} cho Power-Up đó.
     */
    @Override
    public List<Image> getPowerUpFrames(PowerUpType type) {
        // Dùng switch expression để chọn đúng cache tương ứng với từng loại Power-Up
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
     * Lấy danh sách các frame ảnh tương ứng với trạng thái của Paddle.
     * Dùng để hiển thị các hiệu ứng như mở rộng, phát sáng, bắn laser, v.v.
     *
     * @param state trạng thái của Paddle (WIDE, PULSATE, LASER, v.v.)
     * @return danh sách {@link Image} frame cho trạng thái đó.
     */
    @Override
    public List<Image> getPaddleFrames(PaddleState state) {
        // NORMAL là hình tĩnh, nên không có danh sách frame động -> ném ngoại lệ
        return switch (state) {
            case NORMAL -> throw new IllegalStateException("NORMAL is static image");
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
     * Lấy danh sách các frame nứt vỡ của viên gạch bạc (Silver Brick).
     * Dùng cho hiệu ứng nứt dần khi va chạm.
     *
     * @return danh sách {@link Image} frame của hiệu ứng nứt gạch bạc.
     */
    @Override
    public List<Image> getSilverCrackFrames() {
        return cache.getSilverCrackCache();
    }

    /**
     * Kiểm tra xem bộ nhớ đệm SpriteCache đã được khởi tạo chưa.
     *
     * @return true nếu cache đã sẵn sàng để sử dụng, ngược lại false.
     */
    @Override
    public boolean isReady() {
        // Trả về trạng thái đã được khởi tạo hay chưa của cache
        return cache.isInitialized();
    }
}
