# WinScreen

## Tổng quan
Lớp `WinScreen` là màn hình Chiến thắng (Win Screen) được hiển thị khi người chơi hoàn thành tất cả các cấp độ (rounds) trong game. Màn hình này hiển thị thông báo chiến thắng, điểm cuối cùng, điểm cao nhất (high score), tổng số vòng đã hoàn thành, và thông báo nếu người chơi đạt high score mới với animation ngôi sao quay.

## Package
```java
package UI.Screens;
```

## Implements
```java
public class WinScreen implements Screen
```

## Thuộc tính

### Quản lý tài nguyên
- `SpriteProvider sprites`: Nguồn cung cấp sprite để tải hình ảnh
- `HighScoreManager highScoreManager`: Quản lý điểm cao để lấy và kiểm tra high score
- `Image logo`: Sprite logo game

### Font
- `String fontFamilyOptimus`: Font family Optimus
- `String fontFamilyGeneration`: Font family Generation
- `String fontFamilyEmulogic`: Font family Emulogic

### Thông tin kết quả game
- `int finalScore`: Điểm số cuối cùng đạt được
- `int highScore`: Điểm cao nhất hiện tại
- `int totalRounds`: Tổng số vòng chơi đã hoàn thành (tất cả các vòng)
- `boolean isNewHighScore`: Cờ kiểm tra xem điểm cuối cùng có phải là high score mới không

### Animation
- `double starRotation = 0`: Góc quay hiện tại của các ngôi sao trang trí (dùng cho hiệu ứng animation)

### Hằng số Layout
- `WINDOW_WIDTH`, `WINDOW_HEIGHT`: Kích thước cửa sổ game
- `LOGO_WIDTH = 320`: Chiều rộng logo được điều chỉnh
- `LOGO_HEIGHT = 116`: Chiều cao logo (giữ tỷ lệ)

## Constructor

### WinScreen(SpriteProvider sprites, HighScoreManager highScoreManager)
Khởi tạo màn hình Win với:
- **sprites**: SpriteProvider để lấy tài nguyên hình ảnh
- **highScoreManager**: HighScoreManager để kiểm tra high score

**Công việc:**
1. Lưu tham chiếu sprites và highScoreManager
2. Gọi `loadAssets()` để tải tài nguyên

```java
WinScreen winScreen = new WinScreen(sprites, highScoreManager);
```

## Phương thức chính

### loadAssets()
Tải các sprite và font cần thiết cho màn hình.

**Công việc:**
1. Tải logo từ SpriteProvider (`logo.png`)
2. Load 3 custom fonts:
   - **emulogic.ttf** - Font cho tiêu đề
   - **generation.ttf** - Font cho subtitle
   - **optimus.otf** - Font chính
3. Nếu lỗi: Sử dụng font mặc định (Courier New, Monospaced)

### setGameResult(int score, int rounds)
Thiết lập kết quả game để hiển thị trên màn hình.

**Tham số:**
- **score**: Điểm cuối cùng người chơi đạt được
- **rounds**: Tổng số vòng chơi đã hoàn thành (tất cả các vòng)

**Công việc:**
1. Lưu finalScore và totalRounds
2. Lấy điểm cao nhất hiện tại từ HighScoreManager
3. Kiểm tra xem điểm mới có lọt vào top không

**Sử dụng:**
```java
winScreen.setGameResult(playerScore, totalRoundsCompleted);
```

**Phương thức này được gọi bởi GameManager khi chuyển sang trạng thái WIN.**

## Phương thức Screen Interface

### render(GraphicsContext gc)
Vẽ tất cả các thành phần UI lên màn hình, bao gồm nền, hộp thông báo, và thống kê.

**Các thành phần:**

1. **Gradient background** (màu lễ hội/chiến thắng)
   - Top: `rgb(10,20,40)`
   - Bottom: `rgb(40,10,60)`

2. **Hộp thông báo chính**
   - Kích thước: 450x450
   - Căn giữa màn hình
   - Màu nền: `rgb(20,40,60,0.95)`
   - Viền vàng (GOLD), độ dày 3px

3. **Logo** (bên trong hộp, top)

4. **Tiêu đề chính**: "CONGRATULATIONS!" (vàng, font Emulogic size 27)

5. **Tiêu đề phụ**: "You Won!" (vàng, font Generation size 24)

6. **Thống kê game**:
   - **Your Score**: Điểm cuối cùng (định dạng có dấu phẩy)
   - **High Score**: Điểm cao nhất
   - **Rounds Completed**: Tổng số vòng đã hoàn thành

7. **Thông báo High Score mới** (nếu có):
   - 2 ngôi sao quay trang trí hai bên
   - Text "NEW HIGH SCORE!" (vàng GOLD, font Emulogic size 21)

8. **Hướng dẫn**: "Press ENTER to return to menu" (xám nhạt)

**Code vẽ tiêu đề:**
```java
// Tiêu đề chính
UIHelper.drawCenteredText(gc, "CONGRATULATIONS!",
        WINDOW_WIDTH / 2, boxY + 140,
        Font.font(fontFamilyEmulogic, 27), Color.GOLD);

// Tiêu đề phụ
UIHelper.drawCenteredText(gc, "You Won!",
        WINDOW_WIDTH / 2, boxY + 180,
        Font.font(fontFamilyGeneration, 24), Color.YELLOW);
```

**Code vẽ stats:**
```java
// Điểm của bạn
UIHelper.drawCenteredText(gc, String.format("Your Score: %,d", finalScore),
        WINDOW_WIDTH / 2, statsY,
        statsFont, statsColor);

// Điểm cao nhất
UIHelper.drawCenteredText(gc, String.format("High Score: %,d", highScore),
        WINDOW_WIDTH / 2, statsY + lineSpacing,
        statsFont, statsColor);

// Tổng số vòng đã hoàn thành
UIHelper.drawCenteredText(gc, String.format("Rounds Completed: %d", totalRounds),
        WINDOW_WIDTH / 2, statsY + lineSpacing * 2,
        statsFont, statsColor);
```

### drawRotatingStar(GraphicsContext gc, double x, double y)
Vẽ một ngôi sao quay tại tọa độ xác định để tạo hiệu ứng animation.

**Cơ chế:** (Giống GameOverScreen)
1. Lưu trạng thái GC (`gc.save()`)
2. Di chuyển hệ tọa độ về tâm ngôi sao (`gc.translate(x, y)`)
3. Xoay GC theo góc quay hiện tại (`gc.rotate(starRotation)`)
4. Vẽ ngôi sao (ký tự Unicode "★") tại (0, 0)
5. Khôi phục trạng thái GC (`gc.restore()`)

**Tham số:**
- **x**: Tọa độ X của tâm ngôi sao
- **y**: Tọa độ Y của tâm ngôi sao

**Màu sắc:** GOLD (vàng kim) cho WinScreen (khác với vàng thường ở GameOverScreen)

### update(long deltaTime)
Cập nhật logic màn hình cho mỗi frame (chủ yếu dùng để cập nhật animation).

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
Xử lý sự kiện nhấn phím (logic chuyển trạng thái được xử lý ở MainGameLoop).

**Lưu ý:** Thông thường ENTER sẽ được xử lý ở GameManager để chuyển về MENU state.

### handleKeyReleased(KeyCode keyCode)
Không sử dụng.

### handleMouseClicked(MouseEvent event)
Không sử dụng.

### handleMouseMoved(MouseEvent event)
Không sử dụng.

### onEnter()
Xử lý khi màn hình được kích hoạt (vào trạng thái Win).

**Công việc:**
- Đặt lại `starRotation = 0`
- Có thể thêm logic phát nhạc chiến thắng tại đây

### onExit()
Xử lý khi màn hình bị vô hiệu hóa (thoát Win Screen).

**Công việc:**
- Dọn dẹp tài nguyên hoặc dừng nhạc nếu cần thiết

## Cách sử dụng

### Ví dụ khởi tạo
```java
// Trong GameManager hoặc StateManager
SpriteProvider sprites = new SpriteProvider(...);
HighScoreManager highScoreManager = new HighScoreManager();
WinScreen winScreen = new WinScreen(sprites, highScoreManager);
```

### Ví dụ thiết lập kết quả và hiển thị
```java
// Khi người chơi hoàn thành tất cả rounds
if (currentRound > totalRounds) {
    // Thiết lập kết quả
    winScreen.setGameResult(currentScore, totalRounds);
    
    // Lưu điểm vào HighScoreManager
    String playerName = mainMenu.getPlayerName();
    highScoreManager.addScore(playerName, currentScore);
    
    // Chuyển state
    stateManager.setState(GameState.WIN);
    winScreen.onEnter();
    
    // Phát nhạc chiến thắng (nếu có)
    audioManager.playVictoryMusic();
}
```

### Ví dụ trong Game Loop
```java
// Render
if (currentState == GameState.WIN) {
    winScreen.render(gc);
}

// Update
if (currentState == GameState.WIN) {
    winScreen.update(deltaTime);
}

// Input
if (currentState == GameState.WIN) {
    if (keyCode == KeyCode.ENTER) {
        winScreen.onExit();
        audioManager.stopVictoryMusic();
        stateManager.setState(GameState.MENU);
    }
}
```

## Thiết kế UI

### Layout
```
┌──────────────────────────────────────┐
│   (Nền xanh lễ hội gradient)         │
│                                      │
│  ┌──────────────────────────────┐   │
│  │        [LOGO]                │   │
│  │                              │   │
│  │    CONGRATULATIONS!          │   │
│  │      You Won!                │   │
│  │                              │   │
│  │  Your Score: 25,000          │   │
│  │  High Score: 25,000          │   │
│  │  Rounds Completed: 4         │   │
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

- **Background**: Gradient xanh lễ hội (tâm trạng chiến thắng)
- **Box**: Nền `rgb(20,40,60,0.95)`, viền vàng GOLD 3px
- **Logo**: Top của box
- **Tiêu đề chính**: Vàng GOLD, font Emulogic size 27
- **Tiêu đề phụ**: Vàng YELLOW, font Generation size 24
- **Stats**: Trắng, font Optimus size 22
- **High Score mới**: Vàng GOLD, font Emulogic size 21
- **Ngôi sao**: Vàng GOLD, quay animation
- **Hướng dẫn**: Xám nhạt

## Tính năng đặc biệt

### 1. Animation ngôi sao quay
Khi đạt high score mới:
- 2 ngôi sao (★) trang trí hai bên
- Quay liên tục 2 độ/frame
- Màu vàng GOLD (cao cấp hơn vàng thường)

**Animation giống GameOverScreen nhưng màu khác.**

### 2. Tiêu đề kép
Hai dòng tiêu đề để nhấn mạnh:
- "CONGRATULATIONS!" (chính thức)
- "You Won!" (thân thiện)

### 3. Định dạng điểm đẹp
Sử dụng `String.format("%,d", score)`:
- 25000 → 25,000
- 1234567 → 1,234,567

### 4. Gradient lễ hội
Màu xanh lễ hội tạo cảm giác vui mừng, chiến thắng:
- Top: `rgb(10,20,40)` - Xanh đậm
- Bottom: `rgb(40,10,60)` - Tím nhạt

### 5. Thống kê hoàn chỉnh
Hiển thị tổng số vòng đã hoàn thành, không chỉ vòng đạt được như GameOverScreen.

## So sánh với GameOverScreen

| Tính năng | WinScreen | GameOverScreen |
|-----------|-----------|----------------|
| Background | Xanh lễ hội | Đỏ đậm/đen |
| Viền box | Vàng GOLD | Đỏ |
| Tiêu đề | "CONGRATULATIONS!" + "You Won!" | "GAME OVER" |
| Màu tiêu đề | Vàng GOLD | Đỏ |
| Box size | 450x450 | 450x400 |
| Stats | Your Score, High Score, Rounds Completed | Your Score, High Score, Round Reached |
| Ngôi sao | Vàng GOLD | Vàng |
| Tâm trạng | Vui, lễ hội, chiến thắng | Buồn, nghiêm trọng, thua cuộc |

## Luồng dữ liệu

```
Complete all rounds → setGameResult(score, totalRounds)
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
4. **Phát victory music**: Tạo cảm giác chiến thắng hoàn toàn
5. **Định dạng điểm đẹp**: Luôn dùng `%,d` cho số lớn
6. **Gradient phù hợp tâm trạng**: Xanh lễ hội cho chiến thắng
7. **Màu GOLD cho highlight**: Cao cấp hơn màu vàng thường

## Điều kiện chiến thắng

```java
// Kiểm tra chiến thắng
if (allBricksDestroyed && currentRound == totalRounds) {
    // Win!
    winScreen.setGameResult(score, totalRounds);
    highScoreManager.addScore(playerName, score);
    stateManager.setState(GameState.WIN);
}
```

## Mở rộng trong tương lai

### Ý tưởng cải tiến
1. **Victory animation**: Pháo hoa, confetti rơi
2. **Credits roll**: Hiển thị credits như phim
3. **Statistics detail**: Tổng bricks destroyed, accuracy, time
4. **Achievement system**: Hiển thị achievements đạt được
5. **Share victory**: Chia sẻ lên mạng xã hội
6. **Trophy display**: Hiển thị cúp/huy chương
7. **Replay value**: Điểm thưởng để chơi lại
8. **Leaderboard integration**: Kết nối với leaderboard online

## Edge Cases

### 1. Win với score thấp
```java
// Vẫn hiển thị thông báo win dù điểm thấp
// Không hiển thị "NEW HIGH SCORE!" nếu không phải
```

### 2. Win round cuối cùng
```java
// Kiểm tra chắc chắn là round cuối cùng
if (currentRound >= totalRounds) {
    // Trigger win
}
```

### 3. Multiple win triggers
```java
// Chỉ trigger win một lần
if (!hasWon) {
    triggerWin();
    hasWon = true;
}
```

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
