# Class SpriteRenderer - Hệ Thống Render Sprites Thực Thể Game

## 📋 Mục Lục

1. [Tổng Quan](#tổng-quan)
2. [Kiến Trúc](#kiến-trúc)
3. [Thuộc Tính (Fields)](#thuộc-tính-fields)
4. [Constructor](#constructor)
5. [Phương Thức Render](#phương-thức-render)
6. [Logic Phức Tạp](#logic-phức-tạp)
7. [Ví Dụ Sử Dụng](#ví-dụ-sử-dụng)
8. [Design Pattern](#design-pattern)
9. [Best Practices](#best-practices)
10. [Tích Hợp Với Game](#tích-hợp-với-game)

---

## Tổng Quan

### Khái Niệm

**SpriteRenderer** là lớp chịu trách nhiệm **vẽ các sprite (hình ảnh)** của tất cả các thực thể trong game (Ball, Paddle, Brick, PowerUp, Laser) lên Canvas. Nó quản lý **logic vẽ phức tạp** như hoạt ảnh (animations) và trạng thái (states).

### Mục Đích

```
┌─────────────────────────────────────────────────────┐
│           SpriteRenderer Purpose                     │
├─────────────────────────────────────────────────────┤
│  🎨 Entity Rendering    → Vẽ tất cả thực thể game   │
│  🎬 Animation Handling  → Xử lý animation frames     │
│  🔄 State Management    → Render theo state          │
│  🧱 Brick Logic         → Logic đặc biệt cho gạch   │
│  ⚡ Performance         → Tối ưu rendering           │
└─────────────────────────────────────────────────────┘
```

### Vai Trò Trong Game

| Thực Thể | Phương Thức | Độ Phức Tạp | Đặc Điểm |
|----------|-------------|-------------|----------|
| **Ball** | `drawBall()` | ⭐ Simple | Sprite tĩnh |
| **Laser** | `drawLaser()` | ⭐ Simple | Sprite tĩnh |
| **Paddle** | `drawPaddle()` | ⭐⭐⭐ Complex | Animation + State-based |
| **Brick** | `drawBrick()` | ⭐⭐⭐⭐ Very Complex | Polymorphic + Animation |
| **PowerUp** | `drawPowerUp()` | ⭐⭐ Medium | Animation fallback |

---

## Kiến Trúc

### Sơ Đồ UML

```
┌──────────────────────────────────────────────────────┐
│                  SpriteRenderer                       │
├──────────────────────────────────────────────────────┤
│ - gc: GraphicsContext                                │
│ - sprites: SpriteProvider                            │
│ - ball: Ball (unused)                                │
│ - paddle: Paddle (unused)                            │
│ - brick: Brick (unused)                              │
│ - normalBrick: NormalBrick (unused)                  │
│ - silverBrick: SilverBrick (unused)                  │
│ - goldBrick: GoldBrick (unused)                      │
│ - powerUp: PowerUp (unused)                          │
├──────────────────────────────────────────────────────┤
│ + SpriteRenderer(gc, sprites)                        │
│ + drawBall(ball): void                               │
│ + drawLaser(laser): void                             │
│ + drawPaddle(paddle): void                           │
│ + drawBrick(brick): void                             │
│ + drawPowerUp(powerUp): void                         │
└──────────────────────────────────────────────────────┘
              │                           │
              │ uses                      │ uses
              ▼                           ▼
┌────────────────────────┐    ┌──────────────────────┐
│    SpriteProvider      │    │  GraphicsContext     │
├────────────────────────┤    ├──────────────────────┤
│ + get(name): Image     │    │ + drawImage(...)     │
└────────────────────────┘    │ + setFill(...)       │
                              │ + fillRect(...)      │
                              │ + fillOval(...)      │
                              └──────────────────────┘
```

### Dependency Graph

```
     CanvasRenderer
            │
            │ contains
            ▼
     SpriteRenderer
       │         │
       │         └────────────► SpriteProvider
       │                              │
       │                              │ provides
       │                              ▼
       │                          Image assets
       │
       └──────────────────────► GraphicsContext
                                     │
                                     │ draws to
                                     ▼
                                  Canvas
```

### Luồng Render Paddle (Ví Dụ)

```
┌─────────────────────┐
│  CanvasRenderer     │
│  drawPaddle(paddle) │
└─────────┬───────────┘
          │ delegates
          ▼
┌─────────────────────┐
│  SpriteRenderer     │
│  drawPaddle(paddle) │
└─────────┬───────────┘
          │
          ▼
┌─────────────────────────┐
│ Kiểm tra: Animation?    │
└────┬────────────────┬───┘
     │ YES            │ NO
     ▼                ▼
┌──────────────┐  ┌────────────────┐
│ Lấy frame    │  │ Kiểm tra state │
│ hiện tại     │  │ (NORMAL/WIDE/  │
│ từ animation │  │  LASER)        │
└──────┬───────┘  └────────┬───────┘
       │                   │
       │                   ▼
       │          ┌─────────────────┐
       │          │ Chọn sprite     │
       │          │ tương ứng       │
       │          └────────┬────────┘
       │                   │
       └───────────────────┘
                │
                ▼
       ┌─────────────────┐
       │ Tính vị trí X   │
       │ (center align)  │
       └────────┬────────┘
                │
                ▼
       ┌─────────────────┐
       │ gc.drawImage()  │
       └─────────────────┘
```

---

## Thuộc Tính (Fields)

### 1. gc: GraphicsContext

```java
private final GraphicsContext gc;
```

- **Mô Tả**: Context đồ họa để thực hiện các thao tác vẽ
- **Tính Chất**: `final` - không thể thay đổi sau khi khởi tạo
- **Nhiệm Vụ**: Vẽ sprites, hình dạng, text lên Canvas

### 2. sprites: SpriteProvider

```java
private final SpriteProvider sprites;
```

- **Mô Tả**: Đối tượng cung cấp các hình ảnh sprite
- **Tính Chất**: `final`
- **Nhiệm Vụ**: Truy xuất sprites theo tên file

#### Danh Sách Sprites Sử Dụng

```java
// Ball
sprites.get("ball.png")

// Laser
sprites.get("laser_bullet.png")

// Paddle
sprites.get("paddle.png")         // NORMAL state
sprites.get("paddle_wide.png")    // WIDE/WIDE_PULSATE state
sprites.get("paddle_laser.png")   // LASER/LASER_PULSATE state

// Bricks
sprites.get("brick_red.png")      // NormalBrick RED
sprites.get("brick_blue.png")     // NormalBrick BLUE
sprites.get("brick_green.png")    // NormalBrick GREEN
sprites.get("brick_yellow.png")   // NormalBrick YELLOW
sprites.get("brick_silver.png")   // SilverBrick
sprites.get("brick_gold.png")     // GoldBrick
```

### 3. Các Biến Thực Thể (Không Sử Dụng)

```java
private Ball ball;
private Paddle paddle;
private Brick brick;
private NormalBrick normalBrick;
private SilverBrick silverBrick;
private GoldBrick goldBrick;
private PowerUp powerUp;
```

- **Mô Tả**: Các biến này được khai báo nhưng **không dùng để giữ trạng thái**
- **Lý Do**: Có thể là placeholder hoặc legacy code
- **Cải Tiến**: Nên xóa để code gọn hơn

---

## Constructor

### Chữ Ký

```java
public SpriteRenderer(GraphicsContext gc, SpriteProvider sprites)
```

### Tham Số

| Tham Số | Kiểu | Mô Tả |
|---------|------|-------|
| `gc` | `GraphicsContext` | Context đồ họa |
| `sprites` | `SpriteProvider` | Đối tượng cung cấp sprites |

### Implementation

```java
public SpriteRenderer(GraphicsContext gc, SpriteProvider sprites) {
    this.gc = gc;
    this.sprites = sprites;
}
```

### Ví Dụ Khởi Tạo

```java
// Trong CanvasRenderer
public class CanvasRenderer {
    private SpriteRenderer spriteRenderer;
    
    public CanvasRenderer(Canvas canvas, SpriteProvider sprites) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Khởi tạo SpriteRenderer
        this.spriteRenderer = new SpriteRenderer(gc, sprites);
    }
}
```

---

## Phương Thức Render

### 1. drawBall()

#### Mục Đích
Vẽ quả bóng (Ball) - **sprite tĩnh đơn giản**.

#### Signature

```java
public void drawBall(Ball ball)
```

#### Implementation

```java
public void drawBall(Ball ball) {
    gc.drawImage(sprites.get("ball.png"), ball.getX(), ball.getY());
}
```

#### Đặc Điểm

- ✅ **Đơn giản**: Chỉ 1 dòng code
- ✅ **Sprite tĩnh**: Không có animation
- ✅ **Position-based**: Vẽ tại `(ball.getX(), ball.getY())`

#### Kích Thước

```java
// Ball sprite dimensions (ví dụ)
Width:  16 pixels
Height: 16 pixels
```

#### Ví Dụ

```java
// Render ball trong game loop
Ball ball = new Ball(300, 400);
spriteRenderer.drawBall(ball);

// Output: ball.png được vẽ tại (300, 400)
```

---

### 2. drawLaser()

#### Mục Đích
Vẽ tia laser (Laser) - **sprite tĩnh đơn giản**.

#### Signature

```java
public void drawLaser(Laser laser)
```

#### Implementation

```java
public void drawLaser(Laser laser) {
    gc.drawImage(sprites.get("laser_bullet.png"), laser.getX(), laser.getY());
}
```

#### Đặc Điểm

- ✅ **Đơn giản**: Tương tự `drawBall()`
- ✅ **Sprite tĩnh**: Không có animation
- ⚡ **Fast**: Không cần kiểm tra state

#### Kích Thước

```java
// Laser sprite dimensions (ví dụ)
Width:  8 pixels
Height: 24 pixels
```

#### Ví Dụ

```java
// Khi paddle bắn laser
if (paddle.getState() == PaddleState.LASER && Input.isSpacePressed()) {
    Laser laser1 = new Laser(paddle.getX() + 10, paddle.getY());
    Laser laser2 = new Laser(paddle.getX() + paddle.getWidth() - 18, paddle.getY());
    lasers.add(laser1);
    lasers.add(laser2);
}

// Render
for (Laser laser : lasers) {
    spriteRenderer.drawLaser(laser);
}
```

---

### 3. drawPaddle()

#### Mục Đích
Vẽ thanh trượt (Paddle) - **logic phức tạp với animation và state**.

#### Signature

```java
public void drawPaddle(Paddle paddle)
```

#### Implementation

```java
public void drawPaddle(Paddle paddle) {
    // Ưu tiên 1: Vẽ khung hình animation nếu đang chạy (dùng cho hiệu ứng chuyển trạng thái)
    if (paddle.isAnimationPlaying()) {
        Animation animation = paddle.getAnimation();
        if (animation != null) {
            Image frame = animation.getCurrentFrame();
            if (frame != null) {
                // Tính toán vị trí X để căn giữa frame theo chiều ngang của paddle
                // Điều này đảm bảo animation mở rộng/thu nhỏ đều từ tâm
                double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
                double frameWidth = frame.getWidth();
                double drawX = paddleCenterX - frameWidth / 2.0;
                
                gc.drawImage(frame, drawX, paddle.getY());
                return;
            }
        }
    }

    // Ưu tiên 2: Vẽ sprite tĩnh dựa trên trạng thái
    PaddleState state = paddle.getState();

    if (state == PaddleState.NORMAL) {
        gc.drawImage(sprites.get("paddle.png"), paddle.getX(), paddle.getY());
    } else if (state == PaddleState.WIDE || state == PaddleState.WIDE_PULSATE) {
        // Vẽ thanh trượt rộng
        gc.drawImage(sprites.get("paddle_wide.png"), paddle.getX(), paddle.getY());
    } else if (state == PaddleState.LASER || state == PaddleState.LASER_PULSATE) {
        // Vẽ thanh trượt laser
        gc.drawImage(sprites.get("paddle_laser.png"), paddle.getX(), paddle.getY());
    } else {
        // Fallback: Mặc định vẽ paddle thường
        gc.drawImage(sprites.get("paddle.png"), paddle.getX(), paddle.getY());
    }
}
```

#### Logic Tree

```
drawPaddle()
    │
    ▼
┌─────────────────────────┐
│ Kiểm tra:               │
│ isAnimationPlaying()?   │
└────┬────────────────┬───┘
     │ TRUE           │ FALSE
     ▼                ▼
┌──────────────┐  ┌────────────────┐
│ Priority 1:  │  │ Priority 2:    │
│ Animation    │  │ State-based    │
└──────┬───────┘  └────────┬───────┘
       │                   │
       ▼                   ▼
┌───────────────┐   ┌──────────────────┐
│ Get animation │   │ switch(state) {  │
│ getCurrentFrame()  │   NORMAL →       │
└──────┬────────┘   │     paddle.png   │
       │            │   WIDE →         │
       ▼            │     paddle_wide. │
┌───────────────┐   │   LASER →        │
│ Tính drawX:   │   │     paddle_laser.│
│ centerX -     │   └──────┬───────────┘
│ frameWidth/2  │          │
└──────┬────────┘          │
       │                   │
       └───────────────────┘
                │
                ▼
       ┌─────────────────┐
       │ gc.drawImage()  │
       └─────────────────┘
```

#### Centering Algorithm

```java
// Tại sao cần center animation frame?
// - Paddle expand: Width tăng từ 80px → 120px
// - Nếu vẽ từ paddle.getX(), paddle sẽ "mở rộng sang phải"
// - Với centering, paddle "mở rộng đều 2 bên"

// Công thức:
double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;  // Tâm paddle
double frameWidth = frame.getWidth();                             // Rộng của frame
double drawX = paddleCenterX - frameWidth / 2.0;                  // X để căn giữa
```

#### Ví Dụ Center Calculation

```
NORMAL Paddle:
- Position: (260, 700)
- Width: 80
- Center: 260 + 80/2 = 300

Frame 0 (expanding):
- Frame Width: 90
- Draw X: 300 - 90/2 = 255  ← Dịch trái 5px

Frame 1:
- Frame Width: 100
- Draw X: 300 - 100/2 = 250 ← Dịch trái 10px

Frame 2 (fully expanded):
- Frame Width: 120
- Draw X: 300 - 120/2 = 240 ← Dịch trái 20px

→ Paddle mở rộng đều từ tâm!
```

#### State Mapping

| PaddleState | Sprite | Animation |
|-------------|--------|-----------|
| `NORMAL` | `paddle.png` | No |
| `EXPANDING` | Animation frames | Yes |
| `WIDE` | `paddle_wide.png` | No |
| `WIDE_PULSATE` | `paddle_wide.png` | No (hoặc pulsate animation) |
| `SHRINKING` | Animation frames (reversed) | Yes |
| `LASER` | `paddle_laser.png` | No |
| `LASER_PULSATE` | `paddle_laser.png` | No (hoặc pulsate animation) |

---

### 4. drawBrick()

#### Mục Đích
Vẽ viên gạch (Brick) - **logic phức tạp nhất với polymorphism và animation**.

#### Signature

```java
public void drawBrick(Brick brick)
```

#### Implementation

```java
public void drawBrick(Brick brick) {
    double x = brick.getX();
    double y = brick.getY();
    double w = Constants.Bricks.BRICK_WIDTH;
    double h = Constants.Bricks.BRICK_HEIGHT;

    if (brick instanceof NormalBrick) {
        // Gạch thường: Vẽ sprite tương ứng với màu gạch
        NormalBrick normalBrick = (NormalBrick) brick;
        String spriteName = "brick_" + normalBrick.getBrickType().name().toLowerCase() + ".png";
        gc.drawImage(sprites.get(spriteName), x, y, w, h);
    } else if (brick instanceof SilverBrick) {
        // Gạch Bạc: Xử lý animation vết nứt
        SilverBrick silverBrick = (SilverBrick) brick;
        Animation crackAnimation = silverBrick.getCrackAnimation();

        if (silverBrick.isCrackAnimationPlaying() && crackAnimation != null) {
            // Vẽ sprite gạch bạc làm nền
            gc.drawImage(sprites.get("brick_silver.png"), x, y, w, h);

            // Vẽ frame nứt đè lên trên
            Image crackFrame = crackAnimation.getCurrentFrame();
            if (crackFrame != null) {
                gc.drawImage(crackFrame, x, y, w, h);
            }
        } else {
            // Chỉ vẽ gạch bạc (chưa nứt)
            gc.drawImage(sprites.get("brick_silver.png"), x, y, w, h);
        }
    } else if (brick instanceof GoldBrick) {
        // Gạch Vàng: Chỉ vẽ sprite gạch vàng
        GoldBrick goldBrick = (GoldBrick) brick;
        gc.drawImage(sprites.get("brick_gold.png"), x, y, w, h);
    } else {
        // Fallback: Vẽ hình chữ nhật màu xám
        gc.setFill(Color.GRAY);
        gc.fillRect(x, y, brick.getWidth(), brick.getHeight());
    }
}
```

#### Logic Tree

```
drawBrick(brick)
    │
    ▼
┌────────────────────────┐
│ Kiểm tra instance type │
└───┬────────┬───────┬───┘
    │        │       │
    ▼        ▼       ▼
NormalBrick SilverBrick GoldBrick
    │           │           │
    ▼           ▼           ▼
┌──────────┐ ┌─────────┐ ┌──────────┐
│ Get type │ │ Check   │ │ Draw     │
│ (RED/    │ │ crack   │ │ gold.png │
│  BLUE/   │ │ anim?   │ └──────────┘
│  GREEN/  │ └────┬────┘
│  YELLOW) │      │
└────┬─────┘      ▼
     │      ┌──────────────┐
     │      │ TRUE: Overlay│
     │      │ crack frame  │
     │      │ FALSE: Plain │
     │      │ silver brick │
     │      └──────────────┘
     ▼
┌──────────────────┐
│ Construct sprite │
│ name: "brick_"   │
│ + type.lower()   │
│ + ".png"         │
└────┬─────────────┘
     │
     ▼
┌──────────────────┐
│ Draw sprite      │
└──────────────────┘
```

#### NormalBrick Rendering

```java
// NormalBrick có nhiều màu
if (brick instanceof NormalBrick) {
    NormalBrick normalBrick = (NormalBrick) brick;
    
    // Lấy BrickType (RED, BLUE, GREEN, YELLOW)
    BrickType type = normalBrick.getBrickType();
    
    // Tạo tên sprite: "brick_red.png", "brick_blue.png", etc.
    String spriteName = "brick_" + type.name().toLowerCase() + ".png";
    
    // Vẽ với kích thước cố định
    gc.drawImage(sprites.get(spriteName), x, y, 
        Constants.Bricks.BRICK_WIDTH, 
        Constants.Bricks.BRICK_HEIGHT);
}
```

#### SilverBrick Crack Overlay

```java
// SilverBrick có animation vết nứt (crack)
if (brick instanceof SilverBrick) {
    SilverBrick silverBrick = (SilverBrick) brick;
    
    // Lấy crack animation (5 frames: crack_0 → crack_4)
    Animation crackAnimation = silverBrick.getCrackAnimation();
    
    if (silverBrick.isCrackAnimationPlaying() && crackAnimation != null) {
        // Layer 1: Vẽ base brick (silver)
        gc.drawImage(sprites.get("brick_silver.png"), x, y, w, h);
        
        // Layer 2: Overlay crack frame
        Image crackFrame = crackAnimation.getCurrentFrame();
        if (crackFrame != null) {
            gc.drawImage(crackFrame, x, y, w, h); // Đè lên trên
        }
    } else {
        // Chưa bị đánh, vẽ gạch bạc nguyên vẹn
        gc.drawImage(sprites.get("brick_silver.png"), x, y, w, h);
    }
}
```

**Minh Họa Overlay**:
```
┌─────────────────────┐
│                     │ ← brick_silver.png (base layer)
│   SilverBrick       │
│                     │
└─────────────────────┘
         +
┌─────────────────────┐
│   ╱╱╱  ╲╲╲         │ ← crack_2.png (overlay layer)
│  ╱ Crack ╲         │
│ ╱  Pattern ╲       │
└─────────────────────┘
         =
┌─────────────────────┐
│   ╱╱╱  ╲╲╲         │ ← Kết quả: Gạch bạc có vết nứt
│  ╱ Silver ╲        │
│ ╱  + Crack ╲       │
└─────────────────────┘
```

#### GoldBrick Rendering

```java
// GoldBrick đơn giản, không có animation
if (brick instanceof GoldBrick) {
    gc.drawImage(sprites.get("brick_gold.png"), x, y, w, h);
}
```

#### Fallback Rendering

```java
// Nếu không phải NormalBrick, SilverBrick, GoldBrick
// → Vẽ hình chữ nhật màu xám
else {
    gc.setFill(Color.GRAY);
    gc.fillRect(x, y, brick.getWidth(), brick.getHeight());
}
```

---

### 5. drawPowerUp()

#### Mục Đích
Vẽ PowerUp đang rơi - **ưu tiên animation, fallback là hình tròn**.

#### Signature

```java
public void drawPowerUp(PowerUp powerUp)
```

#### Implementation

```java
public void drawPowerUp(PowerUp powerUp) {
    // Chỉ vẽ nếu PowerUp đang hoạt động
    if (powerUp == null || !powerUp.isActive()) {
        return;
    }

    double x = powerUp.getX();
    double y = powerUp.getY();

    // Vẽ animation của PowerUp
    Animation animation = powerUp.getAnimation();
    if (animation != null && animation.isPlaying()) {
        Image frame = animation.getCurrentFrame();
        if (frame != null) {
            gc.drawImage(frame, x, y);
            return;
        }
    }

    // Fallback: Vẽ hình tròn màu vàng nếu không có sprite
    gc.setFill(Color.YELLOW);
    gc.fillOval(x, y, powerUp.getWidth(), powerUp.getHeight());
}
```

#### Logic Tree

```
drawPowerUp(powerUp)
    │
    ▼
┌────────────────────────┐
│ Kiểm tra: active?      │
└───┬────────────────┬───┘
    │ FALSE          │ TRUE
    ▼                ▼
Return       ┌────────────────┐
             │ Kiểm tra:      │
             │ animation?     │
             └───┬────────┬───┘
                 │ TRUE   │ FALSE
                 ▼        ▼
         ┌──────────┐ ┌──────────┐
         │ Draw     │ │ Fallback:│
         │ frame    │ │ Yellow   │
         │          │ │ oval     │
         └──────────┘ └──────────┘
```

#### Active Check

```java
// Chỉ vẽ PowerUp đang active
if (powerUp == null || !powerUp.isActive()) {
    return; // Không vẽ nếu null hoặc đã bị thu thập
}
```

#### Animation Rendering

```java
// Ưu tiên: Animation frames (quay vòng/phát sáng)
Animation animation = powerUp.getAnimation();
if (animation != null && animation.isPlaying()) {
    Image frame = animation.getCurrentFrame();
    if (frame != null) {
        gc.drawImage(frame, x, y);
        return; // Early return, không chạy fallback
    }
}
```

#### Fallback Rendering

```java
// Fallback: Hình tròn màu vàng (nếu không có animation)
gc.setFill(Color.YELLOW);
gc.fillOval(x, y, powerUp.getWidth(), powerUp.getHeight());
```

**Khi Nào Sử Dụng Fallback**:
- Animation chưa được khởi tạo (`null`)
- Animation không đang chạy (`!isPlaying()`)
- Frame hiện tại là `null`
- Sprite không tải được

---

## Logic Phức Tạp

### 1. Paddle Animation Centering

#### Vấn Đề

```
Nếu vẽ animation từ paddle.getX():
┌────────┐
│ Frame 0│ Width: 80 (paddle.getX() = 260)
└────────┘

┌──────────┐
│ Frame 1  │ Width: 90 (paddle.getX() = 260)
└──────────┘ → Mở rộng sang PHẢI

┌────────────┐
│  Frame 2   │ Width: 100 (paddle.getX() = 260)
└────────────┘ → Không đều!
```

#### Giải Pháp

```java
// Tính center của paddle
double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;

// Tính vị trí X để frame căn giữa
double drawX = paddleCenterX - frameWidth / 2.0;
```

```
Với centering:
    ┌────────┐
    │ Frame 0│ Width: 80, drawX = 260
    └────────┘

  ┌──────────┐
  │ Frame 1  │ Width: 90, drawX = 255
  └──────────┘ → Mở rộng ĐỀU 2 BÊN

 ┌────────────┐
 │  Frame 2   │ Width: 100, drawX = 250
 └────────────┘ → Smooth!
```

---

### 2. SilverBrick Crack Overlay

#### Vấn Đề
Làm sao vẽ vết nứt mà không thay thế toàn bộ sprite?

#### Giải Pháp: Layered Rendering

```java
// Layer 1: Base brick
gc.drawImage(sprites.get("brick_silver.png"), x, y, w, h);

// Layer 2: Crack overlay (transparent background)
gc.drawImage(crackFrame, x, y, w, h);
```

#### Yêu Cầu Sprite
- Crack sprite phải có **transparent background** (PNG with alpha)
- Chỉ vẽ vết nứt, phần còn lại trong suốt

```
crack_0.png:       crack_2.png:       crack_4.png:
┌─────────┐       ┌─────────┐       ┌─────────┐
│         │       │  ╱  ╲   │       │ ╱╱╱ ╲╲╲ │
│    ╱    │  →    │ ╱    ╲  │  →    │╱  X  ╲│
│         │       │╱      ╲ │       │╲     ╱│
└─────────┘       └─────────┘       └─────────┘
(Nhẹ)             (Trung bình)       (Nặng)
```

---

### 3. PowerUp Active Check

#### Tại Sao Cần Kiểm Tra active?

```java
// PowerUp lifecycle:
1. Spawn:     active = true, isCollected = false
2. Falling:   active = true (đang rơi)
3. Collected: active = false, isCollected = true
4. Removed:   Xóa khỏi list
```

#### Nếu Không Kiểm Tra

```java
// ❌ BAD: Vẽ PowerUp đã bị thu thập
if (powerUp != null) { // Chỉ check null
    gc.drawImage(animation.getCurrentFrame(), x, y); // Vẫn vẽ!
}
```

#### Với Active Check

```java
// ✅ GOOD: Chỉ vẽ khi đang active
if (powerUp == null || !powerUp.isActive()) {
    return; // Không vẽ nếu không active
}
```

---

## Ví Dụ Sử Dụng

### 1. Render Tất Cả Thực Thể

```java
public class GameRenderer {
    private SpriteRenderer spriteRenderer;
    
    public void renderGame(GameState gameState) {
        // 1. Render ball
        spriteRenderer.drawBall(gameState.getBall());
        
        // 2. Render paddle
        spriteRenderer.drawPaddle(gameState.getPaddle());
        
        // 3. Render bricks
        for (Brick brick : gameState.getBricks()) {
            if (!brick.isDestroyed()) {
                spriteRenderer.drawBrick(brick);
            }
        }
        
        // 4. Render lasers
        for (Laser laser : gameState.getLasers()) {
            spriteRenderer.drawLaser(laser);
        }
        
        // 5. Render power-ups
        for (PowerUp powerUp : gameState.getPowerUps()) {
            spriteRenderer.drawPowerUp(powerUp);
        }
    }
}
```

---

### 2. Custom SpriteRenderer (Debug Mode)

```java
public class DebugSpriteRenderer extends SpriteRenderer {
    private boolean debugMode = false;
    
    public DebugSpriteRenderer(GraphicsContext gc, SpriteProvider sprites) {
        super(gc, sprites);
    }
    
    @Override
    public void drawBrick(Brick brick) {
        super.drawBrick(brick);
        
        if (debugMode) {
            // Vẽ bounding box
            gc.setStroke(Color.RED);
            gc.setLineWidth(1);
            gc.strokeRect(brick.getX(), brick.getY(), 
                brick.getWidth(), brick.getHeight());
            
            // Vẽ thông tin
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Monospaced", 8));
            gc.fillText(String.format("HP:%d", brick.getHitsRemaining()), 
                brick.getX() + 2, brick.getY() + 10);
        }
    }
    
    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
    }
}
```

---

### 3. Batch Rendering (Performance Optimization)

```java
public class BatchSpriteRenderer extends SpriteRenderer {
    private Map<String, List<Point2D>> batchMap = new HashMap<>();
    
    public BatchSpriteRenderer(GraphicsContext gc, SpriteProvider sprites) {
        super(gc, sprites);
    }
    
    // Thêm vào batch thay vì vẽ ngay
    public void batchBrick(Brick brick) {
        String spriteName = getSpriteName(brick);
        Point2D position = new Point2D(brick.getX(), brick.getY());
        
        batchMap.computeIfAbsent(spriteName, k -> new ArrayList<>())
               .add(position);
    }
    
    // Vẽ tất cả cùng lúc (giảm state changes)
    public void flush() {
        for (Map.Entry<String, List<Point2D>> entry : batchMap.entrySet()) {
            Image sprite = sprites.get(entry.getKey());
            for (Point2D pos : entry.getValue()) {
                gc.drawImage(sprite, pos.getX(), pos.getY());
            }
        }
        batchMap.clear();
    }
}
```

---

## Design Pattern

### 1. Delegation Pattern

```java
// CanvasRenderer delegates rendering to SpriteRenderer
public class CanvasRenderer {
    private SpriteRenderer spriteRenderer;
    
    public void drawBall(Ball ball) {
        spriteRenderer.drawBall(ball); // 👈 Delegate
    }
    
    public void drawPaddle(Paddle paddle) {
        spriteRenderer.drawPaddle(paddle); // 👈 Delegate
    }
}
```

### 2. Strategy Pattern

```java
// Mỗi loại brick có strategy render khác nhau
if (brick instanceof NormalBrick) {
    renderNormalBrick((NormalBrick) brick);
} else if (brick instanceof SilverBrick) {
    renderSilverBrick((SilverBrick) brick);
} else if (brick instanceof GoldBrick) {
    renderGoldBrick((GoldBrick) brick);
}
```

### 3. Null Object Pattern

```java
// Fallback rendering thay vì crash
if (animation == null || !animation.isPlaying()) {
    // Vẽ hình thay thế (oval, rect) thay vì crash
    gc.fillOval(x, y, width, height);
}
```

---

## Best Practices

### ✅ DO

#### 1. Kiểm Tra Null/Active Trước Khi Vẽ

```java
// ✅ GOOD
if (powerUp != null && powerUp.isActive()) {
    spriteRenderer.drawPowerUp(powerUp);
}

// ❌ BAD
spriteRenderer.drawPowerUp(powerUp); // NullPointerException!
```

#### 2. Sử Dụng Constants Cho Kích Thước

```java
// ✅ GOOD
double w = Constants.Bricks.BRICK_WIDTH;
double h = Constants.Bricks.BRICK_HEIGHT;
gc.drawImage(sprite, x, y, w, h);

// ❌ BAD
gc.drawImage(sprite, x, y, 40, 20); // Magic numbers!
```

#### 3. Early Return Trong Fallback

```java
// ✅ GOOD
if (animation != null && animation.isPlaying()) {
    gc.drawImage(frame, x, y);
    return; // 👈 Early return
}
gc.fillOval(x, y, w, h); // Fallback

// ❌ BAD: Nested if-else
if (animation != null && animation.isPlaying()) {
    gc.drawImage(frame, x, y);
} else {
    gc.fillOval(x, y, w, h);
}
```

#### 4. Render Destroyed Check

```java
// ✅ GOOD: Không vẽ brick đã phá hủy
for (Brick brick : bricks) {
    if (!brick.isDestroyed()) {
        spriteRenderer.drawBrick(brick);
    }
}
```

### ❌ DON'T

#### 1. Không Hardcode Sprite Names

```java
// ❌ BAD
gc.drawImage(sprites.get("paddle_wide.png"), x, y);

// ✅ GOOD: Dựa vào state
String spriteName = getSpriteName(paddle.getState());
gc.drawImage(sprites.get(spriteName), x, y);
```

#### 2. Không Vẽ Nhiều Lần Không Cần Thiết

```java
// ❌ BAD
spriteRenderer.drawPaddle(paddle);
spriteRenderer.drawPaddle(paddle); // Lãng phí!

// ✅ GOOD: Vẽ một lần mỗi frame
spriteRenderer.drawPaddle(paddle);
```

#### 3. Không Quên Xử Lý Polymorphism

```java
// ❌ BAD: Không kiểm tra type
gc.drawImage(sprites.get("brick.png"), x, y); // Gạch nào?

// ✅ GOOD: Kiểm tra instanceof
if (brick instanceof NormalBrick) {
    // Render normal brick
} else if (brick instanceof SilverBrick) {
    // Render silver brick with crack
}
```

---

## Tích Hợp Với Game

### Game Loop Integration

```java
public class GameLoop {
    private CanvasRenderer canvasRenderer;
    private SpriteRenderer spriteRenderer;
    
    public void render() {
        // 1. Clear canvas
        canvasRenderer.clear();
        
        // 2. Render UI (score, lives, borders)
        canvasRenderer.drawUI(score, highScore, lives);
        
        // 3. Render game entities (delegated to SpriteRenderer)
        renderGameEntities();
        
        // 4. Present
        canvasRenderer.present();
    }
    
    private void renderGameEntities() {
        // Ball
        canvasRenderer.drawBall(ball);
        
        // Paddle
        canvasRenderer.drawPaddle(paddle);
        
        // Bricks
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                canvasRenderer.drawBrick(brick);
            }
        }
        
        // Lasers
        for (Laser laser : lasers) {
            canvasRenderer.drawLaser(laser);
        }
        
        // Power-ups
        for (PowerUp powerUp : powerUps) {
            canvasRenderer.drawPowerUp(powerUp);
        }
    }
}
```

---

## Tổng Kết

### Điểm Mạnh

| Điểm Mạnh | Mô Tả |
|-----------|-------|
| ✅ **Separation of Concerns** | Tách logic render ra khỏi game logic |
| ✅ **Animation Support** | Xử lý animation cho paddle, brick, powerup |
| ✅ **State-based Rendering** | Render dựa trên state (paddle) |
| ✅ **Polymorphic Rendering** | Xử lý nhiều loại brick khác nhau |
| ✅ **Fallback Handling** | Vẽ hình thay thế nếu sprite không có |
| ✅ **Centering Algorithm** | Paddle animation mở rộng đều từ tâm |

### Hạn Chế & Cải Tiến

| Hạn Chế | Cải Tiến Đề Xuất |
|---------|-----------------|
| ⚠️ Unused fields (ball, paddle, etc.) | Xóa các biến không sử dụng |
| ⚠️ Không có batch rendering | Thêm batch mode cho performance |
| ⚠️ Hardcoded instanceof checks | Refactor thành Visitor Pattern |
| ⚠️ Không có sprite caching | Cache sprites để tránh get() nhiều lần |
| ⚠️ Fallback rendering inconsistent | Standardize fallback shapes |

---

**Tác Giả**: Sprite Rendering System Documentation  
**Phiên Bản**: 1.0  
**Ngày Cập Nhật**: 2024
