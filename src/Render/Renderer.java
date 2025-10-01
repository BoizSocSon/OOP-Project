package Render;

import Objects.Ball;
import Objects.Paddle;
import Objects.Brick;
import Objects.PowerUp;

/**
 * Renderer is an abstraction used by game objects to draw themselves.
 * Implementations may use JavaFX, Swing or a software renderer. The Game
 * code depends only on this interface so rendering can be swapped easily.
 */
public interface Renderer {
    void clear();
    void drawBall(Ball b);
    void drawPaddle(Paddle p);
    void drawBrick(Brick b);
    void drawPowerUp(PowerUp p);
    void drawText(String text, double x, double y);
    void present();
}
