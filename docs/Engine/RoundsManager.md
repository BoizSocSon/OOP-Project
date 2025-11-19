# RoundsManager

## Tổng quan
`RoundsManager` là lớp quản lý toàn bộ hệ thống vòng chơi (rounds/levels) trong game Arkanoid. Lớp này chịu trách nhiệm:
- Khởi tạo và lưu trữ tất cả các vòng chơi
- Tải và chuyển đổi giữa các vòng chơi
- Theo dõi tiến độ của người chơi
- Kiểm tra điều kiện hoàn thành vòng
- Quản lý danh sách gạch của vòng hiện tại

RoundsManager là lớp trung tâm của game progression, đảm bảo người chơi có thể chơi qua các màn chơi một cách tuần tự và mượt mà.

## Package
```
Engine.RoundsManager
```

## Kiến trúc Round System
```
┌──────────────────────────────┐
│      RoundsManager           │
│  - rounds: List<RoundBase>   │
│  - currentRoundIndex: int    │
│  - currentRound: RoundBase   │
│  - currentBricks: List       │
└──────────┬───────────────────┘
           │
           │ manages
           ↓
┌──────────────────────────────┐
│    RoundBase (Abstract)      │
│  + getRoundName()            │
│  + createBricks()            │
└──────────┬───────────────────┘
           │
           │ implements
           ↓
┌──────────────────────────────┐
│  Concrete Round Classes      │
├──────────────────────────────┤
│  • Round1 (Tutorial)         │
│  • Round2 (Easy)             │
│  • Round3 (Medium)           │
│  • Round4 (Hard)             │
└──────────────────────────────┘
```

---

## Thuộc tính

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Mô tả |
|-----------|-------------|-----------------|-------|
| `rounds` | `List<RoundBase>` | `private final` | Danh sách tất cả các vòng chơi có sẵn trong game |
| `currentRoundIndex` | `int` | `private` | Chỉ số của vòng chơi hiện tại (0-based indexing) |
| `currentRound` | `RoundBase` | `private` | Đối tượng vòng chơi đang được chơi |
| `currentBricks` | `List<Brick>` | `private` | Danh sách gạch của vòng hiện tại |

### Chi tiết thuộc tính

#### rounds
Danh sách chứa tất cả các vòng chơi có sẵn trong game.

```java
private final List<RoundBase> rounds = new ArrayList<>();
```

**Đặc điểm:**
- Immutable reference (final)
- Được khởi tạo trong `initializeRounds()`
- Chứa 4 rounds: Round1, Round2, Round3, Round4
- Thứ tự trong list quyết định progression

**Ví dụ state:**
```java
rounds: [
    Round1 (index=0),  // Tutorial round
    Round2 (index=1),  // Easy
    Round3 (index=2),  // Medium
    Round4 (index=3)   // Hard/Boss
]
```

**Diagram:**
```
rounds List:
┌────────┬────────┬────────┬────────┐
│Round 1 │Round 2 │Round 3 │Round 4 │
└───┬────┴────────┴────────┴────────┘
    │
    └─→ currentRoundIndex points here (example)
```

#### currentRoundIndex
Chỉ số của vòng chơi hiện tại trong danh sách rounds.

```java
private int currentRoundIndex = 0;
```

**Đặc điểm:**
- 0-based indexing (bắt đầu từ 0)
- Range: 0 đến rounds.size() - 1
- Tăng lên khi gọi `nextRound()`
- Reset về 0 khi gọi `reset()`

**Ví dụ progression:**
```
Start game:     currentRoundIndex = 0 (Round 1)
Complete R1:    currentRoundIndex = 1 (Round 2)
Complete R2:    currentRoundIndex = 2 (Round 3)
Complete R3:    currentRoundIndex = 3 (Round 4)
Complete R4:    Game won! (no more rounds)
```

**Quan hệ với display number:**
```java
currentRoundIndex = 0  →  Display: "Round 1"
currentRoundIndex = 1  →  Display: "Round 2"
currentRoundIndex = 2  →  Display: "Round 3"
currentRoundIndex = 3  →  Display: "Round 4"

// Convert to display:
displayNumber = currentRoundIndex + 1
```

#### currentRound
Reference đến đối tượng vòng chơi hiện tại.

```java
private RoundBase currentRound;
```

**Đặc điểm:**
- Có thể null khi mới khởi tạo
- Được set khi gọi `loadRound()`
- Sử dụng để lấy tên và tạo gạch

**Ví dụ:**
```java
currentRound = new Round1();
String name = currentRound.getRoundName(); // "Round 1"
List<Brick> bricks = currentRound.createBricks();
```

#### currentBricks
Danh sách các gạch của vòng chơi hiện tại.

```java
private List<Brick> currentBricks = new ArrayList<>();
```

**Đặc điểm:**
- Được tạo bởi `currentRound.createBricks()`
- Clear khi load round mới
- Các brick có thể bị destroy trong gameplay
- Không bao gồm brick đã destroyed (removed từ list)

**Lifecycle:**
```
loadRound() called
    ↓
currentBricks.clear()
    ↓
currentBricks = currentRound.createBricks()
    ↓
[Gameplay - bricks destroyed]
    ↓
isRoundComplete() checks remaining bricks
    ↓
nextRound() → clear and reload
```

**Ví dụ state:**
```java
// Start of round
currentBricks: [
    NormalBrick(RED, alive=true),
    NormalBrick(BLUE, alive=true),
    SilverBrick(alive=true),
    GoldBrick(alive=true),
    // ... 50+ bricks
]

// During gameplay
currentBricks: [
    NormalBrick(RED, alive=false),  // Destroyed
    NormalBrick(BLUE, alive=true),
    SilverBrick(alive=true),
    GoldBrick(alive=true),          // Indestructible
    // ...
]

// Round complete
currentBricks: [
    GoldBrick(alive=true)  // Only GOLD bricks remain
]
```

---

## Constructor

### RoundsManager()
```java
public RoundsManager()
```

Khởi tạo RoundsManager với cấu hình mặc định.

**Chức năng:**
1. Khởi tạo empty lists
2. Set currentRoundIndex = 0
3. Gọi `initializeRounds()` để load tất cả rounds

**Ví dụ:**
```java
public class GameManager {
    private RoundsManager roundsManager;
    
    public void initialize() {
        roundsManager = new RoundsManager();
        
        // RoundsManager now has 4 rounds loaded
        System.out.println("Rounds available: " + roundsManager.hasNextRound());
    }
}
```

---

## Phương thức riêng tư

### initializeRounds()
```java
private void initializeRounds()
```

Khởi tạo tất cả các vòng chơi có sẵn trong game.

**Chức năng:**
- Tạo instance của mỗi Round class
- Add vào rounds list theo thứ tự

**Implementation:**
```java
private void initializeRounds() {
    rounds.add(new Round1());  // Tutorial/Easy
    rounds.add(new Round2());  // Medium
    rounds.add(new Round3());  // Hard
    rounds.add(new Round4());  // Boss/Final
}
```

**Round characteristics:**

| Round | Tên | Độ khó | Đặc điểm |
|-------|-----|--------|----------|
| Round1 | "Round 1" | Tutorial | Simple patterns, few bricks |
| Round2 | "Round 2" | Easy | More bricks, basic patterns |
| Round3 | "Round 3" | Medium | Complex patterns, silver bricks |
| Round4 | "Round 4" | Hard | Dense patterns, gold bricks |

**Diagram:**
```
initializeRounds()
    │
    ├──→ new Round1() → rounds[0]
    │
    ├──→ new Round2() → rounds[1]
    │
    ├──→ new Round3() → rounds[2]
    │
    └──→ new Round4() → rounds[3]

Result:
rounds.size() = 4
```

---

## Phương thức công khai

### 1. loadRound()
```java
public List<Brick> loadRound(int roundNumber)
```

Tải một vòng chơi cụ thể theo chỉ số.

**Tham số:**
- `roundNumber` - Chỉ số vòng chơi (0-based: 0, 1, 2, 3)

**Giá trị trả về:**
- `List<Brick>` - Danh sách gạch mới của vòng được tải

**Ném exception:**
- `IllegalArgumentException` - Nếu roundNumber < 0 hoặc >= rounds.size()

**Thuật toán:**

1. **Validate index:**
   ```java
   if (roundNumber < 0 || roundNumber >= rounds.size()) {
       throw new IllegalArgumentException("Invalid round number: " + roundNumber);
   }
   ```

2. **Update state:**
   ```java
   currentRoundIndex = roundNumber;
   currentRound = rounds.get(currentRoundIndex);
   ```

3. **Create bricks:**
   ```java
   currentBricks.clear();
   currentBricks = currentRound.createBricks();
   ```

4. **Return bricks:**
   ```java
   return currentBricks;
   ```

**Ví dụ:**
```java
// Load Round 1 (index 0)
List<Brick> bricks = roundsManager.loadRound(0);
System.out.println("Loaded: " + bricks.size() + " bricks");

// Load Round 3 (index 2)
bricks = roundsManager.loadRound(2);

// Invalid - throws exception
try {
    bricks = roundsManager.loadRound(10); // IllegalArgumentException!
} catch (IllegalArgumentException e) {
    System.err.println(e.getMessage()); // "Invalid round number: 10"
}
```

**State changes:**
```
Before loadRound(2):
    currentRoundIndex = 0
    currentRound = Round1 instance
    currentBricks = [50 bricks from Round1]

After loadRound(2):
    currentRoundIndex = 2
    currentRound = Round3 instance
    currentBricks = [80 bricks from Round3]
```

---

### 2. loadFirstRound()
```java
public List<Brick> loadFirstRound()
```

Tải vòng chơi đầu tiên (Round 1).

**Giá trị trả về:**
- `List<Brick>` - Danh sách gạch của Round 1

**Ví dụ:**
```java
public void startNewGame() {
    // Reset game state
    lives = 3;
    score = 0;
    
    // Load first round
    List<Brick> bricks = roundsManager.loadFirstRound();
    
    System.out.println("Starting Round 1 with " + bricks.size() + " bricks");
}
```

**Equivalent to:**
```java
loadFirstRound() == loadRound(0)
```

---

### 3. isRoundComplete()
```java
public boolean isRoundComplete()
```

Kiểm tra xem vòng chơi hiện tại đã hoàn thành chưa.

**Điều kiện hoàn thành:**
- Tất cả gạch phá hủy được (không phải GOLD) đã bị destroy
- Chỉ còn lại gạch GOLD (indestructible) hoặc không còn gạch nào

**Giá trị trả về:**
- `true` - Vòng chơi hoàn thành
- `false` - Còn gạch cần phá

**Thuật toán:**

1. **Empty check:**
   ```java
   if (currentBricks.isEmpty()) {
       return false; // Chưa load round hoặc lỗi
   }
   ```

2. **Check each brick:**
   ```java
   for (Brick brick : currentBricks) {
       if (brick.isAlive() && brick.getBrickType() != BrickType.GOLD) {
           return false; // Còn gạch phá hủy được
       }
   }
   return true; // Tất cả gạch phá hủy được đã destroyed
   ```

**Ví dụ:**
```java
// During gameplay loop
public void update() {
    // Check collisions, destroy bricks...
    
    if (roundsManager.isRoundComplete()) {
        System.out.println("Level Complete!");
        
        if (roundsManager.hasNextRound()) {
            roundsManager.nextRound();
        } else {
            System.out.println("Game Won!");
            stateManager.setState(GameState.WIN);
        }
    }
}
```

**Logic table:**

| Remaining Bricks | isRoundComplete() |
|-----------------|-------------------|
| No bricks | `false` (error state) |
| Only GOLD bricks | `true` ✓ |
| Normal + GOLD bricks | `false` |
| Only Normal bricks (alive) | `false` |
| Only Normal bricks (all destroyed) | `true` ✓ |
| Mixed (some alive, not GOLD) | `false` |

**Scenarios:**
```
Scenario 1: Round complete
currentBricks: [
    GoldBrick(alive=true),
    GoldBrick(alive=true)
]
→ isRoundComplete() = true ✓

Scenario 2: Still playing
currentBricks: [
    NormalBrick(RED, alive=true),
    GoldBrick(alive=true)
]
→ isRoundComplete() = false ✗

Scenario 3: All destroyed
currentBricks: [
    NormalBrick(RED, alive=false),
    NormalBrick(BLUE, alive=false)
]
→ isRoundComplete() = true ✓

Scenario 4: Silver brick remaining
currentBricks: [
    SilverBrick(alive=true, hits=1)
]
→ isRoundComplete() = false ✗
```

---

### 4. nextRound()
```java
public boolean nextRound()
```

Chuyển sang vòng chơi tiếp theo.

**Giá trị trả về:**
- `true` - Load thành công vòng tiếp theo

**Ném exception:**
- `IllegalStateException` - Nếu không còn vòng nào nữa

**Thuật toán:**
```java
int nextRoundIndex = currentRoundIndex + 1;

if (nextRoundIndex >= rounds.size()) {
    throw new IllegalStateException("No more rounds available.");
}

loadRound(nextRoundIndex);
return true;
```

**Ví dụ:**
```java
public void onRoundComplete() {
    System.out.println("Round " + roundsManager.getCurrentRoundNumber() + " complete!");
    
    if (roundsManager.hasNextRound()) {
        // Safe to advance
        roundsManager.nextRound();
        
        System.out.println("Starting " + roundsManager.getCurrentRoundName());
    } else {
        // No more rounds - game won
        System.out.println("Congratulations! You won the game!");
        stateManager.setState(GameState.WIN);
    }
}
```

**Best practice với error handling:**
```java
// ❌ SAI: No check
public void advance() {
    roundsManager.nextRound(); // Có thể throw exception!
}

// ✅ ĐÚNG: Check trước
public void advance() {
    if (roundsManager.hasNextRound()) {
        roundsManager.nextRound();
    } else {
        handleGameWon();
    }
}

// ✅ ĐÚNG: Try-catch
public void advance() {
    try {
        roundsManager.nextRound();
    } catch (IllegalStateException e) {
        System.out.println("No more rounds - game won!");
        handleGameWon();
    }
}
```

**State transition:**
```
Before nextRound():
    currentRoundIndex = 2 (Round 3)
    currentRound = Round3
    currentBricks = Round3 bricks

After nextRound():
    currentRoundIndex = 3 (Round 4)
    currentRound = Round4
    currentBricks = Round4 bricks (newly created)
```

---

### 5. hasNextRound()
```java
public boolean hasNextRound()
```

Kiểm tra xem còn vòng chơi tiếp theo hay không.

**Giá trị trả về:**
- `true` - Còn ít nhất 1 round nữa
- `false` - Đang ở round cuối cùng

**Logic:**
```java
return currentRoundIndex + 1 < rounds.size();
```

**Ví dụ:**
```java
// Round progression check
System.out.println("Current: Round " + roundsManager.getCurrentRoundNumber());
System.out.println("Has next: " + roundsManager.hasNextRound());

// Output at different stages:
// Round 1: "Current: Round 1, Has next: true"
// Round 2: "Current: Round 2, Has next: true"
// Round 3: "Current: Round 3, Has next: true"
// Round 4: "Current: Round 4, Has next: false" ← Last round
```

**Truth table:**

| currentRoundIndex | rounds.size() | hasNextRound() |
|-------------------|---------------|----------------|
| 0 | 4 | `true` (0+1=1 < 4) |
| 1 | 4 | `true` (1+1=2 < 4) |
| 2 | 4 | `true` (2+1=3 < 4) |
| 3 | 4 | `false` (3+1=4 NOT < 4) |

**Sử dụng trong UI:**
```java
public void renderUI(GraphicsContext gc) {
    // Show progress
    String progress = roundsManager.getCurrentRoundNumber() + " / " + 
                     (roundsManager.hasNextRound() ? "?" : "FINAL");
    
    gc.fillText(progress, 10, 30);
    
    // Show "Next Round" button only if applicable
    if (roundsManager.isRoundComplete()) {
        if (roundsManager.hasNextRound()) {
            nextRoundButton.setVisible(true);
        } else {
            victoryScreen.show();
        }
    }
}
```

---

### 6. getCurrentRoundNumber()
```java
public int getCurrentRoundNumber()
```

Lấy số thứ tự của vòng chơi hiện tại (1-based cho display).

**Giá trị trả về:**
- `int` - Số thứ tự vòng (bắt đầu từ 1)

**Logic:**
```java
return currentRoundIndex + 1;
```

**Ví dụ:**
```java
// Display round info to player
System.out.println("You are playing Round " + roundsManager.getCurrentRoundNumber());

// UI display
String title = "ROUND " + roundsManager.getCurrentRoundNumber();
gc.fillText(title, 350, 50);

// Progress bar
int current = roundsManager.getCurrentRoundNumber();
int total = 4; // Total rounds
double progress = (double) current / total * 100;
System.out.println("Progress: " + progress + "%");
```

**Conversion table:**

| Internal Index | Display Number |
|---------------|----------------|
| 0 | 1 |
| 1 | 2 |
| 2 | 3 |
| 3 | 4 |

---

### 7. getCurrentRoundName()
```java
public String getCurrentRoundName()
```

Lấy tên của vòng chơi hiện tại.

**Giá trị trả về:**
- `String` - Tên vòng chơi (từ RoundBase.getRoundName())
- `"Unknown"` - Nếu chưa load round nào

**Ví dụ:**
```java
// Display in UI
String roundName = roundsManager.getCurrentRoundName();
gc.fillText(roundName, 300, 100);

// Log
System.out.println("Starting " + roundsManager.getCurrentRoundName());

// Null safety
if (currentRound == null) {
    return "Unknown"; // Safe default
}
return currentRound.getRoundName();
```

**Typical values:**
```
Round 1: "Round 1"
Round 2: "Round 2"
Round 3: "Round 3"
Round 4: "Round 4"
```

---

### 8. getCurrentBricks()
```java
public List<Brick> getCurrentBricks()
```

Lấy danh sách gạch hiện tại của vòng chơi.

**Giá trị trả về:**
- `List<Brick>` - Reference đến currentBricks list

**Ví dụ:**
```java
// Render all bricks
public void render(GraphicsContext gc) {
    List<Brick> bricks = roundsManager.getCurrentBricks();
    
    for (Brick brick : bricks) {
        if (brick.isAlive()) {
            brick.render(gc);
        }
    }
}

// Check collision with all bricks
public void checkCollisions(Ball ball) {
    List<Brick> bricks = roundsManager.getCurrentBricks();
    
    for (Brick brick : bricks) {
        if (brick.isAlive() && ball.intersects(brick)) {
            brick.hit();
            ball.bounce();
        }
    }
}
```

**Lưu ý:** Returns direct reference, không phải copy.

---

### 9. getRemainingBrickCount()
```java
public int getRemainingBrickCount()
```

Đếm số lượng gạch còn sống trong vòng hiện tại.

**Giá trị trả về:**
- `int` - Số gạch còn alive (bao gồm cả GOLD)

**Thuật toán:**
```java
int count = 0;
for (Brick brick : currentBricks) {
    if (brick.isAlive()) {
        count++;
    }
}
return count;
```

**Ví dụ:**
```java
// Show progress in UI
public void renderProgress(GraphicsContext gc) {
    int remaining = roundsManager.getRemainingBrickCount();
    int total = roundsManager.getCurrentBricks().size();
    
    String progress = remaining + " / " + total + " bricks remaining";
    gc.fillText(progress, 10, 50);
    
    // Progress bar
    double percentage = (1.0 - (double) remaining / total) * 100;
    gc.fillRect(10, 60, percentage * 2, 10);
}

// Achievement tracking
if (roundsManager.getRemainingBrickCount() == 0) {
    System.out.println("Perfect clear!");
    unlockAchievement("PERFECT_CLEAR");
}
```

**Count scenarios:**
```
Start of round: 60 bricks → getRemainingBrickCount() = 60
After 10 hits: 50 bricks → getRemainingBrickCount() = 50
Near end: 5 bricks → getRemainingBrickCount() = 5
Only GOLD: 3 bricks → getRemainingBrickCount() = 3
```

---

### 10. reset()
```java
public void reset()
```

Đặt lại RoundsManager về trạng thái ban đầu.

**Chức năng:**
1. Reset currentRoundIndex về 0
2. Clear currentBricks
3. Load lại Round 1

**Ví dụ:**
```java
// Start new game
public void startNewGame() {
    roundsManager.reset();
    
    // Game now back to Round 1
    System.out.println("Game reset to " + roundsManager.getCurrentRoundName());
}

// Return to menu
public void returnToMenu() {
    roundsManager.reset(); // Clean slate for next game
    stateManager.setState(GameState.MENU);
}

// After game over
public void onGameOver() {
    // Show game over screen
    showGameOverScreen();
    
    // Reset for replay
    if (playerWantsToRetry()) {
        roundsManager.reset();
        lives = 3;
        score = 0;
        stateManager.setState(GameState.PLAYING);
    }
}
```

**State changes:**
```
Before reset():
    currentRoundIndex = 3 (Round 4)
    currentRound = Round4
    currentBricks = Round4 bricks (some destroyed)

After reset():
    currentRoundIndex = 0 (Round 1)
    currentRound = Round1
    currentBricks = Round1 bricks (all new, all alive)
```

---

### 11. getRoundInfo()
```java
public String getRoundInfo()
```

Trả về thông tin chi tiết về vòng chơi hiện tại.

**Giá trị trả về:**
- `String` - Thông tin formatted
- `"No Round Loaded"` - Nếu chưa load round

**Format:**
```
"Round [Number]: [Name] ([Remaining]/[Total] bricks)"
```

**Ví dụ output:**
```
"Round 1: Round 1 (60/60 bricks)"
"Round 2: Round 2 (45/70 bricks)"
"Round 3: Round 3 (15/80 bricks)"
"Round 4: Round 4 (3/90 bricks)"
"No Round Loaded"
```

**Ví dụ sử dụng:**
```java
// Logging
System.out.println(roundsManager.getRoundInfo());

// Debug display
public void renderDebugInfo(GraphicsContext gc) {
    String info = roundsManager.getRoundInfo();
    gc.fillText(info, 10, 20);
    
    // Additional info
    gc.fillText("Has next: " + roundsManager.hasNextRound(), 10, 40);
    gc.fillText("Complete: " + roundsManager.isRoundComplete(), 10, 60);
}

// Console monitoring
public void update() {
    // Print every 60 frames (1 second at 60 FPS)
    if (frameCount % 60 == 0) {
        System.out.println(roundsManager.getRoundInfo());
    }
}
```

---

## Sơ đồ luồng hoạt động

### Flow 1: Game Progression (Normal Flow)
```
Start Game
    │
    ↓
new RoundsManager()
    │
    ├──→ initializeRounds()
    │    └──→ Load 4 rounds
    │
    ↓
loadFirstRound()
    │
    ├──→ loadRound(0)
    │    ├──→ Set currentRoundIndex = 0
    │    ├──→ Set currentRound = Round1
    │    └──→ Create bricks
    │
    ↓
[PLAYING]
    │
    ├──→ Player destroys bricks
    │
    ├──→ Check: isRoundComplete()?
    │    ├─ No → Continue playing
    │    └─ Yes → Continue below
    │
    ↓
Check: hasNextRound()?
    │
    ├─ Yes →
    │   │
    │   ↓
    │   nextRound()
    │   └──→ loadRound(currentRoundIndex + 1)
    │       └──→ Back to [PLAYING]
    │
    └─ No →
        │
        ↓
    GAME WON!
    (All rounds complete)
```

### Flow 2: Round Loading Detail
```
loadRound(roundNumber)
    │
    ├──→ Validate index
    │    ├─ Valid → Continue
    │    └─ Invalid → throw IllegalArgumentException
    │
    ↓
Update state:
    ├──→ currentRoundIndex = roundNumber
    └──→ currentRound = rounds.get(roundNumber)
    │
    ↓
Clear old bricks:
    └──→ currentBricks.clear()
    │
    ↓
Create new bricks:
    └──→ currentBricks = currentRound.createBricks()
    │
    ↓
Return currentBricks
```

### Flow 3: Round Completion Check
```
isRoundComplete() called
    │
    ↓
Check: currentBricks.isEmpty()?
    ├─ Yes → return false
    └─ No → Continue
    │
    ↓
For each brick:
    │
    ├──→ Check: brick.isAlive()?
    │    └─ No → Skip (already destroyed)
    │
    ├──→ Check: brick.getBrickType() == GOLD?
    │    └─ Yes → Skip (indestructible)
    │
    └──→ Found alive non-GOLD brick?
         ├─ Yes → return false (not complete)
         └─ No (loop end) → return true (complete)
```

### Flow 4: Game State Machine with Rounds
```
┌─────────────┐
│    MENU     │
└──────┬──────┘
       │ Start Game
       ↓
┌─────────────┐
│   PLAYING   │◄────────────┐
│  (Round N)  │             │
└──────┬──────┘             │
       │                    │
       │ All bricks         │ nextRound()
       │ destroyed          │
       ↓                    │
┌─────────────┐             │
│LEVEL COMPLETE│            │
└──────┬──────┘             │
       │                    │
       ├─ hasNextRound()? ──┘
       │  Yes
       │
       └─ No
       ↓
┌─────────────┐
│     WIN     │
└─────────────┘
```

---

## Ví dụ sử dụng

### Ví dụ 1: Initialization trong GameManager
```java
public class GameManager {
    private RoundsManager roundsManager;
    private List<Brick> bricks;
    
    public void initialize() {
        // Create RoundsManager
        roundsManager = new RoundsManager();
        
        // Load first round
        bricks = roundsManager.loadFirstRound();
        
        System.out.println("Game initialized");
        System.out.println(roundsManager.getRoundInfo());
        System.out.println("Total rounds: " + (roundsManager.hasNextRound() ? "4+" : "4"));
    }
}
```

### Ví dụ 2: Complete game loop với round progression
```java
public class GameManager {
    private RoundsManager roundsManager;
    private StateManager stateManager;
    
    public void update(double deltaTime) {
        if (!stateManager.isPlaying()) {
            return;
        }
        
        // Update game objects
        paddle.update(deltaTime);
        updateBalls(deltaTime);
        updatePowerUps(deltaTime);
        
        // Check collisions (bricks get destroyed)
        collisionManager.checkAllCollisions();
        
        // Check round completion
        if (roundsManager.isRoundComplete()) {
            onRoundComplete();
        }
    }
    
    private void onRoundComplete() {
        System.out.println("Level Complete!");
        audioManager.playSFX(SoundEffect.LEVEL_COMPLETE);
        
        // Pause game
        stateManager.setState(GameState.LEVEL_COMPLETE);
        
        // Show completion screen with delay
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                advanceToNextRound();
            }
        }, 2000); // 2 second delay
    }
    
    private void advanceToNextRound() {
        if (roundsManager.hasNextRound()) {
            // Load next round
            roundsManager.nextRound();
            
            // Reset game objects
            resetPaddle();
            resetBalls();
            clearPowerUps();
            
            // Resume playing
            stateManager.setState(GameState.PLAYING);
            
            System.out.println("Starting " + roundsManager.getCurrentRoundName());
        } else {
            // No more rounds - game won!
            onGameWon();
        }
    }
    
    private void onGameWon() {
        System.out.println("Congratulations! You won the game!");
        audioManager.playMusic(MusicTrack.VICTORY);
        
        stateManager.setState(GameState.WIN);
        
        // Check high score
        if (highScoreManager.isHighScore(score)) {
            highScoreManager.addScore("Player", score);
        }
    }
}
```

### Ví dụ 3: Render với round info
```java
public class GameRenderer {
    private RoundsManager roundsManager;
    
    public void render(GraphicsContext gc) {
        // Clear screen
        gc.clearRect(0, 0, Constants.Window.WINDOW_WIDTH, Constants.Window.WINDOW_HEIGHT);
        
        // Render game objects
        renderBorder(gc);
        renderBricks(gc);
        renderPaddle(gc);
        renderBalls(gc);
        renderPowerUps(gc);
        
        // Render UI
        renderRoundInfo(gc);
        renderProgress(gc);
        renderScore(gc);
    }
    
    private void renderRoundInfo(GraphicsContext gc) {
        // Round title
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.setFill(Color.WHITE);
        
        String roundText = "Round " + roundsManager.getCurrentRoundNumber();
        gc.fillText(roundText, 350, 30);
        
        // Round name
        gc.setFont(Font.font("Arial", 16));
        gc.fillText(roundsManager.getCurrentRoundName(), 350, 55);
    }
    
    private void renderProgress(GraphicsContext gc) {
        int remaining = roundsManager.getRemainingBrickCount();
        int total = roundsManager.getCurrentBricks().size();
        
        // Text
        String progressText = remaining + " / " + total + " bricks";
        gc.setFont(Font.font("Arial", 14));
        gc.fillText(progressText, 10, 50);
        
        // Progress bar
        double percentage = 1.0 - (double) remaining / total;
        
        // Background
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(10, 60, 200, 15);
        
        // Progress fill
        gc.setFill(Color.LIME);
        gc.fillRect(10, 60, 200 * percentage, 15);
        
        // Border
        gc.setStroke(Color.WHITE);
        gc.strokeRect(10, 60, 200, 15);
    }
}
```

### Ví dụ 4: Debug console
```java
public class GameManager {
    private RoundsManager roundsManager;
    
    public void printRoundStatus() {
        System.out.println("=== ROUND STATUS ===");
        System.out.println(roundsManager.getRoundInfo());
        System.out.println("Current number: " + roundsManager.getCurrentRoundNumber());
        System.out.println("Current name: " + roundsManager.getCurrentRoundName());
        System.out.println("Remaining bricks: " + roundsManager.getRemainingBrickCount());
        System.out.println("Total bricks: " + roundsManager.getCurrentBricks().size());
        System.out.println("Round complete: " + roundsManager.isRoundComplete());
        System.out.println("Has next round: " + roundsManager.hasNextRound());
        System.out.println("===================");
    }
    
    // Call in update loop
    public void update(double deltaTime) {
        // ... game logic
        
        // Print status every 5 seconds
        if (frameCount % 300 == 0) {
            printRoundStatus();
        }
    }
}
```

### Ví dụ 5: Save/Load game state
```java
public class SaveManager {
    public GameSaveData saveGame(GameManager gameManager) {
        GameSaveData data = new GameSaveData();
        
        // Save round progress
        data.currentRound = gameManager.getRoundsManager().getCurrentRoundNumber();
        data.score = gameManager.getScore();
        data.lives = gameManager.getLives();
        
        // Save brick states
        List<Brick> bricks = gameManager.getRoundsManager().getCurrentBricks();
        data.brickStates = new ArrayList<>();
        for (Brick brick : bricks) {
            data.brickStates.add(new BrickSaveData(brick));
        }
        
        return data;
    }
    
    public void loadGame(GameManager gameManager, GameSaveData data) {
        // Load round
        RoundsManager rm = gameManager.getRoundsManager();
        rm.loadRound(data.currentRound - 1); // Convert to 0-based
        
        // Restore game state
        gameManager.setScore(data.score);
        gameManager.setLives(data.lives);
        
        // Restore brick states
        List<Brick> bricks = rm.getCurrentBricks();
        for (int i = 0; i < bricks.size(); i++) {
            BrickSaveData brickData = data.brickStates.get(i);
            bricks.get(i).setHealth(brickData.health);
            bricks.get(i).setAlive(brickData.alive);
        }
        
        System.out.println("Game loaded: " + rm.getRoundInfo());
    }
}
```

### Ví dụ 6: Testing RoundsManager
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoundsManagerTest {
    @Test
    void testInitialization() {
        RoundsManager rm = new RoundsManager();
        
        assertEquals(0, rm.getCurrentRoundNumber());
        assertTrue(rm.hasNextRound());
        assertNotNull(rm.getCurrentBricks());
    }
    
    @Test
    void testLoadRound() {
        RoundsManager rm = new RoundsManager();
        
        List<Brick> bricks = rm.loadRound(0);
        assertNotNull(bricks);
        assertTrue(bricks.size() > 0);
        assertEquals(1, rm.getCurrentRoundNumber());
    }
    
    @Test
    void testInvalidRound() {
        RoundsManager rm = new RoundsManager();
        
        assertThrows(IllegalArgumentException.class, () -> {
            rm.loadRound(-1); // Negative index
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            rm.loadRound(100); // Out of bounds
        });
    }
    
    @Test
    void testRoundProgression() {
        RoundsManager rm = new RoundsManager();
        rm.loadFirstRound();
        
        // Round 1
        assertEquals(1, rm.getCurrentRoundNumber());
        assertTrue(rm.hasNextRound());
        
        // Round 2
        rm.nextRound();
        assertEquals(2, rm.getCurrentRoundNumber());
        assertTrue(rm.hasNextRound());
        
        // Round 3
        rm.nextRound();
        assertEquals(3, rm.getCurrentRoundNumber());
        assertTrue(rm.hasNextRound());
        
        // Round 4 (final)
        rm.nextRound();
        assertEquals(4, rm.getCurrentRoundNumber());
        assertFalse(rm.hasNextRound()); // No more rounds
        
        // Try to go beyond
        assertThrows(IllegalStateException.class, () -> {
            rm.nextRound(); // No more rounds!
        });
    }
    
    @Test
    void testRoundComplete() {
        RoundsManager rm = new RoundsManager();
        rm.loadFirstRound();
        
        // Initially not complete
        assertFalse(rm.isRoundComplete());
        
        // Destroy all non-GOLD bricks
        List<Brick> bricks = rm.getCurrentBricks();
        for (Brick brick : bricks) {
            if (brick.getBrickType() != BrickType.GOLD) {
                while (brick.isAlive()) {
                    brick.hit();
                }
            }
        }
        
        // Now should be complete
        assertTrue(rm.isRoundComplete());
    }
    
    @Test
    void testReset() {
        RoundsManager rm = new RoundsManager();
        
        // Advance to Round 3
        rm.loadRound(2);
        assertEquals(3, rm.getCurrentRoundNumber());
        
        // Reset
        rm.reset();
        
        // Should be back to Round 1
        assertEquals(1, rm.getCurrentRoundNumber());
        assertTrue(rm.hasNextRound());
    }
    
    @Test
    void testGetRoundInfo() {
        RoundsManager rm = new RoundsManager();
        rm.loadFirstRound();
        
        String info = rm.getRoundInfo();
        
        assertNotNull(info);
        assertTrue(info.contains("Round 1"));
        assertTrue(info.contains("bricks"));
    }
}
```

---

## Best Practices

### 1. Luôn check hasNextRound() trước nextRound()
```java
// ✅ ĐÚNG: Safe check
if (roundsManager.hasNextRound()) {
    roundsManager.nextRound();
} else {
    handleGameWon();
}

// ❌ SAI: No check
roundsManager.nextRound(); // Có thể throw exception!
```

### 2. Validate round index khi load
```java
// ✅ ĐÚNG: Validate hoặc catch exception
try {
    roundsManager.loadRound(roundIndex);
} catch (IllegalArgumentException e) {
    System.err.println("Invalid round: " + e.getMessage());
    roundsManager.loadFirstRound(); // Fallback
}

// ❌ SAI: Assume valid
roundsManager.loadRound(userInput); // Có thể invalid!
```

### 3. Reset khi cần thiết
```java
// ✅ ĐÚNG: Reset khi start new game
public void startNewGame() {
    roundsManager.reset();
    score = 0;
    lives = 3;
}

// ❌ SAI: Không reset
public void startNewGame() {
    // Vẫn còn state từ game cũ!
}
```

### 4. Sử dụng getCurrentBricks() reference cẩn thận
```java
// ✅ ĐÚNG: Read-only usage
List<Brick> bricks = roundsManager.getCurrentBricks();
for (Brick brick : bricks) {
    brick.render(gc); // OK
}

// ⚠️ CẨN THẬN: Modifying list
List<Brick> bricks = roundsManager.getCurrentBricks();
bricks.remove(0); // Affects internal state!

// ✅ ĐÚNG: Copy nếu cần modify
List<Brick> copy = new ArrayList<>(roundsManager.getCurrentBricks());
copy.remove(0); // Safe
```

### 5. Check null cho currentRound
```java
// ✅ ĐÚNG: Null-safe
public String getRoundName() {
    if (currentRound == null) {
        return "Unknown";
    }
    return currentRound.getRoundName();
}

// ❌ SAI: No null check
public String getRoundName() {
    return currentRound.getRoundName(); // NullPointerException!
}
```

### 6. Log round transitions
```java
// ✅ ĐÚNG: Log for debugging
public void nextRound() {
    int oldRound = roundsManager.getCurrentRoundNumber();
    roundsManager.nextRound();
    int newRound = roundsManager.getCurrentRoundNumber();
    
    System.out.println("Transitioned from Round " + oldRound + " to Round " + newRound);
}
```

---

## Dependencies

### Imports
```java
import Objects.Bricks.BrickType;    // Brick types (GOLD check)
import Rounds.*;                    // All Round classes
import Objects.Bricks.Brick;        // Brick base class
import java.util.ArrayList;
import java.util.List;
```

### Các lớp phụ thuộc

| Lớp | Vai trò | Phương thức sử dụng |
|-----|---------|---------------------|
| `RoundBase` (abstract) | Base class cho rounds | `getRoundName()`, `createBricks()` |
| `Round1` | First round | Constructor, inherited methods |
| `Round2` | Second round | Constructor, inherited methods |
| `Round3` | Third round | Constructor, inherited methods |
| `Round4` | Final round | Constructor, inherited methods |
| `Brick` | Game object | `isAlive()`, `getBrickType()`, `render()` |
| `BrickType` (enum) | Brick classification | `GOLD` comparison |

### Round classes hierarchy:
```
RoundBase (abstract)
    ├─ Round1 extends RoundBase
    ├─ Round2 extends RoundBase
    ├─ Round3 extends RoundBase
    └─ Round4 extends RoundBase
```

### Được sử dụng bởi:
- `GameManager` - Main game logic và round progression
- `CollisionManager` - Access bricks cho collision detection
- `GameRenderer` - Render bricks và UI info
- `SaveManager` - Save/load round progress

### Kiến trúc phụ thuộc
```
┌──────────────────────────────┐
│      RoundsManager           │
└────────┬─────────────────────┘
         │
         ├──→ RoundBase (abstract)
         │    ├─ Round1
         │    ├─ Round2
         │    ├─ Round3
         │    └─ Round4
         │
         ├──→ Brick (game object)
         │
         └──→ BrickType (enum)

Used by:
    ├──→ GameManager (progression)
    ├──→ GameRenderer (display)
    └──→ CollisionManager (bricks)
```

---

## Design Patterns

### 1. Template Method Pattern (via RoundBase)
```
RoundBase defines template:
    ├─ getRoundName() (abstract)
    └─ createBricks() (abstract)

Each Round implements:
    Round1.createBricks() → Easy pattern
    Round2.createBricks() → Medium pattern
    Round3.createBricks() → Hard pattern
    Round4.createBricks() → Boss pattern
```

### 2. Iterator Pattern (progression)
```java
// Sequential iteration through rounds
while (roundsManager.hasNextRound()) {
    // Play current round
    playRound();
    
    // Move to next
    roundsManager.nextRound();
}
```

### 3. State Pattern (current round state)
```
RoundsManager maintains state:
    - currentRoundIndex (which round)
    - currentRound (active round object)
    - currentBricks (active bricks)
```

---

## Mở rộng trong tương lai

### 1. Dynamic round loading
```java
public class RoundsManager {
    private Map<String, RoundBase> availableRounds;
    
    public void loadRoundByName(String roundName) {
        if (availableRounds.containsKey(roundName)) {
            currentRound = availableRounds.get(roundName);
            currentBricks = currentRound.createBricks();
        }
    }
    
    public void addCustomRound(String name, RoundBase round) {
        availableRounds.put(name, round);
    }
}
```

### 2. Round difficulty scaling
```java
public class RoundsManager {
    private DifficultyLevel difficulty;
    
    public void setDifficulty(DifficultyLevel level) {
        this.difficulty = level;
    }
    
    public List<Brick> loadRound(int roundNumber) {
        // ... existing code
        
        // Scale difficulty
        currentBricks = scaleDifficulty(currentBricks, difficulty);
        return currentBricks;
    }
    
    private List<Brick> scaleDifficulty(List<Brick> bricks, DifficultyLevel level) {
        for (Brick brick : bricks) {
            brick.setHealth(brick.getHealth() * level.getHealthMultiplier());
        }
        return bricks;
    }
}
```

### 3. Round unlocking system
```java
public class RoundsManager {
    private Set<Integer> unlockedRounds;
    
    public boolean isRoundUnlocked(int roundNumber) {
        return unlockedRounds.contains(roundNumber);
    }
    
    public void unlockRound(int roundNumber) {
        unlockedRounds.add(roundNumber);
        System.out.println("Unlocked Round " + (roundNumber + 1));
    }
    
    public List<Brick> loadRound(int roundNumber) {
        if (!isRoundUnlocked(roundNumber)) {
            throw new IllegalStateException("Round " + (roundNumber + 1) + " is locked!");
        }
        // ... existing code
    }
}
```

### 4. Round statistics tracking
```java
public class RoundStats {
    private int roundNumber;
    private long completionTime;
    private int bricksDestroyed;
    private int powerUpsCollected;
    private boolean perfectClear;
}

public class RoundsManager {
    private Map<Integer, RoundStats> roundStatistics;
    private long roundStartTime;
    
    public void startRoundTimer() {
        roundStartTime = System.currentTimeMillis();
    }
    
    public RoundStats finishRound() {
        long completionTime = System.currentTimeMillis() - roundStartTime;
        
        RoundStats stats = new RoundStats();
        stats.setRoundNumber(getCurrentRoundNumber());
        stats.setCompletionTime(completionTime);
        stats.setBricksDestroyed(getTotalBricks() - getRemainingBrickCount());
        stats.setPerfectClear(getRemainingBrickCount() == 0);
        
        roundStatistics.put(getCurrentRoundNumber(), stats);
        
        return stats;
    }
}
```

### 5. Procedural round generation
```java
public class RoundsManager {
    private ProceduralRoundGenerator generator;
    
    public void generateRandomRound(int difficulty) {
        RoundBase proceduralRound = generator.generate(difficulty);
        rounds.add(proceduralRound);
        
        System.out.println("Generated random round with difficulty " + difficulty);
    }
    
    public void enableInfiniteMode() {
        // Generate rounds on-demand
        while (true) {
            if (isRoundComplete() && !hasNextRound()) {
                int difficulty = getCurrentRoundNumber(); // Increase with progress
                generateRandomRound(difficulty);
            }
        }
    }
}
```

### 6. Bonus/Secret rounds
```java
public class RoundsManager {
    private List<RoundBase> bonusRounds;
    private boolean canAccessBonus;
    
    public void checkBonusRoundAccess() {
        // Unlock bonus if perfect clear
        if (isRoundComplete() && getRemainingBrickCount() == 0) {
            canAccessBonus = true;
        }
    }
    
    public void loadBonusRound() {
        if (!canAccessBonus) {
            throw new IllegalStateException("Bonus round not unlocked!");
        }
        
        RoundBase bonusRound = bonusRounds.get(0);
        currentRound = bonusRound;
        currentBricks = bonusRound.createBricks();
        
        System.out.println("Entering bonus round!");
    }
}
```

### 7. Round preview system
```java
public class RoundsManager {
    public RoundPreview getPreview(int roundNumber) {
        if (roundNumber < 0 || roundNumber >= rounds.size()) {
            return null;
        }
        
        RoundBase round = rounds.get(roundNumber);
        
        RoundPreview preview = new RoundPreview();
        preview.setName(round.getRoundName());
        preview.setDifficulty(round.getDifficulty());
        preview.setEstimatedTime(round.getEstimatedTime());
        preview.setThumbnail(round.generateThumbnail());
        
        return preview;
    }
    
    public void showRoundSelect() {
        for (int i = 0; i < rounds.size(); i++) {
            RoundPreview preview = getPreview(i);
            displayPreview(preview);
        }
    }
}
```

---

## Tổng kết

`RoundsManager` là lớp quan trọng cho game progression:
- ✅ **Quản lý rounds:** Load, switch, track progress
- ✅ **Completion checking:** Xác định khi nào round complete
- ✅ **Progression control:** Sequential advancement qua levels
- ✅ **State management:** Current round, bricks, progress
- ✅ **Extensible:** Dễ add thêm rounds mới
- ✅ **Robust:** Validation, error handling, null safety

Kết hợp với RoundBase và các Round classes, tạo nên một level progression system hoàn chỉnh!

---

**Tác giả:** Arkanoid Development Team  
**Phiên bản:** 1.0  
**Cập nhật lần cuối:** 9 tháng 11, 2025
