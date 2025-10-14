package Render;

import javafx.scene.image.Image;
import java.util.List;
import java.util.Objects;

/**
 * Lớp lõi để quản lý và phát các chuỗi ảnh động (sprite sequence).
 * Cho phép phát hoạt ảnh với nhiều chế độ khác nhau: lặp lại, phát một lần, hoặc ping-pong.
 *
 * Giải thích tổng quan:
 * - Lưu trữ danh sách các khung hình (frames) dạng Image.
 * - Điều khiển tốc độ chuyển khung hình, trạng thái phát/tạm dừng/dừng.
 * - Hỗ trợ các chế độ phát: lặp lại (LOOP), một lần (ONCE), ping-pong (PING_PONG).
 */
public class Animation {
    /**
     * Enum xác định chế độ phát hoạt ảnh:
     * LOOP: Lặp lại liên tục.
     * ONCE: Phát một lần rồi dừng.
     * PING_PONG: Phát tới cuối rồi phát ngược lại, lặp đi lặp lại.
     */
    public enum AnimationMode {
        LOOP,
        ONCE,
        PING_PONG
    }

    /**
     * Danh sách các khung hình (ảnh) của hoạt ảnh.
     */
    private final List<Image> frames;
    /**
     * Chỉ số khung hình hiện tại đang được hiển thị.
     */
    private int currentFrameIndex;
    /**
     * Thời lượng mỗi khung hình (tính bằng mili giây).
     */
    private long frameDuration; // mili giây cho mỗi khung hình
    /**
     * Thời điểm chuyển sang khung hình tiếp theo lần cuối cùng.
     */
    private long lastFrameTime;
    /**
     * Cờ xác định có lặp lại hoạt ảnh hay không.
     */
    private boolean loop;
    /**
     * Cờ xác định hoạt ảnh đang phát hay tạm dừng.
     */
    private boolean playing;
    /**
     * Chế độ phát hiện tại của hoạt ảnh.
     */
    private AnimationMode mode;

    /**
     * Biến phụ trợ cho chế độ PING_PONG: true nếu đang phát xuôi, false nếu đang phát ngược.
     */
    private boolean pingPongForward = true;

    /**
     * Hàm khởi tạo Animation.
     * @param frames Danh sách các khung hình (Image) cho hoạt ảnh. Không được rỗng.
     * @param frameDuration Thời lượng mỗi khung hình (mili giây), phải > 0.
     * @param loop Có lặp lại hoạt ảnh hay không.
     *
     * - Kiểm tra frames không null và không rỗng.
     * - Thiết lập trạng thái ban đầu: dừng phát, khung hình đầu tiên.
     */
    public Animation(List<Image> frames, long frameDuration, boolean loop) {
        Objects.requireNonNull(frames, "frames must not be null");
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("frames must not be empty");
        }
        if (frameDuration <= 0) {
            throw new IllegalArgumentException("frameDuration must be > 0");
        }

        this.frames = frames;
        this.frameDuration = frameDuration;
        this.loop = loop;
        this.mode = loop ? AnimationMode.LOOP : AnimationMode.ONCE;
        this.currentFrameIndex = 0;
        this.playing = false;
        this.lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Cho phép thay đổi chế độ phát hoạt ảnh khi đang chạy.
     * Khi chuyển sang chế độ PING_PONG sẽ reset hướng phát.
     * @param mode Chế độ phát mới.
     */
    public void setMode(AnimationMode mode) {
        this.mode = Objects.requireNonNull(mode);
        this.loop = (mode == AnimationMode.LOOP);
        // Reset hướng phát khi chuyển chế độ
        this.pingPongForward = true;
    }

    /**
     * Lấy chế độ phát hiện tại.
     */
    public AnimationMode getMode() {
        return mode;
    }

    /**
     * Bắt đầu phát hoạt ảnh.
     * Nếu đã phát xong thì reset về đầu.
     * Đặt playing = true và cập nhật thời gian bắt đầu.
     */
    public void play() {
        if (isFinished()) {
            reset();
        }
        this.playing = true;
        this.lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Tạm dừng phát hoạt ảnh (playing = false).
     */
    public void pause() {
        this.playing = false;
    }

    /**
     * Dừng phát hoạt ảnh và reset về trạng thái ban đầu.
     */
    public void stop() {
        this.playing = false;
        reset();
    }

    /**
     * Đặt lại hoạt ảnh về khung hình đầu tiên, hướng phát xuôi, cập nhật thời gian.
     */
    public void reset() {
        this.currentFrameIndex = 0;
        this.pingPongForward = true;
        this.lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Cập nhật trạng thái hoạt ảnh, chuyển khung hình nếu đủ thời gian.
     * - Nếu không ở trạng thái playing thì không làm gì.
     * - Nếu đã đủ frameDuration kể từ lần cập nhật trước thì chuyển khung hình tiếp theo.
     * - Xử lý logic chuyển khung hình cho từng chế độ (LOOP, ONCE, PING_PONG).
     */
    public void update() {
        if (!playing) return;

        long now = System.currentTimeMillis();
        if (now - lastFrameTime < frameDuration) return;

        // Chuyển khung hình tiếp theo tùy theo chế độ
        if (mode == AnimationMode.PING_PONG) {
            if (pingPongForward) {
                currentFrameIndex++;
                if (currentFrameIndex >= frames.size()) {
                    if (loop) {
                        // Đảo chiều, lùi lại 1 khung hình
                        currentFrameIndex = Math.max(frames.size() - 2, 0);
                        pingPongForward = false;
                    } else {
                        // Đến cuối, dừng lại ở khung cuối
                        currentFrameIndex = frames.size() - 1;
                        playing = false;
                    }
                }
            } else {
                currentFrameIndex--;
                if (currentFrameIndex < 0) {
                    if (loop) {
                        // Đảo chiều, tiến lên 1 khung hình
                        currentFrameIndex = Math.min(1, frames.size() - 1);
                        pingPongForward = true;
                    } else {
                        // Đến đầu, dừng lại ở khung đầu
                        currentFrameIndex = 0;
                        playing = false;
                    }
                }
            }
        } else { // LOOP hoặc ONCE
            currentFrameIndex++;
            if (currentFrameIndex >= frames.size()) {
                if (loop) {
                    currentFrameIndex = 0;
                } else {
                    // Dừng lại ở khung cuối
                    currentFrameIndex = frames.size() - 1;
                    playing = false;
                }
            }
        }

        this.lastFrameTime = now;
    }

    /**
     * Lấy khung hình hiện tại (Image) của hoạt ảnh.
     */
    public Image getCurrentFrame() {
        return frames.get(currentFrameIndex);
    }

    /**
     * Kiểm tra hoạt ảnh đã phát xong chưa (chỉ áp dụng với chế độ không lặp).
     * @return true nếu đã phát xong, false nếu chưa.
     */
    public boolean isFinished() {
        if (mode == AnimationMode.PING_PONG) {
            // Với ping-pong: kết thúc khi không lặp, đã dừng và ở đầu/cuối chuỗi
            return !loop && !playing && (currentFrameIndex == 0 || currentFrameIndex == frames.size() - 1);
        }
        return !loop && currentFrameIndex == frames.size() - 1;
    }

    /**
     * Kiểm tra hoạt ảnh đang phát hay không.
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Thay đổi tốc độ phát hoạt ảnh (thời lượng mỗi khung hình).
     * @param duration Thời lượng mới (mili giây), phải > 0.
     */
    public void setFrameDuration(long duration) {
        if (duration <= 0) throw new IllegalArgumentException("duration phải > 0");
        this.frameDuration = duration;
    }

    /**
     * Lấy tổng thời lượng phát hết hoạt ảnh (không tính lặp).
     * @return Tổng thời gian (mili giây).
     */
    public long getTotalDuration() {
        return frames.size() * frameDuration;
    }

    /**
     * Lấy tiến trình phát hiện tại (từ 0 đến <1).
     * @return Tỉ lệ khung hình hiện tại trên tổng số khung hình.
     */
    public double getProgress() {
        return currentFrameIndex / (double) frames.size();
    }

    /**
     * Lấy chỉ số khung hình hiện tại.
     */
    public int getCurrentFrameIndex() {
        return currentFrameIndex;
    }

    /**
     * Lấy tổng số khung hình của hoạt ảnh.
     */
    public int getFrameCount() {
        return frames.size();
    }
}