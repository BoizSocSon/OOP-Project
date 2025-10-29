package Engine;

import Objects.Bricks.BrickType;
import Objects.GameEntities.Ball;
import Objects.GameEntities.Paddle;
import Objects.GameEntities.PaddleState;
import Objects.GameEntities.Laser;
import Objects.Bricks.Brick;
import GeometryPrimitives.Point;
import GeometryPrimitives.Velocity;
import Utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lớp GameManager quản lý trạng thái, các đối tượng và logic cốt lõi của trò chơi.
 * Nó chịu trách nhiệm cho vòng lặp cập nhật trò chơi, xử lý va chạm, quản lý vòng chơi,
 * điểm số, mạng sống và các hiệu ứng PowerUp.
 */
public class GameManager {
    // Các đối tượng trò chơi chính
    /** Đối tượng ván trượt của người chơi. */
    public Paddle paddle;
    /** Danh sách các quả bóng hiện đang hoạt động trong trò chơi. */
    public List<Ball> balls;
    /** Danh sách các viên gạch còn lại trong màn chơi hiện tại. */
    public List<Brick> bricks;
    /** Danh sách các tia laser đang bay (nếu chức năng laser được kích hoạt). */
    public List<Laser> lasers;

    // Các lớp quản lý (Managers)
    /** Quản lý việc kiểm tra và xử lý va chạm giữa các đối tượng. */
    private CollisionManager collisionManager;
    /** Quản lý việc sinh ra và áp dụng các hiệu ứng PowerUp. */
    private PowerUpManager powerUpManager;
    /** Quản lý việc tải và chuyển đổi giữa các vòng (màn chơi). */
    private RoundsManager roundsManager;
    /** Quản lý việc tính toán và cập nhật điểm số. */
    private ScoreManager scoreManager;
    /** Quản lý trạng thái chung của trò chơi (Menu, Playing, Game Over, v.v.). */
    private StateManager stateManager;

    // Trạng thái trò chơi
    /** Chiều rộng của khu vực chơi game. */
    private int width;
    /** Chiều cao tuyệt đối của cửa sổ game. */
    private int height;
    /** Số mạng sống hiện tại của người chơi. */
    private int lives;

    /**
     * Khởi tạo GameManager, thiết lập kích thước cửa sổ, mạng sống ban đầu
     * và khởi tạo tất cả các lớp quản lý cần thiết.
     */
    public GameManager() {
        this.width = Constants.Window.WINDOW_WIDTH;
        // Chiều cao phải là chiều cao cửa sổ đầy đủ (tọa độ tuyệt đối).
        // Sử dụng chiều cao cửa sổ đầy đủ để kiểm tra giới hạn nhất quán.
        this.height = Constants.Window.WINDOW_HEIGHT;
        this.lives = Constants.GameRules.INITIAL_LIVES;

        this.balls = new ArrayList<>();
        this.bricks = new ArrayList<>();
        this.lasers = new ArrayList<>();

        // Khởi tạo các Manager
        this.collisionManager = new CollisionManager(width, height);
        // PowerUpManager là Singleton
        this.powerUpManager = PowerUpManager.getInstance();
        this.powerUpManager.setGameManager(this);
        this.roundsManager = new RoundsManager();
        this.scoreManager = new ScoreManager();
        this.stateManager = new StateManager();

        // Khởi tạo các đối tượng trò chơi (paddle, bóng ban đầu, gạch)
        initGame();
    }

    /**
     * Khởi tạo các đối tượng trò chơi ở trạng thái ban đầu (paddle, bóng, gạch vòng 1).
     */
    private void initGame() {
        // Khởi tạo ván trượt (Paddle)
        double paddleWidth = Constants.Paddle.PADDLE_WIDTH;
        double paddleHeight = Constants.Paddle.PADDLE_HEIGHT;
        // Đặt paddle ở giữa phía dưới màn hình
        paddle = new Paddle(
                (width - paddleWidth) / 2.0,
                height - paddleHeight - 60, // Đặt cách đáy một khoảng
                paddleWidth,
                paddleHeight
        );

        paddle.playMaterializeAnimation(); // Bắt đầu animation xuất hiện của paddle

        // Khởi tạo quả bóng ban đầu
        double ballRadius = Constants.Ball.BALL_RADIUS;
        Ball ball = new Ball(
                (width/2.0) - ballRadius, // Vị trí X giữa màn hình
                height - 80, // Vị trí Y trên paddle một chút
                ballRadius,
                new Velocity(0, 0)); // Vận tốc ban đầu là 0

        ball.setAttached(true); // Gắn bóng vào paddle ban đầu

        balls.add(ball);

        // Tải vòng chơi đầu tiên
        bricks = roundsManager.loadFirstRound();
    }

    /**
     * Cập nhật logic trò chơi trong mỗi khung hình (game loop).
     * Bao gồm cập nhật vị trí đối tượng, xử lý va chạm và kiểm tra điều kiện game.
     */
    public void update() {
        // Chỉ cập nhật nếu trò chơi đang trong trạng thái PLAYING
        if (!stateManager.isPlaying()) {
            return;
        }

        // Cập nhật ván trượt
        paddle.update();

        // Cập nhật vị trí của TẤT CẢ các quả bóng đang gắn (attached) để đi theo paddle
        for (Ball ball : balls) {
            if (ball.isAttached()) {
                double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
                // Đặt bóng ngay trên paddle
                double ballY = paddle.getY() - ball.getHeight() - 1.0;
                // Sử dụng setCenter để đơn giản hóa việc đặt vị trí
                ball.setCenter(new Point(paddleCenterX, ballY + ball.getHeight() / 2.0));
            }
        }
        checkPaddleOutOfBounds(); // Đảm bảo paddle không đi ra ngoài biên

        // Cập nhật vị trí của tất cả các quả bóng KHÔNG gắn
        for (Ball ball : balls) {
            if (!ball.isAttached()) {
                ball.update();
            }
        }

        // Cập nhật vị trí của các tia laser
        for (Laser laser : lasers) {
            laser.update();
        }

        // Cập nhật các viên gạch (ví dụ: cho animation nứt)
        for (Brick brick : bricks) {
            if (brick.isAlive()) {
                brick.update();
            }
        }

        // Cập nhật trạng thái các PowerUp đang rơi và kiểm tra va chạm với paddle
        powerUpManager.update(paddle);

        // Xóa các tia laser đã bay ra khỏi màn hình
        lasers.removeIf(Laser::isOffScreen);

        // Xử lý va chạm
        handleCollisions();
        // Kiểm tra điều kiện thắng/thua/hết mạng/hoàn thành vòng
        checkGameConditions();
        // Lặp lại kiểm tra out of bounds để xử lý trường hợp paddle thay đổi kích thước sau va chạm
        checkPaddleOutOfBounds();
    }

    /**
     * Đảm bảo ván trượt (paddle) luôn nằm trong giới hạn màn hình chơi game.
     */
    private void checkPaddleOutOfBounds() {
        // Kiểm tra biên trái
        if (paddle.getX() < Constants.Window.WINDOW_SIDE_OFFSET) {
            paddle.setX(Constants.Window.WINDOW_SIDE_OFFSET);
        }
        // Kiểm tra biên phải
        else if (paddle.getX() + paddle.getWidth() > width - Constants.Window.WINDOW_SIDE_OFFSET) {
            paddle.setX(width - paddle.getWidth() - Constants.Window.WINDOW_SIDE_OFFSET);
        }
    }

    /**
     * Xử lý tất cả các va chạm trong trò chơi (bóng-tường, bóng-paddle, bóng-gạch, laser-gạch).
     */
    private void handleCollisions() {
        // Xử lý va chạm của bóng
        for (Ball ball : balls) {
            // Kiểm tra va chạm bóng với tường (trái, phải, trên)
            // Truyền tọa độ biên trên đã điều chỉnh (offset UI + chiều cao sprite biên)
            collisionManager.checkBallWallCollisions(ball,
                    Constants.Borders.BORDER_SIDE_WIDTH,
                    Constants.Window.WINDOW_WIDTH - Constants.Borders.BORDER_SIDE_WIDTH,
                    Constants.Window.WINDOW_TOP_OFFSET + Constants.Borders.BORDER_TOP_HEIGHT);

            // Kiểm tra va chạm bóng với paddle
            if (collisionManager.checkBallPaddleCollision(ball, paddle)) {
                // Nếu Catch Mode (chế độ bắt bóng) được bật VÀ bóng không bị gắn
                if(paddle.isCatchModeEnabled() && !ball.isAttached()) {
                    ball.setVelocity(new Velocity(0,0)); // Dừng bóng lại
                    ball.setAttached(true); // Gắn bóng vào paddle
                }
            }

            // Kiểm tra va chạm bóng với gạch
            List<Brick> destroyedBricks = collisionManager.checkBallBrickCollisions(ball, bricks);

            // Xử lý các viên gạch bị phá hủy
            for (Brick brick : destroyedBricks) {
                BrickType type = brick.getBrickType();
                scoreManager.addDestroyBrickScore(type); // Cộng điểm
                // Sinh PowerUp từ gạch
                powerUpManager.spawnFromBrick(brick.getX(), brick.getY(), type);
            }
        }

        // Xử lý va chạm laser với gạch
        Map<Laser, Brick> laserBrickHits = collisionManager.checkLaserBrickCollisions(lasers, bricks);

        for (Map.Entry<Laser, Brick> entry : laserBrickHits.entrySet()) {
            Laser laser = entry.getKey();
            Brick brick = entry.getValue();

            laser.destroy(); // Hủy tia laser

            BrickType type = brick.getBrickType();
            scoreManager.addDestroyBrickScore(type); // Cộng điểm
            // Không sinh PowerUp khi gạch bị phá bằng laser
        }
    }

    /**
     * Kiểm tra các điều kiện thắng/thua của trò chơi, bao gồm:
     * - Bóng rơi khỏi màn hình.
     * - Hoàn thành vòng chơi hiện tại.
     */
    private void checkGameConditions() {
        // Kiểm tra và xóa các quả bóng rơi ra khỏi đáy màn hình
        balls.removeIf(ball -> ball.getY() > height);

        // Nếu hết bóng
        if (balls.isEmpty()) {
            loseLife(); // Mất một mạng
            return;
        }

        // Kiểm tra hoàn thành vòng chơi
        if (roundsManager.isRoundComplete()) {
            if (roundsManager.hasNextRound()) {
                // Chuyển sang level tiếp theo
                stateManager.setState(GameState.LEVEL_COMPLETE);

                // Xóa tất cả các PowerUp đang rơi
                powerUpManager.clearAllPowerUps();

                roundsManager.nextRound(); // Tải dữ liệu vòng tiếp theo
                bricks = roundsManager.getCurrentBricks(); // Cập nhật danh sách gạch mới
                stateManager.setState(GameState.PLAYING); // Trở lại trạng thái chơi

                resetBall(); // Đặt lại bóng và paddle
            } else {
                // Hoàn thành tất cả các vòng - THẮNG!
                powerUpManager.clearAllPowerUps();
                stateManager.setState(GameState.WIN);
            }
        }
    }

    /**
     * Giảm một mạng sống và xử lý kết thúc game nếu hết mạng.
     */
    private void loseLife() {
        lives--; // Giảm mạng
        scoreManager.applyLoseLifePenalty(); // Áp dụng hình phạt điểm (nếu có)

        paddle.playExplodeAnimation(); // Chơi animation nổ của paddle

        if (lives <= 0) {
            // Hết mạng - GAME OVER
            powerUpManager.clearAllPowerUps();
            stateManager.setState(GameState.GAME_OVER);
        } else {
            // Còn mạng - Đặt lại bóng
            resetBall();
        }
    }

    /**
     * Đặt lại bóng (Ball) về vị trí ban đầu và gắn vào Paddle.
     * Đồng thời đặt lại các hiệu ứng trên Paddle.
     */
    private void resetBall() {
        balls.clear(); // Xóa tất cả các bóng hiện tại

        // Đặt lại paddle về trạng thái bình thường (xóa tất cả hiệu ứng PowerUp)
        resetPaddleEffects();

        double ballRadius = Constants.Ball.BALL_RADIUS;
        double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
        double ballY = paddle.getY() - ballRadius * 2 - 5;

        // Tạo quả bóng mới ở vị trí trung tâm paddle, gắn vào
        Ball ball = new Ball(paddleCenterX - ballRadius, ballY, ballRadius, new Velocity(0, 0));
        ball.setAttached(true);
        balls.add(ball);

        paddle.playMaterializeAnimation(); // Chơi animation xuất hiện
    }

    /**
     * Đặt lại tất cả các hiệu ứng PowerUp trên ván trượt (paddle) khi người chơi mất một mạng.
     * Điều này đảm bảo paddle trở về kích thước và trạng thái bình thường.
     */
    private void resetPaddleEffects() {
        PaddleState currentState = paddle.getState();
        System.out.println("resetPaddleEffects: Current paddle state = " + currentState +
                ", width = " + paddle.getWidth());

        // Đặt lại hiệu ứng mở rộng (EXPAND)
        // Kiểm tra trực tiếp chiều rộng vì trạng thái có thể bị thay đổi
        if (paddle.getWidth() > Constants.Paddle.PADDLE_WIDTH + 1) {
            // Paddle vẫn còn rộng, buộc thu nhỏ
            double centerX = paddle.getX() + paddle.getWidth() / 2.0;
            paddle.setWidth(Constants.Paddle.PADDLE_WIDTH);
            // Đảm bảo paddle vẫn ở vị trí trung tâm cũ
            paddle.setX(centerX - paddle.getWidth() / 2.0);
            System.out.println("resetPaddleEffects: Forced paddle width reset");
        }

        // Đặt lại hiệu ứng Laser
        if (currentState == PaddleState.LASER ||
                currentState == PaddleState.LASER_PULSATE) {
            paddle.disableLaser();
        }

        // Đặt lại chế độ bắt bóng (Catch Mode)
        if (paddle.isCatchModeEnabled()) {
            paddle.setCatchModeEnabled(false);
        }

        // Xóa bộ đếm thời gian hiệu ứng làm chậm (SLOW)
        // (Tốc độ bóng sẽ được xử lý lại trong restoreBallSpeed nếu cần)
        paddle.clearSlowEffect();

        // Buộc trạng thái về NORMAL (xóa mọi trạng thái PowerUp còn sót)
        if (currentState != PaddleState.NORMAL) {
            paddle.setState(PaddleState.NORMAL);
        }

        System.out.println("resetPaddleEffects: After reset - state = " + paddle.getState() +
                ", width = " + paddle.getWidth());
    }

    /**
     * Đặt lại toàn bộ trò chơi về trạng thái ban đầu (thường được gọi khi bắt đầu game mới từ Menu).
     */
    public void resetGame() {
        lives = Constants.GameRules.INITIAL_LIVES; // Đặt lại mạng

        // Đặt lại các Manager
        scoreManager.resetScore();
        scoreManager.resetMultiplier();
        roundsManager.reset();
        // PowerUpManager là Singleton nên chỉ cần reset tham chiếu/trạng thái
        powerUpManager = PowerUpManager.getInstance();
        powerUpManager.setGameManager(this);
        stateManager.setState(GameState.MENU); // Chuyển về trạng thái Menu

        // Xóa danh sách đối tượng
        balls.clear();
        lasers.clear();

        // Khởi tạo lại các đối tượng trò chơi (paddle, bóng, gạch)
        initGame();
    }

    /**
     * Phóng quả bóng đang được gắn (attached) khỏi ván trượt.
     * Chỉ phóng quả bóng đầu tiên được tìm thấy.
     */
    public void launchBall() {
        if (balls.isEmpty()) {
            return;
        }

        // Tìm và phóng quả bóng gắn đầu tiên
        for (Ball ball : balls) {
            if (ball.isAttached()) {
                ball.setAttached(false); // Bỏ gắn
                // Đặt vận tốc ban đầu hướng lên trên
                ball.setVelocity(new Velocity(0, -Constants.Ball.BALL_INITIAL_SPEED));
                return; // Chỉ phóng một quả bóng mỗi lần nhấn phím
            }
        }
    }

    /**
     * Bắn tia laser từ ván trượt nếu chức năng Laser được bật.
     */
    public void shootLaser() {
        // Kiểm tra xem laser có được bật không
        if (!paddle.isLaserEnabled()) {
            return;
        }

        // Tạo ra các tia laser mới từ paddle
        List<Laser> newLasers = paddle.shootLaser();
        lasers.addAll(newLasers); // Thêm vào danh sách laser đang hoạt động

        if (!newLasers.isEmpty()) {
            System.out.println("GameManager: Fired " + newLasers.size() + " lasers");
        }
    }

    /**
     * Bật chế độ bắt bóng (Catch Mode) trên ván trượt (PowerUp CATCH).
     * Khi được bật, bóng sẽ dính vào ván trượt khi va chạm.
     */
    public void enableCatchMode() {
        paddle.setCatchModeEnabled(true);
        System.out.println("GameManager: Catch mode enabled");
    }

    /**
     * Tắt chế độ bắt bóng trên ván trượt.
     */
    public void disableCatchMode() {
        paddle.setCatchModeEnabled(false);
        System.out.println("GameManager: Catch mode disabled");
    }

    /**
     * Lấy số lượng quả bóng hiện đang hoạt động.
     * @return Số lượng quả bóng đang hoạt động.
     */
    public int getBallCount() {
        return balls.size();
    }

    /**
     * Nhân đôi tất cả các quả bóng hiện tại (PowerUp DUPLICATE).
     * Tạo bản sao của các quả bóng hiện có với vận tốc phân kỳ.
     */
    public void duplicateBalls() {
        List<Ball> newBalls = new ArrayList<>();

        for (Ball ball : balls) {
            // Nếu bóng đang gắn (attached), nhân đôi thành các bóng cũng gắn
            if (ball.isAttached()) {
                // Tạo hai bản sao ở cùng vị trí, cũng gắn vào paddle
                Ball leftBall = new Ball(
                        ball.getX(), ball.getY(),
                        Constants.Ball.BALL_RADIUS,
                        new Velocity(0, 0)
                );
                leftBall.setAttached(true);
                newBalls.add(leftBall);

                Ball rightBall = new Ball(
                        ball.getX(), ball.getY(),
                        Constants.Ball.BALL_RADIUS,
                        new Velocity(0, 0)
                );
                rightBall.setAttached(true);
                newBalls.add(rightBall);
            } else {
                // Nếu bóng đang bay, tạo hai bản sao với góc bay ±30° so với góc hiện tại
                Velocity vel = ball.getVelocity();
                double speed = Math.hypot(vel.getDx(), vel.getDy()); // Tính tốc độ hiện tại
                double angle = Math.atan2(vel.getDy(), vel.getDx()); // Tính góc bay hiện tại

                // Bóng bên trái (-30°)
                double leftAngle = angle - Math.toRadians(30);
                Ball leftBall = new Ball(
                        ball.getX(), ball.getY(),
                        Constants.Ball.BALL_RADIUS,
                        // Thiết lập vận tốc mới
                        new Velocity(speed * Math.cos(leftAngle), speed * Math.sin(leftAngle))
                );
                newBalls.add(leftBall);

                // Bóng bên phải (+30°)
                double rightAngle = angle + Math.toRadians(30);
                Ball rightBall = new Ball(
                        ball.getX(), ball.getY(),
                        Constants.Ball.BALL_RADIUS,
                        // Thiết lập vận tốc mới
                        new Velocity(speed * Math.cos(rightAngle), speed * Math.sin(rightAngle))
                );
                newBalls.add(rightBall);
            }
        }

        balls.addAll(newBalls); // Thêm các bóng mới vào danh sách
        System.out.println("GameManager: Balls duplicated! Total: " + balls.size());
    }

    /**
     * Mở rộng chiều rộng của ván trượt (PowerUp EXPAND).
     */
    public void expandPaddle() {
        paddle.expand();
        System.out.println("GameManager: Paddle expanded");
    }

    /**
     * Đặt lại kích thước ván trượt về kích thước ban đầu.
     * (Việc này được Paddle tự động xử lý sau thời gian PowerUp hết hạn).
     */
    public void revertPaddleSize() {
        // Xử lý tự động bởi Paddle.update() sau 10 giây
        System.out.println("GameManager: Paddle size will revert automatically");
    }

    /**
     * Bật chức năng bắn tia laser trên ván trượt (PowerUp LASER).
     */
    public void enableLaser() {
        paddle.enableLaser();
        System.out.println("GameManager: Laser enabled with " + Constants.Laser.LASER_SHOTS + " shots");
    }

    /**
     * Tắt chức năng bắn tia laser trên ván trượt.
     */
    public void disableLaser() {
        paddle.setLaserEnabled(false);
        System.out.println("GameManager: Laser disabled");
    }

    /**
     * Lấy số mạng sống hiện tại của người chơi.
     * @return Số mạng sống hiện tại.
     */
    public int getLives() {
        return lives;
    }

    /**
     * Thêm một mạng sống cho người chơi (PowerUp LIFE).
     * Mạng sống tối đa được định nghĩa trong Constants.
     */
    public void addLife() {
        if (lives < Constants.GameRules.MAX_LIVES) {
            lives++;
            System.out.println("GameManager: Life added! Lives: " + lives);
        } else {
            System.out.println("GameManager: Max lives reached");
        }
    }

    /**
     * Làm chậm tốc độ của tất cả các quả bóng (PowerUp SLOW).
     * @param multiplier Hệ số nhân tốc độ (ví dụ: 0.7).
     */
    public void slowBalls(double multiplier) {
        for (Ball ball : balls) {
            Velocity currentVel = ball.getVelocity();
            // Nhân vận tốc hiện tại với hệ số làm chậm
            double newDx = currentVel.getDx() * multiplier;
            double newDy = currentVel.getDy() * multiplier;
            ball.setVelocity(new Velocity(newDx, newDy));
        }

        // Đặt thời gian hết hạn hiệu ứng làm chậm trên paddle (dùng cho animation cảnh báo)
        long expiryTime = System.currentTimeMillis() + Constants.PowerUps.SLOW_DURATION;
        paddle.setSlowEffectExpiry(expiryTime);

        System.out.println("GameManager: Balls slowed by " + multiplier + "x");
    }

    /**
     * Khôi phục tốc độ ban đầu của tất cả các quả bóng (loại bỏ hiệu ứng SLOW).
     */
    public void restoreBallSpeed() {
        for (Ball ball : balls) {
            Velocity currentVel = ball.getVelocity();
            double speed = Math.hypot(currentVel.getDx(), currentVel.getDy());

            // Nếu tốc độ quá chậm (ví dụ: bị làm chậm quá nhiều lần hoặc khởi tạo lại)
            if (speed < Constants.Ball.BALL_MIN_SPEED) {
                // Đặt lại về tốc độ ban đầu (BALL_INITIAL_SPEED)
                double angle = Math.atan2(currentVel.getDy(), currentVel.getDx());
                double newDx = Math.cos(angle) * Constants.Ball.BALL_INITIAL_SPEED;
                double newDy = Math.sin(angle) * Constants.Ball.BALL_INITIAL_SPEED;
                ball.setVelocity(new Velocity(newDx, newDy));
            } else {
                // Khôi phục về tốc độ bình thường (bằng cách nhân với nghịch đảo của hệ số làm chậm 0.7)
                double restoreMultiplier = 1.0 / 0.7; // Nghịch đảo của hệ số làm chậm
                double newDx = currentVel.getDx() * restoreMultiplier;
                double newDy = currentVel.getDy() * restoreMultiplier;
                ball.setVelocity(new Velocity(newDx, newDy));
            }
        }

        // Xóa bộ đếm thời gian hết hạn hiệu ứng làm chậm trên paddle
        paddle.clearSlowEffect();

        System.out.println("GameManager: Ball speed restored");
    }

    /**
     * Chuyển ngay lập tức đến vòng chơi tiếp theo (PowerUp WARP).
     * Xóa vòng hiện tại và chuyển tiếp.
     * @return true nếu chuyển vòng thành công, false nếu đã ở vòng cuối.
     */
    public boolean warpToNextLevel() {
        if (roundsManager.hasNextRound()) {
            // Xóa tất cả PowerUp đang rơi khi chuyển level
            powerUpManager.clearAllPowerUps();

            roundsManager.nextRound(); // Tải vòng tiếp theo
            bricks = roundsManager.getCurrentBricks(); // Cập nhật gạch
            resetBall(); // Đặt lại bóng
            System.out.println("GameManager: Warped to next level!");
            return true;
        } else {
            // Đã ở vòng cuối - THẮNG!
            powerUpManager.clearAllPowerUps();
            stateManager.setState(GameState.WIN);
            return false;
        }
    }

    // --- Các phương thức Getter cho các Manager và Trạng thái ---

    /**
     * Lấy thể hiện (instance) của PowerUpManager.
     * @return Thể hiện của PowerUpManager.
     */
    public PowerUpManager getPowerUpManager() {
        return powerUpManager;
    }

    /**
     * Lấy thể hiện của StateManager.
     * @return Thể hiện của StateManager.
     */
    public StateManager getStateManager() {
        return stateManager;
    }

    /**
     * Lấy thể hiện của RoundsManager.
     * @return Thể hiện của RoundsManager.
     */
    public RoundsManager getRoundsManager() {
        return roundsManager;
    }

    /**
     * Lấy thể hiện của ScoreManager.
     * @return Thể hiện của ScoreManager.
     */
    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    /**
     * Lấy thể hiện của CollisionManager.
     * @return Thể hiện của CollisionManager.
     */
    public CollisionManager getCollisionManager() {
        return collisionManager;
    }

    /**
     * Lấy điểm số hiện tại (từ ScoreManager).
     * @return Điểm số hiện tại.
     */
    public int getScore() {
        return scoreManager.getScore();
    }

    /**
     * Kiểm tra xem trò chơi đã kết thúc hay chưa (GAME_OVER hoặc WIN).
     * @return true nếu trò chơi đã kết thúc.
     */
    public boolean isGameOver() {
        return stateManager.isGameOver();
    }

    /**
     * Kiểm tra xem người chơi đã thắng trò chơi hay chưa (trạng thái WIN).
     * @return true nếu trò chơi đang ở trạng thái WIN.
     */
    public boolean hasWon() {
        return stateManager.getState() == GameState.WIN;
    }

    /**
     * Lấy danh sách các tia laser đang hoạt động.
     * @return Danh sách các đối tượng Laser.
     */
    public List<Laser> getLasers() {
        return lasers;
    }

    /**
     * Kiểm tra xem có bất kỳ quả bóng nào đang được gắn vào paddle hay không.
     * @return true nếu có ít nhất một quả bóng đang được gắn.
     */
    public boolean isAttached() {
        for (Ball ball : balls) {
            if (ball.isAttached()) {
                return true;
            }
        }
        return false;
    }
}