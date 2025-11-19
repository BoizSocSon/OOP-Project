# README8 — Naming guidelines for Arkanoid (source `src`)

Mục tiêu: cung cấp quy tắc đặt tên rõ ràng, dễ hiểu và cụ thể cho mã nguồn Java trong thư mục `src` của dự án Arkanoid. File này bao gồm: nguyên tắc chung, ví dụ cụ thể từ codebase hiện tại, checklist khi tạo PR/refactor và các bước tiếp theo.

**Nguyên tắc chung**
- **Class**: Sử dụng `PascalCase` (ví dụ `AudioManager`, `GameManager`, `RoundBase`). Tên lớp nên là danh từ hoặc danh từ ghép, phản ánh trách nhiệm chính.
- **Interface**: `PascalCase` và là một khái niệm/contract (ví dụ `Drawable`, `Updatable`). Không cần tiền tố `I` (không bắt buộc trong Java hiện đại).
- **Enum**: `PascalCase` cho tên enum, các giá trị enum viết `UPPER_SNAKE` hoặc `PascalCase` (thống nhất toàn dự án).
- **Method**: Sử dụng `camelCase`, bắt đầu bằng động từ (ví dụ `loadResources()`, `playMusic()`, `stopMusic()`).
- **Variable / Field**: `camelCase`. Biến boolean nên bắt đầu bằng `is` / `has` / `can` nếu miêu tả trạng thái (ví dụ `isMuted`, `hasLives`).
- **Constants**: `UPPER_SNAKE_CASE` (static final) cho hằng số (ví dụ `DEFAULT_MUSIC_VOLUME`).
- **Property / Observable**: giữ tên dạng `somethingProperty()` cho getter (ví dụ `volumeProperty()`), và `isSomething()` hoặc `getSomething()` cho giá trị.
- **Packages**: toàn bộ chữ thường, cấu trúc phản ánh chức năng (`Engine`, `UI`, `Utils`, `Objects`).
- **Ràng buộc ngữ nghĩa**: tên phải nói lên mục đích — tránh viết tắt không phổ biến (ví dụ `mgr` -> `Manager`), tránh tên quá chung chung (`Helper` chỉ dùng nếu thực sự là bộ helper đa dụng).

**Nguyên tắc cụ thể cho project**
- Các lớp điều phối trạng thái (ví dụ `GameManager`, `StateManager`) nên dùng tên phản ánh phạm vi: nếu quản lý state màn hình thì `ScreenStateManager`; nếu điều khiển luồng game thì `GameController` hoặc `GameEngine`.
- `*Manager` thường ngụ ý quản lý tài nguyên hoặc hành vi (ví dụ `AudioManager`, `PowerUpManager`), đó là hợp lý — chỉ đổi khi lớp thực hiện nhiều hơn 'quản lý'.
- Tránh dùng `Helper` trừ khi đó thực sự là tập hàm tiện ích không phụ thuộc trạng thái; nếu helper giữ trạng thái, đổi thành `Service` hoặc `Provider`.
- Cho các nhà sản xuất/nhà cung cấp tài nguyên dùng hậu tố `Factory` hoặc `Provider` theo vai trò: `SpriteCacheProvider` (nếu trả về cache) hoặc `SpriteFactory` (nếu tạo sprite mới).

**Đặt tên biến/field — ví dụ và chú ý**
- Tránh tên chung như `player` cho `MediaPlayer` hoặc `currentPlayer` (khi ngữ cảnh lẫn lộn). Trong `AudioManager` dùng `currentMediaPlayer` hoặc `activeMusicPlayer` sẽ rõ ràng hơn.
- `currentTrack` là một `MusicTrack` (enum) — đây là identifier; `currentPlayer` là `MediaPlayer` (thực thể playback). Tên hiện tại `currentTrack` / `currentPlayer` là chấp nhận được; nếu muốn rõ ràng hơn: `activeTrack` và `activeMediaPlayer`.
- Với thuộc tính JavaFX: giữ tên `volumeProperty` / `mutedProperty` — đó là chuẩn tốt.

**Ví dụ đổi tên có gợi ý (không bắt buộc, mang tính tham khảo)**
- `GameManager` -> `GameController` (nếu lớp chịu trách nhiệm điều khiển luồng chơi: start/pause/resume/stop). Lý do: "Manager" mơ hồ hơn "Controller" khi điều khiển luồng.
- `StateManager` -> `ScreenStateManager` hoặc `AppStateManager` (làm rõ loại state được quản lý).
- `SpriteCacheProvider` -> `SpriteCacheFactory` (nếu nhiệm vụ là tạo/khởi tạo cache) hoặc `SpriteCacheProvider` vẫn hợp nếu chỉ cung cấp instance đã có.
- `UIHelper` -> `UIUtils` (nếu là tập hàm tiện ích thuần) hoặc `UIFactory` (nếu tạo component UI).
- `currentPlayer` -> `activeMediaPlayer` (tăng tính rõ ràng khi có nhiều loại "player").
- `player` (nếu dùng làm biến cục bộ) -> `mediaPlayer` hoặc `musicPlayer`.

**Cách đặt tên method**
- Hành động rõ ràng: `playMusic(MusicTrack)` (đang dùng) là tốt. Nếu method vừa play vừa set track, tên có thể `setAndPlay(MusicTrack)` hoặc `playTrack(MusicTrack)`.
- Tránh tên `loadMusicTrack` nếu method tạo và cache `MediaPlayer` — đổi thành `createMediaPlayerForTrack` hoặc `loadAndCacheTrack` để phản ánh side-effect.
- Các getter boolean: `isMuted()` (ok). Nếu trả về property: `mutedProperty()` (ok).

**Checklist khi refactor/đặt tên (PR checklist)**
- **Consistency**: tuân theo quy tắc trên trong toàn dự án.
- **Clarity**: tên nên biểu đạt vai trò/kiểu trả về/quiz hành vi.
- **No breaking of public API**: nếu thay đổi tên công khai (ví dụ class hoặc phương thức dùng bởi code khác/external), cập nhật mọi nơi tham chiếu và document change.
- **Small commits**: chia nhỏ refactor theo module để dễ review.
- **Use IDE refactor**: dùng chức năng Rename của IntelliJ/VSCode để đảm bảo mọi tham chiếu được cập nhật.

**Một số ví dụ trực tiếp từ code hiện tại**
- `AudioManager`: tên tốt. Đề xuất biến trong class: `currentPlayer` -> `activeMediaPlayer` (tùy chọn). Method `loadMusicTrack(MusicTrack)` -> `createMediaPlayerForTrack(MusicTrack)` hoặc `loadAndCacheTrack(MusicTrack)` để làm rõ là method tạo `MediaPlayer` và cache.
- `FileManager`: nếu chỉ lưu/tải settings, hãy tách responsibilities: `SettingsManager` hoặc giữ `FileManager` nếu nó xử lý mọi thao tác file chung.
- `PowerUpManager`: tên hợp lý nếu lớp quản lý toàn bộ vòng đời power-ups.

**Next steps (kỹ thuật)**
- Áp dụng thay đổi nhỏ theo module: bắt đầu với `Utils` và `Engine` (ít phụ thuộc), dùng IDE rename.
- Viết unit tests cho thành phần đã đổi tên để bắt lỗi tham chiếu bị bỏ sót.
- Thực hiện PRs nhỏ (mỗi PR 1-2 lớp/khối refactor).

Nếu bạn muốn, tôi có thể:
- Tự động đề xuất một danh sách đổi tên bằng cách scan toàn bộ `src` (liệt kê mọi class/method/field có tên không tuân thủ quy tắc),
- Hoặc tạo PR mẫu với một vài refactor (ví dụ đổi `currentPlayer`->`activeMediaPlayer` trong `AudioManager`).

---
File đã tạo: `docs/README8.md` — mở file này để đọc đầy đủ nội dung và áp dụng các đề xuất.

