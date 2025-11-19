# Button

## Tổng quan
Lớp `Button` là một component UI dùng để tạo các nút bấm trong giao diện menu của trò chơi Arkanoid. Lớp này quản lý việc render, các trạng thái tương tác (hover, selected) và xử lý sự kiện click.

## Package
```java
package UI;
```

## Thuộc tính

### Thuộc tính vị trí và kích thước
- `double x, y`: Tọa độ góc trên bên trái của button
- `double width, height`: Chiều rộng và chiều cao của button
- `String text`: Text hiển thị trên button

### Thuộc tính trạng thái
- `boolean isHovered`: Cho biết chuột có đang di chuyển qua button hay không
- `boolean isSelected`: Cho biết button có đang được chọn (highlight) hay không
- `Runnable onClick`: Callback function sẽ được thực thi khi button được click

### Thuộc tính màu sắc (Constants)
- `NORMAL_BG_COLOR`: Màu nền khi button ở trạng thái bình thường `rgb(40, 40, 60, 0.85)`
- `HOVER_BG_COLOR`: Màu nền khi chuột hover qua button `rgb(60, 60, 100, 0.9)`
- `SELECTED_BG_COLOR`: Màu nền khi button được chọn `rgb(80, 80, 140, 0.95)`
- `NORMAL_BORDER`: Màu viền bình thường `rgb(100, 150, 200)`
- `HOVER_BORDER`: Màu viền khi hover `rgb(150, 200, 255)`
- `SELECTED_BORDER`: Màu viền khi selected (vàng) `#FFD700`
- `TEXT_COLOR`: Màu chữ (trắng)
- `BORDER_WIDTH`: Độ dày viền `3.0`
- `CORNER_RADIUS`: Bán kính bo góc `8.0`

### Thuộc tính khác
- `Font font`: Font chữ được tải từ AssetLoader hoặc font mặc định

## Constructor

### Button(double x, double y, double width, double height, String text, Runnable onClick)
Khởi tạo một button mới với các thông số:
- **x, y**: Vị trí góc trên bên trái
- **width, height**: Kích thước button
- **text**: Text hiển thị
- **onClick**: Callback khi button được click

```java
Button playButton = new Button(100, 200, 200, 50, "PLAY", () -> {
    // Code xử lý khi click
});
```

## Phương thức

### render(GraphicsContext gc)
Vẽ button lên canvas với thiết kế bo góc đẹp mắt.

**Tính năng:**
- Vẽ shadow/glow effect nhẹ khi button được hover hoặc selected
- Vẽ nền với bo góc (rounded rectangle)
- Vẽ viền với màu tương ứng trạng thái
- Vẽ inner glow khi button được selected
- Vẽ text căn giữa

**Ưu tiên trạng thái:** SELECTED > HOVER > NORMAL

```java
button.render(gc);
```

### contains(double mouseX, double mouseY)
Kiểm tra xem một điểm (tọa độ chuột) có nằm trong bounds của button không.

**Tham số:**
- **mouseX**: Tọa độ X của chuột
- **mouseY**: Tọa độ Y của chuột

**Trả về:** `true` nếu điểm nằm trong button

```java
if (button.contains(mouseX, mouseY)) {
    button.setHovered(true);
}
```

### click()
Thực hiện action (Runnable) khi button được click. Phương thức này sẽ gọi callback `onClick` nếu nó không null.

```java
button.click(); // Thực thi callback
```

### loadFont()
Phương thức private để tải font chữ tùy chỉnh (`generation.ttf`, size 20). Nếu không tải được, sẽ sử dụng font mặc định `Monospaced` size 18.

## Getters và Setters

### Getters
- `boolean isHovered()`: Lấy trạng thái hover
- `boolean isSelected()`: Lấy trạng thái selected
- `String getText()`: Lấy text của button
- `double getX()`: Lấy tọa độ X
- `double getY()`: Lấy tọa độ Y
- `double getWidth()`: Lấy chiều rộng
- `double getHeight()`: Lấy chiều cao

### Setters
- `void setHovered(boolean hovered)`: Đặt trạng thái hover
- `void setSelected(boolean selected)`: Đặt trạng thái selected
- `void setText(String text)`: Đặt text mới cho button

## Cách sử dụng

### Ví dụ cơ bản
```java
// Tạo button
Button startButton = new Button(300, 400, 200, 50, "START GAME", () -> {
    gameManager.startGame();
});

// Trong vòng lặp render
startButton.render(gc);

// Xử lý sự kiện chuột di chuyển
canvas.setOnMouseMoved(event -> {
    double mouseX = event.getX();
    double mouseY = event.getY();
    
    if (startButton.contains(mouseX, mouseY)) {
        startButton.setHovered(true);
    } else {
        startButton.setHovered(false);
    }
});

// Xử lý sự kiện click
canvas.setOnMouseClicked(event -> {
    if (startButton.contains(event.getX(), event.getY())) {
        startButton.click();
    }
});
```

### Ví dụ với nhiều buttons
```java
List<Button> buttons = new ArrayList<>();
buttons.add(new Button(300, 300, 200, 50, "PLAY", () -> {}));
buttons.add(new Button(300, 370, 200, 50, "OPTIONS", () -> {}));
buttons.add(new Button(300, 440, 200, 50, "EXIT", () -> {}));

// Render tất cả
for (Button btn : buttons) {
    btn.render(gc);
}

// Xử lý hover
for (Button btn : buttons) {
    btn.setHovered(btn.contains(mouseX, mouseY));
}
```

## Thiết kế UI

Button được thiết kế với:
- **Bo góc mượt mà**: Corner radius 8px
- **Hiệu ứng shadow/glow**: Khi hover hoặc selected
- **Viền rõ ràng**: 3px border với màu sắc phân biệt trạng thái
- **Inner glow**: Viền sáng bên trong khi selected
- **Màu sắc phù hợp**: Gradient từ tối đến sáng theo trạng thái
- **Font chữ tùy chỉnh**: Generation font hoặc Monospaced làm dự phòng

## Dependencies
- `javafx.scene.canvas.GraphicsContext`: Để vẽ UI
- `javafx.scene.paint.Color`: Để quản lý màu sắc
- `javafx.scene.text.Font`: Để quản lý font chữ
- `javafx.scene.text.TextAlignment`: Để căn chỉnh text
- `Utils.AssetLoader`: Để tải font tùy chỉnh
