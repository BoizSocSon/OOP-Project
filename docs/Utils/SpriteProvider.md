# SpriteProvider Interface

## Tổng quan
`SpriteProvider` là **interface** định nghĩa contract cho các class cung cấp **sprite resources** và **animation frames** trong Arkanoid game. Interface này implement **Provider Pattern** (hay Service Provider Interface), cho phép decoupling giữa sprite consumers và sprite storage implementation. Nó định nghĩa standardized API để access static sprites và animated sprite sequences.

## Vị trí
- **Package**: `Utils`
- **File**: `src/Utils/SpriteProvider.java`
- **Type**: Interface (Contract Definition)
- **Pattern**: Provider Pattern / Service Provider Interface (SPI)
- **Implementations**: `SpriteCacheProvider`

## Mục đích
SpriteProvider interface:
- Define contract cho sprite providers
- Decouple sprite consumers từ storage
- Enable multiple implementations
- Support dependency injection
- Provide type-safe sprite access
- Centralize sprite retrieval API
- Support animation frame sequences

---

## Interface Structure

```java
public interface SpriteProvider {
    // Static sprite access
    Image get(String filename);
    
    // Animation frame sequences
    List<Image> getPowerUpFrames(PowerUpType type);
    List<Image> getPaddleFrames(PaddleState state);
    List<Image> getSilverCrackFrames();
    
    // Readiness check
    boolean isReady();
}
```

---

## Methods

### 1. `Image get(String filename)`

**Mô tả**: Trả về static sprite dựa trên filename.

**Tham số**: `filename` - Sprite filename (e.g., "ball.png")

**Trả về**: JavaFX `Image` object

**Usage**:
```java
// Access ball sprite
Image ballSprite = provider.get("ball.png");

// Access brick sprite
Image brickSprite = provider.get("brick_red.png");

// Access paddle sprite
Image paddleSprite = provider.get("paddle.png");

// Access edge sprite
Image topEdge = provider.get("edge_top.png");

// Access logo
Image logo = provider.get("logo.png");
```

**Contract**:
- Must return non-null Image
- Filename is case-sensitive
- File extension included (".png")
- Returns same Image instance for repeated calls (caching)

---

### 2. `List<Image> getPowerUpFrames(PowerUpType type)`

**Mô tả**: Trả về animation frames cho PowerUp type.

**Tham số**: `type` - PowerUpType enum (CATCH, EXPAND, LASER, etc.)

**Trả về**: List of Image frames (ordered sequence)

**Usage**:
```java
// Get CATCH power-up animation frames
List<Image> catchFrames = provider.getPowerUpFrames(PowerUpType.CATCH);
// → [powerup_catch_1.png, powerup_catch_2.png, ..., powerup_catch_8.png]

// Get EXPAND power-up frames
List<Image> expandFrames = provider.getPowerUpFrames(PowerUpType.EXPAND);

// Get LASER power-up frames
List<Image> laserFrames = provider.getPowerUpFrames(PowerUpType.LASER);

// Create animation from frames
Animation catchAnim = new Animation(
    provider.getPowerUpFrames(PowerUpType.CATCH),
    100, // 100ms per frame
    true // loop
);
```

**Power-Up Types**:
```java
PowerUpType.CATCH      // Sticky paddle
PowerUpType.EXPAND     // Wider paddle
PowerUpType.LASER      // Laser cannons
PowerUpType.DUPLICATE  // Duplicate balls
PowerUpType.SLOW       // Slow ball speed
PowerUpType.LIFE       // Extra life
PowerUpType.WARP       // Skip level
```

**Frame Count**: Typically 8 frames per power-up animation

**Contract**:
- Returns non-null, non-empty List
- Frames ordered by sequence (frame 1, 2, 3, ...)
- All types must be supported
- List size may vary by type

---

### 3. `List<Image> getPaddleFrames(PaddleState state)`

**Mô tả**: Trả về animation frames cho Paddle state.

**Tham số**: `state` - PaddleState enum (LASER, WIDE, etc.)

**Trả về**: List of Image frames

**Throws**: May throw exception cho `PaddleState.NORMAL` (static sprite)

**Usage**:
```java
// Get LASER paddle animation frames
List<Image> laserFrames = provider.getPaddleFrames(PaddleState.LASER);

// Get WIDE paddle frames
List<Image> wideFrames = provider.getPaddleFrames(PaddleState.WIDE);

// Get MATERIALIZE animation (paddle appearing)
List<Image> materializeFrames = provider.getPaddleFrames(PaddleState.MATERIALIZE);

// Get EXPLODE animation (paddle destroyed)
List<Image> explodeFrames = provider.getPaddleFrames(PaddleState.EXPLODE);

// ❌ NORMAL state - should use static sprite
try {
    List<Image> normalFrames = provider.getPaddleFrames(PaddleState.NORMAL);
} catch (IllegalStateException e) {
    // NORMAL uses static image from get("paddle.png")
}
```

**Paddle States**:
```java
PaddleState.NORMAL          // Static sprite (no animation)
PaddleState.WIDE            // Expanded paddle animation
PaddleState.WIDE_PULSATE    // Wide paddle pulsing
PaddleState.LASER           // Laser cannons active
PaddleState.LASER_PULSATE   // Laser cannons pulsing
PaddleState.PULSATE         // Normal paddle pulsing
PaddleState.MATERIALIZE     // Paddle spawning/appearing
PaddleState.EXPLODE         // Paddle destruction
```

**Contract**:
- Returns non-null List for animated states
- NORMAL state behavior implementation-defined (may throw exception)
- Frame count varies by state
- Frames ordered sequentially

---

### 4. `List<Image> getSilverCrackFrames()`

**Mô tả**: Trả về animation frames cho silver brick crack effect.

**Trả về**: List of crack animation frames

**Usage**:
```java
// Get crack animation frames
List<Image> crackFrames = provider.getSilverCrackFrames();
// → [brick_silver_1.png, brick_silver_2.png, ..., brick_silver_10.png]

// Use in silver brick damage animation
public class SilverBrick extends Brick {
    private Animation crackAnimation;
    
    @Override
    public void onHit() {
        hits++;
        
        if (hits < maxHits) {
            // Show crack animation
            List<Image> frames = spriteProvider.getSilverCrackFrames();
            crackAnimation = new Animation(frames, 20, false);
            crackAnimation.start();
        } else {
            // Brick destroyed
            destroy();
        }
    }
}
```

**Frame Count**: Typically 10 frames (progressive damage)

**Contract**:
- Returns non-null, non-empty List
- Frames show progressive crack damage
- Ordered from light damage to heavy damage

---

### 5. `boolean isReady()`

**Mô tả**: Check xem provider đã sẵn sàng cung cấp sprites.

**Trả về**: `true` nếu ready, `false` nếu chưa initialized

**Usage**:
```java
// Check readiness before using provider
if (provider.isReady()) {
    // Safe to access sprites
    Image ball = provider.get("ball.png");
    startGame();
} else {
    // Still loading - show loading screen
    showLoadingScreen();
}

// Wait for provider to be ready
while (!provider.isReady()) {
    Thread.sleep(100);
    updateLoadingProgress();
}

// Defensive check
public void render(Graphics2D g) {
    if (!provider.isReady()) {
        renderLoadingScreen(g);
        return;
    }
    
    // Normal rendering
    Image sprite = provider.get("ball.png");
    g.drawImage(sprite, x, y, null);
}
```

**Contract**:
- Returns `false` until fully initialized
- Returns `true` when all sprites loaded
- Should be checked before first sprite access
- Thread-safe implementation recommended

---

## Provider Pattern Benefits

### Decoupling

```java
// Without Provider Pattern:
public class Ball {
    private Image sprite = SpriteCache.getInstance().getImage("ball.png");
    // Tightly coupled to SpriteCache
}

// With Provider Pattern:
public class Ball {
    private SpriteProvider provider;
    private Image sprite;
    
    public Ball(SpriteProvider provider) {
        this.provider = provider;
        this.sprite = provider.get("ball.png");
    }
    // Decoupled - works with any SpriteProvider implementation
}
```

---

### Testability

```java
// Mock provider for testing
public class MockSpriteProvider implements SpriteProvider {
    @Override
    public Image get(String filename) {
        // Return test image
        return new WritableImage(10, 10);
    }
    
    @Override
    public List<Image> getPowerUpFrames(PowerUpType type) {
        // Return mock frames
        return List.of(new WritableImage(10, 10));
    }
    
    // ... other methods
}

// Test with mock
@Test
public void testBallRendering() {
    SpriteProvider mockProvider = new MockSpriteProvider();
    Ball ball = new Ball(mockProvider);
    // Test without loading actual sprites
}
```

---

### Multiple Implementations

```java
// Cache-based implementation
public class SpriteCacheProvider implements SpriteProvider {
    private SpriteCache cache;
    
    @Override
    public Image get(String filename) {
        return cache.getImage(filename);
    }
}

// On-demand loading implementation
public class LazyLoadProvider implements SpriteProvider {
    @Override
    public Image get(String filename) {
        return AssetLoader.loadImage(filename);
    }
}

// Remote loading implementation
public class RemoteSpriteProvider implements SpriteProvider {
    @Override
    public Image get(String filename) {
        return downloadSprite(filename);
    }
}

// Usage - same client code works with any implementation
public class Game {
    private SpriteProvider provider;
    
    public Game(SpriteProvider provider) {
        this.provider = provider;
    }
}
```

---

## Dependency Injection

### Constructor Injection

```java
public class GameManager {
    private final SpriteProvider sprites;
    
    public GameManager(SpriteProvider sprites) {
        this.sprites = sprites;
    }
    
    public void render(Graphics2D g) {
        Image ball = sprites.get("ball.png");
        g.drawImage(ball, x, y, null);
    }
}

// Inject at runtime
SpriteCache cache = SpriteCache.getInstance();
cache.initialize();

SpriteProvider provider = new SpriteCacheProvider(cache);
GameManager manager = new GameManager(provider);
```

---

### Factory Pattern

```java
public class AnimationFactory {
    private static SpriteProvider sprites;
    
    public static void initialize(SpriteProvider provider) {
        sprites = provider;
    }
    
    public static Animation createPowerUpAnimation(PowerUpType type) {
        List<Image> frames = sprites.getPowerUpFrames(type);
        return new Animation(frames, 100, true);
    }
}
```

---

## Implementation Example

### Complete Implementation

```java
public class SpriteCacheProvider implements SpriteProvider {
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
        if (state == PaddleState.NORMAL) {
            throw new IllegalStateException("NORMAL is static image");
        }
        return switch (state) {
            case WIDE -> cache.getPaddleWideCache();
            case WIDE_PULSATE -> cache.getPaddleWidePulsateCache();
            case LASER -> cache.getPaddleLaserCache();
            case LASER_PULSATE -> cache.getPaddleLaserPulsateCache();
            case PULSATE -> cache.getPaddlePulsateCache();
            case MATERIALIZE -> cache.getPaddleMaterializeCache();
            case EXPLODE -> cache.getPaddleExplodeCache();
            default -> throw new IllegalStateException("Unknown state: " + state);
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
```

---

## Best Practices

### 1. Check Readiness

```java
// ✅ Đúng - check before use
if (provider.isReady()) {
    Image sprite = provider.get("ball.png");
} else {
    showLoadingScreen();
}

// ❌ Sai - assume ready
Image sprite = provider.get("ball.png");
// May fail if not ready
```

---

### 2. Handle Null Returns

```java
// ✅ Đúng - check for null
Image sprite = provider.get("ball.png");
if (sprite != null) {
    g.drawImage(sprite, x, y, null);
} else {
    renderFallback(g, x, y);
}

// ❌ Sai - assume non-null
Image sprite = provider.get("ball.png");
g.drawImage(sprite, x, y, null); // NullPointerException if null
```

---

### 3. Cache Provider References

```java
// ✅ Đúng - cache provider reference
public class GameObject {
    private final SpriteProvider provider;
    
    public GameObject(SpriteProvider provider) {
        this.provider = provider;
    }
}

// ❌ Sai - re-create or lookup repeatedly
public void render() {
    SpriteProvider provider = getProvider(); // Expensive
    Image sprite = provider.get("ball.png");
}
```

---

### 4. Use Type-Safe Methods

```java
// ✅ Đúng - use typed methods
List<Image> frames = provider.getPowerUpFrames(PowerUpType.CATCH);
// Compile-time type checking

// ❌ Sai - string-based (error-prone)
List<Image> frames = provider.getAnimationFrames("catch");
// No type safety, typo errors
```

---

## Usage Patterns

### Pattern 1: Singleton Provider

```java
public class GameResources {
    private static SpriteProvider instance;
    
    public static void initialize() {
        SpriteCache cache = SpriteCache.getInstance();
        cache.initialize();
        instance = new SpriteCacheProvider(cache);
    }
    
    public static SpriteProvider getProvider() {
        return instance;
    }
}

// Usage
GameResources.initialize();
SpriteProvider provider = GameResources.getProvider();
```

---

### Pattern 2: Dependency Injection

```java
public class GameApplication {
    public void start() {
        // Initialize cache
        SpriteCache cache = SpriteCache.getInstance();
        cache.initialize();
        
        // Create provider
        SpriteProvider provider = new SpriteCacheProvider(cache);
        
        // Inject into game components
        GameManager gameManager = new GameManager(provider);
        AnimationFactory.initialize(provider);
        UIRenderer uiRenderer = new UIRenderer(provider);
    }
}
```

---

### Pattern 3: Lazy Initialization

```java
public class GameComponent {
    private SpriteProvider provider;
    private Image sprite;
    
    public void setProvider(SpriteProvider provider) {
        this.provider = provider;
    }
    
    public void render(Graphics2D g) {
        // Lazy load sprite
        if (sprite == null && provider.isReady()) {
            sprite = provider.get("sprite.png");
        }
        
        if (sprite != null) {
            g.drawImage(sprite, x, y, null);
        }
    }
}
```

---

## Testing

### Mock Implementation

```java
public class TestSpriteProvider implements SpriteProvider {
    private Map<String, Image> mockImages = new HashMap<>();
    
    @Override
    public Image get(String filename) {
        return mockImages.computeIfAbsent(filename,
            f -> new WritableImage(10, 10));
    }
    
    @Override
    public List<Image> getPowerUpFrames(PowerUpType type) {
        List<Image> frames = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            frames.add(new WritableImage(10, 10));
        }
        return frames;
    }
    
    @Override
    public boolean isReady() {
        return true; // Always ready in tests
    }
    
    // ... other methods
}
```

### Unit Tests

```java
@Test
public void testProviderContract() {
    SpriteProvider provider = new TestSpriteProvider();
    
    // Test static sprite access
    Image sprite = provider.get("ball.png");
    assertNotNull(sprite);
    
    // Test animation frames
    List<Image> frames = provider.getPowerUpFrames(PowerUpType.CATCH);
    assertNotNull(frames);
    assertFalse(frames.isEmpty());
    
    // Test readiness
    assertTrue(provider.isReady());
}
```

---

## Kết luận

`SpriteProvider` interface là **service contract** cho sprite access trong Arkanoid:

- **Contract Definition**: Clear API cho sprite providers
- **Decoupling**: Separates consumers từ storage implementation
- **Flexibility**: Enables multiple implementations
- **Testability**: Easy to mock cho unit tests
- **Type Safety**: Compile-time checking với enums
- **Dependency Injection**: Supports IoC patterns

SpriteProvider exemplifies **interface-based design**. Bằng việc define contract thay vì implementation, code becomes more flexible và testable. Clients depend on abstraction (interface) chứ không phải concrete class (SpriteCache). This allows swapping implementations (cache-based, lazy-loading, remote) without changing client code.

**Design Philosophy**: "Program to an interface, not an implementation." Interface defines WHAT operations are available, không HOW they're implemented. This separation enables flexibility, testing, và evolution. New implementations có thể added mà không breaking existing code.

**Pattern Significance**: Provider Pattern (SPI) là foundational pattern trong Java. Used extensively trong JDBC, Logging frameworks (SLF4J), và Plugin systems. Interface defines service contract, implementations provide actual functionality. This design enables extensibility và loose coupling - cornerstone của maintainable software.

