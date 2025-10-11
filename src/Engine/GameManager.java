package Engine;

import Objects.GameEntities.Ball;
import Objects.GameEntities.Paddle;
import Objects.Bricks.Brick;
import Objects.Bricks.NormalBrick;
import Objects.Bricks.SilverBrick;
import GeometryPrimitives.Point;
import GeometryPrimitives.Rectangle;
import GeometryPrimitives.Velocity;
import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý trạng thái và quy tắc chơi của trò Arkanoid.
 *
 * Nhiệm vụ chính:
 * - Tạo và giữ tham chiếu tới các đối tượng chính: {@link Objects.Paddle}, {@link Objects.Ball}
 *   và danh sách {@code bricks}.
 * - Cập nhật trạng thái mỗi frame (vị trí, va chạm, tính điểm, số mạng).
 * - Kiểm soát logic win/lose, reset và phóng bóng (launch).
 *
 * Trạng thái quan trọng:
 * - {@code lives}: số mạng còn lại.
 * - {@code score}: điểm hiện tại.
 * - {@code ballAttached}: khi true, bóng dính vào paddle và chưa được phóng.
 *76
 * Lưu ý thiết kế:
 * - GameManager hiện giữ các đối tượng công khai (public) để ví dụ đơn giản; trong
 *   ứng dụng lớn hơn nên dùng getter/setter hoặc API riêng để đóng gói trạng thái.
 */
public class GameManager {
    public Paddle paddle;
    public Ball ball;
    public List<Brick> bricks = new ArrayList<>();

    public int width;
    public int height;
    public int lives = 3;
    public boolean gameOver = false;
    public boolean won = false;
    public int score = 0;
    private int nextBrickScore = 50; // điểm cộng cho viên gạch bị phá tiếp theo
    public boolean ballAttached = true; // khi true, bóng dính vào paddle cho tới khi được phóng

    /**
     * Tạo GameManager với kích thước vùng chơi.
     *
     * @param width  chiều rộng vùng chơi (pixel)
     * @param height chiều cao vùng chơi (pixel)
     */
    public GameManager(int width, int height) {
        this.width = width; this.height = height;
        initDemo();
    }

    /**
     * Khởi tạo trạng thái demo: tạo grid brick, paddle và đặt bóng ban đầu.
     * Phương thức này được dùng bởi constructor và khi reset game.
     */
    private void initDemo() {
        // Brick grid parameters
        int cols = 6;
        int rows = 3;
        double brickW = 60.0;
        double brickH = 20.0;
        double hSpacing = 5.0; // khoảng cách ngang giữa các viên gạch
        double vSpacing = 5.0; // khoảng cách dọc giữa các hàng gạch

        // paddle nên có chiều rộng gấp đôi viên gạch và chiều cao bằng một nửa viên gạch
        double paddleW = brickW * 2.0;
        double paddleH = Math.max(4.0, brickH / 2.0);
        paddle = new Paddle((width - paddleW) / 2.0, height - 40, paddleW, paddleH, 6);

        // bóng ban đầu đặt ở giữa phía trên paddle và dính (attached)
        ball = new Ball((width/2.0) - 8, height - 60, 8, new Velocity(0, 0));
        ballAttached = true;

        // compute centered start X for the whole grid
        double totalWidth = cols * brickW + (cols - 1) * hSpacing;
        double startX = (width - totalWidth) / 2.0;
        double startY = 50.0;

        java.util.Random rnd = new java.util.Random();
        bricks.clear();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double x = startX + c * (brickW + hSpacing);
                double y = startY + r * (brickH + vSpacing);
                if (rnd.nextBoolean()) {
                    bricks.add(new NormalBrick(x, y, brickW, brickH));
                } else {
                    bricks.add(new SilverBrick(x, y, brickW, brickH, 2));
                }
            }
        }
    }

    /**
     * Cập nhật trạng thái game cho mỗi frame.
     *
     * Hành vi chính:
     * - Cập nhật paddle và ball (nếu đã phóng).
     * - Xử lý va chạm bóng với tường, paddle và brick.
     * - Kiểm tra điều kiện thua (mất mạng) hoặc thắng (không còn brick sống).
     */
    public void update() {
        if (gameOver) return;
        // update objects
        paddle.update();
        // if ball is attached to paddle, keep it positioned and skip physics until launch
        if (ballAttached) {
            double paddleCenterX = paddle.getBounds().getUpperLeft().getX() + paddle.getBounds().getWidth() / 2.0;
            double ballCenterY = paddle.getBounds().getUpperLeft().getY() - ball.getBounds().getHeight() / 2.0 - 1.0;
            ball.setCenter(new Point(paddleCenterX, ballCenterY));
            return;
        }
        ball.update();

        // wall collision for ball
        Rectangle bounds = new Rectangle(new Point(0,0), width, height);
        if (ball.getBounds().getUpperLeft().getX() <= 0 || ball.getBounds().getUpperLeft().getX() + ball.getBounds().getWidth() >= width) {
            ball.setVelocity(new Velocity(-ball.getVelocity().getDx(), ball.getVelocity().getDy()));
        }
        if (ball.getBounds().getUpperLeft().getY() <= 0) {
            ball.setVelocity(new Velocity(ball.getVelocity().getDx(), -ball.getVelocity().getDy()));
        }

        // ball fell below bottom
        if (ball.getBounds().getUpperLeft().getY() > height) {
            // mất một mạng
            lives--;
            // trừ 500 điểm khi mất mạng, nhưng không thấp hơn 0
            score = Math.max(0, score - 500);
            if (lives <= 0) {
                // game over due to no lives left
                gameOver = true;
                // ensure won is false when dying with bricks remaining
                won = false;
            } else {
                // reset ball above paddle and attach
                ball = new Ball(paddle.getBounds().getUpperLeft().getX() + paddle.getBounds().getWidth()/2.0 - 8,
                        paddle.getBounds().getUpperLeft().getY() - 20, 8, new Velocity(0, 0));
                ballAttached = true;
            }
            return;
        }

        // ball vs bricks
        for (Brick b : new ArrayList<>(bricks)) {
            if (!b.isAlive()) continue;
            boolean beforeAlive = b.isAlive();
            if (ball.checkCollisionWithRect(b.getBounds())) {
                b.takeHit();
                if (beforeAlive && !b.isAlive()) {
                    // bị phá — cộng điểm theo cơ chế tăng dần
                    score += nextBrickScore;
                    // increment nextBrickScore depending on type
                    if (b instanceof SilverBrick) nextBrickScore += 20; else nextBrickScore += 10;
                }
            }
        }

        // check win: if no bricks alive, player wins
        boolean anyAlive = false;
        for (Brick b : bricks) { if (b.isAlive()) { anyAlive = true; break; } }
        if (!anyAlive) {
            // Chỉ tính là thắng nếu người chơi còn ít nhất một mạng
            gameOver = true;
            won = (lives > 0);
            return;
        }

        // ball vs paddle
        if (ball.checkCollisionWithRect(paddle.getBounds())) {
            // Tùy chọn: điều chỉnh phản xạ dựa trên vị trí chạm trên paddle để thay đổi góc
            // compute offset from paddle center
            double paddleCenterX = paddle.getBounds().getUpperLeft().getX() + paddle.getBounds().getWidth() / 2.0;
            double diff = ball.getCenter().getX() - paddleCenterX;
            double norm = diff / (paddle.getBounds().getWidth() / 2.0); // -1..1
            // adjust dx proportionally (small factor)
            double baseSpeedX = ball.getVelocity().getDx();
            ball.setVelocity(new Velocity(baseSpeedX + norm * 1.5, ball.getVelocity().getDy()));
        }

        // paddle bounds clamp
        if (paddle.getBounds().getUpperLeft().getX() < 0) paddle.setX(0);
        if (paddle.getBounds().getUpperLeft().getX() + paddle.getBounds().getWidth() > width) paddle.setX(width - paddle.getBounds().getWidth());
    }

    /**
     * Đặt lại trò chơi về trạng thái ban đầu (số mạng, điểm, bảng brick,...).
     * Được gọi khi người chơi muốn chơi lại.
     */
    public void reset() {
        lives = 3;
        gameOver = false;
        won = false;
        score = 0;
        nextBrickScore = 50;
        bricks.clear();
        initDemo();
    }

    /**
     * Phóng bóng từ paddle nếu bóng đang dính (attached) và game chưa kết thúc.
     * Phương thức đặt {@code ballAttached=false} và gán vận tốc ban đầu cho bóng.
     */
    public void launchBall() {
        if (!ballAttached || gameOver) return;
        // gán vận tốc ban đầu hướng lên cho bóng
        ballAttached = false;
        // phóng thẳng lên (không có thành phần ngang)
        ball.setVelocity(new Velocity(0, -2));
    }

}
