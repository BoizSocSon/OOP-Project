package Engine;

import Objects.Bricks.BrickType;
import Rounds.*;
import Objects.Bricks.Brick;
import java.util.ArrayList;
import java.util.List;

public class RoundsManager {
    private final List<RoundBase> rounds;
    private int currentRoundIndex;
    private RoundBase currentRound;
    private List<Brick> currentBricks;

    public RoundsManager() {
        this.rounds = new ArrayList<>();
        this.currentRoundIndex = 0;
        this.currentBricks = new ArrayList<>();

        initializeRounds();
    }

    /**
     * Initializes all rounds in the game.
     */
    private void initializeRounds() {
        rounds.add(new Round1());
        rounds.add(new Round2());
        rounds.add(new Round3());
        rounds.add(new Round4());
    }
    public List<Brick> loadRound(int roundNumber) {
        if (roundNumber < 0 || roundNumber >= rounds.size()) {
            throw new IllegalArgumentException("Invalid round number: " + roundNumber);
        }

        currentRoundIndex = roundNumber;
        currentRound = rounds.get(currentRoundIndex);
        currentBricks.clear();
        currentBricks = currentRound.createBricks();

        return currentBricks;
    }

    public List<Brick> loadFirstRound() {
        return loadRound(0);
    }

    public boolean isRoundComplete() {
        if (currentBricks.isEmpty()) {
            return false;
        }

        for (Brick brick : currentBricks) {
            if (brick.isAlive() && !(brick.getBrickType() == BrickType.GOLD)) {
                return false;
            }
        }
        return true;
    }

    public boolean nextRound() {
        int nextRoundIndex = currentRoundIndex + 1;
        if (nextRoundIndex >= rounds.size()) {
            throw new IllegalStateException("No more rounds available.");
        }

        loadRound(nextRoundIndex);
        return true;
    }

    public boolean hasNextRound() {
        return currentRoundIndex + 1 < rounds.size();
    }

    public int getCurrentRoundNumber() {
        return currentRoundIndex + 1;
    }

    public String getCurrentRoundName() {
        if (currentRound == null) {
            return "Unknown";
        }
        return currentRound.getRoundName();
    }

    public List<Brick> getCurrentBricks() {
        return currentBricks;
    }

    public int getRemainingBrickCount() {
        int count = 0;
        for (Brick brick : currentBricks) {
            if (brick.isAlive()) {
                count++;
            }
        }
        return count;
    }

    public void reset() {
        currentRoundIndex = 0;
        currentBricks.clear();
        loadFirstRound();
    }

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
