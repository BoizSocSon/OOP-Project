package Rounds;

import Objects.Bricks.*;
import Utils.Constants;

//import Audio.MusicTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code Round3} là lớp đại diện cho vòng chơi thứ ba trong trò chơi Brick Breaker.
 * <p>
 * Vòng này có tên là “Diamond Challenge” và được thiết kế đặc biệt với bố cục hình kim cương,
 * sử dụng kết hợp nhiều loại gạch khác nhau như:
 * <ul>
 *     <li>{@link NormalBrick} – gạch thông thường, có nhiều màu sắc khác nhau.</li>
 *     <li>{@link SilverBrick} – gạch bạc, cần nhiều lần đánh trúng để phá.</li>
 *     <li>{@link GoldBrick} – gạch vàng, không thể phá hủy (indestructible).</li>
 * </ul>
 * </p>
 *
 * <p><b>Đặc điểm nổi bật:</b></p>
 * <ul>
 *     <li>Bố cục gạch hình kim cương được xác định thủ công qua mảng hai chiều (layout matrix).</li>
 *     <li>Tăng độ khó rõ rệt so với các vòng trước nhờ sự kết hợp của gạch vàng và bạc.</li>
 *     <li>Giữ nguyên việc canh giữa lưới gạch trong khu vực chơi (play area).</li>
 * </ul>
 *
 * @author
 * @version 1.0
 */
public class Round3 extends RoundBase {

    /**
     * Khởi tạo đối tượng {@code Round3}.
     * <p>
     * Gọi constructor của lớp cha {@link RoundBase} với số vòng là 3
     * và tiêu đề là “Diamond Challenge”.
     * </p>
     */
    public Round3() {
        super(3, "Diamond Challenge");
    }

    /**
     * Tạo danh sách các viên gạch (bricks) cho vòng 3.
     * <p>
     * Bố cục gạch được xác định dựa trên một mảng hai chiều, trong đó:
     * <ul>
     *     <li><b>0</b>: vị trí trống (không có gạch).</li>
     *     <li><b>1–8</b>: gạch thường, với 8 màu khác nhau.</li>
     *     <li><b>9</b>: gạch vàng (indestructible, không thể phá).</li>
     *     <li><b>10</b>: gạch bạc (SilverBrick, cần nhiều lần đánh để phá).</li>
     * </ul>
     * Các viên gạch được vẽ thành hình kim cương, canh giữa theo chiều ngang
     * và đặt phía dưới thanh giao diện (UI bar).
     * </p>
     *
     * @return Danh sách {@link Brick} chứa toàn bộ các viên gạch được tạo cho vòng 3.
     */
    @Override
    public List<Brick> createBricks() {
        List<Brick> bricks = new ArrayList<>();

        double brickW = Constants.Bricks.BRICK_WIDTH;
        double brickH = Constants.Bricks.BRICK_HEIGHT;
        double hSpacing = Constants.Bricks.BRICK_H_SPACING;
        double vSpacing = Constants.Bricks.BRICK_V_SPACING;
        // Place bricks relative to the play area top so they appear below the UI/bar
        double startY = Constants.PlayArea.PLAY_AREA_Y + Constants.Bricks.BRICK_START_Y / 2.0;

        // Diamond layout pattern (0 = empty, 1-8 = normal colors, 9 = gold, 10 = silver)
        int[][] layout = {
                {0,0,0,0,0,0,9,0,0,0,0,0,0},        // Row 0: 1 gold at center
                {0,0,0,0,0,1,2,1,0,0,0,0,0},        // Row 1: 3 bricks
                {0,0,0,0,3,4,10,4,3,0,0,0,0},       // Row 2: 5 bricks (1 silver)
                {0,0,0,5,6,7,8,7,6,5,0,0,0},        // Row 3: 7 bricks
                {0,0,2,3,4,5,9,5,4,3,2,0,0},        // Row 4: 9 bricks (1 gold)
                {0,1,2,3,10,5,6,5,10,3,2,1,0},      // Row 5: 11 bricks (2 silver)
                {7,8,1,2,3,4,5,4,3,2,1,8,7},        // Row 6: 13 bricks (full row)
                {0,6,7,8,1,10,2,10,1,8,7,6,0},      // Row 7: 11 bricks (2 silver)
                {0,0,5,6,7,8,9,8,7,6,5,0,0},        // Row 8: 9 bricks (1 gold)
                {0,0,0,4,5,6,7,6,5,4,0,0,0},        // Row 9: 7 bricks
                {0,0,0,0,3,10,4,10,3,0,0,0,0},      // Row 10: 5 bricks (2 silver)
                {0,0,0,0,0,2,1,2,0,0,0,0,0},        // Row 11: 3 bricks
                {0,0,0,0,0,0,9,0,0,0,0,0,0}         // Row 12: 1 gold at center
        };

        int rows = layout.length;
        int cols = layout[0].length;

        // Center the grid horizontally
        double totalWidth = cols * brickW + (cols - 1) * hSpacing;
        double startX = Constants.PlayArea.PLAY_AREA_X + (playAreaWidth - totalWidth) / 2.0;

        // Color mapping for normal bricks
        BrickType[] colors = {
                BrickType.RED,      // 1
                BrickType.BLUE,     // 2
                BrickType.GREEN,    // 3
                BrickType.YELLOW,   // 4
                BrickType.ORANGE,   // 5
                BrickType.CYAN,     // 6
                BrickType.PINK,     // 7
                BrickType.WHITE     // 8
        };

        // Create bricks from layout
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int brickType = layout[r][c];

                if (brickType == 0) {
                    continue; // Empty space
                }

                double x = startX + c * (brickW + hSpacing);
                double y = startY + r * (brickH + vSpacing);

                if (brickType == 9) {
                    // Gold brick (indestructible)
                    bricks.add(new GoldBrick(x, y, brickW, brickH));
                } else if (brickType == 10) {
                    // Silver brick (takes 10 hits)
                    bricks.add(new SilverBrick(x, y, brickW, brickH));
                } else {
                    // Normal colored brick
                    BrickType color = colors[(brickType - 1) % colors.length];
                    bricks.add(new NormalBrick(x, y, brickW, brickH, color));
                }
            }
        }

        return bricks;
    }

//    @Override
//    public MusicTrack getMusicTrack() {
//        return MusicTrack.ROUND_3;
//    }
}
