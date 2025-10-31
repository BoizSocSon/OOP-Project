package Rounds;

import Objects.Bricks.*;
import Utils.Constants;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Round3 đại diện cho Cấp độ 3 của trò chơi.
 * Nó định nghĩa bố cục gạch phức tạp theo hình thoi (Diamond Challenge),
 * bao gồm gạch Normal, Silver và Gold (bất khả xâm phạm).
 */
public class Round3 extends RoundBase{

    /**
     * Khởi tạo cấp độ 3 với tên "Diamond Challenge".
     */
    public Round3() {
        super(3, "Diamond Challenge");
    }

    /**
     * Phương thức định nghĩa bố cục và loại gạch cho Round 3.
     * Sử dụng một mảng 2D (layout) để xác định vị trí và loại gạch.
     *
     * @return Danh sách các đối tượng Brick.
     */
    @Override
    public List<Brick> createBricks() {
        List<Brick> bricks = new ArrayList<>();

        double brickW = Constants.Bricks.BRICK_WIDTH;
        double brickH = Constants.Bricks.BRICK_HEIGHT;
        double hSpacing = Constants.Bricks.BRICK_H_SPACING;
        double vSpacing = Constants.Bricks.BRICK_V_SPACING;

        // Vị trí Y bắt đầu, đặt gạch ở khu vực chơi
        double startY = Constants.PlayArea.PLAY_AREA_Y + Constants.Bricks.BRICK_START_Y / 2.0;

        // --- 1. Ma trận bố cục hình thoi (Diamond layout) ---
        // 0=Trống, 1-8=Normal, 9=Gold (bất hoại), 10=Silver (cứng)
        int[][] layout = {
                {0,0,0,0,0,0,9,0,0,0,0,0,0},        // Row 0: 1 Gold
                {0,0,0,0,0,1,2,1,0,0,0,0,0},
                {0,0,0,0,3,4,10,4,3,0,0,0,0},       // Row 2: 1 Silver
                {0,0,0,5,6,7,8,7,6,5,0,0,0},
                {0,0,2,3,4,5,9,5,4,3,2,0,0},        // Row 4: 1 Gold
                {0,1,2,3,10,5,6,5,10,3,2,1,0},      // Row 5: 2 Silver
                {7,8,1,2,3,4,5,4,3,2,1,8,7},        // Row 6: Hàng đầy đủ
                {0,6,7,8,1,10,2,10,1,8,7,6,0},      // Row 7: 2 Silver
                {0,0,5,6,7,8,9,8,7,6,5,0,0},        // Row 8: 1 Gold
                {0,0,0,4,5,6,7,6,5,4,0,0,0},
                {0,0,0,0,3,10,4,10,3,0,0,0,0},      // Row 10: 2 Silver
                {0,0,0,0,0,2,1,2,0,0,0,0,0},
                {0,0,0,0,0,0,9,0,0,0,0,0,0}         // Row 12: 1 Gold
        };

        int rows = layout.length;
        int cols = layout[0].length;

        // --- 2. Căn giữa lưới gạch theo chiều ngang ---
        double totalWidth = cols * brickW + (cols - 1) * hSpacing;
        double startX = Constants.PlayArea.PLAY_AREA_X + (playAreaWidth - totalWidth) / 2.0;

        // --- 3. Ánh xạ các số (1-8) sang màu sắc Normal Brick ---
        BrickType[] colors = {
                BrickType.RED, BrickType.BLUE, BrickType.GREEN, BrickType.YELLOW,
                BrickType.ORANGE, BrickType.CYAN, BrickType.PINK, BrickType.WHITE
        };

        // --- 4. Duyệt qua layout và tạo đối tượng Brick ---
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int brickType = layout[r][c];

                if (brickType == 0) {
                    continue; // Bỏ qua ô trống
                }

                // Tính toán tọa độ X, Y
                double x = startX + c * (brickW + hSpacing);
                double y = startY + r * (brickH + vSpacing);

                if (brickType == 9) {
                    // Loại 9: Gold Brick (Indestructible - Bất khả xâm phạm)
                    bricks.add(new GoldBrick(x, y, brickW, brickH));
                } else if (brickType == 10) {
                    // Loại 10: Silver Brick (Cần nhiều hơn 1 hit)
                    bricks.add(new SilverBrick(x, y, brickW, brickH));
                } else {
                    // Loại 1-8: Normal Brick (Lấy màu từ mảng colors)
                    BrickType color = colors[(brickType - 1) % colors.length];
                    bricks.add(new NormalBrick(x, y, brickW, brickH, color));
                }
            }
        }

        return bricks;
    }
}