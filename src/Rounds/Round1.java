package Rounds;

import Objects.Bricks.*;
import Utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class Round1 extends RoundBase{

    public Round1() {
        super(1, "Beginner's Challenge");
    }

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

        for (int r = 0; r < rows; r++) {
            BrickType rowColor = colors[r % colors.length];

            for (int c = 0; c < cols; c++) {
                double x = startX + c * (brickW + hSpacing);
                double y = startY + r * (brickH + vSpacing);

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
