# DuplicatePowerUp Class

## Tổng quan
`DuplicatePowerUp` là power-up "Nhân đôi bóng" - một trong những power-up offense mạnh nhất trong Arkanoid. Khi nhặt được, power-up này sẽ TẠO BẢN SAO của TẤT CẢ các quả bóng hiện có trong game, effectively nhân đôi số lượng bóng đang chơi. Đây là instant effect (tức thời) và permanent (vĩnh viễn cho đến khi bóng bị mất), giúp tăng tốc độ phá gạch và cung cấp "safety net" nếu mất một vài bóng.

## Vị trí
- **Package**: `Objects.PowerUps`
- **File**: `src/Objects/PowerUps/DuplicatePowerUp.java`
- **Kế thừa**: `PowerUp` (abstract)
- **Implements**: `GameObject` (gián tiếp qua PowerUp)

## Mục đích
DuplicatePowerUp:
- Nhân đôi số lượng bóng trong game
- Tăng tốc độ clear level
- Cung cấp backup balls (safety)
- Tạo chaos và pressure lên bricks
- Instant effect (không có duration)
- Permanent effect (cho đến khi bóng mất)

## Kế thừa

```
GameObject (Interface)
    ↑
    │ implements
    │
PowerUp (Abstract Class)
    ↑
    │ extends
    │
DuplicatePowerUp (Concrete Class)
    │
    └── PowerUpType.DUPLICATE (Instant effect)
```

---

## Constructor

### `DuplicatePowerUp(double x, double y)`

**Mô tả**: Khởi tạo Duplicate power-up với vị trí ban đầu.

**Tham số**:
- `x` - Tọa độ X (thường là vị trí brick vừa phá)
- `y` - Tọa độ Y

**Hành vi**:
```java
super(x, y, PowerUpType.DUPLICATE);
```
- Gọi constructor của PowerUp
- Type = `PowerUpType.DUPLICATE`
- Animation = "powerup_duplicate_0.png", "powerup_duplicate_1.png", ...
- Velocity = (0, POWERUP_FALL_SPEED)
- active = true, collected = false

**Ví dụ**:
```java
// Spawn khi brick destroyed
if (shouldSpawnPowerUp()) {
    PowerUpType type = PowerUpType.randomWeighted();
    if (type == PowerUpType.DUPLICATE) {
        double x = brick.getX() + brick.getWidth() / 2;
        double y = brick.getY();
        DuplicatePowerUp duplicatePowerUp = new DuplicatePowerUp(x, y);
        powerUps.add(duplicatePowerUp);
    }
}
```

---

## Phương thức

### 1. `void applyEffect(GameManager gameManager)` (Override)

**Mô tả**: Nhân đôi tất cả các quả bóng hiện có trong game.

**Tham số**: `gameManager` - GameManager để access ball list.

**Hành vi**:
1. Null safety check:
   ```java
   if (gameManager == null) {
       System.err.println("DuplicatePowerUp: GameManager is null");
       return;
   }
   ```

2. Get current ball count (for logging):
   ```java
   int originalCount = gameManager.getBallCount();
   ```

3. Duplicate all balls:
   ```java
   gameManager.duplicateBalls();
   ```

4. Get new ball count:
   ```java
   int newCount = gameManager.getBallCount();
   ```

5. Log message:
   ```java
   System.out.println("DuplicatePowerUp: Balls duplicated from " +
       originalCount + " to " + newCount);
   ```

**Effect trong GameManager**:
```java
// GameManager.duplicateBalls()
public void duplicateBalls() {
    // Tạo list tạm để tránh ConcurrentModificationException
    List<Ball> ballsToDuplicate = new ArrayList<>(balls);
    
    for (Ball originalBall : ballsToDuplicate) {
        // Clone ball
        Ball newBall = new Ball(
            originalBall.getX(),
            originalBall.getY(),
            originalBall.getRadius()
        );
        
        // Copy velocity (có thể slightly modify để tránh overlap)
        Velocity originalVelocity = originalBall.getVelocity();
        double angle = Math.random() * 30 - 15; // ±15 degrees variation
        Velocity newVelocity = originalVelocity.rotate(angle);
        newBall.setVelocity(newVelocity);
        
        // Copy other properties
        newBall.setSpeed(originalBall.getSpeed());
        newBall.setColor(originalBall.getColor());
        
        // Add to game
        balls.add(newBall);
    }
    
    // Visual/audio feedback
    audioManager.playDuplicateSound();
    particleManager.spawnDuplicationEffect();
}
```

**Result**:
- 1 ball → 2 balls
- 2 balls → 4 balls
- 3 balls → 6 balls
- N balls → 2N balls

**Gọi**: Ngay khi power-up collision với Paddle.

---

### 2. `void removeEffect(GameManager gameManager)` (Override)

**Mô tả**: Loại bỏ effect của Duplicate power-up.

**Hành vi**: **EMPTY METHOD** - không làm gì cả!

```java
@Override
public void removeEffect(GameManager gameManager) {
    // Hiệu ứng DUPLICATE là tức thời và vĩnh viễn.
    // Không có expiration logic.
}
```

**Lý do**:
- DUPLICATE là instant effect (apply ngay lập tức)
- Effect là permanent (bóng tồn tại cho đến khi bị mất)
- Không có duration (không hết hạn)
- Không cần cleanup logic

**Lưu ý**: Bóng duplicate sẽ tự động removed khi:
- Rơi ra khỏi màn hình (lost)
- Level clear
- Player loses life (all balls reset)

---

## Duplication Mechanics

### Ball Cloning

```java
public Ball cloneBall(Ball original) {
    // 1. Create new ball at same position
    Ball clone = new Ball(
        original.getX(),
        original.getY(),
        original.getRadius()
    );
    
    // 2. Copy velocity với slight variation
    Velocity vel = original.getVelocity();
    double angleVariation = (Math.random() * 30) - 15; // -15° to +15°
    Velocity newVel = vel.rotate(angleVariation);
    clone.setVelocity(newVel);
    
    // 3. Copy properties
    clone.setSpeed(original.getSpeed());
    clone.setPowerUpModifiers(original.getPowerUpModifiers());
    
    // 4. Slightly offset position để tránh overlap visual
    double offsetX = (Math.random() * 10) - 5;
    double offsetY = (Math.random() * 10) - 5;
    clone.setPosition(
        clone.getX() + offsetX,
        clone.getY() + offsetY
    );
    
    return clone;
}
```

---

### Velocity Variation Strategy

```java
// Strategy 1: Random angle variation
public Velocity varyVelocity(Velocity original, double maxAngleDeg) {
    double randomAngle = (Math.random() * 2 - 1) * maxAngleDeg;
    return original.rotate(randomAngle);
}

// Strategy 2: Symmetric splitting
public List<Velocity> splitSymmetric(Velocity original) {
    double angle = 15; // degrees
    return Arrays.asList(
        original.rotate(-angle), // Left
        original.rotate(angle)   // Right
    );
}

// Strategy 3: Random direction
public Velocity randomizeDirection(Velocity original) {
    double speed = original.getSpeed();
    double randomAngle = Math.random() * 360;
    
    double dx = speed * Math.cos(Math.toRadians(randomAngle));
    double dy = speed * Math.sin(Math.toRadians(randomAngle));
    
    // Ensure upward component
    if (dy > 0) dy = -dy;
    
    return new Velocity(dx, dy);
}
```

---

## Luồng hoạt động

### Lifecycle của DuplicatePowerUp Effect

```
1. SPAWN
   ↓
   Brick destroyed
   → Random weighted selection (12% chance)
   → new DuplicatePowerUp(x, y)
   → Rơi xuống với animation (powerup_duplicate)

2. COLLECTION
   ↓
   Power-up hits paddle
   → collect() called
   → applyEffect(gameManager)
   
   gameManager.duplicateBalls():
     Current balls: [Ball1, Ball2]
     
     Clone Ball1 → Ball1_clone
       - Same position
       - Velocity rotated +10°
       - Slightly offset position
     
     Clone Ball2 → Ball2_clone
       - Same position
       - Velocity rotated -10°
       - Slightly offset position
     
     New balls: [Ball1, Ball2, Ball1_clone, Ball2_clone]
   
   Visual: Particle burst effect at each ball
   Sound: "duplicate.wav" - echo/multiplication sound
   UI: Brief "x2" indicator

3. INSTANT EFFECT (No duration)
   ↓
   Effect applied → complete
   No tracking needed
   No expiration timer
   removeEffect() is never called

4. PERMANENT STATE
   ↓
   Duplicated balls continue playing:
   
   For each ball:
     - Update position
     - Check collisions
     - Can be lost independently
     
   Ball count decreases as balls are lost:
     4 balls → 3 balls (one lost)
     3 balls → 2 balls (one lost)
     2 balls → 1 ball (one lost)
     1 ball → Game Over (last ball lost)

5. CLEANUP (Natural)
   ↓
   Case A: Balls lost naturally
     → balls.removeIf(b -> b.isLost())
     → Ball count decreases
     
   Case B: Level complete
     → All balls cleared
     → New level starts with 1 ball
     
   Case C: Life lost
     → All balls cleared
     → Respawn with 1 ball
```

---

## Chiến thuật sử dụng

### 1. Early Game Advantage

```java
// Duplicate early → more time with multiple balls
if (levelProgress < 0.3 && hasDuplicatePowerUp) {
    // Maximum benefit - nhiều gạch còn lại
    // Tốc độ clear tăng exponentially
}
```

---

### 2. Combo với Slow Ball

```java
// Duplicate + Slow = Controllable chaos
if (hasSlowBallEffect && collectDuplicatePowerUp) {
    // Multiple slow balls → easy to track
    // High brick destruction rate
    // Low risk of losing balls
}
```

---

### 3. Emergency Backup

```java
// Còn 1 bóng cuối → collect duplicate để safety
if (ballCount == 1 && seeDuplicatePowerUp) {
    // Ưu tiên collect to create backup
    // 2 balls = much safer than 1
}
```

---

### 4. Avoid Over-Duplication

```java
// Cẩn thận với nhiều balls quá
if (ballCount >= 6) {
    // Có thể skip duplicate power-up
    // Quá nhiều balls → hard to track
    // Risk of confusion
}
```

---

## Visual & Audio Feedback

### Duplication Effect

```java
public void renderDuplicationEffect(Graphics2D g, Ball original, Ball clone) {
    // 1. Particle burst tại vị trí duplicate
    int particleCount = 20;
    for (int i = 0; i < particleCount; i++) {
        double angle = (360.0 / particleCount) * i;
        double speed = 2 + Math.random() * 3;
        
        Particle p = new Particle(
            clone.getX() + clone.getWidth() / 2,
            clone.getY() + clone.getHeight() / 2,
            angle, speed,
            Color.CYAN,
            1000 // lifetime ms
        );
        particles.add(p);
    }
    
    // 2. Flash effect
    g.setColor(new Color(0, 255, 255, 150)); // Cyan with alpha
    g.fillOval(
        (int) clone.getX() - 10,
        (int) clone.getY() - 10,
        (int) clone.getWidth() + 20,
        (int) clone.getHeight() + 20
    );
    
    // 3. "x2" text indicator
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 24));
    g.drawString("x2", 
        (int) clone.getX(), 
        (int) clone.getY() - 20);
}
```

---

### Sound Design

```java
// Duplication sound - echo/multiplication effect
audioManager.playDuplicateSound(); 
// "duplicate.wav" - sci-fi multiplication sound với echo

// Alternative: Play pitch-shifted ball bounce sounds
for (Ball ball : duplicatedBalls) {
    audioManager.playPitchedSound("ball_spawn.wav", 1.0 + Math.random() * 0.5);
}
```

---

### UI Ball Counter

```java
public void renderBallCounter(Graphics2D g) {
    // Icon + số lượng
    Sprite ballIcon = spriteCache.getSprite("ball_icon");
    g.drawImage(ballIcon.getImage(), 10, 10);
    
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 20));
    g.drawString("x" + ballCount, 40, 30);
    
    // Flash effect khi duplicate
    if (recentlyDuplicated) {
        g.setColor(Color.CYAN);
        g.drawString("x" + ballCount, 40, 30);
    }
}
```

---

## So sánh với các power-up khác

| Power-Up | Effect | Type | Duration | Risk/Reward |
|----------|--------|------|----------|-------------|
| **DUPLICATE** | 2x balls | Instant | Permanent | ⚠️ Medium risk, high reward |
| LASER | Shoot bricks | Timed | 15-20s | ⭐ Low risk, medium reward |
| CATCH | Catch ball | Timed | 15-20s | ⭐ Low risk, control |
| SLOW | Slow balls | Timed | 15-20s | ⭐ Low risk, easier play |
| WARP | Skip level | Instant | N/A | ⭐⭐⭐ No risk, high reward |

**DUPLICATE Characteristics**:
- **High Power**: Doubles offense capability
- **Permanent**: Effect lasts until balls lost
- **Risky**: More balls = harder to track
- **Snowball**: Can stack with multiple collects
- **No Undo**: Can't "un-duplicate"

---

## Edge Cases & Considerations

### 1. Maximum Ball Limit

```java
// ✅ Đúng - giới hạn số bóng tối đa
public void duplicateBalls() {
    if (balls.size() >= MAX_BALLS) { // e.g. MAX_BALLS = 10
        System.out.println("Max balls reached, duplicate cancelled");
        return;
    }
    
    int ballsToCreate = Math.min(balls.size(), MAX_BALLS - balls.size());
    List<Ball> ballsToDuplicate = new ArrayList<>(
        balls.subList(0, ballsToCreate)
    );
    
    for (Ball ball : ballsToDuplicate) {
        balls.add(cloneBall(ball));
    }
}

// ❌ Sai - unlimited balls (performance issue)
public void duplicateBalls() {
    List<Ball> copy = new ArrayList<>(balls);
    for (Ball ball : copy) {
        balls.add(cloneBall(ball)); // Can grow to 1000+ balls
    }
}
```

---

### 2. Caught Ball Handling

```java
// ✅ Đúng - không duplicate caught balls
public void duplicateBalls() {
    List<Ball> ballsToDuplicate = new ArrayList<>();
    
    for (Ball ball : balls) {
        if (!ball.isCaught()) { // Chỉ duplicate free balls
            ballsToDuplicate.add(ball);
        }
    }
    
    for (Ball ball : ballsToDuplicate) {
        balls.add(cloneBall(ball));
    }
}
```

---

### 3. Performance Optimization

```java
// ✅ Đúng - batch creation với pre-allocation
public void duplicateBalls() {
    int currentSize = balls.size();
    List<Ball> newBalls = new ArrayList<>(currentSize); // Pre-allocate
    
    for (Ball ball : balls) {
        newBalls.add(cloneBall(ball));
    }
    
    balls.addAll(newBalls); // Batch add
}

// ❌ Sai - add one by one (slower)
public void duplicateBalls() {
    int count = balls.size();
    for (int i = 0; i < count; i++) {
        balls.add(cloneBall(balls.get(i))); // Repeated add
    }
}
```

---

### 4. Collision Avoidance

```java
// ✅ Đúng - offset duplicated balls để tránh immediate collision
public Ball cloneBall(Ball original) {
    Ball clone = new Ball(original.getX(), original.getY(), original.getRadius());
    
    // Offset position
    double offsetAngle = Math.random() * 360;
    double offsetDist = 20; // pixels
    double offsetX = offsetDist * Math.cos(Math.toRadians(offsetAngle));
    double offsetY = offsetDist * Math.sin(Math.toRadians(offsetAngle));
    
    clone.setPosition(
        clone.getX() + offsetX,
        clone.getY() + offsetY
    );
    
    // Vary velocity
    clone.setVelocity(varyVelocity(original.getVelocity(), 20));
    
    return clone;
}
```

---

## Best Practices

### 1. Spawn Rate Balance
```java
// ✅ Đúng - reasonable spawn probability
PowerUpType.DUPLICATE("powerup_duplicate", 0.12); // 12% chance

// ❌ Sai - quá cao (too chaotic)
PowerUpType.DUPLICATE("powerup_duplicate", 0.40); // 40% chance
```

---

### 2. Visual Distinction
```java
// ✅ Đúng - duplicated balls có slight visual difference
public void renderBall(Graphics2D g, Ball ball) {
    if (ball.isDuplicated()) {
        // Add glow effect for duplicates
        g.setColor(new Color(0, 255, 255, 50));
        g.fillOval(
            (int) ball.getX() - 5,
            (int) ball.getY() - 5,
            (int) ball.getWidth() + 10,
            (int) ball.getHeight() + 10
        );
    }
    
    // Render normal ball
    ball.render(g);
}
```

---

### 3. Combo Tracking
```java
// ✅ Đúng - track multiple duplicate collects
private int duplicateStackCount = 0;

public void onDuplicateCollected() {
    duplicateStackCount++;
    
    // Achievements
    if (duplicateStackCount >= 3) {
        achievementManager.unlock("BALL_MASTER");
    }
}
```

---

## Kết luận

`DuplicatePowerUp` là power-up mạnh và exciting nhất:

- **Instant Impact**: Effect tức thời, immediately double firepower
- **Permanent**: Lasts until balls are lost naturally
- **High Risk/Reward**: More balls = faster clear nhưng harder to track
- **Snowball Effect**: Có thể stack multiple times
- **Simple Code**: Empty removeEffect() - instant effect design
- **Fun Factor**: Creates satisfying chaos và spectacle

DuplicatePowerUp là ví dụ tốt về instant effect trong game design. Không như timed effects cần tracking expiration, instant effects apply once và done. Code đơn giản hơn nhiều, nhưng impact lên gameplay vẫn rất lớn. Đây cũng là power-up có "momentum" - càng collect nhiều, game càng chaotic và exciting.

**Fun Fact**: Trong Arkanoid gốc, Duplicate (D) power-up là một trong những sought-after nhất vì nó dramatically tăng tốc độ clear level. Speedrunners thường prioritize collect Duplicate power-ups để minimize level time.
