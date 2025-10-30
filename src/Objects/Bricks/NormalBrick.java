package Objects.Bricks;

public class NormalBrick extends Brick {
    private final BrickType brickType;

    public NormalBrick(double x, double y, double width, double height, BrickType brickType) {
        super(x, y, width, height, brickType.getHitPoints());
        this.brickType = brickType;
    }

    @Override
    public void update() {
        // NormalBrick không có hành vi đặc biệt trong phương thức update
    }

    @Override
    public BrickType getBrickType() {
        return brickType;
    }

    public int getScoreValue() {
        return brickType.getBaseScore();
    }
}
