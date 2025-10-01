package Render;

import Objects.Ball;
import Objects.Paddle;
import Objects.Brick;
import Objects.PowerUp;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Minimal renderer that draws simple shapes on a JavaFX Canvas.
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
    public void present() { /* Canvas draws immediately */ }

    @Override
    public void drawText(String text, double x, double y) {
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(18));
        gc.fillText(text, x, y);
    }
}
