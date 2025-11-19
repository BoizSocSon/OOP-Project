# Class CanvasRenderer - Hệ Thống Render Tổng Thể Canvas

## 📋 Mục Lục

1. [Tổng Quan](#tổng-quan)
2. [Kiến Trúc](#kiến-trúc)
3. [Thuộc Tính (Fields)](#thuộc-tính-fields)
4. [Constructor](#constructor)
5. [Phương Thức UI](#phương-thức-ui)
6. [Phương Thức Overlay](#phương-thức-overlay)
7. [Phương Thức Entity](#phương-thức-entity)
8. [Font System](#font-system)
9. [Layout System](#layout-system)
10. [Ví Dụ Sử Dụng](#ví-dụ-sử-dụng)
11. [Design Pattern](#design-pattern)
12. [Best Practices](#best-practices)

---

## Tổng Quan

### Khái Niệm

**CanvasRenderer** là lớp **tổng quản** (orchestrator) chịu trách nhiệm render (vẽ) **tất cả các thành phần** lên Canvas của game, bao gồm:
- **UI Elements**: Logo, Score, High Score, Lives
- **Game Entities**: Ball, Paddle, Bricks, PowerUps, Lasers
- **Borders**: Viền game area (top, left, right)
- **Overlays**: Pause, Game Over, Win, Level Complete

### Mục Đích

```
┌─────────────────────────────────────────────────────┐
│           CanvasRenderer Purpose                     │
├─────────────────────────────────────────────────────┤
│  🎨 Orchestration      → Điều phối tất cả rendering │
│  🖼️  UI Rendering      → Vẽ logo, score, lives      │
│  🎮 Entity Rendering   → Delegate to SpriteRenderer │
│  🔲 Border Rendering   → Delegate to BorderRenderer │
│  📜 Overlay Rendering  → Pause/GameOver/Win screens │
│  🔤 Font Management    → Load custom fonts          │
└─────────────────────────────────────────────────────┘
```

### Vai Trò Trong Game

| Thành Phần | Render Logic | Vị Trí |
|-----------|-------------|---------|
| **Logo** | Direct draw | Top-left (0, 0) |
| **Score (1UP)** | Text rendering | Top-right |
| **High Score** | Text rendering | Top-right, dưới Score |
| **Borders** | Delegate → BorderRenderer | Top/Left/Right edges |
| **Lives** | Sprite array | Bottom-left |
| **Game Entities** | Delegate → SpriteRenderer | Play area |
| **Overlays** | Text + shapes | Center screen |

---

## Kiến Trúc

### Sơ Đồ UML

```
┌──────────────────────────────────────────────────────┐
│                  CanvasRenderer                       │
├──────────────────────────────────────────────────────┤
│ - canvas: Canvas                                     │
│ - gc: GraphicsContext                                │
│ - spriteRenderer: SpriteRenderer                     │
│ - borderRenderer: BorderRenderer                     │
│ - sprites: SpriteProvider                            │
│ - scoreFont: Font                                    │
│ - uiFont: Font                                       │
├──────────────────────────────────────────────────────┤
│ + CanvasRenderer(canvas, sprites)                    │
│ + clear(): void                                      │
│ - loadUIAssets(): void                               │
│ + drawUI(score, highScore, lives): void             │
│ + drawPauseOverlay(): void                           │
│ + drawGameOverOverlay(score): void                   │
│ + drawWinOverlay(score): void                        │
│ + drawLevelCompleteOverlay(): void                   │
│ + drawBall(ball): void                               │
│ + drawLaser(laser): void                             │
│ + drawPaddle(paddle): void                           │
│ + drawBrick(brick): void                             │
│ + drawPowerUp(powerUp): void                         │
│ + present(): void                                    │
└──────────────────────────────────────────────────────┘
              │                    │
              │ contains           │ contains
              ▼                    ▼
    ┌──────────────────┐  ┌──────────────────┐
    │ SpriteRenderer   │  │ BorderRenderer   │
    └──────────────────┘  └──────────────────┘
```

### Dependency Graph

```
        CanvasRenderer (Orchestrator)
               │
       ┌───────┼───────┐
       │       │       │
       ▼       ▼       ▼
   Sprite  Border   Assets
  Renderer Renderer (Sprites/Fonts)
     │       │
     │       └────────► SpriteProvider
     │                       │
     └───────────────────────┘
                 │
                 ▼
          GraphicsContext
                 │
                 ▼
              Canvas
```

### Render Pipeline

```
┌─────────────────┐
│  Game Loop      │
│  (60 FPS)       │
└────────┬────────┘
         │
         ▼
┌─────────────────────────┐
│  CanvasRenderer.clear() │ ← Xóa canvas (màu đen)
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│  CanvasRenderer.drawUI()│ ← Vẽ UI + Borders + Lives
└────────┬────────────────┘
         │
    ┌────┼────────────────────────────────┐
    │    │                                │
    ▼    ▼                                ▼
 Logo  Score/HighScore              Lives (icons)
    │    │                                │
    │    └───────────┐                    │
    │                ▼                    │
    │        BorderRenderer.render()     │
    │           │                         │
    └───────────┴─────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────┐
│  Render Game Entities:                  │
│  - drawBall()    ─────► SpriteRenderer  │
│  - drawPaddle()  ─────► SpriteRenderer  │
│  - drawBrick()   ─────► SpriteRenderer  │
│  - drawLaser()   ─────► SpriteRenderer  │
│  - drawPowerUp() ─────► SpriteRenderer  │
└────────┬────────────────────────────────┘
         │
         ▼
┌─────────────────────────┐
│  Overlay (if needed):   │
│  - Pause?               │
│  - Game Over?           │
│  - Win?                 │
│  - Level Complete?      │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│  CanvasRenderer.present()│ ← Finalize (optional)
└─────────────────────────┘
```

---

## Thuộc Tính (Fields)

### 1. canvas: Canvas

```java
private final Canvas canvas;
```

- **Mô Tả**: Canvas chính của game
- **Tính Chất**: `final` - không thể thay đổi sau khi khởi tạo
- **Nhiệm Vụ**: Cung cấp `GraphicsContext` và kích thước

#### Sử Dụng

```java
// Lấy GraphicsContext
GraphicsContext gc = canvas.getGraphicsContext2D();

// Lấy kích thước
double width = canvas.getWidth();   // 600
double height = canvas.getHeight(); // 800
```

---

### 2. gc: GraphicsContext

```java
private final GraphicsContext gc;
```

- **Mô Tả**: Context đồ họa, dùng để vẽ
- **Khởi Tạo**: `canvas.getGraphicsContext2D()`
- **Nhiệm Vụ**: Vẽ hình ảnh, text, shapes lên canvas

---

### 3. spriteRenderer: SpriteRenderer

```java
private final SpriteRenderer spriteRenderer;
```

- **Mô Tả**: Renderer chuyên dụng để vẽ các sprite của thực thể game
- **Nhiệm Vụ**: Render Ball, Paddle, Brick, PowerUp, Laser

#### Delegation

```java
// CanvasRenderer delegates entity rendering
public void drawBall(Ball ball) {
    spriteRenderer.drawBall(ball);
}

public void drawPaddle(Paddle paddle) {
    spriteRenderer.drawPaddle(paddle);
}
```

---

### 4. borderRenderer: BorderRenderer

```java
private final BorderRenderer borderRenderer;
```

- **Mô Tả**: Renderer chuyên dụng để vẽ viền (border)
- **Nhiệm Vụ**: Vẽ cạnh trên, trái, phải của game area

---

### 5. sprites: SpriteProvider

```java
private final SpriteProvider sprites;
```

- **Mô Tả**: Đối tượng cung cấp các sprite (hình ảnh)
- **Nhiệm Vụ**: Truy xuất sprites theo tên file

#### Sprites Sử Dụng

```java
sprites.get("logo.png")           // Logo game
sprites.get("paddle_life.png")    // Icon mạng sống
sprites.get("ball.png")           // Ball sprite (delegated)
sprites.get("paddle.png")         // Paddle sprite (delegated)
// ... và tất cả sprites khác
```

---

### 6. scoreFont: Font

```java
private Font scoreFont;
```

- **Mô Tả**: Font cho điểm số (Score)
- **Khởi Tạo**: `AssetLoader.loadFont("generation.ttf", 24)`
- **Fallback**: `Font.font("Monospaced", 24)` nếu load thất bại

---

### 7. uiFont: Font

```java
private Font uiFont;
```

- **Mô Tả**: Font cho các phần tử UI khác (labels, overlays)
- **Khởi Tạo**: `AssetLoader.loadFont("emulogic.ttf", 18)`
- **Fallback**: `Font.font("Monospaced", 18)` nếu load thất bại

---

## Constructor

### Chữ Ký

```java
public CanvasRenderer(Canvas canvas, SpriteProvider sprites)
```

### Tham Số

| Tham Số | Kiểu | Mô Tả |
|---------|------|-------|
| `canvas` | `Canvas` | Canvas của game |
| `sprites` | `SpriteProvider` | Đối tượng cung cấp sprite |

### Implementation

```java
public CanvasRenderer(Canvas canvas, SpriteProvider sprites) {
    this.canvas = canvas;
    this.gc = canvas.getGraphicsContext2D();
    // Khởi tạo các Renderer phụ
    this.spriteRenderer = new SpriteRenderer(gc, sprites);
    this.borderRenderer = new BorderRenderer(gc, sprites);
    this.sprites = sprites;
    // Tải font UI khi khởi tạo
    this.loadUIAssets();
}
```

### Initialization Flow

```
Constructor
    │
    ├──► Assign canvas
    ├──► Get GraphicsContext
    ├──► Create SpriteRenderer(gc, sprites)
    ├──► Create BorderRenderer(gc, sprites)
    ├──► Store sprites reference
    └──► loadUIAssets()
              │
              ├──► Load "generation.ttf" (size 24) → scoreFont
              ├──► Load "emulogic.ttf" (size 18) → uiFont
              └──► Fallback to "Monospaced" if fail
```

---

## Phương Thức UI

### 1. clear()

#### Mục Đích
Xóa toàn bộ Canvas, tô màu nền đen.

#### Signature

```java
public void clear()
```

#### Implementation

```java
public void clear() {
    gc.setFill(Color.BLACK);
    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
}
```

#### Đặc Điểm

- ✅ **Gọi đầu tiên**: Luôn gọi trước khi render frame mới
- ✅ **Full screen**: Xóa toàn bộ canvas (0, 0) → (width, height)
- ✅ **Màu đen**: Tạo nền tối cho game

#### Ví Dụ

```java
// Game loop
public void render() {
    canvasRenderer.clear(); // 👈 Xóa frame cũ
    canvasRenderer.drawUI(score, highScore, lives);
    // ... render entities ...
}
```

---

### 2. loadUIAssets()

#### Mục Đích
Tải các font chữ tùy chỉnh cho UI. Nếu lỗi, sử dụng font mặc định.

#### Signature

```java
private void loadUIAssets()
```

#### Implementation

```java
private void loadUIAssets() {
    try {
        scoreFont = AssetLoader.loadFont("generation.ttf", 24);
        uiFont = AssetLoader.loadFont("emulogic.ttf", 18);
    } catch (Exception e) {
        // Sử dụng font mặc định nếu không tải được
        scoreFont = Font.font("Monospaced", 24);
        uiFont = Font.font("Monospaced", 18);
        System.out.println("CanvasRenderer: Failed to load custom fonts, using default.");
    }
}
```

#### Error Handling

```
┌─────────────────────┐
│  loadUIAssets()     │
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│  Try:               │
│  - Load generation. │
│    ttf → scoreFont  │
│  - Load emulogic.   │
│    ttf → uiFont     │
└────┬────────────┬───┘
     │ SUCCESS    │ ERROR
     ▼            ▼
  Use custom   Use fallback
    fonts      (Monospaced)
```

#### Font Mapping

| Font Variable | Custom Font | Size | Fallback | Sử Dụng |
|--------------|-------------|------|----------|---------|
| `scoreFont` | `generation.ttf` | 24 | `Monospaced 24` | Score numbers |
| `uiFont` | `emulogic.ttf` | 18 | `Monospaced 18` | Labels (1UP, HIGH SCORE) |

---

### 3. drawUI()

#### Mục Đích
Vẽ giao diện người dùng (UI), bao gồm logo, điểm số, điểm cao nhất và mạng sống.

#### Signature

```java
public void drawUI(int score, int highScore, int lives)
```

#### Tham Số

| Tham Số | Kiểu | Mô Tả |
|---------|------|-------|
| `score` | `int` | Điểm số hiện tại |
| `highScore` | `int` | Điểm cao nhất |
| `lives` | `int` | Số mạng sống còn lại |

#### Implementation

```java
public void drawUI(int score, int highScore, int lives) {
    // Vẽ Logo
    gc.drawImage(sprites.get("logo.png"),0,0);

    // Vẽ Score (1UP)
    gc.setFont(uiFont);
    gc.setFill(Color.RED);
    gc.setTextAlign(TextAlignment.CENTER);
    gc.fillText("1UP", canvas.getWidth() * 0.82, 30); // Vị trí góc phải
    gc.setFont(scoreFont);
    gc.setFill(Color.GOLD);
    gc.fillText(String.valueOf(score), canvas.getWidth() * 0.82, 60);

    // Vẽ High Score
    gc.setFont(uiFont);
    gc.setFill(Color.RED);
    gc.fillText("HIGH SCORE", canvas.getWidth() * 0.82, 100);
    gc.setFont(scoreFont);
    gc.setFill(Color.GOLD);
    gc.fillText(String.valueOf(highScore), canvas.getWidth() * 0.82, 130);

    // Vẽ các cạnh viền
    borderRenderer.render();

    // Vẽ biểu tượng mạng sống (lives)
    for (int i = 0; i < lives; i++) {
        // Tính toán vị trí X cho mỗi biểu tượng
        double lifeX = Constants.Window.WINDOW_SIDE_OFFSET + i * (Constants.Paddle.PADDLE_LIFE_WIDTH + 10);
        // Vị trí Y cố định ở dưới cùng
        double lifeY = Constants.Window.WINDOW_HEIGHT - Constants.Paddle.PADDLE_LIFE_HEIGHT - 10;
        gc.drawImage(sprites.get("paddle_life.png"), lifeX, lifeY);
    }
}
```

#### Layout Breakdown

```
┌────────────────────────────────────────────────┐
│  Logo (0, 0)                     1UP           │ ← Y=30
│                                12345           │ ← Y=60 (scoreFont)
│                                                │
│                             HIGH SCORE         │ ← Y=100
│                                67890           │ ← Y=130 (scoreFont)
├────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────┐ │
│  │   ← edge_top.png                        │ │
│  │─────────────────────────────────────────│ │
│ edge_left │                       │ edge_right│
│     .png  │     PLAY AREA         │    .png   │
│           │                       │           │
│           │                       │           │
│           │                       │           │
│  │─────────────────────────────────────────│  │
│  └─────────────────────────────────────────┘  │
│                                                │
│  🎮 🎮 🎮  ← Lives (paddle_life.png)          │ ← Y=HEIGHT-40
└────────────────────────────────────────────────┘
```

#### Component Details

**1. Logo**
```java
gc.drawImage(sprites.get("logo.png"), 0, 0);
```
- Position: Top-left corner (0, 0)
- Size: Depends on logo.png dimensions

**2. Score (1UP)**
```java
double x = canvas.getWidth() * 0.82; // 600 * 0.82 = 492

// Label
gc.setFont(uiFont);          // emulogic.ttf 18pt
gc.setFill(Color.RED);       // Màu đỏ
gc.fillText("1UP", x, 30);   // Y=30

// Score value
gc.setFont(scoreFont);       // generation.ttf 24pt
gc.setFill(Color.GOLD);      // Màu vàng
gc.fillText(String.valueOf(score), x, 60); // Y=60
```

**3. High Score**
```java
double x = canvas.getWidth() * 0.82;

// Label
gc.setFont(uiFont);
gc.setFill(Color.RED);
gc.fillText("HIGH SCORE", x, 100); // Y=100

// Value
gc.setFont(scoreFont);
gc.setFill(Color.GOLD);
gc.fillText(String.valueOf(highScore), x, 130); // Y=130
```

**4. Borders**
```java
borderRenderer.render(); // Vẽ top, left, right edges
```

**5. Lives Icons**
```java
for (int i = 0; i < lives; i++) {
    double lifeX = Constants.Window.WINDOW_SIDE_OFFSET 
                 + i * (Constants.Paddle.PADDLE_LIFE_WIDTH + 10);
    double lifeY = Constants.Window.WINDOW_HEIGHT 
                 - Constants.Paddle.PADDLE_LIFE_HEIGHT - 10;
    gc.drawImage(sprites.get("paddle_life.png"), lifeX, lifeY);
}
```

**Lives Positioning**:
```
Lives = 3:
Icon 0: X = 40 + 0*(32+10) = 40
Icon 1: X = 40 + 1*(32+10) = 82
Icon 2: X = 40 + 2*(32+10) = 124

┌───────────────────────────────┐
│                               │
│  🎮       🎮       🎮         │ ← Y = 800 - 32 - 10 = 758
│  40       82      124         │
└───────────────────────────────┘
```

---

## Phương Thức Overlay

### 1. drawPauseOverlay()

#### Mục Đích
Vẽ overlay (lớp phủ) khi game tạm dừng (PAUSED).

#### Signature

```java
public void drawPauseOverlay()
```

#### Implementation

```java
public void drawPauseOverlay() {
    double cx = Constants.Window.WINDOW_WIDTH / 2.0;

    // Vẽ hình chữ nhật trong suốt làm nền
    gc.setFill(Color.rgb(0, 0, 0, 0.6));
    gc.fillRoundRect(120, 320, 360, 160, 10, 10);

    gc.setTextAlign(TextAlignment.CENTER);
    gc.setFill(Color.WHITE);

    // Vẽ chữ "PAUSED" và hướng dẫn
    gc.setFont(Font.font("Monospaced", 18));
    gc.fillText("════════════════════", cx, 350);
    gc.setFont(Font.font("Monospaced", 36));
    gc.fillText("PAUSED", cx, 390);
    gc.setFont(Font.font("Monospaced", 16));
    gc.fillText("Press ESC to resume", cx, 420);
    gc.setFont(Font.font("Monospaced", 18));
    gc.fillText("════════════════════", cx, 450);
}
```

#### Visual Layout

```
┌────────────────────────────────────────┐
│                                        │
│         (Game continues below)         │
│                                        │
│        ┌──────────────────────┐       │ ← Y=320
│        │ ═══════════════════  │       │ ← Y=350
│        │                      │       │
│        │      PAUSED          │       │ ← Y=390 (36pt)
│        │                      │       │
│        │ Press ESC to resume  │       │ ← Y=420 (16pt)
│        │ ═══════════════════  │       │ ← Y=450
│        └──────────────────────┘       │ ← Height=160
│  X=120                    X=480       │
│         Width=360                     │
└────────────────────────────────────────┘
      cx = 300 (center X)
```

#### Component Details

**Background Box**:
```java
gc.setFill(Color.rgb(0, 0, 0, 0.6)); // Đen 60% opacity
gc.fillRoundRect(120, 320, 360, 160, 10, 10);
//                x    y    w    h  rx  ry (rounded corners)
```

**Text Elements**:
| Text | Font Size | Y Position | Color |
|------|-----------|------------|-------|
| Top border (`═══...`) | 18pt | 350 | WHITE |
| "PAUSED" | 36pt | 390 | WHITE |
| "Press ESC..." | 16pt | 420 | WHITE |
| Bottom border | 18pt | 450 | WHITE |

---

### 2. drawGameOverOverlay()

#### Mục Đích
Vẽ overlay khi game kết thúc (GAME OVER).

#### Signature

```java
public void drawGameOverOverlay(int score)
```

#### Implementation

```java
public void drawGameOverOverlay(int score) {
    double cx = Constants.Window.WINDOW_WIDTH / 2.0;
    // Vẽ nền overlay
    gc.setFill(Color.rgb(0, 0, 0, 0.6));
    gc.fillRoundRect(120, 260, 360, 200, 10, 10);

    gc.setTextAlign(TextAlignment.CENTER);
    gc.setFill(Color.WHITE);

    // Vẽ chữ "GAME OVER" và điểm số
    gc.setFont(Font.font("Monospaced", 18));
    gc.fillText("════════════════════", cx, 300);
    gc.setFont(Font.font("Monospaced", 36));
    gc.fillText("GAME OVER", cx, 340);
    gc.setFont(Font.font("Monospaced", 18));
    gc.fillText("Final Score: " + score, cx, 370);
    gc.fillText("Press 'R' to restart", cx, 400);
    gc.setFont(Font.font("Monospaced", 18));
    gc.fillText("════════════════════", cx, 430);
}
```

#### Visual Layout

```
┌────────────────────────────────────────┐
│        ┌──────────────────────┐       │ ← Y=260
│        │ ═══════════════════  │       │ ← Y=300
│        │                      │       │
│        │    GAME OVER         │       │ ← Y=340 (36pt, RED)
│        │                      │       │
│        │ Final Score: 12345   │       │ ← Y=370 (18pt)
│        │ Press 'R' to restart │       │ ← Y=400 (18pt)
│        │                      │       │
│        │ ═══════════════════  │       │ ← Y=430
│        └──────────────────────┘       │ ← Height=200
│  X=120                    X=480       │
└────────────────────────────────────────┘
```

---

### 3. drawWinOverlay()

#### Mục Đích
Vẽ overlay khi người chơi chiến thắng (thắng tất cả các màn).

#### Signature

```java
public void drawWinOverlay(int score)
```

#### Implementation

```java
public void drawWinOverlay(int score) {
    double cx = Constants.Window.WINDOW_WIDTH / 2.0;
    // Vẽ nền overlay
    gc.setFill(Color.rgb(0, 0, 0, 0.6));
    gc.fillRoundRect(120, 260, 360, 220, 10, 10);

    gc.setTextAlign(TextAlignment.CENTER);
    gc.setFill(Color.WHITE);

    // Vẽ chữ "YOU WIN!"
    gc.setFont(Font.font("Monospaced", 18));
    gc.fillText("════════════════════", cx, 300);
    gc.setFont(Font.font("Monospaced", 30));
    gc.fillText("★ YOU WIN! ★", cx, 340);
    gc.setFont(Font.font("Monospaced", 18));
    gc.fillText("Final Score: " + score, cx, 370);
    gc.fillText("All rounds completed!", cx, 400);
    gc.fillText("Press 'R' to restart", cx, 430);
    gc.setFont(Font.font("Monospaced", 18));
    gc.fillText("════════════════════", cx, 460);
}
```

#### Visual Layout

```
┌────────────────────────────────────────┐
│        ┌──────────────────────┐       │ ← Y=260
│        │ ═══════════════════  │       │ ← Y=300
│        │                      │       │
│        │   ★ YOU WIN! ★       │       │ ← Y=340 (30pt, GREEN)
│        │                      │       │
│        │ Final Score: 12345   │       │ ← Y=370
│        │ All rounds completed!│       │ ← Y=400
│        │ Press 'R' to restart │       │ ← Y=430
│        │                      │       │
│        │ ═══════════════════  │       │ ← Y=460
│        └──────────────────────┘       │ ← Height=220
└────────────────────────────────────────┘
```

---

### 4. drawLevelCompleteOverlay()

#### Mục Đích
Vẽ overlay khi hoàn thành một màn chơi (chuyển sang màn tiếp theo).

#### Signature

```java
public void drawLevelCompleteOverlay()
```

#### Implementation

```java
public void drawLevelCompleteOverlay() {
    double cx = Constants.Window.WINDOW_WIDTH / 2.0;
    // Vẽ nền overlay
    gc.setFill(Color.rgb(0, 0, 0, 0.6));
    gc.fillRoundRect(120, 320, 360, 160, 10, 10);

    gc.setTextAlign(TextAlignment.CENTER);
    gc.setFill(Color.WHITE);

    // Vẽ thông báo "LEVEL COMPLETE!"
    gc.setFont(Font.font("Monospaced", 18));
    gc.fillText("════════════════════", cx, 350);
    gc.setFont(Font.font("Monospaced", 28));
    gc.fillText("LEVEL COMPLETE!", cx, 390);
    gc.setFont(Font.font("Monospaced", 16));
    gc.fillText("Loading next round...", cx, 420);
    gc.setFont(Font.font("Monospaced", 18));
    gc.fillText("════════════════════", cx, 450);
}
```

#### Visual Layout

```
┌────────────────────────────────────────┐
│        ┌──────────────────────┐       │ ← Y=320
│        │ ═══════════════════  │       │ ← Y=350
│        │                      │       │
│        │  LEVEL COMPLETE!     │       │ ← Y=390 (28pt)
│        │                      │       │
│        │ Loading next round...│       │ ← Y=420 (16pt)
│        │                      │       │
│        │ ═══════════════════  │       │ ← Y=450
│        └──────────────────────┘       │ ← Height=160
└────────────────────────────────────────┘
```

---

## Phương Thức Entity

### Delegation Pattern

Tất cả các phương thức entity **delegate** (ủy quyền) cho `SpriteRenderer`:

```java
// 1. drawBall()
public void drawBall(Ball ball) {
    spriteRenderer.drawBall(ball);
}

// 2. drawLaser()
public void drawLaser(Laser laser) {
    spriteRenderer.drawLaser(laser);
}

// 3. drawPaddle()
public void drawPaddle(Paddle paddle) {
    spriteRenderer.drawPaddle(paddle);
}

// 4. drawBrick()
public void drawBrick(Brick brick) {
    spriteRenderer.drawBrick(brick);
}

// 5. drawPowerUp()
public void drawPowerUp(PowerUp powerUp) {
    spriteRenderer.drawPowerUp(powerUp);
}
```

### Tại Sao Delegation?

| Lợi Ích | Mô Tả |
|---------|-------|
| ✅ **Separation of Concerns** | CanvasRenderer quản lý UI/overlays, SpriteRenderer quản lý entities |
| ✅ **Single Responsibility** | Mỗi renderer có trách nhiệm riêng |
| ✅ **Maintainability** | Dễ sửa logic render entity mà không ảnh hưởng UI |
| ✅ **Reusability** | SpriteRenderer có thể dùng độc lập |

---

### present()

#### Mục Đích
Phương thức này có thể được sử dụng để hoàn tất việc render.

#### Signature

```java
public void present()
```

#### Implementation

```java
public void present() {
    // Hiện tại không cần thêm code ở đây, nhưng giữ lại cho kiến trúc render
}
```

#### Ý Nghĩa

- 🔮 **Future-proofing**: Placeholder cho tính năng tương lai
- 📦 **Double Buffering**: Có thể dùng để swap buffers
- ✨ **Post-processing**: Có thể thêm effects sau khi render

---

## Font System

### Font Loading Strategy

```
loadUIAssets()
    │
    ├──► Try: Load custom fonts
    │     │
    │     ├──► generation.ttf (24pt) → scoreFont
    │     └──► emulogic.ttf (18pt) → uiFont
    │
    └──► Catch: Use fallback fonts
          │
          ├──► Monospaced 24pt → scoreFont
          └──► Monospaced 18pt → uiFont
```

### Font Usage

| Font | Sử Dụng | Đặc Điểm |
|------|---------|----------|
| `scoreFont` | Score, High Score values | Size lớn (24pt), dễ đọc |
| `uiFont` | Labels (1UP, HIGH SCORE), overlays | Size vừa (18pt), retro style |

### Custom Font Paths

```java
// AssetLoader tìm fonts tại:
Resources/Fonts/generation.ttf
Resources/Fonts/emulogic.ttf
```

---

## Layout System

### Screen Zones

```
┌────────────────────────────────────────────────┐
│  Zone 1: Header (0 - 100)                      │ ← Logo + Score
├────────────────────────────────────────────────┤
│  Zone 2: Play Area (100 - 780)                 │ ← Game entities + Borders
│                                                │
│                                                │
│                                                │
│                                                │
├────────────────────────────────────────────────┤
│  Zone 3: Footer (780 - 800)                    │ ← Lives
└────────────────────────────────────────────────┘
```

### Responsive Positioning

```java
// Score position: 82% từ trái
double scoreX = canvas.getWidth() * 0.82;

// Center X cho overlays
double cx = Constants.Window.WINDOW_WIDTH / 2.0;

// Lives spacing
double lifeSpacing = Constants.Paddle.PADDLE_LIFE_WIDTH + 10;
```

---

## Ví Dụ Sử Dụng

### 1. Game Loop Integration

```java
public class GameLoop {
    private CanvasRenderer canvasRenderer;
    
    public void render(GameState state) {
        // 1. Clear canvas
        canvasRenderer.clear();
        
        // 2. Draw UI (logo, score, borders, lives)
        canvasRenderer.drawUI(
            state.getScore(), 
            state.getHighScore(), 
            state.getLives()
        );
        
        // 3. Draw game entities
        canvasRenderer.drawBall(state.getBall());
        canvasRenderer.drawPaddle(state.getPaddle());
        
        for (Brick brick : state.getBricks()) {
            if (!brick.isDestroyed()) {
                canvasRenderer.drawBrick(brick);
            }
        }
        
        for (Laser laser : state.getLasers()) {
            canvasRenderer.drawLaser(laser);
        }
        
        for (PowerUp powerUp : state.getPowerUps()) {
            canvasRenderer.drawPowerUp(powerUp);
        }
        
        // 4. Draw overlays based on state
        switch (state.getGameState()) {
            case PAUSED:
                canvasRenderer.drawPauseOverlay();
                break;
            case GAME_OVER:
                canvasRenderer.drawGameOverOverlay(state.getScore());
                break;
            case WON:
                canvasRenderer.drawWinOverlay(state.getScore());
                break;
            case LEVEL_COMPLETE:
                canvasRenderer.drawLevelCompleteOverlay();
                break;
        }
        
        // 5. Present (finalize)
        canvasRenderer.present();
    }
}
```

---

### 2. Custom Overlay

```java
public class CustomCanvasRenderer extends CanvasRenderer {
    public CustomCanvasRenderer(Canvas canvas, SpriteProvider sprites) {
        super(canvas, sprites);
    }
    
    // Thêm overlay mới: "Ready?"
    public void drawReadyOverlay() {
        double cx = Constants.Window.WINDOW_WIDTH / 2.0;
        
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRoundRect(150, 350, 300, 100, 10, 10);
        
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Monospaced", 32));
        gc.fillText("Ready?", cx, 400);
        
        gc.setFont(Font.font("Monospaced", 16));
        gc.fillText("Press SPACE to start", cx, 430);
    }
}
```

---

### 3. Debug Overlay

```java
public void drawDebugOverlay(GameState state) {
    gc.setFill(Color.rgb(255, 255, 0, 0.8));
    gc.setFont(Font.font("Monospaced", 12));
    
    // FPS
    gc.fillText("FPS: " + state.getFPS(), 10, 20);
    
    // Ball velocity
    gc.fillText("Ball Speed: " + state.getBall().getVelocity().getSpeed(), 10, 40);
    
    // Brick count
    gc.fillText("Bricks: " + state.getBricks().size(), 10, 60);
}
```

---

## Design Pattern

### 1. Facade Pattern

```
CanvasRenderer = Facade
    │
    ├──► SpriteRenderer (Complex subsystem)
    ├──► BorderRenderer (Complex subsystem)
    └──► AssetLoader (Complex subsystem)

Client chỉ cần gọi:
- canvasRenderer.drawUI()
- canvasRenderer.drawBall()
Không cần biết chi tiết internal
```

### 2. Delegation Pattern

```java
// CanvasRenderer không render entities trực tiếp
public void drawBall(Ball ball) {
    spriteRenderer.drawBall(ball); // Delegate
}
```

### 3. Template Method Pattern (Implicit)

```java
// Render pipeline: Clear → UI → Entities → Overlays → Present
public void renderFrame(GameState state) {
    clear();                  // Step 1
    drawUI(...);             // Step 2
    drawEntities(...);       // Step 3
    drawOverlays(...);       // Step 4
    present();               // Step 5
}
```

---

## Best Practices

### ✅ DO

#### 1. Always Clear Before Rendering

```java
// ✅ GOOD
canvasRenderer.clear();
canvasRenderer.drawUI(...);
canvasRenderer.drawBall(...);

// ❌ BAD: Không clear → double rendering
canvasRenderer.drawUI(...); // Vẽ đè lên frame cũ
```

#### 2. Use Text Alignment

```java
// ✅ GOOD: Text căn giữa
gc.setTextAlign(TextAlignment.CENTER);
gc.fillText("PAUSED", centerX, y);

// ❌ BAD: Tự tính toán offset
String text = "PAUSED";
double textWidth = estimateTextWidth(text);
gc.fillText(text, centerX - textWidth/2, y);
```

#### 3. Layer Overlays Correctly

```java
// ✅ GOOD: Overlay vẽ sau cùng (trên cùng)
canvasRenderer.drawUI(...);
canvasRenderer.drawEntities(...);
canvasRenderer.drawPauseOverlay(); // Đè lên trên

// ❌ BAD: Overlay vẽ trước
canvasRenderer.drawPauseOverlay();
canvasRenderer.drawEntities(...); // Entities che overlay!
```

#### 4. Use Constants for Layout

```java
// ✅ GOOD
double x = Constants.Window.WINDOW_SIDE_OFFSET;

// ❌ BAD
double x = 40; // Magic number!
```

### ❌ DON'T

#### 1. Không Render Entities Trực Tiếp

```java
// ❌ BAD: CanvasRenderer render entity trực tiếp
public void drawBall(Ball ball) {
    gc.drawImage(sprites.get("ball.png"), ball.getX(), ball.getY());
}

// ✅ GOOD: Delegate to SpriteRenderer
public void drawBall(Ball ball) {
    spriteRenderer.drawBall(ball);
}
```

#### 2. Không Hardcode Positions

```java
// ❌ BAD
gc.fillText("1UP", 492, 30);

// ✅ GOOD
gc.fillText("1UP", canvas.getWidth() * 0.82, 30);
```

---

## Tổng Kết

### Điểm Mạnh

| Điểm Mạnh | Mô Tả |
|-----------|-------|
| ✅ **Orchestration** | Điều phối tất cả rendering từ một điểm |
| ✅ **Delegation** | Ủy quyền cho SpriteRenderer, BorderRenderer |
| ✅ **UI Management** | Quản lý tốt logo, score, lives |
| ✅ **Overlay System** | Hệ thống overlay hoàn chỉnh |
| ✅ **Font Fallback** | Graceful degradation nếu custom font fail |
| ✅ **Separation of Concerns** | UI logic tách khỏi entity logic |

### Hạn Chế & Cải Tiến

| Hạn Chế | Cải Tiến Đề Xuất |
|---------|-----------------|
| ⚠️ Hardcoded overlay positions | Extract to constants |
| ⚠️ No animation for overlays | Add fade in/out effects |
| ⚠️ present() không làm gì | Implement double buffering |
| ⚠️ Không có theme system | Add color themes |
| ⚠️ Không có scaling support | Add responsive layout |

---

**Tác Giả**: Canvas Rendering System Documentation  
**Phiên Bản**: 1.0  
**Ngày Cập Nhật**: 2024
