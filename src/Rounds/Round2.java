package Rounds;

import Objects.Bricks.*;
import Utils.Constants;

//import Audio.MusicTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * {@code Round2} là lớp đại diện cho vòng chơi thứ hai trong trò chơi Brick Breaker.
 * <p>
 * Lớp này kế thừa từ {@link RoundBase} và định nghĩa bố cục gạch cho vòng chơi
 * “Silver Challenge”. Trong vòng này, độ khó tăng lên nhờ việc bổ sung thêm
 * một hàng gạch và sự xuất hiện ngẫu nhiên của các viên gạch bạc (SilverBrick),
 * vốn có độ bền cao hơn gạch thường.
 * </p>
 *
 * <p><b>Chức năng chính:</b></p>
 * <ul>
 *     <li>Tạo bố cục gạch gồm 13 cột và 5 hàng (nhiều hơn 1 hàng so với Round 1).</li>
 *     <li>Ngẫu nhiên chèn các viên {@link SilverBrick} với xác suất 30%.</li>
 *     <li>Canh giữa toàn bộ lưới trong khu vực chơi.</li>
 *     <li>Giữ nguyên logic phối màu theo hàng, đồng thời tăng độ đa dạng với 5 màu.</li>
 * </ul>
 *
 * @author
 * @version 1.0
 */
public class Round2 extends RoundBase {

    /**
     * Khởi tạo đối tượng {@code Round2}.
     * <p>
     * Gọi constructor của lớp cha {@link RoundBase} với số vòng là 2
     * và tiêu đề là “Silver Challenge”.
     * </p>
     */
    public Round2() {
        super(2, "Silver Challenge");
    }

    /**
     * Tạo danh sách các viên gạch (bricks) cho vòng 2.
     * <p>
     * Phương thức này tạo lưới gạch 13 cột × 5 hàng, với vị trí canh giữa theo chiều ngang
     * trong khu vực chơi. Các viên gạch được tô màu theo hàng và có 30% xác suất
     * được thay thế bằng {@link SilverBrick}, giúp tăng độ khó của màn chơi.
     * </p>
     *
     * @return Danh sách {@link Brick} chứa toàn bộ các viên gạch được tạo cho vòng 2.
     */
    @Override
    public List<Brick> createBricks() {
        List<Brick> bricks = new ArrayList<>();
        Random rnd = new Random();

        // Grid parameters
        int cols = 13;
        int rows = 5; // One more row than Round 1
        double brickW = Constants.Bricks.BRICK_WIDTH;
        double brickH = Constants.Bricks.BRICK_HEIGHT;
        double hSpacing = Constants.Bricks.BRICK_H_SPACING;
        double vSpacing = Constants.Bricks.BRICK_V_SPACING;

        // Center the grid
        double totalWidth = cols * brickW + (cols - 1) * hSpacing;
        double startX = Constants.PlayArea.PLAY_AREA_X + (playAreaWidth - totalWidth) / 2.0;
        // Place bricks relative to the play area top so they appear below the UI/bar
        double startY = Constants.PlayArea.PLAY_AREA_Y + Constants.Bricks.BRICK_START_Y / 2.0;

        // Color pattern
        BrickType[] colors = {
                BrickType.RED,
                BrickType.BLUE,
                BrickType.GREEN,
                BrickType.YELLOW,
                BrickType.ORANGE
        };

        // Lặp qua từng hàng và cột để tạo gạch
        for (int r = 0; r < rows; r++) {
            BrickType rowColor = colors[r % colors.length]; // Màu theo hàng

            for (int c = 0; c < cols; c++) {
                double x = startX + c * (brickW + hSpacing);
                double y = startY + r * (brickH + vSpacing);

                // 30% chance for silver brick
                if (rnd.nextDouble() < 0.3) {
                    bricks.add(new SilverBrick(x, y, brickW, brickH));
                } else {
                    bricks.add(new NormalBrick(x, y, brickW, brickH, rowColor));
                }
            }
        }

        return bricks;
    }

//    @Override
//    public MusicTrack getMusicTrack() {
//        return MusicTrack.ROUND_2;
//    }
}
