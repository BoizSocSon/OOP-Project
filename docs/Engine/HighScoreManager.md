# HighScoreManager

## Tổng quan
`HighScoreManager` là lớp quản lý bảng xếp hạng điểm cao (High Scores / Leaderboard) của game Arkanoid. Lớp này chịu trách nhiệm lưu trữ, đọc, ghi, và xử lý logic cho top 10 điểm số cao nhất, đồng thời persist data vào file để giữ lại giữa các phiên chơi.

HighScoreManager tích hợp với `FileManager` để lưu trữ persistent và sử dụng inner class `HighScoreEntry` để đại diện cho mỗi entry trong bảng xếp hạng.

## Package
```
Engine.HighScoreManager
```

## Thuộc tính

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Mô tả |
|-----------|-------------|-----------------|-------|
| `highScores` | `List<HighScoreEntry>` | `private` | Danh sách các entry điểm cao, được sắp xếp giảm dần theo điểm |
| `MAX_ENTRIES` | `int` | `private static final` | Số lượng entry tối đa trong bảng xếp hạng (10) |
| `SAVE_FILE` | `String` | `private static final` | Tên file lưu trữ high scores (`"highscores.dat"`) |
| `DATE_FORMATTER` | `DateTimeFormatter` | `private static final` | Định dạng ngày tháng hiển thị (`"MM/dd/yyyy"`) |

### Chi tiết thuộc tính

#### highScores
Danh sách lưu trữ tất cả các high score entries. Đặc điểm:
- Luôn được sắp xếp theo điểm số giảm dần (cao → thấp)
- Tối đa 10 entries (MAX_ENTRIES)
- Được load từ file khi khởi tạo
- Được save vào file mỗi khi có thay đổi

```java
private List<HighScoreEntry> highScores = new ArrayList<>();
```

#### MAX_ENTRIES
Số lượng entry tối đa trong bảng xếp hạng. Giá trị mặc định là 10.

```java
private static final int MAX_ENTRIES = 10; // Top 10 high scores
```

#### SAVE_FILE
Tên file lưu trữ high scores data. File này được lưu trong thư mục game.

```java
private static final String SAVE_FILE = "highscores.dat";
```

**File format:**
```
1|STEVE|50000|2024-11-02
2|ALICE|45000|2024-11-03
3|BOB|40000|2024-11-04
...
```

#### DATE_FORMATTER
Formatter để hiển thị ngày tháng theo định dạng "MM/dd/yyyy" (American format).

```java
private static final DateTimeFormatter DATE_FORMATTER = 
    DateTimeFormatter.ofPattern("MM/dd/yyyy");
```

**Ví dụ:** `11/09/2024`

---

## Inner Class: HighScoreEntry

`HighScoreEntry` là inner class static đại diện cho một entry trong bảng xếp hạng.

### Thuộc tính của HighScoreEntry

| Thuộc tính | Kiểu dữ liệu | Mô tả |
|-----------|-------------|-------|
| `rank` | `int` | Hạng của người chơi (1 = cao nhất) |
| `playerName` | `String` | Tên người chơi (uppercase) |
| `score` | `int` | Điểm số đạt được |
| `date` | `LocalDate` | Ngày đạt được điểm số |

### Constructor

```java
public HighScoreEntry(String playerName, int score, LocalDate date)
```

Tạo một entry mới với tên, điểm và ngày.

**Tham số:**
- `playerName` - Tên người chơi
- `score` - Điểm số
- `date` - Ngày đạt được điểm

**Ví dụ:**
```java
LocalDate today = LocalDate.now();
HighScoreEntry entry = new HighScoreEntry("STEVE", 50000, today);
```

### Phương thức của HighScoreEntry

#### getRank() / setRank()
```java
public int getRank()
public void setRank(int rank)
```

Lấy/đặt hạng của entry.

**Ví dụ:**
```java
entry.setRank(1); // Đặt hạng 1 (cao nhất)
int rank = entry.getRank(); // Lấy hạng
```

#### getPlayerName()
```java
public String getPlayerName()
```

Lấy tên người chơi.

#### getScore()
```java
public int getScore()
```

Lấy điểm số.

#### getDate()
```java
public LocalDate getDate()
```

Lấy ngày đạt được điểm số dạng LocalDate.

#### getFormattedDate()
```java
public String getFormattedDate()
```

Lấy ngày đạt được điểm số theo định dạng chuỗi "MM/dd/yyyy".

**Ví dụ:**
```java
String dateStr = entry.getFormattedDate();
// Output: "11/09/2024"
```

#### toString()
```java
@Override
public String toString()
```

Chuyển đổi entry thành chuỗi để lưu file. Format: `"rank|name|score|date"`

**Ví dụ:**
```java
String line = entry.toString();
// Output: "1|STEVE|50000|2024-11-09"
```

#### fromString()
```java
public static HighScoreEntry fromString(String line)
```

Parse một entry từ String (đọc từ file).

**Tham số:**
- `line` - Chuỗi format: `"rank|name|score|date"`

**Giá trị trả về:**
- `HighScoreEntry` - Entry đã parse
- `null` - Nếu chuỗi không hợp lệ

**Ví dụ:**
```java
String line = "1|STEVE|50000|2024-11-09";
HighScoreEntry entry = HighScoreEntry.fromString(line);

if (entry != null) {
    System.out.println(entry.getPlayerName()); // "STEVE"
    System.out.println(entry.getScore()); // 50000
}
```

---

## Constructor

### HighScoreManager()
Khởi tạo HighScoreManager, tạo danh sách rỗng và load điểm từ file.

**Các bước khởi tạo:**
1. Tạo danh sách rỗng `highScores`
2. Gọi `loadFromFile()` để đọc điểm từ file
3. Nếu không có file → tạo default scores

**Ví dụ:**
```java
HighScoreManager hsm = new HighScoreManager();
// Sau khi khởi tạo:
// - highScores đã được load từ file (hoặc default scores)
// - Ranks đã được cập nhật
```

---

## Phương thức công khai

### 1. addScore()
```java
public boolean addScore(String playerName, int score, LocalDate date)
```

Thêm điểm mới vào danh sách high scores (nếu đủ điều kiện).

**Tham số:**
- `playerName` - Tên người chơi (sẽ được uppercase)
- `score` - Điểm số
- `date` - Ngày đạt được điểm

**Giá trị trả về:**
- `true` - Điểm được thêm vào top scores
- `false` - Điểm không đủ điều kiện

**Thuật toán:**

1. **Xử lý tên rỗng:**
   ```java
   if (playerName == null || playerName.trim().isEmpty()) {
       playerName = "ANONYMOUS";
   }
   ```

2. **Tạo entry mới:**
   ```java
   HighScoreEntry newEntry = new HighScoreEntry(
       playerName.toUpperCase(), 
       score, 
       date
   );
   ```

3. **Kiểm tra điều kiện:**
   ```java
   if (highScores.size() < MAX_ENTRIES || 
       score > highScores.get(highScores.size() - 1).getScore()) {
       // Đủ điều kiện → Thêm vào
   }
   ```

4. **Thêm và sắp xếp:**
   ```java
   highScores.add(newEntry);
   Collections.sort(highScores, 
       Comparator.comparingInt(HighScoreEntry::getScore).reversed());
   ```

5. **Giữ top 10:**
   ```java
   if (highScores.size() > MAX_ENTRIES) {
       highScores = highScores.subList(0, MAX_ENTRIES);
   }
   ```

6. **Cập nhật ranks và save:**
   ```java
   updateRanks();
   saveToFile();
   return true;
   ```

**Ví dụ:**
```java
HighScoreManager hsm = new HighScoreManager();

// Thêm điểm mới
boolean added = hsm.addScore("STEVE", 50000, LocalDate.now());

if (added) {
    System.out.println("New high score!");
} else {
    System.out.println("Score not high enough");
}

// Tên rỗng → ANONYMOUS
hsm.addScore("", 30000, LocalDate.now());
// playerName sẽ là "ANONYMOUS"

// Tên lowercase → UPPERCASE
hsm.addScore("alice", 40000, LocalDate.now());
// playerName sẽ là "ALICE"
```

**Flow Diagram:**
```
addScore(name, score, date)
         │
         ↓
    ┌─────────────┐
    │ name empty? │ Yes → name = "ANONYMOUS"
    └──────┬──────┘
           │ No
           ↓
    Create new entry
    (name.toUpperCase())
           │
           ↓
    ┌──────────────────┐
    │ List full?       │
    │ AND              │
    │ score <= lowest? │ Yes → Return false
    └──────┬───────────┘
           │ No
           ↓
    Add to list
           │
           ↓
    Sort descending
           │
           ↓
    Trim to MAX_ENTRIES
           │
           ↓
    updateRanks()
           │
           ↓
    saveToFile()
           │
           ↓
    Return true
```

---

### 2. getTopScores()
```java
public List<HighScoreEntry> getTopScores(int count)
```

Lấy danh sách N điểm cao nhất.

**Tham số:**
- `count` - Số lượng entries muốn lấy

**Giá trị trả về:**
- `List<HighScoreEntry>` - Danh sách entries (copy mới, không phải reference)

**Ví dụ:**
```java
// Lấy top 5
List<HighScoreEntry> top5 = hsm.getTopScores(5);

for (HighScoreEntry entry : top5) {
    System.out.printf("#%d - %s: %d (%s)%n",
        entry.getRank(),
        entry.getPlayerName(),
        entry.getScore(),
        entry.getFormattedDate()
    );
}

// Output:
// #1 - STEVE: 50000 (11/02/2024)
// #2 - ALICE: 45000 (11/03/2024)
// #3 - BOB: 40000 (11/04/2024)
// #4 - CHARLIE: 35000 (11/05/2024)
// #5 - DIANA: 30000 (11/06/2024)
```

**Lưu ý:**
- Nếu `count > size`, trả về tất cả có sẵn
- Return new ArrayList (defensive copy)

---

### 3. getAllScores()
```java
public List<HighScoreEntry> getAllScores()
```

Lấy tất cả high scores hiện có.

**Giá trị trả về:**
- `List<HighScoreEntry>` - Danh sách tất cả entries (copy)

**Ví dụ:**
```java
List<HighScoreEntry> all = hsm.getAllScores();
System.out.println("Total high scores: " + all.size());

// Render full leaderboard
for (HighScoreEntry entry : all) {
    renderHighScoreRow(entry);
}
```

---

### 4. isHighScore()
```java
public boolean isHighScore(int score)
```

Kiểm tra xem một điểm có đủ điều kiện là high score không.

**Tham số:**
- `score` - Điểm cần kiểm tra

**Giá trị trả về:**
- `true` - Điểm đủ điều kiện lọt vào top 10
- `false` - Điểm không đủ điều kiện

**Logic:**
- Nếu bảng chưa đầy (< 10 entries) → `true` (mọi điểm đều được)
- Ngược lại → so sánh với điểm thấp nhất (entry cuối)

**Ví dụ:**
```java
int playerScore = 35000;

if (hsm.isHighScore(playerScore)) {
    System.out.println("Congratulations! New high score!");
    showHighScoreInputScreen(); // Cho nhập tên
} else {
    System.out.println("Try again!");
}
```

**Use case trong GameManager:**
```java
public void onGameOver() {
    if (highScoreManager.isHighScore(score)) {
        // Show special celebration animation
        showNewHighScoreAnimation();
        
        // Prompt player to enter name
        String name = promptPlayerName();
        highScoreManager.addScore(name, score, LocalDate.now());
    }
    
    // Show game over screen
    stateManager.setState(GameState.GAME_OVER);
}
```

---

### 5. getHighestScore()
```java
public int getHighestScore()
```

Lấy điểm cao nhất (Top 1).

**Giá trị trả về:**
- `int` - Điểm cao nhất
- `0` - Nếu danh sách trống

**Ví dụ:**
```java
int highest = hsm.getHighestScore();
System.out.println("Record to beat: " + highest);

// Hiển thị trên menu
renderText("HIGH SCORE: " + highest, x, y, gc);

// Animation khi phá kỷ lục
if (currentScore > highest) {
    showRecordBreakAnimation();
}
```

---

### 6. reset()
```java
public void reset()
```

Đặt lại (Reset) tất cả high scores về điểm mặc định và lưu file.

**Chức năng:**
- Xóa toàn bộ high scores hiện tại
- Tạo lại 10 default entries
- Lưu vào file

**Ví dụ:**
```java
// Settings screen - Reset leaderboard
public void handleResetButton() {
    boolean confirmed = showConfirmDialog(
        "Are you sure you want to reset all high scores?"
    );
    
    if (confirmed) {
        hsm.reset();
        System.out.println("High scores reset to defaults");
    }
}
```

**Default Scores sau khi reset:**
```
#1 - STEVE:   50000 (7 days ago)
#2 - ALICE:   45000 (6 days ago)
#3 - BOB:     40000 (5 days ago)
#4 - CHARLIE: 35000 (4 days ago)
#5 - DIANA:   30000 (3 days ago)
#6 - EVAN:    25000 (2 days ago)
#7 - FIONA:   20000 (1 day ago)
#8 - GEORGE:  15000 (today)
#9 - HANNAH:  10000 (today)
#10 - IAN:    5000  (today)
```

---

## Phương thức riêng tư

### 1. updateRanks()
```java
private void updateRanks()
```

Cập nhật hạng (rank) cho tất cả các entry trong danh sách.

**Thuật toán:**
```java
for (int i = 0; i < highScores.size(); i++) {
    highScores.get(i).setRank(i + 1);
}
```

**Ví dụ:**
```
Index | Score  | Rank
------|--------|------
  0   | 50000  |  1
  1   | 45000  |  2
  2   | 40000  |  3
  ...
```

**Khi nào gọi:**
- Sau khi thêm entry mới
- Sau khi load từ file
- Sau khi reset

---

### 2. saveToFile()
```java
private void saveToFile()
```

Lưu danh sách high scores hiện tại vào file.

**Thuật toán:**
1. Tạo List<String> rỗng
2. Convert mỗi entry → String bằng `toString()`
3. Ghi tất cả lines vào file bằng `FileManager.writeLinesToFile()`

**File format:**
```
1|STEVE|50000|2024-11-02
2|ALICE|45000|2024-11-03
3|BOB|40000|2024-11-04
```

**Ví dụ code:**
```java
private void saveToFile() {
    List<String> lines = new ArrayList<>();
    
    for (HighScoreEntry entry : highScores) {
        lines.add(entry.toString());
    }
    
    FileManager.writeLinesToFile(SAVE_FILE, lines);
}
```

**Khi nào gọi:**
- Sau khi thêm entry mới (`addScore()`)
- Sau khi reset (`reset()`)

---

### 3. loadFromFile()
```java
private void loadFromFile()
```

Đọc danh sách high scores từ file.

**Thuật toán:**
1. Đọc lines từ file bằng `FileManager.readLinesFromFile()`
2. Nếu file tồn tại và có data:
   - Clear danh sách hiện tại
   - Parse từng line → HighScoreEntry
   - Add vào danh sách
   - Update ranks
3. Nếu không có file hoặc file trống:
   - Tạo default scores

**Ví dụ code:**
```java
private void loadFromFile() {
    List<String> lines = FileManager.readLinesFromFile(SAVE_FILE);
    
    if (lines != null && !lines.isEmpty()) {
        highScores.clear();
        
        for (String line : lines) {
            HighScoreEntry entry = HighScoreEntry.fromString(line);
            if (entry != null) {
                highScores.add(entry);
            }
        }
        
        updateRanks();
    } else {
        createDefaultScores();
    }
}
```

**Khi nào gọi:**
- Trong constructor (khi khởi tạo HighScoreManager)

---

### 4. createDefaultScores()
```java
private void createDefaultScores()
```

Tạo danh sách điểm cao mặc định (Default High Scores).

**Chức năng:**
- Xóa danh sách hiện tại
- Thêm 10 default entries với điểm từ 50000 → 5000
- Mỗi entry có ngày khác nhau (từ 7 ngày trước → hôm nay)

**Default entries:**
```java
LocalDate today = LocalDate.now();

addScore("STEVE", 50000, today.minusDays(7));
addScore("ALICE", 45000, today.minusDays(6));
addScore("BOB", 40000, today.minusDays(5));
addScore("CHARLIE", 35000, today.minusDays(4));
addScore("DIANA", 30000, today.minusDays(3));
addScore("EVAN", 25000, today.minusDays(2));
addScore("FIONA", 20000, today.minusDays(1));
addScore("GEORGE", 15000, today);
addScore("HANNAH", 10000, today);
addScore("IAN", 5000, today);
```

**Khi nào gọi:**
- Khi không tìm thấy file hoặc file rỗng (`loadFromFile()`)
- Khi reset high scores (`reset()`)

---

## Sơ đồ hoạt động

### Flow: Thêm điểm mới
```
Player finishes game
with score = 48000
         │
         ↓
┌────────────────────┐
│ isHighScore(48000)?│
└────────┬───────────┘
         │ true
         ↓
┌────────────────────┐
│ Show input dialog  │
│ "Enter your name"  │
└────────┬───────────┘
         │
    User enters "Alice"
         │
         ↓
┌─────────────────────────────┐
│ addScore("Alice", 48000,    │
│          LocalDate.now())   │
└────────┬────────────────────┘
         │
         ↓
┌────────────────────┐
│ Convert to ALICE   │
└────────┬───────────┘
         │
         ↓
┌────────────────────┐
│ Create new entry   │
└────────┬───────────┘
         │
         ↓
┌────────────────────┐
│ Check if qualified │
│ (list < 10 OR      │
│  score > lowest)   │
└────────┬───────────┘
         │ true
         ↓
┌────────────────────┐
│ Add to list        │
└────────┬───────────┘
         │
         ↓
┌────────────────────┐
│ Sort descending    │
└────────┬───────────┘
         │
         ↓
┌────────────────────┐
│ Trim to top 10     │
└────────┬───────────┘
         │
         ↓
┌────────────────────┐
│ updateRanks()      │
└────────┬───────────┘
         │
         ↓
┌────────────────────┐
│ saveToFile()       │
└────────┬───────────┘
         │
         ↓
┌────────────────────┐
│ Show celebration   │
│ "Rank #2!"         │
└────────────────────┘
```

### Flow: Load từ file
```
HighScoreManager constructor
         │
         ↓
┌────────────────────┐
│ loadFromFile()     │
└────────┬───────────┘
         │
         ↓
┌────────────────────┐
│ File exists?       │
└────┬───────────┬───┘
     │ Yes       │ No
     ↓           ↓
┌─────────┐  ┌──────────────────┐
│ Read    │  │ createDefault-   │
│ lines   │  │ Scores()         │
└────┬────┘  └──────────────────┘
     │
     ↓
┌─────────────────────┐
│ Parse each line     │
│ → HighScoreEntry    │
└────┬────────────────┘
     │
     ↓
┌─────────────────────┐
│ updateRanks()       │
└─────────────────────┘
```

---

## Ví dụ sử dụng

### Ví dụ 1: Khởi tạo và hiển thị leaderboard
```java
public class HighScoreDisplay extends Screen {
    private HighScoreManager highScoreManager;
    
    public HighScoreDisplay(HighScoreManager hsm) {
        this.highScoreManager = hsm;
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Title
        UIHelper.drawCenteredText(gc, "HIGH SCORES", 
            canvas.getWidth() / 2, 100, font48, Color.GOLD);
        
        // Draw table
        List<HighScoreEntry> scores = highScoreManager.getAllScores();
        
        int startY = 200;
        int rowHeight = 40;
        
        for (int i = 0; i < scores.size(); i++) {
            HighScoreEntry entry = scores.get(i);
            int y = startY + (i * rowHeight);
            
            // Rank
            gc.fillText(String.valueOf(entry.getRank()), 150, y);
            
            // Name
            gc.fillText(entry.getPlayerName(), 250, y);
            
            // Score
            gc.fillText(String.valueOf(entry.getScore()), 450, y);
            
            // Date
            gc.fillText(entry.getFormattedDate(), 600, y);
        }
    }
}
```

**Output:**
```
╔══════════════════════════════════════╗
║         🏆 HIGH SCORES 🏆            ║
╠══════════════════════════════════════╣
║                                      ║
║  #  NAME      SCORE      DATE        ║
║  ─  ────────  ─────────  ──────────  ║
║  1  STEVE     50000      11/02/2024  ║
║  2  ALICE     45000      11/03/2024  ║
║  3  BOB       40000      11/04/2024  ║
║  4  CHARLIE   35000      11/05/2024  ║
║  5  DIANA     30000      11/06/2024  ║
║  6  EVAN      25000      11/07/2024  ║
║  7  FIONA     20000      11/08/2024  ║
║  8  GEORGE    15000      11/09/2024  ║
║  9  HANNAH    10000      11/09/2024  ║
║  10 IAN       5000       11/09/2024  ║
║                                      ║
╚══════════════════════════════════════╝
```

### Ví dụ 2: Kiểm tra và save new high score
```java
public class GameManager {
    private HighScoreManager highScoreManager;
    private ScoreManager scoreManager;
    private String playerName;
    
    public void onGameOver() {
        int finalScore = scoreManager.getScore();
        
        // Kiểm tra high score
        if (highScoreManager.isHighScore(finalScore)) {
            System.out.println("NEW HIGH SCORE!");
            
            // Show celebration animation
            showNewHighScoreAnimation();
            
            // Add to leaderboard
            boolean added = highScoreManager.addScore(
                playerName,
                finalScore,
                LocalDate.now()
            );
            
            if (added) {
                // Show rank achieved
                List<HighScoreEntry> all = highScoreManager.getAllScores();
                for (HighScoreEntry entry : all) {
                    if (entry.getPlayerName().equals(playerName.toUpperCase()) &&
                        entry.getScore() == finalScore) {
                        showRankAchieved(entry.getRank());
                        break;
                    }
                }
            }
        }
        
        // Transition to game over screen
        stateManager.setState(GameState.GAME_OVER);
    }
    
    private void showRankAchieved(int rank) {
        System.out.println("You achieved rank #" + rank + "!");
        // Show special UI notification...
    }
}
```

### Ví dụ 3: GameOverScreen với high score animation
```java
public class GameOverScreen extends Screen {
    private HighScoreManager highScoreManager;
    private int finalScore;
    private String playerName;
    private boolean isNewHighScore;
    private int achievedRank;
    private double rotationAngle = 0;
    
    public void setGameResult(String playerName, int score) {
        this.playerName = playerName;
        this.finalScore = score;
        this.isNewHighScore = highScoreManager.isHighScore(score);
        
        if (isNewHighScore) {
            // Find achieved rank
            List<HighScoreEntry> all = highScoreManager.getAllScores();
            for (HighScoreEntry entry : all) {
                if (entry.getScore() == score && 
                    entry.getPlayerName().equals(playerName.toUpperCase())) {
                    achievedRank = entry.getRank();
                    break;
                }
            }
        }
    }
    
    @Override
    public void update(double deltaTime) {
        if (isNewHighScore) {
            rotationAngle += 180 * deltaTime; // Rotate 180°/sec
        }
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Title
        UIHelper.drawCenteredText(gc, "GAME OVER", 
            canvas.getWidth() / 2, 100, font64, Color.RED);
        
        // Score
        String scoreText = "Final Score: " + finalScore;
        UIHelper.drawCenteredText(gc, scoreText,
            canvas.getWidth() / 2, 200, font32, Color.WHITE);
        
        // New high score notification
        if (isNewHighScore) {
            // Rotating stars animation
            drawRotatingStar(gc, 200, 300, rotationAngle);
            drawRotatingStar(gc, 600, 300, -rotationAngle);
            
            // Message
            UIHelper.drawCenteredText(gc, "🏆 NEW HIGH SCORE! 🏆",
                canvas.getWidth() / 2, 300, font48, Color.GOLD);
            
            UIHelper.drawCenteredText(gc, "Rank: #" + achievedRank,
                canvas.getWidth() / 2, 360, font32, Color.YELLOW);
        }
        
        // Return to menu button
        drawButton(gc, "RETURN TO MENU (ENTER)");
    }
    
    private void drawRotatingStar(GraphicsContext gc, double x, double y, double angle) {
        gc.save();
        gc.translate(x, y);
        gc.rotate(angle);
        
        // Draw star polygon...
        gc.setFill(Color.GOLD);
        gc.fillPolygon(/* star points */);
        
        gc.restore();
    }
}
```

### Ví dụ 4: Settings screen với reset button
```java
public class SettingsScreen extends Screen {
    private HighScoreManager highScoreManager;
    private Button resetHighScoresButton;
    
    public void initialize() {
        resetHighScoresButton = new Button(
            "RESET HIGH SCORES",
            300, 400, 400, 60
        );
    }
    
    @Override
    public void handleMousePressed(MouseEvent event) {
        if (resetHighScoresButton.contains(event.getX(), event.getY())) {
            handleResetHighScores();
        }
    }
    
    private void handleResetHighScores() {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset High Scores");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This will reset all high scores to defaults.");
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            highScoreManager.reset();
            System.out.println("High scores reset to defaults");
            
            // Show success message
            showNotification("High scores reset successfully!");
        }
    }
}
```

### Ví dụ 5: Testing HighScoreManager
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HighScoreManagerTest {
    @Test
    void testAddScore() {
        HighScoreManager hsm = new HighScoreManager();
        hsm.reset(); // Start fresh
        
        boolean added = hsm.addScore("TEST", 100000, LocalDate.now());
        assertTrue(added);
        
        assertEquals(100000, hsm.getHighestScore());
    }
    
    @Test
    void testIsHighScore() {
        HighScoreManager hsm = new HighScoreManager();
        
        int lowest = hsm.getAllScores().get(9).getScore();
        
        assertTrue(hsm.isHighScore(lowest + 1));
        assertFalse(hsm.isHighScore(lowest - 1));
    }
    
    @Test
    void testMaxEntries() {
        HighScoreManager hsm = new HighScoreManager();
        hsm.reset();
        
        // Add 20 scores
        for (int i = 0; i < 20; i++) {
            hsm.addScore("PLAYER" + i, (i + 1) * 1000, LocalDate.now());
        }
        
        // Should keep only top 10
        assertEquals(10, hsm.getAllScores().size());
        
        // Highest score should be 20000
        assertEquals(20000, hsm.getHighestScore());
    }
    
    @Test
    void testSortingOrder() {
        HighScoreManager hsm = new HighScoreManager();
        hsm.reset();
        
        List<HighScoreEntry> scores = hsm.getAllScores();
        
        // Verify descending order
        for (int i = 0; i < scores.size() - 1; i++) {
            assertTrue(scores.get(i).getScore() >= scores.get(i + 1).getScore());
        }
    }
    
    @Test
    void testAnonymousName() {
        HighScoreManager hsm = new HighScoreManager();
        
        hsm.addScore("", 60000, LocalDate.now());
        
        List<HighScoreEntry> scores = hsm.getAllScores();
        boolean foundAnonymous = false;
        
        for (HighScoreEntry entry : scores) {
            if (entry.getPlayerName().equals("ANONYMOUS") && entry.getScore() == 60000) {
                foundAnonymous = true;
                break;
            }
        }
        
        assertTrue(foundAnonymous);
    }
    
    @Test
    void testUpperCase() {
        HighScoreManager hsm = new HighScoreManager();
        
        hsm.addScore("alice", 55000, LocalDate.now());
        
        List<HighScoreEntry> scores = hsm.getAllScores();
        boolean foundAlice = false;
        
        for (HighScoreEntry entry : scores) {
            if (entry.getPlayerName().equals("ALICE") && entry.getScore() == 55000) {
                foundAlice = true;
                break;
            }
        }
        
        assertTrue(foundAlice);
    }
}
```

---

## Best Practices

### 1. Defensive copying
```java
// ✅ ĐÚNG: Return new ArrayList (copy)
public List<HighScoreEntry> getAllScores() {
    return new ArrayList<>(highScores);
}

// ❌ SAI: Return reference trực tiếp
public List<HighScoreEntry> getAllScores() {
    return highScores; // Caller có thể modify internal state!
}
```

### 2. Validate input
```java
// ✅ ĐÚNG: Xử lý tên rỗng
if (playerName == null || playerName.trim().isEmpty()) {
    playerName = "ANONYMOUS";
}

// ✅ ĐÚNG: Uppercase để consistency
playerName = playerName.toUpperCase();

// ❌ SAI: Không validate
// Có thể lưu tên rỗng hoặc inconsistent casing
```

### 3. Immutable constants
```java
// ✅ ĐÚNG: Use constants
private static final int MAX_ENTRIES = 10;
private static final String SAVE_FILE = "highscores.dat";

// ❌ SAI: Hardcode values
if (highScores.size() > 10) { ... }
FileManager.readLinesFromFile("highscores.dat");
```

### 4. Persistent storage
```java
// ✅ ĐÚNG: Save sau mỗi thay đổi
public boolean addScore(...) {
    // ... add logic
    saveToFile(); // Persist immediately
    return true;
}

// ❌ SAI: Không save → mất data khi crash
public boolean addScore(...) {
    // ... add logic
    return true; // Forgot to save!
}
```

### 5. Error handling
```java
// ✅ ĐÚNG: Handle parse errors
public static HighScoreEntry fromString(String line) {
    try {
        // ... parse logic
    } catch (Exception e) {
        System.err.println("Error parsing: " + line);
        return null; // Graceful failure
    }
}

// ❌ SAI: Let exceptions propagate
public static HighScoreEntry fromString(String line) {
    String[] parts = line.split("\\|");
    int score = Integer.parseInt(parts[2]); // ArrayIndexOutOfBoundsException!
}
```

### 6. Date handling
```java
// ✅ ĐÚNG: Use LocalDate (modern Java 8+ API)
private LocalDate date;

// ✅ ĐÚNG: Format for display
public String getFormattedDate() {
    return date.format(DATE_FORMATTER);
}

// ❌ SAI: Use deprecated Date class
private Date date; // Old API, avoid
```

---

## Dependencies

### Imports
```java
import Utils.FileManager;               // File I/O operations
import java.time.LocalDate;             // Date representation
import java.time.format.DateTimeFormatter; // Date formatting
import java.util.ArrayList;
import java.util.Collections;            // Sorting
import java.util.Comparator;            // Comparator for sorting
import java.util.List;
```

### Các lớp phụ thuộc

| Lớp | Vai trò | Phương thức sử dụng |
|-----|---------|---------------------|
| `FileManager` | File I/O utility | `readLinesFromFile()`, `writeLinesToFile()` |
| `LocalDate` | Date representation | `now()`, `minusDays()`, `parse()`, `format()`, `toString()` |
| `DateTimeFormatter` | Date formatting | `ofPattern()`, `format()` |
| `Collections` | List utilities | `sort()` |
| `Comparator` | Comparison logic | `comparingInt()`, `reversed()` |

### Được sử dụng bởi:
- `GameManager` - Kiểm tra và save high scores sau game over
- `GameOverScreen` - Hiển thị high score notification và animation
- `WinScreen` - Hiển thị high score cho win state
- `HighScoreDisplay` - Render leaderboard table
- `MainMenu` - Hiển thị highest score
- `SettingsScreen` - Reset high scores

### Kiến trúc phụ thuộc
```
┌──────────────────────────────┐
│    HighScoreManager          │
│  - highScores: List          │
│  + addScore()                │
│  + getTopScores()            │
│  + isHighScore()             │
└────────┬─────────────────────┘
         │
         ├──→ HighScoreEntry (inner class)
         │    - rank, playerName, score, date
         │
         ├──→ FileManager (file I/O)
         │
         ├──→ LocalDate (Java 8 Time API)
         │
         └──→ Collections/Comparator (sorting)

Used by:
    ├──→ GameManager (check & save)
    ├──→ GameOverScreen (display notification)
    ├──→ WinScreen (display notification)
    ├──→ HighScoreDisplay (render leaderboard)
    ├──→ MainMenu (show highest)
    └──→ SettingsScreen (reset function)
```

---

## File Format

### highscores.dat structure
```
1|STEVE|50000|2024-11-02
2|ALICE|45000|2024-11-03
3|BOB|40000|2024-11-04
4|CHARLIE|35000|2024-11-05
5|DIANA|30000|2024-11-06
6|EVAN|25000|2024-11-07
7|FIONA|20000|2024-11-08
8|GEORGE|15000|2024-11-09
9|HANNAH|10000|2024-11-09
10|IAN|5000|2024-11-09
```

**Format:** `rank|playerName|score|date`

**Field descriptions:**
- `rank` - Integer (1-10)
- `playerName` - String (uppercase, no spaces allowed by default)
- `score` - Integer (positive)
- `date` - ISO 8601 format (YYYY-MM-DD)

**Delimiter:** Pipe character `|`

**Encoding:** UTF-8 (default)

---

## Mở rộng trong tương lai

### 1. Thêm statistics
```java
public class HighScoreEntry {
    private int rank;
    private String playerName;
    private int score;
    private LocalDate date;
    
    // New fields
    private int roundReached;        // Round đạt được
    private int bricksDestroyed;     // Số gạch phá
    private int powerUpsCollected;   // Số PowerUps thu thập
    private Duration timePlayed;     // Thời gian chơi
    
    // Getters...
}

// File format extended:
// 1|STEVE|50000|2024-11-02|4|324|12|00:15:32
```

### 2. Multiple leaderboards
```java
public class HighScoreManager {
    private Map<String, List<HighScoreEntry>> leaderboards;
    
    public HighScoreManager() {
        leaderboards = new HashMap<>();
        leaderboards.put("DAILY", new ArrayList<>());
        leaderboards.put("WEEKLY", new ArrayList<>());
        leaderboards.put("MONTHLY", new ArrayList<>());
        leaderboards.put("ALL_TIME", new ArrayList<>());
    }
    
    public List<HighScoreEntry> getDailyTopScores() {
        return leaderboards.get("DAILY");
    }
    
    public void addScore(String name, int score, LocalDate date) {
        // Add to all applicable leaderboards
        addToLeaderboard("ALL_TIME", name, score, date);
        
        if (date.equals(LocalDate.now())) {
            addToLeaderboard("DAILY", name, score, date);
        }
        // ... weekly, monthly checks
    }
}
```

### 3. Cloud sync
```java
public class HighScoreManager {
    private CloudSyncService cloudService;
    private boolean cloudSyncEnabled = true;
    
    public void saveToFile() {
        // Local save
        FileManager.writeLinesToFile(SAVE_FILE, lines);
        
        // Cloud sync
        if (cloudSyncEnabled) {
            cloudService.uploadHighScores(highScores);
        }
    }
    
    public void syncFromCloud() {
        if (cloudSyncEnabled) {
            List<HighScoreEntry> cloudScores = cloudService.downloadHighScores();
            mergeWithLocal(cloudScores);
        }
    }
    
    private void mergeWithLocal(List<HighScoreEntry> cloudScores) {
        // Merge cloud + local, keep top 10
        highScores.addAll(cloudScores);
        Collections.sort(highScores, 
            Comparator.comparingInt(HighScoreEntry::getScore).reversed());
        highScores = highScores.subList(0, Math.min(MAX_ENTRIES, highScores.size()));
        updateRanks();
        saveToFile();
    }
}
```

### 4. Achievements system
```java
public class HighScoreManager {
    private AchievementManager achievementManager;
    
    public boolean addScore(String playerName, int score, LocalDate date) {
        boolean added = // ... existing logic
        
        if (added) {
            // Check achievements
            checkAchievements(playerName, score);
        }
        
        return added;
    }
    
    private void checkAchievements(String name, int score) {
        // First high score
        if (getAllScores().stream()
                .filter(e -> e.getPlayerName().equals(name))
                .count() == 1) {
            achievementManager.unlock("FIRST_HIGH_SCORE");
        }
        
        // Top 3
        if (getRankForPlayer(name) <= 3) {
            achievementManager.unlock("TOP_3");
        }
        
        // Score milestones
        if (score >= 100000) {
            achievementManager.unlock("SCORE_100K");
        }
    }
}
```

### 5. Player profiles
```java
public class PlayerProfile {
    private String playerName;
    private List<HighScoreEntry> personalHistory;
    private int gamesPlayed;
    private int totalScore;
    private int averageScore;
    private int bestRank;
    
    public void addGameResult(int score, LocalDate date) {
        gamesPlayed++;
        totalScore += score;
        averageScore = totalScore / gamesPlayed;
        
        HighScoreEntry entry = new HighScoreEntry(playerName, score, date);
        personalHistory.add(entry);
    }
}

public class HighScoreManager {
    private Map<String, PlayerProfile> playerProfiles;
    
    public PlayerProfile getPlayerProfile(String name) {
        return playerProfiles.get(name.toUpperCase());
    }
    
    public void showPlayerStats(String name) {
        PlayerProfile profile = getPlayerProfile(name);
        System.out.println("Games played: " + profile.getGamesPlayed());
        System.out.println("Best rank: #" + profile.getBestRank());
        System.out.println("Average score: " + profile.getAverageScore());
    }
}
```

### 6. Encryption/Security
```java
public class HighScoreManager {
    private static final String ENCRYPTION_KEY = "ArkanoidSecretKey123";
    
    private void saveToFile() {
        List<String> lines = new ArrayList<>();
        
        for (HighScoreEntry entry : highScores) {
            String encrypted = encrypt(entry.toString(), ENCRYPTION_KEY);
            lines.add(encrypted);
        }
        
        FileManager.writeLinesToFile(SAVE_FILE, lines);
    }
    
    private void loadFromFile() {
        List<String> lines = FileManager.readLinesFromFile(SAVE_FILE);
        
        if (lines != null && !lines.isEmpty()) {
            highScores.clear();
            
            for (String encryptedLine : lines) {
                String decrypted = decrypt(encryptedLine, ENCRYPTION_KEY);
                HighScoreEntry entry = HighScoreEntry.fromString(decrypted);
                if (entry != null) {
                    highScores.add(entry);
                }
            }
            
            updateRanks();
        }
    }
    
    private String encrypt(String data, String key) {
        // Use AES encryption
        // ... implementation
    }
    
    private String decrypt(String data, String key) {
        // Use AES decryption
        // ... implementation
    }
}
```

---

## Tổng kết

`HighScoreManager` là lớp quan trọng cho player engagement:
- ✅ **Persistent:** Lưu trữ high scores giữa các phiên chơi
- ✅ **Simple:** API đơn giản và dễ sử dụng
- ✅ **Sorted:** Luôn maintain top 10 được sắp xếp
- ✅ **Validated:** Kiểm tra điều kiện trước khi thêm
- ✅ **Defensive:** Defensive copying và error handling
- ✅ **Flexible:** Dễ dàng extend cho features mới

Kết hợp với UI components, tạo nên một leaderboard system hoàn chỉnh và professional cho game!

---

**Tác giả:** Arkanoid Development Team  
**Phiên bản:** 1.0  
**Cập nhật lần cuối:** 2024
