# ExpandPaddlePowerUp Class

## Tổng quan
`ExpandPaddlePowerUp` là power-up "Mở rộng thanh đỡ" - một trong những power-up phổ biến và hữu ích nhất trong Arkanoid. Khi nhặt được, Paddle sẽ tăng chiều rộng lên (thường là 1.5x đến 2x kích thước ban đầu) trong một khoảng thời gian nhất định. Paddle rộng hơn giúp người chơi dễ dàng bắt bóng hơn, giảm risk và tạo feeling of safety. Đây là beginner-friendly power-up và thường được ưu tiên trong các tình huống khó.

## Vị trí
- **Package**: `Objects.PowerUps`
- **File**: `src/Objects/PowerUps/ExpandPaddlePowerUp.java`
- **Kế thừa**: `PowerUp` (abstract)
- **Implements**: `GameObject` (gián tiếp qua PowerUp)

## Mục đích
ExpandPaddlePowerUp:
- Tăng chiều rộng của Paddle
- Giảm độ khó của game (easier to catch ball)
- Hữu ích cho beginners
- Timed effect (có thời gian hết hạn)
- Visual change rõ ràng
- Safe power-up (không có risk)

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
ExpandPaddlePowerUp (Concrete Class)
    │
    └── PowerUpType.EXPAND (Timed effect)
```

---

## Constructor

### `ExpandPaddlePowerUp(double x, double y)`

**Mô tả**: Khởi tạo Expand power-up với vị trí ban đầu.

**Tham số**:
- `x` - Tọa độ X (thường là vị trí brick vừa phá)
- `y` - Tọa độ Y

**Hành vi**:
```java
super(x, y, PowerUpType.EXPAND);
```
- Gọi constructor của PowerUp
- Type = `PowerUpType.EXPAND`
- Animation = "powerup_expand_0.png", "powerup_expand_1.png", ...
- Velocity = (0, POWERUP_FALL_SPEED)
- active = true, collected = false

**Ví dụ**:
```java
// Spawn khi brick destroyed
if (shouldSpawnPowerUp() && random.nextDouble() < 0.15) {
    double x = brick.getX() + brick.getWidth() / 2;
    double y = brick.getY();
    ExpandPaddlePowerUp expandPowerUp = new ExpandPaddlePowerUp(x, y);
    powerUps.add(expandPowerUp);
}
```

---

## Phương thức

### 1. `void applyEffect(GameManager gameManager)` (Override)

**Mô tả**: Mở rộng Paddle lên một kích thước lớn hơn.

**Tham số**: `gameManager` - GameManager để access Paddle.

**Hành vi**:
1. Null safety check:
   ```java
   if (gameManager == null) {
       System.err.println("ExpandPaddlePowerUp: GameManager is null");
       return;
   }
   ```

2. Expand paddle:
   ```java
   gameManager.expandPaddle();
   ```

3. Log message:
   ```java
   System.out.println("ExpandPaddlePowerUp: Paddle expanded to " +
       (Constants.PowerUps.EXPAND_MULTIPLIER * 100) + "% for " +
       Constants.PowerUps.EXPAND_DURATION / 1000.0 + " seconds");
   ```
   Example output: `"Paddle expanded to 150% for 15.0 seconds"`

**Effect trong GameManager**:
```java
// GameManager.expandPaddle()
public void expandPaddle() {
    if (paddle != null) {
        paddle.expand(); // Tăng width
        
        // Visual feedback
        paddle.setPaddleState(PaddleState.EXPANDED);
        
        // UI update
        uiManager.showPowerUpIndicator(PowerUpType.EXPAND);
        
        // Sound effect
        audioManager.playExpandSound();
    }
}
```

**Gọi**: Khi power-up collision với Paddle.

---

### 2. `void removeEffect(GameManager gameManager)` (Override)

**Mô tả**: Khôi phục kích thước Paddle về bình thường sau khi hết thời gian.

**Tham số**: `gameManager` - GameManager để access Paddle.

**Hành vi**:
1. Null safety check:
   ```java
   if (gameManager == null) {
       System.err.println("ExpandPaddlePowerUp: GameManager is null");
       return;
   }
   ```

2. Revert paddle size:
   ```java
   gameManager.revertPaddleSize();
   ```

3. Log message:
   ```java
   System.out.println("ExpandPaddlePowerUp: Paddle reverted to normal size (expired)");
   ```

**Effect trong GameManager**:
```java
// GameManager.revertPaddleSize()
public void revertPaddleSize() {
    if (paddle != null) {
        paddle.shrinkToNormal(); // Reset về NORMAL_WIDTH
        
        // Visual feedback
        paddle.setPaddleState(PaddleState.NORMAL);
        
        // UI update
        uiManager.hidePowerUpIndicator(PowerUpType.EXPAND);
        
        // Sound effect (optional)
        audioManager.playShrinkSound();
    }
}
```

**Gọi**: 
- Sau `EXPAND_DURATION` milliseconds (thường 15-20 giây)
- Hoặc khi người chơi mất mạng (reset state)

---

## Paddle Expansion Mechanics

### Paddle Size Management

```java
// Trong Paddle class
public class Paddle {
    private static final double NORMAL_WIDTH = 100;
    private static final double EXPANDED_WIDTH = NORMAL_WIDTH * 1.5; // 150
    
    private double width;
    private PaddleState state;
    
    public void expand() {
        if (state != PaddleState.EXPANDED) {
            // Save current position center
            double centerX = x + width / 2;
            
            // Update width
            this.width = EXPANDED_WIDTH;
            
            // Reposition to keep center aligned
            this.x = centerX - width / 2;
            
            // Update state
            this.state = PaddleState.EXPANDED;
            
            // Update sprite
            updateSprite();
        }
    }
    
    public void shrinkToNormal() {
        if (state == PaddleState.EXPANDED) {
            // Save current position center
            double centerX = x + width / 2;
            
            // Reset width
            this.width = NORMAL_WIDTH;
            
            // Reposition to keep center aligned
            this.x = centerX - width / 2;
            
            // Update state
            this.state = PaddleState.NORMAL;
            
            // Update sprite
            updateSprite();
        }
    }
}
```

---

### Smooth Transition Animation

```java
// Animated expansion thay vì instant
public void expandSmooth() {
    // Target width
    double targetWidth = EXPANDED_WIDTH;
    
    // Animation parameters
    int animationFrames = 10; // 10 frames @ 60fps = ~167ms
    double widthIncrement = (targetWidth - width) / animationFrames;
    
    // Start animation
    new Thread(() -> {
        for (int i = 0; i < animationFrames; i++) {
            double centerX = x + width / 2;
            width += widthIncrement;
            x = centerX - width / 2;
            
            try {
                Thread.sleep(16); // ~60fps
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // Ensure exact final size
        width = targetWidth;
        state = PaddleState.EXPANDED;
    }).start();
}
```

---

### Boundary Constraints

```java
// Đảm bảo expanded paddle không ra khỏi screen
public void expand() {
    double centerX = x + width / 2;
    this.width = EXPANDED_WIDTH;
    this.x = centerX - width / 2;
    
    // Clamp to screen bounds
    if (x < 0) {
        x = 0;
    } else if (x + width > screenWidth) {
        x = screenWidth - width;
    }
    
    state = PaddleState.EXPANDED;
}
```

---

## Luồng hoạt động

### Lifecycle của ExpandPaddlePowerUp Effect

```
1. SPAWN
   ↓
   Brick destroyed
   → Random weighted (15% chance)
   → new ExpandPaddlePowerUp(x, y)
   → Rơi xuống với animation

2. COLLECTION
   ↓
   Power-up hits paddle
   → collect() called
   → applyEffect(gameManager)
   → gameManager.expandPaddle()
   → paddle.expand()
   
   Paddle expansion:
     width: 100 → 150 (1.5x)
     x position adjusted to keep center aligned
     state: NORMAL → EXPANDED
     sprite: "paddle_normal" → "paddle_expanded"
   
   Visual: Smooth growth animation
   Sound: "expand.wav" - stretching sound
   UI: "EXPAND" indicator + timer bar

3. EXPANDED STATE (15-20 seconds)
   ↓
   Paddle is wider:
     - Easier to catch ball
     - Larger collision box
     - Different sprite
     
   Gameplay benefits:
     - Reduced difficulty
     - More forgiving
     - Confidence boost
     
   Visual indicators:
     - Different paddle sprite (wider)
     - Glowing outline
     - UI timer showing remaining duration

4. EXPIRATION (After duration)
   ↓
   Timer expires
   → removeEffect(gameManager)
   → gameManager.revertPaddleSize()
   → paddle.shrinkToNormal()
   
   Paddle shrink:
     width: 150 → 100
     x position adjusted
     state: EXPANDED → NORMAL
     sprite: "paddle_expanded" → "paddle_normal"
   
   Visual: Smooth shrink animation
   Sound: "shrink.wav" (optional)
   UI: "EXPAND" indicator fades out
   Warning: Visual cue before expiration (e.g. flashing)

5. EARLY TERMINATION
   ↓
   Player loses life:
     → removeEffect(gameManager)
     → Paddle reset to normal
   
   New level:
     → All power-up effects cleared
     → Paddle reset
```

---

## Visual Representation

### Paddle States

```java
public enum PaddleState {
    NORMAL,     // 100 width, "paddle_normal.png"
    EXPANDED,   // 150 width, "paddle_expanded.png"
    LASER,      // Normal width với laser cannons
    CATCH       // Normal width với sticky surface
}

// Render based on state
public void renderPaddle(Graphics2D g, Paddle paddle) {
    String spriteName = switch (paddle.getState()) {
        case NORMAL -> "paddle_normal";
        case EXPANDED -> "paddle_expanded";
        case LASER -> "paddle_laser";
        case CATCH -> "paddle_catch";
    };
    
    Sprite sprite = spriteCache.getSprite(spriteName);
    g.drawImage(sprite.getImage(), 
        (int) paddle.getX(), 
        (int) paddle.getY());
}
```

---

### Size Comparison Visual

```
NORMAL PADDLE:
[====================] (100 pixels)

EXPANDED PADDLE:
[==============================] (150 pixels)

DOUBLE EXPANDED (if stacking allowed):
[==========================================] (200 pixels)
```

---

### Expansion Animation Effect

```java
public void renderExpansionEffect(Graphics2D g, Paddle paddle) {
    if (paddle.isExpanding()) {
        // Particle burst từ edges
        double leftEdge = paddle.getX();
        double rightEdge = paddle.getX() + paddle.getWidth();
        double y = paddle.getY() + paddle.getHeight() / 2;
        
        // Left particles
        for (int i = 0; i < 5; i++) {
            Particle p = new Particle(leftEdge, y, 
                180 + (Math.random() * 60 - 30), // ±30° from left
                2, Color.GREEN, 500);
            particles.add(p);
        }
        
        // Right particles
        for (int i = 0; i < 5; i++) {
            Particle p = new Particle(rightEdge, y, 
                0 + (Math.random() * 60 - 30), // ±30° from right
                2, Color.GREEN, 500);
            particles.add(p);
        }
    }
}
```

---

## Chiến thuật sử dụng

### 1. Beginner Safety

```java
// Expand là best power-up cho beginners
if (playerSkillLevel == BEGINNER) {
    // Prioritize collecting EXPAND
    // Skip risky power-ups (WARP, DUPLICATE)
}
```

---

### 2. Combo với Catch

```java
// EXPAND + CATCH = Ultimate control
if (paddle.isExpanded() && paddle.isCatchEnabled()) {
    // Wide paddle + catch ability
    // Nearly impossible to miss ball
    // Perfect cho precision shots
}
```

---

### 3. Multi-Ball Management

```java
// Expanded paddle helpful với multiple balls
if (ballCount >= 3 && hasExpandPowerUp) {
    // Easier to track and catch multiple balls
    // Reduced stress
}
```

---

### 4. Avoid Over-Reliance

```java
// Đừng quá phụ thuộc vào expanded state
// Khi expire, bạn vẫn phải adapt về normal size
if (paddle.isExpanded() && remainingTime < 3000) { // 3s warning
    // Prepare for shrink
    // Start playing more carefully
}
```

---

## So sánh với các power-up khác

| Power-Up | Effect | Difficulty Change | Type | Duration |
|----------|--------|-------------------|------|----------|
| **EXPAND** | Wider paddle | ⬇️ Easier | Timed | 15-20s |
| CATCH | Catch ball | ⬇️ Easier (control) | Timed | 15-20s |
| SLOW | Slow balls | ⬇️ Easier (reaction) | Timed | 15-20s |
| LASER | Shoot bricks | ➡️ Same | Timed | 15-20s |
| DUPLICATE | 2x balls | ⬆️ Harder/Faster | Instant | Permanent |

**EXPAND Characteristics**:
- **Safe**: Không có risk
- **Effective**: Dramatically giảm độ khó
- **Visual**: Clear visual feedback
- **Beginner-Friendly**: Dễ understand và use
- **Stackable**: Có thể refresh duration

---

## Best Practices

### 1. Center Alignment
```java
// ✅ Đúng - keep paddle centered khi expand/shrink
public void expand() {
    double centerX = x + width / 2; // Save center
    this.width = EXPANDED_WIDTH;
    this.x = centerX - width / 2;  // Reposition from center
}

// ❌ Sai - expand từ left edge (confusing)
public void expand() {
    this.width = EXPANDED_WIDTH; // x stays same, paddle "jumps" right
}
```

---

### 2. Boundary Checking
```java
// ✅ Đúng - clamp to screen after expansion
public void expand() {
    double centerX = x + width / 2;
    this.width = EXPANDED_WIDTH;
    this.x = centerX - width / 2;
    
    // Clamp
    if (x < 0) x = 0;
    if (x + width > screenWidth) x = screenWidth - width;
}

// ❌ Sai - paddle can go off-screen
public void expand() {
    double centerX = x + width / 2;
    this.width = EXPANDED_WIDTH;
    this.x = centerX - width / 2;
    // No boundary check - can render partially off-screen
}
```

---

### 3. Effect Stacking
```java
// ✅ Đúng - refresh duration, không stack size
public void onExpandCollected() {
    if (paddle.isExpanded()) {
        // Refresh timer instead of expanding more
        expandExpiryTime = System.currentTimeMillis() + EXPAND_DURATION;
    } else {
        // First expand
        paddle.expand();
        expandExpiryTime = System.currentTimeMillis() + EXPAND_DURATION;
    }
}

// ❌ Sai - stack expansions (paddle too wide)
public void onExpandCollected() {
    paddle.expand(); // 100→150→225→337... (exponential growth)
}
```

---

### 4. Expiration Warning
```java
// ✅ Đúng - warn player before effect expires
public void update() {
    long remaining = expandExpiryTime - System.currentTimeMillis();
    
    if (remaining < 3000 && remaining > 0) { // Last 3 seconds
        // Visual warning
        if (System.currentTimeMillis() % 500 < 250) { // Blink every 500ms
            paddle.setBlinking(true);
        }
        
        // Audio warning (at 3s mark)
        if (!warningPlayed && remaining < 3000) {
            audioManager.playWarningSound();
            warningPlayed = true;
        }
    }
}
```

---

### 5. Sprite Management
```java
// ✅ Đúng - different sprites for different sizes
public void updateSprite() {
    if (state == PaddleState.EXPANDED) {
        currentSprite = "paddle_expanded"; // Wider sprite
    } else {
        currentSprite = "paddle_normal";
    }
}

// ❌ Sai - stretch normal sprite (looks bad)
public void renderPaddle(Graphics g) {
    Sprite sprite = spriteCache.getSprite("paddle_normal");
    // Stretch to expanded width - ugly scaling
    g.drawImage(sprite.getImage(), 
        (int) x, (int) y, (int) width, (int) height);
}
```

---

## Edge Cases

### 1. Screen Edge Expansion

```java
// Paddle ở edge khi expand
public void expand() {
    double centerX = x + width / 2;
    this.width = EXPANDED_WIDTH;
    this.x = centerX - width / 2;
    
    // Left edge
    if (x < 0) {
        x = 0; // Align to left wall
    }
    
    // Right edge
    if (x + width > screenWidth) {
        x = screenWidth - width; // Align to right wall
    }
}
```

---

### 2. Multiple Power-Up Interactions

```java
// EXPAND + LASER simultaneously
if (paddle.isExpanded() && paddle.hasLaser()) {
    // Render expanded paddle với laser cannons
    // Laser spawn points scale với paddle width
    double leftCannonX = paddle.getX() + paddle.getWidth() * 0.25;
    double rightCannonX = paddle.getX() + paddle.getWidth() * 0.75;
}
```

---

### 3. Collision Detection Update

```java
// Expanded paddle → larger collision box
public Rectangle getBounds() {
    return new Rectangle(
        new Point(x, y),
        width, // Automatically uses current width (expanded or normal)
        height
    );
}

// Ball collision detection works automatically
// Vì getBounds() returns current size
```

---

## Sound Effects

```java
// Expansion sound
audioManager.playExpandSound(); // "expand.wav" - stretching/growing sound

// Shrink sound (optional)
audioManager.playShrinkSound(); // "shrink.wav" - compressing sound

// Warning sound (before expiration)
audioManager.playWarningSound(); // "warning.wav" - beep

// Collection sound
audioManager.playPowerUpCollectSound(); // "powerup.wav"
```

---

## Kết luận

`ExpandPaddlePowerUp` là power-up đơn giản nhưng vô cùng effective:

- **Beginner-Friendly**: Dễ hiểu, dễ sử dụng
- **Low Risk**: Không có downside
- **High Impact**: Dramatically giảm độ khó
- **Clear Feedback**: Visual change rõ ràng
- **Universal Appeal**: Hữu ích cho mọi skill levels
- **Clean Implementation**: Straightforward expand/shrink logic

ExpandPaddlePowerUp là ví dụ tốt về game design principle: "Easy to understand, hard to master". Effect đơn giản (paddle rộng hơn) nhưng impact lớn (easier gameplay). Không có complex mechanics, nhưng greatly improves player experience. Perfect example của "less is more" trong game design.

**Fun Fact**: Trong Arkanoid gốc, Expand (E) power-up có icon là một mũi tên pointing outward (←→), symbolizing expansion. Đây là một trong những power-ups được yêu thích nhất vì nó gives players breathing room trong những tình huống tense.
