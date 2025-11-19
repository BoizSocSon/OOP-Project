# Laser Class

## Tổng quan
`Laser` là lớp đại diện cho viên đạn laser được bắn ra từ Paddle khi người chơi kích hoạt Laser Power-Up. Laser bay thẳng lên trên và có thể phá hủy gạch khi va chạm. Đây là một đối tượng đơn giản nhưng quan trọng, cho phép người chơi tấn công gạch theo cách khác ngoài việc dùng bóng.

## Vị trí
- **Package**: `Objects.GameEntities`
- **File**: `src/Objects/GameEntities/Laser.java`
- **Kế thừa**: `MovableObject`
- **Implements**: `GameObject` (gián tiếp qua MovableObject)

## Mục đích
Lớp Laser:
- Đại diện cho đạn laser bắn từ paddle
- Di chuyển thẳng lên trên với vận tốc cố định
- Có thể phá hủy gạch khi va chạm
- Tự động biến mất khi ra khỏi màn hình
- Quản lý trạng thái sống/chết đơn giản

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
Laser (Concrete Class)
```

---

## Thuộc tính (Fields)

### `private boolean destroyed`

**Mô tả**: Cờ đánh dấu laser đã bị phá hủy.

**Giá trị mặc định**: `false`

**Ý nghĩa**:
- `true` = laser đã bị phá hủy (va chạm gạch hoặc ra khỏi màn hình)
- `false` = laser vẫn đang bay

**Sử dụng**:
```java
// Kiểm tra trước khi xử lý
if (!laser.destroyed) {
    laser.update();
    checkLaserCollisions(laser);
}

// Phá hủy laser khi va chạm
if (laser.getBounds().intersects(brick.getBounds())) {
    laser.destroy();
    brick.takeDamage();
}
```

---

## Constructor

### `Laser(double x, double y)`

**Mô tả**: Khởi tạo một viên đạn laser mới.

**Tham số**:
- `x` - tọa độ X ban đầu (vị trí bắn)
- `y` - tọa độ Y ban đầu (vị trí bắn)

**Hành vi**:
1. Gọi `super(x, y, LASER_WIDTH, LASER_HEIGHT)` để thiết lập vị trí và kích thước
2. Thiết lập vận tốc cố định hướng lên: `Velocity(0, -LASER_SPEED)`
3. Khởi tạo `destroyed = false`

**Đặc điểm quan trọng**:
- Laser luôn có kích thước cố định (`LASER_WIDTH` × `LASER_HEIGHT`)
- Laser luôn bay thẳng lên trên (dx = 0, dy = âm)
- Tốc độ laser cố định (`LASER_SPEED`), không thay đổi trong suốt vòng đời

**Ví dụ**:
```java
// Tạo laser từ paddle
double paddleLeft = paddle.getX();
double paddleRight = paddle.getX() + paddle.getWidth();
double paddleTop = paddle.getY();

// Laser bên trái
Laser leftLaser = new Laser(paddleLeft + 10, paddleTop);

// Laser bên phải
Laser rightLaser = new Laser(
    paddleRight - 10 - Constants.Laser.LASER_WIDTH, 
    paddleTop
);

// Thường paddle bắn 2 laser cùng lúc
List<Laser> lasers = paddle.shootLaser();
// lasers.size() = 2 (hoặc 0 nếu không thể bắn)
```

**Constants điển hình**:
```java
public class Constants {
    public static class Laser {
        public static final double LASER_WIDTH = 4;    // Rất nhỏ
        public static final double LASER_HEIGHT = 12;  // Dài hơn
        public static final double LASER_SPEED = 8;    // Nhanh hơn bóng
        public static final long LASER_COOLDOWN = 300; // 0.3 giây
        public static final int LASER_SHOTS = 25;      // 25 phát
    }
}
```

---

## Phương thức

### 1. `void update()` (Override)

**Mô tả**: Cập nhật vị trí laser trong mỗi frame game.

**Hành vi**:
```java
if (destroyed) {
    return; // Không làm gì nếu đã phá hủy
}
move(); // Di chuyển dựa trên vận tốc
```

**Chi tiết**:
- Kiểm tra trạng thái `destroyed` trước khi di chuyển
- Gọi `move()` để cập nhật vị trí: `y += dy` (bay lên trên)
- Không tự động kiểm tra va chạm hoặc boundaries

**Sử dụng**:
```java
// Trong game loop
for (Laser laser : lasers) {
    if (laser.isAlive()) {
        laser.update(); // Di chuyển laser
    }
}

// Sau đó kiểm tra collisions và off-screen
checkLaserCollisions();
removeOffScreenLasers();
```

**Lưu ý**: 
- `update()` chỉ di chuyển, không xử lý logic va chạm
- Collision detection phải được thực hiện riêng bởi CollisionManager
- Off-screen check cũng phải thực hiện riêng

---

### 2. `boolean isOffScreen()`

**Mô tả**: Kiểm tra laser đã bay ra khỏi màn hình (vượt qua biên trên) chưa.

**Kiểu trả về**: `boolean`
- `true` - laser đã ra khỏi màn hình
- `false` - laser vẫn trong vùng chơi

**Công thức**:
```java
return getY() + getHeight() < WINDOW_TOP_OFFSET + BORDER_TOP_HEIGHT;
```

**Giải thích**:
- `getY() + getHeight()` = cạnh dưới của laser
- `WINDOW_TOP_OFFSET + BORDER_TOP_HEIGHT` = ranh giới trên của vùng chơi
- Laser bị coi là "off screen" khi cạnh dưới vượt qua ranh giới trên

**Ví dụ tính toán**:
```
WINDOW_TOP_OFFSET = 60 (thanh title)
BORDER_TOP_HEIGHT = 40 (viền trên)
Ranh giới trên = 60 + 40 = 100

Laser tại y = 95, height = 12
Cạnh dưới = 95 + 12 = 107 → Chưa off-screen (107 >= 100)

Laser tại y = 85, height = 12
Cạnh dưới = 85 + 12 = 97 → Off-screen! (97 < 100)
```

**Sử dụng**:
```java
// Dọn dẹp laser ra khỏi màn hình
for (Laser laser : lasers) {
    if (laser.isOffScreen()) {
        laser.destroy(); // Đánh dấu để xóa
    }
}

// Hoặc dùng removeIf
lasers.removeIf(laser -> laser.isOffScreen() || !laser.isAlive());
```

**Tại sao cần phương thức này?**
- Tiết kiệm tài nguyên: xóa laser không còn nhìn thấy
- Tránh memory leak: không để laser tích lũy vô hạn
- Giới hạn số lượng đối tượng: cải thiện performance

---

### 3. `void destroy()` (Override)

**Mô tả**: Đánh dấu laser là đã bị phá hủy.

**Hành vi**: `this.destroyed = true;`

**Khi nào gọi**:
1. Laser va chạm với gạch
2. Laser bay ra khỏi màn hình
3. Round kết thúc (dọn dẹp tất cả đối tượng)

**Sử dụng**:
```java
// Va chạm với gạch
if (laser.getBounds().intersects(brick.getBounds())) {
    laser.destroy();
    brick.takeDamage();
    
    if (!brick.isAlive()) {
        scoreManager.addPoints(brick.getPoints());
    }
}

// Bay ra khỏi màn hình
if (laser.isOffScreen()) {
    laser.destroy();
}

// Dọn dẹp khi kết thúc round
public void cleanupRound() {
    for (Laser laser : lasers) {
        laser.destroy();
    }
    lasers.clear();
}
```

**Lưu ý**: 
- `destroy()` chỉ đánh dấu, không xóa khỏi danh sách
- Phải gọi `lasers.removeIf(l -> !l.isAlive())` để xóa thật sự

---

### 4. `boolean isAlive()` (Override)

**Mô tả**: Kiểm tra laser còn "sống" (chưa bị phá hủy) không.

**Kiểu trả về**: `boolean`
- `true` - laser chưa bị phá hủy
- `false` - laser đã bị phá hủy

**Công thức**: `return !destroyed;`

**Sử dụng**:
```java
// Chỉ xử lý laser còn sống
for (Laser laser : lasers) {
    if (laser.isAlive()) {
        laser.update();
        checkCollisions(laser);
    }
}

// Dọn dẹp laser chết
lasers.removeIf(laser -> !laser.isAlive());

// Đếm số laser đang hoạt động
int activeLasers = (int) lasers.stream()
    .filter(Laser::isAlive)
    .count();
```

---

## Luồng hoạt động điển hình

### Lifecycle của một Laser

```
1. SPAWN - Paddle bắn laser
   ↓
   paddle.shootLaser()
   → Tạo 2 laser ở 2 bên paddle
   → lasers.add(leftLaser)
   → lasers.add(rightLaser)
   ↓
   Laser(x, y) constructor:
      - x, y = vị trí bắn
      - width = LASER_WIDTH (4)
      - height = LASER_HEIGHT (12)
      - velocity = (0, -LASER_SPEED) // Bay lên
      - destroyed = false
   
2. ACTIVE - Laser đang bay
   ↓
   Mỗi frame trong game loop:
   
   laser.update()
   → if (!destroyed) { move(); }
   → y += dy (dy = -8, nên y giảm = bay lên)
   
   checkLaserCollisions(laser)
   → Kiểm tra va chạm với gạch
   
   if (laser.isOffScreen()) {
       laser.destroy()
   }
   
3. COLLISION - Va chạm với gạch
   ↓
   if (laser.getBounds().intersects(brick.getBounds())) {
       laser.destroy()
       → destroyed = true
       
       brick.takeDamage()
       → Gạch bị sát thương
       
       if (!brick.isAlive()) {
           scoreManager.addPoints(brick.getPoints())
           spawnPowerUp(brick)
       }
   }
   
4. OFF-SCREEN - Bay ra ngoài màn hình
   ↓
   if (laser.isOffScreen()) {
       laser.destroy()
       → destroyed = true
   }
   
5. CLEANUP - Xóa khỏi game
   ↓
   lasers.removeIf(laser -> !laser.isAlive())
   → Laser bị xóa khỏi danh sách
   → Garbage collector thu hồi bộ nhớ
```

### Timeline Example

```
T = 0ms: Paddle bắn laser
   laser.y = 500 (vị trí paddle)
   laser.destroyed = false

T = 16ms: Frame 1
   laser.update() → y = 500 - 8 = 492
   isOffScreen() → false
   Không va chạm

T = 32ms: Frame 2
   laser.update() → y = 492 - 8 = 484
   isOffScreen() → false
   Không va chạm

... (nhiều frame) ...

T = 500ms: Frame 31
   laser.update() → y = 252
   Collision với gạch!
   laser.destroy() → destroyed = true
   brick.takeDamage()

T = 516ms: Frame 32
   laser.update() → return (destroyed = true)
   isAlive() → false
   removeIf() → Laser bị xóa

HOẶC

T = 800ms: Đạt đỉnh màn hình
   laser.y = 80
   isOffScreen() → true (80 + 12 < 100)
   laser.destroy()
   removeIf() → Laser bị xóa
```

---

## Tích hợp với các hệ thống khác

### 1. Paddle - Bắn Laser

```java
public class Paddle extends MovableObject {
    public List<Laser> shootLaser() {
        List<Laser> lasers = new ArrayList<>();
        
        // Kiểm tra điều kiện
        if (laserShots <= 0) return lasers;
        if (System.currentTimeMillis() < laserCooldown) return lasers;
        
        // Giảm đạn
        laserShots--;
        
        // Thiết lập cooldown
        laserCooldown = System.currentTimeMillis() + Constants.Laser.LASER_COOLDOWN;
        
        // Tính vị trí bắn
        double paddleLeft = getX();
        double paddleRight = getX() + getWidth();
        double paddleTop = getY();
        
        // Tạo 2 laser
        lasers.add(new Laser(paddleLeft + 10, paddleTop));
        lasers.add(new Laser(
            paddleRight - 10 - Constants.Laser.LASER_WIDTH, 
            paddleTop
        ));
        
        return lasers;
    }
}
```

### 2. GameManager - Quản lý Laser

```java
public class GameManager {
    private List<Laser> lasers = new ArrayList<>();
    
    public void update() {
        // Update tất cả laser
        for (Laser laser : lasers) {
            if (laser.isAlive()) {
                laser.update();
            }
        }
        
        // Kiểm tra va chạm
        collisionManager.checkLaserCollisions(lasers);
        
        // Dọn dẹp laser chết và off-screen
        lasers.removeIf(laser -> !laser.isAlive() || laser.isOffScreen());
    }
    
    public void addLasers(List<Laser> newLasers) {
        lasers.addAll(newLasers);
    }
    
    public void clearAllLasers() {
        for (Laser laser : lasers) {
            laser.destroy();
        }
        lasers.clear();
    }
}
```

### 3. CollisionManager - Va chạm Laser

```java
public class CollisionManager {
    public void checkLaserCollisions(List<Laser> lasers) {
        for (Laser laser : lasers) {
            if (!laser.isAlive()) continue;
            
            // Kiểm tra va chạm với gạch
            boolean hit = false;
            for (Brick brick : bricks) {
                if (!brick.isAlive()) continue;
                
                if (laser.getBounds().intersects(brick.getBounds())) {
                    handleLaserBrickCollision(laser, brick);
                    hit = true;
                    break; // Laser chỉ phá 1 gạch
                }
            }
            
            // Kiểm tra off-screen nếu chưa va chạm
            if (!hit && laser.isOffScreen()) {
                laser.destroy();
            }
        }
    }
    
    private void handleLaserBrickCollision(Laser laser, Brick brick) {
        // Phá hủy laser
        laser.destroy();
        
        // Gạch chịu sát thương
        brick.takeDamage();
        
        // Phát âm thanh
        audioManager.playLaserHitSound();
        
        // Xử lý gạch bị phá hủy
        if (!brick.isAlive()) {
            scoreManager.addPoints(brick.getPoints());
            
            // Spawn power-up
            if (shouldSpawnPowerUp()) {
                PowerUp powerUp = createRandomPowerUp(
                    brick.getX() + brick.getWidth() / 2,
                    brick.getY() + brick.getHeight() / 2
                );
                powerUps.add(powerUp);
            }
            
            // Hiệu ứng visual
            particleSystem.createExplosion(brick.getX(), brick.getY());
        }
    }
}
```

### 4. Renderer - Vẽ Laser

```java
public class LaserRenderer {
    public void renderLasers(List<Laser> lasers, Graphics g) {
        for (Laser laser : lasers) {
            if (laser.isAlive()) {
                renderLaser(laser, g);
            }
        }
    }
    
    private void renderLaser(Laser laser, Graphics g) {
        double x = laser.getX();
        double y = laser.getY();
        double w = laser.getWidth();
        double h = laser.getHeight();
        
        // Vẽ laser beam sáng
        g.setColor(new Color(0, 255, 255)); // Cyan
        g.fillRect(x, y, w, h);
        
        // Vẽ viền sáng hơn
        g.setColor(new Color(255, 255, 255)); // Trắng
        g.fillRect(x + 1, y, w - 2, h);
        
        // Thêm glow effect
        g.setColor(new Color(0, 255, 255, 100)); // Semi-transparent
        g.fillRect(x - 1, y, w + 2, h);
    }
}
```

---

## Các tính năng nâng cao

### 1. Laser Trail Effect

```java
public class LaserTrailEffect {
    private Queue<Point> trailPoints = new LinkedList<>();
    private final int TRAIL_LENGTH = 5;
    
    public void update(Laser laser) {
        if (laser.isAlive()) {
            Point pos = new Point(
                laser.getX() + laser.getWidth() / 2,
                laser.getY() + laser.getHeight()
            );
            trailPoints.add(pos);
            
            if (trailPoints.size() > TRAIL_LENGTH) {
                trailPoints.poll();
            }
        }
    }
    
    public void render(Graphics g) {
        int alpha = 200;
        int step = 200 / trailPoints.size();
        
        for (Point p : trailPoints) {
            g.setColor(new Color(0, 255, 255, alpha));
            g.fillRect(p.getX() - 2, p.getY(), 4, 6);
            alpha -= step;
        }
    }
}
```

### 2. Laser Power Variation

```java
public enum LaserType {
    NORMAL(1, 8, new Color(0, 255, 255)),
    DOUBLE(2, 8, new Color(255, 0, 255)),
    PIERCING(1, 10, new Color(255, 255, 0)); // Xuyên qua nhiều gạch
    
    private final int damage;
    private final double speed;
    private final Color color;
    
    // Constructor và getters...
}

public class PowerLaser extends Laser {
    private LaserType type;
    private int pierceCount; // Số gạch có thể xuyên qua
    
    public PowerLaser(double x, double y, LaserType type) {
        super(x, y);
        this.type = type;
        this.pierceCount = (type == LaserType.PIERCING) ? 3 : 0;
    }
    
    public void hitBrick() {
        if (pierceCount > 0) {
            pierceCount--;
            if (pierceCount == 0) {
                destroy();
            }
        } else {
            destroy();
        }
    }
}
```

### 3. Laser Count Limiter

```java
public class LaserManager {
    private static final int MAX_ACTIVE_LASERS = 10;
    private List<Laser> lasers = new ArrayList<>();
    
    public boolean canAddLaser() {
        // Đếm laser đang hoạt động
        long activeCount = lasers.stream()
            .filter(Laser::isAlive)
            .count();
        return activeCount < MAX_ACTIVE_LASERS;
    }
    
    public void addLasers(List<Laser> newLasers) {
        if (canAddLaser()) {
            lasers.addAll(newLasers);
        } else {
            System.out.println("Too many active lasers!");
        }
    }
}
```

---

## So sánh Laser vs Ball

| Đặc điểm | Laser | Ball |
|----------|-------|------|
| **Chuyển động** | Thẳng lên trên | Phản xạ khi va chạm |
| **Vận tốc** | Cố định (0, -8) | Thay đổi hướng |
| **Va chạm** | Phá hủy khi chạm gạch | Nảy khi chạm gạch |
| **Tuổi thọ** | Ngắn (bay ra màn hình) | Dài (cho đến khi rơi) |
| **Điều khiển** | Gián tiếp (bắn từ paddle) | Gián tiếp (điều khiển paddle) |
| **Số lượng** | Nhiều (2 cùng lúc, lặp lại) | Ít (1-3 bóng) |
| **Mục đích** | Tấn công chủ động | Gameplay chính |
| **Độ phức tạp** | Đơn giản | Phức tạp (collision physics) |

---

## Best Practices

### 1. Quản lý Lifecycle
```java
// ✅ Đúng - dọn dẹp laser thường xuyên
lasers.removeIf(laser -> !laser.isAlive() || laser.isOffScreen());

// ❌ Sai - giữ laser chết trong memory
if (!laser.isAlive()) {
    laser.destroyed = true; // Không xóa khỏi list
}
```

### 2. Collision Detection
```java
// ✅ Đúng - laser chỉ phá 1 gạch rồi destroy
for (Brick brick : bricks) {
    if (laser.getBounds().intersects(brick.getBounds())) {
        laser.destroy();
        brick.takeDamage();
        break; // Dừng ngay
    }
}

// ❌ Sai - laser phá nhiều gạch cùng lúc
for (Brick brick : bricks) {
    if (laser.getBounds().intersects(brick.getBounds())) {
        brick.takeDamage(); // Không break
    }
}
```

### 3. Performance
```java
// ✅ Đúng - giới hạn số laser
private static final int MAX_LASERS = 10;

if (lasers.size() < MAX_LASERS) {
    lasers.addAll(paddle.shootLaser());
}

// ❌ Sai - không giới hạn, có thể lag
lasers.addAll(paddle.shootLaser()); // Spam laser
```

---

## Kết luận

`Laser` là một lớp đơn giản nhưng hiệu quả:

- **Đơn giản**: Chỉ bay thẳng lên và phá hủy khi va chạm
- **Hiệu quả**: Cho phép người chơi tấn công chủ động
- **Dễ quản lý**: Lifecycle rõ ràng (spawn → active → destroy)
- **Mở rộng được**: Dễ dàng thêm loại laser mới hoặc hiệu ứng

Laser là một công cụ gameplay quan trọng trong Arkanoid, tạo thêm chiều sâu cho trải nghiệm chơi và cho phép người chơi có nhiều cách tiếp cận khác nhau để phá gạch. Tuy đơn giản về mặt code, nhưng laser góp phần quan trọng vào sự đa dạng và thú vị của game.
