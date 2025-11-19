# Constants Class

## Tổng quan
`Constants` là **final utility class** chứa tất cả các **hằng số** (constants) được sử dụng trong game Arkanoid. Đây là **Single Source of Truth** cho tất cả các giá trị cấu hình và magic numbers trong game. Các constants được tổ chức thành **nested static classes** để dễ quản lý và namespace collision avoidance. Class này implement **Constants Pattern** - một best practice trong software engineering để centralize configuration values.

## Vị trí
- **Package**: `Utils`
- **File**: `src/Utils/Constants.java`
- **Type**: Final Utility Class (không thể khởi tạo)
- **Pattern**: Constants Pattern / Configuration Class

## Mục đích
Constants class:
- Centralize tất cả game configuration
- Eliminate magic numbers
- Easy tuning và balancing
- Type safety (compile-time checking)
- Documentation qua naming
- Single point of modification
- Avoid hardcoded values scattered in code

---

## Class Structure

```java
public final class Constants {
    // Private constructor - prevents instantiation
    private Constants() {
        throw new UnsupportedOperationException(
            "This is a utility class and cannot be instantiated");
    }
    
    // Nested static classes for organization
    public static class General { /* ... */ }
    public static class Window { /* ... */ }
    public static class PlayArea { /* ... */ }
    // ... và nhiều nested classes khác
}
```

---

## Nested Static Classes

### 1. General - Hằng số chung

```java
public static class General {
    public static final double EPSILON = 1e-10;
}
```

**EPSILON** (`1e-10`):
- Hằng số cực nhỏ cho floating-point comparison
- Dùng để xử lý floating-point error
- Tránh so sánh `==` trực tiếp với double/float

**Ví dụ sử dụng**:
```java
// ❌ Sai - floating point comparison
if (velocity.getDx() == 0.0) {
    // May fail due to precision errors
}

// ✅ Đúng - use epsilon
if (Math.abs(velocity.getDx()) < Constants.General.EPSILON) {
    // Treats very small values as zero
}
```

---

### 2. Window - Cài đặt cửa sổ

```java
public static class Window {
    public static final int WINDOW_WIDTH = 600;    // pixels
    public static final int WINDOW_HEIGHT = 800;   // pixels
    public static final int WINDOW_TOP_OFFSET = 150;  // Score area
    public static final int WINDOW_SIDE_OFFSET = 22;  // Border width
    public static final String WINDOW_TITLE = "Arkanoid";
    public static final int FPS = 60;  // Frames per second
}
```

**Layout**:
```
┌────────────────────────────────────────┐
│         WINDOW_TOP_OFFSET (150px)      │  ← Score/UI area
├────┬──────────────────────────────┬────┤
│ 22 │                              │ 22 │  ← Borders
│ px │      PLAY AREA               │ px │
│    │                              │    │
│    │                              │    │
│    │                              │    │
│    │                              │    │
└────┴──────────────────────────────┴────┘
      WINDOW_WIDTH = 600px
```

**FPS** (`60`):
- Game update rate: 60 times per second
- Frame time: 1000ms / 60 = ~16.67ms per frame
- Smooth gameplay at 60 FPS

**Sử dụng**:
```java
// Setup JavaFX Stage
stage.setWidth(Constants.Window.WINDOW_WIDTH);
stage.setHeight(Constants.Window.WINDOW_HEIGHT);
stage.setTitle(Constants.Window.WINDOW_TITLE);

// Game loop timing
long frameTime = 1000 / Constants.Window.FPS; // 16.67ms
```

---

### 3. PlayArea - Khu vực chơi

```java
public static class PlayArea {
    // Calculated from Window and Borders constants
    public static final int PLAY_AREA_X = Borders.BORDER_SIDE_WIDTH;
    public static final int PLAY_AREA_Y = 
        Window.WINDOW_TOP_OFFSET + Borders.BORDER_TOP_HEIGHT;
    public static final int PLAY_AREA_WIDTH = 
        Window.WINDOW_WIDTH - (Borders.BORDER_SIDE_WIDTH * 2);
    public static final int PLAY_AREA_HEIGHT = 
        Window.WINDOW_HEIGHT - Window.WINDOW_TOP_OFFSET - Borders.BORDER_TOP_HEIGHT;
}
```

**Calculation**:
```
PLAY_AREA_X = 22 (border width)
PLAY_AREA_Y = 150 + 22 = 172
PLAY_AREA_WIDTH = 600 - (22 * 2) = 556
PLAY_AREA_HEIGHT = 800 - 150 - 22 = 628
```

**Sử dụng**:
```java
// Check if ball is out of bounds
if (ball.getY() > Constants.PlayArea.PLAY_AREA_Y + 
                  Constants.PlayArea.PLAY_AREA_HEIGHT) {
    // Ball fell off screen - lose life
    loseLife();
}

// Position paddle at bottom
paddle.setY(Constants.PlayArea.PLAY_AREA_Y + 
            Constants.PlayArea.PLAY_AREA_HEIGHT - 50);
```

---

### 4. GameRules - Luật chơi

```java
public static class GameRules {
    public static final int INITIAL_LIVES = 3;
    public static final int MAX_LIVES = 5;
    public static final int LIFE_LOST_PENALTY = 500;
    public static final double POWERUP_SPAWN_CHANCE = 0.3; // 30%
}
```

**INITIAL_LIVES** (`3`):
- Start game với 3 mạng
- Classic Arkanoid starting lives

**MAX_LIVES** (`5`):
- Cap số mạng tối đa
- Prevent excessive life farming
- LIFE power-up capped at this value

**LIFE_LOST_PENALTY** (`500`):
- Trừ 500 điểm khi mất mạng
- Risk/reward balance
- Encourage careful play

**POWERUP_SPAWN_CHANCE** (`0.3`):
- 30% chance power-up spawns when brick destroyed
- Then weighted random selection determines type
- Balance: frequent enough to be useful, rare enough to be special

**Sử dụng**:
```java
// Initialize game
int lives = Constants.GameRules.INITIAL_LIVES;

// Life power-up
if (lives < Constants.GameRules.MAX_LIVES) {
    lives++;
}

// Lose life
score = Math.max(0, score + Constants.GameRules.LIFE_LOST_PENALTY);

// Spawn power-up?
if (Math.random() < Constants.GameRules.POWERUP_SPAWN_CHANCE) {
    spawnPowerUp();
}
```

---

### 5. Ball - Cấu hình bóng

```java
public static class Ball {
    public static final double BALL_SIZE = 10.0;     // 10x10 sprite
    public static final double BALL_RADIUS = BALL_SIZE / 2.0;  // 5.0
    public static final double BALL_INITIAL_SPEED = 3.0;  // pixels/frame
    public static final double BALL_MIN_SPEED = 1.5;
    public static final double BALL_MAX_SPEED = 6.0;
    public static final double BALL_SPEED_INCREMENT = 0.1;
}
```

**Speed System**:
```
INITIAL: 3.0 px/frame = 180 px/sec @ 60 FPS
MIN:     1.5 px/frame = 90 px/sec
MAX:     6.0 px/frame = 360 px/sec
INCREMENT: +0.1 px/frame per brick hit
```

**Speed Progression**:
```java
// Start: 3.0
ball.setSpeed(Constants.Ball.BALL_INITIAL_SPEED);

// After 10 brick hits: 3.0 + (10 * 0.1) = 4.0
for (int i = 0; i < 10; i++) {
    onBrickHit();
    speed += Constants.Ball.BALL_SPEED_INCREMENT;
}

// Clamp to max
speed = Math.min(speed, Constants.Ball.BALL_MAX_SPEED);
```

---

### 6. Paddle - Cấu hình thanh đỡ

```java
public static class Paddle {
    public static final double PADDLE_WIDTH = 79.0;       // Normal
    public static final double PADDLE_HEIGHT = 20.0;
    public static final double PADDLE_WIDE_WIDTH = 119.0; // Expanded
    public static final double PADDLE_LIFE_WIDTH = 43.0;  // Life icon
    public static final double PADDLE_LIFE_HEIGHT = 17.0;
    public static final double PADDLE_SPEED = 6.0;        // px/frame
    public static final double PADDLE_MAX_ANGLE = 60.0;   // degrees
}
```

**Paddle Sizes**:
```
Normal:   79px wide × 20px tall
Expanded: 119px wide × 20px tall (1.5x multiplier)
Life Icon: 43px × 17px
```

**PADDLE_MAX_ANGLE** (`60°`):
- Maximum reflection angle
- Ball hits paddle edge → reflects at steeper angle
- Center hit → shallow angle
- Physics calculation:

```java
// Calculate reflection angle based on hit position
double hitOffset = (ballX - paddleCenterX) / (PADDLE_WIDTH / 2);
// hitOffset: -1.0 (left edge) to +1.0 (right edge)

double angle = hitOffset * Constants.Paddle.PADDLE_MAX_ANGLE;
// angle: -60° to +60°

// Convert to velocity
double rad = Math.toRadians(angle);
velocity.setDx(speed * Math.sin(rad));
velocity.setDy(-speed * Math.cos(rad)); // Upward
```

---

### 7. Bricks - Cấu hình gạch

```java
public static class Bricks {
    public static final double BRICK_WIDTH = 32.0;
    public static final double BRICK_HEIGHT = 21.0;
    public static final double BRICK_H_SPACING = 0.0;  // No horizontal gap
    public static final double BRICK_V_SPACING = 0.0;  // No vertical gap
    public static final double BRICK_START_Y = 100.0;  // Y position of first row
}
```

**Layout Calculation**:
```java
// Grid layout
for (int row = 0; row < rows; row++) {
    for (int col = 0; col < cols; col++) {
        double x = col * (Constants.Bricks.BRICK_WIDTH + 
                          Constants.Bricks.BRICK_H_SPACING);
        double y = Constants.Bricks.BRICK_START_Y + 
                   row * (Constants.Bricks.BRICK_HEIGHT + 
                          Constants.Bricks.BRICK_V_SPACING);
        
        Brick brick = new Brick(x, y, type);
        bricks.add(brick);
    }
}
```

**No Spacing** (`0.0`):
- Bricks touch each other
- Classic Arkanoid look
- To add gaps, increase H_SPACING / V_SPACING

---

### 8. PowerUps - Cấu hình power-ups

```java
public static class PowerUps {
    public static final double POWERUP_WIDTH = 38.0;
    public static final double POWERUP_HEIGHT = 19.0;
    public static final double POWERUP_FALL_SPEED = 2.0;  // px/frame
    
    // Duration constants (milliseconds)
    public static final long EXPAND_DURATION = 10_000L;   // 10 seconds
    public static final long CATCH_DURATION = 8_000L;     // 8 seconds
    public static final long LASER_DURATION = 10_000L;    // 10 seconds
    public static final long SLOW_DURATION = 8_000L;      // 8 seconds
    
    // Effect multipliers
    public static final double EXPAND_MULTIPLIER = 1.5;   // 150% width
    public static final double SLOW_MULTIPLIER = 0.7;     // 70% speed
    
    // UI
    public static final long WARNING_THRESHOLD = 2_000L;  // 2 sec before expiry
}
```

**Duration Table**:
| Power-Up | Duration | Note |
|----------|----------|------|
| EXPAND | 10 seconds | Paddle expansion |
| LASER | 10 seconds | Shoot lasers |
| CATCH | 8 seconds | Sticky paddle |
| SLOW | 8 seconds | Slow ball |
| DUPLICATE | Instant | Permanent until balls lost |
| LIFE | Instant | Permanent |
| WARP | Instant | Immediate level skip |

**Warning System**:
```java
// Show warning 2 seconds before expiry
long remaining = expiryTime - System.currentTimeMillis();
if (remaining < Constants.PowerUps.WARNING_THRESHOLD && remaining > 0) {
    showExpiryWarning();
}
```

---

### 9. Laser - Cấu hình laser

```java
public static class Laser {
    public static final double LASER_WIDTH = 4.0;
    public static final double LASER_HEIGHT = 16.0;
    public static final double LASER_SPEED = 8.0;      // Fast projectile
    public static final int LASER_SHOTS = 5;           // Ammo limit
    public static final long LASER_COOLDOWN = 300L;    // 300ms between shots
}
```

**Laser Mechanics**:
```java
// Laser properties
int shotsRemaining = Constants.Laser.LASER_SHOTS;
long lastShotTime = 0;

// Fire laser
public void fireLaser() {
    long now = System.currentTimeMillis();
    
    // Check cooldown
    if (now - lastShotTime < Constants.Laser.LASER_COOLDOWN) {
        return; // Too soon
    }
    
    // Check ammo
    if (shotsRemaining <= 0) {
        return; // Out of ammo
    }
    
    // Fire!
    Laser laser = new Laser(
        paddle.getX(),
        paddle.getY(),
        new Velocity(0, -Constants.Laser.LASER_SPEED) // Upward
    );
    lasers.add(laser);
    
    shotsRemaining--;
    lastShotTime = now;
}
```

---

### 10. Scoring - Hệ thống điểm

```java
public static class Scoring {
    public static final int SCORE_BRICK_BASE = 50;
    public static final int SCORE_BRICK_INCREMENT = 10;
    public static final int SCORE_LEVEL_COMPLETE_BONUS = 1000;
    public static final int SCORE_LIFE_BONUS = 500;
    public static final int SCORE_LOSE_LIFE_PENALTY = -500;
}
```

**Score Calculation**:
```java
// Base score + combo multiplier
int bricksDestroyed = 0;

public int onBrickDestroyed() {
    bricksDestroyed++;
    int baseScore = Constants.Scoring.SCORE_BRICK_BASE;
    int comboBonus = bricksDestroyed * Constants.Scoring.SCORE_BRICK_INCREMENT;
    return baseScore + comboBonus;
}

// Example progression:
// Brick 1: 50 + (1 * 10) = 60
// Brick 2: 50 + (2 * 10) = 70
// Brick 3: 50 + (3 * 10) = 80
// Brick 10: 50 + (10 * 10) = 150
```

**Level Complete Bonus**:
```java
// Level cleared
int bonus = Constants.Scoring.SCORE_LEVEL_COMPLETE_BONUS; // 1000
bonus += lives * Constants.Scoring.SCORE_LIFE_BONUS; // 500 per life

// Example: 3 lives remaining
// Bonus = 1000 + (3 * 500) = 2500
```

---

### 11. Animation - Timing animation

```java
public static class Animation {
    public static final long ANIMATION_FRAME_DURATION = 100L;     // 100ms
    public static final long CRACK_ANIMATION_DURATION = 20L;      // Fast
    public static final long PADDLE_ANIMATION_DURATION = 80L;
    public static final long POWERUP_ANIMATION_DURATION = 100L;
}
```

**Frame Rates**:
```
Default:  100ms = 10 FPS (slow animation)
Crack:    20ms = 50 FPS (fast break effect)
Paddle:   80ms = 12.5 FPS
Power-up: 100ms = 10 FPS
```

**Usage**:
```java
// Create power-up animation
List<Image> frames = loadPowerUpFrames();
Animation anim = new Animation(
    frames,
    Constants.Animation.POWERUP_ANIMATION_DURATION,
    true // loop
);
```

---

### 12. Audio - Cài đặt âm thanh

```java
public static class Audio {
    public static final double DEFAULT_MUSIC_VOLUME = 0.7;  // 70%
    public static final double DEFAULT_SFX_VOLUME = 1.0;    // 100%
    public static final int MAX_SIMULTANEOUS_SOUNDS = 8;
}
```

**Volume Levels**:
- Music: 70% (background, less intrusive)
- SFX: 100% (important feedback)
- Range: 0.0 (mute) to 1.0 (max)

**Sound Limit**:
```java
// Prevent audio overload
List<MediaPlayer> activeSounds = new ArrayList<>();

public void playSFX(String sound) {
    // Clean up finished sounds
    activeSounds.removeIf(p -> p.getStatus() == MediaPlayer.Status.STOPPED);
    
    // Check limit
    if (activeSounds.size() >= Constants.Audio.MAX_SIMULTANEOUS_SOUNDS) {
        // Stop oldest sound
        activeSounds.get(0).stop();
        activeSounds.remove(0);
    }
    
    // Play new sound
    MediaPlayer player = new MediaPlayer(media);
    player.setVolume(Constants.Audio.DEFAULT_SFX_VOLUME);
    player.play();
    activeSounds.add(player);
}
```

---

### 13. Paths - Đường dẫn resources

```java
public static class Paths {
    public static final String RESOURCES_PATH = "/Resources/";
    public static final String GRAPHICS_PATH = RESOURCES_PATH + "Graphics/";
    public static final String AUDIO_PATH = RESOURCES_PATH + "Audio/";
    public static final String FONTS_PATH = RESOURCES_PATH + "Fonts/";
    public static final String HIGHSCORE_FILE = "highscore.dat";
}
```

**Resource Structure**:
```
src/
  Resources/
    Graphics/
      brick.png
      paddle.png
      ball.png
      ...
    Audio/
      hit.wav
      break.wav
      music.mp3
      ...
    Fonts/
      game_font.ttf
      ...
```

**Loading Resources**:
```java
// Load image
String path = Constants.Paths.GRAPHICS_PATH + "brick.png";
Image image = new Image(getClass().getResourceAsStream(path));

// Load font
String fontPath = Constants.Paths.FONTS_PATH + "game_font.ttf";
Font font = Font.loadFont(getClass().getResourceAsStream(fontPath), 16);

// Load audio
String audioPath = Constants.Paths.AUDIO_PATH + "hit.wav";
Media media = new Media(getClass().getResource(audioPath).toExternalForm());
```

---

### 14. Borders - Kích thước viền

```java
public static class Borders {
    public static final int BORDER_TOP_WIDTH = 556;
    public static final int BORDER_TOP_HEIGHT = 22;
    public static final int BORDER_SIDE_WIDTH = 22;
    public static final int BORDER_SIDE_HEIGHT = 650;
}
```

**Border Layout**:
```
Top Border:    556px × 22px (horizontal)
Side Borders:  22px × 650px (vertical)
```

**Rendering**:
```java
// Top border
g.drawImage(
    topBorderSprite,
    Constants.PlayArea.PLAY_AREA_X,
    Constants.Window.WINDOW_TOP_OFFSET,
    Constants.Borders.BORDER_TOP_WIDTH,
    Constants.Borders.BORDER_TOP_HEIGHT
);

// Left border
g.drawImage(
    leftBorderSprite,
    0,
    Constants.PlayArea.PLAY_AREA_Y,
    Constants.Borders.BORDER_SIDE_WIDTH,
    Constants.Borders.BORDER_SIDE_HEIGHT
);

// Right border
g.drawImage(
    rightBorderSprite,
    Constants.Window.WINDOW_WIDTH - Constants.Borders.BORDER_SIDE_WIDTH,
    Constants.PlayArea.PLAY_AREA_Y,
    Constants.Borders.BORDER_SIDE_WIDTH,
    Constants.Borders.BORDER_SIDE_HEIGHT
);
```

---

### 15. UISprites - Kích thước UI sprites

```java
public static class UISprites {
    public static final double LOGO_WIDTH = 400.0;
    public static final double LOGO_HEIGHT = 145.0;
    public static final double LASER_BULLET_WIDTH = 6.0;
    public static final double LASER_BULLET_HEIGHT = 15.0;
}
```

**Logo** (`400×145`):
- Title screen logo
- Large, prominent display

**Laser Bullet** (`6×15`):
- Visual projectile sprite
- Different from gameplay Laser hitbox (4×16)

---

## Best Practices

### 1. Always Use Constants

```java
// ❌ Sai - magic numbers
if (ball.getY() > 800) {
    loseLife();
}

paddle.setWidth(79.0);

// ✅ Đúng - use constants
if (ball.getY() > Constants.Window.WINDOW_HEIGHT) {
    loseLife();
}

paddle.setWidth(Constants.Paddle.PADDLE_WIDTH);
```

---

### 2. Calculate Derived Values

```java
// ✅ Đúng - derived from other constants
public static final int PLAY_AREA_WIDTH = 
    Window.WINDOW_WIDTH - (Borders.BORDER_SIDE_WIDTH * 2);

// ❌ Sai - hardcoded (can become inconsistent)
public static final int PLAY_AREA_WIDTH = 556;
```

---

### 3. Use Descriptive Names

```java
// ✅ Đúng - clear, descriptive
public static final double BALL_INITIAL_SPEED = 3.0;
public static final long EXPAND_DURATION = 10_000L;

// ❌ Sai - unclear, abbreviations
public static final double BIS = 3.0;
public static final long ED = 10000;
```

---

### 4. Type Appropriately

```java
// ✅ Đúng - appropriate types
public static final int WINDOW_WIDTH = 600;      // Integer for pixels
public static final double BALL_SPEED = 3.0;     // Double for physics
public static final long DURATION = 10_000L;     // Long for milliseconds

// ❌ Sai - wrong types
public static final double WINDOW_WIDTH = 600.0; // Unnecessary precision
public static final int BALL_SPEED = 3;          // Loses precision
```

---

### 5. Group Related Constants

```java
// ✅ Đúng - nested classes for organization
public static class Ball {
    public static final double BALL_SIZE = 10.0;
    public static final double BALL_RADIUS = 5.0;
    public static final double BALL_SPEED = 3.0;
}

// ❌ Sai - flat structure (hard to navigate)
public static final double BALL_SIZE = 10.0;
public static final double PADDLE_WIDTH = 79.0;
public static final double BRICK_HEIGHT = 21.0;
public static final double BALL_RADIUS = 5.0; // Lost among other constants
```

---

## Balancing và Tuning

### Game Feel Adjustments

```java
// Too easy? Increase difficulty:
BALL_INITIAL_SPEED = 4.0;        // Faster ball
POWERUP_SPAWN_CHANCE = 0.2;      // Fewer power-ups
EXPAND_DURATION = 7_000L;        // Shorter effects

// Too hard? Decrease difficulty:
BALL_INITIAL_SPEED = 2.5;        // Slower ball
POWERUP_SPAWN_CHANCE = 0.4;      // More power-ups
INITIAL_LIVES = 5;               // More lives
PADDLE_WIDTH = 100.0;            // Wider paddle
```

### Performance Tuning

```java
// Lower FPS for slower devices:
FPS = 30;  // Still playable, less demanding

// Fewer simultaneous sounds:
MAX_SIMULTANEOUS_SOUNDS = 4;  // Reduce audio load
```

---

## Constants vs Configuration Files

### When to Use Constants

```java
// ✅ Use constants for:
// - Core game mechanics
// - Compile-time values
// - Values that rarely change
// - Values used in calculations

public static final double BALL_RADIUS = BALL_SIZE / 2.0;
public static final int PLAY_AREA_WIDTH = 
    WINDOW_WIDTH - (BORDER_SIDE_WIDTH * 2);
```

### When to Use Config Files

```java
// ✅ Use config files for:
// - User preferences (volume, controls)
// - Runtime-modifiable values
// - Per-level configurations
// - Save data (high scores)

// Example: audio_settings.dat
volume=0.7
muted=false
```

---

## Migration to Config Files (Optional)

```java
// If constants need to become configurable:

// 1. Keep constants as defaults
public static class GameRules {
    private static int initialLives = 3; // Default
    
    public static int getInitialLives() {
        return initialLives;
    }
    
    public static void setInitialLives(int lives) {
        initialLives = Math.max(1, Math.min(lives, 10));
    }
}

// 2. Load from config file
public void loadConfig() {
    Properties props = new Properties();
    props.load(new FileInputStream("game.properties"));
    
    int lives = Integer.parseInt(props.getProperty("initial_lives", "3"));
    Constants.GameRules.setInitialLives(lives);
}
```

---

## Testing Constants

```java
// Unit tests for derived constants
@Test
public void testPlayAreaCalculations() {
    int expectedWidth = 600 - (22 * 2); // 556
    assertEquals(expectedWidth, Constants.PlayArea.PLAY_AREA_WIDTH);
    
    int expectedHeight = 800 - 150 - 22; // 628
    assertEquals(expectedHeight, Constants.PlayArea.PLAY_AREA_HEIGHT);
}

@Test
public void testBallSpeedBounds() {
    assertTrue(Constants.Ball.BALL_MIN_SPEED < Constants.Ball.BALL_INITIAL_SPEED);
    assertTrue(Constants.Ball.BALL_INITIAL_SPEED < Constants.Ball.BALL_MAX_SPEED);
}
```

---

## Documentation Benefits

### Self-Documenting Code

```java
// Without constants:
if (speed > 6.0) speed = 6.0;  // What is 6.0?

// With constants:
if (speed > Constants.Ball.BALL_MAX_SPEED) {
    speed = Constants.Ball.BALL_MAX_SPEED;  // Clear intent!
}
```

### IDE Support

```java
// IntelliJ/Eclipse auto-completion:
Constants.Ball.BALL_  // Shows all ball-related constants
Constants.PowerUps.   // Shows all power-up constants
```

---

## Kết luận

`Constants` class là **foundation** của game configuration trong Arkanoid:

- **Single Source of Truth**: Mọi magic number đều defined ở đây
- **Easy Tuning**: Modify một nơi, affect toàn bộ game
- **Organization**: Nested classes provide clear structure
- **Type Safety**: Compile-time checking prevents errors
- **Documentation**: Names serve as inline documentation
- **Maintainability**: Easy to find và modify values

Constants class exemplifies **good software engineering practice**. Bằng việc centralize configuration, game trở nên easier to maintain, tune, và understand. Developers có thể quickly adjust game balance bằng cách modify constants instead of hunting through codebase for hardcoded values. Nested static classes provide clean namespace organization, preventing naming conflicts và making related constants easy to discover.

**Design Philosophy**: "Don't Repeat Yourself" (DRY) và "Single Responsibility Principle" (SRP). Constants class có one job: provide configuration values. It does this job well. When you need to adjust ball speed, paddle size, hay power-up duration, you know exactly where to look. This predictability và organization is invaluable trong large codebases.

**Best Practice**: Always prefer named constants over magic numbers. Code như `if (x > 600)` raises questions: Why 600? What does this represent? Code như `if (x > Constants.Window.WINDOW_WIDTH)` is self-explanatory. The constant's name documents its purpose. This makes code more readable, maintainable, và less error-prone.

