# CollisionManager

## Tổng quan
`CollisionManager` là lớp quản lý trung tâm cho tất cả các logic phát hiện và xử lý va chạm trong game Arkanoid. Lớp này chịu trách nhiệm kiểm tra và xử lý các tương tác vật lý giữa:
- Bóng (Ball) với tường
- Bóng (Ball) với thanh đỡ (Paddle)  
- Bóng (Ball) với gạch (Bricks)
- Tia laser (Laser) với gạch
- Vật phẩm bổ trợ (PowerUp) với thanh đỡ

Lớp sử dụng kỹ thuật AABB (Axis-Aligned Bounding Box) collision detection kết hợp với swept collision cho độ chính xác cao.

## Package
```
Engine.CollisionManager
```

## Thuộc tính

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Mô tả |
|-----------|-------------|-----------------|-------|
| `playAreaWidth` | `int` | `private` | Chiều rộng khu vực chơi (Play Area) |
| `playAreaHeight` | `int` | `private` | Chiều cao khu vực chơi |
| `MAX_BOUNCE_ANGLE` | `double` | `private static final` | Góc phản xạ tối đa (độ) khi bóng chạm thanh đỡ, lấy từ `Constants.Paddle.PADDLE_MAX_ANGLE` |

### Chi tiết thuộc tính

#### playAreaWidth / playAreaHeight
Lưu trữ kích thước của khu vực chơi game. Sử dụng để:
- Tính toán vị trí biên tường
- Xác định giới hạn di chuyển của các đối tượng
- Điều chỉnh khi thay đổi độ phân giải màn hình

#### MAX_BOUNCE_ANGLE  
Góc phản xạ tối đa của bóng khi chạm thanh đỡ. Ví dụ:
- Giá trị 75 độ → bóng có thể phản xạ từ -75° đến +75°
- Giá trị 0 độ → bóng luôn bay thẳng lên (không có góc)
- Giá trị 90 độ → bóng có thể bay gần như ngang

```
        -75°  -45°  0°  +45°  +75°
           \    |    |    |    /
            \   |    |    |   /
             \  |    |    |  /
              \ |    |    | /
        =============================  ← Thanh đỡ (Paddle)
        |<-Trái   Center   Phải->|
```

## Constructor

### CollisionManager(int width, int height)
Khởi tạo CollisionManager với kích thước khu vực chơi.

**Tham số:**
- `width` - Chiều rộng khu vực chơi (pixel)
- `height` - Chiều cao khu vực chơi (pixel)

**Ví dụ:**
```java
// Khởi tạo CollisionManager cho màn hình 800x600
CollisionManager collisionManager = new CollisionManager(800, 600);
```

## Phương thức công khai

### 1. checkBallWallCollisions()
```java
public void checkBallWallCollisions(Ball ball, double leftBorder, double rightBorder, double topBorder)
```

Kiểm tra và xử lý va chạm của bóng với các tường (trên, trái, phải).

**Tham số:**
- `ball` - Đối tượng bóng cần kiểm tra
- `leftBorder` - Tọa độ X của biên trái (vị trí tường trái)
- `rightBorder` - Tọa độ X của biên phải (vị trí tường phải)  
- `topBorder` - Tọa độ Y của biên trên (vị trí tường trên)

**Cơ chế hoạt động:**
1. **Va chạm tường trái:** Nếu `ball.getX() <= leftBorder`
   - Đặt lại vị trí: `ball.setX(leftBorder)`
   - Đảo hướng X: `dx = Math.abs(dx)` (đảm bảo dx dương - bay sang phải)

2. **Va chạm tường phải:** Nếu `ball.getX() + ball.getWidth() >= rightBorder`
   - Đặt lại vị trí: `ball.setX(rightBorder - ball.getWidth())`
   - Đảo hướng X: `dx = -Math.abs(dx)` (đảm bảo dx âm - bay sang trái)

3. **Va chạm tường trên:** Nếu `ball.getY() <= topBorder`
   - Đặt lại vị trí: `ball.setY(topBorder)`
   - Đảo hướng Y: `dy = Math.abs(dy)` (đảm bảo dy dương - bay xuống)

**Lưu ý:**
- Không kiểm tra tường dưới (bottom) - logic thua cuộc được xử lý ở GameManager
- Sử dụng `Math.abs()` để đảm bảo hướng chính xác (tránh bóng bị kẹt)
- Biến `collided` để trigger sound effects (chưa implement)

**Ví dụ:**
```java
Ball ball = new Ball(100, 200, 16, 16);
ball.setVelocity(new Velocity(-5, -3)); // Bay sang trái và lên

// Biên giới: trái=50, phải=750, trên=100
collisionManager.checkBallWallCollisions(ball, 50, 750, 100);

// Nếu ball.getX() <= 50 → dx đảo thành +5 (bay sang phải)
// Nếu ball.getY() <= 100 → dy đảo thành +3 (bay xuống)
```

**Minh họa:**
```
        topBorder (y=100)
        ═════════════════════════════════
        ║                               ║
        ║         ●→  Bóng              ║  
left    ║        (va chạm trên)         ║  right
Border  ║                               ║  Border
(x=50)  ║                               ║  (x=750)
        ║                               ║
        ║   ●←  (va chạm trái)          ║
        ║                               ║
        ═════════════════════════════════
```

---

### 2. checkBallPaddleCollision()
```java
public boolean checkBallPaddleCollision(Ball ball, Paddle paddle)
```

Kiểm tra va chạm giữa bóng và thanh đỡ, tính toán góc phản xạ dựa trên vị trí va chạm.

**Tham số:**
- `ball` - Đối tượng bóng
- `paddle` - Đối tượng thanh đỡ

**Giá trị trả về:**
- `true` - Va chạm xảy ra
- `false` - Không có va chạm

**Thuật toán:**

1. **Kiểm tra AABB (Axis-Aligned Bounding Box):**
   ```java
   if (!ball.getBounds().intersects(paddle.getBounds())) {
       return false; // Không va chạm → bỏ qua
   }
   ```

2. **Tính vị trí va chạm trên thanh đỡ:**
   ```java
   double ballCenterX = ball.getCenter().getX();
   double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
   double paddleHalfWidth = paddle.getWidth() / 2.0;
   
   // hitPosition từ -1.0 (cực trái) đến 1.0 (cực phải)
   double hitPosition = (ballCenterX - paddleCenterX) / paddleHalfWidth;
   hitPosition = Math.max(-1.0, Math.min(1.0, hitPosition)); // Clamp [-1, 1]
   ```

3. **Kiểm tra chế độ Catch (bắt bóng):**
   ```java
   if (paddle.isCatchModeEnabled()) {
       return true; // Bóng bị bắt → không tính góc phản xạ
   }
   ```

4. **Tính toán góc phản xạ:**
   - Gọi `calculateBallAngleFromPaddle(ball, paddle, hitPosition)`
   - Đặt lại vị trí Y: `ball.setY(paddle.getY() - ball.getHeight() - 1)`

**Hit Position Mapping:**
```
    hitPosition = -1.0        0.0        +1.0
                  ↓            ↓           ↓
        ┌─────────────────────────────────┐
        │◄─ Trái  |   Center   |  Phải ─►│  ← Thanh đỡ
        └─────────────────────────────────┘
         -75°          0°           +75°   ← Góc phản xạ
```

**Ví dụ:**
```java
Ball ball = new Ball(400, 550, 16, 16);
Paddle paddle = new Paddle(350, 560, 100, 20);

if (collisionManager.checkBallPaddleCollision(ball, paddle)) {
    // Va chạm xảy ra
    // Nếu bóng chạm giữa thanh đỡ → bay thẳng lên (0°)
    // Nếu bóng chạm cạnh trái → bay sang trái (-75°)
    // Nếu bóng chạm cạnh phải → bay sang phải (+75°)
    System.out.println("Collision detected!");
}
```

**Lưu ý:**
- Vị trí va chạm càng xa tâm thanh đỡ → góc phản xạ càng lớn
- Chế độ Catch làm bóng dính vào thanh đỡ (không phản xạ)
- Điều chỉnh vị trí Y để tránh bóng bị "sink" vào thanh đỡ

---

### 3. checkBallBrickCollisions()
```java
public List<Brick> checkBallBrickCollisions(Ball ball, List<Brick> bricks)
```

Kiểm tra va chạm của bóng với tất cả gạch trong màn chơi.

**Tham số:**
- `ball` - Đối tượng bóng
- `bricks` - Danh sách các gạch cần kiểm tra

**Giá trị trả về:**
- `List<Brick>` - Danh sách các gạch bị phá hủy (để tính điểm và spawn PowerUp)

**Thuật toán:**
1. Lặp qua tất cả các gạch
2. Bỏ qua gạch đã chết: `if (!brick.isAlive()) continue;`
3. Xử lý đặc biệt cho gạch vàng (Gold Brick):
   - Gọi `ignoreGoldBricksCollision(brick, ball)`
   - Gạch vàng không thể phá → hoạt động như tường
4. Kiểm tra va chạm: `ball.checkCollisionWithRect(brick.getBounds())`
5. Gạch nhận sát thương: `brick.takeHit()`
6. Nếu `brick.isDestroyed()` → thêm vào `destroyedBricks`

**Swept Collision:**
- Sử dụng `ball.checkCollisionWithRect()` - swept collision tích hợp
- Tránh hiện tượng bóng "xuyên qua" gạch khi tốc độ cao
- Phát hiện va chạm chính xác hơn so với AABB đơn giản

**Ví dụ:**
```java
List<Brick> bricks = new ArrayList<>();
bricks.add(new NormalBrick(100, 100, 50, 20, BrickType.RED, 1));
bricks.add(new SilverBrick(200, 100, 50, 20, BrickType.SILVER, 2));
bricks.add(new GoldBrick(300, 100, 50, 20, BrickType.GOLD));

Ball ball = new Ball(110, 90, 16, 16);

List<Brick> destroyed = collisionManager.checkBallBrickCollisions(ball, bricks);

// destroyed có thể chứa:
// - NormalBrick (nếu 1 hit = destroyed)
// - SilverBrick (nếu đã bị hit 2 lần trước đó)
// - Không chứa GoldBrick (không thể phá hủy)

System.out.println("Destroyed: " + destroyed.size() + " bricks");
```

**Loại gạch:**

| Loại Gạch | BrickType | Độ bền | Có thể phá? | Điểm |
|-----------|-----------|--------|-------------|------|
| Normal | RED/GREEN/BLUE/etc. | 1 hit | ✅ Có | 50-90 |
| Silver | SILVER | 2 hits | ✅ Có | 50 |
| Gold | GOLD | ∞ | ❌ Không | 0 |

---

### 4. checkLaserBrickCollisions()
```java
public Map<Laser, Brick> checkLaserBrickCollisions(List<Laser> lasers, List<Brick> bricks)
```

Kiểm tra va chạm giữa tia laser và gạch. Mỗi laser chỉ phá được 1 gạch.

**Tham số:**
- `lasers` - Danh sách tia laser đang bay
- `bricks` - Danh sách gạch

**Giá trị trả về:**
- `Map<Laser, Brick>` - Map chứa cặp (laser → gạch bị trúng)

**Thuật toán:**
1. Lặp qua tất cả lasers
2. Bỏ qua laser đã chết: `if (!laser.isAlive()) continue;`
3. Lặp qua tất cả bricks
4. Bỏ qua gạch đã chết: `if (!brick.isAlive()) continue;`
5. Kiểm tra AABB: `laser.getBounds().intersects(brick.getBounds())`
6. Nếu va chạm:
   - `brick.takeHit()` - Gạch nhận sát thương
   - `collisions.put(laser, brick)` - Lưu vào Map
   - `break` - Dừng vòng lặp bricks (1 laser = 1 gạch)

**Ví dụ:**
```java
List<Laser> lasers = new ArrayList<>();
lasers.add(new Laser(400, 300, 4, 20, -10)); // Bay lên với tốc độ -10

List<Brick> bricks = new ArrayList<>();
bricks.add(new NormalBrick(390, 200, 50, 20, BrickType.RED, 1));

Map<Laser, Brick> hits = collisionManager.checkLaserBrickCollisions(lasers, bricks);

for (Map.Entry<Laser, Brick> entry : hits.entrySet()) {
    Laser laser = entry.getKey();
    Brick brick = entry.getValue();
    
    laser.setAlive(false); // Hủy laser
    
    if (brick.isDestroyed()) {
        // Cộng điểm, spawn PowerUp...
    }
}
```

**So sánh với Ball:**

| Đặc điểm | Ball | Laser |
|----------|------|-------|
| Va chạm gạch | Phản xạ (đảo vận tốc) | Xuyên qua và tự hủy |
| Số gạch/lần | Tối đa 1 | Đúng 1 |
| Swept collision | ✅ Có | ❌ Không (AABB đơn giản) |
| Gạch vàng | Phản xạ như tường | Phá được |

---

### 5. checkPowerUpPaddleCollisions()
```java
public List<PowerUp> checkPowerUpPaddleCollisions(List<PowerUp> powerUps, Paddle paddle)
```

Kiểm tra va chạm giữa vật phẩm bổ trợ (PowerUp) và thanh đỡ.

**Tham số:**
- `powerUps` - Danh sách PowerUp đang rơi
- `paddle` - Thanh đỡ người chơi

**Giá trị trả về:**
- `List<PowerUp>` - Danh sách PowerUp đã được thu thập

**Thuật toán:**
1. Kiểm tra paddle null: `if (paddle == null) return collected;`
2. Lặp qua tất cả PowerUps
3. Bỏ qua PowerUp đã chết: `if (!powerUp.isAlive()) continue;`
4. Kiểm tra va chạm: `powerUp.checkPaddleCollision(paddle)`
5. Nếu va chạm → thêm vào `collected`

**Single Source of Truth:**
- Va chạm được xử lý bởi chính PowerUp: `powerUp.checkPaddleCollision(paddle)`
- Không trực tiếp dùng AABB trong CollisionManager
- Tránh logic trùng lặp và mâu thuẫn

**Ví dụ:**
```java
List<PowerUp> powerUps = new ArrayList<>();
powerUps.add(new ExpandPaddlePowerUp(400, 100)); // Rơi từ trên xuống
powerUps.add(new LaserPowerUp(500, 200));

Paddle paddle = new Paddle(380, 560, 100, 20);

List<PowerUp> collected = collisionManager.checkPowerUpPaddleCollisions(powerUps, paddle);

for (PowerUp p : collected) {
    p.setAlive(false); // Hủy PowerUp
    p.apply(paddle);   // Áp dụng hiệu ứng
    scoreManager.addScore(50); // Cộng điểm
    // Phát SFX thu thập...
}
```

**Các loại PowerUp phổ biến:**

| PowerUp | Icon | Hiệu ứng |
|---------|------|----------|
| ExpandPaddle | `[E]` | Tăng kích thước thanh đỡ |
| Laser | `[L]` | Cho phép bắn laser |
| Catch | `[C]` | Bắt bóng vào thanh đỡ |
| SlowBall | `[S]` | Giảm tốc độ bóng |
| Duplicate | `[D]` | Nhân đôi số lượng bóng |
| Life | `[+]` | Thêm 1 mạng |
| Warp | `[W]` | Chuyển màn (skip) |

---

### 6. setPlayAreaSize()
```java
public void setPlayAreaSize(int width, int height)
```

Cập nhật kích thước khu vực chơi (dùng khi thay đổi resolution).

**Tham số:**
- `width` - Chiều rộng mới (pixel)
- `height` - Chiều cao mới (pixel)

**Ví dụ:**
```java
// Game ban đầu 800x600
CollisionManager cm = new CollisionManager(800, 600);

// Người chơi thay đổi độ phân giải lên 1024x768
cm.setPlayAreaSize(1024, 768);

// Tất cả logic va chạm sẽ dùng kích thước mới
```

---

## Phương thức riêng tư

### calculateBallAngleFromPaddle()
```java
private void calculateBallAngleFromPaddle(Ball ball, Paddle paddle, double hitPosition)
```

Tính toán góc phản xạ của bóng dựa trên vị trí va chạm trên thanh đỡ.

**Tham số:**
- `ball` - Đối tượng bóng
- `paddle` - Đối tượng thanh đỡ
- `hitPosition` - Vị trí va chạm chuẩn hóa [-1.0, 1.0]

**Thuật toán:**

1. **Tính tốc độ hiện tại (magnitude):**
   ```java
   double speed = Math.hypot(ball.getVelocity().getDx(), ball.getVelocity().getDy());
   ```
   - `Math.hypot(dx, dy)` = √(dx² + dy²)
   - Ví dụ: dx=3, dy=-4 → speed = √(9+16) = 5

2. **Tính góc phản xạ (độ):**
   ```java
   double angle = hitPosition * MAX_BOUNCE_ANGLE;
   ```
   - `hitPosition = -1.0` → `angle = -75°` (cực trái)
   - `hitPosition = 0.0` → `angle = 0°` (giữa)
   - `hitPosition = +1.0` → `angle = +75°` (cực phải)

3. **Chuyển sang radian:**
   ```java
   double angleRad = Math.toRadians(angle);
   ```

4. **Tính vận tốc mới:**
   ```java
   double dx = speed * Math.sin(angleRad);
   double dy = -speed * Math.cos(angleRad);
   ball.setVelocity(new Velocity(dx, dy));
   ```

**Hệ tọa độ JavaFX:**
```
       Y = 0 (Top)
          ↓
    ┌─────────────┐
    │     ↑       │
    │   dy=-1     │  → Lên trên = âm
    │             │
    │←───────────→│
    │ dx=-1   dx=+1
    └─────────────┘
          ↓
       Y = height (Bottom)
```

**Công thức trigonometry:**
```
         ↑ dy (âm)
         |
         |  /  ← Velocity vector
         | /
         |/) angle
    ─────●────────→ dx
       Paddle

dx = speed × sin(angle)
dy = -speed × cos(angle)  (âm vì hướng lên)
```

**Ví dụ:**
```java
// Bóng va chạm cạnh phải thanh đỡ
double hitPosition = 0.8;  // 80% về bên phải
double speed = 6.0;
double MAX_BOUNCE_ANGLE = 75.0;

// Tính toán:
angle = 0.8 × 75 = 60°
angleRad = Math.toRadians(60) = 1.047 rad

dx = 6.0 × Math.sin(1.047) = 6.0 × 0.866 = 5.196
dy = -6.0 × Math.cos(1.047) = -6.0 × 0.5 = -3.0

// Kết quả: bóng bay sang phải (dx=5.196) và lên (dy=-3.0)
```

---

### ignoreGoldBricksCollision()
```java
private void ignoreGoldBricksCollision(Brick brick, Ball ball)
```

Xử lý va chạm đặc biệt giữa bóng và gạch vàng (không thể phá hủy).

**Tham số:**
- `brick` - Gạch vàng (Gold Brick)
- `ball` - Đối tượng bóng

**Thuật toán:**

1. **Kiểm tra loại gạch:**
   ```java
   if (brick.getBrickType() == BrickType.GOLD) { ... }
   ```

2. **Kiểm tra AABB collision:**
   ```java
   if (ball.getBounds().intersects(brick.getBounds())) { ... }
   ```

3. **Tính toán hướng va chạm:**
   ```java
   double ballCenterX = ball.getCenter().getX();
   double ballCenterY = ball.getCenter().getY();
   double brickCenterX = brick.getX() + brick.getWidth() / 2.0;
   double brickCenterY = brick.getY() + brick.getHeight() / 2.0;
   
   double dx = ballCenterX - brickCenterX;
   double dy = ballCenterY - brickCenterY;
   ```

4. **Xác định cạnh va chạm:**
   ```java
   if (Math.abs(dx) > Math.abs(dy)) {
       // Va chạm ngang (trái hoặc phải)
       ball.setVelocity(new Velocity(-ball.getVelocity().getDx(), ball.getVelocity().getDy()));
   } else {
       // Va chạm dọc (trên hoặc dưới)
       ball.setVelocity(new Velocity(ball.getVelocity().getDx(), -ball.getVelocity().getDy()));
   }
   ```

**Minh họa:**
```
    Case 1: Va chạm ngang (|dx| > |dy|)
    
        ┌─────────┐
        │  GOLD   │
        │    ●──→ │  Bóng từ trái
        │         │
        └─────────┘
        
    → Đảo dx: velocity = (-dx, dy)
    
    
    Case 2: Va chạm dọc (|dy| > |dx|)
    
           ●
           ↓  Bóng từ trên
        ┌─────────┐
        │  GOLD   │
        │         │
        └─────────┘
        
    → Đảo dy: velocity = (dx, -dy)
```

**Ví dụ:**
```java
GoldBrick gold = new GoldBrick(300, 200, 50, 20, BrickType.GOLD);
Ball ball = new Ball(310, 190, 16, 16);
ball.setVelocity(new Velocity(4, 5)); // Bay sang phải và xuống

// Va chạm từ trên xuống
collisionManager.ignoreGoldBricksCollision(gold, ball);

// Kết quả:
// ballCenterX = 318, ballCenterY = 198
// brickCenterX = 325, brickCenterY = 210
// dx = 318 - 325 = -7
// dy = 198 - 210 = -12
// |dy| > |dx| → Va chạm dọc → Đảo dy
// velocity = (4, -5) ← Bóng bật lên
```

**Lưu ý:**
- Gạch vàng không bị phá → hoạt động như tường cứng
- Không gọi `brick.takeHit()` cho gạch vàng
- Không thêm vào `destroyedBricks`
- Có thể phát SFX khác với gạch thường (âm thanh "clanging")

---

## Sơ đồ luồng xử lý va chạm

### Flow 1: Ball Collision trong Game Loop
```
┌─────────────────────────────────────────┐
│   Game Loop (60 FPS)                    │
└────────────┬────────────────────────────┘
             │
             ↓
┌────────────────────────────────────────┐
│  1. Update Ball Position               │
│     ball.move(deltaTime)               │
└────────────┬───────────────────────────┘
             │
             ↓
┌────────────────────────────────────────┐
│  2. Check Wall Collisions              │
│     checkBallWallCollisions()          │
│     → Đảo vận tốc nếu chạm tường       │
└────────────┬───────────────────────────┘
             │
             ↓
┌────────────────────────────────────────┐
│  3. Check Brick Collisions             │
│     checkBallBrickCollisions()         │
│     → Phá gạch, spawn PowerUp          │
└────────────┬───────────────────────────┘
             │
             ↓
┌────────────────────────────────────────┐
│  4. Check Paddle Collision             │
│     checkBallPaddleCollision()         │
│     → Tính góc phản xạ                 │
└────────────┬───────────────────────────┘
             │
             ↓
┌────────────────────────────────────────┐
│  5. Check Bottom Border                │
│     if (ball.getY() > bottomBorder)    │
│     → Mất mạng / Game Over             │
└────────────────────────────────────────┘
```

### Flow 2: Laser & PowerUp Collision
```
┌─────────────────────────────────────────┐
│   Game Loop                             │
└────────────┬────────────────────────────┘
             │
      ┌──────┴──────┐
      │             │
      ↓             ↓
┌───────────┐  ┌──────────────┐
│  Lasers   │  │  PowerUps    │
│  Update   │  │  Update      │
└─────┬─────┘  └──────┬───────┘
      │                │
      ↓                ↓
┌─────────────┐  ┌──────────────────┐
│ checkLaser- │  │ checkPowerUp-    │
│ BrickColl() │  │ PaddleColl()     │
└─────┬───────┘  └──────┬───────────┘
      │                 │
      ↓                 ↓
┌─────────────┐  ┌──────────────────┐
│ Destroy     │  │ Apply PowerUp    │
│ Bricks      │  │ Effect           │
└─────────────┘  └──────────────────┘
```

---

## Ví dụ sử dụng

### Ví dụ 1: Khởi tạo trong GameManager
```java
public class GameManager {
    private CollisionManager collisionManager;
    private List<Ball> balls;
    private Paddle paddle;
    private List<Brick> bricks;
    private List<Laser> lasers;
    private List<PowerUp> powerUps;
    
    public void initialize() {
        // Khởi tạo CollisionManager với kích thước canvas
        collisionManager = new CollisionManager(
            Constants.Screen.PLAY_AREA_WIDTH,
            Constants.Screen.PLAY_AREA_HEIGHT
        );
        
        // Khởi tạo game objects...
        balls = new ArrayList<>();
        balls.add(new Ball(400, 300, 16, 16));
        
        paddle = new Paddle(350, 560, 100, 20);
        bricks = loadBricksFromRound(currentRound);
        lasers = new ArrayList<>();
        powerUps = new ArrayList<>();
    }
}
```

### Ví dụ 2: Update Loop đầy đủ
```java
public void update(double deltaTime) {
    // 1. Update vị trí của tất cả objects
    for (Ball ball : balls) {
        ball.move(deltaTime);
    }
    
    for (Laser laser : lasers) {
        laser.move(deltaTime);
    }
    
    for (PowerUp powerUp : powerUps) {
        powerUp.move(deltaTime);
    }
    
    // 2. Kiểm tra va chạm
    handleAllCollisions();
    
    // 3. Remove dead objects
    balls.removeIf(ball -> !ball.isAlive());
    lasers.removeIf(laser -> !laser.isAlive());
    powerUps.removeIf(powerUp -> !powerUp.isAlive());
    bricks.removeIf(brick -> !brick.isAlive());
}

private void handleAllCollisions() {
    // Biên giới
    double leftBorder = Constants.Border.LEFT_BORDER;
    double rightBorder = Constants.Border.RIGHT_BORDER;
    double topBorder = Constants.Border.TOP_BORDER;
    double bottomBorder = Constants.Border.BOTTOM_BORDER;
    
    // Xử lý từng bóng
    for (Ball ball : balls) {
        // Va chạm tường
        collisionManager.checkBallWallCollisions(ball, leftBorder, rightBorder, topBorder);
        
        // Va chạm gạch
        List<Brick> destroyed = collisionManager.checkBallBrickCollisions(ball, bricks);
        for (Brick brick : destroyed) {
            scoreManager.addScore(brick.getScore());
            spawnPowerUp(brick); // Có xác suất spawn PowerUp
        }
        
        // Va chạm paddle
        if (collisionManager.checkBallPaddleCollision(ball, paddle)) {
            if (paddle.isCatchModeEnabled()) {
                paddle.catchBall(ball); // Bắt bóng
            }
            audioManager.playSFX(SoundEffect.PADDLE_HIT);
        }
        
        // Kiểm tra rơi xuống đáy
        if (ball.getY() > bottomBorder) {
            ball.setAlive(false);
            loseLife();
        }
    }
    
    // Laser collisions
    Map<Laser, Brick> laserHits = collisionManager.checkLaserBrickCollisions(lasers, bricks);
    for (Map.Entry<Laser, Brick> entry : laserHits.entrySet()) {
        entry.getKey().setAlive(false); // Hủy laser
        if (entry.getValue().isDestroyed()) {
            scoreManager.addScore(entry.getValue().getScore());
        }
    }
    
    // PowerUp collection
    List<PowerUp> collected = collisionManager.checkPowerUpPaddleCollisions(powerUps, paddle);
    for (PowerUp p : collected) {
        p.setAlive(false);
        powerUpManager.applyPowerUp(p, paddle, balls, lasers);
        scoreManager.addScore(50);
        audioManager.playSFX(SoundEffect.POWERUP_COLLECT);
    }
}
```

### Ví dụ 3: Xử lý thay đổi resolution
```java
public class SettingsManager {
    private CollisionManager collisionManager;
    
    public void changeResolution(int newWidth, int newHeight) {
        // Update Constants
        Constants.Screen.PLAY_AREA_WIDTH = newWidth;
        Constants.Screen.PLAY_AREA_HEIGHT = newHeight;
        
        // Update CollisionManager
        collisionManager.setPlayAreaSize(newWidth, newHeight);
        
        // Re-scale game objects...
        rescaleGameObjects();
    }
}
```

### Ví dụ 4: Custom collision logic cho game mode đặc biệt
```java
public class BossMode extends GameMode {
    @Override
    public void handleCollisions() {
        // Gọi collision chuẩn
        super.handleCollisions();
        
        // Thêm logic va chạm cho Boss
        if (collisionManager.checkBallPaddleCollision(bossBall, paddle)) {
            // Boss ball có hiệu ứng đặc biệt
            paddle.takeDamage(10);
            bossBall.increaseSpeed(1.1); // Tăng tốc sau mỗi lần chạm
        }
    }
}
```

---

## Best Practices

### 1. Thứ tự kiểm tra va chạm
Luôn kiểm tra theo thứ tự:
1. **Tường** (ngăn bóng bay ra ngoài)
2. **Gạch** (game objective chính)
3. **Paddle** (tránh bóng rơi)
4. **Bottom border** (lose condition)

```java
// ✅ ĐÚNG: Thứ tự hợp lý
checkBallWallCollisions();
checkBallBrickCollisions();
checkBallPaddleCollision();
checkBottomBorder();

// ❌ SAI: Kiểm tra paddle trước tường
checkBallPaddleCollision(); // Có thể paddle chạm bóng đang ở ngoài biên
checkBallWallCollisions();
```

### 2. Tránh double collision
```java
// ✅ ĐÚNG: Kiểm tra alive status
for (Brick brick : bricks) {
    if (!brick.isAlive()) continue;
    // ... collision logic
}

// ❌ SAI: Không kiểm tra
for (Brick brick : bricks) {
    // Có thể xử lý gạch đã chết → crash hoặc bug điểm số
}
```

### 3. Performance optimization
```java
// ✅ ĐÚNG: Early return cho AABB
if (!ball.getBounds().intersects(brick.getBounds())) {
    return; // Không cần tính toán phức tạp
}
// ... swept collision

// ❌ SAI: Luôn dùng swept collision
// Tốn CPU cho 99% trường hợp không va chạm
```

### 4. Consistent collision response
```java
// ✅ ĐÚNG: Luôn đặt lại vị trí sau va chạm
ball.setY(paddle.getY() - ball.getHeight() - 1);
ball.setVelocity(newVelocity);

// ❌ SAI: Chỉ đổi vận tốc
ball.setVelocity(newVelocity);
// Bóng có thể bị "sink" vào paddle → multiple collisions
```

### 5. Sound effects placement
```java
// ✅ ĐÚNG: SFX trong collision check
if (collisionManager.checkBallPaddleCollision(ball, paddle)) {
    audioManager.playSFX(SoundEffect.PADDLE_HIT);
}

// ❌ SAI: SFX ở nơi khác
// Có thể miss SFX hoặc phát nhiều lần
```

### 6. Null safety
```java
// ✅ ĐÚNG: Kiểm tra null
if (paddle == null) return collected;

// ❌ SAI: Giả định paddle luôn tồn tại
// Crash khi game over hoặc transition
```

---

## Dependencies

### Imports
```java
import Objects.Bricks.BrickType;           // Enum loại gạch
import Objects.GameEntities.Ball;          // Đối tượng bóng
import Objects.GameEntities.Paddle;        // Đối tượng thanh đỡ
import Objects.GameEntities.Laser;         // Đối tượng laser
import Objects.Bricks.Brick;               // Base class gạch
import Objects.PowerUps.PowerUp;           // Base class PowerUp
import GeometryPrimitives.Velocity;        // Vector vận tốc (dx, dy)
import Utils.Constants;                    // Hằng số game

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
```

### Các lớp phụ thuộc

| Lớp | Vai trò | Phương thức sử dụng |
|-----|---------|---------------------|
| `Ball` | Đối tượng bóng | `getX()`, `getY()`, `getWidth()`, `getHeight()`, `getBounds()`, `getCenter()`, `getVelocity()`, `setVelocity()`, `setX()`, `setY()`, `checkCollisionWithRect()` |
| `Paddle` | Thanh đỡ | `getX()`, `getY()`, `getWidth()`, `getHeight()`, `getBounds()`, `isCatchModeEnabled()` |
| `Brick` | Gạch | `isAlive()`, `getBounds()`, `getBrickType()`, `takeHit()`, `isDestroyed()` |
| `Laser` | Tia laser | `isAlive()`, `getBounds()` |
| `PowerUp` | Vật phẩm bổ trợ | `isAlive()`, `checkPaddleCollision()` |
| `Velocity` | Vector vận tốc | `getDx()`, `getDy()`, Constructor `Velocity(dx, dy)` |
| `Constants` | Hằng số | `Constants.Paddle.PADDLE_MAX_ANGLE`, `Constants.Border.*` |

### Kiến trúc phụ thuộc
```
┌─────────────────────────────────────┐
│      CollisionManager               │
└───────────┬─────────────────────────┘
            │
            ├──→ Ball (checkCollisionWithRect)
            │    └──→ Rectangle (getBounds)
            │
            ├──→ Paddle (isCatchModeEnabled)
            │    └──→ Rectangle (getBounds)
            │
            ├──→ Brick (takeHit, isDestroyed)
            │    ├──→ BrickType enum
            │    └──→ Rectangle (getBounds)
            │
            ├──→ Laser
            │    └──→ Rectangle (getBounds)
            │
            ├──→ PowerUp (checkPaddleCollision)
            │
            ├──→ Velocity (dx, dy)
            │
            └──→ Constants (MAX_BOUNCE_ANGLE, Borders)
```

---

## Testing

### Unit Test Example
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CollisionManagerTest {
    @Test
    void testBallWallCollision_Left() {
        CollisionManager cm = new CollisionManager(800, 600);
        Ball ball = new Ball(45, 300, 16, 16);
        ball.setVelocity(new Velocity(-5, 0)); // Bay sang trái
        
        cm.checkBallWallCollisions(ball, 50, 750, 100);
        
        // Kiểm tra vị trí đã được điều chỉnh
        assertEquals(50, ball.getX(), 0.01);
        
        // Kiểm tra vận tốc đã đảo chiều
        assertTrue(ball.getVelocity().getDx() > 0); // dx dương (bay sang phải)
    }
    
    @Test
    void testBallPaddleCollision_Center() {
        CollisionManager cm = new CollisionManager(800, 600);
        Ball ball = new Ball(400, 555, 16, 16);
        Paddle paddle = new Paddle(350, 560, 100, 20);
        
        boolean collided = cm.checkBallPaddleCollision(ball, paddle);
        
        assertTrue(collided);
        // Bóng chạm giữa thanh đỡ → góc = 0° → bay thẳng lên
        assertEquals(0, ball.getVelocity().getDx(), 0.1);
        assertTrue(ball.getVelocity().getDy() < 0); // dy âm (bay lên)
    }
    
    @Test
    void testBallBrickCollision() {
        CollisionManager cm = new CollisionManager(800, 600);
        Ball ball = new Ball(110, 110, 16, 16);
        
        List<Brick> bricks = new ArrayList<>();
        bricks.add(new NormalBrick(100, 100, 50, 20, BrickType.RED, 1));
        
        List<Brick> destroyed = cm.checkBallBrickCollisions(ball, bricks);
        
        assertEquals(1, destroyed.size()); // 1 gạch bị phá
        assertTrue(destroyed.get(0).isDestroyed());
    }
    
    @Test
    void testGoldBrickReflection() {
        CollisionManager cm = new CollisionManager(800, 600);
        Ball ball = new Ball(110, 110, 16, 16);
        ball.setVelocity(new Velocity(0, 5)); // Bay xuống
        
        List<Brick> bricks = new ArrayList<>();
        bricks.add(new GoldBrick(100, 120, 50, 20, BrickType.GOLD));
        
        List<Brick> destroyed = cm.checkBallBrickCollisions(ball, bricks);
        
        assertEquals(0, destroyed.size()); // Gạch vàng không bị phá
        assertTrue(ball.getVelocity().getDy() < 0); // Bóng bật lên (dy âm)
    }
}
```

---

## Known Issues & Limitations

### 1. High-speed ball tunneling
**Vấn đề:** Bóng có tốc độ quá cao có thể "xuyên qua" gạch mỏng.

**Giải pháp hiện tại:** 
- Sử dụng swept collision trong `ball.checkCollisionWithRect()`
- Giới hạn tốc độ tối đa của bóng

**Cải thiện tương lai:**
```java
// Continuous collision detection (CCD)
public boolean checkContinuousCollision(Ball ball, Brick brick, double deltaTime) {
    // Kiểm tra va chạm dọc theo path từ oldPos → newPos
}
```

### 2. Corner collision ambiguity
**Vấn đề:** Va chạm ở góc gạch không rõ ràng nên đảo ngang hay dọc.

**Giải pháp hiện tại:**
- Dùng `Math.abs(dx) > Math.abs(dy)` để quyết định
- Ưu tiên va chạm ngang nếu `|dx| > |dy|`

**Cải thiện tương lai:**
```java
// Vector projection để xác định chính xác cạnh va chạm
private CollisionSide detectCollisionSide(Ball ball, Brick brick) {
    // Project velocity vector onto brick edges
}
```

### 3. Multiple balls performance
**Vấn đề:** Nhiều bóng (> 10) làm giảm FPS do O(n × m) checks.

**Giải pháp hiện tại:**
- Early return với AABB check
- Chỉ kiểm tra gạch còn sống

**Cải thiện tương lai:**
```java
// Spatial partitioning (Quadtree, Grid)
private Quadtree<Brick> brickTree;

public List<Brick> getNearbyBricks(Ball ball) {
    return brickTree.query(ball.getBounds());
}
```

### 4. Catch mode edge cases
**Vấn đề:** Bóng có thể bị "catch" khi va chạm cạnh thanh đỡ.

**Giải pháp hiện tại:**
- Return `true` ngay khi `isCatchModeEnabled()`
- Paddle tự xử lý logic catch

**Cải thiện tương lai:**
```java
// Chỉ catch nếu va chạm từ trên xuống
if (paddle.isCatchModeEnabled() && ball.getVelocity().getDy() > 0) {
    return true;
}
```

---

## Mở rộng trong tương lai

### 1. Collision layers & filtering
```java
public enum CollisionLayer {
    BALL(0b0001),
    PADDLE(0b0010),
    BRICK(0b0100),
    LASER(0b1000);
    
    private final int mask;
    
    CollisionLayer(int mask) {
        this.mask = mask;
    }
    
    public boolean canCollideWith(CollisionLayer other) {
        return (this.mask & other.mask) != 0;
    }
}

// Usage:
if (ball.getLayer().canCollideWith(brick.getLayer())) {
    // Check collision
}
```

### 2. Collision callbacks
```java
public interface CollisionCallback {
    void onCollisionEnter(GameObject a, GameObject b);
    void onCollisionStay(GameObject a, GameObject b);
    void onCollisionExit(GameObject a, GameObject b);
}

public class CollisionManager {
    private List<CollisionCallback> callbacks = new ArrayList<>();
    
    public void registerCallback(CollisionCallback callback) {
        callbacks.add(callback);
    }
    
    private void notifyCollision(GameObject a, GameObject b) {
        for (CollisionCallback callback : callbacks) {
            callback.onCollisionEnter(a, b);
        }
    }
}

// Usage:
collisionManager.registerCallback((a, b) -> {
    if (a instanceof Ball && b instanceof Brick) {
        audioManager.playSFX(SoundEffect.BRICK_HIT);
    }
});
```

### 3. Physics material system
```java
public class PhysicsMaterial {
    private double bounciness;    // 0.0 = không bật, 1.0 = bật hoàn toàn
    private double friction;      // 0.0 = trơn, 1.0 = nhám
    
    public PhysicsMaterial(double bounciness, double friction) {
        this.bounciness = bounciness;
        this.friction = friction;
    }
}

public class CollisionManager {
    private Velocity calculateBounce(Velocity incoming, PhysicsMaterial material) {
        // Energy loss: E_out = E_in × bounciness²
        double speed = incoming.magnitude() * material.bounciness;
        // ... calculate new velocity with energy loss
    }
}
```

### 4. Debug visualization
```java
public class CollisionManager {
    private boolean debugMode = false;
    private List<Rectangle> debugRects = new ArrayList<>();
    
    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
    }
    
    public void renderDebug(GraphicsContext gc) {
        if (!debugMode) return;
        
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        
        for (Rectangle rect : debugRects) {
            gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        }
    }
    
    private void recordDebugCollision(Rectangle bounds) {
        if (debugMode) {
            debugRects.add(bounds);
        }
    }
}
```

### 5. Collision events system
```java
public class CollisionEvent {
    private GameObject objectA;
    private GameObject objectB;
    private Point contactPoint;
    private Velocity relativeVelocity;
    private double timestamp;
    
    // Getters...
}

public class CollisionManager {
    private List<CollisionEvent> eventHistory = new ArrayList<>();
    
    public List<CollisionEvent> getCollisionHistory() {
        return new ArrayList<>(eventHistory);
    }
    
    private void recordEvent(GameObject a, GameObject b, Point contact) {
        CollisionEvent event = new CollisionEvent(a, b, contact, 
                                                   a.getVelocity(), 
                                                   System.currentTimeMillis());
        eventHistory.add(event);
        
        // Giới hạn lịch sử
        if (eventHistory.size() > 100) {
            eventHistory.remove(0);
        }
    }
}
```

### 6. Advanced bounce physics
```java
private void calculateAdvancedBounce(Ball ball, Paddle paddle, double hitPosition) {
    // Spin effect: Paddle đang di chuyển → thêm "spin" vào bóng
    double paddleVelocity = paddle.getVelocity().getDx();
    double spinFactor = 0.3; // 30% spin transfer
    
    double speed = ball.getVelocity().magnitude();
    double angle = hitPosition * MAX_BOUNCE_ANGLE;
    
    double angleRad = Math.toRadians(angle);
    double dx = speed * Math.sin(angleRad) + paddleVelocity * spinFactor;
    double dy = -speed * Math.cos(angleRad);
    
    // Normalize để giữ tốc độ không đổi
    Velocity newVel = new Velocity(dx, dy).normalized().multiply(speed);
    ball.setVelocity(newVel);
}
```

---

## Tài liệu tham khảo

### Thuật toán Collision Detection
- **AABB (Axis-Aligned Bounding Box):** 
  - Thuật toán đơn giản nhất, kiểm tra overlap của 2 hình chữ nhật
  - O(1) complexity
  - Không chính xác cho va chạm góc độ cao

- **Swept AABB:**
  - Kiểm tra va chạm dọc theo path di chuyển
  - Tránh tunneling cho high-speed objects
  - O(1) complexity, nhưng phức tạp hơn

- **Circle-Rectangle Collision:**
  - Dùng cho bóng tròn vs gạch chữ nhật
  - Tìm điểm gần nhất trên rect → tính khoảng cách với center circle

### Công thức Trigonometry
```
sin(θ) = đối / huyền
cos(θ) = kề / huyền
tan(θ) = đối / kề

atan2(y, x) → góc từ vector (x, y)
Math.hypot(x, y) → √(x² + y²)
```

### Hệ tọa độ JavaFX
- Gốc tọa độ (0, 0) ở **góc trên trái**
- X tăng → **phải**
- Y tăng → **xuống** (ngược với toán học)

### Link tham khảo
- [JavaFX Canvas API](https://openjfx.io/javadoc/11/javafx.graphics/javafx/scene/canvas/Canvas.html)
- [Game Physics Tutorial](https://www.toptal.com/game/video-game-physics-part-i-an-introduction-to-rigid-body-dynamics)
- [Collision Detection](https://developer.mozilla.org/en-US/docs/Games/Techniques/2D_collision_detection)

---

## Tổng kết

`CollisionManager` là một trong những lớp quan trọng nhất của game engine, đảm bảo:
- ✅ **Chính xác:** Swept collision cho high-speed objects
- ✅ **Hiệu năng:** Early return với AABB checks
- ✅ **Linh hoạt:** Xử lý đa dạng loại va chạm (ball, laser, powerup)
- ✅ **Mở rộng:** Dễ dàng thêm collision types mới
- ✅ **Dễ bảo trì:** Single responsibility, rõ ràng và có tổ chức

Lớp này là nền tảng cho gameplay mechanics, đảm bảo tất cả tương tác vật lý hoạt động chính xác và mượt mà.

---

**Tác giả:** Arkanoid Development Team  
**Phiên bản:** 1.0  
**Cập nhật lần cuối:** 2024
