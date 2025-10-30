package Utils;

import Render.Animation;
import javafx.scene.image.Image;
import java.util.List;
import Objects.PowerUps.PowerUpType;
import Objects.GameEntities.PaddleState;

public final class AnimationFactory {

    private AnimationFactory() {
    }

    private static SpriteProvider sprites;

    public static void initialize(SpriteProvider spriteProvider) {
        sprites = spriteProvider;
    }

    private static SpriteProvider requireProvider() {
        if (sprites == null) {
            throw new IllegalStateException(
                    "AnimationFactory: SpriteProvider not set. Call AnimationFactory.initialize(...) during init.");
        }
        return sprites;
    }

    public static Animation createBrickCrackAnimation() {
        List<Image> frames = requireProvider().getSilverCrackFrames();
        return new Animation(frames, Constants.Animation.CRACK_ANIMATION_DURATION, false);
    }

    public static Animation createPowerUpAnimation(PowerUpType type) {
        List<Image> frames = requireProvider().getPowerUpFrames(type);
        return new Animation(frames, Constants.Animation.POWERUP_ANIMATION_DURATION, true);
    }

    public static Animation createPaddleAnimation(PaddleState state) {
        if (state == PaddleState.NORMAL) {
            throw new IllegalArgumentException("PaddleState.NORMAL does not have animation frames.");
        }
        List<Image> frames = requireProvider().getPaddleFrames(state);

        // Fallback: if frames list is empty (missing assets or not initialized),
        // try to load a single static image as a placeholder so the Animation
        // constructor doesn't throw. This makes the app more robust to init order
        // or missing files.
        // if (frames == null || frames.isEmpty()) {
        //     Image fallback = requireProvider().get(state.getPaddlePrefix() + ".png");
        //     frames = java.util.List.of(fallback);
        // }

        // Use the loop setting from PaddleState (MATERIALIZE and EXPLODE are one-shot)
        return new Animation(frames, Constants.Animation.PADDLE_ANIMATION_DURATION, state.shouldLoop());
    }
}
