# Screen

## Tổng quan
`Screen` là một interface (giao diện) đại diện cho một màn hình hoặc trạng thái (state) trong trò chơi Arkanoid. Mọi màn hình (ví dụ: Menu, GamePlay, GameOver, PauseScreen, v.v.) cần triển khai (implement) interface này để xử lý việc render, cập nhật logic và tương tác người dùng.

## Package
```java
package UI;
```

## Mục đích

Interface `Screen` tạo ra một contract (hợp đồng) chung cho tất cả các màn hình trong game, đảm bảo:
- **Tính nhất quán**: Mọi screen đều có cùng các phương thức cơ bản
- **Dễ quản lý**: StateManager có thể quản lý mọi screen thông qua interface chung
- **Dễ mở rộng**: Thêm screen mới chỉ cần implement interface này
- **Separation of Concerns**: Mỗi screen tự quản lý logic và render của riêng mình

## Các phương thức bắt buộc

### render(GraphicsContext gc)
Vẽ nội dung của màn hình lên GraphicsContext.

**Tham số:**
- **gc**: Context đồ họa để vẽ

**Mục đích:** Render tất cả các thành phần UI và game objects của màn hình

```java
@Override
public void render(GraphicsContext gc) {
    // Vẽ background
    gc.setFill(Color.BLACK);
    gc.fillRect(0, 0, width, height);
    
    // Vẽ các components khác
    // ...
}
```

### handleKeyPressed(KeyCode keyCode)
Xử lý sự kiện khi một phím được nhấn xuống.

**Tham số:**
- **keyCode**: Mã phím được nhấn (LEFT, RIGHT, SPACE, ESCAPE, v.v.)

**Mục đích:** Xử lý input từ bàn phím

```java
@Override
public void handleKeyPressed(KeyCode keyCode) {
    switch (keyCode) {
        case LEFT:
            paddle.moveLeft();
            break;
        case RIGHT:
            paddle.moveRight();
            break;
        case SPACE:
            ball.launch();
            break;
    }
}
```

### handleKeyReleased(KeyCode keyCode)
Xử lý sự kiện khi một phím được nhả ra.

**Tham số:**
- **keyCode**: Mã phím được nhả

**Mục đích:** Dừng các hành động liên tục khi phím được nhả

```java
@Override
public void handleKeyReleased(KeyCode keyCode) {
    if (keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT) {
        paddle.stop();
    }
}
```

### handleMouseClicked(MouseEvent event)
Xử lý sự kiện click chuột.

**Tham số:**
- **event**: Chi tiết sự kiện chuột (tọa độ, button được click, v.v.)

**Mục đích:** Xử lý click vào buttons, menu items, v.v.

```java
@Override
public void handleMouseClicked(MouseEvent event) {
    double x = event.getX();
    double y = event.getY();
    
    for (Button button : buttons) {
        if (button.contains(x, y)) {
            button.click();
        }
    }
}
```

### handleMouseMoved(MouseEvent event)
Xử lý sự kiện di chuyển chuột.

**Tham số:**
- **event**: Chi tiết sự kiện chuột

**Mục đích:** Xử lý hover effects, di chuyển paddle theo chuột, v.v.

```java
@Override
public void handleMouseMoved(MouseEvent event) {
    double mouseX = event.getX();
    double mouseY = event.getY();
    
    // Cập nhật trạng thái hover của buttons
    for (Button button : buttons) {
        button.setHovered(button.contains(mouseX, mouseY));
    }
    
    // Di chuyển paddle theo chuột
    paddle.setX(mouseX);
}
```

### update(long deltaTime)
Cập nhật logic màn hình theo thời gian.

**Tham số:**
- **deltaTime**: Thời gian đã trôi qua kể từ lần cập nhật trước (nanoseconds hoặc milliseconds)

**Mục đích:** Cập nhật game logic, animations, physics, v.v.

```java
@Override
public void update(long deltaTime) {
    // Cập nhật vị trí ball
    ball.update(deltaTime);
    
    // Cập nhật animation
    animations.update(deltaTime);
    
    // Kiểm tra va chạm
    collisionManager.checkCollisions();
}
```

### onEnter()
Được gọi khi màn hình được chuyển đến (bắt đầu hoạt động).

**Mục đích:** Khởi tạo hoặc reset trạng thái khi màn hình được hiển thị

```java
@Override
public void onEnter() {
    // Reset game state
    score = 0;
    lives = 3;
    
    // Load assets nếu cần
    loadAssets();
    
    // Start music
    audioManager.playMusic(MusicTrack.GAME_MUSIC);
}
```

### onExit()
Được gọi khi màn hình bị rời đi (dừng hoạt động).

**Mục đích:** Dọn dẹp tài nguyên, lưu dữ liệu khi màn hình bị tắt

```java
@Override
public void onExit() {
    // Dừng music
    audioManager.stopMusic();
    
    // Lưu high score
    saveHighScore();
    
    // Giải phóng tài nguyên
    cleanup();
}
```

## Cách triển khai (Implementation)

### Ví dụ triển khai cơ bản
```java
public class MainMenuScreen implements Screen {
    private List<Button> buttons;
    private Image background;
    private StateManager stateManager;
    
    public MainMenuScreen(StateManager stateManager) {
        this.stateManager = stateManager;
        this.buttons = new ArrayList<>();
        initializeButtons();
    }
    
    private void initializeButtons() {
        buttons.add(new Button(300, 300, 200, 50, "PLAY", () -> {
            stateManager.setGameState(GameState.PLAYING);
        }));
        buttons.add(new Button(300, 370, 200, 50, "OPTIONS", () -> {
            stateManager.setGameState(GameState.OPTIONS);
        }));
        buttons.add(new Button(300, 440, 200, 50, "EXIT", () -> {
            System.exit(0);
        }));
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Vẽ background
        if (background != null) {
            gc.drawImage(background, 0, 0);
        }
        
        // Vẽ title
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 48));
        gc.fillText("ARKANOID", 400, 150);
        
        // Vẽ buttons
        for (Button button : buttons) {
            button.render(gc);
        }
    }
    
    @Override
    public void handleKeyPressed(KeyCode keyCode) {
        if (keyCode == KeyCode.ESCAPE) {
            System.exit(0);
        }
    }
    
    @Override
    public void handleKeyReleased(KeyCode keyCode) {
        // Không cần xử lý trong menu
    }
    
    @Override
    public void handleMouseClicked(MouseEvent event) {
        for (Button button : buttons) {
            if (button.contains(event.getX(), event.getY())) {
                button.click();
            }
        }
    }
    
    @Override
    public void handleMouseMoved(MouseEvent event) {
        for (Button button : buttons) {
            button.setHovered(button.contains(event.getX(), event.getY()));
        }
    }
    
    @Override
    public void update(long deltaTime) {
        // Có thể cập nhật animations nếu có
    }
    
    @Override
    public void onEnter() {
        System.out.println("Entering Main Menu");
        // Load assets, reset states
    }
    
    @Override
    public void onExit() {
        System.out.println("Exiting Main Menu");
        // Cleanup
    }
}
```

## Sử dụng với StateManager

```java
// Trong StateManager
private Screen currentScreen;
private Map<GameState, Screen> screens;

public void setGameState(GameState newState) {
    // Exit old screen
    if (currentScreen != null) {
        currentScreen.onExit();
    }
    
    // Switch to new screen
    currentScreen = screens.get(newState);
    
    // Enter new screen
    if (currentScreen != null) {
        currentScreen.onEnter();
    }
}

// Trong game loop
public void render(GraphicsContext gc) {
    if (currentScreen != null) {
        currentScreen.render(gc);
    }
}

public void update(long deltaTime) {
    if (currentScreen != null) {
        currentScreen.update(deltaTime);
    }
}
```

## Các Screen phổ biến trong game

1. **MainMenuScreen**: Menu chính với các options (Play, Settings, Exit)
2. **GamePlayScreen**: Màn hình chơi game chính
3. **PauseScreen**: Màn hình tạm dừng
4. **GameOverScreen**: Màn hình kết thúc game
5. **HighScoreScreen**: Hiển thị bảng điểm cao
6. **OptionsScreen**: Cài đặt game
7. **LevelSelectScreen**: Chọn level
8. **CreditsScreen**: Thông tin credits

## Lợi ích của Interface Pattern

1. **Polymorphism**: Có thể xử lý tất cả screens thông qua interface chung
2. **Loose Coupling**: Screens không phụ thuộc vào nhau
3. **Testability**: Dễ dàng mock và test
4. **Maintainability**: Code dễ bảo trì và mở rộng
5. **Single Responsibility**: Mỗi screen chịu trách nhiệm riêng

## Dependencies
- `javafx.scene.canvas.GraphicsContext`: Để render
- `javafx.scene.input.KeyCode`: Để xử lý input phím
- `javafx.scene.input.MouseEvent`: Để xử lý input chuột
