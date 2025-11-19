# MovableObject Abstract Class

## Tổng quan
`MovableObject` là một lớp trừu tượng (abstract class) đại diện cho tất cả các đối tượng có thể di chuyển trong game Arkanoid. Lớp này implement interface `GameObject` và cung cấp một bộ khung hoàn chỉnh cho việc xử lý chuyển động, vị trí, kích thước và trạng thái sống của đối tượng.

## Vị trí
- **Package**: `Objects.Core`
- **File**: `src/Objects/Core/MovableObject.java`
- **Loại**: Abstract Class
- **Implements**: `GameObject`

## Mục đích
Lớp này:
- Cung cấp implementation cơ bản cho các đối tượng có thể di chuyển
- Quản lý vị trí (x, y), kích thước (width, height), và vận tốc (velocity)
- Xử lý trạng thái sống/chết của đối tượng
- Là lớp cha cho Ball, Paddle, PowerUp, Laser, v.v.

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
    ├── Ball
    ├── Paddle
    ├── PowerUp
    │   ├── CatchPowerUp
    │   ├── DuplicatePowerUp
    │   ├── ExpandPaddlePowerUp
    │   ├── LaserPowerUp
    │   ├── LifePowerUp
    │   ├── SlowBallPowerUp
    │   └── WarpPowerUp
    └── Laser
```

## Thuộc tính (Fields)

### 1. Vị trí và Kích thước
```java
private double x;        // Hoành độ góc trên trái
private double y;        // Tung độ góc trên trái
private double width;    // Chiều rộng đối tượng
private double height;   // Chiều cao đối tượng
```

**Mô tả**: 
- `(x, y)` là tọa độ góc trên bên trái của đối tượng trong hệ tọa độ game
- `width` và `height` định nghĩa kích thước của vùng chiếm chỗ (bounding box)

**Ý nghĩa**: 
- Xác định vị trí và kích thước để render và detect collision
- Góc trên trái được chọn vì phù hợp với hệ tọa độ màn hình (y tăng xuống dưới)

---

### 2. Vận tốc
```java
private Velocity velocity;  // Vận tốc di chuyển (dx, dy)
```

**Mô tả**: 
- Đối tượng `Velocity` chứa thành phần vận tốc theo trục X (`dx`) và trục Y (`dy`)
- Vận tốc quyết định hướng và tốc độ di chuyển của đối tượng

**Giá trị mặc định**: `Velocity(0, 0)` - đứng yên

---

### 3. Trạng thái Sống
```java
private boolean alive;  // true = còn tồn tại, false = đã phá hủy
```

**Mô tả**: Flag đánh dấu đối tượng còn hoạt động hay đã bị loại bỏ

**Giá trị mặc định**: `true` - đối tượng được tạo ra ở trạng thái sống

---

## Constructor

### `MovableObject(double x, double y, double width, double height)`

**Mô tả**: Khởi tạo một đối tượng có thể di chuyển với vị trí và kích thước xác định.

**Tham số**:
- `x` - hoành độ góc trên trái
- `y` - tung độ góc trên trái
- `width` - chiều rộng đối tượng
- `height` - chiều cao đối tượng

**Hành vi**:
- Thiết lập vị trí và kích thước theo tham số
- Khởi tạo vận tốc ban đầu = `(0, 0)` (đứng yên)
- Thiết lập trạng thái `alive = true`

**Ví dụ**:
```java
public class Ball extends MovableObject {
    public Ball(double x, double y) {
        super(x, y, 16, 16); // Bóng 16x16 pixels
        setVelocity(new Velocity(5, -5)); // Thiết lập vận tốc ban đầu
    }
}
```

---

## Phương thức

### 1. `void move()`

**Mô tả**: Di chuyển đối tượng dựa trên vận tốc hiện tại.

**Hành vi**:
```java
this.x += this.velocity.getDx();
this.y += this.velocity.getDy();
```

**Ý nghĩa**:
- Cập nhật vị trí bằng cách cộng thêm `dx` và `dy` vào tọa độ hiện tại
- Được gọi trong mỗi frame để tạo chuyển động mượt mà
- Không tự động được gọi - lớp con cần gọi trong `update()`

**Ví dụ sử dụng**:
```java
@Override
public void update() {
    move(); // Di chuyển đối tượng
    checkBoundaries(); // Kiểm tra va chạm biên
    handleCollisions(); // Xử lý va chạm khác
}
```

**Lưu ý**: 
- Phương thức này chỉ thay đổi vị trí, không kiểm tra collision
- Lớp con chịu trách nhiệm gọi `move()` và xử lý boundary/collision

---

### 2. Getter và Setter cho Vận tốc

#### `void setVelocity(Velocity velocity)`
**Mô tả**: Thiết lập vận tốc mới cho đối tượng.

**Sử dụng**:
```java
// Thay đổi hướng bóng
ball.setVelocity(new Velocity(-5, 3));

// Làm chậm bóng (slow power-up)
Velocity current = ball.getVelocity();
ball.setVelocity(new Velocity(current.getDx() * 0.5, current.getDy() * 0.5));

// Dừng đối tượng
powerUp.setVelocity(new Velocity(0, 0));
```

#### `Velocity getVelocity()`
**Mô tả**: Lấy vận tốc hiện tại của đối tượng.

**Kiểu trả về**: `Velocity` - đối tượng chứa `dx` và `dy`

**Sử dụng**:
```java
// Kiểm tra tốc độ
Velocity v = ball.getVelocity();
double speed = Math.sqrt(v.getDx() * v.getDx() + v.getDy() * v.getDy());

// Đảo hướng theo trục X
Velocity current = ball.getVelocity();
ball.setVelocity(new Velocity(-current.getDx(), current.getDy()));
```

---

### 3. Getter và Setter cho Vị trí

#### `double getX()` / `void setX(double x)`
**Mô tả**: Lấy/thiết lập hoành độ của đối tượng.

**Sử dụng**:
```java
// Đặt paddle vào giữa màn hình
paddle.setX((SCREEN_WIDTH - paddle.getWidth()) / 2);

// Giữ đối tượng trong màn hình
if (ball.getX() < 0) {
    ball.setX(0);
    ball.setVelocity(new Velocity(-ball.getVelocity().getDx(), ball.getVelocity().getDy()));
}
```

#### `double getY()` / `void setY(double y)`
**Mô tả**: Lấy/thiết lập tung độ của đối tượng.

**Sử dụng**:
```java
// Power-up rơi xuống
powerUp.setY(powerUp.getY() + 2);

// Kiểm tra bóng rơi khỏi màn hình
if (ball.getY() > SCREEN_HEIGHT) {
    loseLife();
    ball.destroy();
}
```

---

### 4. Getter và Setter cho Kích thước

#### `double getWidth()` / `void setWidth(double width)`
**Mô tả**: Lấy/thiết lập chiều rộng của đối tượng.

**Sử dụng**:
```java
// Mở rộng paddle (expand power-up)
paddle.setWidth(paddle.getWidth() * 1.5);

// Tính toán vị trí giữa
double centerX = paddle.getX() + paddle.getWidth() / 2;
```

#### `double getHeight()` / `void setHeight(double height)`
**Mô tả**: Lấy/thiết lập chiều cao của đối tượng.

**Sử dụng**:
```java
// Thay đổi kích thước động
if (powerUpActive) {
    paddle.setHeight(20);
} else {
    paddle.setHeight(10);
}
```

---

### 5. Phương thức implement từ GameObject

#### `Rectangle getBounds()`
**Mô tả**: Trả về vùng bao (hitbox) của đối tượng cho collision detection.

**Implementation**:
```java
@Override
public Rectangle getBounds() {
    return new Rectangle(new Point(x, y), width, height);
}
```

**Kiểu trả về**: `Rectangle` - hình chữ nhật với góc trên trái `(x, y)` và kích thước `width × height`

**Sử dụng**:
```java
// Kiểm tra collision
if (ball.getBounds().intersects(paddle.getBounds())) {
    handleBallPaddleCollision(ball, paddle);
}

// Kiểm tra trong màn hình
Rectangle screenBounds = new Rectangle(new Point(0, 0), SCREEN_WIDTH, SCREEN_HEIGHT);
if (!screenBounds.contains(powerUp.getBounds())) {
    powerUp.destroy();
}
```

---

#### `boolean isAlive()`
**Mô tả**: Kiểm tra trạng thái sống của đối tượng.

**Implementation**:
```java
@Override
public boolean isAlive() {
    return alive;
}
```

**Kiểu trả về**: `boolean`
- `true` - đối tượng còn hoạt động
- `false` - đối tượng đã bị phá hủy

**Sử dụng**:
```java
// Dọn dẹp danh sách đối tượng
balls.removeIf(ball -> !ball.isAlive());
powerUps.removeIf(powerUp -> !powerUp.isAlive());

// Kiểm tra trước khi xử lý
if (laser.isAlive()) {
    laser.update();
    checkLaserHit(laser);
}
```

---

#### `void destroy()`
**Mô tả**: Phá hủy đối tượng, đánh dấu `alive = false`.

**Implementation**:
```java
@Override
public void destroy() {
    alive = false;
}
```

**Sử dụng**:
```java
// Bóng rơi khỏi màn hình
if (ball.getY() > SCREEN_HEIGHT) {
    ball.destroy();
    lives--;
}

// Thu thập power-up
if (paddle.getBounds().intersects(powerUp.getBounds())) {
    powerUp.destroy();
    activatePowerUp(powerUp.getType());
}

// Laser va gạch
if (laser.getBounds().intersects(brick.getBounds())) {
    laser.destroy();
    brick.takeDamage();
}
```

---

## Ví dụ triển khai lớp con

### Ví dụ 1: Ball
```java
public class Ball extends MovableObject {
    private static final double BALL_SIZE = 16;
    private static final double INITIAL_SPEED = 5;
    
    public Ball(double x, double y) {
        super(x, y, BALL_SIZE, BALL_SIZE);
        // Bóng bay chéo lên trên
        setVelocity(new Velocity(INITIAL_SPEED, -INITIAL_SPEED));
    }
    
    @Override
    public void update() {
        move(); // Di chuyển theo vận tốc
        
        // Kiểm tra va chạm với biên
        if (getX() <= 0 || getX() + getWidth() >= SCREEN_WIDTH) {
            // Đảo hướng theo trục X
            Velocity v = getVelocity();
            setVelocity(new Velocity(-v.getDx(), v.getDy()));
        }
        
        if (getY() <= 0) {
            // Đảo hướng theo trục Y
            Velocity v = getVelocity();
            setVelocity(new Velocity(v.getDx(), -v.getDy()));
        }
        
        // Bóng rơi khỏi màn hình
        if (getY() > SCREEN_HEIGHT) {
            destroy();
        }
    }
}
```

### Ví dụ 2: PowerUp
```java
public abstract class PowerUp extends MovableObject {
    private static final double POWERUP_SIZE = 32;
    private static final double FALL_SPEED = 3;
    private PowerUpType type;
    
    public PowerUp(double x, double y, PowerUpType type) {
        super(x, y, POWERUP_SIZE, POWERUP_SIZE);
        this.type = type;
        // Power-up rơi xuống
        setVelocity(new Velocity(0, FALL_SPEED));
    }
    
    @Override
    public void update() {
        move(); // Rơi xuống theo vận tốc
        
        // Biến mất khi rơi ra khỏi màn hình
        if (getY() > SCREEN_HEIGHT) {
            destroy();
        }
    }
    
    public PowerUpType getType() {
        return type;
    }
}
```

### Ví dụ 3: Paddle
```java
public class Paddle extends MovableObject {
    private static final double PADDLE_WIDTH = 120;
    private static final double PADDLE_HEIGHT = 20;
    private static final double MOVE_SPEED = 8;
    
    public Paddle(double x, double y) {
        super(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
    }
    
    @Override
    public void update() {
        // Paddle không tự động di chuyển
        // Di chuyển được điều khiển bằng input
    }
    
    public void moveLeft() {
        setX(Math.max(0, getX() - MOVE_SPEED));
    }
    
    public void moveRight() {
        setX(Math.min(SCREEN_WIDTH - getWidth(), getX() + MOVE_SPEED));
    }
}
```

---

## Thiết kế và Lợi ích

### 1. **Tái sử dụng Code (Code Reuse)**
- Tránh lặp lại code cho vị trí, kích thước, vận tốc trong mỗi lớp
- Tất cả đối tượng di chuyển đều có chung bộ thuộc tính và phương thức

### 2. **Đóng gói (Encapsulation)**
- Dữ liệu được đánh dấu `private`, chỉ truy cập qua getter/setter
- Thay đổi internal implementation không ảnh hưởng đến code bên ngoài

### 3. **Kế thừa (Inheritance)**
- Lớp con chỉ cần implement `update()` với logic riêng
- Tự động có đầy đủ chức năng di chuyển, va chạm, trạng thái

### 4. **Polymorphism (Đa hình)**
- Có thể xử lý tất cả đối tượng di chuyển qua reference `MovableObject`
- Hỗ trợ collection chung: `List<MovableObject> objects`

---

## Luồng hoạt động điển hình

```
1. Khởi tạo đối tượng
   ↓
   Constructor → Thiết lập x, y, width, height
   ↓
   Velocity = (0, 0), alive = true

2. Thiết lập vận tốc ban đầu
   ↓
   setVelocity(new Velocity(dx, dy))

3. Game Loop (mỗi frame)
   ↓
   update() được gọi
   ↓
   move() → Cập nhật vị trí: x += dx, y += dy
   ↓
   Logic lớp con (kiểm tra boundary, collision, v.v.)
   ↓
   getBounds() được gọi bởi CollisionManager
   ↓
   Collision detection & response

4. Khi cần loại bỏ
   ↓
   destroy() → alive = false
   ↓
   isAlive() = false
   ↓
   Đối tượng bị xóa khỏi game
```

---

## Best Practices khi extend MovableObject

### 1. Luôn gọi `move()` trong `update()`
```java
@Override
public void update() {
    move(); // Cập nhật vị trí trước
    // Sau đó xử lý logic riêng
}
```

### 2. Kiểm tra boundaries
```java
@Override
public void update() {
    move();
    
    // Giữ trong màn hình
    if (getX() < 0) setX(0);
    if (getX() + getWidth() > SCREEN_WIDTH) {
        setX(SCREEN_WIDTH - getWidth());
    }
}
```

### 3. Không modify vận tốc trực tiếp trong `move()`
```java
// ❌ Tránh
public void move() {
    super.move();
    setVelocity(...); // Sẽ gây lỗi logic
}

// ✅ Đúng
@Override
public void update() {
    // Thay đổi vận tốc trước khi move
    if (someCondition) {
        setVelocity(newVelocity);
    }
    move();
}
```

### 4. Cleanup trong `destroy()`
```java
@Override
public void destroy() {
    super.destroy();
    // Cleanup resources nếu cần
    stopAnimation();
    releaseResources();
}
```

---

## Tích hợp với các hệ thống khác

### CollisionManager
```java
public void checkCollisions(List<MovableObject> objects) {
    for (MovableObject obj : objects) {
        if (!obj.isAlive()) continue;
        
        Rectangle bounds = obj.getBounds();
        // Kiểm tra va chạm với bounds
    }
}
```

### GameManager
```java
public void updateAll() {
    // Update tất cả đối tượng di chuyển
    balls.forEach(ball -> {
        if (ball.isAlive()) {
            ball.update();
        }
    });
    
    // Dọn dẹp đối tượng chết
    balls.removeIf(ball -> !ball.isAlive());
}
```

---

## Kết luận

`MovableObject` là một lớp nền tảng quan trọng trong kiến trúc game Arkanoid. Nó:

- **Đơn giản hóa** việc tạo đối tượng di chuyển mới
- **Chuẩn hóa** cách xử lý vị trí, kích thước, và vận tốc
- **Tích hợp** với hệ thống collision và game loop
- **Tái sử dụng** code hiệu quả qua inheritance

Bất kỳ đối tượng nào cần di chuyển trong game (Ball, Paddle, PowerUp, Laser, v.v.) đều nên extend lớp này để tận dụng các chức năng có sẵn và đảm bảo tính nhất quán trong code.
