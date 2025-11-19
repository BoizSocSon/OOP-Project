# Class Animation - Hệ Thống Quản Lý Hoạt Ảnh (Animation System)

## 📋 Mục Lục

1. [Tổng Quan](#tổng-quan)
2. [Kiến Trúc](#kiến-trúc)
3. [Enum AnimationMode](#enum-animationmode)
4. [Thuộc Tính (Fields)](#thuộc-tính-fields)
5. [Constructor](#constructor)
6. [Phương Thức Chính](#phương-thức-chính)
7. [Ví Dụ Sử Dụng](#ví-dụ-sử-dụng)
8. [Design Pattern](#design-pattern)
9. [Xử Lý Animation Timing](#xử-lý-animation-timing)
10. [Best Practices](#best-practices)
11. [Ứng Dụng Trong Game](#ứng-dụng-trong-game)

---

## Tổng Quan

### Khái Niệm

**Animation** là lớp cốt lõi quản lý **hoạt ảnh** (animation) trong game Arkanoid. Nó chuyển đổi một **chuỗi các khung hình** (frames) thành **chuyển động mượt mà** dựa trên **thời gian** (time-based animation).

### Mục Đích

```
┌─────────────────────────────────────────────────────┐
│              Animation System Purpose                │
├─────────────────────────────────────────────────────┤
│  🎬 Frame Management    → Quản lý chuỗi khung hình  │
│  ⏱️  Timing Control     → Kiểm soát thời gian       │
│  🔁 Loop/Once Modes     → Chế độ lặp/một lần        │
│  ⏯️  Playback Control   → Điều khiển play/pause     │
│  ⏪ Reverse Animation   → Hoạt ảnh ngược            │
└─────────────────────────────────────────────────────┘
```

### Vai Trò Trong Game

| Thực Thể | Cách Sử Dụng Animation |
|----------|------------------------|
| **Paddle** | Hiệu ứng chuyển đổi trạng thái (normal → wide → laser) |
| **SilverBrick** | Animation vết nứt khi bị đánh |
| **PowerUp** | Hoạt ảnh rơi xuống (quay vòng/phát sáng) |

---

## Kiến Trúc

### Sơ Đồ UML

```
┌──────────────────────────────────────────────────────┐
│                    Animation                          │
├──────────────────────────────────────────────────────┤
│ - frames: List<Image>                                │
│ - currentFrameIndex: int                             │
│ - frameDuration: long                                │
│ - lastFrameTime: long                                │
│ - loop: boolean                                      │
│ - playing: boolean                                   │
│ - mode: AnimationMode                                │
│ - reversed: boolean                                  │
├──────────────────────────────────────────────────────┤
│ + Animation(frames, duration, loop)                  │
│ + update(): void                                     │
│ + play(): void                                       │
│ + playReversed(): void                               │
│ + pause(): void                                      │
│ + stop(): void                                       │
│ + reset(): void                                      │
│ + isFinished(): boolean                              │
│ + getCurrentFrame(): Image                           │
│ + isPlaying(): boolean                               │
│ + getFrameCount(): int                               │
│ + isReversed(): boolean                              │
│ + setReversed(reversed): void                        │
└──────────────────────────────────────────────────────┘
                      │
                      │ contains
                      ▼
            ┌─────────────────┐
            │  AnimationMode  │
            ├─────────────────┤
            │ LOOP            │
            │ ONCE            │
            └─────────────────┘
```

### Luồng Xử Lý Animation

```
┌─────────┐
│  START  │
└────┬────┘
     │
     ▼
┌─────────────────┐
│  Constructor:   │
│  - Load frames  │
│  - Set duration │
│  - Set mode     │
└────┬────────────┘
     │
     ▼
┌─────────────────┐      NO
│  play() called? ├──────────────┐
└────┬────────────┘              │
     │ YES                        │
     ▼                            │
┌─────────────────┐              │
│ playing = true  │              │
│ Start timer     │              │
└────┬────────────┘              │
     │                            │
     │ ┌──────────────────────┐  │
     └─┤  Game Loop (60 FPS)  ├──┘
       └──────┬───────────────┘
              │
              ▼
       ┌──────────────┐
       │   update()   │
       └──────┬───────┘
              │
              ▼
       ┌─────────────────────────┐
       │ currentTime - lastTime  │
       │    >= frameDuration?    │
       └──────┬─────────┬────────┘
              │ YES     │ NO
              │         └────────────┐
              ▼                      │
       ┌──────────────┐              │
       │ Next Frame:  │              │
       │ index++/--   │              │
       └──────┬───────┘              │
              │                      │
              ▼                      │
       ┌─────────────────┐           │
       │ Check boundary: │           │
       │ - LOOP mode?    │           │
       │ - ONCE mode?    │           │
       │ - Reversed?     │           │
       └──────┬──────────┘           │
              │                      │
              ▼                      │
       ┌──────────────┐              │
       │ Wrap/Stop    │              │
       │ accordingly  │              │
       └──────┬───────┘              │
              │                      │
              └──────────────────────┘
                      │
                      ▼
              ┌───────────────┐
              │ getCurrentFrame()│
              │ returns Image   │
              └───────────────┘
```

---

## Enum AnimationMode

### Định Nghĩa

```java
public enum AnimationMode {
    // Hoạt ảnh sẽ lặp lại vô tận
    LOOP,
    // Hoạt ảnh sẽ chỉ chạy một lần rồi dừng
    ONCE;
}
```

### So Sánh Hai Chế Độ

| Đặc Điểm | LOOP | ONCE |
|----------|------|------|
| **Hành Vi** | Lặp lại vô tận | Chạy một lần rồi dừng |
| **Khi Đến Frame Cuối** | Quay lại frame đầu | Dừng lại ở frame cuối |
| **Cờ playing** | Luôn = true | Tự động = false khi kết thúc |
| **isFinished()** | Luôn = false | = true khi kết thúc |
| **Ví Dụ** | PowerUp rơi (quay vòng), Paddle pulsate | Paddle expand/shrink, Brick crack |

### Minh Họa

```
LOOP Mode:
Frame: 0 → 1 → 2 → 3 → 4 → 0 → 1 → 2 → ... (vô tận)

ONCE Mode:
Frame: 0 → 1 → 2 → 3 → 4 → [STOP]
                           playing = false
                           isFinished() = true
```

---

## Thuộc Tính (Fields)

### 1. frames: List\<Image\>

```java
private final List<Image> frames;
```

- **Mô Tả**: Danh sách các khung hình tạo nên hoạt ảnh
- **Tính Chất**: `final` - không thể thay đổi sau khi khởi tạo
- **Ví Dụ**: `[paddle_0.png, paddle_1.png, paddle_2.png]`

### 2. currentFrameIndex: int

```java
private int currentFrameIndex;
```

- **Mô Tả**: Chỉ số của khung hình hiện tại đang hiển thị
- **Giá Trị**: `0` đến `frames.size() - 1`
- **Cập Nhật**: Tăng/giảm trong `update()`

### 3. frameDuration: long

```java
private long frameDuration;
```

- **Mô Tả**: Thời gian hiển thị mỗi khung hình (đơn vị: milliseconds)
- **Ví Dụ**: 
  - `100` ms = 10 FPS
  - `50` ms = 20 FPS
  - `16` ms ≈ 60 FPS

### 4. lastFrameTime: long

```java
private long lastFrameTime;
```

- **Mô Tả**: Timestamp (mili giây) của lần cập nhật khung hình cuối cùng
- **Sử Dụng**: So sánh với `System.currentTimeMillis()` để biết khi nào chuyển frame

### 5. loop: boolean

```java
private boolean loop;
```

- **Mô Tả**: Cờ báo hiệu animation có lặp lại không (deprecated)
- **Lưu Ý**: Vẫn được giữ để tương thích ngược, nhưng nên dùng `mode` thay thế
- **Quan Hệ**: `loop = true` → `mode = LOOP`, `loop = false` → `mode = ONCE`

### 6. playing: boolean

```java
private boolean playing;
```

- **Mô Tả**: Cờ báo hiệu animation có đang chạy không
- **Giá Trị**: 
  - `true`: Animation đang chạy, `update()` sẽ chuyển frame
  - `false`: Animation tạm dừng/dừng, `update()` không làm gì

### 7. mode: AnimationMode

```java
private AnimationMode mode;
```

- **Mô Tả**: Chế độ hoạt ảnh (LOOP hoặc ONCE)
- **Khởi Tạo**: Dựa vào giá trị `loop` trong constructor

### 8. reversed: boolean

```java
private boolean reversed;
```

- **Mô Tả**: Cờ báo hiệu animation có đang chạy ngược không
- **Giá Trị**:
  - `false`: Chạy xuôi (0 → 1 → 2 → ... → N)
  - `true`: Chạy ngược (N → ... → 2 → 1 → 0)

---

## Constructor

### Chữ Ký

```java
public Animation(List<Image> frames, long frameDuration, boolean loop)
```

### Tham Số

| Tham Số | Kiểu | Mô Tả |
|---------|------|-------|
| `frames` | `List<Image>` | Danh sách các khung hình (không được null/rỗng) |
| `frameDuration` | `long` | Thời gian hiển thị mỗi frame (ms) |
| `loop` | `boolean` | `true` = LOOP mode, `false` = ONCE mode |

### Validation

```java
this.frames = Objects.requireNonNull(frames, "Frames list cannot be null");
if (frames.isEmpty()) {
    throw new IllegalArgumentException("Frames list cannot be empty");
}
```

- ✅ **Kiểm Tra Null**: Sử dụng `Objects.requireNonNull()` với message rõ ràng
- ✅ **Kiểm Tra Empty**: Đảm bảo có ít nhất 1 frame

### Khởi Tạo Mặc Định

```java
this.currentFrameIndex = 0;          // Bắt đầu từ frame đầu tiên
this.playing = false;                // Không tự động chạy
this.lastFrameTime = 0;              // Chưa có timestamp
this.reversed = false;               // Chạy xuôi
this.mode = loop ? AnimationMode.LOOP : AnimationMode.ONCE;
```

### Ví Dụ Khởi Tạo

```java
// 1. Animation lặp vô tận (PowerUp)
List<Image> powerUpFrames = Arrays.asList(
    new Image("powerup_0.png"),
    new Image("powerup_1.png"),
    new Image("powerup_2.png")
);
Animation powerUpAnim = new Animation(powerUpFrames, 100, true); // LOOP, 100ms/frame

// 2. Animation chạy một lần (Paddle expand)
List<Image> expandFrames = AnimationFactory.createPaddleExpandAnimation();
Animation expandAnim = new Animation(expandFrames, 50, false); // ONCE, 50ms/frame

// 3. Xử lý lỗi
try {
    Animation invalid = new Animation(null, 100, true); // ❌ NullPointerException
} catch (NullPointerException e) {
    System.err.println("Frames cannot be null!");
}

try {
    Animation empty = new Animation(new ArrayList<>(), 100, true); // ❌ IllegalArgumentException
} catch (IllegalArgumentException e) {
    System.err.println("Frames cannot be empty!");
}
```

---

## Phương Thức Chính

### 1. update()

#### Mục Đích
Cập nhật trạng thái animation, chuyển sang frame tiếp theo nếu đủ thời gian.

#### Thuật Toán

```java
public void update() {
    if (!playing) {
        return; // Không làm gì nếu không đang chạy
    }

    long currentTime = System.currentTimeMillis();
    // Kiểm tra xem đã đến lúc chuyển khung hình chưa
    if (currentTime - lastFrameTime >= frameDuration) {
        if (reversed) {
            // Chuyển ngược lại
            currentFrameIndex--;
            if (currentFrameIndex < 0) {
                if (loop) { // Nếu lặp, quay lại khung cuối
                    currentFrameIndex = frames.size() - 1;
                } else { // Nếu ONCE, dừng ở khung đầu tiên
                    currentFrameIndex = 0;
                    playing = false;
                }
            }
        } else {
            // Chuyển tiến lên
            currentFrameIndex++;
            if (currentFrameIndex >= frames.size()) {
                if (loop) { // Nếu lặp, quay lại khung đầu
                    currentFrameIndex = 0;
                } else { // Nếu ONCE, dừng ở khung cuối cùng
                    currentFrameIndex = frames.size() - 1;
                    playing = false;
                }
            }
        }
        lastFrameTime = currentTime;
    }
}
```

#### Logic Chi Tiết

```
┌─────────────────────────────────────┐
│  Kiểm Tra: playing == true?         │
└────────┬────────────────────────────┘
         │ YES
         ▼
┌─────────────────────────────────────┐
│  Tính Delta Time:                   │
│  deltaTime = currentTime - lastTime │
└────────┬────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│  Kiểm Tra: deltaTime >= duration?   │
└────┬────────────────────────────┬───┘
     │ YES                         │ NO
     ▼                             └──► Return (chưa đủ thời gian)
┌─────────────────────┐
│  Kiểm Tra: reversed?│
└────┬───────────┬────┘
     │ FALSE     │ TRUE
     ▼           ▼
┌─────────┐  ┌─────────┐
│ index++ │  │ index-- │
└────┬────┘  └────┬────┘
     │            │
     ▼            ▼
┌────────────┐ ┌────────────┐
│ Out of     │ │ Out of     │
│ bounds?    │ │ bounds?    │
└──┬───┬─────┘ └──┬───┬─────┘
   │YES│NO        │YES│NO
   ▼   └──►       ▼   └──►
┌──────────┐   ┌──────────┐
│ LOOP?    │   │ LOOP?    │
└─┬────┬───┘   └─┬────┬───┘
  │YES │NO       │YES │NO
  ▼    ▼         ▼    ▼
Wrap  Stop     Wrap  Stop
index=0       index  
              =N-1
```

#### Ví Dụ Sử Dụng

```java
// Game Loop
public void run() {
    while (gameRunning) {
        // 1. Update tất cả animations
        paddleAnimation.update();
        crackAnimation.update();
        powerUpAnimation.update();
        
        // 2. Render
        render();
        
        // 3. Sleep để đạt 60 FPS
        Thread.sleep(16);
    }
}
```

---

### 2. play()

#### Mục Đích
Bắt đầu hoặc tiếp tục chạy animation từ frame hiện tại.

#### Code

```java
public void play() {
    // Nếu đã kết thúc (chỉ áp dụng cho chế độ ONCE), đặt lại trước khi chạy
    if (isFinished()) {
        reset();
    }
    this.playing = true;
    // Đặt lại thời gian để khung hình đầu tiên xuất hiện ngay lập tức
    this.lastFrameTime = System.currentTimeMillis();
}
```

#### Hành Vi

| Trường Hợp | Hành Vi |
|-----------|---------|
| Animation đang pause | Tiếp tục chạy từ frame hiện tại |
| Animation đã kết thúc (ONCE mode) | Gọi `reset()` rồi chạy lại từ đầu |
| Animation đang chạy | Không có tác dụng gì |

#### Ví Dụ

```java
// Scenario 1: Bắt đầu animation
Animation anim = new Animation(frames, 100, true);
anim.play();
System.out.println(anim.isPlaying()); // true

// Scenario 2: Pause rồi tiếp tục
anim.pause();
System.out.println(anim.isPlaying()); // false
anim.play(); // Tiếp tục từ frame hiện tại
System.out.println(anim.isPlaying()); // true

// Scenario 3: Animation ONCE đã kết thúc
Animation onceAnim = new Animation(frames, 100, false);
onceAnim.play();
// ... sau khi kết thúc ...
System.out.println(onceAnim.isFinished()); // true
onceAnim.play(); // Reset + chạy lại từ đầu
```

---

### 3. playReversed()

#### Mục Đích
Chạy animation theo hướng ngược lại, bắt đầu từ frame cuối cùng.

#### Code

```java
public void playReversed() {
    this.reversed = true;
    this.currentFrameIndex = frames.size() - 1; // Bắt đầu từ khung cuối
    this.playing = true;
    this.lastFrameTime = System.currentTimeMillis();
}
```

#### Minh Họa

```
Normal Play:
Frame: 0 → 1 → 2 → 3 → 4

Reversed Play:
Frame: 4 → 3 → 2 → 1 → 0
```

#### Ứng Dụng

```java
// 1. Paddle shrink animation (đảo ngược của expand)
public void shrinkPaddle() {
    Animation shrinkAnim = paddleExpandAnim.copy(); // Giả sử có phương thức copy
    shrinkAnim.playReversed(); // Chạy ngược từ frame cuối
}

// 2. Door close animation (đảo ngược của open)
Animation doorOpen = new Animation(doorFrames, 50, false);
doorOpen.playReversed(); // Cửa đóng lại

// 3. Rewind effect
Animation rewindAnim = new Animation(frames, 100, true);
rewindAnim.playReversed(); // Chạy ngược vô tận
```

---

### 4. pause()

#### Mục Đích
Tạm dừng animation tại frame hiện tại.

#### Code

```java
public void pause() {
    this.playing = false;
}
```

#### Đặc Điểm

- ✅ Không thay đổi `currentFrameIndex`
- ✅ Không reset `lastFrameTime`
- ✅ Có thể tiếp tục bằng `play()`

#### Ví Dụ

```java
// Tạm dừng khi game pause
public void onGamePause() {
    allAnimations.forEach(Animation::pause);
}

// Tiếp tục khi game resume
public void onGameResume() {
    allAnimations.forEach(Animation::play);
}
```

---

### 5. stop()

#### Mục Đích
Dừng animation và reset về frame ban đầu.

#### Code

```java
public void stop() {
    this.playing = false;
    reset();
}
```

#### So Sánh pause() vs stop()

| Phương Thức | playing | currentFrameIndex | lastFrameTime |
|-------------|---------|-------------------|---------------|
| `pause()` | `false` | Giữ nguyên | Giữ nguyên |
| `stop()` | `false` | Reset về 0 (hoặc N-1 nếu reversed) | Reset |

#### Ví Dụ

```java
// Dừng hoàn toàn khi chuyển màn
public void onLevelComplete() {
    crackAnimation.stop(); // Reset về frame 0
    powerUpAnimation.stop();
}
```

---

### 6. reset()

#### Mục Đích
Đặt lại frame về vị trí ban đầu (không thay đổi trạng thái playing).

#### Code

```java
public void reset() {
    // Đặt lại index tùy thuộc vào hướng chạy
    this.currentFrameIndex = reversed ? frames.size() - 1 : 0;
    this.lastFrameTime = System.currentTimeMillis();
}
```

#### Hành Vi

| reversed | currentFrameIndex sau reset |
|----------|----------------------------|
| `false` | `0` (frame đầu tiên) |
| `true` | `frames.size() - 1` (frame cuối cùng) |

---

### 7. isFinished()

#### Mục Đích
Kiểm tra xem animation đã hoàn thành chưa (chỉ áp dụng cho ONCE mode).

#### Code

```java
public boolean isFinished() {
    if (reversed) {
        // Đã kết thúc nếu không lặp và chỉ số khung hình <= 0 (khung đầu)
        return !loop && currentFrameIndex <= 0;
    } else {
        // Đã kết thúc nếu không lặp và chỉ số khung hình đạt khung cuối
        return !loop && currentFrameIndex >= frames.size() - 1;
    }
}
```

#### Bảng Chân Trị

| loop | reversed | currentFrameIndex | isFinished() |
|------|----------|-------------------|-------------|
| `true` | `false` | bất kỳ | `false` (LOOP không bao giờ kết thúc) |
| `false` | `false` | `< frames.size()-1` | `false` |
| `false` | `false` | `== frames.size()-1` | `true` |
| `false` | `true` | `> 0` | `false` |
| `false` | `true` | `== 0` | `true` |

#### Ứng Dụng

```java
// Kiểm tra khi nào animation expand kết thúc
if (paddleExpandAnim.isFinished()) {
    // Chuyển sang trạng thái WIDE_PULSATE
    paddle.setState(PaddleState.WIDE_PULSATE);
}

// Xóa animation đã kết thúc
animations.removeIf(Animation::isFinished);
```

---

### 8. getCurrentFrame()

#### Mục Đích
Lấy đối tượng `Image` của frame hiện tại để render.

#### Code

```java
public Image getCurrentFrame() {
    return frames.get(currentFrameIndex);
}
```

#### Ứng Dụng

```java
// Trong SpriteRenderer
public void drawPaddle(Paddle paddle) {
    if (paddle.isAnimationPlaying()) {
        Animation animation = paddle.getAnimation();
        Image frame = animation.getCurrentFrame(); // 👈 Lấy frame hiện tại
        gc.drawImage(frame, paddle.getX(), paddle.getY());
    }
}
```

---

### 9. Các Getter/Setter Khác

#### isPlaying()

```java
public boolean isPlaying() {
    return playing;
}
```

- Kiểm tra xem animation có đang chạy không

#### getFrameCount()

```java
public int getFrameCount() {
    return frames.size();
}
```

- Lấy tổng số frame trong animation

#### isReversed()

```java
public boolean isReversed() {
    return reversed;
}
```

- Kiểm tra animation có đang chạy ngược không

#### setReversed(boolean reversed)

```java
public void setReversed(boolean reversed) {
    this.reversed = reversed;
}
```

- Thay đổi hướng chạy của animation (không reset frame)

---

## Ví Dụ Sử Dụng

### 1. PowerUp Rơi (LOOP Mode)

```java
public class PowerUp extends MovableObject {
    private Animation animation;
    
    public PowerUp(double x, double y) {
        super(x, y, 40, 40);
        
        // Tải 4 frame quay vòng
        List<Image> frames = Arrays.asList(
            new Image("powerup_red_0.png"),
            new Image("powerup_red_1.png"),
            new Image("powerup_red_2.png"),
            new Image("powerup_red_3.png")
        );
        
        // Animation lặp vô tận, 80ms mỗi frame
        this.animation = new Animation(frames, 80, true);
        this.animation.play(); // Bắt đầu ngay
    }
    
    @Override
    public void update(long deltaTime) {
        super.update(deltaTime); // Di chuyển xuống
        animation.update(); // Cập nhật animation
    }
    
    public Image getCurrentSprite() {
        return animation.getCurrentFrame();
    }
}
```

**Output**:
```
Frame 0 → Frame 1 → Frame 2 → Frame 3 → Frame 0 → ... (vô tận)
```

---

### 2. Paddle Expand (ONCE Mode)

```java
public class Paddle {
    private Animation expandAnimation;
    private Animation shrinkAnimation;
    private PaddleState state;
    
    public void activateExpandPowerUp() {
        // Tạo animation expand (normal → wide)
        List<Image> expandFrames = AnimationFactory.createPaddleExpandAnimation();
        expandAnimation = new Animation(expandFrames, 50, false); // ONCE mode
        expandAnimation.play();
        
        state = PaddleState.EXPANDING;
    }
    
    public void update(long deltaTime) {
        if (state == PaddleState.EXPANDING) {
            expandAnimation.update();
            
            // Kiểm tra khi nào animation kết thúc
            if (expandAnimation.isFinished()) {
                state = PaddleState.WIDE;
                width = Constants.Paddle.PADDLE_WIDE_WIDTH; // Cập nhật kích thước
            }
        }
    }
    
    public void deactivateExpandPowerUp() {
        // Chạy animation ngược để thu nhỏ
        List<Image> shrinkFrames = AnimationFactory.createPaddleExpandAnimation();
        shrinkAnimation = new Animation(shrinkFrames, 50, false);
        shrinkAnimation.playReversed(); // Chạy ngược
        
        state = PaddleState.SHRINKING;
    }
}
```

**Timeline**:
```
t=0ms:   Frame 0 (paddle_normal.png)
t=50ms:  Frame 1
t=100ms: Frame 2
t=150ms: Frame 3
t=200ms: Frame 4 (paddle_wide.png)
t=250ms: isFinished() = true, state = WIDE
```

---

### 3. SilverBrick Crack Animation

```java
public class SilverBrick extends Brick {
    private int hitsRemaining = 2;
    private Animation crackAnimation;
    private boolean crackAnimationPlaying = false;
    
    public SilverBrick(double x, double y) {
        super(x, y, BrickType.SILVER);
        
        // Tải animation vết nứt
        List<Image> crackFrames = AnimationFactory.createSilverBrickCrackAnimation();
        crackAnimation = new Animation(crackFrames, 60, false); // ONCE, 60ms/frame
    }
    
    @Override
    public void onHit() {
        hitsRemaining--;
        
        if (hitsRemaining == 1) {
            // Lần đánh đầu tiên: Phát animation nứt
            crackAnimation.play();
            crackAnimationPlaying = true;
        } else if (hitsRemaining == 0) {
            // Lần đánh thứ hai: Phá hủy gạch
            destroyed = true;
        }
    }
    
    @Override
    public void update(long deltaTime) {
        if (crackAnimationPlaying) {
            crackAnimation.update();
            
            // Khi animation kết thúc, giữ nguyên frame cuối (vết nứt rõ ràng)
            if (crackAnimation.isFinished()) {
                crackAnimationPlaying = false;
            }
        }
    }
    
    public Image getCrackFrame() {
        return crackAnimation.getCurrentFrame();
    }
}
```

**Render Logic**:
```java
// Trong SpriteRenderer.drawBrick()
if (brick instanceof SilverBrick) {
    SilverBrick silverBrick = (SilverBrick) brick;
    
    // Vẽ sprite gạch bạc làm nền
    gc.drawImage(sprites.get("brick_silver.png"), x, y, w, h);
    
    // Nếu đang có animation nứt, vẽ đè lên
    if (silverBrick.isCrackAnimationPlaying()) {
        Image crackFrame = silverBrick.getCrackFrame();
        gc.drawImage(crackFrame, x, y, w, h); // Overlay vết nứt
    }
}
```

---

### 4. Animation Manager (Quản Lý Nhiều Animation)

```java
public class AnimationManager {
    private List<Animation> animations = new ArrayList<>();
    
    public void addAnimation(Animation animation) {
        animations.add(animation);
    }
    
    public void update() {
        // Cập nhật tất cả animations
        animations.forEach(Animation::update);
        
        // Xóa các animation đã kết thúc (ONCE mode)
        animations.removeIf(anim -> !anim.isPlaying() && anim.isFinished());
    }
    
    public void pauseAll() {
        animations.forEach(Animation::pause);
    }
    
    public void resumeAll() {
        animations.forEach(Animation::play);
    }
    
    public void stopAll() {
        animations.forEach(Animation::stop);
        animations.clear();
    }
}

// Sử dụng trong GameManager
public class GameManager {
    private AnimationManager animManager = new AnimationManager();
    
    public void onPowerUpActivated(PowerUp powerUp) {
        Animation activateAnim = AnimationFactory.createPowerUpActivateAnimation();
        activateAnim.play();
        animManager.addAnimation(activateAnim);
    }
    
    public void update(long deltaTime) {
        animManager.update(); // Cập nhật tất cả
    }
    
    public void onGamePause() {
        animManager.pauseAll();
    }
}
```

---

## Design Pattern

### 1. State Pattern

Animation sử dụng **State Pattern** ngầm định với cờ `playing`:

```
┌──────────────┐  play()   ┌──────────────┐
│   STOPPED    ├──────────►│   PLAYING    │
└──────────────┘           └──────┬───────┘
       ▲                          │
       │                          │ pause()
       │ stop()                   │
       │                          ▼
       │                   ┌──────────────┐
       └───────────────────┤   PAUSED     │
                           └──────────────┘
```

### 2. Iterator Pattern

Animation hoạt động như một **Iterator** trên list frames:

```java
// Tương đương với:
Iterator<Image> frameIterator = frames.iterator();
while (gameRunning) {
    if (shouldAdvance()) {
        if (frameIterator.hasNext()) {
            currentFrame = frameIterator.next();
        } else {
            if (loop) {
                frameIterator = frames.iterator(); // Reset
            }
        }
    }
    render(currentFrame);
}
```

### 3. Strategy Pattern

Hai chế độ LOOP/ONCE là hai **Strategy** khác nhau:

```java
// Strategy Interface
interface AnimationStrategy {
    int getNextFrameIndex(int current, int frameCount);
}

// LOOP Strategy
class LoopStrategy implements AnimationStrategy {
    public int getNextFrameIndex(int current, int frameCount) {
        return (current + 1) % frameCount; // Wrap around
    }
}

// ONCE Strategy
class OnceStrategy implements AnimationStrategy {
    public int getNextFrameIndex(int current, int frameCount) {
        return Math.min(current + 1, frameCount - 1); // Clamp
    }
}
```

---

## Xử Lý Animation Timing

### 1. Delta Time Approach

```java
// Animation hiện tại sử dụng absolute time
long currentTime = System.currentTimeMillis();
if (currentTime - lastFrameTime >= frameDuration) {
    nextFrame();
    lastFrameTime = currentTime;
}
```

**Ưu điểm**:
- ✅ Đơn giản
- ✅ Không phụ thuộc vào game loop speed

**Nhược điểm**:
- ❌ Có thể bị lag nếu `update()` không được gọi đủ nhanh
- ❌ Không smooth nếu deltaTime không đồng đều

### 2. Frame-Based Timing (Cải tiến)

```java
// Cải tiến: Sử dụng delta time từ game loop
private double frameTimer = 0;

public void update(double deltaTime) { // deltaTime in seconds
    if (!playing) return;
    
    frameTimer += deltaTime;
    double frameDurationSec = frameDuration / 1000.0;
    
    while (frameTimer >= frameDurationSec) {
        nextFrame();
        frameTimer -= frameDurationSec;
    }
}
```

**Ưu điểm**:
- ✅ Smooth hơn
- ✅ Không bỏ frame nếu lag tạm thời
- ✅ Dễ debug với deltaTime cố định

### 3. Interpolation (Nâng cao)

```java
public Image getCurrentFrame(double interpolation) {
    // interpolation = 0.0 - 1.0 (% giữa frame hiện tại và frame tiếp theo)
    int nextIndex = (currentFrameIndex + 1) % frames.size();
    
    if (interpolation < 0.5) {
        return frames.get(currentFrameIndex);
    } else {
        return frames.get(nextIndex);
    }
}
```

---

## Best Practices

### ✅ DO

#### 1. Sử Dụng AnimationMode Thay Vì loop

```java
// ❌ BAD: Sử dụng boolean loop (deprecated)
Animation anim = new Animation(frames, 100, true);

// ✅ GOOD: Tham chiếu rõ ràng đến mode
Animation anim = new Animation(frames, 100, true); // LOOP mode
if (anim.isFinished()) { // Chỉ có nghĩa với ONCE mode
    // ...
}
```

#### 2. Kiểm Tra isPlaying() Trước Khi Render

```java
// ✅ GOOD: Chỉ render khi animation đang chạy
if (animation.isPlaying()) {
    Image frame = animation.getCurrentFrame();
    gc.drawImage(frame, x, y);
}
```

#### 3. Reset Khi Play Lại Animation ONCE

```java
// ✅ GOOD: Animation tự động reset nếu đã kết thúc
animation.play(); // Tự gọi reset() nếu isFinished() == true

// Hoặc thủ công:
if (animation.isFinished()) {
    animation.reset();
}
animation.play();
```

#### 4. Sử Dụng AnimationFactory

```java
// ✅ GOOD: Tập trung logic tạo animation
public class AnimationFactory {
    public static Animation createPaddleExpandAnimation() {
        List<Image> frames = new ArrayList<>();
        for (int i = 0; i <= 4; i++) {
            frames.add(new Image("paddle_expand_" + i + ".png"));
        }
        return new Animation(frames, 50, false);
    }
}
```

### ❌ DON'T

#### 1. Không Quên Gọi update()

```java
// ❌ BAD: Chỉ play() mà không update()
animation.play();
// ... trong game loop không gọi animation.update()
Image frame = animation.getCurrentFrame(); // Sẽ luôn là frame 0!

// ✅ GOOD:
animation.play();
while (gameRunning) {
    animation.update(); // 👈 Phải gọi mỗi frame
    render();
}
```

#### 2. Không Nên Thay Đổi frameDuration Khi Đang Chạy

```java
// ❌ BAD: frameDuration là private, không có setter
// Nếu cần thay đổi speed, tạo animation mới

// ✅ GOOD:
Animation slowAnim = new Animation(frames, 200, true); // Chậm
Animation fastAnim = new Animation(frames, 50, true);  // Nhanh
```

#### 3. Không Nên Tái Sử Dụng Animation Cho Nhiều Đối Tượng

```java
// ❌ BAD: Một animation cho nhiều objects
Animation sharedAnim = new Animation(frames, 100, true);
powerUp1.setAnimation(sharedAnim);
powerUp2.setAnimation(sharedAnim); // Sẽ cùng frame!

// ✅ GOOD: Mỗi object có animation riêng
powerUp1.setAnimation(new Animation(frames, 100, true));
powerUp2.setAnimation(new Animation(frames, 100, true));
```

---

## Ứng Dụng Trong Game

### 1. Paddle State Transitions

```java
public enum PaddleState {
    NORMAL,
    EXPANDING,    // Animation đang chạy
    WIDE,
    SHRINKING,    // Animation ngược đang chạy
    LASER
}

public class Paddle {
    private PaddleState state = PaddleState.NORMAL;
    private Animation transitionAnimation;
    
    public void update(long deltaTime) {
        switch (state) {
            case EXPANDING:
                transitionAnimation.update();
                if (transitionAnimation.isFinished()) {
                    state = PaddleState.WIDE;
                    width = Constants.Paddle.PADDLE_WIDE_WIDTH;
                }
                break;
                
            case SHRINKING:
                transitionAnimation.update();
                if (transitionAnimation.isFinished()) {
                    state = PaddleState.NORMAL;
                    width = Constants.Paddle.PADDLE_NORMAL_WIDTH;
                }
                break;
        }
    }
}
```

### 2. PowerUp Visual Effects

```java
public class PowerUp extends MovableObject {
    private Animation idleAnimation;   // LOOP: Quay vòng khi rơi
    private Animation collectAnimation; // ONCE: Hiệu ứng khi thu thập
    private boolean collected = false;
    
    public void onCollected() {
        collected = true;
        idleAnimation.stop();
        collectAnimation.play(); // Phát hiệu ứng thu thập
    }
    
    public void update(long deltaTime) {
        if (!collected) {
            idleAnimation.update();
        } else {
            collectAnimation.update();
            if (collectAnimation.isFinished()) {
                active = false; // Xóa PowerUp sau khi animation kết thúc
            }
        }
    }
}
```

### 3. Brick Destruction Sequence

```java
public class ExplosiveBrick extends Brick {
    private Animation explosionAnimation;
    private boolean exploding = false;
    
    @Override
    public void onHit() {
        exploding = true;
        explosionAnimation.play(); // ONCE mode
    }
    
    @Override
    public void update(long deltaTime) {
        if (exploding) {
            explosionAnimation.update();
            if (explosionAnimation.isFinished()) {
                destroyed = true; // Xóa gạch sau khi nổ xong
            }
        }
    }
}
```

---

## Tổng Kết

### Điểm Mạnh

| Điểm Mạnh | Mô Tả |
|-----------|-------|
| ✅ **Đơn giản** | API rõ ràng với `play()`, `pause()`, `stop()` |
| ✅ **Linh hoạt** | Hỗ trợ LOOP/ONCE, reverse, pause/resume |
| ✅ **Hiệu quả** | Time-based animation, không tốn CPU khi pause |
| ✅ **An toàn** | Validation null/empty frames trong constructor |
| ✅ **Độc lập** | Không phụ thuộc vào game loop speed |

### Hạn Chế & Cải Tiến

| Hạn Chế | Cải Tiến Đề Xuất |
|---------|-----------------|
| ⚠️ Không có setter cho frameDuration | Thêm `setFrameDuration(long)` để thay đổi speed |
| ⚠️ Không hỗ trợ blend/interpolation | Thêm phương thức `getInterpolatedFrame(double alpha)` |
| ⚠️ Không có callback khi kết thúc | Thêm `setOnFinished(Runnable callback)` |
| ⚠️ Không có ping-pong mode | Thêm `AnimationMode.PING_PONG` |
| ⚠️ Không thể pause tại frame cụ thể | Thêm `playFromFrame(int frameIndex)` |

### Khi Nào Sử Dụng

| Trường Hợp | Nên Dùng |
|-----------|----------|
| Sprite animation (walk, run, idle) | ✅ Animation (LOOP) |
| State transition (expand/shrink) | ✅ Animation (ONCE) |
| Visual effects (explosion, sparkle) | ✅ Animation (ONCE) |
| Rotating objects | ✅ Animation (LOOP) hoặc rotation transform |
| Fading in/out | ❌ Dùng opacity transition thay vì animation |

---

## 📚 Tài Liệu Tham Khảo

- **JavaFX Image**: https://openjfx.io/javadoc/17/javafx.graphics/javafx/scene/image/Image.html
- **Game Programming Patterns - Update Method**: https://gameprogrammingpatterns.com/update-method.html
- **Frame-based Animation**: https://en.wikipedia.org/wiki/Computer_animation#Techniques
- **Delta Time**: https://gamedev.stackexchange.com/questions/15435/what-is-delta-time

---

**Tác Giả**: Animation System Documentation  
**Phiên Bản**: 1.0  
**Ngày Cập Nhật**: 2024
