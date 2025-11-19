# NormalBrick Class

## Tổng quan
`NormalBrick` là lớp đại diện cho gạch thường trong game Arkanoid - loại gạch phổ biến nhất và chiếm phần lớn trong mỗi level. Gạch thường có nhiều màu sắc khác nhau, mỗi màu có điểm số riêng, nhưng tất cả đều bị phá sau một lần va chạm. Đây là lớp đơn giản nhất trong hệ thống gạch, kế thừa trực tiếp từ Brick mà không có behavior đặc biệt.

## Vị trí
- **Package**: `Objects.Bricks`
- **File**: `src/Objects/Bricks/NormalBrick.java`
- **Kế thừa**: `Brick` (abstract)
- **Implements**: `GameObject` (gián tiếp qua Brick)

## Mục đích
NormalBrick:
- Đại diện cho gạch có thể phá sau 1 hit
- Hỗ trợ 8 màu sắc khác nhau (BLUE, RED, GREEN, YELLOW, ORANGE, PINK, CYAN, WHITE)
- Mỗi màu có điểm số riêng
- Không có behavior đặc biệt hoặc animation
- Là lớp "vanilla" trong hệ thống gạch

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
NormalBrick (Concrete Class)
    ↓
    Sử dụng 8 BrickType:
    - BLUE
    - RED
    - GREEN
    - YELLOW
    - ORANGE
    - PINK
    - CYAN
    - WHITE
```

---

## Thuộc tính (Fields)

### `private final BrickType brickType`

**Mô tả**: Loại gạch xác định màu sắc, điểm số và sprite.

**Kiểu**: `BrickType` enum (final - không thay đổi sau khi khởi tạo)

**Giá trị có thể**:
- `BrickType.BLUE` - Gạch xanh dương
- `BrickType.RED` - Gạch đỏ
- `BrickType.GREEN` - Gạch xanh lá
- `BrickType.YELLOW` - Gạch vàng
- `BrickType.ORANGE` - Gạch cam
- `BrickType.PINK` - Gạch hồng
- `BrickType.CYAN` - Gạch xanh ngọc
- `BrickType.WHITE` - Gạch trắng

**Getter**: `getBrickType()` (override từ Brick)

**Sử dụng**:
```java
NormalBrick brick = new NormalBrick(x, y, w, h, BrickType.RED);
// brick.brickType = BrickType.RED (immutable)

// Lấy thông tin
String sprite = brick.getBrickType().getSpriteName(); // "brick_red"
int score = brick.getBrickType().getBaseScore(); // BASE + 20
int hp = brick.getBrickType().getHitPoints(); // 1
```

---

## Constructor

### `NormalBrick(double x, double y, double width, double height, BrickType brickType)`

**Mô tả**: Khởi tạo gạch thường với vị trí, kích thước và loại xác định.

**Tham số**:
- `x` - tọa độ X (góc trên trái)
- `y` - tọa độ Y (góc trên trái)
- `width` - chiều rộng gạch
- `height` - chiều cao gạch
- `brickType` - loại gạch (màu sắc)

**Hành vi**:
1. Gọi `super(x, y, width, height, brickType.getHitPoints())`
   - Truyền HP từ BrickType (luôn = 1 cho gạch thường)
2. Lưu `brickType` vào field

**Ví dụ**:
```java
// Tạo gạch đỏ tại (100, 200), kích thước 64x32
NormalBrick redBrick = new NormalBrick(100, 200, 64, 32, BrickType.RED);

// Tạo gạch xanh dương
NormalBrick blueBrick = new NormalBrick(200, 200, 64, 32, BrickType.BLUE);

// Trong level generator
double brickWidth = Constants.Bricks.BRICK_WIDTH;
double brickHeight = Constants.Bricks.BRICK_HEIGHT;

for (int row = 0; row < 8; row++) {
    for (int col = 0; col < 12; col++) {
        double x = col * (brickWidth + spacing) + offsetX;
        double y = row * (brickHeight + spacing) + offsetY;
        
        // Chọn màu dựa trên row
        BrickType type = getColorForRow(row);
        NormalBrick brick = new NormalBrick(x, y, brickWidth, brickHeight, type);
        
        bricks.add(brick);
    }
}
```

---

## Phương thức

### 1. `void update()` (Override)

**Mô tả**: Cập nhật trạng thái gạch mỗi frame.

**Hành vi**: Không làm gì (empty method)

**Lý do**: 
- Gạch thường không có animation
- Không di chuyển
- Không có hiệu ứng đặc biệt

**Code**:
```java
@Override
public void update() {
    // Không có hành vi đặc biệt cho gạch thường
}
```

**Mở rộng tương lai**:
```java
// Giả sử muốn thêm hiệu ứng nhấp nháy
@Override
public void update() {
    if (isBlinking) {
        blinkTimer++;
        if (blinkTimer >= blinkInterval) {
            visible = !visible;
            blinkTimer = 0;
        }
    }
}
```

---

### 2. `BrickType getBrickType()` (Override)

**Mô tả**: Trả về loại gạch hiện tại.

**Kiểu trả về**: `BrickType` enum

**Hành vi**: `return brickType;`

**Sử dụng**:
```java
// Render gạch
String spriteName = brick.getBrickType().getSpriteName();
Sprite sprite = spriteCache.getSprite(spriteName);
graphics.drawImage(sprite.getImage(), brick.getX(), brick.getY());

// Tính điểm
if (!brick.isAlive()) {
    int points = brick.getBrickType().getBaseScore();
    scoreManager.addPoints(points);
}

// Logic đặc biệt theo màu
if (brick.getBrickType() == BrickType.WHITE) {
    // Gạch trắng có thể spawn power-up rare
    if (random.nextDouble() < 0.5) {
        spawnRarePowerUp(brick);
    }
}
```

---

### 3. `int getScoreValue()`

**Mô tả**: Lấy điểm số khi phá gạch (helper method).

**Kiểu trả về**: `int`

**Hành vi**: `return brickType.getBaseScore();`

**Sử dụng**:
```java
// Tính điểm trước khi phá
int potentialPoints = brick.getScoreValue();
if (comboActive) {
    potentialPoints *= comboMultiplier;
}

// Hiển thị preview điểm
showPointsPreview(brick.getX(), brick.getY(), potentialPoints);

// Phá gạch và cộng điểm
brick.takeHit();
if (!brick.isAlive()) {
    scoreManager.addPoints(brick.getScoreValue());
}
```

**Lưu ý**: Phương thức này giống với `getBrickType().getBaseScore()`, nhưng tiện lợi hơn.

---

## Bảng màu và điểm số

| BrickType | Màu sắc | HP | Điểm | Sprite |
|-----------|---------|----|----- |--------|
| BLUE | Xanh dương | 1 | BASE + 10 | brick_blue.png |
| RED | Đỏ | 1 | BASE + 20 | brick_red.png |
| GREEN | Xanh lá | 1 | BASE + 30 | brick_green.png |
| YELLOW | Vàng | 1 | BASE + 40 | brick_yellow.png |
| ORANGE | Cam | 1 | BASE + 50 | brick_orange.png |
| PINK | Hồng | 1 | BASE + 60 | brick_pink.png |
| CYAN | Xanh ngọc | 1 | BASE + 70 | brick_cyan.png |
| WHITE | Trắng | 1 | BASE + 80 | brick_white.png |

**Chiến thuật**:
- Ưu tiên phá gạch WHITE, CYAN, PINK (điểm cao)
- Level design: đặt gạch điểm cao ở vị trí khó

---

## Luồng hoạt động

### Lifecycle của NormalBrick

```
1. CREATION
   ↓
   new NormalBrick(x, y, width, height, BrickType.RED)
   → super(x, y, width, height, 1) // HP = 1
   → this.brickType = BrickType.RED
   → alive = true

2. ACTIVE STATE
   ↓
   Mỗi frame:
     update() → Không làm gì
     Render → Draw sprite "brick_red.png"
     
   Collision detection:
     if (ball hits brick) {
         takeHit()
     }

3. HIT
   ↓
   takeHit() được gọi
   → hitPoints-- (1 → 0)
   → hitPoints <= 0? Yes
   → destroy()
   → alive = false

4. DESTROYED
   ↓
   !isAlive() = true
   
   Spawn power-up (nếu may mắn)
   Add điểm: scoreManager.addPoints(getScoreValue())
   Hiệu ứng: playBrickBreakAnimation()

5. CLEANUP
   ↓
   bricks.removeIf(b -> !b.isAlive())
   → NormalBrick removed from game
   → Garbage collected
```

---

## Sử dụng trong Level Design

### 1. Rainbow Pattern

```java
public List<Brick> createRainbowPattern() {
    List<Brick> bricks = new ArrayList<>();
    BrickType[] rainbow = {
        BrickType.RED, BrickType.ORANGE, BrickType.YELLOW, BrickType.GREEN,
        BrickType.CYAN, BrickType.BLUE, BrickType.PINK, BrickType.WHITE
    };
    
    for (int row = 0; row < rainbow.length; row++) {
        for (int col = 0; col < 12; col++) {
            double x = startX + col * (brickWidth + spacing);
            double y = startY + row * (brickHeight + spacing);
            
            NormalBrick brick = new NormalBrick(x, y, brickWidth, brickHeight, rainbow[row]);
            bricks.add(brick);
        }
    }
    
    return bricks;
}
```

### 2. Checkerboard Pattern

```java
public List<Brick> createCheckerboard() {
    List<Brick> bricks = new ArrayList<>();
    
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 12; col++) {
            // Alternating colors
            BrickType type = ((row + col) % 2 == 0) ? BrickType.RED : BrickType.BLUE;
            
            double x = startX + col * (brickWidth + spacing);
            double y = startY + row * (brickHeight + spacing);
            
            NormalBrick brick = new NormalBrick(x, y, brickWidth, brickHeight, type);
            bricks.add(brick);
        }
    }
    
    return bricks;
}
```

### 3. Random Colors

```java
public List<Brick> createRandomPattern() {
    List<Brick> bricks = new ArrayList<>();
    BrickType[] normalTypes = {
        BrickType.BLUE, BrickType.RED, BrickType.GREEN, BrickType.YELLOW,
        BrickType.ORANGE, BrickType.PINK, BrickType.CYAN, BrickType.WHITE
    };
    
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 12; col++) {
            BrickType type = normalTypes[random.nextInt(normalTypes.length)];
            
            double x = startX + col * (brickWidth + spacing);
            double y = startY + row * (brickHeight + spacing);
            
            NormalBrick brick = new NormalBrick(x, y, brickWidth, brickHeight, type);
            bricks.add(brick);
        }
    }
    
    return bricks;
}
```

---

## So sánh với các loại gạch khác

| Đặc điểm | NormalBrick | SilverBrick | GoldBrick |
|----------|-------------|-------------|-----------|
| **HP** | 1 | 2 | 999 |
| **Hits để phá** | 1 | 2 | Không thể |
| **Animation** | Không | Crack (HP=1) | Không |
| **Màu sắc** | 8 màu | 1 màu | 1 màu |
| **Điểm** | BASE+10~80 | BASE | BASE |
| **Complexity** | Đơn giản | Trung bình | Đơn giản |
| **Lớp** | NormalBrick | SilverBrick | GoldBrick |
| **Use Case** | Phần lớn level | Tăng độ khó | Chướng ngại vật |

---

## Best Practices

### 1. Factory Pattern
```java
// ✅ Đúng - dùng factory
public class BrickFactory {
    public static Brick createNormalBrick(double x, double y, BrickType type) {
        return new NormalBrick(
            x, y,
            Constants.Bricks.BRICK_WIDTH,
            Constants.Bricks.BRICK_HEIGHT,
            type
        );
    }
}

Brick brick = BrickFactory.createNormalBrick(100, 200, BrickType.RED);

// ❌ Sai - hardcode values
Brick brick = new NormalBrick(100, 200, 64, 32, BrickType.RED);
```

### 2. BrickType validation
```java
// ✅ Đúng - chỉ dùng normal types
private static final Set<BrickType> NORMAL_TYPES = Set.of(
    BrickType.BLUE, BrickType.RED, BrickType.GREEN, BrickType.YELLOW,
    BrickType.ORANGE, BrickType.PINK, BrickType.CYAN, BrickType.WHITE
);

public NormalBrick(double x, double y, double w, double h, BrickType type) {
    if (type == BrickType.SILVER || type == BrickType.GOLD) {
        throw new IllegalArgumentException("Use SilverBrick/GoldBrick instead!");
    }
    super(x, y, w, h, type.getHitPoints());
    this.brickType = type;
}

// ❌ Sai - cho phép SILVER/GOLD
new NormalBrick(x, y, w, h, BrickType.SILVER); // Logic error!
```

### 3. Immutability
```java
// ✅ Đúng - brickType là final
private final BrickType brickType;
// Không thể thay đổi sau khi khởi tạo

// ❌ Sai - mutable
private BrickType brickType;
public void setBrickType(BrickType type) { // Không nên có!
    this.brickType = type;
}
```

---

## Tích hợp với game systems

### Collision System
```java
// Trong CollisionManager
if (ball.checkCollisionWithRect(brick.getBounds())) {
    brick.takeHit(); // NormalBrick: HP 1 → 0 → destroy
    
    if (!brick.isAlive()) {
        scoreManager.addPoints(brick.getScoreValue());
        audioManager.playBrickBreakSound();
    }
}
```

### Rendering System
```java
// Trong BrickRenderer
public void renderNormalBrick(NormalBrick brick, Graphics g) {
    String spriteName = brick.getBrickType().getSpriteName();
    Sprite sprite = spriteCache.getSprite(spriteName);
    
    g.drawImage(sprite.getImage(), brick.getX(), brick.getY());
}
```

### Score System
```java
// Trong ScoreManager
public void onBrickDestroyed(Brick brick) {
    int points = 0;
    
    if (brick instanceof NormalBrick) {
        NormalBrick normal = (NormalBrick) brick;
        points = normal.getScoreValue();
        
        // Bonus cho gạch điểm cao
        if (normal.getBrickType() == BrickType.WHITE) {
            points += 50; // Bonus
        }
    }
    
    addPoints(points);
}
```

---

## Kết luận

`NormalBrick` là lớp đơn giản nhất nhưng quan trọng nhất trong hệ thống gạch:

- **Simplicity**: Không có behavior phức tạp
- **Variety**: 8 màu sắc khác nhau
- **Foundation**: Chiếm phần lớn gạch trong game
- **Extensibility**: Dễ dàng thêm màu mới hoặc behavior đặc biệt
- **Clean Design**: Kế thừa sạch sẽ từ Brick

NormalBrick là ví dụ điển hình của một concrete class đơn giản, kế thừa từ abstract class và implement các phương thức trừu tượng một cách straightforward. Lớp này chứng minh rằng không phải lúc nào cũng cần phức tạp - đôi khi đơn giản là tốt nhất.
