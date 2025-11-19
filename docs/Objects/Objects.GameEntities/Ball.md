# Ball Class

## Tổng quan
`Ball` là lớp đại diện cho quả bóng trong game Arkanoid - một trong những đối tượng quan trọng nhất của trò chơi. Bóng di chuyển liên tục trên màn hình, va chạm với các đối tượng khác (gạch, paddle, tường) và phản xạ theo quy luật vật lý. Đây là đối tượng chính để phá hủy gạch và ghi điểm.

## Vị trí
- **Package**: `Objects.GameEntities`
- **File**: `src/Objects/GameEntities/Ball.java`
- **Kế thừa**: `MovableObject`
- **Implements**: `GameObject` (gián tiếp qua MovableObject)

## Mục đích
Lớp Ball:
- Quản lý chuyển động của bóng với vận tốc và hướng
- Xử lý va chạm phức tạp với các hình chữ nhật (gạch, paddle, tường)
- Hỗ trợ tính năng gắn bóng vào paddle (catch power-up)
- Sử dụng thuật toán swept-circle collision detection chính xác

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
Ball (Concrete Class)
```

---

## Thuộc tính (Fields)

### 1. `private double radius`
**Mô tả**: Bán kính của bóng tròn.

**Ý nghĩa**: 
- Xác định kích thước bóng trên màn hình
- Sử dụng trong tính toán va chạm
- Bounding box có kích thước `width = height = radius * 2`

**Ví dụ giá trị**: `8.0` (bóng có đường kính 16 pixels)

---

### 2. `private double bounceCoefficient`
**Mô tả**: Hệ số đàn hồi (coefficient of restitution) của bóng.

**Ý nghĩa**: 
- Xác định mức độ giữ vận tốc sau va chạm
- Giá trị `1.0` = va chạm đàn hồi hoàn toàn (không mất năng lượng)
- Giá trị `< 1.0` = bóng mất vận tốc sau mỗi va chạm
- Giá trị `> 1.0` = bóng tăng tốc sau va chạm (không thực tế)

**Giá trị mặc định**: `1.0`

**Sử dụng**:
```java
// Va chạm bình thường - giữ nguyên vận tốc
ball.bounceCoefficient = 1.0;

// Bóng chậm dần - mất 10% vận tốc mỗi lần va chạm
ball.bounceCoefficient = 0.9;
```

---

### 3. `private boolean isAttached`
**Mô tả**: Cờ đánh dấu bóng đang gắn vào paddle hay không.

**Ý nghĩa**: 
- `true` = bóng đang gắn vào paddle (catch power-up active)
- `false` = bóng đang di chuyển tự do

**Giá trị mặc định**: `false`

**Sử dụng**:
```java
// Khi paddle bắt bóng (catch power-up)
if (paddle.isCatchModeEnabled() && ball.getBounds().intersects(paddle.getBounds())) {
    ball.setAttached(true);
    ball.setVelocity(new Velocity(0, 0)); // Dừng bóng
}

// Khi người chơi thả bóng
if (ball.isAttached() && spacePressed) {
    ball.setAttached(false);
    ball.setVelocity(new Velocity(5, -5)); // Bắn bóng
}
```

---

## Constructor

### `Ball(double centerX, double centerY, double radius, Velocity initialVelocity)`

**Mô tả**: Khởi tạo một quả bóng mới với tâm, bán kính và vận tốc ban đầu.

**Tham số**:
- `centerX` - tọa độ X của tâm bóng
- `centerY` - tọa độ Y của tâm bóng  
- `radius` - bán kính của bóng
- `initialVelocity` - vận tốc ban đầu (dx, dy)

**Hành vi**:
1. Tính toán bounding box: góc trên trái = `(centerX - radius, centerY - radius)`
2. Kích thước bounding box = `(radius * 2, radius * 2)`
3. Gọi `super()` để khởi tạo MovableObject
4. Thiết lập bán kính và vận tốc

**Lưu ý quan trọng**: 
- Bóng sử dụng **tâm** làm tham chiếu chính, không phải góc trên trái
- Bounding box được tự động tính từ tâm và bán kính
- Điều này giúp các phép tính hình học dễ dàng hơn

**Ví dụ**:
```java
// Tạo bóng ở giữa màn hình, bắn chéo lên trên
double centerX = SCREEN_WIDTH / 2;
double centerY = SCREEN_HEIGHT - 100;
double radius = 8;
Velocity initialVel = new Velocity(5, -5); // Bay chéo lên trên-phải

Ball ball = new Ball(centerX, centerY, radius, initialVel);

// Tạo bóng đứng yên (cho catch power-up)
Ball attachedBall = new Ball(paddle.getX() + 60, paddle.getY() - 16, 8, new Velocity(0, 0));
attachedBall.setAttached(true);
```

---

## Phương thức

### 1. `Point getCenter()`

**Mô tả**: Lấy tọa độ tâm của bóng.

**Kiểu trả về**: `Point` - điểm tâm `(centerX, centerY)`

**Công thức**:
```
centerX = x + radius
centerY = y + radius
```
Trong đó `(x, y)` là góc trên trái của bounding box

**Sử dụng**:
```java
// Vẽ bóng ở tâm
Point center = ball.getCenter();
graphics.drawCircle(center.getX(), center.getY(), ball.radius);

// Kiểm tra bóng rơi ra khỏi màn hình
if (ball.getCenter().getY() > SCREEN_HEIGHT) {
    loseLife();
}

// Tính khoảng cách giữa bóng và một điểm
Point target = new Point(100, 200);
double distance = Math.hypot(
    ball.getCenter().getX() - target.getX(),
    ball.getCenter().getY() - target.getY()
);
```

---

### 2. `void setCenter(Point p)`

**Mô tả**: Đặt vị trí tâm mới cho bóng, tự động cập nhật bounding box.

**Tham số**: `p` - điểm tâm mới

**Hành vi**:
```
x = p.getX() - radius
y = p.getY() - radius
```

**Sử dụng**:
```java
// Đặt bóng lên trên paddle khi bắt đầu
Point paddleTop = new Point(
    paddle.getX() + paddle.getWidth() / 2,
    paddle.getY() - ball.radius
);
ball.setCenter(paddleTop);

// Đưa bóng về giữa màn hình
ball.setCenter(new Point(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2));

// Di chuyển bóng đến vị trí cụ thể (teleport)
ball.setCenter(new Point(targetX, targetY));
```

---

### 3. `void update()`

**Mô tả**: Cập nhật vị trí bóng trong mỗi frame (override từ MovableObject).

**Hành vi**: Gọi `move()` để cập nhật vị trí dựa trên vận tốc hiện tại.

**Công thức**:
```
x += velocity.getDx()
y += velocity.getDy()
```

**Sử dụng**:
```java
// Trong game loop
@Override
public void gameLoop() {
    // Cập nhật tất cả bóng
    for (Ball ball : balls) {
        if (ball.isAlive() && !ball.isAttached()) {
            ball.update(); // Di chuyển bóng
        }
    }
    
    // Sau đó kiểm tra va chạm
    checkCollisions();
}
```

**Lưu ý**: 
- Phương thức này CHỈ di chuyển bóng
- KHÔNG kiểm tra va chạm hoặc boundaries
- Collision detection phải được xử lý riêng bởi GameManager/CollisionManager

---

### 4. `boolean checkCollisionWithRect(Rectangle rect)` ⭐ QUAN TRỌNG

**Mô tả**: Kiểm tra và xử lý va chạm giữa bóng và một hình chữ nhật (paddle, gạch, tường). Đây là phương thức phức tạp và quan trọng nhất của Ball.

**Tham số**: `rect` - hình chữ nhật cần kiểm tra va chạm

**Kiểu trả về**: 
- `true` - có va chạm và đã xử lý phản xạ
- `false` - không có va chạm

**Thuật toán**: Swept-Circle vs AABB (Axis-Aligned Bounding Box)

#### Nguyên lý hoạt động:

**Bước 1: Mở rộng hình chữ nhật (Inflate Rectangle)**
```
Thay vì kiểm tra bóng tròn vs rect,
Mở rộng rect theo bán kính bóng và coi bóng như một điểm
=> Swept-point vs inflated-rect

Inflated Rectangle:
  x = rect.x - radius
  y = rect.y - radius
  width = rect.width + 2 * radius
  height = rect.height + 2 * radius
```

**Bước 2: Tìm điểm va chạm (Intersection Point)**
```
Tạo đường đi của tâm bóng:
  Line trajectory = Line(currentCenter, nextCenter)

Tìm điểm giao đầu tiên với inflated rect:
  Point hit = trajectory.closestIntersectionToStartOfLine(inflated)

Nếu hit == null => Không va chạm
```

**Bước 3: Tính vector pháp tuyến (Normal Vector)**
```
Tìm điểm gần nhất trên rect gốc:
  closestX = clamp(hit.x, rect.x, rect.x + rect.width)
  closestY = clamp(hit.y, rect.y, rect.y + rect.height)

Vector pháp tuyến (hướng ra ngoài):
  nx = hit.x - closestX
  ny = hit.y - closestY
  
Chuẩn hóa: (nx, ny) /= length
```

**Bước 4: Phản xạ vận tốc (Velocity Reflection)**
```
Công thức phản xạ:
  v' = v - 2 * (v · n) * n

Trong đó:
  v = vận tốc hiện tại (dx, dy)
  n = vector pháp tuyến đã chuẩn hóa (nx, ny)
  v · n = tích vô hướng (dot product)

Áp dụng hệ số đàn hồi:
  newVelocity = reflectedVelocity * bounceCoefficient
```

**Bước 5: Giải quyết xuyên thủng (Penetration Resolution)**
```
Tính độ xuyên vào:
  penetration = max(0, radius - distance(closest, hit))

Đẩy bóng ra ngoài:
  pushOut = penetration + epsilon
  newCenter = closest + n * (radius + pushOut)
```

#### Trường hợp đặc biệt (Degenerate Case):

Khi vector pháp tuyến có độ dài ≈ 0 (bóng va chạm chính xác vào góc):
```java
if (len < EPSILON) {
    // Xác định trục gần nhất để phản xạ
    double midX = rect.x + rect.width / 2;
    double midY = rect.y + rect.height / 2;
    
    double diffX = abs(hit.x - midX) / (rect.width / 2);
    double diffY = abs(hit.y - midY) / (rect.height / 2);
    
    if (diffX > diffY) {
        dx = -dx; // Phản xạ ngang
    } else {
        dy = -dy; // Phản xạ dọc
    }
}
```

#### Ví dụ sử dụng:

```java
// Kiểm tra va chạm với paddle
if (ball.checkCollisionWithRect(paddle.getBounds())) {
    playBounceSound();
    // Có thể thêm spin effect dựa trên vị trí va chạm
    addPaddleSpin(ball, paddle);
}

// Kiểm tra va chạm với gạch
for (Brick brick : bricks) {
    if (!brick.isAlive()) continue;
    
    if (ball.checkCollisionWithRect(brick.getBounds())) {
        brick.takeDamage();
        playHitSound();
        
        if (!brick.isAlive()) {
            scoreManager.addPoints(brick.getPoints());
            spawnPowerUp(brick);
        }
        break; // Chỉ xử lý 1 gạch mỗi frame
    }
}

// Kiểm tra va chạm với tường
Rectangle topWall = new Rectangle(new Point(0, 0), SCREEN_WIDTH, 10);
Rectangle leftWall = new Rectangle(new Point(0, 0), 10, SCREEN_HEIGHT);
Rectangle rightWall = new Rectangle(new Point(SCREEN_WIDTH - 10, 0), 10, SCREEN_HEIGHT);

ball.checkCollisionWithRect(topWall);
ball.checkCollisionWithRect(leftWall);
ball.checkCollisionWithRect(rightWall);

// Kiểm tra bóng rơi xuống đáy
if (ball.getCenter().getY() > SCREEN_HEIGHT) {
    ball.destroy();
    loseLife();
}
```

#### Tại sao thuật toán này chính xác?

1. **Swept-Circle**: Kiểm tra toàn bộ đường đi, không bỏ sót va chạm tốc độ cao
2. **Minkowski Sum**: Đơn giản hóa bài toán circle-rect thành point-rect
3. **Normal Reflection**: Phản xạ chính xác theo pháp tuyến bề mặt
4. **Penetration Resolution**: Ngăn bóng "застрять" (kẹt) trong đối tượng

---

### 5. `void setAttached(boolean attached)`

**Mô tả**: Đặt trạng thái gắn/không gắn của bóng.

**Tham số**: `attached` - `true` để gắn bóng, `false` để thả

**Sử dụng**:
```java
// Kích hoạt catch power-up
public void activateCatchPowerUp() {
    paddle.enableCatch();
    
    // Khi bóng chạm paddle
    if (paddle.isCatchModeEnabled() && ballHitsPaddle) {
        ball.setAttached(true);
        ball.setVelocity(new Velocity(0, 0));
        
        // Đặt bóng lên paddle
        Point attachPoint = new Point(
            paddle.getX() + paddle.getWidth() / 2,
            paddle.getY() - ball.radius
        );
        ball.setCenter(attachPoint);
    }
}

// Thả bóng khi nhấn Space
if (ball.isAttached() && keyboard.isKeyPressed(KeyCode.SPACE)) {
    ball.setAttached(false);
    
    // Bắn bóng lên trên
    double angle = -Math.PI / 2; // 90 độ lên trên
    double speed = 5;
    ball.setVelocity(new Velocity(
        speed * Math.cos(angle),
        speed * Math.sin(angle)
    ));
}
```

---

### 6. `boolean isAttached()`

**Mô tả**: Kiểm tra bóng có đang gắn vào paddle không.

**Kiểu trả về**: `boolean`
- `true` - bóng đang gắn
- `false` - bóng đang di chuyển tự do

**Sử dụng**:
```java
// Trong game loop - chỉ update bóng đang di chuyển
for (Ball ball : balls) {
    if (ball.isAlive() && !ball.isAttached()) {
        ball.update();
        checkBallCollisions(ball);
    }
}

// Đồng bộ vị trí bóng với paddle khi gắn
if (ball.isAttached()) {
    Point attachPoint = new Point(
        paddle.getX() + paddle.getWidth() / 2,
        paddle.getY() - ball.radius
    );
    ball.setCenter(attachPoint);
}

// UI hiển thị hướng dẫn
if (ball.isAttached()) {
    drawText("Press SPACE to launch ball", centerX, centerY);
}
```

---

## Luồng hoạt động điển hình

### Scenario 1: Bóng di chuyển bình thường

```
1. Game Loop bắt đầu frame mới
   ↓
2. ball.update()
   ↓ 
3. move() → cập nhật x, y dựa trên velocity
   ↓
4. CollisionManager.checkBallCollisions(ball)
   ↓
5. Kiểm tra va chạm với walls
   ball.checkCollisionWithRect(topWall)
   ball.checkCollisionWithRect(leftWall) 
   ball.checkCollisionWithRect(rightWall)
   ↓
6. Kiểm tra va chạm với paddle
   if (ball.checkCollisionWithRect(paddle.getBounds())) {
       // Xử lý catch mode, spin effect, v.v.
   }
   ↓
7. Kiểm tra va chạm với bricks
   for (Brick brick : bricks) {
       if (ball.checkCollisionWithRect(brick.getBounds())) {
           brick.takeDamage();
           break;
       }
   }
   ↓
8. Kiểm tra bóng rơi xuống
   if (ball.getCenter().getY() > SCREEN_HEIGHT) {
       ball.destroy();
       loseLife();
   }
   ↓
9. Render ball
```

### Scenario 2: Catch Power-Up

```
1. Power-up được thu thập
   ↓
2. paddle.enableCatch()
   catchMode = true
   ↓
3. Bóng va chạm paddle
   ball.checkCollisionWithRect(paddle.getBounds()) = true
   ↓
4. Kiểm tra catch mode
   if (paddle.isCatchModeEnabled()) {
       ball.setAttached(true);
       ball.setVelocity(new Velocity(0, 0));
   }
   ↓
5. Trong game loop - bóng gắn với paddle
   if (ball.isAttached()) {
       // Không gọi ball.update()
       // Đồng bộ vị trí với paddle
       syncBallToPaddle(ball, paddle);
   }
   ↓
6. Player nhấn Space
   ball.setAttached(false);
   ball.setVelocity(calculateLaunchVelocity());
   ↓
7. Bóng bay tự do trở lại
```

### Scenario 3: Duplicate Power-Up

```
1. Duplicate power-up activated
   ↓
2. Tạo bóng mới
   for (Ball existingBall : balls) {
       Ball newBall = new Ball(
           existingBall.getCenter().getX(),
           existingBall.getCenter().getY(),
           existingBall.radius,
           calculateNewVelocity() // Hướng khác
       );
       balls.add(newBall);
   }
   ↓
3. Tất cả bóng hoạt động độc lập
   for (Ball ball : balls) {
       ball.update();
       checkCollisions(ball);
   }
```

---

## Tích hợp với các hệ thống khác

### 1. CollisionManager

```java
public class CollisionManager {
    public void checkBallCollisions(Ball ball) {
        // Tường
        ball.checkCollisionWithRect(topWall);
        ball.checkCollisionWithRect(leftWall);
        ball.checkCollisionWithRect(rightWall);
        
        // Paddle
        if (ball.checkCollisionWithRect(paddle.getBounds())) {
            handleBallPaddleCollision(ball, paddle);
        }
        
        // Gạch
        for (Brick brick : bricks) {
            if (brick.isAlive() && ball.checkCollisionWithRect(brick.getBounds())) {
                handleBallBrickCollision(ball, brick);
                break; // Chỉ 1 gạch/frame
            }
        }
    }
    
    private void handleBallPaddleCollision(Ball ball, Paddle paddle) {
        audioManager.playBounceSound();
        
        // Catch power-up
        if (paddle.isCatchModeEnabled()) {
            ball.setAttached(true);
            ball.setVelocity(new Velocity(0, 0));
        } else {
            // Thêm spin effect
            addPaddleSpin(ball, paddle);
        }
    }
    
    private void handleBallBrickCollision(Ball ball, Brick brick) {
        brick.takeDamage();
        audioManager.playHitSound();
        
        if (!brick.isAlive()) {
            scoreManager.addPoints(brick.getPoints());
            
            if (shouldSpawnPowerUp()) {
                spawnPowerUp(brick.getX(), brick.getY());
            }
        }
    }
}
```

### 2. GameManager

```java
public class GameManager {
    private List<Ball> balls = new ArrayList<>();
    
    public void update() {
        // Update balls
        for (Ball ball : balls) {
            if (ball.isAlive()) {
                if (!ball.isAttached()) {
                    ball.update();
                    collisionManager.checkBallCollisions(ball);
                } else {
                    syncBallToPaddle(ball);
                }
            }
        }
        
        // Remove dead balls
        balls.removeIf(ball -> !ball.isAlive());
        
        // Check game over
        if (balls.isEmpty()) {
            loseLife();
            if (lives > 0) {
                spawnBall();
            } else {
                gameOver();
            }
        }
    }
    
    private void syncBallToPaddle(Ball ball) {
        Point attachPoint = new Point(
            paddle.getX() + paddle.getWidth() / 2,
            paddle.getY() - ball.radius
        );
        ball.setCenter(attachPoint);
    }
    
    public void launchAttachedBalls() {
        for (Ball ball : balls) {
            if (ball.isAttached()) {
                ball.setAttached(false);
                ball.setVelocity(new Velocity(0, -5)); // Thẳng lên
            }
        }
    }
}
```

### 3. PowerUpManager

```java
public class PowerUpManager {
    public void applyDuplicatePowerUp() {
        List<Ball> newBalls = new ArrayList<>();
        
        for (Ball ball : gameManager.getBalls()) {
            if (!ball.isAttached()) {
                // Tạo 2 bóng mới với hướng khác nhau
                Velocity v = ball.getVelocity();
                double angle1 = Math.atan2(v.getDy(), v.getDx()) + Math.PI / 6;
                double angle2 = Math.atan2(v.getDy(), v.getDx()) - Math.PI / 6;
                double speed = Math.hypot(v.getDx(), v.getDy());
                
                Ball ball1 = new Ball(
                    ball.getCenter().getX(),
                    ball.getCenter().getY(),
                    ball.radius,
                    new Velocity(speed * Math.cos(angle1), speed * Math.sin(angle1))
                );
                
                Ball ball2 = new Ball(
                    ball.getCenter().getX(),
                    ball.getCenter().getY(),
                    ball.radius,
                    new Velocity(speed * Math.cos(angle2), speed * Math.sin(angle2))
                );
                
                newBalls.add(ball1);
                newBalls.add(ball2);
            }
        }
        
        gameManager.addBalls(newBalls);
    }
    
    public void applySlowBallPowerUp() {
        for (Ball ball : gameManager.getBalls()) {
            Velocity v = ball.getVelocity();
            ball.setVelocity(new Velocity(v.getDx() * 0.5, v.getDy() * 0.5));
        }
    }
}
```

---

## Các tính năng nâng cao

### 1. Paddle Spin Effect

Thêm hiệu ứng spin dựa trên vị trí va chạm với paddle:

```java
public void addPaddleSpin(Ball ball, Paddle paddle) {
    // Tính vị trí tương đối trên paddle (0 = trái, 1 = phải)
    double hitPos = (ball.getCenter().getX() - paddle.getX()) / paddle.getWidth();
    
    // Tính góc phản xạ (-60° đến +60°)
    double reflectionAngle = (hitPos - 0.5) * Math.PI / 1.5; // 60° = π/3
    
    // Giữ nguyên tốc độ
    Velocity v = ball.getVelocity();
    double speed = Math.hypot(v.getDx(), v.getDy());
    
    // Áp dụng góc mới (luôn hướng lên)
    double newDx = speed * Math.sin(reflectionAngle);
    double newDy = -speed * Math.cos(reflectionAngle);
    
    ball.setVelocity(new Velocity(newDx, newDy));
}
```

### 2. Speed Limiter

Ngăn bóng di chuyển quá nhanh hoặc quá chậm:

```java
public void limitBallSpeed(Ball ball) {
    Velocity v = ball.getVelocity();
    double speed = Math.hypot(v.getDx(), v.getDy());
    
    final double MIN_SPEED = 3.0;
    final double MAX_SPEED = 10.0;
    
    if (speed < MIN_SPEED) {
        // Tăng tốc nếu quá chậm
        double scale = MIN_SPEED / speed;
        ball.setVelocity(new Velocity(v.getDx() * scale, v.getDy() * scale));
    } else if (speed > MAX_SPEED) {
        // Giảm tốc nếu quá nhanh
        double scale = MAX_SPEED / speed;
        ball.setVelocity(new Velocity(v.getDx() * scale, v.getDy() * scale));
    }
}
```

### 3. Trail Effect

Tạo hiệu ứng đuôi cho bóng:

```java
public class BallTrail {
    private Queue<Point> trail = new LinkedList<>();
    private final int MAX_TRAIL_LENGTH = 10;
    
    public void update(Ball ball) {
        trail.add(ball.getCenter());
        
        if (trail.size() > MAX_TRAIL_LENGTH) {
            trail.poll();
        }
    }
    
    public void render(Graphics g) {
        int alpha = 255;
        int step = 255 / trail.size();
        
        for (Point p : trail) {
            g.setColor(new Color(255, 255, 255, alpha));
            g.fillCircle(p.getX(), p.getY(), ball.radius * 0.8);
            alpha -= step;
        }
    }
}
```

---

## Best Practices

### 1. Collision Detection
```java
// ✅ Đúng - kiểm tra theo thứ tự ưu tiên
ball.checkCollisionWithRect(paddle.getBounds());
for (Brick brick : bricks) {
    if (ball.checkCollisionWithRect(brick.getBounds())) {
        break; // Chỉ 1 gạch/frame
    }
}

// ❌ Sai - không break, xử lý nhiều va chạm trong 1 frame
for (Brick brick : bricks) {
    ball.checkCollisionWithRect(brick.getBounds()); // Lỗi logic
}
```

### 2. Attached Ball
```java
// ✅ Đúng - không update khi attached
if (!ball.isAttached()) {
    ball.update();
    checkCollisions(ball);
} else {
    syncBallToPaddle(ball);
}

// ❌ Sai - vẫn update khi attached
ball.update(); // Bóng sẽ rơi xuống
```

### 3. Multiple Balls
```java
// ✅ Đúng - sử dụng ConcurrentModification-safe
List<Ball> ballsToRemove = new ArrayList<>();
for (Ball ball : balls) {
    if (!ball.isAlive()) {
        ballsToRemove.add(ball);
    }
}
balls.removeAll(ballsToRemove);

// Hoặc dùng Iterator
balls.removeIf(ball -> !ball.isAlive());

// ❌ Sai - ConcurrentModificationException
for (Ball ball : balls) {
    if (!ball.isAlive()) {
        balls.remove(ball); // CRASH!
    }
}
```

---

## Kết luận

Lớp `Ball` là một trong những component phức tạp nhất trong game Arkanoid:

- **Chuyển động**: Kế thừa hệ thống di chuyển từ MovableObject
- **Va chạm**: Sử dụng thuật toán swept-circle chính xác
- **Tính năng**: Hỗ trợ catch power-up, bounce coefficient
- **Tích hợp**: Làm việc với CollisionManager, PowerUpManager, GameManager

Thuật toán va chạm của Ball là nền tảng cho gameplay physics chính xác và mượt mà, đảm bảo bóng phản xạ đúng trong mọi tình huống, kể cả tốc độ cao hay góc va chạm phức tạp.
