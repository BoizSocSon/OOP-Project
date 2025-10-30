package Rounds;

import Objects.Bricks.*;
import Utils.Constants;

//import Audio.MusicTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Represents Round 4 of the game, titled "Ultimate Challenge".</p>
 * <p>This round features a challenging 10x13 full grid layout with a mix of
 * Normal, Silver, and Gold bricks placed based on an alternating pattern
 * derived from the brick's row and column position.</p>
 *
 * @author (Your Name Here or Original Author)
 * @version 1.0
 */
public class Round4 extends RoundBase {

    /**
     * Constructs Round 4 with its predefined number and name.
     */
    public Round4() {
        super(4, "Ultimate Challenge");
    }

    /**
     * Creates and returns the specific brick layout for Round 4.
     * The layout is a 10x13 grid centered horizontally, featuring a complex
     * alternating pattern of Normal, Silver, and Gold bricks.
     *
     * @return A List of Brick objects representing the round's layout.
     */
    @Override
    public List<Brick> createBricks() {
        // Initialize the list to store all the bricks for the round
        List<Brick> bricks = new ArrayList<>();

        // Retrieve dimensions and spacing from the global Constants class for easy configuration
        double brickW = Constants.Bricks.BRICK_WIDTH;
        double brickH = Constants.Bricks.BRICK_HEIGHT;
        double hSpacing = Constants.Bricks.BRICK_H_SPACING;
        double vSpacing = Constants.Bricks.BRICK_V_SPACING;

        // Calculate the starting Y-coordinate.
        // Place bricks relative to the play area top so they appear below the UI/bar
        double startY = Constants.PlayArea.PLAY_AREA_Y + Constants.Bricks.BRICK_START_Y / 2.0;

        // Full grid layout with mixed bricks
        int rows = 10; // Define the total number of rows in the grid
        int cols = 13; // Define the total number of columns in the grid

        // Center the grid horizontally
        // 1. Calculate the total width occupied by all bricks and their horizontal spaces
        double totalWidth = cols * brickW + (cols - 1) * hSpacing;
        // 2. Calculate the starting X-coordinate to center the grid within the play area
        double startX = Constants.PlayArea.PLAY_AREA_X + (playAreaWidth - totalWidth) / 2.0;

        // Loop through each row (r) and column (c) to place a brick
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Calculate the precise X and Y coordinates for the current brick
                double x = startX + c * (brickW + hSpacing);
                double y = startY + r * (brickH + vSpacing);

                // Determine the brick type based on its grid position (r + c)
                // Alternate between Normal, Silver, and Gold bricks

                // Gold Brick: Placed when (row + col) is a multiple of 7 (least frequent/highest value)
                if ((r + c) % 7 == 0) {
                    bricks.add(new GoldBrick(x, y, brickW, brickH));
                }
                // Silver Brick: Placed when (row + col) is a multiple of 3, but NOT 7 (more frequent)
                else if ((r + c) % 3 == 0) {
                    bricks.add(new SilverBrick(x, y, brickW, brickH));
                }
                // Normal Brick: For all remaining positions (most frequent)
                else {
                    // Cycle through colors for normal bricks
                    // The modulo operation ensures the index stays within the valid range of BrickType enum.
                    // (BrickType.values().length - 2) is used to exclude potential specific types (e.g., Silver/Gold might be the last two)
                    // or just to limit the color palette used for NormalBricks.
                    BrickType color = BrickType.values()[(r + c) % (BrickType.values().length - 2)];
                    bricks.add(new NormalBrick(x, y, brickW, brickH, color));
                }
            }
        }

        return bricks;
    }
}