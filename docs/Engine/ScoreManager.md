# ScoreManager

## Tổng quan
`ScoreManager` là lớp quản lý toàn bộ hệ thống điểm số trong game Arkanoid. Lớp này chịu trách nhiệm:
- Tính toán và cập nhật điểm số của người chơi
- Quản lý hệ số nhân điểm (score multiplier)
- Cộng điểm khi phá hủy gạch với multiplier tăng dần
- Tính toán điểm thưởng hoàn thành vòng chơi
- Tính toán điểm thưởng dựa trên số mạng còn lại
- Áp dụng điểm phạt khi mất mạng và reset multiplier

ScoreManager tạo nên một scoring system dynamic và engaging, khuyến khích người chơi duy trì combo streak để đạt điểm cao.

## Package
```
Engine.ScoreManager
```

## Scoring System Overview
```
┌──────────────────────────────────┐
│       ScoreManager               │
│  - score: int                    │
│  - scoreMultiplier: int          │
└──────────────┬───────────────────┘
               │
               │ calculates
               ↓
┌──────────────────────────────────┐
│      Scoring Components          │
├──────────────────────────────────┤
│ • Brick Base Score               │
│ • Multiplier Bonus               │
│ • Round Complete Bonus           │
│ • Life Bonus                     │
│ • Lose Life Penalty              │
└──────────────────────────────────┘
```

---

## Thuộc tính

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Giá trị mặc định | Mô tả |
|-----------|-------------|-----------------|------------------|-------|
| `score` | `int` | `private` | `0` | Điểm số hiện tại của người chơi |
| `scoreMultiplier` | `int` | `private` | `1` | Hệ số nhân điểm (tăng khi phá gạch liên tiếp) |

### Chi tiết thuộc tính

#### score
Điểm số tích lũy của người chơi trong suốt game.

```java
private int score = 0;
```

**Đặc điểm:**
- Bắt đầu từ 0 mỗi game mới
- Tăng khi phá hủy gạch
- Tăng khi hoàn thành vòng chơi
- Tăng thêm bonus từ số mạng còn lại
- Giảm khi mất mạng (có floor là 0)
- Không có upper limit

**Các cách tăng điểm:**

| Hành động | Công thức | Ví dụ |
|-----------|-----------|-------|
| Phá gạch | `base + (multiplier × increment)` | 50 + (5 × 10) = 100 |
| Hoàn thành round | `+ LEVEL_COMPLETE_BONUS` | + 1000 |
| Life bonus | `lives × LIFE_BONUS` | 3 × 500 = 1500 |
| Mất mạng | `- LOSE_LIFE_PENALTY` | - 200 |

**Ví dụ progression:**
```
Start:           score = 0
Destroy brick 1: score = 50 + (1 × 10) = 60
Destroy brick 2: score = 60 + 50 + (2 × 10) = 130
Destroy brick 3: score = 130 + 50 + (3 × 10) = 210
Lose life:       score = 210 - 200 = 10, multiplier reset
Destroy brick 4: score = 10 + 50 + (1 × 10) = 70
```

#### scoreMultiplier
Hệ số nhân điểm, tăng mỗi khi phá hủy gạch thành công.

```java
private int scoreMultiplier = 1;
```

**Đặc điểm:**
- Bắt đầu từ 1
- Tăng thêm 1 sau mỗi lần phá gạch
- Reset về 1 khi mất mạng
- Không có upper limit (có thể tăng vô hạn)
- Khuyến khích người chơi duy trì combo

**Multiplier progression:**
```
Initial:         multiplier = 1
After brick 1:   multiplier = 2
After brick 2:   multiplier = 3
After brick 3:   multiplier = 4
...
After brick 50:  multiplier = 51
Lose life:       multiplier = 1 (RESET!)
```

**Impact on scoring:**
```
Brick base score: 50 points
Increment: 10 points

Multiplier = 1:  Score = 50 + (1 × 10) = 60
Multiplier = 2:  Score = 50 + (2 × 10) = 70
Multiplier = 5:  Score = 50 + (5 × 10) = 100
Multiplier = 10: Score = 50 + (10 × 10) = 150
Multiplier = 20: Score = 50 + (20 × 10) = 250
```

**Graph visualization:**
```
Score per brick with increasing multiplier:
  300 │                                    •
      │                               •
  250 │                          •
      │                     •
  200 │                •
      │           •
  150 │      •
      │  •
  100 │•
   50 └──────────────────────────────────
      1   5   10  15  20  25  30  35  40
                 Multiplier value
```

---

## Constructor

### ScoreManager()
```java
public ScoreManager()
```

Constructor mặc định, khởi tạo với score = 0 và multiplier = 1.

**Ví dụ:**
```java
public class GameManager {
    private ScoreManager scoreManager;
    
    public void initialize() {
        scoreManager = new ScoreManager();
        
        System.out.println("Score: " + scoreManager.getScore()); // 0
        System.out.println("Multiplier: " + scoreManager.getMultiplier()); // 1
    }
}
```

---

## Phương thức công khai

### 1. resetScore()
```java
public void resetScore()
```

Đặt lại điểm số về 0.

**Chức năng:**
- Set score = 0
- Không ảnh hưởng đến multiplier

**Ví dụ:**
```java
// Start new game
public void startNewGame() {
    scoreManager.resetScore();
    scoreManager.resetMultiplier();
    
    System.out.println("Game reset");
    System.out.println("Score: " + scoreManager.getScore()); // 0
}

// Reset only score (keep multiplier)
public void resetScoreOnly() {
    scoreManager.resetScore();
    // Multiplier stays the same (unusual use case)
}
```

**Khi nào sử dụng:**
- Bắt đầu game mới
- Restart current level
- Cheat/debug purposes

---

### 2. resetMultiplier()
```java
public void resetMultiplier()
```

Đặt lại hệ số nhân điểm về 1.

**Chức năng:**
- Set scoreMultiplier = 1
- Không ảnh hưởng đến score

**Ví dụ:**
```java
// Called automatically when losing life
public void onLifeLost() {
    lives--;
    scoreManager.applyLoseLifePenalty(); // Already calls resetMultiplier()
}

// Manual reset (unusual)
public void resetCombo() {
    scoreManager.resetMultiplier();
    System.out.println("Combo reset!");
}
```

**Khi nào được gọi:**
- Tự động khi gọi `applyLoseLifePenalty()`
- Bắt đầu game mới (với `resetScore()`)
- Có thể dùng cho special events/penalties

---

### 3. addDestroyBrickScore()
```java
public void addDestroyBrickScore(BrickType brickType)
```

Cộng điểm khi phá hủy một viên gạch.

**Tham số:**
- `brickType` - Loại gạch bị phá hủy

**Công thức tính điểm:**
```java
newScore = currentScore + baseScore + (multiplier × increment)
```

**Thuật toán:**

1. **Tính điểm bonus từ multiplier:**
   ```java
   multiplierBonus = scoreMultiplier * Constants.Scoring.SCORE_BRICK_INCREMENT;
   ```

2. **Cộng điểm:**
   ```java
   score = score + brickType.getBaseScore() + multiplierBonus;
   ```

3. **Tăng multiplier:**
   ```java
   scoreMultiplier++;
   ```

**Ví dụ:**
```java
// Destroy red brick (base score = 50)
scoreManager.addDestroyBrickScore(BrickType.RED);

// Calculation:
// score = 0 + 50 + (1 × 10) = 60
// multiplier = 1 + 1 = 2

// Destroy blue brick (base score = 50)
scoreManager.addDestroyBrickScore(BrickType.BLUE);

// Calculation:
// score = 60 + 50 + (2 × 10) = 130
// multiplier = 2 + 1 = 3

// Destroy silver brick (base score = 100)
scoreManager.addDestroyBrickScore(BrickType.SILVER);

// Calculation:
// score = 130 + 100 + (3 × 10) = 260
// multiplier = 3 + 1 = 4
```

**Base scores theo BrickType:**

| BrickType | Base Score |
|-----------|-----------|
| RED | 50 |
| BLUE | 50 |
| GREEN | 50 |
| YELLOW | 50 |
| ORANGE | 50 |
| SILVER | 100 |
| GOLD | 0 (indestructible) |

**Score progression example:**
```
Brick #  | Type   | Mult | Base | Bonus      | Score Added | Total Score
---------|--------|------|------|------------|-------------|------------
1        | RED    | 1    | 50   | 1×10=10    | 60          | 60
2        | BLUE   | 2    | 50   | 2×10=20    | 70          | 130
3        | GREEN  | 3    | 50   | 3×10=30    | 80          | 210
4        | SILVER | 4    | 100  | 4×10=40    | 140         | 350
5        | RED    | 5    | 50   | 5×10=50    | 100         | 450
```

**Integration với collision:**
```java
public void onBrickDestroyed(Brick brick) {
    // Add score based on brick type
    scoreManager.addDestroyBrickScore(brick.getBrickType());
    
    // Visual feedback
    showScorePopup("+" + lastScoreAdded, brick.getX(), brick.getY());
    
    // Audio feedback
    if (scoreManager.getMultiplier() > 10) {
        audioManager.playSFX(SoundEffect.HIGH_COMBO);
    }
}
```

---

### 4. addRoundCompleteScore()
```java
public void addRoundCompleteScore()
```

Cộng điểm thưởng khi hoàn thành một vòng chơi.

**Chức năng:**
- Cộng `Constants.Scoring.SCORE_LEVEL_COMPLETE_BONUS` vào score
- Không ảnh hưởng multiplier

**Ví dụ:**
```java
public void onRoundComplete() {
    // Add round complete bonus
    scoreManager.addRoundCompleteScore();
    
    // Typical bonus: 1000 points
    System.out.println("Level Complete! Bonus: 1000 points");
    System.out.println("Total Score: " + scoreManager.getScore());
    
    // Show completion screen
    showLevelCompleteScreen();
}
```

**Typical value:**
```java
Constants.Scoring.SCORE_LEVEL_COMPLETE_BONUS = 1000
```

**Score calculation:**
```
Before completing round: score = 5430
Add round complete bonus: score = 5430 + 1000 = 6430
```

---

### 5. addLifeBonusScore()
```java
public void addLifeBonusScore(int livesRemaining)
```

Cộng điểm thưởng dựa trên số mạng còn lại.

**Tham số:**
- `livesRemaining` - Số mạng còn lại của người chơi

**Công thức:**
```java
bonusScore = livesRemaining × Constants.Scoring.SCORE_LIFE_BONUS
```

**Ví dụ:**
```java
public void onRoundComplete() {
    int lives = lifeManager.getLives();
    
    // Add round complete bonus
    scoreManager.addRoundCompleteScore();
    
    // Add life bonus
    scoreManager.addLifeBonusScore(lives);
    
    System.out.println("Lives remaining: " + lives);
    System.out.println("Life bonus: " + (lives * 500) + " points");
}
```

**Life bonus calculation:**

| Lives Remaining | Life Bonus (×500) | Total Bonus |
|----------------|-------------------|-------------|
| 1 | 1 × 500 | 500 |
| 2 | 2 × 500 | 1000 |
| 3 | 3 × 500 | 1500 |
| 4 | 4 × 500 | 2000 |
| 5 | 5 × 500 | 2500 |

**Typical constant:**
```java
Constants.Scoring.SCORE_LIFE_BONUS = 500
```

**Complete round scoring example:**
```
Base score:          5430
Round complete:     +1000
Life bonus (3):     +1500
-------------------------
Final score:         7930
```

**Strategic implications:**
- Khuyến khích người chơi bảo toàn mạng
- Reward cho gameplay cẩn thận
- Tạo risk/reward tradeoff

---

### 6. applyLoseLifePenalty()
```java
public void applyLoseLifePenalty()
```

Áp dụng điểm phạt khi người chơi mất mạng.

**Chức năng:**
1. Trừ điểm penalty
2. Đảm bảo score không < 0
3. Reset multiplier về 1

**Công thức:**
```java
score = Math.max(0, score + SCORE_LOSE_LIFE_PENALTY)
// Note: PENALTY is negative, e.g., -200
```

**Thuật toán:**
```java
// Apply penalty (subtract points)
this.score = Math.max(0, this.score + Constants.Scoring.SCORE_LOSE_LIFE_PENALTY);

// Reset multiplier
resetMultiplier();
```

**Ví dụ:**
```java
public void onBallLost() {
    lives--;
    
    if (lives > 0) {
        // Apply penalty
        int oldScore = scoreManager.getScore();
        int oldMultiplier = scoreManager.getMultiplier();
        
        scoreManager.applyLoseLifePenalty();
        
        int newScore = scoreManager.getScore();
        int newMultiplier = scoreManager.getMultiplier();
        
        System.out.println("Life lost!");
        System.out.println("Score: " + oldScore + " → " + newScore);
        System.out.println("Multiplier: " + oldMultiplier + " → " + newMultiplier);
        
        // Reset paddle and ball
        resetGameObjects();
    } else {
        // Game over
        gameOver();
    }
}
```

**Penalty scenarios:**

| Before | Penalty | After | Notes |
|--------|---------|-------|-------|
| 5000 | -200 | 4800 | Normal case |
| 150 | -200 | 0 | Floor at 0 |
| 0 | -200 | 0 | Already at 0 |
| 10000 | -200 | 9800 | Large score |

**Typical constant:**
```java
Constants.Scoring.SCORE_LOSE_LIFE_PENALTY = -200
```

**Impact breakdown:**
```
Before losing life:
    score = 3450
    multiplier = 15 (high combo!)

After losing life:
    score = 3450 + (-200) = 3250
    multiplier = 1 (RESET!)

Next brick destroyed:
    Only gets multiplier=1 bonus instead of 15!
```

**Why reset multiplier?**
- Penalty cho việc mất combo streak
- Khuyến khích không mất mạng
- Makes losing life more impactful
- Balances gameplay difficulty

---

### 7. getScore()
```java
public int getScore()
```

Lấy điểm số hiện tại.

**Giá trị trả về:**
- `int` - Điểm số hiện tại

**Ví dụ:**
```java
// Display score in UI
public void renderUI(GraphicsContext gc) {
    gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
    gc.setFill(Color.WHITE);
    
    String scoreText = "Score: " + scoreManager.getScore();
    gc.fillText(scoreText, 10, 30);
}

// Check for high score
public void checkHighScore() {
    int currentScore = scoreManager.getScore();
    
    if (highScoreManager.isHighScore(currentScore)) {
        System.out.println("New High Score!");
        highScoreManager.addScore("Player", currentScore);
    }
}

// Save score
public void saveGame() {
    GameSaveData data = new GameSaveData();
    data.score = scoreManager.getScore();
    data.multiplier = scoreManager.getMultiplier();
    // ... save to file
}
```

---

### 8. getMultiplier()
```java
public int getMultiplier()
```

Lấy hệ số nhân điểm hiện tại.

**Giá trị trả về:**
- `int` - Hệ số nhân điểm

**Ví dụ:**
```java
// Display multiplier in UI
public void renderUI(GraphicsContext gc) {
    int multiplier = scoreManager.getMultiplier();
    
    // Show multiplier with color based on value
    Color color;
    if (multiplier >= 20) {
        color = Color.GOLD; // Epic combo!
    } else if (multiplier >= 10) {
        color = Color.ORANGE; // Great combo!
    } else if (multiplier >= 5) {
        color = Color.YELLOW; // Good combo
    } else {
        color = Color.WHITE; // Normal
    }
    
    gc.setFill(color);
    gc.fillText("x" + multiplier, 10, 60);
    
    // Combo streak indicator
    if (multiplier > 1) {
        gc.fillText("COMBO!", 10, 80);
    }
}

// Achievement tracking
public void checkComboAchievements() {
    int multiplier = scoreManager.getMultiplier();
    
    if (multiplier >= 50 && !hasAchievement("COMBO_MASTER")) {
        unlockAchievement("COMBO_MASTER");
    }
}
```

---

## Sơ đồ luồng hoạt động

### Flow 1: Brick Destruction Scoring
```
Brick destroyed
      │
      ↓
addDestroyBrickScore(brickType)
      │
      ├──→ Get base score from brickType
      │    (RED=50, SILVER=100, etc.)
      │
      ├──→ Calculate multiplier bonus
      │    bonus = multiplier × INCREMENT
      │
      ├──→ Add to score
      │    score += baseScore + bonus
      │
      └──→ Increase multiplier
           multiplier++
      │
      ↓
Display score popup
Show visual feedback
```

**Timeline example:**
```
t=0s:  Destroy RED brick
       score: 0 → 60 (50 + 1×10)
       multiplier: 1 → 2

t=1s:  Destroy BLUE brick
       score: 60 → 130 (60 + 50 + 2×10)
       multiplier: 2 → 3

t=2s:  Destroy SILVER brick
       score: 130 → 260 (130 + 100 + 3×10)
       multiplier: 3 → 4

t=5s:  LOSE LIFE!
       score: 260 → 60 (260 - 200)
       multiplier: 4 → 1 (RESET)

t=6s:  Destroy RED brick
       score: 60 → 120 (60 + 50 + 1×10)
       multiplier: 1 → 2
```

### Flow 2: Round Completion Scoring
```
All bricks destroyed
      │
      ↓
isRoundComplete() = true
      │
      ↓
Calculate bonuses:
      │
      ├──→ addRoundCompleteScore()
      │    score += 1000
      │
      └──→ addLifeBonusScore(lives)
           score += lives × 500
      │
      ↓
Display completion screen
Show final score
Check high score
```

**Example calculation:**
```
Before round complete:
    score = 5430

Round complete bonus:
    +1000

Life bonus (3 lives):
    +1500

Final score:
    5430 + 1000 + 1500 = 7930
```

### Flow 3: Life Lost Penalty
```
Ball falls off screen
      │
      ↓
lives--
      │
      ↓
applyLoseLifePenalty()
      │
      ├──→ Subtract penalty
      │    score = max(0, score - 200)
      │
      └──→ Reset multiplier
           multiplier = 1
      │
      ↓
Reset game objects
Respawn ball
Continue playing
```

### Flow 4: Complete Game Scoring Flow
```
┌─────────────────────────────────────┐
│     Start Game (score=0, mult=1)    │
└──────────────┬──────────────────────┘
               │
               ↓
        [Play Round 1]
               │
    ┌──────────┴──────────┐
    │                     │
    ↓                     ↓
Destroy bricks      Lose life
score += X          score -= 200
mult++              mult = 1
    │                     │
    └──────────┬──────────┘
               │
               ↓
     isRoundComplete()?
               │
               ↓
        Round bonus
       +1000, +lives×500
               │
               ↓
      hasNextRound()?
               │
        ├─────┴─────┐
        │           │
      Yes          No
        │           │
        ↓           ↓
   [Next Round]  [Game Won]
        │           │
        │           ↓
        │      Final Score
        │      High Score?
        └───────────┘
```

---

## Ví dụ sử dụng

### Ví dụ 1: Basic integration trong GameManager
```java
public class GameManager {
    private ScoreManager scoreManager;
    
    public void initialize() {
        scoreManager = new ScoreManager();
        
        System.out.println("Score system initialized");
    }
    
    public void startNewGame() {
        scoreManager.resetScore();
        scoreManager.resetMultiplier();
        
        System.out.println("New game started - Score: 0, Multiplier: 1x");
    }
}
```

### Ví dụ 2: Collision handling với scoring
```java
public class GameManager {
    public void checkCollisions() {
        List<Brick> destroyedBricks = collisionManager.checkBallBrickCollisions(ball, bricks);
        
        for (Brick brick : destroyedBricks) {
            // Add score
            scoreManager.addDestroyBrickScore(brick.getBrickType());
            
            // Visual feedback
            int scoreAdded = calculateScoreAdded(brick, scoreManager.getMultiplier() - 1);
            showScorePopup("+" + scoreAdded, brick.getX(), brick.getY());
            
            // Audio feedback based on multiplier
            playHitSound(scoreManager.getMultiplier());
            
            // Spawn power-up
            powerUpManager.spawnFromBrick(brick.getX(), brick.getY(), brick.getBrickType());
        }
    }
    
    private int calculateScoreAdded(Brick brick, int mult) {
        return brick.getBrickType().getBaseScore() + 
               mult * Constants.Scoring.SCORE_BRICK_INCREMENT;
    }
    
    private void playHitSound(int multiplier) {
        if (multiplier >= 20) {
            audioManager.playSFX(SoundEffect.EPIC_HIT);
        } else if (multiplier >= 10) {
            audioManager.playSFX(SoundEffect.GREAT_HIT);
        } else {
            audioManager.playSFX(SoundEffect.BRICK_HIT);
        }
    }
}
```

### Ví dụ 3: Round completion với bonuses
```java
public class GameManager {
    public void onRoundComplete() {
        System.out.println("=== LEVEL COMPLETE ===");
        
        // Score before bonuses
        int scoreBefore = scoreManager.getScore();
        System.out.println("Score: " + scoreBefore);
        
        // Round complete bonus
        scoreManager.addRoundCompleteScore();
        int scoreAfterRound = scoreManager.getScore();
        int roundBonus = scoreAfterRound - scoreBefore;
        System.out.println("Round Bonus: +" + roundBonus);
        
        // Life bonus
        int lives = lifeManager.getLives();
        scoreManager.addLifeBonusScore(lives);
        int finalScore = scoreManager.getScore();
        int lifeBonus = finalScore - scoreAfterRound;
        System.out.println("Life Bonus (×" + lives + "): +" + lifeBonus);
        
        // Total
        System.out.println("Final Score: " + finalScore);
        System.out.println("======================");
        
        // Show completion screen
        showLevelCompleteScreen(roundBonus, lifeBonus, finalScore);
        
        // Advance to next round
        if (roundsManager.hasNextRound()) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    roundsManager.nextRound();
                    stateManager.setState(GameState.PLAYING);
                }
            }, 3000); // 3 second delay
        } else {
            onGameWon();
        }
    }
}
```

### Ví dụ 4: Life lost handling
```java
public class GameManager {
    public void onBallLost() {
        // Decrease lives
        lives--;
        
        if (lives > 0) {
            // Still have lives left
            System.out.println("Life lost! Lives remaining: " + lives);
            
            // Save state before penalty
            int scoreBefore = scoreManager.getScore();
            int multiplierBefore = scoreManager.getMultiplier();
            
            // Apply penalty
            scoreManager.applyLoseLifePenalty();
            
            int scoreAfter = scoreManager.getScore();
            int multiplierAfter = scoreManager.getMultiplier();
            
            // Show penalties
            System.out.println("Score: " + scoreBefore + " → " + scoreAfter + 
                             " (-" + (scoreBefore - scoreAfter) + ")");
            System.out.println("Combo lost! Multiplier: " + multiplierBefore + "x → " + 
                             multiplierAfter + "x");
            
            // Visual feedback
            showPenaltyMessage("LIFE LOST!", "Score -200", "Combo Reset!");
            
            // Audio
            audioManager.playSFX(SoundEffect.LOSE_LIFE);
            
            // Reset game objects
            resetPaddle();
            resetBall();
            clearPowerUps();
            
            // Brief pause
            stateManager.setState(GameState.PAUSED);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stateManager.setState(GameState.PLAYING);
                }
            }, 2000);
        } else {
            // Game over
            gameOver();
        }
    }
}
```

### Ví dụ 5: UI rendering với score và multiplier
```java
public class GameRenderer {
    private ScoreManager scoreManager;
    
    public void renderUI(GraphicsContext gc) {
        // Render score
        renderScore(gc);
        
        // Render multiplier
        renderMultiplier(gc);
        
        // Render combo effects
        renderComboEffects(gc);
    }
    
    private void renderScore(GraphicsContext gc) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);
        
        String scoreText = "Score: " + String.format("%08d", scoreManager.getScore());
        gc.fillText(scoreText, 10, 35);
    }
    
    private void renderMultiplier(GraphicsContext gc) {
        int multiplier = scoreManager.getMultiplier();
        
        // Color based on multiplier value
        Color color;
        String comboText = "";
        
        if (multiplier >= 30) {
            color = Color.GOLD;
            comboText = "LEGENDARY COMBO!";
        } else if (multiplier >= 20) {
            color = Color.ORANGE;
            comboText = "EPIC COMBO!";
        } else if (multiplier >= 10) {
            color = Color.YELLOW;
            comboText = "GREAT COMBO!";
        } else if (multiplier >= 5) {
            color = Color.LIGHTGREEN;
            comboText = "Good Combo";
        } else {
            color = Color.WHITE;
        }
        
        // Render multiplier
        gc.setFill(color);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.fillText("x" + multiplier, 10, 65);
        
        // Render combo text
        if (!comboText.isEmpty()) {
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            gc.fillText(comboText, 10, 90);
        }
    }
    
    private void renderComboEffects(GraphicsContext gc) {
        int multiplier = scoreManager.getMultiplier();
        
        // Pulsing effect for high combos
        if (multiplier >= 10) {
            double pulse = Math.sin(System.currentTimeMillis() / 100.0) * 0.5 + 0.5;
            gc.setGlobalAlpha(0.3 + pulse * 0.4);
            
            gc.setFill(Color.YELLOW);
            gc.fillOval(5, 45, 50 + pulse * 20, 50 + pulse * 20);
            
            gc.setGlobalAlpha(1.0);
        }
        
        // Particles for epic combos
        if (multiplier >= 20) {
            renderComboParticles(gc);
        }
    }
    
    private void renderComboParticles(GraphicsContext gc) {
        // Render particle effects around multiplier display
        for (int i = 0; i < 5; i++) {
            double angle = System.currentTimeMillis() / 500.0 + i * Math.PI * 2 / 5;
            double x = 30 + Math.cos(angle) * 25;
            double y = 55 + Math.sin(angle) * 25;
            
            gc.setFill(Color.GOLD);
            gc.fillOval(x - 2, y - 2, 4, 4);
        }
    }
}
```

### Ví dụ 6: Score popup animation
```java
public class ScorePopup {
    private String text;
    private double x, y;
    private double opacity = 1.0;
    private double offsetY = 0;
    private long createdTime;
    
    public ScorePopup(String text, double x, double y) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.createdTime = System.currentTimeMillis();
    }
    
    public void update(double deltaTime) {
        // Float upward
        offsetY += deltaTime * 30;
        
        // Fade out
        double elapsed = (System.currentTimeMillis() - createdTime) / 1000.0;
        opacity = Math.max(0, 1.0 - elapsed / 1.5);
    }
    
    public void render(GraphicsContext gc) {
        gc.setGlobalAlpha(opacity);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.setFill(Color.YELLOW);
        gc.fillText(text, x, y - offsetY);
        gc.setGlobalAlpha(1.0);
    }
    
    public boolean isExpired() {
        return opacity <= 0;
    }
}

public class GameManager {
    private List<ScorePopup> scorePopups = new ArrayList<>();
    
    public void showScorePopup(String text, double x, double y) {
        scorePopups.add(new ScorePopup(text, x, y));
    }
    
    public void updateScorePopups(double deltaTime) {
        Iterator<ScorePopup> it = scorePopups.iterator();
        while (it.hasNext()) {
            ScorePopup popup = it.next();
            popup.update(deltaTime);
            if (popup.isExpired()) {
                it.remove();
            }
        }
    }
    
    public void renderScorePopups(GraphicsContext gc) {
        for (ScorePopup popup : scorePopups) {
            popup.render(gc);
        }
    }
}
```

### Ví dụ 7: Testing ScoreManager
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScoreManagerTest {
    @Test
    void testInitialization() {
        ScoreManager sm = new ScoreManager();
        
        assertEquals(0, sm.getScore());
        assertEquals(1, sm.getMultiplier());
    }
    
    @Test
    void testResetScore() {
        ScoreManager sm = new ScoreManager();
        
        sm.addDestroyBrickScore(BrickType.RED);
        assertTrue(sm.getScore() > 0);
        
        sm.resetScore();
        assertEquals(0, sm.getScore());
    }
    
    @Test
    void testResetMultiplier() {
        ScoreManager sm = new ScoreManager();
        
        sm.addDestroyBrickScore(BrickType.RED);
        sm.addDestroyBrickScore(BrickType.RED);
        assertEquals(3, sm.getMultiplier());
        
        sm.resetMultiplier();
        assertEquals(1, sm.getMultiplier());
    }
    
    @Test
    void testBrickScoring() {
        ScoreManager sm = new ScoreManager();
        
        // First brick: 50 + (1 × 10) = 60
        sm.addDestroyBrickScore(BrickType.RED);
        assertEquals(60, sm.getScore());
        assertEquals(2, sm.getMultiplier());
        
        // Second brick: 50 + (2 × 10) = 70, total = 130
        sm.addDestroyBrickScore(BrickType.BLUE);
        assertEquals(130, sm.getScore());
        assertEquals(3, sm.getMultiplier());
        
        // Third brick (silver): 100 + (3 × 10) = 130, total = 260
        sm.addDestroyBrickScore(BrickType.SILVER);
        assertEquals(260, sm.getScore());
        assertEquals(4, sm.getMultiplier());
    }
    
    @Test
    void testMultiplierProgression() {
        ScoreManager sm = new ScoreManager();
        
        for (int i = 1; i <= 10; i++) {
            assertEquals(i, sm.getMultiplier());
            sm.addDestroyBrickScore(BrickType.RED);
        }
        
        assertEquals(11, sm.getMultiplier());
    }
    
    @Test
    void testRoundCompleteBonus() {
        ScoreManager sm = new ScoreManager();
        
        sm.addDestroyBrickScore(BrickType.RED); // 60
        
        int scoreBefore = sm.getScore();
        sm.addRoundCompleteScore();
        int scoreAfter = sm.getScore();
        
        assertEquals(1000, scoreAfter - scoreBefore);
    }
    
    @Test
    void testLifeBonus() {
        ScoreManager sm = new ScoreManager();
        
        int scoreBefore = sm.getScore();
        sm.addLifeBonusScore(3);
        int scoreAfter = sm.getScore();
        
        assertEquals(1500, scoreAfter - scoreBefore); // 3 × 500
    }
    
    @Test
    void testLoseLifePenalty() {
        ScoreManager sm = new ScoreManager();
        
        // Build up score and multiplier
        for (int i = 0; i < 10; i++) {
            sm.addDestroyBrickScore(BrickType.RED);
        }
        
        int scoreBefore = sm.getScore();
        int multiplierBefore = sm.getMultiplier();
        
        assertTrue(scoreBefore > 200);
        assertTrue(multiplierBefore > 1);
        
        // Lose life
        sm.applyLoseLifePenalty();
        
        int scoreAfter = sm.getScore();
        int multiplierAfter = sm.getMultiplier();
        
        // Score decreased by 200
        assertEquals(scoreBefore - 200, scoreAfter);
        
        // Multiplier reset to 1
        assertEquals(1, multiplierAfter);
    }
    
    @Test
    void testScoreFloorAtZero() {
        ScoreManager sm = new ScoreManager();
        
        // Score starts at 0
        assertEquals(0, sm.getScore());
        
        // Try to apply penalty
        sm.applyLoseLifePenalty();
        
        // Score should stay at 0, not go negative
        assertEquals(0, sm.getScore());
    }
    
    @Test
    void testCompleteGameScenario() {
        ScoreManager sm = new ScoreManager();
        
        // Destroy 10 bricks
        for (int i = 0; i < 10; i++) {
            sm.addDestroyBrickScore(BrickType.RED);
        }
        
        int scoreAfterBricks = sm.getScore();
        assertTrue(scoreAfterBricks > 0);
        
        // Complete round
        sm.addRoundCompleteScore();
        sm.addLifeBonusScore(3);
        
        int finalScore = sm.getScore();
        
        // Final score should be:
        // bricks + round bonus (1000) + life bonus (1500)
        assertEquals(scoreAfterBricks + 1000 + 1500, finalScore);
    }
}
```

---

## Best Practices

### 1. Reset cả score và multiplier khi start game mới
```java
// ✅ ĐÚNG: Reset both
public void startNewGame() {
    scoreManager.resetScore();
    scoreManager.resetMultiplier();
}

// ❌ SAI: Forget multiplier
public void startNewGame() {
    scoreManager.resetScore();
    // Multiplier stays from previous game!
}
```

### 2. Luôn call applyLoseLifePenalty() khi mất mạng
```java
// ✅ ĐÚNG: Auto-handles score và multiplier
public void onBallLost() {
    lives--;
    if (lives > 0) {
        scoreManager.applyLoseLifePenalty();
    }
}

// ❌ SAI: Manual handling
public void onBallLost() {
    lives--;
    scoreManager.resetMultiplier(); // Incomplete!
    // Forgot to subtract penalty score
}
```

### 3. Show visual feedback cho multiplier
```java
// ✅ ĐÚNG: Visual multiplier display
public void renderUI(GraphicsContext gc) {
    int mult = scoreManager.getMultiplier();
    
    Color color = mult >= 10 ? Color.GOLD : Color.WHITE;
    gc.setFill(color);
    gc.fillText("x" + mult, 10, 60);
    
    if (mult >= 5) {
        gc.fillText("COMBO!", 10, 80);
    }
}

// ❌ SAI: No visual feedback
public void renderUI(GraphicsContext gc) {
    // Only show score, ignore multiplier
    gc.fillText("Score: " + scoreManager.getScore(), 10, 30);
}
```

### 4. Calculate và show score added per brick
```java
// ✅ ĐÚNG: Show score popup
public void onBrickDestroyed(Brick brick) {
    int mult = scoreManager.getMultiplier();
    int baseScore = brick.getBrickType().getBaseScore();
    int scoreAdded = baseScore + mult * 10;
    
    scoreManager.addDestroyBrickScore(brick.getBrickType());
    
    showScorePopup("+" + scoreAdded, brick.getX(), brick.getY());
}

// ❌ SAI: No feedback
public void onBrickDestroyed(Brick brick) {
    scoreManager.addDestroyBrickScore(brick.getBrickType());
    // Player doesn't see how much score they got
}
```

### 5. Format score display properly
```java
// ✅ ĐÚNG: Padded format
String scoreText = String.format("Score: %08d", scoreManager.getScore());
// Output: "Score: 00005430"

// ❌ SAI: No padding
String scoreText = "Score: " + scoreManager.getScore();
// Output: "Score: 5430" (looks less polished)
```

### 6. Audio feedback cho combos
```java
// ✅ ĐÚNG: Different sounds for combo levels
public void onBrickDestroyed() {
    int mult = scoreManager.getMultiplier();
    
    if (mult >= 20) {
        audioManager.playSFX(SoundEffect.EPIC_COMBO);
    } else if (mult >= 10) {
        audioManager.playSFX(SoundEffect.HIGH_COMBO);
    } else {
        audioManager.playSFX(SoundEffect.BRICK_HIT);
    }
}
```

---

## Dependencies

### Imports
```java
import Objects.Bricks.BrickType;    // Brick types for base scores
import Utils.Constants;              // Scoring constants
```

### Các lớp phụ thuộc

| Lớp | Vai trò | Sử dụng |
|-----|---------|---------|
| `BrickType` (enum) | Brick classification | `getBaseScore()` |
| `Constants.Scoring` | Scoring rules | All constant values |

### Constants.Scoring values:
```java
public static class Scoring {
    public static final int SCORE_BRICK_INCREMENT = 10;
    public static final int SCORE_LEVEL_COMPLETE_BONUS = 1000;
    public static final int SCORE_LIFE_BONUS = 500;
    public static final int SCORE_LOSE_LIFE_PENALTY = -200;
}
```

### BrickType base scores:
```java
public enum BrickType {
    RED(50),
    BLUE(50),
    GREEN(50),
    YELLOW(50),
    ORANGE(50),
    SILVER(100),
    GOLD(0); // Indestructible
    
    private final int baseScore;
    
    public int getBaseScore() {
        return baseScore;
    }
}
```

### Được sử dụng bởi:
- `GameManager` - Main score tracking
- `GameRenderer` - Display score và multiplier
- `CollisionManager` - Score khi phá gạch
- `HighScoreManager` - Check high score
- `SaveManager` - Save/load score

### Kiến trúc phụ thuộc
```
┌──────────────────────────────┐
│      ScoreManager            │
└────────┬─────────────────────┘
         │
         ├──→ BrickType (base scores)
         │
         └──→ Constants.Scoring (rules)

Used by:
    ├──→ GameManager (main logic)
    ├──→ GameRenderer (display)
    ├──→ CollisionManager (brick hits)
    └──→ HighScoreManager (records)
```

---

## Design Patterns

### 1. Strategy Pattern (implied)
Mỗi BrickType có base score riêng, có thể mở rộng:
```java
public interface ScoringStrategy {
    int calculateScore(BrickType type, int multiplier);
}

public class StandardScoring implements ScoringStrategy {
    public int calculateScore(BrickType type, int multiplier) {
        return type.getBaseScore() + multiplier * 10;
    }
}

public class BonusScoring implements ScoringStrategy {
    public int calculateScore(BrickType type, int multiplier) {
        return type.getBaseScore() * 2 + multiplier * 15;
    }
}
```

### 2. Observer Pattern (potential)
ScoreManager có thể notify listeners:
```java
public interface ScoreListener {
    void onScoreChanged(int oldScore, int newScore);
    void onMultiplierChanged(int oldMult, int newMult);
}

public class ScoreManager {
    private List<ScoreListener> listeners;
    
    public void addScoreListener(ScoreListener listener) {
        listeners.add(listener);
    }
    
    private void notifyScoreChanged(int oldScore, int newScore) {
        for (ScoreListener listener : listeners) {
            listener.onScoreChanged(oldScore, newScore);
        }
    }
}
```

---

## Mở rộng trong tương lai

### 1. Combo tiers với bonuses
```java
public class ScoreManager {
    public enum ComboTier {
        NORMAL(1, 1.0),      // x1-4
        GOOD(5, 1.2),        // x5-9
        GREAT(10, 1.5),      // x10-19
        EPIC(20, 2.0),       // x20-29
        LEGENDARY(30, 3.0);  // x30+
        
        private final int minMultiplier;
        private final double scoreBonus;
        
        ComboTier(int minMultiplier, double scoreBonus) {
            this.minMultiplier = minMultiplier;
            this.scoreBonus = scoreBonus;
        }
        
        public static ComboTier fromMultiplier(int mult) {
            // Find appropriate tier
        }
    }
    
    public void addDestroyBrickScore(BrickType brickType) {
        ComboTier tier = ComboTier.fromMultiplier(scoreMultiplier);
        int baseScore = (int) (brickType.getBaseScore() * tier.scoreBonus);
        
        // ... rest of calculation
    }
}
```

### 2. Score predictions
```java
public class ScoreManager {
    public int predictNextScore(BrickType brickType) {
        return brickType.getBaseScore() + 
               scoreMultiplier * Constants.Scoring.SCORE_BRICK_INCREMENT;
    }
    
    public int predictScoreAtMultiplier(BrickType brickType, int futureMultiplier) {
        return brickType.getBaseScore() + 
               futureMultiplier * Constants.Scoring.SCORE_BRICK_INCREMENT;
    }
}
```

### 3. Score breakdown/statistics
```java
public class ScoreStatistics {
    private int totalFromBricks;
    private int totalFromBonuses;
    private int totalFromPenalties;
    private int highestMultiplier;
    private int bricksDestroyed;
}

public class ScoreManager {
    private ScoreStatistics statistics;
    
    public ScoreStatistics getStatistics() {
        return statistics;
    }
    
    public void addDestroyBrickScore(BrickType brickType) {
        int scoreAdded = // ... calculation
        
        statistics.totalFromBricks += scoreAdded;
        statistics.bricksDestroyed++;
        statistics.highestMultiplier = Math.max(statistics.highestMultiplier, scoreMultiplier);
        
        // ... rest
    }
}
```

### 4. Time-based bonuses
```java
public class ScoreManager {
    private long roundStartTime;
    
    public void startRoundTimer() {
        roundStartTime = System.currentTimeMillis();
    }
    
    public int calculateTimeBonus() {
        long elapsed = System.currentTimeMillis() - roundStartTime;
        long seconds = elapsed / 1000;
        
        // Fast completion bonus
        if (seconds < 30) {
            return 2000; // Lightning fast!
        } else if (seconds < 60) {
            return 1000; // Fast
        } else if (seconds < 120) {
            return 500; // Normal
        }
        return 0;
    }
    
    public void addRoundCompleteScore() {
        score += Constants.Scoring.SCORE_LEVEL_COMPLETE_BONUS;
        score += calculateTimeBonus();
    }
}
```

### 5. Achievements integration
```java
public class ScoreManager {
    private AchievementManager achievementManager;
    
    public void addDestroyBrickScore(BrickType brickType) {
        // ... scoring logic
        
        // Check achievements
        checkScoreAchievements();
        checkMultiplierAchievements();
    }
    
    private void checkScoreAchievements() {
        if (score >= 100000 && !hasAchievement("SCORE_100K")) {
            achievementManager.unlock("SCORE_100K");
        }
    }
    
    private void checkMultiplierAchievements() {
        if (scoreMultiplier >= 50 && !hasAchievement("COMBO_50")) {
            achievementManager.unlock("COMBO_50");
        }
    }
}
```

### 6. Score modifiers/power-ups
```java
public class ScoreManager {
    private double scoreModifier = 1.0;
    
    public void setScoreModifier(double modifier) {
        this.scoreModifier = modifier;
    }
    
    public void addDestroyBrickScore(BrickType brickType) {
        int baseScore = brickType.getBaseScore();
        int multiplierBonus = scoreMultiplier * Constants.Scoring.SCORE_BRICK_INCREMENT;
        
        // Apply modifier
        int totalScore = (int) ((baseScore + multiplierBonus) * scoreModifier);
        
        score += totalScore;
        scoreMultiplier++;
    }
    
    // Power-up: 2x score for 10 seconds
    public void activateDoubleScore() {
        scoreModifier = 2.0;
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                scoreModifier = 1.0;
            }
        }, 10000);
    }
}
```

---

## Tổng kết

`ScoreManager` là lớp quan trọng cho player engagement:
- ✅ **Simple API:** Easy to use, clear methods
- ✅ **Dynamic scoring:** Multiplier system khuyến khích combo
- ✅ **Balanced rewards:** Bonuses cho completion và lives
- ✅ **Fair penalties:** Score penalty và multiplier reset khi mất mạng
- ✅ **Extensible:** Dễ add thêm scoring mechanics
- ✅ **Motivating:** Encourages skillful, combo-based gameplay

Kết hợp với visual feedback và audio, tạo nên một scoring system engaging và rewarding!

---

**Tác giả:** Arkanoid Development Team  
**Phiên bản:** 1.0  
**Cập nhật lần cuối:** 10 tháng 11, 2025
