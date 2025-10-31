package Objects.Bricks;

import Render.Animation;
import Utils.AnimationFactory;
import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;

public class SilverBrick extends Brick{
    private int currentHP;
    private Animation crackAnimation;

    public SilverBrick(double x, double y, double width, double height) {
        super(x, y, width, height, BrickType.SILVER.getHitPoints());
        this.currentHP = BrickType.SILVER.getHitPoints();
        this.crackAnimation = AnimationFactory.createBrickCrackAnimation();
    }

    @Override
    public void takeHit() {
        if (currentHP <= 0) {
            return;
        }

        currentHP--;

        if (currentHP == 1) {
            // Hiển thị hiệu ứng nứt khi còn 1 HP
            crackAnimation.play();
        } else if (currentHP == 0) {
            destroy();
        }
    }

    @Override
    public void update() {
        // Cập nhật hiệu ứng nứt nếu cần
        if (crackAnimation != null && crackAnimation.isPlaying()) {
            crackAnimation.update();
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(new Point(getX(), getY()), getWidth(), getHeight());
    }


    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return currentHP <= 0 || !isAlive();
    }

    @Override
    public BrickType getBrickType() {
        return BrickType.SILVER;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public boolean isCrackAnimationPlaying() {
        return crackAnimation != null && crackAnimation.isPlaying();
    }

    public Animation getCrackAnimation() {
        return crackAnimation;
    }

    public void setCrackAnimation(Animation animation) {
        this.crackAnimation = animation;
    }
}
