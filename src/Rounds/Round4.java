package Rounds;

import Objects.Bricks.*;
import Utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class Round4 extends RoundBase{
    public Round4() {
        super(4, "Ultimate Challenge");
    }
    @Override
    public List<Brick> createBricks() {
        List<Brick> bricks = new ArrayList<>();

        double brickW = Constants.Bricks.BRICK_WIDTH;
        double brickH = Constants.Bricks.BRICK_HEIGHT;
        double hSpacing = Constants.Bricks.BRICK_H_SPACING;
        double vSpacing = Constants.Bricks.BRICK_V_SPACING;
        // Place bricks relative to the play area top so they appear below the UI/bar
        double startY = Constants.PlayArea.PLAY_AREA_Y + Constants.Bricks.BRICK_START_Y / 2.0;

        // Full grid layout with mixed bricks
        int rows = 10;
        int cols = 13;

        // Center the grid horizontally
        double totalWidth = cols * brickW + (cols - 1) * hSpacing;
        double startX = Constants.PlayArea.PLAY_AREA_X + (playAreaWidth - totalWidth) / 2.0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double x = startX + c * (brickW + hSpacing);
                double y = startY + r * (brickH + vSpacing);

                // Alternate between Normal, Silver, and Gold bricks
                if ((r + c) % 7 == 0) {
                    bricks.add(new GoldBrick(x, y, brickW, brickH));
                } else if ((r + c) % 3 == 0) {
                    bricks.add(new SilverBrick(x, y, brickW, brickH));
                } else {
                    // Cycle through colors for normal bricks
                    BrickType color = BrickType.values()[(r + c) % (BrickType.values().length - 2)];
                    bricks.add(new NormalBrick(x, y, brickW, brickH, color));
                }
            }
        }

        return bricks;
    }
}
