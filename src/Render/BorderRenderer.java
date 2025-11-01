package Render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import Utils.SpriteProvider;
import Utils.Constants;

/**
 * Lớp chịu trách nhiệm render (vẽ) các thành phần viền (border) xung quanh khu vực chính của trò chơi.
 * Nó sử dụng các sprite đã được tải sẵn từ {@link SpriteProvider} và vẽ chúng lên {@link GraphicsContext}.
 */
public class BorderRenderer {
    // Đối tượng cung cấp các hình ảnh (sprites) cần thiết cho việc render
    private final SpriteProvider sprites;
    // Context đồ họa của Canvas, dùng để thực hiện các thao tác vẽ
    private final GraphicsContext gc;

    /**
     * Constructor khởi tạo BorderRenderer.
     *
     * @param gc Context đồ họa để vẽ.
     * @param sprites Đối tượng cung cấp các sprite (hình ảnh) viền.
     */
    public BorderRenderer(GraphicsContext gc, SpriteProvider sprites) {
        this.gc = gc;
        this.sprites = sprites;
    }

    /**
     * Thực hiện vẽ toàn bộ các cạnh viền: trên, trái và phải.
     */
    public void render() {
        drawTopEdge(gc);
        drawLeftEdge(gc);
        drawRightEdge(gc);
    }

    /**
     * Vẽ cạnh viền phía trên.
     *
     * @param gc Context đồ họa.
     */
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

    /**
     * Vẽ cạnh viền phía bên trái.
     *
     * @param gc Context đồ họa.
     */
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

    /**
     * Vẽ cạnh viền phía bên phải.
     *
     * @param gc Context đồ họa.
     */
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
}