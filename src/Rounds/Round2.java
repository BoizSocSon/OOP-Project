package Rounds;

import Objects.Bricks.*;
import Utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Round2 extends RoundBase {
    public Round2() {
        super(2, "Silver Challenge");
    }

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

        for (int r = 0; r < rows; r++) {
            BrickType rowColor = colors[r % colors.length];

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
