package Utils;

import Objects.GameEntities.PaddleState;
import Objects.PowerUps.PowerUpType;
import javafx.scene.image.Image;
import java.util.List;

public final class SpriteCacheProvider implements SpriteProvider {
    private final SpriteCache cache;

    public SpriteCacheProvider(SpriteCache cache) {
        this.cache = cache;
    }

    @Override
    public Image get(String filename) {
        return cache.getImage(filename);
    }

    @Override
    public List<Image> getPowerUpFrames(PowerUpType type) {
        return switch (type) {
            case CATCH -> cache.getPowerUpCatchCache();
            case EXPAND -> cache.getPowerUpExpandCache();
            case LASER -> cache.getPowerUpLaserCache();
            case DUPLICATE -> cache.getPowerUpDuplicateCache();
            case SLOW -> cache.getPowerUpSlowCache();
            case LIFE -> cache.getPowerUPLifeCache();
            case WARP -> cache.getPowerUpWarpCache();
        };
    }

    @Override
    public List<Image> getPaddleFrames(PaddleState state) {
        return switch (state) {
            case NORMAL -> throw new IllegalStateException("NORMAL is static image");
            case WIDE -> cache.getPaddleWideCache();
            case WIDE_PULSATE -> cache.getPaddleWidePulsateCache();
            case LASER -> cache.getPaddleLaserCache();
            case LASER_PULSATE -> cache.getPaddleLaserPulsateCache();
            case PULSATE -> cache.getPaddlePulsateCache();
            case MATERIALIZE -> cache.getPaddleMaterializeCache();
            case EXPLODE -> cache.getPaddleExplodeCache();
        };
    }

    @Override
    public List<Image> getSilverCrackFrames() {
        return cache.getSilverCrackCache();
    }

    @Override
    public boolean isReady() {
        return cache.isInitialized();
    }
}