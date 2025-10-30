package Rounds;

import Objects.Bricks.Brick;
import Utils.Constants;
import java.util.List;

public abstract class RoundBase {
    protected int roundNumber;
    protected String roundName;
    protected double playAreaWidth;
    protected double playAreaHeight;

    public RoundBase(int roundNumber, String roundName) {
        this.roundNumber = roundNumber;
        this.roundName = roundName;
        this.playAreaWidth = Constants.PlayArea.PLAY_AREA_WIDTH;
        this.playAreaHeight = Constants.PlayArea.PLAY_AREA_HEIGHT;
    }

    public abstract List<Brick> createBricks();

//    public abstract MusicTrack getMusicTrack();

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getRoundName() {
        return roundName;
    }

    public int getTotalBrickCount() {
        return createBricks().size();
    }
}
