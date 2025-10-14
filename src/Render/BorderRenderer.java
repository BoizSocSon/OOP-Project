package Render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import Utils.AssetLoader;

/**
 * BorderRenderer - Vẽ đường viền/mép sân chơi bằng các sprite lát gạch (tiled).
 *
 * Trách nhiệm:
 * - Vẽ đường viền trang trí xung quanh khu vực chơi
 * - Lát gạch sprite mép theo phương ngang (trên) và phương dọc (trái/phải)
 * - Xử lý góc va chạm/chéo nhau (overlap) gọn gàng
 * - Thích ứng với thay đổi kích thước cửa sổ/canvas
 *
 * Bố cục viền:
 * ┌─────────────┐
 * │             │  ← Mép trên: edge_top.png lặp theo chiều ngang
 * │             │  ← Mép trái: edge_left.png lặp theo chiều dọc
 * │  PLAYFIELD  │
 * │             │  ← Mép phải: edge_right.png lặp theo chiều dọc
 * └─────────────┘  ← Dưới: không có viền (bóng rơi ra ngoài)
 *
 * Tài nguyên sprite:
 * - edge_top.png: 32x16px (lặp theo chiều ngang)
 * - edge_left.png: 16x32px (lặp theo chiều dọc)
 * - edge_right.png: 16x32px (lặp theo chiều dọc)
 *
 * Triết lý thiết kế:
 * - Thuần trang trí (không phải đối tượng va chạm)
 * - Phong cách arcade cổ điển
 * - Đồng nhất với thẩm mỹ Arkanoid
 */
public class BorderRenderer {

    private final Image edgeTop;   // Sprite mép trên (tile ngang)
    private final Image edgeLeft;  // Sprite mép trái (tile dọc)
    private final Image edgeRight; // Sprite mép phải (tile dọc)

    private final double canvasWidth;  // Kích thước canvas để tính số tile
    private final double canvasHeight; // Kích thước canvas để tính số tile

    /**
     * Tạo BorderRenderer với các sprite mép và kích thước canvas.
     *
     * Các bước trong constructor:
     * 1. Tải các sprite mép từ Resources/Graphics/
     * 2. Lưu kích thước canvas để tính toán lát gạch
     * 3. Kiểm tra việc tải sprite có thành công
     *
     * Vị trí sprite kỳ vọng:
     * - Resources/Graphics/edge_top.png
     * - Resources/Graphics/edge_left.png
     * - Resources/Graphics/edge_right.png
     *
     * Hành vi dự phòng:
     * - Nếu thiếu sprite: ghi cảnh báo, tiếp tục chạy nhưng không vẽ viền
     * - Trò chơi vẫn hoạt động (viền chỉ mang tính trang trí)
     *
     * @param canvasWidth  chiều rộng canvas trò chơi
     * @param canvasHeight chiều cao canvas trò chơi
     */
    public BorderRenderer(double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;   // Lưu kích thước dùng cho phép lặp tile
        this.canvasHeight = canvasHeight; // Lưu kích thước dùng cho phép lặp tile

        // Load edge sprites using static methods
        this.edgeTop = AssetLoader.loadImage("edge_top.png");     // Tải sprite mép trên
        this.edgeLeft = AssetLoader.loadImage("edge_left.png");   // Tải sprite mép trái
        this.edgeRight = AssetLoader.loadImage("edge_right.png"); // Tải sprite mép phải

        // Validate sprites loaded
        if (edgeTop == null || edgeLeft == null || edgeRight == null) {
            System.err.println("BorderRenderer: Warning - Some edge sprites failed to load");
            System.err.println("  edge_top: " + (edgeTop != null ? "OK" : "MISSING"));
            System.err.println("  edge_left: " + (edgeLeft != null ? "OK" : "MISSING"));
            System.err.println("  edge_right: " + (edgeRight != null ? "OK" : "MISSING"));
        }
    }

    /**
     * Vẽ toàn bộ viền (trên, trái, phải) lên canvas.
     *
     * Thứ tự vẽ:
     * 1. Mép trên (lát gạch theo chiều ngang)
     * 2. Mép trái (lát gạch theo chiều dọc)
     * 3. Mép phải (lát gạch theo chiều dọc)
     *
     * Thuật toán lát gạch:
     * - Tính số tile cần thiết (canvasSize / tileSize, làm tròn lên)
     * - Vẽ từng tile theo vị trí bù trừ (offset)
     * - Xử lý tile cuối bị tràn: cắt (crop) phần thừa nếu cần
     *
     * Hiệu năng:
     * - Gọi mỗi khung hình (per frame)
     * - Chi phí thấp (chủ yếu là drawImage)
     * - Không cần pha trộn trong suốt
     *
     * @param gc GraphicsContext để vẽ
     */
    public void render(GraphicsContext gc) {
        if (gc == null) {
            System.err.println("BorderRenderer.render: GraphicsContext is null");
            return;
        }

        renderTopEdge(gc);   // Vẽ mép trên (tile ngang)
        renderLeftEdge(gc);  // Vẽ mép trái  (tile dọc)
        renderRightEdge(gc); // Vẽ mép phải (tile dọc)
    }

    /**
     * Vẽ mép trên bằng cách lát gạch theo chiều ngang.
     *
     * Thuật toán:
     * 1. Lấy bề rộng tile (edge_top.width)
     * 2. Tính số tile: ceil(canvasWidth / tileWidth)
     * 3. Với mỗi tile:
     *    - x = index * tileWidth
     *    - y = 0 (đỉnh canvas)
     *    - Vẽ tile
     * 4. Xử lý tràn: cắt (crop) tile cuối nếu vượt canvas
     *
     * Sơ đồ:
     * [Tile][Tile][Tile][Tile][Tile][Partial]
     *  ↑                                  ↑
     *  x=0                         x=canvasWidth
     *
     * @param gc GraphicsContext
     */
    private void renderTopEdge(GraphicsContext gc) {
        if (edgeTop == null) {
            return; // Không vẽ nếu asset thiếu
        }

        double tileWidth = edgeTop.getWidth();   // Bề rộng 1 tile
        double tileHeight = edgeTop.getHeight(); // Chiều cao 1 tile
        int numTiles = (int) Math.ceil(canvasWidth / tileWidth); // Số tile cần vẽ

        for (int i = 0; i < numTiles; i++) {
            double x = i * tileWidth; // Vị trí tile theo trục X
            double y = 0;             // Mép trên canvas

            // Check if we need to crop the last tile
            if (x + tileWidth > canvasWidth) {
                // Crop to fit remaining space
                double remainingWidth = canvasWidth - x; // Phần còn trống ở cuối
                gc.drawImage(edgeTop,
                        0, 0, remainingWidth, tileHeight,  // Vùng nguồn (cắt)
                        x, y, remainingWidth, tileHeight); // Vùng đích
            } else {
                // Draw full tile
                gc.drawImage(edgeTop, x, y); // Vẽ nguyên tile
            }
        }
    }

    /**
     * Vẽ mép trái bằng cách lát gạch theo chiều dọc.
     *
     * Thuật toán:
     * 1. Lấy chiều cao tile (edge_left.height)
     * 2. Tính số tile: ceil(canvasHeight / tileHeight)
     * 3. Với mỗi tile:
     *    - x = 0 (sát mép trái)
     *    - y = index * tileHeight
     *    - Vẽ tile
     * 4. Xử lý tràn: cắt (crop) tile cuối nếu vượt canvas
     *
     * Bố cục:
     * [Tile]
     * [Tile]
     * [Tile]
     * [Tile]
     * [Partial]
     *
     * @param gc GraphicsContext
     */
    private void renderLeftEdge(GraphicsContext gc) {
        if (edgeLeft == null) {
            return; // Không vẽ nếu asset thiếu
        }

        double tileWidth = edgeLeft.getWidth();     // Bề rộng tile mép trái
        double tileHeight = edgeLeft.getHeight();   // Chiều cao tile mép trái
        int numTiles = (int) Math.ceil(canvasHeight / tileHeight); // Số tile dọc

        for (int i = 0; i < numTiles; i++) {
            double x = 0;                 // Sát mép trái canvas
            double y = i * tileHeight;    // Vị trí tile theo trục Y

            // Check if we need to crop the last tile
            if (y + tileHeight > canvasHeight) {
                // Crop to fit remaining space
                double remainingHeight = canvasHeight - y; // Phần còn trống ở đáy
                gc.drawImage(edgeLeft,
                        0, 0, tileWidth, remainingHeight,  // Vùng nguồn (cắt)
                        x, y, tileWidth, remainingHeight); // Vùng đích
            } else {
                // Draw full tile
                gc.drawImage(edgeLeft, x, y); // Vẽ nguyên tile
            }
        }
    }

    /**
     * Vẽ mép phải bằng cách lát gạch theo chiều dọc.
     *
     * Thuật toán:
     * 1. Lấy chiều cao tile (edge_right.height)
     * 2. Tính số tile: ceil(canvasHeight / tileHeight)
     * 3. Với mỗi tile:
     *    - x = canvasWidth - tileWidth (sát mép phải)
     *    - y = index * tileHeight
     *    - Vẽ tile
     * 4. Xử lý tràn: cắt (crop) tile cuối nếu vượt canvas
     *
     * Bố cục:
     *           [Tile]
     *           [Tile]
     *           [Tile]
     *           [Tile]
     *       [Partial]
     *
     * @param gc GraphicsContext
     */
    private void renderRightEdge(GraphicsContext gc) {
        if (edgeRight == null) {
            return; // Không vẽ nếu asset thiếu
        }

        double tileWidth = edgeRight.getWidth();    // Bề rộng tile mép phải
        double tileHeight = edgeRight.getHeight();  // Chiều cao tile mép phải
        int numTiles = (int) Math.ceil(canvasHeight / tileHeight); // Số tile dọc

        for (int i = 0; i < numTiles; i++) {
            double x = canvasWidth - tileWidth; // Canh sát mép phải
            double y = i * tileHeight;          // Vị trí tile theo trục Y

            // Check if we need to crop the last tile
            if (y + tileHeight > canvasHeight) {
                // Crop to fit remaining space
                double remainingHeight = canvasHeight - y; // Phần còn trống ở đáy
                gc.drawImage(edgeRight,
                        0, 0, tileWidth, remainingHeight,  // Vùng nguồn (cắt)
                        x, y, tileWidth, remainingHeight); // Vùng đích
            } else {
                // Draw full tile
                gc.drawImage(edgeRight, x, y); // Vẽ nguyên tile
            }
        }
    }

    /**
     * Lấy chiều rộng sprite mép trái.
     * Hữu ích để tính offset của vùng chơi (playfield).
     *
     * @return chiều rộng (px), hoặc 0 nếu sprite chưa tải
     */
    public double getLeftEdgeWidth() {
        return edgeLeft != null ? edgeLeft.getWidth() : 0; // Trả 0 nếu asset thiếu
    }

    /**
     * Lấy chiều rộng sprite mép phải.
     * Hữu ích để tính ranh giới vùng chơi (playfield bounds).
     *
     * @return chiều rộng (px), hoặc 0 nếu sprite chưa tải
     */
    public double getRightEdgeWidth() {
        return edgeRight != null ? edgeRight.getWidth() : 0; // Trả 0 nếu asset thiếu
    }

    /**
     * Lấy chiều cao sprite mép trên.
     * Hữu ích để tính offset phần trên của vùng chơi.
     *
     * @return chiều cao (px), hoặc 0 nếu sprite chưa tải
     */
    public double getTopEdgeHeight() {
        return edgeTop != null ? edgeTop.getHeight() : 0; // Trả 0 nếu asset thiếu
    }
}
