# GameObject Interface

## Tổng quan
`GameObject` là một interface cơ bản định nghĩa các hành vi mà mọi đối tượng trong trò chơi Arkanoid phải có. Đây là nền tảng của hệ thống đối tượng trong game, đảm bảo tất cả các thực thể (gạch, bóng, thanh trượt, power-ups, v.v.) đều tuân theo một giao thức chung.

## Vị trí
- **Package**: `Objects.Core`
- **File**: `src/Objects/Core/GameObject.java`
- **Loại**: Interface

## Mục đích
Interface này thiết lập một "hợp đồng" (contract) cho tất cả các đối tượng trong game, đảm bảo chúng có thể:
- Cập nhật trạng thái của mình theo thời gian
- Cung cấp thông tin về vùng va chạm (hitbox)
- Báo cáo trạng thái sống/chết
- Bị phá hủy khi cần thiết

## Các phương thức

### 1. `void update()`
**Mô tả**: Cập nhật trạng thái của đối tượng trong mỗi khung hình (frame) của game loop.

**Mục đích**:
- Được gọi liên tục trong vòng lặp game (game loop)
- Xử lý logic cập nhật vị trí, animation, hiệu ứng
- Thực hiện các hành động theo thời gian của đối tượng

**Ví dụ sử dụng**:
```java
// Trong game loop
for (GameObject obj : gameObjects) {
    obj.update(); // Cập nhật tất cả đối tượng mỗi frame
}
```

**Lưu ý**: Mỗi lớp implement phải định nghĩa logic cụ thể của riêng mình (ví dụ: bóng di chuyển, gạch kiểm tra va chạm, v.v.)

---

### 2. `Rectangle getBounds()`
**Mô tả**: Trả về vùng bao (hitbox/bounding box) của đối tượng, dùng cho việc phát hiện va chạm.

**Mục đích**:
- Cung cấp thông tin về kích thước và vị trí đối tượng trong không gian game
- Được sử dụng bởi hệ thống va chạm (CollisionManager) để kiểm tra giao nhau giữa các đối tượng

**Kiểu trả về**: `Rectangle` - một hình chữ nhật đại diện cho vùng chiếm chỗ của đối tượng

**Ví dụ sử dụng**:
```java
// Kiểm tra va chạm giữa bóng và gạch
Rectangle ballBounds = ball.getBounds();
Rectangle brickBounds = brick.getBounds();

if (ballBounds.intersects(brickBounds)) {
    // Xử lý va chạm
}
```

---

### 3. `boolean isAlive()`
**Mô tả**: Kiểm tra xem đối tượng còn tồn tại (hoạt động) trong game hay không.

**Mục đích**:
- Xác định đối tượng có còn cần được cập nhật và vẽ lên màn hình không
- Cho phép game loop loại bỏ các đối tượng đã bị phá hủy khỏi danh sách xử lý

**Kiểu trả về**: 
- `true` - đối tượng còn hoạt động
- `false` - đối tượng đã bị phá hủy/không còn tồn tại

**Ví dụ sử dụng**:
```java
// Lọc bỏ các đối tượng đã chết
gameObjects.removeIf(obj -> !obj.isAlive());

// Chỉ cập nhật đối tượng còn sống
if (brick.isAlive()) {
    brick.update();
}
```

---

### 4. `void destroy()`
**Mô tả**: Phá hủy đối tượng, đánh dấu nó không còn tồn tại trong game.

**Mục đích**:
- Đánh dấu đối tượng cần bị loại bỏ
- Thường được gọi khi: gạch bị phá vỡ, bóng rơi xuống đáy, power-up bị thu thập, v.v.
- Có thể kích hoạt các hiệu ứng phụ (âm thanh, điểm số, particle effects)

**Ví dụ sử dụng**:
```java
// Khi bóng va gạch
if (ball.getBounds().intersects(brick.getBounds())) {
    brick.destroy(); // Gạch bị phá hủy
    scoreManager.addPoints(brick.getPoints()); // Cộng điểm
    audioManager.playBreakSound(); // Phát âm thanh
}

// Khi thu thập power-up
if (paddle.getBounds().intersects(powerUp.getBounds())) {
    powerUp.destroy();
    applyPowerUp(powerUp.getType());
}
```

---

## Các lớp implement
Các lớp sau triển khai interface `GameObject`:

1. **MovableObject** (abstract class): 
   - Lớp trừu tượng kế thừa GameObject
   - Thêm khả năng di chuyển với velocity
   - Là lớp cha của Ball, Paddle, PowerUp, Laser

2. **Brick** (abstract class):
   - Các loại gạch: NormalBrick, GoldBrick, SilverBrick
   - Triển khai trực tiếp từ GameObject

## Quan hệ với các component khác

```
GameObject (Interface)
    ├── MovableObject (Abstract Class)
    │   ├── Ball
    │   ├── Paddle
    │   ├── PowerUp (và các loại power-up)
    │   └── Laser
    └── Brick (Abstract Class)
        ├── NormalBrick
        ├── GoldBrick
        └── SilverBrick
```

## Lợi ích của Interface này

### 1. **Polymorphism (Đa hình)**
Cho phép xử lý tất cả các đối tượng game thông qua một interface chung:
```java
List<GameObject> allObjects = new ArrayList<>();
allObjects.add(ball);
allObjects.add(paddle);
allObjects.add(brick);

// Cập nhật tất cả cùng một cách
for (GameObject obj : allObjects) {
    obj.update();
}
```

### 2. **Loose Coupling (Khớp nối lỏng)**
Các hệ thống game (CollisionManager, GameManager) chỉ cần biết về GameObject interface, không cần biết chi tiết từng loại đối tượng cụ thể.

### 3. **Extensibility (Khả năng mở rộng)**
Dễ dàng thêm loại đối tượng mới bằng cách implement interface này.

### 4. **Maintainability (Dễ bảo trì)**
Thay đổi cách xử lý đối tượng ở một nơi sẽ ảnh hưởng đến tất cả các lớp implement.

## Best Practices khi implement

1. **update()**: Nên giữ logic ngắn gọn, tránh xử lý nặng trong một frame
2. **getBounds()**: Nên cache kết quả nếu không thay đổi thường xuyên
3. **isAlive()**: Nên return một biến boolean đơn giản, không tính toán phức tạp
4. **destroy()**: Nên thực hiện cleanup (giải phóng resources) nếu cần thiết

## Ví dụ triển khai hoàn chỉnh

```java
public class Enemy implements GameObject {
    private double x, y, width, height;
    private boolean alive = true;
    
    @Override
    public void update() {
        // Logic di chuyển và hành vi của enemy
        if (alive) {
            x += speed;
            checkBoundaries();
        }
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point(x, y), width, height);
    }
    
    @Override
    public boolean isAlive() {
        return alive;
    }
    
    @Override
    public void destroy() {
        alive = false;
        // Có thể thêm: phát hiệu ứng nổ, cộng điểm, v.v.
    }
}
```

## Kết luận
`GameObject` là nền tảng của kiến trúc hướng đối tượng trong game Arkanoid. Nó cung cấp một interface thống nhất cho tất cả các thực thể trong game, giúp code dễ quản lý, mở rộng và bảo trì. Mọi đối tượng xuất hiện trong game đều phải tuân theo "hợp đồng" này.
