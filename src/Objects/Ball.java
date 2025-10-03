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
    private double bounceCoefficient = 1.0; // 1.0 = fully elastic

    // Kế thừa constructor từ MovableObject với vị trí (x,y) là góc trên bên trái của bounding box
    public Ball(double centerX, double centerY, double radius, Velocity initialVelocity) {
        super(centerX - radius, centerY - radius, radius * 2, radius * 2);
        this.radius = radius;
        setVelocity(initialVelocity);
    }

    /**
     * Lấy tọa độ tâm bóng.
     * @return {@link GeometryPrimitives.Point} - vị trí trung tâm bóng (pixel)
     */
    public Point getCenter() {
        return new Point(getX() + radius, getY() + radius);
    }

    /**
     * Đặt vị trí tâm bóng.
     * @param p tâm mới của bóng
     */
    public void setCenter(Point p) {
        setX(p.getX() - radius);
        setY(p.getY() - radius);
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
     * Mô tả tổng quát (chi tiết toán học và lý do chọn phương pháp):
     *
     * Bối cảnh: quả bóng có bán kính r, di chuyển theo một vector vận tốc v = (dx, dy)
     * trong một frame. Chúng ta cần biết liệu trong khung thời gian này quả bóng có chạm
     * vào một hình chữ nhật (AABB) hay không, và nếu chạm thì tính phản xạ vận tốc và
     * đẩy bóng ra khỏi vùng va chạm để tránh chạm liên tiếp.
     *
     * Tại sao không chỉ kiểm tra bounding-box tĩnh?
     * - Nếu vận tốc lớn ("tunneling"), kiểm tra vị trí cuối frame tĩnh có thể bỏ qua va chạm
     *   (bỏ qua các va chạm xảy ra trong hành trình). Vì vậy cần kiểm tra quỹ đạo (swept)
     *   thay vì chỉ vị trí tĩnh.
     *
     * Ý tưởng chính (swept-circle vs AABB):
     * 1) Chuyển bài toán quả cầu (trong 2D: vòng tròn bán kính r) di chuyển đến một bài toán
     *    điểm di chuyển (tâm) bằng cách phóng to (mở rộng) hình chữ nhật ban đầu theo bán kính.
     *    Toán học: Minkowski sum giữa hình chữ nhật và vòng tròn bán kính r tương đương với
     *    mở rộng các cạnh của hình chữ nhật ra ngoài r đơn vị (inflated rectangle).
     *    - Lý do: khi một điểm (tâm bóng) chạm vào hình chữ nhật đã mở rộng thì tương đương
     *      với việc vòng tròn va chạm với hình chữ nhật ban đầu.
     *
     * 2) Xây dựng đường thẳng traj từ tâm hiện tại đến tâm dự tính ở frame tiếp theo:
     *    traj(t) = center + t * v, với t in [0,1].
     *    Tìm giao điểm gần điểm bắt đầu nhất giữa traj và biên của "inflated rectangle".
     *    - Hàm trợ giúp: closestIntersectionToStartOfLine(inflated) trả giao điểm gần nhất trên
     *      các cạnh của inflated rectangle (nếu có). Nếu không có => không va chạm trong frame này.
     *
     * 3) Từ giao điểm hit trên inflated rectangle, ta muốn biết bình thường của cạnh bị trúng
     *    để phản xạ vận tốc. Tuy nhiên hit nằm trên inflated rectangle (các cạnh đã được
     *    dịch ra r). Để tìm hướng pháp tuyến liên quan tới hình chữ nhật ban đầu, ta tính
     *    "closest point" trên hình chữ nhật ban đầu đến hit (phép clamp):
     *      closestX = clamp(hit.x, rx, rx+rw)
     *      closestY = clamp(hit.y, ry, ry+rh)
     *    - Nếu hit nằm ngoài góc (corner) của hình chữ nhật ban đầu, thì vector n = hit - closest
     *      sẽ chỉ về góc đó (n ≠ 0) và chính là vectơ pháp tuyến hướng từ cạnh/góc về phía tâm bóng
     *      tại thời điểm va chạm.
     *    - Nếu hit nằm chính xác trên một cạnh (không phải góc), thì n sẽ là vectơ song song với
     *      pháp tuyến cạnh (trong trường hợp lý tưởng sẽ có phương pháp tính trực tiếp):
     *      ví dụ với va chạm cạnh ngang, n sẽ có thành phần y ≠ 0 và x ≈ 0.
     *
     * 4) Trường hợp suy biến (len ~ 0):
     *    - Nếu vectơ n có độ dài rất nhỏ thì hit gần như nằm chính giữa một cạnh (hoặc do làm tròn).
     *    - Trong trường hợp này không thể xác định pháp tuyến một cách chắc chắn từ hit - ta dùng
     *      chiến lược dự phòng: phản xạ theo trục gần hơn (flip dx hoặc dy) dựa trên tỉ lệ vị trí
     *      của hit so với tâm hình chữ nhật. Đây là cách đơn giản và ổn định để tránh kết quả không xác định.
     *
     * 5) Phản xạ vận tốc (toán học):
     *    - Nếu n là vectơ pháp tuyến đơn vị (normalized), phản xạ phản chiếu v qua n theo công thức:
     *         v' = v - 2 * (v · n) * n
     *      Đây là công thức phản xạ chuẩn trong hình học vectơ (một chiều phản xạ trong mặt phẳng).
     *    - Ứng dụng: tính tích vô hướng v·n, rồi trừ 2*(v·n)*n khỏi v ban đầu.
     *    - Sau đó nhân với hệ số nẩy (bounceCoefficient) để mô phỏng mất mát năng lượng nếu cần.
     *
     * 6) Để tránh hiện tượng vẫn còn chồng lấn (circle vẫn nằm một phần trong rect do làm tròn),
     *    ta tính penetration = max(0, r - dist(hit, closestOnOriginalRect)).
     *    - Nếu hit ở góc, dist sẽ là khoảng cách từ hit tới góc (closest). Nếu dist < r => có
     *      chồng lấn và cần đẩy tâm bóng ra ngoài theo pháp tuyến n. Ta đẩy tâm tới
     *          closest + n * (r + small_epsilon)
     *      để đảm bảo khoảng cách giữa tâm và hình chữ nhật ít nhất bằng r (không chồng lấn),
     *      cộng thêm một epsilon nhỏ để tránh phát hiện va chạm lại ngay lập tức trong frame tiếp theo.
     *
     * 7) Tóm tắt các hằng số và biện pháp ổn định:
     *    - EPSILON: dung sai số học để tránh chia cho 0 hoặc so sánh số thực sát nhau.
     *    - eps/push nhỏ: khoảng dịch ra thêm để đảm bảo tách rời (anti-sticking).
     *    - Trong trường hợp suy biến, phản xạ theo trục (axis reflection) là một fallback đơn giản
     *      nhưng hiệu quả.
     *
     * Độ phức tạp và tính chính xác:
     * - Phương pháp này là một hợp lý giữa tính đơn giản và độ chính xác: nó xử lý được tunneling,
     *   góc (corner) va chạm và trả về vectơ phản xạ đúng về mặt hình học.
     * - Nếu cần mô phỏng vật lý chính xác hơn (ví dụ thời gian va chạm t ∈ (0,1) để đưa bóng chỉ
     *   tới thời điểm va chạm chính xác trước khi phản xạ), ta có thể mở rộng để tính t va chạm
     *   và cập nhật vị trí tại thời điểm đó, rồi tích hợp tiếp tục chuyển động trong phần thời gian còn lại.
     *
     * Ghi chú tham chiếu các bước trong mã:
     * - inflate rectangle: chuyển bài toán vòng tròn→điểm (Minkowski sum) nhằm tìm giao điểm với
     *   đường quỹ đạo tâm (traj).
     * - closestIntersectionToStartOfLine: tìm giao điểm gần nhất (nếu không có => return false).
     * - clamp để tìm điểm gần nhất trên rect gốc (closestX/closestY) và xác định vectơ pháp tuyến n = hit - closest.
     * - nếu |n| < EPSILON: fallback axis reflection.
     * - phản xạ bằng công thức v' = v - 2(v·n)n và áp hệ số bounceCoefficient.
     * - tính penetration và push-out để đặt lại tâm sao cho không còn chồng lấn.
     *
     * @param rect vùng chữ nhật kiểm tra va chạm (AABB)
     * @return true nếu có va chạm và trạng thái bóng (vận tốc/tọa độ) đã được cập nhật; false nếu không có va chạm
     */
    public boolean checkCollisionWithRect(Rectangle rect) {
        // Swept-circle vs AABB approach:
        // Inflate the rectangle by the ball radius (Minkowski sum) and treat the ball as a point.
        Point center = getCenter();
        Point next = new Point(center.getX() + getVelocity().getDx(), center.getY() + getVelocity().getDy());
        Line traj = new Line(center, next);

        double rx = rect.getUpperLeft().getX();
        double ry = rect.getUpperLeft().getY();
        double rw = rect.getWidth();
        double rh = rect.getHeight();

        // inflated rectangle
        Rectangle inflated = new Rectangle(new Point(rx - radius, ry - radius), rw + 2 * radius, rh + 2 * radius);
        Point hit = traj.closestIntersectionToStartOfLine(inflated);
        if (hit == null) return false;

        // Compute the closest point on the original rectangle to the hit point (clamp)
        double closestX = Math.max(rx, Math.min(hit.getX(), rx + rw));
        double closestY = Math.max(ry, Math.min(hit.getY(), ry + rh));

        double nx = hit.getX() - closestX; // normal vector (may be zero if hit is exactly on edge)
        double ny = hit.getY() - closestY;

        double dx = getVelocity().getDx();
        double dy = getVelocity().getDy();

        double len = Math.hypot(nx, ny);
        if (len < EPSILON) {
            // Degenerate: hit exactly aligned with edge center - fall back to axis reflection
            // Determine which axis is closer from hit to rectangle center
            double midX = rx + rw / 2.0;
            double midY = ry + rh / 2.0;
            double diffX = Math.abs(hit.getX() - midX) / (rw / 2.0);
            double diffY = Math.abs(hit.getY() - midY) / (rh / 2.0);
            if (diffX > diffY) {
                dx = -dx; // reflect horizontally
            } else {
                dy = -dy; // reflect vertically
            }
            setVelocity(new Velocity(dx, dy));
            // push out along axis
            double push = 0.5;
            double newCenterX = hit.getX();
            double newCenterY = hit.getY();
            if (Math.abs(hit.getX() - rx) < EPSILON) newCenterX = rx - radius - push;
            if (Math.abs(hit.getX() - (rx + rw)) < EPSILON) newCenterX = rx + rw + radius + push;
            if (Math.abs(hit.getY() - ry) < EPSILON) newCenterY = ry - radius - push;
            if (Math.abs(hit.getY() - (ry + rh)) < EPSILON) newCenterY = ry + rh + radius + push;
            setCenter(new Point(newCenterX, newCenterY));
            return true;
        }

        // Normalize normal
        nx /= len; ny /= len;

        // Reflect velocity about normal: v' = v - 2*(v·n)*n, then apply bounce coefficient
        double vdotn = dx * nx + dy * ny;
        double reflectedDx = dx - 2 * vdotn * nx;
        double reflectedDy = dy - 2 * vdotn * ny;
        setVelocity(new Velocity(reflectedDx * bounceCoefficient, reflectedDy * bounceCoefficient));

        // Compute penetration (how much the circle overlaps the rectangle)
        double distFromClosestToHit = Math.hypot(hit.getX() - closestX, hit.getY() - closestY);
        double penetration = Math.max(0.0, radius - distFromClosestToHit);
        double eps = 1e-3; // tiny extra offset to ensure separation
        double pushOut = penetration + eps;
        double newCenterX = closestX + nx * (radius + pushOut);
        double newCenterY = closestY + ny * (radius + pushOut);
        setCenter(new Point(newCenterX, newCenterY));
        return true;
    }

    public double getBounceCoefficient() {
        return bounceCoefficient;
    }

    public void setBounceCoefficient(double bounceCoefficient) {
        this.bounceCoefficient = bounceCoefficient;
    }
}
