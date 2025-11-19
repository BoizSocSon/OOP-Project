# RoundBase Abstract Class

## Tổng quan
`RoundBase` là **abstract base class** định nghĩa foundation cho tất cả level/round trong Arkanoid game. Class này implement **Template Method Pattern**, providing common structure và behavior cho mọi round trong khi allowing subclasses tự define specific brick layouts. RoundBase encapsulates shared properties như round number, name, và play area dimensions, đồng thời declare abstract method `createBricks()` mà mọi concrete round phải implement.

## Vị trí
- **Package**: `Rounds`
- **File**: `src/Rounds/RoundBase.java`
- **Type**: Abstract Class (Base Class)
- **Pattern**: Template Method Pattern
- **Subclasses**: `Round1`, `Round2`, `Round3`, `Round4`

## Mục đích
RoundBase class:
- Define common structure cho all rounds
- Encapsulate shared round properties
- Declare abstract methods cho subclass implementation
- Provide utility methods (brick count, getters)
- Ensure consistent round initialization
- Support Template Method Pattern
- Enable polymorphic round management

---

## Class Structure

```java
public abstract class RoundBase {
    // Round properties
    protected int roundNumber;          // Round ID (1, 2, 3, 4)
    protected String roundName;         // Descriptive name
    protected double playAreaWidth;     // Play area width
    protected double playAreaHeight;    // Play area height
    
    // Constructor
    public RoundBase(int roundNumber, String roundName);
    
    // Abstract method (must be implemented by subclasses)
    public abstract List<Brick> createBricks();
    
    // Concrete methods (inherited by all subclasses)
    public int getRoundNumber();
    public String getRoundName();
    public int getTotalBrickCount();
}
```

---

## Fields

### protected int roundNumber

**Mô tả**: Số thứ tự của round (1, 2, 3, 4, ...)

**Visibility**: `protected` - accessible by subclasses

**Usage**:
```java
// In Round1
public Round1() {
    super(1, "Beginner's Challenge");
    // roundNumber = 1
}

// Access in subclass
@Override
public List<Brick> createBricks() {
    System.out.println("Creating bricks for Round " + roundNumber);
    // ...
}
```

---

### protected String roundName

**Mô tả**: Tên mô tả của round (e.g., "Beginner's Challenge", "Diamond Challenge")

**Visibility**: `protected` - accessible by subclasses

**Usage**:
```java
// Different names for different rounds
Round1: "Beginner's Challenge"
Round2: "Silver Challenge"
Round3: "Diamond Challenge"
Round4: "Ultimate Challenge"

// Display in UI
System.out.println("Now playing: " + roundName);
```

---

### protected double playAreaWidth

**Mô tả**: Chiều rộng của play area (lấy từ `Constants.PlayArea.PLAY_AREA_WIDTH`)

**Initialization**: Set trong constructor từ Constants

**Usage**:
```java
// Used for centering brick layout
double totalBrickWidth = cols * brickWidth + (cols - 1) * spacing;
double startX = (playAreaWidth - totalBrickWidth) / 2.0;
```

---

### protected double playAreaHeight

**Mô tả**: Chiều cao của play area (lấy từ `Constants.PlayArea.PLAY_AREA_HEIGHT`)

**Initialization**: Set trong constructor từ Constants

**Usage**:
```java
// Used for vertical positioning
double startY = Constants.PlayArea.PLAY_AREA_Y + offset;

// Check if bricks fit in play area
if (totalBrickHeight > playAreaHeight) {
    System.err.println("Warning: Bricks exceed play area height!");
}
```

---

## Constructor

### RoundBase(int roundNumber, String roundName)

```java
public RoundBase(int roundNumber, String roundName) {
    this.roundNumber = roundNumber;
    this.roundName = roundName;
    
    // Initialize play area dimensions from Constants
    this.playAreaWidth = Constants.PlayArea.PLAY_AREA_WIDTH;
    this.playAreaHeight = Constants.PlayArea.PLAY_AREA_HEIGHT;
}
```

**Tham số**:
- `roundNumber` - Số thứ tự round (1, 2, 3, 4)
- `roundName` - Tên descriptive cho round

**Chức năng**:
1. Store round number và name
2. Initialize play area dimensions từ Constants
3. Provide consistent initialization cho all rounds

**Usage**:
```java
// In Round1 subclass
public Round1() {
    super(1, "Beginner's Challenge");
    // Calls RoundBase constructor
    // Sets roundNumber = 1, roundName = "Beginner's Challenge"
    // Initializes playAreaWidth and playAreaHeight
}

// In Round3 subclass
public Round3() {
    super(3, "Diamond Challenge");
    // roundNumber = 3, roundName = "Diamond Challenge"
}
```

---

## Abstract Method

### abstract List<Brick> createBricks()

```java
public abstract List<Brick> createBricks();
```

**Mô tả**: Abstract method must be implemented bởi tất cả concrete subclasses để define specific brick layout cho round đó.

**Trả về**: `List<Brick>` - Danh sách tất cả brick objects cho round

**Implementation trong subclasses**:

#### Round1 - Simple Grid
```java
@Override
public List<Brick> createBricks() {
    List<Brick> bricks = new ArrayList<>();
    
    // Create 13x4 grid of Normal Bricks
    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 13; c++) {
            double x = startX + c * (brickW + hSpacing);
            double y = startY + r * (brickH + vSpacing);
            
            BrickType color = colors[r % colors.length];
            bricks.add(new NormalBrick(x, y, brickW, brickH, color));
        }
    }
    
    return bricks;
}
```

#### Round2 - Random Silver Bricks
```java
@Override
public List<Brick> createBricks() {
    List<Brick> bricks = new ArrayList<>();
    Random rnd = new Random();
    
    // 13x5 grid with 30% chance of Silver Bricks
    for (int r = 0; r < 5; r++) {
        for (int c = 0; c < 13; c++) {
            double x = startX + c * (brickW + hSpacing);
            double y = startY + r * (brickH + vSpacing);
            
            if (rnd.nextDouble() < 0.3) {
                bricks.add(new SilverBrick(x, y, brickW, brickH));
            } else {
                BrickType color = colors[r % colors.length];
                bricks.add(new NormalBrick(x, y, brickW, brickH, color));
            }
        }
    }
    
    return bricks;
}
```

#### Round3 - Diamond Pattern
```java
@Override
public List<Brick> createBricks() {
    List<Brick> bricks = new ArrayList<>();
    
    // 13x13 layout array (0=empty, 1-8=Normal, 9=Gold, 10=Silver)
    int[][] layout = {
        {0,0,0,0,0,0,9,0,0,0,0,0,0},
        {0,0,0,0,0,1,2,1,0,0,0,0,0},
        // ... diamond pattern
    };
    
    for (int r = 0; r < layout.length; r++) {
        for (int c = 0; c < layout[0].length; c++) {
            int type = layout[r][c];
            if (type == 0) continue;
            
            double x = startX + c * (brickW + hSpacing);
            double y = startY + r * (brickH + vSpacing);
            
            if (type == 9) {
                bricks.add(new GoldBrick(x, y, brickW, brickH));
            } else if (type == 10) {
                bricks.add(new SilverBrick(x, y, brickW, brickH));
            } else {
                BrickType color = colors[(type - 1) % colors.length];
                bricks.add(new NormalBrick(x, y, brickW, brickH, color));
            }
        }
    }
    
    return bricks;
}
```

#### Round4 - Modulo Pattern
```java
@Override
public List<Brick> createBricks() {
    List<Brick> bricks = new ArrayList<>();
    
    // 13x10 grid with modulo-based brick types
    for (int r = 0; r < 10; r++) {
        for (int c = 0; c < 13; c++) {
            double x = startX + c * (brickW + hSpacing);
            double y = startY + r * (brickH + vSpacing);
            
            if ((r + c) % 7 == 0) {
                bricks.add(new GoldBrick(x, y, brickW, brickH));
            } else if ((r + c) % 3 == 0) {
                bricks.add(new SilverBrick(x, y, brickW, brickH));
            } else {
                BrickType color = BrickType.values()[
                    (r + c) % (BrickType.values().length - 2)
                ];
                bricks.add(new NormalBrick(x, y, brickW, brickH, color));
            }
        }
    }
    
    return bricks;
}
```

---

## Getter Methods

### getRoundNumber()

```java
public int getRoundNumber() {
    return roundNumber;
}
```

**Trả về**: Round number (1, 2, 3, 4)

**Usage**:
```java
RoundBase currentRound = new Round2();
int number = currentRound.getRoundNumber();
// number = 2

// Display round info
System.out.println("Round " + currentRound.getRoundNumber());
// Output: Round 2

// Conditional logic based on round
if (currentRound.getRoundNumber() >= 3) {
    System.out.println("Advanced round!");
}
```

---

### getRoundName()

```java
public String getRoundName() {
    return roundName;
}
```

**Trả về**: Descriptive round name

**Usage**:
```java
RoundBase round = new Round3();
String name = round.getRoundName();
// name = "Diamond Challenge"

// Display in UI
titleLabel.setText(round.getRoundName());

// Display round transition
System.out.println("Starting: " + round.getRoundName());
// Output: Starting: Diamond Challenge
```

---

### getTotalBrickCount()

```java
public int getTotalBrickCount() {
    return createBricks().size();
}
```

**Trả về**: Total number of bricks trong round

**⚠️ Performance Note**: Method này calls `createBricks()` mỗi lần được gọi, có thể expensive nếu gọi repeatedly.

**Usage**:
```java
RoundBase round = new Round1();
int brickCount = round.getTotalBrickCount();
// brickCount = 52 (13 cols × 4 rows)

// Display stats
System.out.println("Total bricks: " + round.getTotalBrickCount());

// Check completion
int remainingBricks = gameManager.getRemainingBricks();
int totalBricks = round.getTotalBrickCount();
int destroyedBricks = totalBricks - remainingBricks;

System.out.println(destroyedBricks + "/" + totalBricks + " bricks destroyed");
```

**Optimization Suggestion**:
```java
// Cache brick count to avoid repeated calls
public class RoundManager {
    private RoundBase currentRound;
    private int totalBrickCount;
    
    public void loadRound(RoundBase round) {
        this.currentRound = round;
        this.totalBrickCount = round.getTotalBrickCount(); // Call once
    }
    
    public int getTotalBrickCount() {
        return totalBrickCount; // Return cached value
    }
}
```

---

## Template Method Pattern

### Pattern Structure

```
RoundBase (Abstract Class)
    ↓ defines
Template Methods:
- getRoundNumber()
- getRoundName()
- getTotalBrickCount() ← calls abstract method
    ↓ requires
Abstract Method:
- createBricks() ← implemented by subclasses
    ↓ implemented by
Concrete Classes:
- Round1 (13×4 simple grid)
- Round2 (13×5 with random Silver)
- Round3 (13×13 diamond pattern)
- Round4 (13×10 modulo pattern)
```

---

### Pattern Benefits

```java
// 1. Code Reuse - Common code in base class
public abstract class RoundBase {
    // ✅ Shared initialization logic
    protected double playAreaWidth;
    protected double playAreaHeight;
    
    public RoundBase(int roundNumber, String roundName) {
        this.playAreaWidth = Constants.PlayArea.PLAY_AREA_WIDTH;
        this.playAreaHeight = Constants.PlayArea.PLAY_AREA_HEIGHT;
    }
    
    // ✅ Shared utility method
    public int getTotalBrickCount() {
        return createBricks().size();
    }
}

// 2. Polymorphism - Work with base class reference
public class RoundsManager {
    private RoundBase currentRound;
    
    public void loadRound(int roundNumber) {
        currentRound = switch (roundNumber) {
            case 1 -> new Round1();
            case 2 -> new Round2();
            case 3 -> new Round3();
            case 4 -> new Round4();
            default -> new Round1();
        };
        
        // Works with any round type
        List<Brick> bricks = currentRound.createBricks();
        System.out.println(currentRound.getRoundName());
    }
}

// 3. Extensibility - Easy to add new rounds
public class Round5 extends RoundBase {
    public Round5() {
        super(5, "Nightmare Challenge");
    }
    
    @Override
    public List<Brick> createBricks() {
        // Custom layout for Round 5
        return customLayout();
    }
}
```

---

## Usage Patterns

### Pattern 1: Round Management System

```java
public class RoundsManager {
    private RoundBase currentRound;
    private int currentRoundNumber = 1;
    
    public void loadRound(int roundNumber) {
        currentRound = switch (roundNumber) {
            case 1 -> new Round1();
            case 2 -> new Round2();
            case 3 -> new Round3();
            case 4 -> new Round4();
            default -> throw new IllegalArgumentException(
                "Invalid round: " + roundNumber);
        };
        
        currentRoundNumber = roundNumber;
    }
    
    public List<Brick> getCurrentBricks() {
        return currentRound.createBricks();
    }
    
    public void nextRound() {
        if (currentRoundNumber < 4) {
            loadRound(currentRoundNumber + 1);
        } else {
            // Game completed
            showVictoryScreen();
        }
    }
    
    public String getCurrentRoundName() {
        return currentRound.getRoundName();
    }
}
```

---

### Pattern 2: Round Initialization

```java
public class GameManager {
    private RoundsManager roundsManager;
    private List<Brick> activeBricks;
    
    public void startGame() {
        // Initialize rounds manager
        roundsManager = new RoundsManager();
        roundsManager.loadRound(1); // Start with Round 1
        
        // Load bricks for current round
        activeBricks = roundsManager.getCurrentBricks();
        
        // Display round info
        displayRoundInfo();
    }
    
    private void displayRoundInfo() {
        RoundBase round = roundsManager.getCurrentRound();
        
        System.out.println("=== Round " + round.getRoundNumber() + " ===");
        System.out.println("Name: " + round.getRoundName());
        System.out.println("Total Bricks: " + round.getTotalBrickCount());
        System.out.println("=========================");
    }
}
```

---

### Pattern 3: Round Progression

```java
public class GameManager {
    private RoundBase currentRound;
    private int destroyedBricks = 0;
    private int totalBricks;
    
    public void loadRound(RoundBase round) {
        this.currentRound = round;
        this.totalBricks = round.getTotalBrickCount(); // Cache count
        this.destroyedBricks = 0;
        
        // Create bricks
        List<Brick> bricks = round.createBricks();
        addBricksToScene(bricks);
    }
    
    public void onBrickDestroyed() {
        destroyedBricks++;
        
        // Check round completion
        if (destroyedBricks >= totalBricks) {
            onRoundComplete();
        }
    }
    
    private void onRoundComplete() {
        System.out.println("Round " + currentRound.getRoundNumber() + 
                          " completed!");
        
        // Load next round
        int nextRoundNum = currentRound.getRoundNumber() + 1;
        
        if (nextRoundNum <= 4) {
            RoundBase nextRound = createRound(nextRoundNum);
            loadRound(nextRound);
        } else {
            showVictoryScreen();
        }
    }
}
```

---

### Pattern 4: Round Preview

```java
public class RoundSelectScreen {
    private List<RoundBase> allRounds;
    
    public void initialize() {
        allRounds = List.of(
            new Round1(),
            new Round2(),
            new Round3(),
            new Round4()
        );
    }
    
    public void displayRoundInfo(int roundNumber) {
        RoundBase round = allRounds.get(roundNumber - 1);
        
        // Display round stats
        infoPanel.setText(
            "Round " + round.getRoundNumber() + "\n" +
            round.getRoundName() + "\n" +
            "Total Bricks: " + round.getTotalBrickCount()
        );
        
        // Preview brick layout
        List<Brick> bricks = round.createBricks();
        renderPreview(bricks);
    }
}
```

---

## Common Brick Creation Patterns

### Pattern 1: Centering Brick Grid

```java
// Used in all rounds to center brick layout
int cols = 13;
double brickW = Constants.Bricks.BRICK_WIDTH;
double hSpacing = Constants.Bricks.BRICK_H_SPACING;

// Calculate total width of brick grid
double totalWidth = cols * brickW + (cols - 1) * hSpacing;

// Calculate starting X position to center grid
double startX = Constants.PlayArea.PLAY_AREA_X + 
                (playAreaWidth - totalWidth) / 2.0;

// Now all bricks use startX as base position
for (int c = 0; c < cols; c++) {
    double x = startX + c * (brickW + hSpacing);
    // Create brick at x position
}
```

---

### Pattern 2: Vertical Positioning

```java
// Calculate starting Y position
double startY = Constants.PlayArea.PLAY_AREA_Y + 
                Constants.Bricks.BRICK_START_Y / 2.0;

// Position bricks row by row
for (int r = 0; r < rows; r++) {
    double y = startY + r * (brickH + vSpacing);
    // Create brick at y position
}
```

---

### Pattern 3: Color Cycling

```java
// Define color array
BrickType[] colors = {
    BrickType.RED,
    BrickType.BLUE,
    BrickType.GREEN,
    BrickType.YELLOW
};

// Cycle through colors by row
for (int r = 0; r < rows; r++) {
    BrickType rowColor = colors[r % colors.length];
    
    for (int c = 0; c < cols; c++) {
        bricks.add(new NormalBrick(x, y, brickW, brickH, rowColor));
    }
}
```

---

## Best Practices

### 1. Cache Expensive Calls

```java
// ❌ Sai - Repeated expensive calls
public void update() {
    if (destroyedBricks >= currentRound.getTotalBrickCount()) {
        // getTotalBrickCount() called every frame!
    }
}

// ✅ Đúng - Cache result
public void loadRound(RoundBase round) {
    this.currentRound = round;
    this.totalBricks = round.getTotalBrickCount(); // Call once
}

public void update() {
    if (destroyedBricks >= totalBricks) {
        // Use cached value
    }
}
```

---

### 2. Validate Round Numbers

```java
// ✅ Đúng - Validate input
public void loadRound(int roundNumber) {
    if (roundNumber < 1 || roundNumber > 4) {
        throw new IllegalArgumentException(
            "Round number must be 1-4, got: " + roundNumber);
    }
    
    currentRound = switch (roundNumber) {
        case 1 -> new Round1();
        case 2 -> new Round2();
        case 3 -> new Round3();
        case 4 -> new Round4();
        default -> throw new AssertionError();
    };
}
```

---

### 3. Use Polymorphism

```java
// ✅ Đúng - Work with base class
public class RoundsManager {
    private RoundBase currentRound;
    
    public void loadRound(RoundBase round) {
        this.currentRound = round; // Polymorphic assignment
    }
    
    public List<Brick> getBricks() {
        return currentRound.createBricks(); // Polymorphic call
    }
}

// ❌ Sai - Type-specific code
public class RoundsManager {
    private Round1 round1;
    private Round2 round2;
    // ... separate fields for each round type
    
    public List<Brick> getBricks(int roundNum) {
        if (roundNum == 1) return round1.createBricks();
        if (roundNum == 2) return round2.createBricks();
        // ... repetitive code
    }
}
```

---

### 4. Encapsulate Round Creation

```java
// ✅ Đúng - Factory method
public class RoundFactory {
    public static RoundBase createRound(int roundNumber) {
        return switch (roundNumber) {
            case 1 -> new Round1();
            case 2 -> new Round2();
            case 3 -> new Round3();
            case 4 -> new Round4();
            default -> throw new IllegalArgumentException(
                "Invalid round: " + roundNumber);
        };
    }
}

// Usage
RoundBase round = RoundFactory.createRound(2);
```

---

## Testing

### Unit Tests

```java
@Test
public void testRoundProperties() {
    RoundBase round1 = new Round1();
    
    assertEquals(1, round1.getRoundNumber());
    assertEquals("Beginner's Challenge", round1.getRoundName());
    assertTrue(round1.getTotalBrickCount() > 0);
}

@Test
public void testBrickCreation() {
    RoundBase round = new Round1();
    List<Brick> bricks = round.createBricks();
    
    assertNotNull(bricks);
    assertFalse(bricks.isEmpty());
    assertEquals(52, bricks.size()); // 13×4 = 52
}

@Test
public void testPlayAreaDimensions() {
    RoundBase round = new Round2();
    
    // Verify dimensions initialized from Constants
    assertEquals(Constants.PlayArea.PLAY_AREA_WIDTH, 
                round.playAreaWidth, 0.01);
    assertEquals(Constants.PlayArea.PLAY_AREA_HEIGHT, 
                round.playAreaHeight, 0.01);
}
```

---

## Design Advantages

### 1. Separation of Concerns
```
RoundBase handles:
- ✅ Common properties (number, name, dimensions)
- ✅ Utility methods (getters, brick count)
- ✅ Contract definition (abstract createBricks)

Subclasses handle:
- ✅ Specific brick layouts
- ✅ Difficulty progression
- ✅ Visual patterns
```

---

### 2. Open-Closed Principle
```
Open for Extension:
- ✅ Easy to add new rounds (Round5, Round6, ...)
- ✅ No modification of base class needed

Closed for Modification:
- ✅ Base class remains stable
- ✅ Existing rounds unaffected by new rounds
```

---

### 3. DRY Principle
```
Don't Repeat Yourself:
- ✅ Play area dimensions: initialized once in base class
- ✅ Getter methods: shared by all rounds
- ✅ Total brick count logic: single implementation
```

---

## Kết luận

`RoundBase` là **foundational abstract class** cho round system trong Arkanoid:

- **Template Method Pattern**: Defines structure, delegates details
- **Code Reuse**: Common properties và methods shared
- **Polymorphism**: Works với RoundBase reference
- **Extensibility**: Easy to add new rounds
- **Encapsulation**: Hides play area dimension management
- **Contract**: Enforces createBricks() implementation
- **Simplicity**: Clean, minimal API

RoundBase exemplifies **abstract class design best practices**. Bằng việc extract common functionality into base class, code avoids duplication. Template Method Pattern enables each round to customize brick layout while inheriting shared behavior. Polymorphism allows round management code to work với any round type through base class reference.

**Design Philosophy**: Abstract classes define "what" is common (properties, utilities) và "what" varies (brick layout). Subclasses provide "how" variations are implemented. This separation enables flexibility without sacrificing code reuse. RoundBase provides stable foundation; concrete rounds provide creative variety.

**Pattern Significance**: Template Method là classic GoF pattern. Used extensively trong frameworks (JUnit TestCase, Java Servlet). Base class defines algorithm skeleton (round structure), subclasses fill in specific steps (brick layout). This inversion of control enables framework-style design where base class calls subclass methods rather than vice versa.

**Extensibility Excellence**: Adding Round5 requires only creating new subclass và implementing createBricks(). No changes tới base class, no changes tới existing rounds, no changes tới round management code. This is Open-Closed Principle in action - system open for extension (new rounds) but closed for modification (existing code unchanged).

