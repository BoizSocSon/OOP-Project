# AnimationFactory Class

## Tổng quan
`AnimationFactory` là **final utility class** implement **Factory Pattern** để tạo ra các `Animation` objects từ sprite resources. Class này đóng vai trò như **centralized animation factory**, ensuring tất cả animations được tạo consistently và sử dụng cùng một `SpriteProvider`. Nó abstract away animation creation complexity và provides simple, semantic methods cho từng animation type trong game.

## Vị trí
- **Package**: `Utils`
- **File**: `src/Utils/AnimationFactory.java`
- **Type**: Final Utility Class (không thể khởi tạo)
- **Pattern**: Factory Pattern / Static Factory
- **Dependencies**: 
  - `Render.Animation`
  - `SpriteProvider`
  - `PowerUpType`
  - `PaddleState`

## Mục đích
AnimationFactory:
- Centralize animation creation
- Abstract sprite loading details
- Ensure consistent animation parameters
- Type-safe animation creation
- Decouple animation creation from usage
- Simplify animation instantiation
- Support different animation types

---

## Class Structure

```java
public final class AnimationFactory {
    // Private constructor - prevents instantiation
    private AnimationFactory() {
    }
    
    // Sprite provider (initialized once)
    private static SpriteProvider sprites;
    
    // Initialization
    public static void initialize(SpriteProvider spriteProvider) { /* ... */ }
    
    // Factory methods
    public static Animation createBrickCrackAnimation() { /* ... */ }
    public static Animation createPowerUpAnimation(PowerUpType type) { /* ... */ }
    public static Animation createPaddleAnimation(PaddleState state) { /* ... */ }
}
```

---

## Initialization

### `static void initialize(SpriteProvider spriteProvider)`

**Mô tả**: Khởi tạo factory với sprite provider. **PHẢI được gọi một lần** khi app starts.

**Tham số**: `spriteProvider` - Object cung cấp sprite frames

**Hành vi**:
```java
public static void initialize(SpriteProvider spriteProvider) {
    sprites = spriteProvider;
}
```

**Ví dụ sử dụng**:
```java
// During game initialization
public class GameInitializer {
    public void initialize() {
        // 1. Create sprite cache
        SpriteCache spriteCache = new SpriteCache();
        
        // 2. Load all sprites
        spriteCache.loadAllSprites();
        
        // 3. Initialize AnimationFactory
        AnimationFactory.initialize(spriteCache);
        
        // 4. Now factory can create animations
        Animation crackAnim = AnimationFactory.createBrickCrackAnimation();
    }
}
```

---

### `private static SpriteProvider requireProvider()`

**Mô tả**: Internal guard method ensuring provider đã được set.

**Trả về**: SpriteProvider instance

**Throws**: `IllegalStateException` nếu chưa initialized

**Hành vi**:
```java
private static SpriteProvider requireProvider() {
    if (sprites == null) {
        throw new IllegalStateException(
            "AnimationFactory: SpriteProvider not set. " +
            "Call AnimationFactory.initialize(...) during init."
        );
    }
    return sprites;
}
```

**Why This Guard?**
- Prevents NullPointerException
- Clear error message
- Fails fast with helpful guidance
- Forces proper initialization

**Error Example**:
```java
// Forgot to initialize
Animation anim = AnimationFactory.createBrickCrackAnimation();
// → IllegalStateException: "AnimationFactory: SpriteProvider not set. 
//    Call AnimationFactory.initialize(...) during init."
```

---

## Factory Methods

### 1. `static Animation createBrickCrackAnimation()`

**Mô tả**: Tạo animation cho brick crack effect (khi silver brick bị hit).

**Trả về**: Animation object với crack frames

**Hành vi**:
```java
public static Animation createBrickCrackAnimation() {
    // Get crack frames from sprite provider
    List<Image> frames = requireProvider().getSilverCrackFrames();
    
    // Create animation with crack duration, no loop
    return new Animation(
        frames,
        Constants.Animation.CRACK_ANIMATION_DURATION, // 20ms
        false // Don't loop - play once
    );
}
```

**Animation Properties**:
- **Frames**: Silver brick crack sequence (0-3)
- **Duration**: 20ms per frame (fast effect)
- **Loop**: false (one-shot animation)
- **Total Time**: 20ms × 4 frames = 80ms

**Ví dụ sử dụng**:
```java
// When silver brick is hit
public class SilverBrick extends Brick {
    private Animation crackAnimation;
    private boolean showingCrack = false;
    
    @Override
    public void onHit() {
        hits++;
        
        if (hits < maxHits) {
            // Show crack animation
            crackAnimation = AnimationFactory.createBrickCrackAnimation();
            crackAnimation.start();
            showingCrack = true;
        } else {
            // Brick destroyed
            destroy();
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        if (showingCrack && crackAnimation != null) {
            crackAnimation.render(g, x, y, width, height);
            
            if (crackAnimation.isFinished()) {
                showingCrack = false;
            }
        } else {
            // Normal brick rendering
            super.render(g);
        }
    }
}
```

---

### 2. `static Animation createPowerUpAnimation(PowerUpType type)`

**Mô tả**: Tạo animation cho power-up dựa trên type.

**Tham số**: `type` - PowerUpType enum (CATCH, DUPLICATE, EXPAND, etc.)

**Trả về**: Looping animation cho power-up

**Hành vi**:
```java
public static Animation createPowerUpAnimation(PowerUpType type) {
    // Get power-up frames from sprite provider
    List<Image> frames = requireProvider().getPowerUpFrames(type);
    
    // Create looping animation with power-up duration
    return new Animation(
        frames,
        Constants.Animation.POWERUP_ANIMATION_DURATION, // 100ms
        true // Loop indefinitely
    );
}
```

**Animation Properties**:
- **Frames**: Type-specific power-up sprites (e.g., "powerup_catch_0.png" to "powerup_catch_7.png")
- **Duration**: 100ms per frame
- **Loop**: true (continuous animation while falling)
- **Total Cycle**: 100ms × 8 frames = 800ms per loop

**Ví dụ sử dụng**:
```java
// Power-up creation
public class PowerUpManager {
    public PowerUp spawnPowerUp(double x, double y) {
        // Random type selection
        PowerUpType type = PowerUpType.randomWeighted();
        
        // Create power-up
        PowerUp powerUp;
        switch (type) {
            case CATCH:
                powerUp = new CatchPowerUp(x, y);
                break;
            case DUPLICATE:
                powerUp = new DuplicatePowerUp(x, y);
                break;
            // ... other types
        }
        
        // Set animation
        Animation animation = AnimationFactory.createPowerUpAnimation(type);
        powerUp.setAnimation(animation);
        
        return powerUp;
    }
}

// In PowerUp class
public class PowerUp extends GameObject {
    private Animation animation;
    
    public void setAnimation(Animation animation) {
        this.animation = animation;
        this.animation.start();
    }
    
    @Override
    public void render(Graphics2D g) {
        if (animation != null) {
            animation.render(g, x, y, width, height);
        }
    }
    
    @Override
    public void update() {
        // Move down
        y += Constants.PowerUps.POWERUP_FALL_SPEED;
        
        // Update animation
        if (animation != null) {
            animation.update();
        }
    }
}
```

**All Power-Up Types**:
```java
// Examples of different power-up animations
Animation catchAnim = AnimationFactory.createPowerUpAnimation(PowerUpType.CATCH);
Animation expandAnim = AnimationFactory.createPowerUpAnimation(PowerUpType.EXPAND);
Animation laserAnim = AnimationFactory.createPowerUpAnimation(PowerUpType.LASER);
Animation slowAnim = AnimationFactory.createPowerUpAnimation(PowerUpType.SLOW);
Animation dupAnim = AnimationFactory.createPowerUpAnimation(PowerUpType.DUPLICATE);
Animation lifeAnim = AnimationFactory.createPowerUpAnimation(PowerUpType.LIFE);
Animation warpAnim = AnimationFactory.createPowerUpAnimation(PowerUpType.WARP);
```

---

### 3. `static Animation createPaddleAnimation(PaddleState state)`

**Mô tả**: Tạo animation cho paddle dựa trên state.

**Tham số**: `state` - PaddleState enum (LASER, FIREBALL, etc.)

**Trả về**: Animation cho paddle state

**Throws**: `IllegalArgumentException` nếu state là NORMAL

**Hành vi**:
```java
public static Animation createPaddleAnimation(PaddleState state) {
    // NORMAL state has no animation - throw exception
    if (state == PaddleState.NORMAL) {
        throw new IllegalArgumentException(
            "PaddleState.NORMAL does not have animation frames.");
    }
    
    // Get paddle frames from sprite provider
    List<Image> frames = requireProvider().getPaddleFrames(state);
    
    // Create animation with state-specific looping
    return new Animation(
        frames,
        Constants.Animation.PADDLE_ANIMATION_DURATION, // 80ms
        state.shouldLoop() // State determines if loop
    );
}
```

**Animation Properties**:
- **Frames**: State-specific paddle sprites (e.g., "paddle_laser_0.png", "paddle_laser_1.png")
- **Duration**: 80ms per frame
- **Loop**: Depends on state (`state.shouldLoop()`)

**PaddleState Loop Behavior**:
```java
public enum PaddleState {
    NORMAL(false),       // No animation
    LASER(true),         // Loops - laser cannons firing
    FIREBALL(false),     // One-shot - fireball launch
    WIDE(false);         // No animation - static wide sprite
    
    private final boolean loops;
    
    PaddleState(boolean loops) {
        this.loops = loops;
    }
    
    public boolean shouldLoop() {
        return loops;
    }
}
```

**Ví dụ sử dụng**:
```java
// Paddle with state animations
public class Paddle extends GameObject {
    private PaddleState currentState = PaddleState.NORMAL;
    private Animation stateAnimation;
    private Image normalSprite;
    
    public void setState(PaddleState newState) {
        if (newState == currentState) return;
        
        currentState = newState;
        
        if (newState == PaddleState.NORMAL) {
            // No animation for normal state
            stateAnimation = null;
        } else {
            // Create animation for new state
            stateAnimation = AnimationFactory.createPaddleAnimation(newState);
            stateAnimation.start();
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        if (stateAnimation != null && currentState != PaddleState.NORMAL) {
            // Render animation
            stateAnimation.render(g, x, y, width, height);
        } else {
            // Render static sprite
            g.drawImage(normalSprite, x, y, width, height, null);
        }
    }
    
    @Override
    public void update() {
        // Update animation if active
        if (stateAnimation != null) {
            stateAnimation.update();
            
            // Check if one-shot animation finished
            if (!currentState.shouldLoop() && stateAnimation.isFinished()) {
                setState(PaddleState.NORMAL);
            }
        }
    }
}

// Power-up activation
public void onLaserPowerUpCollected() {
    paddle.setState(PaddleState.LASER);
    // Laser animation loops while power-up active
    
    // Schedule power-up expiration
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
        @Override
        public void run() {
            paddle.setState(PaddleState.NORMAL);
        }
    }, Constants.PowerUps.LASER_DURATION);
}
```

**Error Handling**:
```java
// ❌ Attempting to create animation for NORMAL state
try {
    Animation anim = AnimationFactory.createPaddleAnimation(PaddleState.NORMAL);
} catch (IllegalArgumentException e) {
    // Exception thrown: "PaddleState.NORMAL does not have animation frames."
    System.err.println("Cannot animate NORMAL paddle state");
}
```

---

## Factory Pattern Benefits

### Centralized Creation Logic

```java
// Without Factory Pattern:
public Animation createAnimation() {
    List<Image> frames = new ArrayList<>();
    frames.add(AssetLoader.loadImage("frame_0.png"));
    frames.add(AssetLoader.loadImage("frame_1.png"));
    frames.add(AssetLoader.loadImage("frame_2.png"));
    return new Animation(frames, 100, true);
}

// With Factory Pattern:
Animation anim = AnimationFactory.createPowerUpAnimation(PowerUpType.CATCH);
// Simple, clean, consistent
```

---

### Consistency

```java
// All animations use same SpriteProvider
// → Consistent sprite loading
// → Shared sprite cache
// → No duplicate loading

// All animations use Constants for timing
// → Easy to tune all animations at once
// → Consistent animation speeds
```

---

### Type Safety

```java
// Compile-time type checking
Animation powerUpAnim = AnimationFactory.createPowerUpAnimation(
    PowerUpType.CATCH // Must be valid PowerUpType
);

Animation paddleAnim = AnimationFactory.createPaddleAnimation(
    PaddleState.LASER // Must be valid PaddleState
);

// Can't mix types:
// AnimationFactory.createPaddleAnimation(PowerUpType.CATCH); // Compile error!
```

---

## Best Practices

### 1. Initialize Early

```java
// ✅ Đúng - initialize during app startup
public class GameApplication {
    public void start() {
        SpriteCache cache = new SpriteCache();
        cache.loadAllSprites();
        AnimationFactory.initialize(cache);
        
        // Now safe to create animations
        startGame();
    }
}

// ❌ Sai - initialize too late
public void createAnimation() {
    // May throw IllegalStateException if not initialized
    return AnimationFactory.createBrickCrackAnimation();
}
```

---

### 2. Cache Animations When Appropriate

```java
// ✅ Đúng - cache reusable animations
public class AnimationCache {
    private Animation crackAnimation;
    
    public Animation getCrackAnimation() {
        if (crackAnimation == null) {
            crackAnimation = AnimationFactory.createBrickCrackAnimation();
        }
        return crackAnimation.copy(); // Return copy for independent state
    }
}

// ❌ Có thể tốt hơn - create new every time (depends on usage)
public void onBrickHit() {
    Animation anim = AnimationFactory.createBrickCrackAnimation();
    // Creates new animation object each time
}
```

---

### 3. Handle Exceptions

```java
// ✅ Đúng - catch and handle exceptions
public void setPaddleState(PaddleState state) {
    if (state == PaddleState.NORMAL) {
        animation = null;
    } else {
        try {
            animation = AnimationFactory.createPaddleAnimation(state);
        } catch (IllegalArgumentException e) {
            System.err.println("Cannot create animation for state: " + state);
            animation = null;
        }
    }
}

// ❌ Sai - assume no exceptions
public void setPaddleState(PaddleState state) {
    animation = AnimationFactory.createPaddleAnimation(state);
    // May throw if state == NORMAL
}
```

---

### 4. Don't Create Factory Instances

```java
// ✅ Đúng - use static methods
Animation anim = AnimationFactory.createBrickCrackAnimation();

// ❌ Sai - attempt to instantiate (will throw exception)
AnimationFactory factory = new AnimationFactory();
// → UnsupportedOperationException
```

---

## Usage Patterns

### Pattern 1: One-Shot Effect

```java
// Brick crack - plays once then stops
public void showCrackEffect() {
    Animation crack = AnimationFactory.createBrickCrackAnimation();
    crack.start();
    effects.add(crack);
}

// Update loop
public void update() {
    effects.removeIf(anim -> anim.isFinished());
    effects.forEach(Animation::update);
}
```

---

### Pattern 2: Continuous Loop

```java
// Power-up - loops while falling
public class PowerUp {
    private Animation animation;
    
    public PowerUp(PowerUpType type, double x, double y) {
        this.animation = AnimationFactory.createPowerUpAnimation(type);
        this.animation.start();
    }
    
    @Override
    public void update() {
        animation.update(); // Loops forever
        y += fallSpeed;
    }
}
```

---

### Pattern 3: State-Based Animation

```java
// Paddle - animation changes with state
public class Paddle {
    private PaddleState state = PaddleState.NORMAL;
    private Animation currentAnimation;
    
    public void setState(PaddleState newState) {
        state = newState;
        
        if (newState != PaddleState.NORMAL) {
            currentAnimation = AnimationFactory.createPaddleAnimation(newState);
            currentAnimation.start();
        } else {
            currentAnimation = null;
        }
    }
    
    @Override
    public void update() {
        if (currentAnimation != null) {
            currentAnimation.update();
        }
    }
}
```

---

## Testing

### Unit Tests

```java
@Test
public void testFactoryInitialization() {
    SpriteProvider provider = mock(SpriteProvider.class);
    AnimationFactory.initialize(provider);
    // Should not throw
}

@Test
public void testCreateBrickCrackAnimation() {
    List<Image> mockFrames = Arrays.asList(
        mock(Image.class), mock(Image.class)
    );
    when(sprites.getSilverCrackFrames()).thenReturn(mockFrames);
    
    Animation anim = AnimationFactory.createBrickCrackAnimation();
    
    assertNotNull(anim);
    assertFalse(anim.isLooping());
}

@Test
public void testCreatePowerUpAnimation() {
    List<Image> mockFrames = Arrays.asList(mock(Image.class));
    when(sprites.getPowerUpFrames(any())).thenReturn(mockFrames);
    
    Animation anim = AnimationFactory.createPowerUpAnimation(PowerUpType.CATCH);
    
    assertNotNull(anim);
    assertTrue(anim.isLooping());
}

@Test(expected = IllegalArgumentException.class)
public void testCreatePaddleAnimationWithNormalState() {
    AnimationFactory.createPaddleAnimation(PaddleState.NORMAL);
    // Should throw IllegalArgumentException
}

@Test(expected = IllegalStateException.class)
public void testCreateAnimationWithoutInitialization() {
    // Reset factory
    AnimationFactory.initialize(null);
    
    // Should throw IllegalStateException
    AnimationFactory.createBrickCrackAnimation();
}
```

---

## Dependency Injection Alternative

```java
// Current: Static factory
Animation anim = AnimationFactory.createBrickCrackAnimation();

// Alternative: Instance-based factory (more testable)
public class AnimationFactoryImpl {
    private final SpriteProvider sprites;
    
    public AnimationFactoryImpl(SpriteProvider sprites) {
        this.sprites = sprites;
    }
    
    public Animation createBrickCrackAnimation() {
        List<Image> frames = sprites.getSilverCrackFrames();
        return new Animation(frames, 20, false);
    }
}

// Usage with DI
AnimationFactoryImpl factory = new AnimationFactoryImpl(spriteProvider);
Animation anim = factory.createBrickCrackAnimation();

// Easier to mock in tests
AnimationFactoryImpl mockFactory = mock(AnimationFactoryImpl.class);
when(mockFactory.createBrickCrackAnimation()).thenReturn(mockAnimation);
```

---

## Kết luận

`AnimationFactory` là **centralized factory** cho animation creation trong Arkanoid:

- **Factory Pattern**: Encapsulates animation creation logic
- **Type Safety**: Compile-time checking với enums
- **Consistency**: All animations use same sprite source
- **Simplicity**: Simple API hides complexity
- **Centralized Configuration**: Animation parameters in Constants
- **Error Handling**: Clear exceptions với helpful messages

AnimationFactory exemplifies **good factory design**. Nó abstracts away complexity của animation creation (loading sprites, setting durations, configuring loops) và provides clean, semantic API. Developers don't need to know implementation details - just call `createPowerUpAnimation(type)` và get back ready-to-use animation.

**Design Philosophy**: Factory Pattern separates object creation từ object usage. Code that uses animations doesn't need to know how animations are created. This separation makes code more modular, testable, và maintainable. Factory can change implementation (different sprite source, caching strategy, etc.) without affecting client code.

**Static vs Instance**: Static factory có trade-offs. Pros: Simple, no instantiation needed, global access. Cons: Harder to test (static dependencies), less flexible (single global configuration). Cho simple game như Arkanoid, static factory là reasonable choice. For larger systems, instance-based factory với dependency injection might be better.

