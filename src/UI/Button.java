package UI;

import Utils.AssetLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * <p>Component Button cho giao diện Menu.</p>
 * <p>Quản lý việc render, các trạng thái tương tác (hover, selected) và xử lý sự kiện click.</p>
 */
public class Button {
    // Tọa độ và kích thước của button
    private double x, y;
    private double width, height;
    private String text;

    // Trạng thái button
    private boolean isHovered;
    private boolean isSelected;

    // Hành động chạy khi button được click
    private Runnable onClick;

    // Màu sắc cố định cho các trạng thái - Thiết kế mới đẹp hơn
    private static final Color NORMAL_BG_COLOR = Color.rgb(40, 40, 60, 0.85);      // Nền tối trong suốt
    private static final Color HOVER_BG_COLOR = Color.rgb(60, 60, 100, 0.9);       // Nền sáng hơn khi hover
    private static final Color SELECTED_BG_COLOR = Color.rgb(80, 80, 140, 0.95);   // Nền sáng nhất khi selected
    
    private static final Color NORMAL_BORDER = Color.rgb(100, 150, 200);           // Viền xanh dương nhạt
    private static final Color HOVER_BORDER = Color.rgb(150, 200, 255);            // Viền sáng hơn khi hover
    private static final Color SELECTED_BORDER = Color.web("#FFD700");             // Viền vàng khi selected
    
    private static final Color TEXT_COLOR = Color.WHITE;

    private static final double BORDER_WIDTH = 3.0;                                // Viền dày hơn
    private static final double CORNER_RADIUS = 8.0;                               // Bo góc 8px

    private Font font;

    /**
     * Constructor cho Button.
     * @param x Tọa độ X (top-left)
     * @param y Tọa độ Y (top-left)
     * @param width Chiều rộng
     * @param height Chiều cao
     * @param text Text hiển thị trên button
     * @param onClick Callback khi button được click
     */
    public Button(double x, double y, double width, double height, String text, Runnable onClick) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.onClick = onClick;
        this.isHovered = false;
        this.isSelected = false;
        loadFont();
    }

    /**
     * Render (vẽ) button lên canvas với thiết kế bo góc đẹp hơn.
     * @param gc GraphicsContext để vẽ
     */
    public void render(GraphicsContext gc) {
        Color bgColor;
        Color borderColor;

        // Xác định màu nền và viền dựa trên trạng thái (Ưu tiên SELECTED > HOVER > NORMAL)
        if (isSelected) {
            bgColor = SELECTED_BG_COLOR;
            borderColor = SELECTED_BORDER;
        } else if (isHovered) {
            bgColor = HOVER_BG_COLOR;
            borderColor = HOVER_BORDER;
        } else {
            bgColor = NORMAL_BG_COLOR;
            borderColor = NORMAL_BORDER;
        }

        // Vẽ shadow/glow effect nhẹ phía dưới button
        if (isSelected || isHovered) {
            gc.setFill(Color.rgb(100, 150, 255, 0.15));
            gc.fillRoundRect(x - 2, y + 2, width + 4, height + 4, CORNER_RADIUS + 2, CORNER_RADIUS + 2);
        }

        // Vẽ nền button với bo góc
        gc.setFill(bgColor);
        gc.fillRoundRect(x, y, width, height, CORNER_RADIUS, CORNER_RADIUS);

        // Vẽ viền với bo góc
        gc.setStroke(borderColor);
        gc.setLineWidth(BORDER_WIDTH);
        gc.strokeRoundRect(x, y, width, height, CORNER_RADIUS, CORNER_RADIUS);

        // Vẽ inner glow khi selected
        if (isSelected) {
            gc.setStroke(Color.rgb(255, 215, 0, 0.3));
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(x + 3, y + 3, width - 6, height - 6, CORNER_RADIUS - 2, CORNER_RADIUS - 2);
        }

        // Thiết lập font và vẽ text
        gc.setFill(TEXT_COLOR);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(javafx.geometry.VPos.CENTER);
        gc.setFont(font);

        // Vẽ text căn giữa button
        gc.fillText(text, x + width / 2, y + height / 2);
    }

    /**
     * Kiểm tra xem một điểm (tọa độ chuột) có nằm trong bounds của button không.
     * @param mouseX Tọa độ X của chuột
     * @param mouseY Tọa độ Y của chuột
     * @return true nếu điểm nằm trong button
     */
    public boolean contains(double mouseX, double mouseY) {
        // Logic kiểm tra phạm vi (boundary check)
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    /**
     * Thực hiện action (Runnable) khi button được click.
     */
    public void click() {
        if (onClick != null) {
            onClick.run();
        }
    }

    /**
     * Tải font chữ tùy chỉnh cho button. Nếu lỗi, sử dụng font mặc định.
     */
    private void loadFont() {
        try {
            font = AssetLoader.loadFont("generation.ttf", 20);
        } catch (Exception e) {
            // Sử dụng font mặc định nếu không tải được
            font = Font.font("Monospaced", 18);
            System.out.println("CanvasRenderer: Failed to load custom fonts, using default.");
        }
    }

    // Các phương thức Getter/Setter cho trạng thái và thuộc tính

    public boolean isHovered() {
        return isHovered;
    }

    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}