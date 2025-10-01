package Objects;

import GeometryPrimitives.Line;
import GeometryPrimitives.Point;
import GeometryPrimitives.Velocity;
import GeometryPrimitives.Rectangle;
import Render.Renderer;

/**
 * Biểu diễn quả bóng trong trò Arkanoid.
 *
 * Chi tiết:
 * - Lưu tâm hình học bằng bounding box (đa số sử dụng {@link GeometryPrimitives.Rectangle}).
 * - Sử dụng phương pháp "swept-line" (đường quỹ đạo tâm bóng theo 1 frame) để phát hiện va chạm
 *   với các cạnh của {@link GeometryPrimitives.Rectangle} (paddle, brick, tường).
 * - Khi va chạm xảy ra sẽ phản xạ vận tốc theo cạnh bị trúng và đẩy bóng ra một chút
 *   để tránh va chạm liên tiếp ngay lập tức.
 */
public class Ball extends MovableObject {
    private static final double EPSILON = 1e-6; // small tolerance for floating-point comparisons
    private double radius;

    // Kế thừa constructor từ MovableObject với vị trí (x,y) là góc trên bên trái của bounding box
    public Ball(double centerX, double centerY, double radius, Velocity initialVelocity) {
        super(centerX - radius, centerY - radius, radius * 2, radius * 2);
        this.radius = radius;
        this.velocity = initialVelocity;
    }

    /**
     * Lấy tọa độ tâm bóng.
     * @return {@link GeometryPrimitives.Point} - vị trí trung tâm bóng (pixel)
     */
    public Point getCenter() {
        return new Point(x + radius, y + radius);
    }

    /**
     * Đặt vị trí tâm bóng.
     * @param p tâm mới của bóng
     */
    public void setCenter(Point p) {
        this.x = p.getX() - radius;
        this.y = p.getY() - radius;
    }

    /** Cập nhật vị trí bóng theo vận tốc hiện tại (gọi move()). */
    @Override
    public void update() {
        move();
    }

    @Override
    public void render(Renderer renderer) {
        renderer.drawBall(this);
    }

    /**
     * Kiểm tra va chạm giữa quỹ đạo tâm bóng trong frame hiện tại và một hình chữ nhật.
     *
     * Thuật toán:
     * - Tạo {@link GeometryPrimitives.Line} từ tâm hiện tại đến tâm ở frame tiếp theo.
     * - Tìm giao điểm gần điểm bắt đầu nhất với các cạnh của {@link GeometryPrimitives.Rectangle}.
     * - Nếu có giao điểm, xác định cạnh bị trúng và phản xạ vận tốc tương ứng.
     * - Điều chỉnh vị trí tâm bóng để tránh va chạm lặp lại (đẩy ra một epsilon).
     *
     * @param rect vùng chữ nhật kiểm tra va chạm
     * @return true nếu có va chạm và trạng thái bóng đã được cập nhật; false nếu không có va chạm
     */
    public boolean checkCollisionWithRect(Rectangle rect) {
        Point center = getCenter();
        Point next = new Point(center.getX() + velocity.getDx(), center.getY() + velocity.getDy());
        Line traj = new Line(center, next);
        Point hit = traj.closestIntersectionToStartOfLine(rect);
        if (hit == null) return false;

        // Determine which side was hit by comparing hit to rectangle edges
        double ux = rect.getUpperLeft().getX();
        double uy = rect.getUpperLeft().getY();
        double w = rect.getWidth();
        double h = rect.getHeight();

        boolean hitTop = Math.abs(hit.getY() - uy) < EPSILON;
        boolean hitBottom = Math.abs(hit.getY() - (uy + h)) < EPSILON;
        boolean hitLeft = Math.abs(hit.getX() - ux) < EPSILON;
        boolean hitRight = Math.abs(hit.getX() - (ux + w)) < EPSILON;

        double dx = velocity.getDx();
        double dy = velocity.getDy();

        if (hitTop || hitBottom) {
            dy = -dy;
        }
        if (hitLeft || hitRight) {
            dx = -dx;
        }

        this.velocity = new Velocity(dx, dy);
        // push the ball slightly out of the collision to avoid immediate re-collision (epsilon)
        double push = 0.5; // pixels
        double newCenterX = hit.getX();
        double newCenterY = hit.getY();
        // adjust according to which side(s) were hit
        if (hitTop) newCenterY = uy - radius - push;
        if (hitBottom) newCenterY = uy + h + radius + push;
        if (hitLeft) newCenterX = ux - radius - push;
        if (hitRight) newCenterX = ux + w + radius + push;
        // corner case: if both axes were hit, push diagonally from the corner
        setCenter(new Point(newCenterX, newCenterY));
        return true;
    }
}
