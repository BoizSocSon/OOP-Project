package Objects.GameEntities;

public enum PaddleState {
    NORMAL("paddle", 1, false),
    WIDE("paddle_wide", 9, false),                 // Transition animation (one-shot)
    WIDE_PULSATE("paddle_wide_pulsate", 4, true),  // Warning animation (loop)
    LASER("paddle_laser", 16, false),              // Transition animation (one-shot)
    LASER_PULSATE("paddle_laser_pulsate", 4, true), // Warning animation (loop)
    PULSATE("paddle_pulsate", 4, true),            // Warning animation (loop)
    MATERIALIZE("paddle_materialize", 15, false),  // Spawn animation (one-shot)
    EXPLODE("paddle_explode", 8, false);           // Death animation (one-shot)

    private final String paddlePrefix;
    private final int frameCount;
    private final boolean shouldLoop;

    PaddleState(String paddlePrefix, int frameCount, boolean shouldLoop) {
        this.paddlePrefix = paddlePrefix;
        this.frameCount = frameCount;
        this.shouldLoop = shouldLoop;
    }

    public String getPaddlePrefix() {
        return paddlePrefix;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public boolean shouldLoop() {
        return shouldLoop;
    }
}
