package Utils;

import javafx.scene.image.Image;
import Objects.Bricks.BrickType;
import Objects.PowerUps.PowerUpType;
import Objects.GameEntities.PaddleState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp Singleton chịu trách nhiệm tải, lưu trữ và quản lý cache cho tất cả các
 * hình ảnh (sprites) và khung hình animation (frames) được sử dụng trong game.
 * Việc sử dụng cache giúp tránh tải lại các tài nguyên giống nhau nhiều lần,
 * cải thiện hiệu suất.
 */
public final class SpriteCache {
    // Instance duy nhất của lớp SpriteCache (mẫu thiết kế Singleton).
    private static SpriteCache instance;
    // Cache chính lưu trữ các sprite tĩnh (không phải animation), key là tên file.
    private final Map<String, Image> cache = new HashMap<>();
    // Các List lưu trữ khung hình animation.
    private final List<Image> silverCrackCache = new ArrayList<>(); // Animation gạch bạc bị nứt
    private final List<Image> powerUpCatchCache = new ArrayList<>(); // Animation PowerUp CATCH
    private final List<Image> powerUpExpandCache = new ArrayList<>(); // Animation PowerUp EXPAND
    private final List<Image> powerUpLaserCache = new ArrayList<>(); // Animation PowerUp LASER
    private final List<Image> powerUpDuplicateCache = new ArrayList<>(); // Animation PowerUp DUPLICATE
    private final List<Image> powerUpSlowCache = new ArrayList<>(); // Animation PowerUp SLOW
    private final List<Image> powerUPLifeCache = new ArrayList<>(); // Animation PowerUp LIFE
    private final List<Image> powerUpWarpCache = new ArrayList<>(); // Animation PowerUp WARP
    private final List<Image> paddleWideCache = new ArrayList<>(); // Animation Paddle WIDE
    private final List<Image> paddleWidePulsateCache = new ArrayList<>(); // Animation Paddle WIDE_PULSATE
    private final List<Image> paddleLaserCache = new ArrayList<>(); // Animation Paddle LASER
    private final List<Image> paddleLaserPulsateCache = new ArrayList<>(); // Animation Paddle LASER_PULSATE
    private final List<Image> paddlePulsateCache = new ArrayList<>(); // Animation Paddle PULSATE
    private final List<Image> paddleMaterializeCache = new ArrayList<>(); // Animation Paddle MATERIALIZE (xuất hiện)
    private final List<Image> paddleExplodeCache = new ArrayList<>(); // Animation Paddle EXPLODE (nổ)
    private boolean initialized = false; // Cờ kiểm tra xem cache đã được khởi tạo chưa.
    private int totalSprites = 0; // Tổng số sprite/khung hình đã tải.

    // Đường dẫn gốc tới thư mục đồ họa.
    private static final String path = Constants.Paths.GRAPHICS_PATH;

    /**
     * Constructor private để đảm bảo chỉ có thể truy cập qua {@link #getInstance()}.
     */
    private SpriteCache() {
    }

    /**
     * Lấy instance duy nhất của SpriteCache.
     *
     * @return Instance của SpriteCache.
     */
    public static synchronized SpriteCache getInstance() {
        if (instance == null) {
            instance = new SpriteCache();
        }

        return instance;
    }

    /**
     * Tải và lưu trữ tất cả các sprite và khung hình animation cần thiết vào cache.
     * Phương thức này phải được gọi một lần duy nhất.
     */
    public synchronized void initialize() {
        // Kiểm tra cờ khởi tạo để tránh tải lại.
        if (initialized) {
            System.out.println("SpriteCache: Already initialized, skipping.");
            return;
        }

        long startTime = System.currentTimeMillis();

        // Tải các nhóm sprite cụ thể.
        loadBrickSprites();
        loadPowerUpSprites();
        loadPaddleSprites();
        loadBallSprite();
        loadLaserSprites();
        loadEdgeSprites();
        loadLogoSprite();

        // Tính tổng số sprite đã tải (bao gồm cả sprite tĩnh và các khung hình animation).
        totalSprites = cache.size()
                + silverCrackCache.size()
                + powerUpCatchCache.size()
                + powerUpExpandCache.size()
                + powerUpLaserCache.size()
                + powerUpDuplicateCache.size()
                + powerUpSlowCache.size()
                + powerUPLifeCache.size()
                + powerUpWarpCache.size()
                + paddleWideCache.size()
                + paddleWidePulsateCache.size()
                + paddleLaserCache.size()
                + paddleLaserPulsateCache.size()
                + paddlePulsateCache.size()
                + paddleMaterializeCache.size()
                + paddleExplodeCache.size();
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.printf("SpriteCache: Loaded %d sprites in %d ms%n", totalSprites, elapsed);

        initialized = true; // Đặt cờ đã khởi tạo.
    }

    /**
     * Xóa tất cả dữ liệu sprite đã lưu trữ khỏi cache và đặt lại trạng thái.
     */
    public void clear() {
        // Xóa tất cả các Map và List cache.
        cache.clear();
        silverCrackCache.clear();
        powerUpCatchCache.clear();
        powerUpExpandCache.clear();
        powerUpLaserCache.clear();
        powerUpDuplicateCache.clear();
        powerUpSlowCache.clear();
        powerUPLifeCache.clear();
        powerUpWarpCache.clear();
        paddleWideCache.clear();
        paddleWidePulsateCache.clear();
        paddleLaserCache.clear();
        paddleLaserPulsateCache.clear();
        paddlePulsateCache.clear();
        paddleMaterializeCache.clear();
        paddleExplodeCache.clear();
        totalSprites = 0; // Đặt lại tổng số sprite.
        initialized = false; // Đặt lại cờ khởi tạo.
        System.out.println("SpriteCache: Cleared all cached sprites.");
    }

    /**
     * In ra trạng thái hiện tại của SpriteCache.
     */
    public void printCacheStatus() {
        System.out.println("=== SpriteCache Status ===");
        System.out.println("Initialized: " + initialized); // Trạng thái khởi tạo.
        System.out.println("Cached images: " + totalSprites); // Tổng số ảnh đã cache.
    }

    /**
     * Tải các sprite của các loại gạch (Bricks).
     */
    private void loadBrickSprites() {
        // Tải sprite tĩnh cho từng loại gạch.
        for (BrickType type : BrickType.values()) {
            String filename = type.getSpriteName() + ".png";
            Image img = AssetLoader.loadImage(filename);
            cache.put(filename, img);
        }

        // Tải các khung hình animation cho gạch bạc (Silver Crack).
        for (int i = 1; i <= 10; i++) {
            String filename = BrickType.SILVER.getSpriteName() + "_" + i + ".png";
            Image img = AssetLoader.loadImage(filename);
            silverCrackCache.add(img);
        }
    }

    /**
     * Tải các khung hình animation cho các vật phẩm bổ trợ (PowerUps).
     */
    private void loadPowerUpSprites() {
        for (PowerUpType type : PowerUpType.values()) {
            // Xác định List cache mục tiêu dựa trên loại PowerUp.
            List<Image> targetCache = switch (type) {
                case CATCH -> powerUpCatchCache;
                case EXPAND -> powerUpExpandCache;
                case LASER -> powerUpLaserCache;
                case DUPLICATE -> powerUpDuplicateCache;
                case SLOW -> powerUpSlowCache;
                case LIFE -> powerUPLifeCache;
                case WARP -> powerUpWarpCache;
            };

            // Tải 8 khung hình animation cho mỗi PowerUp.
            for (int i = 1; i <= 8; i++) {
                String filename = type.getFramePath(i);
                Image img = AssetLoader.loadImage(filename);
                targetCache.add(img);
            }
        }
    }

    /**
     * Tải các sprite và khung hình animation cho thanh đỡ (Paddle).
     */
    private void loadPaddleSprites() {
        // Tải sprite tĩnh cho trạng thái NORMAL.
        String paddlePath = PaddleState.NORMAL.getPaddlePrefix() + ".png";
        Image paddleImg = AssetLoader.loadImage(paddlePath);
        cache.put(paddlePath, paddleImg);

        // Tải sprite tĩnh cho trạng thái LASER (khác với animation).
        String laserPath = PaddleState.LASER.getPaddlePrefix() + ".png";
        Image laserImg = AssetLoader.loadImage(laserPath);
        cache.put(laserPath, laserImg);

        // Tải sprite hiển thị mạng (Life Display).
        String lifePath = PaddleState.NORMAL.getPaddlePrefix() + "_life" + ".png";
        Image lifeImg = AssetLoader.loadImage(lifePath);
        cache.put(lifePath, lifeImg);

        // Tải sprite cho trạng thái mở rộng (Wide).
        String widePath = PaddleState.NORMAL.getPaddlePrefix() + "_wide" + ".png";
        Image wideImg = AssetLoader.loadImage(widePath);
        cache.put(widePath, wideImg);

        // Tải các khung hình animation cho các trạng thái đặc biệt của Paddle.
        for (PaddleState state : PaddleState.values()) {
            if (state == PaddleState.NORMAL) {
                continue; // Bỏ qua trạng thái NORMAL vì đã tải sprite tĩnh.
            }

            // Xác định List cache mục tiêu dựa trên trạng thái Paddle.
            List<Image> targetCache = switch (state) {
                case NORMAL -> throw new IllegalStateException("NORMAL state should use static image.");
                case WIDE -> paddleWideCache;
                case WIDE_PULSATE -> paddleWidePulsateCache;
                case LASER -> paddleLaserCache;
                case LASER_PULSATE -> paddleLaserPulsateCache;
                case PULSATE -> paddlePulsateCache;
                case MATERIALIZE -> paddleMaterializeCache;
                case EXPLODE -> paddleExplodeCache;
            };

            // Tải từng khung hình animation.
            for (int i = 1; i <= state.getFrameCount(); i++) {
                String filename = state.getPaddlePrefix() + "_" + i + ".png";
                Image img = AssetLoader.loadImage(filename);
                targetCache.add(img);
            }
        }
    }

    /**
     * Tải sprite của bóng (Ball).
     */
    private void loadBallSprite() {
        String filename = "ball.png";
        Image img = AssetLoader.loadImage(filename);
        cache.put(filename, img);
    }

    /**
     * Tải sprite của viên đạn laser.
     */
    private void loadLaserSprites() {
        String filename = "laser_bullet.png";
        Image img = AssetLoader.loadImage(filename);
        cache.put(filename, img);
    }

    /**
     * Tải các sprite của khung viền game (Edges).
     */
    private void loadEdgeSprites() {
        String topEdge = "edge_top.png";
        String rightEdge = "edge_right.png";
        String leftEdge = "edge_left.png";

        // Tải từng sprite viền.
        Image topImg = AssetLoader.loadImage(topEdge);
        Image rightImg = AssetLoader.loadImage(rightEdge);
        Image leftImg = AssetLoader.loadImage(leftEdge);

        // Lưu vào cache.
        cache.put(topEdge, topImg);
        cache.put(rightEdge, rightImg);
        cache.put(leftEdge, leftImg);
    }

    /**
     * Tải sprite của logo game.
     */
    private void loadLogoSprite() {
        String filename = "logo.png";
        Image img = AssetLoader.loadImage(filename);
        cache.put(filename, img);
    }

    /**
     * Lấy một sprite tĩnh từ cache bằng tên file.
     *
     * @param filename Tên file của sprite.
     * @return Đối tượng {@link Image} tương ứng.
     */
    public Image getImage(String filename) {
        return cache.get(filename);
    }

    /**
     * Lấy toàn bộ Map cache chứa các sprite tĩnh.
     *
     * @return Map cache.
     */
    public Map<String, Image> getCache() {
        return cache;
    }

    /**
     * Lấy các khung hình animation gạch bạc nứt.
     *
     * @return List các khung hình.
     */
    public List<Image> getSilverCrackCache() {
        return silverCrackCache;
    }

    /**
     * Lấy các khung hình animation PowerUp CATCH.
     *
     * @return List các khung hình.
     */
    public List<Image> getPowerUpCatchCache() {
        return powerUpCatchCache;
    }

    /**
     * Lấy các khung hình animation PowerUp EXPAND.
     *
     * @return List các khung hình.
     */
    public List<Image> getPowerUpExpandCache() {
        return powerUpExpandCache;
    }

    /**
     * Lấy các khung hình animation PowerUp LASER.
     *
     * @return List các khung hình.
     */
    public List<Image> getPowerUpLaserCache() {
        return powerUpLaserCache;
    }

    /**
     * Lấy các khung hình animation PowerUp DUPLICATE.
     *
     * @return List các khung hình.
     */
    public List<Image> getPowerUpDuplicateCache() {
        return powerUpDuplicateCache;
    }

    /**
     * Lấy các khung hình animation PowerUp SLOW.
     *
     * @return List các khung hình.
     */
    public List<Image> getPowerUpSlowCache() {
        return powerUpSlowCache;
    }

    /**
     * Lấy các khung hình animation PowerUp LIFE.
     *
     * @return List các khung hình.
     */
    public List<Image> getPowerUPLifeCache() {
        return powerUPLifeCache;
    }

    /**
     * Lấy các khung hình animation PowerUp WARP.
     *
     * @return List các khung hình.
     */
    public List<Image> getPowerUpWarpCache() {
        return powerUpWarpCache;
    }

    /**
     * Lấy các khung hình animation Paddle WIDE.
     *
     * @return List các khung hình.
     */
    public List<Image> getPaddleWideCache() {
        return paddleWideCache;
    }

    /**
     * Lấy các khung hình animation Paddle WIDE_PULSATE.
     *
     * @return List các khung hình.
     */
    public List<Image> getPaddleWidePulsateCache() {
        return paddleWidePulsateCache;
    }

    /**
     * Lấy các khung hình animation Paddle LASER.
     *
     * @return List các khung hình.
     */
    public List<Image> getPaddleLaserCache() {
        return paddleLaserCache;
    }

    /**
     * Lấy các khung hình animation Paddle LASER_PULSATE.
     *
     * @return List các khung hình.
     */
    public List<Image> getPaddleLaserPulsateCache() {
        return paddleLaserPulsateCache;
    }

    /**
     * Lấy các khung hình animation Paddle PULSATE.
     *
     * @return List các khung hình.
     */
    public List<Image> getPaddlePulsateCache() {
        return paddlePulsateCache;
    }

    /**
     * Lấy các khung hình animation Paddle MATERIALIZE.
     *
     * @return List các khung hình.
     */
    public List<Image> getPaddleMaterializeCache() {
        return paddleMaterializeCache;
    }

    /**
     * Lấy các khung hình animation Paddle EXPLODE.
     *
     * @return List các khung hình.
     */
    public List<Image> getPaddleExplodeCache() {
        return paddleExplodeCache;
    }

    /**
     * Kiểm tra xem cache đã được khởi tạo chưa.
     *
     * @return {@code true} nếu đã khởi tạo, ngược lại là {@code false}.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Lấy tổng số sprite/khung hình đã được tải vào cache.
     *
     * @return Tổng số sprite.
     */
    public int getTotalSprites() {
        return totalSprites;
    }
}