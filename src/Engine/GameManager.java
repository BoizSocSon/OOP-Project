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
 * Lớp quản lý trò chơi (GameManager) là lớp trung tâm điều khiển logic chính
 * của game, bao gồm khởi tạo các đối tượng, cập nhật trạng thái game,
 * xử lý va chạm và quản lý các điều kiện thắng/thua.
 */
public class GameManager {
    // Các đối tượng game
    public Paddle paddle;
    public List<Ball> balls;
    public List<Brick> bricks;
    public List<Laser> lasers;

    // Các lớp quản lý (Managers)
    private CollisionManager collisionManager;
    private PowerUpManager powerUpManager;
    private RoundsManager roundsManager;
    private ScoreManager scoreManager;
    private StateManager stateManager;

    // Trạng thái game
    private int width;
    private int height;
    private int lives;

    /**
     * Khởi tạo GameManager, thiết lập kích thước cửa sổ và các thành phần quản lý.
     */
    public GameManager() {
        this.width = Constants.Window.WINDOW_WIDTH;
        // Chiều cao phải là chiều cao cửa sổ tuyệt đối để xử lý tọa độ Y.
        // Tọa độ Y tuyệt đối (0..WINDOW_HEIGHT) được sử dụng cho các đối tượng game.
        this.height = Constants.Window.WINDOW_HEIGHT;
        this.lives = Constants.GameRules.INITIAL_LIVES; // Số mạng ban đầu.

        this.balls = new ArrayList<>();
        this.bricks = new ArrayList<>();
        this.lasers = new ArrayList<>();

        this.collisionManager = new CollisionManager(width, height);
        this.powerUpManager = PowerUpManager.getInstance();
        this.powerUpManager.setGameManager(this); // Thiết lập tham chiếu ngược.
        this.roundsManager = new RoundsManager();
        this.scoreManager = new ScoreManager();
        this.stateManager = new StateManager();

        // Khởi tạo các đối tượng game cơ bản (paddle, bóng, gạch)
        initGame();
    }

    /**
     * Thiết lập các đối tượng game ban đầu (Paddle, Ball, Bricks).
     */
    private void initGame() {
        // Khởi tạo thanh đỡ (Paddle)
        double paddleWidth = Constants.Paddle.PADDLE_WIDTH;
        double paddleHeight = Constants.Paddle.PADDLE_HEIGHT;
        paddle = new Paddle(
                (width - paddleWidth) / 2.0, // Đặt ở giữa màn hình
                height - paddleHeight - 60, // Cách đáy một khoảng
                paddleWidth,
                paddleHeight
        );

        // Bắt đầu animation xuất hiện (Materialize)
        paddle.playMaterializeAnimation();

        // Khởi tạo bóng ban đầu
        double ballRadius = Constants.Ball.BALL_RADIUS;
        Ball ball = new Ball(
                (width/2.0) - ballRadius,
                height - 80,
                ballRadius,
                new Velocity(0, 0));

        // Gắn bóng vào thanh đỡ ban đầu
        ball.setAttached(true);

        balls.add(ball);

        // Tải gạch cho vòng chơi đầu tiên
        bricks = roundsManager.loadFirstRound();
    }

    /**
     * Cập nhật logic game cho mỗi khung hình.
     */
    public void update() {
        // Chỉ cập nhật logic nếu game đang ở trạng thái PLAYING
        if (!stateManager.isPlaying()) {
            return;
        }

        // Cập nhật trạng thái thanh đỡ
        paddle.update();

        // Cập nhật vị trí của TẤT CẢ các quả bóng được gắn vào thanh đỡ
        for (Ball ball : balls) {
            if (ball.isAttached()) {
                double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
                double ballY = paddle.getY() - ball.getHeight() - 1.0;
                // Cập nhật vị trí tâm bóng theo thanh đỡ
                ball.setCenter(new Point(paddleCenterX, ballY + ball.getHeight() / 2.0));
            }
        }
        // Kiểm tra và giữ thanh đỡ trong biên giới hạn
        checkPaddleOutOfBounds();

        // Cập nhật vị trí và trạng thái của tất cả bóng không được gắn
        for (Ball ball : balls) {
            if (!ball.isAttached()) {
                ball.update();
            }
        }

        // Cập nhật vị trí của các tia laser
        for (Laser laser : lasers) {
            laser.update();
        }

        // Cập nhật gạch (cho animation như gạch nứt)
        for (Brick brick : bricks) {
            if (brick.isAlive()) {
                brick.update();
            }
        }

        // Cập nhật logic vật phẩm bổ trợ
        powerUpManager.update(paddle);

        // Loại bỏ các tia laser đã bay ra ngoài màn hình
        lasers.removeIf(Laser::isOffScreen);

        // Xử lý tất cả va chạm
        handleCollisions();
        // Kiểm tra điều kiện game (mất mạng, qua màn)
        checkGameConditions();
        // Kiểm tra và giữ thanh đỡ trong biên giới hạn (lặp lại đề phòng va chạm đưa ra ngoài)
        checkPaddleOutOfBounds();
    }

    /**
     * Giới hạn vị trí của thanh đỡ trong khu vực chơi.
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
     * Xử lý tất cả va chạm trong game.
     */
    private void handleCollisions() {
        // --- Va chạm của Bóng (Ball Collisions) ---
        for (Ball ball : balls) {
            // Kiểm tra va chạm với tường (tường trên, trái, phải).
            // Truyền tọa độ biên trên tuyệt đối (UI offset + chiều cao viền trên)
            collisionManager.checkBallWallCollisions(ball,
                    Constants.Borders.BORDER_SIDE_WIDTH,
                    Constants.Window.WINDOW_WIDTH - Constants.Borders.BORDER_SIDE_WIDTH,
                    Constants.Window.WINDOW_TOP_OFFSET + Constants.Borders.BORDER_TOP_HEIGHT);

            // Kiểm tra va chạm với thanh đỡ
            if (collisionManager.checkBallPaddleCollision(ball, paddle)) {
                // Nếu chế độ bắt bóng (Catch Mode) đang bật VÀ bóng không bị gắn, thì gắn bóng lại.
                if(paddle.isCatchModeEnabled() && !ball.isAttached()) {
                    ball.setVelocity(new Velocity(0,0));
                    ball.setAttached(true);
                }
            }

            // Kiểm tra va chạm với gạch
            List<Brick> destroyedBricks = collisionManager.checkBallBrickCollisions(ball, bricks);

            // Xử lý các gạch bị phá hủy
            for (Brick brick : destroyedBricks) {
                BrickType type = brick.getBrickType();
                scoreManager.addDestroyBrickScore(type); // Cộng điểm

                // Rơi vật phẩm bổ trợ (nếu có)
                powerUpManager.spawnFromBrick(brick.getX(), brick.getY(), type);
            }
        }

        // --- Va chạm của Laser (Laser Collisions) ---
        // Lấy Map các cặp (Laser, Brick) bị va chạm
        Map<Laser, Brick> laserBrickHits = collisionManager.checkLaserBrickCollisions(lasers, bricks);

        for (Map.Entry<Laser, Brick> entry : laserBrickHits.entrySet()) {
            Laser laser = entry.getKey();
            Brick brick = entry.getValue();

            laser.destroy(); // Hủy tia laser sau khi va chạm

            BrickType type = brick.getBrickType();
            scoreManager.addDestroyBrickScore(type); // Cộng điểm
            // Không cần xử lý PowerUp vì laser không tạo ra PowerUp (chỉ bóng làm điều đó)
        }
    }

    /**
     * Kiểm tra các điều kiện thắng/thua và chuyển trạng thái game.
     */
    private void checkGameConditions() {
        // --- Kiểm tra bóng ra ngoài (mất mạng) ---
        // Loại bỏ các quả bóng rơi qua đáy màn hình
        balls.removeIf(ball -> ball.getY() > height);

        // Nếu không còn bóng, mất một mạng
        if (balls.isEmpty()) {
            loseLife();
            return;
        }

        // --- Kiểm tra hoàn thành vòng chơi ---
        if (roundsManager.isRoundComplete()) {
            // Nếu còn vòng tiếp theo
            if (roundsManager.hasNextRound()) {
                stateManager.setState(GameState.LEVEL_COMPLETE); // Đặt trạng thái qua màn

                // Dọn dẹp tất cả vật phẩm đang rơi khi chuyển màn
                powerUpManager.clearAllPowerUps();

                // Chuyển sang vòng tiếp theo
                roundsManager.nextRound();
                bricks = roundsManager.getCurrentBricks();
                stateManager.setState(GameState.PLAYING); // Chuyển lại trạng thái chơi

                // Đặt lại bóng và thanh đỡ
                resetBall();
            } else {
                // Đã hoàn thành tất cả các vòng - THẮNG!
                // Dọn dẹp tất cả vật phẩm đang rơi khi thắng
                powerUpManager.clearAllPowerUps();
                stateManager.setState(GameState.WIN);
            }
        }
    }

    /**
     * Giảm số mạng hiện tại và xử lý trạng thái GAME_OVER nếu hết mạng.
     */
    private void loseLife() {
        lives--; // Giảm một mạng
        scoreManager.applyLoseLifePenalty(); // Trừ điểm phạt

        paddle.playExplodeAnimation(); // Bắt đầu animation nổ của paddle

        // Kiểm tra hết mạng
        if (lives <= 0) {
            // Dọn dẹp tất cả vật phẩm đang rơi khi game kết thúc
            powerUpManager.clearAllPowerUps();
            stateManager.setState(GameState.GAME_OVER);
        } else {
            // Vẫn còn mạng, đặt lại bóng
            resetBall();
        }
    }

    /**
     * Đặt lại bóng về vị trí ban đầu (gắn vào thanh đỡ).
     */
    private void resetBall() {
        balls.clear(); // Xóa tất cả bóng hiện tại

        // Đặt lại hiệu ứng thanh đỡ (kích thước, laser, catch mode)
        resetPaddleEffects();

        // Tạo quả bóng mới
        double ballRadius = Constants.Ball.BALL_RADIUS;
        double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
        double ballY = paddle.getY() - ballRadius * 2 - 5; // Đặt bóng ngay trên paddle

        Ball ball = new Ball(paddleCenterX - ballRadius, ballY, ballRadius, new Velocity(0, 0));
        ball.setAttached(true); // Gắn bóng vào thanh đỡ
        balls.add(ball);

        // Bắt đầu animation xuất hiện của paddle sau khi mất mạng
        paddle.playMaterializeAnimation();
    }

    /**
     * Đặt lại tất cả các hiệu ứng của thanh đỡ khi người chơi mất mạng.
     * Đảm bảo thanh đỡ trở về kích thước và trạng thái bình thường.
     */
    private void resetPaddleEffects() {
        PaddleState currentState = paddle.getState();
        System.out.println("resetPaddleEffects: Current paddle state = " + currentState +
                ", width = " + paddle.getWidth());

        // --- Đặt lại hiệu ứng EXPAND (Thu nhỏ về kích thước chuẩn) ---
        // Kiểm tra chiều rộng trực tiếp thay vì trạng thái, đề phòng lỗi
        if (paddle.getWidth() > Constants.Paddle.PADDLE_WIDTH + 1) {
            // Thanh đỡ vẫn còn mở rộng, buộc phải thu nhỏ
            double centerX = paddle.getX() + paddle.getWidth() / 2.0;
            paddle.setWidth(Constants.Paddle.PADDLE_WIDTH);
            // Đặt lại X để thanh đỡ vẫn ở giữa tâm cũ
            paddle.setX(centerX - paddle.getWidth() / 2.0);
            System.out.println("resetPaddleEffects: Forced paddle width reset");
        }

        // --- Đặt lại hiệu ứng LASER ---
        if (currentState == PaddleState.LASER ||
                currentState == PaddleState.LASER_PULSATE) {
            paddle.disableLaser();
        }

        // --- Đặt lại chế độ CATCH ---
        if (paddle.isCatchModeEnabled()) {
            paddle.setCatchModeEnabled(false);
        }

        // --- Xóa timer hiệu ứng SLOW ---
        // Tốc độ bóng sẽ được reset trong resetBall() nếu cần
        paddle.clearSlowEffect();

        // --- Buộc trạng thái về NORMAL ---
        if (currentState != PaddleState.NORMAL) {
            paddle.setState(PaddleState.NORMAL);
        }

        System.out.println("resetPaddleEffects: After reset - state = " + paddle.getState() +
                ", width = " + paddle.getWidth());
    }

    /**
     * Đặt lại toàn bộ trò chơi về trạng thái ban đầu.
     */
    public void resetGame() {
        lives = Constants.GameRules.INITIAL_LIVES;

        // Đặt lại các Manager
        scoreManager.resetScore();
        scoreManager.resetMultiplier();
        roundsManager.reset();
        // Thiết lập lại PowerUpManager
        powerUpManager = PowerUpManager.getInstance();
        powerUpManager.setGameManager(this);
        // Chuyển về trạng thái MENU
        stateManager.setState(GameState.MENU);

        // Xóa danh sách đối tượng
        balls.clear();
        lasers.clear();

        // Khởi tạo lại game (tạo paddle, bóng, gạch vòng 1)
        initGame();
    }

    /**
     * Bắn quả bóng đầu tiên ra khỏi thanh đỡ.
     */
    public void launchBall() {
        if (balls.isEmpty()) {
            return;
        }

        // Bắn một quả bóng duy nhất (quả đầu tiên được gắn)
        for (Ball ball : balls) {
            if (ball.isAttached()) {
                ball.setAttached(false);
                // Đặt vận tốc ban đầu hướng lên
                ball.setVelocity(new Velocity(0, -Constants.Ball.BALL_INITIAL_SPEED));
                return; // Chỉ bắn một bóng mỗi lần nhấn phím
            }
        }
    }

    /**
     * Bắn tia laser từ thanh đỡ (nếu hiệu ứng Laser đang hoạt động).
     */
    public void shootLaser() {
        // Kiểm tra Laser có được bật không
        if (!paddle.isLaserEnabled()) {
            return;
        }

        // Thực hiện bắn
        List<Laser> newLasers = paddle.shootLaser();
        lasers.addAll(newLasers);

        if (!newLasers.isEmpty()) {
            System.out.println("GameManager: Fired " + newLasers.size() + " lasers");
        }
    }

    /**
     * Kích hoạt chế độ bắt bóng trên thanh đỡ (PowerUp CATCH).
     * Khi kích hoạt, bóng sẽ dính vào thanh đỡ khi va chạm.
     */
    public void enableCatchMode() {
        paddle.setCatchModeEnabled(true);
        System.out.println("GameManager: Catch mode enabled");
    }

    /**
     * Vô hiệu hóa chế độ bắt bóng.
     */
    public void disableCatchMode() {
        paddle.setCatchModeEnabled(false);
        System.out.println("GameManager: Catch mode disabled");
    }

    /**
     * Lấy số lượng bóng đang hoạt động.
     * @return Số lượng bóng hiện tại.
     */
    public int getBallCount() {
        return balls.size();
    }

    /**
     * Nhân đôi tất cả các quả bóng (PowerUp DUPLICATE).
     * Tạo bản sao của bóng với vận tốc được điều chỉnh.
     */
    public void duplicateBalls() {
        List<Ball> newBalls = new ArrayList<>();

        for (Ball ball : balls) {
            // Nếu bóng đang được gắn (chế độ bắt), nhân đôi và gắn cả bản sao.
            if (ball.isAttached()) {
                // Tạo hai bản sao cùng vị trí, cũng được gắn.
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
                // Tạo hai bản sao với góc lệch ±30° so với hướng bay hiện tại.
                Velocity vel = ball.getVelocity();
                double speed = Math.hypot(vel.getDx(), vel.getDy());
                double angle = Math.atan2(vel.getDy(), vel.getDx());

                // Bóng trái (-30°)
                double leftAngle = angle - Math.toRadians(30);
                Ball leftBall = new Ball(
                        ball.getX(), ball.getY(),
                        Constants.Ball.BALL_RADIUS,
                        new Velocity(speed * Math.cos(leftAngle), speed * Math.sin(leftAngle))
                );
                newBalls.add(leftBall);

                // Bóng phải (+30°)
                double rightAngle = angle + Math.toRadians(30);
                Ball rightBall = new Ball(
                        ball.getX(), ball.getY(),
                        Constants.Ball.BALL_RADIUS,
                        new Velocity(speed * Math.cos(rightAngle), speed * Math.sin(rightAngle))
                );
                newBalls.add(rightBall);
            }
        }

        balls.addAll(newBalls);
        System.out.println("GameManager: Balls duplicated! Total: " + balls.size());
    }

    /**
     * Mở rộng chiều rộng thanh đỡ (PowerUp EXPAND).
     */
    public void expandPaddle() {
        paddle.expand();
        System.out.println("GameManager: Paddle expanded");
    }

    /**
     * Đặt lại kích thước thanh đỡ về kích thước ban đầu.
     * (Việc này thường được xử lý tự động bởi Paddle.update() sau một thời gian).
     */
    public void revertPaddleSize() {
        // Xử lý tự động bởi Paddle.update()
        System.out.println("GameManager: Paddle size will revert automatically");
    }

    /**
     * Kích hoạt khả năng bắn laser trên thanh đỡ (PowerUp LASER).
     */
    public void enableLaser() {
        paddle.enableLaser();
        System.out.println("GameManager: Laser enabled with " + Constants.Laser.LASER_SHOTS + " shots");
    }

    /**
     * Vô hiệu hóa khả năng bắn laser.
     */
    public void disableLaser() {
        paddle.setLaserEnabled(false);
        System.out.println("GameManager: Laser disabled");
    }

    /**
     * Lấy số mạng hiện tại của người chơi.
     * @return Số mạng hiện tại.
     */
    public int getLives() {
        return lives;
    }

    /**
     * Thêm một mạng (PowerUp LIFE).
     * Giới hạn tối đa được định nghĩa trong Constants.
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
     * @param multiplier Hệ số làm chậm tốc độ (ví dụ: 0.7).
     */
    public void slowBalls(double multiplier) {
        for (Ball ball : balls) {
            Velocity currentVel = ball.getVelocity();
            double newDx = currentVel.getDx() * multiplier;
            double newDy = currentVel.getDy() * multiplier;
            ball.setVelocity(new Velocity(newDx, newDy));
        }

        // Đặt thời gian hết hạn hiệu ứng SLOW trên paddle để kích hoạt animation cảnh báo.
        long expiryTime = System.currentTimeMillis() + Constants.PowerUps.SLOW_DURATION;
        paddle.setSlowEffectExpiry(expiryTime);

        System.out.println("GameManager: Balls slowed by " + multiplier + "x");
    }

    /**
     * Khôi phục tốc độ bóng về tốc độ ban đầu (loại bỏ hiệu ứng SLOW).
     */
    public void restoreBallSpeed() {
        for (Ball ball : balls) {
            Velocity currentVel = ball.getVelocity();
            double speed = Math.hypot(currentVel.getDx(), currentVel.getDy());

            // Nếu tốc độ quá chậm (gần như bị dừng), khôi phục về tốc độ khởi tạo.
            if (speed < Constants.Ball.BALL_MIN_SPEED) {
                double angle = Math.atan2(currentVel.getDy(), currentVel.getDx());
                double newDx = Math.cos(angle) * Constants.Ball.BALL_INITIAL_SPEED;
                double newDy = Math.sin(angle) * Constants.Ball.BALL_INITIAL_SPEED;
                ball.setVelocity(new Velocity(newDx, newDy));
            } else {
                // Khôi phục về tốc độ ban đầu bằng cách nhân với hệ số nghịch đảo (1/0.7).
                double restoreMultiplier = 1.0 / Constants.PowerUps.SLOW_MULTIPLIER;
                double newDx = currentVel.getDx() * restoreMultiplier;
                double newDy = currentVel.getDy() * restoreMultiplier;
                ball.setVelocity(new Velocity(newDx, newDy));
            }
        }

        // Xóa thời gian hết hạn hiệu ứng SLOW trên thanh đỡ.
        paddle.clearSlowEffect();

        System.out.println("GameManager: Ball speed restored");
    }

    /**
     * Chuyển ngay lập tức sang cấp độ tiếp theo (PowerUp WARP).
     * Xóa vòng chơi hiện tại và tiến lên.
     * @return {@code true} nếu chuyển màn thành công, {@code false} nếu đã ở màn cuối.
     */
    public boolean warpToNextLevel() {
        if (roundsManager.hasNextRound()) {
            // Dọn dẹp tất cả vật phẩm đang rơi khi chuyển màn
            powerUpManager.clearAllPowerUps();

            roundsManager.nextRound();
            bricks = roundsManager.getCurrentBricks();
            resetBall();
            System.out.println("GameManager: Warped to next level!");
            return true;
        } else {
            // Đã ở màn cuối cùng
            // Dọn dẹp tất cả vật phẩm đang rơi khi thắng
            powerUpManager.clearAllPowerUps();
            stateManager.setState(GameState.WIN); // Chuyển sang trạng thái Thắng
            return false;
        }
    }

    /**
     * Lấy instance của PowerUpManager.
     * @return Instance của PowerUpManager.
     */
    public PowerUpManager getPowerUpManager() {
        return powerUpManager;
    }

    /**
     * Lấy instance của StateManager.
     * @return Instance của StateManager.
     */
    public StateManager getStateManager() {
        return stateManager;
    }

    /**
     * Lấy instance của RoundsManager.
     * @return Instance của RoundsManager.
     */
    public RoundsManager getRoundsManager() {
        return roundsManager;
    }

    /**
     * Lấy instance của ScoreManager.
     * @return Instance của ScoreManager.
     */
    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    /**
     * Lấy instance của CollisionManager.
     * @return Instance của CollisionManager.
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
     * Kiểm tra xem game đã kết thúc chưa.
     * @return {@code true} nếu game đã kết thúc (trạng thái GAME_OVER hoặc WIN).
     */
    public boolean isGameOver() {
        return stateManager.isGameOver();
    }

    /**
     * Kiểm tra xem người chơi đã thắng chưa.
     * @return {@code true} nếu đang ở trạng thái WIN.
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
     * Kiểm tra xem có bất kỳ quả bóng nào đang được gắn vào thanh đỡ không.
     * @return {@code true} nếu có ít nhất một quả bóng đang được gắn.
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