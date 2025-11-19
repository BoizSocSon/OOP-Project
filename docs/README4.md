Tích hợp Âm thanh & Hình ảnh (Audio & Graphics)
===============================================

Mục đích
- Mô tả cách mã nguồn tải, lưu vào cache, và sử dụng tài nguyên hình ảnh/hoạt ảnh (sprites/animations) cùng với âm thanh (nhạc nền, hiệu ứng) trong giao diện JavaFX.

Tóm tắt kiến trúc
- **Tải tài nguyên:** `Utils.AssetLoader` chịu trách nhiệm đọc file hình ảnh, font, và audio từ `resources`. Nó bao bọc mọi thao tác I/O trong `try/catch` và trả về các placeholder (hình/ảnh đơn giản) khi không thể tải được tài nguyên.
- **Cache sprite:** `Utils.SpriteCache` khởi tạo và lưu trữ các sprite/khung hình hoạt ảnh theo nhóm (bricks, paddle, ball, power-ups, v.v.). Các lớp khác truy xuất ảnh bằng `SpriteProvider`/`SpriteCache` theo tên tệp.
- **Tạo Animation:** `Utils.AnimationFactory` dùng các khung hình từ `SpriteProvider` để tạo các đối tượng `Animation` (cung cấp `getCurrentFrame()` và `isPlaying()`), cho phép paddle/bricks/powerups có hiệu ứng chuyển động.
- **Vẽ lên Canvas:** `Render.CanvasRenderer` là tầng vẽ cao hơn, tải font UI (qua `AssetLoader`) và vẽ HUD/overlay; tầng thực thi chi tiết là `Render.SpriteRenderer` (các phương thức `drawBall`, `drawPaddle`, `drawBrick`, `drawPowerUp`) dùng `GraphicsContext.drawImage(...)` để render frame hiện tại.
- **Âm thanh:** `Engine.AudioManager` là singleton quản lý nhạc nền (`MusicTrack`), tạo và cache `MediaPlayer` cho mỗi bản nhạc, exposes volume/muted properties, và lưu cấu hình bằng `Utils.FileManager`.

Luồng thời gian chạy (runtime flow)
- Khi ứng dụng khởi động (`ArkanoidApp.init()` / khởi tạo), bước chính:
  - `SpriteCache.initialize()` được gọi để preload các sprite cần thiết.
  - `AnimationFactory` được khởi tạo với `SpriteProvider` để có thể tạo animation cho các đối tượng.
  - `AudioManager` được khởi tạo/khôi phục cài đặt âm lượng và status (mute) từ `FileManager`.

- Trong vòng lặp game (`AnimationTimer` trong `ArkanoidApp`):
  - `GameManager.update()` cập nhật vị trí đối tượng, trạng thái animation, và các event (ví dụ spawn power-up).
  - Sau cập nhật logic, `CanvasRenderer.render()` được gọi để vẽ khung hình hiện tại.
  - `CanvasRenderer` sử dụng `SpriteRenderer` để vẽ từng thực thể: `SpriteRenderer` lấy frame hiện tại từ `Animation` (nếu animation đang chạy) hoặc lấy sprite tĩnh từ `SpriteProvider`.

- Âm thanh kết hợp vào luồng:
  - `AudioManager.playMusic(MusicTrack)` được gọi khi chuyển màn hình hoặc trạng thái (menu, chơi, pause, game over).
  - Âm lượng và mute có thể liên kết với UI (bindings) để điều khiển trực tiếp từ giao diện.

Chi tiết kỹ thuật và các điểm chú ý
- AssetLoader
  - Các phương thức tải trả về `Image`/`Font`/`Media` và xử lý lỗi bằng `try/catch`.
  - Khi không tìm thấy tệp, `AssetLoader` tạo placeholder (ví dụ hình chữ nhật đơn giản) để tránh NullPointerException ở tầng render.

- SpriteCache & SpriteProvider
  - `SpriteCache.initialize()` phân loại và preload nhiều nhóm sprites, giúp tránh IO trong vòng render.
  - Lấy sprite theo tên tệp như `sprites.get("paddle.png")` hoặc `sprites.get("brick_red.png")`.
  - Việc preload làm giảm giật khung hình do I/O ở thời gian chạy.

- AnimationFactory
  - Chuyển chuỗi khung hình thành một đối tượng `Animation` dùng cho paddle, gạch bạc (crack), power-ups.
  - `Animation` trả khung hiện tại bằng `getCurrentFrame()` — `SpriteRenderer` vẽ frame này thay vì sprite tĩnh khi `animation.isPlaying()`.

- SpriteRenderer / CanvasRenderer
  - `SpriteRenderer` vẽ từng loại đối tượng với logic fallback (ví dụ: nếu animation tồn tại -> vẽ frame, nếu không -> vẽ sprite tĩnh, nếu sprite null -> vẽ hình đơn giản).
  - `CanvasRenderer` chịu trách nhiệm vẽ toàn cảnh (background, game objects, HUD) và sử dụng `SpriteRenderer` cho các thực thể.
  - Các phép vẽ sử dụng `GraphicsContext.drawImage(...)` và tuân thủ kích thước từ `Constants` để scale chính xác.

- AudioManager
  - Quản lý `MediaPlayer` cho từng `MusicTrack`; mỗi MediaPlayer được cache để tránh tạo nhiều instance.
  - Cung cấp API: `play`, `pause`, `stop`, `setVolume`, `setMuted`.
  - Lưu trạng thái (volume/muted) bằng `FileManager` để khôi phục khi khởi động lại ứng dụng.
  - Xử lý lỗi khi file audio không tồn tại: bắt exception, log lỗi, và tiếp tục (không crash ứng dụng).

Xử lý lỗi và fallback
- Hình ảnh missing:
  - `AssetLoader` trả về placeholder Image thay vì null.
  - `SpriteRenderer` có fallback vẽ hình đơn giản (màu, oval, rect) cho power-up/brick nếu sprite không có.
- Âm thanh missing:
  - `AudioManager` bắt ngoại lệ khi tạo `Media`/`MediaPlayer` và log lỗi; không phát âm thanh thay vì ném ngoại lệ gây dừng chương trình.
- Persistence I/O:
  - `FileManager` sử dụng ghi file nguyên tử (atomic write) để tránh file settings bị hỏng nửa chừng.

Gợi ý cải tiến
- Logging: Thay `System.out/err` bằng một framework logging (SLF4J + Logback) để dễ cấu hình mức log và ghi file.
- Tải không đồng bộ: Với các ảnh lớn hoặc nhiều tài nguyên, cân nhắc preload trên background thread trước khi hiển thị menu để tránh giật UI.
- LRU Cache: Nếu game mở rộng nhiều sprite, dùng LRU cache để giới hạn bộ nhớ.

Văn bản tham chiếu trong mã nguồn
- `ArkanoidApp` : nơi gọi `SpriteCache.initialize()`, `AnimationFactory` và thiết lập `AnimationTimer`.
- `Utils.AssetLoader` : các hàm load image/font/audio.
- `Utils.SpriteCache` / `Utils.SpriteProvider` : nơi lưu và truy xuất các sprite.
- `Utils.AnimationFactory` : tạo `Animation` từ frames.
- `Render.SpriteRenderer` / `Render.CanvasRenderer` : vẽ entity và HUD.
- `Engine.AudioManager` : load/play/persist audio settings.

Tiếp theo
- Muốn tôi: (gạch đầu dòng chọn 1)
  - [ ] thêm sơ đồ tuần tự (sequence diagram) cho luồng tải và render.
  - [ ] chuyển logging sang SLF4J + Logback (tôi có thể tạo patch).
  - [ ] tách preload assets ra background thread (tôi có thể tạo branch demo).

File đã tạo: `docs/README4.md`

---
Tao bởi GitHub Copilot (GPT-5 mini) — tôi có thể mở rộng mô tả này nếu bạn muốn chi tiết hơn ở phần nào.
