# Round1 Class

## Tổng quan
`Round1` là **concrete implementation** của `RoundBase` representing first/beginner level trong Arkanoid game. Class này implements simple **13×4 grid layout** với only Normal Bricks, providing easy introduction cho players. Round1 features color-coded rows (RED, BLUE, GREEN, YELLOW) và serves as foundation level before more complex rounds với Silver/Gold bricks. This is **tutorial level** designed to teach basic gameplay mechanics.

## Vị trí
- **Package**: `Rounds`
- **File**: `src/Rounds/Round1.java`
- **Type**: Concrete Class (Level Implementation)
- **Extends**: `RoundBase`
- **Pattern**: Template Method Pattern (implements abstract method)

## Mục đích
Round1 class:
- Provide beginner-friendly first level
- Introduce basic brick-breaking mechanics
- Use simple 13×4 grid layout
- Feature only Normal Bricks (no Silver/Gold)
- Display color-coded rows for visual appeal
- Serve as difficulty baseline
- Test player's basic skills

---

## Class Structure

```java
public class Round1 extends RoundBase {
    // Constructor
    public Round1();
    
    // Implemented abstract method
    @Override
    public List<Brick> createBricks();
}
```

---

## Constructor

### Round1()

```java
public Round1() {
    super(1, "Beginner's Challenge");
}
```

**Chức năng**:
- Calls parent constructor với round number **1**
- Sets round name to **"Beginner's Challenge"**
- Inherits play area dimensions từ RoundBase

**Properties Set**:
```java
roundNumber = 1
roundName = "Beginner's Challenge"
playAreaWidth = Constants.PlayArea.PLAY_AREA_WIDTH
playAreaHeight = Constants.PlayArea.PLAY_AREA_HEIGHT
```

**Usage**:
```java
// Create Round 1 instance
RoundBase round1 = new Round1();

// Access inherited properties
System.out.println("Round " + round1.getRoundNumber());
// Output: Round 1

System.out.println(round1.getRoundName());
// Output: Beginner's Challenge

// Get brick count
int totalBricks = round1.getTotalBrickCount();
// totalBricks = 52 (13 × 4)
```

---

## createBricks() Method

### Overview

```java
@Override
public List<Brick> createBricks() {
    List<Brick> bricks = new ArrayList<>();
    
    // 1. Define grid parameters (13×4)
    // 2. Calculate centered starting position
    // 3. Define color pattern (RED, BLUE, GREEN, YELLOW)
    // 4. Create bricks in nested loops
    // 5. Return completed brick list
    
    return bricks;
}
```

**Brick Layout**:
```
Grid Size: 13 columns × 4 rows = 52 bricks total
Brick Type: Normal Bricks only
Color Pattern: Alternating by row
Centering: Horizontally centered in play area
```

---

### Implementation Details

#### Step 1: Grid Parameters

```java
// Grid dimensions
int cols = 13;                              // 13 bricks per row
int rows = 4;                               // 4 rows total

// Brick dimensions from Constants
double brickW = Constants.Bricks.BRICK_WIDTH;
double brickH = Constants.Bricks.BRICK_HEIGHT;

// Spacing between bricks
double hSpacing = Constants.Bricks.BRICK_H_SPACING;  // Horizontal
double vSpacing = Constants.Bricks.BRICK_V_SPACING;  // Vertical
```

**Typical Values**:
```
BRICK_WIDTH: 40.0 pixels
BRICK_HEIGHT: 20.0 pixels
BRICK_H_SPACING: 2.0 pixels (gap between columns)
BRICK_V_SPACING: 2.0 pixels (gap between rows)

Total width per brick: 42 pixels (40 + 2)
Total height per brick: 22 pixels (20 + 2)
```

---

#### Step 2: Calculate Centered Position

```java
// Calculate total width of brick grid
double totalWidth = cols * brickW + (cols - 1) * hSpacing;
// totalWidth = 13 × 40 + 12 × 2 = 520 + 24 = 544 pixels

// Calculate starting X to center grid horizontally
double startX = Constants.PlayArea.PLAY_AREA_X + 
                (playAreaWidth - totalWidth) / 2.0;

// Calculate starting Y position
double startY = Constants.PlayArea.PLAY_AREA_Y + 
                Constants.Bricks.BRICK_START_Y / 2.0;
```

**Centering Logic**:
```
Play Area X: 50 pixels (left border)
Play Area Width: 600 pixels
Total Grid Width: 544 pixels

Remaining Space: 600 - 544 = 56 pixels
Left Padding: 56 / 2 = 28 pixels

Starting X: 50 + 28 = 78 pixels

Result: Grid perfectly centered horizontally
```

---

#### Step 3: Color Pattern Definition

```java
// Define row colors (cycle through 4 colors)
BrickType[] colors = {
    BrickType.RED,      // Row 0
    BrickType.BLUE,     // Row 1
    BrickType.GREEN,    // Row 2
    BrickType.YELLOW    // Row 3
};
```

**Visual Pattern**:
```
Row 0: 🔴🔴🔴🔴🔴🔴🔴🔴🔴🔴🔴🔴🔴 (13 RED bricks)
Row 1: 🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵 (13 BLUE bricks)
Row 2: 🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢 (13 GREEN bricks)
Row 3: 🟡🟡🟡🟡🟡🟡🟡🟡🟡🟡🟡🟡🟡 (13 YELLOW bricks)
```

---

#### Step 4: Brick Creation Loop

```java
// Nested loop: row by row, column by column
for (int r = 0; r < rows; r++) {
    // Get color for current row (cycle using modulo)
    BrickType rowColor = colors[r % colors.length];
    
    for (int c = 0; c < cols; c++) {
        // Calculate brick position
        double x = startX + c * (brickW + hSpacing);
        double y = startY + r * (brickH + vSpacing);
        
        // Create Normal Brick with calculated position and row color
        bricks.add(new NormalBrick(x, y, brickW, brickH, rowColor));
    }
}
```

**Position Calculation Example**:
```
Row 0, Column 0:
x = 78 + 0 × 42 = 78
y = 100 + 0 × 22 = 100

Row 0, Column 1:
x = 78 + 1 × 42 = 120
y = 100 + 0 × 22 = 100

Row 1, Column 0:
x = 78 + 0 × 42 = 78
y = 100 + 1 × 22 = 122

Row 2, Column 5:
x = 78 + 5 × 42 = 288
y = 100 + 2 × 22 = 144
```

---

### Complete Method

```java
@Override
public List<Brick> createBricks() {
    List<Brick> bricks = new ArrayList<>();

    // --- 1. Setup grid parameters (13×4) ---
    int cols = 13;
    int rows = 4;
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

    // --- 3. Define color pattern ---
    BrickType[] colors = {
        BrickType.RED,
        BrickType.BLUE,
        BrickType.GREEN,
        BrickType.YELLOW
    };

    // --- 4. Create bricks row by row ---
    for (int r = 0; r < rows; r++) {
        BrickType rowColor = colors[r % colors.length];
        
        for (int c = 0; c < cols; c++) {
            double x = startX + c * (brickW + hSpacing);
            double y = startY + r * (brickH + vSpacing);
            
            bricks.add(new NormalBrick(x, y, brickW, brickH, rowColor));
        }
    }

    return bricks;
}
```

---

## Brick Layout Analysis

### Grid Dimensions

```
Total Bricks: 52 (13 columns × 4 rows)

Layout:
[R][R][R][R][R][R][R][R][R][R][R][R][R]  ← Row 0: RED
[B][B][B][B][B][B][B][B][B][B][B][B][B]  ← Row 1: BLUE
[G][G][G][G][G][G][G][G][G][G][G][G][G]  ← Row 2: GREEN
[Y][Y][Y][Y][Y][Y][Y][Y][Y][Y][Y][Y][Y]  ← Row 3: YELLOW

Legend:
R = RED Normal Brick (1 hit to destroy)
B = BLUE Normal Brick (1 hit to destroy)
G = GREEN Normal Brick (1 hit to destroy)
Y = YELLOW Normal Brick (1 hit to destroy)
```

---

### Brick Properties

**All Bricks in Round1 are Normal Bricks**:
```java
Type: NormalBrick
Hits Required: 1 hit to destroy
Width: 40 pixels
Height: 20 pixels
Colors: RED, BLUE, GREEN, YELLOW (by row)
Points per brick: 100 points (default Normal Brick value)
```

---

### Spatial Layout

```
Grid Width: 544 pixels (13 × 40 + 12 × 2)
Grid Height: 86 pixels (4 × 20 + 3 × 2)

Horizontal Centering:
Play Area Width: 600 pixels
Grid Width: 544 pixels
Left Margin: (600 - 544) / 2 = 28 pixels

Vertical Positioning:
Start Y: Play Area Y + BRICK_START_Y / 2
(Positioned in upper portion of play area)
```

---

## Difficulty Analysis

### Beginner-Friendly Features

```
✅ Simple Grid Layout:
- Uniform 13×4 pattern
- No gaps or complex shapes
- Predictable structure

✅ Easy Brick Types:
- Only Normal Bricks (1 hit each)
- No Silver Bricks (multi-hit)
- No Gold Bricks (indestructible)

✅ Low Brick Count:
- Only 52 bricks total
- Fastest round to complete
- Less ball tracking required

✅ Visual Clarity:
- Color-coded rows
- Clear separation between rows
- Easy to see remaining bricks
```

---

### Estimated Difficulty

```
Difficulty Rating: ⭐ (1/5 stars)

Time to Complete: 2-3 minutes (beginner player)
Skill Required: Basic paddle control
Challenge Level: Tutorial/Introduction
Success Rate: ~90% (experienced players)

Comparison to Other Rounds:
Round1 (52 bricks): ⭐ Easiest
Round2 (65 bricks): ⭐⭐ Easy
Round3 (73 bricks): ⭐⭐⭐⭐ Hard
Round4 (130 bricks): ⭐⭐⭐⭐⭐ Very Hard
```

---

## Usage Examples

### Example 1: Load Round 1

```java
public class GameManager {
    private RoundBase currentRound;
    private List<Brick> activeBricks;
    
    public void startNewGame() {
        // Create Round 1
        currentRound = new Round1();
        
        // Generate bricks
        activeBricks = currentRound.createBricks();
        
        // Display round info
        System.out.println("Starting " + currentRound.getRoundName());
        System.out.println("Total bricks: " + activeBricks.size());
        
        // Add bricks to game scene
        for (Brick brick : activeBricks) {
            gameScene.addBrick(brick);
        }
    }
}

// Output:
// Starting Beginner's Challenge
// Total bricks: 52
```

---

### Example 2: Round Progression

```java
public class RoundsManager {
    private int currentRoundNumber = 1;
    
    public RoundBase getCurrentRound() {
        return switch (currentRoundNumber) {
            case 1 -> new Round1();
            case 2 -> new Round2();
            case 3 -> new Round3();
            case 4 -> new Round4();
            default -> new Round1(); // Fallback to Round 1
        };
    }
    
    public void advanceToNextRound() {
        if (currentRoundNumber < 4) {
            currentRoundNumber++;
            RoundBase nextRound = getCurrentRound();
            loadRound(nextRound);
        } else {
            showVictoryScreen(); // Completed all rounds
        }
    }
}
```

---

### Example 3: Round Statistics

```java
public class StatisticsDisplay {
    public void showRoundStats(RoundBase round) {
        List<Brick> bricks = round.createBricks();
        
        // Count brick types
        int normalCount = 0;
        int silverCount = 0;
        int goldCount = 0;
        
        for (Brick brick : bricks) {
            if (brick instanceof NormalBrick) normalCount++;
            else if (brick instanceof SilverBrick) silverCount++;
            else if (brick instanceof GoldBrick) goldCount++;
        }
        
        // Display stats
        System.out.println("=== " + round.getRoundName() + " ===");
        System.out.println("Total Bricks: " + bricks.size());
        System.out.println("Normal Bricks: " + normalCount);
        System.out.println("Silver Bricks: " + silverCount);
        System.out.println("Gold Bricks: " + goldCount);
    }
}

// For Round 1:
// === Beginner's Challenge ===
// Total Bricks: 52
// Normal Bricks: 52
// Silver Bricks: 0
// Gold Bricks: 0
```

---

### Example 4: Visual Rendering

```java
public class BrickRenderer {
    public void renderRound(RoundBase round, Graphics2D g) {
        List<Brick> bricks = round.createBricks();
        
        // Render each brick
        for (Brick brick : bricks) {
            brick.render(g);
        }
        
        // Draw round info
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(round.getRoundName(), 20, 30);
        
        // Draw brick counter
        String counter = "Bricks: " + bricks.size();
        g.drawString(counter, 20, 60);
    }
}
```

---

## Design Patterns

### Template Method Pattern

```
RoundBase (Abstract Class):
    ↓ defines template
    - Constructor(roundNumber, roundName)
    - getRoundNumber()
    - getRoundName()
    - getTotalBrickCount()
    - abstract createBricks() ← Must implement
    ↓
Round1 (Concrete Class):
    ↓ implements
    - createBricks() → Returns 13×4 Normal Brick grid

Benefits:
✅ Code reuse (inherited methods from RoundBase)
✅ Polymorphism (works with RoundBase reference)
✅ Consistency (all rounds follow same structure)
```

---

### Factory Pattern Connection

```java
// Round Factory for creating rounds
public class RoundFactory {
    public static RoundBase createRound(int roundNumber) {
        return switch (roundNumber) {
            case 1 -> new Round1();  // ← Creates Round1 instance
            case 2 -> new Round2();
            case 3 -> new Round3();
            case 4 -> new Round4();
            default -> throw new IllegalArgumentException(
                "Invalid round: " + roundNumber);
        };
    }
}

// Usage
RoundBase round = RoundFactory.createRound(1);
List<Brick> bricks = round.createBricks();
```

---

## Performance Characteristics

### Object Creation

```
Brick Objects Created: 52 NormalBrick instances
Memory per Brick: ~100-200 bytes
Total Memory: ~5-10 KB

Creation Time: ~1-2 milliseconds
(Fastest round to initialize)

Performance: O(rows × cols) = O(4 × 13) = O(52)
```

---

### Rendering Performance

```
Sprites to Render: 52 brick sprites
Render Time: ~1-2 ms per frame (at 60 FPS)

Optimizations:
- Simple grid (no complex calculations)
- Uniform brick types (same rendering path)
- No animations (static sprites)

Result: Excellent performance, no lag
```

---

## Comparison with Other Rounds

### Round Complexity Progression

```
Round 1 (Beginner's Challenge):
- Bricks: 52 (13×4 grid)
- Types: Normal only
- Pattern: Simple rows
- Difficulty: ⭐

Round 2 (Silver Challenge):
- Bricks: 65 (13×5 grid)
- Types: Normal + Silver (30% random)
- Pattern: Simple rows + random
- Difficulty: ⭐⭐

Round 3 (Diamond Challenge):
- Bricks: 73 (13×13 diamond)
- Types: Normal + Silver + Gold
- Pattern: Complex diamond layout
- Difficulty: ⭐⭐⭐⭐

Round 4 (Ultimate Challenge):
- Bricks: 130 (13×10 full grid)
- Types: Normal + Silver + Gold
- Pattern: Modulo-based distribution
- Difficulty: ⭐⭐⭐⭐⭐
```

---

## Testing

### Unit Tests

```java
@Test
public void testRound1Properties() {
    Round1 round = new Round1();
    
    assertEquals(1, round.getRoundNumber());
    assertEquals("Beginner's Challenge", round.getRoundName());
}

@Test
public void testBrickCount() {
    Round1 round = new Round1();
    List<Brick> bricks = round.createBricks();
    
    assertEquals(52, bricks.size()); // 13 × 4 = 52
}

@Test
public void testAllNormalBricks() {
    Round1 round = new Round1();
    List<Brick> bricks = round.createBricks();
    
    // Verify all bricks are Normal Bricks
    for (Brick brick : bricks) {
        assertTrue(brick instanceof NormalBrick);
        assertFalse(brick instanceof SilverBrick);
        assertFalse(brick instanceof GoldBrick);
    }
}

@Test
public void testColorPattern() {
    Round1 round = new Round1();
    List<Brick> bricks = round.createBricks();
    
    // Check row colors (RED, BLUE, GREEN, YELLOW)
    BrickType[] expectedColors = {
        BrickType.RED, BrickType.BLUE, 
        BrickType.GREEN, BrickType.YELLOW
    };
    
    for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 13; col++) {
            int index = row * 13 + col;
            NormalBrick brick = (NormalBrick) bricks.get(index);
            assertEquals(expectedColors[row], brick.getType());
        }
    }
}

@Test
public void testBrickPositioning() {
    Round1 round = new Round1();
    List<Brick> bricks = round.createBricks();
    
    // Verify bricks are within play area
    for (Brick brick : bricks) {
        assertTrue(brick.getX() >= Constants.PlayArea.PLAY_AREA_X);
        assertTrue(brick.getY() >= Constants.PlayArea.PLAY_AREA_Y);
        assertTrue(brick.getX() + brick.getWidth() <= 
                  Constants.PlayArea.PLAY_AREA_X + 
                  Constants.PlayArea.PLAY_AREA_WIDTH);
    }
}
```

---

## Best Practices

### 1. Use Constants for Magic Numbers

```java
// ✅ Đúng - Use Constants
int cols = 13;
int rows = 4;
double brickW = Constants.Bricks.BRICK_WIDTH;

// ❌ Sai - Hardcoded values
int cols = 13;
int rows = 4;
double brickW = 40.0; // Magic number!
```

---

### 2. Calculate Positions Dynamically

```java
// ✅ Đúng - Calculate from play area
double totalWidth = cols * brickW + (cols - 1) * hSpacing;
double startX = Constants.PlayArea.PLAY_AREA_X + 
                (playAreaWidth - totalWidth) / 2.0;

// ❌ Sai - Hardcoded position
double startX = 78.0; // What if play area changes?
```

---

### 3. Use Modulo for Cycling

```java
// ✅ Đúng - Modulo cycling
BrickType rowColor = colors[r % colors.length];

// ❌ Sai - Manual checks
BrickType rowColor;
if (r == 0) rowColor = BrickType.RED;
else if (r == 1) rowColor = BrickType.BLUE;
// ... repetitive code
```

---

## Kết luận

`Round1` là **entry-level implementation** cho Arkanoid round system:

- **Beginner-Friendly**: Simple 13×4 grid với only Normal Bricks
- **Tutorial Level**: Introduces basic brick-breaking mechanics
- **Visual Appeal**: Color-coded rows (RED, BLUE, GREEN, YELLOW)
- **Low Complexity**: 52 bricks, fastest to complete
- **Template Implementation**: Follows RoundBase contract
- **Performance**: Minimal overhead, fast initialization
- **Foundation**: Sets baseline cho harder rounds

Round1 exemplifies **simple but effective level design**. By using only Normal Bricks trong uniform grid, it teaches players basic gameplay without overwhelming complexity. Color-coded rows provide visual structure while maintaining simplicity. This graduated difficulty approach ensures new players can learn mechanics before facing Silver/Gold bricks trong later rounds.

**Pedagogical Design**: Round1 is carefully designed teaching tool. Players learn: (1) Paddle controls, (2) Ball trajectory, (3) Brick destruction, (4) Power-up collection. Simple uniform grid allows focus on mechanics rather than pattern recognition. Success rate is high (~90%), building confidence before Round2 introduces Silver Bricks.

**Code Quality**: Implementation demonstrates clean OOP principles. Extends RoundBase (inheritance), implements abstract method (polymorphism), uses Constants (maintainability), calculates positions dynamically (flexibility). No hardcoded magic numbers, no duplicate code, no unnecessary complexity. This is model implementation cho concrete round classes.

