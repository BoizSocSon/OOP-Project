# Class BorderRenderer - Hệ Thống Render Viền Game

## 📋 Mục Lục

1. [Tổng Quan](#tổng-quan)
2. [Kiến Trúc](#kiến-trúc)
3. [Thuộc Tính (Fields)](#thuộc-tính-fields)
4. [Constructor](#constructor)
5. [Phương Thức](#phương-thức)
6. [Ví Dụ Sử Dụng](#ví-dụ-sử-dụng)
7. [Layout System](#layout-system)
8. [Best Practices](#best-practices)
9. [Tích Hợp Với CanvasRenderer](#tích-hợp-với-canvasrenderer)

---

## Tổng Quan

### Khái Niệm

**BorderRenderer** là lớp chuyên trách **render (vẽ) các thành phần viền** (border) xung quanh khu vực chơi game (play area). Nó đảm bảo các cạnh **trên**, **trái**, và **phải** được vẽ chính xác với sprites đã tải sẵn.

### Mục Đích

```
┌─────────────────────────────────────────────────────┐
│            BorderRenderer Purpose                    │
├─────────────────────────────────────────────────────┤
│  🖼️  Border Rendering   → Vẽ viền game area         │
│  📐 Layout Management  → Quản lý vị trí viền         │
│  🎨 Sprite Integration → Tích hợp với SpriteProvider│
│  🔧 Separation of Concerns → Tách logic render viền │
└─────────────────────────────────────────────────────┘
```

### Vai Trò Trong Game

| Cạnh Viền | Sprite | Vị Trí | Mục Đích |
|-----------|--------|--------|----------|
| **Top** | `edge_top.png` | `(SIDE_OFFSET, TOP_OFFSET)` | Giới hạn trên, ngăn ball bay ra |
| **Left** | `edge_left.png` | `(0, TOP_OFFSET)` | Giới hạn trái, ball bounce lại |
| **Right** | `edge_right.png` | `(WIDTH - SIDE_OFFSET, TOP_OFFSET)` | Giới hạn phải, ball bounce lại |
| **Bottom** | ❌ Không có | N/A | Vùng "chết" - ball rơi xuống mất mạng |

### Minh Họa Layout

```
┌────────────────────────────────────────────────┐
│                   LOGO                         │ ← Vùng UI (không phải border)
│                                                │
│  ┌──────────────────────────────────────────┐ │
│  │   ← edge_top.png (cạnh trên)            │ │
│  │─────────────────────────────────────────│ │
│ edge_left  │                     │  edge_right│
│     .png   │                     │     .png   │
│      ↓     │                     │       ↓    │
│  │         │   PLAY AREA         │         │  │
│  │         │   (Ball, Paddle,    │         │  │
│  │         │    Bricks, etc.)    │         │  │
│  │         │                     │         │  │
│  │         │                     │         │  │
│  │─────────────────────────────────────────│  │
│  │            [BOTTOM: No border]          │  │
│  └─────────────────────────────────────────┘  │
│                                                │
│  🎮 🎮 🎮  ← Lives (paddle_life.png)          │
└────────────────────────────────────────────────┘
```

---

## Kiến Trúc

### Sơ Đồ UML

```
┌──────────────────────────────────────────────────────┐
│                  BorderRenderer                       │
├──────────────────────────────────────────────────────┤
│ - sprites: SpriteProvider                            │
│ - gc: GraphicsContext                                │
├──────────────────────────────────────────────────────┤
│ + BorderRenderer(gc, sprites)                        │
│ + render(): void                                     │
│ - drawTopEdge(gc): void                              │
│ - drawLeftEdge(gc): void                             │
│ - drawRightEdge(gc): void                            │
└──────────────────────────────────────────────────────┘
              │                           │
              │ uses                      │ uses
              ▼                           ▼
┌────────────────────────┐    ┌──────────────────────┐
│    SpriteProvider      │    │  GraphicsContext     │
├────────────────────────┤    ├──────────────────────┤
│ + get(name): Image     │    │ + drawImage(...)     │
└────────────────────────┘    └──────────────────────┘
```

### Dependency Graph

```
     CanvasRenderer
            │
            │ contains
            ▼
     BorderRenderer
       │        │
       │        └────────────► SpriteProvider
       │                             │
       │                             │ loads
       │                             ▼
       └───────────────────► GraphicsContext
                                     │
                                     │ draws to
                                     ▼
                                  Canvas
```

### Luồng Render

```
┌─────────────────────┐
│  CanvasRenderer     │
│  drawUI()           │
└─────────┬───────────┘
          │ calls
          ▼
┌─────────────────────┐
│  BorderRenderer     │
│  render()           │
└─────────┬───────────┘
          │
          ├───► drawTopEdge()
          │       │
          │       └─► gc.drawImage(edge_top.png, x, y)
          │
          ├───► drawLeftEdge()
          │       │
          │       └─► gc.drawImage(edge_left.png, x, y)
          │
          └───► drawRightEdge()
                  │
                  └─► gc.drawImage(edge_right.png, x, y)
```

---

## Thuộc Tính (Fields)

### 1. sprites: SpriteProvider

```java
private final SpriteProvider sprites;
```

- **Mô Tả**: Đối tượng cung cấp các hình ảnh (sprites) cần thiết cho việc render
- **Tính Chất**: `final` - không thể thay đổi sau khi khởi tạo
- **Nhiệm Vụ**: Cung cấp 3 sprites chính:
  - `edge_top.png` (cạnh trên)
  - `edge_left.png` (cạnh trái)
  - `edge_right.png` (cạnh phải)

#### Ví Dụ Sử Dụng

```java
// Lấy sprite viền trên
Image edgeTop = sprites.get("edge_top.png");

// Xử lý trường hợp sprite không tồn tại
if (edgeTop == null) {
    System.err.println("Warning: edge_top.png not found.");
    return;
}
```

### 2. gc: GraphicsContext

```java
private final GraphicsContext gc;
```

- **Mô Tả**: Context đồ họa của Canvas, dùng để thực hiện các thao tác vẽ
- **Tính Chất**: `final` - reference không đổi
- **Nhiệm Vụ**: Vẽ sprites lên Canvas với phương thức `drawImage()`

#### Ví Dụ Sử Dụng

```java
// Vẽ hình ảnh tại vị trí (x, y)
gc.drawImage(edgeTop, startX, startY);

// Vẽ với kích thước tùy chỉnh
gc.drawImage(edgeTop, startX, startY, width, height);
```

---

## Constructor

### Chữ Ký

```java
public BorderRenderer(GraphicsContext gc, SpriteProvider sprites)
```

### Tham Số

| Tham Số | Kiểu | Mô Tả |
|---------|------|-------|
| `gc` | `GraphicsContext` | Context đồ họa để vẽ |
| `sprites` | `SpriteProvider` | Đối tượng cung cấp sprites viền |

### Implementation

```java
public BorderRenderer(GraphicsContext gc, SpriteProvider sprites) {
    this.gc = gc;
    this.sprites = sprites;
}
```

### Ví Dụ Khởi Tạo

```java
// Trong CanvasRenderer
public class CanvasRenderer {
    private BorderRenderer borderRenderer;
    
    public CanvasRenderer(Canvas canvas, SpriteProvider sprites) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Khởi tạo BorderRenderer
        this.borderRenderer = new BorderRenderer(gc, sprites);
    }
    
    public void drawUI(int score, int highScore, int lives) {
        // Vẽ logo, score, lives...
        
        // Vẽ viền
        borderRenderer.render(); // 👈 Gọi render()
        
        // Vẽ lives icons...
    }
}
```

---

## Phương Thức

### 1. render()

#### Mục Đích
Thực hiện vẽ toàn bộ các cạnh viền: trên, trái và phải.

#### Signature

```java
public void render()
```

#### Implementation

```java
public void render() {
    drawTopEdge(gc);
    drawLeftEdge(gc);
    drawRightEdge(gc);
}
```

#### Đặc Điểm

- ✅ **Public**: Được gọi từ `CanvasRenderer`
- ✅ **Orchestrator**: Điều phối 3 phương thức private
- ✅ **Order**: Vẽ theo thứ tự trên → trái → phải (không quan trọng vì không overlap)

#### Call Chain

```
render()
   │
   ├──► drawTopEdge(gc)    → Vẽ viền trên
   │
   ├──► drawLeftEdge(gc)   → Vẽ viền trái
   │
   └──► drawRightEdge(gc)  → Vẽ viền phải
```

---

### 2. drawTopEdge()

#### Mục Đích
Vẽ cạnh viền phía trên.

#### Signature

```java
private void drawTopEdge(GraphicsContext gc)
```

#### Implementation

```java
private void drawTopEdge(GraphicsContext gc) {
    // Lấy hình ảnh (sprite) cho cạnh trên
    Image edgeTop = sprites.get("edge_top.png");
    if (edgeTop == null) {
        // Tránh lỗi nếu sprite không được tìm thấy
        System.err.println("Warning: edge_top.png not found.");
        return;
    }

    // Tọa độ X bắt đầu, có tính đến offset bên (lề trái)
    double startX = Constants.Window.WINDOW_SIDE_OFFSET;
    // Tọa độ Y bắt đầu (lề trên)
    double srartY = Constants.Window.WINDOW_TOP_OFFSET;

    // Vẽ hình ảnh cạnh trên tại vị trí đã xác định
    gc.drawImage(edgeTop, startX, srartY);
}
```

#### Vị Trí Tính Toán

| Biến | Giá Trị (Ví dụ) | Mô Tả |
|------|-----------------|-------|
| `startX` | `WINDOW_SIDE_OFFSET` (40) | Lề trái để căn chỉnh với viền trái |
| `startY` | `WINDOW_TOP_OFFSET` (100) | Lề trên để tránh vùng logo/score |

#### Minh Họa

```
  0         40                                   560   600
  ├─────────┼──────────────────────────────────┼─────┤
0 │         │                                   │     │
  │  LOGO   │                                   │     │
100├─────────┼═══════════════════════════════════┼─────┤
  │         │   edge_top.png (bắt đầu tại 40,100)│     │
  │   L     │───────────────────────────────────│  R  │
  │   E     │                                   │  I  │
  │   F     │        PLAY AREA                  │  G  │
  │   T     │                                   │  H  │
  │         │                                   │  T  │
```

#### Xử Lý Lỗi

```java
if (edgeTop == null) {
    System.err.println("Warning: edge_top.png not found.");
    return; // 👈 Early return, không crash game
}
```

---

### 3. drawLeftEdge()

#### Mục Đích
Vẽ cạnh viền phía bên trái.

#### Signature

```java
private void drawLeftEdge(GraphicsContext gc)
```

#### Implementation

```java
private void drawLeftEdge(GraphicsContext gc) {
    // Lấy hình ảnh (sprite) cho cạnh trái
    Image edgeLeft = sprites.get("edge_left.png");
    if (edgeLeft == null) {
        System.err.println("Warning: edge_left.png not found.");
        return;
    }

    // Cạnh trái bắt đầu từ tọa độ X=0
    double startX = 0;
    // Bắt đầu từ offset Y của cửa sổ (sau cạnh trên)
    double startY = Constants.Window.WINDOW_TOP_OFFSET;

    // Vẽ hình ảnh cạnh trái
    gc.drawImage(edgeLeft, startX, startY);
}
```

#### Vị Trí Tính Toán

| Biến | Giá Trị (Ví dụ) | Mô Tả |
|------|-----------------|-------|
| `startX` | `0` | Sát cạnh trái cùng của canvas |
| `startY` | `WINDOW_TOP_OFFSET` (100) | Bắt đầu sau vùng UI trên |

#### Minh Họa

```
 0                               600
 ├───────────────────────────────┤
0│                               │
 │         LOGO AREA             │
100├───┐                           │
 │ e │                           │
 │ d │                           │
 │ g │      PLAY AREA            │
 │ e │                           │
 │ _ │                           │
 │ l │                           │
 │ e │                           │
 │ f │                           │
 │ t │                           │
800└───┘                           │
```

---

### 4. drawRightEdge()

#### Mục Đích
Vẽ cạnh viền phía bên phải.

#### Signature

```java
private void drawRightEdge(GraphicsContext gc)
```

#### Implementation

```java
private void drawRightEdge(GraphicsContext gc) {
    // Lấy hình ảnh (sprite) cho cạnh phải
    Image edgeRight = sprites.get("edge_right.png");
    if (edgeRight == null) {
        System.err.println("Warning: edge_right.png not found.");
        return;
    }

    // Tọa độ X bắt đầu: Chiều rộng cửa sổ trừ đi offset bên (độ dày của viền)
    double startX = Constants.Window.WINDOW_WIDTH - Constants.Window.WINDOW_SIDE_OFFSET;
    // Bắt đầu từ offset Y của cửa sổ (sau cạnh trên)
    double startY = Constants.Window.WINDOW_TOP_OFFSET;

    // Vẽ hình ảnh cạnh phải
    gc.drawImage(edgeRight, startX, startY);
}
```

#### Vị Trí Tính Toán

| Biến | Giá Trị (Ví dụ) | Công Thức |
|------|-----------------|-----------|
| `startX` | `560` | `WINDOW_WIDTH (600) - WINDOW_SIDE_OFFSET (40)` |
| `startY` | `100` | `WINDOW_TOP_OFFSET` |

#### Minh Họa

```
 0                           560 600
 ├───────────────────────────┼───┤
0│                           │   │
 │      LOGO AREA            │   │
100├───────────────────────────┼───┤
 │                           │ e │
 │                           │ d │
 │      PLAY AREA            │ g │
 │                           │ e │
 │                           │ _ │
 │                           │ r │
 │                           │ i │
 │                           │ g │
 │                           │ h │
800└───────────────────────────┴───┘
```

#### Alignment

```java
// Đảm bảo viền phải khớp với viền trái
// edgeLeft startX = 0
// edgeRight startX = WINDOW_WIDTH - WINDOW_SIDE_OFFSET
// Khoảng cách giữa = WINDOW_WIDTH - 2*WINDOW_SIDE_OFFSET (play area width)
```

---

## Ví Dụ Sử Dụng

### 1. Tích Hợp Cơ Bản Trong CanvasRenderer

```java
public class CanvasRenderer {
    private BorderRenderer borderRenderer;
    
    public CanvasRenderer(Canvas canvas, SpriteProvider sprites) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        this.borderRenderer = new BorderRenderer(gc, sprites);
    }
    
    public void drawUI(int score, int highScore, int lives) {
        // 1. Vẽ logo
        gc.drawImage(sprites.get("logo.png"), 0, 0);
        
        // 2. Vẽ score
        gc.setFont(uiFont);
        gc.setFill(Color.RED);
        gc.fillText("1UP", canvas.getWidth() * 0.82, 30);
        
        // 3. Vẽ viền
        borderRenderer.render(); // 👈 Vẽ tất cả viền
        
        // 4. Vẽ lives
        for (int i = 0; i < lives; i++) {
            double lifeX = Constants.Window.WINDOW_SIDE_OFFSET + i * 50;
            double lifeY = Constants.Window.WINDOW_HEIGHT - 40;
            gc.drawImage(sprites.get("paddle_life.png"), lifeX, lifeY);
        }
    }
}
```

### 2. Custom BorderRenderer (Thêm Border Dưới)

```java
public class CustomBorderRenderer extends BorderRenderer {
    public CustomBorderRenderer(GraphicsContext gc, SpriteProvider sprites) {
        super(gc, sprites);
    }
    
    @Override
    public void render() {
        super.render(); // Vẽ 3 cạnh gốc
        drawBottomEdge(); // Thêm cạnh dưới
    }
    
    private void drawBottomEdge() {
        Image edgeBottom = sprites.get("edge_bottom.png");
        if (edgeBottom != null) {
            double startX = Constants.Window.WINDOW_SIDE_OFFSET;
            double startY = Constants.Window.WINDOW_HEIGHT - Constants.Window.WINDOW_SIDE_OFFSET;
            gc.drawImage(edgeBottom, startX, startY);
        }
    }
}
```

### 3. Animated Border (Viền Phát Sáng)

```java
public class AnimatedBorderRenderer extends BorderRenderer {
    private Animation glowAnimation;
    private boolean animationEnabled = false;
    
    public AnimatedBorderRenderer(GraphicsContext gc, SpriteProvider sprites) {
        super(gc, sprites);
        
        // Tạo animation phát sáng (4 frames)
        List<Image> glowFrames = Arrays.asList(
            sprites.get("edge_glow_0.png"),
            sprites.get("edge_glow_1.png"),
            sprites.get("edge_glow_2.png"),
            sprites.get("edge_glow_3.png")
        );
        glowAnimation = new Animation(glowFrames, 100, true); // LOOP
    }
    
    @Override
    public void render() {
        super.render(); // Vẽ viền thường
        
        if (animationEnabled) {
            glowAnimation.update();
            Image glowFrame = glowAnimation.getCurrentFrame();
            
            // Vẽ đè overlay phát sáng lên viền
            gc.drawImage(glowFrame, 
                Constants.Window.WINDOW_SIDE_OFFSET, 
                Constants.Window.WINDOW_TOP_OFFSET);
        }
    }
    
    public void enableGlow() {
        animationEnabled = true;
        glowAnimation.play();
    }
    
    public void disableGlow() {
        animationEnabled = false;
        glowAnimation.pause();
    }
}
```

### 4. Debug BorderRenderer (Vẽ Bounding Box)

```java
public class DebugBorderRenderer extends BorderRenderer {
    private boolean debugMode = false;
    
    public DebugBorderRenderer(GraphicsContext gc, SpriteProvider sprites) {
        super(gc, sprites);
    }
    
    @Override
    public void render() {
        super.render();
        
        if (debugMode) {
            drawDebugInfo();
        }
    }
    
    private void drawDebugInfo() {
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        
        // Vẽ bounding box của play area
        double x = Constants.Window.WINDOW_SIDE_OFFSET;
        double y = Constants.Window.WINDOW_TOP_OFFSET;
        double width = Constants.Window.WINDOW_WIDTH - 2 * Constants.Window.WINDOW_SIDE_OFFSET;
        double height = Constants.Window.WINDOW_HEIGHT - Constants.Window.WINDOW_TOP_OFFSET;
        
        gc.strokeRect(x, y, width, height);
        
        // Vẽ tọa độ các góc
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Monospaced", 10));
        gc.fillText(String.format("(%.0f,%.0f)", x, y), x, y - 5);
        gc.fillText(String.format("(%.0f,%.0f)", x + width, y), x + width - 50, y - 5);
    }
    
    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
    }
}
```

---

## Layout System

### Constants Liên Quan

```java
public class Constants {
    public static class Window {
        // Kích thước cửa sổ
        public static final double WINDOW_WIDTH = 600;
        public static final double WINDOW_HEIGHT = 800;
        
        // Offset cho viền
        public static final double WINDOW_SIDE_OFFSET = 40;  // Độ dày viền trái/phải
        public static final double WINDOW_TOP_OFFSET = 100;  // Độ cao vùng UI trên
    }
}
```

### Tính Toán Play Area

```java
public class PlayArea {
    // Kích thước vùng chơi (không bao gồm viền)
    public static final double PLAY_AREA_WIDTH = 
        Constants.Window.WINDOW_WIDTH - 2 * Constants.Window.WINDOW_SIDE_OFFSET;
    // = 600 - 2*40 = 520 pixels
    
    public static final double PLAY_AREA_HEIGHT = 
        Constants.Window.WINDOW_HEIGHT - Constants.Window.WINDOW_TOP_OFFSET;
    // = 800 - 100 = 700 pixels
    
    // Tọa độ góc trên trái của play area
    public static final double PLAY_AREA_X = Constants.Window.WINDOW_SIDE_OFFSET; // 40
    public static final double PLAY_AREA_Y = Constants.Window.WINDOW_TOP_OFFSET;  // 100
}
```

### Border Dimensions

| Border | Sprite Size (Ví dụ) | Position | Dimensions |
|--------|---------------------|----------|------------|
| **Top** | 520×30 pixels | `(40, 100)` | Width = PLAY_AREA_WIDTH |
| **Left** | 40×700 pixels | `(0, 100)` | Height = PLAY_AREA_HEIGHT |
| **Right** | 40×700 pixels | `(560, 100)` | Height = PLAY_AREA_HEIGHT |

### Responsive Layout (Cải tiến)

```java
public class ResponsiveBorderRenderer extends BorderRenderer {
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    
    public ResponsiveBorderRenderer(GraphicsContext gc, SpriteProvider sprites) {
        super(gc, sprites);
    }
    
    public void setScale(double scaleX, double scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    
    @Override
    public void render() {
        gc.save(); // Lưu state
        gc.scale(scaleX, scaleY);
        super.render();
        gc.restore(); // Khôi phục state
    }
}
```

---

## Best Practices

### ✅ DO

#### 1. Kiểm Tra Null Trước Khi Vẽ

```java
// ✅ GOOD: Kiểm tra null và log warning
Image edgeTop = sprites.get("edge_top.png");
if (edgeTop == null) {
    System.err.println("Warning: edge_top.png not found.");
    return; // Không crash game
}
gc.drawImage(edgeTop, x, y);
```

#### 2. Sử Dụng Constants Cho Vị Trí

```java
// ✅ GOOD: Dùng constants thay vì magic numbers
double startX = Constants.Window.WINDOW_SIDE_OFFSET;
double startY = Constants.Window.WINDOW_TOP_OFFSET;

// ❌ BAD:
double startX = 40; // Magic number!
double startY = 100;
```

#### 3. Tách Logic Vẽ Từng Cạnh

```java
// ✅ GOOD: Mỗi cạnh có phương thức riêng
public void render() {
    drawTopEdge(gc);
    drawLeftEdge(gc);
    drawRightEdge(gc);
}

// ❌ BAD: Tất cả trong một phương thức
public void render() {
    Image edgeTop = sprites.get("edge_top.png");
    gc.drawImage(edgeTop, ...);
    Image edgeLeft = sprites.get("edge_left.png");
    gc.drawImage(edgeLeft, ...);
    // ... quá dài và khó maintain
}
```

#### 4. Gọi render() Trong Mỗi Frame

```java
// ✅ GOOD: Vẽ viền mỗi frame
public void drawUI(int score, int highScore, int lives) {
    // ... vẽ UI ...
    borderRenderer.render(); // 👈 Mỗi frame
    // ... vẽ lives ...
}
```

### ❌ DON'T

#### 1. Không Hardcode Vị Trí

```java
// ❌ BAD: Hardcode coordinates
gc.drawImage(edgeTop, 40, 100);

// ✅ GOOD: Sử dụng constants
gc.drawImage(edgeTop, 
    Constants.Window.WINDOW_SIDE_OFFSET,
    Constants.Window.WINDOW_TOP_OFFSET);
```

#### 2. Không Bỏ Qua Null Check

```java
// ❌ BAD: Không kiểm tra null
Image edgeTop = sprites.get("edge_top.png");
gc.drawImage(edgeTop, x, y); // NullPointerException nếu không tìm thấy!

// ✅ GOOD: Kiểm tra trước
if (edgeTop != null) {
    gc.drawImage(edgeTop, x, y);
}
```

#### 3. Không Vẽ Border Nhiều Lần

```java
// ❌ BAD: Vẽ lại border không cần thiết
for (Ball ball : balls) {
    drawBall(ball);
    borderRenderer.render(); // ❌ Lãng phí performance!
}

// ✅ GOOD: Vẽ border một lần mỗi frame
borderRenderer.render(); // Vẽ một lần
for (Ball ball : balls) {
    drawBall(ball);
}
```

#### 4. Không Thay Đổi GraphicsContext State

```java
// ❌ BAD: Thay đổi state mà không restore
private void drawTopEdge(GraphicsContext gc) {
    gc.setGlobalAlpha(0.5); // ❌ Ảnh hưởng đến render khác!
    gc.drawImage(edgeTop, x, y);
}

// ✅ GOOD: Save/restore state
private void drawTopEdge(GraphicsContext gc) {
    gc.save();
    gc.setGlobalAlpha(0.5);
    gc.drawImage(edgeTop, x, y);
    gc.restore(); // Khôi phục state
}
```

---

## Tích Hợp Với CanvasRenderer

### Dependency Injection

```java
public class CanvasRenderer {
    private final BorderRenderer borderRenderer;
    
    public CanvasRenderer(Canvas canvas, SpriteProvider sprites) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Inject dependencies vào BorderRenderer
        this.borderRenderer = new BorderRenderer(gc, sprites);
    }
}
```

### Render Order

```java
public void drawUI(int score, int highScore, int lives) {
    // 1. Background (optional)
    gc.setFill(Color.BLACK);
    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    
    // 2. Logo
    gc.drawImage(sprites.get("logo.png"), 0, 0);
    
    // 3. Score & High Score
    drawScoreUI(score, highScore);
    
    // 4. Borders (trước lives để lives nằm trên viền)
    borderRenderer.render(); // 👈
    
    // 5. Lives
    drawLivesUI(lives);
}
```

### Performance Consideration

```java
// Tối ưu: Chỉ vẽ border khi cần thiết
public class OptimizedBorderRenderer extends BorderRenderer {
    private boolean dirty = true; // Cần vẽ lại?
    
    @Override
    public void render() {
        if (dirty) {
            super.render();
            dirty = false; // Đánh dấu đã vẽ
        }
    }
    
    public void markDirty() {
        dirty = true; // Cần vẽ lại (ví dụ: khi resize)
    }
}
```

---

## Tổng Kết

### Điểm Mạnh

| Điểm Mạnh | Mô Tả |
|-----------|-------|
| ✅ **Separation of Concerns** | Tách riêng logic render viền |
| ✅ **Đơn giản** | API rõ ràng với một phương thức public `render()` |
| ✅ **An toàn** | Null check cho tất cả sprites |
| ✅ **Maintainable** | Mỗi cạnh có phương thức riêng |
| ✅ **Reusable** | Dễ dàng thay đổi sprites viền |

### Hạn Chế & Cải Tiến

| Hạn Chế | Cải Tiến Đề Xuất |
|---------|-----------------|
| ⚠️ Typo: `srartY` → `startY` | Sửa lỗi chính tả |
| ⚠️ Không có border dưới | Thêm `drawBottomEdge()` nếu cần |
| ⚠️ Không hỗ trợ animation | Mở rộng với `AnimatedBorderRenderer` |
| ⚠️ Không có debug mode | Thêm bounding box visualizer |
| ⚠️ Không có caching | Tối ưu với dirty flag |

### Khi Nào Sử Dụng

| Trường Hợp | Giải Pháp |
|-----------|-----------|
| Cần vẽ viền tĩnh | ✅ BorderRenderer |
| Cần viền động (phát sáng, pulse) | ✅ AnimatedBorderRenderer |
| Cần debug play area | ✅ DebugBorderRenderer |
| Cần responsive layout | ✅ ResponsiveBorderRenderer |
| Không cần viền (flat design) | ❌ Không dùng BorderRenderer |

---

## 📚 Tài Liệu Tham Khảo

- **JavaFX GraphicsContext**: https://openjfx.io/javadoc/17/javafx.graphics/javafx/scene/canvas/GraphicsContext.html
- **Image Drawing**: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/canvas/GraphicsContext.html#drawImage
- **SpriteProvider Documentation**: `docs/Utils/SpriteProvider.md`
- **CanvasRenderer Documentation**: `docs/Render/CanvasRenderer.md`

---

**Tác Giả**: Border Rendering System Documentation  
**Phiên Bản**: 1.0  
**Ngày Cập Nhật**: 2024
