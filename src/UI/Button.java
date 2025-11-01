package UI;

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

    // Màu sắc cố định cho các trạng thái
    private static final Color NORMAL_COLOR = Color.rgb(100, 100, 100);
    private static final Color HOVER_COLOR = Color.rgb(150, 150, 200);
    private static final Color SELECTED_COLOR = Color.rgb(200, 200, 255);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = Color.WHITE;

    private static final double BORDER_WIDTH = 2.0;

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
    }

    /**
     * Render (vẽ) button lên canvas.
     * @param gc GraphicsContext để vẽ
     */
    public void render(GraphicsContext gc) {
        Color bgColor;

        // Xác định màu nền dựa trên trạng thái (Ưu tiên SELECTED > HOVER > NORMAL)
        if (isSelected) {
            bgColor = SELECTED_COLOR;
        } else if (isHovered) {
            bgColor = HOVER_COLOR;
        } else {
            bgColor = NORMAL_COLOR;
        }

        // Vẽ nền button
        gc.setFill(bgColor);
        gc.fillRect(x, y, width, height);

        // Vẽ viền
        gc.setStroke(BORDER_COLOR);
        gc.setLineWidth(BORDER_WIDTH);
        gc.strokeRect(x, y, width, height);

        // Thiết lập font và vẽ text
        gc.setFill(TEXT_COLOR);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(javafx.geometry.VPos.CENTER);
        gc.setFont(Font.font("Courier New", 20));

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