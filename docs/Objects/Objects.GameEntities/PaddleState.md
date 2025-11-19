# PaddleState Enum

## Tổng quan
`PaddleState` là một enum định nghĩa tất cả các trạng thái hình ảnh và animation có thể có của Paddle trong game Arkanoid. Mỗi trạng thái chứa metadata cần thiết để tạo và quản lý animation tương ứng, bao gồm tiền tố tên sprite, số lượng frame, và cờ lặp (loop). Đây là nền tảng của hệ thống animation và quản lý trạng thái cho Paddle.

## Vị trí
- **Package**: `Objects.GameEntities`
- **File**: `src/Objects/GameEntities/PaddleState.java`
- **Loại**: Enum

## Mục đích
Enum PaddleState:
- Định nghĩa tất cả trạng thái visual của paddle
- Cung cấp metadata cho animation system
- Phân biệt animation transition (chạy một lần) và animation loop (lặp lại)
- Hỗ trợ AnimationFactory tạo animation phù hợp
- Quản lý trạng thái cảnh báo (pulsate) cho power-up

---

## Các Trạng Thái

### 1. `NORMAL`

```java
NORMAL("paddle", 1, false)
```

**Mô tả**: Trạng thái mặc định của paddle, không có animation đặc biệt.

**Đặc điểm**:
- **paddlePrefix**: `"paddle"` - Sử dụng sprite tĩnh `paddle.png`
- **frameCount**: `1` - Chỉ có 1 frame (không animation)
- **shouldLoop**: `false` - Không áp dụng (chỉ có 1 frame)

**Khi nào xuất hiện**:
- Khi game bắt đầu
- Khi tất cả power-up hết hạn
- Sau khi animation transition hoàn thành

**Visual**: Paddle thông thường, không có hiệu ứng đặc biệt

**Ví dụ**:
```java
// Khởi tạo paddle
Paddle paddle = new Paddle(x, y, width, height);
// paddle.currentState = PaddleState.NORMAL (mặc định)

// Quay về NORMAL sau khi power-up hết hạn
paddle.setState(PaddleState.NORMAL);
```

---

### 2. `WIDE`

```java
WIDE("paddle_wide", 9, false)
```

**Mô tả**: Trạng thái paddle mở rộng (Expand Power-Up).

**Đặc điểm**:
- **paddlePrefix**: `"paddle_wide"` 
- **Sprite files**: `paddle_wide_0.png`, `paddle_wide_1.png`, ..., `paddle_wide_8.png`
- **frameCount**: `9` - Animation có 9 frame
- **shouldLoop**: `false` - Chạy một lần (transition animation)

**Animation Type**: **Transition** - Chuyển đổi từ NORMAL sang WIDE

**Luồng animation**:
```
Frame 0: Paddle bắt đầu mở rộng
Frame 1-7: Paddle dần dần rộng hơn
Frame 8: Paddle đạt kích thước tối đa (final frame)
→ Animation kết thúc
→ Giữ nguyên frame 8 cho đến khi hết hạn
```

**Khi nào xuất hiện**:
- Khi thu thập Expand Power-Up
- Khi `paddle.expand()` được gọi

**Visual**: Paddle dần dần mở rộng từ kích thước bình thường (120px) lên kích thước lớn (180px)

**Ví dụ**:
```java
// Kích hoạt Expand
paddle.expand();
// → setState(WIDE)
// → AnimationFactory tạo animation 9 frame
// → Animation chạy một lần
// → Giữ frame cuối cùng
```

---

### 3. `WIDE_PULSATE`

```java
WIDE_PULSATE("paddle_wide_pulsate", 4, true)
```

**Mô tả**: Trạng thái cảnh báo cho WIDE sắp hết hạn.

**Đặc điểm**:
- **paddlePrefix**: `"paddle_wide_pulsate"`
- **Sprite files**: `paddle_wide_pulsate_0.png`, ..., `paddle_wide_pulsate_3.png`
- **frameCount**: `4` - Animation ngắn
- **shouldLoop**: `true` - Lặp liên tục (warning animation)

**Animation Type**: **Loop** - Nhấp nháy cảnh báo

**Luồng animation**:
```
Frame 0 → Frame 1 → Frame 2 → Frame 3 → Frame 0 → ...
(Lặp vô hạn cho đến khi hết hạn hoặc gia hạn)
```

**Khi nào xuất hiện**:
- Khi WIDE còn lại < 3 giây (WARNING_THRESHOLD)
- Tự động chuyển từ WIDE trong `paddle.update()`

**Visual**: Paddle rộng nhưng nhấp nháy/pulsate để cảnh báo người chơi sắp hết hiệu ứng

**Timeline Example**:
```
T = 0s: paddle.expand()
  → WIDE (9 frame transition)

T = 0-12s: WIDE
  → Giữ nguyên frame cuối

T = 12s: Còn 3s
  → WIDE_PULSATE (4 frame loop)
  → Nhấp nháy cảnh báo

T = 15s: Hết hạn
  → shrinkToNormal()
  → Chạy animation đảo ngược về NORMAL
```

---

### 4. `LASER`

```java
LASER("paddle_laser", 16, false)
```

**Mô tả**: Trạng thái paddle có khả năng bắn laser (Laser Power-Up).

**Đặc điểm**:
- **paddlePrefix**: `"paddle_laser"`
- **Sprite files**: `paddle_laser_0.png`, ..., `paddle_laser_15.png`
- **frameCount**: `16` - Animation dài (hiệu ứng phức tạp)
- **shouldLoop**: `false` - Chạy một lần (transition animation)

**Animation Type**: **Transition** - Súng laser xuất hiện trên paddle

**Luồng animation**:
```
Frame 0: Paddle bắt đầu biến đổi
Frame 1-14: Súng laser dần dần xuất hiện
Frame 15: Súng laser hoàn chỉnh (final frame)
→ Animation kết thúc
→ Giữ frame 15, paddle có thể bắn laser
```

**Khi nào xuất hiện**:
- Khi thu thập Laser Power-Up
- Khi `paddle.enableLaser()` được gọi

**Visual**: Hai súng laser xuất hiện ở hai đầu paddle, sẵn sàng bắn

**Ví dụ**:
```java
// Kích hoạt Laser
paddle.enableLaser();
// → setState(LASER)
// → Animation 16 frame (súng laser xuất hiện)
// → laserShots = 25
// → Có thể bắn laser

// Bắn laser
List<Laser> lasers = paddle.shootLaser();
// → Tạo 2 laser từ vị trí súng
```

---

### 5. `LASER_PULSATE`

```java
LASER_PULSATE("paddle_laser_pulsate", 4, true)
```

**Mô tả**: Trạng thái cảnh báo cho LASER sắp hết hạn.

**Đặc điểm**:
- **paddlePrefix**: `"paddle_laser_pulsate"`
- **Sprite files**: `paddle_laser_pulsate_0.png`, ..., `paddle_laser_pulsate_3.png`
- **frameCount**: `4` - Animation ngắn
- **shouldLoop**: `true` - Lặp liên tục (warning animation)

**Animation Type**: **Loop** - Nhấp nháy cảnh báo

**Khi nào xuất hiện**:
- Khi LASER còn lại < 3 giây
- Tự động chuyển từ LASER trong `paddle.update()`

**Visual**: Paddle có laser nhưng nhấp nháy cảnh báo sắp hết hiệu ứng

---

### 6. `PULSATE`

```java
PULSATE("paddle_pulsate", 4, true)
```

**Mô tả**: Trạng thái cảnh báo chung cho các hiệu ứng độc lập với hình dạng (Catch/Slow).

**Đặc điểm**:
- **paddlePrefix**: `"paddle_pulsate"`
- **Sprite files**: `paddle_pulsate_0.png`, ..., `paddle_pulsate_3.png`
- **frameCount**: `4` - Animation ngắn
- **shouldLoop**: `true` - Lặp liên tục

**Animation Type**: **Loop** - Nhấp nháy cảnh báo

**Khi nào xuất hiện**:
- Khi Catch hoặc Slow còn lại < 3 giây
- Chỉ xuất hiện khi paddle ở trạng thái NORMAL
- Tự động chuyển từ NORMAL trong `paddle.update()`

**Đặc biệt**: 
- Catch và Slow là **hiệu ứng độc lập** - không thay đổi hình dạng paddle
- PULSATE cảnh báo cho các hiệu ứng này trên paddle NORMAL
- WIDE/LASER có trạng thái pulsate riêng của chúng

**Visual**: Paddle bình thường nhưng nhấp nháy để báo hiệu Catch/Slow sắp hết

**Ví dụ**:
```java
// Paddle NORMAL + Catch active
paddle.enableCatch(); // catchMode = true, nhưng không đổi state
// → currentState = NORMAL (hình dạng không đổi)

// Sau 7 giây (còn 3s)
// → setState(PULSATE)
// → Cảnh báo Catch sắp hết

// Sau 10 giây (hết hạn)
// → setState(NORMAL)
// → catchMode = false
```

---

### 7. `MATERIALIZE`

```java
MATERIALIZE("paddle_materialize", 15, false)
```

**Mô tả**: Animation xuất hiện khi paddle spawn/respawn.

**Đặc điểm**:
- **paddlePrefix**: `"paddle_materialize"`
- **Sprite files**: `paddle_materialize_0.png`, ..., `paddle_materialize_14.png`
- **frameCount**: `15` - Animation dài (hiệu ứng đẹp)
- **shouldLoop**: `false` - Chạy một lần

**Animation Type**: **One-Shot** - Chỉ chạy khi spawn

**Luồng animation**:
```
Frame 0: Bắt đầu xuất hiện (mờ, nhỏ)
Frame 1-13: Dần dần rõ nét và lớn hơn
Frame 14: Hoàn toàn xuất hiện
→ Animation kết thúc
→ setState(NORMAL)
```

**Khi nào xuất hiện**:
- Khi bắt đầu round mới
- Khi respawn sau khi mất mạng
- Khi `paddle.playMaterializeAnimation()` được gọi

**Visual**: Paddle dần dần xuất hiện với hiệu ứng "materialize" (vật chất hóa)

**Ví dụ**:
```java
// Bắt đầu round
public void startRound() {
    paddle = new Paddle(x, y, width, height);
    paddle.playMaterializeAnimation();
    
    // Đợi animation kết thúc
    waitUntil(() -> !paddle.isAnimationPlaying());
    
    // Sau đó cho phép điều khiển
    enablePlayerControl();
}
```

---

### 8. `EXPLODE`

```java
EXPLODE("paddle_explode", 8, false)
```

**Mô tả**: Animation nổ khi paddle bị phá hủy.

**Đặc điểm**:
- **paddlePrefix**: `"paddle_explode"`
- **Sprite files**: `paddle_explode_0.png`, ..., `paddle_explode_7.png`
- **frameCount**: `8` - Animation ngắn (nổ nhanh)
- **shouldLoop**: `false` - Chạy một lần

**Animation Type**: **One-Shot** - Chỉ chạy khi paddle chết

**Luồng animation**:
```
Frame 0: Bắt đầu nổ
Frame 1-6: Hiệu ứng nổ lan rộng
Frame 7: Kết thúc nổ (mảnh vỡ bay)
→ Animation kết thúc
→ Paddle biến mất
```

**Khi nào xuất hiện**:
- Khi bóng rơi xuống (mất mạng)
- Khi va chạm với enemy (nếu có)
- Khi `paddle.playExplodeAnimation()` được gọi

**Visual**: Paddle phát nổ thành nhiều mảnh

**Ví dụ**:
```java
// Mất mạng
public void loseLife() {
    paddle.playExplodeAnimation();
    
    // Đợi animation kết thúc
    waitUntil(() -> !paddle.isAnimationPlaying());
    
    lives--;
    
    if (lives > 0) {
        respawnPaddle();
    } else {
        gameOver();
    }
}
```

---

## Thuộc tính Enum

### 1. `private final String paddlePrefix`

**Mô tả**: Tiền tố được sử dụng để tìm kiếm các file sprite.

**Mục đích**: 
- AnimationFactory sử dụng prefix để load sprite files
- Tạo tên file: `{prefix}_{frameIndex}.png`
- Ví dụ: `"paddle_wide"` → `paddle_wide_0.png`, `paddle_wide_1.png`, ...

**Getter**: `String getPaddlePrefix()`

---

### 2. `private final int frameCount`

**Mô tả**: Tổng số frame trong animation.

**Mục đích**:
- Xác định số lượng sprite file cần load
- Điều khiển độ dài animation
- Giúp Animation class biết khi nào animation kết thúc

**Getter**: `int getFrameCount()`

---

### 3. `private final boolean shouldLoop`

**Mô tả**: Cờ chỉ định animation có nên lặp lại hay không.

**Giá trị**:
- `true` = Animation lặp vô hạn (PULSATE animations)
- `false` = Animation chạy một lần rồi dừng (TRANSITION animations)

**Mục đích**:
- Phân biệt animation transition và animation loop
- Animation class sử dụng để quyết định behavior sau khi hết frame

**Getter**: `boolean shouldLoop()`

---

## Phân loại Animation

### Transition Animations (shouldLoop = false)

**Mục đích**: Chuyển đổi từ trạng thái này sang trạng thái khác

**Đặc điểm**:
- Chạy một lần
- Giữ frame cuối cùng
- Thường dài hơn (nhiều frame)

**Danh sách**:
- `WIDE` (9 frames) - Chuyển từ NORMAL → WIDE
- `LASER` (16 frames) - Chuyển từ NORMAL → LASER
- `MATERIALIZE` (15 frames) - Xuất hiện
- `EXPLODE` (8 frames) - Phá hủy

**Ví dụ**:
```java
// WIDE transition
paddle.setState(PaddleState.WIDE);
// Frame 0 → 1 → 2 → ... → 8
// Dừng ở frame 8
// Không lặp lại
```

---

### Loop Animations (shouldLoop = true)

**Mục đích**: Cảnh báo hoặc hiệu ứng liên tục

**Đặc điểm**:
- Lặp vô hạn
- Thường ngắn (ít frame)
- Hiệu ứng nhấp nháy/pulsate

**Danh sách**:
- `WIDE_PULSATE` (4 frames) - Cảnh báo WIDE hết hạn
- `LASER_PULSATE` (4 frames) - Cảnh báo LASER hết hạn
- `PULSATE` (4 frames) - Cảnh báo Catch/Slow hết hạn

**Ví dụ**:
```java
// PULSATE loop
paddle.setState(PaddleState.PULSATE);
// Frame 0 → 1 → 2 → 3 → 0 → 1 → 2 → 3 → 0 → ...
// Lặp liên tục cho đến khi setState(NORMAL)
```

---

### Static State (frameCount = 1)

**Mục đích**: Trạng thái tĩnh không có animation

**Đặc điểm**:
- Chỉ có 1 frame
- Không có animation

**Danh sách**:
- `NORMAL` (1 frame) - Trạng thái mặc định

**Ví dụ**:
```java
// NORMAL state
paddle.setState(PaddleState.NORMAL);
// Không có animation
// Chỉ render sprite "paddle.png"
```

---

## State Transition Diagram

```
                    ┌─────────────┐
         ┌─────────→│   NORMAL    │←──────────┐
         │          └─────────────┘           │
         │                 │                  │
         │      ┌──────────┼──────────┐       │
         │      │          │          │       │
         │      ↓          ↓          ↓       │
         │  ┌──────┐  ┌────────┐  ┌────────┐ │
         │  │ WIDE │  │ LASER  │  │ CATCH/ │ │
         │  │      │  │        │  │ SLOW   │ │
         │  └──────┘  └────────┘  └────────┘ │
         │      │          │          │       │
         │      ↓          ↓          ↓       │
         │  ┌────────────┐ ┌──────────────┐  │
         │  │WIDE_PULSATE│ │LASER_PULSATE │  │
         │  └────────────┘ └──────────────┘  │
         │      │               │             │
         │      └───────┬───────┘             │
         │              ↓                     │
         │         ┌─────────┐                │
         └─────────│ PULSATE │────────────────┘
                   └─────────┘
                   (Catch/Slow
                    on NORMAL)

Special Animations (One-Time):
    ┌──────────────┐
    │ MATERIALIZE  │ → Used on spawn
    └──────────────┘
    
    ┌──────────────┐
    │   EXPLODE    │ → Used on death
    └──────────────┘
```

---

## Tích hợp với AnimationFactory

```java
public class AnimationFactory {
    public static Animation createPaddleAnimation(PaddleState state) {
        if (state == PaddleState.NORMAL) {
            return null; // Không có animation
        }
        
        // Lấy metadata từ enum
        String prefix = state.getPaddlePrefix();
        int frameCount = state.getFrameCount();
        boolean shouldLoop = state.shouldLoop();
        
        // Load sprites
        List<Sprite> frames = new ArrayList<>();
        for (int i = 0; i < frameCount; i++) {
            String filename = prefix + "_" + i + ".png";
            Sprite sprite = spriteCache.getSprite(filename);
            frames.add(sprite);
        }
        
        // Tạo animation
        int frameDuration = calculateFrameDuration(state);
        Animation animation = new Animation(frames, frameDuration, shouldLoop);
        
        return animation;
    }
    
    private static int calculateFrameDuration(PaddleState state) {
        // Transition animations: chậm hơn (smooth)
        if (!state.shouldLoop()) {
            return 50; // 50ms/frame
        }
        
        // Loop animations: nhanh hơn (pulsate effect)
        return 100; // 100ms/frame
    }
}
```

---

## Sử dụng trong Paddle

```java
public class Paddle extends MovableObject {
    private PaddleState currentState = PaddleState.NORMAL;
    
    public void setState(PaddleState newState) {
        if (this.currentState == newState && animationPlaying) {
            return; // Không đổi nếu đã ở trạng thái này
        }
        
        this.currentState = newState;
        
        if (newState == PaddleState.NORMAL) {
            // NORMAL không có animation
            this.currentAnimation = null;
            this.animationPlaying = false;
            return;
        }
        
        // Tạo animation cho trạng thái mới
        this.currentAnimation = AnimationFactory.createPaddleAnimation(newState);
        
        if (currentAnimation != null) {
            currentAnimation.play();
            animationPlaying = true;
        }
    }
}
```

---

## Ví dụ Timeline hoàn chỉnh

```
T = 0s: Game Start
   ↓
   paddle.setState(MATERIALIZE)
   → Animation 15 frames (one-shot)
   → Paddle dần xuất hiện
   
T = 0.75s: Animation kết thúc
   ↓
   setState(NORMAL)
   → Không có animation
   → Render sprite tĩnh

T = 5s: Thu thập Expand Power-Up
   ↓
   paddle.expand()
   → setState(WIDE)
   → Animation 9 frames (transition)
   → Paddle dần mở rộng
   
T = 5.45s: Animation kết thúc
   ↓
   Giữ frame cuối (frame 8)
   → Paddle ở kích thước rộng

T = 17s: Còn 3s WIDE
   ↓
   setState(WIDE_PULSATE)
   → Animation 4 frames (loop)
   → Nhấp nháy cảnh báo

T = 20s: WIDE hết hạn
   ↓
   shrinkToNormal()
   → playReversedAnimation(WIDE)
   → Animation chạy ngược: frame 8 → 7 → ... → 0
   
T = 20.45s: Animation đảo ngược kết thúc
   ↓
   setState(NORMAL)

T = 25s: Thu thập Catch Power-Up
   ↓
   paddle.enableCatch()
   → catchMode = true
   → KHÔNG đổi state (vẫn NORMAL)
   
T = 32s: Còn 3s Catch
   ↓
   setState(PULSATE)
   → Animation 4 frames (loop)
   → Cảnh báo độc lập

T = 35s: Catch hết hạn
   ↓
   setState(NORMAL)
   → catchMode = false

T = 45s: Mất mạng
   ↓
   paddle.playExplodeAnimation()
   → setState(EXPLODE)
   → Animation 8 frames (one-shot)
   → Paddle nổ tung
   
T = 45.4s: Animation kết thúc
   ↓
   paddle.destroy()
   → Paddle biến mất
   
T = 46s: Respawn
   ↓
   new Paddle(...)
   → setState(MATERIALIZE)
   → Vòng lặp lại từ đầu
```

---

## Best Practices

### 1. Naming Convention
```java
// ✅ Đúng - tên file nhất quán với prefix và index
paddle_wide_0.png
paddle_wide_1.png
...
paddle_wide_8.png

// ❌ Sai - tên không nhất quán
paddle_wide1.png  // Thiếu underscore
paddleWide_0.png  // Khác prefix
wide_paddle_0.png // Thứ tự sai
```

### 2. Frame Count chính xác
```java
// ✅ Đúng - frameCount khớp với số file thực tế
WIDE("paddle_wide", 9, false)
// → paddle_wide_0.png đến paddle_wide_8.png (9 files)

// ❌ Sai - frameCount không khớp
WIDE("paddle_wide", 10, false)
// → Sẽ tìm paddle_wide_9.png (không tồn tại) → crash!
```

### 3. shouldLoop phù hợp
```java
// ✅ Đúng - transition không loop
WIDE("paddle_wide", 9, false)

// ✅ Đúng - warning animation loop
WIDE_PULSATE("paddle_wide_pulsate", 4, true)

// ❌ Sai - transition lặp lại
WIDE("paddle_wide", 9, true) // Paddle sẽ liên tục mở rộng/thu nhỏ!
```

---

## Kết luận

`PaddleState` enum là nền tảng của hệ thống animation cho Paddle:

- **Định nghĩa rõ ràng**: 8 trạng thái với metadata đầy đủ
- **Phân loại khoa học**: Transition vs Loop vs Static
- **Dễ mở rộng**: Thêm trạng thái mới chỉ cần thêm enum constant
- **Type-safe**: Enum đảm bảo an toàn kiểu dữ liệu
- **Metadata-driven**: AnimationFactory tự động tạo animation từ metadata

Enum này là ví dụ điển hình về cách sử dụng enum không chỉ như constant, mà như một data structure chứa metadata và behavior, giúp code sạch hơn và dễ bảo trì hơn.
