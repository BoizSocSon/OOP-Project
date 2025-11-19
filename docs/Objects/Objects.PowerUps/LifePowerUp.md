# LifePowerUp Class

## Tổng quan
`LifePowerUp` là power-up "Mạng sống" - power-up QUÝ GIÁ NHẤT trong Arkanoid. Khi nhặt được, người chơi sẽ được thêm 1 MẠNG SỐNG (extra life), tăng cơ hội chơi tiếp khi mắc lỗi. Đây là instant effect (tức thời) và permanent (vĩnh viễn - không có expiration). Life power-up thường có spawn rate RẤT THẤP (5%) vì quá powerful, và việc collect được nó là một sự kiện đáng mừng trong game.

## Vị trí
- **Package**: `Objects.PowerUps`
- **File**: `src/Objects/PowerUps/LifePowerUp.java`
- **Kế thừa**: `PowerUp` (abstract)
- **Implements**: `GameObject` (gián tiếp qua PowerUp)

## Mục đích
LifePowerUp:
- Thêm 1 mạng sống cho người chơi
- Tăng forgiveness của game
- Reward cho skilled play
- Instant effect (apply ngay lập tức)
- Permanent effect (không expire)
- Spawn rate thấp (rare power-up)

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
LifePowerUp (Concrete Class)
    │
    └── PowerUpType.LIFE (Instant effect, 5% spawn rate)
```

---

## Constructor

### `LifePowerUp(double x, double y)`

**Mô tả**: Khởi tạo Life power-up với vị trí ban đầu.

**Tham số**:
- `x` - Tọa độ X (thường là vị trí brick vừa phá)
- `y` - Tọa độ Y

**Hành vi**:
```java
super(x, y, PowerUpType.LIFE);
```
- Gọi constructor của PowerUp
- Type = `PowerUpType.LIFE`
- Animation = "powerup_life_0.png", "powerup_life_1.png", ...
- Velocity = (0, POWERUP_FALL_SPEED)
- active = true, collected = false

**Ví dụ**:
```java
// Spawn khi brick destroyed (rare)
if (shouldSpawnPowerUp() && random.nextDouble() < 0.05) { // 5% only!
    PowerUpType type = PowerUpType.randomWeighted();
    if (type == PowerUpType.LIFE) {
        double x = brick.getX() + brick.getWidth() / 2;
        double y = brick.getY();
        LifePowerUp lifePowerUp = new LifePowerUp(x, y);
        powerUps.add(lifePowerUp);
    }
}
```

---

## Phương thức

### 1. `void applyEffect(GameManager gameManager)` (Override)

**Mô tả**: Thêm 1 mạng sống cho người chơi.

**Tham số**: `gameManager` - GameManager để access lives system.

**Hành vi**:
1. Null safety check:
   ```java
   if (gameManager == null) {
       System.err.println("LifePowerUp: GameManager is null");
       return;
   }
   ```

2. Get current lives (for logging):
   ```java
   int livesBeforeAdd = gameManager.getLives();
   ```

3. Add life:
   ```java
   gameManager.addLife();
   ```

4. Get new lives count:
   ```java
   int livesAfter = gameManager.getLives();
   ```

5. Check if life was actually added:
   ```java
   if (livesAfter > livesBeforeAdd) {
       System.out.println("LifePowerUp: Life added! Lives: " +
           livesBeforeAdd + " → " + livesAfter);
   } else {
       System.out.println("LifePowerUp: Max lives reached (" +
           Constants.GameRules.MAX_LIVES + "), no effect");
   }
   ```

**Effect trong GameManager**:
```java
// GameManager.addLife()
public void addLife() {
    if (lives < Constants.GameRules.MAX_LIVES) {
        lives++;
        
        // UI update
        uiManager.updateLivesDisplay(lives);
        
        // Sound effect
        audioManager.play1UpSound(); // "1up.wav" - iconic sound!
        
        // Visual effect
        particleManager.spawnLifeGainEffect();
        
        // Achievement check
        if (lives == MAX_LIVES) {
            achievementManager.unlock("MAX_LIVES_ACHIEVED");
        }
    } else {
        // Already at max - no effect
        audioManager.playMaxLivesSound(); // Different sound
    }
}
```

**Result**:
- Lives: 2 → 3
- Lives: 3 → 4
- Lives: 4 → 5 (if MAX_LIVES = 5)
- Lives: 5 → 5 (at max, no increase)

**Gọi**: Ngay khi power-up collision với Paddle.

---

### 2. `void removeEffect(GameManager gameManager)` (Override)

**Mô tả**: Loại bỏ effect của Life power-up.

**Hành vi**: **EMPTY METHOD** - không làm gì cả!

```java
@Override
public void removeEffect(GameManager gameManager) {
    // Hiệu ứng LIFE là tức thời và vĩnh viễn.
    // Không có expiration logic.
}
```

**Lý do**:
- LIFE là instant effect (apply ngay lập tức)
- Effect là permanent (mạng sống không "expire")
- Không có duration (không hết hạn)
- Không cần cleanup logic

**Lưu ý**: Mạng sống chỉ giảm khi:
- Ball rơi ra khỏi màn hình (lost ball)
- Player makes mistake

---

## Lives System

### Lives Management

```java
// Trong GameManager
private int lives;
private static final int STARTING_LIVES = 3;
private static final int MAX_LIVES = 5; // Hoặc 9 trong một số versions

public void initGame() {
    lives = STARTING_LIVES;
}

public void addLife() {
    if (lives < MAX_LIVES) {
        lives++;
    }
}

public void loseLife() {
    lives--;
    if (lives <= 0) {
        gameOver();
    } else {
        resetLevel(); // Reset balls, paddle, etc.
    }
}

public int getLives() {
    return lives;
}
```

---

### Lives Display

```java
// UI rendering
public void renderLivesDisplay(Graphics2D g) {
    // Text display
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 20));
    g.drawString("LIVES: " + lives, 10, 30);
    
    // Icon display (visual)
    Sprite ballIcon = spriteCache.getSprite("life_icon");
    for (int i = 0; i < lives; i++) {
        int x = 10 + i * (ballIcon.getWidth() + 5);
        int y = 40;
        g.drawImage(ballIcon.getImage(), x, y);
    }
}
```

---

## Luồng hoạt động

### Lifecycle của LifePowerUp Effect

```
1. SPAWN (RARE)
   ↓
   Brick destroyed
   → Random weighted (only 5% chance!)
   → new LifePowerUp(x, y)
   → Rơi xuống với special animation
   
   Visual: Gold/heart icon
   Rarity: Very rare - players excited to see it!

2. COLLECTION
   ↓
   Power-up hits paddle
   → collect() called
   → applyEffect(gameManager)
   → gameManager.addLife()
   
   Check max lives:
     if (lives < MAX_LIVES) {
         lives++ (e.g. 3 → 4)
         Effect: Success
     } else {
         lives unchanged (at max)
         Effect: No change
     }
   
   Visual: 
     - Bright flash effect
     - "+1 LIFE" text floating up
     - Heart particle burst
     
   Sound: "1up.wav" - iconic happy sound!
   
   UI: Lives counter updates (3 → 4)

3. INSTANT EFFECT (No duration)
   ↓
   Effect applied → complete
   No tracking needed
   No expiration timer
   removeEffect() is never called

4. PERMANENT STATE
   ↓
   Extra life persists:
   
   Player now has 4 lives instead of 3
   
   Lives decrease naturally through:
     - Ball lost (life--)
     - Death (life-- → respawn)
     - Game Over (lives = 0)
   
   Lives remain until used:
     4 lives → lose ball → 3 lives
     3 lives → lose ball → 2 lives
     2 lives → lose ball → 1 life
     1 life → lose ball → 0 lives → GAME OVER

5. NO CLEANUP
   ↓
   No cleanup logic needed
   Effect is permanent addition to lives counter
   
   Lives persist:
     - Through level transitions
     - Until used by player mistakes
     - Or until game ends
```

---

## Max Lives Cap

### Why Cap Lives?

```java
// Prevent infinite lives accumulation
public static final int MAX_LIVES = 5; // Common cap

// Reasons for cap:
// 1. Balance: Too many lives = too easy
// 2. UI: Limited space to display lives
// 3. Design: Game should still be challenging
// 4. Achievement: Max lives can be an achievement
```

---

### Handling Max Lives

```java
// ✅ Đúng - check before adding
public void addLife() {
    if (lives < MAX_LIVES) {
        lives++;
        playSuccessSound();
    } else {
        // At max - different feedback
        playMaxLivesSound();
        showMessage("Max Lives Reached!");
    }
}

// ❌ Sai - unlimited lives
public void addLife() {
    lives++; // Can grow to 100+ (imbalanced)
}
```

---

## Visual & Audio Feedback

### 1UP Effect

```java
public void renderLifeGainEffect(Graphics2D g, double x, double y) {
    // 1. Bright flash
    long flashDuration = 500; // 500ms
    if (System.currentTimeMillis() - effectStartTime < flashDuration) {
        g.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.5f));
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, screenWidth, screenHeight);
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    // 2. "+1 LIFE" floating text
    g.setColor(Color.GREEN);
    g.setFont(new Font("Arial", Font.BOLD, 32));
    
    // Float upward
    double floatY = y - (System.currentTimeMillis() - effectStartTime) * 0.1;
    g.drawString("+1 LIFE", (int) x, (int) floatY);
    
    // 3. Heart particle burst
    for (int i = 0; i < 30; i++) {
        double angle = (360.0 / 30) * i;
        double dist = 50 + (System.currentTimeMillis() - effectStartTime) * 0.05;
        
        double px = x + dist * Math.cos(Math.toRadians(angle));
        double py = y + dist * Math.sin(Math.toRadians(angle));
        
        g.setColor(new Color(255, 0, 0, 200));
        g.fillOval((int) px - 3, (int) py - 3, 6, 6);
    }
}
```

---

### 1UP Sound

```java
// Iconic 1UP sound (similar to Mario/classic arcade games)
audioManager.play1UpSound(); // "1up.wav"

// Characteristics:
// - Rising pitch melody
// - Happy, rewarding tone
// - Instantly recognizable
// - Volume slightly louder than other SFX

// Alternative sounds for max lives
if (lives == MAX_LIVES) {
    audioManager.playMaxLivesSound(); // "max_lives.wav"
}
```

---

## Chiến thuật sử dụng

### 1. Risk vs Reward

```java
// Life power-up rơi xuống
if (seesLifePowerUp()) {
    // Quyết định:
    // A. Rủi ro catch nó (có thể miss ball)
    // B. An toàn ignore nó (focus on ball)
    
    if (ballCount >= 2 || paddle.isCatchEnabled()) {
        // Safe to go for life - có backup
        movePaddleTowards(lifePowerUp);
    } else {
        // Risky - chỉ 1 ball, ưu tiên ball hơn
        if (ball.getY() < screenHeight * 0.5) {
            // Ball ở trên, có time - go for life!
            movePaddleTowards(lifePowerUp);
        } else {
            // Ball đang rơi - prioritize ball
            trackBall();
        }
    }
}
```

---

### 2. Value Assessment

```java
// Life power-up là valuable nhất
PowerUpValue priority:
1. LIFE (nếu < MAX_LIVES) - TOP PRIORITY
2. WARP (skip level)
3. LASER (offensive)
4. CATCH (control)
5. EXPAND (safety)
6. SLOW (easier)
7. DUPLICATE (chaotic)

if (lives < MAX_LIVES && seesLifePowerUp()) {
    // Sacrifice everything else cho life
    ignorePowerUps();
    focusOnLife();
}
```

---

### 3. Aggressive Play After Collect

```java
// Sau khi có extra life, có thể play aggressive hơn
if (recentlyGainedLife()) {
    // Có buffer - có thể take risks
    playAggressively();
    
    // Try for hard-to-reach bricks
    attemptDifficultShots();
    
    // Go for other risky power-ups
    collectDuplicatePowerUp(); // Worth the chaos now
}
```

---

## So sánh với các power-up khác

| Power-Up | Value | Rarity | Effect | Type | Risk |
|----------|-------|--------|--------|------|------|
| **LIFE** | ⭐⭐⭐⭐⭐ Highest | 5% | +1 life | Instant | Worth ANY risk |
| WARP | ⭐⭐⭐⭐ Very High | 1% | Skip level | Instant | Worth high risk |
| LASER | ⭐⭐⭐ High | 15% | Shoot bricks | Timed | Low risk |
| CATCH | ⭐⭐⭐ High | 15% | Control | Timed | No risk |
| EXPAND | ⭐⭐⭐ High | 15% | Wider paddle | Timed | No risk |
| SLOW | ⭐⭐⭐ High | 15% | Slow balls | Timed | No risk |
| DUPLICATE | ⭐⭐ Medium | 12% | 2x balls | Instant | Medium risk |

**LIFE Characteristics**:
- **Highest Value**: Most impactful power-up
- **Rarest**: Only 5% spawn rate
- **No Downside**: Pure benefit
- **Permanent**: Doesn't expire
- **Excitement Factor**: Players celebrate collecting it

---

## Best Practices

### 1. Spawn Rate Balance
```java
// ✅ Đúng - very low spawn rate
PowerUpType.LIFE("powerup_life", 0.05); // 5% chance

// ❌ Sai - quá cao (game too easy)
PowerUpType.LIFE("powerup_life", 0.20); // 20% chance
```

---

### 2. Max Lives Cap
```java
// ✅ Đúng - reasonable cap
public static final int MAX_LIVES = 5; // Or 9

public void addLife() {
    if (lives < MAX_LIVES) {
        lives++;
    } else {
        // Show "MAX LIVES" message
        displayMaxLivesMessage();
    }
}

// ❌ Sai - no cap (can accumulate 50+ lives)
public void addLife() {
    lives++; // Unlimited
}
```

---

### 3. Visual Distinction
```java
// ✅ Đúng - life power-up looks special
public void renderPowerUp(PowerUp powerUp) {
    if (powerUp.getType() == PowerUpType.LIFE) {
        // Special rendering - glow effect
        renderGlowEffect(powerUp);
        
        // Particle trail
        renderParticleTrail(powerUp);
        
        // Different color (gold/green)
        renderWithGoldenTint(powerUp);
    }
    
    // Standard rendering
    powerUp.getAnimation().render(g, powerUp.getX(), powerUp.getY());
}
```

---

### 4. Audio Feedback
```java
// ✅ Đúng - special sound for life
if (powerUp.getType() == PowerUpType.LIFE) {
    audioManager.play1UpSound(); // Iconic!
} else {
    audioManager.playPowerUpSound(); // Generic
}
```

---

### 5. No Effect When At Max
```java
// ✅ Đúng - clear feedback when at max
public void applyEffect(GameManager gm) {
    int before = gm.getLives();
    gm.addLife();
    int after = gm.getLives();
    
    if (after == before) {
        // No change - show message
        uiManager.showMessage("MAX LIVES!", 2000);
        audioManager.playInfoSound();
    } else {
        // Success!
        uiManager.showMessage("+1 LIFE!", 2000);
        audioManager.play1UpSound();
    }
}
```

---

## Edge Cases

### 1. Collecting at Max Lives

```java
// What happens if player at MAX_LIVES collects life?
if (lives >= MAX_LIVES && collectLifePowerUp()) {
    // Option A: No effect, show message
    showMessage("MAX LIVES REACHED");
    
    // Option B: Convert to points
    scoreManager.addBonusPoints(10000);
    showMessage("+10000 BONUS!");
    
    // Option C: Temporary invincibility
    activateInvincibility(5000); // 5s invincible
}
```

---

### 2. Multiple Life Power-Ups

```java
// Hai life power-ups rơi cùng lúc
if (twoLifePowerUpsOnScreen()) {
    // Can collect both
    // Each adds 1 life (up to MAX)
    
    collect(lifePowerUp1); // 3 → 4
    collect(lifePowerUp2); // 4 → 5
    
    // If at max after first
    collect(lifePowerUp1); // 4 → 5
    collect(lifePowerUp2); // 5 → 5 (no effect)
}
```

---

### 3. Life Gain During Death

```java
// Ball rơi ĐÚNG LÚC collect life power-up
if (ball.isLost() && lifePowerUp.isCollected()) {
    // Race condition!
    
    // Solution: Process power-up collection BEFORE checking ball lost
    updatePowerUps(); // Process collections first
    checkBallLost();  // Then check ball state
    
    // Result: Life gained, then lost → net zero
    // But technically correct (gained then used)
}
```

---

## Achievements & Milestones

```java
// Achievements related to lives
public class AchievementManager {
    public void checkLifeAchievements() {
        // Collect first life power-up
        if (lifePowerUpsCollected == 1) {
            unlock("FIRST_LIFE");
        }
        
        // Reach max lives
        if (lives == MAX_LIVES) {
            unlock("MAX_LIVES");
        }
        
        // Complete level without losing life
        if (levelComplete && livesAtLevelStart == livesAtLevelEnd) {
            unlock("PERFECT_LEVEL");
        }
        
        // Complete game without losing life
        if (gameComplete && lives == STARTING_LIVES) {
            unlock("PERFECT_GAME");
        }
    }
}
```

---

## Statistics Tracking

```java
// Track life power-up stats
public class Statistics {
    private int lifePowerUpsSpawned;
    private int lifePowerUpsCollected;
    private int lifePowerUpsMissed;
    private int maxLivesReached;
    
    public void onLifePowerUpSpawned() {
        lifePowerUpsSpawned++;
    }
    
    public void onLifePowerUpCollected() {
        lifePowerUpsCollected++;
    }
    
    public void onLifePowerUpMissed() {
        lifePowerUpsMissed++;
    }
    
    public double getCollectionRate() {
        if (lifePowerUpsSpawned == 0) return 0.0;
        return (double) lifePowerUpsCollected / lifePowerUpsSpawned;
    }
}
```

---

## Kết luận

`LifePowerUp` là power-up quan trọng và valuable nhất:

- **Highest Value**: Most impactful cho survival
- **Rarest**: 5% spawn rate (precious)
- **Instant Effect**: Apply immediately
- **Permanent**: Doesn't expire until used
- **Universal Appeal**: Everyone wants extra lives
- **Clean Implementation**: Simple code, powerful effect

LifePowerUp là embodiment của core game design principle: "meaningful rewards". Việc collect được extra life là một sự kiện đáng nhớ trong gameplay, tạo excitement và relief cho người chơi. Rarity (5%) làm cho nó precious, và permanent nature làm cho effect meaningful. Simple implementation nhưng huge psychological impact.

**Fun Fact**: "1UP" terminology xuất phát từ arcade games thời kỳ đầu, nơi "1UP" nghĩa là "Player 1 Up" (player 1 gets extra turn). Sound effect "1UP" trong Mario Bros (1983) trở thành iconic và được reference trong vô số games sau này, including Arkanoid. Life power-up trong Arkanoid với icon "P" (màu xanh lá) là một trong những rare spawns được người chơi săn đón nhất.

