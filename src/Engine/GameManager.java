package Engine;

import Objects.GameEntities.Ball;
import Objects.GameEntities.Paddle;
import Objects.Bricks.BrickType;
import Objects.Bricks.Brick;
import Objects.Bricks.NormalBrick;
import Objects.Bricks.SilverBrick;
import GeometryPrimitives.Point;
import GeometryPrimitives.Velocity;
import Utils.Constants;
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
    
    // PowerUp Manager
    private PowerUpManager powerUpManager;

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
     * LƯU Ý: width và height là kích thước PLAY AREA (không bao gồm UI bar).
     * CanvasRenderer sẽ thêm offset khi render.
     *
     * @param width  chiều rộng vùng chơi (pixel) - thường là 600
     * @param height chiều cao vùng chơi (pixel) - thường là 650 (800 - 150 UI bar)
     */
    public GameManager(int width, int height) {
        this.width = width; 
        this.height = height;
        
        // Initialize PowerUpManager
        this.powerUpManager = PowerUpManager.getInstance();
        this.powerUpManager.setGameManager(this);
        
        initDemo();
    }

    /**
     * Khởi tạo trạng thái demo: tạo grid brick, paddle và đặt bóng ban đầu.
     * Phương thức này được dùng bởi constructor và khi reset game.
     */
    private void initDemo() {
        // Brick grid parameters - use actual sprite dimensions
        int cols = 13;
        int rows = 4;
        double brickW = Constants.Bricks.BRICK_WIDTH; // 32px
        double brickH = Constants.Bricks.BRICK_HEIGHT; // 21px
        double hSpacing = Constants.Bricks.BRICK_H_SPACING; // 2px
        double vSpacing = Constants.Bricks.BRICK_V_SPACING; // 2px

        // Paddle - use actual sprite dimensions
        double paddleW = Constants.Physics.PADDLE_WIDTH; // 79px
        double paddleH = Constants.Physics.PADDLE_HEIGHT; // 20px
        paddle = new Paddle((width - paddleW) / 2.0, height - 60, paddleW, paddleH, Constants.Physics.PADDLE_SPEED);
        
        // Trigger materialize animation when entering level
        paddle.playMaterializeAnimation();

        // Ball - use actual sprite dimensions (10x10, radius=5)
        double ballSize = Constants.Physics.BALL_SIZE; // 10px
        double ballRadius = Constants.Physics.BALL_RADIUS; // 5px
        ball = new Ball((width/2.0) - ballRadius, height - 80, ballRadius, new Velocity(0, 0));
        ballAttached = true;

        // Compute centered start X for the whole grid
        double totalWidth = cols * brickW + (cols - 1) * hSpacing;
        double startX = (width - totalWidth) / 2.0;
        double startY = Constants.Bricks.BRICK_START_Y; // 100px from top

        java.util.Random rnd = new java.util.Random();
        bricks.clear();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double x = startX + c * (brickW + hSpacing);
                double y = startY + r * (brickH + vSpacing);
                if (rnd.nextBoolean()) {
                    bricks.add(new NormalBrick(BrickType.BLUE, x, y, brickW, brickH));
                } else {
                    bricks.add(new SilverBrick(x, y, brickW, brickH));
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
                    
                    // Spawn PowerUp from destroyed brick (30% chance)
                    double brickCenterX = b.getBounds().getUpperLeft().getX() + b.getBounds().getWidth() / 2.0;
                    double brickCenterY = b.getBounds().getUpperLeft().getY() + b.getBounds().getHeight() / 2.0;
                    // Use a default brick type for spawning (actual type doesn't affect spawn rate)
                    powerUpManager.spawnFromBrick(brickCenterX, brickCenterY, BrickType.BLUE);
                }
            }
        }
        
        // Update PowerUpManager (falling powerups and collision detection)
        powerUpManager.update(paddle);

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
            // Check if catch mode is enabled
            if (paddle.isCatchModeEnabled() && !ballAttached) {
                // Catch the ball - attach it to paddle
                ballAttached = true;
                ball.setVelocity(new Velocity(0, 0)); // Stop ball movement
                System.out.println("GameManager: Ball caught by paddle!");
            } else {
                // Normal paddle reflection
                // Adjust reflection based on hit position on paddle to change angle
                double paddleCenterX = paddle.getBounds().getUpperLeft().getX() + paddle.getBounds().getWidth() / 2.0;
                double diff = ball.getCenter().getX() - paddleCenterX;
                double norm = diff / (paddle.getBounds().getWidth() / 2.0); // -1..1
                // adjust dx proportionally (small factor)
                double baseSpeedX = ball.getVelocity().getDx();
                ball.setVelocity(new Velocity(baseSpeedX + norm * 1.5, ball.getVelocity().getDy()));
            }
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

    // ============================================================
    // PowerUp Effect Methods
    // ============================================================
    
    /**
     * Enables catch mode on paddle (CATCH PowerUp).
     * When enabled, ball sticks to paddle on collision.
     */
    public void enableCatchMode() {
        paddle.setCatchModeEnabled(true);
        System.out.println("GameManager: Catch mode enabled");
    }
    
    /**
     * Disables catch mode on paddle.
     */
    public void disableCatchMode() {
        paddle.setCatchModeEnabled(false);
        System.out.println("GameManager: Catch mode disabled");
    }
    
    /**
     * Gets current ball count.
     * @return Number of balls (currently always 1)
     */
    public int getBallCount() {
        return 1; // TODO: Support multiple balls in future
    }
    
    /**
     * Duplicates all balls (DUPLICATE PowerUp).
     * TODO: Implement multi-ball support with List<Ball>.
     */
    public void duplicateBalls() {
        System.out.println("GameManager: Balls duplicated (multi-ball support coming soon)");
        // TODO: When multi-ball is implemented:
        // - Create copy of current ball with ±45° angle
        // - Add to balls list
        // - Update collision detection to handle multiple balls
    }
    
    /**
     * Expands paddle width (EXPAND PowerUp).
     * @param multiplier Width multiplier (e.g., 1.5)
     */
    public void expandPaddle(double multiplier) {
        double currentWidth = paddle.getWidth();
        double newWidth = paddle.getOriginalWidth() * multiplier;
        paddle.setWidth(newWidth);
        paddle.setState(Objects.GameEntities.PaddleState.WIDE); // Trigger WIDE animation
        System.out.println("GameManager: Paddle expanded from " + currentWidth + " to " + newWidth);
    }
    
    /**
     * Reverts paddle to original size.
     */
    public void revertPaddleSize() {
        paddle.setWidth(paddle.getOriginalWidth());
        paddle.setState(Objects.GameEntities.PaddleState.NORMAL); // Back to NORMAL
        System.out.println("GameManager: Paddle size reverted to " + paddle.getOriginalWidth());
    }
    
    /**
     * Enables laser shooting on paddle (LASER PowerUp).
     * @param shots Number of laser shots available
     */
    public void enableLaser(int shots) {
        paddle.setLaserEnabled(true);
        paddle.setLaserShots(shots);
        System.out.println("GameManager: Laser enabled with " + shots + " shots");
    }
    
    /**
     * Disables laser shooting on paddle.
     */
    public void disableLaser() {
        paddle.setLaserEnabled(false);
        paddle.setLaserShots(0);
        System.out.println("GameManager: Laser disabled");
    }
    
    /**
     * Gets current number of lives.
     * @return Current lives count
     */
    public int getLives() {
        return lives;
    }
    
    /**
     * Adds one life (LIFE PowerUp).
     * Maximum 5 lives.
     */
    public void addLife() {
        if (lives < Constants.GameRules.MAX_LIVES) {
            lives++;
            System.out.println("GameManager: Life added! Lives: " + lives);
        } else {
            System.out.println("GameManager: Max lives reached (" + Constants.GameRules.MAX_LIVES + ")");
        }
    }
    
    /**
     * Slows down all balls (SLOW PowerUp).
     * @param multiplier Speed multiplier (e.g., 0.7)
     */
    public void slowBalls(double multiplier) {
        // Get current velocity
        Velocity currentVel = ball.getVelocity();
        double newDx = currentVel.getDx() * multiplier;
        double newDy = currentVel.getDy() * multiplier;
        ball.setVelocity(new Velocity(newDx, newDy));
        System.out.println("GameManager: Balls slowed by " + multiplier + "x");
    }
    
    /**
     * Restores original ball speed (removes SLOW effect).
     * Calculates original speed from current velocity direction.
     */
    public void restoreBallSpeed() {
        // Get current velocity direction
        Velocity currentVel = ball.getVelocity();
        double speed = Math.hypot(currentVel.getDx(), currentVel.getDy());
        
        // If speed is very slow (< 1.5), restore to initial speed
        if (speed < Constants.Physics.BALL_MIN_SPEED) {
            double angle = Math.atan2(currentVel.getDy(), currentVel.getDx());
            double newDx = Math.cos(angle) * Constants.Physics.BALL_INITIAL_SPEED;
            double newDy = Math.sin(angle) * Constants.Physics.BALL_INITIAL_SPEED;
            ball.setVelocity(new Velocity(newDx, newDy));
            System.out.println("GameManager: Ball speed restored to " + Constants.Physics.BALL_INITIAL_SPEED);
        } else {
            // Calculate restoration multiplier (inverse of SLOW_MULTIPLIER)
            double restoreMultiplier = 1.0 / Constants.PowerUps.SLOW_MULTIPLIER;
            double newDx = currentVel.getDx() * restoreMultiplier;
            double newDy = currentVel.getDy() * restoreMultiplier;
            ball.setVelocity(new Velocity(newDx, newDy));
            System.out.println("GameManager: Ball speed restored by " + restoreMultiplier + "x");
        }
    }
    
    /**
     * Warps to next level (WARP PowerUp).
     * Clears all bricks and advances to next round.
     * @return true if next level exists, false if last level
     */
    public boolean warpToNextLevel() {
        // Clear all bricks
        bricks.clear();
        // Mark as won to trigger level transition
        gameOver = true;
        won = true;
        System.out.println("GameManager: Warped to next level!");
        return true;
    }
    
    /**
     * Gets the PowerUpManager instance.
     * @return PowerUpManager instance
     */
    public PowerUpManager getPowerUpManager() {
        return powerUpManager;
    }

}

