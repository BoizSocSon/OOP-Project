# AssetLoader Class

## Tổng quan
`AssetLoader` là **final utility class** chịu trách nhiệm tải tất cả các **tài nguyên** (assets) như hình ảnh, phông chữ, và âm thanh từ resource folder của ứng dụng. Class này implement **Resource Loader Pattern** với comprehensive error handling, fallback mechanisms, và logging. Nó sử dụng Java's **ClassLoader** để access resources embedded trong JAR file hay classpath, đảm bảo tính portable và independence từ file system paths.

## Vị trí
- **Package**: `Utils`
- **File**: `src/Utils/AssetLoader.java`
- **Type**: Final Utility Class (không thể khởi tạo)
- **Pattern**: Utility Pattern / Resource Loader Pattern
- **Dependencies**: JavaFX (Image, Font, Media, MediaPlayer)

## Mục đích
AssetLoader:
- Centralize resource loading logic
- Handle errors gracefully (fallbacks)
- Support image sequences (animations)
- Load fonts with size specification
- Load audio for music/SFX
- Use ClassLoader for portability
- Provide consistent error handling

---

## Class Structure

```java
public final class AssetLoader {
    // Private constructor - prevents instantiation
    private AssetLoader() {
        throw new UnsupportedOperationException(
            "Utility class - cannot be instantiated");
    }
    
    // Static methods for resource loading
    public static Image loadImage(String filename) { /* ... */ }
    public static List<Image> loadImageSequence(...) { /* ... */ }
    public static Font loadFont(String filename, int size) { /* ... */ }
    public static MediaPlayer loadBackgroundMusic(...) { /* ... */ }
}
```

---

## Phương thức - IMAGE LOADING

### 1. `static Image loadImage(String filename)`

**Mô tả**: Tải một hình ảnh từ graphics folder.

**Tham số**: `filename` - Tên file (e.g., "brick.png")

**Trả về**: JavaFX `Image` object, hoặc placeholder nếu failed

**Hành vi**:
```java
public static Image loadImage(String filename) {
    // Build full path: /Resources/Graphics/ + filename
    String path = Constants.Paths.GRAPHICS_PATH + filename;
    return loadImageFromPath(path);
}
```

**Ví dụ**:
```java
// Load brick sprite
Image brickImage = AssetLoader.loadImage("brick.png");
// → Loads from: /Resources/Graphics/brick.png

// Load paddle sprite
Image paddleImage = AssetLoader.loadImage("paddle.png");
// → Loads from: /Resources/Graphics/paddle.png

// Load ball sprite
Image ballImage = AssetLoader.loadImage("ball.png");
// → Loads from: /Resources/Graphics/ball.png
```

---

### 2. `private static Image loadImageFromPath(String path)`

**Mô tả**: Core image loading logic với comprehensive error handling.

**Tham số**: `path` - Full resource path (e.g., "/Resources/Graphics/brick.png")

**Trả về**: Image hoặc placeholder

**Algorithm**:
```java
private static Image loadImageFromPath(String path) {
    // Step 1: Try-with-resources for automatic stream closing
    try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
        
        // Step 2: Check if resource exists
        if (is == null) {
            System.err.println("Error: Image file not found - " + path);
            return createPlaceholderImage();
        }
        
        // Step 3: Load image from stream
        Image image = new Image(is);
        
        // Step 4: Check for loading errors
        if (image.isError()) {
            System.err.println(); // Separator line
            return createPlaceholderImage();
        }
        
        return image;
        
    } catch (IOException e) {
        System.err.println("AssetLoader: IOException loading image: " + path);
        e.printStackTrace();
        return createPlaceholderImage();
        
    } catch (IllegalArgumentException e) {
        System.err.println("AssetLoader: Invalid image format: " + path);
        e.printStackTrace();
        return createPlaceholderImage();
        
    } catch (Exception e) {
        System.err.println("AssetLoader: Unexpected error loading image: " + path);
        e.printStackTrace();
        return createPlaceholderImage();
    }
}
```

**Error Handling Layers**:
1. **Resource Not Found**: File doesn't exist in classpath
2. **IO Exception**: Error reading stream
3. **Invalid Format**: Unsupported image format
4. **Image Loading Error**: JavaFX image loading failed
5. **Unexpected Errors**: Catch-all for unknown issues

---

### 3. `static List<Image> loadImageSequence(String pattern, int from, int to)`

**Mô tả**: Tải sequence của nhiều images theo pattern (for animations).

**Tham số**:
- `pattern` - Filename pattern với `%d` placeholder (e.g., "frame_%d.png")
- `from` - Starting index (inclusive)
- `to` - Ending index (inclusive)

**Trả về**: List of Image objects

**Hành vi**:
```java
public static List<Image> loadImageSequence(String patternWithPercentD, 
                                             int from, int to) {
    List<Image> images = new ArrayList<>();
    
    // Loop through indices
    for (int i = from; i <= to; i++) {
        // Format filename: "frame_%d.png" → "frame_0.png", "frame_1.png", ...
        String filename = String.format(patternWithPercentD, i);
        
        // Load image and add to list
        Image tempImage = loadImage(filename);
        images.add(tempImage);
    }
    
    return images;
}
```

**Ví dụ**:
```java
// Load animation frames: brick_crack_0.png to brick_crack_3.png
List<Image> crackFrames = AssetLoader.loadImageSequence(
    "brick_crack_%d.png",
    0,  // from
    3   // to
);
// → Loads: brick_crack_0.png, brick_crack_1.png, 
//          brick_crack_2.png, brick_crack_3.png

// Load power-up animation: powerup_catch_0.png to powerup_catch_7.png
List<Image> catchFrames = AssetLoader.loadImageSequence(
    "powerup_catch_%d.png",
    0,
    7
);
// → 8 frames for catch power-up animation

// Load paddle laser frames
List<Image> laserFrames = AssetLoader.loadImageSequence(
    "paddle_laser_%d.png",
    0,
    2
);
// → 3 frames for laser paddle animation
```

**Use Case - Animation Creation**:
```java
// Load frames
List<Image> frames = AssetLoader.loadImageSequence("explosion_%d.png", 0, 9);

// Create animation
Animation explosion = new Animation(
    frames,
    100, // 100ms per frame
    false // Don't loop
);

// Render animation
explosion.render(g, x, y);
```

---

### 4. `private static Image createPlaceholderImage()`

**Mô tả**: Tạo empty placeholder image khi loading fails.

**Trả về**: 50×50 WritableImage (blank)

**Hành vi**:
```java
private static Image createPlaceholderImage() {
    // Create blank 50x50 image
    WritableImage placeholder = new WritableImage(50, 50);
    return placeholder;
}
```

**Why Placeholder?**
- Prevents null pointer exceptions
- Game can continue running
- Visual indication of missing asset
- Better than crashing

**Visual Representation**:
```
Missing sprite appears as: □ (blank square)
Instead of: ❌ NullPointerException
```

---

## Phương thức - FONT LOADING

### 5. `static Font loadFont(String filename, int size)`

**Mô tả**: Tải custom font từ resources với size specification.

**Tham số**:
- `filename` - Font file name (e.g., "game_font.ttf")
- `size` - Font size in points

**Trả về**: JavaFX `Font` object, hoặc Arial fallback

**Hành vi**:
```java
public static Font loadFont(String filename, int size) {
    // Build full path: /Resources/Fonts/ + filename
    String path = Constants.Paths.FONTS_PATH + filename;
    
    try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
        // Check if font exists
        if (is == null) {
            System.err.println("AssetLoader: Font not found: " + path);
            return Font.font("Arial", size); // Fallback
        }
        
        // Load font from stream
        Font font = Font.loadFont(is, size);
        
        // Check loading success
        if (font == null) {
            System.err.println("AssetLoader: Failed to load font: " + path);
            return Font.font("Arial", size); // Fallback
        }
        
        System.out.println("AssetLoader: Loaded font: " + filename + 
                           " (" + size + "pt)");
        return font;
        
    } catch (IOException e) {
        System.err.println("AssetLoader: IOException loading font: " + path);
        e.printStackTrace();
        return Font.font("Arial", size);
        
    } catch (Exception e) {
        System.err.println("AssetLoader: Unexpected error loading font: " + path);
        e.printStackTrace();
        return Font.font("Arial", size);
    }
}
```

**Fallback Strategy**:
- Primary: Load custom font from resources
- Fallback: Use system "Arial" font
- Ensures text always renders (even if not ideal)

**Ví dụ**:
```java
// Load game font at 24pt
Font titleFont = AssetLoader.loadFont("game_font.ttf", 24);

// Load different sizes
Font scoreFont = AssetLoader.loadFont("game_font.ttf", 32);
Font menuFont = AssetLoader.loadFont("game_font.ttf", 18);

// Use font
Text text = new Text("ARKANOID");
text.setFont(titleFont);

// If font missing, Arial is used automatically
// Output: "AssetLoader: Font not found: /Resources/Fonts/missing.ttf"
// → Uses Arial 24pt instead
```

---

## Phương thức - AUDIO LOADING

### 6. `static MediaPlayer loadBackgroundMusic(String track, boolean loop, double volume)`

**Mô tả**: Tải và configure background music MediaPlayer.

**Tham số**:
- `track` - Audio filename (e.g., "background.mp3")
- `loop` - Loop infinitely? (true/false)
- `volume` - Volume level (0.0 to 1.0)

**Trả về**: Configured `MediaPlayer`, hoặc `null` nếu failed

**Hành vi**:
```java
public static MediaPlayer loadBackgroundMusic(String track, 
                                               boolean loop, 
                                               double volume) {
    // Build full path: /Resources/Audio/ + track
    String path = Constants.Paths.AUDIO_PATH + track;
    
    try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
        // Check if audio file exists
        if (is == null) {
            System.err.println("AssetLoader: Music file not found: " + path);
            return null;
        }
        
        // Create Media object from external URL
        Media media = new Media(
            AssetLoader.class.getResource(path).toExternalForm()
        );
        
        // Create MediaPlayer from Media
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        
        // Configure volume
        mediaPlayer.setVolume(volume);
        
        // Configure looping
        if (loop) {
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        }
        
        return mediaPlayer;
        
    } catch (Exception e) {
        System.err.println("AssetLoader: Error loading music: " + path);
        e.printStackTrace();
        return null;
    }
}
```

**Ví dụ**:
```java
// Load looping background music at 70% volume
MediaPlayer bgMusic = AssetLoader.loadBackgroundMusic(
    "background_music.mp3",
    true,  // loop infinitely
    0.7    // 70% volume
);

// Start playing
if (bgMusic != null) {
    bgMusic.play();
}

// Load one-shot sound effect at full volume
MediaPlayer victorySound = AssetLoader.loadBackgroundMusic(
    "victory_fanfare.mp3",
    false, // play once
    1.0    // 100% volume
);

// Play and dispose
if (victorySound != null) {
    victorySound.play();
    victorySound.setOnEndOfMedia(() -> victorySound.dispose());
}
```

**Media vs MediaPlayer**:
- `Media`: Represents audio file data
- `MediaPlayer`: Plays Media, has controls (play/pause/stop/volume)

**Looping**:
```java
// Loop forever
mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

// Loop 3 times
mediaPlayer.setCycleCount(3);

// Play once (default)
mediaPlayer.setCycleCount(1);
```

---

## Resource Path System

### ClassLoader Resource Loading

```java
// Using getResourceAsStream()
InputStream is = AssetLoader.class.getResourceAsStream(path);

// Why ClassLoader?
// 1. Works with JAR files
// 2. Platform-independent
// 3. Handles classpath properly
// 4. No absolute paths needed
```

### Path Structure

```
Project Structure:
src/
  Resources/           ← Root resource folder
    Graphics/          ← Images
      brick.png
      paddle.png
      ball.png
      powerup_catch_0.png
      powerup_catch_1.png
      ...
    Audio/             ← Sounds
      hit.wav
      break.wav
      background.mp3
      ...
    Fonts/             ← Custom fonts
      game_font.ttf
      menu_font.ttf
      ...
```

### Path Building

```java
// Constants define base paths
public static class Paths {
    public static final String RESOURCES_PATH = "/Resources/";
    public static final String GRAPHICS_PATH = RESOURCES_PATH + "Graphics/";
    public static final String AUDIO_PATH = RESOURCES_PATH + "Audio/";
    public static final String FONTS_PATH = RESOURCES_PATH + "Fonts/";
}

// AssetLoader builds full paths
String imagePath = Constants.Paths.GRAPHICS_PATH + "brick.png";
// → "/Resources/Graphics/brick.png"

String audioPath = Constants.Paths.AUDIO_PATH + "hit.wav";
// → "/Resources/Audio/hit.wav"

String fontPath = Constants.Paths.FONTS_PATH + "game_font.ttf";
// → "/Resources/Fonts/game_font.ttf"
```

---

## Error Handling Strategy

### Multi-Layer Defense

```java
// Layer 1: Check resource existence
if (is == null) {
    System.err.println("Resource not found: " + path);
    return fallback;
}

// Layer 2: Catch IO errors
try (InputStream is = ...) {
    // ...
} catch (IOException e) {
    System.err.println("IO error: " + e.getMessage());
    return fallback;
}

// Layer 3: Catch format errors
catch (IllegalArgumentException e) {
    System.err.println("Invalid format: " + e.getMessage());
    return fallback;
}

// Layer 4: Catch unexpected errors
catch (Exception e) {
    System.err.println("Unexpected error: " + e.getMessage());
    return fallback;
}
```

### Fallback Hierarchy

```
Images:  Placeholder (50×50 blank)
         ↓
Fonts:   System "Arial"
         ↓
Audio:   null (game continues without audio)
```

---

## Best Practices

### 1. Always Check Return Values

```java
// ✅ Đúng - check for null/placeholder
Image image = AssetLoader.loadImage("brick.png");
if (image != null && image.getWidth() > 1) {
    // Valid image loaded
    sprite.setImage(image);
} else {
    // Placeholder or null - handle gracefully
    System.err.println("Failed to load brick sprite");
}

// ❌ Sai - assume success
Image image = AssetLoader.loadImage("brick.png");
sprite.setImage(image); // May be placeholder!
```

---

### 2. Load Resources Once

```java
// ✅ Đúng - load once, cache
public class SpriteCache {
    private static final Map<String, Image> cache = new HashMap<>();
    
    public static Image getImage(String filename) {
        return cache.computeIfAbsent(filename, 
            name -> AssetLoader.loadImage(name));
    }
}

// ❌ Sai - load repeatedly
public void render() {
    Image brick = AssetLoader.loadImage("brick.png"); // Every frame!
    g.drawImage(brick, x, y);
}
```

---

### 3. Use Try-With-Resources

```java
// ✅ Đúng - automatic stream closing
try (InputStream is = getResourceAsStream(path)) {
    // Use stream
} // Automatically closed

// ❌ Sai - manual closing (error-prone)
InputStream is = null;
try {
    is = getResourceAsStream(path);
    // Use stream
} finally {
    if (is != null) {
        is.close(); // May throw exception
    }
}
```

---

### 4. Meaningful Error Messages

```java
// ✅ Đúng - informative errors
System.err.println("AssetLoader: Image file not found - " + path);
System.err.println("AssetLoader: Invalid image format: " + path);

// ❌ Sai - vague errors
System.err.println("Error");
System.err.println("Failed to load");
```

---

### 5. Consistent Path Format

```java
// ✅ Đúng - consistent leading slash
public static final String RESOURCES_PATH = "/Resources/";
public static final String GRAPHICS_PATH = RESOURCES_PATH + "Graphics/";

// ❌ Sai - inconsistent
public static final String RESOURCES_PATH = "Resources/"; // No leading /
public static final String GRAPHICS_PATH = "/Graphics/"; // Absolute
```

---

## Usage Patterns

### Initialization Phase

```java
// Load all essential assets at startup
public class AssetManager {
    private Map<String, Image> sprites = new HashMap<>();
    private Map<String, Font> fonts = new HashMap<>();
    private MediaPlayer bgMusic;
    
    public void loadAssets() {
        // Load sprites
        sprites.put("brick", AssetLoader.loadImage("brick.png"));
        sprites.put("paddle", AssetLoader.loadImage("paddle.png"));
        sprites.put("ball", AssetLoader.loadImage("ball.png"));
        
        // Load animation sequences
        List<Image> crackAnim = AssetLoader.loadImageSequence(
            "brick_crack_%d.png", 0, 3);
        
        // Load fonts
        fonts.put("title", AssetLoader.loadFont("game_font.ttf", 32));
        fonts.put("menu", AssetLoader.loadFont("game_font.ttf", 18));
        
        // Load music
        bgMusic = AssetLoader.loadBackgroundMusic(
            "background.mp3", true, 0.7);
    }
}
```

---

### Lazy Loading

```java
// Load assets on-demand
public class LazyAssetManager {
    private Map<String, Image> cache = new HashMap<>();
    
    public Image getImage(String filename) {
        return cache.computeIfAbsent(filename,
            name -> AssetLoader.loadImage(name));
    }
}

// Usage
Image brick = manager.getImage("brick.png");
// First call: loads from disk
// Subsequent calls: returns cached image
```

---

### Resource Verification

```java
// Verify all required assets exist at startup
public class AssetVerifier {
    private static final String[] REQUIRED_IMAGES = {
        "brick.png", "paddle.png", "ball.png",
        "powerup_catch_0.png", "edge_top.png"
    };
    
    public static boolean verifyAssets() {
        boolean allFound = true;
        
        for (String filename : REQUIRED_IMAGES) {
            Image img = AssetLoader.loadImage(filename);
            if (img.getWidth() <= 1) { // Placeholder?
                System.err.println("Missing required asset: " + filename);
                allFound = false;
            }
        }
        
        return allFound;
    }
}

// Call before starting game
if (!AssetVerifier.verifyAssets()) {
    showErrorDialog("Missing game assets. Please reinstall.");
    System.exit(1);
}
```

---

## Performance Considerations

### Memory Management

```java
// Images can be large - cache wisely
public class SpriteCache {
    private static final int MAX_CACHE_SIZE = 100;
    private LinkedHashMap<String, Image> cache = 
        new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
}
```

### Preloading vs Lazy Loading

```java
// Preload: All at startup (slower start, faster gameplay)
public void preloadAllAssets() {
    for (String sprite : ALL_SPRITES) {
        cache.put(sprite, AssetLoader.loadImage(sprite));
    }
}

// Lazy load: On-demand (fast start, potential hiccups)
public Image getSprite(String name) {
    return cache.computeIfAbsent(name, 
        AssetLoader::loadImage);
}
```

---

## Testing

### Unit Tests

```java
@Test
public void testLoadValidImage() {
    Image image = AssetLoader.loadImage("brick.png");
    assertNotNull(image);
    assertTrue(image.getWidth() > 1); // Not placeholder
}

@Test
public void testLoadInvalidImage() {
    Image image = AssetLoader.loadImage("nonexistent.png");
    assertNotNull(image); // Placeholder returned
    assertEquals(50, image.getWidth()); // Placeholder size
}

@Test
public void testLoadImageSequence() {
    List<Image> frames = AssetLoader.loadImageSequence(
        "frame_%d.png", 0, 3);
    assertEquals(4, frames.size());
    assertNotNull(frames.get(0));
}

@Test
public void testLoadFont() {
    Font font = AssetLoader.loadFont("game_font.ttf", 16);
    assertNotNull(font);
    assertEquals(16, font.getSize(), 0.1);
}
```

---

## Debugging Tips

### Enable Verbose Logging

```java
// Add debug flag
private static final boolean DEBUG = true;

public static Image loadImage(String filename) {
    if (DEBUG) {
        System.out.println("Loading image: " + filename);
    }
    String path = Constants.Paths.GRAPHICS_PATH + filename;
    Image img = loadImageFromPath(path);
    if (DEBUG) {
        System.out.println("  → Width: " + img.getWidth() + 
                           ", Height: " + img.getHeight());
    }
    return img;
}
```

### Verify Resource Paths

```java
// Print all resources in Graphics folder
public static void listResources() {
    try {
        URL url = AssetLoader.class.getResource(
            Constants.Paths.GRAPHICS_PATH);
        if (url != null) {
            File dir = new File(url.toURI());
            for (File file : dir.listFiles()) {
                System.out.println("Found: " + file.getName());
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

---

## Kết luận

`AssetLoader` class là **gateway** cho tất cả external resources trong Arkanoid game:

- **Centralized Loading**: Single point for all resource access
- **Robust Error Handling**: Multi-layer error catching với fallbacks
- **Graceful Degradation**: Game continues even với missing assets
- **Platform Independence**: ClassLoader works on all platforms
- **Type Support**: Images, fonts, và audio
- **Sequence Loading**: Easy animation frame loading

AssetLoader exemplifies **defensive programming**. Bằng việc anticipate và handle mọi possible failure (missing files, corrupt formats, IO errors), class này ensures game stability. Placeholder images và fallback fonts mean game never crashes từ missing assets. Detailed error logging helps developers debug resource issues quickly.

**Design Pattern**: Resource Loader pattern với Fail-Safe design. Every loading operation has fallback. This makes game more robust và easier to debug. When asset loading fails, developers get clear error messages pointing to exact problem. Game continues running với degraded visuals rather than crashing - much better UX.

**Best Practice**: Separate resource loading từ resource usage. AssetLoader handles loading complexity (paths, streams, errors). Rest của codebase just calls simple methods như `loadImage()` và gets back usable objects. This separation of concerns makes code cleaner và more maintainable.

