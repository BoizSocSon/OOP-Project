# README10 — Design Patterns in Arkanoid (`src`)

Mục tiêu: liệt kê các Design Pattern được sử dụng trong mã nguồn `src`, chỉ ra vị trí (file / lớp) làm ví dụ, giải thích vì sao pattern đó phù hợp, và đề xuất nơi nên áp dụng hoặc cải tiến thêm.

---

**Tóm tắt nhanh**
- Patterns được tìm thấy: Singleton, Factory / Factory Method, Template Method, Strategy (renderers), State (state machine), Observer-like (JavaFX properties), Facade/Utility, Flyweight / Cache.
- Một số pattern không rõ ràng hoặc chưa áp dụng nhưng sẽ có lợi: Command (input handling), Dependency Injection (loose coupling), Builder (tạo object phức tạp), Observer (explicit event bus), Strategy cho movement/AI.

---

## 1) Singleton
- Ví dụ trong mã: `AudioManager` (has `getInstance()`), `PowerUpManager.getInstance()` được sử dụng.
- Tại sao: các manager dùng trạng thái toàn cục (audio settings, power-up registry) nên chỉ cần một instance chia sẻ trong ứng dụng.
- File tham khảo: `src/Engine/AudioManager.java`, `src/Engine/PowerUpManager.java`.
- Lưu ý: Singleton dễ dùng nhưng gây tight coupling. Nếu muốn test dễ hơn, cân nhắc cung cấp ctor công khai và cho phép inject instance (Factory/DI) trong môi trường test.

## 2) Factory / Factory Method
- Ví dụ hiện có:
  - `AnimationFactory` (factory class cho animation) — điển hình Factory pattern.
  - `RoundsManager` có trách nhiệm tạo/tải `RoundBase` / `Round1..Round4` — đây là Factory Method / simple Factory.
  - PowerUp creation: `PowerUpManager.spawnFromBrick(...)` hiện trách nhiệm sinh powerups từ brick type — dạng factory logic.
- File tham khảo: `src/Utils/AnimationFactory.java`, `src/Engine/RoundsManager.java`, `src/Engine/PowerUpManager.java`.
- Đề xuất: tách tạo `PowerUp` vào `PowerUpFactory` nếu logic tạo phức tạp (để dễ mở rộng). Tạo `RoundFactory` nếu cần thêm nhiều cách tạo level.

## 3) Template Method
- Ví dụ: `MovableObject` (abstract) và concrete classes `Ball`, `Paddle`, `Laser`.
- Tại sao: `MovableObject` cung cấp khung (skeleton) hành vi `update()` hoặc các helper, các lớp con chỉ cần override chi tiết.
- File tham khảo: `src/Objects/Core/MovableObject.java`, `src/Objects/GameEntities/Ball.java`, `src/Objects/GameEntities/Paddle.java`.
- Đề xuất: kiểm tra `update()` chung — nếu có nhiều bước cố định + bước có thể mở rộng, Template Method rõ ràng sẽ hữu dụng.

## 4) Strategy (Rendering / Behavior)
- Ví dụ: `SpriteRenderer`, `CanvasRenderer`, `BorderRenderer` thể hiện các chiến lược khác nhau cho việc vẽ/ render. Các renderer có thể được chọn/hoán đổi tùy mục đích.
- File tham khảo: `src/Render/SpriteRenderer.java`, `src/Render/CanvasRenderer.java`, `src/Render/BorderRenderer.java`.
- Đề xuất: chính thức hóa interface `Renderer` hoặc `IRenderer` nếu chưa có, và làm cho `ArkanoidApp` hoặc `GameManager` nhận `Renderer` như dependency (Strategy injection) để dễ thay đổi target renderer (headless testing, different backends).

## 5) State (State Machine)
- Ví dụ rõ rệt: `StateManager` + `GameState` enum quản lý chuyển trạng thái của game (MENU, PLAYING, PAUSED, LEVEL_COMPLETE, GAME_OVER, WIN).
- Tại sao: `StateManager` giữ current/previous state, rules of transition, onEnter/onExit logic → là một State Machine implementation.
- File tham khảo: `src/Engine/StateManager.java`.
- Đề xuất: nếu logic onEnter/onExit phức tạp, có thể tách từng state thành các object (State pattern: mỗi state là class implementing an interface) để giảm switch-case và di chuyển logic vào lớp trạng thái.

## 6) Observer-like (Bindings / Properties)
- Ví dụ: `AudioManager` sử dụng `DoubleProperty volumeProperty` và `BooleanProperty mutedProperty` (JavaFX properties). UI có thể bind vào các property này.
- Tại sao: JavaFX Properties cung cấp observer-binding semantics (listeners are notified on change) — thực tế là Observer pattern thông qua JavaFX API.
- File tham khảo: `src/Engine/AudioManager.java`.
- Đề xuất: dùng event bus / property binding cho các thay đổi game state (score, lives, current level) để UI không cần polling `GameManager` thường xuyên.

## 7) Facade / Utility
- Ví dụ: `FileManager`, `AssetLoader` cung cấp API đơn giản để đọc/ghi file và nạp tài nguyên, che dấu chi tiết I/O.
- File tham khảo: `src/Utils/FileManager.java`, `src/Utils/AssetLoader.java`.
- Đề xuất: giữ role Facade cho các subsystems (Audio, Rendering, IO). Có thể thêm `GameServiceFacade` để cung cấp một API đơn giản cho UI để tương tác (start, pause, reset, getScore).

## 8) Flyweight / Cache
- Ví dụ: `SpriteCache`, `SpriteCacheProvider` thực hiện caching sprites để tránh tạo mới nhiều lần — điển hình Flyweight/Caching.
- File tham khảo: `src/Utils/SpriteCache.java`, `src/Utils/SpriteCacheProvider.java`.
- Đề xuất: đảm bảo cache có lifecycle rõ ràng (clear on level change), và nếu memory pressure cần, dùng soft references / eviction policy.

---

## Patterns chưa hoặc ít được dùng — nơi áp dụng sẽ có lợi
Đây là những pattern tôi khuyến nghị cân nhắc áp dụng để cải thiện thiết kế:

1) Command — input handling
- Vấn đề hiện tại: input handling (key presses) có thể đặt trực tiếp gọi các method như `launchBall()` / `resetGame()`.
- Lợi ích Command: tách hành động (command) khỏi nơi gọi, dễ undo/redo, dễ map phím/joystick, dễ unit-test.
- Gợi ý: tạo `Command` interface (`execute()`), concrete commands: `LaunchBallCommand`, `PauseCommand`, `MovePaddleCommand`. Có thể thêm `InputController` để map key->Command.

2) Dependency Injection / Service Locator
- Vấn đề: nhiều lớp gọi `SomeManager.getInstance()` (Singleton) gây tight coupling và khó test.
- Lợi ích DI: dễ thay thế mock trong test, giảm coupling.
- Gợi ý: thêm `ServiceLocator` hoặc (better) cho `GameManager` / `StateManager` nhận dependencies qua constructor (inject AudioManager, PowerUpManager) and provide a centralized bootstrapping in `ArkanoidApp`.

3) Builder — tạo object phức tạp
- Nơi hữu ích: khởi tạo `Paddle` / `Ball` / `Round` có nhiều tham số (position, size, behaviors). Builder giúp code gọi rõ ràng hơn.
- Gợi ý: `new Ball.Builder().withRadius(r).withPosition(x,y).withVelocity(v).build()`.

4) Observer / Event Bus (game events)
- Hiện tại JavaFX properties cover some UI binding, nhưng game events (ball lost, brick destroyed, score changed) có thể dùng Event Bus để tách UI và logic.
- Gợi ý: simple `EventBus` interface, `Event` types, `EventListener` registration.

5) Strategy cho PowerUp Effects
- Hiện PowerUp classes có thể implement effect directly. Nếu effects có thể được hoán đổi, tách thành `PowerUpEffect` strategy objects.

---

## Concrete suggestions & quick refactor ideas
- Tách `PowerUp` creation: create `src/Engine/PowerUpFactory.java` that returns `PowerUp` instances based on `BrickType` / random chance. Update `PowerUpManager.spawnFromBrick(...)` to use factory.
- Introduce `IRenderer` interface and make `CanvasRenderer` / `SpriteRenderer` implement it. Inject an `IRenderer` into the main render loop.
- Implement `Command` pattern for user input: add `src/Input/Command.java`, `LaunchBallCommand`, `PauseCommand`, and an `InputMapper`.
- Replace a few heavy `switch(state)` in `StateManager` with state objects (one class per GameState) if entering/exiting logic grows.
- Replace direct `SomeManager.getInstance()` calls in key classes with constructor injection where feasible (start with `GameManager` and `StateManager`).

---

## Quick mapping (file → suggested pattern / action)
- `src/Engine/AudioManager.java` → Singleton + Observer (JavaFX properties). Suggest: allow injection for tests.
- `src/Engine/PowerUpManager.java` → Singleton + Factory usage; suggest `PowerUpFactory` extraction.
- `src/Utils/AnimationFactory.java` → Factory (keep).
- `src/Utils/SpriteCache.java`, `SpriteCacheProvider.java` → Flyweight / Cache (keep + ensure lifecycle management).
- `src/Render/*Renderer.java` → Strategy (formalize via `Renderer` interface).
- `src/Objects/Core/MovableObject.java` → Template Method (keep).
- `src/Engine/StateManager.java` → State Machine; consider full State classes if complexity increases.
- `src/Utils/FileManager.java` → Facade/Utility (keep).

---

## Next steps I can take for you
- (A) Generate a short PR/patch that extracts `PowerUpFactory` and updates `PowerUpManager.spawnFromBrick(...)` to use it.
- (B) Add an `IRenderer` interface and modify one renderer (e.g., `CanvasRenderer`) to implement it and update call sites.
- (C) Create a small `Command` scaffold and show how input mapping would use it.
- (D) Produce a report listing every class that calls `getInstance()` (Singleton usage) so we can plan DI replacement.

Cho tôi biết bạn muốn tôi thực hiện bước (A)/(B)/(C)/(D) nào, hoặc chỉ cần file `README10.md` như trên là đủ.