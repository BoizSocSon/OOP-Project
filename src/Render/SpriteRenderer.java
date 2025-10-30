package Render;

import Objects.Bricks.Brick;
import Objects.Bricks.GoldBrick;
import Objects.Bricks.NormalBrick;
import Objects.Bricks.SilverBrick;
import Objects.GameEntities.Ball;
import Objects.GameEntities.Laser;
import Objects.GameEntities.Paddle;
import Objects.GameEntities.PaddleState;
import Objects.PowerUps.PowerUp;
import Utils.Constants;
import Utils.SpriteProvider;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class SpriteRenderer {
    private final GraphicsContext gc;
    private final SpriteProvider sprites;

    private Ball ball;
    private Paddle paddle;
    private Brick brick;
    private NormalBrick normalBrick;
    private SilverBrick silverBrick;
    private GoldBrick goldBrick;
    private PowerUp powerUp;

    public SpriteRenderer(GraphicsContext gc, SpriteProvider sprites) {
        this.gc = gc;
        this.sprites = sprites;
    }

    public void drawBall(Ball ball) {
        gc.drawImage(sprites.get("ball.png"), ball.getX(), ball.getY());
    }

    public void drawLaser(Laser laser) {
        gc.drawImage(sprites.get("laser_bullet.png"), laser.getX(), laser.getY());
    }

    public void drawPaddle(Paddle paddle) {
        // If animation is actively playing, draw current frame
        if (paddle.isAnimationPlaying()) {
            Animation animation = paddle.getAnimation();
            if (animation != null) {
                Image frame = animation.getCurrentFrame();
                if (frame != null) {
                    gc.drawImage(frame, paddle.getX(), paddle.getY());
                    return;
                }
            }
        }

        // If no animation playing, draw static sprite based on state
        // This handles: NORMAL, WIDE (after transition), LASER (after transition)
        PaddleState state = paddle.getState();

        if (state == PaddleState.NORMAL) {
            gc.drawImage(sprites.get("paddle.png"), paddle.getX(), paddle.getY());
        } else if (state == PaddleState.WIDE || state == PaddleState.WIDE_PULSATE) {
            // After WIDE transition animation finishes, render static wide paddle sprite
            gc.drawImage(sprites.get("paddle_wide.png"), paddle.getX(), paddle.getY());
        } else if (state == PaddleState.LASER || state == PaddleState.LASER_PULSATE) {
            // After LASER transition animation finishes, render static laser paddle sprite
            gc.drawImage(sprites.get("paddle_laser.png"), paddle.getX(), paddle.getY());
        } else {
            // Default fallback for any other states (PULSATE, etc.)
            gc.drawImage(sprites.get("paddle.png"), paddle.getX(), paddle.getY());
        }
    }

    public void drawBrick(Brick brick) {
        double x = brick.getX();
        double y = brick.getY();
        double w = Constants.Bricks.BRICK_WIDTH;
        double h = Constants.Bricks.BRICK_HEIGHT;

        if (brick instanceof NormalBrick) {
            NormalBrick normalBrick = (NormalBrick) brick;
            String spriteName = "brick_" + normalBrick.getBrickType().name().toLowerCase() + ".png";
            gc.drawImage(sprites.get(spriteName), x, y, w, h);
        } else if (brick instanceof SilverBrick) {
            SilverBrick silverBrick = (SilverBrick) brick;
            Animation crackAnimation = silverBrick.getCrackAnimation();

            // If crack animation is playing, draw the current frame OVER the brick
            if (silverBrick.isCrackAnimationPlaying() && crackAnimation != null) {
                // First draw the brick
                gc.drawImage(sprites.get("brick_silver.png"), x, y, w, h);

                // Then draw the crack animation frame on top
                Image crackFrame = crackAnimation.getCurrentFrame();
                if (crackFrame != null) {
                    gc.drawImage(crackFrame, x, y, w, h);
                }
            } else {
                // No animation, just draw the brick
                gc.drawImage(sprites.get("brick_silver.png"), x, y, w, h);
            }
        } else if (brick instanceof GoldBrick) {
            GoldBrick goldBrick = (GoldBrick) brick;
            gc.drawImage(sprites.get("brick_gold.png"), x, y, w, h);
        } else {
            // Fallback: draw a gray rectangle
            gc.setFill(Color.GRAY);
            gc.fillRect(x, y, brick.getWidth(), brick.getHeight());
        }
    }

    public void drawPowerUp(PowerUp powerUp) {
        if (powerUp == null || !powerUp.isActive()) {
            return;
        }

        double x = powerUp.getX();
        double y = powerUp.getY();

        Animation animation = powerUp.getAnimation();
        if (animation != null && animation.isPlaying()) {
            Image frame = animation.getCurrentFrame();
            if (frame != null) {
                gc.drawImage(frame, x, y);
                return;
            }
        }

        // Fallback: draw a simple shape if no animation
        gc.setFill(Color.YELLOW);
        gc.fillOval(x, y, powerUp.getWidth(), powerUp.getHeight());
    }
}
