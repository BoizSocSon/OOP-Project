# FileManager Class

## Tổng quan
`FileManager` là **final utility class** chịu trách nhiệm **persistent storage** cho game data như high scores, settings, và configurations. Class này implement **safe file I/O operations** với atomic writes, synchronization, và comprehensive error handling. Nó sử dụng hidden application directory trong user's home folder (`~/.arkanoid`), ensuring cross-platform compatibility và proper user data separation.

## Vị trí
- **Package**: `Utils`
- **File**: `src/Utils/FileManager.java`
- **Type**: Final Utility Class (không thể khởi tạo)
- **Pattern**: Utility Pattern / Persistent Storage Manager
- **Dependencies**: JavaFX (Platform, Alert), Java NIO (Files, Paths)

## Mục đích
FileManager:
- Persistent storage cho game data
- Safe atomic file operations
- Thread-safe synchronization
- Cross-platform compatibility
- Automatic directory management
- Error handling với user feedback
- High score persistence
- Settings persistence (audio, etc.)

---

## Class Structure

```java
public final class FileManager {
    // Application directory: ~/.arkanoid
    private static final String APP_DIR_NAME = ".arkanoid";
    private static final Path APP_DIR = Paths.get(
        System.getProperty("user.home"), APP_DIR_NAME);
    
    // File paths
    private static final Path HIGHSCORE_FILE = 
        APP_DIR.resolve(Constants.Paths.HIGHSCORE_FILE);
    private static final String AUDIO_SETTINGS_FILE = "audio_settings.dat";
    
    // Synchronization lock
    private static final Object LOCK = new Object();
    
    // Private constructor
    private FileManager() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    // Public API methods
    public static int loadHighscore() { /* ... */ }
    public static void saveHighscore(int score) { /* ... */ }
    public static double[] loadAudioSettings() { /* ... */ }
    public static void saveAudioSettings(double volume, boolean isMuted) { /* ... */ }
    public static List<String> readLinesFromFile(String filename) { /* ... */ }
    public static void writeLinesToFile(String filename, List<String> lines) { /* ... */ }
}
```

---

## Directory Structure

### Application Directory

```
User Home Directory (e.g., C:\Users\Username\ or /home/username/)
  └── .arkanoid/                    ← Hidden app directory
      ├── highscore.dat             ← High score file
      ├── audio_settings.dat        ← Audio configuration
      └── tmp_XXXXX.tmp             ← Temporary files (atomic writes)
```

**Why Hidden Directory?**
- `.arkanoid` starts with `.` → hidden on Unix/Linux
- Windows: Hidden attribute set automatically
- Keeps user home clean
- Standard practice for app data

**Path Construction**:
```java
// Get user home: C:\Users\John or /home/john
String userHome = System.getProperty("user.home");

// Build app directory: C:\Users\John\.arkanoid
Path appDir = Paths.get(userHome, ".arkanoid");

// Build file path: C:\Users\John\.arkanoid\highscore.dat
Path highscoreFile = appDir.resolve("highscore.dat");
```

---

## Phương thức - HIGH SCORE MANAGEMENT

### 1. `static int loadHighscore()`

**Mô tả**: Tải high score từ persistent storage.

**Trả về**: High score (int), hoặc 0 nếu không có file/error

**Hành vi**:
```java
public static int loadHighscore() {
    synchronized (LOCK) {
        try {
            // Ensure app directory exists
            ensureAppDirExists();
            
            // Check if highscore file exists
            if (!Files.exists(HIGHSCORE_FILE)) {
                return 0; // No highscore yet
            }
            
            // Read file content
            String s = Files.readString(HIGHSCORE_FILE).trim();
            
            // Empty file?
            if (s.isEmpty()) {
                return 0;
            }
            
            try {
                // Parse integer
                int v = Integer.parseInt(s);
                return Math.max(0, v); // Non-negative
            } catch (NumberFormatException ex) {
                System.err.println("Filemanager: highscore corrupt - " +
                    "returning default. (" + ex.getMessage() + ")");
                return 0;
            }
            
        } catch (IOException ex) {
            System.err.println("Filemanager: failed to read highscore - " +
                ex.getMessage());
            return 0;
        }
    }
}
```

**Error Handling**:
1. **Directory Missing**: Created automatically
2. **File Missing**: Return 0 (first run)
3. **Empty File**: Return 0
4. **Corrupt Data**: Return 0, log error
5. **IO Error**: Return 0, log error

**Ví dụ sử dụng**:
```java
// Load at game startup
public class GameInitializer {
    public void initialize() {
        int highScore = FileManager.loadHighscore();
        System.out.println("High Score: " + highScore);
        
        scoreManager.setHighScore(highScore);
    }
}

// Check if new high score
public void onGameOver() {
    int currentScore = scoreManager.getScore();
    int highScore = FileManager.loadHighscore();
    
    if (currentScore > highScore) {
        System.out.println("NEW HIGH SCORE!");
        FileManager.saveHighscore(currentScore);
        showNewHighScoreAnimation();
    }
}
```

---

### 2. `static void saveHighscore(int score)`

**Mô tả**: Lưu high score vào persistent storage (atomic write).

**Tham số**: `score` - Score to save (non-negative)

**Hành vi**:
```java
public static void saveHighscore(int score) {
    synchronized (LOCK) {
        try {
            // Ensure app directory exists
            ensureAppDirExists();
            
            // Prepare content (ensure non-negative)
            String content = String.valueOf(Math.max(0, score));
            
            // Atomic write
            writeFileAtomic(HIGHSCORE_FILE, content.getBytes());
            
        } catch (IOException ex) {
            System.err.println("Filemanager: failed to save highscore - " +
                ex.getMessage());
            
            // Show error dialog to user
            showWriteErrorDialog("Lưu điểm cao nhất thất bại:\n" +
                ex.getMessage());
        }
    }
}
```

**Atomic Write**: See `writeFileAtomic()` below.

**Ví dụ**:
```java
// Save new high score
if (score > oldHighScore) {
    FileManager.saveHighscore(score);
    System.out.println("High score saved: " + score);
}

// Negative score → saved as 0
FileManager.saveHighscore(-100);
// → File content: "0"
```

---

## Phương thức - AUDIO SETTINGS

### 3. `static double[] loadAudioSettings()`

**Mô tả**: Tải audio settings (volume và mute state).

**Trả về**: `double[2]` array: `[volume, isMuted]`, hoặc `null` nếu no file

**Format**:
- `[0]`: Volume (0.0 to 1.0)
- `[1]`: Muted (1.0 = muted, 0.0 = unmuted)

**Hành vi**:
```java
public static double[] loadAudioSettings() {
    synchronized (LOCK) {
        try {
            ensureAppDirExists();
            Path audioFile = APP_DIR.resolve(AUDIO_SETTINGS_FILE);
            
            // File exists?
            if (!Files.exists(audioFile)) {
                return null; // Use defaults
            }
            
            // Read all lines
            List<String> lines = Files.readAllLines(audioFile);
            
            // Check format: must have 2 lines
            if (lines.size() < 2) {
                return null;
            }
            
            // Parse volume (line 1)
            double volume = Double.parseDouble(lines.get(0).trim());
            
            // Parse muted (line 2)
            boolean muted = Boolean.parseBoolean(lines.get(1).trim());
            
            // Return array: [volume, isMuted]
            return new double[] { volume, muted ? 1.0 : 0.0 };
            
        } catch (Exception ex) {
            System.err.println("FileManager: failed to load audio settings - " +
                ex.getMessage());
            return null;
        }
    }
}
```

**File Format**:
```
0.7
false
```
Line 1: Volume (double)
Line 2: Muted (boolean)

**Ví dụ sử dụng**:
```java
// Load settings at startup
public class AudioManager {
    public void initialize() {
        double[] settings = FileManager.loadAudioSettings();
        
        if (settings != null) {
            // Loaded from file
            double volume = settings[0];
            boolean muted = settings[1] == 1.0;
            
            setVolume(volume);
            setMuted(muted);
            
            System.out.println("Loaded audio: volume=" + volume + 
                               ", muted=" + muted);
        } else {
            // Use defaults
            setVolume(Constants.Audio.DEFAULT_MUSIC_VOLUME);
            setMuted(false);
            
            System.out.println("Using default audio settings");
        }
    }
}
```

---

### 4. `static void saveAudioSettings(double volume, boolean isMuted)`

**Mô tả**: Lưu audio settings (atomic write).

**Tham số**:
- `volume` - Volume level (0.0 to 1.0)
- `isMuted` - Mute state (true/false)

**Hành vi**:
```java
public static void saveAudioSettings(double volume, boolean isMuted) {
    synchronized (LOCK) {
        try {
            ensureAppDirExists();
            Path audioFile = APP_DIR.resolve(AUDIO_SETTINGS_FILE);
            
            // Build content
            StringBuilder sb = new StringBuilder();
            sb.append(volume).append(System.lineSeparator());
            sb.append(isMuted).append(System.lineSeparator());
            
            // Atomic write
            writeFileAtomic(audioFile, sb.toString().getBytes());
            
        } catch (IOException ex) {
            System.err.println("FileManager: failed to save audio settings - " +
                ex.getMessage());
            // No dialog - background save
        }
    }
}
```

**Ví dụ**:
```java
// Save when user changes settings
public void onVolumeChanged(double newVolume) {
    audioPlayer.setVolume(newVolume);
    FileManager.saveAudioSettings(newVolume, isMuted);
}

public void onMuteToggled() {
    isMuted = !isMuted;
    audioPlayer.setMuted(isMuted);
    FileManager.saveAudioSettings(volume, isMuted);
}

// Saved file:
// 0.75
// true
```

---

## Phương thức - GENERIC FILE I/O

### 5. `static List<String> readLinesFromFile(String filename)`

**Mô tả**: Đọc tất cả lines từ file trong app directory.

**Tham số**: `filename` - File name (relative to app directory)

**Trả về**: List of lines, hoặc `null` nếu file not found/error

**Hành vi**:
```java
public static List<String> readLinesFromFile(String filename) {
    synchronized (LOCK) {
        try {
            ensureAppDirExists();
            Path filePath = APP_DIR.resolve(filename);
            
            // File exists?
            if (!Files.exists(filePath)) {
                return null;
            }
            
            // Read all lines
            return Files.readAllLines(filePath);
            
        } catch (IOException ex) {
            System.err.println("FileManager: failed to read file " + 
                filename + " - " + ex.getMessage());
            return null;
        }
    }
}
```

**Ví dụ**:
```java
// Read custom configuration
List<String> config = FileManager.readLinesFromFile("game_config.txt");
if (config != null) {
    for (String line : config) {
        parseConfigLine(line);
    }
} else {
    System.out.println("No config file - using defaults");
}

// Read level definitions
List<String> levelData = FileManager.readLinesFromFile("custom_level.dat");
if (levelData != null) {
    Level level = Level.fromLines(levelData);
    loadLevel(level);
}
```

---

### 6. `static void writeLinesToFile(String filename, List<String> lines)`

**Mô tả**: Ghi list of lines vào file (atomic write).

**Tham số**:
- `filename` - File name (relative to app directory)
- `lines` - Lines to write

**Hành vi**:
```java
public static void writeLinesToFile(String filename, List<String> lines) {
    synchronized (LOCK) {
        try {
            ensureAppDirExists();
            Path filePath = APP_DIR.resolve(filename);
            
            // Build content from lines
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line).append(System.lineSeparator());
            }
            
            // Atomic write
            writeFileAtomic(filePath, sb.toString().getBytes());
            
        } catch (IOException ex) {
            System.err.println("FileManager: failed to write file " + 
                filename + " - " + ex.getMessage());
            showWriteErrorDialog("Lưu file thất bại:\n" + ex.getMessage());
        }
    }
}
```

**Ví dụ**:
```java
// Save game configuration
List<String> configLines = Arrays.asList(
    "difficulty=hard",
    "sound=enabled",
    "fullscreen=false"
);
FileManager.writeLinesToFile("game_config.txt", configLines);

// Save custom level
List<String> levelLines = level.toLines();
FileManager.writeLinesToFile("custom_level_1.dat", levelLines);
```

---

## Core Internal Methods

### 7. `private static void ensureAppDirExists() throws IOException`

**Mô tả**: Ensure application directory exists, create nếu chưa có.

**Throws**: `IOException` nếu cannot create directory

**Hành vi**:
```java
private static void ensureAppDirExists() throws IOException {
    if (!Files.exists(APP_DIR)) {
        // Create directory (and parents if needed)
        Files.createDirectories(APP_DIR);
    }
}
```

**Directory Creation**:
- `Files.createDirectories()`: Creates all parent directories
- Không throw exception nếu already exists
- Cross-platform compatible

---

### 8. `private static void writeFileAtomic(Path target, byte[] data) throws IOException` ⭐

**Mô tả**: **Atomic file write** using temporary file + rename strategy.

**Tham số**:
- `target` - Destination file path
- `data` - Bytes to write

**Throws**: `IOException` on error

**Algorithm**:
```java
private static void writeFileAtomic(Path target, byte[] data) throws IOException {
    // Step 1: Create temporary file in same directory
    Path tmp = Files.createTempFile(APP_DIR, "tmp", ".tmp");
    
    // Step 2: Write data to temporary file
    try (OutputStream out = new BufferedOutputStream(
            Files.newOutputStream(tmp, 
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING))) {
        out.write(data);
        out.flush(); // Ensure data written to disk
    }
    
    // Step 3: Atomic move/rename
    try {
        Files.move(tmp, target, 
            StandardCopyOption.ATOMIC_MOVE,
            StandardCopyOption.REPLACE_EXISTING);
    } catch (AtomicMoveNotSupportedException e) {
        // Fallback: non-atomic move
        Files.move(tmp, target, 
            StandardCopyOption.REPLACE_EXISTING);
    }
}
```

**Why Atomic Write?**
```
Without Atomic Write:
1. Open file for writing
2. Start writing data
3. ⚡ CRASH during write
4. → File corrupted (partial data)

With Atomic Write:
1. Write to temporary file
2. Finish writing completely
3. Atomically rename temp → target
4. ⚡ CRASH before rename → old file intact
5. ⚡ CRASH after rename → new file complete
6. → Never corrupted (always old OR new)
```

**Benefits**:
- **No Corruption**: File always valid (old or new)
- **Crash Safe**: Partial writes discarded
- **Concurrency Safe**: Atomic rename operation
- **Data Integrity**: Users never see incomplete data

---

### 9. `private static void showWriteErrorDialog(String message)`

**Mô tả**: Show error dialog to user về file write failures.

**Tham số**: `message` - Error message

**Hành vi**:
```java
private static void showWriteErrorDialog(String message) {
    try {
        if (Platform.isFxApplicationThread()) {
            // Already on JavaFX thread - show immediately
            Alert a = new Alert(AlertType.WARNING);
            a.setTitle("Lưu thất bại");
            a.setHeaderText(null);
            a.setContentText(message);
            a.show();
        } else {
            // Not on JavaFX thread - schedule on it
            Platform.runLater(() -> {
                Alert a = new Alert(AlertType.WARNING);
                a.setTitle("Lưu thất bại");
                a.setHeaderText(null);
                a.setContentText(message);
                a.show();
            });
        }
    } catch (Throwable t) {
        // Can't show dialog - log to console
        System.err.println("Filemanager: cannot show dialog - " + 
            t.getMessage());
    }
}
```

**Thread Safety**:
- JavaFX UI must be updated on **JavaFX Application Thread**
- `Platform.isFxApplicationThread()`: Check current thread
- `Platform.runLater()`: Schedule on JavaFX thread

**Error Dialog**:
```
┌─────────────────────────────────────┐
│ Lưu thất bại                    [X] │
├─────────────────────────────────────┤
│ Lưu điểm cao nhất thất bại:        │
│ Access denied: .arkanoid/           │
│ highscore.dat                       │
│                                     │
│                 [ OK ]              │
└─────────────────────────────────────┘
```

---

## Thread Safety

### Synchronization Strategy

```java
private static final Object LOCK = new Object();

public static int loadHighscore() {
    synchronized (LOCK) {
        // Only one thread can execute at a time
        // Prevents concurrent read/write conflicts
    }
}

public static void saveHighscore(int score) {
    synchronized (LOCK) {
        // Blocks if another thread is reading/writing
        // Ensures file consistency
    }
}
```

**Why Synchronization?**
```
Without Synchronization:
Thread 1: Reading highscore.dat
Thread 2: Writing highscore.dat
→ Read gets corrupt/incomplete data
→ Write may be interleaved
→ File corrupted

With Synchronization:
Thread 1: Lock acquired, reading
Thread 2: Waits for lock...
Thread 1: Finishes, releases lock
Thread 2: Acquires lock, writing
→ Sequential access
→ Data integrity guaranteed
```

---

## Error Handling Philosophy

### Graceful Degradation

```java
// Load methods return default values on error
public static int loadHighscore() {
    try {
        // Try to load
    } catch (Exception e) {
        // Log error
        return 0; // Default value
    }
}

// Game continues with defaults if file loading fails
// Better UX than crashing
```

### User Notification

```java
// Save methods notify user on critical errors
public static void saveHighscore(int score) {
    try {
        // Try to save
    } catch (IOException e) {
        // Log error
        showWriteErrorDialog(...); // Inform user
        // Game continues (score not persisted)
    }
}
```

---

## Cross-Platform Compatibility

### Path Separators

```java
// ✅ Đúng - use Path API (cross-platform)
Path file = APP_DIR.resolve("highscore.dat");
// Windows: C:\Users\Name\.arkanoid\highscore.dat
// Linux: /home/name/.arkanoid/highscore.dat

// ❌ Sai - hardcoded separators
String file = APP_DIR + "\\highscore.dat"; // Windows only!
String file = APP_DIR + "/highscore.dat";  // Unix only!
```

### Line Separators

```java
// ✅ Đúng - use system line separator
sb.append(line).append(System.lineSeparator());
// Windows: \r\n
// Unix: \n

// ❌ Sai - hardcoded line ending
sb.append(line).append("\n"); // Unix only
```

### Home Directory

```java
// ✅ Đúng - use system property
String home = System.getProperty("user.home");
// Windows: C:\Users\Username
// Linux: /home/username
// macOS: /Users/username

// ❌ Sai - hardcoded path
String home = "C:\\Users\\John"; // Windows specific!
```

---

## Best Practices

### 1. Check Return Values

```java
// ✅ Đúng - check for null
List<String> lines = FileManager.readLinesFromFile("config.txt");
if (lines != null) {
    processConfig(lines);
} else {
    useDefaultConfig();
}

// ❌ Sai - assume success
List<String> lines = FileManager.readLinesFromFile("config.txt");
for (String line : lines) { // NullPointerException if null!
    processConfig(line);
}
```

---

### 2. Handle Errors Gracefully

```java
// ✅ Đúng - provide defaults
int highScore = FileManager.loadHighscore();
// Returns 0 if error - game continues

// ❌ Sai - crash on error
int highScore = loadHighScoreOrDie();
// throws exception → game crash → poor UX
```

---

### 3. Use Atomic Writes

```java
// ✅ Đúng - atomic write (built into FileManager)
FileManager.saveHighscore(score);
// Uses writeFileAtomic internally
// Never corrupts file

// ❌ Sai - direct write (can corrupt)
Files.write(path, data); 
// If crash during write → file corrupted
```

---

### 4. Synchronize File Access

```java
// ✅ Đúng - synchronized in FileManager
synchronized (LOCK) {
    Files.readString(path);
}

// ❌ Sai - no synchronization
Files.readString(path);
// Concurrent access → data races
```

---

## Testing

### Unit Tests

```java
@Test
public void testSaveAndLoadHighscore() {
    int score = 1000;
    FileManager.saveHighscore(score);
    int loaded = FileManager.loadHighscore();
    assertEquals(score, loaded);
}

@Test
public void testLoadNonexistentHighscore() {
    // Delete highscore file if exists
    int score = FileManager.loadHighscore();
    assertEquals(0, score); // Default value
}

@Test
public void testNegativeHighscoreClamped() {
    FileManager.saveHighscore(-100);
    int loaded = FileManager.loadHighscore();
    assertEquals(0, loaded); // Clamped to 0
}

@Test
public void testSaveAndLoadAudioSettings() {
    FileManager.saveAudioSettings(0.75, true);
    double[] settings = FileManager.loadAudioSettings();
    assertNotNull(settings);
    assertEquals(0.75, settings[0], 0.01);
    assertEquals(1.0, settings[1], 0.01); // muted
}
```

---

## Kết luận

`FileManager` class là **persistent storage layer** cho Arkanoid game:

- **Safe File I/O**: Atomic writes prevent corruption
- **Thread Safety**: Synchronized access prevents races
- **Error Handling**: Graceful degradation với defaults
- **Cross-Platform**: Works on Windows, Linux, macOS
- **User Friendly**: Dialogs inform users of save errors
- **Organized Storage**: Hidden app directory keeps data clean

FileManager exemplifies **robust file handling**. Atomic writes ensure data integrity even during crashes. Synchronization prevents concurrent access issues. Graceful error handling means game continues even if file operations fail. Clear error messages help users understand problems.

**Design Philosophy**: "Fail-Safe" design. Prefer returning default values over crashing. Inform users of errors but don't block gameplay. Atomic operations ensure data consistency. Thread safety prevents subtle bugs. Cross-platform code works everywhere.

**Key Innovation**: Atomic write implementation. Temporary file + rename strategy is **industry standard** for safe file updates. Used by databases, text editors, và many critical systems. Ensures users never see corrupted data - they always see complete old version or complete new version, never partial write.

