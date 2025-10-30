package Utils;

import Objects.GameEntities.PaddleState;
import Objects.PowerUps.PowerUpType;
import javafx.scene.image.Image;

import java.util.List;

public interface SpriteProvider {
    // 1) Ảnh đơn lẻ (ball, edges, logo, bricks static, paddle static...)
    Image get(String filename);

    // 2) PowerUp animated frames
    List<Image> getPowerUpFrames(PowerUpType type);

    // 3) Paddle animated frames
    List<Image> getPaddleFrames(PaddleState state);

    // 4) Silver brick crack frames
    List<Image> getSilverCrackFrames();

    boolean isReady();
}