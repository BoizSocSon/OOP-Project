package Objects;

import Render.Renderer;
import GeometryPrimitives.Rectangle;

    /**
     * GameObject is the base interface for every object that participates in the game world.
     * Implementations represent entities with a position and bounding box and provide
     * lifecycle methods used by the game loop.
     *
     * Contract:
     * - update() is called once per frame to progress object state.
     * - render(Renderer) draws the object via the provided renderer abstraction.
     * - getBounds() returns the object's bounding {@link GeometryPrimitives.Rectangle} used
     *   for collision checks.
     * - isAlive()/destroy() manage simple object lifetime.
     */
public interface GameObject {
        /** Called once per frame to update object logic. */
        void update();

        /** Draw the object using the provided renderer implementation. */
        void render(Renderer renderer);

        /** Return the axis-aligned bounding rectangle for collisions and rendering. */
        Rectangle getBounds();

        /** Return true if the object is currently active/alive in the world. */
        boolean isAlive();

        /** Mark the object as destroyed / inactive. */
        void destroy();
}
