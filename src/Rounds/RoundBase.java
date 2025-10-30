package Rounds;

import Objects.Bricks.Brick;
import Utils.Constants;
//import Audio.MusicTrack;
import java.util.List;

/**
 * <h2>Lớp cơ sở cho mọi vòng chơi (Round) trong game Brick Breaker</h2>
 * <p>
 * Đây là lớp trừu tượng (abstract) định nghĩa cấu trúc chung cho tất cả các vòng chơi.
 * Mỗi vòng (Round1, Round2, Round3, ...) sẽ kế thừa lớp này và định nghĩa cách sắp xếp gạch riêng
 * bằng cách ghi đè (override) phương thức {@link #createBricks()}.
 * </p>
 *
 * <p><b>Chức năng chính:</b></p>
 * <ul>
 *     <li>Chứa thông tin định danh của vòng chơi (số thứ tự, tên, độ khó, mô tả).</li>
 *     <li>Cung cấp kích thước khu vực chơi (play area) lấy từ {@link Constants.PlayArea}.</li>
 *     <li>Định nghĩa giao diện bắt buộc để tạo danh sách gạch (bricks) cho vòng chơi.</li>
 * </ul>
 *
 * <p><b>Cách sử dụng:</b></p>
 * <pre>
 * public class Round1 extends RoundBase {
 *     public Round1() {
 *         super(1, "Beginner's Challenge", "Màn mở đầu dễ nhất", 1);
 *     }
 *
 *     @Override
 *     public List&lt;Brick&gt; createBricks() {
 *         // Khởi tạo danh sách gạch, định dạng bố cục vòng chơi
 *         return ...
 *     }
 * }
 * </pre>
 */
public abstract class RoundBase {

    protected int roundNumber;
    protected String roundName;
    protected String roundDescription;
    /**
     * Mức độ khó của vòng chơi:
     * <ul>
     *     <li>1 - Dễ (Beginner)</li>
     *     <li>2 - Trung bình (Intermediate)</li>
     *     <li>3 - Khó (Advanced)</li>
     *     <li>4 - Rất khó (Expert)</li>
     * </ul>
     */
    protected int difficultyLevel;
    protected double playAreaWidth;
    protected double playAreaHeight;


    /**
     * Hàm khởi tạo cho lớp cơ sở RoundBase.
     *
     * @param roundNumber     Số thứ tự của vòng chơi (ví dụ: 1, 2, 3)
     * @param roundName       Tên vòng chơi hiển thị trong UI
     * @param roundDescription Mô tả ngắn gọn (tùy chọn)
     * @param difficultyLevel  Mức độ khó (1 = dễ, 2 = trung bình, 3 = khó, ...)
     */
    public RoundBase(int roundNumber, String roundName, String roundDescription, int difficultyLevel) {
        this.roundNumber = roundNumber;
        this.roundName = roundName;
        this.roundDescription = roundDescription;
        this.difficultyLevel = difficultyLevel;

        // Lấy kích thước vùng chơi từ Constants để các round có thể dùng trực tiếp
        this.playAreaWidth = Constants.PlayArea.PLAY_AREA_WIDTH;
        this.playAreaHeight = Constants.PlayArea.PLAY_AREA_HEIGHT;
    }


    /**
     * <b>Phương thức trừu tượng bắt buộc override</b>.
     * <p>
     * Mỗi vòng chơi phải tự định nghĩa bố cục và loại gạch (Normal, Silver, Gold, ...)
     * thông qua phương thức này.
     * </p>
     *
     * @return Danh sách các đối tượng gạch ({@link Brick}) được tạo ra trong vòng chơi.
     */
    public abstract List<Brick> createBricks();


    /**
     * (Tùy chọn) Trả về bản nhạc nền tương ứng cho vòng chơi.
     * Nếu không sử dụng nhạc nền, có thể để comment như bên dưới.
     */
    //    public abstract MusicTrack getMusicTrack();


    // ====================== GETTERS ======================

    /**
     * Lấy số thứ tự của vòng chơi.
     *
     * @return roundNumber
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * Lấy tên vòng chơi.
     *
     * @return roundName
     */
    public String getRoundName() {
        return roundName;
    }

    /**
     * Lấy mô tả ngắn của vòng chơi.
     *
     * @return roundDescription
     */
    public String getRoundDescription() {
        return roundDescription;
    }

    /**
     * Lấy mức độ khó của vòng chơi.
     *
     * @return difficultyLevel
     */
    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    /**
     * Tính tổng số gạch trong vòng chơi.
     * <p>
     * Hữu ích cho việc hiển thị thông tin, tính điểm hoặc xác định điều kiện hoàn thành.
     * </p>
     *
     * @return Tổng số gạch được tạo ra.
     */
    public int getTotalBrickCount() {
        return createBricks().size();
    }
}
