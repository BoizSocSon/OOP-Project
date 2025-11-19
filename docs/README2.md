# Power-up & Xử lý lỗi (tóm tắt mã nguồn)

Mục tiêu: mô tả rõ ràng cách trò chơi xử lý Power-up (sinh, rơi, thu thập, áp dụng/hủy hiệu ứng) và các cách xử lý lỗi/defensive coding có mặt trong mã nguồn.

---

**1) Vòng đời Power-up (lý thuyết & triển khai)**

- Manager: `src/Engine/PowerUpManager.java`
  - Singleton: `getInstance()`; `setGameManager(GameManager)` để có quyền thay đổi trạng thái game.
  - Sinh vật phẩm: `spawnFromBrick(x, y, brickType)`
    - Kiểm tra tỉ lệ rơi (`Constants.GameRules.POWERUP_SPAWN_CHANCE`).
    - Chọn loại bằng `PowerUpType.randomWeighted()`.
    - Dùng factory `createPowerUp(x, y, type)` để tạo instance (các lớp con ở `src/Objects/PowerUps/`).
    - Nếu loại không hợp lệ, ghi `System.err` và trả về `ExpandPaddlePowerUp` mặc định.
  - Update mỗi frame: `update(Paddle paddle)`
    - Tạo bản sao danh sách (`new ArrayList<>(activePowerUps)`) để tránh ConcurrentModification khi xoá.
    - Gọi `powerUp.update()` (di chuyển rơi) và `powerUp.checkPaddleCollision(paddle)`.
    - Khi thu thập: gọi `powerUp.collect()`, `applyPowerUpEffect(powerUp)` rồi `scheduleEffectExpiry(type)` nếu power-up có thời hạn; xóa khỏi `activePowerUps`.
    - Kiểm tra power-up rơi quá đáy (x > WINDOW_HEIGHT) và xóa.
  - Áp dụng hiệu ứng: `applyPowerUpEffect(powerUp)`
    - Kiểm tra an toàn `gameManager != null`; nếu null thì log `System.err` và bỏ qua.
    - Gọi `powerUp.applyEffect(gameManager)` — triển khai cụ thể trong từng lớp PowerUp.
  - Hủy hiệu ứng khi hết hạn: `updateActiveEffects()` sử dụng `Iterator` trên `activeEffects.entrySet()` và gọi `removePowerUpEffect(type)` khi expiry đạt.
    - `removePowerUpEffect(type)` tạm tạo một PowerUp (factory `createPowerUp(0,0,type)`) và gọi `tempPowerUp.removeEffect(gameManager)`.
  - Dọn dẹp: `clearAllPowerUps()` để xóa cả danh sách rơi và hiệu ứng đang hoạt động (dùng khi chuyển màn/thoát game).

- Lớp trừu tượng: `src/Objects/PowerUps/PowerUp.java`
  - Quản lý vị trí, kích thước, vận tốc rơi và animation.
  - Logic va chạm với paddle: `checkPaddleCollision(Paddle)` dùng `getBounds().intersects(paddle.getBounds())`.
  - Khi nhặt: `collect()` đặt `collected = true`, `active = false`.
  - Hai phương thức abstract: `applyEffect(GameManager)` và `removeEffect(GameManager)` — do các lớp con triển khai.

- Các PowerUp cụ thể (và hành vi chính):
  - `CatchPowerUp`: `applyEffect` gọi `gameManager.enableCatchMode()`; `removeEffect` gọi `gameManager.disableCatchMode()`.
  - `ExpandPaddlePowerUp`: `applyEffect` gọi `gameManager.expandPaddle()`; `removeEffect` gọi `gameManager.revertPaddleSize()`.
  - `LaserPowerUp`: `applyEffect` gọi `gameManager.enableLaser()`; `removeEffect` gọi `gameManager.disableLaser()`.
  - `LifePowerUp`: `applyEffect` gọi `gameManager.addLife()` (có check giới hạn MAX_LIVES trong `GameManager`).
  - `DuplicatePowerUp`: `applyEffect` gọi `gameManager.duplicateBalls()` (tạo thêm ball); `removeEffect` thường rỗng (hiệu ứng tức thời).
  - `SlowBallPowerUp`: `applyEffect` gọi `gameManager.slowBalls(multiplier)`; `removeEffect` gọi `gameManager.restoreBallSpeed()`.
  - `WarpPowerUp`: gọi `gameManager.warpToNextLevel()` (nếu có màn tiếp) — thay đổi state/rounds.

**2) Cách mã nguồn xử lý các tình huống lỗi (error handling & defensive coding)**

- Logging & fallback:
  - Nhiều nơi dùng `System.err.println(...)` để log lỗi (ví dụ `PowerUpManager.createPowerUp()` khi gặp loại không xác định, `PowerUp.applyEffect()` khi `gameManager==null`).
  - Game dùng `System.out.println(...)` cho debug/info và `System.err` cho tình huống bất thường.

- Kiểm tra null & an toàn trước khi thực hiện hành vi:
  - `PowerUpManager.applyPowerUpEffect()` kiểm tra `gameManager == null` trước khi gọi `applyEffect`.
  - Mỗi `PowerUp.applyEffect(...)` thường kiểm tra tham số `gameManager` và ghi lỗi nếu null.
  - `PowerUp.checkPaddleCollision()` kiểm tra `paddle == null` và `active` trước khi kiểm tra hình học.

- Tránh lỗi đồng bộ/ConcurrentModification:
  - `PowerUpManager.update()` lặp trên một bản sao của `activePowerUps` để có thể xóa phần tử khỏi danh sách gốc trong vòng lặp.
  - `updateActiveEffects()` dùng `Iterator` và `iterator.remove()` khi duyệt `activeEffects.entrySet()` để xóa an toàn.

- Xử lý I/O và fault-tolerance (ví dụ `FileManager`):
  - `src/Utils/FileManager.java` là ví dụ điển hình cho defensive I/O:
    - Dùng `try/catch` để bắt `IOException` và `NumberFormatException` khi đọc file highscore, trả về giá trị mặc định (0) khi file hỏng hoặc lỗi đọc.
    - Ghi file nguyên tử (atomic write) bằng cách tạo file tạm, ghi, rồi `Files.move(tmp, target, ATOMIC_MOVE)`; nếu ATOMIC_MOVE không hỗ trợ thì fallback dùng REPLACE_EXISTING.
    - Khi hiển thị dialog lỗi, bắt `Throwable` để phòng môi trường không có JavaFX, rồi ghi `System.err` nếu không thể hiện dialog.

- Xử lý tài nguyên (assets, audio):
  - `Utils.AssetLoader` và `Engine.AudioManager` có các khối `try/catch` và in ra lỗi nếu không tìm thấy tài nguyên hoặc quá trình load thất bại.

- State validation:
  - `Engine.StateManager.setState()` kiểm tra transition hợp lệ và in `System.err` khi cố gắng chuyển sang `null` hoặc transition không hợp lệ.

**3) Một số chi tiết thiết kế/điểm cần lưu ý / đề xuất cải tiến**

- Hiện tại logging dùng `System.out`/`System.err`. Đề xuất thay bằng một logging framework (SLF4J + Logback hoặc java.util.logging) để hỗ trợ levels, output file, và dễ tắt/mở khi deploy.
- `PowerUpManager.removePowerUpEffect()` tạo một instance tạm `createPowerUp(0,0,type)` chỉ để gọi `removeEffect()` — đây là giải pháp nhanh nhưng hơi lạ. Có thể lưu trực tiếp một map từ `PowerUpType` → `Consumer<GameManager>` (hàm hủy hiệu ứng) hoặc lưu reference tới instance đã áp dụng để gọi `removeEffect()` trực tiếp.
- Sử dụng exceptions cho lỗi nghiêm trọng thay vì chỉ log `System.err` sẽ giúp test và xử lý luồng lỗi nhất quán; tuy nhiên việc chỉ log và tiếp tục giúp game không crash khi gặp lỗi non-fatal.
- Xác thực invariant: nhiều hàm dùng `gameManager` và `paddle` — hiện có null-checks nhưng có thể thêm `Objects.requireNonNull(...)` nơi phù hợp để phát hiện lỗi sớm trong giai đoạn phát triển.

**4) Các file liên quan (tham khảo nhanh)**
- Power-up core: `src/Engine/PowerUpManager.java`
- Power-up base: `src/Objects/PowerUps/PowerUp.java`
- Power-up cụ thể: `src/Objects/PowerUps/CatchPowerUp.java`, `ExpandPaddlePowerUp.java`, `LaserPowerUp.java`, `LifePowerUp.java`, `DuplicatePowerUp.java`, `SlowBallPowerUp.java`, `WarpPowerUp.java`.
- Game hooks: `src/Engine/GameManager.java` (các method như `enableCatchMode()`, `expandPaddle()`, `enableLaser()`, `duplicateBalls()`, `slowBalls()`, `restoreBallSpeed()`, `addLife()`, `warpToNextLevel()`).
- File & assets I/O: `src/Utils/FileManager.java`, `src/Utils/AssetLoader.java`, `src/Engine/AudioManager.java`.
- State validation/logging: `src/Engine/StateManager.java`.

---

Nếu bạn muốn, tôi có thể:
- Mở rộng README2 với sơ đồ sequence (PowerUp spawn → collect → apply → expiry).
- Tạo PR patch để thay `System.err`/`System.out` bằng logger (ví dụ `java.util.logging.Logger`) cho PowerUpManager và FileManager.
- Sửa `PowerUpManager.removePowerUpEffect()` theo đề xuất (lưu instance hoặc handler) và chạy test code.

Hoàn tất draft README2.
