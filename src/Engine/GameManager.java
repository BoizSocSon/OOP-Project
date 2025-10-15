package Engine;

import Objects.GameEntities.Ball;
import Objects.GameEntities.Paddle;
import Objects.GameEntities.Laser;
import Objects.Bricks.Brick;
import GeometryPrimitives.Point;
import GeometryPrimitives.Velocity;
import Utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Quản lý trạng thái và quy tắc chơi của trò Arkanoid.
 *
 * REFACTORED VERSION - Tuần 2:
 * - Integrated managers: CollisionManager, StateManager, RoundsManager, PowerUpManager
 * - Simplified update loop
 * - Proper state machine handling
 * - Multi-ball and laser support (lists)
 *
 * @author SteveHoang aka BoizSocSon
 */
public class GameManager {
    // Game objects
    public Paddle paddle;
    public List<Ball> balls = new ArrayList<>();
    public List<Laser> lasers = new ArrayList<>();
    public List<Brick> bricks = new ArrayList<>();
    
    // Managers
    private CollisionManager collisionManager;
    private PowerUpManager powerUpManager;
    private RoundsManager roundsManager;
    private StateManager stateManager;
    private ScoreManager scoreManager;
    // private AudioManager audioManager; // To be implemented
    
    // Game state
    public int width;
    public int height;
    public int lives = Constants.GameRules.INITIAL_LIVES;
    public boolean ballAttached = true; // Ball stuck to paddle before launch

    /**
     * Creates GameManager with play area dimensions.
     * 
     * NOTE: width and height are PLAY AREA size (excluding UI bar).
     * CanvasRenderer will add offset when rendering.
     *
     * @param width  Play area width (pixels) - usually 600
     * @param height Play area height (pixels) - usually 650 (800 - 150 UI bar)
     */
    public GameManager(int width, int height) {
        this.width = width; 
        this.height = height;
        
        // Initialize managers
        this.collisionManager = new CollisionManager(width, height);
        this.powerUpManager = PowerUpManager.getInstance();
        this.powerUpManager.setGameManager(this);
        this.roundsManager = new RoundsManager(width, height);
        this.stateManager = new StateManager();
        this.scoreManager = new ScoreManager();
        
        // Initialize game
        initGame();
    }

    /**
     * Initializes game objects and loads first round.
     */
    private void initGame() {
        // Create paddle - use actual sprite dimensions
        double paddleW = Constants.Physics.PADDLE_WIDTH;
        double paddleH = Constants.Physics.PADDLE_HEIGHT;
        paddle = new Paddle((width - paddleW) / 2.0, height - 60, paddleW, paddleH, Constants.Physics.PADDLE_SPEED);
        
        // Trigger materialize animation
        paddle.playMaterializeAnimation();

        // Create ball - use actual sprite dimensions
        double ballRadius = Constants.Physics.BALL_RADIUS;
        Ball ball = new Ball((width/2.0) - ballRadius, height - 80, ballRadius, new Velocity(0, 0));
        balls.add(ball);
        ballAttached = true;

        // Load first round
        bricks = roundsManager.loadFirstRound();
    }

    /**
     * Main update loop - called every frame.
     * 
     * NEW ARCHITECTURE (Tuần 2):
     * - Uses StateManager to control flow
     * - Delegates collisions to CollisionManager
     * - Cleaner, more maintainable code
     */
    public void update() {
        // Only update if game is playing
        if (!stateManager.isPlaying()) {
            return;
        }
        
        // Update game objects
        paddle.update();
        
        // If ball is attached to paddle, keep it positioned
        if (ballAttached && !balls.isEmpty()) {
            Ball ball = balls.get(0);
            double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
            double ballY = paddle.getY() - ball.getHeight() - 1.0;
            ball.setCenter(new Point(paddleCenterX, ballY + ball.getHeight() / 2.0));
            return; // Skip physics until launch
        }
        
        // Update all balls
        for (Ball ball : balls) {
            ball.update();
        }
        
        // Update all lasers
        for (Laser laser : lasers) {
            laser.update();
        }
        
        // Update PowerUpManager (falling powerups)
        powerUpManager.update(paddle);
        
        // Note: PowerUpManager handles its own collision detection and applies effects
        // No need to manually check collisions here
        
        // Handle all collisions
        handleCollisions();
        
        // Remove off-screen lasers
        lasers.removeIf(Laser::isOffScreen);
        
        // Check game conditions (win/lose)
        checkGameConditions();
        
        // Clamp paddle to screen bounds
        if (paddle.getX() < 0) {
            paddle.setX(0);
        }
        if (paddle.getX() + paddle.getWidth() > width) {
            paddle.setX(width - paddle.getWidth());
        }
    }
    
    /**
     * Handles all collision detection and responses.
     * Delegates to CollisionManager for clean separation.
     */
    private void handleCollisions() {
        // Ball collisions
        for (Ball ball : new ArrayList<>(balls)) {
            // Ball vs walls
            collisionManager.checkBallWallCollisions(ball, 0, width, 0);
            
            // Ball vs paddle
            if (collisionManager.checkBallPaddleCollision(ball, paddle)) {
                // Check if catch mode triggered
                if (paddle.isCatchModeEnabled() && !ballAttached) {
                    ballAttached = true;
                    ball.setVelocity(new Velocity(0, 0));
                    System.out.println("GameManager: Ball caught!");
                }
            }
            
            // Ball vs bricks
            List<Brick> destroyedBricks = collisionManager.checkBallBrickCollisions(ball, bricks);
            
            // Award score for destroyed bricks
            for (Brick brick : destroyedBricks) {
                int points = brick instanceof Objects.Bricks.SilverBrick ? 100 : 50;
                scoreManager.addScore(points);
                
                // Spawn PowerUp from brick
                double brickCenterX = brick.getX() + brick.getWidth() / 2.0;
                double brickCenterY = brick.getY() + brick.getHeight() / 2.0;
                powerUpManager.spawnFromBrick(brickCenterX, brickCenterY, Objects.Bricks.BrickType.BLUE);
            }
        }
        
        // Laser collisions
        Map<Laser, Brick> laserHits = collisionManager.checkLaserBrickCollisions(lasers, bricks);
        
        for (Map.Entry<Laser, Brick> entry : laserHits.entrySet()) {
            Laser laser = entry.getKey();
            Brick brick = entry.getValue();
            
            // Destroy laser
            laser.destroy();
            
            // Award score
            int points = brick instanceof Objects.Bricks.SilverBrick ? 100 : 50;
            scoreManager.addScore(points);
        }
    }
    
    /**
     * Checks win/lose conditions and updates game state.
     */
    private void checkGameConditions() {
        // Check if all balls fell off screen (lose life)
        balls.removeIf(ball -> ball.getY() > height);
        
        if (balls.isEmpty()) {
            loseLife();
            return;
        }
        
        // Check if round is complete
        if (roundsManager.isRoundComplete()) {
            if (roundsManager.hasNextRound()) {
                stateManager.setState(GameState.LEVEL_COMPLETE);
                
                // Schedule next round load after delay
                // In a real implementation, this would be in a timer callback
                roundsManager.nextRound();
                bricks = roundsManager.getCurrentBricks();
                stateManager.setState(GameState.PLAYING);
            } else {
                // All rounds completed - WIN!
                stateManager.setState(GameState.WIN);
            }
        }
    }
    
    /**
     * Handles losing a life.
     */
    private void loseLife() {
        lives--;
        scoreManager.addScore(-500); // Penalty
        
        paddle.playExplodeAnimation();
        // AudioManager.playSFX(LOSE_LIFE) - to be implemented
        
        if (lives <= 0) {
            // Game Over
            stateManager.setState(GameState.GAME_OVER);
        } else {
            // Reset ball
            resetBall();
        }
    }
    
    /**
     * Resets ball to paddle position after losing a life.
     */
    private void resetBall() {
        balls.clear();
        
        double ballRadius = Constants.Physics.BALL_RADIUS;
        double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
        double ballY = paddle.getY() - ballRadius * 2 - 5;
        
        Ball ball = new Ball(paddleCenterX - ballRadius, ballY, ballRadius, new Velocity(0, 0));
        balls.add(ball);
        ballAttached = true;
        
        paddle.playMaterializeAnimation();
    }

    /**
     * Resets game to initial state.
     * Called when player wants to restart.
     */
    public void reset() {
        lives = Constants.GameRules.INITIAL_LIVES;
        ballAttached = true;
        
        // Reset managers
        scoreManager.reset();
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

    /**
     * Launches ball from paddle.
     * Supports multi-ball - launches all attached balls.
     */
    public void launchBall() {
        if (!ballAttached || balls.isEmpty()) {
            return;
        }
        
        ballAttached = false;
        
        // Launch all balls with initial velocity
        for (Ball ball : balls) {
            ball.setVelocity(new Velocity(0, -Constants.Physics.BALL_INITIAL_SPEED));
        }
        
        System.out.println("GameManager: Ball(s) launched!");
    }
    
    /**
     * Shoots laser from paddle (Space bar or fire button).
     * Called by input handler when player wants to shoot.
     */
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
            // Create two copies with ±30° angles
            Velocity vel = ball.getVelocity();
            double speed = Math.hypot(vel.getDx(), vel.getDy());
            double angle = Math.atan2(vel.getDy(), vel.getDx());
            
            // Left ball (-30°)
            double leftAngle = angle - Math.toRadians(30);
            Ball leftBall = new Ball(
                ball.getX(), ball.getY(), 
                Constants.Physics.BALL_RADIUS,
                new Velocity(speed * Math.cos(leftAngle), speed * Math.sin(leftAngle))
            );
            newBalls.add(leftBall);
            
            // Right ball (+30°)
            double rightAngle = angle + Math.toRadians(30);
            Ball rightBall = new Ball(
                ball.getX(), ball.getY(),
                Constants.Physics.BALL_RADIUS,
                new Velocity(speed * Math.cos(rightAngle), speed * Math.sin(rightAngle))
            );
            newBalls.add(rightBall);
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
     * @param shots Number of laser shots available
     */
    public void enableLaser(int shots) {
        paddle.enableLaser(shots);
        System.out.println("GameManager: Laser enabled with " + shots + " shots");
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
            if (speed < Constants.Physics.BALL_MIN_SPEED) {
                double angle = Math.atan2(currentVel.getDy(), currentVel.getDx());
                double newDx = Math.cos(angle) * Constants.Physics.BALL_INITIAL_SPEED;
                double newDy = Math.sin(angle) * Constants.Physics.BALL_INITIAL_SPEED;
                ball.setVelocity(new Velocity(newDx, newDy));
            } else {
                // Restore to normal speed
                double restoreMultiplier = 1.0 / 0.7; // Inverse of slow multiplier
                double newDx = currentVel.getDx() * restoreMultiplier;
                double newDy = currentVel.getDy() * restoreMultiplier;
                ball.setVelocity(new Velocity(newDx, newDy));
            }
        }
        System.out.println("GameManager: Ball speed restored");
    }
    
    /**
     * Warps to next level (WARP PowerUp).
     * Clears current round and advances.
     */
    public boolean warpToNextLevel() {
        if (roundsManager.hasNextRound()) {
            roundsManager.nextRound();
            bricks = roundsManager.getCurrentBricks();
            resetBall();
            System.out.println("GameManager: Warped to next level!");
            return true;
        } else {
            // Already on last level
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

}
