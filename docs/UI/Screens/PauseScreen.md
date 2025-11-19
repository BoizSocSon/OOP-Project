# PauseScreen

## Tổng quan
Lớp `PauseScreen` là màn hình Tạm dừng (Pause Screen) được hiển thị khi game bị tạm dừng (nhấn phím PAUSE/ESC). Màn hình này hiển thị thông tin vòng chơi hiện tại, điểm số, số mạng và cung cấp tùy chọn để người chơi tiếp tục hoặc thoát về menu.

## Package
```java
package UI.Screens;
```

## Implements
```java
public class PauseScreen implements Screen
```

## Thuộc tính

### Quản lý tài nguyên
- `SpriteProvider sprites`: Nguồn cung cấp sprite (hình ảnh)
- `Image logo`: Sprite logo game

### Font
- `String fontFamilyOptimus`: Font family Optimus
- `String fontFamilyGeneration`: Font family Generation
- `String fontFamilyEmulogic`: Font family Emulogic

### Thông tin trạng thái game
- `int currentRound`: Vòng chơi hiện tại
- `String roundName`: Tên vòng chơi (ví dụ: "Beginner's Luck", "Tower of Power")
- `int currentScore`: Điểm số hiện tại
- `int currentLives`: Số mạng hiện tại

### Hằng số Layout
- `WINDOW_WIDTH`, `WINDOW_HEIGHT`: Kích thước cửa sổ game
- `LOGO_WIDTH = 320`: Chiều rộng logo được điều chỉnh
- `LOGO_HEIGHT = 116`: Chiều cao logo (giữ tỷ lệ)

## Constructor

### PauseScreen(SpriteProvider sprites)
Khởi tạo màn hình Pause với:
- **sprites**: SpriteProvider để lấy tài nguyên hình ảnh

**Công việc:**
1. Lưu tham chiếu sprites
2. Gọi `loadAssets()` để tải tài nguyên

```java
PauseScreen pauseScreen = new PauseScreen(sprites);
```

## Phương thức chính

### loadAssets()
Tải các sprite và font cần thiết cho màn hình.

**Công việc:**
1. Tải logo từ SpriteProvider (`logo.png`)
2. Load 3 custom fonts:
   - **emulogic.ttf** - Font cho tiêu đề
   - **generation.ttf** - Font phụ
   - **optimus.otf** - Font chính
3. Nếu lỗi: Sử dụng font mặc định (Courier New, Monospaced)

### setGameInfo(int round, String roundName, int score, int lives)
Thiết lập thông tin game để màn hình hiển thị.

**Tham số:**
- **round**: Vòng chơi hiện tại (số)
- **roundName**: Tên vòng chơi
- **score**: Điểm hiện tại
- **lives**: Số mạng hiện tại

**Sử dụng:**
```java
pauseScreen.setGameInfo(
    currentRound,
    roundsManager.getCurrentRoundName(),
    scoreManager.getScore(),
    livesManager.getLives()
);
```

## Phương thức Screen Interface

### render(GraphicsContext gc)
Vẽ tất cả các thành phần UI lên màn hình.

**Các thành phần:**

1. **Lớp phủ bán trong suốt** (làm tối màn hình nền game)
   - Màu: `rgb(0,0,0,0.7)`
   - Phủ toàn bộ màn hình

2. **Hộp thông báo chính**
   - Kích thước: 400x350
   - Căn giữa màn hình
   - Màu nền: `rgb(20,20,40,0.95)`
   - Viền cyan, độ dày 3px

3. **Logo** (bên trong hộp, top)

4. **Tiêu đề**: "GAME PAUSED" (vàng, font Emulogic size 32)

5. **Thông tin game**:
   - **Round**: Số vòng và tên vòng (ví dụ: "Round: 1 - \"Beginner's Luck\"")
   - **Score**: Điểm số (định dạng có dấu phẩy)
   - **Lives**: Số mạng (hiển thị bằng biểu tượng trái tim ♥)

6. **Hướng dẫn**:
   - "Press SPACE to continue" (tiếp tục game)
   - "Press ESC to return to menu" (quay về menu)

**Code vẽ thông tin:**
```java
// Thông tin vòng chơi
UIHelper.drawCenteredText(gc, String.format("Round: %d - \"%s\"", currentRound, roundName),
        WINDOW_WIDTH / 2, infoY,
        infoFont, infoColor);

// Thông tin điểm số
UIHelper.drawCenteredText(gc, String.format("Score: %,d", currentScore),
        WINDOW_WIDTH / 2, infoY + lineSpacing,
        infoFont, infoColor);

// Thông tin số mạng
String livesStr = "Lives: " + getHeartString(currentLives);
UIHelper.drawCenteredText(gc, livesStr,
        WINDOW_WIDTH / 2, infoY + lineSpacing * 2,
        infoFont, Color.RED);
```

### getHeartString(int lives)
Tạo chuỗi biểu tượng trái tim (♥) tương ứng với số mạng.

**Tham số:**
- **lives**: Số mạng hiện tại

**Trả về:** Chuỗi ký tự trái tim (ví dụ: "♥ ♥ ♥")

**Cơ chế:**
```java
private String getHeartString(int lives) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < lives; i++) {
        sb.append("♥ ");
    }
    return sb.toString().trim();
}
```

**Ví dụ:**
- lives = 3 → "♥ ♥ ♥"
- lives = 1 → "♥"
- lives = 5 → "♥ ♥ ♥ ♥ ♥"

### update(long deltaTime)
Cập nhật logic màn hình (không có animation).

**Lưu ý:** PauseScreen không có animation nên phương thức này rỗng.

### handleKeyPressed(KeyCode keyCode)
Xử lý sự kiện nhấn phím (logic chuyển trạng thái được xử lý bởi lớp GameManager/MainGameLoop).

**Các phím thường được xử lý ở GameManager:**
- `SPACE`: Tiếp tục game (resume)
- `ESC`: Quay về menu

### handleKeyReleased(KeyCode keyCode)
Không sử dụng.

### handleMouseClicked(MouseEvent event)
Không sử dụng.

### handleMouseMoved(MouseEvent event)
Không sử dụng.

### onEnter()
Xử lý khi màn hình được kích hoạt (vào trạng thái Pause).

**Công việc:**
- Có thể thêm logic lưu trạng thái game tạm thời tại đây

### onExit()
Xử lý khi màn hình bị vô hiệu hóa (thoát trạng thái Pause).

**Công việc:**
- Dọn dẹp nếu cần thiết

## Cách sử dụng

### Ví dụ khởi tạo
```java
// Trong GameManager hoặc StateManager
SpriteProvider sprites = new SpriteProvider(...);
PauseScreen pauseScreen = new PauseScreen(sprites);
```

### Ví dụ thiết lập thông tin và hiển thị
```java
// Khi người chơi nhấn phím PAUSE/ESC
if (keyCode == KeyCode.ESCAPE && currentState == GameState.PLAYING) {
    // Thiết lập thông tin
    pauseScreen.setGameInfo(
        roundsManager.getCurrentRound(),
        roundsManager.getCurrentRoundName(),
        scoreManager.getScore(),
        3 // số mạng hiện tại
    );
    
    // Chuyển state
    previousState = GameState.PLAYING;
    stateManager.setState(GameState.PAUSED);
    pauseScreen.onEnter();
}
```

### Ví dụ trong Game Loop
```java
// Render
if (currentState == GameState.PAUSED) {
    // Vẽ game ở phía sau (không update)
    gamePlayScreen.render(gc);
    // Vẽ pause screen ở trên
    pauseScreen.render(gc);
}

// Update
if (currentState == GameState.PAUSED) {
    pauseScreen.update(deltaTime);
    // KHÔNG update game logic
}

// Input
if (currentState == GameState.PAUSED) {
    if (keyCode == KeyCode.SPACE) {
        // Tiếp tục game
        pauseScreen.onExit();
        stateManager.setState(previousState);
    } else if (keyCode == KeyCode.ESCAPE) {
        // Quay về menu
        pauseScreen.onExit();
        stateManager.setState(GameState.MENU);
    }
}
```

### Ví dụ render với game ở phía sau
```java
@Override
public void render(GraphicsContext gc) {
    // Vẽ game state bên dưới
    renderGameState(gc);
    
    // Nếu đang pause, vẽ pause screen lên trên
    if (isPaused) {
        pauseScreen.render(gc);
    }
}
```

## Thiết kế UI

### Layout
```
┌──────────────────────────────────────┐
│ (Lớp phủ tối bán trong suốt)         │
│                                      │
│  ┌──────────────────────────────┐   │
│  │        [LOGO]                │   │
│  │                              │   │
│  │      GAME PAUSED             │   │
│  │                              │   │
│  │  Round: 2 - "Tower of Power" │   │
│  │  Score: 5,320                │   │
│  │  Lives: ♥ ♥ ♥                │   │
│  │                              │   │
│  │  Press SPACE to continue     │   │
│  │  Press ESC to return to menu │   │
│  └──────────────────────────────┘   │
│                                      │
└──────────────────────────────────────┘
```

### Màu sắc

- **Overlay**: Đen bán trong suốt `rgb(0,0,0,0.7)` - làm tối game phía sau
- **Box**: Nền tối `rgb(20,20,40,0.95)`, viền cyan 3px
- **Logo**: Top của box
- **Tiêu đề**: Vàng, font Emulogic size 32
- **Thông tin**: Trắng, font Optimus size 20
- **Lives**: Đỏ (trái tim)
- **Hướng dẫn**: Xám nhạt

## Tính năng đặc biệt

### 1. Overlay bán trong suốt
Lớp phủ làm tối game phía sau nhưng vẫn nhìn thấy:
```java
gc.setFill(Color.rgb(0, 0, 0, 0.7));
gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
```

**Hiệu quả:**
- Tạo focus vào pause screen
- Người chơi vẫn thấy trạng thái game
- Không phải render lại toàn bộ

### 2. Biểu tượng trái tim cho Lives
Thay vì hiển thị số, sử dụng ký tự Unicode "♥":
- Trực quan hơn
- Dễ nhìn hơn
- Màu đỏ nổi bật

### 3. Thông tin vòng chơi chi tiết
Hiển thị cả số và tên vòng:
```
Round: 2 - "Tower of Power"
```

### 4. Định dạng điểm đẹp
Sử dụng `String.format("%,d", score)`:
- 5320 → 5,320
- 12500 → 12,500

### 5. Không có animation
PauseScreen không có animation để:
- Tiết kiệm CPU khi pause
- Tạo cảm giác "đông lạnh"
- Focus vào thông tin

## Luồng xử lý Pause

```
PLAYING State + ESC key
    ↓
setGameInfo(round, name, score, lives)
    ↓
setState(PAUSED)
    ↓
pauseScreen.onEnter()
    ↓
Render game + Pause overlay
    ↓
SPACE key → Resume → setState(PLAYING)
ESC key → Menu → setState(MENU)
```

## Best Practices

1. **Render game phía sau**: Để người chơi nhìn thấy trạng thái
2. **Không update game logic**: Khi pause, dừng tất cả
3. **Lưu previous state**: Để resume đúng state
4. **Gọi setGameInfo() mỗi lần pause**: Đảm bảo thông tin mới nhất
5. **Overlay mờ vừa đủ**: 0.7 alpha là lý tưởng
6. **Hướng dẫn rõ ràng**: 2 options (SPACE/ESC)

## So sánh với các Screen khác

| Tính năng | PauseScreen | GameOverScreen | WinScreen |
|-----------|-------------|----------------|-----------|
| Overlay | Có (0.7 alpha) | Không | Không |
| Background | Trong suốt | Đỏ đậm/đen | Xanh lễ hội |
| Animation | Không | Ngôi sao quay | Ngôi sao quay |
| Tương tác | 2 options (SPACE/ESC) | 1 option (ENTER) | 1 option (ENTER) |
| Thông tin | Round, Score, Lives | Score, High Score, Round | Score, High Score, Rounds |
| Mục đích | Tạm dừng | Kết thúc (thua) | Kết thúc (thắng) |

## Mở rộng trong tương lai

### Ý tưởng cải tiến
1. **Resume countdown**: Đếm ngược 3-2-1 trước khi resume
2. **Settings quick access**: Thêm nút Settings trong pause
3. **Mini preview**: Hiển thị screenshot game ở background
4. **Pause time**: Hiển thị thời gian đã chơi
5. **Power-ups active**: Danh sách power-ups đang hoạt động
6. **Statistics**: Bricks destroyed, accuracy, etc.
7. **Music control**: Volume slider trong pause screen

## Xử lý Edge Cases

### 1. Pause trong transition
```java
// Chỉ cho phép pause khi đang PLAYING
if (keyCode == KeyCode.ESCAPE && currentState == GameState.PLAYING) {
    // pause
}
```

### 2. Multiple pause presses
```java
// Kiểm tra state trước khi pause
if (!isPaused) {
    pause();
}
```

### 3. Resume to correct state
```java
// Lưu state trước khi pause
GameState previousState = currentState;

// Resume
currentState = previousState;
```

## Dependencies
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
