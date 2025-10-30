/**
 * Lớp Velocity mô tả một vận tốc/độ dịch chuyển trong mặt phẳng 2D.
 *
 * Hai thành phần chính:
 * - dx: dịch chuyển theo trục X cho một bước thời gian.
 * - dy: dịch chuyển theo trục Y cho một bước thời gian.
 *
 * Các phương thức:
 * - fromAngleAndSpeed(angle, speed): chuyển từ biểu diễn cực (góc, độ lớn)
 *   sang thành phần Cartesian (dx, dy). Lưu ý: góc được cho theo độ.
 * - applyToPoint(point): trả về một `Point` mới bằng cách cộng dx, dy vào toạ độ
 *   của `point` (point + velocity).
 *
 * Giải thích toán học cho fromAngleAndSpeed:
 * - Nếu dùng hệ trục toạ độ Cartesian chuẩn (x ngang sang phải, y lên trên),
 *   ta có:
 *     dx = speed * cos(theta)
 *     dy = speed * sin(theta)
 *   với theta là góc theo radian tính từ trục x dương.
 *
 * - Trong nhiều ứng dụng đồ họa (như trò chơi Arkanoid), trục Y tăng xuống dưới màn
 *   hình (screen coordinates). Trong lớp này, tác giả chọn dùng quy ước góc 0 độ
 *   tương ứng hướng lên (negative Y) và tính:
 *     dx = speed * sin(angle)
 *     dy = -speed * cos(angle)
 *   (angle được chuyển sang radian bằng Math.toRadians(angle)).
 *
 *   Quy ước này có thể tùy chỉnh tuỳ theo cách bạn định nghĩa góc ban đầu trong trò chơi.
 */
package GeometryPrimitives;

public class Velocity {
    private double dx;
    private double dy;

    /**
     * Tạo một Velocity với thành phần dx, dy.
     *
     * @param dx dịch chuyển theo trục X
     * @param dy dịch chuyển theo trục Y
     */
    public Velocity(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Áp dụng velocity lên một điểm: trả về một điểm mới có toạ độ
     * (point.x + dx, point.y + dy).
     *
     * @param point điểm ban đầu
     * @return điểm mới sau khi dịch chuyển
     */
    public Point applyToPoint(Point point) {
        double newX = point.getX() + this.dx;
        double newY = point.getY() + this.dy;
        return new Point(newX, newY);
    }

    /**
     * Trả về thành phần dx.
     * @return dx
     */
    public double getDx() {
        return dx;
    }

    /**
     * Trả về thành phần dy.
     * @return dy
     */
    public double getDy() {
        return dy;
    }
}
