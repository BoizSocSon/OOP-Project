# README12 — OOP principles applied in Arkanoid (`src`)

Mục tiêu: phân tích cách các nguyên tắc OOP (Encapsulation, Abstraction, Inheritance, Polymorphism, SRP, Composition, Loose Coupling) được áp dụng trong mã nguồn `src`, nêu ví dụ thực tế, chỗ làm tốt, chỗ cần cải thiện và các bước refactor cụ thể.

**Tóm tắt ngắn**
- Nhiều phần của code tuân theo OOP: interface/abstract cho game objects, inheritance cho bricks/powerups, JavaFX properties cho binding (observer-like), factories/caches dùng pattern phù hợp.
- Một số vấn đề thường thấy: public mutable fields (`GameManager`), widespread Singletons (tight coupling, testability), một số classes (ví dụ `GameManager`) có nhiều trách nhiệm (vi phạm SRP).

**1. Encapsulation**
- Áp dụng tốt:
  - Utility classes (`Utils.FileManager`, `Utils.AssetLoader`, `Utils.AnimationFactory`) đều có `private` constructor và `static` API — ẩn chi tiết triển khai.
  - `Objects.Core.MovableObject` đóng gói vị trí/velocity logic cho các lớp con.

- Vấn đề:
  - `GameManager` exposes mutable state as `public` fields: `public Paddle paddle; public List<Ball> balls; public List<Brick> bricks; public List<Laser> lasers;` — làm phá vỡ đóng gói, các phần khác có thể sửa trực tiếp state, gây lỗi invariant.

- Đề xuất:
  - `GameManager` nên chuyển sang `private` fields, cung cấp `get` trả `unmodifiableList` hoặc controlled methods (e.g., `spawnBall()`, `removeBrick(...)`).
  - Đảm bảo fields `final` khi chúng ta giữ collection nội bộ (e.g., `private final List<Ball> balls = new ArrayList<>();`).

**2. Abstraction**
- Áp dụng tốt:
  - `GameObject` (interface) làm giao diện trừu tượng cho mọi đối tượng game.
  - `MovableObject` là lớp trừu tượng cung cấp hành vi chung cho đối tượng di động.

- Ví dụ: `Brick` là `abstract class Brick implements GameObject` — tách behavior chung (health, isAlive) ra, các `NormalBrick`/`SilverBrick`/`GoldBrick` override chi tiết.

- Đề xuất:
  - Đảm bảo interface/abstract method signatures chỉ chứa những gì cần thiết cho mọi implementer (Interface Segregation).

**3. Inheritance và Polymorphism**
- Áp dụng tốt:
  - Lớp tổ tiên `MovableObject` -> `Ball`, `Paddle`, `Laser`, `PowerUp` — reuse code via inheritance.
  - `PowerUp` subclasses override behavior để áp dụng hiệu ứng cụ thể.
  - `RoundBase` -> concrete `Round1..Round4` cho phép thêm rounds mới mà không sửa code dùng rounds nhiều.

- Lưu ý về Liskov Substitution Principle (LSP):
  - Kiểm tra các lớp con không làm thay đổi contract (e.g., `PowerUp` subclasses không phá vỡ `update()` hoặc `isAlive()` hợp lệ). Nếu một subclass thay đổi hành vi theo cách làm hỏng giả định của người gọi, cần refactor.

- Đề xuất:
  - Khi cần thay đổi hành vi runtime, dùng composition/strategy thay vì kế thừa sâu.

**4. Single Responsibility Principle (SRP) và Cohesion**
- Vấn đề:
  - `GameManager` là quá nhiều trách nhiệm: khởi tạo objects, cập nhật game loop, xử lý va chạm, quản lý rounds, score, powerups, reset logic. Đây là dấu hiệu vi phạm SRP.

- Đề xuất:
  - Tách trách nhiệm:
    - `GameLoop` (chỉ update tick và render loop orchestration)
    - `GameStateController` (quản lý transitions/flow)
    - `CollisionManager` (đã có) — đảm bảo nó chỉ xử lý detection & response.
    - `EntitySpawner` hoặc `World` để quản lý creation/destruction của game objects.
  - Giữ `GameManager` nhỏ hoặc đổi tên thành `GameController` với phạm vi trách nhiệm rõ ràng.

**5. Composition over Inheritance**
- Áp dụng:
  - `GameManager` composes `CollisionManager`, `PowerUpManager`, `RoundsManager`, `ScoreManager`, `StateManager` — đây là cách dùng composition tốt.

- Đề xuất:
  - Khi cần mở rộng hành vi (ví dụ powerup effects), sử dụng composition (gắn một `PowerUpEffect` vào `PowerUp`) thay vì nối dài hierarchy.

**6. Loose coupling & Dependency Management**
- Vấn đề:
  - Singletons như `AudioManager.getInstance()` và `PowerUpManager.getInstance()` được gọi trực tiếp trong nhiều lớp — dẫn tới tight coupling và khó test.

- Đề xuất:
  - Dùng constructor injection hoặc service locator (cẩn trọng) để cung cấp dependencies.
  - Ví dụ: `public GameManager(AudioManager audioManager, PowerUpManager powerUpManager, RoundsManager roundsManager)` — cho phép test bằng mock implementations.

**7. Polymorphic design patterns hiện tại**
- `Renderer` family acts like Strategy (but no common interface enforced). Đề xuất: define `Renderer` interface and have `CanvasRenderer`/`SpriteRenderer` implement it to enable swapping at runtime.

**8. Encapsulation & Mutability recommendations (concrete changes)**
- `GameManager` changes suggested:
  - Before:
    - `public List<Ball> balls;`
  - After:
    - `private final List<Ball> balls = new ArrayList<>();`
    - `public List<Ball> getBalls() { return Collections.unmodifiableList(balls); }`
    - `public void spawnBall(Ball b) { balls.add(b); }` etc.

- Convert public object references to either private + getter or provide read-only views.

**9. Testing & OOP**
- To support unit testing (and thus better OOP design):
  - Replace global Singletons usage with dependency injection.
  - Extract side-effect heavy code (file I/O in `FileManager`) behind interfaces to allow mocking (e.g., `SettingsRepository`).

**10. Checklist for PRs that improve OOP compliance**
- **Encapsulation**: No `public` mutable fields — all must be private or immutable.
- **SRP**: Each class should have one clear responsibility — move unrelated logic out.
- **DI**: Avoid `getInstance()` calls inside business logic; inject dependencies where practical.
- **Interfaces**: Provide interfaces for swappable components (`Renderer`, `SpriteProvider`, `SettingsRepository`).
- **Collections**: Return `Collections.unmodifiableList(...)` for internal lists.
- **Naming**: Follow the conventions in `docs/README8.md`.

**11. Examples from codebase (mapping)**
- `Objects.Core.GameObject` + `MovableObject` — good OOP abstraction and reuse.
- `Objects.Bricks.Brick` and its subtypes — good use of inheritance and polymorphism.
- `Utils.SpriteCache` / `SpriteCacheProvider` — good use of flyweight/cache pattern (composition + encapsulation).
- `Engine.StateManager` — good separation of state logic, but uses `AudioManager.getInstance()` internally (consider inject).
- `Engine.GameManager` — major candidate for SRP/encapsulation refactor.

**12. Recommended next actions (practical)**
- (1) Encapsulate `GameManager` public fields (small safe refactor). I can create a patch for this change.
- (2) Introduce `Renderer` interface and adapt one renderer as example.
- (3) Replace one Singleton usage with constructor injection (e.g., inject `AudioManager` into `StateManager`) as a demonstration.
- (4) Add unit tests for a small module (e.g., `CollisionManager`) to make sure refactors are safe.

---

File created: `docs/README12.md`.

Bạn muốn tôi tự động áp dụng một trong các thay đổi đề xuất (ví dụ: encapsulate `GameManager` fields, add `Renderer` interface, or convert one Singleton to DI)? Nếu có, chọn một mục và tôi sẽ tạo patch mẫu.