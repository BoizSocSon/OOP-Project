# Paddle Class

## Tổng quan
`Paddle` là lớp đại diện cho thanh đỡ (paddle/vợt) của người chơi trong game Arkanoid - đối tượng chính mà người chơi điều khiển. Paddle không chỉ đơn giản là một thanh trượt mà còn là một hệ thống phức tạp quản lý nhiều trạng thái, hiệu ứng power-up, và animation. Đây là đối tượng duy nhất mà người chơi có thể điều khiển trực tiếp trong game.

## Vị trí
- **Package**: `Objects.GameEntities`
- **File**: `src/Objects/GameEntities/Paddle.java`
- **Kế thừa**: `MovableObject`
- **Implements**: `GameObject` (gián tiếp qua MovableObject)

## Mục đích
Lớp Paddle:
- Quản lý di chuyển trái/phải theo input của người chơi
- Xử lý nhiều trạng thái (NORMAL, WIDE, LASER, v.v.) với animation tương ứng
- Quản lý các power-up: Expand, Laser, Catch, Slow
- Theo dõi thời gian hết hạn và hiệu ứng cảnh báo (pulsate) cho power-up
- Bắn laser khi có power-up
- Bắt và giữ bóng với Catch power-up

## Kế thừa

```
GameObject (Interface)
    ↑
    │ implements
    │
MovableObject (Abstract Class)
    ↑
    │ extends
    │
Paddle (Concrete Class)
```

---

## Thuộc tính (Fields)

### 1. Power-Up States

#### `private boolean catchMode`
**Mô tả**: Cờ kích hoạt chế độ bắt bóng (Catch Power-Up).

**Giá trị mặc định**: `false`

**Ý nghĩa**: 
- `true` = paddle có thể bắt và giữ bóng khi va chạm
- `false` = bóng nảy bình thường

---

#### `private int laserShots`
**Mô tả**: Số lần bắn laser còn lại.

**Giá trị mặc định**: `0`

**Ý nghĩa**: 
- `> 0` = có thể bắn laser
- `= 0` = hết đạn laser
- Mỗi lần bắn giảm 1

**Giá trị điển hình**: `Constants.Laser.LASER_SHOTS` (thường là 20-30)

---

#### `private long laserCooldown`
**Mô tả**: Thời điểm (milliseconds) cooldown laser kết thúc.

**Giá trị mặc định**: `0`

**Ý nghĩa**: 
- Kiểm soát tốc độ bắn (không spam)
- `System.currentTimeMillis() < laserCooldown` = không thể bắn
- `System.currentTimeMillis() >= laserCooldown` = có thể bắn

---

### 2. Animation & State Management

#### `private PaddleState currentState`
**Mô tả**: Trạng thái hiện tại của paddle.

**Giá trị mặc định**: `PaddleState.NORMAL`

**Các trạng thái**:
- `NORMAL` - trạng thái mặc định
- `WIDE` - đang mở rộng
- `WIDE_PULSATE` - mở rộng sắp hết hạn (nhấp nháy)
- `LASER` - có khả năng bắn laser
- `LASER_PULSATE` - laser sắp hết hạn (nhấp nháy)
- `PULSATE` - hiệu ứng độc lập (Catch/Slow) sắp hết hạn
- `MATERIALIZE` - animation xuất hiện
- `EXPLODE` - animation nổ

---

#### `private Animation currentAnimation`
**Mô tả**: Animation hiện tại đang được phát.

**Giá trị mặc định**: `null`

---

#### `private boolean animationPlaying`
**Mô tả**: Cờ báo hiệu animation đang chạy.

**Giá trị mặc định**: `false`

---

### 3. Power-Up Expiry Times

#### `private long expandExpiryTime`
**Mô tả**: Thời điểm (milliseconds) hiệu ứng EXPAND hết hạn.

**Giá trị mặc định**: `0`

---

#### `private long laserExpiryTime`
**Mô tả**: Thời điểm (milliseconds) hiệu ứng LASER hết hạn.

**Giá trị mặc định**: `0`

---

#### `private long catchExpiryTime`
**Mô tả**: Thời điểm (milliseconds) hiệu ứng CATCH hết hạn.

**Giá trị mặc định**: `0`

---

#### `private long slowExpiryTime`
**Mô tả**: Thời điểm (milliseconds) hiệu ứng SLOW hết hạn.

**Giá trị mặc định**: `0`

---

## Constructor

### `Paddle(double x, double y, double width, double height)`

**Mô tả**: Khởi tạo một Paddle mới.

**Tham số**:
- `x` - tọa độ X (góc trên trái)
- `y` - tọa độ Y (góc trên trái)
- `width` - chiều rộng paddle
- `height` - chiều cao paddle

**Ví dụ**:
```java
// Tạo paddle ở giữa dưới màn hình
double paddleX = (SCREEN_WIDTH - Constants.Paddle.PADDLE_WIDTH) / 2;
double paddleY = SCREEN_HEIGHT - 50;
Paddle paddle = new Paddle(
    paddleX, 
    paddleY, 
    Constants.Paddle.PADDLE_WIDTH,  // 120
    Constants.Paddle.PADDLE_HEIGHT   // 20
);
```

---

## Phương thức chính

### 1. `void update()` ⭐

**Mô tả**: Cập nhật trạng thái paddle trong mỗi frame. Đây là phương thức phức tạp nhất của Paddle.

**Chức năng**:
1. Di chuyển paddle theo velocity
2. Cập nhật animation hiện tại
3. Kiểm tra và xử lý hết hạn power-up
4. Chuyển đổi trạng thái cảnh báo (pulsate)

**Luồng hoạt động**:

```
1. move() → Di chuyển paddle
   ↓
2. Cập nhật animation
   - currentAnimation.update()
   - Kiểm tra animation kết thúc
   - Chuyển trạng thái nếu cần
   ↓
3. Kiểm tra EXPAND expiry
   - Hết hạn → shrinkToNormal()
   - Gần hết hạn → setState(WIDE_PULSATE)
   ↓
4. Kiểm tra LASER expiry
   - Hết hạn → disableLaser()
   - Gần hết hạn → setState(LASER_PULSATE)
   ↓
5. Kiểm tra CATCH/SLOW expiry
   - Có hiệu ứng + gần hết hạn → setState(PULSATE)
   - Hết hiệu ứng → setState(NORMAL)
```

**Ví dụ timeline**:
```java
// T = 0ms: Kích hoạt EXPAND
paddle.expand();
// expandExpiryTime = currentTime + 15000 (15 giây)

// T = 0-12000ms: Trạng thái WIDE
// paddle.currentState = WIDE

// T = 12000ms: Gần hết hạn (còn 3s)
// paddle.currentState = WIDE_PULSATE (nhấp nháy cảnh báo)

// T = 15000ms: Hết hạn
// paddle.shrinkToNormal() được gọi
// paddle.currentState = NORMAL (sau animation đảo ngược)
```

---

### 2. Di chuyển Paddle

#### `void moveLeft()`
**Mô tả**: Đặt vận tốc để paddle di chuyển sang trái.

**Hành vi**: `setVelocity(new Velocity(-PADDLE_SPEED, 0))`

**Sử dụng**:
```java
// Trong input handler
if (keyboard.isKeyPressed(KeyCode.LEFT) || keyboard.isKeyPressed(KeyCode.A)) {
    paddle.moveLeft();
} else if (keyboard.isKeyPressed(KeyCode.RIGHT) || keyboard.isKeyPressed(KeyCode.D)) {
    paddle.moveRight();
} else {
    paddle.stop();
}
```

---

#### `void moveRight()`
**Mô tả**: Đặt vận tốc để paddle di chuyển sang phải.

**Hành vi**: `setVelocity(new Velocity(PADDLE_SPEED, 0))`

---

#### `void stop()`
**Mô tả**: Dừng di chuyển của paddle.

**Hành vi**: `setVelocity(new Velocity(0, 0))`

**Sử dụng**:
```java
// Dừng khi không nhấn phím
if (!leftPressed && !rightPressed) {
    paddle.stop();
}

// Dừng khi chạm biên
if (paddle.getX() < 0) {
    paddle.setX(0);
    paddle.stop();
}
if (paddle.getX() + paddle.getWidth() > SCREEN_WIDTH) {
    paddle.setX(SCREEN_WIDTH - paddle.getWidth());
    paddle.stop();
}
```

---

### 3. Quản lý Trạng thái & Animation

#### `void setState(PaddleState newState)`
**Mô tả**: Thay đổi trạng thái paddle và bắt đầu animation tương ứng.

**Tham số**: `newState` - trạng thái mới

**Hành vi**:
- Bỏ qua nếu trạng thái không đổi và animation đang chạy
- NORMAL không có animation
- Các trạng thái khác tạo và phát animation từ `AnimationFactory`

**Sử dụng**:
```java
// Chuyển sang WIDE
paddle.setState(PaddleState.WIDE);

// Chuyển sang LASER
paddle.setState(PaddleState.LASER);

// Cảnh báo hết hạn
paddle.setState(PaddleState.WIDE_PULSATE);

// Về NORMAL
paddle.setState(PaddleState.NORMAL);
```

---

#### `PaddleState getState()`
**Mô tả**: Lấy trạng thái hiện tại.

**Kiểu trả về**: `PaddleState`

---

#### `Animation getAnimation()`
**Mô tả**: Lấy animation hiện tại.

**Kiểu trả về**: `Animation` (có thể `null`)

---

#### `boolean isAnimationPlaying()`
**Mô tả**: Kiểm tra animation có đang chạy không.

**Kiểu trả về**: `boolean`

**Sử dụng**:
```java
// Render
if (paddle.isAnimationPlaying()) {
    paddle.getAnimation().render(graphics, paddle.getX(), paddle.getY());
} else {
    renderPaddleSprite(paddle);
}
```

---

### 4. Laser Power-Up

#### `void enableLaser()`
**Mô tả**: Kích hoạt hiệu ứng laser.

**Hành vi**:
1. Nếu đang WIDE → thu nhỏ về kích thước chuẩn
2. Đặt trạng thái LASER
3. Thiết lập số đạn = `LASER_SHOTS`
4. Thiết lập thời gian hết hạn

**Sử dụng**:
```java
// Khi thu thập Laser Power-Up
public void collectLaserPowerUp() {
    paddle.enableLaser();
    audioManager.playPowerUpSound();
}
```

**Lưu ý**: LASER và WIDE không thể cùng tồn tại. Kích hoạt LASER sẽ hủy WIDE.

---

#### `void disableLaser()`
**Mô tả**: Vô hiệu hóa laser với animation đảo ngược.

**Hành vi**:
1. Chơi animation đảo ngược từ LASER → NORMAL
2. Đặt laserShots = 0
3. Xóa laserExpiryTime

---

#### `List<Laser> shootLaser()`
**Mô tả**: Bắn laser từ paddle.

**Kiểu trả về**: 
- Danh sách rỗng nếu không thể bắn
- Danh sách 2 đối tượng `Laser` nếu bắn thành công

**Điều kiện bắn**:
- `laserShots > 0` - còn đạn
- `System.currentTimeMillis() >= laserCooldown` - hết cooldown

**Hành vi**:
1. Giảm `laserShots--`
2. Thiết lập cooldown mới
3. Tạo 2 laser ở 2 bên paddle

**Sử dụng**:
```java
// Trong game loop khi nhấn Space
if (keyboard.isKeyPressed(KeyCode.SPACE) && paddle.isLaserEnabled()) {
    List<Laser> newLasers = paddle.shootLaser();
    
    if (!newLasers.isEmpty()) {
        lasers.addAll(newLasers);
        audioManager.playLaserSound();
    }
}
```

---

#### `boolean isLaserEnabled()`
**Mô tả**: Kiểm tra laser có hoạt động không.

**Công thức**: `laserShots > 0 && (state == LASER || state == LASER_PULSATE)`

---

#### `int getLaserShots()`
**Mô tả**: Lấy số đạn còn lại.

**Sử dụng**:
```java
// UI hiển thị số đạn
if (paddle.isLaserEnabled()) {
    drawText("Laser: " + paddle.getLaserShots(), 10, 10);
}
```

---

### 5. Expand Power-Up

#### `void expand()`
**Mô tả**: Kích hoạt hiệu ứng mở rộng paddle.

**Hành vi**:
1. Nếu đã WIDE → chỉ gia hạn thời gian
2. Nếu đang LASER → hủy LASER trước
3. Đặt trạng thái WIDE
4. Tăng width lên `PADDLE_WIDE_WIDTH`
5. Giữ paddle ở giữa (tính lại x từ centerX)
6. Thiết lập thời gian hết hạn

**Sử dụng**:
```java
// Khi thu thập Expand Power-Up
public void collectExpandPowerUp() {
    paddle.expand();
    audioManager.playPowerUpSound();
}
```

**Công thức giữ paddle ở giữa**:
```java
double centerX = paddle.getX() + paddle.getWidth() / 2;
paddle.setWidth(newWidth);
paddle.setX(centerX - newWidth / 2);
```

---

#### `void shrinkToNormal()`
**Mô tả**: Thu nhỏ paddle về kích thước NORMAL.

**Hành vi**:
1. Bỏ qua nếu không phải WIDE
2. Chơi animation đảo ngược
3. Thu nhỏ width về `PADDLE_WIDTH`
4. Giữ paddle ở giữa
5. Xóa expandExpiryTime

**Gọi tự động**: Trong `update()` khi EXPAND hết hạn

---

### 6. Catch Power-Up

#### `void enableCatch()`
**Mô tả**: Kích hoạt chế độ bắt bóng.

**Hành vi**:
- `catchMode = true`
- Thiết lập thời gian hết hạn

---

#### `void disableCatch()`
**Mô tả**: Tắt chế độ bắt bóng.

**Hành vi**:
- `catchMode = false`
- Xóa catchExpiryTime

---

#### `boolean isCatchModeEnabled()`
**Mô tả**: Kiểm tra Catch có đang hoạt động không.

**Sử dụng**:
```java
// Trong collision detection
if (paddle.getBounds().intersects(ball.getBounds())) {
    if (paddle.isCatchModeEnabled()) {
        ball.setAttached(true);
        ball.setVelocity(new Velocity(0, 0));
    } else {
        ball.checkCollisionWithRect(paddle.getBounds());
    }
}
```

---

### 7. Slow Power-Up

#### `void setSlowEffectExpiry(long expiryTime)`
**Mô tả**: Đặt thời gian hết hạn cho hiệu ứng Slow.

**Lưu ý**: Slow là hiệu ứng độc lập - không ảnh hưởng hình dạng paddle, chỉ làm chậm bóng.

---

#### `void clearSlowEffect()`
**Mô tả**: Xóa hiệu ứng Slow.

---

### 8. Animation Effects

#### `void playMaterializeAnimation()`
**Mô tả**: Phát animation xuất hiện (khi bắt đầu round/respawn).

**Sử dụng**:
```java
// Khi bắt đầu round
public void startRound() {
    paddle = new Paddle(...);
    paddle.playMaterializeAnimation();
}
```

---

#### `void playExplodeAnimation()`
**Mô tả**: Phát animation nổ (khi paddle bị phá hủy).

**Sử dụng**:
```java
// Khi mất mạng
public void loseLife() {
    paddle.playExplodeAnimation();
    
    // Sau khi animation kết thúc
    waitForAnimation(() -> {
        lives--;
        if (lives > 0) {
            respawnPaddle();
        } else {
            gameOver();
        }
    });
}
```

---

## Luồng hoạt động Power-Up

### Scenario 1: Expand Power-Up

```
1. Người chơi thu thập Expand Power-Up
   ↓
2. paddle.expand() được gọi
   ↓
3. Kiểm tra trạng thái hiện tại:
   
   Nếu đã WIDE:
      → Chỉ gia hạn expandExpiryTime
      → Return
   
   Nếu đang LASER:
      → laserShots = 0
      → laserExpiryTime = 0
      → Hủy LASER
   ↓
4. setState(WIDE)
   → Tạo và phát animation chuyển đổi
   ↓
5. Tăng width từ 120 → 180
   Tính lại x để giữ paddle ở giữa
   ↓
6. expandExpiryTime = now + 15000ms
   ↓
7. Trong update() - mỗi frame:
   
   T = 0-12000ms:
      → currentState = WIDE
      → Animation transition chạy (nếu chưa kết thúc)
   
   T = 12000ms: (còn 3s)
      → setState(WIDE_PULSATE)
      → Animation cảnh báo nhấp nháy
   
   T = 15000ms: (hết hạn)
      → shrinkToNormal() được gọi
      → playReversedAnimation(WIDE)
      → Thu nhỏ width về 120
      → setState(NORMAL) sau animation kết thúc
```

### Scenario 2: Laser Power-Up

```
1. Người chơi thu thập Laser Power-Up
   ↓
2. paddle.enableLaser() được gọi
   ↓
3. Kiểm tra trạng thái hiện tại:
   
   Nếu đang WIDE:
      → Thu nhỏ về kích thước chuẩn
      → expandExpiryTime = 0
   ↓
4. setState(LASER)
   → Animation chuyển đổi (paddle thêm súng laser)
   ↓
5. laserShots = 20-30
   laserExpiryTime = now + 20000ms
   ↓
6. Người chơi nhấn Space để bắn:
   
   paddle.shootLaser():
      → Kiểm tra laserShots > 0 ✓
      → Kiểm tra cooldown ✓
      → laserShots--
      → laserCooldown = now + 300ms
      → Tạo 2 laser ở 2 bên paddle
      → Return [laser1, laser2]
   ↓
7. Trong update() - theo thời gian:
   
   T = 0-17000ms:
      → currentState = LASER
      → Có thể bắn laser
   
   T = 17000ms: (còn 3s)
      → setState(LASER_PULSATE)
      → Cảnh báo nhấp nháy
   
   T = 20000ms: (hết hạn)
      → disableLaser() được gọi
      → playReversedAnimation(LASER)
      → laserShots = 0
      → setState(NORMAL)
```

### Scenario 3: Catch Power-Up

```
1. Người chơi thu thập Catch Power-Up
   ↓
2. paddle.enableCatch() được gọi
   ↓
3. catchMode = true
   catchExpiryTime = now + 10000ms
   ↓
4. Bóng va chạm paddle:
   
   if (paddle.isCatchModeEnabled()) {
       ball.setAttached(true);
       ball.setVelocity(0, 0);
       // Đồng bộ vị trí với paddle
   }
   ↓
5. Trong update() - nếu currentState == NORMAL:
   
   T = 0-7000ms:
      → catchMode = true
      → Paddle ở trạng thái NORMAL
   
   T = 7000ms: (còn 3s)
      → setState(PULSATE)
      → Cảnh báo nhấp nháy (độc lập với hình dạng)
   
   T = 10000ms: (hết hạn)
      → catchExpiryTime = 0
      → setState(NORMAL)
   ↓
6. Người chơi nhấn Space:
   → ball.setAttached(false)
   → ball.setVelocity(calculateLaunchVelocity())
   → Bóng bay tự do
```

### Scenario 4: Nhiều Power-Up cùng lúc

```
Trường hợp: Có WIDE + CATCH đang hoạt động

1. Trạng thái ban đầu:
   currentState = WIDE
   expandExpiryTime = now + 5000ms
   catchMode = true
   catchExpiryTime = now + 3000ms
   ↓
2. T = 0ms: (còn 5s WIDE, 3s CATCH)
   → currentState = WIDE
   ↓
3. T = 2000ms: (còn 3s WIDE, 1s CATCH)
   → setState(WIDE_PULSATE)
   → Cảnh báo WIDE hết hạn
   ↓
4. T = 3000ms: (còn 2s WIDE, CATCH hết hạn)
   → catchExpiryTime = 0
   → Vẫn ở WIDE_PULSATE (vì WIDE chưa hết)
   ↓
5. T = 5000ms: (WIDE hết hạn)
   → shrinkToNormal()
   → setState(NORMAL)
```

---

## Tích hợp với các hệ thống khác

### 1. InputManager

```java
public class InputManager {
    private Paddle paddle;
    
    public void handleInput() {
        // Di chuyển paddle
        if (keyboard.isKeyPressed(KeyCode.LEFT) || keyboard.isKeyPressed(KeyCode.A)) {
            paddle.moveLeft();
        } else if (keyboard.isKeyPressed(KeyCode.RIGHT) || keyboard.isKeyPressed(KeyCode.D)) {
            paddle.moveRight();
        } else {
            paddle.stop();
        }
        
        // Giới hạn paddle trong màn hình
        if (paddle.getX() < Constants.Borders.BORDER_LEFT_WIDTH) {
            paddle.setX(Constants.Borders.BORDER_LEFT_WIDTH);
            paddle.stop();
        }
        if (paddle.getX() + paddle.getWidth() > Constants.Window.WINDOW_WIDTH - Constants.Borders.BORDER_RIGHT_WIDTH) {
            paddle.setX(Constants.Window.WINDOW_WIDTH - Constants.Borders.BORDER_RIGHT_WIDTH - paddle.getWidth());
            paddle.stop();
        }
        
        // Bắn laser
        if (keyboard.isKeyPressed(KeyCode.SPACE)) {
            if (paddle.isLaserEnabled()) {
                List<Laser> newLasers = paddle.shootLaser();
                if (!newLasers.isEmpty()) {
                    gameManager.addLasers(newLasers);
                    audioManager.playLaserSound();
                }
            }
            
            // Thả bóng nếu đang attached
            gameManager.launchAttachedBalls();
        }
    }
}
```

### 2. PowerUpManager

```java
public class PowerUpManager {
    public void applyPowerUp(PowerUp powerUp, Paddle paddle) {
        switch (powerUp.getType()) {
            case EXPAND:
                paddle.expand();
                break;
                
            case LASER:
                paddle.enableLaser();
                break;
                
            case CATCH:
                paddle.enableCatch();
                break;
                
            case SLOW:
                // Slow ảnh hưởng bóng, nhưng paddle theo dõi thời gian hết hạn
                long expiry = System.currentTimeMillis() + Constants.PowerUps.SLOW_DURATION;
                paddle.setSlowEffectExpiry(expiry);
                slowDownAllBalls();
                break;
                
            case LIFE:
                // Không ảnh hưởng paddle
                lives++;
                break;
        }
        
        audioManager.playPowerUpSound();
    }
}
```

### 3. Renderer

```java
public class PaddleRenderer {
    public void render(Paddle paddle, Graphics g) {
        double x = paddle.getX();
        double y = paddle.getY();
        
        // Render animation nếu đang chạy
        if (paddle.isAnimationPlaying()) {
            Animation anim = paddle.getAnimation();
            anim.render(g, x, y);
        } else {
            // Render sprite tĩnh dựa trên trạng thái
            String spriteKey = getSpriteKeyForState(paddle.getState());
            Sprite sprite = spriteCache.getSprite(spriteKey);
            g.drawImage(sprite.getImage(), x, y);
        }
        
        // Render laser indicator
        if (paddle.isLaserEnabled()) {
            renderLaserIndicator(paddle, g);
        }
        
        // Render catch indicator
        if (paddle.isCatchModeEnabled()) {
            renderCatchIndicator(paddle, g);
        }
    }
    
    private String getSpriteKeyForState(PaddleState state) {
        switch (state) {
            case NORMAL: return "paddle_normal";
            case WIDE: 
            case WIDE_PULSATE: return "paddle_wide_final";
            case LASER:
            case LASER_PULSATE: return "paddle_laser_final";
            default: return "paddle_normal";
        }
    }
}
```

---

## Best Practices

### 1. Power-Up Management
```java
// ✅ Đúng - kiểm tra trạng thái trước khi áp dụng
if (powerUp.getType() == PowerUpType.EXPAND) {
    if (paddle.getState() == PaddleState.LASER) {
        // Người chơi biết LASER sẽ bị hủy
        showWarning("Collecting EXPAND will cancel LASER!");
    }
    paddle.expand();
}

// ❌ Sai - áp dụng mù quáng
paddle.expand(); // Có thể hủy LASER bất ngờ
```

### 2. Animation Handling
```java
// ✅ Đúng - đợi animation kết thúc trước khi hành động
if (paddle.getState() == PaddleState.EXPLODE) {
    if (!paddle.isAnimationPlaying()) {
        respawnPaddle(); // Animation đã kết thúc
    }
}

// ❌ Sai - không đợi animation
paddle.playExplodeAnimation();
respawnPaddle(); // Quá nhanh, animation không hoàn thành
```

### 3. Boundary Checking
```java
// ✅ Đúng - luôn kiểm tra boundaries sau khi di chuyển
paddle.update(); // Di chuyển

// Giới hạn trong màn hình
double minX = Constants.Borders.BORDER_LEFT_WIDTH;
double maxX = Constants.Window.WINDOW_WIDTH - Constants.Borders.BORDER_RIGHT_WIDTH - paddle.getWidth();

if (paddle.getX() < minX) {
    paddle.setX(minX);
    paddle.stop();
} else if (paddle.getX() > maxX) {
    paddle.setX(maxX);
    paddle.stop();
}

// ❌ Sai - không kiểm tra, paddle có thể ra khỏi màn hình
paddle.update();
```

### 4. State Transitions
```java
// ✅ Đúng - sử dụng setState() để chuyển trạng thái
paddle.setState(PaddleState.LASER);
// Tự động tạo và phát animation

// ❌ Sai - thay đổi trực tiếp
paddle.currentState = PaddleState.LASER; // Không có animation!
```

---

## Kết luận

`Paddle` là một lớp phức tạp và quan trọng trong game Arkanoid:

- **Di chuyển**: Điều khiển trái/phải bởi người chơi
- **Power-Ups**: Quản lý 4 loại hiệu ứng (Expand, Laser, Catch, Slow)
- **Animation**: Hệ thống animation phức tạp cho mỗi trạng thái
- **State Management**: Theo dõi và chuyển đổi giữa 8 trạng thái
- **Expiry Tracking**: Tự động hết hạn và cảnh báo cho power-up

Lớp này là ví dụ điển hình của một game object với nhiều trạng thái và hành vi phức tạp, sử dụng State Pattern và Animation System để quản lý các hiệu ứng visual và gameplay mechanics.
