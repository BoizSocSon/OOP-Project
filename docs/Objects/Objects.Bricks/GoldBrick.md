# GoldBrick Class

## Tổng quan
`GoldBrick` là gạch vàng - loại gạch đặc biệt và khó nhất trong game Arkanoid. Đây là gạch KHÔNG THỂ PHÁ (indestructible), với 999 HP và phương thức `takeHit()` không làm gì cả. GoldBrick hoạt động như một chướng ngại vật vĩnh viễn, buộc người chơi phải tính toán trajectory của ball để tránh hoặc sử dụng chúng như điểm bounce chiến lược.

## Vị trí
- **Package**: `Objects.Bricks`
- **File**: `src/Objects/Bricks/GoldBrick.java`
- **Kế thừa**: `Brick` (abstract)
- **Implements**: `GameObject` (gián tiếp qua Brick)

## Mục đích
GoldBrick:
- Tạo chướng ngại vật vĩnh viễn trong level
- Buộc người chơi phải suy nghĩ về ball trajectory
- Tạo "safe zones" mà ball có thể bounce
- Tăng độ khó và chiến thuật của level
- Hạn chế không gian di chuyển của ball
- Tạo patterns và layouts thú vị hơn

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
GoldBrick (Concrete Class)
    │
    └── BrickType.GOLD (999 HP - INDESTRUCTIBLE)
```

---

## Thuộc tính (Fields)

### `private final BrickType brickType = BrickType.GOLD`

**Mô tả**: Loại gạch (luôn là GOLD).

**Kiểu**: `BrickType` enum (final constant)

**Giá trị**: `BrickType.GOLD`
- hitPoints = 999 (thực tế không bao giờ giảm)
- spriteName = "brick_gold"
- baseScore = BASE (nhưng không bao giờ được điểm vì không phá được)

**Đặc điểm**:
- **Indestructible**: Không thể phá bằng ball thường
- **Permanent**: Tồn tại suốt level
- **Obstacle**: Chỉ là chướng ngại vật

**Getter**: `getBrickType()` (override từ Brick)

**Lưu ý**: 
- HP = 999 là "magic number" đại diện cho vô hạn
- Trong thực tế, `takeHit()` không làm giảm HP
- Có thể phá bằng power-ups đặc biệt (nếu implement)

---

## Constructor

### `GoldBrick(double x, double y, double width, double height)`

**Mô tả**: Khởi tạo gạch vàng với vị trí và kích thước xác định.

**Tham số**:
- `x` - tọa độ X (góc trên trái)
- `y` - tọa độ Y (góc trên trái)
- `width` - chiều rộng gạch
- `height` - chiều cao gạch

**Hành vi**:
1. Gọi `super(x, y, width, height, BrickType.GOLD.getHitPoints())`
   - HP = 999 (từ BrickType.GOLD)
2. alive = true (và sẽ luôn là true)

**Khởi tạo nội bộ**:
```java
public GoldBrick(double x, double y, double width, double height) {
    super(x, y, width, height, BrickType.GOLD.getHitPoints()); // HP = 999
    // No additional initialization needed
}
```

**Ví dụ**:
```java
// Tạo gạch vàng đơn lẻ
GoldBrick goldBrick = new GoldBrick(300, 150, 64, 32);

// Tạo hàng gạch vàng (wall)
List<GoldBrick> goldWall = new ArrayList<>();
for (int i = 0; i < 10; i++) {
    double x = startX + i * (brickWidth + spacing);
    double y = 200;
    goldWall.add(new GoldBrick(x, y, brickWidth, brickHeight));
}

// Trong level generator
public void addGoldObstacles(List<Brick> bricks) {
    // Tạo chữ "T" bằng gold bricks
    // Vertical line
    for (int row = 0; row < 5; row++) {
        double x = centerX;
        double y = startY + row * (brickHeight + spacing);
        bricks.add(new GoldBrick(x, y, brickWidth, brickHeight));
    }
    
    // Horizontal line
    for (int col = -2; col <= 2; col++) {
        double x = centerX + col * (brickWidth + spacing);
        double y = startY;
        bricks.add(new GoldBrick(x, y, brickWidth, brickHeight));
    }
}
```

---

## Phương thức

### 1. `void update()` (Override)

**Mô tả**: Cập nhật trạng thái gạch mỗi frame.

**Hành vi**: Không làm gì (empty method)

**Lý do**:
- GoldBrick không có animation
- Không di chuyển
- Không thay đổi trạng thái
- Là object hoàn toàn static

**Code**:
```java
@Override
public void update() {
    // Gold bricks không có hành vi động
}
```

**Mở rộng tương lai**:
```java
// Có thể thêm shine animation
@Override
public void update() {
    shineTimer++;
    if (shineTimer >= shineInterval) {
        playShineEffect();
        shineTimer = 0;
    }
}
```

---

### 2. `void takeHit()` (Override)

**Mô tả**: Xử lý khi gạch bị đánh - KHÔNG LÀM GÌ CẢ.

**Hành vi**: Empty method (không gọi `super.takeHit()`)

**Code**:
```java
@Override
public void takeHit() {
    // Gold brick không bị phá
    // HP vẫn là 999, alive vẫn là true
}
```

**Lý do**:
- GoldBrick là indestructible
- Không giảm HP
- Không có destroy logic
- Ball chỉ bounce off

**Effect**:
```java
// Khi ball hits gold brick
if (ball.checkCollisionWithRect(goldBrick.getBounds())) {
    goldBrick.takeHit(); // Does nothing
    
    // Ball bounces (collision response)
    ball.reverseVelocityY();
    
    // Play metallic sound
    audioManager.playGoldBrickHitSound(); // "clang!"
    
    // Gold brick vẫn alive, HP vẫn 999
}
```

**Comparison**:
```java
// NormalBrick
normalBrick.takeHit(); // HP: 1 → 0, destroyed

// SilverBrick
silverBrick.takeHit(); // HP: 2 → 1, cracked

// GoldBrick
goldBrick.takeHit(); // HP: 999 → 999, no change
```

---

### 3. `BrickType getBrickType()` (Override)

**Mô tả**: Trả về loại gạch (luôn là GOLD).

**Kiểu trả về**: `BrickType.GOLD`

**Hành vi**: `return brickType;`

**Sử dụng**:
```java
// Render logic
if (brick.getBrickType() == BrickType.GOLD) {
    // Special rendering cho gold (shine, glow)
    Sprite goldSprite = spriteCache.getSprite("brick_gold");
    graphics.drawImage(goldSprite.getImage(), brick.getX(), brick.getY());
    
    // Add glow effect
    renderGlowEffect(brick, graphics);
}

// Sound logic
if (brick.getBrickType() == BrickType.GOLD) {
    audioManager.playMetallicSound(); // Different from normal brick
} else {
    audioManager.playNormalBrickSound();
}

// Power-up logic
if (brick.getBrickType() == BrickType.GOLD) {
    // Gold bricks không spawn power-ups
    return;
}
```

---

### 4. `int getScoreValue()`

**Mô tả**: Lấy điểm số khi phá gạch.

**Kiểu trả về**: `int`

**Hành vi**: `return brickType.getBaseScore();`

**Giá trị**: `BrickType.GOLD.getBaseScore()` (thường = BASE score)

**Lưu ý quan trọng**: 
- Phương thức này tồn tại nhưng KHÔNG BAO GIỜ được gọi
- Vì GoldBrick không bao giờ destroyed
- `!isAlive()` luôn luôn là false

**Ví dụ**:
```java
// Code này sẽ KHÔNG BAO GIỜ execute
if (!goldBrick.isAlive()) { // Always false
    scoreManager.addPoints(goldBrick.getScoreValue()); // Never called
}

// Đúng cách
if (brick instanceof GoldBrick) {
    // Không cộng điểm, chỉ bounce
    ball.reverseVelocityY();
} else {
    brick.takeHit();
    if (!brick.isAlive()) {
        scoreManager.addPoints(brick.getScoreValue());
    }
}
```

---

## Luồng hoạt động

### Lifecycle của GoldBrick

```
1. CREATION (HP = 999)
   ↓
   new GoldBrick(x, y, width, height)
   → super(..., 999) // hitPoints = 999
   → alive = true
   
   Render: brick_gold.png (vàng sáng)

2. ACTIVE STATE (Vĩnh viễn)
   ↓
   Mỗi frame:
     update() → Không làm gì
     Render → Draw gold sprite (có thể có shine effect)
     
   Collision detection:
     if (ball hits brick) {
         takeHit() → Does nothing
         ball.bounce() → Phản xạ
         playMetallicSound()
     }

3. HIT (Infinite times)
   ↓
   Ball collision #1
   → takeHit() called → Nothing happens
   → HP: 999 (unchanged)
   → alive: true (unchanged)
   → Ball bounces off
   
   Ball collision #2
   → takeHit() called → Nothing happens
   → HP: 999 (unchanged)
   → alive: true (unchanged)
   → Ball bounces off
   
   ... repeat forever

4. END OF LEVEL
   ↓
   All other bricks destroyed
   GoldBrick still exists
   
   Level clear condition:
     if (allNormalBricksGone()) {
         levelComplete(); // Ignore gold bricks
     }

5. CLEANUP
   ↓
   Level transition
   → bricks.clear() → GoldBrick removed
   → New level loads
   → New GoldBricks created (if level has them)
```

**Key Point**: GoldBrick không có "destroyed" state - chỉ có "active" và "removed" (khi level ends)

---

## So sánh với các loại gạch khác

| Đặc điểm | NormalBrick | SilverBrick | GoldBrick |
|----------|-------------|-------------|-----------|
| **HP** | 1 | 2 | 999 (vô hạn) |
| **Destructible** | ✅ Yes | ✅ Yes | ❌ No |
| **Hits để phá** | 1 | 2 | ∞ (không thể) |
| **Animation** | Không | Crack | Không |
| **takeHit() effect** | Giảm HP → destroy | Giảm HP → crack → destroy | Không làm gì |
| **Score** | BASE+10~80 | BASE | 0 (không phá được) |
| **Purpose** | Phá để clear level | Tăng độ khó | Chướng ngại vật |
| **Ball interaction** | Break | Break (2 hits) | Bounce only |
| **Strategy** | Hit để phá | Tập trung 2 hits | Tránh hoặc dùng để bounce |
| **isAlive()** | true → false | true → false | Always true |
| **Level clear** | Phải phá | Phải phá | Ignore |

---

## Sử dụng trong Level Design

### 1. Wall Obstacle (Tường cản)

```java
public List<Brick> createGoldWall() {
    List<Brick> bricks = new ArrayList<>();
    
    // Vertical wall in center
    for (int row = 2; row < 6; row++) {
        double x = screenWidth / 2 - brickWidth / 2;
        double y = startY + row * (brickHeight + spacing);
        
        bricks.add(new GoldBrick(x, y, brickWidth, brickHeight));
    }
    
    // Normal bricks on both sides
    for (int row = 2; row < 6; row++) {
        for (int col = 0; col < 5; col++) {
            // Left side
            double xLeft = startX + col * (brickWidth + spacing);
            double y = startY + row * (brickHeight + spacing);
            bricks.add(new NormalBrick(xLeft, y, brickWidth, brickHeight, 
                BrickType.RED));
            
            // Right side
            double xRight = screenWidth / 2 + brickWidth / 2 + 
                           col * (brickWidth + spacing);
            bricks.add(new NormalBrick(xRight, y, brickWidth, brickHeight, 
                BrickType.BLUE));
        }
    }
    
    return bricks;
    // Strategy: Ball phải curve around gold wall
}
```

### 2. Box Enclosure (Hộp vây)

```java
public List<Brick> createGoldBox() {
    List<Brick> bricks = new ArrayList<>();
    
    int boxWidth = 8;
    int boxHeight = 6;
    
    // Top and bottom borders (gold)
    for (int col = 0; col < boxWidth; col++) {
        double x = centerX - (boxWidth / 2) * (brickWidth + spacing) + 
                   col * (brickWidth + spacing);
        
        // Top
        double yTop = centerY - (boxHeight / 2) * (brickHeight + spacing);
        bricks.add(new GoldBrick(x, yTop, brickWidth, brickHeight));
        
        // Bottom
        double yBottom = centerY + (boxHeight / 2) * (brickHeight + spacing);
        bricks.add(new GoldBrick(x, yBottom, brickWidth, brickHeight));
    }
    
    // Left and right borders (gold)
    for (int row = 1; row < boxHeight - 1; row++) {
        double y = centerY - (boxHeight / 2) * (brickHeight + spacing) + 
                   row * (brickHeight + spacing);
        
        // Left
        double xLeft = centerX - (boxWidth / 2) * (brickWidth + spacing);
        bricks.add(new GoldBrick(xLeft, y, brickWidth, brickHeight));
        
        // Right
        double xRight = centerX + (boxWidth / 2) * (brickWidth + spacing);
        bricks.add(new GoldBrick(xRight, y, brickWidth, brickHeight));
    }
    
    // Inner bricks (normal - high value)
    for (int row = 1; row < boxHeight - 1; row++) {
        for (int col = 1; col < boxWidth - 1; col++) {
            double x = centerX - (boxWidth / 2) * (brickWidth + spacing) + 
                       col * (brickWidth + spacing);
            double y = centerY - (boxHeight / 2) * (brickHeight + spacing) + 
                       row * (brickHeight + spacing);
            
            bricks.add(new NormalBrick(x, y, brickWidth, brickHeight, 
                BrickType.WHITE)); // High score bricks inside
        }
    }
    
    return bricks;
    // Strategy: Phải tìm cách bắn ball vào trong box
}
```

### 3. Maze Pattern

```java
public List<Brick> createGoldMaze() {
    List<Brick> bricks = new ArrayList<>();
    
    // Define maze pattern (1 = gold, 0 = space/normal)
    int[][] maze = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1},
        {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1},
        {1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    
    for (int row = 0; row < maze.length; row++) {
        for (int col = 0; col < maze[row].length; col++) {
            double x = startX + col * (brickWidth + spacing);
            double y = startY + row * (brickHeight + spacing);
            
            if (maze[row][col] == 1) {
                bricks.add(new GoldBrick(x, y, brickWidth, brickHeight));
            } else {
                // Random normal bricks in open spaces
                if (random.nextDouble() < 0.6) {
                    BrickType type = randomNormalType();
                    bricks.add(new NormalBrick(x, y, brickWidth, brickHeight, type));
                }
            }
        }
    }
    
    return bricks;
    // Strategy: Navigate ball through maze corridors
}
```

### 4. Strategic Bounce Points

```java
public List<Brick> createBouncePoints() {
    List<Brick> bricks = new ArrayList<>();
    
    // Create normal bricks in hard-to-reach areas
    for (int row = 0; row < 5; row++) {
        for (int col = 2; col < 10; col++) {
            double x = startX + col * (brickWidth + spacing);
            double y = startY + row * (brickHeight + spacing);
            bricks.add(new NormalBrick(x, y, brickWidth, brickHeight, 
                BrickType.CYAN));
        }
    }
    
    // Add gold bricks as bounce points to reach top area
    double[] goldPositions = {
        screenWidth * 0.25,
        screenWidth * 0.5,
        screenWidth * 0.75
    };
    
    for (double xPos : goldPositions) {
        double y = startY + 6 * (brickHeight + spacing);
        bricks.add(new GoldBrick(xPos, y, brickWidth, brickHeight));
    }
    
    return bricks;
    // Strategy: Phải dùng gold bricks để bounce ball lên phá bricks ở trên
}
```

---

## Rendering GoldBrick

### Basic Rendering

```java
public void renderGoldBrick(GoldBrick brick, Graphics g) {
    if (!brick.isAlive()) return; // Always true for gold
    
    Sprite goldSprite = spriteCache.getSprite("brick_gold");
    g.drawImage(goldSprite.getImage(), 
               (int) brick.getX(), 
               (int) brick.getY());
}
```

### Advanced Rendering với Effects

```java
public void renderGoldBrickAdvanced(GoldBrick brick, Graphics2D g) {
    if (!brick.isAlive()) return;
    
    double x = brick.getX();
    double y = brick.getY();
    double w = brick.getWidth();
    double h = brick.getHeight();
    
    // 1. Base gold sprite
    Sprite sprite = spriteCache.getSprite("brick_gold");
    g.drawImage(sprite.getImage(), (int) x, (int) y);
    
    // 2. Metallic shine effect (animated)
    if (shineEffect) {
        int shineOffset = (int) ((System.currentTimeMillis() / 10) % w);
        
        g.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.5f));
        
        GradientPaint shine = new GradientPaint(
            (float) (x + shineOffset), (float) y, Color.WHITE,
            (float) (x + shineOffset + 20), (float) y, 
            new Color(255, 255, 255, 0)
        );
        
        g.setPaint(shine);
        g.fillRect((int) (x + shineOffset), (int) y, 20, (int) h);
        
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    // 3. Glow effect
    if (glowEffect) {
        g.setColor(new Color(255, 215, 0, 50)); // Gold color with alpha
        g.fillRect((int) (x - 2), (int) (y - 2), 
                  (int) (w + 4), (int) (h + 4));
    }
    
    // 4. Indestructible indicator
    if (showIndestructibleIcon) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("∞", (int) (x + w / 2 - 5), (int) (y + h / 2 + 5));
    }
}
```

---

## Best Practices

### 1. Level Clear Condition
```java
// ✅ Đúng - ignore gold bricks khi check level clear
public boolean isLevelComplete() {
    for (Brick brick : bricks) {
        // Chỉ đếm normal và silver bricks
        if (brick.isAlive() && !(brick instanceof GoldBrick)) {
            return false;
        }
    }
    return true;
}

// ❌ Sai - đếm cả gold bricks
public boolean isLevelComplete() {
    for (Brick brick : bricks) {
        if (brick.isAlive()) { // Gold sẽ luôn alive → never complete
            return false;
        }
    }
    return true;
}
```

### 2. Score System
```java
// ✅ Đúng - không cộng điểm cho gold
if (brick instanceof GoldBrick) {
    // Just bounce, no score
    ball.reverseVelocity();
} else {
    brick.takeHit();
    if (!brick.isAlive()) {
        scoreManager.addPoints(brick.getScoreValue());
    }
}

// ❌ Sai - cố cộng điểm cho gold (nhưng sẽ không bao giờ xảy ra)
brick.takeHit();
if (!brick.isAlive()) { // Always false for gold
    scoreManager.addPoints(brick.getScoreValue());
}
```

### 3. Sound Effect
```java
// ✅ Đúng - different sound cho gold
if (brick instanceof GoldBrick) {
    audioManager.playMetallicClang(); // "CLANG!"
} else if (brick instanceof SilverBrick) {
    audioManager.playHeavyHit(); // "THUD!"
} else {
    audioManager.playNormalHit(); // "crack"
}
```

### 4. Power-Up Spawning
```java
// ✅ Đúng - gold không spawn power-ups
public void onBrickHit(Brick brick) {
    if (brick instanceof GoldBrick) {
        // Gold không spawn anything
        return;
    }
    
    brick.takeHit();
    if (!brick.isAlive() && random.nextDouble() < 0.2) {
        spawnPowerUp(brick);
    }
}
```

---

## Special Power-Up: Gold Breaker

```java
// Có thể implement power-up đặc biệt để phá gold bricks
public class GoldBreakerPowerUp extends PowerUp {
    @Override
    public void activate(GameManager game) {
        game.setGoldBrickBreakable(true, 5000); // 5 seconds
    }
}

// Trong CollisionManager
if (goldBrickBreakable && brick instanceof GoldBrick) {
    brick.forceDestroy(); // Special method
    scoreManager.addBonusPoints(1000); // Huge bonus
}
```

---

## Kết luận

`GoldBrick` là loại gạch độc đáo trong game:

- **Indestructible**: Không thể phá bằng cách thường
- **Obstacle**: Hoạt động như tường/chướng ngại vật
- **Strategic**: Tạo challenge và yêu cầu suy nghĩ
- **Permanent**: Tồn tại suốt level
- **Simple Code**: Implementation đơn giản nhất (empty methods)
- **Complex Gameplay**: Tạo gameplay phức tạp và thú vị

GoldBrick là ví dụ tốt về việc đôi khi "làm ít hơn" (empty methods) lại tạo ra impact lớn hơn. Chỉ bằng việc TỒN TẠI và không thể phá, GoldBrick buộc người chơi phải thay đổi chiến thuật và tư duy. Đây là lesson quan trọng trong game design: constraints tạo creativity.

**Fun Fact**: HP = 999 là reference đến nhiều RPG games Nhật Bản, nơi 999/9999 là số damage/HP tối đa. Trong Arkanoid, 999 = "basically infinite" trong context của game (ai mà hit 999 lần được đâu!).
