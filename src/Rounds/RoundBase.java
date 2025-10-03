package Rounds;

public abstract class RoundBase {
    public abstract void startRound();
    public abstract boolean isRoundOver();
    public abstract void update();
    public abstract void render(Render.Renderer renderer);
}
