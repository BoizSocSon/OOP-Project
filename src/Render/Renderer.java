package Render;

import Objects.GameEntities.Ball;
import Objects.Bricks.Brick;
import Objects.GameEntities.Paddle;
import Objects.PowerUps.PowerUp;
import javafx.scene.image.Image;

/**
 * Interface for rendering game objects to screen.
 *
 * Purpose:
 * - Separates rendering from game logic (e.g., JavaFX Canvas or other engines)
 * - Provides necessary methods for game objects to call when rendering
 * - Supports both sprite-based and primitive drawing
 */
public interface Renderer {
    /** Clear screen before redrawing */
    void clear();

    /** Draw game objects using their specific rendering logic */
    void drawBall(Ball b);
    void drawPaddle(Paddle p);
    void drawBrick(Brick b);
    void drawPowerUp(PowerUp p);
    
    /** Draw raw images (for animations, sprites, etc.) */
    void drawImage(Image image, double x, double y);
    
    /** Draw sprite by name (loads from sprite cache) */
    void drawSprite(String spriteName, double x, double y);
    
    /** Draw text at position */
    void drawText(String text, double x, double y);

    /** Finalize drawing commands for current frame */
    void present();
}
