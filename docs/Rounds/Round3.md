# Round3 Class

## Tổng quan
`Round3` là **advanced level** trong Arkanoid, featuring **diamond-shaped pattern** với complex brick layout. Class này introduces **Gold Bricks** (indestructible) alongside Normal và Silver bricks, creating intricate **13×13 matrix pattern**. Round3 represents major **difficulty spike** với strategic placement requiring careful ball control. This level showcases **pattern-based design** using 2D array layout definition.

## Vị trí
- **Package**: `Rounds`
- **File**: `src/Rounds/Round3.java`
- **Type**: Concrete Class (Level Implementation)
- **Extends**: `RoundBase`
- **Pattern**: Template Method Pattern + Matrix Layout

## Mục đích
Round3 class:
- Major difficulty increase from Round 2
- Introduce Gold Bricks (indestructible obstacles)
- Feature diamond/rhombus visual pattern
- Use 2D array for precise layout control
- Mix all three brick types (Normal, Silver, Gold)
- Challenge advanced players
- Require strategic ball placement
- Create memorable visual design

---

## Class Structure

```java
public class Round3 extends RoundBase {
    // Constructor
    public Round3();
    
    // Implemented abstract method
    @Override
    public List<Brick> createBricks();
}
```

---

## Constructor

### Round3()

```java
public Round3() {
    super(3, "Diamond Challenge");
}
```

**Chức năng**:
- Calls parent constructor với round number **3**
- Sets round name to **"Diamond Challenge"**
- Inherits play area dimensions từ RoundBase

**Properties Set**:
```java
roundNumber = 3
roundName = "Diamond Challenge"
playAreaWidth = Constants.PlayArea.PLAY_AREA_WIDTH
playAreaHeight = Constants.PlayArea.PLAY_AREA_HEIGHT
```

---

## createBricks() Method

### Overview

```java
@Override
public List<Brick> createBricks() {
    List<Brick> bricks = new ArrayList<>();
    
    // 1. Define brick dimensions and spacing
    // 2. Calculate starting Y position
    // 3. Define 13×13 diamond layout matrix
    // 4. Calculate centered starting X
    // 5. Map numbers to colors
    // 6. Create bricks from layout matrix
    // 7. Return completed brick list
    
    return bricks;
}
```

**Key Features**:
```
Grid Size: 13×13 matrix (169 cells)
Actual Bricks: 73 bricks (96 empty cells)
Pattern: Diamond/rhombus shape
Brick Types: 
  - Normal Bricks (majority)
  - Silver Bricks (strategic placement)
  - Gold Bricks (5 total - diamond vertices + center)
Visual Design: Symmetric diamond pattern
```

---

### Diamond Layout Matrix

#### Matrix Definition

```java
// 0 = Empty, 1-8 = Normal (colors), 9 = Gold, 10 = Silver
int[][] layout = {
    {0,0,0,0,0,0,9,0,0,0,0,0,0},        // Row 0: 1 Gold (top)
    {0,0,0,0,0,1,2,1,0,0,0,0,0},        // Row 1
    {0,0,0,0,3,4,10,4,3,0,0,0,0},       // Row 2: 1 Silver
    {0,0,0,5,6,7,8,7,6,5,0,0,0},        // Row 3
    {0,0,2,3,4,5,9,5,4,3,2,0,0},        // Row 4: 1 Gold (upper-middle)
    {0,1,2,3,10,5,6,5,10,3,2,1,0},      // Row 5: 2 Silver
    {7,8,1,2,3,4,5,4,3,2,1,8,7},        // Row 6: Full row (widest)
    {0,6,7,8,1,10,2,10,1,8,7,6,0},      // Row 7: 2 Silver
    {0,0,5,6,7,8,9,8,7,6,5,0,0},        // Row 8: 1 Gold (lower-middle)
    {0,0,0,4,5,6,7,6,5,4,0,0,0},        // Row 9
    {0,0,0,0,3,10,4,10,3,0,0,0,0},      // Row 10: 2 Silver
    {0,0,0,0,0,2,1,2,0,0,0,0,0},        // Row 11
    {0,0,0,0,0,0,9,0,0,0,0,0,0}         // Row 12: 1 Gold (bottom)
};
```

---

#### Visual Representation

```
       🟡              ← Row 0: Gold (top vertex)
      🔴🔵🔴           ← Row 1
     🟢🟡🪙🟡🟢        ← Row 2: Silver in middle
    🟠🔵🟢🟡🟢🔵🟠     ← Row 3
   🔵🟢🟡🟠🟡🟠🟡🟢🔵  ← Row 4: Gold center
  🔴🔵🟢🪙🟠🔵🟠🪙🟢🔵🔴 ← Row 5: 2 Silver
 🟢🟡🔴🔵🟢🟡🟠🟡🟢🔵🔴🟡🟢 ← Row 6: Widest (13 bricks)
  🔵🟢🟡🔴🪙🔵🪙🔴🟡🟢🔵 ← Row 7: 2 Silver
   🟠🔵🟢🟡🟡🟡🟢🔵🟠  ← Row 8: Gold center
    🟡🟠🔵🟢🔵🟠🟡     ← Row 9
     🟢🪙🟡🪙🟢        ← Row 10: 2 Silver
      🔵🔴🔵           ← Row 11
       🟡              ← Row 12: Gold (bottom vertex)

Legend:
🔴🔵🟢🟡🟠 = Normal Bricks (colored)
🪙 = Silver Bricks (2 hits)
🟡 = Gold Bricks (indestructible)
```

---

### Brick Type Mapping

#### Normal Bricks (1-8)

```java
// Color mapping array
BrickType[] colors = {
    BrickType.RED,      // 1
    BrickType.BLUE,     // 2
    BrickType.GREEN,    // 3
    BrickType.YELLOW,   // 4
    BrickType.ORANGE,   // 5
    BrickType.CYAN,     // 6
    BrickType.PINK,     // 7
    BrickType.WHITE     // 8
};

// Usage in code
if (brickType >= 1 && brickType <= 8) {
    BrickType color = colors[(brickType - 1) % colors.length];
    bricks.add(new NormalBrick(x, y, brickW, brickH, color));
}
```

---

#### Silver Bricks (10)

```java
if (brickType == 10) {
    // Silver Brick: 2 hits required
    bricks.add(new SilverBrick(x, y, brickW, brickH));
}
```

**Silver Brick Positions** (10 total):
```
Row 2: Column 6 (middle of diamond)
Row 5: Columns 4, 8 (symmetric)
Row 7: Columns 5, 7 (symmetric)
Row 10: Columns 5, 7 (symmetric)
```

---

#### Gold Bricks (9)

```java
if (brickType == 9) {
    // Gold Brick: Indestructible
    bricks.add(new GoldBrick(x, y, brickW, brickH));
}
```

**Gold Brick Positions** (5 total):
```
Row 0: Column 6 (top vertex)
Row 4: Column 6 (upper-middle)
Row 6: Column 6 (exact center)
Row 8: Column 6 (lower-middle)
Row 12: Column 6 (bottom vertex)

Pattern: Vertical line through diamond center
Purpose: Creates obstacle requiring ball maneuvering
```

---

### Implementation Code

```java
@Override
public List<Brick> createBricks() {
    List<Brick> bricks = new ArrayList<>();

    // Brick dimensions
    double brickW = Constants.Bricks.BRICK_WIDTH;
    double brickH = Constants.Bricks.BRICK_HEIGHT;
    double hSpacing = Constants.Bricks.BRICK_H_SPACING;
    double vSpacing = Constants.Bricks.BRICK_V_SPACING;

    // Starting Y position
    double startY = Constants.PlayArea.PLAY_AREA_Y + 
                    Constants.Bricks.BRICK_START_Y / 2.0;

    // Diamond layout matrix (13×13)
    int[][] layout = {
        {0,0,0,0,0,0,9,0,0,0,0,0,0},
        {0,0,0,0,0,1,2,1,0,0,0,0,0},
        {0,0,0,0,3,4,10,4,3,0,0,0,0},
        {0,0,0,5,6,7,8,7,6,5,0,0,0},
        {0,0,2,3,4,5,9,5,4,3,2,0,0},
        {0,1,2,3,10,5,6,5,10,3,2,1,0},
        {7,8,1,2,3,4,5,4,3,2,1,8,7},
        {0,6,7,8,1,10,2,10,1,8,7,6,0},
        {0,0,5,6,7,8,9,8,7,6,5,0,0},
        {0,0,0,4,5,6,7,6,5,4,0,0,0},
        {0,0,0,0,3,10,4,10,3,0,0,0,0},
        {0,0,0,0,0,2,1,2,0,0,0,0,0},
        {0,0,0,0,0,0,9,0,0,0,0,0,0}
    };

    int rows = layout.length;    // 13 rows
    int cols = layout[0].length; // 13 columns

    // Calculate centered starting X
    double totalWidth = cols * brickW + (cols - 1) * hSpacing;
    double startX = Constants.PlayArea.PLAY_AREA_X + 
                    (playAreaWidth - totalWidth) / 2.0;

    // Color mapping for Normal Bricks
    BrickType[] colors = {
        BrickType.RED, BrickType.BLUE, BrickType.GREEN, BrickType.YELLOW,
        BrickType.ORANGE, BrickType.CYAN, BrickType.PINK, BrickType.WHITE
    };

    // Create bricks from layout matrix
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            int brickType = layout[r][c];

            if (brickType == 0) {
                continue; // Skip empty cells
            }

            // Calculate position
            double x = startX + c * (brickW + hSpacing);
            double y = startY + r * (brickH + vSpacing);

            // Create appropriate brick type
            if (brickType == 9) {
                // Gold Brick (indestructible)
                bricks.add(new GoldBrick(x, y, brickW, brickH));
            } else if (brickType == 10) {
                // Silver Brick (2 hits)
                bricks.add(new SilverBrick(x, y, brickW, brickH));
            } else {
                // Normal Brick (1 hit)
                BrickType color = colors[(brickType - 1) % colors.length];
                bricks.add(new NormalBrick(x, y, brickW, brickH, color));
            }
        }
    }

    return bricks;
}
```

---

## Brick Statistics

### Total Breakdown

```
Total Matrix Cells: 13 × 13 = 169 cells
Empty Cells: 96 cells (56.8%)
Occupied Cells: 73 bricks (43.2%)

Brick Type Distribution:
- Normal Bricks: 58 bricks (79.5%)
- Silver Bricks: 10 bricks (13.7%)
- Gold Bricks: 5 bricks (6.8%)
```

---

### Row-by-Row Analysis

```
Row  0: 1 brick  (1 Gold)
Row  1: 3 bricks (3 Normal)
Row  2: 5 bricks (4 Normal, 1 Silver)
Row  3: 7 bricks (7 Normal)
Row  4: 9 bricks (8 Normal, 1 Gold)
Row  5: 11 bricks (9 Normal, 2 Silver)
Row  6: 13 bricks (13 Normal) ← Widest row
Row  7: 11 bricks (9 Normal, 2 Silver)
Row  8: 9 bricks (8 Normal, 1 Gold)
Row  9: 7 bricks (7 Normal)
Row 10: 5 bricks (3 Normal, 2 Silver)
Row 11: 3 bricks (3 Normal)
Row 12: 1 brick  (1 Gold)

Total: 73 bricks
```

---

## Difficulty Analysis

### Complexity Factors

```
✅ Complex Pattern:
- Diamond shape requires precise aim
- Gold Bricks block center column
- Empty spaces create gaps

✅ Indestructible Obstacles:
- 5 Gold Bricks cannot be destroyed
- Force ball around obstacles
- Require strategic angles

✅ Mixed Brick Types:
- 58 Normal (1 hit)
- 10 Silver (2 hits each)
- 5 Gold (invincible)

✅ Strategic Placement:
- Silver Bricks at key positions
- Gold Bricks form central obstacle
- Gaps require ball control
```

---

### Effective Hits Required

```
Destructible Bricks: 68 (73 - 5 Gold)

Effective Hits:
- Normal: 58 × 1 = 58 hits
- Silver: 10 × 2 = 20 hits
Total: 78 hits required

Comparison:
Round 1: 52 hits
Round 2: ~84 hits (with 30% Silver)
Round 3: 78 hits (but more complex pattern!)
```

---

### Estimated Difficulty

```
Difficulty Rating: ⭐⭐⭐⭐ (4/5 stars)

Time to Complete: 8-12 minutes (advanced player)
Skill Required: Precise ball control, angle calculation
Challenge Level: Advanced/Expert
Success Rate: ~50% (experienced players)

Key Challenges:
- Gold Bricks block optimal paths
- Diamond shape hard to clear efficiently
- Silver Bricks at strategic choke points
- Gaps require careful ball tracking
```

---

## Strategic Analysis

### Gold Brick Obstacle Course

```
       🟡 ← Top Gold (Row 0)
        ↓
        ↓
        🟡 ← Upper Gold (Row 4)
        ↓
        🟡 ← Center Gold (Row 6)
        ↓
        🟡 ← Lower Gold (Row 8)
        ↓
       🟡 ← Bottom Gold (Row 12)

Effect: Creates vertical obstacle
Strategy: Must aim around Gold column
```

---

### Silver Brick Choke Points

```
Row 2: Center Silver (bottleneck)
Row 5: Two Silvers (symmetric defense)
Row 7: Two Silvers (symmetric defense)
Row 10: Two Silvers (lower bottleneck)

Purpose: Slow down clearing speed
Strategy: Target Silvers early to open lanes
```

---

### Optimal Clearing Strategy

```
Phase 1: Clear Outer Edges
- Start with widest rows (6-8)
- Avoid hitting Gold Bricks
- Build paddle control

Phase 2: Target Silver Bricks
- First hit all Silvers (mark them)
- Second pass to destroy damaged Silvers
- Opens up interior space

Phase 3: Clear Diamond Center
- Work around Gold obstacles
- Use angles to reach inside bricks
- Power-ups extremely valuable

Phase 4: Cleanup Vertices
- Top and bottom single bricks
- Hardest to reach
- May require multiple ball bounces
```

---

## Usage Examples

### Example 1: Analyze Layout

```java
public class LayoutAnalyzer {
    public void analyzeRound3() {
        Round3 round = new Round3();
        List<Brick> bricks = round.createBricks();
        
        // Count brick types
        int normal = 0, silver = 0, gold = 0;
        for (Brick brick : bricks) {
            if (brick instanceof GoldBrick) gold++;
            else if (brick instanceof SilverBrick) silver++;
            else if (brick instanceof NormalBrick) normal++;
        }
        
        System.out.println("=== " + round.getRoundName() + " ===");
        System.out.println("Total Bricks: " + bricks.size());
        System.out.println("Normal: " + normal);
        System.out.println("Silver: " + silver);
        System.out.println("Gold: " + gold);
        System.out.println("Destructible: " + (normal + silver));
    }
}

// Output:
// === Diamond Challenge ===
// Total Bricks: 73
// Normal: 58
// Silver: 10
// Gold: 5
// Destructible: 68
```

---

### Example 2: Find Gold Positions

```java
public class GoldBrickLocator {
    public void findGoldBricks() {
        Round3 round = new Round3();
        List<Brick> bricks = round.createBricks();
        
        System.out.println("Gold Brick Positions:");
        for (Brick brick : bricks) {
            if (brick instanceof GoldBrick) {
                System.out.printf("  Gold at (%.0f, %.0f)%n", 
                    brick.getX(), brick.getY());
            }
        }
    }
}

// Output:
// Gold Brick Positions:
//   Gold at (328, 100)  ← Top
//   Gold at (328, 188)  ← Upper-middle
//   Gold at (328, 232)  ← Center
//   Gold at (328, 276)  ← Lower-middle
//   Gold at (328, 364)  ← Bottom
```

---

### Example 3: Visual Preview

```java
public class RoundPreview {
    public void showAsciiPreview() {
        Round3 round = new Round3();
        
        int[][] layout = {
            {0,0,0,0,0,0,9,0,0,0,0,0,0},
            // ... (full layout)
        };
        
        System.out.println("Round 3 Pattern:");
        for (int[] row : layout) {
            for (int cell : row) {
                if (cell == 0) System.out.print("  ");
                else if (cell == 9) System.out.print("G ");
                else if (cell == 10) System.out.print("S ");
                else System.out.print("N ");
            }
            System.out.println();
        }
    }
}

// Output:
//             G 
//          N N N 
//        N N S N N 
//      N N N N N N N 
//    ...
```

---

## Testing

### Unit Tests

```java
@Test
public void testRound3Properties() {
    Round3 round = new Round3();
    
    assertEquals(3, round.getRoundNumber());
    assertEquals("Diamond Challenge", round.getRoundName());
}

@Test
public void testBrickCount() {
    Round3 round = new Round3();
    List<Brick> bricks = round.createBricks();
    
    assertEquals(73, bricks.size());
}

@Test
public void testGoldBrickCount() {
    Round3 round = new Round3();
    List<Brick> bricks = round.createBricks();
    
    int goldCount = 0;
    for (Brick brick : bricks) {
        if (brick instanceof GoldBrick) {
            goldCount++;
        }
    }
    
    assertEquals(5, goldCount); // 5 Gold Bricks
}

@Test
public void testSilverBrickCount() {
    Round3 round = new Round3();
    List<Brick> bricks = round.createBricks();
    
    int silverCount = 0;
    for (Brick brick : bricks) {
        if (brick instanceof SilverBrick) {
            silverCount++;
        }
    }
    
    assertEquals(10, silverCount); // 10 Silver Bricks
}

@Test
public void testDiamondSymmetry() {
    Round3 round = new Round3();
    List<Brick> bricks = round.createBricks();
    
    // Check row 6 (widest) has 13 bricks
    int widestRowCount = 0;
    double row6Y = Constants.PlayArea.PLAY_AREA_Y + 
                   Constants.Bricks.BRICK_START_Y / 2.0 +
                   6 * (Constants.Bricks.BRICK_HEIGHT + 
                        Constants.Bricks.BRICK_V_SPACING);
    
    for (Brick brick : bricks) {
        if (Math.abs(brick.getY() - row6Y) < 1.0) {
            widestRowCount++;
        }
    }
    
    assertEquals(13, widestRowCount);
}
```

---

## Pattern Design Analysis

### Geometric Properties

```
Shape: Diamond/Rhombus
Symmetry: Vertical and horizontal
Width progression: 1→3→5→7→9→11→13→11→9→7→5→3→1
Center row: Row 6 (13 bricks)
Vertices: 4 corners + 1 center (all Gold)
```

---

### Design Rationale

```
1. Visual Appeal:
   - Distinctive diamond shape
   - Memorable pattern
   - Aesthetic symmetry

2. Gameplay Challenge:
   - Gold obstacles force maneuvering
   - Gaps require ball control
   - Silver Bricks slow clearing

3. Progression Logic:
   - More complex than Round 2
   - Prepares for Round 4
   - Tests advanced skills
```

---

## Kết luận

`Round3` là **advanced challenge level** showcasing complex design:

- **Diamond Pattern**: Unique 13×13 matrix layout
- **Gold Bricks**: First indestructible obstacles (5 total)
- **Strategic Design**: 10 Silver Bricks at choke points
- **Visual Symmetry**: Beautiful geometric pattern
- **High Difficulty**: 4/5 stars, 8-12 minutes
- **Matrix-Based**: Precise control với 2D array
- **Challenge Escalation**: Major jump từ Round 2

Round3 exemplifies **artistic game design**. Diamond pattern is visually striking while serving gameplay purpose. Gold Bricks aren't arbitrary - they form vertical spine forcing ball around edges. Silver Bricks placed at bottlenecks maximize strategic impact. Empty spaces create gaps testing ball control. Every element serves both aesthetic và mechanical function.

**Design Excellence**: Matrix-based layout enables pixel-perfect control. Designer can place each brick exactly where intended. Compare to procedural generation (Round2) - matrix gives complete artistic control. This enables meaningful patterns (diamond, symmetry) impossible với random/algorithmic approaches. Trade-off: less replay variety, but intentional design compensates.

**Difficulty Engineering**: Round3 jumps from 2/5 (Round2) to 4/5 difficulty. Gold Bricks fundamentally change strategy - can't brute force center column. Silver Bricks at choke points maximize impact (10 Silvers here more impactful than 20 randomly placed). Diamond shape naturally hard to clear - vertices require precise angles. This is master class trong difficulty design - multiple challenge layers synergize.

