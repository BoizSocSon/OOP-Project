# PowerUpManager

## Tổng quan
`PowerUpManager` là lớp Singleton quản lý toàn bộ hệ thống vật phẩm bổ trợ (PowerUps) trong game Arkanoid. Lớp này chịu trách nhiệm:
- Sinh ra PowerUps ngẫu nhiên khi gạch bị phá hủy
- Cập nhật vị trí các PowerUps đang rơi
- Phát hiện va chạm với paddle và áp dụng hiệu ứng
- Quản lý thời gian kéo dài của các hiệu ứng
- Tự động hủy bỏ hiệu ứng khi hết hạn

PowerUpManager sử dụng **Singleton Pattern** để đảm bảo chỉ có một instance duy nhất quản lý tất cả PowerUps trong game, và tích hợp chặt chẽ với `GameManager` để apply/remove effects.

## Package
```
Engine.PowerUpManager
```

## Design Pattern
**Singleton Pattern** + **Factory Pattern**

```
┌──────────────────────────────────┐
│   PowerUpManager (Singleton)     │
│  - instance: static               │
│  - activePowerUps: List           │
│  - activeEffects: Map             │
│  + getInstance(): static          │
│  + reset(): static                │
└──────────────────────────────────┘
           │
           │ creates
           ↓
┌──────────────────────────────────┐
│    PowerUp (Abstract)            │
├──────────────────────────────────┤
│  + CatchPowerUp                  │
│  + DuplicatePowerUp              │
│  + ExpandPaddlePowerUp           │
│  + LaserPowerUp                  │
│  + LifePowerUp                   │
│  + SlowBallPowerUp               │
│  + WarpPowerUp                   │
└──────────────────────────────────┘
```

---

## Thuộc tính

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Mô tả |
|-----------|-------------|-----------------|-------|
| `instance` | `PowerUpManager` | `private static` | Singleton instance duy nhất |
| `activePowerUps` | `List<PowerUp>` | `private final` | Danh sách các PowerUps đang rơi trên màn hình |
| `activeEffects` | `Map<PowerUpType, Long>` | `private final` | Map lưu các hiệu ứng đang hoạt động và thời gian hết hạn (timestamp) |
| `gameManager` | `GameManager` | `private` | Reference đến GameManager để apply/remove effects |

### Chi tiết thuộc tính

#### instance
Singleton instance của PowerUpManager. Được khởi tạo lần đầu khi gọi `getInstance()`.

```java
private static PowerUpManager instance;
```

**Đặc điểm:**
- Lazy initialization (tạo khi cần)
- Thread-unsafe (phù hợp cho single-threaded game)
- Có thể reset bằng `reset()` method

#### activePowerUps
Danh sách các PowerUps đang rơi xuống trên màn hình.

```java
private final List<PowerUp> activePowerUps = new ArrayList<>();
```

**Đặc điểm:**
- PowerUp được add khi spawn từ brick
- PowerUp được remove khi:
  - Thu thập bởi paddle
  - Rơi ra khỏi màn hình (y > window height)
- Update position mỗi frame

**Ví dụ state:**
```java
activePowerUps: [
    ExpandPaddlePowerUp(x=250, y=320),
    LaserPowerUp(x=400, y=180),
    LifePowerUp(x=550, y=450)
]
```

#### activeEffects
Map lưu trữ các hiệu ứng đang hoạt động và thời gian hết hạn.

```java
private final Map<PowerUpType, Long> activeEffects = new HashMap<>();
```

**Key:** `PowerUpType` - Loại PowerUp  
**Value:** `Long` - Timestamp khi hiệu ứng hết hạn (milliseconds)

**Đặc điểm:**
- Chỉ lưu effects có duration > 0 (không lưu instant effects như LIFE, WARP)
- Automatically remove khi hết hạn
- Multiple effects có thể active đồng thời

**Ví dụ state:**
```java
activeEffects: {
    EXPAND: 1699522345000L,  // Expires at this timestamp
    LASER: 1699522350000L,
    SLOW: 1699522355000L
}
```

**Timeline diagram:**
```
Current time: 1699522340000

EXPAND  |████████████████|---------> (5s left)
LASER   |████████████████████|-----> (10s left)
SLOW    |████████████████████████|-> (15s left)
        0s              10s       20s
```

#### gameManager
Reference đến GameManager để có thể apply và remove effects.

```java
private GameManager gameManager;
```

**Được set bởi:** `setGameManager(GameManager gm)`  
**Sử dụng trong:** `applyPowerUpEffect()`, `removePowerUpEffect()`

---

## Constructor

### PowerUpManager() - private
Constructor private để enforce Singleton pattern.

```java
private PowerUpManager() {
    this.activePowerUps = new ArrayList<>();
    this.activeEffects = new HashMap<>();
}
```

**Lưu ý:** Không thể tạo instance trực tiếp, phải dùng `getInstance()`

---

## Phương thức tĩnh (Static Methods)

### 1. getInstance()
```java
public static PowerUpManager getInstance()
```

Lấy instance duy nhất của PowerUpManager (Singleton pattern).

**Giá trị trả về:**
- `PowerUpManager` - Instance duy nhất

**Thuật toán:**
```java
if (instance == null) {
    instance = new PowerUpManager(); // Lazy initialization
}
return instance;
```

**Ví dụ:**
```java
// Lấy instance
PowerUpManager pum = PowerUpManager.getInstance();

// Tất cả calls đều trả về cùng một instance
PowerUpManager pum2 = PowerUpManager.getInstance();
assert pum == pum2; // true - same instance
```

---

### 2. reset()
```java
public static void reset()
```

Đặt lại trạng thái của PowerUpManager (reset to initial state).

**Chức năng:**
1. Clear tất cả PowerUps đang rơi
2. Clear tất cả effects đang hoạt động
3. Set instance = null (để có thể tạo mới)

**Ví dụ:**
```java
// Khi bắt đầu game mới
public void startNewGame() {
    PowerUpManager.reset(); // Reset PowerUp system
    
    // Get new instance (sẽ tạo mới vì đã reset)
    PowerUpManager pum = PowerUpManager.getInstance();
    pum.setGameManager(this);
}

// Khi về menu
public void returnToMenu() {
    PowerUpManager.reset(); // Cleanup
}
```

**Khi nào sử dụng:**
- Bắt đầu game mới
- Return to menu
- Load saved game
- Game over → retry

---

## Phương thức công khai

### 1. setGameManager()
```java
public void setGameManager(GameManager gameManager)
```

Thiết lập reference đến GameManager.

**Tham số:**
- `gameManager` - Reference đến GameManager instance

**Ví dụ:**
```java
public class GameManager {
    private PowerUpManager powerUpManager;
    
    public void initialize() {
        powerUpManager = PowerUpManager.getInstance();
        powerUpManager.setGameManager(this); // Pass reference to self
    }
}
```

**Quan trọng:** Phải gọi method này trước khi PowerUps có thể apply effects!

---

### 2. spawnFromBrick()
```java
public void spawnFromBrick(double x, double y, BrickType brickType)
```

Sinh ra một PowerUp tại vị trí gạch bị phá hủy (có xác suất).

**Tham số:**
- `x` - Tọa độ X của gạch
- `y` - Tọa độ Y của gạch
- `brickType` - Loại gạch bị phá (hiện chưa ảnh hưởng spawn rate)

**Thuật toán:**

1. **Kiểm tra spawn chance:**
   ```java
   if (Math.random() > Constants.GameRules.POWERUP_SPAWN_CHANCE) {
       return; // No spawn (70% không spawn nếu chance = 0.3)
   }
   ```

2. **Random weighted type:**
   ```java
   PowerUpType type = PowerUpType.randomWeighted();
   if (type == null) return;
   ```

3. **Create và add PowerUp:**
   ```java
   PowerUp powerUp = createPowerUp(x, y, type);
   activePowerUps.add(powerUp);
   ```

**Ví dụ:**
```java
public void onBrickDestroyed(Brick brick) {
    // Cộng điểm
    scoreManager.addScore(brick.getScore());
    
    // Spawn PowerUp (có xác suất)
    powerUpManager.spawnFromBrick(
        brick.getX(), 
        brick.getY(), 
        brick.getBrickType()
    );
}
```

**Spawn chance visualization:**
```
Random: 0.0 ──────────────────── 1.0
        ├─────┤                     ← 30% spawn
        │     └─ SPAWN_CHANCE
        └─ Spawn PowerUp

Random: 0.0 ──────────────────── 1.0
                    ├──────────────┤ ← 70% no spawn
                    └─ SPAWN_CHANCE
                    └─ No PowerUp
```

**PowerUp spawn weights:**
```
PowerUpType weights (example):
├─ EXPAND:    25% ████████
├─ LASER:     20% ██████
├─ SLOW:      15% █████
├─ CATCH:     15% █████
├─ DUPLICATE: 10% ███
├─ LIFE:       8% ██
└─ WARP:       7% ██
```

---

### 3. update()
```java
public void update(Paddle paddle)
```

Cập nhật vị trí các PowerUps đang rơi và kiểm tra va chạm với paddle.

**Tham số:**
- `paddle` - Paddle của người chơi

**Thuật toán:**

1. **Null check:**
   ```java
   if (paddle == null) return;
   ```

2. **Copy list để tránh ConcurrentModificationException:**
   ```java
   List<PowerUp> powerUpsCopy = new ArrayList<>(activePowerUps);
   ```

3. **Update mỗi PowerUp:**
   ```java
   for (PowerUp powerUp : powerUpsCopy) {
       if (!activePowerUps.contains(powerUp)) continue; // Double check
       
       powerUp.update(); // Move down
       
       // Check collision with paddle
       if (powerUp.checkPaddleCollision(paddle)) {
           powerUp.collect();
           applyPowerUpEffect(powerUp);
           scheduleEffectExpiry(powerUp.getType());
           activePowerUps.remove(powerUp);
       }
       // Check if off-screen
       else if (powerUp.getY() > Constants.Window.WINDOW_HEIGHT) {
           activePowerUps.remove(powerUp);
       }
   }
   ```

4. **Update active effects:**
   ```java
   updateActiveEffects(); // Check and remove expired effects
   ```

**Ví dụ trong game loop:**
```java
public void update(double deltaTime) {
    // Update game objects
    paddle.update(deltaTime);
    
    for (Ball ball : balls) {
        ball.move(deltaTime);
    }
    
    // Update PowerUps (check collision, remove off-screen)
    powerUpManager.update(paddle);
    
    // Check collisions...
}
```

**Flow diagram:**
```
update(paddle) called
         │
         ↓
    ┌─────────┐
    │Copy list│
    └────┬────┘
         │
    For each PowerUp:
         │
         ├──→ powerUp.update() (move down)
         │
         ├──→ Check paddle collision?
         │    ├─ Yes → collect()
         │    │        applyEffect()
         │    │        scheduleExpiry()
         │    │        remove from list
         │    │
         │    └─ No → Check off-screen?
         │           ├─ Yes → remove
         │           └─ No → keep
         │
         ↓
    updateActiveEffects()
    (check expired effects)
```

---

### 4. getActivePowerUps()
```java
public List<PowerUp> getActivePowerUps()
```

Lấy danh sách các PowerUps đang rơi trên màn hình.

**Giá trị trả về:**
- `List<PowerUp>` - Bản sao của danh sách (defensive copy)

**Ví dụ:**
```java
// Render all active PowerUps
public void render(GraphicsContext gc) {
    List<PowerUp> powerUps = powerUpManager.getActivePowerUps();
    
    for (PowerUp powerUp : powerUps) {
        powerUp.render(gc);
    }
}
```

**Lưu ý:** Return bản sao để tránh external modification của internal list.

---

### 5. clearAllPowerUps()
```java
public void clearAllPowerUps()
```

Xóa tất cả PowerUps đang rơi và effects đang hoạt động.

**Chức năng:**
- Clear `activePowerUps` list
- Clear `activeEffects` map
- Log message

**Ví dụ:**
```java
// Khi chuyển màn
public void loadNextRound() {
    powerUpManager.clearAllPowerUps(); // Clear old PowerUps
    
    // Load new round...
    roundsManager.loadRound(currentRound + 1);
}

// Khi pause/unpause (optional)
public void onPause() {
    // Clear PowerUps to avoid confusion
    powerUpManager.clearAllPowerUps();
}
```

---

## Phương thức riêng tư

### 1. createPowerUp() - Factory Method
```java
private PowerUp createPowerUp(double x, double y, PowerUpType type)
```

Factory method để tạo đối tượng PowerUp cụ thể dựa trên type.

**Tham số:**
- `x`, `y` - Vị trí spawn
- `type` - Loại PowerUp cần tạo

**Giá trị trả về:**
- `PowerUp` - Instance của subclass tương ứng

**Implementation:**
```java
switch (type) {
    case CATCH:     return new CatchPowerUp(x, y);
    case DUPLICATE: return new DuplicatePowerUp(x, y);
    case EXPAND:    return new ExpandPaddlePowerUp(x, y);
    case LASER:     return new LaserPowerUp(x, y);
    case LIFE:      return new LifePowerUp(x, y);
    case SLOW:      return new SlowBallPowerUp(x, y);
    case WARP:      return new WarpPowerUp(x, y);
    default:
        System.err.println("Unknown PowerUpType: " + type);
        return new ExpandPaddlePowerUp(x, y); // Fallback
}
```

**Design Pattern:** Factory Pattern - tách việc tạo object khỏi logic sử dụng

---

### 2. applyPowerUpEffect()
```java
private void applyPowerUpEffect(PowerUp powerUp)
```

Áp dụng hiệu ứng của PowerUp thông qua GameManager.

**Tham số:**
- `powerUp` - PowerUp vừa được thu thập

**Thuật toán:**
```java
if (gameManager == null) {
    System.err.println("PowerUpManager: GameManager is null");
    return;
}

powerUp.applyEffect(gameManager); // Delegate to PowerUp
```

**PowerUp effects:**

| PowerUp | Effect |
|---------|--------|
| CATCH | Enable catch mode on paddle |
| DUPLICATE | Spawn 2 more balls |
| EXPAND | Increase paddle width |
| LASER | Enable laser shooting |
| LIFE | Add 1 life |
| SLOW | Decrease ball speed |
| WARP | Skip to next round |

**Ví dụ flow:**
```
Player collects EXPAND PowerUp
         │
         ↓
applyPowerUpEffect(expandPowerUp)
         │
         ↓
expandPowerUp.applyEffect(gameManager)
         │
         ↓
gameManager.getPaddle().expandWidth()
         │
         ↓
Paddle width increases
```

---

### 3. removePowerUpEffect()
```java
private void removePowerUpEffect(PowerUpType type)
```

Hủy bỏ hiệu ứng của PowerUp đã hết hạn.

**Tham số:**
- `type` - Loại PowerUp cần remove effect

**Thuật toán:**
```java
if (gameManager == null) {
    System.err.println("Cannot remove effect");
    return;
}

// Create temp PowerUp just to call removeEffect
PowerUp tempPowerUp = createPowerUp(0, 0, type);
tempPowerUp.removeEffect(gameManager);
```

**Ví dụ:**
```
EXPAND effect expires (after 10 seconds)
         │
         ↓
removePowerUpEffect(EXPAND)
         │
         ↓
Create temp ExpandPaddlePowerUp
         │
         ↓
tempPowerUp.removeEffect(gameManager)
         │
         ↓
gameManager.getPaddle().resetWidth()
         │
         ↓
Paddle returns to normal width
```

**Effects that need removal:**
- EXPAND → reset paddle width
- LASER → disable laser mode
- CATCH → disable catch mode
- SLOW → restore ball speed

**Effects that don't need removal (instant):**
- LIFE → already added
- DUPLICATE → balls already spawned
- WARP → already changed round

---

### 4. scheduleEffectExpiry()
```java
private void scheduleEffectExpiry(PowerUpType type)
```

Lên lịch thời gian hết hạn cho hiệu ứng kéo dài.

**Tham số:**
- `type` - Loại PowerUp

**Thuật toán:**
```java
long duration = type.getDuration();

// Only schedule if duration > 0
if (duration > 0) {
    long expiryTime = System.currentTimeMillis() + duration;
    activeEffects.put(type, expiryTime);
    
    System.out.println("Scheduled expiry for " + type + " at " + expiryTime);
}
```

**Duration examples:**
```java
PowerUpType.EXPAND.getDuration()    // 10000 ms (10 seconds)
PowerUpType.LASER.getDuration()     // 10000 ms
PowerUpType.SLOW.getDuration()      // 8000 ms
PowerUpType.LIFE.getDuration()      // 0 ms (instant, no expiry)
```

**Timeline example:**
```
Current time: 1000ms

Collect EXPAND (duration=10000ms)
    │
    ↓
scheduleEffectExpiry(EXPAND)
    │
    ↓
expiryTime = 1000 + 10000 = 11000ms
    │
    ↓
activeEffects.put(EXPAND, 11000)

Timeline:
  1000ms       11000ms
    ├────────────┤
    │  EXPAND    │ → Remove at 11000ms
    └────────────┘
```

---

### 5. updateActiveEffects()
```java
private void updateActiveEffects()
```

Cập nhật các hiệu ứng đang hoạt động, loại bỏ các hiệu ứng đã hết hạn.

**Thuật toán:**
```java
if (activeEffects.isEmpty()) return;

long currentTime = System.currentTimeMillis();
Iterator<Map.Entry<PowerUpType, Long>> iterator = activeEffects.entrySet().iterator();

while (iterator.hasNext()) {
    Map.Entry<PowerUpType, Long> entry = iterator.next();
    PowerUpType type = entry.getKey();
    long expiryTime = entry.getValue();
    
    if (currentTime >= expiryTime) {
        removePowerUpEffect(type);  // Remove effect
        iterator.remove();           // Remove from map
        
        System.out.println("Effect expired for " + type);
    }
}
```

**Ví dụ execution:**
```
activeEffects: {
    EXPAND: 11000,
    LASER: 12000,
    SLOW: 13000
}

currentTime: 11500

Check EXPAND: 11500 >= 11000? YES
    → removePowerUpEffect(EXPAND)
    → iterator.remove()
    
Check LASER: 11500 >= 12000? NO
    → Keep

Check SLOW: 11500 >= 13000? NO
    → Keep

Result:
activeEffects: {
    LASER: 12000,
    SLOW: 13000
}
```

**Được gọi trong:** `update()` method (mỗi frame)

---

## Sơ đồ luồng hoạt động

### Flow 1: PowerUp Lifecycle (Full Flow)
```
Brick destroyed
      │
      ↓
spawnFromBrick(x, y, type)
      │
      ├─ Random check?
      │  ├─ No (70%) → Return (no spawn)
      │  └─ Yes (30%) → Continue
      │
      ↓
PowerUpType.randomWeighted()
      │
      ↓
createPowerUp(x, y, type)
      │
      ↓
activePowerUps.add(powerUp)
      │
      │ [PowerUp rơi xuống mỗi frame]
      ↓
update(paddle) - every frame
      │
      ├──→ powerUp.update()
      │    (y += speed)
      │
      ├──→ Check collision?
      │    │
      │    ├─ Hit paddle?
      │    │  ├─ Yes →
      │    │  │   ├─ applyEffect()
      │    │  │   ├─ scheduleExpiry()
      │    │  │   └─ remove from list
      │    │  │
      │    │  └─ No → Continue falling
      │    │
      │    └─ Off screen?
      │       ├─ Yes → remove
      │       └─ No → Continue
      │
      ↓
[Effect active for duration]
      │
      ↓
updateActiveEffects()
      │
      ├─ Check expiry time?
      │  ├─ Expired → removeEffect()
      │  └─ Active → Keep
      │
      ↓
Effect removed
```

### Flow 2: Multiple PowerUps Active
```
Time: 0s
    │
    ↓ Collect EXPAND
activeEffects: { EXPAND: 10s }
    │
    ↓ (2s later)
Time: 2s
    │
    ↓ Collect LASER
activeEffects: { EXPAND: 10s, LASER: 12s }
    │
    ↓ (3s later)
Time: 5s
    │
    ↓ Collect SLOW
activeEffects: { EXPAND: 10s, LASER: 12s, SLOW: 13s }
    │
    ↓ (5s later)
Time: 10s
    │
    ↓ EXPAND expires
activeEffects: { LASER: 12s, SLOW: 13s }
    │
    ↓ (2s later)
Time: 12s
    │
    ↓ LASER expires
activeEffects: { SLOW: 13s }
    │
    ↓ (1s later)
Time: 13s
    │
    ↓ SLOW expires
activeEffects: { }
```

---

## Ví dụ sử dụng

### Ví dụ 1: Initialization trong GameManager
```java
public class GameManager {
    private PowerUpManager powerUpManager;
    
    public void initialize() {
        // Get singleton instance
        powerUpManager = PowerUpManager.getInstance();
        
        // Set reference to self
        powerUpManager.setGameManager(this);
        
        System.out.println("PowerUpManager initialized");
    }
    
    public void startNewGame() {
        // Reset PowerUp system
        PowerUpManager.reset();
        
        // Re-initialize
        initialize();
    }
}
```

### Ví dụ 2: Game loop với PowerUp updates
```java
public class GameManager {
    public void update(double deltaTime) {
        if (!stateManager.isPlaying()) {
            return; // Don't update if not playing
        }
        
        // Update game objects
        paddle.update(deltaTime);
        
        for (Ball ball : balls) {
            ball.move(deltaTime);
        }
        
        // Update PowerUps (va chạm, rơi, expire effects)
        powerUpManager.update(paddle);
        
        // Check collisions
        collisionManager.checkBallBrickCollisions(ball, bricks);
        
        // ... rest of game logic
    }
}
```

### Ví dụ 3: Spawn PowerUp khi phá gạch
```java
public class GameManager {
    private void checkBallBrickCollisions() {
        List<Brick> destroyed = collisionManager.checkBallBrickCollisions(ball, bricks);
        
        for (Brick brick : destroyed) {
            // Add score
            scoreManager.addScore(brick.getScore());
            
            // Spawn PowerUp (30% chance)
            powerUpManager.spawnFromBrick(
                brick.getX() + brick.getWidth() / 2,  // Center X
                brick.getY(),
                brick.getBrickType()
            );
            
            // Play sound effect
            audioManager.playSFX(SoundEffect.BRICK_BREAK);
        }
    }
}
```

### Ví dụ 4: Render PowerUps
```java
public class GameRenderer {
    private PowerUpManager powerUpManager;
    
    public void render(GraphicsContext gc) {
        // Render game objects first
        renderBorder(gc);
        renderBricks(gc);
        renderPaddle(gc);
        renderBalls(gc);
        
        // Render PowerUps on top
        List<PowerUp> powerUps = powerUpManager.getActivePowerUps();
        
        for (PowerUp powerUp : powerUps) {
            powerUp.render(gc);
        }
        
        // Render UI
        renderUI(gc);
    }
}
```

### Ví dụ 5: Clear PowerUps khi chuyển màn
```java
public class GameManager {
    public void loadNextRound() {
        // Clear all PowerUps and effects from previous round
        powerUpManager.clearAllPowerUps();
        
        // Remove all balls except one
        balls.clear();
        balls.add(new Ball(400, 300, 16, 16));
        
        // Reset paddle
        paddle.resetToCenter();
        paddle.resetWidth();
        
        // Load new round
        currentRound++;
        bricks = roundsManager.loadRound(currentRound);
        
        System.out.println("Loaded round " + currentRound);
    }
}
```

### Ví dụ 6: Testing PowerUpManager
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PowerUpManagerTest {
    @Test
    void testSingleton() {
        PowerUpManager pum1 = PowerUpManager.getInstance();
        PowerUpManager pum2 = PowerUpManager.getInstance();
        
        assertSame(pum1, pum2); // Same instance
    }
    
    @Test
    void testReset() {
        PowerUpManager pum = PowerUpManager.getInstance();
        pum.spawnFromBrick(100, 100, BrickType.RED);
        
        int beforeReset = pum.getActivePowerUps().size();
        assertTrue(beforeReset > 0);
        
        PowerUpManager.reset();
        
        PowerUpManager pum2 = PowerUpManager.getInstance();
        assertEquals(0, pum2.getActivePowerUps().size());
    }
    
    @Test
    void testSpawnChance() {
        PowerUpManager.reset();
        PowerUpManager pum = PowerUpManager.getInstance();
        
        int spawned = 0;
        int total = 1000;
        
        for (int i = 0; i < total; i++) {
            int before = pum.getActivePowerUps().size();
            pum.spawnFromBrick(100, 100, BrickType.RED);
            int after = pum.getActivePowerUps().size();
            
            if (after > before) {
                spawned++;
            }
        }
        
        double spawnRate = (double) spawned / total;
        
        // Should be approximately 30% (0.3 ± 0.05)
        assertTrue(spawnRate >= 0.25 && spawnRate <= 0.35);
    }
    
    @Test
    void testEffectExpiry() throws InterruptedException {
        PowerUpManager.reset();
        PowerUpManager pum = PowerUpManager.getInstance();
        GameManager gm = new GameManager();
        pum.setGameManager(gm);
        
        // Spawn and collect EXPAND PowerUp
        pum.spawnFromBrick(400, 500, BrickType.RED);
        
        List<PowerUp> powerUps = pum.getActivePowerUps();
        if (!powerUps.isEmpty()) {
            PowerUp powerUp = powerUps.get(0);
            
            // Simulate collection
            powerUp.collect();
            pum.applyPowerUpEffect(powerUp);
            pum.scheduleEffectExpiry(powerUp.getType());
            
            // Check effect is active
            assertTrue(pum.activeEffects.containsKey(powerUp.getType()));
            
            // Wait for expiry
            Thread.sleep(powerUp.getType().getDuration() + 100);
            
            // Update to trigger expiry check
            pum.updateActiveEffects();
            
            // Effect should be removed
            assertFalse(pum.activeEffects.containsKey(powerUp.getType()));
        }
    }
}
```

---

## Best Practices

### 1. Singleton initialization
```java
// ✅ ĐÚNG: Use getInstance()
PowerUpManager pum = PowerUpManager.getInstance();

// ❌ SAI: Try to instantiate directly
PowerUpManager pum = new PowerUpManager(); // Compile error - private constructor
```

### 2. Set GameManager reference
```java
// ✅ ĐÚNG: Set reference before using
PowerUpManager pum = PowerUpManager.getInstance();
pum.setGameManager(gameManager);

// ❌ SAI: Không set reference
PowerUpManager pum = PowerUpManager.getInstance();
pum.spawnFromBrick(...); // Effects won't work!
```

### 3. Reset khi cần
```java
// ✅ ĐÚNG: Reset khi start new game
public void startNewGame() {
    PowerUpManager.reset();
    // Re-initialize...
}

// ❌ SAI: Không reset → còn PowerUps/effects từ game cũ
public void startNewGame() {
    // PowerUps from previous game still exist!
}
```

### 4. Defensive copying
```java
// ✅ ĐÚNG: Method returns copy
public List<PowerUp> getActivePowerUps() {
    return new ArrayList<>(activePowerUps);
}

// ❌ SAI: Return reference
public List<PowerUp> getActivePowerUps() {
    return activePowerUps; // External code can modify!
}
```

### 5. Null checks
```java
// ✅ ĐÚNG: Check null before using
public void update(Paddle paddle) {
    if (paddle == null) return;
    // ... rest of logic
}

// ❌ SAI: No null check
public void update(Paddle paddle) {
    for (PowerUp p : activePowerUps) {
        p.checkPaddleCollision(paddle); // NullPointerException!
    }
}
```

### 6. ConcurrentModificationException prevention
```java
// ✅ ĐÚNG: Copy list before iterating và removing
List<PowerUp> copy = new ArrayList<>(activePowerUps);
for (PowerUp p : copy) {
    if (condition) {
        activePowerUps.remove(p); // Safe
    }
}

// ❌ SAI: Modify while iterating
for (PowerUp p : activePowerUps) {
    if (condition) {
        activePowerUps.remove(p); // ConcurrentModificationException!
    }
}
```

---

## Dependencies

### Imports
```java
import Objects.PowerUps.*;              // All PowerUp classes
import Objects.Bricks.BrickType;        // Brick type for spawn logic
import Objects.GameEntities.Paddle;     // Paddle for collision
import Utils.Constants;                 // Game constants
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;              // Safe removal from map
import java.util.List;
import java.util.Map;
```

### Các lớp phụ thuộc

| Lớp | Vai trò | Phương thức sử dụng |
|-----|---------|---------------------|
| `PowerUp` (abstract) | Base class cho PowerUps | `update()`, `render()`, `checkPaddleCollision()`, `applyEffect()`, `removeEffect()` |
| `PowerUpType` (enum) | Định nghĩa các loại PowerUp | `randomWeighted()`, `getDuration()` |
| `GameManager` | Apply/remove effects | Các methods của GameManager (getBalls, getPaddle, etc.) |
| `Paddle` | Collision detection | `getBounds()`, collision checking |
| `BrickType` | Loại gạch (hiện chưa dùng) | - |
| `Constants` | Game rules | `POWERUP_SPAWN_CHANCE`, `WINDOW_HEIGHT` |

### PowerUp subclasses:
- `CatchPowerUp` - Bắt bóng vào paddle
- `DuplicatePowerUp` - Nhân đôi số bóng
- `ExpandPaddlePowerUp` - Tăng kích thước paddle
- `LaserPowerUp` - Cho phép bắn laser
- `LifePowerUp` - Thêm 1 mạng
- `SlowBallPowerUp` - Giảm tốc độ bóng
- `WarpPowerUp` - Skip round

### Được sử dụng bởi:
- `GameManager` - Main game logic
- `GameRenderer` - Render PowerUps
- `CollisionManager` - (Có thể) kiểm tra va chạm

### Kiến trúc phụ thuộc
```
┌──────────────────────────────┐
│    PowerUpManager            │
│  (Singleton + Factory)       │
└────────┬─────────────────────┘
         │
         ├──→ GameManager (apply/remove effects)
         │
         ├──→ PowerUp (abstract)
         │    └──→ 7 concrete PowerUps
         │
         ├──→ PowerUpType (enum)
         │    ├─ randomWeighted()
         │    └─ getDuration()
         │
         ├──→ Paddle (collision)
         │
         └──→ Constants (rules)

Used by:
    ├──→ GameManager (update, spawn)
    └──→ GameRenderer (render)
```

---

## Design Patterns

### 1. Singleton Pattern
```
┌────────────────────────────┐
│   PowerUpManager           │
│  - instance: static        │ ◄─── Only one instance
│  - PowerUpManager()        │      in entire game
│  + getInstance(): static   │
└────────────────────────────┘
```

**Ưu điểm:**
- ✅ Global access point
- ✅ Controlled initialization
- ✅ Shared state (activePowerUps, activeEffects)

**Nhược điểm:**
- ❌ Hard to test (global state)
- ❌ Hidden dependencies

### 2. Factory Pattern
```java
private PowerUp createPowerUp(double x, double y, PowerUpType type) {
    switch (type) {
        case CATCH:     return new CatchPowerUp(x, y);
        case DUPLICATE: return new DuplicatePowerUp(x, y);
        // ...
    }
}
```

**Ưu điểm:**
- ✅ Encapsulate object creation
- ✅ Easy to add new PowerUp types
- ✅ Centralized creation logic

### 3. Strategy Pattern (PowerUp effects)
Mỗi PowerUp có cách apply/remove effect riêng:
```java
powerUp.applyEffect(gameManager);  // Polymorphism
powerUp.removeEffect(gameManager);
```

---

## Mở rộng trong tương lai

### 1. PowerUp stacking
```java
public class PowerUpManager {
    // Track stack count for each effect
    private Map<PowerUpType, Integer> effectStacks;
    
    private void applyPowerUpEffect(PowerUp powerUp) {
        PowerUpType type = powerUp.getType();
        
        if (effectStacks.containsKey(type)) {
            // Stack effect
            int count = effectStacks.get(type) + 1;
            effectStacks.put(type, count);
            
            // Apply stacked effect (e.g., bigger paddle)
            powerUp.applyStackedEffect(gameManager, count);
        } else {
            // First application
            effectStacks.put(type, 1);
            powerUp.applyEffect(gameManager);
        }
    }
}
```

### 2. PowerUp queue/inventory
```java
public class PowerUpManager {
    private Queue<PowerUp> powerUpQueue;
    private int maxQueueSize = 3;
    
    public void collectPowerUp(PowerUp powerUp) {
        if (powerUpQueue.size() < maxQueueSize) {
            powerUpQueue.add(powerUp);
            System.out.println("PowerUp queued: " + powerUp.getType());
        } else {
            System.out.println("PowerUp queue full!");
        }
    }
    
    public void activateQueuedPowerUp(int index) {
        if (index >= 0 && index < powerUpQueue.size()) {
            PowerUp powerUp = powerUpQueue.remove();
            applyPowerUpEffect(powerUp);
        }
    }
}
```

### 3. PowerUp combinations/combos
```java
public class PowerUpManager {
    private Set<PowerUpType> activeTypes;
    
    private void checkCombos() {
        // Combo: EXPAND + LASER = Super Laser
        if (activeTypes.contains(PowerUpType.EXPAND) && 
            activeTypes.contains(PowerUpType.LASER)) {
            activateCombo(ComboType.SUPER_LASER);
        }
        
        // Combo: SLOW + CATCH = Easy Catch
        if (activeTypes.contains(PowerUpType.SLOW) && 
            activeTypes.contains(PowerUpType.CATCH)) {
            activateCombo(ComboType.EASY_CATCH);
        }
    }
}
```

### 4. Negative PowerUps (debuffs)
```java
public enum PowerUpType {
    // Positive
    EXPAND, LASER, LIFE,
    
    // Negative (red color, avoid!)
    SHRINK,      // Shrink paddle
    FAST_BALL,   // Increase ball speed
    REVERSE,     // Reverse paddle controls
    INVISIBLE    // Hide ball temporarily
}

public class PowerUpManager {
    public void spawnNegativePowerUp(double x, double y) {
        PowerUpType type = PowerUpType.randomNegative();
        PowerUp powerUp = createPowerUp(x, y, type);
        powerUp.setNegative(true); // Red color
        activePowerUps.add(powerUp);
    }
}
```

### 5. PowerUp rarity system
```java
public enum PowerUpRarity {
    COMMON(0.7),    // 70% - EXPAND, LASER
    RARE(0.25),     // 25% - DUPLICATE, CATCH
    LEGENDARY(0.05); // 5% - LIFE, WARP
    
    private final double chance;
    
    PowerUpRarity(double chance) {
        this.chance = chance;
    }
}

public class PowerUpManager {
    public void spawnFromBrick(double x, double y, BrickType brickType) {
        // Higher tier bricks = better PowerUp chance
        double rarityBonus = getRarityBonus(brickType);
        PowerUpType type = PowerUpType.randomByRarity(rarityBonus);
        
        // ...
    }
    
    private double getRarityBonus(BrickType type) {
        switch (type) {
            case GOLD: return 0.5;   // 50% bonus to rare/legendary
            case SILVER: return 0.2; // 20% bonus
            default: return 0.0;
        }
    }
}
```

### 6. Visual feedback system
```java
public class PowerUpManager {
    private PowerUpUI powerUpUI;
    
    public void applyPowerUpEffect(PowerUp powerUp) {
        // ... apply effect
        
        // Show visual notification
        powerUpUI.showNotification(
            powerUp.getType().getName() + " Activated!",
            powerUp.getType().getColor(),
            3000 // Duration in ms
        );
        
        // Show icon in UI
        powerUpUI.addActiveIcon(powerUp.getType());
    }
    
    public void removePowerUpEffect(PowerUpType type) {
        // ... remove effect
        
        // Remove icon from UI
        powerUpUI.removeActiveIcon(type);
        
        // Show expiry notification
        powerUpUI.showNotification(
            type.getName() + " Expired",
            Color.GRAY,
            1000
        );
    }
}
```

---

## Tổng kết

`PowerUpManager` là lớp quan trọng cho game mechanics:
- ✅ **Singleton:** Global access, shared state
- ✅ **Factory:** Centralized PowerUp creation
- ✅ **Automated:** Tự động spawn, update, expire effects
- ✅ **Robust:** Null checks, defensive copying, safe iteration
- ✅ **Extensible:** Dễ thêm PowerUp types mới
- ✅ **Integrated:** Tích hợp chặt chẽ với GameManager

Kết hợp với các PowerUp subclasses, tạo nên một power-up system hoàn chỉnh và engaging cho gameplay!

---

**Tác giả:** Arkanoid Development Team  
**Phiên bản:** 1.0  
**Cập nhật lần cuối:** 2024
