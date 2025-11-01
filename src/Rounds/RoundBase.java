package Rounds;

import Objects.Bricks.Brick;
import Utils.Constants;
import java.util.List;

/**
 * Lớp trừu tượng RoundBase là lớp cơ sở cho tất cả các cấp độ (Rounds) trong game.
 * Nó định nghĩa các thuộc tính chung của một cấp độ như số thứ tự, tên,
 * và kích thước khu vực chơi.
 */
public abstract class RoundBase {
    // Số thứ tự của cấp độ (ví dụ: 1, 2, 3)
    protected int roundNumber;
    // Tên mô tả của cấp độ (ví dụ: "Beginner's Challenge")
    protected String roundName;
    // Chiều rộng và chiều cao của khu vực chơi, lấy từ Constants
    protected double playAreaWidth;
    protected double playAreaHeight;

    /**
     * Constructor khởi tạo cấp độ.
     * Thiết lập số và tên cấp độ, đồng thời lấy kích thước khu vực chơi cố định.
     *
     * @param roundNumber Số thứ tự của cấp độ.
     * @param roundName Tên của cấp độ.
     */
    public RoundBase(int roundNumber, String roundName) {
        this.roundNumber = roundNumber;
        this.roundName = roundName;
        // Lấy kích thước khu vực chơi từ Constants
        this.playAreaWidth = Constants.PlayArea.PLAY_AREA_WIDTH;
        this.playAreaHeight = Constants.PlayArea.PLAY_AREA_HEIGHT;
    }

    /**
     * Phương thức trừu tượng, bắt buộc các lớp con phải triển khai
     * để định nghĩa bố cục và loại gạch cụ thể cho cấp độ đó.
     *
     * @return Danh sách các đối tượng Brick đã được đặt vị trí.
     */
    public abstract List<Brick> createBricks();

//    /**
//     * (Hàm đã được comment out trong code gốc)
//     * Phương thức trừu tượng để định nghĩa bản nhạc nền cho cấp độ.
//     // * @return Bản nhạc nền (MusicTrack).
//     */
//    public abstract MusicTrack getMusicTrack();

    /**
     * Lấy số thứ tự của cấp độ.
     * @return roundNumber.
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * Lấy tên của cấp độ.
     * @return roundName.
     */
    public String getRoundName() {
        return roundName;
    }

    /**
     * Tính tổng số gạch ban đầu trong cấp độ.
     * Lưu ý: Phương thức này gọi createBricks() nên có thể tốn kém nếu gọi nhiều lần.
     *
     * @return Tổng số viên gạch.
     */
    public int getTotalBrickCount() {
        // Gọi createBricks() để tạo và đếm số gạch
        return createBricks().size();
    }
}