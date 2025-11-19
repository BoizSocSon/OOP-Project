# SlowBallPowerUp Class

## Tổng quan
`SlowBallPowerUp` là power-up "Làm chậm bóng" - một trong những power-up defensive hữu ích nhất trong Arkanoid. Khi nhặt được, TẤT CẢ các quả bóng đang chơi sẽ di chuyển CHẬM HƠN (thường 50-70% tốc độ ban đầu) trong một khoảng thời gian nhất định. Bóng chậm hơn = dễ theo dõi hơn, dễ bắt hơn, nhiều thời gian suy nghĩ hơn. Đây là beginner-friendly power-up và rất hữu ích trong các tình huống chaotic (nhiều bóng, bóng nhanh).

## Vị trí
- **Package**: `Objects.PowerUps`
- **File**: `src/Objects/PowerUps/SlowBallPowerUp.java`
- **Kế thừa**: `PowerUp` (abstract)
- **Implements**: `GameObject` (gián tiếp qua PowerUp)

## Mục đích
SlowBallPowerUp:
- Giảm tốc độ tất cả các bóng
- Giúp người chơi dễ tracking và reacting
- Hữu ích cho beginners
- Timed effect (có thời gian hết hạn)
- Safe power-up (không có risk)
- Combo tốt với Duplicate/multi-ball

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
SlowBallPowerUp (Concrete Class)
    │
    └── PowerUpType.SLOW (Timed effect, 15% spawn rate)
```

---

## Constructor

### `SlowBallPowerUp(double x, double y)`

**Mô tả**: Khởi tạo Slow Ball power-up với vị trí ban đầu.

**Tham số**:
- `x` - Tọa độ X (thường là vị trí brick vừa phá)
- `y` - Tọa độ Y

**Hành vi**:
```java
super(x, y, PowerUpType.SLOW);
```
- Gọi constructor của PowerUp
- Type = `PowerUpType.SLOW`
- Animation = "powerup_slow_0.png", "powerup_slow_1.png", ...
- Velocity = (0, POWERUP_FALL_SPEED)
- active = true, collected = false

**Ví dụ**:
```java
// Spawn khi brick destroyed
if (shouldSpawnPowerUp() && random.nextDouble() < 0.15) {
    double x = brick.getX() + brick.getWidth() / 2;
    double y = brick.getY();
    SlowBallPowerUp slowPowerUp = new SlowBallPowerUp(x, y);
    powerUps.add(slowPowerUp);
}
```

---

## Phương thức

### 1. `void applyEffect(GameManager gameManager)` (Override)

**Mô tả**: Giảm tốc độ của tất cả các quả bóng trong game.

**Tham số**: `gameManager` - GameManager để access ball list.

**Hành vi**:
1. Null safety check:
   ```java
   if (gameManager == null) {
       System.err.println("SlowBallPowerUp: GameManager is null");
       return;
   }
   ```

2. Slow all balls:
   ```java
   gameManager.slowBalls(Constants.PowerUps.SLOW_MULTIPLIER);
   ```

3. Log message:
   ```java
   System.out.println("SlowBallPowerUp: Balls slowed to " +
       (Constants.PowerUps.SLOW_MULTIPLIER * 100) + "% speed for " +
       Constants.PowerUps.SLOW_DURATION / 1000.0 + " seconds");
   ```
   Example output: `"Balls slowed to 60% speed for 15.0 seconds"`

**Effect trong GameManager**:
```java
// GameManager.slowBalls(double multiplier)
public void slowBalls(double multiplier) {
    for (Ball ball : balls) {
        if (ball.isAlive()) {
            // Save original speed if not already slowed
            if (!ball.isSlowed()) {
                ball.saveOriginalSpeed();
            }
            
            // Apply slow multiplier
            ball.multiplySpeed(multiplier); // 0.6 = 60% speed
            ball.setSlowed(true);
        }
    }
    
    // Visual feedback
    uiManager.showPowerUpIndicator(PowerUpType.SLOW);
    
    // Sound effect
    audioManager.playSlowMotionSound();
    
    // Particle effect on all balls
    for (Ball ball : balls) {
        particleManager.spawnSlowTrail(ball);
    }
}
```

**Gọi**: Khi power-up collision với Paddle.

---

### 2. `void removeEffect(GameManager gameManager)` (Override)

**Mô tả**: Khôi phục tốc độ ban đầu của tất cả các bóng.

**Tham số**: `gameManager` - GameManager để access ball list.

**Hành vi**:
1. Null safety check:
   ```java
   if (gameManager == null) {
       System.err.println("SlowBallPowerUp: GameManager is null");
       return;
   }
   ```

2. Restore ball speed:
   ```java
   gameManager.restoreBallSpeed();
   ```

3. Log message:
   ```java
   System.out.println("SlowBallPowerUp: Ball speed restored (slow expired)");
   ```

**Effect trong GameManager**:
```java
// GameManager.restoreBallSpeed()
public void restoreBallSpeed() {
    for (Ball ball : balls) {
        if (ball.isAlive() && ball.isSlowed()) {
            // Restore original speed
            ball.restoreOriginalSpeed();
            ball.setSlowed(false);
        }
    }
    
    // Visual feedback
    uiManager.hidePowerUpIndicator(PowerUpType.SLOW);
    
    // Sound effect
    audioManager.playSpeedRestoreSound();
}
```

**Gọi**: 
- Sau `SLOW_DURATION` milliseconds (thường 15-20 giây)
- Hoặc khi người chơi mất mạng

---

## Ball Speed Mechanics

### Speed Management in Ball

```java
// Trong Ball class
private double speed;
private double originalSpeed;
private boolean slowed = false;

public void saveOriginalSpeed() {
    if (!slowed) { // Chỉ save nếu chưa bị slow
        originalSpeed = speed;
    }
}

public void multiplySpeed(double multiplier) {
    // Multiply current speed
    speed *= multiplier;
    
    // Update velocity magnitude
    Velocity vel = getVelocity();
    double currentMag = vel.getSpeed();
    double newMag = currentMag * multiplier;
    
    // Keep direction, change magnitude
    Velocity newVel = vel.normalize().multiply(newMag);
    setVelocity(newVel);
}

public void restoreOriginalSpeed() {
    if (slowed) {
        speed = originalSpeed;
        
        // Restore velocity magnitude
        Velocity vel = getVelocity();
        Velocity newVel = vel.normalize().multiply(originalSpeed);
        setVelocity(newVel);
    }
}

public void setSlowed(boolean slowed) {
    this.slowed = slowed;
}

public boolean isSlowed() {
    return slowed;
}
```

---

### Velocity Adjustment

```java
// Giảm tốc độ nhưng GIỮ NGUYÊN hướng
public void slowDown(double multiplier) {
    Velocity currentVel = getVelocity();
    
    // Get current direction
    double angle = Math.atan2(currentVel.getDy(), currentVel.getDx());
    
    // Get current speed
    double currentSpeed = currentVel.getSpeed();
    
    // Calculate new speed
    double newSpeed = currentSpeed * multiplier; // e.g. * 0.6
    
    // Create new velocity with same direction, slower speed
    double newDx = newSpeed * Math.cos(angle);
    double newDy = newSpeed * Math.sin(angle);
    
    setVelocity(new Velocity(newDx, newDy));
}
```

---

## Luồng hoạt động

### Lifecycle của SlowBallPowerUp Effect

```
1. SPAWN
   ↓
   Brick destroyed
   → Random weighted (15% chance)
   → new SlowBallPowerUp(x, y)
   → Rơi xuống với animation (blue/clock icon)

2. COLLECTION
   ↓
   Power-up hits paddle
   → collect() called
   → applyEffect(gameManager)
   → gameManager.slowBalls(0.6) // 60% speed
   
   For each ball:
     Original speed: 300 pixels/sec
     → saveOriginalSpeed() → originalSpeed = 300
     → multiplySpeed(0.6)
     → New speed: 180 pixels/sec (60%)
     → slowed = true
   
   Visual: 
     - Blue tint on balls
     - Trail effect (motion blur)
     - Slow-motion particles
     
   Sound: "slow_motion.wav" - whoosh sound
   UI: "SLOW" indicator + timer bar

3. SLOWED STATE (15-20 seconds)
   ↓
   All balls move at 60% speed:
   
   Ball movement:
     - Easier to track visually
     - More time to react
     - Easier to predict trajectory
     - Reduced difficulty
   
   Gameplay effects:
     - Easier to position paddle
     - More forgiving timing
     - Better control with Catch
     - Combo well with multiple balls
   
   Visual indicators:
     - Blue/cyan tint on balls
     - Slower animation
     - Motion trail effect
     - UI timer showing remaining duration

4. EXPIRATION (After duration)
   ↓
   Timer expires
   → removeEffect(gameManager)
   → gameManager.restoreBallSpeed()
   
   For each ball:
     → restoreOriginalSpeed()
     → speed = originalSpeed (300)
     → Update velocity to match
     → slowed = false
   
   Visual: 
     - Tint fades away
     - Speed-up particle burst
     - Flash effect
     
   Sound: "speed_restore.wav"
   UI: "SLOW" indicator fades
   Warning: Visual cue 3s before expiration

5. EARLY TERMINATION
   ↓
   Player loses life:
     → removeEffect(gameManager)
     → All balls reset to normal speed
   
   New balls spawned:
     → Start at normal speed
     → Not affected by expired slow effect
```

---

## Visual Representation

### Slowed Ball Appearance

```java
public void renderSlowedBall(Graphics2D g, Ball ball) {
    if (ball.isSlowed()) {
        // 1. Motion blur trail
        int trailLength = 5;
        for (int i = 0; i < trailLength; i++) {
            double prevX = ball.getX() - ball.getVelocity().getDx() * i * 0.3;
            double prevY = ball.getY() - ball.getVelocity().getDy() * i * 0.3;
            
            float alpha = 0.3f - (i * 0.05f);
            g.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, alpha));
            
            g.setColor(Color.CYAN);
            g.fillOval((int) prevX, (int) prevY, 
                (int) ball.getWidth(), (int) ball.getHeight());
        }
        g.setComposite(AlphaComposite.SrcOver);
        
        // 2. Blue tint overlay
        g.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.3f));
        g.setColor(new Color(0, 100, 255)); // Blue
        g.fillOval((int) ball.getX(), (int) ball.getY(),
            (int) ball.getWidth(), (int) ball.getHeight());
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    // 3. Normal ball rendering
    ball.render(g);
}
```

---

### Speed Indicator

```java
public void renderSpeedIndicator(Graphics2D g) {
    if (ballsAreSlowed()) {
        // Speed meter
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("SPEED: 60%", screenWidth - 120, 30);
        
        // Visual bar
        int barWidth = 100;
        int currentWidth = (int) (barWidth * 0.6); // 60%
        
        g.setColor(Color.CYAN);
        g.fillRect(screenWidth - 120, 40, currentWidth, 10);
        
        g.setColor(Color.GRAY);
        g.drawRect(screenWidth - 120, 40, barWidth, 10);
    }
}
```

---

## Chiến thuật sử dụng

### 1. Multi-Ball Management

```java
// Slow + multiple balls = much easier
if (ballCount >= 3 && hasSlowPowerUp) {
    // Collect slow to make tracking easier
    // 3+ balls at normal speed = chaos
    // 3+ balls at 60% speed = manageable
    collectSlowPowerUp();
}
```

---

### 2. Combo với Catch

```java
// SLOW + CATCH = ultimate control
if (paddle.isCatchEnabled() && ballsAreSlowed()) {
    // Slow balls → easier to catch
    // Catch → position precisely
    // Release → still slow → accurate aim
    
    // Perfect combo for precision shots
}
```

---

### 3. Beginner Assistance

```java
// Slow power-up is best for beginners
if (playerSkillLevel == BEGINNER) {
    // Prioritize SLOW over offensive power-ups
    // Reduces difficulty significantly
    // Builds confidence
}
```

---

### 4. Fast Ball Rescue

```java
// Bóng đang rất nhanh (sau nhiều paddle hits)
if (ball.getSpeed() > BALL_SPEED * 1.5) {
    // Ball too fast to track comfortably
    if (hasSlowPowerUp) {
        // Collect to bring speed back to manageable
        collectSlowPowerUp();
        // Fast ball at 60% = normal speed
    }
}
```

---

## So sánh với các power-up khác

| Power-Up | Difficulty Change | Type | Visual | Duration |
|----------|-------------------|------|--------|----------|
| **SLOW** | ⬇️⬇️ Much easier | Timed | Blue tint | 15-20s |
| EXPAND | ⬇️ Easier | Timed | Wider paddle | 15-20s |
| CATCH | ⬇️ Easier (control) | Timed | Sticky | 15-20s |
| LASER | ➡️ Same | Timed | Cannons | 15-20s |
| DUPLICATE | ⬆️ Harder (chaos) | Instant | More balls | Permanent |

**SLOW Characteristics**:
- **Huge Difficulty Reduction**: Most impactful defensive power-up
- **Universal**: Helps in all situations
- **Visual**: Clear blue/slow-motion effect
- **Combo Friendly**: Works well with all other power-ups
- **Beginner-Friendly**: Dramatically improves experience

---

## Best Practices

### 1. Speed Multiplier Balance
```java
// ✅ Đúng - reasonable slow (60%)
public static final double SLOW_MULTIPLIER = 0.6; // 60% speed

// ❌ Sai - quá chậm (boring)
public static final double SLOW_MULTIPLIER = 0.3; // 30% speed - too slow

// ❌ Sai - không đủ chậm (không có impact)
public static final double SLOW_MULTIPLIER = 0.9; // 90% speed - barely noticeable
```

---

### 2. Save Original Speed
```java
// ✅ Đúng - save speed trước khi slow
public void slowBalls(double multiplier) {
    for (Ball ball : balls) {
        if (!ball.isSlowed()) {
            ball.saveOriginalSpeed(); // Save first!
        }
        ball.multiplySpeed(multiplier);
        ball.setSlowed(true);
    }
}

// ❌ Sai - không save (không restore được)
public void slowBalls(double multiplier) {
    for (Ball ball : balls) {
        ball.multiplySpeed(multiplier); // Lost original speed!
        ball.setSlowed(true);
    }
}
```

---

### 3. Handle New Balls During Slow
```java
// ✅ Đúng - new balls cũng bị slow nếu effect còn active
public void spawnBall(double x, double y) {
    Ball newBall = new Ball(x, y);
    balls.add(newBall);
    
    // Check if slow effect is active
    if (isSlowEffectActive()) {
        newBall.saveOriginalSpeed();
        newBall.multiplySpeed(SLOW_MULTIPLIER);
        newBall.setSlowed(true);
    }
}

// ❌ Sai - new balls at normal speed (inconsistent)
public void spawnBall(double x, double y) {
    Ball newBall = new Ball(x, y);
    balls.add(newBall);
    // New ball không bị slow → confusing
}
```

---

### 4. Expiration Warning
```java
// ✅ Đúng - warn player before effect expires
public void update() {
    long remaining = slowExpiryTime - System.currentTimeMillis();
    
    if (remaining < 3000 && remaining > 0) {
        // Visual warning
        if (System.currentTimeMillis() % 500 < 250) {
            flashSlowIndicator();
        }
        
        // Audio warning at 3s mark
        if (!warningPlayed && remaining < 3000) {
            audioManager.playWarningSound();
            warningPlayed = true;
        }
    }
}
```

---

### 5. Stack Handling
```java
// ✅ Đúng - refresh duration, không stack multipliers
public void onSlowPowerUpCollected() {
    if (isSlowEffectActive()) {
        // Refresh timer
        slowExpiryTime = System.currentTimeMillis() + SLOW_DURATION;
        showMessage("SLOW TIME EXTENDED!");
    } else {
        // First slow
        applySlowEffect();
        slowExpiryTime = System.currentTimeMillis() + SLOW_DURATION;
    }
}

// ❌ Sai - stack multipliers (balls stop moving)
public void onSlowPowerUpCollected() {
    applySlowEffect(); // 0.6 * 0.6 = 0.36 = 36% speed (too slow)
}
```

---

## Edge Cases

### 1. Ball Speed Increase During Slow

```java
// Ball hits paddle → speed increases normally
// Nhưng vẫn có slow multiplier applied
if (ballsAreSlowed() && ball.hitPaddle()) {
    // Normal speed increase
    ball.increaseSpeed(1.05); // 5% increase
    
    // Slow multiplier still applies
    // If ball was at 180 (60% of 300)
    // After hit: 189 (60% of 315)
    
    // Effect: Slow effect maintained even with speed increases
}
```

---

### 2. Duplicate During Slow

```java
// Collect duplicate power-up while slow is active
if (isSlowEffectActive() && collectDuplicatePowerUp()) {
    // Clone balls with current slowed speed
    List<Ball> clones = duplicateBalls();
    
    for (Ball clone : clones) {
        clone.setSlowed(true);
        clone.saveOriginalSpeed(); // Save for when slow expires
    }
}
```

---

### 3. Slow Expiration Mid-Flight

```java
// Slow expires khi ball đang mid-air
public void onSlowExpired() {
    // Smooth transition
    for (Ball ball : balls) {
        // Gradually restore speed (optional)
        animateSpeedRestore(ball, 500); // 500ms transition
        
        // Or instant (simpler)
        ball.restoreOriginalSpeed();
    }
}
```

---

## Performance Considerations

### Particle Effects

```java
// Slow motion particles for each ball
// Can be performance-intensive with many balls
public void updateSlowParticles() {
    if (ballsAreSlowed()) {
        for (Ball ball : balls) {
            // Limit particle spawn rate
            if (frameCount % 3 == 0) { // Every 3 frames
                spawnSlowParticle(ball);
            }
        }
    }
}
```

---

## Sound Design

```java
// Slow motion sound
audioManager.playSlowMotionSound(); 
// "slow_motion.wav" - whoosh/time-slow effect

// During slow effect - lower pitch audio (optional)
audioManager.setPitchMultiplier(0.8); // Deeper sounds

// Speed restore
audioManager.playSpeedRestoreSound();
// "speed_up.wav" - acceleration sound

// Reset audio pitch
audioManager.setPitchMultiplier(1.0);
```

---

## Kết luận

`SlowBallPowerUp` là defensive power-up cực kỳ hiệu quả:

- **Huge Impact**: Dramatically giảm độ khó
- **Beginner-Friendly**: Perfect cho new players
- **Universal**: Useful trong mọi situation
- **Visual Clarity**: Clear blue slow-motion effect
- **Safe**: Không có downside hay risk
- **Combo Potential**: Enhances all other power-ups

SlowBallPowerUp là ví dụ tốt về difficulty modulation trong game design. Bằng việc đơn giản giảm ball speed, power-up này dramatically improves player experience without changing core mechanics. Nó cho phép beginners enjoy game và skilled players appreciate the control. Simple concept (slow down), huge impact (much easier gameplay).

**Fun Fact**: Slow-motion mechanics trong games thường được associate với "bullet time" từ The Matrix (1999) và Max Payne (2001). Trong Arkanoid, slow ball power-up có effect tương tự - giving players more time to react và tạo feeling of control. Blue visual tint là common convention cho time-slowdown effects trong nhiều games.

