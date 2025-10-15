package Engine;

import Rounds.RoundBase;
import Rounds.Round1;
import Rounds.Round2;
import Rounds.Round3;
import Objects.Bricks.Brick;
import Audio.MusicTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * RoundsManager - Manages round progression and round-related logic.
 * 
 * Responsibilities:
 * - Load rounds in sequence
 * - Track current round
 * - Check round completion
 * - Trigger round transitions
 * - Provide round info for HUD
 * 
 * @author SteveHoang aka BoizSocSon
 */
public class RoundsManager {
    private final List<RoundBase> rounds;
    private int currentRoundIndex;
    private RoundBase currentRound;
    private List<Brick> currentBricks;
    private final int playAreaWidth;
    private final int playAreaHeight;
    
    /**
     * Creates a new RoundsManager.
     * @param playAreaWidth Width of play area
     * @param playAreaHeight Height of play area
     */
    public RoundsManager(int playAreaWidth, int playAreaHeight) {
        this.playAreaWidth = playAreaWidth;
        this.playAreaHeight = playAreaHeight;
        this.rounds = new ArrayList<>();
        this.currentRoundIndex = 0;
        this.currentBricks = new ArrayList<>();
        
        initializeRounds();
    }
    
    /**
     * Initializes all rounds in the game.
     */
    private void initializeRounds() {
        rounds.add(new Round1(playAreaWidth, playAreaHeight));
        rounds.add(new Round2(playAreaWidth, playAreaHeight));
        rounds.add(new Round3(playAreaWidth, playAreaHeight));
    }
    
    /**
     * Loads a specific round by number (0-indexed).
     * Clears current bricks and creates new ones from round definition.
     * 
     * @param roundNumber Round index to load (0 = Round1, 1 = Round2, etc.)
     * @return List of bricks for this round
     */
    public List<Brick> loadRound(int roundNumber) {
        if (roundNumber < 0 || roundNumber >= rounds.size()) {
            System.err.printf("RoundsManager: Invalid round number %d%n", roundNumber);
            return currentBricks;
        }
        
        System.out.printf("RoundsManager: Loading Round %d%n", roundNumber + 1);
        
        currentRoundIndex = roundNumber;
        currentRound = rounds.get(roundNumber);
        
        // Clear old bricks
        currentBricks.clear();
        
        // Create new bricks from round definition
        currentBricks = currentRound.createBricks();
        
        System.out.printf("RoundsManager: Round %d loaded with %d bricks%n", 
            roundNumber + 1, currentBricks.size());
        
        // Music change should be handled by StateManager/AudioManager
        // AudioManager.playMusic(currentRound.getMusicTrack());
        
        return currentBricks;
    }
    
    /**
     * Loads the first round (Round 1).
     * @return List of bricks for Round 1
     */
    public List<Brick> loadFirstRound() {
        return loadRound(0);
    }
    
    /**
     * Checks if current round is complete (all bricks destroyed).
     * @return true if no bricks are alive
     */
    public boolean isRoundComplete() {
        if (currentBricks.isEmpty()) {
            return false; // No round loaded yet
        }
        
        // Check if any brick is still alive
        for (Brick brick : currentBricks) {
            if (brick.isAlive()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Advances to the next round.
     * If no more rounds, returns false (game won).
     * 
     * @return true if next round loaded, false if no more rounds
     */
    public boolean nextRound() {
        int nextIndex = currentRoundIndex + 1;
        
        if (nextIndex >= rounds.size()) {
            System.out.println("RoundsManager: No more rounds - game won!");
            return false; // No more rounds - player wins
        }
        
        loadRound(nextIndex);
        return true;
    }
    
    /**
     * Checks if there are more rounds after current one.
     * @return true if more rounds exist
     */
    public boolean hasNextRound() {
        return currentRoundIndex + 1 < rounds.size();
    }
    
    /**
     * Gets current round number (1-indexed for display).
     * @return Current round number (1, 2, 3, etc.)
     */
    public int getCurrentRoundNumber() {
        return currentRoundIndex + 1;
    }
    
    /**
     * Gets current round object.
     * @return Current RoundBase instance
     */
    public RoundBase getCurrentRound() {
        return currentRound;
    }
    
    /**
     * Gets current bricks list.
     * @return List of bricks in current round
     */
    public List<Brick> getCurrentBricks() {
        return currentBricks;
    }
    
    /**
     * Gets total number of rounds in game.
     * @return Total round count
     */
    public int getTotalRounds() {
        return rounds.size();
    }
    
    /**
     * Gets number of remaining bricks in current round.
     * @return Count of alive bricks
     */
    public int getRemainingBrickCount() {
        int count = 0;
        for (Brick brick : currentBricks) {
            if (brick.isAlive()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Gets music track for current round.
     * @return MusicTrack enum value
     */
    public MusicTrack getCurrentMusicTrack() {
        if (currentRound == null) {
            return MusicTrack.MENU;
        }
        return currentRound.getMusicTrack();
    }
    
    /**
     * Resets rounds manager to initial state (Round 1).
     */
    public void reset() {
        currentRoundIndex = 0;
        currentBricks.clear();
        loadFirstRound();
    }
    
    /**
     * Gets round info for HUD display.
     * @return Formatted string with round info
     */
    public String getRoundInfo() {
        if (currentRound == null) {
            return "No Round Loaded";
        }
        
        return String.format("Round %d: %s (%d/%d bricks)", 
            getCurrentRoundNumber(),
            currentRound.getRoundName(),
            getRemainingBrickCount(),
            currentBricks.size()
        );
    }
}
