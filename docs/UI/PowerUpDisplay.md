# PowerUpDisplay

## Tổng quan
Lớp `PowerUpDisplay` dùng để hiển thị biểu tượng PowerUp trên giao diện (UI) của trò chơi. Lớp này chạy một animation nhỏ để chỉ báo PowerUp nào đang hoạt động, tạo hiệu ứng sinh động cho người chơi.

## Package
```java
package UI;
```

## Thuộc tính

### Thuộc tính PowerUp
- `PowerUpType type`: Loại PowerUp mà đối tượng này đại diện (CATCH, LASER, EXPAND, v.v.)
- `SpriteProvider sprites`: Đối tượng cung cấp sprite frames cho animation

### Thuộc tính vị trí và kích thước
- `double x, y`: Tọa độ trung tâm để vẽ (sử dụng để căn giữa biểu tượng)
- `double width, height`: Kích thước vẽ của biểu tượng

### Thuộc tính animation
- `int currentFrame`: Chỉ số khung hình hiện tại của animation
- `long lastFrameTime`: Thời điểm (milliseconds) lần cuối chuyển khung hình
- `static final long FRAME_DURATION = 100`: Thời gian hiển thị mỗi frame (100ms)
- `static final int TOTAL_FRAMES = 8`: Tổng số frames trong animation PowerUp

## Constructor

### PowerUpDisplay(PowerUpType type, double x, double y, double width, double height, SpriteProvider sprites)
Khởi tạo một PowerUpDisplay mới với các thông số:
- **type**: Loại PowerUp cần hiển thị
- **x, y**: Tọa độ trung tâm để vẽ
- **width, height**: Kích thước hiển thị
- **sprites**: Đối tượng SpriteProvider để lấy frames

```java
PowerUpDisplay display = new PowerUpDisplay(
    PowerUpType.LASER,
    50,   // x center
    50,   // y center
    40,   // width
    40,   // height
    spriteProvider
);
```

## Phương thức

### update(long currentTime)
Cập nhật chỉ số khung hình để tạo hiệu ứng animation.

**Cơ chế hoạt động:**
1. Kiểm tra xem đã đủ thời gian (100ms) để chuyển frame chưa
2. Nếu đủ thời gian, chuyển sang frame tiếp theo
3. Lặp lại từ frame 0 khi đạt đến frame cuối cùng (frame 7)
4. Cập nhật thời gian chuyển frame

**Tham số:**
- **currentTime**: Thời gian hệ thống hiện tại (mili giây), thường lấy từ `System.currentTimeMillis()`

```java
long currentTime = System.currentTimeMillis();
display.update(currentTime);
```

### render(GraphicsContext gc)
Vẽ khung hình animation hiện tại lên canvas.

**Cơ chế hoạt động:**
1. Lấy danh sách frames tương ứng với loại PowerUp từ `SpriteProvider`
2. Kiểm tra tính hợp lệ của frames
3. Lấy sprite tại chỉ số `currentFrame`
4. Tính toán tọa độ vẽ để căn giữa sprite tại (x, y)
5. Vẽ sprite lên canvas

**Tham số:**
- **gc**: GraphicsContext để vẽ

```java
display.render(gc);
```

## Getters

- `PowerUpType getType()`: Lấy loại PowerUp
- `double getX()`: Lấy tọa độ trung tâm X
- `double getY()`: Lấy tọa độ trung tâm Y

## Cách sử dụng

### Ví dụ cơ bản
```java
// Khởi tạo
SpriteProvider spriteProvider = new SpriteProvider(...);
PowerUpDisplay laserDisplay = new PowerUpDisplay(
    PowerUpType.LASER,
    100, 100,  // vị trí center
    40, 40,    // kích thước
    spriteProvider
);

// Trong game loop
long currentTime = System.currentTimeMillis();
laserDisplay.update(currentTime);
laserDisplay.render(gc);
```

### Ví dụ hiển thị nhiều PowerUps đang hoạt động
```java
// Danh sách các PowerUp đang active
List<PowerUpDisplay> activeDisplays = new ArrayList<>();

// Khi PowerUp được kích hoạt
void activatePowerUp(PowerUpType type) {
    PowerUpDisplay display = new PowerUpDisplay(
        type,
        startX + (activeDisplays.size() * 50),  // Xếp ngang
        50,
        40, 40,
        spriteProvider
    );
    activeDisplays.add(display);
}

// Trong game loop
long currentTime = System.currentTimeMillis();
for (PowerUpDisplay display : activeDisplays) {
    display.update(currentTime);
    display.render(gc);
}

// Khi PowerUp hết hiệu lực
void deactivatePowerUp(PowerUpType type) {
    activeDisplays.removeIf(d -> d.getType() == type);
}
```

### Ví dụ hiển thị PowerUp với thông tin thời gian còn lại
```java
// Vẽ PowerUp icon
powerUpDisplay.render(gc);

// Vẽ thời gian còn lại bên cạnh
gc.setFill(Color.WHITE);
gc.setFont(Font.font("Arial", 14));
gc.fillText(
    remainingTime + "s",
    powerUpDisplay.getX() + 25,
    powerUpDisplay.getY()
);
```

## Animation

PowerUpDisplay sử dụng sprite animation với:
- **8 frames**: Mỗi PowerUp có 8 khung hình khác nhau
- **100ms per frame**: Mỗi khung hình hiển thị trong 100 milliseconds
- **Looping**: Animation lặp lại liên tục từ frame 0 đến 7

### Timeline animation
```
Frame 0 -> Frame 1 -> Frame 2 -> ... -> Frame 7 -> Frame 0 (lặp lại)
   |          |          |                 |
 100ms      100ms      100ms             100ms
```

Tổng thời gian một chu kỳ: **8 frames × 100ms = 800ms (0.8 giây)**

## Các loại PowerUp hỗ trợ

PowerUpDisplay hỗ trợ hiển thị tất cả các loại PowerUp trong game:
- `CATCH`: Bắt bóng
- `LASER`: Bắn laser
- `EXPAND`: Mở rộng paddle
- `DUPLICATE`: Nhân đôi bóng
- `SLOW_BALL`: Làm chậm bóng
- `LIFE`: Thêm mạng
- `WARP`: Dịch chuyển bóng

## Lưu ý kỹ thuật

1. **Căn giữa sprite**: Tọa độ (x, y) là tâm, không phải góc trên trái
   ```java
   double drawX = x - width / 2;
   double drawY = y - height / 2;
   ```

2. **Frame validation**: Luôn kiểm tra frames trước khi vẽ để tránh lỗi
   ```java
   if (frames != null && !frames.isEmpty() && currentFrame < frames.size())
   ```

3. **Time-based animation**: Sử dụng thời gian thực, không phụ thuộc vào FPS

4. **Modulo cho looping**: Sử dụng `(currentFrame + 1) % TOTAL_FRAMES` để lặp animation

## Dependencies
- `Objects.PowerUps.PowerUpType`: Enum định nghĩa các loại PowerUp
- `Utils.SpriteProvider`: Cung cấp sprite frames
- `javafx.scene.canvas.GraphicsContext`: Để vẽ UI
- `javafx.scene.image.Image`: Đại diện cho sprite image
- `java.util.List`: Để lưu danh sách frames
