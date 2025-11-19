# LaserPowerUp Class

## Tổng quan
`LaserPowerUp` là power-up "Laser" - một trong những power-up offensive mạnh nhất trong Arkanoid. Khi nhặt được, Paddle sẽ được trang bị HAI KHẨU PHÁO LASER ở hai bên, cho phép bắn tia laser thẳng lên trên để PHÁ GẠCH từ xa mà không cần dùng bóng. Người chơi có số lượng đạn giới hạn (hoặc time limit), và có thể bắn bằng cách nhấn phím. Đây là power-up yêu thích của nhiều người vì cho phép active attack thay vì passive defense.

## Vị trí
- **Package**: `Objects.PowerUps`
- **File**: `src/Objects/PowerUps/LaserPowerUp.java`
- **Kế thừa**: `PowerUp` (abstract)
- **Implements**: `GameObject` (gián tiếp qua PowerUp)

## Mục đích
LaserPowerUp:
- Trang bị laser cannons cho Paddle
- Cho phép bắn phá gạch từ xa (projectile attack)
- Tạo offensive playstyle option
- Limited ammo hoặc timed duration
- Hữu ích cho hard-to-reach bricks
- Active ability (require input)

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
LaserPowerUp (Concrete Class)
    │
    ├── PowerUpType.LASER (Timed effect)
    └── Spawns Laser objects when fired
```

---

## Constructor

### `LaserPowerUp(double x, double y)`

**Mô tả**: Khởi tạo Laser power-up với vị trí ban đầu.

**Tham số**:
- `x` - Tọa độ X (thường là vị trí brick vừa phá)
- `y` - Tọa độ Y

**Hành vi**:
```java
super(x, y, PowerUpType.LASER);
```
- Gọi constructor của PowerUp
- Type = `PowerUpType.LASER`
- Animation = "powerup_laser_0.png", "powerup_laser_1.png", ...
- Velocity = (0, POWERUP_FALL_SPEED)
- active = true, collected = false

**Ví dụ**:
```java
// Spawn khi brick destroyed
if (shouldSpawnPowerUp() && random.nextDouble() < 0.15) {
    double x = brick.getX() + brick.getWidth() / 2;
    double y = brick.getY();
    LaserPowerUp laserPowerUp = new LaserPowerUp(x, y);
    powerUps.add(laserPowerUp);
}
```

---

## Phương thức

### 1. `void applyEffect(GameManager gameManager)` (Override)

**Mô tả**: Kích hoạt chế độ laser trên Paddle, trang bị laser cannons.

**Tham số**: `gameManager` - GameManager để access Paddle và game state.

**Hành vi**:
1. Null safety check:
   ```java
   if (gameManager == null) {
       System.err.println("LaserPowerUp: GameManager is null");
       return;
   }
   ```

2. Enable laser:
   ```java
   gameManager.enableLaser();
   ```

3. Log message:
   ```java
   System.out.println("LaserPowerUp: Laser enabled with " +
       Constants.Laser.LASER_SHOTS + " shots for " +
       Constants.PowerUps.LASER_DURATION / 1000.0 + " seconds");
   ```
   Example output: `"Laser enabled with 30 shots for 20.0 seconds"`

**Effect trong GameManager**:
```java
// GameManager.enableLaser()
public void enableLaser() {
    if (paddle != null) {
        paddle.enableLaser(); // Set laserEnabled = true
        paddle.setLaserShots(Constants.Laser.LASER_SHOTS); // e.g. 30 shots
        
        // Visual feedback
        paddle.setPaddleState(PaddleState.LASER);
        
        // UI update
        uiManager.showPowerUpIndicator(PowerUpType.LASER);
        uiManager.showLaserAmmo(paddle.getLaserShots());
        
        // Sound effect
        audioManager.playLaserEquipSound();
    }
}
```

**Gọi**: Khi power-up collision với Paddle.

---

### 2. `void removeEffect(GameManager gameManager)` (Override)

**Mô tả**: Vô hiệu hóa chế độ laser sau khi hết thời gian hoặc hết đạn.

**Tham số**: `gameManager` - GameManager để access Paddle.

**Hành vi**:
1. Null safety check:
   ```java
   if (gameManager == null) {
       System.err.println("LaserPowerUp: GameManager is null");
       return;
   }
   ```

2. Disable laser:
   ```java
   gameManager.disableLaser();
   ```

3. Log message:
   ```java
   System.out.println("LaserPowerUp: Laser disabled (expired or shots depleted)");
   ```

**Effect trong GameManager**:
```java
// GameManager.disableLaser()
public void disableLaser() {
    if (paddle != null) {
        paddle.disableLaser(); // Set laserEnabled = false
        paddle.setLaserShots(0);
        
        // Visual feedback
        paddle.setPaddleState(PaddleState.NORMAL);
        
        // UI update
        uiManager.hidePowerUpIndicator(PowerUpType.LASER);
        uiManager.hideLaserAmmo();
        
        // Clear any active lasers
        lasers.clear();
    }
}
```

**Gọi**: 
- Sau `LASER_DURATION` milliseconds (thường 15-20 giây)
- Hoặc khi hết đạn (`laserShots == 0`)
- Hoặc khi người chơi mất mạng

---

## Laser Mechanics

### Paddle Laser State

```java
// Trong Paddle class
private boolean laserEnabled = false;
private int laserShots = 0;
private long lastShotTime = 0;
private static final long SHOT_COOLDOWN = 200; // 200ms between shots

public void enableLaser() {
    laserEnabled = true;
    laserShots = Constants.Laser.LASER_SHOTS; // e.g. 30
}

public void disableLaser() {
    laserEnabled = false;
    laserShots = 0;
}

public boolean canShootLaser() {
    if (!laserEnabled) return false;
    if (laserShots <= 0) return false;
    
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastShotTime < SHOT_COOLDOWN) return false;
    
    return true;
}
```

---

### Firing Lasers

```java
// Trong Paddle class
public List<Laser> shootLaser() {
    if (!canShootLaser()) {
        return Collections.emptyList();
    }
    
    List<Laser> lasers = new ArrayList<>();
    
    // Left cannon position
    double leftCannonX = x + width * 0.25 - Constants.Laser.LASER_WIDTH / 2;
    double leftCannonY = y;
    
    // Right cannon position
    double rightCannonX = x + width * 0.75 - Constants.Laser.LASER_WIDTH / 2;
    double rightCannonY = y;
    
    // Create two lasers
    lasers.add(new Laser(leftCannonX, leftCannonY));
    lasers.add(new Laser(rightCannonX, rightCannonY));
    
    // Update state
    laserShots--;
    lastShotTime = System.currentTimeMillis();
    
    // Sound effect
    audioManager.playLaserShootSound();
    
    // Muzzle flash effect
    spawnMuzzleFlash(leftCannonX, leftCannonY);
    spawnMuzzleFlash(rightCannonX, rightCannonY);
    
    return lasers;
}
```

---

### Input Handling

```java
// Trong GameManager hoặc InputHandler
public void handleInput(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        if (paddle.canShootLaser()) {
            List<Laser> newLasers = paddle.shootLaser();
            lasers.addAll(newLasers);
        }
    }
}

// Alternative: Auto-fire mode
public void update() {
    if (paddle.isLaserEnabled() && autoFireEnabled) {
        if (paddle.canShootLaser()) {
            List<Laser> newLasers = paddle.shootLaser();
            lasers.addAll(newLasers);
        }
    }
}
```

---

### Laser Projectile

```java
// Laser class (already documented in Objects.GameEntities)
public class Laser extends MovableObject {
    private boolean active = true;
    
    public Laser(double x, double y) {
        super(x, y, LASER_WIDTH, LASER_HEIGHT);
        // Velocity: straight up
        setVelocity(new Velocity(0, -LASER_SPEED)); // Negative = upward
    }
    
    @Override
    public void update() {
        // Move upward
        Point current = new Point(getX(), getY());
        Point next = getVelocity().applyToPoint(current);
        setPosition(next.getX(), next.getY());
        
        // Deactivate if off-screen
        if (getY() + getHeight() < 0) {
            active = false;
        }
    }
    
    public boolean checkBrickCollision(Brick brick) {
        if (!active || !brick.isAlive()) return false;
        
        if (getBounds().intersects(brick.getBounds())) {
            brick.takeHit(); // Damage brick
            active = false; // Laser destroyed on impact
            return true;
        }
        return false;
    }
}
```

---

## Luồng hoạt động

### Lifecycle của LaserPowerUp Effect

```
1. SPAWN
   ↓
   Brick destroyed
   → Random weighted (15% chance)
   → new LaserPowerUp(x, y)
   → Rơi xuống với animation

2. COLLECTION
   ↓
   Power-up hits paddle
   → collect() called
   → applyEffect(gameManager)
   → gameManager.enableLaser()
   → paddle.enableLaser()
   
   Paddle changes:
     laserEnabled = true
     laserShots = 30 (or config value)
     state = LASER
     sprite = "paddle_laser" (với laser cannons visible)
   
   Visual: Laser cannons appear on paddle
   Sound: "laser_equip.wav" - power-up sound
   UI: "LASER" indicator + ammo counter (30/30)

3. LASER MODE ACTIVE (15-20 seconds OR until ammo depleted)
   ↓
   Player can shoot:
   
   A. Input (SPACE key pressed)
      → paddle.canShootLaser()? Yes
      → paddle.shootLaser()
      
      Creates 2 Laser objects:
        - Left laser at (paddleX + 25%, paddleY)
        - Right laser at (paddleX + 75%, paddleY)
        - Velocity = (0, -LASER_SPEED) upward
      
      lasers.addAll([leftLaser, rightLaser])
      laserShots-- (30 → 29)
      
      Sound: "laser_shoot.wav" - pew pew!
      Effect: Muzzle flash particles
      UI: Ammo counter update (29/30)
   
   B. Laser Update (each frame)
      → laser.update()
      → y -= LASER_SPEED
      
      Collision check với bricks:
        if (laser.checkBrickCollision(brick)) {
            brick.takeHit() → HP decrease
            if (!brick.isAlive()) {
                scoreManager.addPoints(brick.getScoreValue())
            }
            laser.active = false
        }
      
      Out of bounds check:
        if (laser.y < 0) {
            laser.active = false
        }
   
   C. Cleanup
      lasers.removeIf(l -> !l.isActive())

4. EXPIRATION
   ↓
   Case A: Time expired (after LASER_DURATION)
     → removeEffect(gameManager)
     → paddle.disableLaser()
     
   Case B: Ammo depleted (laserShots == 0)
     → Auto-call removeEffect(gameManager)
     → paddle.disableLaser()
   
   Paddle revert:
     laserEnabled = false
     laserShots = 0
     state = NORMAL
     sprite = "paddle_normal"
   
   Visual: Laser cannons disappear
   Sound: "laser_depleted.wav" (optional)
   UI: Indicator fades out
   
   Clear active lasers:
     lasers.clear()

5. EARLY TERMINATION
   ↓
   Player loses life:
     → removeEffect(gameManager)
     → All lasers cleared
     → Paddle reset
```

---

## Visual Representation

### Paddle with Lasers

```java
public void renderPaddleWithLasers(Graphics2D g, Paddle paddle) {
    if (paddle.isLaserEnabled()) {
        // 1. Render paddle sprite with laser cannons
        Sprite sprite = spriteCache.getSprite("paddle_laser");
        g.drawImage(sprite.getImage(), 
            (int) paddle.getX(), (int) paddle.getY());
        
        // 2. Render laser cannon glow effect
        g.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.5f));
        g.setColor(Color.RED);
        
        // Left cannon
        double leftCannonX = paddle.getX() + paddle.getWidth() * 0.25;
        g.fillOval((int) leftCannonX - 3, (int) paddle.getY() - 3, 6, 6);
        
        // Right cannon
        double rightCannonX = paddle.getX() + paddle.getWidth() * 0.75;
        g.fillOval((int) rightCannonX - 3, (int) paddle.getY() - 3, 6, 6);
        
        g.setComposite(AlphaComposite.SrcOver);
    } else {
        // Normal paddle
        renderNormalPaddle(g, paddle);
    }
}
```

---

### Laser Beam Rendering

```java
public void renderLaser(Graphics2D g, Laser laser) {
    if (!laser.isActive()) return;
    
    // 1. Core beam (bright)
    g.setColor(Color.RED);
    g.fillRect(
        (int) laser.getX(), 
        (int) laser.getY(), 
        (int) laser.getWidth(), 
        (int) laser.getHeight()
    );
    
    // 2. Inner glow (white)
    g.setColor(Color.WHITE);
    g.fillRect(
        (int) laser.getX() + 1, 
        (int) laser.getY(), 
        (int) laser.getWidth() - 2, 
        (int) laser.getHeight()
    );
    
    // 3. Outer glow (translucent)
    g.setComposite(AlphaComposite.getInstance(
        AlphaComposite.SRC_OVER, 0.3f));
    g.setColor(Color.RED);
    g.fillRect(
        (int) laser.getX() - 2, 
        (int) laser.getY(), 
        (int) laser.getWidth() + 4, 
        (int) laser.getHeight()
    );
    g.setComposite(AlphaComposite.SrcOver);
}
```

---

### Muzzle Flash Effect

```java
public void spawnMuzzleFlash(double x, double y) {
    // Particle burst
    for (int i = 0; i < 10; i++) {
        double angle = Math.random() * 360;
        double speed = 1 + Math.random() * 2;
        
        Particle p = new Particle(
            x, y,
            angle, speed,
            Color.ORANGE,
            300 // 300ms lifetime
        );
        particles.add(p);
    }
}
```

---

## Chiến thuật sử dụng

### 1. Target Priority

```java
// Ưu tiên bắn gạch khó tiếp cận
if (paddle.isLaserEnabled()) {
    // 1. Gạch ở góc
    // 2. Gạch phía sau GoldBrick
    // 3. Gạch cuối cùng của level
    // 4. SilverBrick (2 HP - laser one-shots)
}
```

---

### 2. Ammo Conservation

```java
// Đừng spam lasers
// Mỗi shot phải có mục đích
if (laserShots < 5) {
    // Low ammo - chỉ bắn khi chắc chắn hit
    if (canHitTarget(targetBrick)) {
        shootLaser();
    }
} else {
    // Plenty ammo - có thể shoot freely
    shootLaser();
}
```

---

### 3. Combo với Ball

```java
// Laser + Ball = double offense
// Ball phá gạch dưới, Laser phá gạch trên
if (ball.getY() > screenHeight / 2 && paddle.isLaserEnabled()) {
    // Ball đang ở nửa dưới
    // Dùng laser để phá gạch nửa trên
    shootAtTopBricks();
}
```

---

### 4. Avoid Friendly Fire

```java
// Laser có thể hit power-ups đang rơi? No (nếu design đúng)
// Nhưng có thể miss target và waste ammo
if (targetBrick != null && !isObstructed(targetBrick)) {
    shootLaser();
}
```

---

## So sánh với các power-up khác

| Power-Up | Offense Type | Control | Ammo/Duration | Difficulty |
|----------|--------------|---------|---------------|------------|
| **LASER** | Active (shoot) | ⭐⭐⭐⭐⭐ Full | 30 shots / 20s | ⭐⭐⭐ Medium |
| Ball | Passive (bounce) | ⭐⭐ Limited | Unlimited | ⭐⭐⭐⭐ Hard |
| DUPLICATE | Passive (more balls) | ⭐ Chaos | Permanent | ⭐⭐⭐ Medium |
| EXPAND | Defense | ⭐⭐⭐ Easier catch | 15-20s | ⭐⭐ Easy |
| CATCH | Control | ⭐⭐⭐⭐⭐ Full | 15-20s | ⭐⭐ Easy |

**LASER Characteristics**:
- **Active Offense**: Require player input
- **Precision**: Direct shots at specific bricks
- **Limited Resource**: Finite ammo
- **High Skill Ceiling**: Good players use very effectively
- **Fun Factor**: Satisfying pew-pew gameplay

---

## Best Practices

### 1. Cooldown Management
```java
// ✅ Đúng - cooldown giữa shots
private static final long SHOT_COOLDOWN = 200; // 200ms

public boolean canShootLaser() {
    long timeSinceLastShot = System.currentTimeMillis() - lastShotTime;
    return laserEnabled && laserShots > 0 && timeSinceLastShot >= SHOT_COOLDOWN;
}

// ❌ Sai - no cooldown (spam shots instantly)
public boolean canShootLaser() {
    return laserEnabled && laserShots > 0;
}
```

---

### 2. Two Laser Projectiles
```java
// ✅ Đúng - bắn 2 lasers từ 2 cannons
public List<Laser> shootLaser() {
    List<Laser> lasers = new ArrayList<>();
    lasers.add(new Laser(leftCannonX, cannonY));
    lasers.add(new Laser(rightCannonX, cannonY));
    return lasers;
}

// ❌ Sai - chỉ 1 laser từ center (không giống original)
public List<Laser> shootLaser() {
    return List.of(new Laser(centerX, cannonY));
}
```

---

### 3. Laser-Brick Interaction
```java
// ✅ Đúng - laser destroyed on brick hit
public boolean checkBrickCollision(Brick brick) {
    if (getBounds().intersects(brick.getBounds())) {
        brick.takeHit();
        this.active = false; // Laser disappears
        return true;
    }
    return false;
}

// ❌ Sai - laser pierces through (too OP)
public boolean checkBrickCollision(Brick brick) {
    if (getBounds().intersects(brick.getBounds())) {
        brick.takeHit();
        // Laser continues → can hit multiple bricks (imbalanced)
        return true;
    }
    return false;
}
```

---

### 4. Auto-Disable on Ammo Depletion
```java
// ✅ Đúng - auto-disable khi hết đạn
public void update() {
    if (paddle.isLaserEnabled() && paddle.getLaserShots() <= 0) {
        removeEffect(gameManager); // Auto-disable
    }
    
    // Also check time expiration
    if (System.currentTimeMillis() > laserExpiryTime) {
        removeEffect(gameManager);
    }
}

// ❌ Sai - laser mode remains even with 0 ammo
```

---

### 5. UI Ammo Display
```java
// ✅ Đúng - show remaining shots
public void renderLaserUI(Graphics2D g, Paddle paddle) {
    if (paddle.isLaserEnabled()) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("LASER: " + paddle.getLaserShots(), 10, 50);
        
        // Visual ammo bar
        int maxWidth = 100;
        int currentWidth = (int) (maxWidth * 
            (paddle.getLaserShots() / (double) Constants.Laser.LASER_SHOTS));
        
        g.setColor(Color.RED);
        g.fillRect(10, 60, currentWidth, 10);
        g.setColor(Color.GRAY);
        g.drawRect(10, 60, maxWidth, 10);
    }
}
```

---

## Sound Effects

```java
// Equip laser
audioManager.playLaserEquipSound(); // "laser_on.wav" - power-up charging

// Shoot laser
audioManager.playLaserShootSound(); // "laser_shoot.wav" - pew!

// Laser hit brick
audioManager.playLaserHitSound(); // "laser_hit.wav" - explosion

// Ammo depleted
audioManager.playAmmoDepletedSound(); // "empty_click.wav"

// Laser disabled
audioManager.playLaserOffSound(); // "laser_off.wav" - power-down
```

---

## Kết luận

`LaserPowerUp` là power-up offensive và engaging nhất:

- **Active Gameplay**: Require player input (skill-based)
- **Precision**: Allows targeting specific bricks
- **Resource Management**: Limited ammo adds strategy
- **High Satisfaction**: Pew-pew shooting is fun!
- **Versatile**: Useful in many situations
- **Iconic**: Memorable mechanic từ original Arkanoid

LaserPowerUp transform Arkanoid từ purely reactive game (bouncing ball) thành proactive game (shooting bricks). Nó adds một layer của strategy và skill expression, cho phép good players showcase their aim và decision-making. Đây là perfect example của power-up design: simple concept (shoot lasers), deep gameplay (ammo management, targeting, timing).

**Fun Fact**: Laser power-up (L) trong Arkanoid original (1986) là một trong những features innovative nhất của game. Nó inspired countless clones và trở thành staple của breakout genre. Việc combine passive defense (paddle) với active offense (lasers) created một gameplay loop vô cùng addictive.
