# README9 — Codebase audit vs. naming & design conventions (applied to `src`)

Mục tiêu: tổng hợp kết quả phân tích mã nguồn trong thư mục `src` theo các nguyên tắc đặt tên (naming) và một số nguyên tắc thiết kế nhẹ (encapsulation, responsibilities). File này liệt kê những phần đã tuân thủ, những chỗ cần cải thiện, và các đề xuất cụ thể (tên mới, thay đổi phạm vi truy cập, và bước refactor).

Tôi đã quét toàn bộ `src` để thu thập các khai báo lớp và mở một số file đại diện (`GameManager`, `StateManager`, `FileManager`, `UIHelper`, `AudioManager`) để lấy ví dụ chi tiết.

**Tổng quan nhanh**
- Số lượng file Java được phát hiện (khai báo `class`): nhiều lớp (ví dụ `AudioManager`, `GameManager`, `StateManager`, `PowerUpManager`, `FileManager`, `UIHelper`, `RoundBase`, `Ball`, `Paddle`, `Brick`, v.v.).
- Nhiều phần trong code hiện đã áp dụng các quy tắc đặt tên chuẩn (PascalCase cho lớp, camelCase cho method/field, UPPER_SNAKE cho constants trong `Constants`/`UIConstants`).
- Một số vấn đề lặp lại cần chú ý: package names viết hoa (khuyên đổi thành chữ thường), một vài field công khai (public) gây mất đóng gói (encapsulation), và một số tên có thể làm rõ nghĩa hơn.

**Những phần tuân thủ tốt (khá chuẩn)**
- Class names: `AudioManager`, `GameManager`, `StateManager`, `PowerUpManager`, `CollisionManager`, `RoundsManager`, `HighScoreManager`, `ScoreManager`, `SpriteRenderer`, `CanvasRenderer`, `BorderRenderer`, `Animation` — tất cả đều ở PascalCase.
- Utility/Constants classes: `FileManager`, `AssetLoader`, `AnimationFactory`, `Constants`, `UIConstants` — đều dùng `final` + private constructor (đã áp dụng pattern utility class) — rất tốt.
- Constants naming: trong `Utils.Constants` và chứa inner static classes với hằng số rõ ràng như `PADDLE_WIDTH`, `WINDOW_WIDTH`, `DEFAULT_MUSIC_VOLUME` — dùng UPPER_SNAKE cho hằng số tĩnh.
- JavaFX property naming: `volumeProperty()` / `mutedProperty()` pattern được AudioManager sử dụng — phù hợp với convention của JavaFX.
- Enum usage: có `GameState`, `BrickType`, `MusicTrack` (đếm thấy ở code) — enum thích hợp để liệt trạng thái/loại.

**Vấn đề / Cơ hội cải tiến (ưu tiên)**
1. Public mutable fields in `GameManager`
   - Vị trí: `GameManager` có các field public: `public Paddle paddle; public List<Ball> balls; public List<Brick> bricks; public List<Laser> lasers;`
   - Vấn đề: public mutable fields cho phép bất kỳ mã nào thay đổi trạng thái nội bộ game trực tiếp, làm mất tính đóng gói, khó kiểm soát lifecycle, khó debug.
   - Đề xuất: chuyển các field này sang `private` và cung cấp getter đọc (`getPaddle()`, `getBalls()`), hoặc trả bản sao/Collection view không thay đổi (Collections.unmodifiableList) nếu cần an toàn. Nếu cần ghi từ bên ngoài, cung cấp method có kiểm soát (ví dụ `spawnBall(...)`, `addLaser(...)`, `removeBrick(...)`).
   - Ví dụ:
     - `public Paddle paddle;` -> `private Paddle paddle; public Paddle getPaddle()`
     - `public List<Ball> balls;` -> `private final List<Ball> balls = new ArrayList<>(); public List<Ball> getBalls() { return Collections.unmodifiableList(balls); }`

2. Package naming convention
   - Vị trí: source packages đang dùng chữ hoa (ví dụ `Engine`, `UI`, `Utils`, `Objects`, `Render`, `Rounds`, `ArkanoidGame`).
   - Vấn đề: Java package convention là lowercase (e.g., `engine`, `ui`, `utils`). Giữ viết hoa phá vỡ convention và có thể dẫn đến vấn đề tương thích ở một số môi trường case-sensitive.
   - Đề xuất: đổi tên packages sang chữ thường. Cách an toàn: thực hiện refactor package bằng IDE (IntelliJ/VSCode) để cập nhật import và module path.

3. Naming clarity for playback fields in `AudioManager`
   - Vị trí: `AudioManager` dùng `currentTrack` (MusicTrack enum) và `currentPlayer` (MediaPlayer instance).
   - Gợi ý: tên hiện tại chấp nhận được; nếu muốn rõ ràng hơn, đổi `currentPlayer` -> `activeMediaPlayer` hoặc `activeMusicPlayer`, `currentTrack` -> `activeTrack`.
   - Ngoài ra: method `loadMusicTrack(MusicTrack)` có thể đổi thành `createMediaPlayerForTrack` hoặc `loadAndCacheTrack` nếu method tạo player và cache là side-effect.

4. Utility classes vs stateful helpers
   - Vị trí: `UIHelper` là một utility class tĩnh — đã cấm khởi tạo (private ctor) — đúng.
   - Nếu có lớp mang tên `Helper` nhưng giữ state hoặc có nhiều phụ thuộc, cân nhắc đổi thành `Service` hoặc `Manager`.

5. Boolean naming check
   - Kiểm tra các boolean fields nếu có: tên boolean nên bắt đầu bằng `is`, `has`, `can` để đọc code rõ ràng (ví dụ `isMuted`, `isAttached` đã dùng — tốt).

6. Public API / method naming clarity
   - Ví dụ: `loadMusicTrack` (AudioManager) — đổi tên để mô tả side-effects.
   - `GameManager` có method `initGame()` và `resetGame()` — rõ ràng.

**Cụ thể — danh sách file/class cần refactor (gợi ý độ ưu tiên)**
- High priority (encapsulation / correctness):
  - `src/Engine/GameManager.java` — đổi các field public thành private + getter/unmodifiable view; kiểm tra mọi external usage và cập nhật.
  - `src/Engine/AudioManager.java` — (optional) rename `currentPlayer` -> `activeMediaPlayer`; rename `loadMusicTrack` -> `createMediaPlayerForTrack` nếu phù hợp.
  - Package names — đổi tất cả packages top-level sang chữ thường: `engine`, `ui`, `utils`, `objects`, `render`, `rounds`, `arkanoidgame`.

- Medium priority (clarity / naming):
  - `UI.UIHelper` ok; `UI.Button`/`UI.Screen` — giữ; kiểm tra `UIHelper` method names consistent.
  - `Utils.SpriteCacheProvider` vs `SpriteCacheFactory` — nếu class tạo cache, rename to `Factory`, nếu chỉ cung cấp instance giữ `Provider`.
  - `StateManager` name is appropriate; `GameManager` could be `GameController` if you prefer controller-style naming, but this is optional.

- Low priority (style/consistency):
  - Inner classes naming in `Constants` are fine. Ensure hằng số UPPER_SNAKE across files.
  - Ensure enum values style consistent project-wide (choose `UPPER_SNAKE` for enum values or `PascalCase` consistently).

**Ví dụ refactor ngắn (GameManager):

Before (current):
```java
public class GameManager {
    public Paddle paddle;
    public List<Ball> balls;
    public List<Brick> bricks;
    public List<Laser> lasers;
    ...
}
```
After (recommended):
```java
public class GameManager {
    private final Paddle paddle;
    private final List<Ball> balls = new ArrayList<>();
    private final List<Brick> bricks = new ArrayList<>();
    private final List<Laser> lasers = new ArrayList<>();

    public Paddle getPaddle() { return paddle; }
    public List<Ball> getBalls() { return Collections.unmodifiableList(balls); }
    // Methods to mutate: spawnBall(), removeBrick(), addLaser() ...
}
```

**Checklist khi thực hiện refactor (recommended PR process)**
- Tách nhỏ thay đổi: mỗi PR là 1–3 lớp thay đổi để dễ review.
- Dùng IDE refactor (Rename/Move) để cập nhật import mọi nơi.
- Thêm/khai báo method truy cập (getter/setter) hoặc API để thao tác thay vì mở field.
- Chạy toàn bộ ứng dụng/manual smoke test (nếu có) để kiểm tra runtime behaviour.
- Nếu đổi package (chữ hoa -> chữ thường), chắc chắn cập nhật `module-info.java` và file hệ thống build nếu có.
- Viết mô tả PR rõ ràng: list các đổi tên, lý do, và danh sách file thay đổi.

**Các bước tiếp theo tôi có thể giúp**
- Tự động liệt kê (report) tất cả field public/mutable trong `src` để bạn review.
- Tạo PR mẫu: áp dụng refactor cho `GameManager` (đổi public fields -> private, thêm getters), commit và show diff.
- Tự động đề xuất renames (ví dụ `currentPlayer` -> `activeMediaPlayer`) và tạo một patch (dùng apply_patch) nếu bạn muốn tôi thực hiện.

---
File này đã được tạo thành `docs/README9.md` trong workspace. Nếu muốn, tôi có thể:
- (A) tự động đổi các field public trong `GameManager` sang private + thêm getters và gửi patch, hoặc
- (B) tạo một báo cáo chi tiết hơn (CSV/markdown) liệt kê tất cả vị trí vi phạm nhỏ (public fields, non-lowercase package names, tên boolean chưa chuẩn).

Bạn muốn tôi tiếp tục với lựa chọn (A) hay (B), hay chỉ cần danh sách hiện tại là đủ?