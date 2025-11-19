Hệ thống Cấp độ, Các loại Brick đặc biệt và Power-up
=====================================================

Mục tiêu
- Trình bày rõ ràng các phần trong mã nguồn chịu trách nhiệm về: hệ thống cấp độ (rounds), các loại gạch (bricks) đặc biệt, và hệ thống Power-up (sinh, áp dụng, hết hạn).

1) Hệ thống cấp độ (Rounds)
- Lớp quản lý: `Engine.RoundsManager`
  - `initializeRounds()` khởi tạo danh sách các `RoundBase` (hiện có `Round1`..`Round4`).
  - `loadFirstRound()`, `loadRound(int)`, `nextRound()`, `hasNextRound()`, `isRoundComplete()`.
  - `isRoundComplete()` coi là hoàn thành khi tất cả gạch còn sống **ngoại trừ** gạch `GOLD` đã bị phá (GOLD là bất khả xâm phạm).
- Lớp cơ sở: `Rounds.RoundBase`
  - Định nghĩa `createBricks()` (abstract) để các `Round` triển khai bố cục gạch.
  - Cung cấp `getRoundName()`, `getTotalBrickCount()`.
- Các cấp độ mặc định:
  - `Rounds.Round1` — Beginner's Challenge: lưới 13x4 toàn `NormalBrick` với màu theo hàng.
  - `Rounds.Round2` — Silver Challenge: lưới 13x5, mỗi viên có ~30% khả năng là `SilverBrick` (cần nhiều hit).
  - `Rounds.Round3` — Diamond Challenge: bố cục hình thoi (layout 2D) gồm `Normal`, `Silver`, `Gold` theo layout.
  - `Rounds.Round4` — Ultimate Challenge: lưới 13x10, phân bố phức tạp bằng toán học modulo để tạo pattern giữa `Normal`/`Silver`/`Gold`.

2) Các loại Brick (được định nghĩa trong `Objects.Bricks`)
- `Brick` (abstract)
  - Thuộc tính: x,y,width,height,hitPoints,alive.
  - API: `takeHit()`, `destroy()`, `getBounds()`, `isAlive()`, `update()`.
- `NormalBrick` (`Objects.Bricks.NormalBrick`)
  - Khởi tạo theo `BrickType` (màu) — hitPoints và điểm (score) lấy từ `Objects.Bricks.BrickType`.
  - `getScoreValue()` trả điểm khi bị phá.
- `SilverBrick` (`Objects.Bricks.SilverBrick`)
  - Bền hơn: `BrickType.SILVER.getHitPoints()` (hiện = 2).
  - Có `crackAnimation` (tạo từ `AnimationFactory.createBrickCrackAnimation()`);
  - Khi còn 1 HP, phát animation nứt; khi HP→0 gọi `destroy()`.
  - `update()` cập nhật animation mỗi frame.
- `GoldBrick` (`Objects.Bricks.GoldBrick`)
  - Gạch vàng gần như bất khả xâm phạm (hitPoints=999 trong `BrickType`).
  - `takeHit()` và `update()` để trống — dùng để trang trí hoặc tạo thử thách (không tính vào điều kiện hoàn thành round).
- `BrickType` (enum)
  - Định nghĩa các loại: `BLUE, RED, GREEN, YELLOW, ORANGE, PINK, CYAN, WHITE, SILVER, GOLD`.
  - Thuộc tính: `hitPoints`, `spriteName`, `baseScore`.

3) Power-up (Objects.PowerUps)
- Lớp cơ sở: `Objects.PowerUps.PowerUp`
  - Là `MovableObject` (có vị trí, vận tốc rơi), có `Animation` từ `AnimationFactory.createPowerUpAnimation(type)`.
  - Thuộc tính: `type` (`PowerUpType`), `collected`, `active`.
  - Method quan trọng: `update()` (di chuyển + update animation), `checkPaddleCollision(Paddle)`, `collect()`, `applyEffect(GameManager)` (abstract), `removeEffect(GameManager)` (abstract).
- Enum `PowerUpType` (cấu hình các loại):
  - Các loại: `CATCH`, `DUPLICATE`, `EXPAND`, `LASER`, `LIFE`, `SLOW`, `WARP`.
  - Mỗi loại có `powerupPrefix` (tên sprite), `spawnChance` (trọng số xác suất xuất hiện), `getDuration()` trả thời lượng (ms) cho các loại có thời hạn.
  - `isInstant()` phân biệt các power-up tức thời (LIFE, WARP, DUPLICATE) vs. có thời hạn (CATCH, EXPAND, LASER, SLOW).

- Các lớp Power-up hiện có (tệp và hành vi chính):
  - `CatchPowerUp` — bật chế độ bắt bóng: gọi `gameManager.enableCatchMode()`. Hết hạn gọi `disableCatchMode()`.
  - `DuplicatePowerUp` — nhân đôi tất cả Ball: gọi `gameManager.duplicateBalls()`; tác động tức thời, không hết hạn.
  - `ExpandPaddlePowerUp` — phóng to paddle: gọi `gameManager.expandPaddle()`; khi hết hạn gọi `gameManager.revertPaddleSize()`.
  - `LaserPowerUp` — cho phép bắn laser: gọi `gameManager.enableLaser()`; hết hạn gọi `gameManager.disableLaser()`.
  - `LifePowerUp` — +1 mạng sống: gọi `gameManager.addLife()`; tức thời, có kiểm tra `MAX_LIVES` trong `GameManager`.
  - `SlowBallPowerUp` — giảm tốc các ball: gọi `gameManager.slowBalls(multiplier)`, hết hạn gọi `gameManager.restoreBallSpeed()`.
  - `WarpPowerUp` — chuyển sang level tiếp theo: gọi `gameManager.warpToNextLevel()`; tức thời.

4) Cơ chế sinh & quản lý lifecycle của Power-up
- Sinh: `Engine.PowerUpManager.spawnFromBrick(x,y,brickType)` gọi khi một viên gạch được phá; dùng `PowerUpType.randomWeighted()` (dựa trên `spawnChance`) để chọn loại power-up.
- Danh sách hiện thời: `activePowerUps` (list). Khi power-up được nhặt, `applyPowerUpEffect()` được gọi, rồi `scheduleEffectExpiry()` sẽ lập lịch expiry (lưu `expiryTime` vào `activeEffects` nếu loại có duration).
- Hẹn giờ / hết hạn: `updateActiveEffects()` được gọi trong `PowerUpManager.update()` (mỗi frame), so sánh `System.currentTimeMillis()` với expiry times và gọi `removePowerUpEffect(type)` khi hết hạn.
- Ghi chú: manager sao chép danh sách (`new ArrayList<>(activePowerUps)`) để tránh ConcurrentModification khi xóa trong vòng lặp.

5) Tương tác với `GameManager` và vòng lặp chính
- `GameManager` giữ `RoundsManager`, `PowerUpManager`, danh sách `bricks`, `balls`, `paddle`.
- Khi Power-up áp dụng, các phương thức trên `GameManager` thay đổi trạng thái (e.g., `enableCatchMode()`, `duplicateBalls()`, `expandPaddle()`, `enableLaser()`, `addLife()`, `slowBalls()`, `warpToNextLevel()`).
- `RoundsManager.isRoundComplete()` được `GameManager` kiểm tra sau mỗi cập nhật để quyết định chuyển round và gọi `roundsManager.nextRound()`.

6) Tệp UI liên quan
- `UI.PowerUpDisplay` hiển thị icon/animation cho từng loại Power-up (dùng `PowerUpType`).
- `UI.Menu.MainMenu` có ví dụ hiển thị các PowerUpTypes cho hướng dẫn (tham chiếu sprites).

7) Gợi ý mở rộng và cân bằng
- Cân bằng xác suất: `PowerUpType.spawnChance` hiện là trọng số tĩnh; để cân bằng, bạn có thể:
  - Điều chỉnh spawnChance per round (ví dụ nhiều power-up mạnh hơn trong round cao hơn).
  - Thêm hệ thống rarity tiers (common/rare/epic) thay cho trọng số tĩnh.
- Cân bằng điểm & HP: `BrickType` lưu `hitPoints` và `baseScore` dễ mở rộng — thêm new types đơn giản.
- Power-up scaling: thêm tham số magnitude vào `PowerUpType` (ví dụ EXPAND_MULTIPLIER riêng cho mỗi level).
- Thiết kế new brick behaviors: thêm override `update()` trong Brick (ví dụ: moving bricks, timed shields, exploding bricks) và tích hợp vào `CollisionManager`.

8) Vị trí mã nguồn tham chiếu
- Rounds: `src/Engine/RoundsManager.java`, `src/Rounds/RoundBase.java`, `src/Rounds/Round1.java` ... `Round4.java`.
- Bricks: `src/Objects/Bricks/Brick.java`, `NormalBrick.java`, `SilverBrick.java`, `GoldBrick.java`, `BrickType.java`.
- Power-ups: `src/Objects/PowerUps/PowerUp.java`, `PowerUpType.java`, `CatchPowerUp.java`, `DuplicatePowerUp.java`, `ExpandPaddlePowerUp.java`, `LaserPowerUp.java`, `LifePowerUp.java`, `SlowBallPowerUp.java`, `WarpPowerUp.java`.
- Manager: `src/Engine/PowerUpManager.java`, sử dụng `scheduleEffectExpiry()` + `updateActiveEffects()`.

Kết luận ngắn
- Hệ thống cấp độ hiện tại là modular: mỗi `Round` tự tạo layout qua `createBricks()`; dễ mở rộng bằng cách thêm `RoundN` mới.
- Brick types có cấu hình rõ ràng trong `BrickType` (HP & score). `Silver` và `Gold` là hai loại đặc biệt có hành vi khác (`Silver` có animation, `Gold` bất khả xâm phạm).
- Power-up có lifecycle đầy đủ: spawn (weighted), apply effect via `GameManager`, scheduled expiry và removal. Hệ thống đủ linh hoạt để mở rộng thêm loại mới và điều chỉnh cân bằng.

File đã tạo: `docs/README6.md`
