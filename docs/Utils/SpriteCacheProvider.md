# SpriteCacheProvider Class

## Tổng quan
`SpriteCacheProvider` là **Adapter class** implement `SpriteProvider` interface bằng cách wrap `SpriteCache` singleton. Class này implement **Adapter Pattern**, providing clean interface-based access tới cached sprites mà không expose cache implementation details. SpriteCacheProvider sử dụng **switch expressions** để route enum-based requests tới appropriate cache collections, ensuring type-safe sprite retrieval.

## Vị trí
- **Package**: `Utils`
- **File**: `src/Utils/SpriteCacheProvider.java`
- **Type**: Adapter Class
- **Pattern**: Adapter Pattern (Object Adapter)
- **Implements**: `SpriteProvider` interface
- **Dependencies**: `SpriteCache`, `PowerUpType`, `PaddleState`

## Mục đích
SpriteCacheProvider class:
- Adapt SpriteCache tới SpriteProvider interface
- Provide clean API cho sprite access
- Route enum requests tới correct caches
- Decouple consumers từ cache implementation
- Enable dependency injection
- Support interface-based programming
- Provide type-safe sprite retrieval

---

## Class Structure

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
            // ... other cases
        };
    }
    
    @Override
    public List<Image> getPaddleFrames(PaddleState state) {
        if (state == PaddleState.NORMAL) {
            throw new IllegalStateException(
                "NORMAL paddle state uses static sprite");
        }
        return switch (state) {
            case WIDE -> cache.getPaddleWideCache();
            // ... other cases
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

## Adapter Pattern

### Structure

```
Client (GameObject)
    ↓ uses
SpriteProvider (Interface)
    ↓ implemented by
SpriteCacheProvider (Adapter)
    ↓ wraps
SpriteCache (Adaptee)
```

**Components**:
- **Target**: `SpriteProvider` interface (what clients want)
- **Adapter**: `SpriteCacheProvider` class (converts interface)
- **Adaptee**: `SpriteCache` class (existing implementation)
- **Client**: Game objects using `SpriteProvider`

---

### Benefits

```java
// Without Adapter - Tight Coupling
public class Ball {
    private SpriteCache cache = SpriteCache.getInstance();
    private Image sprite = cache.getImage("ball.png");
    // Directly depends on SpriteCache
}

// With Adapter - Loose Coupling
public class Ball {
    private SpriteProvider provider;
    private Image sprite;
    
    public Ball(SpriteProvider provider) {
        this.provider = provider;
        this.sprite = provider.get("ball.png");
    }
    // Depends only on interface
}
```

---

## Constructor

### SpriteCacheProvider(SpriteCache cache)

```java
public SpriteCacheProvider(SpriteCache cache) {
    this.cache = cache;
}
```

**Tham số**: `cache` - SpriteCache instance to adapt

**Usage**:
```java
// Initialize cache
SpriteCache cache = SpriteCache.getInstance();
cache.initialize();

// Create adapter
SpriteProvider provider = new SpriteCacheProvider(cache);

// Use adapter
Image ball = provider.get("ball.png");
```

**Design Notes**:
- Constructor injection (dependency injection)
- Final field ensures immutability
- Cache must be initialized before use
- Provider lifetime tied to cache

---

## Interface Implementation

### get(String filename) - Static Sprite Access

```java
@Override
public Image get(String filename) {
    return cache.getImage(filename);
}
```

**Implementation**: Simple delegation tới cache

**Usage**:
```java
SpriteCacheProvider provider = new SpriteCacheProvider(cache);

// Access static sprites
Image ball = provider.get("ball.png");
Image paddle = provider.get("paddle.png");
Image redBrick = provider.get("brick_red.png");
Image leftLaser = provider.get("laser_left.png");
Image topEdge = provider.get("edge_top.png");
Image logo = provider.get("logo.png");
```

**Supported Files**:
- Ball: `ball.png`
- Paddle: `paddle.png` (NORMAL state)
- Bricks: `brick_red.png`, `brick_orange.png`, etc.
- Lasers: `laser_left.png`, `laser_right.png`
- Edges: `edge_top.png`, `edge_left.png`, `edge_right.png`
- Logo: `logo.png`

---

### getPowerUpFrames(PowerUpType type) - Power-Up Animations

```java
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
```

**Implementation**: Switch expression routes enum tới appropriate cache

**Usage**:
```java
SpriteCacheProvider provider = new SpriteCacheProvider(cache);

// Get CATCH power-up frames
List<Image> catchFrames = provider.getPowerUpFrames(PowerUpType.CATCH);
Animation catchAnim = new Animation(catchFrames, 100, true);

// Get EXPAND power-up frames
List<Image> expandFrames = provider.getPowerUpFrames(PowerUpType.EXPAND);
Animation expandAnim = new Animation(expandFrames, 100, true);

// Get LASER power-up frames
List<Image> laserFrames = provider.getPowerUpFrames(PowerUpType.LASER);
Animation laserAnim = new Animation(laserFrames, 100, true);
```

**Switch Expression Benefits**:
- **Exhaustive**: Compiler ensures all enum cases covered
- **Type-safe**: No string-based lookups
- **Concise**: Clean, readable syntax
- **Maintainable**: Adding enum value requires update here

---

### getPaddleFrames(PaddleState state) - Paddle Animations

```java
@Override
public List<Image> getPaddleFrames(PaddleState state) {
    if (state == PaddleState.NORMAL) {
        throw new IllegalStateException(
            "PaddleState.NORMAL uses a static sprite. " +
            "Use get(\"paddle.png\") instead.");
    }
    
    return switch (state) {
        case WIDE -> cache.getPaddleWideCache();
        case WIDE_PULSATE -> cache.getPaddleWidePulsateCache();
        case LASER -> cache.getPaddleLaserCache();
        case LASER_PULSATE -> cache.getPaddleLaserPulsateCache();
        case PULSATE -> cache.getPaddlePulsateCache();
        case MATERIALIZE -> cache.getPaddleMaterializeCache();
        case EXPLODE -> cache.getPaddleExplodeCache();
        case NORMAL -> throw new IllegalStateException(
            "Already handled above");
    };
}
```

**Implementation**: 
- Guard clause cho NORMAL state
- Switch expression cho animated states
- Throws exception cho invalid usage

**Usage**:
```java
SpriteCacheProvider provider = new SpriteCacheProvider(cache);

// ✅ Correct - Animated states
List<Image> wideFrames = provider.getPaddleFrames(PaddleState.WIDE);
List<Image> laserFrames = provider.getPaddleFrames(PaddleState.LASER);
List<Image> explodeFrames = provider.getPaddleFrames(PaddleState.EXPLODE);

// ❌ Incorrect - NORMAL is static sprite
try {
    List<Image> normalFrames = provider.getPaddleFrames(PaddleState.NORMAL);
} catch (IllegalStateException e) {
    System.out.println(e.getMessage());
    // "PaddleState.NORMAL uses a static sprite. Use get(\"paddle.png\") instead."
}

// ✅ Correct way to get NORMAL paddle
Image normalPaddle = provider.get("paddle.png");
```

**State Categories**:

**Static State**:
- `NORMAL` - Use `get("paddle.png")`

**Animated States**:
- `WIDE` - Expanded paddle animation
- `WIDE_PULSATE` - Wide paddle pulsing effect
- `LASER` - Laser cannons animation
- `LASER_PULSATE` - Laser cannons pulsing
- `PULSATE` - Normal paddle pulsing
- `MATERIALIZE` - Paddle spawn animation
- `EXPLODE` - Paddle destruction animation

---

### getSilverCrackFrames() - Silver Brick Damage

```java
@Override
public List<Image> getSilverCrackFrames() {
    return cache.getSilverCrackCache();
}
```

**Implementation**: Simple delegation tới cache

**Usage**:
```java
SpriteCacheProvider provider = new SpriteCacheProvider(cache);

// Get silver brick crack animation frames
List<Image> crackFrames = provider.getSilverCrackFrames();

// Create animation (non-looping)
Animation crackAnim = new Animation(crackFrames, 20, false);

// Play on silver brick hit
public class SilverBrick extends Brick {
    private Animation crackAnimation;
    
    @Override
    public void onHit() {
        hits++;
        
        if (hits < maxHits) {
            // Show progressive damage
            List<Image> frames = provider.getSilverCrackFrames();
            crackAnimation = new Animation(frames, 20, false);
            crackAnimation.start();
        } else {
            destroy();
        }
    }
}
```

**Frame Count**: 10 frames (progressive crack damage)

---

### isReady() - Readiness Check

```java
@Override
public boolean isReady() {
    return cache.isInitialized();
}
```

**Implementation**: Delegates tới cache initialization status

**Usage**:
```java
SpriteCacheProvider provider = new SpriteCacheProvider(cache);

// Check before using
if (provider.isReady()) {
    // Safe to access sprites
    Image ball = provider.get("ball.png");
    startGame();
} else {
    // Cache not ready - show loading
    showLoadingScreen();
}

// Wait loop
while (!provider.isReady()) {
    Thread.sleep(100);
    updateLoadingBar();
}

// Defensive rendering
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

---

## Switch Expressions (Java 14+)

### Modern Syntax

```java
// Old switch statement
public List<Image> getPowerUpFrames(PowerUpType type) {
    switch (type) {
        case CATCH:
            return cache.getPowerUpCatchCache();
        case EXPAND:
            return cache.getPowerUpExpandCache();
        // ... more cases
        default:
            throw new IllegalArgumentException("Unknown type: " + type);
    }
}

// New switch expression (Java 14+)
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
```

**Benefits**:
- **Concise**: No break statements
- **Expression**: Returns value directly
- **Exhaustive**: Compiler checks all enum values covered
- **Safe**: No fall-through bugs
- **Readable**: Clean, functional style

---

### Exhaustiveness Checking

```java
// Compiler error if enum case missing
public List<Image> getPowerUpFrames(PowerUpType type) {
    return switch (type) {
        case CATCH -> cache.getPowerUpCatchCache();
        case EXPAND -> cache.getPowerUpExpandCache();
        // Missing other cases!
    };
    // ❌ Compiler error: "switch expression does not cover all possible input values"
}

// ✅ All cases covered
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
    // ✅ Compiles successfully
}
```

---

## Usage Patterns

### Pattern 1: Dependency Injection

```java
public class GameManager {
    private final SpriteProvider sprites;
    
    // Constructor injection
    public GameManager(SpriteProvider sprites) {
        this.sprites = sprites;
    }
    
    public void initialize() {
        // Wait for sprites to be ready
        while (!sprites.isReady()) {
            Thread.sleep(100);
        }
        
        // Load game sprites
        loadSprites();
    }
    
    private void loadSprites() {
        Image ball = sprites.get("ball.png");
        Image paddle = sprites.get("paddle.png");
        // ...
    }
}

// Setup
SpriteCache cache = SpriteCache.getInstance();
cache.initialize();

SpriteProvider provider = new SpriteCacheProvider(cache);
GameManager manager = new GameManager(provider);
```

---

### Pattern 2: Factory Integration

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
    
    public static Animation createPaddleAnimation(PaddleState state) {
        if (state == PaddleState.NORMAL) {
            return null; // Static sprite, no animation
        }
        
        List<Image> frames = sprites.getPaddleFrames(state);
        
        // Different speeds for different animations
        int frameDuration = switch (state) {
            case MATERIALIZE, EXPLODE -> 30; // Fast
            case WIDE, LASER -> 50; // Medium
            default -> 100; // Slow
        };
        
        boolean loop = state != PaddleState.MATERIALIZE && 
                      state != PaddleState.EXPLODE;
        
        return new Animation(frames, frameDuration, loop);
    }
}

// Usage
AnimationFactory.initialize(provider);
Animation catchAnim = AnimationFactory.createPowerUpAnimation(PowerUpType.CATCH);
Animation explodeAnim = AnimationFactory.createPaddleAnimation(PaddleState.EXPLODE);
```

---

### Pattern 3: Game Component Usage

```java
public class PowerUp extends MovableObject {
    private PowerUpType type;
    private Animation animation;
    
    public PowerUp(PowerUpType type, SpriteProvider provider) {
        this.type = type;
        
        // Get animation frames from provider
        List<Image> frames = provider.getPowerUpFrames(type);
        this.animation = new Animation(frames, 100, true);
        this.animation.start();
    }
    
    @Override
    public void render(Graphics2D g) {
        Image currentFrame = animation.getCurrentFrame();
        g.drawImage(currentFrame, x, y, null);
    }
}

// Usage
SpriteProvider provider = new SpriteCacheProvider(cache);
PowerUp catchPowerUp = new PowerUp(PowerUpType.CATCH, provider);
```

---

### Pattern 4: Paddle State Management

```java
public class Paddle {
    private PaddleState currentState;
    private Animation currentAnimation;
    private Image staticSprite;
    private SpriteProvider provider;
    
    public Paddle(SpriteProvider provider) {
        this.provider = provider;
        this.currentState = PaddleState.NORMAL;
        this.staticSprite = provider.get("paddle.png");
    }
    
    public void setState(PaddleState newState) {
        if (newState == currentState) return;
        
        currentState = newState;
        
        if (newState == PaddleState.NORMAL) {
            // Static sprite
            currentAnimation = null;
            staticSprite = provider.get("paddle.png");
        } else {
            // Animated state
            List<Image> frames = provider.getPaddleFrames(newState);
            
            boolean loop = newState != PaddleState.MATERIALIZE &&
                          newState != PaddleState.EXPLODE;
            
            currentAnimation = new Animation(frames, 50, loop);
            currentAnimation.start();
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        Image sprite = (currentAnimation != null) ?
            currentAnimation.getCurrentFrame() : staticSprite;
        
        g.drawImage(sprite, x, y, null);
    }
}
```

---

## Error Handling

### IllegalStateException for NORMAL State

```java
@Override
public List<Image> getPaddleFrames(PaddleState state) {
    if (state == PaddleState.NORMAL) {
        throw new IllegalStateException(
            "PaddleState.NORMAL uses a static sprite. " +
            "Use get(\"paddle.png\") instead.");
    }
    
    return switch (state) {
        // ... animated states
    };
}

// Caller should handle
public void loadPaddleAnimation(PaddleState state, SpriteProvider provider) {
    if (state == PaddleState.NORMAL) {
        // Use static sprite
        Image paddle = provider.get("paddle.png");
    } else {
        // Use animation
        List<Image> frames = provider.getPaddleFrames(state);
        Animation anim = new Animation(frames, 50, true);
    }
}
```

---

### Null Safety

```java
public class SafeSpriteCacheProvider implements SpriteProvider {
    private final SpriteCache cache;
    
    public SafeSpriteCacheProvider(SpriteCache cache) {
        if (cache == null) {
            throw new IllegalArgumentException("Cache cannot be null");
        }
        this.cache = cache;
    }
    
    @Override
    public Image get(String filename) {
        if (!cache.isInitialized()) {
            throw new IllegalStateException("Cache not initialized");
        }
        
        Image image = cache.getImage(filename);
        
        if (image == null) {
            throw new IllegalArgumentException("Image not found: " + filename);
        }
        
        return image;
    }
}
```

---

## Testing

### Unit Tests

```java
@Test
public void testProviderImplementation() {
    // Create mock cache
    SpriteCache mockCache = mock(SpriteCache.class);
    when(mockCache.isInitialized()).thenReturn(true);
    when(mockCache.getImage("ball.png")).thenReturn(new WritableImage(10, 10));
    
    // Create provider
    SpriteProvider provider = new SpriteCacheProvider(mockCache);
    
    // Test static sprite access
    Image ball = provider.get("ball.png");
    assertNotNull(ball);
    verify(mockCache).getImage("ball.png");
}

@Test
public void testPowerUpFramesRouting() {
    SpriteCache mockCache = mock(SpriteCache.class);
    List<Image> mockFrames = List.of(new WritableImage(10, 10));
    
    when(mockCache.getPowerUpCatchCache()).thenReturn(mockFrames);
    
    SpriteProvider provider = new SpriteCacheProvider(mockCache);
    
    // Test routing
    List<Image> frames = provider.getPowerUpFrames(PowerUpType.CATCH);
    
    assertEquals(mockFrames, frames);
    verify(mockCache).getPowerUpCatchCache();
}

@Test
public void testPaddleNormalStateException() {
    SpriteCache mockCache = mock(SpriteCache.class);
    SpriteProvider provider = new SpriteCacheProvider(mockCache);
    
    // Should throw exception
    assertThrows(IllegalStateException.class, () -> {
        provider.getPaddleFrames(PaddleState.NORMAL);
    });
}

@Test
public void testReadinessCheck() {
    SpriteCache mockCache = mock(SpriteCache.class);
    when(mockCache.isInitialized()).thenReturn(false);
    
    SpriteProvider provider = new SpriteCacheProvider(mockCache);
    
    assertFalse(provider.isReady());
    
    when(mockCache.isInitialized()).thenReturn(true);
    
    assertTrue(provider.isReady());
}
```

---

### Integration Test

```java
@Test
public void testFullIntegration() {
    // Real cache
    SpriteCache cache = SpriteCache.getInstance();
    cache.initialize();
    
    // Real provider
    SpriteProvider provider = new SpriteCacheProvider(cache);
    
    // Test readiness
    assertTrue(provider.isReady());
    
    // Test static sprite
    Image ball = provider.get("ball.png");
    assertNotNull(ball);
    
    // Test power-up frames
    List<Image> catchFrames = provider.getPowerUpFrames(PowerUpType.CATCH);
    assertNotNull(catchFrames);
    assertEquals(8, catchFrames.size());
    
    // Test paddle frames
    List<Image> wideFrames = provider.getPaddleFrames(PaddleState.WIDE);
    assertNotNull(wideFrames);
    
    // Test silver crack frames
    List<Image> crackFrames = provider.getSilverCrackFrames();
    assertNotNull(crackFrames);
    assertEquals(10, crackFrames.size());
}
```

---

## Best Practices

### 1. Check Readiness

```java
// ✅ Đúng - check readiness
if (provider.isReady()) {
    Image sprite = provider.get("ball.png");
} else {
    showLoadingScreen();
}

// ❌ Sai - assume ready
Image sprite = provider.get("ball.png");
// May fail if cache not initialized
```

---

### 2. Handle NORMAL State Correctly

```java
// ✅ Đúng - check state before calling
public void loadPaddleSprite(PaddleState state) {
    if (state == PaddleState.NORMAL) {
        Image paddle = provider.get("paddle.png");
    } else {
        List<Image> frames = provider.getPaddleFrames(state);
    }
}

// ❌ Sai - blindly call
List<Image> frames = provider.getPaddleFrames(state);
// Throws exception if state == NORMAL
```

---

### 3. Use Dependency Injection

```java
// ✅ Đúng - inject provider
public class GameObject {
    private final SpriteProvider provider;
    
    public GameObject(SpriteProvider provider) {
        this.provider = provider;
    }
}

// ❌ Sai - create provider internally
public class GameObject {
    private SpriteProvider provider = 
        new SpriteCacheProvider(SpriteCache.getInstance());
    // Hard to test, tightly coupled
}
```

---

### 4. Cache Provider Reference

```java
// ✅ Đúng - cache as field
public class GameManager {
    private final SpriteProvider sprites;
    
    public GameManager(SpriteProvider sprites) {
        this.sprites = sprites;
    }
}

// ❌ Sai - re-create repeatedly
public void render() {
    SpriteProvider provider = new SpriteCacheProvider(cache);
    // Wasteful object creation
}
```

---

## Design Patterns Analysis

### Adapter Pattern

```
Intent: Convert interface of class into another interface clients expect

Structure:
- Target: SpriteProvider (desired interface)
- Adapter: SpriteCacheProvider (converts interface)
- Adaptee: SpriteCache (existing class)
- Client: Game objects (use Target interface)

Benefits:
- Decouples clients from implementation
- Enables interface-based programming
- Supports dependency injection
- Improves testability
```

---

### Object Adapter vs Class Adapter

```java
// Object Adapter (composition) - Used in SpriteCacheProvider
public class SpriteCacheProvider implements SpriteProvider {
    private final SpriteCache cache; // Composition
    
    public SpriteCacheProvider(SpriteCache cache) {
        this.cache = cache;
    }
}

// Class Adapter (inheritance) - Not used, but possible
public class SpriteCacheProviderInheritance 
        extends SpriteCache implements SpriteProvider {
    // Uses inheritance instead of composition
    // Less flexible, not recommended
}
```

**Object Adapter Benefits**:
- More flexible (can adapt any SpriteCache instance)
- Supports runtime configuration
- Better encapsulation
- Preferred in most cases

---

### Strategy Pattern Connection

```java
// Adapter enables Strategy Pattern
public interface SpriteProvider {
    // Strategy for sprite provision
}

// Different strategies
public class SpriteCacheProvider implements SpriteProvider {
    // Cache-based strategy
}

public class LazyLoadProvider implements SpriteProvider {
    // On-demand loading strategy
}

public class RemoteProvider implements SpriteProvider {
    // Remote loading strategy
}

// Client works with any strategy
public class GameManager {
    private SpriteProvider sprites;
    
    public GameManager(SpriteProvider sprites) {
        this.sprites = sprites; // Strategy injected
    }
}
```

---

## Performance Characteristics

### Delegation Overhead

```
Method Call Chain:
provider.get("ball.png")
    → cache.getImage("ball.png")
        → imageCache.get("ball.png")
            → return Image

Overhead: ~1-2 nanoseconds per delegation
Impact: Negligible (method inlining by JIT)

Conclusion: Adapter pattern has minimal performance cost
```

---

### Switch Expression Performance

```
Switch Expression Compilation:
- Compiled to tableswitch (O(1) lookup)
- No string comparisons
- No hash lookups
- Direct array indexing by enum ordinal

Performance: ~1-2 nanoseconds per switch
Equivalent to: Direct method call

Conclusion: Switch expressions are highly efficient
```

---

## Kết luận

`SpriteCacheProvider` là **Adapter class** bridge giữa interface và implementation:

- **Adapter Pattern**: Converts SpriteCache API tới SpriteProvider interface
- **Clean Delegation**: Simple pass-through tới cache methods
- **Switch Expressions**: Type-safe, exhaustive enum routing
- **Error Handling**: Guards against invalid NORMAL state usage
- **Dependency Injection**: Constructor-injected cache instance
- **Interface Segregation**: Clean, minimal API
- **Performance**: Negligible overhead (~1-2 ns per call)

SpriteCacheProvider exemplifies **interface-based design best practices**. Bằng việc adapt concrete cache class tới abstract interface, code achieves loose coupling. Clients depend on `SpriteProvider` abstraction, không `SpriteCache` implementation. This enables dependency injection, testing với mocks, và future implementation swaps.

**Adapter Pattern Benefits**: SpriteCacheProvider demonstrates why Adapter pattern is valuable. Existing `SpriteCache` class has specific API (getPowerUpCatchCache(), getPowerUpExpandCache(), etc.). New `SpriteProvider` interface requires different API (getPowerUpFrames(type)). Adapter bridges this gap WITHOUT modifying either class. This preserves existing code và enables clean interface.

**Switch Expression Elegance**: Modern Java switch expressions make adapter routing clean và safe. Compiler enforces exhaustive enum coverage - adding new PowerUpType requires updating switch, preventing bugs. Type safety eliminates string-based errors. Concise syntax improves readability. Performance matches direct method calls after JIT compilation.

**Design Trade-offs**: Adapter adds indirection (provider → adapter → cache), but benefits outweigh costs. Indirection enables testing (mock providers), flexibility (swap implementations), và decoupling (interface dependency). Minimal performance overhead (~1-2 ns) is trivial compared tới sprite rendering (~1000+ ns). This is textbook example của "premature optimization" - choose design clarity over micro-optimizations.

**Architecture Significance**: SpriteCacheProvider is **keystone** của sprite management architecture. Interface (`SpriteProvider`) defines contract. Adapter (`SpriteCacheProvider`) implements contract. Cache (`SpriteCache`) provides storage. Clients depend only on interface. This layered design enables evolution - new caching strategies, lazy loading, remote sprites - WITHOUT changing client code. Separation của concerns at its finest.

