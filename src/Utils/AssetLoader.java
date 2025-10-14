package Utils;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Font;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * BASE LAYER - Tổng quát hóa việc load tất cả loại assets từ Resources.
 * 
 * Kiến trúc phân tầng:
 * AssetLoader (load từ disk) → SpriteCache (cache sprites) → AnimationFactory (tạo animations)
 * 
 * Chức năng:
 * - Load images (sprites) từ Resources/Graphics
 * - Load fonts từ Resources/Fonts
 * - Load audio từ Resources/Audio
 * - Validate tất cả resources có tồn tại
 * - Handle exceptions gracefully với fallback
 * 
 * @author SteveHoang aka BoizSocSon
 */
public final class AssetLoader {
    
    // Danh sách fonts cần thiết cho game
    private static final String[] REQUIRED_FONTS = {
        "emulogic.ttf",      // Retro pixel font
        "generation.ttf",    // Modern gaming font
        "optimus.otf"        // Title/header font
    };

    // Danh sách sprites quan trọng
    private static final String[] CRITICAL_SPRITES = {
        "ball.png",
        "brick_silver.png",
        "edge_left.png",
        "edge_right.png",
        "edge_top.png",
        "laser_bullet.png",
        "logo.png",
        "paddle.png",
    };

    private static final String[] AUDIO_FILES = {
        "Game_Over_background_music.mp3",
        "Main_Menu_background_music.mp3",
        "Rounds_background_music.mp3",
        "Winer_Menu_background_music.mp3"
    };
    
    private AssetLoader() {
        throw new UnsupportedOperationException("Utility class - cannot instantiate");
    }
    
    // ==================== IMAGE LOADING ====================
    
    /**
     * Load một image từ Graphics folder.
     * 
     * @param filename tên file (ví dụ: "paddle.png")
     * @return Image object hoặc placeholder nếu failed
     */
    public static Image loadImage(String filename) {
        String path = Constants.Paths.GRAPHICS_PATH + filename;
        return loadImageFromPath(path);
    }
    
    /**
     * Load image từ đường dẫn tùy chỉnh.
     * 
     * @param path đường dẫn đầy đủ (ví dụ: "/Resources/Graphics/paddle.png")
     * @return Image object hoặc placeholder nếu failed
     */
    public static Image loadImageFromPath(String path) {
        try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("AssetLoader: Image not found: " + path);
                return createPlaceholderImage();
            }
            
            Image image = new Image(is);
            if (image.isError()) {
                System.err.println("AssetLoader: Image loading error: " + path);
                return createPlaceholderImage();
            }
            
            return image;
            
        } catch (IOException e) {
            System.err.println("AssetLoader: IOException loading image: " + path);
            e.printStackTrace();
            return createPlaceholderImage();
        } catch (IllegalArgumentException e) {
            System.err.println("AssetLoader: Invalid image format: " + path);
            e.printStackTrace();
            return createPlaceholderImage();
        } catch (Exception e) {
            System.err.println("AssetLoader: Unexpected error loading image: " + path);
            e.printStackTrace();
            return createPlaceholderImage();
        }
    }
    
    /**
     * Load nhiều images theo pattern với số thứ tự.
     * 
     * @param patternWithPercentD pattern chứa %d (ví dụ: "brick_%d.png")
     * @param from số bắt đầu
     * @param to số kết thúc
     * @return List các Image objects
     */
    public static List<Image> loadImageSequence(String patternWithPercentD, int from, int to) {
        List<Image> images = new ArrayList<>();
        for (int i = from; i <= to; i++) {
            String filename = String.format(patternWithPercentD, i);
            images.add(loadImage(filename));
        }
        return images;
    }
    
    /**
     * Tạo placeholder image 1x1 pixel khi load failed.
     */
    private static Image createPlaceholderImage() {
        return new WritableImage(1, 1);
    }
    
    // ==================== FONT LOADING ====================
    
    /**
     * Load font từ Resources/Fonts với size chỉ định.
     * 
     * @param filename tên file font (ví dụ: "emulogic.ttf")
     * @param size font size
     * @return Font object hoặc default font nếu load failed
     */
    public static Font loadFont(String filename, int size) {
        String path = Constants.Paths.FONTS_PATH + filename;
        
        try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("AssetLoader: Font not found: " + path);
                return Font.font("Arial", size); // Fallback
            }
            
            Font font = Font.loadFont(is, size);
            if (font == null) {
                System.err.println("AssetLoader: Failed to load font: " + path);
                return Font.font("Arial", size);
            }
            
            System.out.println("AssetLoader: Loaded font: " + filename + " (" + size + "pt)");
            return font;
            
        } catch (Exception e) {
            System.err.println("AssetLoader: Error loading font: " + path);
            e.printStackTrace();
            return Font.font("Arial", size);
        }
    }
    
    /**
     * Validate tất cả assets có tồn tại.
     * 
     * Kiểm tra:
     * - Fonts (3 fonts: emulogic, generation, optimus)
     * - Critical sprites (paddle, bricks, powerups)
     * - Audio files (nếu có)
     * 
     * @return List các files bị missing
     */
    public static List<String> validateAllAssets() {
        List<String> missingFiles = new ArrayList<>();

        System.out.println("AssetLoader: Validating assets...");

        // Validate fonts
        for (String fontFile : REQUIRED_FONTS) {
            if (!resourceExists(Constants.Paths.FONTS_PATH + fontFile)) {
                missingFiles.add("Font: " + fontFile);
            }
        }

        // Validate critical sprites
        for (String spriteFile : CRITICAL_SPRITES) {
            if (!resourceExists(Constants.Paths.GRAPHICS_PATH + spriteFile)) {
                missingFiles.add("Sprite: " + spriteFile);
            }
        }

        for (String audioFile : AUDIO_FILES) {
            if (!resourceExists(Constants.Paths.AUDIO_PATH + audioFile)) {
                missingFiles.add("Audio: " + audioFile);
            }
        }

        // Print results
        if (missingFiles.isEmpty()) {
            System.out.println("AssetLoader: ✓ All assets validated successfully");
        } else {
            System.err.println("AssetLoader: ✗ Missing " + missingFiles.size() + " assets:");
            for (String missing : missingFiles) {
                System.err.println("  - " + missing);
            }
        }

        return missingFiles;
    }
    
    /**
     * Kiểm tra resource có tồn tại không.
     * 
     * @param path đường dẫn tương đối (ví dụ: "/Resources/Fonts/emulogic.ttf")
     * @return true nếu tồn tại
     */
    private static boolean resourceExists(String path) {
        try {
            URL url = AssetLoader.class.getResource(path);
            if (url != null) {
                return true;
            }
            
            // Try as stream
            try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
                return is != null;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Lấy URL của resource.
     * 
     * @param filename tên file (relative path trong Resources)
     * @return URL hoặc null nếu không tìm thấy
     */
    public static URL getResourcePath(String filename) {
        // Try different base paths
        String[] basePaths = {
            Constants.Paths.FONTS_PATH,
            Constants.Paths.AUDIO_PATH,
            Constants.Paths.RESOURCES_PATH
        };
        
        for (String basePath : basePaths) {
            URL url = AssetLoader.class.getResource(basePath + filename);
            if (url != null) {
                return url;
            }
        }
        
        // Try direct path
        URL url = AssetLoader.class.getResource(filename);
        if (url != null) {
            return url;
        }
        
        System.err.println("AssetLoader: Resource not found: " + filename);
        return null;
    }
    
    /**
     * Load audio metadata (placeholder - sẽ implement khi có AudioManager).
     * 
     * @param filename tên file audio
     * @return true nếu file tồn tại
     */
    public static boolean validateAudioFile(String filename) {
        String path = Constants.Paths.AUDIO_PATH + filename;
        return resourceExists(path);
    }
    
    /**
     * Pre-load tất cả fonts với các sizes phổ biến.
     * 
     * Gọi method này khi khởi tạo game để tránh lag khi render lần đầu.
     */
    public static void preloadFonts() {
        System.out.println("AssetLoader: Preloading fonts...");
        
        // Load emulogic (retro font) - sizes for body text
        loadFont("emulogic.ttf", 8);
        loadFont("emulogic.ttf", 10);
        loadFont("emulogic.ttf", 12);
        loadFont("emulogic.ttf", 14);
        
        // Load generation (modern font) - sizes for menus
        loadFont("generation.ttf", 16);
        loadFont("generation.ttf", 20);
        loadFont("generation.ttf", 24);
        
        // Load optimus (title font) - sizes for headers
        loadFont("optimus.otf", 32);
        loadFont("optimus.otf", 48);
        loadFont("optimus.otf", 64);
        
        System.out.println("AssetLoader: Fonts preloaded");
    }
    
    /**
     * Handle exception khi load assets.
     * 
     * @param e exception
     * @param assetType loại asset (ví dụ: "Font", "Sprite")
     * @param filename tên file
     */
    private static void handleLoadException(Exception e, String assetType, String filename) {
        System.err.println("AssetLoader: Failed to load " + assetType + ": " + filename);
        System.err.println("  Reason: " + e.getMessage());
        
        // Log to file if needed
        // TODO: Implement file logging
        
        // Don't throw - return fallback instead để game không crash
    }
    
    /**
     * Get input stream for resource.
     * 
     * @param path đường dẫn resource
     * @return InputStream hoặc null
     */
    public static InputStream getResourceStream(String path) {
        try {
            InputStream is = AssetLoader.class.getResourceAsStream(path);
            if (is == null) {
                System.err.println("AssetLoader: Cannot open stream for: " + path);
            }
            return is;
        } catch (Exception e) {
            System.err.println("AssetLoader: Error opening stream: " + path);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Print asset loading summary.
     */
    public static void printAssetSummary() {
        System.out.println("=== Asset Loading Summary ===");

        // Validate và in kết quả
        List<String> missing = validateAllAssets();

        System.out.println("Required fonts: " + REQUIRED_FONTS.length);
        System.out.println("Critical sprites: " + CRITICAL_SPRITES.length);
        System.out.println("Missing assets: " + missing.size());

        if (missing.isEmpty()) {
            System.out.println("Status: ✓ Ready to play");
        } else {
            System.err.println("Status: ✗ Some assets missing (using fallbacks)");
        }
    }
}
