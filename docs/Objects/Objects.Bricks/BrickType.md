# BrickType Enum

## Tổng quan
`BrickType` là một enum định nghĩa tất cả các loại gạch khác nhau trong game Arkanoid. Mỗi loại gạch có các thuộc tính riêng về độ bền (hit points), hình ảnh (sprite), và điểm số (score). Enum này là trung tâm của hệ thống phân loại gạch, cung cấp metadata cho tất cả các lớp Brick sử dụng.

## Vị trí
- **Package**: `Objects.Bricks`
- **File**: `src/Objects/Bricks/BrickType.java`
- **Loại**: Enum

## Mục đích
Enum BrickType:
- Định nghĩa tất cả loại gạch trong game
- Lưu trữ metadata cho mỗi loại (HP, sprite, score)
- Cung cấp thông tin cho BrickFactory, ScoreManager, Renderer
- Phân biệt gạch thường, gạch đặc biệt (Silver, Gold)
- Hỗ trợ việc tính điểm và render

---

## Các Loại Gạch

### 1. Gạch Màu Thường (Normal Bricks)

#### `BLUE`
```java
BLUE(1, "brick_blue", Constants.Scoring.SCORE_BRICK_BASE + 10)
```

**Thuộc tính**:
- **hitPoints**: `1` - Phá sau 1 hit
- **spriteName**: `"brick_blue"` → `brick_blue.png`
- **baseScore**: `SCORE_BRICK_BASE + 10`

**Đặc điểm**: Gạch dễ nhất, điểm thấp nhất

---

#### `RED`
```java
RED(1, "brick_red", Constants.Scoring.SCORE_BRICK_BASE + 20)
```

**Thuộc tính**:
- **hitPoints**: `1`
- **spriteName**: `"brick_red"`
- **baseScore**: `SCORE_BRICK_BASE + 20`

**Đặc điểm**: Điểm cao hơn BLUE

---

#### `GREEN`
```java
GREEN(1, "brick_green", Constants.Scoring.SCORE_BRICK_BASE + 30)
```

**Thuộc tính**:
- **hitPoints**: `1`
- **spriteName**: `"brick_green"`
- **baseScore**: `SCORE_BRICK_BASE + 30`

---

#### `YELLOW`
```java
YELLOW(1, "brick_yellow", Constants.Scoring.SCORE_BRICK_BASE + 40)
```

**Thuộc tính**:
- **hitPoints**: `1`
- **spriteName**: `"brick_yellow"`
- **baseScore**: `SCORE_BRICK_BASE + 40`

---

#### `ORANGE`
```java
ORANGE(1, "brick_orange", Constants.Scoring.SCORE_BRICK_BASE + 50)
```

**Thuộc tính**:
- **hitPoints**: `1`
- **spriteName**: `"brick_orange"`
- **baseScore**: `SCORE_BRICK_BASE + 50`

---

#### `PINK`
```java
PINK(1, "brick_pink", Constants.Scoring.SCORE_BRICK_BASE + 60)
```

**Thuộc tính**:
- **hitPoints**: `1`
- **spriteName**: `"brick_pink"`
- **baseScore**: `SCORE_BRICK_BASE + 60`

---

#### `CYAN`
```java
CYAN(1, "brick_cyan", Constants.Scoring.SCORE_BRICK_BASE + 70)
```

**Thuộc tính**:
- **hitPoints**: `1`
- **spriteName**: `"brick_cyan"`
- **baseScore**: `SCORE_BRICK_BASE + 70`

---

#### `WHITE`
```java
WHITE(1, "brick_white", Constants.Scoring.SCORE_BRICK_BASE + 80)
```

**Thuộc tính**:
- **hitPoints**: `1`
- **spriteName**: `"brick_white"`
- **baseScore**: `SCORE_BRICK_BASE + 80`

**Đặc điểm**: Gạch màu thường với điểm cao nhất

---

### 2. Gạch Đặc Biệt (Special Bricks)

#### `SILVER`
```java
SILVER(2, "brick_silver", Constants.Scoring.SCORE_BRICK_BASE)
```

**Thuộc tính**:
- **hitPoints**: `2` - Cần 2 hits để phá
- **spriteName**: `"brick_silver"`
- **baseScore**: `SCORE_BRICK_BASE` (điểm cơ bản)

**Đặc điểm**:
- Bền hơn gạch thường gấp đôi
- Hit lần 1: Hiển thị hiệu ứng nứt (crack animation)
- Hit lần 2: Bị phá hủy
- Được implement bởi lớp `SilverBrick`

**Gameplay**:
```
Hit 1: HP 2 → 1 (crack animation plays)
Hit 2: HP 1 → 0 (destroyed)
```

---

#### `GOLD`
```java
GOLD(999, "brick_gold", Constants.Scoring.SCORE_BRICK_BASE + 0)
```

**Thuộc tính**:
- **hitPoints**: `999` - Gần như không thể phá
- **spriteName**: `"brick_gold"`
- **baseScore**: `SCORE_BRICK_BASE` (không bonus)

**Đặc điểm**:
- Không thể phá hủy (hoặc cực kỳ khó)
- Bóng và laser nảy lại khi va chạm
- Thường dùng làm tường hoặc chướng ngại vật
- Được implement bởi lớp `GoldBrick`

**Gameplay**:
- `takeHit()` không làm gì
- Tạo thách thức cho người chơi
- Có thể dùng để tạo mê cung hoặc pattern phức tạp

---

## Thuộc tính Enum

### 1. `private final int hitPoints`

**Mô tả**: Số lần gạch có thể chịu đòn trước khi bị phá.

**Giá trị**:
- `1` - Gạch thường (8 màu)
- `2` - Gạch bạc
- `999` - Gạch vàng (không thể phá)

**Getter**: `int getHitPoints()`

**Sử dụng**:
```java
// Tạo gạch với HP phù hợp
Brick brick = new NormalBrick(x, y, w, h, BrickType.RED);
// brick.hitPoints = BrickType.RED.getHitPoints() = 1

// Kiểm tra độ bền
if (brickType.getHitPoints() > 1) {
    System.out.println("This is a durable brick!");
}
```

---

### 2. `private final String spriteName`

**Mô tả**: Tên file hình ảnh (sprite) tương ứng với loại gạch.

**Format**: `"brick_{color}"` → `brick_{color}.png`

**Ví dụ**:
- `"brick_red"` → `brick_red.png`
- `"brick_silver"` → `brick_silver.png`

**Getter**: `String getSpriteName()`

**Sử dụng**:
```java
// Render gạch
String spriteName = brick.getBrickType().getSpriteName();
Sprite sprite = spriteCache.getSprite(spriteName);
graphics.drawImage(sprite.getImage(), brick.getX(), brick.getY());

// Preload sprites
for (BrickType type : BrickType.values()) {
    String filename = type.getSpriteName() + ".png";
    spriteCache.loadSprite(filename);
}
```

---

### 3. `private final int baseScore`

**Mô tả**: Điểm cơ bản người chơi nhận khi phá gạch.

**Công thức**: `SCORE_BRICK_BASE + bonus`

**Bảng điểm**:
| Loại | Bonus | Tổng điểm |
|------|-------|-----------|
| BLUE | +10 | BASE + 10 |
| RED | +20 | BASE + 20 |
| GREEN | +30 | BASE + 30 |
| YELLOW | +40 | BASE + 40 |
| ORANGE | +50 | BASE + 50 |
| PINK | +60 | BASE + 60 |
| CYAN | +70 | BASE + 70 |
| WHITE | +80 | BASE + 80 |
| SILVER | +0 | BASE |
| GOLD | +0 | BASE |

**Getter**: `int getBaseScore()`

**Sử dụng**:
```java
// Cộng điểm khi phá gạch
if (!brick.isAlive()) {
    int points = brick.getBrickType().getBaseScore();
    scoreManager.addPoints(points);
    
    // Hiển thị điểm bay lên
    showFloatingScore(brick.getX(), brick.getY(), points);
}

// Tính tổng điểm có thể đạt được
int maxScore = 0;
for (Brick brick : bricks) {
    maxScore += brick.getBrickType().getBaseScore();
}
```

---

## Phân loại gạch

### Normal Bricks (HP = 1)

**Danh sách**: BLUE, RED, GREEN, YELLOW, ORANGE, PINK, CYAN, WHITE

**Đặc điểm chung**:
- Phá sau 1 hit
- Màu sắc khác nhau
- Điểm số tăng dần theo màu

**Sử dụng**: Chiếm phần lớn gạch trong level

**Hierarchy**:
```
BrickType → NormalBrick
  ↓
  Các màu: BLUE, RED, GREEN, YELLOW, ORANGE, PINK, CYAN, WHITE
```

---

### Durable Bricks (HP = 2)

**Danh sách**: SILVER

**Đặc điểm**:
- Phá sau 2 hits
- Có hiệu ứng nứt sau hit đầu tiên
- Tạo thách thức cho người chơi

**Sử dụng**: Tạo độ khó, strategic positioning

**Hierarchy**:
```
BrickType.SILVER → SilverBrick
  ↓
  crackAnimation plays when HP = 1
```

---

### Indestructible Bricks (HP = 999)

**Danh sách**: GOLD

**Đặc điểm**:
- Không thể phá (hoặc cực khó)
- Hoạt động như tường
- Tạo chướng ngại vật

**Sử dụng**: Maze, obstacles, level design

**Hierarchy**:
```
BrickType.GOLD → GoldBrick
  ↓
  takeHit() does nothing
```

---

## Sử dụng trong code

### 1. Tạo gạch từ BrickType

```java
public class BrickFactory {
    public static Brick createBrick(double x, double y, double w, double h, BrickType type) {
        switch (type) {
            case SILVER:
                return new SilverBrick(x, y, w, h);
            case GOLD:
                return new GoldBrick(x, y, w, h);
            default:
                // Tất cả gạch màu thường
                return new NormalBrick(x, y, w, h, type);
        }
    }
}
```

### 2. Random BrickType

```java
public class LevelGenerator {
    private static final BrickType[] NORMAL_TYPES = {
        BrickType.BLUE, BrickType.RED, BrickType.GREEN, BrickType.YELLOW,
        BrickType.ORANGE, BrickType.PINK, BrickType.CYAN, BrickType.WHITE
    };
    
    public BrickType getRandomNormalBrickType() {
        return NORMAL_TYPES[random.nextInt(NORMAL_TYPES.length)];
    }
    
    public BrickType getBrickTypeForLevel(int level, int row, int col) {
        // Level càng cao, càng nhiều SILVER và GOLD
        if (level > 5 && random.nextDouble() < 0.2) {
            return BrickType.SILVER;
        }
        if (level > 10 && random.nextDouble() < 0.1) {
            return BrickType.GOLD;
        }
        return getRandomNormalBrickType();
    }
}
```

### 3. Scoring System

```java
public class ScoreManager {
    private int score = 0;
    
    public void brickDestroyed(Brick brick) {
        int points = brick.getBrickType().getBaseScore();
        
        // Áp dụng multiplier
        if (comboActive) {
            points *= comboMultiplier;
        }
        
        score += points;
        
        // Hiển thị điểm
        showFloatingScore(brick.getX(), brick.getY(), points);
    }
}
```

### 4. Render System

```java
public class BrickRenderer {
    private Map<String, Sprite> spriteCache = new HashMap<>();
    
    public void preloadSprites() {
        for (BrickType type : BrickType.values()) {
            String spriteName = type.getSpriteName();
            Sprite sprite = AssetLoader.loadSprite(spriteName + ".png");
            spriteCache.put(spriteName, sprite);
        }
    }
    
    public void renderBrick(Brick brick, Graphics g) {
        String spriteName = brick.getBrickType().getSpriteName();
        Sprite sprite = spriteCache.get(spriteName);
        
        g.drawImage(sprite.getImage(), brick.getX(), brick.getY());
    }
}
```

### 5. Level Design Pattern

```java
public class PatternDesigner {
    // Tạo pattern rainbow
    public List<Brick> createRainbowPattern(double startX, double startY) {
        List<Brick> bricks = new ArrayList<>();
        BrickType[] colors = {
            BrickType.RED, BrickType.ORANGE, BrickType.YELLOW,
            BrickType.GREEN, BrickType.CYAN, BrickType.BLUE,
            BrickType.PINK, BrickType.WHITE
        };
        
        for (int row = 0; row < colors.length; row++) {
            for (int col = 0; col < 12; col++) {
                double x = startX + col * (brickWidth + spacing);
                double y = startY + row * (brickHeight + spacing);
                
                Brick brick = new NormalBrick(x, y, brickWidth, brickHeight, colors[row]);
                bricks.add(brick);
            }
        }
        
        return bricks;
    }
    
    // Tạo tường vàng
    public List<Brick> createGoldWall(double startX, double startY, int length) {
        List<Brick> wall = new ArrayList<>();
        
        for (int i = 0; i < length; i++) {
            double x = startX + i * brickWidth;
            GoldBrick gold = new GoldBrick(x, startY, brickWidth, brickHeight);
            wall.add(gold);
        }
        
        return wall;
    }
}
```

---

## Quan hệ với các lớp khác

```
BrickType (Enum - Metadata)
    ↓ provides data for
    │
    ├─→ Brick (Abstract)
    │   ├─→ NormalBrick (uses any normal BrickType)
    │   ├─→ SilverBrick (uses BrickType.SILVER)
    │   └─→ GoldBrick (uses BrickType.GOLD)
    │
    ├─→ BrickFactory (creates bricks based on type)
    ├─→ ScoreManager (gets baseScore)
    ├─→ Renderer (gets spriteName)
    └─→ LevelGenerator (selects types for patterns)
```

---

## Best Practices

### 1. Dùng enum thay vì string/int
```java
// ✅ Đúng - type-safe
BrickType type = BrickType.RED;
int score = type.getBaseScore();

// ❌ Sai - error-prone
String type = "red"; // Typo, không compile error
int score = getScore(type); // Phải check null/invalid
```

### 2. Iterate qua tất cả types
```java
// ✅ Đúng
for (BrickType type : BrickType.values()) {
    System.out.println(type + ": " + type.getBaseScore());
}

// Hoặc với Stream
Arrays.stream(BrickType.values())
    .filter(t -> t.getHitPoints() == 1)
    .forEach(System.out::println);
```

### 3. Switch statements
```java
// ✅ Đúng - xử lý tất cả cases
switch (brickType) {
    case SILVER:
        return new SilverBrick(x, y, w, h);
    case GOLD:
        return new GoldBrick(x, y, w, h);
    default:
        return new NormalBrick(x, y, w, h, brickType);
}

// ❌ Sai - thiếu default
switch (brickType) {
    case SILVER:
        return new SilverBrick(x, y, w, h);
    case GOLD:
        return new GoldBrick(x, y, w, h);
    // Thiếu default → NullPointerException!
}
```

---

## Mở rộng trong tương lai

### Thêm loại gạch mới

```java
// Thêm vào enum
EXPLOSIVE(1, "brick_explosive", Constants.Scoring.SCORE_BRICK_BASE + 100),
MOVING(1, "brick_moving", Constants.Scoring.SCORE_BRICK_BASE + 90),
REGENERATING(3, "brick_regen", Constants.Scoring.SCORE_BRICK_BASE + 150);

// Tạo lớp tương ứng
public class ExplosiveBrick extends Brick {
    @Override
    public void destroy() {
        super.destroy();
        explodeNearbyBricks(); // Phá các gạch xung quanh
    }
}

public class MovingBrick extends Brick {
    @Override
    public void update() {
        move(); // Di chuyển theo pattern
    }
}
```

---

## Kết luận

`BrickType` enum là nền tảng của hệ thống phân loại gạch:

- **Metadata-driven**: Chứa tất cả thông tin về loại gạch
- **Type-safe**: Enum đảm bảo không có giá trị invalid
- **Extensible**: Dễ thêm loại gạch mới
- **Centralized**: Tất cả logic liên quan đến loại gạch ở một chỗ
- **Clean Code**: Tránh magic numbers và hardcoded strings

Enum này là ví dụ điển hình về cách sử dụng enum không chỉ như constant, mà như một data structure phong phú với behavior và metadata, giúp code sạch hơn, an toàn hơn và dễ maintain hơn.
