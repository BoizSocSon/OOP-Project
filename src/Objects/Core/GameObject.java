package Objects.Core;

import GeometryPrimitives.Rectangle;

public interface GameObject {
    void update();

    Rectangle getBounds();

    boolean isAlive();

    void destroy();
}
