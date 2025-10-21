package Rounds;

import Objects.Bricks.Brick;
import Objects.Bricks.BrickType;
import Objects.Bricks.NormalBrick;
import Objects.Bricks.SilverBrick;
import Audio.MusicTrack;
import Utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Round 2 - Mixed brick types with increased difficulty.
 *
 * Layout:
 * - 13 columns x 5 rows = 65 bricks
 * - Mix of normal and silver bricks
 * - Medium difficulty
 *
 */
public class Round2 extends RoundBase {

    public Round2(int playAreaWidth, int playAreaHeight) {
        super(2, "Silver Challenge", playAreaWidth, playAreaHeight);
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
        double startX = (playAreaWidth - totalWidth) / 2.0;
        double startY = Constants.Bricks.BRICK_START_Y;

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
                    bricks.add(new NormalBrick(rowColor, x, y, brickW, brickH));
                }
            }
        }

        return bricks;
    }

    @Override
    public MusicTrack getMusicTrack() {
        return MusicTrack.ROUND_2;
    }
}
