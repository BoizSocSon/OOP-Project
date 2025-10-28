package Utils;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.text.Font;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp tiện ích chịu trách nhiệm tải tất cả tài nguyên (assets) của game bao gồm:
 * <ul>
 *     <li>Ảnh (Image)</li>
 *     <li>Phông chữ (Font)</li>
 *     <li>Âm thanh và nhạc nền (Audio, Music)</li>
 * </ul>
 *
 * Lớp này là final và có constructor private để đảm bảo không thể tạo instance,
 * chỉ cung cấp các phương thức static để tải tài nguyên khi cần.
 */
public final class AssetLoader {

    private AssetLoader() {
        // Ngăn việc khởi tạo lớp tiện ích
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    // ==================== IMAGE LOADING ====================

    /**
     * Tải một ảnh từ thư mục đồ họa của game.
     *
     * @param filename tên file ảnh (ví dụ "brick.png")
     * @return đối tượng {@link Image}, hoặc ảnh thay thế nếu không tìm thấy file
     */
    public static Image loadImage(String filename) {
        String path = Constants.Paths.GRAPHICS_PATH + filename;
        return loadImageFromPath(path);
    }

    /**
     * Thực hiện tải ảnh từ đường dẫn tuyệt đối bên trong resource.
     * Có xử lý lỗi chi tiết và trả về ảnh mặc định nếu gặp sự cố.
     */
    private static Image loadImageFromPath(String path) {
        try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
            // Kiểm tra xem file có tồn tại không
            if (is == null) {
                System.err.println("Error: Image file not found - " + path);
                return createPlaceholderImage(); // Trả về ảnh trống 50x50
            }

            Image image = new Image(is);

            // Kiểm tra lỗi trong quá trình load (nếu file bị hỏng)
            if (image.isError()) {
                System.err.println();
                return createPlaceholderImage();
            }

            return image;

        } catch (IOException e) {
            // Lỗi khi đọc luồng dữ liệu
            System.err.println("AssetLoader: IOException loading image: " + path);
            e.printStackTrace();
            return createPlaceholderImage();
        } catch (IllegalArgumentException e) {
            // Định dạng file ảnh không hợp lệ
            System.err.println("AssetLoader: Invalid image format: " + path);
            e.printStackTrace();
            return createPlaceholderImage();
        } catch (Exception e) {
            // Lỗi không xác định
            System.err.println("AssetLoader: Unexpected error loading image: " + path);
            e.printStackTrace();
            return createPlaceholderImage();
        }
    }

    /**
     * Tải chuỗi ảnh có định dạng tên theo mẫu (ví dụ: frame_1.png, frame_2.png,...)
     *
     * @param patternWithPercentD mẫu chuỗi chứa %d (ví dụ: "brick_%d.png")
     * @param from chỉ số bắt đầu
     * @param to chỉ số kết thúc
     * @return danh sách ảnh {@link List<Image>} đã tải
     */
    public static List<Image> loadImageSequence(String patternWithPercentD, int from, int to) {
        List<Image> images = new ArrayList<>();
        for (int i = from; i <= to; i++) {
            // Sinh tên file từ mẫu
            String filename = String.format(patternWithPercentD, i);
            Image tempImage = loadImage(filename);
            images.add(tempImage);
        }

        return images;
    }

    /**
     * Tạo một ảnh placeholder (50x50 pixel) dùng khi không tải được ảnh thật.
     *
     * @return ảnh trống
     */
    private static Image createPlaceholderImage() {
        WritableImage placeholder = new WritableImage(50, 50);
        return placeholder;
    }

    // ==================== FONT LOADING ====================

    /**
     * Tải font chữ từ thư mục fonts của game.
     * Nếu không tìm thấy, sử dụng font mặc định "Arial".
     *
     * @param filename tên file font (ví dụ "arcade.ttf")
     * @param size cỡ chữ
     * @return {@link Font} đã tải hoặc font thay thế
     */
    public static Font loadFont(String filename, int size) {
        String path = Constants.Paths.FONTS_PATH + filename;
        try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
            // Nếu không có file font, trả về Arial
            if (is == null) {
                System.err.println("AssetLoader: Font not found: " + path);
                return Font.font("Arial", size);
            }

            Font font = Font.loadFont(is, size);
            if (font == null) {
                // Font không thể load (do lỗi định dạng)
                System.err.println("AssetLoader: Failed to load font: " + path);
                return Font.font("Arial", size);
            }

            System.out.println("AssetLoader: Loaded font: " + filename + " (" + size + "pt)");
            return font;
        } catch (IOException e) {
            // Lỗi khi đọc luồng file font
            System.err.println("AssetLoader: IOException loading font: " + path);
            e.printStackTrace();
            return Font.font("Arial", size);
        } catch (Exception e) {
            // Lỗi không xác định
            System.err.println("AssetLoader: Unexpected error loading font: " + path);
            e.printStackTrace();
            return Font.font("Arial", size);
        }
    }

    // ==================== AUDIO LOADING ====================

    /**
     * Tải nhạc nền từ thư mục audio của game.
     * Cho phép cài đặt chế độ lặp và âm lượng.
     *
     * @param track tên file nhạc (ví dụ "bgm.mp3")
     * @param loop true nếu muốn phát lặp
     * @param volume âm lượng (0.0 → 1.0)
     * @return {@link MediaPlayer} để phát nhạc hoặc null nếu tải lỗi
     */
    public static MediaPlayer loadBackgroundMusic(String track, boolean loop, double volume) {
        String path = Constants.Paths.AUDIO_PATH + track;
        try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
            // Kiểm tra file tồn tại
            if (is == null) {
                System.err.println("AssetLoader: Music file not found: " + path);
                return null;
            }

            // Tạo Media từ đường dẫn resource
            Media media = new Media(AssetLoader.class.getResource(path).toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            // Thiết lập âm lượng và chế độ lặp
            mediaPlayer.setVolume(volume);
            if (loop) {
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
            return mediaPlayer;
        } catch (Exception e) {
            System.err.println("AssetLoader: Error loading music: " + path);
            e.printStackTrace();
            return null;
        }
    }
}
