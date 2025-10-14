package Render;

import Objects.GameEntities.Ball;
import Objects.GameEntities.Paddle;
import Objects.Bricks.Brick;
import Objects.Bricks.NormalBrick;
import Objects.Bricks.SilverBrick;
import Objects.PowerUps.PowerUp;
import Utils.AssetLoader;
import Utils.SpriteCache;
import Utils.Constants;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class CanvasRenderer implements Renderer {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final SpriteRenderer spriteRenderer;
    private final BorderRenderer borderRenderer;
    private final SpriteCache spriteCache;

    private static final double UI_BAR_HEIGHT = 150.0;
    private static final double PLAY_AREA_Y_OFFSET = UI_BAR_HEIGHT;

    private Image logoImage;
    private Image paddleLifeImage;
    private Font scoreFont;
    private Font uiFont;

    public CanvasRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.spriteRenderer = new SpriteRenderer(gc);

        double playAreaHeight = canvas.getHeight() - UI_BAR_HEIGHT;
        this.borderRenderer = new BorderRenderer(canvas.getWidth(), playAreaHeight);

        this.spriteCache = SpriteCache.getInstance();
        loadUIAssets();
    }

    private void loadUIAssets() {
        try {
            logoImage = AssetLoader.loadImage("logo.png");
            paddleLifeImage = AssetLoader.loadImage("paddle_life.png");

            try {
                scoreFont = Font.loadFont(
                        getClass().getResourceAsStream(Constants.Paths.FONTS_PATH + "arkanoid.ttf"), 24
                );
                uiFont = Font.loadFont(
                        getClass().getResourceAsStream(Constants.Paths.FONTS_PATH + "arkanoid.ttf"), 18
                );
            } catch (Exception e) {
                scoreFont = Font.font("Monospaced", 24);
                uiFont = Font.font("Monospaced", 18);
            }
        } catch (Exception e) {
            System.err.println("CanvasRenderer: Failed to load UI assets: " + e.getMessage());
        }
    }

    @Override
    public void clear() {
        gc.setFill(Constants.Colors.COLOR_BACKGROUND);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawTopUI(int score, int highScore) {
        if (logoImage != null) {
            double logoScale = 0.4;
            double logoX = 20;
            double logoY = (UI_BAR_HEIGHT - logoImage.getHeight() * logoScale) / 2.0;
            spriteRenderer.drawSpriteScaled(logoImage, logoX, logoY,
                    logoImage.getWidth() * logoScale, logoImage.getHeight() * logoScale);
        }

        gc.setFont(uiFont);
        gc.setFill(Color.RED);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("1UP", canvas.getWidth() * 0.4, 50);

        gc.setFont(scoreFont);
        gc.setFill(Constants.Colors.COLOR_TEXT_TITLE);
        gc.fillText(String.valueOf(score), canvas.getWidth() * 0.4, 85);

        gc.setFont(uiFont);
        gc.setFill(Color.RED);
        gc.fillText("HIGH SCORE", canvas.getWidth() * 0.7, 50);

        gc.setFont(scoreFont);
        gc.setFill(Constants.Colors.COLOR_TEXT_TITLE);
        gc.fillText(String.valueOf(highScore), canvas.getWidth() * 0.7, 85);
    }

    public void drawBorders() {
        gc.save();
        gc.translate(0, PLAY_AREA_Y_OFFSET);
        borderRenderer.render(gc);
        gc.restore();
    }

    public void drawLivesDisplay(int lives) {
        if (paddleLifeImage == null || lives <= 0) {
            return;
        }

        double lifeWidth = Constants.Physics.PADDLE_LIFE_WIDTH;
        double lifeHeight = Constants.Physics.PADDLE_LIFE_HEIGHT;
        double startX = 10;
        double startY = canvas.getHeight() - 60;

        for (int i = 0; i < lives; i++) {
            double lifeX = startX + i * (lifeWidth + 5);
            spriteRenderer.drawSprite(paddleLifeImage, lifeX, startY, lifeWidth, lifeHeight);
        }
    }

    @Override
    public void drawBall(Ball ball) {
        Image ballSprite = spriteCache.get("ball.png");
        if (ballSprite != null) {
            double renderX = ball.getX();
            double renderY = ball.getY() + PLAY_AREA_Y_OFFSET;
            spriteRenderer.drawSprite(ballSprite, renderX, renderY,
                    Constants.Physics.BALL_SIZE, Constants.Physics.BALL_SIZE);
        } else {
            gc.setFill(Color.WHITE);
            gc.fillOval(ball.getX(), ball.getY() + PLAY_AREA_Y_OFFSET,
                    ball.getBounds().getWidth(), ball.getBounds().getHeight());
        }
    }

    @Override
    public void drawPaddle(Paddle paddle) {
        double x = paddle.getBounds().getUpperLeft().getX();
        double y = paddle.getBounds().getUpperLeft().getY() + PLAY_AREA_Y_OFFSET;
        double w = paddle.getWidth();
        double h = paddle.getBounds().getHeight();

        if (paddle.isAnimationPlaying()) {
            Animation animation = paddle.getAnimation();
            if (animation != null) {
                Image currentFrame = animation.getCurrentFrame();
                if (currentFrame != null) {
                    spriteRenderer.drawSprite(currentFrame, x, y, w, h);
                    return;
                }
            }
        }

        Image paddleSprite = spriteCache.get("paddle.png");
        if (paddleSprite != null) {
            spriteRenderer.drawSprite(paddleSprite, x, y, w, h);
        } else {
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(x, y, w, h);
        }
    }

    @Override
    public void drawBrick(Brick brick) {
        double x = brick.getX();
        double y = brick.getY() + PLAY_AREA_Y_OFFSET;
        double w = Constants.Bricks.BRICK_WIDTH;
        double h = Constants.Bricks.BRICK_HEIGHT;

        if (brick instanceof SilverBrick) {
            SilverBrick silverBrick = (SilverBrick) brick;
            Animation crackAnim = silverBrick.getCrackAnimation();

            if (crackAnim != null && crackAnim.isPlaying()) {
                Image currentFrame = crackAnim.getCurrentFrame();
                if (currentFrame != null) {
                    spriteRenderer.drawSprite(currentFrame, x, y, w, h);
                }
            } else {
                Image silverSprite = spriteCache.get("brick_silver.png");
                if (silverSprite != null) {
                    spriteRenderer.drawSprite(silverSprite, x, y, w, h);
                } else {
                    gc.setFill(Color.DARKGRAY);
                    gc.fillRect(x, y, w, h);
                }
            }
        } else if (brick instanceof NormalBrick) {
            NormalBrick normalBrick = (NormalBrick) brick;
            String spriteName = "brick_" + normalBrick.getType().name().toLowerCase() + ".png";
            Image sprite = spriteCache.get(spriteName);

            if (sprite != null) {
                spriteRenderer.drawSprite(sprite, x, y, w, h);
            } else {
                gc.setFill(Color.BLUE);
                gc.fillRect(x, y, w, h);
            }
        } else {
            gc.setFill(Color.GRAY);
            gc.fillRect(x, y, w, h);
        }
    }

    @Override
    public void drawPowerUp(PowerUp powerUp) {
        if (powerUp == null || !powerUp.isActive()) {
            return;
        }

        Animation animation = powerUp.getAnimation();
        if (animation != null && animation.isPlaying()) {
            Image currentFrame = animation.getCurrentFrame();
            if (currentFrame != null) {
                double x = powerUp.getX();
                double y = powerUp.getY() + PLAY_AREA_Y_OFFSET;
                double w = Constants.PowerUps.POWERUP_WIDTH;
                double h = Constants.PowerUps.POWERUP_HEIGHT;
                spriteRenderer.drawSprite(currentFrame, x, y, w, h);
            }
        }
    }

    @Override
    public void drawText(String text, double x, double y) {
        gc.setFill(Constants.Colors.COLOR_TEXT_BODY);
        gc.setFont(scoreFont);
        gc.fillText(text, x, y + PLAY_AREA_Y_OFFSET);
    }

    @Override
    public void drawImage(Image img, double x, double y) {
        if (img != null) {
            gc.drawImage(img, x, y);
        }
    }

    @Override
    public void drawSprite(String name, double x, double y) {
        Image sprite = spriteCache.get(name);
        if (sprite != null) {
            spriteRenderer.drawSprite(sprite, x, y);
        }
    }

    @Override
    public void present() {
    }
}
