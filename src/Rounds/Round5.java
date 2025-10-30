package Rounds;

import Objects.Bricks.*;
import Utils.Constants;

//import Audio.MusicTrack;

import java.util.ArrayList;
import java.util.List;

public class Round5 extends RoundBase {

    public Round5() {
        super(5, "Legendary Spiral");
    }

    @Override
    public List<Brick> createBricks() {
        List<Brick> bricks = new ArrayList<>();

        double brickW = Constants.Bricks.BRICK_WIDTH;
        double brickH = Constants.Bricks.BRICK_HEIGHT;
        double hSpacing = Constants.Bricks.BRICK_H_SPACING;
        double vSpacing = Constants.Bricks.BRICK_V_SPACING;

        int rows = 11;
        int cols = 13;

        double totalWidth = cols * brickW + (cols - 1) * hSpacing;
        double startX = Constants.PlayArea.PLAY_AREA_X + (playAreaWidth - totalWidth) / 2.0;
        double startY = Constants.PlayArea.PLAY_AREA_Y + Constants.Bricks.BRICK_START_Y / 2.0;

        BrickType[] colors = {
                BrickType.RED,
                BrickType.BLUE,
                BrickType.GREEN,
                BrickType.YELLOW,
                BrickType.ORANGE,
                BrickType.CYAN,
                BrickType.PINK
        };

        // Tạo layout xoắn ốc
        int top = 0, bottom = rows - 1;
        int left = 0, right = cols - 1;
        int count = 0;

        while (top <= bottom && left <= right) {
            for (int c = left; c <= right; c++) {
                addBrick(bricks, top, c, startX, startY, brickW, brickH, hSpacing, vSpacing, colors, count++);
            }
            top++;

            for (int r = top; r <= bottom; r++) {
                addBrick(bricks, r, right, startX, startY, brickW, brickH, hSpacing, vSpacing, colors, count++);
            }
            right--;

            if (top <= bottom) {
                for (int c = right; c >= left; c--) {
                    addBrick(bricks, bottom, c, startX, startY, brickW, brickH, hSpacing, vSpacing, colors, count++);
                }
                bottom--;
            }

            if (left <= right) {
                for (int r = bottom; r >= top; r--) {
                    addBrick(bricks, r, left, startX, startY, brickW, brickH, hSpacing, vSpacing, colors, count++);
                }
                left++;
            }
        }

        return bricks;
    }

    private void addBrick(List<Brick> bricks, int r, int c, double startX, double startY,
                          double brickW, double brickH, double hSpacing, double vSpacing,
                          BrickType[] colors, int index) {

        double x = startX + c * (brickW + hSpacing);
        double y = startY + r * (brickH + vSpacing);

        // Quy tắc tạo gạch đa dạng
        if (index % 11 == 0) {
            bricks.add(new GoldBrick(x, y, brickW, brickH));
        } else if (index % 7 == 0) {
            bricks.add(new SilverBrick(x, y, brickW, brickH));
        } else if (index % 5 == 0) {
            bricks.add(new ExplosiveBrick(x, y, brickW, brickH)); // Gạch nổ (nếu game có)
        } else {
            BrickType color = colors[index % colors.length];
            bricks.add(new NormalBrick(x, y, brickW, brickH, color));
        }
    }

//    @Override
//    public MusicTrack getMusicTrack() {
//        return MusicTrack.ROUND_5;
//    }
}
