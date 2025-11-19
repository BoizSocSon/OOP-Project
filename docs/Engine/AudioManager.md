# AudioManager

## Tổng quan
Lớp `AudioManager` là một Singleton quản lý tất cả các hoạt động liên quan đến nhạc nền (background music) trong trò chơi Arkanoid. Lớp này chịu trách nhiệm tải, phát, dừng, tạm dừng, điều chỉnh âm lượng và trạng thái tắt tiếng, cũng như lưu/tải cài đặt âm thanh vào file.

## Package
```java
package Engine;
```

## Pattern
**Singleton Pattern** - Đảm bảo chỉ có một instance duy nhất quản lý âm thanh trong toàn bộ ứng dụng.

## Thuộc tính

### Singleton Instance
- `static AudioManager instance`: Instance duy nhất của AudioManager (Singleton)

### Media Players
- `MediaPlayer currentPlayer`: MediaPlayer hiện tại đang phát
- `Map<MusicTrack, MediaPlayer> musicPlayers`: Map lưu trữ các MediaPlayer cho từng MusicTrack để tránh tải lại (cache)
- `MusicTrack currentTrack`: Track nhạc hiện tại đang được phát

### JavaFX Properties
- `DoubleProperty volumeProperty`: Thuộc tính JavaFX để theo dõi và ràng buộc (bind) âm lượng
- `BooleanProperty mutedProperty`: Thuộc tính JavaFX để theo dõi và ràng buộc trạng thái tắt tiếng

**Lợi ích của Properties:**
- Hỗ trợ data binding với UI
- Tự động notify khi giá trị thay đổi
- Dễ dàng tích hợp với JavaFX components

## Constructor

### private AudioManager()
Constructor private để ngăn việc tạo instance từ bên ngoài (Singleton pattern).

**Công việc:**
1. Khởi tạo HashMap cho cache MediaPlayer
2. Đặt currentPlayer và currentTrack = null
3. Khởi tạo volumeProperty với giá trị mặc định `Constants.Audio.DEFAULT_MUSIC_VOLUME`
4. Khởi tạo mutedProperty = false (không tắt tiếng)
5. Gọi `loadSettings()` để tải cài đặt đã lưu
6. Thêm listeners cho volumeProperty và mutedProperty:
   - Khi volume thay đổi: Cập nhật player và lưu settings
   - Khi muted thay đổi: Cập nhật player và lưu settings

**Listeners:**
```java
volumeProperty.addListener((obs, oldVal, newVal) -> {
    updateCurrentPlayerVolume();
    saveSettings();
});

mutedProperty.addListener((obs, oldVal, newVal) -> {
    updateCurrentPlayerVolume();
    saveSettings();
});
```

## Phương thức Singleton

### getInstance()
Lấy instance duy nhất của AudioManager.

**Trả về:** Instance của AudioManager

**Pattern:** Lazy initialization - Instance chỉ được tạo khi cần thiết

```java
AudioManager audioManager = AudioManager.getInstance();
```

## Phương thức khởi tạo

### initialize()
Khởi tạo AudioManager bằng cách tải tất cả các track nhạc đã định nghĩa.

**Công việc:**
1. Lặp qua tất cả các MusicTrack (enum values)
2. Gọi `loadMusicTrack()` cho từng track
3. Log thành công hoặc lỗi

**Lưu ý:** Phương thức này nên được gọi một lần khi ứng dụng khởi động.

```java
AudioManager audioManager = AudioManager.getInstance();
audioManager.initialize();
```

### loadMusicTrack(MusicTrack track)
Tải một track nhạc cụ thể và tạo MediaPlayer cho nó.

**Tham số:**
- **track**: MusicTrack cần tải

**Công việc:**
1. Xây dựng đường dẫn: `Constants.Paths.AUDIO_PATH + track.getFilename()`
2. Lấy URL của tài nguyên từ ClassLoader
3. Xử lý fallback nếu resource không tìm thấy
4. Tạo đối tượng Media từ URL
5. Tạo MediaPlayer với Media
6. Thiết lập cycle count = INDEFINITE (lặp vô hạn)
7. Thiết lập âm lượng ban đầu (tính đến trạng thái muted)
8. Lưu vào cache map

**Xử lý lỗi:**
```java
// Thử lấy resource
java.net.URL resourceUrl = getClass().getResource(path);
if (resourceUrl == null) {
    // Thử cách khác (bỏ "/" đầu)
    if (path.startsWith("/")) {
        resourceUrl = getClass().getResource(path.substring(1));
    }
    if (resourceUrl == null) {
        throw new RuntimeException("Cannot find audio file: " + path);
    }
}
```

## Phương thức phát nhạc

### playMusic(MusicTrack track)
Bắt đầu phát một track nhạc mới. Nếu track đó đang phát, không làm gì.

**Tham số:**
- **track**: Track nhạc muốn phát

**Logic:**
1. Kiểm tra track != null
2. Nếu track hiện tại đã là track muốn phát VÀ đang PLAYING → return
3. Gọi `stopMusic()` để dừng nhạc hiện tại
4. Lấy MediaPlayer từ cache
5. Thiết lập currentPlayer và currentTrack
6. Gọi `updateCurrentPlayerVolume()` để đảm bảo âm lượng đúng
7. Gọi `player.play()` để bắt đầu phát

```java
audioManager.playMusic(MusicTrack.MENU_MUSIC);
audioManager.playMusic(MusicTrack.GAME_MUSIC);
```

### stopMusic()
Dừng nhạc nền hiện tại và đặt lại trạng thái player/track.

**Công việc:**
1. Nếu currentPlayer != null: Gọi `currentPlayer.stop()`
2. Đặt currentPlayer = null
3. Đặt currentTrack = null

```java
audioManager.stopMusic();
```

### pauseMusic()
Tạm dừng (Pause) nhạc nền hiện tại nếu nó đang phát.

**Điều kiện:**
- currentPlayer != null
- Player status == MediaPlayer.Status.PLAYING

```java
audioManager.pauseMusic();
```

### resumeMusic()
Tiếp tục (Resume) phát nhạc nền nếu nó đang tạm dừng.

**Điều kiện:**
- currentPlayer != null
- Player status == MediaPlayer.Status.PAUSED

```java
audioManager.resumeMusic();
```

## Phương thức điều khiển âm lượng

### setVolume(double volume)
Đặt giá trị âm lượng mới (giới hạn từ 0.0 đến 1.0).

**Tham số:**
- **volume**: Giá trị âm lượng mới (0.0 - 1.0)

**Logic:**
1. Giới hạn giá trị: `if (volume < 0.0) volume = 0.0;`
2. Giới hạn giá trị: `if (volume > 1.0) volume = 1.0;`
3. Đặt giá trị: `volumeProperty.set(volume)`
4. Listener tự động gọi `updateCurrentPlayerVolume()` và `saveSettings()`

```java
audioManager.setVolume(0.5);  // 50%
audioManager.setVolume(0.75); // 75%
audioManager.setVolume(1.0);  // 100%
```

### getVolume()
Lấy giá trị âm lượng hiện tại (không tính trạng thái tắt tiếng).

**Trả về:** Giá trị âm lượng hiện tại (0.0 - 1.0)

```java
double currentVolume = audioManager.getVolume();
int volumePercent = (int)(currentVolume * 100);
```

### volumeProperty()
Trả về thuộc tính âm lượng để ràng buộc (binding) trong UI.

**Trả về:** DoubleProperty của âm lượng

**Sử dụng với UI binding:**
```java
Slider volumeSlider = new Slider(0, 1, 0.5);
volumeSlider.valueProperty().bindBidirectional(audioManager.volumeProperty());
```

## Phương thức điều khiển Mute

### setMuted(boolean muted)
Đặt trạng thái tắt tiếng mới.

**Tham số:**
- **muted**: true để tắt tiếng, false để bật

**Logic:**
1. Đặt giá trị: `mutedProperty.set(muted)`
2. Listener tự động gọi `updateCurrentPlayerVolume()` và `saveSettings()`

```java
audioManager.setMuted(true);  // Tắt tiếng
audioManager.setMuted(false); // Bật tiếng
```

### isMuted()
Kiểm tra xem nhạc nền có đang bị tắt tiếng không.

**Trả về:** true nếu tắt tiếng, false nếu không

```java
if (audioManager.isMuted()) {
    System.out.println("Music is muted");
}
```

### mutedProperty()
Trả về thuộc tính tắt tiếng để ràng buộc (binding) trong UI.

**Trả về:** BooleanProperty của trạng thái tắt tiếng

**Sử dụng với UI binding:**
```java
CheckBox muteCheckbox = new CheckBox("Mute");
muteCheckbox.selectedProperty().bindBidirectional(audioManager.mutedProperty());
```

## Phương thức hỗ trợ

### updateCurrentPlayerVolume()
Cập nhật âm lượng thực tế của MediaPlayer hiện tại dựa trên volumeProperty và mutedProperty.

**Logic:**
```java
if (currentPlayer != null) {
    double actualVolume = mutedProperty.get() ? 0.0 : volumeProperty.get();
    currentPlayer.setVolume(actualVolume);
}
```

**Âm lượng thực tế:**
- Nếu muted = true: actualVolume = 0.0
- Nếu muted = false: actualVolume = volumeProperty value

### getCurrentTrack()
Lấy track nhạc hiện tại đang được phát.

**Trả về:** MusicTrack hiện tại, hoặc null nếu không có track nào đang phát

```java
MusicTrack current = audioManager.getCurrentTrack();
if (current == MusicTrack.GAME_MUSIC) {
    // ...
}
```

## Phương thức lưu/tải cài đặt

### loadSettings()
Tải cài đặt âm thanh đã lưu từ file.

**Công việc:**
1. Gọi `FileManager.loadAudioSettings()` để lấy mảng settings
2. Kiểm tra mảng hợp lệ (length >= 2)
3. Đặt volume: `volumeProperty.set(settings[0])`
4. Đặt muted: `mutedProperty.set(settings[1] > 0.5)`
5. Nếu không có settings: Sử dụng giá trị mặc định

**Format settings:**
- `settings[0]`: volume (0.0 - 1.0)
- `settings[1]`: muted (1.0 nếu muted, 0.0 nếu không)

### saveSettings()
Lưu cài đặt âm thanh hiện tại vào file.

**Công việc:**
1. Gọi `FileManager.saveAudioSettings(volume, muted)`
2. Xử lý exception nếu có

**Tự động gọi khi:**
- Volume thay đổi (qua listener)
- Muted thay đổi (qua listener)
- Dispose được gọi

## Phương thức dọn dẹp

### dispose()
Dọn dẹp tài nguyên khi ứng dụng kết thúc.

**Công việc:**
1. Gọi `stopMusic()` để dừng nhạc
2. Lặp qua tất cả MediaPlayer trong cache và gọi `dispose()`
3. Xóa cache: `musicPlayers.clear()`
4. Gọi `saveSettings()` lần cuối
5. Log "AudioManager: Disposed"

```java
// Khi ứng dụng đóng
audioManager.dispose();
```

## Cách sử dụng

### Ví dụ khởi tạo và sử dụng cơ bản
```java
// Lấy instance (Singleton)
AudioManager audioManager = AudioManager.getInstance();

// Khởi tạo (tải tất cả tracks)
audioManager.initialize();

// Phát nhạc menu
audioManager.playMusic(MusicTrack.MENU_MUSIC);

// Điều chỉnh âm lượng
audioManager.setVolume(0.7); // 70%

// Tắt tiếng
audioManager.setMuted(true);

// Bật lại
audioManager.setMuted(false);

// Tạm dừng
audioManager.pauseMusic();

// Tiếp tục
audioManager.resumeMusic();

// Chuyển sang nhạc game
audioManager.playMusic(MusicTrack.GAME_MUSIC);

// Dừng nhạc
audioManager.stopMusic();

// Dọn dẹp khi thoát
audioManager.dispose();
```

### Ví dụ tích hợp với UI
```java
// Trong SettingsScreen
public class SettingsScreen {
    private AudioManager audioManager;
    private Slider volumeSlider;
    private CheckBox muteCheckbox;
    
    public void initialize() {
        audioManager = AudioManager.getInstance();
        
        // Volume slider (0-100%)
        volumeSlider = new Slider(0, 1, audioManager.getVolume());
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            audioManager.setVolume(newVal.doubleValue());
        });
        
        // Hoặc sử dụng bidirectional binding
        volumeSlider.valueProperty().bindBidirectional(audioManager.volumeProperty());
        
        // Mute checkbox
        muteCheckbox = new CheckBox("Mute");
        muteCheckbox.selectedProperty().bindBidirectional(audioManager.mutedProperty());
    }
}
```

### Ví dụ chuyển đổi nhạc theo game state
```java
// Trong StateManager hoặc GameManager
public void setState(GameState newState) {
    AudioManager audioManager = AudioManager.getInstance();
    
    switch (newState) {
        case MENU:
            audioManager.playMusic(MusicTrack.MENU_MUSIC);
            break;
        case PLAYING:
            audioManager.playMusic(MusicTrack.GAME_MUSIC);
            break;
        case PAUSED:
            audioManager.pauseMusic();
            break;
        case GAME_OVER:
            audioManager.stopMusic();
            // Có thể phát SFX game over ở đây
            break;
    }
}
```

### Ví dụ xử lý pause/resume
```java
// Khi pause game
public void pauseGame() {
    audioManager.pauseMusic();
    currentState = GameState.PAUSED;
}

// Khi resume game
public void resumeGame() {
    audioManager.resumeMusic();
    currentState = GameState.PLAYING;
}
```

## Tính năng đặc biệt

### 1. Singleton Pattern
Đảm bảo chỉ có một instance quản lý âm thanh:
- Tránh xung đột khi nhiều nơi cố phát nhạc
- Dễ truy cập từ mọi nơi trong code
- Quản lý tài nguyên tốt hơn

### 2. Cache MediaPlayer
Tránh tải lại file nhạc nhiều lần:
- Tải một lần khi initialize
- Lưu trong HashMap
- Tái sử dụng khi cần

### 3. JavaFX Properties
Hỗ trợ data binding với UI:
- volumeProperty: Bind với Slider
- mutedProperty: Bind với CheckBox
- Tự động cập nhật UI khi giá trị thay đổi

### 4. Automatic Settings Persistence
Tự động lưu cài đặt khi thay đổi:
- Listener tự động gọi saveSettings()
- Không cần gọi thủ công
- Đảm bảo settings luôn được lưu

### 5. Smooth Transitions
Tự động xử lý chuyển đổi nhạc:
- Dừng nhạc cũ trước khi phát nhạc mới
- Kiểm tra xem nhạc đã phát chưa
- Tránh phát trùng lặp

### 6. Volume vs Muted
Phân biệt rõ giữa volume và muted:
- Volume: Giá trị âm lượng thực sự (0.0 - 1.0)
- Muted: Trạng thái tắt tiếng (true/false)
- Khi unmute, volume trở về giá trị trước đó

## Luồng hoạt động

### Khởi tạo
```
Application Start
    ↓
AudioManager.getInstance()
    ↓
Constructor (private)
    ↓
loadSettings() từ file
    ↓
initialize() - Load all tracks
    ↓
loadMusicTrack() cho từng track
    ↓
Cache vào musicPlayers map
```

### Phát nhạc
```
playMusic(track)
    ↓
Check if already playing → return
    ↓
stopMusic() - Dừng nhạc hiện tại
    ↓
Get player from cache
    ↓
Set currentPlayer, currentTrack
    ↓
updateCurrentPlayerVolume()
    ↓
player.play()
```

### Thay đổi volume
```
setVolume(newValue)
    ↓
Giới hạn 0.0 - 1.0
    ↓
volumeProperty.set(newValue)
    ↓
Listener triggered
    ↓
updateCurrentPlayerVolume()
    ↓
saveSettings()
```

### Thay đổi muted
```
setMuted(true/false)
    ↓
mutedProperty.set(value)
    ↓
Listener triggered
    ↓
updateCurrentPlayerVolume()
    ↓
saveSettings()
```

## Best Practices

1. **Gọi initialize() một lần**: Ở Application.start() hoặc main()
2. **Sử dụng Properties cho UI binding**: Tận dụng volumeProperty() và mutedProperty()
3. **Gọi dispose() khi thoát**: Giải phóng tài nguyên MediaPlayer
4. **Không tạo instance mới**: Luôn dùng getInstance()
5. **Kiểm tra null**: Trước khi sử dụng currentPlayer hoặc currentTrack
6. **Xử lý exception**: Khi load file nhạc có thể bị lỗi

## Xử lý lỗi

### 1. File không tìm thấy
```java
// AudioManager tự động thử nhiều cách load resource
// Nếu thất bại, throw RuntimeException và skip track đó
```

### 2. MediaPlayer null
```java
// Luôn kiểm tra currentPlayer != null trước khi sử dụng
if (currentPlayer != null) {
    currentPlayer.stop();
}
```

### 3. Settings load failed
```java
// Sử dụng giá trị mặc định từ Constants
// Log lỗi ra console
```

## Dependencies
- `Audio.MusicTrack`: Enum định nghĩa các track nhạc
- `Utils.Constants`: Các hằng số (đường dẫn, volume mặc định)
- `Utils.FileManager`: Lưu/tải cài đặt từ file
- `javafx.scene.media.Media`: Đại diện cho file media
- `javafx.scene.media.MediaPlayer`: Player để phát media
- `javafx.beans.property.*`: Properties cho data binding

## Mở rộng trong tương lai

### Ý tưởng cải tiến
1. **Sound Effects (SFX)**: Thêm phương thức playSoundEffect() cho hiệu ứng âm thanh ngắn
2. **Fade In/Out**: Hiệu ứng fade khi chuyển nhạc
3. **Playlist**: Hỗ trợ phát danh sách nhạc
4. **Cross-fade**: Chuyển đổi mượt mà giữa các track
5. **Multiple channels**: Phát nhiều âm thanh đồng thời (nhạc nền + SFX)
6. **Equalizer**: Bộ chỉnh âm
7. **Audio profiles**: Nhiều profiles cài đặt khác nhau
8. **3D audio**: Âm thanh theo vị trí trong game
