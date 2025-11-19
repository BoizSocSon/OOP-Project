# Giao diện người dùng (UI) — Tóm tắt mã nguồn

Mục tiêu: mô tả các phần trong mã nguồn (`src`) đảm nhiệm giao diện người dùng (GUI) — entry point, màn hình (screens), thành phần UI, hệ thống render và input. Dự án sử dụng JavaFX cho GUI (không có giao diện dòng lệnh riêng).

**Tổng quan**
- Framework: JavaFX (xem `module-info.java` yêu cầu `javafx.controls`, `javafx.fxml`, `javafx.media`, `javafx.graphics`).
- Entry point: `src/ArkanoidGame/ArkanoidApp.java` — tạo Stage/Scene, Canvas, vòng lặp `AnimationTimer`, xử lý input (keyboard/mouse), quản lý trạng thái và chuyển giữa các màn hình.
- Renderer: `src/Render/CanvasRenderer.java` (vẽ lên `Canvas`), `SpriteRenderer`, `BorderRenderer` giúp hiển thị sprites, UI overlay, các đối tượng game.

**Các thành phần UI chính**
- `ArkanoidApp` (Entry point)
  - Tạo `Canvas` và `GraphicsContext`.
  - Khởi tạo `SpriteCache`/`SpriteProvider` và `AnimationFactory`.
  - Khởi tạo `GameManager`, `HighScoreManager`, và các màn hình UI (`MainMenu`, `PauseScreen`, `GameOverScreen`, `WinScreen`).
  - Thiết lập `Scene` event handlers: `setOnKeyPressed`, `setOnKeyReleased`, `setOnMouseClicked`, `setOnMouseMoved`.
  - `AnimationTimer` chạy vòng lặp chính: cập nhật game/screen theo `GameState`, render bằng `CanvasRenderer`.

- `CanvasRenderer` / `SpriteRenderer`
  - `CanvasRenderer` chịu trách nhiệm vẽ mọi lớp: UI overlay, paddle, balls, bricks, lasers, power-ups.
  - Nó sử dụng `GraphicsContext` của JavaFX Canvas — tốc độ tốt cho game 2D nhẹ.

- UI helpers & constants
  - `src/UI/UIHelper.java`: các hàm tiện ích vẽ text, hộp, hiệu ứng đơn giản sử dụng `GraphicsContext`.
  - `src/UI/UIConstants.java`: các hằng số về font, màu sắc, kích thước dùng cho UI.

- UI primitives
  - `src/UI/Button.java`: class đơn giản để vẽ button (không phải JavaFX Control), dùng Canvas drawing, hỗ trợ kiểm tra vùng click và highlight.
  - `src/UI/PowerUpDisplay.java`: vẽ icon/time còn lại của power-up trên HUD.

- Menu & Screens (mỗi screen có `update()` và `render(GraphicsContext)` và input handlers)
  - `src/UI/Menu/MainMenu.java`: màn hình chính (cho nhập tên người chơi, bắt đầu chơi, vào settings, hiển thị high scores). Dùng `TextField` và `Pane` để nhận input chuột/khóa, kết hợp vẽ Canvas.
  - `src/UI/Menu/SettingsScreen.java`, `HighScoreDisplay.java`: màn hình phụ cho cài đặt và hiển thị điểm cao.
  - `src/UI/Screens/PauseScreen.java`, `GameOverScreen.java`, `WinScreen.java`: các lớp màn hình trạng thái (pause/game over/win). Chúng được vẽ lên Canvas và có handlers để nhận phím/chuột.

**Input (Keyboard & Mouse)**
- `ArkanoidApp` đăng ký handlers trên `Scene`:
  - Keyboard: `setOnKeyPressed` / `setOnKeyReleased` — định tuyến theo `GameState` (MENU, PLAYING, PAUSED, GAME_OVER, WIN). Trong `PLAYING`, phím `LEFT`/`RIGHT` điều khiển paddle; `SPACE` phóng bóng hoặc bắn laser; `ESC` pause; `R` reset (debug).
  - Mouse: `setOnMouseClicked` / `setOnMouseMoved` — chủ yếu xử lý input menu qua `MainMenu`.
  - Một số UI (ví dụ `MainMenu`) cũng tạo `TextField` JavaFX để nhập tên người chơi (hỗ trợ clipboard/IME, v.v.).

**Hiển thị HUD (UI overlay)**
- `CanvasRenderer.drawUI(...)` vẽ HUD: điểm số, high score, số mạng (lives) và các indicator power-up. `PowerUpDisplay` hỗ trợ vẽ các icon power-up đang active.

**Cách hoạt động khi chuyển trạng thái**
- `ArkanoidApp.onStateChange(...)` gọi `onExit()`/`onEnter()` cho màn hình tương ứng, lưu tên người chơi (từ `MainMenu`) khi bắt đầu chơi, và xử lý lưu high-score khi GAME_OVER/WIN.
- Vòng lặp chính chọn update/render theo `GameState`:
  - MENU → `mainMenu.update()` / `mainMenu.render(gc)`
  - PLAYING → `gameManager.update()` + `renderGameplay()` (vẽ gameplay + UI)
  - PAUSED → render gameplay rồi `pauseScreen.render(gc)`

**Tích hợp JavaFX Controls và Canvas**
- `MainMenu` dùng `javafx.scene.control.TextField` để thuận tiện nhập tên; phần rendering menu vẫn dựa trên Canvas, nhưng JavaFX Node (TextField) được thêm vào `Pane root` để nhận input. Lưu ý: đây là hybrid approach (Canvas + JavaFX controls).

**Cách tuỳ chỉnh giao diện**
- Thay đổi fonts/ màu / kích thước: chỉnh trong `src/UI/UIConstants.java` và `Utils/AnimationFactory` để dùng sprite khác.
- Thay đổi layout/hành vi menu: sửa `src/UI/Menu/MainMenu.java` và các lớp screen tương ứng.
- Thay đổi assets (sprites, fonts, âm thanh): cập nhật thư mục `src/Resources/*` và `Utils/AssetLoader`/`SpriteCache`.

**Yêu cầu & môi trường chạy**
- Yêu cầu JavaFX (module-info đã khai báo). Khi chạy từ command line hoặc IDE, đảm bảo JavaFX runtime được cấu hình (VM args cho modules path nếu cần trên Windows).

**Các file tham khảo chính**
- Entry point: `src/ArkanoidGame/ArkanoidApp.java`
- Renderer: `src/Render/CanvasRenderer.java`, `src/Render/SpriteRenderer.java`
- UI core: `src/UI/Screen.java`, `src/UI/UIHelper.java`, `src/UI/UIConstants.java`, `src/UI/Button.java`, `src/UI/PowerUpDisplay.java`
- Menus & Screens: `src/UI/Menu/MainMenu.java`, `src/UI/Menu/SettingsScreen.java`, `src/UI/Menu/HighScoreDisplay.java`, `src/UI/Screens/PauseScreen.java`, `src/UI/Screens/GameOverScreen.java`, `src/UI/Screens/WinScreen.java`

---

Muốn tôi làm tiếp:
- Thêm sơ đồ sequence cho luồng input → state → render?
- Hay tạo hướng dẫn ngắn (chạy project với JavaFX trên Windows PowerShell)?

-- Hết --
