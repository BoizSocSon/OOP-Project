package Rounds;

import Objects.Bricks.*;
import Utils.Constants;

//import Audio.MusicTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code Round1} là lớp đại diện cho vòng chơi đầu tiên trong trò chơi Brick Breaker.
 * <p>
 * Lớp này kế thừa từ {@link RoundBase} và định nghĩa bố cục các viên gạch (bricks)
 * cho màn chơi “Beginner's Challenge”. Các viên gạch được sắp xếp thành một lưới
 * gồm nhiều hàng và cột, với màu sắc thay đổi theo từng hàng.
 * </p>
 *
 * <p><b>Chức năng chính:</b></p>
 * <ul>
 *     <li>Khởi tạo các viên gạch với kích thước, vị trí và màu sắc xác định.</li>
 *     <li>Canh giữa lưới gạch trong khu vực chơi (play area).</li>
 *     <li>Cung cấp dữ liệu cấu trúc cho {@link RoundBase} để hiển thị màn chơi.</li>
 * </ul>
 *
 * @author
 * @version 1.0
 */
public class Round1 extends RoundBase {

    /**
     * Khởi tạo đối tượng {@code Round1}.
     * <p>
     * Gọi constructor của lớp cha {@link RoundBase} với số vòng là 1
     * và tiêu đề là “Beginner's Challenge”.
     * </p>
     */
    public Round1() {
        super(1, "Beginner's Challenge");
    }

    /**
     * Tạo danh sách các viên gạch (bricks) cho vòng 1.
     * <p>
     * Phương thức này xây dựng một lưới các viên gạch gồm 13 cột và 4 hàng,
     * được canh giữa theo chiều ngang trong khu vực chơi. Mỗi hàng có một màu khác nhau,
     * tuần tự theo thứ tự: đỏ, xanh dương, xanh lá, vàng.
     * </p>
     *
     * @return Danh sách {@link Brick} chứa toàn bộ các viên gạch được tạo cho vòng 1.
     */
    @Override
    public List<Brick> createBricks() {
        List<Brick> bricks = new ArrayList<>();

        // Grid parameters (same as original GameManager demo)
        int cols = 13;
        int rows = 4;
        double brickW = Constants.Bricks.BRICK_WIDTH;
        double brickH = Constants.Bricks.BRICK_HEIGHT;
        double hSpacing = Constants.Bricks.BRICK_H_SPACING;
        double vSpacing = Constants.Bricks.BRICK_V_SPACING;

        // Center the grid
        double totalWidth = cols * brickW + (cols - 1) * hSpacing;
        double startX = Constants.PlayArea.PLAY_AREA_X + (playAreaWidth - totalWidth) / 2.0;
        // BRICK_START_Y is defined relative to the play area — convert to absolute window
        // coordinates by adding the play area top (PLAY_AREA_Y).
        double startY = Constants.PlayArea.PLAY_AREA_Y + Constants.Bricks.BRICK_START_Y / 2.0;

        // Color pattern: cycle through colors by row
        BrickType[] colors = {
                BrickType.RED,
                BrickType.BLUE,
                BrickType.GREEN,
                BrickType.YELLOW
        };

        // Lặp qua từng hàng và cột để tạo và thêm các viên gạch vào danh sách
        for (int r = 0; r < rows; r++) {
            BrickType rowColor = colors[r % colors.length]; // Chọn màu theo hàng

            for (int c = 0; c < cols; c++) {
                double x = startX + c * (brickW + hSpacing);
                double y = startY + r * (brickH + vSpacing);

                // Thêm viên gạch thường (NormalBrick) vào danh sách
                bricks.add(new NormalBrick(x, y, brickW, brickH, rowColor));
            }
        }

        return bricks;
    }

//    @Override
//    public MusicTrack getMusicTrack() {
//        return MusicTrack.ROUND_1;
//    }
}
