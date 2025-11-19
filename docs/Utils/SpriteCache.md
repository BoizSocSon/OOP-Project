# SpriteCache Class

## Tổng quan
`SpriteCache` là **Singleton class** quản lý loading và caching của tất cả sprite resources trong Arkanoid game. Class này implement **Eager Loading** strategy, loading tất cả sprites một lần khi khởi tạo để đảm bảo không có loading delays trong gameplay. SpriteCache sử dụng **combination of HashMap và ArrayList** để cache static sprites và animation frames, với performance monitoring và thread-safe initialization.

## Vị trí
- **Package**: `Utils`
- **File**: `src/Utils/SpriteCache.java`
- **Type**: Singleton Class (Eager Loading)
- **Pattern**: Singleton Pattern + Cache Pattern
- **Dependencies**: `AssetLoader`, `PowerUpType`, `PaddleState`, `BrickType`

## Mục đích
SpriteCache class:
- Load tất cả sprites khi initialization
- Cache sprites trong memory cho fast access
- Quản lý static sprites và animation frames
- Track performance metrics (load time, sprite count)
- Provide centralized sprite storage
- Ensure thread-safe initialization
- Support efficient sprite retrieval

---

## Class Structure

```java
public class SpriteCache {
    // Singleton instance
    private static SpriteCache instance = null;
    
    // Static sprite cache
    private HashMap<String, Image> imageCache;
    
    // Animation frame caches
    private List<Image> powerUpCatchCache;
    private List<Image> powerUpExpandCache;
    private List<Image> powerUpLaserCache;
    private List<Image> powerUpDuplicateCache;
    private List<Image> powerUpSlowCache;
    private List<Image> powerUpLifeCache;
    private List<Image> powerUpWarpCache;
    
    private List<Image> paddleWideCache;
    private List<Image> paddleWidePulsateCache;
    private List<Image> paddleLaserCache;
    private List<Image> paddleLaserPulsateCache;
    private List<Image> paddlePulsateCache;
    private List<Image> paddleMaterializeCache;
    private List<Image> paddleExplodeCache;
    
    private List<Image> silverCrackCache;
    
    // State tracking
    private boolean initialized = false;
    private int totalSprites = 0;
    
    // Private constructor
    private SpriteCache() {}
}
```

---

## Singleton Pattern

### getInstance() - Thread-Safe Singleton

```java
public static synchronized SpriteCache getInstance() {
    if (instance == null) {
        instance = new SpriteCache();
    }
    return instance;
}
```

**Features**:
- Thread-safe với `synchronized` keyword
- Lazy initialization (creates instance on first call)
- Single global instance

**Usage**:
```java
// Get singleton instance
SpriteCache cache = SpriteCache.getInstance();

// Initialize cache
cache.initialize();

// Access sprites
Image ball = cache.getImage("ball.png");
```

---

## Initialization

### initialize() - Main Loading Method

```java
public synchronized void initialize() {
    if (initialized) {
        return; // Prevent double initialization
    }
    
    System.out.println("Initializing SpriteCache...");
    long startTime = System.currentTimeMillis();
    
    // Initialize collections
    imageCache = new HashMap<>();
    powerUpCatchCache = new ArrayList<>();
    powerUpExpandCache = new ArrayList<>();
    // ... (14 more ArrayLists)
    
    // Load all sprites
    loadBrickSprites();
    loadPowerUpSprites();
    loadPaddleSprites();
    loadBallSprite();
    loadLaserSprites();
    loadEdgeSprites();
    loadLogoSprite();
    
    // Calculate stats
    long elapsedTime = System.currentTimeMillis() - startTime;
    totalSprites = calculateTotalSprites();
    
    initialized = true;
    
    System.out.println("SpriteCache initialized successfully.");
    System.out.println("Total sprites loaded: " + totalSprites);
    System.out.println("Load time: " + elapsedTime + "ms");
}
```

**Process**:
1. Check if already initialized (idempotent)
2. Create HashMap và ArrayLists
3. Load sprites by category
4. Calculate performance metrics
5. Set initialized flag
6. Print statistics

**Example Output**:
```
Initializing SpriteCache...
Loading 7 brick types...
Loading 7 power-up types (8 frames each)...
Loading 8 paddle states...
Total sprites loaded: 142
Load time: 234ms
SpriteCache initialized successfully.
```

---

## Loading Methods

### loadBrickSprites() - Load All Brick Types

```java
private void loadBrickSprites() {
    // Load all brick type sprites
    for (BrickType type : BrickType.values()) {
        String filename = type.getFilename();
        Image sprite = AssetLoader.loadImage(filename);
        imageCache.put(filename, sprite);
    }
    
    System.out.println("Loaded " + BrickType.values().length + " brick sprites");
}
```

**Sprites Loaded**:
- `brick_red.png`
- `brick_orange.png`
- `brick_yellow.png`
- `brick_green.png`
- `brick_blue.png`
- `brick_purple.png`
- `brick_silver.png`
- `brick_gold.png`

**Total**: 8 brick sprites

---

### loadPowerUpSprites() - Load All Power-Up Animations

```java
private void loadPowerUpSprites() {
    // Each power-up has 8 animation frames
    int framesPerPowerUp = 8;
    
    // CATCH power-up
    for (int i = 1; i <= framesPerPowerUp; i++) {
        String filename = "powerup_catch_" + i + ".png";
        Image frame = AssetLoader.loadImage(filename);
        powerUpCatchCache.add(frame);
    }
    
    // EXPAND power-up
    for (int i = 1; i <= framesPerPowerUp; i++) {
        String filename = "powerup_expand_" + i + ".png";
        Image frame = AssetLoader.loadImage(filename);
        powerUpExpandCache.add(frame);
    }
    
    // ... (5 more power-up types)
    
    System.out.println("Loaded 7 power-up types (56 frames total)");
}
```

**Power-Up Types**:
1. **CATCH** - 8 frames (`powerup_catch_1.png` to `powerup_catch_8.png`)
2. **EXPAND** - 8 frames
3. **LASER** - 8 frames
4. **DUPLICATE** - 8 frames
5. **SLOW** - 8 frames
6. **LIFE** - 8 frames
7. **WARP** - 8 frames

**Total**: 7 types × 8 frames = **56 frames**

---

### loadPaddleSprites() - Load Paddle Animations

```java
private void loadPaddleSprites() {
    // NORMAL state (static sprite)
    Image normalPaddle = AssetLoader.loadImage("paddle.png");
    imageCache.put("paddle.png", normalPaddle);
    
    // WIDE state animation (8 frames)
    for (int i = 1; i <= 8; i++) {
        String filename = "paddle_wide_" + i + ".png";
        Image frame = AssetLoader.loadImage(filename);
        paddleWideCache.add(frame);
    }
    
    // WIDE_PULSATE state animation (8 frames)
    for (int i = 1; i <= 8; i++) {
        String filename = "paddle_wide_pulsate_" + i + ".png";
        Image frame = AssetLoader.loadImage(filename);
        paddleWidePulsateCache.add(frame);
    }
    
    // LASER state animation
    for (int i = 1; i <= 8; i++) {
        String filename = "paddle_laser_" + i + ".png";
        Image frame = AssetLoader.loadImage(filename);
        paddleLaserCache.add(frame);
    }
    
    // ... (4 more paddle states)
    
    System.out.println("Loaded 8 paddle states");
}
```

**Paddle States**:
1. **NORMAL** - Static sprite (`paddle.png`)
2. **WIDE** - 8 frames
3. **WIDE_PULSATE** - 8 frames
4. **LASER** - 8 frames
5. **LASER_PULSATE** - 8 frames
6. **PULSATE** - 8 frames
7. **MATERIALIZE** - 8 frames (spawn animation)
8. **EXPLODE** - 8 frames (death animation)

**Total**: 1 static + 7 animated states × 8 frames = **57 frames**

---

### loadBallSprite() - Load Ball Sprite

```java
private void loadBallSprite() {
    Image ballSprite = AssetLoader.loadImage("ball.png");
    imageCache.put("ball.png", ballSprite);
    
    System.out.println("Loaded ball sprite");
}
```

**Sprite**: `ball.png` (static sprite)

---

### loadLaserSprites() - Load Laser Beam Sprites

```java
private void loadLaserSprites() {
    // Left laser beam
    Image leftLaser = AssetLoader.loadImage("laser_left.png");
    imageCache.put("laser_left.png", leftLaser);
    
    // Right laser beam
    Image rightLaser = AssetLoader.loadImage("laser_right.png");
    imageCache.put("laser_right.png", rightLaser);
    
    System.out.println("Loaded 2 laser sprites");
}
```

**Sprites**:
- `laser_left.png` - Left cannon projectile
- `laser_right.png` - Right cannon projectile

---

### loadEdgeSprites() - Load Border Edge Sprites

```java
private void loadEdgeSprites() {
    // Top edge
    Image topEdge = AssetLoader.loadImage("edge_top.png");
    imageCache.put("edge_top.png", topEdge);
    
    // Left edge
    Image leftEdge = AssetLoader.loadImage("edge_left.png");
    imageCache.put("edge_left.png", leftEdge);
    
    // Right edge
    Image rightEdge = AssetLoader.loadImage("edge_right.png");
    imageCache.put("edge_right.png", rightEdge);
    
    System.out.println("Loaded 3 edge sprites");
}
```

**Sprites**:
- `edge_top.png` - Top border
- `edge_left.png` - Left border
- `edge_right.png` - Right border

---

### loadLogoSprite() - Load Logo

```java
private void loadLogoSprite() {
    Image logo = AssetLoader.loadImage("logo.png");
    imageCache.put("logo.png", logo);
    
    System.out.println("Loaded logo sprite");
}
```

**Sprite**: `logo.png` - Game logo (title screen)

---

### Silver Crack Animation

```java
private void loadSilverCrackAnimation() {
    // 10 frames showing progressive damage
    for (int i = 1; i <= 10; i++) {
        String filename = "brick_silver_" + i + ".png";
        Image frame = AssetLoader.loadImage(filename);
        silverCrackCache.add(frame);
    }
    
    System.out.println("Loaded silver crack animation (10 frames)");
}
```

**Frames**: `brick_silver_1.png` to `brick_silver_10.png` (damage progression)

---

## Getter Methods

### Static Sprite Access

```java
public Image getImage(String filename) {
    return imageCache.get(filename);
}

// Usage
Image ball = cache.getImage("ball.png");
Image paddle = cache.getImage("paddle.png");
Image redBrick = cache.getImage("brick_red.png");
Image leftLaser = cache.getImage("laser_left.png");
```

---

### Animation Frame Access

#### Power-Up Animations

```java
public List<Image> getPowerUpCatchCache() {
    return powerUpCatchCache;
}

public List<Image> getPowerUpExpandCache() {
    return powerUpExpandCache;
}

public List<Image> getPowerUpLaserCache() {
    return powerUpLaserCache;
}

public List<Image> getPowerUpDuplicateCache() {
    return powerUpDuplicateCache;
}

public List<Image> getPowerUpSlowCache() {
    return powerUpSlowCache;
}

public List<Image> getPowerUPLifeCache() {
    return powerUpLifeCache;
}

public List<Image> getPowerUpWarpCache() {
    return powerUpWarpCache;
}

// Usage
List<Image> catchFrames = cache.getPowerUpCatchCache();
Animation catchAnim = new Animation(catchFrames, 100, true);
```

---

#### Paddle Animations

```java
public List<Image> getPaddleWideCache() {
    return paddleWideCache;
}

public List<Image> getPaddleWidePulsateCache() {
    return paddleWidePulsateCache;
}

public List<Image> getPaddleLaserCache() {
    return paddleLaserCache;
}

public List<Image> getPaddleLaserPulsateCache() {
    return paddleLaserPulsateCache;
}

public List<Image> getPaddlePulsateCache() {
    return paddlePulsateCache;
}

public List<Image> getPaddleMaterializeCache() {
    return paddleMaterializeCache;
}

public List<Image> getPaddleExplodeCache() {
    return paddleExplodeCache;
}

// Usage
List<Image> wideFrames = cache.getPaddleWideCache();
Animation wideAnim = new Animation(wideFrames, 50, false);
```

---

#### Silver Crack Animation

```java
public List<Image> getSilverCrackCache() {
    return silverCrackCache;
}

// Usage
List<Image> crackFrames = cache.getSilverCrackCache();
Animation crackAnim = new Animation(crackFrames, 20, false);
```

---

## Utility Methods

### isInitialized() - Check Initialization Status

```java
public boolean isInitialized() {
    return initialized;
}

// Usage
SpriteCache cache = SpriteCache.getInstance();

if (!cache.isInitialized()) {
    cache.initialize();
}

// Defensive check
if (cache.isInitialized()) {
    Image sprite = cache.getImage("ball.png");
} else {
    showLoadingScreen();
}
```

---

### getTotalSprites() - Get Sprite Count

```java
public int getTotalSprites() {
    return totalSprites;
}

// Usage
int count = cache.getTotalSprites();
System.out.println("Total sprites in cache: " + count);
// Output: Total sprites in cache: 142
```

---

### clear() - Clear Cache

```java
public void clear() {
    if (imageCache != null) {
        imageCache.clear();
    }
    
    // Clear all animation caches
    if (powerUpCatchCache != null) powerUpCatchCache.clear();
    if (powerUpExpandCache != null) powerUpExpandCache.clear();
    // ... (clear all other caches)
    
    initialized = false;
    totalSprites = 0;
    
    System.out.println("SpriteCache cleared");
}

// Usage
cache.clear(); // Free memory
cache.initialize(); // Reload if needed
```

---

### printCacheStatus() - Debug Information

```java
public void printCacheStatus() {
    System.out.println("=== SpriteCache Status ===");
    System.out.println("Initialized: " + initialized);
    System.out.println("Total sprites: " + totalSprites);
    
    if (initialized) {
        System.out.println("\nCache Contents:");
        System.out.println("- Static sprites: " + imageCache.size());
        System.out.println("- PowerUp CATCH frames: " + powerUpCatchCache.size());
        System.out.println("- PowerUp EXPAND frames: " + powerUpExpandCache.size());
        // ... (print all caches)
    }
    
    System.out.println("=========================");
}

// Example output:
// === SpriteCache Status ===
// Initialized: true
// Total sprites: 142
// 
// Cache Contents:
// - Static sprites: 15
// - PowerUp CATCH frames: 8
// - PowerUp EXPAND frames: 8
// ...
// =========================
```

---

## Usage Patterns

### Pattern 1: Singleton Initialization

```java
public class GameApplication {
    public void start() {
        // Get singleton instance
        SpriteCache cache = SpriteCache.getInstance();
        
        // Initialize (load all sprites)
        if (!cache.isInitialized()) {
            System.out.println("Loading game assets...");
            cache.initialize();
            System.out.println("Assets loaded successfully!");
        }
        
        // Cache is ready for use
        startGame();
    }
}
```

---

### Pattern 2: With SpriteProvider

```java
public class GameManager {
    private SpriteProvider sprites;
    
    public void initialize() {
        // Initialize cache
        SpriteCache cache = SpriteCache.getInstance();
        cache.initialize();
        
        // Create provider
        sprites = new SpriteCacheProvider(cache);
        
        // Pass provider to components
        AnimationFactory.initialize(sprites);
        
        // Access sprites through provider
        Image ball = sprites.get("ball.png");
    }
}
```

---

### Pattern 3: Direct Access

```java
public class GameObject {
    protected Image sprite;
    
    public void loadSprite(String filename) {
        SpriteCache cache = SpriteCache.getInstance();
        
        if (cache.isInitialized()) {
            sprite = cache.getImage(filename);
        } else {
            System.err.println("Cache not initialized!");
        }
    }
}

// Usage
Ball ball = new Ball();
ball.loadSprite("ball.png");
```

---

### Pattern 4: Animation Creation

```java
public class PowerUp {
    private Animation animation;
    
    public PowerUp(PowerUpType type) {
        SpriteCache cache = SpriteCache.getInstance();
        
        List<Image> frames = switch (type) {
            case CATCH -> cache.getPowerUpCatchCache();
            case EXPAND -> cache.getPowerUpExpandCache();
            case LASER -> cache.getPowerUpLaserCache();
            case DUPLICATE -> cache.getPowerUpDuplicateCache();
            case SLOW -> cache.getPowerUpSlowCache();
            case LIFE -> cache.getPowerUPLifeCache();
            case WARP -> cache.getPowerUpWarpCache();
        };
        
        animation = new Animation(frames, 100, true);
        animation.start();
    }
}
```

---

## Performance Characteristics

### Memory Usage

```
Static Sprites (HashMap):
- Bricks: 8 sprites
- Ball: 1 sprite
- Paddle: 1 sprite (NORMAL)
- Lasers: 2 sprites
- Edges: 3 sprites
- Logo: 1 sprite
Total: ~16 static sprites

Animation Frames (ArrayLists):
- Power-ups: 7 types × 8 frames = 56 frames
- Paddle states: 7 states × 8 frames = 56 frames
- Silver crack: 10 frames
Total: ~122 animation frames

Grand Total: ~138-142 sprites in memory
Estimated Memory: 5-10 MB (depends on sprite sizes)
```

---

### Load Time

```
Typical Performance:
- Small sprites (16×16): ~0.5ms per sprite
- Medium sprites (32×32): ~1ms per sprite
- Large sprites (64×64): ~2ms per sprite

Total Load Time: 150-300ms (for ~140 sprites)

Factors Affecting Load Time:
- Disk I/O speed
- Image format (PNG compression)
- Sprite dimensions
- System memory available
```

---

### Access Time

```
HashMap Access (Static Sprites):
- O(1) average case
- ~1-2 nanoseconds per lookup
- No loading delay

ArrayList Access (Animation Frames):
- O(1) indexed access
- ~1 nanosecond per frame
- No loading delay

Conclusion: Eager loading eliminates gameplay delays
```

---

## Thread Safety

### Synchronized Methods

```java
public static synchronized SpriteCache getInstance() {
    // Thread-safe singleton creation
}

public synchronized void initialize() {
    // Thread-safe initialization
}
```

**Thread Safety Features**:
- `synchronized` keywords prevent race conditions
- Singleton instance created only once
- Initialization called only once (idempotent)
- Safe for multi-threaded environments

---

### Thread-Safe Usage

```java
// Thread 1
SpriteCache cache1 = SpriteCache.getInstance();
cache1.initialize();

// Thread 2 (concurrent)
SpriteCache cache2 = SpriteCache.getInstance();
cache2.initialize();

// Result: Same instance, initialized only once
assert cache1 == cache2; // ✅ True
assert cache1.isInitialized(); // ✅ True
```

---

## Best Practices

### 1. Initialize Early

```java
// ✅ Đúng - initialize at startup
public void startGame() {
    SpriteCache cache = SpriteCache.getInstance();
    cache.initialize(); // Load all sprites
    
    // Now safe to use
    startGameplay();
}

// ❌ Sai - initialize on-demand
public void render() {
    SpriteCache cache = SpriteCache.getInstance();
    // May not be initialized - cause delays
    Image sprite = cache.getImage("ball.png");
}
```

---

### 2. Check Initialization

```java
// ✅ Đúng - check before use
SpriteCache cache = SpriteCache.getInstance();

if (cache.isInitialized()) {
    Image sprite = cache.getImage("ball.png");
} else {
    showLoadingScreen();
}

// ❌ Sai - assume initialized
Image sprite = cache.getImage("ball.png");
// May return null if not initialized
```

---

### 3. Use Provider Pattern

```java
// ✅ Đúng - use provider for decoupling
SpriteCache cache = SpriteCache.getInstance();
cache.initialize();

SpriteProvider provider = new SpriteCacheProvider(cache);
GameManager manager = new GameManager(provider);

// ❌ Sai - directly couple to cache
public class GameManager {
    private SpriteCache cache = SpriteCache.getInstance();
    // Tightly coupled
}
```

---

### 4. Memory Management

```java
// ✅ Đúng - clear when needed
public void exitGame() {
    SpriteCache cache = SpriteCache.getInstance();
    cache.clear(); // Free memory
}

// ✅ Reload if returning to game
public void returnToGame() {
    SpriteCache cache = SpriteCache.getInstance();
    
    if (!cache.isInitialized()) {
        cache.initialize(); // Reload sprites
    }
}

// ❌ Sai - never clear (memory leak)
// Cache holds references forever
```

---

## Error Handling

### Null Checks

```java
public Image getImage(String filename) {
    if (imageCache == null) {
        System.err.println("Cache not initialized!");
        return null;
    }
    
    Image image = imageCache.get(filename);
    
    if (image == null) {
        System.err.println("Image not found: " + filename);
    }
    
    return image;
}

// Usage with defensive check
Image sprite = cache.getImage("ball.png");

if (sprite != null) {
    g.drawImage(sprite, x, y, null);
} else {
    // Fallback rendering
    g.setColor(Color.RED);
    g.fillOval(x, y, 10, 10);
}
```

---

### Loading Failures

```java
public void initialize() {
    try {
        System.out.println("Initializing SpriteCache...");
        
        // Load sprites
        loadBrickSprites();
        loadPowerUpSprites();
        // ...
        
        initialized = true;
        System.out.println("Initialization successful");
        
    } catch (Exception e) {
        System.err.println("Failed to initialize SpriteCache!");
        e.printStackTrace();
        initialized = false;
        
        // Cleanup partial loading
        clear();
    }
}
```

---

## Testing

### Unit Test Example

```java
@Test
public void testSingletonPattern() {
    SpriteCache cache1 = SpriteCache.getInstance();
    SpriteCache cache2 = SpriteCache.getInstance();
    
    assertSame(cache1, cache2); // Same instance
}

@Test
public void testInitialization() {
    SpriteCache cache = SpriteCache.getInstance();
    
    assertFalse(cache.isInitialized());
    
    cache.initialize();
    
    assertTrue(cache.isInitialized());
    assertTrue(cache.getTotalSprites() > 0);
}

@Test
public void testSpriteRetrieval() {
    SpriteCache cache = SpriteCache.getInstance();
    cache.initialize();
    
    Image ball = cache.getImage("ball.png");
    assertNotNull(ball);
    
    List<Image> catchFrames = cache.getPowerUpCatchCache();
    assertNotNull(catchFrames);
    assertEquals(8, catchFrames.size());
}

@Test
public void testClear() {
    SpriteCache cache = SpriteCache.getInstance();
    cache.initialize();
    
    assertTrue(cache.isInitialized());
    
    cache.clear();
    
    assertFalse(cache.isInitialized());
    assertEquals(0, cache.getTotalSprites());
}
```

---

## Design Patterns

### Singleton Pattern

```
Purpose: Ensure single instance
Benefits:
- Global access point
- Controlled instantiation
- Shared resource management

Implementation:
- Private constructor
- Static instance variable
- Public getInstance() method
- Synchronized for thread safety
```

---

### Cache Pattern

```
Purpose: Store expensive-to-load resources
Benefits:
- Fast access (no I/O)
- Reduced load times
- Memory efficiency (single copy)

Implementation:
- HashMap for key-value lookup
- ArrayList for ordered sequences
- Eager loading (initialize once)
- O(1) retrieval
```

---

### Eager Initialization

```
Purpose: Load all resources upfront
Benefits:
- No gameplay delays
- Predictable load time
- Simple error handling

Drawbacks:
- Higher startup time
- More memory usage
- Not suitable for large assets

Alternative: Lazy loading (load on-demand)
```

---

## Kết luận

`SpriteCache` là **core resource management class** trong Arkanoid:

- **Singleton Pattern**: Single global cache instance
- **Eager Loading**: Load all sprites at startup
- **Dual Storage**: HashMap (static) + ArrayLists (animations)
- **Performance**: ~140 sprites loaded in 150-300ms
- **Thread Safety**: Synchronized initialization
- **Memory Efficient**: Single copy per sprite
- **Fast Access**: O(1) retrieval time

SpriteCache exemplifies **resource management best practices**. Bằng việc load tất cả sprites một lần, game eliminates loading delays trong gameplay. Singleton pattern ensures single shared cache, reducing memory overhead. HashMap và ArrayList provide O(1) access time, critical cho 60 FPS rendering. Thread-safe initialization prevents race conditions trong multi-threaded environments.

**Performance Trade-offs**: Eager loading increases startup time nhưng guarantees smooth gameplay. For Arkanoid với ~140 sprites, this trade-off is acceptable (200-300ms load time). Alternative lazy loading would reduce startup time nhưng cause frame drops khi sprites first accessed. Eager loading is preferred cho games với moderate asset counts.

**Memory Management**: Cache holds ~5-10 MB of sprites trong memory. This is trivial trên modern systems (GB of RAM). Clear() method provided cho memory cleanup khi exiting game. Singleton pattern prevents duplicate caches, ensuring single copy per sprite. ArrayLists reused for animation frames, avoiding object creation overhead.

**Design Excellence**: SpriteCache demonstrates **separation of concerns**. Loading logic isolated trong private methods. Access methods provide clean API. Initialization separated từ construction (two-phase initialization). Provider pattern decouples consumers từ cache. This architecture makes system maintainable, testable, và extensible.

