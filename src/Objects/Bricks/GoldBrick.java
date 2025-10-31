package Objects.Bricks;

public class GoldBrick extends Brick{
    public GoldBrick(double x, double y, double width, double height) {
        super(x, y, width, height, BrickType.GOLD.getHitPoints());
    }

    @Override
    public void takeHit() {
        // Gold bricks are indestructible; do nothing on hit
    }

    @Override
    public void update() {
        // GoldBrick does not have special behavior in update
    }

    @Override
    public BrickType getBrickType() {
        return BrickType.GOLD;
    }
}
