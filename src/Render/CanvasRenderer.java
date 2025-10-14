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

/**
 * CanvasRenderer - Lớp chịu trách nhiệm vẽ toàn bộ nội dung trò chơi lên Canvas.
 *
 * <p>Chức năng chính:</p>
 * <ul>
 *   <li>Xóa nền khung hình</li>
 *   <li>Vẽ UI phía trên (logo, điểm, high score)</li>
 *   <li>Vẽ đường viền khu vực chơi (bằng BorderRenderer)</li>
 *   <li>Vẽ thực thể game: bóng, thanh đỡ, gạch, power-up</li>
 *   <li>Vẽ text/sprite tự do theo yêu cầu</li>
 * </ul>
 *
 * <p>Ghi chú bố cục:</p>
 * <ul>
 *   <li>Thanh UI phía trên có chiều cao cố định {@code UI_BAR_HEIGHT}</li>
 *   <li>Khu vực chơi (play area) được dời xuống theo {@code PLAY_AREA_Y_OFFSET}</li>
 * </ul>
 */
public class CanvasRenderer implements Renderer {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final SpriteRenderer spriteRenderer;
    private final BorderRenderer borderRenderer;
    private final SpriteCache spriteCache;

    private static final double UI_BAR_HEIGHT = 150.0;      // Chiều cao vùng UI phía trên
    private static final double PLAY_AREA_Y_OFFSET = UI_BAR_HEIGHT; // Offset vẽ khu vực chơi

    private Image logoImage;
    private Image paddleLifeImage;
    private Font scoreFont;
    private Font uiFont;

    /**
     * Khởi tạo renderer với một {@link Canvas} đích.
     * <ul>
     *   <li>Lấy {@link GraphicsContext} từ canvas</li>
     *   <li>Tạo {@link SpriteRenderer} dùng chung</li>
     *   <li>Khởi tạo {@link BorderRenderer} với kích thước khu vực chơi (canvasHeight - UI)</li>
     *   <li>Nạp tài nguyên UI (logo, font, icon mạng)</li>
     * </ul>
     *
     * @param canvas Canvas đích để vẽ
     */
    public CanvasRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();       // Lấy GC để vẽ 2D
        this.spriteRenderer = new SpriteRenderer(gc);  // Dùng chung cho vẽ sprite

        double playAreaHeight = canvas.getHeight() - UI_BAR_HEIGHT; // Phần chiều cao còn lại sau UI
        this.borderRenderer = new BorderRenderer(canvas.getWidth(), playAreaHeight); // Renderer viền

        this.spriteCache = SpriteCache.getInstance();  // Cache sprite dùng chung
        loadUIAssets();                                 // Nạp tài nguyên UI
    }

    /**
     * Nạp tài nguyên UI: logo, icon mạng sống, font chữ.
     * <p>Nếu font tùy biến không tải được sẽ fallback sang Monospaced.</p>
     */
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
                scoreFont = Font.font("Monospaced", 24); // Fallback khi font không tải được
                uiFont = Font.font("Monospaced", 18);
            }
        } catch (Exception e) {
            System.err.println("CanvasRenderer: Failed to load UI assets: " + e.getMessage());
        }
    }

    /**
     * Xóa khung hình hiện tại và tô nền theo màu cấu hình.
     */
    @Override
    public void clear() {
        gc.setFill(Constants.Colors.COLOR_BACKGROUND);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Tô kín toàn bộ canvas
    }

    /**
     * Vẽ thanh UI phía trên: logo, nhãn “1UP”, điểm hiện tại và “HIGH SCORE”.
     *
     * @param score     điểm hiện tại
     * @param highScore điểm cao nhất
     */
    public void drawTopUI(int score, int highScore) {
        if (logoImage != null) {
            double logoScale = 0.4;
            double logoX = 20;
            double logoY = (UI_BAR_HEIGHT - logoImage.getHeight() * logoScale) / 2.0; // Căn giữa theo chiều dọc của UI
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

    /**
     * Vẽ đường viền khu vực chơi. Nội dung viền được vẽ trong hệ tọa độ
     * đã dịch xuống theo {@code PLAY_AREA_Y_OFFSET} để không đè lên UI.
     */
    public void drawBorders() {
        gc.save();
        gc.translate(0, PLAY_AREA_Y_OFFSET); // Dịch gốc tọa độ xuống dưới UI
        borderRenderer.render(gc);
        gc.restore();
    }

    /**
     * Vẽ số mạng còn lại ở góc dưới. Mỗi mạng là một icon paddle nhỏ.
     *
     * @param lives số mạng
     */
    public void drawLivesDisplay(int lives) {
        if (paddleLifeImage == null || lives <= 0) {
            return;
        }

        double lifeWidth = Constants.Physics.PADDLE_LIFE_WIDTH;
        double lifeHeight = Constants.Physics.PADDLE_LIFE_HEIGHT;
        double startX = 10;
        double startY = canvas.getHeight() - 60; // Vị trí hiển thị gần đáy

        for (int i = 0; i < lives; i++) {
            double lifeX = startX + i * (lifeWidth + 5); // Cách nhau 5px
            spriteRenderer.drawSprite(paddleLifeImage, lifeX, startY, lifeWidth, lifeHeight);
        }
    }

    /**
     * Vẽ quả bóng. Nếu có sprite trong cache thì vẽ sprite; nếu không thì vẽ hình tròn fallback.
     *
     * @param ball thực thể bóng
     */
    @Override
    public void drawBall(Ball ball) {
        Image ballSprite = spriteCache.get("ball.png");
        if (ballSprite != null) {
            double renderX = ball.getX();
            double renderY = ball.getY() + PLAY_AREA_Y_OFFSET; // Dời xuống khu vực chơi
            spriteRenderer.drawSprite(ballSprite, renderX, renderY,
                    Constants.Physics.BALL_SIZE, Constants.Physics.BALL_SIZE);
        } else {
            gc.setFill(Color.WHITE);
            gc.fillOval(ball.getX(), ball.getY() + PLAY_AREA_Y_OFFSET,
                    ball.getBounds().getWidth(), ball.getBounds().getHeight());
        }
    }

    /**
     * Vẽ thanh đỡ. Ưu tiên vẽ animation nếu đang phát; nếu không, vẽ sprite tĩnh hoặc fallback hình chữ nhật.
     *
     * @param paddle thực thể thanh đỡ
     */
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
                    return; // Đã vẽ theo animation, kết thúc sớm
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

    /**
     * Vẽ một viên gạch theo loại:
     * <ul>
     *   <li>{@link SilverBrick}: có thể có animation nứt (crack)</li>
     *   <li>{@link NormalBrick}: chọn sprite theo {@code type}</li>
     *   <li>Khác: fallback hình chữ nhật màu</li>
     * </ul>
     *
     * @param brick thực thể gạch
     */
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
            String spriteName = "brick_" + normalBrick.getType().name().toLowerCase() + ".png"; // Đổi tên theo type
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

    /**
     * Vẽ power-up đang hoạt động (active) bằng frame hiện tại của animation.
     * <p>Chỉ vẽ khi power-up còn hiệu lực và animation đang phát.</p>
     *
     * @param powerUp thực thể power-up
     */
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
                double y = powerUp.getY() + PLAY_AREA_Y_OFFSET; // Dời xuống khu vực chơi
                double w = Constants.PowerUps.POWERUP_WIDTH;
                double h = Constants.PowerUps.POWERUP_HEIGHT;
                spriteRenderer.drawSprite(currentFrame, x, y, w, h);
            }
        }
    }

    /**
     * Vẽ một chuỗi văn bản tại vị trí (x, y) trong hệ tọa độ khu vực chơi.
     *
     * @param text nội dung văn bản
     * @param x    tọa độ X (trong canvas)
     * @param y    tọa độ Y (tính theo khu vực chơi, sẽ được cộng offset)
     */
    @Override
    public void drawText(String text, double x, double y) {
        gc.setFill(Constants.Colors.COLOR_TEXT_BODY);
        gc.setFont(scoreFont);
        gc.fillText(text, x, y + PLAY_AREA_Y_OFFSET); // Dịch xuống khu vực chơi
    }

    /**
     * Vẽ trực tiếp một {@link Image} tại tọa độ cho trước (không tự dịch offset).
     *
     * @param img ảnh
     * @param x   tọa độ X
     * @param y   tọa độ Y
     */
    @Override
    public void drawImage(Image img, double x, double y) {
        if (img != null) {
            gc.drawImage(img, x, y);
        }
    }

    /**
     * Vẽ sprite theo tên đã có trong {@link SpriteCache} tại vị trí (x, y).
     * <p>Nếu không tìm thấy sprite trong cache, không vẽ gì.</p>
     *
     * @param name tên sprite trong cache
     * @param x    tọa độ X
     * @param y    tọa độ Y
     */
    @Override
    public void drawSprite(String name, double x, double y) {
        Image sprite = spriteCache.get(name);
        if (sprite != null) {
            spriteRenderer.drawSprite(sprite, x, y);
        }
    }

    /**
     * Kết thúc khung hình hiện tại và “trình chiếu” lên màn hình.
     * <p>Hiện tại không cần xử lý vì JavaFX tự quản lý buffer; giữ lại để tương thích giao diện {@link Renderer}.</p>
     */
    @Override
    public void present() {
    }
}
