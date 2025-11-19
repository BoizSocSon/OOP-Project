# WarpPowerUp Class

## Tổng quan
`WarpPowerUp` là power-up "Dịch chuyển tức thì" (Warp/Skip Level) - một trong những power-up HIẾM NHẤT (1% spawn rate) và GÂY TRANH CÃI NHẤT trong Arkanoid. Khi nhặt được, người chơi NGAY LẬP TỨC chuyển sang level tiếp theo, bỏ qua toàn bộ bricks còn lại. Nếu đang ở level cuối → trigger Win Screen. Đây là instant effect power-up, không có duration. Risk/reward cực cao: skip level khó nhưng mất points từ bricks còn lại.

## Vị trí
- **Package**: `Objects.PowerUps`
- **File**: `src/Objects/PowerUps/WarpPowerUp.java`
- **Kế thừa**: `PowerUp` (abstract)
- **Implements**: `GameObject` (gián tiếp qua PowerUp)

## Mục đích
WarpPowerUp:
- Skip level hiện tại ngay lập tức
- Trigger win screen nếu last level
- Instant effect (không có duration)
- Rarest power-up (1% spawn)
- High risk/reward trade-off
- Strategic choice: speed vs score

## Kế thừa

```
GameObject (Interface)
    ↑
    │ implements
    │
PowerUp (Abstract Class)
    ↑
    │ extends
    │
WarpPowerUp (Concrete Class)
    │
    └── PowerUpType.WARP (Instant effect, 1% spawn rate)
```

---

## Constructor

### `WarpPowerUp(double x, double y)`

**Mô tả**: Khởi tạo Warp power-up với vị trí ban đầu.

**Tham số**:
- `x` - Tọa độ X (thường là vị trí brick vừa phá)
- `y` - Tọa độ Y

**Hành vi**:
```java
super(x, y, PowerUpType.WARP);
```
- Gọi constructor của PowerUp
- Type = `PowerUpType.WARP`
- Animation = "powerup_warp_0.png", "powerup_warp_1.png", ...
- Velocity = (0, POWERUP_FALL_SPEED)
- active = true, collected = false

**Spawn Rate**: WARP là power-up hiếm nhất sau LIFE:
```java
// PowerUpType.WARP.getSpawnChance() = 0.01 (1%)
// Chỉ xuất hiện ~1/100 lần khi brick destroyed
```

**Ví dụ**:
```java
// Spawn khi brick destroyed (rất hiếm)
if (shouldSpawnPowerUp()) {
    PowerUpType type = PowerUpType.randomWeighted();
    if (type == PowerUpType.WARP) { // 1% chance
        double x = brick.getX() + brick.getWidth() / 2;
        double y = brick.getY();
        WarpPowerUp warpPowerUp = new WarpPowerUp(x, y);
        powerUps.add(warpPowerUp);
        System.out.println("Rare WARP power-up spawned!");
    }
}
```

---

## Phương thức

### 1. `void applyEffect(GameManager gameManager)` (Override)

**Mô tả**: Dịch chuyển ngay lập tức sang level tiếp theo hoặc trigger win screen.

**Tham số**: `gameManager` - GameManager để trigger level transition.

**Hành vi**:
1. Null safety check:
   ```java
   if (gameManager == null) {
       System.err.println("WarpPowerUp: GameManager is null");
       return;
   }
   ```

2. Warp to next level:
   ```java
   gameManager.warpToNextLevel();
   ```

3. Log message:
   ```java
   System.out.println("WarpPowerUp: Warping to next level!");
   ```

**Effect trong GameManager**:
```java
// GameManager.warpToNextLevel()
public void warpToNextLevel() {
    // Get current level
    int currentLevel = roundsManager.getCurrentRoundNumber();
    int totalLevels = roundsManager.getTotalRounds();
    
    // Check if last level
    if (currentLevel >= totalLevels) {
        // LAST LEVEL → Win game
        System.out.println("WARP: Last level reached - YOU WIN!");
        
        // Play victory fanfare
        audioManager.playVictoryMusic();
        
        // Show win screen
        stateManager.setState(GameState.WIN);
        
        // Save high score
        int finalScore = scoreManager.getScore();
        highScoreManager.submitScore(finalScore);
        
    } else {
        // NOT LAST LEVEL → Next level
        System.out.println("WARP: Jumping to level " + (currentLevel + 1));
        
        // Clean up current level
        clearAllPowerUps();
        clearAllBalls();
        resetPaddle();
        
        // Load next round
        roundsManager.loadNextRound();
        
        // Spawn new ball
        spawnBall();
        
        // Visual transition
        transitionManager.playWarpTransition();
        
        // Sound effect
        audioManager.playWarpSound(); // "warp.wav"
        
        // Show level intro
        showLevelIntro(currentLevel + 1);
    }
}
```

**Gọi**: Khi power-up collision với Paddle.

---

### 2. `void removeEffect(GameManager gameManager)` (Override)

**Mô tả**: EMPTY - Warp là instant effect, không có state để remove.

**Tham số**: `gameManager` - Không sử dụng.

**Hành vi**:
```java
// No-op - instant effect không cần cleanup
```

**Giải thích**:
- Warp effect xảy ra NGAY LẬP TỨC
- Không có "warp state" để track
- Không có duration hay timer
- Level transition là one-time event

**Gọi**: Không bao giờ (instant effect).

---

## Warp Mechanics

### Level Transition Logic

```java
public void warpToNextLevel() {
    // 1. Save current progress
    saveProgress();
    
    // 2. Calculate bonus (nếu có)
    int warpBonus = calculateWarpBonus(); // Small bonus for fast clear
    scoreManager.addScore(warpBonus);
    
    // 3. Clean up current level
    cleanup();
    
    // 4. Check win condition
    if (isLastLevel()) {
        triggerWinScreen();
    } else {
        loadNextLevel();
    }
}

private void cleanup() {
    // Remove all entities
    balls.clear();
    powerUps.clear();
    lasers.clear();
    
    // Reset paddle
    paddle.reset();
    paddle.removeAllEffects();
    
    // Reset power-up state
    powerUpManager.resetAllEffects();
}

private void loadNextLevel() {
    // Increment level
    roundsManager.nextRound();
    
    // Load new brick layout
    RoundBase newRound = roundsManager.getCurrentRound();
    bricks.addAll(newRound.getBricks());
    
    // Spawn ball
    Ball newBall = new Ball(
        paddle.getX() + paddle.getWidth() / 2,
        paddle.getY() - Ball.RADIUS * 2
    );
    balls.add(newBall);
    
    // Visual transition
    fadeOutOldLevel();
    fadeInNewLevel();
}
```

---

### Win Screen Trigger

```java
public void triggerWinScreen() {
    // Calculate final stats
    int finalScore = scoreManager.getScore();
    int totalTime = gameTime;
    int bricksDestroyed = scoreManager.getBricksDestroyed();
    
    // Check high score
    boolean isHighScore = highScoreManager.isHighScore(finalScore);
    
    // Play victory music
    audioManager.stopGameMusic();
    audioManager.playVictoryMusic(); // Epic fanfare
    
    // Fireworks effect
    for (int i = 0; i < 10; i++) {
        spawnFirework(
            random.nextInt(screenWidth),
            random.nextInt(screenHeight)
        );
    }
    
    // Show win screen
    stateManager.setState(GameState.WIN);
    
    // Display stats
    winScreen.setFinalScore(finalScore);
    winScreen.setTotalTime(totalTime);
    winScreen.setBricksDestroyed(bricksDestroyed);
    winScreen.setHighScore(isHighScore);
}
```

---

## Luồng hoạt động

### Lifecycle của WarpPowerUp

```
1. SPAWN (1% chance)
   ↓
   Brick destroyed
   → Random weighted
   → 1% chance = WARP
   → new WarpPowerUp(x, y)
   → Rơi xuống với animation (portal/warp icon)
   → Rare spawn message
   → Visual: Golden glow, special particles

2. FALL
   ↓
   Power-up rơi xuống với POWERUP_FALL_SPEED
   → Animated sprite (portal swirling)
   → Particle trail (sparkles)
   → Glowing aura (golden/purple)
   
   Player reaction:
     - Notice rare power-up
     - Decision: Collect or avoid?
     - Consider: Score vs Time
     - Risk: Skip potential high-value bricks

3. COLLECTION
   ↓
   Power-up hits paddle
   → collect() called
   → applyEffect(gameManager)
   → gameManager.warpToNextLevel()
   
   Immediate effects:
     - Screen flash (white)
     - Warp sound effect
     - Portal animation
     - All entities freeze
     - Visual distortion (ripple)

4. LEVEL TRANSITION
   ↓
   Check if last level:
   
   IF Last Level:
     → triggerWinScreen()
     → Victory music
     → Fireworks
     → Show final stats
     → High score check
     → Credits/Thank you message
   
   ELSE (Not last level):
     → Clean up current level:
         • Remove all balls
         • Remove all power-ups
         • Remove all lasers
         • Reset paddle
         • Remove paddle effects
     
     → Load next round:
         • Increment round number
         • Load new brick layout
         • Spawn new ball
         • Reset score multiplier (optional)
     
     → Visual transition:
         • Fade out old level
         • Portal/warp animation
         • Fade in new level
         • Show level intro text
     
     → Audio:
         • Warp sound effect
         • Level intro music

5. POST-WARP STATE
   ↓
   New level loaded:
     - Fresh brick layout
     - New ball spawned
     - Paddle at starting position
     - No active power-up effects
     - Score retained
     - Lives retained
   
   Player continues:
     - Play new level normally
     - Missed points from skipped bricks
     - Gained time by skipping
```

---

## Visual Effects

### Warp Animation

```java
public void renderWarpTransition(Graphics2D g) {
    long elapsed = System.currentTimeMillis() - warpStartTime;
    
    if (elapsed < WARP_DURATION) { // 2000ms
        // 1. Portal vortex effect
        double progress = elapsed / (double) WARP_DURATION;
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        
        // Spinning circles
        for (int i = 0; i < 10; i++) {
            double radius = 50 + i * 30 * (1 - progress);
            double alpha = (1 - progress) * 0.5;
            
            g.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, (float) alpha));
            
            g.setColor(Color.CYAN);
            g.drawOval(
                (int) (centerX - radius),
                (int) (centerY - radius),
                (int) (radius * 2),
                (int) (radius * 2)
            );
        }
        g.setComposite(AlphaComposite.SrcOver);
        
        // 2. Screen distortion (ripple)
        applyRippleEffect(g, centerX, centerY, progress);
        
        // 3. Particles
        for (int i = 0; i < 50; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = random.nextDouble() * 200;
            double x = centerX + Math.cos(angle) * dist * progress;
            double y = centerY + Math.sin(angle) * dist * progress;
            
            g.setColor(Color.WHITE);
            g.fillOval((int) x, (int) y, 3, 3);
        }
    }
}
```

---

### Rare Power-Up Glow

```java
// WarpPowerUp có special visual để highlight rarity
public void render(Graphics2D g) {
    // Base power-up rendering
    super.render(g);
    
    // Golden glow for rare power-up
    if (this instanceof WarpPowerUp) {
        double time = System.currentTimeMillis() / 1000.0;
        float alpha = (float) (0.3 + 0.2 * Math.sin(time * 3));
        
        g.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, alpha));
        
        // Golden aura
        g.setColor(new Color(255, 215, 0)); // Gold
        g.fillOval(
            (int) (x - 5),
            (int) (y - 5),
            (int) (width + 10),
            (int) (height + 10)
        );
        
        g.setComposite(AlphaComposite.SrcOver);
    }
}
```

---

## Chiến thuật sử dụng

### 1. Score vs Time Trade-off

```java
// Decision matrix khi thấy Warp power-up
if (hasWarpPowerUp) {
    int currentScore = scoreManager.getScore();
    int bricksRemaining = getBrickCount();
    int potentialScore = bricksRemaining * AVG_BRICK_SCORE;
    
    // High score remaining → AVOID warp
    if (potentialScore > 1000) {
        System.out.println("Skip WARP - too many points left");
        avoidPowerUp();
    }
    
    // Few bricks left → COLLECT warp
    else if (bricksRemaining < 5) {
        System.out.println("Collect WARP - almost done anyway");
        collectPowerUp();
    }
    
    // Difficult level → COLLECT warp
    else if (levelDifficulty == HARD) {
        System.out.println("Collect WARP - skip hard level");
        collectPowerUp();
    }
}
```

---

### 2. Speedrun Strategy

```java
// Speedrunners LOVE warp power-up
if (isSpeedrun) {
    // Goal: Complete game as fast as possible
    // Score is irrelevant
    
    if (hasWarpPowerUp) {
        // ALWAYS collect
        collectWarpPowerUp();
        // Shave off seconds/minutes
    }
}
```

---

### 3. Last Level Awareness

```java
// Trên level cuối cùng
if (isLastLevel() && hasWarpPowerUp) {
    // Collect = instant win
    // Skip remaining bricks
    
    // Decision:
    // - Collect if struggling
    // - Avoid if going for high score
    
    if (lives == 1 && ballCount == 1) {
        // Desperate situation → collect
        collectWarpPowerUp(); // Instant win
    }
}
```

---

### 4. Difficult Level Escape

```java
// Level quá khó (nhiều Gold bricks, ít power-ups)
if (currentLevel == DIFFICULT_LEVEL) {
    // Example: Level 4 với nhiều indestructible bricks
    
    if (hasWarpPowerUp) {
        // Skip painful level
        collectWarpPowerUp();
        // Trade score for sanity
    }
}
```

---

## So sánh với các power-up khác

| Power-Up | Effect | Type | Spawn Rate | Risk/Reward |
|----------|--------|------|------------|-------------|
| **WARP** | Skip level | Instant | 1% (rarest) | ⚠️⚠️ Extreme |
| LIFE | +1 life | Instant | 5% | ✅ Safe |
| DUPLICATE | Clone balls | Instant | 10% | ⚠️ Moderate |
| CATCH | Sticky paddle | Timed | 15% | ✅ Safe |
| EXPAND | Wider paddle | Timed | 15% | ✅ Safe |
| LASER | Shoot bricks | Timed | 15% | ✅ Safe |
| SLOW | Slow balls | Timed | 15% | ✅ Safe |

**WARP Characteristics**:
- **Rarity**: Tied for rarest (1% with LIFE being 5%)
- **Risk**: Highest - lose all remaining points
- **Reward**: Skip difficult level, save time
- **Controversy**: Some players hate it, others love it
- **Strategic**: Requires decision-making
- **Speedrun**: Essential for fast times

---

## Player Psychology

### Collector's Dilemma

```java
// Người chơi thấy rare power-up
// Instinct: "I must collect it!"
// But should they?

// Psychological factors:
// 1. Rarity bias - "It's rare, must be good!"
// 2. FOMO - "What if I never see it again?"
// 3. Curiosity - "What does it do?"
// 4. Risk aversion - "What if it's bad?"

// Good game design: Make player THINK
// Not all power-ups should be auto-collect
```

---

## Best Practices

### 1. Visual Warning
```java
// ✅ Đúng - warn player về warp effect
public void renderWarpPowerUp(Graphics2D g) {
    super.render(g);
    
    // Show warning icon
    if (shouldShowWarning()) {
        g.drawImage(warningIcon, x, y - 20, null);
        
        // Tooltip
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(Color.YELLOW);
        g.drawString("WARP: Skip Level!", x - 20, y - 25);
    }
}

// ❌ Sai - không warning (player confused)
// Just render power-up with no indication of effect
```

---

### 2. Confirmation Prompt (Optional)
```java
// ✅ Có thể - confirm trước khi warp (cho beginners)
public void onWarpPowerUpCollected() {
    if (gameSettings.confirmWarp) {
        // Pause game
        pauseGame();
        
        // Show dialog
        showConfirmDialog(
            "Skip to next level?\n" +
            "You will lose points from remaining bricks.",
            () -> { warpToNextLevel(); },
            () -> { resumeGame(); }
        );
    } else {
        // Instant warp
        warpToNextLevel();
    }
}
```

---

### 3. Stats Tracking
```java
// ✅ Đúng - track warp usage
public void onWarpCollected() {
    gameStats.incrementWarpCount();
    gameStats.recordSkippedBricks(getBrickCount());
    gameStats.recordSkippedScore(calculatePotentialScore());
    
    warpToNextLevel();
}

// Useful cho achievements:
// "Speed Demon" - Complete game with 3+ warps
// "Completionist" - Complete game without using warp
```

---

### 4. Disable on Last Level (Optional)
```java
// ✅ Có thể - disable warp spawn trên level cuối
public PowerUpType randomPowerUp() {
    if (isLastLevel()) {
        // Don't spawn WARP on final level
        // Prevents trivializing final challenge
        return PowerUpType.randomWeightedExcluding(PowerUpType.WARP);
    }
    return PowerUpType.randomWeighted();
}
```

---

## Edge Cases

### 1. Warp During Power-Up Effects

```java
// Warp khi có active power-up effects
public void warpToNextLevel() {
    // Clean up all effects first
    paddle.removeCatchEffect();
    paddle.removeExpandEffect();
    paddle.removeLaserEffect();
    powerUpManager.removeSlowEffect();
    
    // Then proceed with warp
    loadNextLevel();
}
```

---

### 2. Warp on Last Level

```java
// Warp collected on final level
if (currentLevel == FINAL_LEVEL) {
    // Instant win
    triggerWinScreen();
    
    // Achievement: "Easy Way Out"
    achievementManager.unlock("WARP_WIN");
}
```

---

### 3. Multiple Warps

```java
// Collect 2+ warps in succession (unlikely but possible)
public void onWarpCollected() {
    if (isTransitioning) {
        // Already warping, ignore
        return;
    }
    
    isTransitioning = true;
    warpToNextLevel();
}
```

---

## Balancing Considerations

### 1. Spawn Rate

```java
// 1% spawn rate = very rare
// Average game: 100-200 bricks destroyed
// Expected warps per game: 1-2

// Too common (10%): Everyone skips levels → boring
// Too rare (0.1%): Never seen → wasted feature
// Just right (1%): Rare treat, strategic choice
```

---

### 2. Score Impact

```java
// Lost score from skipped bricks
int bricksRemaining = 20;
int avgBrickScore = 50;
int lostScore = bricksRemaining * avgBrickScore; // 1000 points

// Significant loss for high score runs
// Acceptable trade-off for speedruns
```

---

## Sound Design

```java
// Warp sound effects
audioManager.playWarpSound(); 
// "warp.wav" - sci-fi transporter sound
// Think: Star Trek beam-up, portal opening

// Victory music (if last level)
audioManager.playVictoryMusic();
// "victory.mp3" - triumphant fanfare
```

---

## Achievement Integration

```java
// Warp-related achievements
public void checkWarpAchievements() {
    int warpCount = gameStats.getWarpCount();
    
    if (warpCount == 0 && gameComplete) {
        // "Completionist" - beat game without warp
        unlockAchievement("NO_WARP");
    }
    
    if (warpCount >= 3 && gameComplete) {
        // "Speed Demon" - used 3+ warps
        unlockAchievement("SPEED_DEMON");
    }
    
    if (warpCount == 1 && currentLevel == FINAL_LEVEL) {
        // "One Shot" - used single warp on final level
        unlockAchievement("ONE_SHOT_WARP");
    }
}
```

---

## Kết luận

`WarpPowerUp` là most controversial power-up trong Arkanoid:

- **Instant Win**: Có thể trigger instant victory
- **Extreme Rarity**: 1% spawn rate (very rare)
- **High Risk**: Lose significant score
- **High Reward**: Save time, skip difficulty
- **Strategic**: Requires player decision
- **Divisive**: Some love it, some hate it

WarpPowerUp exemplifies interesting game design choice - not all power-ups should be pure upgrades. By creating a trade-off (time vs score), it adds strategic depth. Players must evaluate situation và make meaningful choice. Collect để skip khó? Hay avoid để max score? Không có "correct" answer - depends on player goals (speedrun vs high score vs casual fun).

**Design Philosophy**: Power-ups don't have to be always beneficial. Creating tension và choice makes gameplay more engaging than mindless collection. Warp exemplifies này - it's powerful (skip entire level!) but comes with clear cost (lost points). Good players evaluate risk/reward. Great game design gives players interesting decisions.

**Historical Note**: Warp/Skip mechanics có trong nhiều classic games. Trong Super Mario Bros có warp pipes (skip worlds). Trong Arkanoid, warp power-up serves similar purpose - cho phép skilled/desperate players shortcut through content. Controversy xung quanh feature này is testament to its effectiveness - if everyone agreed, it wouldn't be interesting!

