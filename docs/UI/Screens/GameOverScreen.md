# GameOverScreen

## Tổng quan
Lớp `GameOverScreen` là màn hình Game Over được hiển thị khi người chơi thua (hết mạng). Màn hình này hiển thị điểm cuối cùng, điểm cao nhất (high score), vòng chơi (round) đạt được, và thông báo nếu người chơi đạt high score mới với animation ngôi sao quay.

## Package
```java
package UI.Screens;
```

## Implements
```java
public class GameOverScreen implements Screen
```

## Thuộc tính

### Quản lý tài nguyên
- `SpriteProvider sprites`: Nguồn cung cấp sprite (hình ảnh)
- `HighScoreManager highScoreManager`: Quản lý điểm cao
- `Image logo`: Sprite logo game

### Font
- `String fontFamilyOptimus`: Font family Optimus
- `String fontFamilyGeneration`: Font family Generation
- `String fontFamilyEmulogic`: Font family Emulogic

### Thông tin kết quả game
- `int finalScore`: Điểm số cuối cùng đạt được
- `int highScore`: Điểm cao nhất hiện tại
- `int roundReached`: Vòng chơi đã đạt được
- `boolean isNewHighScore`: Cờ kiểm tra xem điểm cuối cùng có phải là high score mới không

### Animation
- `double starRotation = 0`: Góc quay hiện tại của các ngôi sao trang trí (dùng cho hiệu ứng animation)

### Hằng số Layout
- `WINDOW_WIDTH`, `WINDOW_HEIGHT`: Kích thước cửa sổ game
- `LOGO_WIDTH = 320`: Chiều rộng logo được điều chỉnh
- `LOGO_HEIGHT = 116`: Chiều cao logo (giữ tỷ lệ)

## Constructor

### GameOverScreen(SpriteProvider sprites, HighScoreManager highScoreManager)
Khởi tạo màn hình Game Over với:
- **sprites**: SpriteProvider để lấy tài nguyên hình ảnh
- **highScoreManager**: HighScoreManager để kiểm tra điểm cao nhất

**Công việc:**
1. Lưu tham chiếu sprites và highScoreManager
2. Gọi `loadAssets()` để tải tài nguyên

```java
GameOverScreen gameOverScreen = new GameOverScreen(sprites, highScoreManager);
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

### setGameResult(int score, int round)
Thiết lập kết quả game để hiển thị trên màn hình.

**Tham số:**
- **score**: Điểm cuối cùng người chơi đạt được
- **round**: Vòng chơi đã đạt được

**Công việc:**
1. Lưu finalScore và roundReached
2. Lấy điểm cao nhất hiện tại từ HighScoreManager
3. Kiểm tra xem điểm cuối cùng có phải là high score mới không

**Sử dụng:**
```java
gameOverScreen.setGameResult(playerScore, currentRound);
```

## Phương thức Screen Interface

### render(GraphicsContext gc)
Vẽ tất cả các thành phần UI lên màn hình.

**Các thành phần:**

1. **Gradient background** (màu đỏ đậm/đen)
   - Top: `rgb(20,0,0)`
   - Bottom: `rgb(50,10,10)`

2. **Hộp thông báo chính**
   - Kích thước: 450x400
   - Căn giữa màn hình
   - Màu nền: `rgb(30,10,10,0.95)`
   - Viền đỏ, độ dày 3px

3. **Logo** (bên trong hộp, top)

4. **Tiêu đề**: "GAME OVER" (đỏ, font Emulogic size 40)

5. **Thống kê game**:
   - **Your Score**: Điểm cuối cùng (định dạng có dấu phẩy)
   - **High Score**: Điểm cao nhất
   - **Round Reached**: Vòng chơi đạt được

6. **Thông báo High Score mới** (nếu có):
   - 2 ngôi sao quay trang trí hai bên
   - Text "NEW HIGH SCORE!" (vàng, font Emulogic size 21)

7. **Hướng dẫn**: "Press ENTER to return to menu" (xám nhạt)

**Code vẽ stats:**
```java
// Điểm cuối cùng
UIHelper.drawCenteredText(gc, String.format("Your Score: %,d", finalScore),
        WINDOW_WIDTH / 2, statsY,
        statsFont, statsColor);

// Điểm cao nhất
UIHelper.drawCenteredText(gc, String.format("High Score: %,d", highScore),
        WINDOW_WIDTH / 2, statsY + lineSpacing,
        statsFont, statsColor);

// Vòng chơi đạt được
UIHelper.drawCenteredText(gc, String.format("Round Reached: %d", roundReached),
        WINDOW_WIDTH / 2, statsY + lineSpacing * 2,
        statsFont, statsColor);
```

### drawRotatingStar(GraphicsContext gc, double x, double y)
Vẽ một ngôi sao quay tại tọa độ xác định để tạo hiệu ứng animation.

**Cơ chế:**
1. Lưu trạng thái GC (`gc.save()`)
2. Di chuyển hệ tọa độ về tâm ngôi sao (`gc.translate(x, y)`)
3. Xoay GC theo góc quay hiện tại (`gc.rotate(starRotation)`)
4. Vẽ ngôi sao (ký tự Unicode "★") tại (0, 0)
5. Khôi phục trạng thái GC (`gc.restore()`)

**Tham số:**
- **x**: Tọa độ X của tâm ngôi sao
- **y**: Tọa độ Y của tâm ngôi sao

**Vị trí ngôi sao:**
- Trái: `WINDOW_WIDTH / 2 - 180`
- Phải: `WINDOW_WIDTH / 2 + 180`

### update(long deltaTime)
Cập nhật logic màn hình (animation ngôi sao).

**Công việc:**
- Tăng `starRotation` thêm 2.0 độ mỗi frame
- Reset về 0 khi đạt 360 độ (vòng quay đầy đủ)

```java
starRotation += 2.0;
if (starRotation >= 360) {
    starRotation -= 360;
}
```

### handleKeyPressed(KeyCode keyCode)
Xử lý sự kiện nhấn phím (logic chuyển trạng thái được xử lý bởi lớp GameManager).

**Lưu ý:** Thông thường ENTER sẽ được xử lý ở GameManager để chuyển về MENU state.

### handleKeyReleased(KeyCode keyCode)
Không sử dụng.

### handleMouseClicked(MouseEvent event)
Không sử dụng.

### handleMouseMoved(MouseEvent event)
Không sử dụng.

### onEnter()
Xử lý khi màn hình được kích hoạt (vào trạng thái Game Over).

**Công việc:**
- Đặt lại `starRotation = 0`
- Có thể thêm logic lưu điểm cao vào file tại đây nếu chưa làm ở GameManager

### onExit()
Xử lý khi màn hình bị vô hiệu hóa (thoát Game Over).

**Công việc:**
- Dọn dẹp nếu cần thiết

## Cách sử dụng

### Ví dụ khởi tạo
```java
// Trong GameManager hoặc StateManager
SpriteProvider sprites = new SpriteProvider(...);
HighScoreManager highScoreManager = new HighScoreManager();
GameOverScreen gameOverScreen = new GameOverScreen(sprites, highScoreManager);
```

### Ví dụ thiết lập kết quả và hiển thị
```java
// Khi người chơi thua (hết mạng)
if (lives <= 0) {
    // Thiết lập kết quả
    gameOverScreen.setGameResult(currentScore, currentRound);
    
    // Lưu điểm vào HighScoreManager
    String playerName = mainMenu.getPlayerName();
    highScoreManager.addScore(playerName, currentScore);
    
    // Chuyển state
    stateManager.setState(GameState.GAME_OVER);
    gameOverScreen.onEnter();
}
```

### Ví dụ trong Game Loop
```java
// Render
if (currentState == GameState.GAME_OVER) {
    gameOverScreen.render(gc);
}

// Update
if (currentState == GameState.GAME_OVER) {
    gameOverScreen.update(deltaTime);
}

// Input
if (currentState == GameState.GAME_OVER) {
    if (keyCode == KeyCode.ENTER) {
        gameOverScreen.onExit();
        stateManager.setState(GameState.MENU);
    }
}
```

## Thiết kế UI

### Layout
```
┌──────────────────────────────────────┐
│   (Nền đỏ đậm/đen gradient)          │
│                                      │
│  ┌──────────────────────────────┐   │
│  │        [LOGO]                │   │
│  │                              │   │
│  │      GAME OVER               │   │
│  │                              │   │
│  │  Your Score: 12,500          │   │
│  │  High Score: 15,000          │   │
│  │  Round Reached: 3            │   │
│  │                              │   │
│  │  ★ NEW HIGH SCORE! ★         │   │
│  │  (nếu đạt high score mới)    │   │
│  │                              │   │
│  │  Press ENTER to return       │   │
│  └──────────────────────────────┘   │
│                                      │
└──────────────────────────────────────┘
```

### Màu sắc

- **Background**: Gradient đỏ đậm/đen (tâm trạng thua cuộc)
- **Box**: Nền tối `rgb(30,10,10,0.95)`, viền đỏ 3px
- **Logo**: Top của box
- **Tiêu đề**: Đỏ, font Emulogic size 40
- **Stats**: Trắng, font Optimus size 22
- **High Score mới**: Vàng, font Emulogic size 21
- **Ngôi sao**: Vàng, quay animation
- **Hướng dẫn**: Xám nhạt

## Tính năng đặc biệt

### 1. Animation ngôi sao quay
Khi đạt high score mới:
- 2 ngôi sao (★) trang trí hai bên
- Quay liên tục 2 độ/frame
- Màu vàng nổi bật

**Cơ chế rotation:**
```java
gc.save();
gc.translate(x, y);      // Di chuyển tâm
gc.rotate(starRotation); // Xoay
gc.fillText("★", 0, 0);  // Vẽ tại tâm
gc.restore();
```

### 2. Định dạng điểm đẹp
Sử dụng `String.format("%,d", score)` để thêm dấu phẩy:
- 12500 → 12,500
- 1234567 → 1,234,567

### 3. Gradient background tâm trạng
Màu đỏ đậm/đen tạo cảm giác thua cuộc, nghiêm trọng.

### 4. Thông báo có điều kiện
Chỉ hiển thị "NEW HIGH SCORE!" khi `isNewHighScore == true`.

### 5. Tích hợp HighScoreManager
- Tự động kiểm tra high score
- Không cần logic phức tạp ở màn hình

## Luồng dữ liệu

```
Game Over (lives <= 0) → setGameResult(score, round)
    ↓
HighScoreManager.getHighestScore()
HighScoreManager.isHighScore(score)
    ↓
render() → Hiển thị kết quả + animation
    ↓
update() → Cập nhật animation ngôi sao
    ↓
ENTER → onExit() → Quay về MENU
```

## Best Practices

1. **Gọi setGameResult() trước render()**: Đảm bảo dữ liệu sẵn sàng
2. **Lưu score vào HighScoreManager**: Làm ở GameManager trước khi chuyển state
3. **Reset animation onEnter()**: Đảm bảo animation bắt đầu từ 0
4. **Định dạng điểm đẹp**: Luôn dùng `%,d` cho số lớn
5. **Gradient phù hợp tâm trạng**: Đỏ đen cho thua cuộc

## So sánh với WinScreen

| Tính năng | GameOverScreen | WinScreen |
|-----------|---------------|-----------|
| Background | Đỏ đậm/đen | Xanh lễ hội |
| Viền box | Đỏ | Vàng (GOLD) |
| Tiêu đề | "GAME OVER" (đỏ) | "CONGRATULATIONS!" (vàng) |
| Thống kê | Score, High Score, Round Reached | Score, High Score, Rounds Completed |
| Animation | Ngôi sao vàng | Ngôi sao vàng |
| Tâm trạng | Buồn, nghiêm trọng | Vui, chiến thắng |

## Mở rộng trong tương lai

### Ý tưởng cải tiến
1. **Replay button**: Thêm nút "PLAY AGAIN" để chơi lại nhanh
2. **Fade-in effect**: Màn hình từ từ hiện ra
3. **Sound effect**: Âm thanh game over
4. **Particle effect**: Hiệu ứng hạt rơi
5. **Stats mở rộng**: Thêm thống kế như bricks destroyed, time played
6. **Share score**: Chia sẻ điểm lên mạng xã hội
7. **Death animation**: Hiệu ứng khi ball rơi xuống

## Dependencies
- `Engine.HighScoreManager`: Quản lý điểm cao
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
