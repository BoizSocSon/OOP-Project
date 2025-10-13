package Utils;

import javafx.scene.image.Image;
import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Task 1.1 – SpriteCache.java
 * Mục tiêu: Cache tất cả sprites trong thư mục src/Resources/Graphics
 * Dùng Singleton pattern để đảm bảo chỉ load 1 lần duy nhất.
 */
public class SpriteCache {

    private static SpriteCache instance;
    private final Map<String, Image> cache = new HashMap<>();

    private SpriteCache() {}

    /** Lấy instance duy nhất của SpriteCache */
    public static SpriteCache getInstance() {
        if (instance == null)
            instance = new SpriteCache();
        return instance;
    }

    /** Load tất cả ảnh từ src/Resources/Graphics */
    public void initialize() {
        if (!cache.isEmpty()) return; // tránh load trùng

        File folder = new File("OOP-Project/src/Resources/Graphics");
        if (!folder.exists()) {
            System.err.println("⚠️ Không tìm thấy thư mục: " + folder.getAbsolutePath());
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            System.err.println("⚠️ Thư mục Graphics rỗng!");
            return;
        }

        // Load ảnh bằng ClassLoader → tránh lỗi URI và tương thích khi build .jar
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".png")) {
                try (InputStream stream = getClass().getResourceAsStream("/Resources/Graphics/" + file.getName())) {
                    Image img;
                    if (stream != null) {
                        img = new Image(stream);
                    } else {
                        // fallback nếu resource chưa được copy vào classpath (chạy trực tiếp trong IDE)
                        img = new Image(file.toURI().toString());
                    }
                    cache.put(file.getName(), img);
                } catch (Exception e) {
                    System.err.println("Lỗi khi load ảnh: " + file.getName() + " → " + e.getMessage());
                }
            }
        }

        System.out.println("✅ SpriteCache load thành công " + cache.size() + " sprites.");
    }

    /** Lấy ảnh theo tên file (ví dụ "brick_blue.png") */
    public Image get(String filename) {
        return cache.get(filename);
    }

    /** Lấy sprite của gạch theo loại (ví dụ "red" → brick_red.png) */
    public Image getBrickSprite(String brickTypeName) {
        return cache.get("brick_" + brickTypeName.toLowerCase() + ".png");
    }

    /** Lấy danh sách frame ảnh PowerUp */
    public List<Image> getPowerUpFrames(String powerUpType) {
        List<Image> frames = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            String name = "powerup_" + powerUpType.toLowerCase() + "_" + i + ".png";
            Image img = cache.get(name);
            if (img != null) frames.add(img);
        }
        return frames;
    }

    /** Lấy danh sách frame ảnh Paddle theo trạng thái */
    public List<Image> getPaddleFrames(String paddleState) {
        List<Image> frames = new ArrayList<>();

        switch (paddleState.toUpperCase()) {
            case "NORMAL" -> frames.add(cache.get("paddle.png"));
            case "WIDE" -> addFrames(frames, "paddle_wide_", 9);
            case "LASER" -> addFrames(frames, "paddle_laser_", 16);
            case "PULSATE" -> addFrames(frames, "paddle_pulsate_", 4);
            case "MATERIALIZE" -> addFrames(frames, "paddle_materialize_", 15);
            case "EXPLODE" -> addFrames(frames, "paddle_explode_", 8);
            default -> System.err.println("⚠️ Trạng thái paddle không hợp lệ: " + paddleState);
        }

        return frames;
    }

    private void addFrames(List<Image> frames, String prefix, int count) {
        for (int i = 1; i <= count; i++) {
            Image img = cache.get(prefix + i + ".png");
            if (img != null) frames.add(img);
        }
    }

    /** Dành cho test */
    public void clear() {
        cache.clear();
    }

    /** Dành cho test */
    public int getSpriteCount() {
        return cache.size();
    }
}
