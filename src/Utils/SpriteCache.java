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
 * Lớp Singleton chịu trách nhiệm tải, lưu trữ và quản lý toàn bộ sprite (hình ảnh)
 * được sử dụng trong trò chơi Arkanoid.
 * Các sprite được cache để tránh việc tải lại nhiều lần, giúp tăng hiệu năng.
 */
public final class SpriteCache {

    // Singleton instance duy nhất
    private static SpriteCache instance;

    // Cache cho các ảnh tĩnh: bricks, ball, laser, paddle, edge, logo, ...
    private final Map<String, Image> cache = new HashMap<>();

    // Cache cho các ảnh động (frame animation)
    private final List<Image> silverCrackCache = new ArrayList<>();
    private final List<Image> powerUpCatchCache = new ArrayList<>();
    private final List<Image> powerUpExpandCache = new ArrayList<>();
    private final List<Image> powerUpLaserCache = new ArrayList<>();
    private final List<Image> powerUpDuplicateCache = new ArrayList<>();
    private final List<Image> powerUpSlowCache = new ArrayList<>();
    private final List<Image> powerUPLifeCache = new ArrayList<>();
    private final List<Image> powerUpWarpCache = new ArrayList<>();
    private final List<Image> paddleWideCache = new ArrayList<>();
    private final List<Image> paddleWidePulsateCache = new ArrayList<>();
    private final List<Image> paddleLaserCache = new ArrayList<>();
    private final List<Image> paddleLaserPulsateCache = new ArrayList<>();
    private final List<Image> paddlePulsateCache = new ArrayList<>();
    private final List<Image> paddleMaterializeCache = new ArrayList<>();
    private final List<Image> paddleExplodeCache = new ArrayList<>();

    // Biến đánh dấu trạng thái đã khởi tạo chưa
    private boolean initialized = false;

    // Tổng số sprite được load
    private int totalSprites = 0;

    // Đường dẫn gốc đến thư mục chứa hình ảnh
    private static final String path = Constants.Paths.GRAPHICS_PATH;

    // Constructor private để chặn khởi tạo trực tiếp
    private SpriteCache() {}

    /**
     * Lấy instance duy nhất của SpriteCache (mẫu Singleton).
     * @return instance của SpriteCache
     */
    public static synchronized SpriteCache getInstance() {
        if (instance == null) {
            instance = new SpriteCache();
        }
        return instance;
    }

    /**
     * Khởi tạo và tải toàn bộ sprite nếu chưa được load.
     * Bao gồm: brick, power-up, paddle, ball, laser, edge, logo.
     */
    public synchronized void initialize() {
        if (initialized) {
            System.out.println("SpriteCache: Already initialized, skipping.");
            return;
        }

        long startTime = System.currentTimeMillis(); // Bắt đầu tính thời gian load

        // 1. Load Brick sprites
        loadBrickSprites();
        // 2. Load PowerUp sprites
        loadPowerUpSprites();
        // 3. Load Paddle sprites
        loadPaddleSprites();
        // 4. Load Ball sprite
        loadBallSprite();
        // 5. Load Laser sprites
        loadLaserSprites();
        // 6. Load Edge sprites
        loadEdgeSprites();
        // 7. Load Logo sprite
        loadLogoSprite();

        // Tính tổng số sprite đã load
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

        initialized = true; // Đánh dấu đã khởi tạo
    }

    /**
     * Xóa toàn bộ cache — thường dùng khi reset hoặc thoát game.
     */
    public void clear() {
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
        totalSprites = 0;
        initialized = false;
        System.out.println("SpriteCache: Cleared all cached sprites.");
    }

    /**
     * In thông tin trạng thái của cache ra console.
     */
    public void printCacheStatus() {
        System.out.println("=== SpriteCache Status ===");
        System.out.println("Initialized: " + initialized);
        System.out.println("Cached images: " + totalSprites);
//        System.out.println("Memory usage (approx): " + estimateMemoryUsage() + " MB");
    }

    /**
     * Tải toàn bộ sprite cho gạch (Brick).
     */
    private void loadBrickSprites() {
        // Load các loại gạch thông thường
        for (BrickType type : BrickType.values()) {
            String filename = type.getSpriteName() + ".png";
            Image img = AssetLoader.loadImage(filename);
            cache.put(filename, img); // Lưu vào cache chính
        }

        // Load các frame nứt (crack) cho gạch bạc (Silver brick)
        for (int i = 1; i <= 10; i++) {
            String filename = BrickType.SILVER.getSpriteName() + "_" + i + ".png";
            Image img = AssetLoader.loadImage(filename);
            silverCrackCache.add(img);
        }
    }

    /**
     * Tải sprite cho tất cả các loại PowerUp.
     */
    private void loadPowerUpSprites() {
        for (PowerUpType type : PowerUpType.values()) {
            // Xác định cache tương ứng cho từng loại power-up
            List<Image> targetCache = switch (type) {
                case CATCH -> powerUpCatchCache;
                case EXPAND -> powerUpExpandCache;
                case LASER -> powerUpLaserCache;
                case DUPLICATE -> powerUpDuplicateCache;
                case SLOW -> powerUpSlowCache;
                case LIFE -> powerUPLifeCache;
                case WARP -> powerUpWarpCache;
            };

            // Mỗi power-up có 8 frame animation
            for (int i = 1; i <= 8; i++) {
                String filename = type.getFramePath(i);
                Image img = AssetLoader.loadImage(filename);
                targetCache.add(img);
            }
        }
    }

    /**
     * Tải các sprite cho paddle (thanh đỡ) ở nhiều trạng thái khác nhau.
     */
    private void loadPaddleSprites() {
        // Load các ảnh tĩnh cơ bản của paddle
        String paddlePath = PaddleState.NORMAL.getPaddlePrefix() + ".png";
        Image paddleImg = AssetLoader.loadImage(paddlePath);
        cache.put(paddlePath, paddleImg);

        String laserPath = PaddleState.LASER.getPaddlePrefix() + ".png";
        Image laserImg = AssetLoader.loadImage(laserPath);
        cache.put(laserPath, laserImg);

        String lifePath = PaddleState.NORMAL.getPaddlePrefix() + "_life" + ".png";
        Image lifeImg = AssetLoader.loadImage(lifePath);
        cache.put(lifePath, lifeImg);

        String widePath = PaddleState.NORMAL.getPaddlePrefix() + "_wide" + ".png";
        Image wideImg = AssetLoader.loadImage(widePath);
        cache.put(widePath, wideImg);

        // Load các trạng thái animation của paddle
        for (PaddleState state : PaddleState.values()) {
            if (state == PaddleState.NORMAL) {
                continue; // NORMAL sử dụng ảnh tĩnh, đã load ở trên
            }

            // Lựa chọn cache tương ứng cho mỗi trạng thái
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

            // Load từng frame của animation
            for (int i = 1; i <= state.getFrameCount(); i++) {
                String filename = state.getPaddlePrefix() + "_" + i + ".png";
                Image img = AssetLoader.loadImage(filename);
                targetCache.add(img);
            }
        }
    }

    /** Tải sprite quả bóng. */
    private void loadBallSprite() {
        String filename = "ball.png";
        Image img = AssetLoader.loadImage(filename);
        cache.put(filename, img);
    }

    /** Tải sprite của tia laser. */
    private void loadLaserSprites() {
        String filename = "laser_bullet.png";
        Image img = AssetLoader.loadImage(filename);
        cache.put(filename, img);
    }

    /** Tải sprite cho ba cạnh của màn chơi (top, right, left). */
    private void loadEdgeSprites() {
        String topEdge = "edge_top.png";
        String rightEdge = "edge_right.png";
        String leftEdge = "edge_left.png";

        Image topImg = AssetLoader.loadImage(topEdge);
        Image rightImg = AssetLoader.loadImage(rightEdge);
        Image leftImg = AssetLoader.loadImage(leftEdge);

        cache.put(topEdge, topImg);
        cache.put(rightEdge, rightImg);
        cache.put(leftEdge, leftImg);
    }

    /** Tải sprite logo game. */
    private void loadLogoSprite() {
        String filename = "logo.png";
        Image img = AssetLoader.loadImage(filename);
        cache.put(filename, img);
    }

    /** Trả về ảnh tĩnh tương ứng với tên file. */
    public Image getImage(String filename) {
        return cache.get(filename);
    }

    // Các getter để lấy danh sách sprite tương ứng
    public Map<String, Image> getCache() { return cache; }
    public List<Image> getSilverCrackCache() { return silverCrackCache; }
    public List<Image> getPowerUpCatchCache() { return powerUpCatchCache; }
    public List<Image> getPowerUpExpandCache() { return powerUpExpandCache; }
    public List<Image> getPowerUpLaserCache() { return powerUpLaserCache; }
    public List<Image> getPowerUpDuplicateCache() { return powerUpDuplicateCache; }
    public List<Image> getPowerUpSlowCache() { return powerUpSlowCache; }
    public List<Image> getPowerUPLifeCache() { return powerUPLifeCache; }
    public List<Image> getPowerUpWarpCache() { return powerUpWarpCache; }
    public List<Image> getPaddleWideCache() { return paddleWideCache; }
    public List<Image> getPaddleWidePulsateCache() { return paddleWidePulsateCache; }
    public List<Image> getPaddleLaserCache() { return paddleLaserCache; }
    public List<Image> getPaddleLaserPulsateCache() { return paddleLaserPulsateCache; }
    public List<Image> getPaddlePulsateCache() { return paddlePulsateCache; }
    public List<Image> getPaddleMaterializeCache() { return paddleMaterializeCache; }
    public List<Image> getPaddleExplodeCache() { return paddleExplodeCache; }
    public boolean isInitialized() { return initialized; }
    public int getTotalSprites() { return totalSprites; }
}
