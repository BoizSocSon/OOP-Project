# Tóm tắt phần mã nguồn theo trách nhiệm

Mục tiêu: mô tả rõ ràng các phần trong mã nguồn (`src`) đảm nhiệm các chức năng sau:
- Quản lý trạng thái game
- Điều khiển Paddle
- Di chuyển Ball
- Phá hủy Brick

Nội dung dưới đây trình bày file/ lớp chính và các phương thức liên quan, cùng luồng tương tác ngắn gọn.

**Quản lý trạng thái game**:
- File: `src/Engine/GameState.java` — định nghĩa enum các trạng thái: `MENU`, `PLAYING`, `PAUSED`, `LEVEL_COMPLETE`, `GAME_OVER`, `WIN`.
- File: `src/Engine/StateManager.java` — quản lý trạng thái hiện tại, chuyển trạng thái và hành vi khi vào/thoát trạng thái.
  - Phương thức chính: `setState(GameState newState)` — kiểm tra transition hợp lệ, gọi `onStateExit`/`onStateEnter`.
  - Trợ giúp: `isPlaying()`, `isPaused()`, `getState()`.
- File: `src/Engine/GameManager.java` — giữ một `StateManager stateManager` và sử dụng `stateManager.isPlaying()` để quyết định cập nhật logic.
  - `checkGameConditions()` thay đổi trạng thái (ví dụ: `stateManager.setState(GameState.LEVEL_COMPLETE)` hoặc `GAME_OVER`/`WIN`) dựa trên điều kiện trò chơi.

Luồng ngắn: vòng lặp chính gọi `GameManager.update()` → nếu `stateManager.isPlaying()` thì cập nhật đối tượng, xử lý va chạm, và `checkGameConditions()` có thể gọi `stateManager.setState(...)` để chuyển trạng thái.

**Điều khiển Paddle**:
- File: `src/Objects/GameEntities/Paddle.java` — lớp đại diện cho thanh đỡ, kế thừa từ `MovableObject`.
  - Điều khiển chuyển động: `moveLeft()`, `moveRight()`, `stop()` thiết lập `Velocity` cho paddle; `update()` gọi `move()` (kế thừa) để áp vận tốc.
  - Quản lý trạng thái/power-up: `setState(PaddleState newState)`, `enableLaser()`, `disableLaser()`, `expand()`, `shrinkToNormal()`, `enableCatch()`, `setCatchModeEnabled()`, `shootLaser()`.
  - Quản lý animation và thời hạn power-ups: `update()` xử lý `animation`, `expandExpiryTime`, `laserExpiryTime`, `catchExpiryTime`, `slowExpiryTime`.
  - Các getter/flag: `isCatchModeEnabled()`, `isLaserEnabled()`, `getLaserShots()`.
- Giao tiếp/luồng: `GameManager` gọi `paddle.update()` trong `update()`; `CollisionManager.checkBallPaddleCollision(ball, paddle)` dùng trạng thái paddle (ví dụ `isCatchModeEnabled()`) để quyết định gắn bóng hay phản xạ; khi bắn laser `Paddle.shootLaser()` trả về các `Laser` để `GameManager` thêm vào danh sách.

**Di chuyển Ball**:
- File: `src/Objects/GameEntities/Ball.java` — kế thừa `MovableObject`.
  - Di chuyển cơ bản: `update()` gọi `move()` kế thừa; vận tốc lưu trong `Velocity`.
  - Trạng thái gắn: `setAttached(boolean)`, `isAttached()` — khi gắn vào paddle, `GameManager.update()` cập nhật vị trí bóng theo `paddle` thay vì gọi `ball.update()`.
  - Hệ thống va chạm điểm-đường (swept-circle): `checkCollisionWithRect(Rectangle rect)` thực hiện kiểm tra đường đi (trajectory), tính normal, phản xạ vận tốc (v' = v - 2*(v·n)*n), xử lý penetration resolution để không bị dính vào vật cản.
  - Bounce coefficient: `bounceCoefficient` cho hiệu ứng đàn hồi.

**Phá hủy Brick**:
- File: `src/Objects/Bricks/Brick.java` — lớp trừu tượng cho gạch.
  - Trạng thái & HP: `hitPoints`, `alive`; phương thức `takeHit()` giảm HP và gọi `destroy()` khi HP <= 0; `destroy()` đặt `alive = false`.
  - `getBounds()` trả về `Rectangle` để kiểm tra va chạm.
- File: `src/Engine/CollisionManager.java` — xử lý va chạm giữa Ball/Laser và Brick.
  - `checkBallBrickCollisions(Ball ball, List<Brick> bricks)`:
    - Duyệt các gạch còn sống, bỏ qua `GOLD` (không phá hủy).
    - Nếu `ball.checkCollisionWithRect(brick.getBounds())` trả true → gọi `brick.takeHit()`; nếu gạch bị phá hủy (`isDestroyed()`), thêm vào danh sách `destroyedBricks`.
  - `checkLaserBrickCollisions(List<Laser> lasers, List<Brick> bricks)` xử lý laser bắn trúng gạch: giảm HP bằng `brick.takeHit()` và ghi lại cặp `laser -> brick`.
- File: `src/Engine/GameManager.java` — sau khi nhận `destroyedBricks` từ `CollisionManager`, GameManager:
  - Cập nhật điểm: `scoreManager.addDestroyBrickScore(type)`.
  - Gọi `powerUpManager.spawnFromBrick(...)` để tạo vật phẩm rơi (nếu có).
  - Quản lý điều kiện hoàn thành vòng/qua màn dựa trên trạng thái gạch (qua `RoundsManager`).

**Luồng tổng hợp (tóm tắt cách các phần phối hợp)**
1. Vòng chính gọi `GameManager.update()`.
2. `GameManager.update()`:
   - Kiểm tra `stateManager.isPlaying()` → nếu không, bỏ qua.
   - `paddle.update()` — xử lý chuyển động và logic power-up của paddle.
   - Nếu bóng đang `attached`, `GameManager` cập nhật vị trí bóng theo `paddle`.
   - Cập nhật các `ball.update()` (nếu không attached) và `laser.update()`.
   - `collisionManager.checkBallWallCollisions(...)` xử lý va chạm tường.
   - `collisionManager.checkBallPaddleCollision(ball, paddle)` xử lý va chạm paddle và tính góc bật.
   - `collisionManager.checkBallBrickCollisions(ball, bricks)` xử lý va chạm gạch → `brick.takeHit()` → nếu `destroy()` thì `GameManager` nhận `destroyedBricks` và cập nhật điểm, rơi power-up.
   - `collisionManager.checkLaserBrickCollisions(...)` xử lý tia laser.
   - `GameManager.checkGameConditions()` kiểm tra số bóng/mạng/vòng và gọi `stateManager.setState(...)` khi cần.

**Các file chính đã tham khảo**
- `src/Engine/GameManager.java`
- `src/Engine/StateManager.java`
- `src/Engine/GameState.java`
- `src/Engine/CollisionManager.java`
- `src/Objects/GameEntities/Paddle.java`
- `src/Objects/GameEntities/Ball.java`
- `src/Objects/Bricks/Brick.java`

-- Hết --

Nếu bạn muốn, tôi có thể:
- Thêm sơ đồ luồng (sequence) minh họa tương tác giữa `GameManager` → `CollisionManager` → `Ball`/`Paddle`/`Brick`.
- Mở rộng README với ví dụ gọi hàm (ví dụ chuỗi gọi khi va chạm) hoặc dẫn link tới các phương thức cụ thể (số dòng).
