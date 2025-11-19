# SettingsScreen

## Tổng quan
Lớp `SettingsScreen` là màn hình Cài đặt (Settings Screen) của trò chơi Arkanoid. Màn hình này cho phép người chơi điều chỉnh âm lượng nhạc nền và bật/tắt tiếng thông qua giao diện trực quan với thanh volume bar và các nút MUTE/UNMUTE.

## Package
```java
package UI.Menu;
```

## Implements
```java
public class SettingsScreen implements Screen
```

## Thuộc tính

### Quản lý âm thanh và callback
- `AudioManager audioManager`: Quản lý và điều khiển âm thanh game (nhạc nền, SFX)
- `Runnable onBack`: Callback được gọi khi người dùng chọn thoát màn hình cài đặt (quay lại Menu chính)
- `SpriteProvider sprites`: Cung cấp các tài nguyên hình ảnh (sprites)
- `Image logo`: Sprite logo game được hiển thị trên màn hình

### Thành phần UI
- `Button muteButton`: Nút để tắt toàn bộ âm thanh (MUTE)
- `Button unmuteButton`: Nút để bật lại âm thanh (UNMUTE)

### Trạng thái
- `int selectedOption`: Lựa chọn hiện tại
  - `0` = Điều chỉnh âm lượng (VOLUME)
  - `1` = Bật/Tắt tiếng (SOUND)

### Font
- `String fontFamilyOptimus`: Font family Optimus
- `String fontFamilyGeneration`: Font family Generation
- `String fontFamilyEmulogic`: Font family Emulogic

### Hằng số điều khiển
- `static final double VOLUME_STEP = 0.01`: Bước điều chỉnh âm lượng (1% mỗi lần nhấn phím)

### Hằng số Layout
- `WINDOW_WIDTH`, `WINDOW_HEIGHT`: Kích thước cửa sổ game
- `BUTTON_WIDTH = 150`: Chiều rộng nút
- `BUTTON_HEIGHT = 50`: Chiều cao nút
- `LOGO_WIDTH`, `LOGO_HEIGHT`: Kích thước logo

## Constructor

### SettingsScreen(AudioManager audioManager, SpriteProvider sprites, Runnable onBack)
Khởi tạo màn hình Cài đặt với các thông số:
- **audioManager**: AudioManager để điều khiển âm thanh
- **sprites**: SpriteProvider để lấy tài nguyên hình ảnh
- **onBack**: Callback được gọi khi nhấn nút BACK (ESC) để quay lại màn hình trước

**Công việc:**
1. Lưu các tham chiếu
2. Tải logo từ SpriteProvider
3. Gọi `initializeComponents()` để khởi tạo UI

```java
SettingsScreen settings = new SettingsScreen(
    audioManager,
    sprites,
    () -> mainMenu.onBackFromSettings()
);
```

## Phương thức chính

### initializeComponents()
Khởi tạo các UI components (chủ yếu là nút MUTE/UNMUTE) và tính toán vị trí.

**Công việc:**
1. Tính toán vị trí center
2. Tính toán vị trí Y cho khối nút (buttonY = 490)
3. Tính toán vị trí X để căn giữa cả khối 2 nút
4. Khởi tạo nút **MUTE**:
   - Callback: `audioManager.setMuted(true)` - Tắt tiếng
5. Khởi tạo nút **UNMUTE**:
   - Callback: `audioManager.setMuted(false)` - Bật tiếng
6. Load 3 custom fonts (emulogic, generation, optimus)

## Phương thức Screen Interface

### render(GraphicsContext gc)
Vẽ tất cả các thành phần UI của màn hình cài đặt lên GraphicsContext.

**Các thành phần:**

1. **Gradient background** (tương tự Menu chính)
   - Top: `rgb(10,10,30)`
   - Bottom: `rgb(30,10,50)`

2. **Logo** ở trên cùng (Y = 100)

3. **Tiêu đề chính**: "AUDIO SETTINGS" (vàng, size 30)

4. **Khu vực VOLUME** (selectedOption == 0):
   - Text "VOLUME" (vàng nếu selected, trắng nếu không)
   - Volume bar trực quan (gọi `drawVolumeBar()`)
   - Phần trăm âm lượng hiện tại
   - Hướng dẫn: "Use LEFT/RIGHT arrows to adjust"

5. **Khu vực SOUND** (selectedOption == 1):
   - Text "SOUND" (vàng nếu selected, trắng nếu không)
   - Nút MUTE/UNMUTE (highlight nút tương ứng với trạng thái)
   - Hướng dẫn: "Use UP/DOWN arrows to select, ENTER to toggle"

6. **Hướng dẫn thoát**: "Press ESC to return to menu" (vàng)

**Logic cập nhật trạng thái nút:**
```java
boolean isMuted = audioManager.isMuted();

// MUTE button: Sáng nếu đang tắt tiếng VÀ đang chọn SOUND
muteButton.setSelected(isMuted && selectedOption == 1);

// UNMUTE button: Sáng nếu đang bật tiếng VÀ đang chọn SOUND
unmuteButton.setSelected(!isMuted && selectedOption == 1);

// Hủy hover nếu đang chọn SOUND (tránh xung đột)
if (selectedOption == 1) {
    muteButton.setHovered(false);
    unmuteButton.setHovered(false);
}
```

### drawVolumeBar(GraphicsContext gc)
Vẽ thanh volume bar (biểu thị mức âm lượng) với thiết kế đẹp, bo góc và hiệu ứng gradient.

**Thiết kế:**
- **Kích thước**: 500x30 (rộng và cao hơn)
- **Bo góc**: cornerRadius = 8
- **Shadow/Glow**: Hiệu ứng shadow phía dưới
- **Track**: Nền tối `rgb(30,30,50,0.9)`
- **Fill**: Màu gradient dựa trên mức âm lượng
  - < 33%: Xanh lá nhạt `rgb(100,200,100,0.9)`
  - 33-66%: Xanh dương `rgb(100,180,220,0.9)`
  - > 66%: Xanh dương sáng `rgb(80,150,255,0.9)`
- **Highlight**: Hiệu ứng sáng bên trong phần fill
- **Border**: Viền sáng `rgb(100,150,200)`, độ dày 3px
- **Inner border**: Viền nhẹ bên trong

**Công thức tính fillWidth:**
```java
double fillWidth = barWidth * audioManager.getVolume();
```

### update(long deltaTime)
Cập nhật logic màn hình (hiện tại không có animation phức tạp).

### handleKeyPressed(KeyCode keyCode)
Xử lý sự kiện nhấn phím để điều hướng và thay đổi cài đặt.

**Các phím hỗ trợ:**

| Phím | Hành động |
|------|-----------|
| `ESCAPE` | Thoát màn hình cài đặt (gọi `onBack.run()`) |
| `UP` | Chuyển lên mục VOLUME (`selectedOption = 0`) |
| `DOWN` | Chuyển xuống mục SOUND (`selectedOption = 1`) |
| `LEFT` | Giảm âm lượng (nếu `selectedOption == 0`) |
| `RIGHT` | Tăng âm lượng (nếu `selectedOption == 0`) |
| `ENTER/SPACE` | Toggle Mute/Unmute (nếu `selectedOption == 1`) |

**Logic điều chỉnh âm lượng:**
```java
// Giảm âm lượng
double newVolume = Math.max(0.0, audioManager.getVolume() - VOLUME_STEP);
audioManager.setVolume(newVolume);

// Tăng âm lượng
double newVolume = Math.min(1.0, audioManager.getVolume() + VOLUME_STEP);
audioManager.setVolume(newVolume);
```

**Giới hạn:**
- Âm lượng tối thiểu: 0.0 (0%)
- Âm lượng tối đa: 1.0 (100%)

### handleKeyReleased(KeyCode keyCode)
Không sử dụng.

### handleMouseClicked(MouseEvent event)
Xử lý sự kiện nhấp chuột.

**Logic:**
1. Lấy tọa độ chuột (mouseX, mouseY)
2. Kiểm tra nhấp vào nút MUTE:
   - Chuyển sang mục SOUND (`selectedOption = 1`)
   - Gọi `muteButton.click()` - tắt tiếng
3. Kiểm tra nhấp vào nút UNMUTE:
   - Chuyển sang mục SOUND (`selectedOption = 1`)
   - Gọi `unmuteButton.click()` - bật tiếng

**Lưu ý:** Nhấp chuột không ảnh hưởng đến thanh volume bar (phải dùng phím LEFT/RIGHT).

### handleMouseMoved(MouseEvent event)
Xử lý sự kiện di chuyển chuột (dùng để cập nhật trạng thái hover).

**Logic:**
- Chỉ cập nhật hover nếu đang không ở chế độ chọn nút bằng phím (`selectedOption != 1`)
- Cập nhật trạng thái hover cho MUTE/UNMUTE buttons dựa trên vị trí chuột

**Lý do:** Tránh xung đột giữa hover (chuột) và selected (phím)

### onEnter()
Xử lý khi màn hình được kích hoạt (vào màn hình).

**Công việc:**
- Đặt `selectedOption = 0` (luôn bắt đầu với mục Volume được chọn)

### onExit()
Xử lý khi màn hình bị vô hiệu hóa (thoát màn hình).

**Công việc:**
- Không có hành động dọn dẹp cần thiết

## Cách sử dụng

### Ví dụ khởi tạo
```java
// Trong MainMenu
AudioManager audioManager = stateManager.getAudioManager();
SpriteProvider sprites = new SpriteProvider(...);

SettingsScreen settingsScreen = new SettingsScreen(
    audioManager,
    sprites,
    this::onBackFromSettings // Callback quay lại menu
);
```

### Ví dụ tích hợp với MainMenu
```java
// Khi người chơi chọn SETTINGS
private void onSettings() {
    showingSettings = true;
    settingsScreen.onEnter();
}

// Callback quay lại
private void onBackFromSettings() {
    showingSettings = false;
}

// Trong render()
if (showingSettings) {
    settingsScreen.render(gc);
    return;
}

// Trong update()
if (showingSettings) {
    settingsScreen.update(deltaTime);
    return;
}

// Trong handleKeyPressed()
if (showingSettings) {
    settingsScreen.handleKeyPressed(keyCode);
    return;
}
```

## Thiết kế UI

### Layout
```
┌──────────────────────────────────────┐
│            [LOGO]                    │
│                                      │
│        AUDIO SETTINGS                │
│                                      │
│           VOLUME ⬅                   │
│   [══════════════════════     ]      │
│            75%                       │
│   Use LEFT/RIGHT arrows to adjust    │
│                                      │
│           SOUND                      │
│      [MUTE]    [UNMUTE]              │
│ Use UP/DOWN arrows to select, ENTER │
│                                      │
│   Press ESC to return to menu        │
└──────────────────────────────────────┘
```

### Màu sắc Volume Bar

**Gradient dựa trên mức âm lượng:**
- **0-33%**: Xanh lá nhạt (thấp)
- **33-66%**: Xanh dương (trung bình)
- **66-100%**: Xanh dương sáng (cao)

### Trạng thái Highlight

**Selected Option:**
- `selectedOption == 0`: Text "VOLUME" màu vàng
- `selectedOption == 1`: Text "SOUND" màu vàng, nút tương ứng sáng

## Tính năng đặc biệt

### 1. Điều chỉnh âm lượng mượt mà
- Bước điều chỉnh nhỏ: 1% mỗi lần
- Giới hạn rõ ràng: 0% - 100%
- Phản hồi trực quan qua volume bar

### 2. Volume Bar trực quan
- Thiết kế đẹp với bo góc, shadow, highlight
- Màu sắc thay đổi theo mức âm lượng
- Hiển thị phần trăm chính xác

### 3. Mute/Unmute nhanh
- Toggle bằng ENTER/SPACE
- Click chuột trực tiếp vào nút
- Hiển thị rõ trạng thái hiện tại

### 4. Điều hướng linh hoạt
- Phím UP/DOWN: Chuyển giữa VOLUME và SOUND
- Phím LEFT/RIGHT: Điều chỉnh âm lượng
- Chuột: Click và hover

### 5. Tránh xung đột UI
- Khi chọn SOUND bằng phím: Tắt hover để tránh xung đột
- Phân biệt rõ 2 cách điều khiển: Phím và Chuột

## Best Practices

1. **Luôn giới hạn âm lượng**: Sử dụng `Math.max()` và `Math.min()` để đảm bảo giá trị hợp lệ
2. **Cập nhật AudioManager ngay lập tức**: Phản hồi nhanh cho người dùng
3. **Thiết kế volume bar rõ ràng**: Dễ nhìn và hiểu
4. **Callback onBack linh hoạt**: Cho phép tích hợp với bất kỳ màn hình nào

## Dependencies
- `Engine.AudioManager`: Quản lý âm thanh
- `UI.Button`: Component nút bấm
- `UI.Screen`: Interface màn hình
- `UI.UIHelper`: Utility vẽ UI
- `Utils.AssetLoader`: Tải font
- `Utils.Constants`: Các hằng số
- `Utils.SpriteProvider`: Cung cấp sprites
- `javafx.scene.canvas.GraphicsContext`: Để vẽ
- `javafx.scene.input.KeyCode`: Xử lý phím
- `javafx.scene.input.MouseEvent`: Xử lý chuột
- `javafx.scene.paint.Color`: Quản lý màu sắc
- `javafx.scene.text.Font`: Quản lý font
