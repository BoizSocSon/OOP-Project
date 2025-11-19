# GameState

## Tổng quan
`GameState` là một enum định nghĩa tất cả các trạng thái có thể có của game Arkanoid. Đây là thành phần cốt lõi của State Machine Pattern, giúp quản lý luồng điều khiển game từ menu đến gameplay, pause, win, và game over.

Enum này được sử dụng bởi `StateManager` để kiểm soát chuyển đổi trạng thái hợp lệ và trigger các hành động tương ứng (như phát nhạc, render UI, cập nhật logic game).

## Package
```
Engine.GameState
```

## Kiểu dữ liệu
```java
public enum GameState
```

## Các giá trị Enum

### 1. MENU
**Mô tả:** Trạng thái menu chính của game.

**Đặc điểm:**
- Hiển thị logo game, tiêu đề
- Cho phép nhập tên người chơi
- Hiển thị high scores (bảng xếp hạng)
- Các button: Start Game, Settings, Quit
- Phát nhạc menu background

**Chuyển đến từ:**
- Khởi động game (trạng thái ban đầu)
- `GAME_OVER` - Khi người chơi chọn quay về menu
- `WIN` - Sau khi hoàn thành game
- `PAUSED` - Khi người chơi chọn "Return to Menu"

**Có thể chuyển sang:**
- `PLAYING` - Khi nhấn "Start Game"

**Minh họa:**
```
╔══════════════════════════════════════╗
║          🎮 ARKANOID 🎮              ║
║                                      ║
║  Player Name: [________]             ║
║                                      ║
║  ┌──────────────┐                   ║
║  │  START GAME  │ ◄── Click → PLAYING
║  └──────────────┘                   ║
║  ┌──────────────┐                   ║
║  │   SETTINGS   │                   ║
║  └──────────────┘                   ║
║  ┌──────────────┐                   ║
║  │  HIGH SCORES │                   ║
║  └──────────────┘                   ║
║                                      ║
║  🎵 Menu Music Playing               ║
╚══════════════════════════════════════╝
```

---

### 2. PLAYING
**Mô tả:** Trạng thái chơi game chính thức.

**Đặc điểm:**
- Bóng di chuyển và va chạm với gạch/paddle
- Gạch bị phá hủy dần
- PowerUps rơi xuống và có thể thu thập
- Laser có thể bắn (nếu có PowerUp Laser)
- Điểm số tăng khi phá gạch
- Lives (mạng) giảm khi bóng rơi xuống đáy
- Phát nhạc gameplay background

**Chuyển đến từ:**
- `MENU` - Khi bắt đầu game mới
- `PAUSED` - Khi resume game
- `LEVEL_COMPLETE` - Khi chuyển sang round tiếp theo

**Có thể chuyển sang:**
- `PAUSED` - Khi nhấn ESC
- `LEVEL_COMPLETE` - Khi phá hết gạch trong round
- `GAME_OVER` - Khi hết mạng (lives = 0)
- `WIN` - Khi hoàn thành round cuối cùng

**Minh họa:**
```
╔══════════════════════════════════════╗
║ Lives: ♥♥♥  Score: 1250  Round: 2/4  ║
╠══════════════════════════════════════╣
║  ████ ████ ████ ████ ████           ║ ← Bricks
║  ████ ████ ████ ████ ████           ║
║                                      ║
║           ●  ← Ball                  ║
║                                      ║
║         ▼ PowerUp                    ║
║                                      ║
║           ═══════  ← Paddle          ║
║                                      ║
║  🎵 Gameplay Music Playing           ║
╚══════════════════════════════════════╝
        │
        ├─ ESC → PAUSED
        ├─ All bricks destroyed → LEVEL_COMPLETE
        ├─ Lives = 0 → GAME_OVER
        └─ Last round complete → WIN
```

---

### 3. PAUSED
**Mô tả:** Trạng thái tạm dừng game.

**Đặc điểm:**
- Tất cả đối tượng game bị "đóng băng" (freeze)
- Bóng, PowerUps, Laser dừng di chuyển
- Hiển thị overlay menu pause
- Nhạc bị pause
- Thời gian không trôi (deltaTime = 0)

**Chuyển đến từ:**
- `PLAYING` - Khi nhấn ESC

**Có thể chuyển sang:**
- `PLAYING` - Khi nhấn "Resume" hoặc ESC lần nữa
- `MENU` - Khi nhấn "Return to Menu"

**Minh họa:**
```
╔══════════════════════════════════════╗
║ Lives: ♥♥♥  Score: 1250  Round: 2/4  ║
╠══════════════════════════════════════╣
║  ████ ████ ████ ████ ████           ║
║  ████ ████ ████ ████ ████           ║
║                                      ║
║  ┌────────────────────────────┐     ║
║  │      ⏸ PAUSED              │     ║
║  │                             │     ║
║  │  ┌──────────────────────┐  │     ║
║  │  │  RESUME (ESC)        │  │ → PLAYING
║  │  └──────────────────────┘  │     ║
║  │  ┌──────────────────────┐  │     ║
║  │  │  RETURN TO MENU      │  │ → MENU
║  │  └──────────────────────┘  │     ║
║  └────────────────────────────┘     ║
║                                      ║
║  🔇 Music Paused                     ║
╚══════════════════════════════════════╝
```

---

### 4. LEVEL_COMPLETE
**Mô tả:** Trạng thái chuyển tiếp khi hoàn thành một round.

**Đặc điểm:**
- Hiển thị thông báo "Level Complete!"
- Hiển thị điểm thưởng (bonus points)
- Countdown 2-3 giây trước khi chuyển màn
- Animation hiệu ứng (optional: stars, confetti)
- Không có tương tác người chơi

**Chuyển đến từ:**
- `PLAYING` - Khi phá hết gạch trong round

**Có thể chuyển sang:**
- `PLAYING` - Nếu còn round tiếp theo (load round mới)
- `WIN` - Nếu đã là round cuối cùng

**Minh họa:**
```
╔══════════════════════════════════════╗
║ Lives: ♥♥♥  Score: 1250  Round: 2/4  ║
╠══════════════════════════════════════╣
║                                      ║
║                                      ║
║         🎊 LEVEL COMPLETE! 🎊        ║
║                                      ║
║         Bonus: +500 points           ║
║                                      ║
║      Next level in 3 seconds...      ║
║                  ⭐                   ║
║                                      ║
║                                      ║
╚══════════════════════════════════════╝
        │
        ├─ If (currentRound < totalRounds) → PLAYING
        └─ If (currentRound == totalRounds) → WIN
```

---

### 5. GAME_OVER
**Mô tả:** Trạng thái kết thúc game khi người chơi hết mạng.

**Đặc điểm:**
- Hiển thị "GAME OVER" với hiệu ứng text
- Hiển thị điểm số cuối cùng (Final Score)
- Hiển thị round đạt được (Round Reached)
- Kiểm tra high score (nếu top 10 → lưu vào bảng xếp hạng)
- Animation rotating stars (nếu đạt high score)
- Phát nhạc Game Over
- Button "Return to Menu"

**Chuyển đến từ:**
- `PLAYING` - Khi lives = 0 (bóng rơi xuống đáy lần cuối)

**Có thể chuyển sang:**
- `MENU` - Khi nhấn "Return to Menu" hoặc ENTER

**Minh họa:**
```
╔══════════════════════════════════════╗
║                                      ║
║        💀 GAME OVER 💀               ║
║                                      ║
║     Final Score: 1250                ║
║     Round Reached: 2 / 4             ║
║                                      ║
║  ┌────────────────────────────────┐  ║
║  │    🏆 NEW HIGH SCORE! 🏆       │  ║ (nếu top 10)
║  │    Rank: #5                    │  ║
║  │         ⭐  ⭐                  │  ║
║  └────────────────────────────────┘  ║
║                                      ║
║  ┌──────────────────────────────┐   ║
║  │   RETURN TO MENU (ENTER)     │ → MENU
║  └──────────────────────────────┘   ║
║                                      ║
║  🎵 Game Over Music Playing          ║
╚══════════════════════════════════════╝
```

---

### 6. WIN
**Mô tả:** Trạng thái chiến thắng khi hoàn thành tất cả các round.

**Đặc điểm:**
- Hiển thị "VICTORY!" / "YOU WIN!" với hiệu ứng vàng
- Hiển thị điểm số cuối cùng
- Hiển thị thống kê chi tiết:
  - Total Bricks Destroyed
  - Total PowerUps Collected
  - Time Taken
- Animation đặc biệt (fireworks, rotating stars)
- Phát nhạc chiến thắng
- Kiểm tra và lưu high score
- Button "Return to Menu"

**Chuyển đến từ:**
- `LEVEL_COMPLETE` - Khi hoàn thành round cuối cùng

**Có thể chuyển sang:**
- `MENU` - Khi nhấn "Return to Menu" hoặc ENTER

**Minh họa:**
```
╔══════════════════════════════════════╗
║                                      ║
║      🎊🎉 VICTORY! 🎉🎊               ║
║    YOU COMPLETED ALL LEVELS!         ║
║                                      ║
║     Final Score: 8750                ║
║                                      ║
║  ┌────────────────────────────────┐  ║
║  │      STATISTICS                │  ║
║  │  Bricks Destroyed: 324         │  ║
║  │  PowerUps Collected: 12        │  ║
║  │  Time: 15m 32s                 │  ║
║  └────────────────────────────────┘  ║
║                                      ║
║         ⭐ ⭐ ⭐ ⭐ ⭐                ║
║                                      ║
║  ┌──────────────────────────────┐   ║
║  │   RETURN TO MENU (ENTER)     │ → MENU
║  └──────────────────────────────┘   ║
║                                      ║
║  🎵 Victory Music Playing            ║
╚══════════════════════════════════════╝
```

---

## State Machine Diagram

### Sơ đồ chuyển đổi trạng thái đầy đủ
```
                    ┌─────────┐
              ┌─────│  MENU   │◄──────┐
              │     └────┬────┘       │
              │          │            │
              │    Start Game         │
              │          │            │
              │          ↓            │
              │     ┌─────────┐       │
              │  ┌──│ PLAYING │──┐    │
              │  │  └────┬────┘  │    │
              │  │       │       │    │
              │  │    Lives=0    │    │
              │  │       │       │    │
              │  │       ↓       │    │
              │  │  ┌──────────┐ │    │
              │  │  │GAME_OVER │─┼────┘
              │  │  └──────────┘ │
              │  │                │
              │  │ ESC       All Bricks
              │  │           Destroyed
              │  │                │
              │  ↓                ↓
              │ ┌────────┐  ┌─────────────┐
              │ │ PAUSED │  │LEVEL_COMPLETE│
              │ └───┬────┘  └──────┬──────┘
              │     │              │
              │  Resume        If More Rounds
              │     │              │
              │     └──────┬───────┘
              │            │
              │       If Last Round
              │            │
              │            ↓
              │       ┌─────────┐
              └───────│   WIN   │
                      └─────────┘

Legend:
  → : Valid transition
  │ : Conditional path
```

### Bảng chuyển đổi trạng thái (State Transition Table)

| Từ (From) | Đến (To) | Điều kiện (Condition) | Action khi vào |
|-----------|----------|----------------------|---------------|
| MENU | PLAYING | Nhấn "Start Game" | Load round đầu, phát nhạc gameplay |
| PLAYING | PAUSED | Nhấn ESC | Pause nhạc, freeze objects |
| PLAYING | LEVEL_COMPLETE | Phá hết gạch | Hiển thị bonus, countdown |
| PLAYING | GAME_OVER | Lives = 0 | Phát nhạc Game Over, kiểm tra high score |
| PLAYING | WIN | Phá hết gạch ở round cuối | Phát nhạc Victory, hiển thị stats |
| PAUSED | PLAYING | Nhấn "Resume" / ESC | Resume nhạc, unfreeze objects |
| PAUSED | MENU | Nhấn "Return to Menu" | Reset game, phát nhạc menu |
| LEVEL_COMPLETE | PLAYING | Còn round tiếp theo | Load round mới, reset paddle/ball |
| LEVEL_COMPLETE | WIN | Đã là round cuối | Chuyển sang màn chiến thắng |
| GAME_OVER | MENU | Nhấn "Return to Menu" / ENTER | Reset game, phát nhạc menu |
| WIN | MENU | Nhấn "Return to Menu" / ENTER | Reset game, phát nhạc menu |

---

## Cách sử dụng

### Ví dụ 1: Kiểm tra trạng thái hiện tại
```java
public class GameManager {
    private StateManager stateManager;
    
    public void update(double deltaTime) {
        GameState currentState = stateManager.getState();
        
        switch (currentState) {
            case MENU:
                // Không cập nhật game logic
                break;
                
            case PLAYING:
                // Cập nhật ball, paddle, bricks, powerups...
                updateGameObjects(deltaTime);
                checkCollisions();
                checkWinLoseConditions();
                break;
                
            case PAUSED:
                // Không cập nhật game objects (frozen)
                break;
                
            case LEVEL_COMPLETE:
                // Countdown timer, sau đó chuyển sang màn tiếp theo
                levelCompleteTimer += deltaTime;
                if (levelCompleteTimer >= 3.0) {
                    loadNextLevel();
                }
                break;
                
            case GAME_OVER:
            case WIN:
                // Không cập nhật game logic
                break;
        }
    }
}
```

### Ví dụ 2: Render theo trạng thái
```java
public class GameRenderer {
    public void render(GraphicsContext gc, GameState state) {
        switch (state) {
            case MENU:
                renderMenu(gc);
                break;
                
            case PLAYING:
                renderGameplay(gc);
                break;
                
            case PAUSED:
                renderGameplay(gc); // Render game ở dưới
                renderPauseOverlay(gc); // Overlay ở trên
                break;
                
            case LEVEL_COMPLETE:
                renderGameplay(gc);
                renderLevelCompleteScreen(gc);
                break;
                
            case GAME_OVER:
                renderGameOverScreen(gc);
                break;
                
            case WIN:
                renderWinScreen(gc);
                break;
        }
    }
}
```

### Ví dụ 3: Xử lý input theo trạng thái
```java
public class InputHandler {
    public void handleKeyPressed(KeyEvent event, GameState state) {
        switch (state) {
            case MENU:
                if (event.getCode() == KeyCode.ENTER) {
                    stateManager.setState(GameState.PLAYING);
                }
                break;
                
            case PLAYING:
                if (event.getCode() == KeyCode.ESCAPE) {
                    stateManager.setState(GameState.PAUSED);
                }
                // Xử lý di chuyển paddle...
                break;
                
            case PAUSED:
                if (event.getCode() == KeyCode.ESCAPE) {
                    stateManager.setState(GameState.PLAYING);
                }
                break;
                
            case GAME_OVER:
            case WIN:
                if (event.getCode() == KeyCode.ENTER) {
                    stateManager.setState(GameState.MENU);
                }
                break;
                
            default:
                break;
        }
    }
}
```

### Ví dụ 4: Logic kiểm tra điều kiện thắng/thua
```java
public class GameManager {
    private void checkWinLoseConditions() {
        // Kiểm tra game over
        if (lives <= 0) {
            stateManager.setState(GameState.GAME_OVER);
            highScoreManager.checkAndSaveScore(score, playerName);
            return;
        }
        
        // Kiểm tra hoàn thành level
        if (areAllBricksDestroyed()) {
            stateManager.setState(GameState.LEVEL_COMPLETE);
            
            // Kiểm tra xem có phải round cuối không
            if (currentRound >= totalRounds) {
                // Sẽ chuyển sang WIN từ LEVEL_COMPLETE
            } else {
                // Sẽ load round tiếp theo
            }
        }
    }
    
    private boolean areAllBricksDestroyed() {
        for (Brick brick : bricks) {
            if (brick.isAlive() && brick.getBrickType() != BrickType.GOLD) {
                return false; // Còn gạch (không phải Gold)
            }
        }
        return true; // Tất cả gạch đã bị phá
    }
}
```

---

## Best Practices

### 1. Single Source of Truth
```java
// ✅ ĐÚNG: Luôn dùng StateManager để kiểm tra state
if (stateManager.getState() == GameState.PLAYING) {
    // Update logic
}

// ❌ SAI: Không nên tự quản lý state flag riêng
private boolean isGamePlaying; // Redundant với StateManager!
```

### 2. Không hardcode state transitions
```java
// ✅ ĐÚNG: Dùng StateManager.setState() để chuyển state
stateManager.setState(GameState.PAUSED);

// ❌ SAI: Trực tiếp thay đổi state
currentState = GameState.PAUSED; // Bỏ qua validation và callbacks!
```

### 3. Xử lý state-specific logic trong update loop
```java
// ✅ ĐÚNG: Tách biệt logic theo state
public void update(double deltaTime) {
    switch (stateManager.getState()) {
        case PLAYING:
            updateGameplay(deltaTime);
            break;
        case PAUSED:
            // Không update gameplay
            break;
    }
}

// ❌ SAI: Update mọi thứ bất kể state
public void update(double deltaTime) {
    ball.move(deltaTime); // Vẫn di chuyển khi PAUSED!
}
```

### 4. State-aware rendering
```java
// ✅ ĐÚNG: Render phù hợp với state
public void render(GraphicsContext gc) {
    if (stateManager.getState() == GameState.PAUSED) {
        renderPauseOverlay(gc);
    }
}

// ❌ SAI: Render overlay luôn luôn
public void render(GraphicsContext gc) {
    renderPauseOverlay(gc); // Hiển thị ngay cả khi PLAYING!
}
```

### 5. Graceful state transitions
```java
// ✅ ĐÚNG: Kiểm tra và xử lý transition failed
if (!stateManager.setState(GameState.PLAYING)) {
    System.err.println("Cannot start game from current state");
}

// ❌ SAI: Giả định transition luôn thành công
stateManager.setState(GameState.PLAYING);
// Không biết nếu transition bị từ chối
```

---

## Dependencies

### Được sử dụng bởi:
- `StateManager` - Quản lý trạng thái hiện tại và chuyển đổi
- `GameManager` - Kiểm tra state để cập nhật logic
- `Screen` implementations - Render UI theo state
- `InputHandler` - Xử lý input theo state

### Liên quan đến:
- `AudioManager` - Phát nhạc khác nhau cho mỗi state
- `HighScoreManager` - Lưu điểm cao khi GAME_OVER/WIN
- `RoundsManager` - Load round mới khi LEVEL_COMPLETE

---

## Design Pattern

### State Pattern
`GameState` enum là implementation của **State Pattern**:

```
┌─────────────────────────────────┐
│      StateManager (Context)     │
│  - currentState: GameState      │
│  - setState(newState)            │
└──────────┬──────────────────────┘
           │ uses
           ↓
┌─────────────────────────────────┐
│     GameState (State Enum)      │
│  - MENU                          │
│  - PLAYING                       │
│  - PAUSED                        │
│  - LEVEL_COMPLETE                │
│  - GAME_OVER                     │
│  - WIN                           │
└─────────────────────────────────┘
```

**Ưu điểm:**
- ✅ Tách biệt logic cho từng trạng thái
- ✅ Dễ thêm state mới (thêm enum value)
- ✅ Type-safe (không thể có invalid state)
- ✅ Switch statement compile-time check

**So với OOP State Pattern:**
- Enum đơn giản hơn (không cần class hierarchy)
- Phù hợp cho game nhỏ với số state ít
- Trade-off: Ít flexible hơn (không thể override behavior per state)

---

## Testing

### Unit Test Example
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {
    @Test
    void testEnumValues() {
        GameState[] states = GameState.values();
        assertEquals(6, states.length);
        
        assertEquals(GameState.MENU, states[0]);
        assertEquals(GameState.PLAYING, states[1]);
        assertEquals(GameState.PAUSED, states[2]);
        assertEquals(GameState.LEVEL_COMPLETE, states[3]);
        assertEquals(GameState.GAME_OVER, states[4]);
        assertEquals(GameState.WIN, states[5]);
    }
    
    @Test
    void testEnumValueOf() {
        assertEquals(GameState.PLAYING, GameState.valueOf("PLAYING"));
        assertEquals(GameState.PAUSED, GameState.valueOf("PAUSED"));
    }
    
    @Test
    void testEnumEquality() {
        GameState state1 = GameState.MENU;
        GameState state2 = GameState.MENU;
        
        assertTrue(state1 == state2); // Enum values are singletons
        assertEquals(state1, state2);
    }
    
    @Test
    void testSwitchStatement() {
        GameState state = GameState.PLAYING;
        String result = "";
        
        switch (state) {
            case MENU:
                result = "menu";
                break;
            case PLAYING:
                result = "playing";
                break;
            default:
                result = "other";
        }
        
        assertEquals("playing", result);
    }
}
```

---

## Mở rộng trong tương lai

### 1. Sub-states (Nested states)
```java
public enum GameState {
    MENU(MenuSubState.MAIN),
    PLAYING(PlayingSubState.ACTIVE),
    // ...
    
    private final Enum<?> defaultSubState;
    
    GameState(Enum<?> defaultSubState) {
        this.defaultSubState = defaultSubState;
    }
}

public enum MenuSubState {
    MAIN,
    SETTINGS,
    HIGH_SCORES
}

public enum PlayingSubState {
    ACTIVE,
    BALL_STUCK,    // Bóng dính vào paddle (catch mode)
    BONUS_ROUND    // Vòng bonus đặc biệt
}
```

### 2. State metadata
```java
public enum GameState {
    MENU(true, false, MusicTrack.MENU),
    PLAYING(false, true, MusicTrack.ROUNDS),
    PAUSED(true, false, null),
    // ...
    
    private final boolean canShowMenu;
    private final boolean updatesGameLogic;
    private final MusicTrack backgroundMusic;
    
    GameState(boolean canShowMenu, boolean updatesGameLogic, MusicTrack music) {
        this.canShowMenu = canShowMenu;
        this.updatesGameLogic = updatesGameLogic;
        this.backgroundMusic = music;
    }
    
    public boolean canShowMenu() { return canShowMenu; }
    public boolean updatesGameLogic() { return updatesGameLogic; }
    public MusicTrack getBackgroundMusic() { return backgroundMusic; }
}

// Usage:
if (currentState.updatesGameLogic()) {
    updateGameObjects(deltaTime);
}
```

### 3. State transition logging
```java
public enum GameState {
    MENU,
    PLAYING,
    // ...
    
    private long lastEnterTime;
    private long totalTimeInState;
    
    public void onEnter() {
        lastEnterTime = System.currentTimeMillis();
    }
    
    public void onExit() {
        long duration = System.currentTimeMillis() - lastEnterTime;
        totalTimeInState += duration;
        System.out.println(this + " duration: " + duration + "ms");
    }
    
    public long getTotalTimeInState() {
        return totalTimeInState;
    }
}
```

### 4. Thêm state mới cho multiplayer
```java
public enum GameState {
    MENU,
    
    // Single player states
    PLAYING,
    PAUSED,
    
    // Multiplayer states
    LOBBY,              // Phòng chờ multiplayer
    WAITING_FOR_PLAYER, // Đang chờ người chơi thứ 2
    MULTIPLAYER_GAME,   // Chơi 2 người
    
    LEVEL_COMPLETE,
    GAME_OVER,
    WIN
}
```

### 5. Debug states
```java
public enum GameState {
    // Normal states
    MENU,
    PLAYING,
    // ...
    
    // Debug states (chỉ dùng khi DEBUG_MODE = true)
    DEBUG_BRICK_EDITOR,   // Chỉnh sửa vị trí gạch
    DEBUG_COLLISION_VIEW, // Hiển thị bounding boxes
    DEBUG_PERFORMANCE     // Hiển thị FPS, memory usage
}
```

---

## Tổng kết

`GameState` enum là thành phần cốt lõi của game architecture:
- ✅ **Đơn giản:** Chỉ 6 states, dễ hiểu và maintain
- ✅ **Type-safe:** Compile-time checking, không có invalid states
- ✅ **Rõ ràng:** Mỗi state có mục đích và behavior riêng biệt
- ✅ **Scalable:** Dễ dàng thêm states mới cho features trong tương lai
- ✅ **Testable:** Enum values dễ dàng test và mock

Kết hợp với `StateManager`, tạo nên một state machine robust và maintainable cho toàn bộ game flow.

---

**Tác giả:** Arkanoid Development Team  
**Phiên bản:** 1.0  
**Cập nhật lần cuối:** 2024
