# StateManager

## Tổng quan
`StateManager` là lớp quản lý trạng thái trung tâm (State Manager) của game Arkanoid. Lớp này chịu trách nhiệm theo dõi trạng thái hiện tại của game, kiểm soát các chuyển đổi hợp lệ giữa các trạng thái, và trigger các hành động tương ứng khi vào/thoát mỗi trạng thái.

StateManager implement **State Pattern** để đảm bảo game flow logic và có tổ chức, đồng thời tích hợp với `AudioManager` để điều khiển nhạc nền phù hợp với từng trạng thái.

## Package
```
Engine.StateManager
```

## Thuộc tính

| Thuộc tính | Kiểu dữ liệu | Phạm vi truy cập | Mô tả |
|-----------|-------------|-----------------|-------|
| `currentState` | `GameState` | `private` | Trạng thái hiện tại của game |
| `previousState` | `GameState` | `private` | Trạng thái trước đó (dùng cho rollback hoặc tracking) |
| `audioManager` | `AudioManager` | `private final` | Reference đến AudioManager singleton để điều khiển nhạc |
| `validTransitions` | `Map<GameState, Set<GameState>>` | `private final` | Map định nghĩa các quy tắc chuyển đổi hợp lệ |

### Chi tiết thuộc tính

#### currentState
Lưu trữ trạng thái hiện tại của game. Khởi tạo ban đầu là `GameState.MENU`.

```java
private GameState currentState = GameState.MENU;
```

#### previousState
Lưu trữ trạng thái trước đó. Hữu ích cho:
- Resume từ PAUSED (biết pause từ đâu)
- Debug và logging
- Analytics (tracking state flow)

```java
private GameState previousState = null; // null khi mới khởi động
```

#### audioManager
Singleton instance của AudioManager, được sử dụng để:
- Phát nhạc phù hợp khi vào state mới
- Pause/Resume nhạc khi PAUSED
- Dừng nhạc khi cần

```java
private final AudioManager audioManager = AudioManager.getInstance();
```

#### validTransitions
Map định nghĩa **state transition rules** (quy tắc chuyển đổi):
- **Key:** Trạng thái xuất phát (From State)
- **Value:** Set các trạng thái đích hợp lệ (To States)

Ví dụ:
```java
validTransitions.put(GameState.MENU, EnumSet.of(GameState.PLAYING));
// Từ MENU chỉ có thể → PLAYING

validTransitions.put(GameState.PLAYING, 
    EnumSet.of(GameState.PAUSED, GameState.LEVEL_COMPLETE, 
               GameState.GAME_OVER, GameState.WIN));
// Từ PLAYING có thể → PAUSED, LEVEL_COMPLETE, GAME_OVER, hoặc WIN
```

**Minh họa State Transition Map:**
```
validTransitions:
┌─────────────┬──────────────────────────────────────┐
│ From State  │ To States (Valid Destinations)       │
├─────────────┼──────────────────────────────────────┤
│ MENU        │ → PLAYING                            │
│ PLAYING     │ → PAUSED, LEVEL_COMPLETE,            │
│             │   GAME_OVER, WIN                     │
│ PAUSED      │ → PLAYING, MENU                      │
│ LEVEL_COMP. │ → PLAYING, WIN                       │
│ GAME_OVER   │ → MENU                               │
│ WIN         │ → MENU                               │
└─────────────┴──────────────────────────────────────┘
```

---

## Constructor

### StateManager()
Khởi tạo StateManager với trạng thái ban đầu là MENU.

**Các bước khởi tạo:**
1. Set `currentState = GameState.MENU`
2. Set `previousState = null`
3. Lấy AudioManager singleton instance
4. Khởi tạo `validTransitions` map
5. Gọi `initializeTransitionRules()` để thiết lập quy tắc
6. Gọi `onStateEnter(GameState.MENU)` để trigger actions cho MENU state

**Ví dụ:**
```java
StateManager stateManager = new StateManager();
// Sau khi khởi tạo:
// - currentState = MENU
// - previousState = null
// - Nhạc menu đang phát
// - Transition rules đã được setup
```

---

## Phương thức công khai

### 1. setState()
```java
public boolean setState(GameState newState)
```

Thực hiện chuyển đổi trạng thái game sang trạng thái mới.

**Tham số:**
- `newState` - Trạng thái mới cần chuyển đến

**Giá trị trả về:**
- `true` - Chuyển đổi hợp lệ và thành công
- `false` - Chuyển đổi không hợp lệ hoặc thất bại

**Thuật toán:**

1. **Null check:**
   ```java
   if (newState == null) {
       System.err.println("StateManager: Cannot transition to null state");
       return false;
   }
   ```

2. **Same state check:**
   ```java
   if (currentState == newState) {
       return true; // Không cần làm gì
   }
   ```

3. **Validation check:**
   ```java
   if (!canTransitionTo(currentState, newState)) {
       System.err.println("StateManager: Invalid transition from " 
                          + currentState + " to " + newState);
       return false;
   }
   ```

4. **Perform transition:**
   ```java
   System.out.println("StateManager: Transitioning from " 
                      + currentState + " to " + newState);
   
   onStateExit(currentState);      // Cleanup old state
   previousState = currentState;   // Save old state
   currentState = newState;        // Update to new state
   onStateEnter(currentState);     // Initialize new state
   return true;
   ```

**Ví dụ:**
```java
StateManager sm = new StateManager();

// Chuyển từ MENU → PLAYING
boolean success = sm.setState(GameState.PLAYING);
// ✅ success = true (valid transition)
// Console: "StateManager: Transitioning from MENU to PLAYING"
// Console: "Exiting menu."
// Console: "Game resumed/started."
// Nhạc gameplay bắt đầu phát

// Thử chuyển từ PLAYING → MENU (invalid!)
success = sm.setState(GameState.MENU);
// ❌ success = false (không được phép transition trực tiếp)
// Console: "StateManager: Invalid transition from PLAYING to MENU"
// Trạng thái vẫn là PLAYING
```

**Transition Flow Diagram:**
```
setState(newState) called
         │
         ↓
    ┌─────────┐
    │ Null?   │ Yes → Return false
    └────┬────┘
         │ No
         ↓
    ┌─────────┐
    │ Same?   │ Yes → Return true
    └────┬────┘
         │ No
         ↓
    ┌─────────┐
    │ Valid?  │ No → Return false
    └────┬────┘
         │ Yes
         ↓
    onStateExit(currentState)
         │
         ↓
    previousState = currentState
         │
         ↓
    currentState = newState
         │
         ↓
    onStateEnter(newState)
         │
         ↓
    Return true
```

---

### 2. canTransitionTo()
```java
public boolean canTransitionTo(GameState from, GameState to)
```

Kiểm tra xem việc chuyển đổi từ state `from` sang state `to` có hợp lệ không.

**Tham số:**
- `from` - Trạng thái xuất phát
- `to` - Trạng thái đích

**Giá trị trả về:**
- `true` - Chuyển đổi hợp lệ (có trong transition rules)
- `false` - Chuyển đổi không hợp lệ

**Thuật toán:**
```java
Set<GameState> allowed = validTransitions.get(from);
return allowed != null && allowed.contains(to);
```

**Ví dụ:**
```java
StateManager sm = new StateManager();

// Kiểm tra các transition hợp lệ
System.out.println(sm.canTransitionTo(GameState.MENU, GameState.PLAYING));
// Output: true ✅

System.out.println(sm.canTransitionTo(GameState.PLAYING, GameState.PAUSED));
// Output: true ✅

System.out.println(sm.canTransitionTo(GameState.PLAYING, GameState.MENU));
// Output: false ❌ (phải qua PAUSED trước)

System.out.println(sm.canTransitionTo(GameState.GAME_OVER, GameState.PLAYING));
// Output: false ❌ (phải về MENU trước)
```

**Use cases:**
- Pre-validation trước khi gọi `setState()`
- UI logic (disable buttons cho invalid transitions)
- Testing và debugging

```java
// UI example: Disable "Resume" button nếu không thể resume
if (!stateManager.canTransitionTo(stateManager.getState(), GameState.PLAYING)) {
    resumeButton.setDisable(true);
}
```

---

### 3. getState()
```java
public GameState getState()
```

Lấy trạng thái game hiện tại.

**Giá trị trả về:**
- `GameState` - Trạng thái hiện tại

**Ví dụ:**
```java
StateManager sm = new StateManager();

GameState current = sm.getState();
System.out.println("Current state: " + current);
// Output: "Current state: MENU"

sm.setState(GameState.PLAYING);
current = sm.getState();
System.out.println("Current state: " + current);
// Output: "Current state: PLAYING"

// Sử dụng trong update loop
if (sm.getState() == GameState.PLAYING) {
    updateGameplay(deltaTime);
}
```

---

### 4. getPreviousState()
```java
public GameState getPreviousState()
```

Lấy trạng thái game trước đó.

**Giá trị trả về:**
- `GameState` - Trạng thái trước đó
- `null` - Nếu chưa có state transition nào

**Ví dụ:**
```java
StateManager sm = new StateManager();

GameState prev = sm.getPreviousState();
System.out.println("Previous state: " + prev);
// Output: "Previous state: null" (chưa có transition)

sm.setState(GameState.PLAYING);
prev = sm.getPreviousState();
System.out.println("Previous state: " + prev);
// Output: "Previous state: MENU"

sm.setState(GameState.PAUSED);
prev = sm.getPreviousState();
System.out.println("Previous state: " + prev);
// Output: "Previous state: PLAYING"

// Use case: Resume music khi unpause
if (sm.getPreviousState() == GameState.PLAYING) {
    audioManager.resumeMusic();
}
```

---

### 5. isPlaying()
```java
public boolean isPlaying()
```

Kiểm tra xem game có đang ở trạng thái PLAYING không.

**Giá trị trả về:**
- `true` - Đang chơi game
- `false` - Không đang chơi

**Ví dụ:**
```java
if (stateManager.isPlaying()) {
    // Cập nhật game objects
    ball.move(deltaTime);
    paddle.move(deltaTime);
    checkCollisions();
}
```

---

### 6. isPaused()
```java
public boolean isPaused()
```

Kiểm tra xem game có đang ở trạng thái PAUSED không.

**Giá trị trả về:**
- `true` - Game đang tạm dừng
- `false` - Game không tạm dừng

**Ví dụ:**
```java
if (stateManager.isPaused()) {
    // Hiển thị pause overlay
    renderPauseMenu(gc);
}

// Toggle pause/unpause
if (keyPressed == KeyCode.ESCAPE) {
    if (stateManager.isPaused()) {
        stateManager.setState(GameState.PLAYING);
    } else if (stateManager.isPlaying()) {
        stateManager.setState(GameState.PAUSED);
    }
}
```

---

### 7. isGameOver()
```java
public boolean isGameOver()
```

Kiểm tra xem game có đang ở trạng thái GAME_OVER không.

**Giá trị trả về:**
- `true` - Game over (hết mạng)
- `false` - Chưa game over

**Ví dụ:**
```java
if (stateManager.isGameOver()) {
    // Hiển thị game over screen
    renderGameOverScreen(gc);
    
    // Save high score
    highScoreManager.checkAndSaveScore(score, playerName);
}
```

---

### 8. getAudioManager()
```java
public AudioManager getAudioManager()
```

Lấy AudioManager instance đang được sử dụng.

**Giá trị trả về:**
- `AudioManager` - Singleton instance

**Ví dụ:**
```java
AudioManager am = stateManager.getAudioManager();
am.setVolume(0.5); // 50% volume
am.toggleMute();
```

---

## Phương thức riêng tư

### 1. initializeTransitionRules()
```java
private void initializeTransitionRules()
```

Khởi tạo các quy tắc chuyển đổi hợp lệ giữa các trạng thái game.

**Transition Rules:**

```java
// MENU → PLAYING
validTransitions.put(GameState.MENU, 
    EnumSet.of(GameState.PLAYING));

// PLAYING → PAUSED, LEVEL_COMPLETE, GAME_OVER, WIN
validTransitions.put(GameState.PLAYING,
    EnumSet.of(GameState.PAUSED, GameState.LEVEL_COMPLETE, 
               GameState.GAME_OVER, GameState.WIN));

// PAUSED → PLAYING, MENU
validTransitions.put(GameState.PAUSED,
    EnumSet.of(GameState.PLAYING, GameState.MENU));

// LEVEL_COMPLETE → PLAYING, WIN
validTransitions.put(GameState.LEVEL_COMPLETE,
    EnumSet.of(GameState.PLAYING, GameState.WIN));

// GAME_OVER → MENU
validTransitions.put(GameState.GAME_OVER,
    EnumSet.of(GameState.MENU));

// WIN → MENU
validTransitions.put(GameState.WIN,
    EnumSet.of(GameState.MENU));
```

**State Transition Graph:**
```
         ┌──────────────┐
    ┌───►│     MENU     │◄──────┐
    │    └──────┬───────┘       │
    │           │               │
    │           ↓               │
    │    ┌──────────────┐       │
    │ ┌─►│   PLAYING    │──┐    │
    │ │  └──────┬───────┘  │    │
    │ │         │          │    │
    │ │         ↓          ↓    │
    │ │  ┌──────────┐  ┌──────────┐
    │ │  │  PAUSED  │  │  L_COMP  │
    │ │  └────┬─────┘  └────┬─────┘
    │ │       │             │
    │ └───────┘             │
    │                       ↓
    │                  ┌─────────┐
    │                  │   WIN   │
    │                  └────┬────┘
    │                       │
    │    ┌──────────────┐   │
    └────│  GAME_OVER   │◄──┘
         └──────────────┘
```

---

### 2. onStateEnter()
```java
private void onStateEnter(GameState state)
```

Xử lý các hành động cần thiết khi game VÀO một trạng thái mới.

**Tham số:**
- `state` - Trạng thái vừa được vào

**Actions cho từng state:**

#### MENU
```java
case MENU:
    System.out.println("Returned to menu.");
    audioManager.playMusic(MusicTrack.MENU);
    break;
```
- Log message
- Phát nhạc menu

#### PLAYING
```java
case PLAYING:
    System.out.println("Game resumed/started.");
    if (previousState == GameState.PAUSED) {
        audioManager.resumeMusic(); // Resume từ pause
    } else {
        audioManager.playMusic(MusicTrack.ROUNDS); // Start mới
    }
    break;
```
- Log message
- **Nếu từ PAUSED:** Resume nhạc (tiếp tục từ chỗ đang phát)
- **Ngược lại:** Phát nhạc gameplay từ đầu

#### PAUSED
```java
case PAUSED:
    System.out.println("Game paused.");
    audioManager.pauseMusic();
    break;
```
- Log message
- Pause nhạc (không stop, để resume sau)

#### LEVEL_COMPLETE
```java
case LEVEL_COMPLETE:
    System.out.println("Level completed!");
    // TODO: Add bonus points logic
    break;
```
- Log message
- (Có thể thêm: tính điểm thưởng, animation)

#### GAME_OVER
```java
case GAME_OVER:
    System.out.println("Game over!");
    audioManager.playMusic(MusicTrack.GAME_OVER);
    break;
```
- Log message
- Phát nhạc game over

#### WIN
```java
case WIN:
    System.out.println("You win!");
    audioManager.playMusic(MusicTrack.VICTORY);
    break;
```
- Log message
- Phát nhạc chiến thắng

**Ví dụ flow:**
```
setState(PLAYING) called
    │
    ↓
onStateEnter(PLAYING)
    │
    ├─ previousState == PAUSED?
    │  ├─ Yes → audioManager.resumeMusic()
    │  └─ No  → audioManager.playMusic(ROUNDS)
    │
    └─ System.out.println("Game resumed/started.")
```

---

### 3. onStateExit()
```java
private void onStateExit(GameState state)
```

Xử lý các hành động cần thiết khi game THOÁT khỏi một trạng thái.

**Tham số:**
- `state` - Trạng thái vừa được thoát

**Actions cho từng state:**

```java
case PAUSED:
    System.out.println("Exiting pause.");
    break;

case PLAYING:
    System.out.println("Exiting playing state.");
    // TODO: Stop timers if needed
    break;

case LEVEL_COMPLETE:
    System.out.println("Exiting level complete state.");
    break;

case GAME_OVER:
    System.out.println("Exiting game over state.");
    // TODO: Save high score here
    break;

case WIN:
    System.out.println("Exiting win state.");
    break;

case MENU:
    System.out.println("Exiting menu.");
    // TODO: Stop menu music
    break;
```

**Hiện tại:** Chủ yếu logging  
**Tương lai:** Có thể thêm cleanup logic (stop timers, save data, etc.)

---

## Sơ đồ hoạt động

### State Lifecycle
```
┌─────────────────────────────────────────────────────┐
│                setState(newState)                   │
└──────────────────────┬──────────────────────────────┘
                       │
                       ↓
          ┌────────────────────────┐
          │   Validate Transition  │
          └────────────┬───────────┘
                       │
              Valid transition?
                       │
            ┌──────────┴──────────┐
            │ No                  │ Yes
            ↓                     ↓
    ┌──────────────┐      ┌──────────────────┐
    │ Return false │      │ onStateExit(old) │
    └──────────────┘      └────────┬─────────┘
                                   │
                                   ↓
                          ┌─────────────────┐
                          │ previousState = │
                          │  currentState   │
                          └────────┬────────┘
                                   │
                                   ↓
                          ┌─────────────────┐
                          │ currentState =  │
                          │   newState      │
                          └────────┬────────┘
                                   │
                                   ↓
                          ┌──────────────────┐
                          │ onStateEnter(new)│
                          └────────┬─────────┘
                                   │
                                   ↓
                          ┌──────────────┐
                          │ Return true  │
                          └──────────────┘
```

---

## Ví dụ sử dụng

### Ví dụ 1: Khởi tạo và bắt đầu game
```java
public class ArkanoidApp extends Application {
    private StateManager stateManager;
    private GameManager gameManager;
    
    @Override
    public void start(Stage primaryStage) {
        // Khởi tạo StateManager
        stateManager = new StateManager();
        // Lúc này: currentState = MENU, nhạc menu đang phát
        
        // Khởi tạo GameManager với StateManager
        gameManager = new GameManager(stateManager);
        
        // Setup UI và game loop...
        setupUI(primaryStage);
        startGameLoop();
    }
    
    private void handleStartButton() {
        // User nhấn "Start Game" button
        boolean success = stateManager.setState(GameState.PLAYING);
        
        if (success) {
            // Game started!
            gameManager.startNewGame();
        } else {
            System.err.println("Cannot start game from current state");
        }
    }
}
```

### Ví dụ 2: Xử lý ESC key (Pause/Resume)
```java
public class InputHandler {
    private StateManager stateManager;
    
    public void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            GameState current = stateManager.getState();
            
            if (current == GameState.PLAYING) {
                // Pause game
                stateManager.setState(GameState.PAUSED);
                System.out.println("Game paused");
            } 
            else if (current == GameState.PAUSED) {
                // Resume game
                stateManager.setState(GameState.PLAYING);
                System.out.println("Game resumed");
            }
        }
    }
}
```

### Ví dụ 3: Game loop với state-aware updates
```java
public class GameManager {
    private StateManager stateManager;
    private List<Ball> balls;
    private Paddle paddle;
    private List<Brick> bricks;
    
    public void update(double deltaTime) {
        GameState state = stateManager.getState();
        
        switch (state) {
            case MENU:
                // Chỉ update menu animations
                updateMenuAnimations(deltaTime);
                break;
                
            case PLAYING:
                // Full game logic update
                updateBalls(deltaTime);
                updatePaddle(deltaTime);
                updatePowerUps(deltaTime);
                updateLasers(deltaTime);
                checkCollisions();
                checkWinLoseConditions();
                break;
                
            case PAUSED:
                // Không update game objects (frozen)
                break;
                
            case LEVEL_COMPLETE:
                // Update countdown timer
                levelCompleteTimer += deltaTime;
                if (levelCompleteTimer >= 3.0) {
                    if (hasMoreRounds()) {
                        stateManager.setState(GameState.PLAYING);
                        loadNextRound();
                    } else {
                        stateManager.setState(GameState.WIN);
                    }
                }
                break;
                
            case GAME_OVER:
            case WIN:
                // Update animations (rotating stars, etc.)
                updateEndScreenAnimations(deltaTime);
                break;
        }
    }
    
    private void checkWinLoseConditions() {
        // Check lose condition
        if (lives <= 0) {
            stateManager.setState(GameState.GAME_OVER);
            return;
        }
        
        // Check win condition (all bricks destroyed)
        if (areAllBricksDestroyed()) {
            stateManager.setState(GameState.LEVEL_COMPLETE);
        }
    }
}
```

### Ví dụ 4: State-aware rendering
```java
public class GameRenderer {
    private StateManager stateManager;
    
    public void render(GraphicsContext gc) {
        GameState state = stateManager.getState();
        
        switch (state) {
            case MENU:
                renderMenu(gc);
                break;
                
            case PLAYING:
                // Render game objects
                renderBorder(gc);
                renderBricks(gc);
                renderPaddle(gc);
                renderBalls(gc);
                renderPowerUps(gc);
                renderLasers(gc);
                renderUI(gc); // Lives, Score, Round
                break;
                
            case PAUSED:
                // Render game ở dưới (frozen state)
                renderBorder(gc);
                renderBricks(gc);
                renderPaddle(gc);
                renderBalls(gc);
                renderUI(gc);
                
                // Render pause overlay ở trên
                renderPauseOverlay(gc);
                break;
                
            case LEVEL_COMPLETE:
                renderBorder(gc);
                renderUI(gc);
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

### Ví dụ 5: Testing state transitions
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StateManagerTest {
    @Test
    void testInitialState() {
        StateManager sm = new StateManager();
        assertEquals(GameState.MENU, sm.getState());
        assertNull(sm.getPreviousState());
    }
    
    @Test
    void testValidTransition() {
        StateManager sm = new StateManager();
        
        boolean success = sm.setState(GameState.PLAYING);
        assertTrue(success);
        assertEquals(GameState.PLAYING, sm.getState());
        assertEquals(GameState.MENU, sm.getPreviousState());
    }
    
    @Test
    void testInvalidTransition() {
        StateManager sm = new StateManager();
        sm.setState(GameState.PLAYING);
        
        // Không thể từ PLAYING → MENU trực tiếp
        boolean success = sm.setState(GameState.MENU);
        assertFalse(success);
        assertEquals(GameState.PLAYING, sm.getState()); // Vẫn PLAYING
    }
    
    @Test
    void testPauseResumeFlow() {
        StateManager sm = new StateManager();
        
        sm.setState(GameState.PLAYING);
        sm.setState(GameState.PAUSED);
        assertEquals(GameState.PAUSED, sm.getState());
        assertEquals(GameState.PLAYING, sm.getPreviousState());
        
        sm.setState(GameState.PLAYING);
        assertEquals(GameState.PLAYING, sm.getState());
        assertEquals(GameState.PAUSED, sm.getPreviousState());
    }
    
    @Test
    void testCanTransitionTo() {
        StateManager sm = new StateManager();
        
        assertTrue(sm.canTransitionTo(GameState.MENU, GameState.PLAYING));
        assertTrue(sm.canTransitionTo(GameState.PLAYING, GameState.PAUSED));
        assertFalse(sm.canTransitionTo(GameState.PLAYING, GameState.MENU));
        assertFalse(sm.canTransitionTo(GameState.GAME_OVER, GameState.PLAYING));
    }
}
```

---

## Best Practices

### 1. Luôn dùng setState() để chuyển state
```java
// ✅ ĐÚNG: Dùng setState() để transition
stateManager.setState(GameState.PAUSED);

// ❌ SAI: Trực tiếp gán (bỏ qua validation và callbacks)
stateManager.currentState = GameState.PAUSED; // Không compile (private)
```

### 2. Kiểm tra return value của setState()
```java
// ✅ ĐÚNG: Xử lý transition failed
if (!stateManager.setState(GameState.PLAYING)) {
    System.err.println("Cannot start game");
    showErrorDialog("Please return to menu first");
}

// ❌ SAI: Giả định luôn thành công
stateManager.setState(GameState.PLAYING);
// Không biết nếu transition bị reject
```

### 3. Dùng helper methods thay vì so sánh trực tiếp
```java
// ✅ ĐÚNG: Dùng isPlaying(), isPaused()...
if (stateManager.isPlaying()) {
    updateGameplay(deltaTime);
}

// ⚠️ OK nhưng verbose:
if (stateManager.getState() == GameState.PLAYING) {
    updateGameplay(deltaTime);
}
```

### 4. Validate transitions trước UI interactions
```java
// ✅ ĐÚNG: Disable button nếu không thể transition
public void updateUI() {
    boolean canResume = stateManager.canTransitionTo(
        stateManager.getState(), 
        GameState.PLAYING
    );
    resumeButton.setDisable(!canResume);
}

// ❌ SAI: Luôn enable button
resumeButton.setDisable(false); // User có thể click vào invalid state
```

### 5. Sử dụng previousState cho context-aware logic
```java
// ✅ ĐÚNG: Khác nhau giữa resume và start mới
private void onStateEnter(GameState state) {
    if (state == GameState.PLAYING) {
        if (previousState == GameState.PAUSED) {
            audioManager.resumeMusic(); // Resume
        } else {
            audioManager.playMusic(MusicTrack.ROUNDS); // Start mới
        }
    }
}

// ❌ SAI: Luôn start mới
private void onStateEnter(GameState state) {
    if (state == GameState.PLAYING) {
        audioManager.playMusic(MusicTrack.ROUNDS); // Mất nhạc từ pause
    }
}
```

### 6. Centralized state management
```java
// ✅ ĐÚNG: StateManager là single source of truth
GameState state = stateManager.getState();

// ❌ SAI: Tự quản lý state flags
private boolean isPaused; // Duplicate với StateManager!
private boolean isGameOver;
```

---

## Dependencies

### Imports
```java
import Audio.MusicTrack;        // Enum các track nhạc
import java.util.EnumSet;       // Efficient set for enums
import java.util.HashMap;       // Map cho transition rules
import java.util.Map;
import java.util.Set;
```

### Các lớp phụ thuộc

| Lớp | Vai trò | Phương thức sử dụng |
|-----|---------|---------------------|
| `GameState` | Enum định nghĩa các trạng thái | Sử dụng làm key/value trong Map, so sánh equality |
| `AudioManager` | Quản lý nhạc nền | `getInstance()`, `playMusic()`, `pauseMusic()`, `resumeMusic()` |
| `MusicTrack` | Enum các track nhạc | `MENU`, `ROUNDS`, `GAME_OVER`, `VICTORY` |

### Được sử dụng bởi:
- `GameManager` - Kiểm tra state để update logic
- `GameRenderer` - Render UI khác nhau theo state
- `InputHandler` - Xử lý input khác nhau theo state
- `Screen` implementations - Navigate giữa các screens
- `ArkanoidApp` - Điều khiển flow của toàn bộ app

### Kiến trúc phụ thuộc
```
┌──────────────────────────────────┐
│       StateManager               │
│  - currentState: GameState       │
│  - previousState: GameState      │
│  - validTransitions: Map         │
│  - audioManager: AudioManager    │
└────────┬─────────────────────────┘
         │
         ├──→ GameState (enum values)
         │
         ├──→ AudioManager (singleton)
         │    └──→ MusicTrack (enum)
         │
         └──→ EnumSet, HashMap (Java collections)

Used by:
    ├──→ GameManager (update logic)
    ├──→ GameRenderer (render)
    ├──→ InputHandler (input handling)
    └──→ Screen implementations
```

---

## Design Pattern: State Pattern

StateManager implement **State Pattern** với một số modification:

### Classic State Pattern (OOP)
```
┌─────────────┐
│   Context   │
│ - state     │───┐
└─────────────┘   │
                  │ has-a
                  ↓
           ┌──────────────┐
           │ State (ABC)  │
           │ + handle()   │
           └──────┬───────┘
                  │ extends
      ┌───────────┼───────────┐
      ↓           ↓           ↓
┌─────────┐ ┌─────────┐ ┌─────────┐
│ StateA  │ │ StateB  │ │ StateC  │
└─────────┘ └─────────┘ └─────────┘
```

### Simplified State Pattern (Enum-based)
```
┌──────────────────┐
│  StateManager    │ ─── uses ───> GameState (enum)
│  - currentState  │                    ├─ MENU
│  - transition()  │                    ├─ PLAYING
└──────────────────┘                    ├─ PAUSED
                                        ├─ LEVEL_COMPLETE
                                        ├─ GAME_OVER
                                        └─ WIN
```

**Ưu điểm của Enum-based approach:**
- ✅ Đơn giản hơn (không cần class hierarchy)
- ✅ Type-safe (compile-time checking)
- ✅ Switch statement exhaustiveness check
- ✅ Dễ serialize (enum values có toString/valueOf)

**Nhược điểm:**
- ❌ Ít flexible (không thể override behavior per state)
- ❌ onStateEnter/onStateExit phình to với nhiều states

**Trade-off:** Phù hợp cho game nhỏ với số state ít (< 10)

---

## Mở rộng trong tương lai

### 1. State transition events/callbacks
```java
public interface StateTransitionListener {
    void onTransition(GameState from, GameState to);
}

public class StateManager {
    private List<StateTransitionListener> listeners = new ArrayList<>();
    
    public void addListener(StateTransitionListener listener) {
        listeners.add(listener);
    }
    
    private void notifyTransition(GameState from, GameState to) {
        for (StateTransitionListener listener : listeners) {
            listener.onTransition(from, to);
        }
    }
}

// Usage:
stateManager.addListener((from, to) -> {
    System.out.println("Transitioned: " + from + " → " + to);
    analyticsManager.trackStateChange(from, to);
});
```

### 2. State history tracking
```java
public class StateManager {
    private Stack<GameState> stateHistory = new Stack<>();
    private int maxHistorySize = 10;
    
    public void setState(GameState newState) {
        // ... existing logic
        
        stateHistory.push(currentState);
        if (stateHistory.size() > maxHistorySize) {
            stateHistory.remove(0); // Remove oldest
        }
    }
    
    public List<GameState> getStateHistory() {
        return new ArrayList<>(stateHistory);
    }
    
    public void printStateHistory() {
        System.out.println("State History:");
        for (int i = 0; i < stateHistory.size(); i++) {
            System.out.println((i+1) + ". " + stateHistory.get(i));
        }
    }
}
```

### 3. State duration tracking
```java
public class StateManager {
    private Map<GameState, Long> stateDurations = new HashMap<>();
    private long stateEnterTime;
    
    private void onStateEnter(GameState state) {
        stateEnterTime = System.currentTimeMillis();
        // ... existing logic
    }
    
    private void onStateExit(GameState state) {
        long duration = System.currentTimeMillis() - stateEnterTime;
        stateDurations.merge(state, duration, Long::sum);
        // ... existing logic
    }
    
    public long getTotalTimeInState(GameState state) {
        return stateDurations.getOrDefault(state, 0L);
    }
    
    public void printStateStatistics() {
        System.out.println("State Statistics:");
        for (GameState state : GameState.values()) {
            long ms = getTotalTimeInState(state);
            System.out.printf("%s: %.2f seconds%n", state, ms / 1000.0);
        }
    }
}
```

### 4. Conditional transitions
```java
public class StateManager {
    // Thêm điều kiện cho transitions
    public boolean setState(GameState newState, Object context) {
        if (!canTransitionTo(currentState, newState, context)) {
            return false;
        }
        // ... existing logic
    }
    
    private boolean canTransitionTo(GameState from, GameState to, Object context) {
        // Check basic rules
        if (!canTransitionTo(from, to)) {
            return false;
        }
        
        // Additional context-based checks
        if (to == GameState.PLAYING && context instanceof Integer) {
            int lives = (Integer) context;
            if (lives <= 0) {
                System.err.println("Cannot start game with 0 lives");
                return false;
            }
        }
        
        return true;
    }
}

// Usage:
stateManager.setState(GameState.PLAYING, lives); // Chỉ start nếu lives > 0
```

### 5. Sub-states
```java
public class StateManager {
    private GameState currentState;
    private Enum<?> currentSubState; // Sub-state cho state hiện tại
    
    public void setSubState(Enum<?> subState) {
        this.currentSubState = subState;
    }
    
    public Enum<?> getSubState() {
        return currentSubState;
    }
}

// Define sub-states
public enum PlayingSubState {
    BALL_ACTIVE,
    BALL_STUCK,     // Bóng dính vào paddle (catch mode)
    LASER_MODE,     // Đang bắn laser
    BONUS_ROUND     // Vòng bonus
}

// Usage:
if (stateManager.getState() == GameState.PLAYING) {
    if (stateManager.getSubState() == PlayingSubState.LASER_MODE) {
        renderLaserUI(gc);
    }
}
```

### 6. State save/load
```java
public class StateManager {
    public String serializeState() {
        JSONObject json = new JSONObject();
        json.put("currentState", currentState.name());
        json.put("previousState", previousState != null ? previousState.name() : null);
        return json.toString();
    }
    
    public void deserializeState(String json) {
        JSONObject obj = new JSONObject(json);
        String current = obj.getString("currentState");
        String previous = obj.optString("previousState", null);
        
        this.currentState = GameState.valueOf(current);
        this.previousState = previous != null ? GameState.valueOf(previous) : null;
        
        onStateEnter(currentState); // Trigger enter actions
    }
}

// Usage: Save/Load game
String savedState = stateManager.serializeState();
FileManager.saveToFile("savegame.json", savedState);

// Later...
String loaded = FileManager.loadFromFile("savegame.json");
stateManager.deserializeState(loaded);
```

---

## Tổng kết

`StateManager` là lớp quan trọng nhất trong game architecture:
- ✅ **Centralized:** Single source of truth cho game state
- ✅ **Validated:** Enforces valid transitions với transition rules
- ✅ **Organized:** Tách biệt logic cho từng state (onEnter/onExit)
- ✅ **Maintainable:** Dễ debug và extend (add new states/transitions)
- ✅ **Integrated:** Tích hợp chặt chẽ với AudioManager
- ✅ **Testable:** Dễ dàng unit test state transitions

Kết hợp với `GameState` enum, tạo nên một state machine robust, type-safe và dễ maintain cho toàn bộ game flow.

---

**Tác giả:** Arkanoid Development Team  
**Phiên bản:** 1.0  
**Cập nhật lần cuối:** 2024
