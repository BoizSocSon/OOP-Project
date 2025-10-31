package Rounds;

import Objects.Bricks.*;
import Utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Lớp Round2 đại diện cho Cấp độ 2 của trò chơi.
 * Nó định nghĩa bố cục lưới gạch có sự xuất hiện ngẫu nhiên của gạch Silver (cần 2 hit).
 */
public class Round2 extends RoundBase {
    /**
     * Khởi tạo cấp độ 2 với tên "Silver Challenge".
     */
    public Round2() {
        super(2, "Silver Challenge");
    }

    /**
     * Phương thức định nghĩa bố cục và loại gạch cho Round 2.
     * Tạo một lưới gạch 13x5, với 30% khả năng mỗi viên là Silver Brick.
     *
     * @return Danh sách các đối tượng Brick.
     */
    @Override
    public List<Brick> createBricks() {
        List<Brick> bricks = new ArrayList<>();
        // Khởi tạo đối tượng Random để tạo gạch ngẫu nhiên
        Random rnd = new Random();


        // --- 1. Thiết lập thông số lưới gạch 13x5 ---
        int cols = 13;
        int rows = 5; // Tăng thêm 1 hàng so với Round 1
        double brickW = Constants.Bricks.BRICK_WIDTH;
        double brickH = Constants.Bricks.BRICK_HEIGHT;
        double hSpacing = Constants.Bricks.BRICK_H_SPACING;
        double vSpacing = Constants.Bricks.BRICK_V_SPACING;


        // --- 2. Tính toán vị trí X và Y bắt đầu để căn giữa ---
        double totalWidth = cols * brickW + (cols - 1) * hSpacing;
        double startX = Constants.PlayArea.PLAY_AREA_X + (playAreaWidth - totalWidth) / 2.0;

        double startY = Constants.PlayArea.PLAY_AREA_Y + Constants.Bricks.BRICK_START_Y / 2.0;


        // Mẫu màu luân phiên cho gạch Normal
        BrickType[] colors = {
                BrickType.RED,
                BrickType.BLUE,
                BrickType.GREEN,
                BrickType.YELLOW,
                BrickType.ORANGE
        };

        // --- 3. Tạo gạch theo lưới với xác suất ngẫu nhiên ---
        for (int r = 0; r < rows; r++) {
            // Xác định màu cho hàng gạch Normal
            BrickType rowColor = colors[r % colors.length];

            for (int c = 0; c < cols; c++) {
                // Tính toán tọa độ X, Y
                double x = startX + c * (brickW + hSpacing);
                double y = startY + r * (brickH + vSpacing);

                // 30% khả năng (0.3) tạo Silver Brick (cần 2 hit)
                if (rnd.nextDouble() < 0.3) {
                    bricks.add(new SilverBrick(x, y, brickW, brickH));
                } else {
                    // 70% còn lại tạo Normal Brick
                    bricks.add(new NormalBrick(x, y, brickW, brickH, rowColor));
                }
            }
        }

        return bricks;
    }
}