package Rounds;

import Objects.Bricks.Brick;
import Objects.Bricks.BrickType;
import Objects.Bricks.NormalBrick;
import Audio.MusicTrack;
import Utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Round 1 - Simple grid layout for beginners.
 * 
 * Layout:
 * - 13 columns x 4 rows = 52 bricks
 * - Only normal bricks (red, blue, green, yellow)
 * - Easy difficulty
 * 
 * @author SteveHoang aka BoizSocSon
 */
public class Round1 extends RoundBase {
    
    public Round1(int playAreaWidth, int playAreaHeight) {
        super(1, "Beginner's Challenge", playAreaWidth, playAreaHeight);
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
        double startX = (playAreaWidth - totalWidth) / 2.0;
        double startY = Constants.Bricks.BRICK_START_Y;
        
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
                
                bricks.add(new NormalBrick(rowColor, x, y, brickW, brickH));
            }
        }
        
        return bricks;
    }
    
    @Override
    public MusicTrack getMusicTrack() {
        return MusicTrack.ROUND_1;
    }
}
