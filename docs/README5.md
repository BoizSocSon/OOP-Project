Sử dụng Đa luồng để Cải thiện Trải nghiệm Người dùng (GUI)
=========================================================

Mục tiêu
- Mô tả rõ phần nào trong mã nguồn hiện tại đã/không sử dụng đa luồng, các rủi ro liên quan (block UI, race), và cung cấp các đề xuất cùng ví dụ mã cụ thể để cải thiện trải nghiệm GUI bằng cách chuyển I/O/khởi tạo nặng ra luồng nền.

Tóm tắt hiện trạng (các phần liên quan và hành vi hiện tại)
- `ArkanoidApp` (start): gọi `AudioManager.initialize()` và `SpriteCache.initialize()` trong luồng JavaFX (JavaFX Application Thread). Những gọi này thực hiện I/O (tải file) đồng bộ.
- `Utils.AssetLoader`: tải `Image`, `Font`, `MediaPlayer` đồng bộ. Constructor `new Image(is)` hiện tại không bật tải nền.
- `Utils.SpriteCache.initialize()`: tải toàn bộ sprites/frames đồng bộ (nhiều file). Phương thức này `synchronized`, nhưng vẫn chạy đồng bộ khi gọi.
- `Engine.AudioManager.initialize()`: duyệt `MusicTrack.values()` và gọi `loadMusicTrack()` (tạo `MediaPlayer`) đồng bộ. Lưu ý: `Media`/`MediaPlayer` bản thân dùng internals background threads của JavaFX Media nhưng tạo/khởi tạo có thể tốn thời gian và nên tránh trên UI thread.
- `Utils.FileManager`: dùng `synchronized` để bảo vệ file I/O; khi có lỗi, `showWriteErrorDialog()` dùng `Platform.runLater()` nếu gọi từ luồng không phải JavaFX, đây là một điểm tích hợp luồng an toàn.
- `Engine.PowerUpManager`: không dùng thread, dùng timestamp để hủy hiệu ứng (được gọi trong vòng lặp chính) — không có IO blocking ở đây.

Vấn đề và rủi ro hiện tại
- Khởi tạo tài nguyên (sprite/audio) đồng bộ trên luồng JavaFX có thể gây:
  - Giật/đơ giao diện khi khởi động hoặc chuyển màn (khi có nhiều file lớn).
  - Thời gian chờ dài trước khi `Stage.show()` hoặc menu trở nên tương tác được.
- Nếu chuyển `SpriteCache.initialize()` sang luồng nền nhưng render code tiếp tục truy xuất cache chưa đầy đủ, có thể gặp NullPointerException hoặc ảnh hiển thị bị thiếu. Do đó cần một chiến lược publish/visibility an toàn.

Các phần trong mã nguồn đã hỗ trợ an toàn luồng (tốt)
- `FileManager.showWriteErrorDialog()` kiểm tra `Platform.isFxApplicationThread()` và dùng `Platform.runLater()` khi cần — đây là ví dụ xử lý UI update từ luồng nền đúng.
- `SpriteCache.getInstance()` và `SpriteCache.initialize()` là `synchronized`, tránh race khi khởi tạo Singleton.
- `AudioManager` lưu settings thông qua `FileManager` (đã có đồng bộ hóa), và `MediaPlayer` hoạt động với JavaFX media subsystem (một phần sử dụng luồng nền nội bộ).

Đề xuất cải tiến (chi tiết, có ví dụ mã)
1) Tải tài nguyên (sprites, images, audio) trên luồng nền
   - Mục tiêu: tránh khởi tạo/IO nặng trên JavaFX Application Thread.
   - Kỹ thuật: sử dụng `ExecutorService` (hoặc `CompletableFuture.supplyAsync`), tải mọi ảnh vào bộ nhớ (hoặc pre-built data structure) rồi publish về UI thread với `Platform.runLater()`.

Ví dụ (simplified) — dùng `CompletableFuture` để preload sprites:

```java
// UI thread: khởi động preload
CompletableFuture.supplyAsync(() -> {
    // chạy trên thread pool: tải tất cả sprite vào một map tạm
    Map<String, Image> tmp = new HashMap<>();
    for (String name : spriteNames) {
        Image img = AssetLoader.loadImage(name); // có thể sửa AssetLoader để hỗ trợ background loading
        tmp.put(name, img);
    }
    return tmp;
}).thenAccept(spriteMap -> {
    // chạy trên JavaFX Application Thread
    Platform.runLater(() -> {
        SpriteCache.getInstance().setCacheFromMap(spriteMap); // cần API an toàn để publish cache
        // thông báo UI: ẩn progress, enable Start
    });
});
```

Gợi ý implement `setCacheFromMap(...)` an toàn:
- Xây dựng toàn bộ cache (Map + frame lists) trong local thread,
- Khi hoàn tất, gọi một method đồng bộ (trên FX thread) để gán tham chiếu cache (hoặc hoán đổi field map) một lần — tránh ghi đồng thời vào map đang đọc.

2) Sử dụng JavaFX `Task` nếu cần cập nhật tiến trình (progress)
   - `Task` hỗ trợ binding `progressProperty()` để gắn vào `ProgressIndicator`.

Ví dụ `Task`:

```java
Task<Void> preloadTask = new Task<>() {
    @Override
    protected Void call() throws Exception {
        int total = spriteNames.size();
        int i = 0;
        Map<String, Image> tmp = new HashMap<>();
        for (String name : spriteNames) {
            if (isCancelled()) break;
            Image img = AssetLoader.loadImage(name);
            tmp.put(name, img);
            i++;
            updateProgress(i, total);
        }
        // Publish to UI
        Platform.runLater(() -> SpriteCache.getInstance().setCacheFromMap(tmp));
        return null;
    }
};
new Thread(preloadTask, "asset-preload").start();
progressIndicator.progressProperty().bind(preloadTask.progressProperty());
```

3) Làm cho `AssetLoader` hỗ trợ background loading của `Image`
   - JavaFX `Image` có constructor hỗ trợ background loading: `new Image(url, requestedWidth, requestedHeight, preserveRatio, smooth, backgroundLoading)` — khi `backgroundLoading` = `true`, JavaFX tải hình trong luồng nền.
   - Thay vì `new Image(is)` có thể dùng URL form hoặc dùng `Image(InputStream, ..., boolean backgroundLoading)` phù hợp để tận dụng khả năng load nền.

4) Quản lý thread-safety cho `SpriteCache`
   - Hiện tại `SpriteCache` dùng `synchronized` trên `initialize()` và `getInstance()` nhưng maps/lists nội bộ không phải `ConcurrentHashMap`.
   - Khi preload trên thread nền, tạo toàn bộ data structure immutable (hoặc thread-safe) rồi hoán đổi tham chiếu atomically (ví dụ gán vào một `volatile` field hoặc dùng `synchronized` setter). Tránh sửa trực tiếp các `List`/`Map` đang được render đọc.

5) Audio: tải nhẹ trên background và khởi tạo `MediaPlayer` khi cần
   - `AudioManager.initialize()` hiện gọi `loadMusicTrack()` đồng bộ; nếu có nhiều track lớn, consider lazy-loading (load khi chuyển màn) hoặc preload trong background.
   - `Media`/`MediaPlayer` có xử lý nền nhưng việc khởi tạo nhiều player cùng lúc có thể gây delay.

6) File I/O (FileManager)
   - `FileManager` đã dùng `synchronized` và `Platform.runLater()` cho dialog — tốt.
   - Nếu gọi `saveHighscore()` từ luồng UI nhiều lần, có thể offload write vào executor để tránh block (vẫn giữ `synchronized` và atomic write logic).

Ví dụ: write audio settings off-UI thread

```java
ExecutorService ex = Executors.newSingleThreadExecutor();
ex.submit(() -> FileManager.saveAudioSettings(volume, muted));
ex.shutdown();
```

Kiến trúc đề xuất tóm tắt
- Khởi tạo nhẹ lúc start: show UI nhanh (menu) với trạng thái "Loading assets...". Sau đó preload assets trên thread nền (Task/ExecutorService/CompletableFuture). Khi preload hoàn tất, enable nút Start / ẩn progress.
- Đổi `SpriteCache` sang model publish-once: build toàn bộ cache trong background, sau đó publish atomically.
- Sử dụng `Task` nếu cần progress binding; sử dụng `Platform.runLater()` để cập nhật UI từ background.
- Duy trì `FileManager` synchronized nhưng gọi các write không bắt buộc từ UI thread bằng executor.

Hành động tiếp theo tôi có thể làm cho bạn
- [ ] Tạo patch demo: `SpriteCache.initializeAsync()` + `MainMenu` hiển thị progress và enable Start khi hoàn tất.
- [ ] Hiện thực `SpriteCache.setCacheFromMap(...)` an toàn và cập nhật `AssetLoader` để hỗ trợ background-loading của `Image`.
- [ ] Offload `AudioManager.initialize()` sang background (với fallback lazy-load).

Kết luận
- Hiện tại mã đã tôn trọng một số quy tắc thread-safety (FileManager dialog, synchronized singleton), nhưng còn chỗ gây block UI (sprite/audio synchronous loads). Việc tách khởi tạo nặng ra luồng nền bằng `Task`/`ExecutorService` hoặc `CompletableFuture` sẽ cải thiện trải nghiệm người dùng đáng kể.

File đã tạo: `docs/README5.md`
