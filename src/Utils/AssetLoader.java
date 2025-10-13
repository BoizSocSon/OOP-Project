package Utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * AssetLoader (Task 1.2)
 * - Load font từ Resources/Fonts
 * - Trả URL asset (audio/image/...)
 * - Validate các asset "bắt buộc"
 *
 * Thiết kế:
 *  - Dùng ClassLoader để tương thích khi chạy IDE/JAR.
 *  - Graceful handling: thiếu file -> log cảnh báo + fallback, không crash.
 */
public final class AssetLoader {

    // ------- Singleton (tùy chọn) -------
    private static AssetLoader instance;
    public static AssetLoader getInstance() {
        if (instance == null) instance = new AssetLoader();
        return instance;
    }

    // ------- Roots -------
    private final String fontsRoot = "Resources/Fonts/";
    private final String audioRoot = "Resources/Audio/";
    private final String graphicsRoot = "Resources/Graphics/";

    // Cache font theo (filename#size)
    private final Map<String, Font> fontCache = new HashMap<>();

    // Danh sách asset bắt buộc (mặc định theo checklist; có thể truyền vào ctor khác)
    private final List<String> requiredFonts;
    private final List<String> requiredAudio;
    private final List<String> requiredGraphics;

    // ------- Constructors -------
    public AssetLoader() {
        this(
                List.of("emulogic.ttf", "generation.ttf", "optimus.ttf"), // theo đặc tả tuần 1
                List.of("hit.wav", "powerup.wav", "laser.wav"),           // ví dụ; có thể chỉnh theo dự án
                List.of()                                                 // nếu muốn, liệt kê sprite bắt buộc
        );
    }

    public AssetLoader(List<String> requiredFonts,
                       List<String> requiredAudio,
                       List<String> requiredGraphics) {
        this.requiredFonts = new ArrayList<>(requiredFonts);
        this.requiredAudio = new ArrayList<>(requiredAudio);
        this.requiredGraphics = new ArrayList<>(requiredGraphics);
    }

    // ============= API BẮT BUỘC =============

    /**
     * Load font từ Resources/Fonts/<filename> với kích thước size.
     * Thành công -> Font derived.
     * Lỗi/thiếu -> log cảnh báo + trả về SansSerif fallback (không crash).
     */
    public Font loadFont(String filename, int size) {
        String key = filename + "#" + size;
        if (fontCache.containsKey(key)) return fontCache.get(key);

        try (InputStream is = resourceAsStream(fontsRoot + filename)) {
            if (is == null) throw new FileNotFoundException("Missing font: " + fontsRoot + filename);
            Font base = Font.createFont(Font.TRUETYPE_FONT, is);
            Font derived = base.deriveFont((float) size);
            fontCache.put(key, derived);
            return derived;
        } catch (FontFormatException | IOException e) {
            System.err.println("[AssetLoader] Cannot load font '" + filename + "': " + e.getMessage()
                    + " -> using default fallback.");
            Font fallback = new Font("SansSerif", Font.PLAIN, size);
            fontCache.put(key, fallback);
            return fallback;
        }
    }

    /**
     * Trả về URL cho một resource.
     * - Có thể truyền full path kiểu "Resources/Audio/hit.wav"
     * - Hoặc tên file ngắn: sẽ thử Audio -> Graphics -> Fonts
     * Không tìm thấy -> trả null.
     */
    public URL getResourcePath(String filename) {
        URL url = getResource(filename); // trường hợp truyền full path
        if (url != null) return url;

        url = getResource(audioRoot + filename);
        if (url != null) return url;

        url = getResource(graphicsRoot + filename);
        if (url != null) return url;

        url = getResource(fontsRoot + filename);
        return url;
    }

    /**
     * Validate tài nguyên bắt buộc; trả về danh sách file thiếu
     * (đường dẫn tương đối trong thư mục Resources/).
     */
    public List<String> validateAllAssets() {
        List<String> missing = new ArrayList<>();

        for (String f : requiredFonts) {
            if (!exists(fontsRoot + f)) missing.add("Fonts/" + f);
        }
        for (String a : requiredAudio) {
            if (!exists(audioRoot + a)) missing.add("Audio/" + a);
        }
        for (String g : requiredGraphics) {
            if (!exists(graphicsRoot + g)) missing.add("Graphics/" + g);
        }
        return missing;
    }

    // =================== Helpers ===================

    private URL getResource(String path) {
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }
    private InputStream resourceAsStream(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }
    private boolean exists(String path) {
        return getResource(path) != null;
    }

    // (Tuỳ chọn) Cho phép bổ sung danh sách required động
    public void addRequiredFont(String filename)    { requiredFonts.add(filename); }
    public void addRequiredAudio(String filename)   { requiredAudio.add(filename); }
    public void addRequiredGraphic(String filename) { requiredGraphics.add(filename); }

    // Expose roots (nếu cần debug)
    public String getFontsRoot()    { return fontsRoot; }
    public String getAudioRoot()    { return audioRoot; }
    public String getGraphicsRoot() { return graphicsRoot; }
}
