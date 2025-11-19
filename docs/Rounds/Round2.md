# Round2 Class

## Tổng quan
`Round2` là **second level implementation** trong Arkanoid, introducing **Silver Bricks** để increase difficulty. Class này creates **13×5 grid** với **30% random chance** cho Silver Bricks (require 2 hits), còn lại là Normal Bricks. Round2 represents **first difficulty spike**, teaching players về multi-hit bricks và strategic ball placement. This level introduces **randomization**, making each playthrough unique.

## Vị trí
- **Package**: `Rounds`
- **File**: `src/Rounds/Round2.java`
- **Type**: Concrete Class (Level Implementation)
- **Extends**: `RoundBase`
- **Pattern**: Template Method Pattern + Randomization

## Mục đích
Round2 class:
- Increase difficulty từ Round 1
- Introduce Silver Bricks (2-hit bricks)
- Add randomization element
- Expand grid to 13×5 (65 bricks)
- Challenge player's precision
- Teach multi-hit brick mechanics
- Create varied gameplay experiences

---

## Class Structure

```java
public class Round2 extends RoundBase {
    // Constructor
    public Round2();
    
    // Implemented abstract method
    @Override
    public List<Brick> createBricks();
}
```

---

## Constructor

### Round2()

```java
public Round2() {
    super(2, "Silver Challenge");
}
```

**Chức năng**:
- Calls parent constructor với round number **2**
- Sets round name to **"Silver Challenge"**
- Inherits play area dimensions từ RoundBase

**Properties Set**:
```java
roundNumber = 2
roundName = "Silver Challenge"
playAreaWidth = Constants.PlayArea.PLAY_AREA_WIDTH
playAreaHeight = Constants.PlayArea.PLAY_AREA_HEIGHT
```

**Usage**:
```java
// Create Round 2 instance
RoundBase round2 = new Round2();

System.out.println("Round " + round2.getRoundNumber());
// Output: Round 2

System.out.println(round2.getRoundName());
// Output: Silver Challenge

int totalBricks = round2.getTotalBrickCount();
// totalBricks = 65 (13 × 5)
```

---

## createBricks() Method

### Overview

```java
@Override
public List<Brick> createBricks() {
    List<Brick> bricks = new ArrayList<>();
    Random rnd = new Random();
    
    // 1. Define grid parameters (13×5)
    // 2. Calculate centered starting position
    // 3. Define color pattern (5 colors)
    // 4. Create bricks with 30% Silver chance
    // 5. Return completed brick list
    
    return bricks;
}
```

**Key Features**:
```
Grid Size: 13 columns × 5 rows = 65 bricks total
Brick Types: Normal Bricks (70%) + Silver Bricks (30%)
Randomization: Each brick has 30% chance to be Silver
Color Pattern: RED, BLUE, GREEN, YELLOW, ORANGE (by row)
Centering: Horizontally centered in play area
```

---

### Implementation Details

#### Step 1: Initialize Random Generator

```java
Random rnd = new Random();
```

**Purpose**: Generate random values cho Silver Brick placement

**Usage**: `rnd.nextDouble()` returns value trong [0.0, 1.0)

**30% Probability Check**:
```java
if (rnd.nextDouble() < 0.3) {
    // 30% chance: Create Silver Brick
} else {
    // 70% chance: Create Normal Brick
}
```

---

#### Step 2: Grid Parameters

```java
// Grid dimensions (larger than Round 1)
int cols = 13;                              // 13 bricks per row
int rows = 5;                               // 5 rows (was 4 in Round1)

// Brick dimensions from Constants
double brickW = Constants.Bricks.BRICK_WIDTH;
double brickH = Constants.Bricks.BRICK_HEIGHT;
double hSpacing = Constants.Bricks.BRICK_H_SPACING;
double vSpacing = Constants.Bricks.BRICK_V_SPACING;
```

**Grid Comparison**:
```
Round 1: 13 × 4 = 52 bricks
Round 2: 13 × 5 = 65 bricks
Increase: +13 bricks (+25%)
```

---

#### Step 3: Calculate Centered Position

```java
// Calculate total width of brick grid
double totalWidth = cols * brickW + (cols - 1) * hSpacing;

// Calculate starting X to center grid horizontally
double startX = Constants.PlayArea.PLAY_AREA_X + 
                (playAreaWidth - totalWidth) / 2.0;

// Calculate starting Y position
double startY = Constants.PlayArea.PLAY_AREA_Y + 
                Constants.Bricks.BRICK_START_Y / 2.0;
```

**Same centering logic as Round1** - maintains consistency

---

#### Step 4: Color Pattern (5 Colors)

```java
// Define row colors (5 colors for 5 rows)
BrickType[] colors = {
    BrickType.RED,      // Row 0
    BrickType.BLUE,     // Row 1
    BrickType.GREEN,    // Row 2
    BrickType.YELLOW,   // Row 3
    BrickType.ORANGE    // Row 4 (new!)
};
```

**Visual Pattern** (Normal Bricks only):
```
Row 0: 🔴🔴🔴🔴🔴🔴🔴🔴🔴🔴🔴🔴🔴 (RED)
Row 1: 🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵 (BLUE)
Row 2: 🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢 (GREEN)
Row 3: 🟡🟡🟡🟡🟡🟡🟡🟡🟡🟡🟡🟡🟡 (YELLOW)
Row 4: 🟠🟠🟠🟠🟠🟠🟠🟠🟠🟠🟠🟠🟠 (ORANGE - new row)
```

**But**: 30% of bricks will be Silver (overrides row color)

---

#### Step 5: Brick Creation with Randomization

```java
// Nested loop: row by row, column by column
for (int r = 0; r < rows; r++) {
    // Get color for current row
    BrickType rowColor = colors[r % colors.length];
    
    for (int c = 0; c < cols; c++) {
        // Calculate brick position
        double x = startX + c * (brickW + hSpacing);
        double y = startY + r * (brickH + vSpacing);
        
        // Random brick type selection
        if (rnd.nextDouble() < 0.3) {
            // 30% chance: Create Silver Brick (2 hits required)
            bricks.add(new SilverBrick(x, y, brickW, brickH));
        } else {
            // 70% chance: Create Normal Brick (1 hit required)
            bricks.add(new NormalBrick(x, y, brickW, brickH, rowColor));
        }
    }
}
```

**Randomization Logic**:
```
Random value: 0.0 to 1.0
If value < 0.3 (30% chance): Silver Brick
If value >= 0.3 (70% chance): Normal Brick

Example with 65 bricks:
Expected Silver: 65 × 0.3 = ~19-20 bricks
Expected Normal: 65 × 0.7 = ~45-46 bricks
```

---

### Complete Method

```java
@Override
public List<Brick> createBricks() {
    List<Brick> bricks = new ArrayList<>();
    Random rnd = new Random(); // Initialize random generator

    // --- 1. Setup grid parameters (13×5) ---
    int cols = 13;
    int rows = 5; // Increased from 4 in Round1
    double brickW = Constants.Bricks.BRICK_WIDTH;
    double brickH = Constants.Bricks.BRICK_HEIGHT;
    double hSpacing = Constants.Bricks.BRICK_H_SPACING;
    double vSpacing = Constants.Bricks.BRICK_V_SPACING;

    // --- 2. Calculate centered starting position ---
    double totalWidth = cols * brickW + (cols - 1) * hSpacing;
    double startX = Constants.PlayArea.PLAY_AREA_X + 
                    (playAreaWidth - totalWidth) / 2.0;
    double startY = Constants.PlayArea.PLAY_AREA_Y + 
                    Constants.Bricks.BRICK_START_Y / 2.0;

    // --- 3. Define color pattern (5 colors) ---
    BrickType[] colors = {
        BrickType.RED,
        BrickType.BLUE,
        BrickType.GREEN,
        BrickType.YELLOW,
        BrickType.ORANGE
    };

    // --- 4. Create bricks with random Silver Bricks ---
    for (int r = 0; r < rows; r++) {
        BrickType rowColor = colors[r % colors.length];
        
        for (int c = 0; c < cols; c++) {
            double x = startX + c * (brickW + hSpacing);
            double y = startY + r * (brickH + vSpacing);
            
            // 30% chance for Silver Brick
            if (rnd.nextDouble() < 0.3) {
                bricks.add(new SilverBrick(x, y, brickW, brickH));
            } else {
                bricks.add(new NormalBrick(x, y, brickW, brickH, rowColor));
            }
        }
    }

    return bricks;
}
```

---

## Brick Layout Analysis

### Expected Distribution (Statistical)

```
Total Bricks: 65 (13 × 5)

Expected Normal Bricks: ~46 (70% of 65)
Expected Silver Bricks: ~19 (30% of 65)

Actual distribution varies each playthrough!
```

### Example Layout

```
[N][S][N][N][S][N][N][N][S][N][N][N][S]  ← Row 0 (RED/Silver mix)
[N][N][N][S][N][S][N][N][N][N][S][N][N]  ← Row 1 (BLUE/Silver mix)
[S][N][N][N][N][N][N][S][N][N][S][N][N]  ← Row 2 (GREEN/Silver mix)
[N][N][S][N][N][N][S][N][N][S][N][N][N]  ← Row 3 (YELLOW/Silver mix)
[N][S][N][N][N][N][N][N][S][N][N][S][N]  ← Row 4 (ORANGE/Silver mix)

Legend:
N = Normal Brick (1 hit, colored by row)
S = Silver Brick (2 hits, gray color)

Note: Pattern changes each time createBricks() is called!
```

---

### Brick Properties

**Normal Bricks (70%)**:
```java
Type: NormalBrick
Hits Required: 1 hit to destroy
Colors: RED, BLUE, GREEN, YELLOW, ORANGE (by row)
Points: 100 points
Behavior: Destroyed on first hit
```

**Silver Bricks (30%)**:
```java
Type: SilverBrick
Hits Required: 2 hits to destroy
Color: Gray/Silver (ignores row color)
Points: 300 points (higher than Normal)
Behavior: 
  - 1st hit: Shows crack animation, remains
  - 2nd hit: Destroyed
```

---

## Randomization Deep Dive

### Probability Distribution

```java
Random rnd = new Random();

// For each brick:
double randomValue = rnd.nextDouble(); // 0.0 to 1.0

if (randomValue < 0.3) {
    // Probability: 30%
    // Range: [0.0, 0.3)
    createSilverBrick();
} else {
    // Probability: 70%
    // Range: [0.3, 1.0)
    createNormalBrick();
}
```

---

### Distribution Examples

**Example Run 1**:
```
Total: 65 bricks
Silver: 21 bricks (32.3%)
Normal: 44 bricks (67.7%)
```

**Example Run 2**:
```
Total: 65 bricks
Silver: 18 bricks (27.7%)
Normal: 47 bricks (72.3%)
```

**Example Run 3**:
```
Total: 65 bricks
Silver: 20 bricks (30.8%)
Normal: 45 bricks (69.2%)
```

**Statistical Average** (over many runs):
```
Silver Bricks: ~19.5 (30% of 65)
Normal Bricks: ~45.5 (70% of 65)
```

---

### Seed Control (for Testing)

```java
// Reproducible randomization with seed
public List<Brick> createBricksWithSeed(long seed) {
    List<Brick> bricks = new ArrayList<>();
    Random rnd = new Random(seed); // Fixed seed
    
    // ... same logic as createBricks()
    
    return bricks;
}

// Usage for testing
@Test
public void testConsistentLayout() {
    Round2 round = new Round2();
    
    // Same seed = same layout
    List<Brick> bricks1 = round.createBricksWithSeed(12345L);
    List<Brick> bricks2 = round.createBricksWithSeed(12345L);
    
    // Verify identical layouts
    assertEquals(bricks1.size(), bricks2.size());
    for (int i = 0; i < bricks1.size(); i++) {
        assertEquals(
            bricks1.get(i).getClass(), 
            bricks2.get(i).getClass()
        );
    }
}
```

---

## Difficulty Analysis

### Complexity Factors

```
✅ Increased Brick Count:
- 65 bricks (vs 52 in Round1)
- +25% more targets

✅ Multi-Hit Bricks:
- Silver Bricks require 2 hits
- Forces precision aiming
- Increases time to complete

✅ Randomization:
- Unpredictable brick placement
- Different strategy each playthrough
- Can't memorize pattern

✅ Visual Complexity:
- Mixed Normal/Silver bricks
- Harder to track targets
- More cognitive load
```

---

### Estimated Difficulty

```
Difficulty Rating: ⭐⭐ (2/5 stars)

Time to Complete: 4-6 minutes (intermediate player)
Skill Required: Precision aiming, multi-hit awareness
Challenge Level: Moderate introduction to difficulty
Success Rate: ~70% (experienced players)

Comparison:
Round1 (52 bricks, all Normal): ⭐ (2-3 min)
Round2 (65 bricks, 30% Silver): ⭐⭐ (4-6 min)
Difficulty Increase: ~2x longer playtime
```

---

### Strategic Considerations

**Player Strategy**:
```
1. Target Normal Bricks First:
   - Clear easy targets quickly
   - Reduces total brick count faster

2. Hit Silver Bricks Opportunistically:
   - First hit when convenient
   - Return later for second hit

3. Use Power-Ups Wisely:
   - Laser power-up excellent vs Silver
   - Duplicate balls help coverage

4. Track Damaged Silver Bricks:
   - Remember which Silvers have 1 hit
   - Prioritize finishing damaged bricks
```

---

## Usage Examples

### Example 1: Load Round 2

```java
public class GameManager {
    public void loadRound2() {
        // Create Round 2
        RoundBase round = new Round2();
        
        // Generate bricks (will be different each time!)
        List<Brick> bricks = round.createBricks();
        
        // Count brick types
        int normalCount = 0;
        int silverCount = 0;
        
        for (Brick brick : bricks) {
            if (brick instanceof NormalBrick) normalCount++;
            else if (brick instanceof SilverBrick) silverCount++;
        }
        
        // Display round info
        System.out.println("=== " + round.getRoundName() + " ===");
        System.out.println("Total Bricks: " + bricks.size());
        System.out.println("Normal Bricks: " + normalCount);
        System.out.println("Silver Bricks: " + silverCount);
        System.out.println("Silver %: " + 
            (silverCount * 100.0 / bricks.size()) + "%");
    }
}

// Example Output:
// === Silver Challenge ===
// Total Bricks: 65
// Normal Bricks: 46
// Silver Bricks: 19
// Silver %: 29.2%
```

---

### Example 2: Track Silver Brick Hits

```java
public class SilverBrickTracker {
    private Map<SilverBrick, Integer> hitCount = new HashMap<>();
    
    public void onBrickHit(Brick brick) {
        if (brick instanceof SilverBrick silverBrick) {
            // Increment hit count
            int hits = hitCount.getOrDefault(silverBrick, 0) + 1;
            hitCount.put(silverBrick, hits);
            
            System.out.println("Silver Brick hit " + hits + "/2 times");
            
            if (hits >= 2) {
                System.out.println("Silver Brick destroyed!");
                hitCount.remove(silverBrick);
            }
        }
    }
    
    public int getDamagedSilverCount() {
        return hitCount.size(); // Bricks with 1 hit
    }
}
```

---

### Example 3: Difficulty Comparison

```java
public class DifficultyAnalyzer {
    public void compareRounds() {
        Round1 round1 = new Round1();
        Round2 round2 = new Round2();
        
        List<Brick> bricks1 = round1.createBricks();
        List<Brick> bricks2 = round2.createBricks();
        
        // Calculate effective hits required
        int hits1 = bricks1.size(); // All Normal = 1 hit each
        
        int hits2 = 0;
        for (Brick brick : bricks2) {
            if (brick instanceof SilverBrick) {
                hits2 += 2; // Silver requires 2 hits
            } else {
                hits2 += 1; // Normal requires 1 hit
            }
        }
        
        System.out.println("Round 1 effective hits: " + hits1);
        // Round 1: 52 hits
        
        System.out.println("Round 2 effective hits: " + hits2);
        // Round 2: ~84 hits (46 × 1 + 19 × 2)
        
        double increase = (hits2 - hits1) * 100.0 / hits1;
        System.out.println("Difficulty increase: " + increase + "%");
        // ~62% more hits required!
    }
}
```

---

## Testing

### Unit Tests

```java
@Test
public void testRound2Properties() {
    Round2 round = new Round2();
    
    assertEquals(2, round.getRoundNumber());
    assertEquals("Silver Challenge", round.getRoundName());
}

@Test
public void testBrickCount() {
    Round2 round = new Round2();
    List<Brick> bricks = round.createBricks();
    
    assertEquals(65, bricks.size()); // 13 × 5 = 65
}

@Test
public void testBrickTypeMix() {
    Round2 round = new Round2();
    List<Brick> bricks = round.createBricks();
    
    int normalCount = 0;
    int silverCount = 0;
    
    for (Brick brick : bricks) {
        if (brick instanceof NormalBrick) normalCount++;
        else if (brick instanceof SilverBrick) silverCount++;
    }
    
    // Both types should exist
    assertTrue(normalCount > 0);
    assertTrue(silverCount > 0);
    
    // Total should match
    assertEquals(65, normalCount + silverCount);
}

@Test
public void testSilverProbability() {
    Round2 round = new Round2();
    
    // Run multiple times to check average
    int totalRuns = 100;
    int totalSilver = 0;
    
    for (int run = 0; run < totalRuns; run++) {
        List<Brick> bricks = round.createBricks();
        
        for (Brick brick : bricks) {
            if (brick instanceof SilverBrick) {
                totalSilver++;
            }
        }
    }
    
    double averageSilver = totalSilver / (double) totalRuns;
    double expectedSilver = 65 * 0.3; // ~19.5
    
    // Should be close to 30% (with tolerance)
    assertEquals(expectedSilver, averageSilver, 3.0);
    // Average should be 19.5 ± 3
}

@Test
public void testRandomization() {
    Round2 round = new Round2();
    
    // Generate two layouts
    List<Brick> bricks1 = round.createBricks();
    List<Brick> bricks2 = round.createBricks();
    
    // Layouts should be different (very high probability)
    boolean different = false;
    for (int i = 0; i < bricks1.size(); i++) {
        if (!bricks1.get(i).getClass().equals(
             bricks2.get(i).getClass())) {
            different = true;
            break;
        }
    }
    
    assertTrue(different); // Should be different layouts
}
```

---

## Best Practices

### 1. Initialize Random Object Once

```java
// ✅ Đúng - Create Random once
@Override
public List<Brick> createBricks() {
    Random rnd = new Random(); // Create at method start
    
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (rnd.nextDouble() < 0.3) { // Reuse same Random
                // ...
            }
        }
    }
}

// ❌ Sai - Create Random repeatedly
for (int r = 0; r < rows; r++) {
    for (int c = 0; c < cols; c++) {
        Random rnd = new Random(); // WRONG! Poor randomization
        if (rnd.nextDouble() < 0.3) {
            // ...
        }
    }
}
```

---

### 2. Use Consistent Probability

```java
// ✅ Đúng - Define constant
private static final double SILVER_PROBABILITY = 0.3;

if (rnd.nextDouble() < SILVER_PROBABILITY) {
    // 30% Silver
}

// ❌ Sai - Magic number
if (rnd.nextDouble() < 0.3) { // What does 0.3 mean?
    // ...
}
```

---

### 3. Document Randomization Behavior

```java
// ✅ Đúng - Clear documentation
/**
 * Creates bricks for Round 2 with randomized layout.
 * Each brick has a 30% chance to be a Silver Brick (2 hits)
 * and 70% chance to be a Normal Brick (1 hit).
 * 
 * @return List of 65 bricks (13×5 grid) with random distribution
 */
@Override
public List<Brick> createBricks() {
    // ...
}
```

---

## Kết luận

`Round2` là **intermediate difficulty level** introducing important mechanics:

- **Silver Bricks**: First multi-hit bricks (2 hits required)
- **Randomization**: 30% Silver, 70% Normal distribution
- **Larger Grid**: 13×5 = 65 bricks (+25% from Round1)
- **Unpredictability**: Different layout each playthrough
- **Strategic Depth**: Players must prioritize targets
- **Difficulty Spike**: ~62% more hits required than Round1
- **Learning Curve**: Teaches patience và precision

Round2 exemplifies **graduated difficulty progression**. By introducing Silver Bricks gradually (30%) rather than overwhelming (100%), players adapt to multi-hit mechanics. Randomization ensures replay value - no two playthroughs identical. This design keeps gameplay fresh while teaching advanced concepts.

**Probability Engineering**: 30% Silver ratio is carefully tuned. Too low (10%) và Silver Bricks feel rare/forgettable. Too high (50%+) và level becomes frustratingly slow. 30% hits sweet spot - Silver Bricks present enough to challenge, not overwhelm. Statistical variation (19 ± 3 bricks) adds variety without extreme outliers.

**Educational Design**: Round2 bridges gap between trivial (Round1) và complex (Round3/4). Players who master Round1 (uniform grid, Normal only) are ready for Round2 (randomized, multi-hit). This scaffolding approach ensures skill development matches challenge progression. Success rate drops from 90% (Round1) to 70% (Round2) - appropriate difficulty curve.

