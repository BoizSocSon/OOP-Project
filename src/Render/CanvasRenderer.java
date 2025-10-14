package Render;

import Objects.GameEntities.Ball;
import Objects.Bricks.Brick;
import Objects.GameEntities.Paddle;
import Objects.PowerUps.PowerUp;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Triển khai {@link Renderer} sử dụng JavaFX {@link javafx.scene.canvas.Canvas}.
 *
 * Mỗi phương thức vẽ sử dụng các hình cơ bản (fillRect, fillOval) và màu sắc cố định
 * để hiển thị các đối tượng trò chơi. Thiết kế này giữ phần render đơn giản và dễ đọc.
 */
public class CanvasRenderer implements Renderer {
    private final Canvas canvas;
    private final GraphicsContext gc;

    public CanvasRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    @Override
    public void clear() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
    }

    @Override
    public void drawBall(Ball b) {
        gc.setFill(Color.WHITE);
        double r = b.getBounds().getWidth() / 2.0;
        gc.fillOval(b.getBounds().getUpperLeft().getX(), b.getBounds().getUpperLeft().getY(), r*2, r*2);
    }

    @Override
    public void drawPaddle(Paddle p) {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(p.getBounds().getUpperLeft().getX(), p.getBounds().getUpperLeft().getY(), p.getBounds().getWidth(), p.getBounds().getHeight());
    }

    @Override
    public void drawBrick(Brick b) {
        gc.setFill(Color.DARKRED);
        gc.fillRect(b.getBounds().getUpperLeft().getX(), b.getBounds().getUpperLeft().getY(), b.getBounds().getWidth(), b.getBounds().getHeight());
    }

    @Override
    public void drawPowerUp(PowerUp p) {
        gc.setFill(Color.GOLD);
        gc.fillRect(p.getBounds().getUpperLeft().getX(), p.getBounds().getUpperLeft().getY(), p.getBounds().getWidth(), p.getBounds().getHeight());
    }

    @Override
    public void present() { /* Canvas vẽ ngay lập tức nên không cần hành động thêm */ }

    @Override
    public void drawText(String text, double x, double y) {
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(18));
        gc.fillText(text, x, y);
    }
    
    @Override
    public void drawImage(javafx.scene.image.Image image, double x, double y) {
        if (image != null) {
            gc.drawImage(image, x, y);
        }
    }
    
    @Override
    public void drawSprite(String spriteName, double x, double y) {
        javafx.scene.image.Image sprite = Utils.SpriteCache.getInstance().get(spriteName);
        if (sprite != null) {
            gc.drawImage(sprite, x, y);
        }
    }
}
