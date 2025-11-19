# PowerUpType Enum

## Tổng quan
`PowerUpType` là **enum** định nghĩa tất cả 7 loại power-up có thể xuất hiện trong Arkanoid. Enum này đóng vai trò như một **registry** và **factory configuration** cho power-up system, chứa metadata về mỗi loại power-up (sprite prefix, spawn probability, duration). Nó cung cấp weighted random selection algorithm để spawn power-ups với xác suất khác nhau, reflecting game balance và strategic importance của từng loại.

## Vị trí
- **Package**: `Objects.PowerUps`
- **File**: `src/Objects/PowerUps/PowerUpType.java`
- **Type**: Java Enum
- **Dependencies**: `Utils.Constants`

## Mục đích
PowerUpType:
- Định nghĩa tất cả power-up types
- Lưu trữ configuration (sprite, spawn rate)
- Implement weighted random selection
- Provide duration information
- Classify instant vs timed effects
- Centralize power-up metadata

---

## Enum Constants

### Overview Table

| Constant | Sprite Prefix | Spawn Chance | Duration | Type | Effect |
|----------|---------------|--------------|----------|------|--------|
| **CATCH** | `powerup_catch` | 15% (0.15) | 15-20s | Timed | Bắt và giữ bóng |
| **DUPLICATE** | `powerup_duplicate` | 12% (0.12) | N/A | Instant | Nhân đôi bóng |
| **EXPAND** | `powerup_expand` | 15% (0.15) | 15-20s | Timed | Mở rộng paddle |
| **LASER** | `powerup_laser` | 15% (0.15) | 15-20s | Timed | Bắn laser |
| **LIFE** | `powerup_life` | 5% (0.05) | N/A | Instant | Thêm 1 mạng |
| **SLOW** | `powerup_slow` | 15% (0.15) | 15-20s | Timed | Làm chậm bóng |
| **WARP** | `powerup_warp` | 1% (0.01) | N/A | Instant | Skip level |

**Total Weight**: 0.15 + 0.12 + 0.15 + 0.15 + 0.05 + 0.15 + 0.01 = **0.78**

---

## Enum Definition

```java
public enum PowerUpType {
    CATCH("powerup_catch", 0.15),      // 15% - Catch ball
    DUPLICATE("powerup_duplicate", 0.12), // 12% - Duplicate balls
    EXPAND("powerup_expand", 0.15),    // 15% - Expand paddle
    LASER("powerup_laser", 0.15),      // 15% - Shoot lasers
    LIFE("powerup_life", 0.05),        // 5% - Extra life (rare)
    SLOW("powerup_slow", 0.15),        // 15% - Slow balls
    WARP("powerup_warp", 0.01);        // 1% - Skip level (very rare)
    
    private final String powerupPrefix;
    private final double spawnChance;
}
```

---

## Constructor

### `PowerUpType(String powerupPrefix, double spawnChance)`

**Mô tả**: Private constructor để khởi tạo enum constant với metadata.

**Tham số**:
- `powerupPrefix` - Prefix cho sprite files (e.g., "powerup_catch")
- `spawnChance` - Spawn probability (0.0 - 1.0)

**Ví dụ**:
```java
// Trong enum definition
CATCH("powerup_catch", 0.15)
// → powerupPrefix = "powerup_catch"
// → spawnChance = 0.15 (15%)
```

---

## Phương thức

### 1. `String getFramePath(int frameNumber)`

**Mô tả**: Tạo đường dẫn file sprite cho một animation frame cụ thể.

**Tham số**: `frameNumber` - Index của frame (0, 1, 2, ...)

**Trả về**: String path đến sprite file.

**Công thức**:
```java
return powerupPrefix + "_" + frameNumber + ".png";
```

**Ví dụ**:
```java
// CATCH power-up, frame 0
PowerUpType.CATCH.getFramePath(0);
// → "powerup_catch_0.png"

// LASER power-up, frame 2
PowerUpType.LASER.getFramePath(2);
// → "powerup_laser_2.png"

// LIFE power-up, frame 1
PowerUpType.LIFE.getFramePath(1);
// → "powerup_life_1.png"
```

**Sử dụng trong Animation**:
```java
// Load animation frames cho power-up
List<BufferedImage> frames = new ArrayList<>();
for (int i = 0; i < POWERUP_FRAME_COUNT; i++) {
    String path = type.getFramePath(i);
    BufferedImage image = assetLoader.loadImage(path);
    frames.add(image);
}
Animation animation = new Animation(frames, POWERUP_FRAME_DELAY);
```

---

### 2. `boolean isInstant()`

**Mô tả**: Kiểm tra xem power-up có phải là instant effect hay không.

**Trả về**: `true` nếu instant (không có duration), `false` nếu timed.

**Logic**:
```java
public boolean isInstant() {
    return this == LIFE || this == WARP || this == DUPLICATE;
}
```

**Classification**:
- **Instant** (3): LIFE, WARP, DUPLICATE
  - Effect xảy ra ngay lập tức
  - Không có expiration timer
  - Không cần cleanup/removal
  
- **Timed** (4): CATCH, EXPAND, LASER, SLOW
  - Effect có duration
  - Cần tracking expiration
  - Phải remove effect khi hết hạn

**Ví dụ**:
```java
// Check power-up type
if (type.isInstant()) {
    System.out.println("Instant effect - no timer needed");
    // Apply effect immediately
    // No need to track expiration
} else {
    System.out.println("Timed effect - start timer");
    // Apply effect
    // Schedule removal after duration
    long expiryTime = System.currentTimeMillis() + type.getDuration();
}
```

---

### 3. `long getDuration()`

**Mô tả**: Lấy thời gian hiệu lực của power-up (milliseconds).

**Trả về**: Duration trong milliseconds, hoặc `0L` cho instant effects.

**Logic**:
```java
public long getDuration() {
    switch (this) {
        case CATCH:
            return Constants.PowerUps.CATCH_DURATION;    // 15000-20000ms
        case EXPAND:
            return Constants.PowerUps.EXPAND_DURATION;   // 15000-20000ms
        case LASER:
            return Constants.PowerUps.LASER_DURATION;    // 15000-20000ms
        case SLOW:
            return Constants.PowerUps.SLOW_DURATION;     // 15000-20000ms
        default:
            // Instant effects (LIFE, DUPLICATE, WARP)
            return 0L;
    }
}
```

**Ví dụ**:
```java
// Get duration for timer display
long duration = type.getDuration();
if (duration > 0) {
    // Show countdown timer
    int seconds = (int) (duration / 1000);
    System.out.println("Effect lasts " + seconds + " seconds");
    
    // Update UI
    uiManager.showPowerUpTimer(type, duration);
}
```

---

### 4. `static PowerUpType randomWeighted()` ⭐

**Mô tả**: Chọn ngẫu nhiên power-up type dựa trên weighted probability.

**Trả về**: PowerUpType được chọn theo trọng số.

**Algorithm**: Weighted Random Selection (Roulette Wheel Selection)

**Steps**:
```java
public static PowerUpType randomWeighted() {
    // Step 1: Calculate total weight
    double totalWeight = 0.0;
    for (PowerUpType type : PowerUpType.values()) {
        totalWeight += type.spawnChance;
    }
    // totalWeight = 0.78
    
    // Step 2: Random value [0, totalWeight)
    double randomValue = Math.random() * totalWeight;
    // randomValue = 0.0 đến 0.78
    
    // Step 3: Find corresponding type
    double cumulativeWeight = 0.0;
    for (PowerUpType type : PowerUpType.values()) {
        cumulativeWeight += type.spawnChance;
        if (randomValue <= cumulativeWeight) {
            return type; // Found it!
        }
    }
    
    // Fallback (should never happen)
    return EXPAND;
}
```

**Visual Representation**:
```
Probability Distribution (Roulette Wheel):

0.00                                                   0.78
 |----CATCH----|DUPLICATE|-EXPAND--|--LASER--|L|--SLOW---|W
 0.00    0.15   0.27   0.42   0.57  0.62  0.77 0.78
        15%     12%    15%    15%   5%    15%  1%

Legend:
- CATCH:     0.00 - 0.15 (15%)
- DUPLICATE: 0.15 - 0.27 (12%)
- EXPAND:    0.27 - 0.42 (15%)
- LASER:     0.42 - 0.57 (15%)
- LIFE:      0.57 - 0.62 (5%)  [L - smaller segment]
- SLOW:      0.62 - 0.77 (15%)
- WARP:      0.77 - 0.78 (1%)  [W - tiny segment]
```

**Ví dụ**:
```java
// Example random values and results:

randomValue = 0.10 → CATCH (in 0.00-0.15 range)
randomValue = 0.20 → DUPLICATE (in 0.15-0.27 range)
randomValue = 0.35 → EXPAND (in 0.27-0.42 range)
randomValue = 0.50 → LASER (in 0.42-0.57 range)
randomValue = 0.60 → LIFE (in 0.57-0.62 range)
randomValue = 0.70 → SLOW (in 0.62-0.77 range)
randomValue = 0.775 → WARP (in 0.77-0.78 range) [RARE!]
```

**Sử dụng**:
```java
// Spawn power-up when brick destroyed
if (shouldSpawnPowerUp()) {
    PowerUpType type = PowerUpType.randomWeighted();
    
    double x = brick.getX() + brick.getWidth() / 2;
    double y = brick.getY();
    
    PowerUp powerUp = createPowerUp(type, x, y);
    powerUps.add(powerUp);
}
```

---

### 5. `String getSpritePrefix()`

**Mô tả**: Getter cho sprite prefix.

**Trả về**: Sprite prefix string.

**Ví dụ**:
```java
String prefix = PowerUpType.CATCH.getSpritePrefix();
// → "powerup_catch"
```

---

### 6. `double getSpawnChance()`

**Mô tả**: Getter cho spawn probability.

**Trả về**: Spawn chance (0.0 - 1.0).

**Ví dụ**:
```java
double chance = PowerUpType.WARP.getSpawnChance();
// → 0.01 (1%)

System.out.println("WARP spawns " + (chance * 100) + "% of the time");
// → "WARP spawns 1.0% of the time"
```

---

## Probability Analysis

### Expected Spawns per 100 Power-Ups

```java
// Given 100 power-ups spawned:
// (Assuming uniform distribution over weighted selection)

CATCH:     15 spawns (15%)   ███████████████
DUPLICATE: 12 spawns (12%)   ████████████
EXPAND:    15 spawns (15%)   ███████████████
LASER:     15 spawns (15%)   ███████████████
LIFE:       5 spawns (5%)    █████
SLOW:      15 spawns (15%)   ███████████████
WARP:       1 spawn  (1%)    █

Total: 78 spawns (out of 100 attempts)
Why not 100? Because total weight = 0.78, not 1.0
```

---

### Rarity Tiers

```java
public enum RarityTier {
    COMMON,    // 15% - CATCH, EXPAND, LASER, SLOW
    UNCOMMON,  // 12% - DUPLICATE
    RARE,      // 5%  - LIFE
    VERY_RARE  // 1%  - WARP
}

public RarityTier getRarityTier() {
    if (spawnChance >= 0.15) return RarityTier.COMMON;
    if (spawnChance >= 0.10) return RarityTier.UNCOMMON;
    if (spawnChance >= 0.05) return RarityTier.RARE;
    return RarityTier.VERY_RARE;
}
```

---

## Power-Up Factory

### Factory Method Pattern

```java
// Factory để create power-up instances
public static PowerUp createPowerUp(PowerUpType type, double x, double y) {
    switch (type) {
        case CATCH:
            return new CatchPowerUp(x, y);
        case DUPLICATE:
            return new DuplicatePowerUp(x, y);
        case EXPAND:
            return new ExpandPaddlePowerUp(x, y);
        case LASER:
            return new LaserPowerUp(x, y);
        case LIFE:
            return new LifePowerUp(x, y);
        case SLOW:
            return new SlowBallPowerUp(x, y);
        case WARP:
            return new WarpPowerUp(x, y);
        default:
            throw new IllegalArgumentException("Unknown power-up type: " + type);
    }
}
```

---

## Spawn Logic Integration

### Complete Spawn System

```java
public class PowerUpManager {
    // Probability that ANY power-up spawns when brick destroyed
    private static final double POWERUP_SPAWN_RATE = 0.20; // 20%
    
    public void onBrickDestroyed(Brick brick) {
        // Step 1: Should we spawn a power-up?
        if (Math.random() < POWERUP_SPAWN_RATE) {
            
            // Step 2: Which type? (weighted selection)
            PowerUpType type = PowerUpType.randomWeighted();
            
            // Step 3: Create instance
            double x = brick.getX() + brick.getWidth() / 2;
            double y = brick.getY();
            PowerUp powerUp = createPowerUp(type, x, y);
            
            // Step 4: Add to game
            powerUps.add(powerUp);
            
            // Step 5: Log (for debugging)
            System.out.println("Spawned " + type + 
                " power-up (" + (type.getSpawnChance() * 100) + "%)");
        }
    }
}
```

---

### Two-Stage Randomness

```java
// Stage 1: Should power-up spawn? (20% chance)
if (Math.random() < 0.20) {
    // YES - proceed to stage 2
    
    // Stage 2: Which type? (weighted by spawn chances)
    PowerUpType type = PowerUpType.randomWeighted();
    
    // Combined probability:
    // P(CATCH spawns) = P(any spawns) × P(CATCH | spawned)
    //                  = 0.20 × 0.15
    //                  = 0.03 (3% per brick)
    
    // P(WARP spawns)  = 0.20 × 0.01
    //                  = 0.002 (0.2% per brick)
}
```

---

## Balance Considerations

### Why These Spawn Rates?

```java
// COMMON (15%): Safe, useful, gameplay-enhancing
CATCH, EXPAND, LASER, SLOW
→ 15% each because:
  - No downside
  - Help all players
  - Keep game interesting
  - Balanced with each other

// UNCOMMON (12%): Powerful but risky
DUPLICATE
→ 12% because:
  - Can cause chaos (too many balls)
  - Helpful when controlled
  - Slightly rarer than safe options

// RARE (5%): Very powerful, permanent
LIFE
→ 5% because:
  - Most valuable (extra life)
  - Can't be spammed
  - Rewards lucky/skilled players
  - Too common = game too easy

// VERY RARE (1%): Controversial, extreme effect
WARP
→ 1% because:
  - Skips entire level
  - Not always beneficial
  - Should feel special when appears
  - Too common = breaks game progression
```

---

## Statistical Testing

### Verify Distribution

```java
// Test randomWeighted() distribution
public static void testDistribution() {
    Map<PowerUpType, Integer> counts = new HashMap<>();
    int iterations = 100000; // 100k samples
    
    // Count occurrences
    for (int i = 0; i < iterations; i++) {
        PowerUpType type = PowerUpType.randomWeighted();
        counts.put(type, counts.getOrDefault(type, 0) + 1);
    }
    
    // Print results
    System.out.println("Distribution test (" + iterations + " samples):");
    for (PowerUpType type : PowerUpType.values()) {
        int count = counts.getOrDefault(type, 0);
        double observed = count / (double) iterations;
        double expected = type.getSpawnChance() / 0.78; // Normalize
        double error = Math.abs(observed - expected) / expected * 100;
        
        System.out.printf("%s: %d (%.2f%%) - Expected: %.2f%% - Error: %.2f%%\n",
            type, count, observed * 100, expected * 100, error);
    }
}

// Expected output:
// CATCH: 19231 (19.23%) - Expected: 19.23% - Error: 0.00%
// DUPLICATE: 15385 (15.39%) - Expected: 15.38% - Error: 0.05%
// EXPAND: 19231 (19.23%) - Expected: 19.23% - Error: 0.00%
// LASER: 19231 (19.23%) - Expected: 19.23% - Error: 0.00%
// LIFE: 6410 (6.41%) - Expected: 6.41% - Error: 0.01%
// SLOW: 19231 (19.23%) - Expected: 19.23% - Error: 0.00%
// WARP: 1282 (1.28%) - Expected: 1.28% - Error: 0.02%
```

---

## UI Integration

### Visual Indicators

```java
// Color-code by rarity
public Color getRarityColor() {
    switch (getRarityTier()) {
        case COMMON:
            return Color.WHITE;
        case UNCOMMON:
            return Color.GREEN;
        case RARE:
            return Color.BLUE;
        case VERY_RARE:
            return Color.GOLD;
        default:
            return Color.WHITE;
    }
}

// Render with rarity indication
public void renderPowerUp(Graphics2D g, PowerUp powerUp) {
    // Glow based on rarity
    Color glowColor = powerUp.getType().getRarityColor();
    renderGlow(g, powerUp.getX(), powerUp.getY(), glowColor);
    
    // Base sprite
    powerUp.render(g);
}
```

---

## Best Practices

### 1. Total Weight Normalization
```java
// ✅ Đúng - không cần normalize (algorithm handles any total)
public static PowerUpType randomWeighted() {
    double totalWeight = calculateTotalWeight(); // 0.78
    double randomValue = Math.random() * totalWeight; // 0.0 - 0.78
    // Works correctly
}

// ❌ Không cần - explicit normalization
public static PowerUpType randomWeightedNormalized() {
    // This works but is unnecessary
    double totalWeight = calculateTotalWeight();
    double normalized = Math.random(); // 0.0 - 1.0
    normalized *= totalWeight; // Back to 0.0 - 0.78
    // Extra step with no benefit
}
```

---

### 2. Type Safety
```java
// ✅ Đúng - enum provides type safety
public void applyPowerUp(PowerUpType type) {
    // Compiler ensures only valid types
    switch (type) {
        case CATCH: /* ... */ break;
        case DUPLICATE: /* ... */ break;
        // etc.
    }
}

// ❌ Sai - string-based (error-prone)
public void applyPowerUp(String typeStr) {
    if (typeStr.equals("catch")) { /* ... */ }
    else if (typeStr.equals("Catch")) { /* ... */ } // Typo!
    // No compile-time checking
}
```

---

### 3. Duration Access
```java
// ✅ Đúng - use getDuration() method
long duration = type.getDuration();
if (duration > 0) {
    scheduleRemoval(duration);
}

// ✅ Tốt hơn - combine with isInstant()
if (!type.isInstant()) {
    long duration = type.getDuration();
    scheduleRemoval(duration);
}

// ❌ Sai - hardcode durations
if (type == PowerUpType.CATCH) {
    scheduleRemoval(15000); // Magic number!
}
```

---

### 4. Extensibility
```java
// ✅ Đúng - easy to add new power-ups
// Just add new enum constant:
SHIELD("powerup_shield", 0.10), // 10% spawn rate

// Update factory:
case SHIELD:
    return new ShieldPowerUp(x, y);

// Update isInstant() or getDuration() if needed
// That's it!
```

---

## Advanced: Custom Spawn Rules

### Context-Aware Spawning

```java
// Adjust spawn rates based on game state
public PowerUpType selectPowerUpForContext(GameContext context) {
    // Low lives → increase LIFE spawn chance
    if (context.getLives() <= 1) {
        if (Math.random() < 0.30) { // 30% chance
            return PowerUpType.LIFE;
        }
    }
    
    // Fast balls → increase SLOW spawn chance
    if (context.getAverageBallSpeed() > FAST_THRESHOLD) {
        if (Math.random() < 0.25) { // 25% chance
            return PowerUpType.SLOW;
        }
    }
    
    // Default weighted selection
    return PowerUpType.randomWeighted();
}
```

---

### Exclude Certain Types

```java
// Don't spawn WARP on final level
public static PowerUpType randomWeightedExcluding(PowerUpType... excluded) {
    Set<PowerUpType> excludedSet = new HashSet<>(Arrays.asList(excluded));
    
    double totalWeight = 0.0;
    for (PowerUpType type : PowerUpType.values()) {
        if (!excludedSet.contains(type)) {
            totalWeight += type.spawnChance;
        }
    }
    
    double randomValue = Math.random() * totalWeight;
    double cumulativeWeight = 0.0;
    
    for (PowerUpType type : PowerUpType.values()) {
        if (!excludedSet.contains(type)) {
            cumulativeWeight += type.spawnChance;
            if (randomValue <= cumulativeWeight) {
                return type;
            }
        }
    }
    
    return EXPAND; // Fallback
}

// Usage:
if (isLastLevel()) {
    type = PowerUpType.randomWeightedExcluding(PowerUpType.WARP);
}
```

---

## Kết luận

`PowerUpType` enum là **cornerstone** của power-up system trong Arkanoid:

- **Central Registry**: Tất cả power-up types trong một nơi
- **Configuration**: Sprite prefixes, spawn rates, durations
- **Weighted Selection**: Fair probability distribution
- **Type Safety**: Compile-time checking
- **Extensible**: Easy to add new types
- **Balanced**: Carefully tuned spawn rates

Enum này exemplifies good software design:
1. **DRY Principle**: Không duplicate metadata
2. **Single Source of Truth**: Tất cả config ở một nơi
3. **Type Safety**: Compiler catches errors
4. **Easy Maintenance**: Add/modify types easily
5. **Testable**: Can verify distribution statistically

Weighted random selection algorithm (roulette wheel) là elegant solution cho balancing power-up frequency. Common power-ups (15%) keep gameplay dynamic. Rare power-ups (1-5%) create excitement khi spawn. Total weight không cần bằng 1.0 - algorithm works với any total, providing flexibility cho tuning.

**Design Wisdom**: Spawn rates reflect power-up value và impact. Defensive power-ups (CATCH, EXPAND, SLOW) are common because they help all players và don't break game balance. LIFE is rare because extra lives dramatically affect difficulty. WARP is very rare because it fundamentally changes progression. Good game design uses probability to control player experience without feeling arbitrary.

