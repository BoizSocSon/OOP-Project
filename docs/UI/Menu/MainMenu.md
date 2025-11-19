# MainMenu

## Tổng quan
Lớp `MainMenu` là màn hình Menu chính của trò chơi Arkanoid. Lớp này chịu trách nhiệm hiển thị các tùy chọn chính của game (Bắt đầu chơi, Điểm cao, Cài đặt, Thoát), xử lý tương tác của người dùng thông qua bàn phím và chuột, quản lý việc chuyển đổi giữa các màn hình con, và cho phép người chơi nhập tên.

## Package
```java
package UI.Menu;
```

## Implements
```java
public class MainMenu implements Screen
```

## Thuộc tính

### Quản lý trạng thái và tài nguyên
- `StateManager stateManager`: Quản lý trạng thái game, dùng để chuyển từ MENU sang PLAYING
- `SpriteProvider sprites`: Nguồn cung cấp sprite (hình ảnh) cho các thành phần UI
- `HighScoreDisplay highScoreDisplay`: Màn hình hiển thị điểm cao (màn hình con)
- `SettingsScreen settingsScreen`: Màn hình cài đặt (màn hình con)

### Thành phần UI
- `List<Button> buttons`: Danh sách các nút bấm chính trong menu
- `List<PowerUpDisplay> leftPowerUps`: Danh sách hiển thị PowerUp bên trái (trang trí animation)
- `List<PowerUpDisplay> rightPowerUps`: Danh sách hiển thị PowerUp bên phải (trang trí animation)
- `PowerUpDisplay middlePowerUp`: PowerUp hiển thị ở giữa màn hình
- `Image logo`: Logo game được hiển thị ở trên cùng
- `TextField playerNameField`: TextField để người chơi nhập tên
- `Pane parentPane`: Pane chứa canvas để quản lý TextField

### Trạng thái
- `int selectedButtonIndex`: Chỉ số của nút đang được chọn bằng phím điều hướng
- `boolean textFieldFocused`: Trạng thái focus của TextField
- `boolean showingHighScore`: Cờ kiểm tra đang hiển thị màn hình điểm cao
- `boolean showingSettings`: Cờ kiểm tra đang hiển thị màn hình cài đặt

### Font
- `String fontFamily`: Lưu tên font family để tái sử dụng

### Hằng số Layout
- `WINDOW_WIDTH`, `WINDOW_HEIGHT`: Kích thước cửa sổ game
- `BUTTON_WIDTH = 180`: Chiều rộng nút
- `BUTTON_HEIGHT = 60`: Chiều cao nút
- `BUTTON_SPACING = 20`: Khoảng cách giữa các nút
- `POWERUP_SIZE_WIDTH`, `POWERUP_SIZE_HEIGHT`: Kích thước PowerUp display (x1.3)
- `POWERUP_DISPLAY_OFFSET_X = 60`, `POWERUP_DISPLAY_OFFSET_Y = 60`: Khoảng cách từ trung tâm đến vị trí PowerUp
- `LOGO_WIDTH`, `LOGO_HEIGHT`: Kích thước logo (400x145)

### Hằng số TextField
- `TEXTFIELD_WIDTH = 400`: Chiều rộng khung nhập tên
- `TEXTFIELD_HEIGHT = 50`: Chiều cao khung nhập tên
- `TEXTFIELD_Y_OFFSET = 70`: Khoảng cách từ logo xuống
- `TEXTFIELD_FONT_SIZE = 22`: Cỡ chữ
- `TEXTFIELD_PROMPT_FONT_SIZE = 18`: Cỡ chữ prompt
- `TEXTFIELD_MAX_LENGTH = 15`: Số ký tự tối đa
- `TEXTFIELD_TEXT_COLOR = "#00FFFF"`: Màu cyan sáng cho text
- `TEXTFIELD_BG_COLOR = "rgba(10, 10, 40, 0.85)"`: Nền tối trong suốt
- `TEXTFIELD_BORDER_COLOR = "#00BFFF"`: Viền xanh dương sáng
- `TEXTFIELD_BORDER_WIDTH = 3`: Độ dày viền
- `TEXTFIELD_FOCUS_BORDER_COLOR = "#FFD700"`: Màu viền khi focus (vàng)
- `TEXTFIELD_FOCUS_GLOW = "0 0 10px #FFD700, 0 0 20px #FFD700"`: Hiệu ứng glow

## Constructor

### MainMenu(StateManager stateManager, SpriteProvider sprites, Pane parentPane)
Khởi tạo màn hình Menu chính với các thông số:
- **stateManager**: StateManager để chuyển trạng thái game
- **sprites**: SpriteProvider để lấy tài nguyên hình ảnh
- **parentPane**: Pane chứa canvas để thêm TextField

**Công việc:**
1. Khởi tạo màn hình điểm cao (HighScoreDisplay)
2. Khởi tạo màn hình cài đặt (SettingsScreen) với callback `onBackFromSettings`
3. Khởi tạo danh sách trống cho các thành phần UI
4. Gọi `initializeComponents()` để thiết lập UI

```java
MainMenu mainMenu = new MainMenu(stateManager, spriteProvider, pane);
```

## Phương thức chính

### initializeComponents()
Khởi tạo tất cả các thành phần UI (buttons, PowerUp displays, TextField).

**Công việc:**
1. Tải logo từ SpriteProvider
2. Tính toán vị trí center cho khối buttons
3. Tạo 4 buttons với callbacks:
   - **START GAME**: Gọi `onStartGame()` - chuyển state sang PLAYING
   - **HIGH SCORE**: Gọi `onHighScore()` - bật cờ hiển thị màn hình điểm cao
   - **SETTINGS**: Gọi `onSettings()` - bật cờ hiển thị màn hình cài đặt
   - **QUIT GAME**: Gọi `onQuitGame()` - thoát ứng dụng
4. Đặt button đầu tiên là selected mặc định
5. Tạo PowerUp displays trang trí (3 bên trái, 3 bên phải, 1 ở giữa):
   - **Trái**: SLOW, CATCH, DUPLICATE
   - **Phải**: EXPAND, LASER, LIFE
   - **Giữa**: WARP
6. Tải font UI (optimus.otf)
7. Gọi `initializePlayerNameField()` để khởi tạo TextField

### initializePlayerNameField()
Khởi tạo TextField để người chơi nhập tên với thiết kế đẹp.

**Tính năng:**
- Prompt text: "Enter Your Name"
- Giới hạn độ dài: 15 ký tự
- Căn giữa theo chiều ngang, dưới logo một khoảng
- Custom font (optimus.otf, size 22)
- Style với viền, nền, màu sắc tùy chỉnh
- Focus listener: Thay đổi viền và thêm glow effect khi focus
- Key handler: Chỉ cho phép ESC để thoát focus
- Mouse handler: Click để focus
- Ban đầu ẩn (chỉ hiển thị ở menu chính)

## Phương thức Screen Interface

### render(GraphicsContext gc)
Vẽ màn hình Menu chính hoặc ủy quyền vẽ cho màn hình con.

**Logic:**
1. Nếu `showingHighScore == true`: Vẽ màn hình điểm cao và return
2. Nếu `showingSettings == true`: Vẽ màn hình cài đặt và return
3. Vẽ Menu chính:
   - Gradient background (tối trên, sáng dưới)
   - Logo ở trên cùng
   - PowerUp displays với animation (trái, phải, giữa)
   - Tên và mô tả PowerUps
   - Các buttons
   - Hướng dẫn sử dụng

**PowerUps hiển thị:**
- **Trái**: Slow, Catch, Duplicate (với mô tả)
- **Phải**: Expand, Laser, Extra Life (với mô tả)
- **Giữa**: Warp (với mô tả)

### update(long deltaTime)
Cập nhật logic màn hình (animation PowerUp) hoặc ủy quyền cho màn hình con.

**Logic:**
1. Nếu đang hiển thị màn hình con: Ủy quyền update và return
2. Cập nhật animation cho tất cả PowerUp displays

### handleKeyPressed(KeyCode keyCode)
Xử lý sự kiện nhấn phím để điều hướng hoặc kích hoạt chức năng.

**Logic phân cấp:**
1. **Nếu ở màn hình điểm cao**: Chỉ xử lý ESC để thoát
2. **Nếu ở màn hình cài đặt**: Ủy quyền cho SettingsScreen
3. **Nếu TextField đang focus**: Chỉ xử lý ESC để bỏ focus
4. **Menu chính** (TextField không focus):
   - `UP`: Gọi `navigateUp()` - chọn nút phía trên
   - `DOWN`: Gọi `navigateDown()` - chọn nút phía dưới
   - `ENTER/SPACE`: Kích hoạt nút đang chọn
   - `ESCAPE`: Thoát game

### handleKeyReleased(KeyCode keyCode)
Không sử dụng trong Menu.

### handleMouseClicked(MouseEvent event)
Xử lý sự kiện nhấp chuột.

**Logic:**
1. Nếu ở màn hình con: Bỏ qua hoặc ủy quyền
2. Kiểm tra click vào TextField:
   - **Click vào TextField**: Focus vào TextField, không xử lý buttons
   - **Click ra ngoài**: Bỏ focus khỏi TextField
3. Kiểm tra và kích hoạt nút bấm (nếu không click vào TextField)

### handleMouseMoved(MouseEvent event)
Xử lý sự kiện di chuyển chuột để cập nhật hover và selected.

**Logic:**
1. Nếu ở màn hình con: Bỏ qua hoặc ủy quyền
2. Cập nhật trạng thái hover cho từng button
3. Đồng bộ trạng thái selected:
   - Nếu chuột hover qua nút khác: Bỏ chọn nút cũ, chọn nút mới
   - Cập nhật `selectedButtonIndex`

### onEnter()
Xử lý khi màn hình được kích hoạt (vào Menu).

**Công việc:**
- Đặt lại các cờ: `showingHighScore = false`, `showingSettings = false`
- Reset `selectedButtonIndex = 0`
- Gọi `updateButtonSelection()` để đảm bảo nút đầu tiên được chọn
- Hiển thị lại TextField

### onExit()
Xử lý khi màn hình bị vô hiệu hóa (thoát Menu).

**Công việc:**
- Ẩn TextField

## Phương thức hỗ trợ

### navigateUp()
Điều hướng lên nút phía trên.

**Cơ chế:**
1. Bỏ chọn nút hiện tại
2. Giảm `selectedButtonIndex`
3. Nếu vượt quá giới hạn trên (< 0): Quay vòng lên nút cuối cùng
4. Gọi `updateButtonSelection()` để cập nhật trạng thái

### navigateDown()
Điều hướng xuống nút phía dưới.

**Cơ chế:**
1. Bỏ chọn nút hiện tại
2. Tăng `selectedButtonIndex`
3. Nếu vượt quá giới hạn dưới (>= số nút): Quay vòng xuống nút đầu tiên
4. Gọi `updateButtonSelection()` để cập nhật trạng thái

### updateButtonSelection()
Cập nhật trạng thái selected của tất cả buttons (đảm bảo chỉ có một nút được chọn).

### showPlayerNameField()
Hiển thị TextField nhập tên (set visible = true).

### hidePlayerNameField()
Ẩn TextField nhập tên (set visible = false).

### getPlayerName()
Lấy tên người chơi đã nhập.

**Trả về:**
- Tên đã nhập (uppercase, đã trim) nếu có
- "PLAYER" nếu chưa nhập hoặc TextField rỗng

```java
String playerName = mainMenu.getPlayerName(); // "JOHN" hoặc "PLAYER"
```

## Button Callbacks

### onStartGame()
Chuyển trạng thái game sang PLAYING để bắt đầu game mới.

```java
stateManager.setState(GameState.PLAYING);
```

### onHighScore()
Hiển thị màn hình điểm cao.

```java
showingHighScore = true;
highScoreDisplay.onEnter();
```

### onSettings()
Hiển thị màn hình cài đặt.

```java
showingSettings = true;
settingsScreen.onEnter();
```

### onBackFromSettings()
Callback được gọi khi thoát khỏi màn hình cài đặt (SettingsScreen).

```java
showingSettings = false;
```

### onQuitGame()
Thoát ứng dụng.

```java
System.exit(0);
```

## Cách sử dụng

### Ví dụ khởi tạo
```java
// Trong ArkanoidApp hoặc GameManager
StateManager stateManager = new StateManager(...);
SpriteProvider sprites = new SpriteProvider(...);
Pane parentPane = new Pane();

MainMenu mainMenu = new MainMenu(stateManager, sprites, parentPane);

// Thêm vào StateManager
stateManager.addScreen(GameState.MENU, mainMenu);
```

### Ví dụ trong Game Loop
```java
// Render
mainMenu.render(gc);

// Update
mainMenu.update(deltaTime);

// Xử lý input
canvas.setOnKeyPressed(event -> mainMenu.handleKeyPressed(event.getCode()));
canvas.setOnMouseMoved(event -> mainMenu.handleMouseMoved(event));
canvas.setOnMouseClicked(event -> mainMenu.handleMouseClicked(event));
```

### Lấy tên người chơi khi bắt đầu game
```java
// Khi chuyển từ MENU sang PLAYING
String playerName = mainMenu.getPlayerName();
gameManager.setPlayerName(playerName);
```

## Thiết kế UI

### Layout
```
┌─────────────────────────────────────┐
│           [LOGO]                    │
│                                     │
│     [Enter Your Name TextField]     │
│                                     │
│  [Slow]    [START GAME]    [Expand]│
│  [Catch]   [HIGH SCORE]    [Laser] │
│  [Dup]     [SETTINGS]      [Life]  │
│            [QUIT GAME]              │
│  [Warp]                             │
│                                     │
│  Use Arrow Keys or Mouse to Navigate│
└─────────────────────────────────────┘
```

### Màu sắc
- **Background**: Gradient từ `rgb(10,10,30)` đến `rgb(30,10,50)`
- **Logo**: Vị trí Y = 100
- **TextField**: Cyan text, xanh dương viền, vàng khi focus
- **Buttons**: Theo thiết kế Button class
- **PowerUps**: Animation 8 frames
- **Text**: Trắng, vàng cho highlight

## Tính năng đặc biệt

### 1. Quản lý màn hình con
MainMenu quản lý 2 màn hình con:
- **HighScoreDisplay**: Hiển thị top scores
- **SettingsScreen**: Cài đặt âm thanh

**Cơ chế delegation:**
- Khi `showingHighScore` hoặc `showingSettings` = true
- Tất cả render, update, input được ủy quyền cho màn hình con

### 2. TextField động
- Hiển thị/ẩn tự động dựa trên trạng thái
- Focus management với ESC
- Style động với focus/blur
- Giới hạn độ dài và ký tự

### 3. PowerUp trang trí
- 7 PowerUp displays với animation
- Hiển thị tên và mô tả chi tiết
- Layout đối xứng (trái/phải)

### 4. Điều hướng linh hoạt
- Phím điều hướng (UP/DOWN)
- Chuột (hover, click)
- Đồng bộ giữa 2 cách điều hướng

### 5. Vòng lặp điều hướng
- Từ nút cuối lên nút đầu
- Từ nút đầu xuống nút cuối

## Dependencies
- `Engine.GameState`: Enum các trạng thái game
- `Engine.StateManager`: Quản lý chuyển đổi state
- `Objects.PowerUps.PowerUpType`: Enum các loại PowerUp
- `UI.Button`: Component nút bấm
- `UI.PowerUpDisplay`: Component hiển thị PowerUp
- `UI.Screen`: Interface màn hình
- `UI.UIHelper`: Utility vẽ UI
- `Utils.AssetLoader`: Tải font và assets
- `Utils.Constants`: Các hằng số game
- `Utils.SpriteProvider`: Cung cấp sprites
- `javafx.scene.canvas.GraphicsContext`: Để vẽ
- `javafx.scene.control.TextField`: TextField nhập tên
- `javafx.scene.layout.Pane`: Quản lý layout
