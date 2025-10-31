package Render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import Utils.SpriteProvider;
import Utils.Constants;

public class BorderRenderer {
    private final SpriteProvider sprites;
    private final GraphicsContext gc;

    public BorderRenderer(GraphicsContext gc, SpriteProvider sprites) {
        this.gc = gc;
        this.sprites = sprites;
    }

    public void render() {
        drawTopEdge(gc);
        drawLeftEdge(gc);
        drawRightEdge(gc);
    }

    private void drawTopEdge(GraphicsContext gc) {
        Image edgeTop = sprites.get("edge_top.png");
        if (edgeTop == null) {
            return;
        }

        double startX = Constants.Window.WINDOW_SIDE_OFFSET;
        double srartY = Constants.Window.WINDOW_TOP_OFFSET;

        gc.drawImage(edgeTop, startX, srartY);
    }

    private void drawLeftEdge(GraphicsContext gc) {
        Image edgeLeft = sprites.get("edge_left.png");
        if (edgeLeft == null) {
            return;
        }

        double startX = 0;
        double startY = Constants.Window.WINDOW_TOP_OFFSET;

        gc.drawImage(edgeLeft, startX, startY);
    }

    private void drawRightEdge(GraphicsContext gc) {
        Image edgeRight = sprites.get("edge_right.png");
        if (edgeRight == null) {
            return;
        }

        double startX = Constants.Window.WINDOW_WIDTH - Constants.Window.WINDOW_SIDE_OFFSET;
        double startY = Constants.Window.WINDOW_TOP_OFFSET;

        gc.drawImage(edgeRight, startX, startY);
    }
}
