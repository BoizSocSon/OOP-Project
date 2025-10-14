package Render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import Utils.AssetLoader;

/**
 * BorderRenderer - Renders game borders/edges using tiled sprites.
 * 
 * Responsibilities:
 * - Draw decorative borders around playfield
 * - Tile edge sprites horizontally (top) and vertically (sides)
 * - Handle corners properly (overlapping)
 * - Responsive to window size changes
 * 
 * Border layout:
 * ┌─────────────┐
 * │             │  ← Top edge: edge_top.png tiled horizontally
 * │             │  ← Left edge: edge_left.png tiled vertically
 * │  PLAYFIELD  │
 * │             │  ← Right edge: edge_right.png tiled vertically
 * └─────────────┘  ← Bottom: No edge (ball falls off)
 * 
 * Sprite assets:
 * - edge_top.png: 32x16px (repeats horizontally)
 * - edge_left.png: 16x32px (repeats vertically)
 * - edge_right.png: 16x32px (repeats vertically)
 * 
 * Design philosophy:
 * - Decorative only (not collision objects)
 * - Classic arcade aesthetic
 * - Consistent with Arkanoid visual style
 * 
 * @author SteveHoang aka BoizSocSon
 */
public class BorderRenderer {
    
    private final Image edgeTop;
    private final Image edgeLeft;
    private final Image edgeRight;
    
    private final double canvasWidth;
    private final double canvasHeight;
    
    /**
     * Creates a BorderRenderer with edge sprites and canvas dimensions.
     * 
     * Constructor actions:
     * 1. Load edge sprite assets from Resources/Graphics/
     * 2. Store canvas dimensions for tiling calculations
     * 3. Validate sprites loaded successfully
     * 
     * Expected sprite locations:
     * - Resources/Graphics/edge_top.png
     * - Resources/Graphics/edge_left.png
     * - Resources/Graphics/edge_right.png
     * 
     * Fallback behavior:
     * - If sprites missing: Log warning, continue without borders
     * - Game still playable (borders are decorative)
     * 
     * @param canvasWidth Width of the game canvas
     * @param canvasHeight Height of the game canvas
     */
    public BorderRenderer(double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        
        // Load edge sprites using static methods
        this.edgeTop = AssetLoader.loadImage("edge_top.png");
        this.edgeLeft = AssetLoader.loadImage("edge_left.png");
        this.edgeRight = AssetLoader.loadImage("edge_right.png");
        
        // Validate sprites loaded
        if (edgeTop == null || edgeLeft == null || edgeRight == null) {
            System.err.println("BorderRenderer: Warning - Some edge sprites failed to load");
            System.err.println("  edge_top: " + (edgeTop != null ? "OK" : "MISSING"));
            System.err.println("  edge_left: " + (edgeLeft != null ? "OK" : "MISSING"));
            System.err.println("  edge_right: " + (edgeRight != null ? "OK" : "MISSING"));
        }
    }
    
    /**
     * Renders all borders (top, left, right) onto the canvas.
     * 
     * Rendering order:
     * 1. Top edge (horizontal tiling)
     * 2. Left edge (vertical tiling)
     * 3. Right edge (vertical tiling)
     * 
     * Tiling algorithm:
     * - Calculate number of tiles needed (canvasSize / tileSize)
     * - Draw each tile at offset position
     * - Handle partial tiles at edges (crop if needed)
     * 
     * Performance:
     * - Called once per frame
     * - Minimal overhead (just image draws)
     * - No transparency blending needed
     * 
     * @param gc The graphics context to draw on
     */
    public void render(GraphicsContext gc) {
        if (gc == null) {
            System.err.println("BorderRenderer.render: GraphicsContext is null");
            return;
        }
        
        renderTopEdge(gc);
        renderLeftEdge(gc);
        renderRightEdge(gc);
    }
    
    /**
     * Renders the top edge by tiling horizontally.
     * 
     * Algorithm:
     * 1. Get tile width (edge_top.width)
     * 2. Calculate number of tiles: ceil(canvasWidth / tileWidth)
     * 3. For each tile:
     *    - x = tileIndex * tileWidth
     *    - y = 0 (top of canvas)
     *    - Draw tile
     * 4. Handle overflow: Crop last tile if needed
     * 
     * Layout:
     * [Tile][Tile][Tile][Tile][Tile][Partial]
     *  ↑                                  ↑
     *  x=0                         x=canvasWidth
     * 
     * @param gc The graphics context
     */
    private void renderTopEdge(GraphicsContext gc) {
        if (edgeTop == null) {
            return;
        }
        
        double tileWidth = edgeTop.getWidth();
        double tileHeight = edgeTop.getHeight();
        int numTiles = (int) Math.ceil(canvasWidth / tileWidth);
        
        for (int i = 0; i < numTiles; i++) {
            double x = i * tileWidth;
            double y = 0;
            
            // Check if we need to crop the last tile
            if (x + tileWidth > canvasWidth) {
                // Crop to fit remaining space
                double remainingWidth = canvasWidth - x;
                gc.drawImage(edgeTop, 
                           0, 0, remainingWidth, tileHeight,  // Source rect (cropped)
                           x, y, remainingWidth, tileHeight); // Dest rect
            } else {
                // Draw full tile
                gc.drawImage(edgeTop, x, y);
            }
        }
    }
    
    /**
     * Renders the left edge by tiling vertically.
     * 
     * Algorithm:
     * 1. Get tile height (edge_left.height)
     * 2. Calculate number of tiles: ceil(canvasHeight / tileHeight)
     * 3. For each tile:
     *    - x = 0 (left of canvas)
     *    - y = tileIndex * tileHeight
     *    - Draw tile
     * 4. Handle overflow: Crop last tile if needed
     * 
     * Layout:
     * [Tile]
     * [Tile]
     * [Tile]
     * [Tile]
     * [Partial]
     * 
     * @param gc The graphics context
     */
    private void renderLeftEdge(GraphicsContext gc) {
        if (edgeLeft == null) {
            return;
        }
        
        double tileWidth = edgeLeft.getWidth();
        double tileHeight = edgeLeft.getHeight();
        int numTiles = (int) Math.ceil(canvasHeight / tileHeight);
        
        for (int i = 0; i < numTiles; i++) {
            double x = 0;
            double y = i * tileHeight;
            
            // Check if we need to crop the last tile
            if (y + tileHeight > canvasHeight) {
                // Crop to fit remaining space
                double remainingHeight = canvasHeight - y;
                gc.drawImage(edgeLeft,
                           0, 0, tileWidth, remainingHeight,  // Source rect (cropped)
                           x, y, tileWidth, remainingHeight); // Dest rect
            } else {
                // Draw full tile
                gc.drawImage(edgeLeft, x, y);
            }
        }
    }
    
    /**
     * Renders the right edge by tiling vertically.
     * 
     * Algorithm:
     * 1. Get tile height (edge_right.height)
     * 2. Calculate number of tiles: ceil(canvasHeight / tileHeight)
     * 3. For each tile:
     *    - x = canvasWidth - tileWidth (right side)
     *    - y = tileIndex * tileHeight
     *    - Draw tile
     * 4. Handle overflow: Crop last tile if needed
     * 
     * Layout:
     *           [Tile]
     *           [Tile]
     *           [Tile]
     *           [Tile]
     *       [Partial]
     * 
     * @param gc The graphics context
     */
    private void renderRightEdge(GraphicsContext gc) {
        if (edgeRight == null) {
            return;
        }
        
        double tileWidth = edgeRight.getWidth();
        double tileHeight = edgeRight.getHeight();
        int numTiles = (int) Math.ceil(canvasHeight / tileHeight);
        
        for (int i = 0; i < numTiles; i++) {
            double x = canvasWidth - tileWidth;
            double y = i * tileHeight;
            
            // Check if we need to crop the last tile
            if (y + tileHeight > canvasHeight) {
                // Crop to fit remaining space
                double remainingHeight = canvasHeight - y;
                gc.drawImage(edgeRight,
                           0, 0, tileWidth, remainingHeight,  // Source rect (cropped)
                           x, y, tileWidth, remainingHeight); // Dest rect
            } else {
                // Draw full tile
                gc.drawImage(edgeRight, x, y);
            }
        }
    }
    
    /**
     * Gets the width of the left edge sprite.
     * Useful for calculating playfield offset.
     * 
     * @return Width in pixels, or 0 if sprite not loaded
     */
    public double getLeftEdgeWidth() {
        return edgeLeft != null ? edgeLeft.getWidth() : 0;
    }
    
    /**
     * Gets the width of the right edge sprite.
     * Useful for calculating playfield bounds.
     * 
     * @return Width in pixels, or 0 if sprite not loaded
     */
    public double getRightEdgeWidth() {
        return edgeRight != null ? edgeRight.getWidth() : 0;
    }
    
    /**
     * Gets the height of the top edge sprite.
     * Useful for calculating playfield offset.
     * 
     * @return Height in pixels, or 0 if sprite not loaded
     */
    public double getTopEdgeHeight() {
        return edgeTop != null ? edgeTop.getHeight() : 0;
    }
}
