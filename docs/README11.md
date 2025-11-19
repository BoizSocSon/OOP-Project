# README11 — Các lớp chính và cấu trúc kế thừa (thư mục `src`)

Mục tiêu: xác định các lớp chính trong `src`, trình bày các quan hệ kế thừa (`extends`/`implements`), mối quan hệ thành phần (composition), và giải thích vai trò của từng thành phần. Tài liệu này giúp hiểu kiến trúc lớp của trò chơi Arkanoid để thuận tiện cho bảo trì và refactor.

---

Tổng quan ngắn gọn:
- Hệ thống lõi gồm: `Objects` (các đối tượng game: Ball, Paddle, Bricks, PowerUps), `Engine` (các manager, vòng lặp game), `Render` (các renderer), `UI` (màn hình, điều khiển), `Utils` (hàm tiện ích, cache), `Rounds` (định nghĩa các vòng chơi), `ArkanoidGame` (khởi tạo ứng dụng).
- Các kiểu quan hệ chính: interface `GameObject`, lớp trừu tượng `MovableObject`, các lớp con cụ thể (Ball, Paddle, Laser, PowerUp...), lớp trừu tượng `Brick` implements `GameObject` với các loại gạch cụ thể; `RoundBase` với các lớp round cụ thể; interface `Screen` với nhiều triển khai màn hình.

---

1) Cấu trúc đối tượng lõi (Core object hierarchy)

- `Objects.Core.GameObject` (interface)
  - Giao diện trừu tượng cho tất cả các đối tượng trong game. Thông thường khai báo các phương thức như `update()`, `render()` và các accessor vị trí/biên.

- `Objects.Core.MovableObject` (lớp trừu tượng) implements `GameObject`
  - Cung cấp hành vi chung cho các đối tượng di chuyển (vị trí, vận tốc, helper cho va chạm).
  - Các lớp con:
    - `Objects.GameEntities.Ball` extends `MovableObject`
    - `Objects.GameEntities.Paddle` extends `MovableObject`
    - `Objects.GameEntities.Laser` extends `MovableObject`
    - `Objects.PowerUps.PowerUp` extends `MovableObject` (và chính nó là abstract)
      - Các PowerUp cụ thể kế thừa `PowerUp`:
        - `CatchPowerUp`, `DuplicatePowerUp`, `ExpandPaddlePowerUp`, `LifePowerUp`, `LaserPowerUp`, `SlowBallPowerUp`, `WarpPowerUp`, ...

Sơ đồ (đơn giản):

GameObject (interface)
└─ MovableObject (abstract, implements GameObject)
   ├─ Ball
   ├─ Paddle
   ├─ Laser
   └─ PowerUp (abstract)
      ├─ CatchPowerUp
      ├─ DuplicatePowerUp
      ├─ ExpandPaddlePowerUp
      ├─ LifePowerUp
      ├─ LaserPowerUp
      ├─ SlowBallPowerUp
      └─ WarpPowerUp

Ghi chú:
- `MovableObject` đóng vai trò giống Template Method: chứa logic dùng chung về vị trí/vận tốc và cho phép lớp con override chi tiết `update()`/`render()`.
- Các lớp con của `PowerUp` triển khai hành vi hiệu ứng riêng, đồng thời tái sử dụng logic di chuyển.

2) Cấu trúc gạch (Bricks hierarchy)

- `Objects.Bricks.Brick` (abstract) implements `GameObject`
  - Đại diện cho một viên gạch chung (vị trí, độ bền/máu, `isAlive()`, `update()`).
  - Các gạch cụ thể kế thừa `Brick`:
    - `NormalBrick` extends `Brick`
    - `SilverBrick` extends `Brick`
    - `GoldBrick` extends `Brick`

Sơ đồ (gạch):

GameObject (interface)
└─ Brick (abstract, implements GameObject)
   ├─ NormalBrick
   ├─ SilverBrick
   └─ GoldBrick

Ghi chú:
- Gạch thường là đối tượng tĩnh (không kế thừa `MovableObject`), nên implement `GameObject` trực tiếp.
- `Brick` chứa logic về thiệt hại/máu và có thể có animation nứt vỡ.

3) Vòng chơi / Rounds

- `Rounds.RoundBase` (abstract)
  - Định nghĩa contract và mã dùng chung cho một vòng chơi (ví dụ: layout gạch, quy tắc spawn).
  - Các vòng cụ thể:
    - `Round1` extends `RoundBase`
    - `Round2` extends `RoundBase`
    - `Round3` extends `RoundBase`
    - `Round4` extends `RoundBase`

Ghi chú:
- `RoundsManager` kết hợp các `RoundBase` và chịu trách nhiệm nạp danh sách gạch cho vòng hiện tại.

4) Lớp giao diện người dùng (UI)

- `UI.Screen` (interface)
  - Định nghĩa lifecycle và render cho các màn hình UI khác nhau.
  - Các triển khai:
    - `UI.Menu.MainMenu` implements `Screen`
    - `UI.Menu.SettingsScreen` implements `Screen`
    - `UI.Menu.HighScoreDisplay` implements `Screen`
    - `UI.Screens.PauseScreen` implements `Screen`
    - `UI.Screens.GameOverScreen` implements `Screen`
    - `UI.Screens.WinScreen` implements `Screen`

Ghi chú:
- Các `Screen` là thành phần chỉ phục vụ UI; chúng tương tác với `GameManager` / `StateManager` để thực thi hành động.

5) Các manager và điều phối game (Engine)

Các lớp manager chính (quan hệ composition):
- `Engine.GameManager`
  - Bộ điều phối chính của game. Thành phần:
    - các field: `Paddle paddle`, `List<Ball> balls`, `List<Brick> bricks`, `List<Laser> lasers` (hiện đang public — nên đổi sang private và cung cấp getter an toàn)
    - quản lý `CollisionManager`, `PowerUpManager`, `RoundsManager`, `ScoreManager`, `StateManager`.
  - Trách nhiệm: khởi tạo đối tượng, vòng update, xử lý va chạm, kiểm tra điều kiện sống/qua màn, reset trò chơi.

- `Engine.StateManager`
  - Lưu `GameState` (enum) và xử lý chuyển trạng thái, logic onEnter/onExit.
  - Sử dụng `AudioManager` (composition/ dependency).

- `Engine.CollisionManager`
  - Chịu trách nhiệm phát hiện va chạm giữa Ball/Paddle/Brick/Laser.

- `Engine.PowerUpManager` (Singleton)
  - Sinh và cập nhật các powerup; có thể spawn từ gạch bị phá.

- `Engine.RoundsManager`
  - Nạp/chuyển vòng chơi và cung cấp danh sách gạch hiện tại.

- `Engine.ScoreManager`, `Engine.HighScoreManager`
  - Quản lý điểm và lưu điểm cao (`HighScoreManager` có thể dùng `FileManager`).

- `Engine.AudioManager` (Singleton)
  - Quản lý nhạc nền và MediaPlayer; phơi bày các JavaFX property cho volume/muted.

Sơ đồ thành phần (đơn giản):

ArkanoidApp -> GameManager
GameManager -> { CollisionManager, PowerUpManager, RoundsManager, ScoreManager, StateManager }
GameManager -> chứa danh sách GameObjects -> { Paddle, Ball(s), Brick(s), Laser(s) }
StateManager -> sử dụng AudioManager
PowerUpManager -> có thể tham chiếu GameManager (setGameManager(this))
RoundsManager -> cung cấp gạch (composition)

6) Renderers và tiện ích (Utilities)

- Thư mục `Render` chứa các lớp renderer (ví dụ: `SpriteRenderer`, `CanvasRenderer`, `BorderRenderer`) đảm nhiệm việc vẽ lên màn hình. Chúng được sử dụng bởi mã UI/Render (qua composition).

- Thư mục `Utils` chứa các lớp hỗ trợ và pattern:
  - `SpriteCache` và `SpriteCacheProvider` (caching / flyweight)
  - `SpriteProvider` (interface) và `SpriteCacheProvider` implement nó
  - `FileManager`, `AssetLoader`, `AnimationFactory` (utility/factory patterns)

7) Các quan hệ khác / ghi chú

- `SpriteProvider` là interface; `SpriteCacheProvider` implement interface này — đây là pattern Provider.
- Nhiều lớp sử dụng enum như `GameState`, `BrickType`, `MusicTrack` để biểu diễn giá trị trong domain.
- Một số manager được triển khai dưới dạng Singleton (ví dụ `AudioManager`, `PowerUpManager`) — thuận tiện nhưng có thể chuyển sang DI để dễ test hơn.

---

Khuyến nghị để rõ ràng và dễ bảo trì:
- Đóng gói (Encapsulation): chuyển các field public trong `GameManager` (paddle, balls, bricks, lasers) thành `private` và cung cấp accessor/mutator có kiểm soát (ví dụ `spawnBall()`, `removeBrick(...)`) để bảo vệ bất biến.
- Interfaces: cân nhắc trích `Renderer` interface cho tất cả renderer và inject renderer vào vòng render để dễ hoán đổi.
- State pattern: `StateManager` hiện dùng `switch` trên `GameState` — nếu logic onEnter/onExit phức tạp, có thể refactor sang State pattern (mỗi state là một lớp).
- Tài liệu: thêm sơ đồ lớp (class-diagram) hoặc PlantUML (repository đã có `.puml`) để hỗ trợ onboarding.

---

Nếu bạn muốn, tôi có thể thực hiện tiếp:
- (A) tự động sinh sơ đồ PlantUML cho các hierarchy trên và thêm vào `docs/` hoặc cập nhật `Objects.puml`/`UI.puml`.
- (B) tạo patch để đóng gói các field của `GameManager` (chuyển sang private + thêm getter) và cập nhật mọi tham chiếu.
- (C) xuất bảng CSV hoặc markdown liệt kê mỗi lớp và parent/interface trực tiếp.

Chọn (A), (B), (C) hoặc trả lời "Không cần" để kết thúc.