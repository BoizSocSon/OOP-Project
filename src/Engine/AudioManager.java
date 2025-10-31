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
 * Singleton class quản lý âm thanh của game.
 * Xử lý phát nhạc nền cho các trạng thái khác nhau của game.
 *
 */
public class AudioManager {
    private static AudioManager instance;

    // MediaPlayer management
    private MediaPlayer currentPlayer;
    private final Map<MusicTrack, MediaPlayer> musicPlayers;
    private MusicTrack currentTrack;

    // Audio properties (observable for UI binding)
    private final DoubleProperty volumeProperty;
    private final BooleanProperty mutedProperty;

    /**
     * Private constructor để enforce singleton pattern.
     */
    private AudioManager() {
        this.musicPlayers = new HashMap<>();
        this.currentPlayer = null;
        this.currentTrack = null;
        this.volumeProperty = new SimpleDoubleProperty(Constants.Audio.DEFAULT_MUSIC_VOLUME);
        this.mutedProperty = new SimpleBooleanProperty(false);

        // Load settings từ file
        loadSettings();

        // Lắng nghe thay đổi volume và muted để update MediaPlayer và save
        volumeProperty.addListener((obs, oldVal, newVal) -> {
            updateCurrentPlayerVolume();
            saveSettings();
        });
        mutedProperty.addListener((obs, oldVal, newVal) -> {
            updateCurrentPlayerVolume();
            saveSettings();
        });
    }

    /**
     * Lấy singleton instance của AudioManager.
     * @return Instance duy nhất của AudioManager
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * Khởi tạo và load tất cả music tracks vào memory.
     * Nên gọi method này khi game khởi động.
     */
    public void initialize() {
        try {
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
     * Load một music track vào cache.
     * @param track Track cần load
     */
    private void loadMusicTrack(MusicTrack track) {
        try {
            String path = Constants.Paths.AUDIO_PATH + track.getFilename();
            System.out.println("AudioManager: Attempting to load: " + path);

            java.net.URL resourceUrl = getClass().getResource(path);
            if (resourceUrl == null) {
                System.err.println("AudioManager: Resource not found: " + path);
                System.err.println("AudioManager: Trying alternative paths...");

                // Try without leading slash
                if (path.startsWith("/")) {
                    resourceUrl = getClass().getResource(path.substring(1));
                }

                if (resourceUrl == null) {
                    throw new RuntimeException("Cannot find audio file: " + path);
                }
            }

            String url = resourceUrl.toExternalForm();
            System.out.println("AudioManager: Loading from URL: " + url);

            Media media = new Media(url);
            MediaPlayer player = new MediaPlayer(media);
            player.setCycleCount(MediaPlayer.INDEFINITE); // Loop vô hạn

            // Set initial volume
            player.setVolume(mutedProperty.get() ? 0.0 : volumeProperty.get());

            musicPlayers.put(track, player);
            System.out.println("AudioManager: Successfully loaded " + track.name());
        } catch (Exception e) {
            System.err.println("AudioManager: Failed to load " + track.name() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Phát một music track.
     * Tự động dừng track đang phát (nếu có) và chuyển sang track mới.
     * @param track Track cần phát
     */
    public void playMusic(MusicTrack track) {
        if (track == null) {
            return;
        }

        // Nếu đang phát cùng track thì không làm gì
        if (currentTrack == track && currentPlayer != null &&
                currentPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }

        // Dừng track đang phát
        stopMusic();

        // Lấy MediaPlayer cho track mới
        MediaPlayer player = musicPlayers.get(track);
        if (player == null) {
            System.err.println("AudioManager: Track " + track.name() + " not found in cache");
            return;
        }

        // Set volume và play
        currentPlayer = player;
        currentTrack = track;
        updateCurrentPlayerVolume();
        currentPlayer.play();

        System.out.println("AudioManager: Playing " + track.name());
    }

    /**
     * Dừng nhạc đang phát.
     */
    public void stopMusic() {
        if (currentPlayer != null) {
            currentPlayer.stop();
        }
        currentPlayer = null;
        currentTrack = null;
    }

    /**
     * Tạm dừng nhạc đang phát.
     */
    public void pauseMusic() {
        if (currentPlayer != null && currentPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            currentPlayer.pause();
            System.out.println("AudioManager: Music paused");
        }
    }

    /**
     * Tiếp tục phát nhạc sau khi pause.
     */
    public void resumeMusic() {
        if (currentPlayer != null && currentPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            currentPlayer.play();
            System.out.println("AudioManager: Music resumed");
        }
    }

    /**
     * Set âm lượng nhạc.
     * @param volume Giá trị từ 0.0 đến 1.0
     */
    public void setVolume(double volume) {
        if (volume < 0.0) volume = 0.0;
        if (volume > 1.0) volume = 1.0;
        volumeProperty.set(volume);
    }

    /**
     * Lấy âm lượng hiện tại.
     * @return Giá trị từ 0.0 đến 1.0
     */
    public double getVolume() {
        return volumeProperty.get();
    }

    /**
     * Lấy volume property để bind với UI.
     * @return DoubleProperty của volume
     */
    public DoubleProperty volumeProperty() {
        return volumeProperty;
    }

    /**
     * Set trạng thái mute/unmute.
     * @param muted true để tắt tiếng, false để bật tiếng
     */
    public void setMuted(boolean muted) {
        mutedProperty.set(muted);
    }

    /**
     * Kiểm tra trạng thái mute.
     * @return true nếu đang tắt tiếng
     */
    public boolean isMuted() {
        return mutedProperty.get();
    }

    /**
     * Lấy muted property để bind với UI.
     * @return BooleanProperty của muted
     */
    public BooleanProperty mutedProperty() {
        return mutedProperty;
    }

    /**
     * Update volume của MediaPlayer hiện tại dựa trên volume và muted state.
     */
    private void updateCurrentPlayerVolume() {
        if (currentPlayer != null) {
            double actualVolume = mutedProperty.get() ? 0.0 : volumeProperty.get();
            currentPlayer.setVolume(actualVolume);
        }
    }

    /**
     * Lấy track đang phát.
     * @return Track hiện tại hoặc null
     */
    public MusicTrack getCurrentTrack() {
        return currentTrack;
    }

    /**
     * Giải phóng tất cả resources.
     * Nên gọi method này khi game đóng.
     */
    public void dispose() {
        stopMusic();
        for (MediaPlayer player : musicPlayers.values()) {
            player.dispose();
        }
        musicPlayers.clear();
        saveSettings(); // Save lần cuối trước khi dispose
        System.out.println("AudioManager: Disposed");
    }

    /**
     * Load audio settings từ file.
     */
    private void loadSettings() {
        try {
            double[] settings = FileManager.loadAudioSettings();
            if (settings != null && settings.length >= 2) {
                volumeProperty.set(settings[0]);
                mutedProperty.set(settings[1] > 0.5);
                System.out.println("AudioManager: Loaded settings - volume=" + settings[0] + ", muted=" + (settings[1] > 0.5));
            } else {
                System.out.println("AudioManager: No saved settings found, using defaults");
            }
        } catch (Exception e) {
            System.err.println("AudioManager: Error loading settings - " + e.getMessage());
        }
    }

    /**
     * Save audio settings vào file.
     */
    private void saveSettings() {
        try {
            FileManager.saveAudioSettings(volumeProperty.get(), mutedProperty.get());
        } catch (Exception e) {
            System.err.println("AudioManager: Error saving settings - " + e.getMessage());
        }
    }
}
