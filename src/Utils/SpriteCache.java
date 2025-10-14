package Utils;

import javafx.scene.image.Image;
import Objects.Bricks.BrickType;
import Objects.PowerUps.PowerUpType;
import Objects.GameEntities.PaddleState;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CACHING LAYER - Singleton cache cho tất cả sprites trong game.
 * 
 * Kiến trúc:
 * AssetLoader (load từ disk) → **SpriteCache** (cache sprites) → AnimationFactory (tạo animations)
 * 
 * Chức năng:
 * - Cache tất cả images trong HashMap để tránh load lặp lại
 * - Gọi AssetLoader để load sprites từ disk
 * - Cung cấp API tiện lợi để lấy sprites theo type
 * 
 * Performance:
 * - Load time < 3 giây cho toàn bộ assets
 * - Không memory leak (images được reuse)
 * 
 * @author SteveHoang aka BoizSocSon
 */
public final class SpriteCache {
    private static SpriteCache instance;
    private final Map<String, Image> cache;
    private boolean initialized = false;
    
    private static final String BASE_PATH = Constants.Paths.GRAPHICS_PATH;
    
    /**
     * Private constructor cho singleton pattern.
     */
    private SpriteCache() {
        cache = new HashMap<>();
    }
    
    /**
     * Lấy instance duy nhất của SpriteCache.
     */
    public static synchronized SpriteCache getInstance() {
        if (instance == null) {
            instance = new SpriteCache();
        }
        return instance;
    }
    
    /**
     * Initialize cache - load tất cả sprites vào memory.
     * 
     * Load order:
     * 1. Brick sprites (10 types × 10 crack frames + 1 normal = 11 per silver, 1 per others)
     * 2. PowerUp sprites (7 types × 8 frames = 56 images)
     * 3. Paddle sprites (6 states, tổng ~53 frames)
     * 4. Laser sprites
     * 5. UI sprites
     * 
     * @throws RuntimeException nếu critical sprites không load được
     */
    public void initialize() {
        if (initialized) {
            System.out.println("SpriteCache: Already initialized, skipping...");
            return;
        }
        
        long startTime = System.currentTimeMillis();
        System.out.println("SpriteCache: Initializing...");
        
        // Load brick sprites
        loadBrickSprites();
        
        // Load powerup sprites
        loadPowerUpSprites();
        
        // Load paddle sprites
        loadPaddleSprites();
        
        // Load laser sprites
        loadLaserSprites();
        
        // Load UI sprites (optional)
        loadUISprites();
        
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.printf("SpriteCache: Loaded %d sprites in %d ms%n", cache.size(), elapsed);
        
        initialized = true;
    }
    
    /**
     * Load tất cả brick sprites.
     */
    private void loadBrickSprites() {
        // Load brick sprites cho tất cả BrickType
        for (BrickType type : BrickType.values()) {
            String filename = type.getSpriteName();
            loadImage(filename);
        }
        
        // Load crack animation frames cho silver brick
        for (int i = 1; i <= 10; i++) {
            loadImage("brick_silver_" + i + ".png");
        }
    }
    
    /**
     * Load tất cả powerup sprites.
     */
    private void loadPowerUpSprites() {
        for (PowerUpType type : PowerUpType.values()) {
            String prefix = type.getSpritePrefix();
            for (int i = 1; i <= 8; i++) {
                loadImage(prefix + "_" + i + ".png");
            }
        }
    }
    
    /**
     * Load tất cả paddle sprites.
     */
    private void loadPaddleSprites() {
        // NORMAL: paddle.png (static)
        loadImage("paddle.png");
        
        // WIDE: paddle_wide_1.png...9.png
        for (int i = 1; i <= 9; i++) {
            loadImage("paddle_wide_" + i + ".png");
        }
        
        // LASER: paddle_laser_1.png...16.png
        for (int i = 1; i <= 16; i++) {
            loadImage("paddle_laser_" + i + ".png");
        }
        
        // PULSATE: paddle_pulsate_1.png...4.png
        for (int i = 1; i <= 4; i++) {
            loadImage("paddle_pulsate_" + i + ".png");
        }
        
        // MATERIALIZE: paddle_materialize_1.png...15.png
        for (int i = 1; i <= 15; i++) {
            loadImage("paddle_materialize_" + i + ".png");
        }
        
        // EXPLODE: paddle_explode_1.png...8.png
        for (int i = 1; i <= 8; i++) {
            loadImage("paddle_explode_" + i + ".png");
        }
    }
    
    /**
     * Load laser sprites.
     */
    private void loadLaserSprites() {
        loadImage("laser_bullet.png");
    }
    
    /**
     * Load UI sprites (optional, không critical).
     */
    private void loadUISprites() {
        // TODO: Load menu, HUD, button sprites nếu có
        // Không throw exception nếu missing
    }
    
    /**
     * Load một image và cache lại.
     * 
     * Workflow: SpriteCache gọi → AssetLoader.loadImage() → trả về Image → cache
     * 
     * @param filename tên file (relative to GRAPHICS_PATH)
     */
    private void loadImage(String filename) {
        if (cache.containsKey(filename)) {
            return; // Đã có trong cache
        }
        
        // GỌI AssetLoader để load image từ disk
        Image image = AssetLoader.loadImage(filename);
        
        // Cache lại kết quả (kể cả placeholder nếu load failed)
        cache.put(filename, image);
    }
    
    /**
     * Tạo placeholder image 1x1 pixel.
     * Deprecated - sử dụng AssetLoader.createPlaceholderImage() thay thế
     */
    @Deprecated
    private Image createPlaceholder() {
        return new javafx.scene.image.WritableImage(1, 1);
    }
    
    /**
     * Lấy image theo filename.
     * 
     * @param filename tên file (ví dụ: "paddle.png")
     * @return Image hoặc placeholder nếu không tìm thấy
     */
    public Image get(String filename) {
        if (!initialized) {
            System.err.println("SpriteCache: Warning - Not initialized! Call initialize() first.");
            initialize();
        }
        
        Image img = cache.get(filename);
        if (img == null) {
            System.err.println("SpriteCache: Image not found in cache: " + filename);
            // Try load on demand
            loadImage(filename);
            img = cache.get(filename);
        }
        return img;
    }
    
    /**
     * Lấy sprite cho brick type.
     * 
     * @param type loại brick
     * @return Image của brick
     */
    public static Image getBrickSprite(BrickType type) {
        return getInstance().get(type.getSpriteName());
    }
    
    /**
     * Lấy tất cả frames cho powerup animation.
     * 
     * @param type loại powerup
     * @return List các Image frames (8 frames)
     */
    public static List<Image> getPowerUpFrames(PowerUpType type) {
        List<Image> frames = new ArrayList<>();
        String prefix = type.getSpritePrefix();
        
        for (int i = 1; i <= 8; i++) {
            String filename = prefix + "_" + i + ".png";
            frames.add(getInstance().get(filename));
        }
        
        return frames;
    }
    
    /**
     * Lấy tất cả frames cho paddle animation.
     * 
     * @param state trạng thái paddle
     * @return List các Image frames
     */
    public static List<Image> getPaddleFrames(PaddleState state) {
        List<Image> frames = new ArrayList<>();
        SpriteCache cache = getInstance();
        
        switch (state) {
            case NORMAL:
                frames.add(cache.get("paddle.png"));
                break;
                
            case WIDE:
                for (int i = 1; i <= 9; i++) {
                    frames.add(cache.get("paddle_wide_" + i + ".png"));
                }
                break;
                
            case LASER:
                for (int i = 1; i <= 16; i++) {
                    frames.add(cache.get("paddle_laser_" + i + ".png"));
                }
                break;
                
            case PULSATE:
                for (int i = 1; i <= 4; i++) {
                    frames.add(cache.get("paddle_pulsate_" + i + ".png"));
                }
                break;
                
            case MATERIALIZE:
                for (int i = 1; i <= 15; i++) {
                    frames.add(cache.get("paddle_materialize_" + i + ".png"));
                }
                break;
                
            case EXPLODE:
                for (int i = 1; i <= 8; i++) {
                    frames.add(cache.get("paddle_explode_" + i + ".png"));
                }
                break;
        }
        
        return frames;
    }
    
    /**
     * Lấy crack animation frames cho silver brick.
     * 
     * @return List 10 frames
     */
    public static List<Image> getSilverBrickCrackFrames() {
        List<Image> frames = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            frames.add(getInstance().get("brick_silver_" + i + ".png"));
        }
        return frames;
    }
    
    /**
     * Clear cache (dùng để testing hoặc reload assets).
     */
    public void clear() {
        cache.clear();
        initialized = false;
        System.out.println("SpriteCache: Cache cleared");
    }
    
    /**
     * Lấy thông tin về cache.
     */
    public void printStats() {
        System.out.println("=== SpriteCache Stats ===");
        System.out.println("Initialized: " + initialized);
        System.out.println("Cached images: " + cache.size());
        System.out.println("Memory usage (approx): " + estimateMemoryUsage() + " MB");
    }
    
    /**
     * Ước lượng memory usage (rough estimate).
     */
    private double estimateMemoryUsage() {
        long totalPixels = 0;
        for (Image img : cache.values()) {
            totalPixels += (long)(img.getWidth() * img.getHeight());
        }
        // Giả sử 4 bytes per pixel (RGBA)
        return (totalPixels * 4) / (1024.0 * 1024.0);
    }
}
