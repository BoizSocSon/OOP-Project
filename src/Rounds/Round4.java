package Rounds;

import Objects.Bricks.*;
import Utils.Constants;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Round4 đại diện cho Cấp độ 4 (Cấp độ thử thách cao nhất).
 * Nó định nghĩa bố cục gạch ngẫu nhiên có trật tự, sử dụng các phép tính modulo
 * để tạo ra sự phân bố phức tạp giữa các loại gạch Normal, Silver và Gold.
 */
public class Round4 extends RoundBase{
    /**
     * Khởi tạo cấp độ 4 với tên "Ultimate Challenge".
     */
    public Round4() {
        super(4, "Ultimate Challenge");
    }

    /**
     * Phương thức định nghĩa bố cục và loại gạch cho Round 4.
     * Tạo một lưới gạch 13x10, với sự phân bố phức tạp giữa ba loại gạch
     * dựa trên tổng chỉ số hàng và cột (r + c).
     *
     * @return Danh sách các đối tượng Brick.
     */
    @Override
    public List<Brick> createBricks() {
        List<Brick> bricks = new ArrayList<>();

        // --- 1. Thiết lập thông số và vị trí Y bắt đầu ---
        double brickW = Constants.Bricks.BRICK_WIDTH;
        double brickH = Constants.Bricks.BRICK_HEIGHT;
        double hSpacing = Constants.Bricks.BRICK_H_SPACING;
        double vSpacing = Constants.Bricks.BRICK_V_SPACING;
        double startY = Constants.PlayArea.PLAY_AREA_Y + Constants.Bricks.BRICK_START_Y / 2.0;

        // Kích thước lưới cố định
        int rows = 10;
        int cols = 13;

        // --- 2. Tính toán vị trí X bắt đầu để căn giữa lưới gạch ---
        double totalWidth = cols * brickW + (cols - 1) * hSpacing;
        double startX = Constants.PlayArea.PLAY_AREA_X + (playAreaWidth - totalWidth) / 2.0;

        // --- 3. Tạo gạch theo lưới với logic phân bố modulo ---
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double x = startX + c * (brickW + hSpacing);
                double y = startY + r * (brickH + vSpacing);

                // Logic phân loại gạch dựa trên tổng chỉ số (r + c):

                // Nếu (r + c) chia hết cho 7: Tạo Gold Brick (hiếm và khó phá nhất)
                if ((r + c) % 7 == 0) {
                    bricks.add(new GoldBrick(x, y, brickW, brickH));
                }
                // Nếu (r + c) chia hết cho 3: Tạo Silver Brick (cần nhiều hit hơn)
                else if ((r + c) % 3 == 0) {
                    bricks.add(new SilverBrick(x, y, brickW, brickH));
                }
                // Trường hợp còn lại: Tạo Normal Brick với màu sắc luân phiên
                else {
                    // Lấy màu sắc bằng cách sử dụng modulo trên chỉ số BrickType
                    // Trừ 2 vì cần loại bỏ 2 loại cuối cùng (Gold và Silver) khỏi chu kỳ màu
                    BrickType color = BrickType.values()[(r + c) % (BrickType.values().length - 2)];
                    bricks.add(new NormalBrick(x, y, brickW, brickH, color));
                }
            }
        }

        return bricks;
    }
}