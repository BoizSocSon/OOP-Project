package Utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;

/**
 * Lớp tiện ích chịu trách nhiệm quản lý việc đọc/ghi dữ liệu ra file,
 * bao gồm highscore và các file cài đặt.
 * Mọi thao tác được thực hiện thread-safe thông qua LOCK.
 */
public final class FileManager {

    // Tên thư mục ứng dụng nằm trong thư mục người dùng
    private static final String APP_DIR_NAME = ".arkanoid";
    // Đường dẫn tuyệt đối đến thư mục ứng dụng
    private static final Path APP_DIR = Paths.get(System.getProperty("user.home"), APP_DIR_NAME);
    // Đường dẫn file lưu điểm cao
    private static final Path HIGHSCORE_FILE = APP_DIR.resolve(Constants.Paths.HIGHSCORE_FILE);

    // Đối tượng khóa dùng để đồng bộ truy cập file giữa các thread
    private static final Object LOCK = new Object();

    // Chặn khởi tạo instance - chỉ dùng static method
    private FileManager() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Đọc điểm cao nhất từ file lưu trữ.
     * Nếu file không tồn tại hoặc bị hỏng -> trả về 0.
     *
     * @return highscore (>=0)
     */
    public static int loadHighscore() {
        synchronized (LOCK) { // Đảm bảo chỉ một thread đọc file tại một thời điểm
            try {
                ensureAppDirExists(); // Tạo thư mục nếu chưa có

                // Nếu file chưa tồn tại, trả về 0
                if (!Files.exists(HIGHSCORE_FILE)) {
                    return 0;
                }

                // Đọc toàn bộ nội dung file và loại bỏ khoảng trắng thừa
                String s = Files.readString(HIGHSCORE_FILE).trim();

                if (s.isEmpty()) {
                    return 0;
                }

                try {
                    int v = Integer.parseInt(s);
                    // Đảm bảo giá trị không âm
                    return Math.max(0, v);
                } catch (NumberFormatException ex) {
                    System.err.println("Filemanager: highscore corrupt - returning default. (" + ex.getMessage() + ")");
                    return 0;
                }
            } catch (IOException ex) {
                System.err.println("Filemanager: failed to read highscore - " + ex.getMessage());
                return 0;
            }
        }
    }

    /**
     * Lưu điểm cao nhất xuống file.
     * Quá trình ghi được thực hiện an toàn bằng cách ghi qua file tạm.
     *
     * @param score giá trị điểm cao nhất (>=0)
     */
    public static void saveHighscore(int score) {
        synchronized (LOCK) { // Đảm bảo chỉ một thread ghi file tại một thời điểm
            try {
                ensureAppDirExists(); // Đảm bảo thư mục tồn tại
                String content = String.valueOf(Math.max(0, score)); // Không cho phép giá trị âm

                // Ghi file an toàn
                writeFileAtomic(HIGHSCORE_FILE, content.getBytes());
            } catch (IOException ex) {
                System.err.println("Filemanager: failed to save highscore - " + ex.getMessage());
                // Hiển thị cảnh báo nếu lỗi ghi file
                showWriteErrorDialog("Lưu điểm cao nhất thất bại:\n" + ex.getMessage());
            }
        }
    }

    /**
     * Đảm bảo thư mục ứng dụng tồn tại, nếu chưa thì tạo mới.
     */
    private static void ensureAppDirExists() throws IOException {
        if (!Files.exists(APP_DIR)) {
            Files.createDirectories(APP_DIR); // Tạo tất cả thư mục con nếu cần
        }
    }

    /**
     * Ghi dữ liệu ra file một cách an toàn ("atomic"):
     * Ghi tạm ra file trung gian, sau đó move sang file chính.
     */
    private static void writeFileAtomic(Path target, byte[] data) throws IOException {
        // Tạo file tạm trong thư mục ứng dụng
        Path tmp = Files.createTempFile(APP_DIR, "tmp", ".tmp");

        // Ghi dữ liệu vào file tạm
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(
                tmp, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING))) {
            out.write(data);
            out.flush(); // Đảm bảo toàn bộ dữ liệu đã được ghi
        }

        try {
            // Di chuyển file tạm sang file đích (atomic move)
            Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            // Nếu hệ thống không hỗ trợ move atomic -> move thường
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Hiển thị hộp thoại cảnh báo khi ghi file thất bại.
     * Đảm bảo luôn chạy trên luồng JavaFX Application Thread.
     */
    private static void showWriteErrorDialog(String message) {
        try {
            if (Platform.isFxApplicationThread()) {
                // Nếu đang trên luồng JavaFX, hiển thị trực tiếp
                Alert a = new Alert(AlertType.WARNING);
                a.setTitle("Lưu thất bại");
                a.setHeaderText(null);
                a.setContentText(message);
                a.show();
            } else {
                // Nếu không, gửi yêu cầu hiển thị sang luồng JavaFX
                Platform.runLater(() -> {
                    Alert a = new Alert(AlertType.WARNING);
                    a.setTitle("Lưu thất bại");
                    a.setHeaderText(null);
                    a.setContentText(message);
                    a.show();
                });
            }
        } catch (Throwable t) {
            System.err.println("Filemanager: cannot show dialog - " + t.getMessage());
        }
    }

    /**
     * Đọc toàn bộ nội dung file dưới dạng danh sách dòng.
     *
     * @param filename Tên file (được lưu trong thư mục APP_DIR)
     * @return danh sách các dòng hoặc null nếu lỗi/xảy ra sự cố
     */
    public static java.util.List<String> readLinesFromFile(String filename) {
        synchronized (LOCK) {
            try {
                ensureAppDirExists();
                Path filePath = APP_DIR.resolve(filename);

                // Nếu file chưa tồn tại thì trả về null
                if (!Files.exists(filePath)) {
                    return null;
                }

                // Đọc toàn bộ các dòng trong file
                return Files.readAllLines(filePath);
            } catch (IOException ex) {
                System.err.println("FileManager: failed to read file " + filename + " - " + ex.getMessage());
                return null;
            }
        }
    }

    /**
     * Ghi danh sách các dòng ra file văn bản.
     *
     * @param filename tên file (được lưu trong thư mục APP_DIR)
     * @param lines danh sách dòng cần ghi
     */
    public static void writeLinesToFile(String filename, java.util.List<String> lines) {
        synchronized (LOCK) {
            try {
                ensureAppDirExists();
                Path filePath = APP_DIR.resolve(filename);

                // Ghép các dòng lại thành chuỗi hoàn chỉnh
                StringBuilder sb = new StringBuilder();
                for (String line : lines) {
                    sb.append(line).append(System.lineSeparator());
                }

                // Ghi ra file an toàn
                writeFileAtomic(filePath, sb.toString().getBytes());
            } catch (IOException ex) {
                System.err.println("FileManager: failed to write file " + filename + " - " + ex.getMessage());
                showWriteErrorDialog("Lưu file thất bại:\n" + ex.getMessage());
            }
        }
    }
}
