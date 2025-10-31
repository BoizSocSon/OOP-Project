package Engine;

import Audio.MusicTrack;
import Utils.Constants;
import Utils.FileManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * Lớp Singleton quản lý tất cả các hoạt động liên quan đến nhạc nền (background music)
 * trong trò chơi, bao gồm tải, phát, dừng, điều chỉnh âm lượng và trạng thái tắt tiếng,
 * cũng như lưu/tải cài đặt âm thanh.
 */
public class AudioManager {
    // Instance duy nhất của AudioManager (Singleton).
    private static AudioManager instance;

    // MediaPlayer hiện tại đang phát.
    private MediaPlayer currentPlayer;
    // Map lưu trữ các MediaPlayer cho từng MusicTrack để tránh tải lại.
    private final Map<MusicTrack, MediaPlayer> musicPlayers;
    // Track nhạc hiện tại đang được phát.
    private MusicTrack currentTrack;

    // Thuộc tính (Property) của JavaFX để theo dõi và ràng buộc (bind) âm lượng.
    private final DoubleProperty volumeProperty;
    // Thuộc tính (Property) của JavaFX để theo dõi và ràng buộc trạng thái tắt tiếng.
    private final BooleanProperty mutedProperty;

    /**
     * Constructor private để ngăn việc tạo instance từ bên ngoài (Singleton).
     */
    private AudioManager() {
        this.musicPlayers = new HashMap<>();
        this.currentPlayer = null;
        this.currentTrack = null;
        // Khởi tạo thuộc tính âm lượng với giá trị mặc định.
        this.volumeProperty = new SimpleDoubleProperty(Constants.Audio.DEFAULT_MUSIC_VOLUME);
        // Khởi tạo thuộc tính tắt tiếng mặc định là false (không tắt tiếng).
        this.mutedProperty = new SimpleBooleanProperty(false);


        // Tải cài đặt âm thanh đã lưu.
        loadSettings();

        // Lắng nghe sự thay đổi của thuộc tính âm lượng.
        volumeProperty.addListener((obs, oldVal, newVal) -> {
            updateCurrentPlayerVolume(); // Cập nhật âm lượng của player hiện tại.
            saveSettings(); // Lưu cài đặt mới.
        });
        // Lắng nghe sự thay đổi của thuộc tính tắt tiếng.
        mutedProperty.addListener((obs, oldVal, newVal) -> {
            updateCurrentPlayerVolume(); // Cập nhật âm lượng của player hiện tại (0.0 hoặc giá trị volume).
            saveSettings(); // Lưu cài đặt mới.
        });
    }

    /**
     * Lấy instance duy nhất của AudioManager.
     *
     * @return Instance của AudioManager.
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * Khởi tạo AudioManager bằng cách tải tất cả các track nhạc đã định nghĩa.
     * Phương thức này nên được gọi một lần khi ứng dụng khởi động.
     */
    public void initialize() {
        try {
            // Lặp qua tất cả các MusicTrack đã định nghĩa và tải chúng.
            for (MusicTrack track : MusicTrack.values()) {
                loadMusicTrack(track);
            }
            System.out.println("AudioManager: All music tracks loaded successfully");
        } catch (Exception e) {
            System.err.println("AudioManager: Error initializing audio - " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tải một track nhạc cụ thể và tạo MediaPlayer cho nó.
     *
     * @param track MusicTrack cần tải.
     */
    private void loadMusicTrack(MusicTrack track) {
        try {
            // Xây dựng đường dẫn tới file nhạc.
            String path = Constants.Paths.AUDIO_PATH + track.getFilename();
            System.out.println("AudioManager: Attempting to load: " + path);

            // Cố gắng lấy URL của tài nguyên từ ClassLoader.
            java.net.URL resourceUrl = getClass().getResource(path);
            if (resourceUrl == null) {
                System.err.println("AudioManager: Resource not found: " + path);
                System.err.println("AudioManager: Trying alternative paths...");

                // Thử cách lấy resource khác (bỏ ký tự "/" đầu tiên).
                if (path.startsWith("/")) {
                    resourceUrl = getClass().getResource(path.substring(1));
                }

                if (resourceUrl == null) {
                    throw new RuntimeException("Cannot find audio file: " + path);
                }
            }

            // Lấy URL bên ngoài (External Form) để Media class có thể sử dụng.
            String url = resourceUrl.toExternalForm();
            System.out.println("AudioManager: Loading from URL: " + url);

            // Tạo đối tượng Media và MediaPlayer.
            Media media = new Media(url);
            MediaPlayer player = new MediaPlayer(media);
            // Thiết lập lặp lại vô hạn cho nhạc nền.
            player.setCycleCount(MediaPlayer.INDEFINITE);

            // Thiết lập âm lượng ban đầu (có tính đến trạng thái tắt tiếng).
            player.setVolume(mutedProperty.get() ? 0.0 : volumeProperty.get());

            // Lưu trữ MediaPlayer vào Map cache.
            musicPlayers.put(track, player);
            System.out.println("AudioManager: Successfully loaded " + track.name());
        } catch (Exception e) {
            System.err.println("AudioManager: Failed to load " + track.name() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Bắt đầu phát một track nhạc mới. Nếu track đó đang phát, không làm gì.
     *
     * @param track Track nhạc muốn phát.
     */
    public void playMusic(MusicTrack track) {
        if (track == null) {
            return;
        }

        // Kiểm tra nếu track hiện tại đã là track muốn phát và đang PLAYING, thì thoát.
        if (currentTrack == track && currentPlayer != null &&
                currentPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }

        // Dừng nhạc hiện tại trước khi phát nhạc mới.
        stopMusic();

        // Lấy MediaPlayer từ cache.
        MediaPlayer player = musicPlayers.get(track);
        if (player == null) {
            System.err.println("AudioManager: Track " + track.name() + " not found in cache");
            return;
        }

        // Thiết lập player và track hiện tại.
        currentPlayer = player;
        currentTrack = track;
        // Đảm bảo âm lượng được thiết lập chính xác trước khi phát.
        updateCurrentPlayerVolume();
        // Bắt đầu phát.
        currentPlayer.play();

        System.out.println("AudioManager: Playing " + track.name());
    }

    /**
     * Dừng nhạc nền hiện tại và đặt lại trạng thái player/track.
     */
    public void stopMusic() {
        if (currentPlayer != null) {
            currentPlayer.stop(); // Dừng player.
        }
        currentPlayer = null;
        currentTrack = null;
    }

    /**
     * Tạm dừng (Pause) nhạc nền hiện tại nếu nó đang phát.
     */
    public void pauseMusic() {
        if (currentPlayer != null && currentPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            currentPlayer.pause();
            System.out.println("AudioManager: Music paused");
        }
    }

    /**
     * Tiếp tục (Resume) phát nhạc nền nếu nó đang tạm dừng.
     */
    public void resumeMusic() {
        if (currentPlayer != null && currentPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            currentPlayer.play();
            System.out.println("AudioManager: Music resumed");
        }
    }

    /**
     * Đặt giá trị âm lượng mới (giới hạn từ 0.0 đến 1.0).
     *
     * @param volume Giá trị âm lượng mới.
     */
    public void setVolume(double volume) {
        // Giới hạn giá trị âm lượng.
        if (volume < 0.0) volume = 0.0;
        if (volume > 1.0) volume = 1.0;
        volumeProperty.set(volume); // Đặt giá trị mới, trigger listener để cập nhật player và lưu setting.
    }

    /**
     * Lấy giá trị âm lượng hiện tại (không tính trạng thái tắt tiếng).
     *
     * @return Giá trị âm lượng hiện tại.
     */
    public double getVolume() {
        return volumeProperty.get();
    }

    /**
     * Trả về thuộc tính âm lượng để ràng buộc (binding) trong UI.
     *
     * @return {@link DoubleProperty} của âm lượng.
     */
    public DoubleProperty volumeProperty() {
        return volumeProperty;
    }

    /**
     * Đặt trạng thái tắt tiếng mới.
     *
     * @param muted {@code true} để tắt tiếng, {@code false} để bật.
     */
    public void setMuted(boolean muted) {
        mutedProperty.set(muted); // Đặt giá trị mới, trigger listener để cập nhật player và lưu setting.
    }

    /**
     * Kiểm tra xem nhạc nền có đang bị tắt tiếng không.
     *
     * @return {@code true} nếu tắt tiếng, ngược lại là {@code false}.
     */
    public boolean isMuted() {
        return mutedProperty.get();
    }

    /**
     * Trả về thuộc tính tắt tiếng để ràng buộc (binding) trong UI.
     *
     * @return {@link BooleanProperty} của trạng thái tắt tiếng.
     */
    public BooleanProperty mutedProperty() {
        return mutedProperty;
    }

    /**
     * Cập nhật âm lượng thực tế của MediaPlayer hiện tại dựa trên
     * giá trị {@code volumeProperty} và {@code mutedProperty}.
     */
    private void updateCurrentPlayerVolume() {
        if (currentPlayer != null) {
            // Âm lượng thực tế là 0.0 nếu tắt tiếng, hoặc là giá trị volume nếu bật.
            double actualVolume = mutedProperty.get() ? 0.0 : volumeProperty.get();
            currentPlayer.setVolume(actualVolume);
        }
    }

    /**
     * Lấy track nhạc hiện tại đang được phát.
     *
     * @return {@link MusicTrack} hiện tại, hoặc {@code null} nếu không có track nào đang phát.
     */
    public MusicTrack getCurrentTrack() {
        return currentTrack;
    }

    /**
     * Dọn dẹp tài nguyên khi ứng dụng kết thúc.
     */
    public void dispose() {
        stopMusic(); // Dừng nhạc trước.
        // Giải phóng tài nguyên của tất cả các MediaPlayer đã tải.
        for (MediaPlayer player : musicPlayers.values()) {
            player.dispose();
        }
        musicPlayers.clear(); // Xóa cache.
        saveSettings(); // Lưu lần cuối cài đặt trước khi dispose.
        System.out.println("AudioManager: Disposed");
    }

    /**
     * Tải cài đặt âm thanh đã lưu từ file.
     */
    private void loadSettings() {
        try {
            // Tải mảng cài đặt từ FileManager.
            double[] settings = FileManager.loadAudioSettings();
            if (settings != null && settings.length >= 2) {
                // settings[0] là volume, settings[1] là trạng thái muted (1.0 nếu muted, 0.0 nếu không).
                volumeProperty.set(settings[0]);
                // Chuyển đổi giá trị double (settings[1]) thành boolean.
                mutedProperty.set(settings[1] > 0.5);
                System.out.println("AudioManager: Loaded settings - volume=" + settings[0] + ", muted=" + (settings[1] > 0.5));
            } else {
                // Sử dụng giá trị mặc định nếu không có cài đặt.
                System.out.println("AudioManager: No saved settings found, using defaults");
            }
        } catch (Exception e) {
            System.err.println("AudioManager: Error loading settings - " + e.getMessage());
        }
    }

    /**
     * Lưu cài đặt âm thanh hiện tại vào file.
     */
    private void saveSettings() {
        try {
            // Lưu giá trị volume và muted hiện tại.
            FileManager.saveAudioSettings(volumeProperty.get(), mutedProperty.get());
        } catch (Exception e) {
            System.err.println("AudioManager: Error saving settings - " + e.getMessage());
        }
    }
}