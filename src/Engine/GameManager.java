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

public class GameManager {
    //Game objects
    public Paddle paddle;
    public List<Ball> balls;
    public List<Brick> bricks;
    public List<Laser> lasers;

    //Managers
    private CollisionManager collisionManager;
    private PowerUpManager powerUpManager;
    private RoundsManager roundsManager;
    private ScoreManager scoreManager;
    private StateManager stateManager;

    //Game state
    private int width;
    private int height;
    private int lives;

    public GameManager() {
        this.width = Constants.Window.WINDOW_WIDTH;
        // Height should be the full window height (absolute coordinates). Many game
        // objects use absolute window Y coordinates (0..WINDOW_HEIGHT) while UI
        // elements occupy the top portion (WINDOW_TOP_OFFSET). Using
        // WINDOW_HEIGHT - WINDOW_TOP_OFFSET caused inconsistent bounds checks and
        // placed the play area incorrectly. Use absolute window height here and
        // pass an adjusted top border to collision checks instead.
        this.height = Constants.Window.WINDOW_HEIGHT;
        this.lives = Constants.GameRules.INITIAL_LIVES;

        this.balls = new ArrayList<>();
        this.bricks = new ArrayList<>();
        this.lasers = new ArrayList<>();

        this.collisionManager = new CollisionManager(width, height);
        this.powerUpManager = PowerUpManager.getInstance();
        this.powerUpManager.setGameManager(this);
        this.roundsManager = new RoundsManager();
        this.scoreManager = new ScoreManager();
        this.stateManager = new StateManager();
        // Initialize the game objects (paddle, initial ball, bricks)
        // This must run here so that the main loop can safely call update()
        initGame();
    }

    private void initGame() {
        //Initialize paddle
        double paddleWidth = Constants.Paddle.PADDLE_WIDTH;
        double paddleHeight = Constants.Paddle.PADDLE_HEIGHT;
        paddle = new Paddle(
                (width - paddleWidth) / 2.0,
                height - paddleHeight - 60,
                paddleWidth,
                paddleHeight
        );

        paddle.playMaterializeAnimation();

        double ballRadius = Constants.Ball.BALL_RADIUS;
        Ball ball = new Ball(
                (width/2.0) - ballRadius,
                height - 80,
                ballRadius,
                new Velocity(0, 0));

        ball.setAttached(true);

        balls.add(ball);

        bricks = roundsManager.loadFirstRound();
    }

    public void update() {
        if (!stateManager.isPlaying()) {
            return;
        }

        // Update paddle
        paddle.update();

        // Update position of ALL attached balls to follow paddle
        for (Ball ball : balls) {
            if (ball.isAttached()) {
                double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
                double ballY = paddle.getY() - ball.getHeight() - 1.0;
                ball.setCenter(new Point(paddleCenterX, ballY + ball.getHeight() / 2.0));
            }
        }
        checkPaddleOutOfBounds();

        // Update all balls
        for (Ball ball : balls) {
            if (!ball.isAttached()) {
                ball.update();
            }
        }

        // Update lasers
        for (Laser laser : lasers) {
            laser.update();
        }

        // Update bricks (for animations like crack animation)
        for (Brick brick : bricks) {
            if (brick.isAlive()) {
                brick.update();
            }
        }

        powerUpManager.update(paddle);

        lasers.removeIf(Laser::isOffScreen);

        handleCollisions();
        checkGameConditions();
        checkPaddleOutOfBounds();
    }

    private void checkPaddleOutOfBounds() {
        if (paddle.getX() < Constants.Window.WINDOW_SIDE_OFFSET) {
            paddle.setX(Constants.Window.WINDOW_SIDE_OFFSET);
        } else if (paddle.getX() + paddle.getWidth() > width - Constants.Window.WINDOW_SIDE_OFFSET) {
            paddle.setX(width - paddle.getWidth() - Constants.Window.WINDOW_SIDE_OFFSET);
        }
    }

    private void handleCollisions() {
        // Ball collisions
        for (Ball ball : balls) {
            // Pass absolute top border (top UI offset + top border sprite height)
            collisionManager.checkBallWallCollisions(ball,
                    Constants.Borders.BORDER_SIDE_WIDTH,
                    Constants.Window.WINDOW_WIDTH - Constants.Borders.BORDER_SIDE_WIDTH,
                    Constants.Window.WINDOW_TOP_OFFSET + Constants.Borders.BORDER_TOP_HEIGHT);

            if (collisionManager.checkBallPaddleCollision(ball, paddle)) {
                if(paddle.isCatchModeEnabled() && !ball.isAttached()) {
                    ball.setVelocity(new Velocity(0,0));
                    ball.setAttached(true);
                }
            }

            List<Brick> destroyedBricks = collisionManager.checkBallBrickCollisions(ball, bricks);

            for (Brick brick : destroyedBricks) {
                BrickType type = brick.getBrickType();
                scoreManager.addDestroyBrickScore(type);

                powerUpManager.spawnFromBrick(brick.getX(), brick.getY(), type);
            }
        }

        Map<Laser, Brick> laserBrickHits = collisionManager.checkLaserBrickCollisions(lasers, bricks);

        for (Map.Entry<Laser, Brick> entry : laserBrickHits.entrySet()) {
            Laser laser = entry.getKey();
            Brick brick = entry.getValue();

            laser.destroy();

            BrickType type = brick.getBrickType();
            scoreManager.addDestroyBrickScore(type);
        }
    }

    private void checkGameConditions() {
        // Check for ball out of bounds
        balls.removeIf(ball -> ball.getY() > height);

        if (balls.isEmpty()) {
            loseLife();
            return;
        }

        // Check for round completion
        if (roundsManager.isRoundComplete()) {
            if (roundsManager.hasNextRound()) {
                stateManager.setState(GameState.LEVEL_COMPLETE);

                // Clear all falling power-ups when transitioning to next level
                powerUpManager.clearAllPowerUps();

                // Schedule next round load after delay
                // In a real implementation, this would be in a timer callback
                roundsManager.nextRound();
                bricks = roundsManager.getCurrentBricks();
                stateManager.setState(GameState.PLAYING);

                resetBall();
            } else {
                // All rounds completed - WIN!
                // Clear all falling power-ups when winning
                powerUpManager.clearAllPowerUps();
                stateManager.setState(GameState.WIN);
            }
        }
    }

    private void loseLife() {
        lives--;
        scoreManager.applyLoseLifePenalty();

        paddle.playExplodeAnimation();

        if (lives <= 0) {
            // Clear all falling power-ups when game is over
            powerUpManager.clearAllPowerUps();
            stateManager.setState(GameState.GAME_OVER);
        } else {
            resetBall();
        }
    }

    private void resetBall() {
        balls.clear();

        // Reset paddle to normal state (clear all effects)
        resetPaddleEffects();

        double ballRadius = Constants.Ball.BALL_RADIUS;
        double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
        double ballY = paddle.getY() - ballRadius * 2 - 5;

        Ball ball = new Ball(paddleCenterX - ballRadius, ballY, ballRadius, new Velocity(0, 0));
        ball.setAttached(true);
        balls.add(ball);

        paddle.playMaterializeAnimation();
    }

    /**
     * Resets all paddle effects when player loses a life.
     * This ensures paddle returns to normal size and state.
     */
    private void resetPaddleEffects() {
        PaddleState currentState = paddle.getState();
        System.out.println("resetPaddleEffects: Current paddle state = " + currentState +
                ", width = " + paddle.getWidth());

        // Reset expand effect (shrink to normal if expanded)
        // Check width directly instead of state, in case state changed
        if (paddle.getWidth() > Constants.Paddle.PADDLE_WIDTH + 1) {
            // Paddle is still wide, force shrink
            double centerX = paddle.getX() + paddle.getWidth() / 2.0;
            paddle.setWidth(Constants.Paddle.PADDLE_WIDTH);
            paddle.setX(centerX - paddle.getWidth() / 2.0);
            System.out.println("resetPaddleEffects: Forced paddle width reset");
        }

        // Reset laser effect
        if (currentState == PaddleState.LASER ||
                currentState == PaddleState.LASER_PULSATE) {
            paddle.disableLaser();
        }

        // Reset catch mode
        if (paddle.isCatchModeEnabled()) {
            paddle.setCatchModeEnabled(false);
        }

        // Clear slow effect timer (ball speeds are not affected by paddle reset)
        paddle.clearSlowEffect();

        // Force state to NORMAL (clear any lingering states)
        if (currentState != PaddleState.NORMAL) {
            paddle.setState(PaddleState.NORMAL);
        }

        System.out.println("resetPaddleEffects: After reset - state = " + paddle.getState() +
                ", width = " + paddle.getWidth());
    }

    public void resetGame() {
        lives = Constants.GameRules.INITIAL_LIVES;

        // Reset managers
        scoreManager.resetScore();
        scoreManager.resetMultiplier();
        roundsManager.reset();
        powerUpManager = PowerUpManager.getInstance();
        powerUpManager.setGameManager(this);
        stateManager.setState(GameState.MENU);

        // Clear lists
        balls.clear();
        lasers.clear();

        // Reinitialize game
        initGame();
    }

    public void launchBall() {
        if (balls.isEmpty()) {
            return;
        }

        // Launch one ball at a time (first attached ball found)
        for (Ball ball : balls) {
            if (ball.isAttached()) {
                ball.setAttached(false);
                ball.setVelocity(new Velocity(0, -Constants.Ball.BALL_INITIAL_SPEED));
                return; // Only launch one ball per key press
            }
        }
    }

    public void shootLaser() {
        if (!paddle.isLaserEnabled()) {
            return;
        }

        List<Laser> newLasers = paddle.shootLaser();
        lasers.addAll(newLasers);

        if (!newLasers.isEmpty()) {
            System.out.println("GameManager: Fired " + newLasers.size() + " lasers");
        }
    }

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
     * @return Number of active balls
     */
    public int getBallCount() {
        return balls.size();
    }

    /**
     * Duplicates all balls (DUPLICATE PowerUp).
     * Creates copies of existing balls with angled velocities.
     */
    public void duplicateBalls() {
        List<Ball> newBalls = new ArrayList<>();

        for (Ball ball : balls) {
            // If ball is attached (catch mode), duplicate as attached
            if (ball.isAttached()) {
                // Create two copies at same position, also attached
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
                // Create two copies with ±30° angles
                Velocity vel = ball.getVelocity();
                double speed = Math.hypot(vel.getDx(), vel.getDy());
                double angle = Math.atan2(vel.getDy(), vel.getDx());

                // Left ball (-30°)
                double leftAngle = angle - Math.toRadians(30);
                Ball leftBall = new Ball(
                        ball.getX(), ball.getY(),
                        Constants.Ball.BALL_RADIUS,
                        new Velocity(speed * Math.cos(leftAngle), speed * Math.sin(leftAngle))
                );
                newBalls.add(leftBall);

                // Right ball (+30°)
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
     * Expands paddle width (EXPAND PowerUp).
     */
    public void expandPaddle() {
        paddle.expand();
        System.out.println("GameManager: Paddle expanded");
    }

    /**
     * Reverts paddle to original size.
     */
    public void revertPaddleSize() {
        // Handled automatically by Paddle.update() after 10 seconds
        System.out.println("GameManager: Paddle size will revert automatically");
    }

    /**
     * Enables laser shooting on paddle (LASER PowerUp).
     */
    public void enableLaser() {
        paddle.enableLaser();
        System.out.println("GameManager: Laser enabled with " + Constants.Laser.LASER_SHOTS + " shots");
    }

    /**
     * Disables laser shooting on paddle.
     */
    public void disableLaser() {
        paddle.setLaserEnabled(false);
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
     * Maximum lives defined in Constants.
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
     * Slows down all balls (SLOW PowerUp).
     * @param multiplier Speed multiplier (e.g., 0.7)
     */
    public void slowBalls(double multiplier) {
        for (Ball ball : balls) {
            Velocity currentVel = ball.getVelocity();
            double newDx = currentVel.getDx() * multiplier;
            double newDy = currentVel.getDy() * multiplier;
            ball.setVelocity(new Velocity(newDx, newDy));
        }

        // Set slow effect expiry time on paddle for warning animation
        long expiryTime = System.currentTimeMillis() + Constants.PowerUps.SLOW_DURATION;
        paddle.setSlowEffectExpiry(expiryTime);

        System.out.println("GameManager: Balls slowed by " + multiplier + "x");
    }

    /**
     * Restores original ball speed (removes SLOW effect).
     */
    public void restoreBallSpeed() {
        for (Ball ball : balls) {
            Velocity currentVel = ball.getVelocity();
            double speed = Math.hypot(currentVel.getDx(), currentVel.getDy());

            // If speed is very slow, restore to initial speed
            if (speed < Constants.Ball.BALL_MIN_SPEED) {
                double angle = Math.atan2(currentVel.getDy(), currentVel.getDx());
                double newDx = Math.cos(angle) * Constants.Ball.BALL_INITIAL_SPEED;
                double newDy = Math.sin(angle) * Constants.Ball.BALL_INITIAL_SPEED;
                ball.setVelocity(new Velocity(newDx, newDy));
            } else {
                // Restore to normal speed
                double restoreMultiplier = 1.0 / 0.7; // Inverse of slow multiplier
                double newDx = currentVel.getDx() * restoreMultiplier;
                double newDy = currentVel.getDy() * restoreMultiplier;
                ball.setVelocity(new Velocity(newDx, newDy));
            }
        }

        // Clear slow effect expiry time on paddle
        paddle.clearSlowEffect();

        System.out.println("GameManager: Ball speed restored");
    }

    /**
     * Warps to next level (WARP PowerUp).
     * Clears current round and advances.
     */
    public boolean warpToNextLevel() {
        if (roundsManager.hasNextRound()) {
            // Clear all falling power-ups when warping to next level
            powerUpManager.clearAllPowerUps();

            roundsManager.nextRound();
            bricks = roundsManager.getCurrentBricks();
            resetBall();
            System.out.println("GameManager: Warped to next level!");
            return true;
        } else {
            // Already on last level
            // Clear all falling power-ups when winning
            powerUpManager.clearAllPowerUps();
            stateManager.setState(GameState.WIN);
            return false;
        }
    }

    /**
     * Gets the PowerUpManager instance.
     * @return PowerUpManager instance
     */
    public PowerUpManager getPowerUpManager() {
        return powerUpManager;
    }

    /**
     * Gets the StateManager instance.
     * @return StateManager instance
     */
    public StateManager getStateManager() {
        return stateManager;
    }

    /**
     * Gets the RoundsManager instance.
     * @return RoundsManager instance
     */
    public RoundsManager getRoundsManager() {
        return roundsManager;
    }

    /**
     * Gets the ScoreManager instance.
     * @return ScoreManager instance
     */
    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    /**
     * Gets the CollisionManager instance.
     * @return CollisionManager instance
     */
    public CollisionManager getCollisionManager() {
        return collisionManager;
    }

    /**
     * Gets current score (from ScoreManager).
     * @return Current score
     */
    public int getScore() {
        return scoreManager.getScore();
    }

    /**
     * Checks if game is over.
     * @return true if game ended (GAME_OVER or WIN state)
     */
    public boolean isGameOver() {
        return stateManager.isGameOver();
    }

    /**
     * Checks if player won.
     * @return true if in WIN state
     */
    public boolean hasWon() {
        return stateManager.getState() == GameState.WIN;
    }

    /**
     * Gets list of active lasers.
     * @return List of lasers
     */
    public List<Laser> getLasers() {
        return lasers;
    }

    public boolean isAttached() {
        for (Ball ball : balls) {
            if (ball.isAttached()) {
                return true;
            }
        }
        return false;
    }

}