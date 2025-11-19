# SilverBrick Class

## Tổng quan
`SilverBrick` là gạch bạc - loại gạch đặc biệt trong game Arkanoid có độ bền cao hơn gạch thường. Cần 2 hits mới phá được, và khi còn 1 HP sẽ hiển thị vết nứt để người chơi biết gần phá được rồi. SilverBrick là gạch trung gian về độ khó, khó hơn NormalBrick nhưng không "impossible" như GoldBrick.

## Vị trí
- **Package**: `Objects.Bricks`
- **File**: `src/Objects/Bricks/SilverBrick.java`
- **Kế thừa**: `Brick` (abstract)
- **Implements**: `GameObject` (gián tiếp qua Brick)

## Mục đích
SilverBrick:
- Tạo độ khó cao hơn trong level (2 hits thay vì 1)
- Cung cấp visual feedback qua crack animation
- Buộc người chơi phải lên chiến thuật (tập trung attack)
- Tăng độ dài của level (không thể clear nhanh)
- Thường được đặt ở vị trí chiến lược

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
SilverBrick (Concrete Class)
    │
    ├── BrickType.SILVER (2 HP)
    └── crackAnimation (hiệu ứng vết nứt)
```

---

## Thuộc tính (Fields)

### 1. `private final BrickType brickType = BrickType.SILVER`

**Mô tả**: Loại gạch (luôn là SILVER).

**Kiểu**: `BrickType` enum (final constant)

**Giá trị**: `BrickType.SILVER`
- hitPoints = 2
- spriteName = "brick_silver"
- baseScore = BASE

**Getter**: `getBrickType()` (override từ Brick)

---

### 2. `private int currentHP`

**Mô tả**: HP hiện tại của gạch (0 đến 2).

**Kiểu**: `int`

**Giá trị**:
- 2 - Gạch nguyên (chưa bị hit)
- 1 - Gạch nứt (đã bị hit 1 lần, crack animation active)
- 0 - Gạch bị phá (destroyed)

**Khởi tạo**: `currentHP = 2` (trong constructor)

**Thay đổi**: Giảm trong `takeHit()` method

**Sử dụng**:
```java
// Kiểm tra HP để render đúng sprite
if (brick.currentHP == 2) {
    // Render brick_silver.png (nguyên)
} else if (brick.currentHP == 1) {
    // Render crackAnimation (nứt)
}

// Logic damage
brick.takeHit(); // currentHP: 2 → 1
brick.takeHit(); // currentHP: 1 → 0 (destroyed)
```

---

### 3. `private Animation crackAnimation`

**Mô tả**: Animation hiển thị vết nứt khi HP = 1.

**Kiểu**: `Animation` object

**Khởi tạo**: 
```java
crackAnimation = AnimationFactory.createSilverBrickCrackAnimation();
```

**Frame rate**: Thường ~5-10 frames, 60ms per frame

**Sprite sheets**: 
- Frame 1: Vết nứt nhỏ
- Frame 2: Vết nứt lớn hơn
- Frame 3: Sắp vỡ

**Vòng lặp**: `true` (loop animation)

**Active khi**: `currentHP == 1`

**Sử dụng**:
```java
// Update animation
if (currentHP == 1 && crackAnimation != null) {
    crackAnimation.update(); // Next frame
}

// Render animation
if (currentHP == 1) {
    crackAnimation.render(graphics, x, y);
} else {
    // Render normal silver sprite
}
```

---

## Constructor

### `SilverBrick(double x, double y, double width, double height)`

**Mô tả**: Khởi tạo gạch bạc với vị trí và kích thước xác định.

**Tham số**:
- `x` - tọa độ X (góc trên trái)
- `y` - tọa độ Y (góc trên trái)
- `width` - chiều rộng gạch
- `height` - chiều cao gạch

**Hành vi**:
1. Gọi `super(x, y, width, height, BrickType.SILVER.getHitPoints())`
   - HP = 2 (từ BrickType.SILVER)
2. Khởi tạo `currentHP = 2`
3. Tạo `crackAnimation` từ AnimationFactory

**Ví dụ**:
```java
// Tạo gạch bạc tại (100, 200)
SilverBrick silverBrick = new SilverBrick(100, 200, 64, 32);

// Trong level generator
for (int col = 3; col < 9; col++) {
    double x = startX + col * (brickWidth + spacing);
    double y = startY + 2 * (brickHeight + spacing); // Row 2
    
    SilverBrick brick = new SilverBrick(x, y, brickWidth, brickHeight);
    bricks.add(brick);
}
```

---

## Phương thức

### 1. `void update()` (Override)

**Mô tả**: Cập nhật animation mỗi frame nếu gạch bị nứt.

**Hành vi**:
```java
if (currentHP == 1 && crackAnimation != null) {
    crackAnimation.update();
}
```

**Trigger**: Mỗi frame (60 FPS)

**Logic**:
- HP = 2: Không làm gì
- HP = 1: Update crack animation (chuyển frame)
- HP = 0: Gạch đã destroyed, không gọi update nữa

**Ví dụ**:
```java
// Game loop
for (Brick brick : bricks) {
    if (brick.isAlive()) {
        brick.update();
        // SilverBrick với HP=1 → crackAnimation.update()
    }
}
```

---

### 2. `void takeHit()` (Override)

**Mô tả**: Xử lý khi gạch bị đánh (override để track currentHP).

**Hành vi**:
```java
@Override
public void takeHit() {
    super.takeHit(); // Giảm hitPoints (2→1 hoặc 1→0)
    currentHP--;     // Giảm currentHP để trigger animation
    
    // Logic tự động trong super:
    // if (hitPoints <= 0) {
    //     destroy(); → alive = false
    // }
}
```

**Flow**:
1. Ball collision detected
2. `takeHit()` called
3. `currentHP--` (2→1 hoặc 1→0)
4. Nếu `currentHP == 1`: Crack animation starts
5. Nếu `currentHP == 0`: `destroy()` called, gạch mất

**Ví dụ**:
```java
// Collision với ball
if (ball.checkCollisionWithRect(silverBrick.getBounds())) {
    silverBrick.takeHit();
    
    if (silverBrick.currentHP == 1) {
        // Still alive, show crack
        audioManager.playSilverBrickHitSound();
    } else if (!silverBrick.isAlive()) {
        // Destroyed
        scoreManager.addPoints(silverBrick.getScoreValue());
        audioManager.playBrickBreakSound();
    }
}
```

---

### 3. `BrickType getBrickType()` (Override)

**Mô tả**: Trả về loại gạch (luôn là SILVER).

**Kiểu trả về**: `BrickType.SILVER`

**Hành vi**: `return brickType;`

**Sử dụng**:
```java
// Render logic
if (brick.getBrickType() == BrickType.SILVER) {
    SilverBrick silver = (SilverBrick) brick;
    
    if (silver.getCurrentHP() == 2) {
        // Render full silver sprite
        graphics.drawImage(silverSprite, brick.getX(), brick.getY());
    } else if (silver.getCurrentHP() == 1) {
        // Render crack animation
        silver.getCrackAnimation().render(graphics, brick.getX(), brick.getY());
    }
}
```

---

### 4. `int getScoreValue()`

**Mô tả**: Lấy điểm số khi phá gạch.

**Kiểu trả về**: `int`

**Hành vi**: `return brickType.getBaseScore();`

**Giá trị**: `BrickType.SILVER.getBaseScore()` (thường = BASE score)

**Lưu ý**: Dù cần 2 hits nhưng chỉ được điểm 1 lần (khi phá)

**Ví dụ**:
```java
// Không được điểm khi hit lần 1
silver.takeHit(); // HP: 2 → 1
// scoreManager.addPoints(...) // Không gọi

// Chỉ được điểm khi phá
silver.takeHit(); // HP: 1 → 0
if (!silver.isAlive()) {
    scoreManager.addPoints(silver.getScoreValue()); // Cộng điểm
}
```

---

### 5. `int getCurrentHP()` (Getter)

**Mô tả**: Lấy HP hiện tại của gạch (0-2).

**Kiểu trả về**: `int`

**Hành vi**: `return currentHP;`

**Sử dụng**:
```java
// Render với HP-based sprite
if (silver.getCurrentHP() == 2) {
    renderFullBrick(silver);
} else if (silver.getCurrentHP() == 1) {
    renderCrackedBrick(silver);
}

// UI HP indicator
int hp = silver.getCurrentHP();
String hpBar = "HP: " + "█".repeat(hp) + "░".repeat(2 - hp);
graphics.drawString(hpBar, silver.getX(), silver.getY() - 10);
```

---

### 6. `Animation getCrackAnimation()` (Getter)

**Mô tả**: Lấy crack animation object.

**Kiểu trả về**: `Animation`

**Hành vi**: `return crackAnimation;`

**Sử dụng**:
```java
// Custom rendering
if (silver.getCurrentHP() == 1) {
    Animation anim = silver.getCrackAnimation();
    anim.render(graphics, silver.getX(), silver.getY());
}
```

---

## Luồng hoạt động

### Lifecycle của SilverBrick

```
1. CREATION (HP = 2)
   ↓
   new SilverBrick(x, y, width, height)
   → super(..., 2) // hitPoints = 2
   → currentHP = 2
   → crackAnimation = AnimationFactory.create(...)
   → alive = true
   
   Render: brick_silver.png (nguyên)

2. FIRST HIT (HP: 2 → 1)
   ↓
   Ball collision
   → takeHit() called
   → hitPoints-- (2 → 1)
   → currentHP-- (2 → 1)
   → hitPoints > 0 → Still alive
   
   update() → crackAnimation.update() // Animation starts
   Render: crackAnimation frames (vết nứt)
   Sound: "silver_hit.wav"

3. DAMAGED STATE (HP = 1)
   ↓
   Mỗi frame:
     update() → crackAnimation.update() // Loop animation
     Render → Draw crack animation frames
     
   Visual: Gạch có vết nứt rõ ràng
   Player feedback: "Hit thêm 1 lần nữa là phá được!"

4. SECOND HIT (HP: 1 → 0)
   ↓
   Ball collision again
   → takeHit() called
   → hitPoints-- (1 → 0)
   → currentHP-- (1 → 0)
   → hitPoints <= 0 → destroy()
   → alive = false
   
   Sound: "brick_break.wav"
   Score: +BrickType.SILVER.getBaseScore()
   Effect: Particle explosion

5. DESTROYED
   ↓
   !isAlive() = true
   
   May spawn power-up
   bricks.removeIf(b -> !b.isAlive())
   → SilverBrick removed from game
```

---

## So sánh với các loại gạch khác

| Đặc điểm | NormalBrick | SilverBrick | GoldBrick |
|----------|-------------|-------------|-----------|
| **HP** | 1 | 2 | 999 |
| **Hits để phá** | 1 | 2 | Không thể |
| **Animation** | Không | Crack (HP=1) | Không |
| **Visual feedback** | Không | Vết nứt | Không |
| **Màu sắc** | 8 màu | 1 màu (bạc) | 1 màu (vàng) |
| **Điểm** | BASE+10~80 | BASE | BASE |
| **Difficulty** | Easy | Medium | Hard (obstacle) |
| **Strategy** | Hit bất kì | Tập trung 2 hits | Tránh/dùng ball bounce |

---

## Sử dụng trong Level Design

### 1. Shield Pattern (Bảo vệ gạch yếu)

```java
public List<Brick> createShieldPattern() {
    List<Brick> bricks = new ArrayList<>();
    
    // Row 1: Silver shield (top)
    for (int col = 2; col < 10; col++) {
        double x = startX + col * (brickWidth + spacing);
        double y = startY;
        bricks.add(new SilverBrick(x, y, brickWidth, brickHeight));
    }
    
    // Row 2-5: Normal bricks (protected)
    for (int row = 1; row < 5; row++) {
        for (int col = 2; col < 10; col++) {
            double x = startX + col * (brickWidth + spacing);
            double y = startY + row * (brickHeight + spacing);
            
            BrickType type = BrickType.values()[row]; // Different colors
            bricks.add(new NormalBrick(x, y, brickWidth, brickHeight, type));
        }
    }
    
    return bricks;
    // Strategy: Phá silver trước mới vào được bên trong
}
```

### 2. Silver Fortress

```java
public List<Brick> createSilverFortress() {
    List<Brick> bricks = new ArrayList<>();
    
    // Walls (silver)
    for (int row = 0; row < 8; row++) {
        // Left wall
        bricks.add(new SilverBrick(
            startX, 
            startY + row * (brickHeight + spacing), 
            brickWidth, brickHeight
        ));
        
        // Right wall
        bricks.add(new SilverBrick(
            startX + 11 * (brickWidth + spacing), 
            startY + row * (brickHeight + spacing), 
            brickWidth, brickHeight
        ));
    }
    
    // Inner area (normal bricks)
    for (int row = 0; row < 8; row++) {
        for (int col = 1; col < 11; col++) {
            double x = startX + col * (brickWidth + spacing);
            double y = startY + row * (brickHeight + spacing);
            
            bricks.add(new NormalBrick(x, y, brickWidth, brickHeight, 
                BrickType.WHITE)); // High score
        }
    }
    
    return bricks;
    // Strategy: Cần phá vào từ top/bottom
}
```

### 3. Mixed Difficulty

```java
public List<Brick> createMixedPattern() {
    List<Brick> bricks = new ArrayList<>();
    
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 12; col++) {
            double x = startX + col * (brickWidth + spacing);
            double y = startY + row * (brickHeight + spacing);
            
            // 20% silver, 80% normal
            if (random.nextDouble() < 0.2) {
                bricks.add(new SilverBrick(x, y, brickWidth, brickHeight));
            } else {
                BrickType type = randomNormalType();
                bricks.add(new NormalBrick(x, y, brickWidth, brickHeight, type));
            }
        }
    }
    
    return bricks;
    // Strategy: Unpredictable, requires adaptability
}
```

---

## Rendering SilverBrick

### Basic Rendering

```java
public void renderSilverBrick(SilverBrick brick, Graphics g) {
    if (!brick.isAlive()) return;
    
    if (brick.getCurrentHP() == 2) {
        // Full health - render solid silver sprite
        Sprite silverSprite = spriteCache.getSprite("brick_silver");
        g.drawImage(silverSprite.getImage(), 
                   (int) brick.getX(), 
                   (int) brick.getY());
    } else if (brick.getCurrentHP() == 1) {
        // Damaged - render crack animation
        brick.getCrackAnimation().render(g, 
                                         (int) brick.getX(), 
                                         (int) brick.getY());
    }
}
```

### Advanced Rendering với Effects

```java
public void renderSilverBrickAdvanced(SilverBrick brick, Graphics2D g) {
    if (!brick.isAlive()) return;
    
    double x = brick.getX();
    double y = brick.getY();
    
    if (brick.getCurrentHP() == 2) {
        // Full health - shine effect
        Sprite sprite = spriteCache.getSprite("brick_silver");
        g.drawImage(sprite.getImage(), (int) x, (int) y);
        
        // Add metallic shine
        if (shineEffect) {
            g.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.3f));
            g.setColor(Color.WHITE);
            g.fillRect((int) x, (int) y, (int) brick.getWidth() / 3, 
                      (int) brick.getHeight());
            g.setComposite(AlphaComposite.SrcOver);
        }
        
    } else if (brick.getCurrentHP() == 1) {
        // Damaged - render crack animation + red tint
        Animation anim = brick.getCrackAnimation();
        anim.render(g, (int) x, (int) y);
        
        // Red tint overlay
        g.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.2f));
        g.setColor(new Color(255, 0, 0));
        g.fillRect((int) x, (int) y, 
                  (int) brick.getWidth(), (int) brick.getHeight());
        g.setComposite(AlphaComposite.SrcOver);
    }
}
```

---

## Best Practices

### 1. HP Tracking
```java
// ✅ Đúng - dùng currentHP cho logic
if (silver.getCurrentHP() == 1) {
    // Render crack animation
    // Play "almost broken" sound
}

// ❌ Sai - dùng hitPoints trực tiếp (protected in Brick)
// if (silver.hitPoints == 1) // Compile error
```

### 2. Animation Null Check
```java
// ✅ Đúng - check null
@Override
public void update() {
    if (currentHP == 1 && crackAnimation != null) {
        crackAnimation.update();
    }
}

// ❌ Sai - không check null
@Override
public void update() {
    if (currentHP == 1) {
        crackAnimation.update(); // NullPointerException nếu animation fail load
    }
}
```

### 3. Score Timing
```java
// ✅ Đúng - chỉ cộng điểm khi phá
silver.takeHit();
if (!silver.isAlive()) { // Chỉ khi HP = 0
    scoreManager.addPoints(silver.getScoreValue());
}

// ❌ Sai - cộng điểm mỗi hit
silver.takeHit();
scoreManager.addPoints(silver.getScoreValue()); // Sai! 2x points
```

### 4. Animation Reset
```java
// ✅ Đúng - reset animation khi HP thay đổi
@Override
public void takeHit() {
    super.takeHit();
    currentHP--;
    
    if (currentHP == 1 && crackAnimation != null) {
        crackAnimation.reset(); // Start from frame 0
    }
}
```

---

## Tích hợp với game systems

### Power-Up System
```java
// Silver brick có tỉ lệ spawn power-up cao hơn
public void onBrickDestroyed(Brick brick) {
    if (brick instanceof SilverBrick) {
        // 40% chance (vs 20% for normal)
        if (random.nextDouble() < 0.4) {
            spawnPowerUp(brick.getX(), brick.getY());
        }
    }
}
```

### Combo System
```java
// Silver brick break trong combo → bonus
if (comboActive && brick instanceof SilverBrick) {
    int basePoints = brick.getScoreValue();
    int bonusPoints = basePoints * comboMultiplier * 1.5; // Extra bonus
    scoreManager.addPoints(bonusPoints);
}
```

---

## Kết luận

`SilverBrick` là gạch trung gian quan trọng trong game:

- **Durability**: 2 HP tạo challenge vừa phải
- **Feedback**: Crack animation cho người chơi biết tiến độ
- **Strategy**: Buộc người chơi tập trung attack
- **Balance**: Không quá khó như Gold, không quá dễ như Normal
- **Visual Polish**: Animation làm game professional hơn

SilverBrick chứng minh rằng việc thêm visual feedback (crack animation) và tracking state (currentHP) có thể biến một lớp đơn giản thành một game element thú vị và engaging. Đây là ví dụ tốt về việc balance giữa game difficulty và player satisfaction.
