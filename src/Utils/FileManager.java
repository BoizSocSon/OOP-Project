package Utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;

/**
 * Quản lý I/O cho highscore và settings.
 */
public final class FileManager {

    private static final String APP_DIR_NAME = ".arkanoid";
    private static final Path APP_DIR = Paths.get(System.getProperty("user.home"), APP_DIR_NAME);
    private static final Path HIGHSCORE_FILE = APP_DIR.resolve(Constants.Paths.HIGHSCORE_FILE);
    private static final String AUDIO_SETTINGS_FILE = "audio_settings.dat";

    private static final Object LOCK = new Object();

    private FileManager() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Tải highscore từ file. Nếu file không tồn tại hoặc corrupt trả về 0.
     * @return highscore (>=0)
     */
    public static int loadHighscore() {
        synchronized (LOCK) {
            try {
                ensureAppDirExists();
                if (!Files.exists(HIGHSCORE_FILE)) {
                    return 0;
                }
                String s = Files.readString(HIGHSCORE_FILE).trim();
                if (s.isEmpty()) {
                    return 0;
                }
                try {
                    int v = Integer.parseInt(s);
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
     * Lưu highscore. Ghi an toàn qua file tạm để tránh corrupt.
     * @param score giá trị highscore (>=0)
     */
    public static void saveHighscore(int score) {
        synchronized (LOCK) {
            try {
                ensureAppDirExists();
                String content = String.valueOf(Math.max(0, score));
                writeFileAtomic(HIGHSCORE_FILE, content.getBytes());
            } catch (IOException ex) {
                System.err.println("Filemanager: failed to save highscore - " + ex.getMessage());
                showWriteErrorDialog("Lưu điểm cao nhất thất bại:\n" + ex.getMessage());
            }
        }
    }

    /** Tạo thư mục ứng dụng nếu chưa tồn tại */
    private static void ensureAppDirExists() throws IOException {
        if (!Files.exists(APP_DIR)) {
            Files.createDirectories(APP_DIR);
        }
    }

    /**
     * Ghi bytes vào file một cách "atomic": viết vào tệp tạm rồi move tới đích.
     */
    private static void writeFileAtomic(Path target, byte[] data) throws IOException {
        Path tmp = Files.createTempFile(APP_DIR, "tmp", ".tmp");
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(tmp, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING))) {
            out.write(data);
            out.flush();
        }
        try {
            Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Hiển thị dialog thông báo khi ghi file thất bại. Chạy trên JavaFX Application Thread.
     * Không ném exception ra ngoài để tránh crash game.
     */
    private static void showWriteErrorDialog(String message) {
        try {
            if (Platform.isFxApplicationThread()) {
                Alert a = new Alert(AlertType.WARNING);
                a.setTitle("Lưu thất bại");
                a.setHeaderText(null);
                a.setContentText(message);
                a.show();
            } else {
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
     * Đọc các dòng từ file.
     * @param filename Tên file (sẽ được lưu trong APP_DIR)
     * @return List các dòng, hoặc null nếu file không tồn tại/lỗi
     */
    public static java.util.List<String> readLinesFromFile(String filename) {
        synchronized (LOCK) {
            try {
                ensureAppDirExists();
                Path filePath = APP_DIR.resolve(filename);

                if (!Files.exists(filePath)) {
                    return null;
                }

                return Files.readAllLines(filePath);
            } catch (IOException ex) {
                System.err.println("FileManager: failed to read file " + filename + " - " + ex.getMessage());
                return null;
            }
        }
    }

    /**
     * Ghi các dòng vào file.
     * @param filename Tên file (sẽ được lưu trong APP_DIR)
     * @param lines List các dòng cần ghi
     */
    public static void writeLinesToFile(String filename, java.util.List<String> lines) {
        synchronized (LOCK) {
            try {
                ensureAppDirExists();
                Path filePath = APP_DIR.resolve(filename);

                // Tạo content từ lines
                StringBuilder sb = new StringBuilder();
                for (String line : lines) {
                    sb.append(line).append(System.lineSeparator());
                }

                writeFileAtomic(filePath, sb.toString().getBytes());
            } catch (IOException ex) {
                System.err.println("FileManager: failed to write file " + filename + " - " + ex.getMessage());
                showWriteErrorDialog("Lưu file thất bại:\n" + ex.getMessage());
            }
        }
    }

    /**
     * Load audio settings từ file.
     * @return Array [volume, isMuted], hoặc null nếu file không tồn tại
     */
    public static double[] loadAudioSettings() {
        synchronized (LOCK) {
            try {
                ensureAppDirExists();
                Path audioFile = APP_DIR.resolve(AUDIO_SETTINGS_FILE);

                if (!Files.exists(audioFile)) {
                    return null;
                }

                java.util.List<String> lines = Files.readAllLines(audioFile);
                if (lines.size() < 2) {
                    return null;
                }

                double volume = Double.parseDouble(lines.get(0).trim());
                boolean muted = Boolean.parseBoolean(lines.get(1).trim());

                return new double[] { volume, muted ? 1.0 : 0.0 };
            } catch (Exception ex) {
                System.err.println("FileManager: failed to load audio settings - " + ex.getMessage());
                return null;
            }
        }
    }

    /**
     * Save audio settings vào file.
     * @param volume Âm lượng (0.0 - 1.0)
     * @param isMuted Trạng thái mute
     */
    public static void saveAudioSettings(double volume, boolean isMuted) {
        synchronized (LOCK) {
            try {
                ensureAppDirExists();
                Path audioFile = APP_DIR.resolve(AUDIO_SETTINGS_FILE);

                StringBuilder sb = new StringBuilder();
                sb.append(volume).append(System.lineSeparator());
                sb.append(isMuted).append(System.lineSeparator());

                writeFileAtomic(audioFile, sb.toString().getBytes());
            } catch (IOException ex) {
                System.err.println("FileManager: failed to save audio settings - " + ex.getMessage());
            }
        }
    }
}