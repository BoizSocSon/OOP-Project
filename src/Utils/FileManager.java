package Utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.List; // Thêm import List

/**
 * Lớp tiện ích (Utility class) chịu trách nhiệm quản lý việc đọc/ghi file
 * cấu hình và dữ liệu game (ví dụ: điểm cao, cài đặt âm thanh) một cách an toàn
 * và độc lập với hệ điều hành. Dữ liệu được lưu trong thư mục ẩn của ứng dụng
 * tại thư mục home của người dùng.
 */
public final class FileManager {

    // Tên thư mục ứng dụng (ẩn) sẽ được tạo trong thư mục home của người dùng.
    private static final String APP_DIR_NAME = ".arkanoid";
    // Đường dẫn đầy đủ đến thư mục ứng dụng (.arkanoid)
    private static final Path APP_DIR = Paths.get(System.getProperty("user.home"), APP_DIR_NAME);
    // Đường dẫn đầy đủ đến file lưu điểm cao nhất.
    private static final Path HIGHSCORE_FILE = APP_DIR.resolve(Constants.Paths.HIGHSCORE_FILE);
    // Tên file lưu cài đặt âm thanh.
    private static final String AUDIO_SETTINGS_FILE = "audio_settings.dat";

    // Đối tượng khóa (lock) để đồng bộ hóa (synchronization) các thao tác đọc/ghi file.
    private static final Object LOCK = new Object();

    /**
     * Constructor private để ngăn việc tạo ra các instance của lớp tiện ích này.
     *
     * @throws UnsupportedOperationException Luôn ném ngoại lệ vì đây là lớp tiện ích.
     */
    private FileManager() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Tải và trả về điểm cao nhất đã được lưu.
     *
     * @return Điểm cao nhất đã lưu, hoặc 0 nếu không tìm thấy file, file bị hỏng hoặc lỗi đọc.
     */
    public static int loadHighscore() {
        // Đồng bộ hóa để đảm bảo chỉ có một luồng thực hiện việc đọc file tại một thời điểm.
        synchronized (LOCK) {
            try {
                // Đảm bảo thư mục ứng dụng đã tồn tại.
                ensureAppDirExists();
                // Nếu file điểm cao chưa tồn tại, trả về 0.
                if (!Files.exists(HIGHSCORE_FILE)) {
                    return 0;
                }
                // Đọc toàn bộ nội dung file điểm cao dưới dạng chuỗi và loại bỏ khoảng trắng.
                String s = Files.readString(HIGHSCORE_FILE).trim();
                // Nếu file trống, trả về 0.
                if (s.isEmpty()) {
                    return 0;
                }
                try {
                    // Chuyển chuỗi thành số nguyên.
                    int v = Integer.parseInt(s);
                    // Đảm bảo điểm không âm và trả về.
                    return Math.max(0, v);
                } catch (NumberFormatException ex) {
                    // Xử lý lỗi nếu nội dung file không phải là số hợp lệ.
                    System.err.println("Filemanager: highscore corrupt - returning default. (" + ex.getMessage() + ")");
                    return 0;
                }
            } catch (IOException ex) {
                // Xử lý lỗi I/O trong quá trình đọc file.
                System.err.println("Filemanager: failed to read highscore - " + ex.getMessage());
                return 0;
            }
        }
    }

    /**
     * Lưu điểm số cao nhất mới vào file.
     *
     * @param score Điểm số cần lưu.
     */
    public static void saveHighscore(int score) {
        // Đồng bộ hóa thao tác ghi file.
        synchronized (LOCK) {
            try {
                // Đảm bảo thư mục ứng dụng đã tồn tại.
                ensureAppDirExists();
                // Chuẩn bị nội dung là điểm số (không âm) dưới dạng chuỗi.
                String content = String.valueOf(Math.max(0, score));
                // Ghi nội dung vào file điểm cao một cách nguyên tử (atomic).
                writeFileAtomic(HIGHSCORE_FILE, content.getBytes());
            } catch (IOException ex) {
                // Xử lý lỗi I/O trong quá trình ghi file.
                System.err.println("Filemanager: failed to save highscore - " + ex.getMessage());
                // Hiển thị hộp thoại cảnh báo cho người dùng về lỗi ghi file.
                showWriteErrorDialog("Lưu điểm cao nhất thất bại:\n" + ex.getMessage());
            }
        }
    }

    /**
     * Đảm bảo rằng thư mục ứng dụng đã tồn tại. Nếu chưa, tạo thư mục.
     *
     * @throws IOException Nếu xảy ra lỗi khi tạo thư mục.
     */
    private static void ensureAppDirExists() throws IOException {
        // Kiểm tra nếu thư mục ứng dụng chưa tồn tại.
        if (!Files.exists(APP_DIR)) {
            // Tạo thư mục (và các thư mục cha nếu cần).
            Files.createDirectories(APP_DIR);
        }
    }

    /**
     * Ghi dữ liệu vào file một cách nguyên tử (atomic write) bằng cách
     * sử dụng file tạm thời và sau đó đổi tên file.
     *
     * @param target Đường dẫn đến file đích.
     * @param data Mảng byte chứa dữ liệu cần ghi.
     * @throws IOException Nếu xảy ra lỗi I/O trong quá trình ghi hoặc di chuyển file.
     */
    private static void writeFileAtomic(Path target, byte[] data) throws IOException {
        // Tạo file tạm thời trong thư mục ứng dụng.
        Path tmp = Files.createTempFile(APP_DIR, "tmp", ".tmp");
        // Ghi dữ liệu vào file tạm thời.
        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(tmp, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING))) {
            out.write(data);
            out.flush(); // Đảm bảo dữ liệu được ghi xuống đĩa trước khi đổi tên.
        }
        try {
            // Cố gắng di chuyển (đổi tên) file tạm thời sang file đích một cách nguyên tử.
            Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            // Nếu di chuyển nguyên tử không được hỗ trợ, thực hiện di chuyển/thay thế bình thường.
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Hiển thị hộp thoại cảnh báo lỗi ghi file trên luồng ứng dụng JavaFX (JavaFX Application Thread).
     *
     * @param message Nội dung thông báo lỗi.
     */
    private static void showWriteErrorDialog(String message) {
        try {
            // Kiểm tra xem luồng hiện tại có phải là luồng JavaFX không.
            if (Platform.isFxApplicationThread()) {
                // Nếu đúng, tạo và hiển thị hộp thoại ngay lập tức.
                Alert a = new Alert(AlertType.WARNING);
                a.setTitle("Lưu thất bại");
                a.setHeaderText(null);
                a.setContentText(message);
                a.show();
            } else {
                // Nếu không, chuyển việc tạo và hiển thị hộp thoại sang luồng JavaFX.
                Platform.runLater(() -> {
                    Alert a = new Alert(AlertType.WARNING);
                    a.setTitle("Lưu thất bại");
                    a.setHeaderText(null);
                    a.setContentText(message);
                    a.show();
                });
            }
        } catch (Throwable t) {
            // Xử lý lỗi nếu không thể hiển thị hộp thoại (ví dụ: môi trường không có JavaFX UI).
            System.err.println("Filemanager: cannot show dialog - " + t.getMessage());
        }
    }

    /**
     * Đọc tất cả các dòng từ một file cấu hình nằm trong thư mục ứng dụng.
     *
     * @param filename Tên file cần đọc (nằm trong thư mục ứng dụng).
     * @return Một {@link List} chứa các dòng đã đọc, hoặc {@code null} nếu file không tồn tại hoặc lỗi đọc.
     */
    public static List<String> readLinesFromFile(String filename) {
        // Đồng bộ hóa thao tác đọc file.
        synchronized (LOCK) {
            try {
                // Đảm bảo thư mục ứng dụng đã tồn tại.
                ensureAppDirExists();
                // Xây dựng đường dẫn đầy đủ đến file.
                Path filePath = APP_DIR.resolve(filename);

                // Nếu file không tồn tại, trả về null.
                if (!Files.exists(filePath)) {
                    return null;
                }

                // Đọc tất cả các dòng từ file.
                return Files.readAllLines(filePath);
            } catch (IOException ex) {
                // Xử lý lỗi I/O.
                System.err.println("FileManager: failed to read file " + filename + " - " + ex.getMessage());
                return null;
            }
        }
    }

    /**
     * Ghi một danh sách các dòng vào file cấu hình trong thư mục ứng dụng.
     *
     * @param filename Tên file cần ghi.
     * @param lines Danh sách các chuỗi (dòng) cần ghi vào file.
     */
    public static void writeLinesToFile(String filename, List<String> lines) {
        // Đồng bộ hóa thao tác ghi file.
        synchronized (LOCK) {
            try {
                // Đảm bảo thư mục ứng dụng đã tồn tại.
                ensureAppDirExists();
                // Xây dựng đường dẫn đầy đủ đến file.
                Path filePath = APP_DIR.resolve(filename);

                // Tạo content từ lines, thêm dấu ngắt dòng cho mỗi dòng.
                StringBuilder sb = new StringBuilder();
                for (String line : lines) {
                    sb.append(line).append(System.lineSeparator());
                }

                // Ghi nội dung vào file một cách nguyên tử.
                writeFileAtomic(filePath, sb.toString().getBytes());
            } catch (IOException ex) {
                // Xử lý lỗi I/O trong quá trình ghi file.
                System.err.println("FileManager: failed to write file " + filename + " - " + ex.getMessage());
                // Hiển thị hộp thoại cảnh báo.
                showWriteErrorDialog("Lưu file thất bại:\n" + ex.getMessage());
            }
        }
    }

    /**
     * Tải cài đặt âm thanh (âm lượng và trạng thái tắt tiếng) từ file cấu hình.
     *
     * @return Một mảng {@code double[]} chứa {volume, isMuted (1.0 nếu tắt tiếng, 0.0 nếu bật)},
     * hoặc {@code null} nếu file không tồn tại, bị hỏng hoặc lỗi đọc.
     */
    public static double[] loadAudioSettings() {
        // Đồng bộ hóa thao tác đọc file.
        synchronized (LOCK) {
            try {
                // Đảm bảo thư mục ứng dụng đã tồn tại.
                ensureAppDirExists();
                Path audioFile = APP_DIR.resolve(AUDIO_SETTINGS_FILE);

                // Nếu file không tồn tại, trả về null.
                if (!Files.exists(audioFile)) {
                    return null;
                }

                // Đọc tất cả các dòng.
                List<String> lines = Files.readAllLines(audioFile);
                // Kiểm tra định dạng: phải có ít nhất 2 dòng (volume và muted).
                if (lines.size() < 2) {
                    return null;
                }

                // Chuyển đổi dòng đầu tiên thành âm lượng (double).
                double volume = Double.parseDouble(lines.get(0).trim());
                // Chuyển đổi dòng thứ hai thành trạng thái tắt tiếng (boolean).
                boolean muted = Boolean.parseBoolean(lines.get(1).trim());

                // Trả về mảng double: [âm lượng, trạng thái tắt tiếng (1.0 là tắt, 0.0 là bật)].
                return new double[] { volume, muted ? 1.0 : 0.0 };
            } catch (Exception ex) {
                // Xử lý lỗi chung (I/O hoặc định dạng không hợp lệ).
                System.err.println("FileManager: failed to load audio settings - " + ex.getMessage());
                return null;
            }
        }
    }

    /**
     * Lưu cài đặt âm thanh (âm lượng và trạng thái tắt tiếng) vào file cấu hình.
     *
     * @param volume Âm lượng cần lưu (0.0 đến 1.0).
     * @param isMuted Trạng thái tắt tiếng (true/false).
     */
    public static void saveAudioSettings(double volume, boolean isMuted) {
        // Đồng bộ hóa thao tác ghi file.
        synchronized (LOCK) {
            try {
                // Đảm bảo thư mục ứng dụng đã tồn tại.
                ensureAppDirExists();
                Path audioFile = APP_DIR.resolve(AUDIO_SETTINGS_FILE);

                // Chuẩn bị nội dung để ghi: volume ở dòng 1, isMuted ở dòng 2.
                StringBuilder sb = new StringBuilder();
                sb.append(volume).append(System.lineSeparator());
                sb.append(isMuted).append(System.lineSeparator());

                // Ghi nội dung vào file một cách nguyên tử.
                writeFileAtomic(audioFile, sb.toString().getBytes());
            } catch (IOException ex) {
                // Xử lý lỗi I/O trong quá trình ghi file. (Không hiển thị dialog vì đây là cài đặt nền).
                System.err.println("FileManager: failed to save audio settings - " + ex.getMessage());
            }
        }
    }
}