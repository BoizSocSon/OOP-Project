package Rounds;

import Objects.Bricks.Brick;
import Audio.MusicTrack;
import java.util.List;

/**
 * Abstract base class for all game rounds/levels.
 *
 * Each round defines:
 * - Brick layout pattern
 * - Background music track
 * - Round metadata (name, number, difficulty)
 *
 * Subclasses implement:
 * - createBricks() - generates brick layout
 * - getMusicTrack() - returns music for this round
 *
 */
public abstract class RoundBase {
    protected int roundNumber;
    protected String roundName;
    protected int playAreaWidth;
    protected int playAreaHeight;

    /**
     * Creates a new round.
     * @param roundNumber Round number (1, 2, 3, etc.)
     * @param roundName Display name for the round
     * @param playAreaWidth Width of play area
     * @param playAreaHeight Height of play area
     */
    public RoundBase(int roundNumber, String roundName, int playAreaWidth, int playAreaHeight) {
        this.roundNumber = roundNumber;
        this.roundName = roundName;
        this.playAreaWidth = playAreaWidth;
        this.playAreaHeight = playAreaHeight;
    }

    /**
     * Creates the brick layout for this round.
     * Each subclass defines its own pattern.
     *
     * @return List of bricks for this round
     */
    public abstract List<Brick> createBricks();

    /**
     * Gets the music track for this round.
     * @return MusicTrack enum value
     */
    public abstract MusicTrack getMusicTrack();

    /**
     * Gets round number.
     * @return Round number (1-indexed)
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * Gets round display name.
     * @return Round name
     */
    public String getRoundName() {
        return roundName;
    }

    /**
     * Gets total number of bricks in this round.
     * Useful for progress tracking.
     * @return Total brick count
     */
    public int getTotalBrickCount() {
        return createBricks().size();
    }
}
