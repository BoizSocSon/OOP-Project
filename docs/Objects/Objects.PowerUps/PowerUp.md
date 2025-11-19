# PowerUp Class (Abstract)

## Tổng quan
`PowerUp` là lớp trừu tượng (abstract class) đại diện cho tất cả các vật phẩm power-up có thể rơi và được nhặt trong game Arkanoid. Đây là base class cho toàn bộ hệ thống power-up, quản lý các thuộc tính cơ bản như vị trí, kích thước, vận tốc rơi, animation, và logic va chạm với Paddle. Mỗi loại power-up cụ thể (Laser, Catch, Expand, v.v.) sẽ kế thừa từ lớp này và implement các hiệu ứng riêng.

## Vị trí
- **Package**: `Objects.PowerUps`
- **File**: `src/Objects/PowerUps/PowerUp.java`
- **Implements**: `GameObject` interface
- **Kế thừa**: Abstract class (các lớp con: CatchPowerUp, DuplicatePowerUp, ExpandPaddlePowerUp, LaserPowerUp, LifePowerUp, SlowBallPowerUp, WarpPowerUp)

## Mục đích
PowerUp:
- Định nghĩa cấu trúc chung cho tất cả power-ups
- Quản lý logic rơi xuống (falling mechanics)
- Xử lý animation và rendering
- Kiểm tra va chạm với Paddle
- Cung cấp template cho apply/remove effects
- Theo dõi trạng thái active/collected

## Kế thừa và Architecture

```
GameObject (Interface)
    ↑
    │ implements
    │
PowerUp (Abstract Class)
    ↑
    ├── CatchPowerUp (Concrete)
    ├── DuplicatePowerUp (Concrete)
    ├── ExpandPaddlePowerUp (Concrete)
    ├── LaserPowerUp (Concrete)
    ├── LifePowerUp (Concrete)
    ├── SlowBallPowerUp (Concrete)
    └── WarpPowerUp (Concrete)
    
PowerUpType (Enum)
    - Defines all 7 types
    - Manages spawn probabilities
    - Provides sprite paths
```

---

## Thuộc tính (Fields)

### 1. Position & Dimensions

#### `private double x`
**Mô tả**: Tọa độ X hiện tại (góc trên trái).

**Khởi tạo**: Từ constructor parameter.

**Thay đổi**: Update mỗi frame trong `update()` method.

**Getter**: `getX()`

---

#### `private double y`
**Mô tả**: Tọa độ Y hiện tại (góc trên trái).

**Khởi tạo**: Từ constructor parameter.

**Thay đổi**: Tăng mỗi frame (rơi xuống) trong `update()` method.

**Getter**: `getY()`

---

#### `private final double width`
**Mô tả**: Chiều rộng của power-up (constant).

**Giá trị**: `Constants.PowerUps.POWERUP_WIDTH`

**Immutable**: Không thay đổi sau khi khởi tạo.

**Getter**: `getWidth()`

---

#### `private final double height`
**Mô tả**: Chiều cao của power-up (constant).

**Giá trị**: `Constants.PowerUps.POWERUP_HEIGHT`

**Immutable**: Không thay đổi sau khi khởi tạo.

**Getter**: `getHeight()`

---

### 2. Type & Identity

#### `private final PowerUpType type`
**Mô tả**: Loại power-up (LASER, CATCH, EXPAND, v.v.).

**Kiểu**: `PowerUpType` enum.

**Khởi tạo**: Từ constructor parameter.

**Immutable**: Không thay đổi sau khi khởi tạo.

**Getter**: `getType()`

**Sử dụng**:
- Xác định sprite/animation nào sẽ load
- Xác định hiệu ứng nào sẽ apply
- Xác định thời gian hiệu lực (duration)

---

### 3. Movement

#### `private final Velocity velocity`
**Mô tả**: Vận tốc rơi của power-up (luôn hướng xuống dưới).

**Khởi tạo**: 
```java
new Velocity(0, Constants.PowerUps.POWERUP_FALL_SPEED)
// dx = 0 (không di chuyển ngang)
// dy = POWERUP_FALL_SPEED (rơi thẳng đứng xuống)
```

**Immutable**: Velocity object không thay đổi (constant fall speed).

**Sử dụng**: Apply lên position mỗi frame trong `update()`.

---

### 4. Visual

#### `private final Animation animation`
**Mô tả**: Animation hiển thị của power-up.

**Khởi tạo**: 
```java
AnimationFactory.createPowerUpAnimation(type)
```

**Behavior**:
- Loop animation liên tục
- Update mỗi frame
- Render sprite tương ứng với current frame

**Getter**: `getAnimation()`

---

### 5. State Management

#### `private boolean collected`
**Mô tả**: Đã được người chơi nhặt chưa?

**Giá trị**:
- `false` - Chưa nhặt (default)
- `true` - Đã nhặt (gọi `collect()`)

**Getter**: `isCollected()`

**Setter**: `collect()` (sets to true)

---

#### `private boolean active`
**Mô tả**: Power-up còn hoạt động/hiển thị trong game không?

**Giá trị**:
- `true` - Còn active (default), cần update/render
- `false` - Không active, nên remove khỏi game

**Getter**: `isActive()` và `isAlive()` (từ GameObject)

**Setter**: 
- `collect()` - sets to false
- `destroy()` - sets to false

**Lý do inactive**:
- Đã được nhặt (`collected = true`)
- Rơi ra khỏi màn hình (out of bounds)

---

## Constructor

### `PowerUp(double x, double y, PowerUpType type)`

**Mô tả**: Khởi tạo power-up với vị trí ban đầu và loại.

**Tham số**:
- `x` - Tọa độ X ban đầu (thường là vị trí của brick vừa bị phá)
- `y` - Tọa độ Y ban đầu
- `type` - Loại power-up (từ PowerUpType enum)

**Hành vi**:
1. Set `this.type = type`
2. Set position: `this.x = x`, `this.y = y`
3. Get dimensions: `width/height` từ Constants
4. Initialize state: `collected = false`, `active = true`
5. Create velocity: `new Velocity(0, POWERUP_FALL_SPEED)`
6. Create animation: `AnimationFactory.createPowerUpAnimation(type)`
7. Start animation: `animation.play()`

**Ví dụ**:
```java
// Khi brick bị phá, spawn power-up
if (shouldSpawnPowerUp(brick)) {
    PowerUpType type = PowerUpType.randomWeighted();
    PowerUp powerUp = createPowerUp(brick.getX(), brick.getY(), type);
    powerUps.add(powerUp);
}

// Factory method để tạo concrete power-up
public PowerUp createPowerUp(double x, double y, PowerUpType type) {
    switch (type) {
        case LASER: return new LaserPowerUp(x, y);
        case CATCH: return new CatchPowerUp(x, y);
        case EXPAND: return new ExpandPaddlePowerUp(x, y);
        // ... other types
    }
}
```

---

## Phương thức

### 1. `void update()` (Public)

**Mô tả**: Cập nhật trạng thái power-up mỗi frame.

**Hành vi**:
1. Tính vị trí mới:
   ```java
   Point currentPos = new Point(x, y);
   Point newPos = velocity.applyToPoint(currentPos);
   this.x = newPos.getX();
   this.y = newPos.getY();
   ```
2. Cập nhật animation:
   ```java
   if (animation != null) {
       animation.update(); // Next frame
   }
   ```

**Gọi**: Mỗi frame trong game loop (60 FPS).

**Ví dụ**:
```java
// Trong GameManager.update()
for (PowerUp powerUp : powerUps) {
    if (powerUp.isAlive()) {
        powerUp.update(); // Move down + animate
    }
}
```

---

### 2. `boolean checkPaddleCollision(Paddle paddle)` (Public)

**Mô tả**: Kiểm tra va chạm giữa power-up và Paddle.

**Tham số**: `paddle` - Paddle của người chơi.

**Kiểu trả về**: `boolean`
- `true` - Va chạm xảy ra
- `false` - Không va chạm

**Logic**:
```java
if (paddle == null || !active) {
    return false; // Invalid state
}

return getBounds().intersects(paddle.getBounds());
```

**Sử dụng**:
```java
// Trong CollisionManager
for (PowerUp powerUp : powerUps) {
    if (powerUp.checkPaddleCollision(paddle)) {
        powerUp.collect(); // Mark as collected
        powerUp.applyEffect(gameManager); // Apply effect
        audioManager.playPowerUpSound();
    }
}
```

---

### 3. `void collect()` (Public)

**Mô tả**: Đánh dấu power-up là đã được nhặt.

**Hành vi**:
```java
this.collected = true;
this.active = false; // Biến mất khỏi màn hình
```

**Effect**: 
- Power-up ngừng update/render
- Nên remove khỏi list trong cleanup

**Gọi**: Khi va chạm với Paddle.

---

### 4. `abstract void applyEffect(GameManager gameManager)` (Abstract)

**Mô tả**: Áp dụng hiệu ứng cụ thể của power-up lên game.

**Tham số**: `gameManager` - GameManager để thay đổi game state.

**Abstract**: Phải implement trong lớp con.

**Implementations**:

```java
// LaserPowerUp
@Override
public void applyEffect(GameManager gameManager) {
    gameManager.enableLaser();
}

// CatchPowerUp
@Override
public void applyEffect(GameManager gameManager) {
    gameManager.enableCatchMode();
}

// ExpandPaddlePowerUp
@Override
public void applyEffect(GameManager gameManager) {
    gameManager.expandPaddle();
}

// DuplicatePowerUp (instant effect)
@Override
public void applyEffect(GameManager gameManager) {
    gameManager.duplicateBalls();
}
```

---

### 5. `abstract void removeEffect(GameManager gameManager)` (Abstract)

**Mô tả**: Loại bỏ hiệu ứng của power-up (khi hết thời gian).

**Tham số**: `gameManager` - GameManager để revert game state.

**Abstract**: Phải implement trong lớp con.

**Lưu ý**: Instant effects (DUPLICATE, LIFE, WARP) để trống method này.

**Implementations**:

```java
// LaserPowerUp
@Override
public void removeEffect(GameManager gameManager) {
    gameManager.disableLaser();
}

// CatchPowerUp
@Override
public void removeEffect(GameManager gameManager) {
    gameManager.disableCatchMode();
}

// ExpandPaddlePowerUp
@Override
public void removeEffect(GameManager gameManager) {
    gameManager.revertPaddleSize();
}

// DuplicatePowerUp (instant - no removal)
@Override
public void removeEffect(GameManager gameManager) {
    // Empty - instant effect không có expiration
}
```

---

### 6. Getters

#### `double getX()`
Return current X position.

#### `double getY()`
Return current Y position.

#### `double getWidth()`
Return power-up width.

#### `double getHeight()`
Return power-up height.

#### `PowerUpType getType()`
Return power-up type.

#### `boolean isActive()`
Return active state.

#### `boolean isCollected()`
Return collected state.

#### `Animation getAnimation()`
Return animation object (for rendering).

---

### 7. GameObject Interface Methods

#### `Rectangle getBounds()` (Override)
**Mô tả**: Trả về bounding box cho collision detection.

**Implementation**:
```java
@Override
public Rectangle getBounds() {
    return new Rectangle(new Point(x, y), width, height);
}
```

---

#### `boolean isAlive()` (Override)
**Mô tả**: Kiểm tra power-up có còn "sống" (cần update/render) không.

**Implementation**:
```java
@Override
public boolean isAlive() {
    return active;
}
```

---

#### `void destroy()` (Override)
**Mô tả**: Vô hiệu hóa power-up (gọi khi rơi ra khỏi màn hình).

**Implementation**:
```java
@Override
public void destroy() {
    active = false;
}
```

**Gọi**:
```java
// Cleanup power-ups that fell off screen
for (PowerUp powerUp : powerUps) {
    if (powerUp.getY() > screenHeight) {
        powerUp.destroy();
    }
}

powerUps.removeIf(p -> !p.isAlive());
```

---

## Lifecycle của PowerUp

```
1. CREATION
   ↓
   Brick destroyed → should spawn power-up?
   → type = PowerUpType.randomWeighted()
   → new ConcretePowerUp(x, y) // e.g. LaserPowerUp
   → super(x, y, type)
   → active = true, collected = false
   → velocity = (0, FALL_SPEED)
   → animation = createPowerUpAnimation(type)
   → animation.play()

2. FALLING STATE
   ↓
   Mỗi frame (60 FPS):
     update() called
     → y += velocity.dy
     → animation.update()
     
   Render:
     → Draw animation.getCurrentFrame()
   
   Collision check:
     → checkPaddleCollision(paddle)

3. COLLECTED (Hit Paddle)
   ↓
   Collision detected
   → collect() called
   → collected = true
   → active = false
   
   → applyEffect(gameManager) called
   → GameManager changes state
   
   Sound: "powerup_collect.wav"
   Effect: Visual feedback, UI update

4. EFFECT ACTIVE (For timed effects)
   ↓
   PowerUpManager tracks:
     - Effect start time
     - Effect duration (from PowerUpType)
     
   Example (Laser):
     - Paddle.laserEnabled = true
     - Can shoot lasers
     
   When duration expires:
     → removeEffect(gameManager) called
     → Paddle.laserEnabled = false

5. CLEANUP
   ↓
   Case A: Collected
     → active = false
     → Remove from powerUps list
     
   Case B: Fell off screen
     → y > screenHeight
     → destroy() called → active = false
     → Remove from powerUps list
     
   powerUps.removeIf(p -> !p.isAlive())
```

---

## Template Method Pattern

PowerUp sử dụng Template Method pattern:

```java
// Template (in PowerUp abstract class)
public final void onCollect(GameManager gm) {
    collect();              // Common behavior
    applyEffect(gm);        // Specific behavior (abstract)
    playCollectSound();     // Common behavior
}

// Concrete implementations
class LaserPowerUp extends PowerUp {
    @Override
    public void applyEffect(GameManager gm) {
        gm.enableLaser(); // Specific to Laser
    }
}

class CatchPowerUp extends PowerUp {
    @Override
    public void applyEffect(GameManager gm) {
        gm.enableCatchMode(); // Specific to Catch
    }
}
```

---

## Tích hợp với game systems

### 1. PowerUpManager

```java
public class PowerUpManager {
    private List<PowerUp> activePowerUps; // Falling power-ups
    private Map<PowerUpType, Long> activeEffects; // Timed effects
    
    public void update() {
        // Update all falling power-ups
        for (PowerUp powerUp : activePowerUps) {
            powerUp.update();
            
            // Check collision with paddle
            if (powerUp.checkPaddleCollision(paddle)) {
                powerUp.collect();
                powerUp.applyEffect(gameManager);
                
                // Track timed effects
                if (!powerUp.getType().isInstant()) {
                    long duration = powerUp.getType().getDuration();
                    activeEffects.put(powerUp.getType(), 
                        System.currentTimeMillis() + duration);
                }
            }
            
            // Check out of bounds
            if (powerUp.getY() > screenHeight) {
                powerUp.destroy();
            }
        }
        
        // Cleanup inactive power-ups
        activePowerUps.removeIf(p -> !p.isAlive());
        
        // Check expired effects
        checkExpiredEffects();
    }
    
    private void checkExpiredEffects() {
        Iterator<Map.Entry<PowerUpType, Long>> it = 
            activeEffects.entrySet().iterator();
        
        while (it.hasNext()) {
            Map.Entry<PowerUpType, Long> entry = it.next();
            if (System.currentTimeMillis() > entry.getValue()) {
                // Effect expired
                PowerUp powerUp = createDummyPowerUp(entry.getKey());
                powerUp.removeEffect(gameManager);
                it.remove();
            }
        }
    }
    
    public void spawnPowerUp(double x, double y) {
        PowerUpType type = PowerUpType.randomWeighted();
        PowerUp powerUp = createPowerUp(x, y, type);
        activePowerUps.add(powerUp);
    }
}
```

---

### 2. Brick Integration

```java
// Trong CollisionManager hoặc Brick.destroy()
public void onBrickDestroyed(Brick brick) {
    // 20% chance to spawn power-up
    if (Math.random() < 0.2) {
        double x = brick.getX() + brick.getWidth() / 2;
        double y = brick.getY();
        powerUpManager.spawnPowerUp(x, y);
    }
}
```

---

### 3. Rendering System

```java
public void renderPowerUps(Graphics g) {
    for (PowerUp powerUp : activePowerUps) {
        if (powerUp.isAlive()) {
            Animation anim = powerUp.getAnimation();
            anim.render(g, (int) powerUp.getX(), (int) powerUp.getY());
        }
    }
}
```

---

## Best Practices

### 1. Factory Pattern
```java
// ✅ Đúng - dùng factory method
public PowerUp createPowerUp(double x, double y, PowerUpType type) {
    switch (type) {
        case LASER: return new LaserPowerUp(x, y);
        case CATCH: return new CatchPowerUp(x, y);
        case EXPAND: return new ExpandPaddlePowerUp(x, y);
        case DUPLICATE: return new DuplicatePowerUp(x, y);
        case LIFE: return new LifePowerUp(x, y);
        case SLOW: return new SlowBallPowerUp(x, y);
        case WARP: return new WarpPowerUp(x, y);
        default: throw new IllegalArgumentException("Unknown type: " + type);
    }
}

// ❌ Sai - hardcode type trong constructor
PowerUp powerUp = new PowerUp(x, y, PowerUpType.LASER); // Compile error (abstract)
```

---

### 2. Null Safety
```java
// ✅ Đúng - check null
@Override
public void applyEffect(GameManager gameManager) {
    if (gameManager == null) {
        System.err.println("GameManager is null!");
        return;
    }
    gameManager.enableLaser();
}

// ❌ Sai - không check null
@Override
public void applyEffect(GameManager gameManager) {
    gameManager.enableLaser(); // NullPointerException nếu null
}
```

---

### 3. Effect Stacking
```java
// ✅ Đúng - không stack cùng loại effect
public void applyEffect(PowerUpType type) {
    if (activeEffects.containsKey(type)) {
        // Refresh duration thay vì stack
        long newExpiry = System.currentTimeMillis() + type.getDuration();
        activeEffects.put(type, newExpiry);
    } else {
        // Apply new effect
        PowerUp powerUp = createPowerUp(0, 0, type);
        powerUp.applyEffect(gameManager);
        activeEffects.put(type, 
            System.currentTimeMillis() + type.getDuration());
    }
}
```

---

### 4. Instant vs Timed Effects
```java
// ✅ Đúng - phân biệt instant và timed
public void onPowerUpCollected(PowerUp powerUp) {
    powerUp.applyEffect(gameManager);
    
    if (powerUp.getType().isInstant()) {
        // Instant effects - không track expiration
        // (DUPLICATE, LIFE, WARP)
    } else {
        // Timed effects - track expiration
        long expiry = System.currentTimeMillis() + 
                     powerUp.getType().getDuration();
        activeEffects.put(powerUp.getType(), expiry);
    }
}
```

---

### 5. Cleanup Strategy
```java
// ✅ Đúng - remove inactive power-ups
public void cleanupPowerUps() {
    activePowerUps.removeIf(p -> !p.isAlive());
}

// ❌ Sai - không cleanup (memory leak)
public void cleanupPowerUps() {
    // Nothing - list grows forever
}
```

---

## Mở rộng hệ thống

### Thêm Power-Up mới

```java
// 1. Add to PowerUpType enum
public enum PowerUpType {
    // Existing...
    SHIELD("powerup_shield", 0.10); // New power-up
}

// 2. Create concrete class
public class ShieldPowerUp extends PowerUp {
    public ShieldPowerUp(double x, double y) {
        super(x, y, PowerUpType.SHIELD);
    }
    
    @Override
    public void applyEffect(GameManager gameManager) {
        gameManager.enableShield();
    }
    
    @Override
    public void removeEffect(GameManager gameManager) {
        gameManager.disableShield();
    }
}

// 3. Add to factory
case SHIELD: return new ShieldPowerUp(x, y);

// 4. Add sprites
// Resources/Graphics/powerup_shield_0.png
// Resources/Graphics/powerup_shield_1.png
// ...

// 5. Add constants
public class Constants {
    public static class PowerUps {
        public static final long SHIELD_DURATION = 10000; // 10s
    }
}
```

---

## Kết luận

`PowerUp` là abstract base class quan trọng trong hệ thống power-up:

- **Abstraction**: Cung cấp template cho tất cả power-ups
- **Encapsulation**: Quản lý state và behavior chung
- **Extensibility**: Dễ dàng thêm power-up types mới
- **Clean Separation**: Abstract methods tách logic chung và riêng
- **GameObject Integration**: Implement GameObject interface một cách nhất quán

PowerUp là ví dụ điển hình của abstract class trong OOP - nó cung cấp implementation cho behavior chung (falling, collision, animation) nhưng để lại abstract methods cho behavior specific (applyEffect, removeEffect). Thiết kế này giúp code DRY (Don't Repeat Yourself) và dễ maintain khi có nhiều loại power-ups.

**Fun Fact**: System power-up trong Arkanoid là một trong những mechanic kinh điển của breakout games, giúp game không chỉ về phá gạch mà còn về strategic decision making (nhặt power-up nào? bỏ qua power-up nào?).
