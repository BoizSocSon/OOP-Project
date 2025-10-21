package Rounds;

import Objects.Bricks.Brick;
import Objects.Bricks.BrickType;
import Objects.Bricks.NormalBrick;
import Objects.Bricks.SilverBrick;
import Objects.Bricks.GoldBrick;
import Audio.MusicTrack;
import Utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Round 3 - Diamond pattern with all brick types.
 *
 * Layout:
 * - Diamond/pyramid formation
 * - Mix of normal, silver, and gold bricks
 * - Hard difficulty
 * - Strategic placement of gold bricks (indestructible)
 *
 * Pattern visualization (simplified):
 *         G
 *       1 2 1
 *     3 4 S 4 3
 *   5 6 7 8 7 6 5
 * 2 3 4 5 G 5 4 3 2
 *   ... (symmetric)
 *
 */
public class Round3 extends RoundBase {

    public Round3(int playAreaWidth, int playAreaHeight) {
        super(3, "Diamond Challenge", playAreaWidth, playAreaHeight);
    }

    @Override
    public List<Brick> createBricks() {
        List<Brick> bricks = new ArrayList<>();

        double brickW = Constants.Bricks.BRICK_WIDTH;
        double brickH = Constants.Bricks.BRICK_HEIGHT;
        double hSpacing = Constants.Bricks.BRICK_H_SPACING;
        double vSpacing = Constants.Bricks.BRICK_V_SPACING;
        double startY = Constants.Bricks.BRICK_START_Y;

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
        double startX = (playAreaWidth - totalWidth) / 2.0;

        // Color mapping
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
                    bricks.add(new NormalBrick(color, x, y, brickW, brickH));
                }
            }
        }

        return bricks;
    }

    @Override
    public MusicTrack getMusicTrack() {
        return MusicTrack.ROUND_3;
    }
}
