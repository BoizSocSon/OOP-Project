# HighScoreDisplay

## Tổng quan
Lớp `HighScoreDisplay` là màn hình hiển thị điểm cao (High Score Display) của trò chơi Arkanoid. Lớp này chịu trách nhiệm tải, hiển thị danh sách top scores với giao diện bảng đẹp mắt, bao gồm hạng, tên người chơi, điểm số và ngày đạt được.

## Package
```java
package UI.Menu;
```

## Implements
```java
public class HighScoreDisplay implements Screen
```

## Thuộc tính

### Quản lý dữ liệu và tài nguyên
- `SpriteProvider sprites`: Nguồn cung cấp sprite (hình ảnh)
- `HighScoreManager highScoreManager`: Quản lý điểm cao (tải, lưu, quản lý scores)
- `Image logo`: Sprite logo game

### Font
- `String fontFamilyOptimus`: Font family Optimus
- `String fontFamilyGeneration`: Font family Generation
- `String fontFamilyEmulogic`: Font family Emulogic

### Hằng số Layout
- `WINDOW_WIDTH`, `WINDOW_HEIGHT`: Kích thước cửa sổ game
- `LOGO_WIDTH = 320`: Chiều rộng logo
- `LOGO_HEIGHT = 116`: Chiều cao logo
- `TABLE_START_Y = 250`: Vị trí Y bắt đầu bảng điểm
- `ROW_HEIGHT = 40`: Chiều cao mỗi hàng trong bảng

### Hằng số vị trí cột
- `COL_RANK_X = 65`: Vị trí X cột Hạng (RANK)
- `COL_NAME_X = COL_RANK_X + 100 = 165`: Vị trí X cột Tên (NAME)
- `COL_SCORE_X = COL_NAME_X + 150 = 315`: Vị trí X cột Điểm (SCORE)
- `COL_DATE_X = COL_SCORE_X + 130 = 445`: Vị trí X cột Ngày (DATE)

## Constructor

### HighScoreDisplay(SpriteProvider sprites)
Khởi tạo màn hình hiển thị điểm cao với:
- **sprites**: Đối tượng SpriteProvider để tải tài nguyên

**Công việc:**
1. Lưu tham chiếu sprites
2. Khởi tạo HighScoreManager (manager tự động tải scores từ file)
3. Gọi `loadAssets()` để tải tài nguyên

```java
HighScoreDisplay highScoreDisplay = new HighScoreDisplay(sprites);
```

## Phương thức chính

### loadAssets()
Tải các sprite và font cần thiết cho màn hình.

**Công việc:**
1. Tải logo từ SpriteProvider (`logo.png`)
2. Load 3 custom fonts:
   - **emulogic.ttf** - Font cho tiêu đề và số liệu
   - **generation.ttf** - Font phụ
   - **optimus.otf** - Font chính
3. Nếu lỗi: Sử dụng font mặc định (Courier New, Monospaced)

## Phương thức Screen Interface

### render(GraphicsContext gc)
Vẽ tất cả các thành phần UI lên màn hình.

**Các thành phần:**

1. **Gradient background**
   - Top: `rgb(10,10,30)`
   - Bottom: `rgb(30,10,50)`

2. **Logo** (center, Y = 120, size 320x116)

3. **Tiêu đề**: "HIGH SCORES" (center, Y = 200, font Emulogic size 30, màu trắng)

4. **Tiêu đề bảng (Header)**
   - Font: Optimus size 18, màu vàng
   - Các cột: RANK, NAME, SCORE, DATE
   - Đường phân cách (separator line) màu vàng, độ dày 2px

5. **Dữ liệu điểm số (Scores Data)**
   - Font: Optimus size 16, màu trắng
   - Lấy danh sách từ `highScoreManager.getAllScores()`
   - Vẽ từng hàng với:
     - **Rank**: Hạng (1, 2, 3, ...)
     - **Name**: Tên người chơi
     - **Score**: Điểm số (định dạng có dấu phẩy ngăn cách hàng nghìn)
     - **Date**: Ngày đạt được (formatted)
   - **Màu nền xen kẽ**: Hàng chẵn có màu tối mờ `rgb(20,20,40,0.5)`

6. **Hướng dẫn thoát**: "Press ESC to return to menu" (center, bottom, màu xám nhạt)

**Code vẽ header:**
```java
UIHelper.drawLeftAlignedText(gc, "RANK", COL_RANK_X, headerY, headerFont, headerColor);
UIHelper.drawLeftAlignedText(gc, "NAME", COL_NAME_X, headerY, headerFont, headerColor);
UIHelper.drawLeftAlignedText(gc, "SCORE", COL_SCORE_X, headerY, headerFont, headerColor);
UIHelper.drawLeftAlignedText(gc, "DATE", COL_DATE_X, headerY, headerFont, headerColor);

// Separator line
gc.setStroke(Color.YELLOW);
gc.setLineWidth(2);
gc.strokeLine(COL_RANK_X, headerY + 25, WINDOW_WIDTH - 60, headerY + 25);
```

**Code vẽ dữ liệu:**
```java
List<HighScoreEntry> scores = highScoreManager.getAllScores();
double rowY = TABLE_START_Y + 40;

for (HighScoreEntry entry : scores) {
    // Màu nền xen kẽ
    if (entry.getRank() % 2 == 0) {
        gc.setFill(Color.rgb(20, 20, 40, 0.5));
        gc.fillRect(COL_RANK_X - 10, rowY - 5, WINDOW_WIDTH - 180, ROW_HEIGHT - 5);
    }
    
    // Vẽ các cột
    UIHelper.drawLeftAlignedText(gc, String.valueOf(entry.getRank()),
            COL_RANK_X, rowY, dataFont, dataColor);
    UIHelper.drawLeftAlignedText(gc, entry.getPlayerName(),
            COL_NAME_X, rowY, dataFont, dataColor);
    UIHelper.drawLeftAlignedText(gc, String.format("%,d", entry.getScore()),
            COL_SCORE_X, rowY, dataFont, dataColor);
    UIHelper.drawLeftAlignedText(gc, entry.getFormattedDate(),
            COL_DATE_X, rowY, dataFont, dataColor);
    
    rowY += ROW_HEIGHT;
}
```

### update(long deltaTime)
Cập nhật logic màn hình (không có animation đặc biệt).

### handleKeyPressed(KeyCode keyCode)
Xử lý sự kiện nhấn phím (được xử lý bởi MainMenu để quay lại).

**Lưu ý:** Logic quay lại Menu được xử lý ở lớp MainMenu, không phải trong HighScoreDisplay.

### handleKeyReleased(KeyCode keyCode)
Không sử dụng.

### handleMouseClicked(MouseEvent event)
Không sử dụng.

### handleMouseMoved(MouseEvent event)
Không sử dụng.

### onEnter()
Xử lý khi màn hình được kích hoạt (vào màn hình).

**Lưu ý:** HighScoreManager tự động load từ file khi khởi tạo, không cần reload thủ công.

### onExit()
Xử lý khi màn hình bị vô hiệu hóa (thoát màn hình).

**Công việc:**
- Dọn dẹp nếu cần thiết (hiện tại không cần)

## Phương thức công khai

### getHighScoreManager()
Lấy HighScoreManager instance để truy cập điểm số.

**Trả về:** Instance của HighScoreManager

**Sử dụng:**
```java
HighScoreManager manager = highScoreDisplay.getHighScoreManager();
manager.addScore("PLAYER", 10000);
```

## Cách sử dụng

### Ví dụ khởi tạo
```java
// Trong MainMenu
SpriteProvider sprites = new SpriteProvider(...);
HighScoreDisplay highScoreDisplay = new HighScoreDisplay(sprites);
```

### Ví dụ tích hợp với MainMenu
```java
// Khi người chơi chọn HIGH SCORE
private void onHighScore() {
    showingHighScore = true;
    highScoreDisplay.onEnter();
}

// Trong render()
if (showingHighScore) {
    highScoreDisplay.render(gc);
    return;
}

// Trong update()
if (showingHighScore) {
    highScoreDisplay.update(deltaTime);
    return;
}

// Trong handleKeyPressed()
if (showingHighScore) {
    if (keyCode == KeyCode.ESCAPE) {
        showingHighScore = false; // Quay lại menu
    }
    return;
}
```

### Ví dụ thêm điểm mới
```java
// Sau khi game over
HighScoreManager manager = highScoreDisplay.getHighScoreManager();
String playerName = mainMenu.getPlayerName();
int finalScore = gameManager.getScore();

manager.addScore(playerName, finalScore);
// HighScoreManager tự động lưu vào file
```

## Thiết kế UI

### Layout
```
┌─────────────────────────────────────────┐
│              [LOGO]                     │
│                                         │
│          HIGH SCORES                    │
│                                         │
│  RANK   NAME       SCORE        DATE    │
│  ───────────────────────────────────────│
│  1      ALICE      15,000    01/01/2025│ (nền tối)
│  2      BOB        12,500    02/01/2025│
│  3      CHARLIE    10,000    03/01/2025│ (nền tối)
│  4      DAVID       8,500    04/01/2025│
│  5      EVE         7,000    05/01/2025│ (nền tối)
│  ...                                    │
│                                         │
│   Press ESC to return to menu           │
└─────────────────────────────────────────┘
```

### Bảng layout chi tiết

| Cột | Vị trí X | Nội dung | Ví dụ |
|-----|----------|----------|-------|
| RANK | 65 | Hạng | 1, 2, 3 |
| NAME | 165 | Tên người chơi | PLAYER, JOHN |
| SCORE | 315 | Điểm số | 10,000 |
| DATE | 445 | Ngày | 09/11/2025 |

### Màu sắc

- **Background**: Gradient từ tối đến sáng
- **Logo**: Trung tâm, Y = 120
- **Tiêu đề**: Trắng, font Emulogic size 30
- **Header bảng**: Vàng, font Optimus size 18
- **Separator line**: Vàng, độ dày 2px
- **Dữ liệu**: Trắng, font Optimus size 16
- **Nền xen kẽ**: `rgb(20,20,40,0.5)` cho hàng chẵn
- **Hướng dẫn**: Xám nhạt

## Tính năng đặc biệt

### 1. Định dạng số đẹp
Sử dụng `String.format("%,d", score)` để thêm dấu phẩy ngăn cách hàng nghìn:
- 10000 → 10,000
- 1234567 → 1,234,567

### 2. Màu nền xen kẽ
Hàng chẵn có màu nền tối mờ để dễ đọc:
```java
if (entry.getRank() % 2 == 0) {
    gc.setFill(Color.rgb(20, 20, 40, 0.5));
    gc.fillRect(...);
}
```

### 3. Tự động tải và lưu
HighScoreManager tự động:
- Tải điểm từ file khi khởi tạo
- Lưu điểm vào file khi thêm mới
- Sắp xếp điểm theo thứ tự giảm dần
- Giới hạn số lượng scores (thường là 10)

### 4. Ngày giờ formatted
Sử dụng `entry.getFormattedDate()` để hiển thị ngày theo định dạng đẹp:
```java
// Ví dụ: "09/11/2025" hoặc "09-11-2025"
```

### 5. Tích hợp dễ dàng
Chỉ cần truyền SpriteProvider, không cần truyền thêm dependencies phức tạp.

## Dữ liệu HighScoreEntry

Mỗi entry trong bảng điểm có:
- **rank**: int - Hạng (1, 2, 3, ...)
- **playerName**: String - Tên người chơi
- **score**: int - Điểm số
- **date**: LocalDateTime hoặc Date - Ngày đạt được
- **formattedDate**: String - Ngày đã định dạng

## Luồng dữ liệu

```
Game Over → MainMenu.getPlayerName() → HighScoreManager.addScore()
    ↓
HighScoreManager lưu vào file
    ↓
User chọn HIGH SCORE → HighScoreDisplay.render()
    ↓
HighScoreManager.getAllScores() → Hiển thị trên màn hình
```

## Best Practices

1. **Không reload thủ công**: HighScoreManager tự động tải khi khởi tạo
2. **Format điểm đẹp**: Luôn sử dụng `String.format("%,d", score)`
3. **Màu nền xen kẽ**: Giúp bảng dễ đọc hơn
4. **Left-aligned text**: Dùng `drawLeftAlignedText()` cho tất cả các cột
5. **Separator line rõ ràng**: Phân tách header và data

## Dependencies
- `Engine.HighScoreManager`: Quản lý điểm cao
- `Engine.HighScoreManager.HighScoreEntry`: Đại diện cho một entry điểm
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
- `java.util.List`: Danh sách scores

## Mở rộng trong tương lai

### Ý tưởng cải tiến
1. **Pagination**: Nếu có nhiều hơn 10 scores
2. **Filter**: Lọc theo ngày, tháng, năm
3. **Highlight**: Highlight entry của người chơi hiện tại
4. **Animation**: Hiệu ứng khi entry mới được thêm
5. **Sound effect**: Âm thanh khi hiển thị bảng điểm
6. **Export**: Xuất bảng điểm ra file CSV hoặc PDF
