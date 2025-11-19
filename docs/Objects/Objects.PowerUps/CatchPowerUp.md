# CatchPowerUp Class

## Tổng quan
`CatchPowerUp` là power-up "Bắt bóng" - một trong những power-up hữu ích nhất trong Arkanoid. Khi nhặt được, Paddle sẽ có khả năng BẮT và GIỮ quả bóng khi chạm vào, thay vì làm bóng nảy ngay. Người chơi có thể giữ bóng, di chuyển Paddle đến vị trí mong muốn, rồi nhả bóng ra với góc và hướng được tính toán. Power-up này cho phép người chơi kiểm soát hoàn toàn trajectory của bóng, rất hữu ích khi cần phá những viên gạch khó tiếp cận.

## Vị trí
- **Package**: `Objects.PowerUps`
- **File**: `src/Objects/PowerUps/CatchPowerUp.java`
- **Kế thừa**: `PowerUp` (abstract)
- **Implements**: `GameObject` (gián tiếp qua PowerUp)

## Mục đích
CatchPowerUp:
- Cho phép Paddle bắt và giữ bóng
- Tạo strategic pause trong gameplay
- Giúp người chơi aim chính xác hơn
- Hữu ích khi có ít bóng còn lại
- Tạo feeling of control cho người chơi
- Duration-based effect (có thời gian hết hạn)

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
CatchPowerUp (Concrete Class)
    │
    └── PowerUpType.CATCH
```

---

## Constructor

### `CatchPowerUp(double x, double y)`

**Mô tả**: Khởi tạo Catch power-up với vị trí ban đầu.

**Tham số**:
- `x` - Tọa độ X (thường là vị trí brick vừa phá)
- `y` - Tọa độ Y

**Hành vi**:
```java
super(x, y, PowerUpType.CATCH);
```
- Gọi constructor của PowerUp
- Type = `PowerUpType.CATCH`
- Animation = "powerup_catch_0.png", "powerup_catch_1.png", ...
- Velocity = (0, POWERUP_FALL_SPEED)
- active = true, collected = false

**Ví dụ**:
```java
// Spawn khi brick destroyed
if (shouldSpawnPowerUp() && random.nextDouble() < 0.15) {
    double x = brick.getX() + brick.getWidth() / 2;
    double y = brick.getY();
    CatchPowerUp catchPowerUp = new CatchPowerUp(x, y);
    powerUps.add(catchPowerUp);
}
```

---

## Phương thức

### 1. `void applyEffect(GameManager gameManager)` (Override)

**Mô tả**: Kích hoạt chế độ bắt bóng (Catch mode) trên Paddle.

**Tham số**: `gameManager` - GameManager để access Paddle và game state.

**Hành vi**:
1. Null safety check:
   ```java
   if (gameManager == null) {
       System.err.println("CatchPowerUp: GameManager is null");
       return;
   }
   ```

2. Enable catch mode:
   ```java
   gameManager.enableCatchMode();
   ```
   
3. Log message:
   ```java
   System.out.println("CatchPowerUp: Catch mode enabled for " +
       Constants.PowerUps.CATCH_DURATION / 1000.0 + " seconds");
   ```

**Effect trong GameManager**:
```java
// GameManager.enableCatchMode()
public void enableCatchMode() {
    if (paddle != null) {
        paddle.enableCatch(); // Set paddle.catchEnabled = true
        
        // Visual feedback
        paddle.setPaddleState(PaddleState.CATCH);
        
        // UI update
        uiManager.showPowerUpIndicator(PowerUpType.CATCH);
    }
}
```

**Gọi**: Khi power-up collision với Paddle.

---

### 2. `void removeEffect(GameManager gameManager)` (Override)

**Mô tả**: Vô hiệu hóa chế độ bắt bóng sau khi hết thời gian.

**Tham số**: `gameManager` - GameManager để access Paddle.

**Hành vi**:
1. Null safety check:
   ```java
   if (gameManager == null) {
       System.err.println("CatchPowerUp: GameManager is null");
       return;
   }
   ```

2. Disable catch mode:
   ```java
   gameManager.disableCatchMode();
   ```

3. Log message:
   ```java
   System.out.println("CatchPowerUp: Catch mode disabled (expired)");
   ```

**Effect trong GameManager**:
```java
// GameManager.disableCatchMode()
public void disableCatchMode() {
    if (paddle != null) {
        paddle.disableCatch(); // Set paddle.catchEnabled = false
        
        // Release any caught ball
        if (paddle.hasCaughtBall()) {
            paddle.releaseBall();
        }
        
        // Revert visual
        paddle.setPaddleState(PaddleState.NORMAL);
        
        // UI update
        uiManager.hidePowerUpIndicator(PowerUpType.CATCH);
    }
}
```

**Gọi**: 
- Sau `CATCH_DURATION` milliseconds (thường 15-20 giây)
- Hoặc khi người chơi mất mạng

---

## Cơ chế Catch Mode

### Paddle.catchEnabled

```java
// Trong Paddle class
private boolean catchEnabled = false;
private Ball caughtBall = null;

public void enableCatch() {
    catchEnabled = true;
}

public void disableCatch() {
    catchEnabled = false;
    if (caughtBall != null) {
        releaseBall();
    }
}
```

---

### Ball Collision với Catch Mode

```java
// Trong CollisionManager.checkBallPaddleCollision()
if (ball.checkCollisionWithRect(paddle.getBounds())) {
    if (paddle.isCatchEnabled() && !paddle.hasCaughtBall()) {
        // CATCH the ball instead of bouncing
        paddle.catchBall(ball);
        
        // Stop ball movement
        ball.setVelocity(new Velocity(0, 0));
        
        // Attach ball to paddle position
        ball.attachToPaddle(paddle);
        
        // Play catch sound
        audioManager.playCatchSound();
        
    } else {
        // Normal bounce
        ball.handlePaddleCollision(paddle);
    }
}
```

---

### Holding & Releasing Ball

```java
// Trong Paddle class
public void catchBall(Ball ball) {
    this.caughtBall = ball;
    ball.setCaught(true);
}

public void releaseBall() {
    if (caughtBall != null) {
        // Calculate launch angle based on paddle movement
        double launchAngle = calculateLaunchAngle();
        
        // Set ball velocity
        double speed = Constants.Ball.BALL_SPEED;
        double dx = speed * Math.cos(Math.toRadians(launchAngle));
        double dy = -speed * Math.sin(Math.toRadians(launchAngle));
        
        caughtBall.setVelocity(new Velocity(dx, dy));
        caughtBall.setCaught(false);
        
        // Play release sound
        audioManager.playBallReleaseSound();
        
        caughtBall = null;
    }
}

// Người chơi nhấn phím để nhả bóng
public void handleInput(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SPACE && hasCaughtBall()) {
        releaseBall();
    }
}
```

---

## Luồng hoạt động

### Lifecycle của CatchPowerUp Effect

```
1. SPAWN
   ↓
   Brick destroyed
   → Random chance (15%)
   → new CatchPowerUp(x, y)
   → Rơi xuống với animation

2. COLLECTION
   ↓
   Power-up hits paddle
   → collect() called
   → applyEffect(gameManager)
   → gameManager.enableCatchMode()
   → paddle.catchEnabled = true
   → paddle.state = CATCH
   
   Visual: Paddle có visual indicator (e.g. glowing, sticky surface)
   UI: Icon hiển thị "CATCH" + timer bar

3. CATCH MODE ACTIVE (15-20 seconds)
   ↓
   Paddle có thể bắt bóng:
   
   A. Ball hits paddle
      → paddle.catchBall(ball)
      → ball.velocity = (0, 0)
      → ball.attachToPaddle(paddle)
      → ball.x = paddle.x + offset
      → ball.y = paddle.y - ball.height
      
   B. Paddle moves (ball follows)
      → ball.x = paddle.x + paddle.width / 2 - ball.width / 2
      → ball.y = paddle.y - ball.height
      
   C. Player releases (SPACE key)
      → paddle.releaseBall()
      → Calculate angle based on paddle velocity
      → ball.velocity = new Velocity(dx, dy)
      → ball.detachFromPaddle()

4. EXPIRATION (After duration)
   ↓
   Timer expires
   → removeEffect(gameManager)
   → gameManager.disableCatchMode()
   → paddle.catchEnabled = false
   
   If ball was caught:
     → Auto-release ball upward
     → ball.velocity = (0, -BALL_SPEED)
   
   Visual: Paddle returns to normal
   UI: "CATCH" indicator fades out

5. EARLY TERMINATION
   ↓
   Player loses life:
     → removeEffect(gameManager)
     → Ball và effects reset
   
   New level:
     → All power-up effects cleared
```

---

## Chiến thuật sử dụng

### 1. Precision Targeting

```java
// Người chơi có thể:
// 1. Catch ball
// 2. Di chuyển paddle đến vị trí mong muốn
// 3. Release để bóng bay đến mục tiêu chính xác

// Example: Phá viên gạch cuối cùng ở góc
if (paddle.hasCaughtBall()) {
    // Di chuyển paddle đến dưới gạch target
    double targetX = lastBrick.getX() + lastBrick.getWidth() / 2;
    movePaddleTo(targetX);
    
    // Release khi đúng vị trí
    paddle.releaseBall();
}
```

---

### 2. Multi-Ball Control

```java
// Với nhiều bóng, catch mode giúp:
// - Giữ một bóng để đảm bảo không lose all
// - Release bóng ở timing tối ưu
// - Tạo strategic spacing giữa các bóng

if (ballCount > 1 && paddle.isCatchEnabled()) {
    // Catch một bóng để làm "safety net"
    // Các bóng khác tiếp tục bounce
}
```

---

### 3. Last Ball Rescue

```java
// Khi chỉ còn 1 bóng và đang rơi xuống
if (ballCount == 1 && ball.getY() > screenHeight * 0.8) {
    if (paddle.isCatchEnabled()) {
        // Catch để save ball
        // Sau đó reposition và release an toàn
    }
}
```

---

## Visual Feedback

### Paddle Appearance

```java
public void renderPaddle(Graphics2D g, Paddle paddle) {
    if (paddle.isCatchEnabled()) {
        // 1. Render normal paddle sprite
        Sprite sprite = spriteCache.getSprite(paddle.getCurrentSprite());
        g.drawImage(sprite.getImage(), 
            (int) paddle.getX(), (int) paddle.getY());
        
        // 2. Add "sticky" effect overlay
        g.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.3f));
        g.setColor(new Color(255, 215, 0)); // Gold color
        g.fillRect((int) paddle.getX(), (int) paddle.getY(), 
            (int) paddle.getWidth(), (int) paddle.getHeight());
        g.setComposite(AlphaComposite.SrcOver);
        
        // 3. Pulsing border
        if (pulseEffect) {
            g.setColor(Color.YELLOW);
            g.setStroke(new BasicStroke(2));
            g.drawRect((int) paddle.getX() - 2, (int) paddle.getY() - 2,
                (int) paddle.getWidth() + 4, (int) paddle.getHeight() + 4);
        }
    }
}
```

---

### Ball Attachment Visual

```java
public void renderCaughtBall(Graphics2D g, Ball ball, Paddle paddle) {
    if (ball.isCaught()) {
        // 1. Render ball
        ball.render(g);
        
        // 2. Draw "connection" effect between ball and paddle
        g.setColor(new Color(255, 255, 0, 100)); // Transparent yellow
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, 
            BasicStroke.JOIN_ROUND));
        
        int ballCenterX = (int) (ball.getX() + ball.getWidth() / 2);
        int ballCenterY = (int) (ball.getY() + ball.getHeight() / 2);
        int paddleCenterX = (int) (paddle.getX() + paddle.getWidth() / 2);
        int paddleTopY = (int) paddle.getY();
        
        g.drawLine(ballCenterX, ballCenterY, paddleCenterX, paddleTopY);
    }
}
```

---

## So sánh với các power-up khác

| Power-Up | Effect | Duration | Type | Control Level |
|----------|--------|----------|------|---------------|
| **CATCH** | Bắt bóng | 15-20s | Timed | ⭐⭐⭐⭐⭐ Full control |
| EXPAND | Mở rộng paddle | 15-20s | Timed | ⭐⭐⭐ Easier catch |
| LASER | Bắn laser | 15-20s | Timed | ⭐⭐⭐⭐ Active offense |
| SLOW | Làm chậm bóng | 15-20s | Timed | ⭐⭐⭐⭐ Easier tracking |
| DUPLICATE | Nhân đôi bóng | Instant | Instant | ⭐⭐ More balls |

**Catch Power-Up Advantages**:
- Highest level of control
- Strategic positioning
- Precision targeting
- Risk reduction
- Time to plan next move

---

## Best Practices

### 1. Auto-Release on Expiration
```java
// ✅ Đúng - auto release ball khi effect expires
@Override
public void removeEffect(GameManager gameManager) {
    if (gameManager.getPaddle().hasCaughtBall()) {
        // Release upward để an toàn
        gameManager.getPaddle().releaseBall();
    }
    gameManager.disableCatchMode();
}

// ❌ Sai - không release ball (ball stuck forever)
@Override
public void removeEffect(GameManager gameManager) {
    gameManager.disableCatchMode(); // Ball vẫn caught!
}
```

---

### 2. Multiple Balls Handling
```java
// ✅ Đúng - chỉ catch một bóng tại một thời điểm
public void catchBall(Ball ball) {
    if (caughtBall == null) { // Chỉ catch nếu chưa có bóng caught
        this.caughtBall = ball;
        ball.setCaught(true);
    }
}

// ❌ Sai - catch nhiều bóng (confusing)
public void catchBall(Ball ball) {
    this.caughtBall = ball; // Overwrite previous caught ball
    ball.setCaught(true);
}
```

---

### 3. Visual Indicator
```java
// ✅ Đúng - clear visual feedback
public void render(Graphics2D g) {
    if (paddle.isCatchEnabled()) {
        // Draw indicator
        drawCatchModeIndicator(g);
        
        // Draw timer
        drawEffectTimer(g, remainingTime);
    }
}
```

---

### 4. Input Handling
```java
// ✅ Đúng - allow release only when ball is caught
public void handleInput(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        if (paddle.hasCaughtBall()) {
            paddle.releaseBall();
        }
    }
}

// ❌ Sai - không check hasCaughtBall
public void handleInput(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        paddle.releaseBall(); // NullPointerException nếu không có ball
    }
}
```

---

### 5. Release Angle Calculation
```java
// ✅ Đúng - angle based on paddle velocity
public void releaseBall() {
    double paddleVelocity = paddle.getVelocity().getDx();
    
    // Angle: -90° (straight up) to -45° or -135° (diagonal)
    double baseAngle = 90; // Straight up
    double angleOffset = paddleVelocity * 2; // Adjust based on movement
    
    double launchAngle = baseAngle + angleOffset;
    launchAngle = Math.max(45, Math.min(135, launchAngle)); // Clamp
    
    ball.setVelocity(velocityFromAngle(launchAngle, BALL_SPEED));
}
```

---

## Sound Effects

```java
// Catch sound
audioManager.playCatchSound(); // "stick.wav" - sticky sound

// Release sound
audioManager.playReleaseSound(); // "release.wav" - launch sound

// Catch mode enabled
audioManager.playPowerUpCollectSound(); // "powerup.wav"

// Catch mode expired
audioManager.playEffectExpireSound(); // "expire.wav"
```

---

## Kết luận

`CatchPowerUp` là một trong những power-up mạnh và thú vị nhất:

- **Strategic Control**: Cho người chơi kiểm soát hoàn toàn ball trajectory
- **Risk Reduction**: Giảm nguy cơ mất bóng
- **Precision**: Cho phép aim chính xác đến target
- **Pacing**: Tạo strategic pause trong gameplay
- **Skill-Based**: Người chơi giỏi sẽ tận dụng tối đa
- **Clean Implementation**: Simple code nhưng powerful effect

CatchPowerUp là ví dụ tuyệt vời về việc một mechanic đơn giản (bắt và giữ bóng) có thể tạo ra depth lớn trong gameplay. Nó transform Arkanoid từ pure reflex game thành strategic game, nơi người chơi có thể plan moves và execute tactics chính xác.

**Fun Fact**: Catch power-up xuất hiện trong Arkanoid original (1986) và trở thành một trong những power-ups iconic nhất của series. Nhiều breakout clones sau này đều copy mechanic này vì nó quá effective trong việc improve player experience.
