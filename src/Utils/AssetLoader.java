package Utils;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Font;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp tiện ích (Utility class) chịu trách nhiệm tải tất cả các tài nguyên (assets)
 * như hình ảnh, phông chữ và âm thanh từ hệ thống file của ứng dụng.
 * Lớp này sử dụng ClassLoader để truy cập tài nguyên.
 */
public final class AssetLoader {

    /**
     * Constructor private để ngăn việc tạo ra các instance của lớp tiện ích này.
     *
     * @throws UnsupportedOperationException Luôn ném ngoại lệ vì đây là lớp tiện ích.
     */
    private AssetLoader() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    // ==================== IMAGE LOADING ====================

    /**
     * Tải một hình ảnh (Image) dựa trên tên file, sử dụng đường dẫn đồ họa mặc định.
     *
     * @param filename Tên file ảnh (ví dụ: "brick.png").
     * @return Đối tượng {@link Image} đã được tải, hoặc một ảnh giữ chỗ (placeholder) nếu thất bại.
     */
    public static Image loadImage(String filename) {
        // Xây dựng đường dẫn đầy đủ tới tài nguyên ảnh.
        String path = Constants.Paths.GRAPHICS_PATH + filename;
        return loadImageFromPath(path);
    }

    /**
     * Tải hình ảnh từ một đường dẫn tài nguyên cụ thể.
     *
     * @param path Đường dẫn đầy đủ của tài nguyên ảnh.
     * @return Đối tượng {@link Image} đã được tải, hoặc ảnh giữ chỗ nếu có lỗi.
     */
    private static Image loadImageFromPath(String path) {
        // Sử dụng try-with-resources để đảm bảo InputStream được đóng.
        try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
            // Kiểm tra xem file có tồn tại hay không.
            if (is == null) {
                System.err.println("Error: Image file not found - " + path);
                return createPlaceholderImage(); // Trả về ảnh giữ chỗ nếu không tìm thấy file.
            }

            // Tạo đối tượng Image từ InputStream.
            Image image = new Image(is);

            // Kiểm tra nếu việc tải Image gặp lỗi (ví dụ: định dạng không hợp lệ).
            if(image.isError()) {
                System.err.println(); // In dòng trống để tách lỗi nếu cần.
                return createPlaceholderImage(); // Trả về ảnh giữ chỗ.
            }

            return image;

        } catch (IOException e) {
            // Xử lý lỗi I/O trong quá trình đọc file.
            System.err.println("AssetLoader: IOException loading image: " + path);
            e.printStackTrace();
            return createPlaceholderImage();
        } catch (IllegalArgumentException e) {
            // Xử lý lỗi định dạng ảnh không hợp lệ.
            System.err.println("AssetLoader: Invalid image format: " + path);
            e.printStackTrace();
            return createPlaceholderImage();
        } catch (Exception e) {
            // Xử lý các lỗi bất ngờ khác.
            System.err.println("AssetLoader: Unexpected error loading image: " + path);
            e.printStackTrace();
            return createPlaceholderImage();
        }
    }

    /**
     * Tải một chuỗi các hình ảnh (image sequence) theo một mẫu tên file.
     * Hữu ích cho việc tải các khung hình của animation.
     *
     * @param patternWithPercentD Mẫu tên file chứa "%d" (ví dụ: "frame_%d.png").
     * @param from Số bắt đầu của chuỗi.
     * @param to Số kết thúc của chuỗi.
     * @return Một {@link List} chứa các đối tượng {@link Image} đã được tải.
     */
    public static List<Image> loadImageSequence(String patternWithPercentD, int from, int to) {
        List<Image> images = new ArrayList<>();
        // Lặp qua các chỉ số từ 'from' đến 'to'.
        for (int i = from; i <= to; i++) {
            // Định dạng tên file bằng cách thay "%d" bằng chỉ số hiện tại 'i'.
            String filename = String.format(patternWithPercentD, i);
            // Tải ảnh và thêm vào danh sách.
            Image tempImage = loadImage(filename);
            images.add(tempImage);
        }

        return images;
    }

    /**
     * Tạo một hình ảnh giữ chỗ (placeholder) nhỏ, trống để thay thế cho
     * các hình ảnh bị lỗi tải.
     *
     * @return Một {@link WritableImage} 50x50 trống.
     */
    private static Image createPlaceholderImage() {
        // Tạo một ảnh trống 50x50.
        WritableImage placeholder = new WritableImage(50, 50);
        return placeholder;
    }

    // ==================== FONT LOADING ====================

    /**
     * Tải một phông chữ (Font) từ file và trả về nó với kích thước chỉ định.
     *
     * @param filename Tên file phông chữ (ví dụ: "game_font.ttf").
     * @param size Kích thước phông chữ (size in points).
     * @return Đối tượng {@link Font} đã được tải, hoặc phông chữ "Arial" mặc định nếu thất bại.
     */
    public static Font loadFont(String filename, int size) {
        // Xây dựng đường dẫn đầy đủ tới tài nguyên phông chữ.
        String path = Constants.Paths.FONTS_PATH + filename;
        try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
            // Kiểm tra nếu file phông chữ không được tìm thấy.
            if (is == null) {
                System.err.println("AssetLoader: Font not found: " + path);
                // Trả về phông chữ mặc định "Arial".
                return Font.font("Arial", size);
            }

            // Tải phông chữ từ InputStream với kích thước đã cho.
            Font font = Font.loadFont(is, size);
            // Kiểm tra xem việc tải phông chữ có thành công không.
            if (font == null) {
                System.err.println("AssetLoader: Failed to load font: " + path);
                // Trả về phông chữ mặc định "Arial" nếu tải thất bại.
                return Font.font("Arial", size);
            }

            System.out.println("AssetLoader: Loaded font: " + filename + " (" + size + "pt)");
            return font;
        } catch (IOException e) {
            // Xử lý lỗi I/O trong quá trình đọc file.
            System.err.println("AssetLoader: IOException loading font: " + path);
            e.printStackTrace();
            return Font.font("Arial", size);
        } catch (Exception e) {
            // Xử lý các lỗi bất ngờ khác.
            System.err.println("AssetLoader: Unexpected error loading font: " + path);
            e.printStackTrace();
            return Font.font("Arial", size);
        }
    }

    // ==================== AUDIO LOADING ====================

    /**
     * Tải một file nhạc nền và tạo ra một {@link MediaPlayer} để phát.
     *
     * @param track Tên file nhạc (ví dụ: "background.mp3").
     * @param loop Có lặp lại nhạc nền vô hạn hay không.
     * @param volume Âm lượng phát lại (từ 0.0 đến 1.0).
     * @return Đối tượng {@link MediaPlayer} đã được cấu hình, hoặc {@code null} nếu lỗi.
     */
    public static MediaPlayer loadBackgroundMusic(String track, boolean loop, double volume) {
        // Xây dựng đường dẫn đầy đủ tới tài nguyên âm thanh.
        String path = Constants.Paths.AUDIO_PATH + track;
        // Mặc dù MediaPlayer không yêu cầu InputStream, vẫn dùng try-with-resources để kiểm tra sự tồn tại của file.
        try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
            // Kiểm tra xem file nhạc có tồn tại hay không.
            if (is == null) {
                System.err.println("AssetLoader: Music file not found: " + path);
                return null;
            }

            // Tạo đối tượng Media sử dụng URL bên ngoài của tài nguyên.
            Media media = new Media(AssetLoader.class.getResource(path).toExternalForm());
            // Tạo MediaPlayer từ đối tượng Media.
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volume);
            // Thiết lập lặp lại vô hạn nếu cờ 'loop' là true.
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