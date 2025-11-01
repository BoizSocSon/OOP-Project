package Rounds;

import Objects.Bricks.*;
import Utils.Constants;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Round1 đại diện cho Cấp độ 1 của trò chơi.
 * Nó định nghĩa bố cục lưới gạch cơ bản (Beginner's Challenge).
 */
public class Round1 extends RoundBase{

    /**
     * Khởi tạo cấp độ 1 với tên "Beginner's Challenge".
     */
    public Round1() {
        super(1, "Beginner's Challenge");
    }

    /**
     * Phương thức định nghĩa bố cục và loại gạch cho Round 1.
     * Tạo ra một lưới gạch Normal Brick 13x4 với màu sắc thay đổi theo hàng.
     *
     * @return Danh sách các đối tượng Brick.
     */
    @Override
    public List<Brick> createBricks() {
        List<Brick> bricks = new ArrayList<>();

        // --- 1. Thiết lập thông số lưới gạch 13x4 ---
        int cols = 13;
        int rows = 4;
        double brickW = Constants.Bricks.BRICK_WIDTH;
        double brickH = Constants.Bricks.BRICK_HEIGHT;
        double hSpacing = Constants.Bricks.BRICK_H_SPACING;
        double vSpacing = Constants.Bricks.BRICK_V_SPACING;

        // --- 2. Tính toán vị trí X bắt đầu để căn giữa lưới gạch ---
        double totalWidth = cols * brickW + (cols - 1) * hSpacing;
        double startX = Constants.PlayArea.PLAY_AREA_X + (playAreaWidth - totalWidth) / 2.0;

        // Vị trí Y bắt đầu
        double startY = Constants.PlayArea.PLAY_AREA_Y + Constants.Bricks.BRICK_START_Y / 2.0;

        // Mẫu màu luân phiên theo hàng
        BrickType[] colors = {
                BrickType.RED,
                BrickType.BLUE,
                BrickType.GREEN,
                BrickType.YELLOW
        };

        // --- 3. Tạo gạch theo hàng và cột ---
        for (int r = 0; r < rows; r++) {
            // Xác định màu cho hàng (sử dụng modulo để lặp lại)
            BrickType rowColor = colors[r % colors.length];

            for (int c = 0; c < cols; c++) {
                // Tính toán tọa độ X, Y cho viên gạch
                double x = startX + c * (brickW + hSpacing);
                double y = startY + r * (brickH + vSpacing);

                // Thêm một Normal Brick (gạch cơ bản)
                bricks.add(new NormalBrick(x, y, brickW, brickH, rowColor));
            }
        }

        return bricks;
    }
}