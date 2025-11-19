# UIHelper

## Tổng quan
`UIHelper` là một utility class (lớp tiện ích) chứa các phương thức tĩnh (static methods) chuyên dụng để hỗ trợ việc rendering các thành phần giao diện người dùng (UI) lên `GraphicsContext`. Lớp này cung cấp các công cụ vẽ text, hình ảnh, box, và gradient một cách tiện lợi và nhất quán.

## Package
```java
package UI;
```

## Đặc điểm

- **Utility Class**: Tất cả phương thức đều là static
- **Non-instantiable**: Constructor private để ngăn chặn khởi tạo đối tượng
- **Helper Functions**: Cung cấp các hàm tiện ích cho UI rendering
- **Reusable**: Có thể sử dụng ở bất kỳ đâu trong project

## Constructor

### private UIHelper()
Constructor private với exception để ngăn chặn việc khởi tạo đối tượng.

```java
private UIHelper() {
    throw new UnsupportedOperationException("Utility class");
}
```

**Lý do:** Utility class không nên được instantiate (khởi tạo)

## Phương thức vẽ Text

### drawCenteredText (Overload 1)
Vẽ text căn giữa trong một vùng rectangle.

**Signature:**
```java
public static void drawCenteredText(GraphicsContext gc, String text,
                                   double x, double y, double width, double height,
                                   Font font, Color color)
```

**Tham số:**
- **gc**: GraphicsContext để vẽ
- **text**: Text cần vẽ
- **x, y**: Tọa độ góc trên trái của vùng
- **width, height**: Kích thước vùng
- **font**: Font chữ
- **color**: Màu chữ

**Cơ chế:**
- Thiết lập font và màu
- Căn giữa theo cả horizontal (CENTER) và vertical (CENTER)
- Tính toán vị trí center: `(x + width/2, y + height/2)`

**Ví dụ:**
```java
UIHelper.drawCenteredText(
    gc, "GAME OVER",
    100, 200, 400, 100,
    Font.font("Arial", 36),
    Color.RED
);
```

### drawCenteredText (Overload 2)
Vẽ text căn giữa tại một điểm cụ thể (centerX, centerY).

**Signature:**
```java
public static void drawCenteredText(GraphicsContext gc, String text,
                                   double centerX, double centerY,
                                   Font font, Color color)
```

**Tham số:**
- **gc**: GraphicsContext để vẽ
- **text**: Text cần vẽ
- **centerX**: Tọa độ X center
- **centerY**: Tọa độ Y center
- **font**: Font chữ
- **color**: Màu chữ

**Ví dụ:**
```java
UIHelper.drawCenteredText(
    gc, "SCORE: 1000",
    400, 300,
    Font.font("Arial", 24),
    Color.WHITE
);
```

### drawLeftAlignedText
Vẽ text với alignment bên trái.

**Signature:**
```java
public static void drawLeftAlignedText(GraphicsContext gc, String text,
                                      double x, double y,
                                      Font font, Color color)
```

**Tham số:**
- **gc**: GraphicsContext để vẽ
- **text**: Text cần vẽ
- **x, y**: Tọa độ góc trên trái
- **font**: Font chữ
- **color**: Màu chữ

**Cơ chế:**
- Text align LEFT
- Baseline TOP

**Ví dụ:**
```java
UIHelper.drawLeftAlignedText(
    gc, "Level: 1",
    20, 20,
    Font.font("Arial", 16),
    Color.WHITE
);
```

### drawRightAlignedText
Vẽ text với alignment bên phải.

**Signature:**
```java
public static void drawRightAlignedText(GraphicsContext gc, String text,
                                       double x, double y,
                                       Font font, Color color)
```

**Tham số:**
- **gc**: GraphicsContext để vẽ
- **text**: Text cần vẽ
- **x**: Tọa độ X (right edge - cạnh phải)
- **y**: Tọa độ Y
- **font**: Font chữ
- **color**: Màu chữ

**Ví dụ:**
```java
UIHelper.drawRightAlignedText(
    gc, "High Score: 5000",
    780, 20,  // Căn phải tại x=780
    Font.font("Arial", 16),
    Color.YELLOW
);
```

## Phương thức vẽ hình ảnh

### drawLogo
Vẽ logo game căn giữa.

**Signature:**
```java
public static void drawLogo(GraphicsContext gc, Image logo,
                           double centerX, double centerY,
                           double width, double height)
```

**Tham số:**
- **gc**: GraphicsContext để vẽ
- **logo**: Image logo
- **centerX**: Tọa độ X center
- **centerY**: Tọa độ Y center
- **width**: Chiều rộng hiển thị
- **height**: Chiều cao hiển thị

**Cơ chế:**
- Tính toán tọa độ góc trên trái: `x = centerX - width/2`, `y = centerY - height/2`
- Vẽ image tại vị trí đã tính toán

**Ví dụ:**
```java
Image logo = new Image("logo.png");
UIHelper.drawLogo(
    gc, logo,
    400, 200,  // Center tại (400, 200)
    300, 150   // Kích thước 300x150
);
```

## Phương thức vẽ hình học

### drawBox
Vẽ một box (hình chữ nhật) với border tùy chọn.

**Signature:**
```java
public static void drawBox(GraphicsContext gc, double x, double y,
                          double width, double height,
                          Color fillColor, Color borderColor, double borderWidth)
```

**Tham số:**
- **gc**: GraphicsContext để vẽ
- **x, y**: Tọa độ góc trên trái
- **width, height**: Kích thước
- **fillColor**: Màu fill (null nếu không fill)
- **borderColor**: Màu border (null nếu không vẽ border)
- **borderWidth**: Độ rộng border

**Tính năng:**
- Fill: Chỉ vẽ khi `fillColor != null`
- Border: Chỉ vẽ khi `borderColor != null && borderWidth > 0`

**Ví dụ:**
```java
// Box với fill và border
UIHelper.drawBox(
    gc, 100, 100, 200, 100,
    Color.rgb(50, 50, 50, 0.8),  // Fill semi-transparent
    Color.WHITE,                  // Border trắng
    2.0                           // Border 2px
);

// Box chỉ có border (không fill)
UIHelper.drawBox(
    gc, 100, 100, 200, 100,
    null,           // Không fill
    Color.RED,      // Border đỏ
    3.0
);
```

## Phương thức vẽ Gradient

### drawGradientBackground
Vẽ một gradient background dọc đơn giản.

**Signature:**
```java
public static void drawGradientBackground(GraphicsContext gc, double x, double y,
                                         double width, double height,
                                         Color topColor, Color bottomColor)
```

**Tham số:**
- **gc**: GraphicsContext để vẽ
- **x, y**: Tọa độ góc trên trái
- **width, height**: Kích thước
- **topColor**: Màu trên
- **bottomColor**: Màu dưới

**Cơ chế:**
- Mô phỏng gradient đứng bằng cách vẽ nhiều đường ngang mỏng
- Sử dụng 100 steps (đường ngang)
- Mỗi đường cao `height / 100`
- Nội suy màu giữa `topColor` và `bottomColor` theo tỉ lệ

**Ví dụ:**
```java
// Gradient từ xanh đậm đến đen
UIHelper.drawGradientBackground(
    gc, 0, 0, 800, 600,
    Color.rgb(0, 50, 100),  // Xanh đậm ở trên
    Color.BLACK             // Đen ở dưới
);

// Gradient từ trắng đến xám
UIHelper.drawGradientBackground(
    gc, 0, 0, 800, 600,
    Color.WHITE,
    Color.GRAY
);
```

### interpolateColor (Private)
Nội suy (Interpolate) giữa hai màu dựa trên tỉ lệ.

**Signature:**
```java
private static Color interpolateColor(Color c1, Color c2, double ratio)
```

**Tham số:**
- **c1**: Màu 1
- **c2**: Màu 2
- **ratio**: Tỉ lệ (0.0 đến 1.0)

**Công thức:**
```
red   = c1.red   + (c2.red   - c1.red)   * ratio
green = c1.green + (c2.green - c1.green) * ratio
blue  = c1.blue  + (c2.blue  - c1.blue)  * ratio
```

**Ví dụ:**
- `ratio = 0.0`: Trả về màu c1
- `ratio = 0.5`: Trả về màu giữa c1 và c2
- `ratio = 1.0`: Trả về màu c2

## Ví dụ sử dụng tổng hợp

### Vẽ UI cho Game Over Screen
```java
public class GameOverScreen implements Screen {
    @Override
    public void render(GraphicsContext gc) {
        // Vẽ gradient background
        UIHelper.drawGradientBackground(
            gc, 0, 0, 800, 600,
            Color.rgb(20, 20, 40),
            Color.BLACK
        );
        
        // Vẽ box chứa thông tin
        UIHelper.drawBox(
            gc, 200, 150, 400, 300,
            Color.rgb(40, 40, 60, 0.9),
            Color.rgb(100, 150, 200),
            3.0
        );
        
        // Vẽ title
        UIHelper.drawCenteredText(
            gc, "GAME OVER",
            200, 200, 400, 80,
            Font.font("Arial", 48),
            Color.RED
        );
        
        // Vẽ score
        UIHelper.drawCenteredText(
            gc, "Final Score: " + score,
            400, 320,
            Font.font("Arial", 24),
            Color.WHITE
        );
        
        // Vẽ high score (căn phải)
        UIHelper.drawRightAlignedText(
            gc, "High Score: " + highScore,
            580, 380,
            Font.font("Arial", 16),
            Color.YELLOW
        );
    }
}
```

### Vẽ HUD (Heads-Up Display)
```java
public void renderHUD(GraphicsContext gc) {
    Font hudFont = Font.font("Arial", 18);
    
    // Score (căn trái)
    UIHelper.drawLeftAlignedText(
        gc, "Score: " + score,
        20, 20,
        hudFont, Color.WHITE
    );
    
    // Lives (căn trái)
    UIHelper.drawLeftAlignedText(
        gc, "Lives: " + lives,
        20, 45,
        hudFont, Color.WHITE
    );
    
    // Level (căn phải)
    UIHelper.drawRightAlignedText(
        gc, "Level: " + currentLevel,
        780, 20,
        hudFont, Color.CYAN
    );
    
    // High Score (căn phải)
    UIHelper.drawRightAlignedText(
        gc, "High: " + highScore,
        780, 45,
        hudFont, Color.YELLOW
    );
}
```

## Best Practices

1. **Sử dụng UIHelper thay vì code trực tiếp**: Đảm bảo tính nhất quán
2. **Tái sử dụng màu và font**: Định nghĩa constants cho màu và font thường dùng
3. **Căn chỉnh cẩn thận**: Chú ý đến alignment mode (LEFT/CENTER/RIGHT, TOP/CENTER)
4. **Performance**: Gradient có thể chậm, cân nhắc sử dụng image thay thế cho gradient phức tạp

## Lợi ích

1. **Code reusability**: Không cần viết lại code vẽ UI
2. **Consistency**: UI nhất quán trên toàn project
3. **Maintainability**: Dễ sửa đổi và bảo trì
4. **Cleaner code**: Code gọn gàng, dễ đọc
5. **Centralized logic**: Logic vẽ UI tập trung một chỗ

## Dependencies
- `javafx.scene.canvas.GraphicsContext`: Context để vẽ
- `javafx.scene.image.Image`: Để vẽ hình ảnh
- `javafx.scene.paint.Color`: Để quản lý màu sắc
- `javafx.scene.text.Font`: Để quản lý font
- `javafx.scene.text.TextAlignment`: Để căn chỉnh text
- `javafx.geometry.VPos`: Để căn chỉnh text theo vertical
