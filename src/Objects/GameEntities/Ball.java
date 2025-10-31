package Objects.GameEntities;

import GeometryPrimitives.Line;
import GeometryPrimitives.Point;
import GeometryPrimitives.Velocity;
import GeometryPrimitives.Rectangle;
import Objects.Core.MovableObject;
import Utils.Constants;

/**
 * Represents a Ball object in the game, capable of movement and collision handling.
 * The Ball is defined by its center position, radius, and velocity.
 * It inherits basic movement capabilities from MovableObject.
 */
public class Ball extends MovableObject{
    private double radius;
    /** The coefficient of restitution (đàn hồi). 1.0 means perfectly elastic collision. */
    private double bounceCoefficient = 1.0;
    /** Flag indicating if the ball is currently attached (e.g., to a paddle). */
    private boolean isAttached = false;

    // Kế thừa constructor từ MovableObject với vị trí (x,y) là góc trên bên trái của bounding box
    /**
     * Constructs a Ball object.
     * The bounding box is set up so that its upper-left corner is (centerX - radius, centerY - radius).
     *
     * @param centerX The x-coordinate of the ball's center.
     * @param centerY The y-coordinate of the ball's center.
     * @param radius The radius of the ball.
     * @param initialVelocity The initial velocity of the ball.
     */
    public Ball(double centerX, double centerY, double radius, Velocity initialVelocity) {
        // Gọi constructor của MovableObject: (x, y, width, height)
        // Bounding box của bóng tròn có góc trên bên trái là (centerX - radius, centerY - radius)
        super(centerX - radius, centerY - radius, radius * 2, radius * 2);
        this.radius = radius;
        setVelocity(initialVelocity);
    }

    /**
     * Gets the center point of the ball.
     *
     * @return The center Point of the ball.
     */
    public Point getCenter() {
        // Center = (x + radius, y + radius). x, y là góc trên bên trái của bounding box
        return new Point(getX() + radius, getY() + radius);
    }

    /**
     * Sets the center point of the ball, updating the position of its bounding box.
     *
     * @param p The new center Point.
     */
    public void setCenter(Point p) {
        // Cập nhật góc trên bên trái của bounding box: x = centerX - radius, y = centerY - radius
        setX(p.getX() - radius);
        setY(p.getY() - radius);
    }

    /**
     * Updates the ball's position based on its current velocity (calls the inherited move() method).
     * This should be called in the game's main loop.
     */
    @Override
    public void update() {
        // Kế thừa move() từ MovableObject: x += dx, y += dy
        move();
    }

    /**
     * Checks for and handles the collision between the ball and an axis-aligned rectangle (paddle, brick, wall).
     * Uses a swept-circle vs AABB (axis-aligned bounding box) approach, equivalent to
     * a point vs an inflated rectangle (Minkowski Sum).
     *
     * @param rect The rectangle (AABB) to check collision against.
     * @return {@code true} if a collision occurred and the reflection was handled, {@code false} otherwise.
     */
    public boolean checkCollisionWithRect(Rectangle rect) {
        // Cách tiếp cận: swept-circle so với hình chữ nhật căn theo trục (AABB - axis-aligned bounding box):
        // Mở rộng (inflate) hình chữ nhật theo bán kính bóng (tương tự phép Minkowski sum) và coi bóng như một điểm

        Point center = getCenter();
        // Vị trí tâm bóng sau khi di chuyển 1 frame (dùng để tạo đường đi)
        Point next = new Point(center.getX() + getVelocity().getDx(), center.getY() + getVelocity().getDy());
        // Đường đi (quỹ đạo) của tâm bóng trong frame này
        Line traj = new Line(center, next);

        // Lấy thông tin hình chữ nhật
        double rx = rect.getUpperLeft().getX();
        double ry = rect.getUpperLeft().getY();
        double rw = rect.getWidth();
        double rh = rect.getHeight();

        // inflated rectangle: mở rộng rect theo bán kính bóng
        Rectangle inflated = new Rectangle(new Point(rx - radius, ry - radius), rw + 2 * radius, rh + 2 * radius);

        // Tìm điểm va chạm đầu tiên của đường đi tâm bóng với inflated rectangle
        Point hit = traj.closestIntersectionToStartOfLine(inflated);
        if (hit == null) return false; // Không có va chạm trong đường đi này

        // --- Va chạm đã xảy ra ---

        // Compute the closest point on the original rectangle to the hit point (clamp)
        // Điểm P_closest (closestX, closestY) là điểm trên biên/bên trong original rect gần nhất với điểm hit (tâm bóng tại thời điểm va chạm)
        double closestX = Math.max(rx, Math.min(hit.getX(), rx + rw));
        double closestY = Math.max(ry, Math.min(hit.getY(), ry + rh));

        // Vector pháp tuyến (normal vector) **hướng ra ngoài** hình chữ nhật tại điểm va chạm
        // nx, ny là thành phần của vector từ P_closest đến P_hit (tâm bóng)
        double nx = hit.getX() - closestX;
        double ny = hit.getY() - closestY;

        double dx = getVelocity().getDx();
        double dy = getVelocity().getDy();

        double len = Math.hypot(nx, ny);
        if (len < Constants.General.EPSILON) {
            // Trường hợp suy biến (Degenerate case): va chạm trùng tâm cạnh/góc -> phản xạ theo trục (fallback)
            // Điều này xảy ra khi tâm bóng (hit) nằm **chính xác** trên biên của inflated rect,
            // và điểm gần nhất P_closest nằm **chính xác** trên biên của original rect,
            // dẫn đến vector pháp tuyến (hit - closest) có độ dài gần bằng 0.

            // Xác định trục nào gần hơn từ điểm va chạm đến tâm hình chữ nhật để chọn phản xạ
            double midX = rx + rw / 2.0;
            double midY = ry + rh / 2.0;
            // Tỷ lệ khoảng cách từ điểm va chạm đến tâm theo từng trục (chuẩn hóa theo nửa chiều rộng/cao)
            double diffX = Math.abs(hit.getX() - midX) / (rw / 2.0);
            double diffY = Math.abs(hit.getY() - midY) / (rh / 2.0);

            if (diffX > diffY) {
                dx = -dx; // Phản xạ theo phương ngang (ngang hơn/gần cạnh đứng)
            } else {
                dy = -dy; // Phản xạ theo phương dọc (dọc hơn/gần cạnh ngang)
            }
            setVelocity(new Velocity(dx, dy));

            // Đẩy bóng ra ngoài theo trục đã chọn để tránh dính chùm
            double push = 0.5; // Khoảng cách đẩy nhỏ
            double newCenterX = hit.getX();
            double newCenterY = hit.getY();

            // Nếu va chạm gần cạnh trái (hit.x ~ rx - radius) -> đẩy sang trái
            if (Math.abs(hit.getX() - (rx - radius)) < Constants.General.EPSILON) newCenterX = rx - radius - push;
                // Nếu va chạm gần cạnh phải (hit.x ~ rx + rw + radius) -> đẩy sang phải
            else if (Math.abs(hit.getX() - (rx + rw + radius)) < Constants.General.EPSILON) newCenterX = rx + rw + radius + push;
                // Nếu va chạm gần cạnh trên (hit.y ~ ry - radius) -> đẩy lên trên
            else if (Math.abs(hit.getY() - (ry - radius)) < Constants.General.EPSILON) newCenterY = ry - radius - push;
                // Nếu va chạm gần cạnh dưới (hit.y ~ ry + rh + radius) -> đẩy xuống dưới
            else if (Math.abs(hit.getY() - (ry + rh + radius)) < Constants.General.EPSILON) newCenterY = ry + rh + radius + push;

            setCenter(new Point(newCenterX, newCenterY));
            return true;
        }

        // --- Trường hợp va chạm bình thường (Normal collision) ---

        // Chuẩn hoá vector pháp tuyến (độ dài = 1)
        nx /= len; ny /= len;

        // Phản xạ vector vận tốc theo pháp tuyến: v' = v - 2*(v·n)*n
        double vdotn = dx * nx + dy * ny; // Chiếu vector vận tốc lên pháp tuyến
        double reflectedDx = dx - 2 * vdotn * nx;
        double reflectedDy = dy - 2 * vdotn * ny;

        // Áp dụng hệ số đàn hồi (bounceCoefficient)
        setVelocity(new Velocity(reflectedDx * bounceCoefficient, reflectedDy * bounceCoefficient));

        // Giải quyết chồng lấn (Penetration Resolution): đẩy bóng ra khỏi hình chữ nhật

        // Khoảng cách từ điểm P_closest (trên rect) đến điểm hit (tâm bóng)
        double distFromClosestToHit = Math.hypot(hit.getX() - closestX, hit.getY() - closestY);

        // Độ chồng lấn (penetration): khoảng cách bóng đã lấn vào rect (luôn >= 0)
        double penetration = Math.max(0.0, radius - distFromClosestToHit);

        // Đẩy bóng ra khỏi rect bằng penetration + một lượng nhỏ (eps) để đảm bảo không dính lại
        double eps = 1e-3; // tiny extra offset to ensure separation
        double pushOut = penetration + eps;

        // Vị trí tâm mới: P_closest + n * (radius + pushOut)
        // Đặt tâm bóng ở vị trí mới, đảm bảo nó nằm chính xác ngoài biên của rect
        double newCenterX = closestX + nx * (radius + pushOut);
        double newCenterY = closestY + ny * (radius + pushOut);
        setCenter(new Point(newCenterX, newCenterY));

        return true;
    }

    /**
     * Sets the attached state of the ball.
     *
     * @param attached {@code true} to attach the ball, {@code false} otherwise.
     */
    public void setAttached(boolean attached) {
        isAttached = attached;
    }

    /**
     * Checks if the ball is currently attached (e.g., to a paddle).
     *
     * @return {@code true} if the ball is attached, {@code false} otherwise.
     */
    public boolean isAttached() {
        return isAttached;
    }
}