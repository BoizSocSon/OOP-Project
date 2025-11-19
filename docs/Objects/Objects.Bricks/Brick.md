# Brick Abstract Class

## Tổng quan
`Brick` là lớp trừu tượng (abstract class) đại diện cho viên gạch trong game Arkanoid - mục tiêu chính mà người chơi cần phá hủy để hoàn thành màn chơi. Lớp này cung cấp khung nền cho tất cả các loại gạch khác nhau, quản lý vị trí, kích thước, độ bền (hit points), và trạng thái sống/chết của gạch.

## Vị trí
- **Package**: `Objects.Bricks`
- **File**: `src/Objects/Bricks/Brick.java`
- **Loại**: Abstract Class
- **Implements**: `GameObject`

## Mục đích
Lớp Brick:
- Cung cấp cấu trúc cơ bản cho tất cả loại gạch
- Quản lý vị trí và kích thước gạch trên màn hình
- Theo dõi độ bền (hit points) và xử lý sát thương
- Xác định vùng va chạm (hitbox) cho collision detection
- Quản lý trạng thái sống/chết của gạch
- Là lớp cha cho NormalBrick, SilverBrick, GoldBrick

## Kế thừa

```
GameObject (Interface)
    ↑
    │ implements
    │
Brick (Abstract Class)
    ↑
    │ extends
    │
    ├── NormalBrick (các gạch màu thường)
    ├── SilverBrick (gạch bạc, độ bền cao)
    └── GoldBrick (gạch vàng, không thể phá)
```

---

## Thuộc tính (Fields)

### 1. Vị trí

#### `private double x`
**Mô tả**: Tọa độ X của góc trên bên trái gạch.

**Đơn vị**: Pixels

**Sử dụng**: Xác định vị trí ngang của gạch trên màn hình

---

#### `private double y`
**Mô tả**: Tọa độ Y của góc trên bên trái gạch.

**Đơn vị**: Pixels

**Sử dụng**: Xác định vị trí dọc của gạch trên màn hình

---

### 2. Kích thước

#### `private double width`
**Mô tả**: Chiều rộng của gạch.

**Giá trị điển hình**: `64` pixels (theo Constants.Bricks.BRICK_WIDTH)

---

#### `private double height`
**Mô tả**: Chiều cao của gạch.

**Giá trị điển hình**: `32` pixels (theo Constants.Bricks.BRICK_HEIGHT)

---

### 3. Độ bền

#### `private int hitPoints`
**Mô tả**: Số lần gạch có thể chịu đòn trước khi bị phá hủy.

**Giá trị**:
- `1` - Gạch thường (NormalBrick): phá sau 1 hit
- `2` - Gạch bạc (SilverBrick): phá sau 2 hits
- `999` - Gạch vàng (GoldBrick): không thể phá (hoặc rất khó)

**Ý nghĩa**:
- Mỗi lần bị đánh (ball/laser), `hitPoints` giảm 1
- Khi `hitPoints <= 0`, gạch bị phá hủy

**Ví dụ**:
```java
// Gạch bạc có 2 HP
SilverBrick silver = new SilverBrick(x, y, w, h);
// silver.hitPoints = 2

// Hit lần 1
silver.takeHit(); // hitPoints = 1 (chưa vỡ)

// Hit lần 2
silver.takeHit(); // hitPoints = 0 → destroy()
```

---

### 4. Trạng thái

#### `private boolean alive`
**Mô tả**: Cờ đánh dấu gạch còn tồn tại hay đã bị phá hủy.

**Giá trị mặc định**: `true` (khi khởi tạo)

**Ý nghĩa**:
- `true` = gạch còn sống, cần render và kiểm tra collision
- `false` = gạch đã bị phá, cần xóa khỏi game

**Sử dụng**:
```java
// Chỉ xử lý gạch còn sống
for (Brick brick : bricks) {
    if (brick.isAlive()) {
        brick.update();
        checkCollision(brick);
    }
}

// Dọn dẹp gạch chết
bricks.removeIf(brick -> !brick.isAlive());
```

---

## Constructor

### `Brick(double x, double y, double width, double height, int hitPoints)`

**Mô tả**: Khởi tạo một viên gạch mới (protected - chỉ được gọi từ lớp con).

**Tham số**:
- `x` - tọa độ X (góc trên trái)
- `y` - tọa độ Y (góc trên trái)
- `width` - chiều rộng gạch
- `height` - chiều cao gạch
- `hitPoints` - số lần chịu đòn

**Hành vi**:
1. Thiết lập vị trí `(x, y)`
2. Thiết lập kích thước `(width, height)`
3. Thiết lập độ bền `hitPoints`
4. Khởi tạo `alive = true`

**Ví dụ từ lớp con**:
```java
// NormalBrick gọi constructor cha
public class NormalBrick extends Brick {
    public NormalBrick(double x, double y, double width, double height, BrickType type) {
        super(x, y, width, height, type.getHitPoints());
        // type.getHitPoints() = 1 cho gạch thường
    }
}

// SilverBrick gọi constructor cha
public class SilverBrick extends Brick {
    public SilverBrick(double x, double y, double width, double height) {
        super(x, y, width, height, BrickType.SILVER.getHitPoints());
        // BrickType.SILVER.getHitPoints() = 2
    }
}
```

---

## Phương thức

### 1. `void takeHit()`

**Mô tả**: Xử lý khi gạch bị đánh trúng (bởi bóng hoặc laser).

**Hành vi**:
```java
hitPoints--;
if (hitPoints <= 0) {
    destroy();
}
```

**Chi tiết**:
1. Giảm `hitPoints` đi 1
2. Nếu `hitPoints <= 0` → gọi `destroy()`

**Sử dụng**:
```java
// Khi ball va chạm gạch
if (ball.checkCollisionWithRect(brick.getBounds())) {
    brick.takeHit(); // Gạch chịu sát thương
    audioManager.playHitSound();
    
    if (!brick.isAlive()) {
        scoreManager.addPoints(brick.getBrickType().getBaseScore());
        spawnPowerUp(brick);
    }
}

// Khi laser va chạm gạch
if (laser.getBounds().intersects(brick.getBounds())) {
    brick.takeHit();
    laser.destroy();
    
    if (!brick.isAlive()) {
        scoreManager.addPoints(brick.getBrickType().getBaseScore());
    }
}
```

**Lưu ý**: Lớp con có thể override để thêm hành vi đặc biệt (như SilverBrick phát animation nứt).

---

### 2. `boolean isDestroyed()`

**Mô tả**: Kiểm tra gạch đã bị phá hủy chưa.

**Kiểu trả về**: `boolean`
- `true` - gạch đã bị phá hủy
- `false` - gạch vẫn còn

**Công thức**: `return !alive;`

**Sử dụng**:
```java
// Kiểm tra trước khi xử lý
if (!brick.isDestroyed()) {
    checkCollision(brick);
}

// Đếm số gạch còn lại
int remainingBricks = (int) bricks.stream()
    .filter(b -> !b.isDestroyed())
    .count();

if (remainingBricks == 0) {
    levelComplete();
}
```

**Quan hệ với `isAlive()`**:
- `isDestroyed()` = `!isAlive()`
- Hai phương thức ngược nhau, cùng ý nghĩa

---

### 3. `Rectangle getBounds()` (Override từ GameObject)

**Mô tả**: Trả về vùng bao (hitbox) của gạch cho collision detection.

**Kiểu trả về**: `Rectangle`

**Công thức**:
```java
return new Rectangle(new Point(x, y), width, height);
```

**Sử dụng**:
```java
// Kiểm tra collision với ball
Rectangle ballBounds = ball.getBounds();
Rectangle brickBounds = brick.getBounds();

if (ball.checkCollisionWithRect(brickBounds)) {
    brick.takeHit();
}

// Kiểm tra collision với laser
if (laser.getBounds().intersects(brick.getBounds())) {
    brick.takeHit();
    laser.destroy();
}

// Render debug hitbox
if (DEBUG_MODE) {
    graphics.setColor(Color.RED);
    graphics.drawRect(
        brick.getBounds().getX(),
        brick.getBounds().getY(),
        brick.getBounds().getWidth(),
        brick.getBounds().getHeight()
    );
}
```

---

### 4. `boolean isAlive()` (Override từ GameObject)

**Mô tả**: Kiểm tra gạch còn sống không.

**Kiểu trả về**: `boolean`
- `true` - gạch còn sống
- `false` - gạch đã chết

**Công thức**: `return alive;`

**Sử dụng**: Tương tự `isDestroyed()` nhưng ngược logic

---

### 5. `void destroy()` (Override từ GameObject)

**Mô tả**: Phá hủy gạch bằng cách đặt `alive = false`.

**Hành vi**: `alive = false;`

**Khi nào gọi**:
- Tự động trong `takeHit()` khi `hitPoints <= 0`
- Có thể gọi trực tiếp để phá hủy ngay lập tức

**Sử dụng**:
```java
// Phá hủy tất cả gạch (cheat code hoặc debug)
public void destroyAllBricks() {
    for (Brick brick : bricks) {
        brick.destroy();
    }
}

// Phá hủy gạch khi dùng power-up đặc biệt
public void useMegaBomb() {
    for (Brick brick : bricks) {
        if (isInBlastRadius(brick)) {
            brick.destroy();
            scoreManager.addPoints(brick.getBrickType().getBaseScore());
        }
    }
}
```

**Lưu ý**: 
- `destroy()` chỉ đánh dấu, không xóa khỏi danh sách
- Phải gọi `bricks.removeIf(b -> !b.isAlive())` để xóa thật sự

---

### 6. `void update()` (Override từ GameObject)

**Mô tả**: Cập nhật trạng thái gạch mỗi frame.

**Hành vi mặc định**: Không làm gì (empty method)

**Mục đích**: Để lớp con override và thêm hành vi đặc biệt

**Ví dụ override trong lớp con**:
```java
// SilverBrick cập nhật animation nứt
@Override
public void update() {
    if (crackAnimation != null && crackAnimation.isPlaying()) {
        crackAnimation.update();
    }
}

// Giả sử có MovingBrick
public class MovingBrick extends Brick {
    @Override
    public void update() {
        // Di chuyển gạch
        x += velocityX;
        y += velocityY;
        
        // Đổi hướng khi chạm biên
        if (x < minX || x > maxX) velocityX = -velocityX;
        if (y < minY || y > maxY) velocityY = -velocityY;
    }
}
```

---

### 7. `abstract BrickType getBrickType()`

**Mô tả**: Phương thức trừu tượng - lớp con PHẢI implement.

**Mục đích**: Xác định loại gạch (màu sắc, điểm số, v.v.)

**Kiểu trả về**: `BrickType` enum

**Implementation trong lớp con**:
```java
// NormalBrick
@Override
public BrickType getBrickType() {
    return brickType; // RED, BLUE, GREEN, v.v.
}

// SilverBrick
@Override
public BrickType getBrickType() {
    return BrickType.SILVER;
}

// GoldBrick
@Override
public BrickType getBrickType() {
    return BrickType.GOLD;
}
```

**Sử dụng**:
```java
// Tính điểm dựa trên loại gạch
if (!brick.isAlive()) {
    int points = brick.getBrickType().getBaseScore();
    scoreManager.addPoints(points);
}

// Render sprite phù hợp
String spriteName = brick.getBrickType().getSpriteName();
Sprite sprite = spriteCache.getSprite(spriteName);
graphics.drawImage(sprite.getImage(), brick.getX(), brick.getY());

// Logic đặc biệt cho loại gạch
if (brick.getBrickType() == BrickType.GOLD) {
    // Gạch vàng không thể phá
    return;
}
```

---

### 8. Getter Methods

#### `double getX()`
**Mô tả**: Lấy tọa độ X của gạch.

**Kiểu trả về**: `double`

---

#### `double getY()`
**Mô tả**: Lấy tọa độ Y của gạch.

**Kiểu trả về**: `double`

---

#### `double getWidth()`
**Mô tả**: Lấy chiều rộng của gạch.

**Kiểu trả về**: `double`

---

#### `double getHeight()`
**Mô tả**: Lấy chiều cao của gạch.

**Kiểu trả về**: `double`

---

#### `int getHitPoints()`
**Mô tả**: Lấy số hit points còn lại.

**Kiểu trả về**: `int`

**Sử dụng**:
```java
// Hiển thị độ bền gạch
if (brick.getHitPoints() > 1) {
    graphics.drawText(
        String.valueOf(brick.getHitPoints()),
        brick.getX() + brick.getWidth() / 2,
        brick.getY() + brick.getHeight() / 2
    );
}

// Logic đặc biệt cho gạch sắp vỡ
if (brick.getHitPoints() == 1) {
    // Hiển thị hiệu ứng cảnh báo
    renderWarningEffect(brick);
}
```

---

## Luồng hoạt động điển hình

### Lifecycle của một Brick

```
1. CREATION - Tạo gạch
   ↓
   new NormalBrick(x, y, width, height, BrickType.RED)
   → Gọi Brick constructor
   → x, y, width, height được set
   → hitPoints = BrickType.RED.getHitPoints() = 1
   → alive = true

2. ACTIVE - Gạch hoạt động
   ↓
   Mỗi frame:
   
   brick.update()
   → Cập nhật animation (nếu có)
   
   if (collision detected) {
       brick.takeHit()
       → hitPoints--
       → if (hitPoints <= 0) { destroy() }
   }

3. COLLISION - Va chạm với ball/laser
   ↓
   ball.checkCollisionWithRect(brick.getBounds())
   → Collision detected!
   
   brick.takeHit()
   → hitPoints = 0
   → destroy() được gọi
   → alive = false

4. DESTROYED - Gạch bị phá
   ↓
   !brick.isAlive() = true
   
   Spawn power-up (nếu may mắn)
   Add điểm: scoreManager.addPoints(brick.getBrickType().getBaseScore())
   
5. CLEANUP - Dọn dẹp
   ↓
   bricks.removeIf(b -> !b.isAlive())
   → Gạch bị xóa khỏi danh sách
   → Garbage collector thu hồi memory
```

---

## Tích hợp với các hệ thống khác

### 1. CollisionManager

```java
public class CollisionManager {
    public void checkBallBrickCollisions(Ball ball, List<Brick> bricks) {
        for (Brick brick : bricks) {
            if (!brick.isAlive()) continue;
            
            // Kiểm tra collision
            if (ball.checkCollisionWithRect(brick.getBounds())) {
                handleBallBrickCollision(ball, brick);
                break; // Chỉ xử lý 1 gạch/frame
            }
        }
    }
    
    private void handleBallBrickCollision(Ball ball, Brick brick) {
        // Gạch vàng không thể phá
        if (brick.getBrickType() == BrickType.GOLD) {
            audioManager.playMetalHitSound();
            return; // Bóng nảy nhưng gạch không vỡ
        }
        
        // Gạch thường và bạc
        brick.takeHit();
        audioManager.playBrickBreakSound();
        
        // Xử lý khi gạch vỡ
        if (!brick.isAlive()) {
            // Cộng điểm
            int points = brick.getBrickType().getBaseScore();
            scoreManager.addPoints(points);
            
            // Spawn power-up
            if (shouldSpawnPowerUp()) {
                PowerUp powerUp = createRandomPowerUp(
                    brick.getX() + brick.getWidth() / 2,
                    brick.getY() + brick.getHeight() / 2
                );
                powerUps.add(powerUp);
            }
            
            // Hiệu ứng visual
            particleSystem.createBrickExplosion(
                brick.getX(),
                brick.getY(),
                brick.getBrickType()
            );
        }
    }
}
```

### 2. RoundsManager - Tạo Layout Gạch

```java
public class RoundsManager {
    public List<Brick> createBrickLayout(int level) {
        List<Brick> bricks = new ArrayList<>();
        
        double brickWidth = Constants.Bricks.BRICK_WIDTH;
        double brickHeight = Constants.Bricks.BRICK_HEIGHT;
        double spacing = Constants.Bricks.BRICK_SPACING;
        
        int rows = 8;
        int cols = 12;
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = col * (brickWidth + spacing) + offsetX;
                double y = row * (brickHeight + spacing) + offsetY;
                
                // Chọn loại gạch dựa trên pattern
                BrickType type = getBrickTypeForPosition(row, col, level);
                Brick brick = createBrick(x, y, brickWidth, brickHeight, type);
                
                bricks.add(brick);
            }
        }
        
        return bricks;
    }
    
    private Brick createBrick(double x, double y, double w, double h, BrickType type) {
        switch (type) {
            case SILVER:
                return new SilverBrick(x, y, w, h);
            case GOLD:
                return new GoldBrick(x, y, w, h);
            default:
                return new NormalBrick(x, y, w, h, type);
        }
    }
}
```

### 3. Renderer - Vẽ Gạch

```java
public class BrickRenderer {
    public void renderBricks(List<Brick> bricks, Graphics g) {
        for (Brick brick : bricks) {
            if (!brick.isAlive()) continue;
            
            renderBrick(brick, g);
        }
    }
    
    private void renderBrick(Brick brick, Graphics g) {
        double x = brick.getX();
        double y = brick.getY();
        
        // Lấy sprite dựa trên loại gạch
        String spriteName = brick.getBrickType().getSpriteName();
        Sprite sprite = spriteCache.getSprite(spriteName);
        
        // Render sprite chính
        g.drawImage(sprite.getImage(), x, y);
        
        // Render animation đặc biệt (SilverBrick crack)
        if (brick instanceof SilverBrick) {
            SilverBrick silver = (SilverBrick) brick;
            if (silver.isCrackAnimationPlaying()) {
                Animation anim = silver.getCrackAnimation();
                anim.render(g, x, y);
            }
        }
        
        // Render hit points (debug)
        if (DEBUG_MODE) {
            g.setColor(Color.WHITE);
            g.drawText(
                String.valueOf(brick.getHitPoints()),
                x + brick.getWidth() / 2,
                y + brick.getHeight() / 2
            );
        }
    }
}
```

---

## Design Patterns

### 1. Template Method Pattern

Brick sử dụng Template Method Pattern:
- `takeHit()` định nghĩa thuật toán chung
- Lớp con override để customize behavior

```java
// Template trong Brick
public void takeHit() {
    hitPoints--;
    if (hitPoints <= 0) {
        destroy();
    }
}

// SilverBrick override để thêm animation
@Override
public void takeHit() {
    currentHP--;
    if (currentHP == 1) {
        crackAnimation.play(); // Custom behavior
    } else if (currentHP == 0) {
        destroy();
    }
}

// GoldBrick override để không làm gì
@Override
public void takeHit() {
    // Không làm gì - gạch vàng không thể phá
}
```

### 2. Factory Pattern (kết hợp với BrickType)

```java
public class BrickFactory {
    public static Brick createBrick(double x, double y, BrickType type) {
        double w = Constants.Bricks.BRICK_WIDTH;
        double h = Constants.Bricks.BRICK_HEIGHT;
        
        switch (type) {
            case SILVER:
                return new SilverBrick(x, y, w, h);
            case GOLD:
                return new GoldBrick(x, y, w, h);
            default:
                return new NormalBrick(x, y, w, h, type);
        }
    }
}
```

---

## Best Practices

### 1. Kiểm tra isAlive() trước xử lý
```java
// ✅ Đúng
for (Brick brick : bricks) {
    if (brick.isAlive()) {
        brick.update();
        checkCollision(brick);
    }
}

// ❌ Sai - xử lý cả gạch chết
for (Brick brick : bricks) {
    brick.update(); // Lãng phí CPU
}
```

### 2. Dọn dẹp gạch chết thường xuyên
```java
// ✅ Đúng - dọn dẹp mỗi frame
bricks.removeIf(brick -> !brick.isAlive());

// ❌ Sai - giữ gạch chết trong memory
// Không dọn dẹp → memory leak
```

### 3. Xử lý GoldBrick đúng cách
```java
// ✅ Đúng - kiểm tra trước khi takeHit
if (brick.getBrickType() != BrickType.GOLD) {
    brick.takeHit();
}

// ✅ Hoặc let GoldBrick.takeHit() handle
brick.takeHit(); // GoldBrick không làm gì

// ❌ Sai - force destroy GoldBrick
brick.destroy(); // Phá logic game!
```

### 4. Sử dụng getBounds() đúng cách
```java
// ✅ Đúng - cache nếu cần nhiều lần
Rectangle bounds = brick.getBounds();
if (ball.getBounds().intersects(bounds)) {
    // ...
}
if (laser.getBounds().intersects(bounds)) {
    // ...
}

// ❌ Sai - tạo object mới mỗi lần
if (ball.getBounds().intersects(brick.getBounds())) {} // OK
if (laser.getBounds().intersects(brick.getBounds())) {} // Tạo lại!
```

---

## Kết luận

`Brick` là nền tảng của hệ thống gạch trong Arkanoid:

- **Abstract Class**: Cung cấp khung chung cho tất cả loại gạch
- **Hit Points System**: Quản lý độ bền linh hoạt
- **GameObject Integration**: Tích hợp hoàn chỉnh với game loop
- **Extensible**: Dễ dàng tạo loại gạch mới bằng inheritance
- **Template Method**: Cho phép customize behavior trong lớp con

Lớp này là ví dụ điển hình của OOP design, sử dụng abstraction và inheritance để tạo một hệ thống linh hoạt và dễ mở rộng. Tất cả các loại gạch trong game đều kế thừa từ lớp này và chia sẻ behavior cơ bản, đồng thời có thể customize các hành vi đặc biệt của riêng mình.
