package Render;

import javafx.scene.image.Image;
import java.util.List;
import java.util.Objects;

public class Animation {
    public enum AnimationMode {
        LOOP,
        ONCE;
    }

    private final List<Image> frames;
    private int currentFrameIndex;
    private long frameDuration; // mili giây cho mỗi khung hình
    private long lastFrameTime;
    private boolean loop;
    private boolean playing;
    private AnimationMode mode;
    private boolean reversed; // Để chạy animation ngược lại

    public Animation(List<Image> frames, long frameDuration, boolean loop) {
        this.frames = Objects.requireNonNull(frames, "Frames list cannot be null");
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("Frames list cannot be empty");
        }
        this.frameDuration = frameDuration;
        this.loop = loop;
        this.mode = loop ? AnimationMode.LOOP : AnimationMode.ONCE;
        this.currentFrameIndex = 0;
        this.playing = false;
        this.lastFrameTime = 0;
        this.reversed = false;
    }

    public void update() {
        if (!playing) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= frameDuration) {
            if (reversed) {
                currentFrameIndex--;
                if (currentFrameIndex < 0) {
                    if (loop) {
                        currentFrameIndex = frames.size() - 1;
                    } else {
                        currentFrameIndex = 0;
                        playing = false;
                    }
                }
            } else {
                currentFrameIndex++;
                if (currentFrameIndex >= frames.size()) {
                    if (loop) {
                        currentFrameIndex = 0;
                    } else {
                        currentFrameIndex = frames.size() - 1;
                        playing = false;
                    }
                }
            }
            lastFrameTime = currentTime;
        }
    }

    public void play() {
        if (isFinished()) {
            reset();
        }
        this.playing = true;
        this.lastFrameTime = System.currentTimeMillis();
    }

    public void playReversed() {
        this.reversed = true;
        this.currentFrameIndex = frames.size() - 1;
        this.playing = true;
        this.lastFrameTime = System.currentTimeMillis();
    }

    public void pause() {
        this.playing = false;
    }

    public void stop() {
        this.playing = false;
        reset();
    }

    public void reset() {
        this.currentFrameIndex = reversed ? frames.size() - 1 : 0;
        this.lastFrameTime = System.currentTimeMillis();
    }

    public boolean isFinished() {
        if (reversed) {
            return !loop && currentFrameIndex <= 0;
        } else {
            return !loop && currentFrameIndex >= frames.size() - 1;
        }
    }

    public Image getCurrentFrame() {
        return frames.get(currentFrameIndex);
    }

    public boolean isPlaying() {
        return playing;
    }

    public int getFrameCount() {
        return frames.size();
    }

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }
}
