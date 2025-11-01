package Render;

import javafx.scene.image.Image;
import java.util.List;
import java.util.Objects;

/**
 * <p>Lớp đại diện cho một hoạt ảnh (Animation) bao gồm một chuỗi các khung hình (frames).</p>
 * <p>Nó quản lý việc chuyển đổi giữa các khung hình dựa trên thời gian và chế độ hoạt động (lặp/chạy một lần).</p>
 */
public class Animation {

    /**
     * Enum định nghĩa các chế độ chạy hoạt ảnh.
     */
    public enum AnimationMode {
        // Hoạt ảnh sẽ lặp lại vô tận
        LOOP,
        // Hoạt ảnh sẽ chỉ chạy một lần rồi dừng
        ONCE;
    }

    // Danh sách các khung hình (Image) tạo nên hoạt ảnh
    private final List<Image> frames;
    // Chỉ số của khung hình hiện tại đang được hiển thị
    private int currentFrameIndex;
    // Thời gian (mili giây) hiển thị mỗi khung hình
    private long frameDuration;
    // Thời điểm (timestamp) của lần cập nhật khung hình cuối cùng
    private long lastFrameTime;
    // Cờ báo hiệu hoạt ảnh có lặp lại không (deprecated, thay bằng mode)
    private boolean loop;
    // Cờ báo hiệu hoạt ảnh có đang chạy không
    private boolean playing;
    // Chế độ hoạt ảnh: LOOP hoặc ONCE
    private AnimationMode mode;
    // Cờ báo hiệu hoạt ảnh có đang chạy ngược không
    private boolean reversed;

    /**
     * Constructor tạo một đối tượng Animation mới.
     *
     * @param frames Danh sách các {@link Image} tạo nên hoạt ảnh. Không được null hoặc rỗng.
     * @param frameDuration Thời gian (mili giây) hiển thị mỗi khung hình.
     * @param loop true nếu hoạt ảnh nên lặp lại (LOOP mode), false nếu chỉ chạy một lần (ONCE mode).
     * @throws NullPointerException nếu danh sách frames là null.
     * @throws IllegalArgumentException nếu danh sách frames rỗng.
     */
    public Animation(List<Image> frames, long frameDuration, boolean loop) {
        this.frames = Objects.requireNonNull(frames, "Frames list cannot be null");
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("Frames list cannot be empty");
        }
        this.frameDuration = frameDuration;
        this.loop = loop; // Dùng để khởi tạo mode
        this.mode = loop ? AnimationMode.LOOP : AnimationMode.ONCE;
        this.currentFrameIndex = 0;
        this.playing = false;
        this.lastFrameTime = 0;
        this.reversed = false;
    }

    /**
     * Cập nhật trạng thái của hoạt ảnh, chuyển sang khung hình tiếp theo nếu đủ thời gian.
     * Phương thức này nên được gọi trong vòng lặp game/render chính.
     */
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

    /**
     * Bắt đầu hoặc tiếp tục chạy hoạt ảnh từ khung hình hiện tại.
     */
    public void play() {
        // Nếu đã kết thúc (chỉ áp dụng cho chế độ ONCE), đặt lại trước khi chạy
        if (isFinished()) {
            reset();
        }
        this.playing = true;
        // Đặt lại thời gian để khung hình đầu tiên xuất hiện ngay lập tức
        this.lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Bắt đầu chạy hoạt ảnh theo hướng ngược lại, bắt đầu từ khung hình cuối cùng.
     */
    public void playReversed() {
        this.reversed = true;
        this.currentFrameIndex = frames.size() - 1; // Bắt đầu từ khung cuối
        this.playing = true;
        this.lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Tạm dừng hoạt ảnh.
     */
    public void pause() {
        this.playing = false;
    }

    /**
     * Dừng hoạt ảnh và đặt lại về khung hình ban đầu (hoặc khung cuối nếu đang reversed).
     */
    public void stop() {
        this.playing = false;
        reset();
    }

    /**
     * Đặt lại chỉ số khung hình về vị trí ban đầu (0 nếu xuôi, size-1 nếu ngược).
     */
    public void reset() {
        // Đặt lại index tùy thuộc vào hướng chạy
        this.currentFrameIndex = reversed ? frames.size() - 1 : 0;
        this.lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Kiểm tra xem hoạt ảnh đã hoàn thành chưa (chỉ true trong chế độ ONCE).
     *
     * @return true nếu hoạt ảnh đã kết thúc và không lặp lại, false nếu đang chạy hoặc ở chế độ lặp.
     */
    public boolean isFinished() {
        if (reversed) {
            // Đã kết thúc nếu không lặp và chỉ số khung hình <= 0 (khung đầu)
            return !loop && currentFrameIndex <= 0;
        } else {
            // Đã kết thúc nếu không lặp và chỉ số khung hình đạt khung cuối
            return !loop && currentFrameIndex >= frames.size() - 1;
        }
    }

    /**
     * Lấy đối tượng {@link Image} của khung hình hiện tại.
     *
     * @return Khung hình hiện tại của hoạt ảnh.
     */
    public Image getCurrentFrame() {
        return frames.get(currentFrameIndex);
    }

    /**
     * Kiểm tra xem hoạt ảnh có đang chạy không.
     *
     * @return true nếu đang chạy, false nếu đang tạm dừng hoặc đã kết thúc.
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Lấy tổng số khung hình trong hoạt ảnh.
     *
     * @return Số lượng khung hình.
     */
    public int getFrameCount() {
        return frames.size();
    }

    /**
     * Kiểm tra xem hoạt ảnh có đang chạy ngược không.
     *
     * @return true nếu hoạt ảnh đang chạy ngược.
     */
    public boolean isReversed() {
        return reversed;
    }

    /**
     * Đặt hướng chạy của hoạt ảnh.
     *
     * @param reversed true để đặt hoạt ảnh chạy ngược, false để chạy xuôi.
     */
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }
}