Lưu / Tải Game và Bảng Xếp Hạng
=================================

Mục đích
- Tập hợp và mô tả những phần mã nguồn chịu trách nhiệm về việc lưu/tải dữ liệu và bảng xếp hạng (high score). Ghi rõ định dạng file, vị trí lưu, API có sẵn, và đề xuất nếu bạn muốn mở rộng thành "save game" hoàn chỉnh.

1) Vị trí lưu trữ
- Tất cả file cấu hình/do người dùng tạo đều được lưu trong thư mục ứng dụng ẩn trong thư mục nhà người dùng: `Path APP_DIR = Paths.get(System.getProperty("user.home"), ".arkanoid")` (xem `Utils.FileManager`).
- Tên file mặc định (tương ứng trong `Utils.Constants`):
  - `highscore.dat` (hằng số `Constants.Paths.HIGHSCORE_FILE`) — dùng bởi `FileManager` cho highscore cũ.
  - `highscores.dat` — file do `Engine.HighScoreManager` dùng để lưu danh sách high scores.
  - `audio_settings.dat` — file do `FileManager`/`AudioManager` dùng để lưu âm lượng và trạng thái mute.

2) Lớp quản lý file: `Utils.FileManager`
- Tính năng chính
  - Đảm bảo thư mục ứng dụng tồn tại (`ensureAppDirExists()`).
  - Ghi file nguyên tử (atomic) bằng `writeFileAtomic(Path, byte[])` (ghi vào temp file rồi move với `ATOMIC_MOVE` nếu có hỗ trợ). Điều này giảm rủi ro corrupt khi ghi dở.
  - Các phương thức tiện ích: `readLinesFromFile(String)`, `writeLinesToFile(String, List<String>)`, `loadHighscore()`, `saveHighscore(int)`, `loadAudioSettings()`, `saveAudioSettings(double, boolean)`.
  - Đồng bộ hóa: mọi thao tác đọc/ghi dùng `synchronized (LOCK)` để tránh race conditions trên I/O.
  - UI-safe: Khi cần hiển thị dialog báo lỗi ghi file, `showWriteErrorDialog()` kiểm tra `Platform.isFxApplicationThread()` và nếu cần dùng `Platform.runLater()` để thực hiện tạo hộp thoại trên JavaFX Application Thread.

3) Bảng xếp hạng (High Score) — `Engine.HighScoreManager` 
- Mô tả
  - Quản lý một danh sách tối đa `MAX_ENTRIES = 10` entries.
  - Entry: `HighScoreEntry` bao gồm `rank|playerName|score|date` trên một dòng file.
  - Khi khởi tạo, `HighScoreManager()` tự động `loadFromFile()` (với fallback là `createDefaultScores()` nếu file không tồn tại hoặc rỗng).
- API quan trọng
  - `boolean addScore(String playerName, int score, LocalDate date)` — thêm entry nếu đủ điều kiện; sắp xếp giảm dần, trim xuống `MAX_ENTRIES`, cập nhật rank, gọi `saveToFile()`.
  - `List<HighScoreEntry> getTopScores(int count)` — trả về bản sao của top N.
  - `boolean isHighScore(int score)` — kiểm tra điều kiện.
  - `void reset()` — reset về default và lưu file.
- Lưu/Đọc
  - `saveToFile()` chuyển mỗi `HighScoreEntry` thành chuỗi `rank|name|score|date` rồi gọi `FileManager.writeLinesToFile(SAVE_FILE, lines)`.
  - `loadFromFile()` gọi `FileManager.readLinesFromFile(SAVE_FILE)` và parse từng dòng bằng `HighScoreEntry.fromString(...)`.

4) Cài đặt âm thanh
- `Engine.AudioManager` dùng `FileManager.loadAudioSettings()` và `saveAudioSettings(...)` để lưu hai dòng: volume (double) và isMuted (boolean). File là `audio_settings.dat` trong `APP_DIR`.

5) Lưu trò chơi (Save Game)
- Hiện tại trong mã nguồn: KHÔNG có implementation sẵn cho "save game" (snapshot trạng thái game để resume sau khi đóng). `GameManager` có nhiều trạng thái (vị trí paddle, balls, bricks, rounds, score, lives) nhưng không có `saveGame()` / `loadGame()`.

6) Đề xuất nhanh để thêm tính năng Save/Load Game
- Yêu cầu lưu trữ
  - Những dữ liệu cần lưu: current round index / round name, positions & velocities của `Ball`(s), trạng thái `Paddle` (x, width, laser/catch flags), danh sách `Bricks` với type + remaining HP + alive flag, current score, lives, active power-ups (type + remaining time), timestamp.

- Định dạng khuyến nghị
  - JSON (convenient, human-readable) — dễ parse bằng thư viện (Gson/Jackson). Nếu không muốn thêm dependency, có thể dùng đơn giản `key=value` hoặc CSV-like lines và parse thủ công.

- Minimal example (no external deps) — text format (demo):
  - Dòng 1: `roundIndex=2`
  - Dòng 2: `playerName=ALICE`
  - Dòng 3: `score=12345`
  - Dòng 4: `lives=3`
  - Dòng 5+: `ball=x,y,dx,dy,attached` (một line mỗi ball)
  - Tiếp theo: `paddle=x,width,laserEnabled,catchEnabled`
  - Tiếp theo: `brick:type,x,y,hp,alive` (một line mỗi brick)
  - Tiếp theo: `powerup:type,x,y,expiryTimestamp` (một line mỗi active powerup)

- Simple save/load methods (sketch)
  - Save (in `GameManager`): build `List<String>` lines, then `FileManager.writeLinesToFile("savegame.dat", lines)`.
  - Load: `List<String> lines = FileManager.readLinesFromFile("savegame.dat")` → parse and reconstruct objects.

Code snippet (simplified save sketch):

```java
// Example inside GameManager
public void saveGame() {
    List<String> lines = new ArrayList<>();
    lines.add("roundIndex=" + roundsManager.getCurrentRoundNumber());
    lines.add("playerName=" + getPlayerName());
    lines.add("score=" + getScore());
    lines.add("lives=" + getLives());

    for (Ball b : balls) {
        lines.add(String.format("ball,%f,%f,%f,%f,%b", b.getX(), b.getY(), b.getVelocity().getDx(), b.getVelocity().getDy(), b.isAttached()));
    }
    // paddle line
    lines.add(String.format("paddle,%f,%f,%b,%b", paddle.getX(), paddle.getWidth(), paddle.isLaserEnabled(), paddle.isCatchModeEnabled()));

    // bricks
    for (Brick br : bricks) {
        lines.add(String.format("brick,%s,%f,%f,%d,%b", br.getBrickType().name(), br.getX(), br.getY(), br.getHitPoints(), br.isAlive()));
    }

    FileManager.writeLinesToFile("savegame.dat", lines);
}
```

7) Bảo mật & atomicity
- `FileManager.writeFileAtomic()` đảm bảo ghi nguyên tử (viết vào temp + move). Điều này là phù hợp cho save/load.
- Các thao tác I/O nặng nên chạy trên background thread (xem `README5.md` về đa luồng) để tránh block UI.

8) Gợi ý mở rộng/tiện ích
- Thêm versioning cho save format (`formatVersion=1`) để tương thích ngược khi thay đổi cấu trúc.
- Cân nhắc dùng JSON + Gson/Jackson nếu chấp nhận thêm dependency — làm code sạch hơn và dễ mở rộng.
- Thêm autosave (periodic) trong background, lưu khi thoát ứng dụng, và menu Load/Save trong UI (screens).

Tham chiếu mã nguồn
- `Utils.FileManager` — atomic write / read, dialog safe, path: `src/Utils/FileManager.java`.
- `Engine.HighScoreManager` — quản lý high scores, lưu/đọc `highscores.dat`, path: `src/Engine/HighScoreManager.java`.
- `Engine.AudioManager` — load/save audio settings via `FileManager`.
- `GameManager` — nơi thích hợp để thêm `saveGame()` / `loadGame()`.

File đã tạo: `docs/README7.md`
