package UI;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;

/**
 * Helper class chứa các utility methods cho UI rendering.
 */
public class UIHelper {

    /**
     * Vẽ text căn giữa trong một vùng rectangle.
     * @param gc GraphicsContext để vẽ
     * @param text Text cần vẽ
     * @param x Tọa độ X của vùng
     * @param y Tọa độ Y của vùng
     * @param width Chiều rộng vùng
     * @param height Chiều cao vùng
     * @param font Font chữ
     * @param color Màu chữ
     */
    public static void drawCenteredText(GraphicsContext gc, String text,
                                        double x, double y, double width, double height,
                                        Font font, Color color) {
        gc.setFont(font);
        gc.setFill(color);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(text, x + width / 2, y + height / 2);
    }

    /**
     * Vẽ text căn giữa tại một điểm cụ thể.
     * @param gc GraphicsContext để vẽ
     * @param text Text cần vẽ
     * @param centerX Tọa độ X center
     * @param centerY Tọa độ Y center
     * @param font Font chữ
     * @param color Màu chữ
     */
    public static void drawCenteredText(GraphicsContext gc, String text,
                                        double centerX, double centerY,
                                        Font font, Color color) {
        gc.setFont(font);
        gc.setFill(color);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(text, centerX, centerY);
    }

    /**
     * Vẽ logo game căn giữa.
     * @param gc GraphicsContext để vẽ
     * @param logo Image logo
     * @param centerX Tọa độ X center
     * @param centerY Tọa độ Y center
     * @param width Chiều rộng hiển thị
     * @param height Chiều cao hiển thị
     */
    public static void drawLogo(GraphicsContext gc, Image logo,
                                double centerX, double centerY,
                                double width, double height) {
        if (logo != null) {
            double x = centerX - width / 2;
            double y = centerY - height / 2;
            gc.drawImage(logo, x, y, width, height);
        }
    }

    /**
     * Vẽ text với alignment bên trái.
     * @param gc GraphicsContext để vẽ
     * @param text Text cần vẽ
     * @param x Tọa độ X
     * @param y Tọa độ Y
     * @param font Font chữ
     * @param color Màu chữ
     */
    public static void drawLeftAlignedText(GraphicsContext gc, String text,
                                           double x, double y,
                                           Font font, Color color) {
        gc.setFont(font);
        gc.setFill(color);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.TOP);
        gc.fillText(text, x, y);
    }

    /**
     * Vẽ text với alignment bên phải.
     * @param gc GraphicsContext để vẽ
     * @param text Text cần vẽ
     * @param x Tọa độ X (right edge)
     * @param y Tọa độ Y
     * @param font Font chữ
     * @param color Màu chữ
     */
    public static void drawRightAlignedText(GraphicsContext gc, String text,
                                            double x, double y,
                                            Font font, Color color) {
        gc.setFont(font);
        gc.setFill(color);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setTextBaseline(VPos.TOP);
        gc.fillText(text, x, y);
    }

    /**
     * Vẽ một box với border.
     * @param gc GraphicsContext để vẽ
     * @param x Tọa độ X
     * @param y Tọa độ Y
     * @param width Chiều rộng
     * @param height Chiều cao
     * @param fillColor Màu fill (null nếu không fill)
     * @param borderColor Màu border
     * @param borderWidth Độ rộng border
     */
    public static void drawBox(GraphicsContext gc, double x, double y,
                               double width, double height,
                               Color fillColor, Color borderColor, double borderWidth) {
        if (fillColor != null) {
            gc.setFill(fillColor);
            gc.fillRect(x, y, width, height);
        }

        if (borderColor != null && borderWidth > 0) {
            gc.setStroke(borderColor);
            gc.setLineWidth(borderWidth);
            gc.strokeRect(x, y, width, height);
        }
    }

    /**
     * Vẽ một gradient background.
     * @param gc GraphicsContext để vẽ
     * @param x Tọa độ X
     * @param y Tọa độ Y
     * @param width Chiều rộng
     * @param height Chiều cao
     * @param topColor Màu trên
     * @param bottomColor Màu dưới
     */
    public static void drawGradientBackground(GraphicsContext gc, double x, double y,
                                              double width, double height,
                                              Color topColor, Color bottomColor) {
        // Simple vertical gradient simulation bằng cách vẽ nhiều horizontal lines
        int steps = 100;
        double stepHeight = height / steps;

        for (int i = 0; i < steps; i++) {
            double ratio = (double) i / steps;
            Color interpolated = interpolateColor(topColor, bottomColor, ratio);
            gc.setFill(interpolated);
            gc.fillRect(x, y + i * stepHeight, width, stepHeight + 1); // +1 để tránh gap
        }
    }

    /**
     * Interpolate giữa 2 màu.
     * @param c1 Màu 1
     * @param c2 Màu 2
     * @param ratio Tỉ lệ (0.0 to 1.0)
     * @return Màu interpolated
     */
    private static Color interpolateColor(Color c1, Color c2, double ratio) {
        double red = c1.getRed() + (c2.getRed() - c1.getRed()) * ratio;
        double green = c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio;
        double blue = c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio;
        return new Color(red, green, blue, 1.0);
    }
}
