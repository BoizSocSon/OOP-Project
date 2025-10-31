/**
 * Lớp Point biểu diễn một điểm trong hệ toạ độ 2D với toạ độ thực (double).
 *
 * Các phương thức chính:
 * - distance(Point): tính khoảng cách Euclid giữa hai điểm.
 * - equals(Point): so sánh hai điểm theo toạ độ với một sai số nhỏ (EPSILON) để
 *   tránh lỗi làm tròn của số thực.
 * - getX(), getY(): trả về toạ độ từng trục.
 *
 * Ghi chú về thiết kế:
 * - Lớp cung cấp phương thức `equals(Point)` để so sánh theo giá trị tọa độ với EPSILON,
 *   nhưng không override `equals(Object)` của `Object`. Do đó, khi muốn so sánh
 *   giá trị logic giữa hai đối tượng `Point` trong danh sách hoặc tập hợp, cần gọi
 *   trực tiếp `p1.equals(p2)` (kiểu an toàn) thay vì `List.contains` mặc định.
 *
 * Toán học:
 * - Khoảng cách Euclid giữa P1(x1,y1) và P2(x2,y2) được tính bằng
 *   sqrt((x1-x2)^2 + (y1-y2)^2).
 * - So sánh toạ độ sử dụng EPSILON = 1e-6: hai toạ độ được coi là bằng nhau nếu
 *   |a - b| < EPSILON.
 *
 * @author SteveHoang aka BoizSocSon
 * Student ID: 23020845
 */
package GeometryPrimitives;

import Utils.Constants;

public class Point {
    private double x;
    private double y;

    /**
     * Khởi tạo một điểm với toạ độ (x, y).
     *
     * @param x toạ độ x
     * @param y toạ độ y
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Tính khoảng cách Euclid từ điểm hiện tại đến `other`.
     *
     * Công thức: sqrt((x1 - x2)^2 + (y1 - y2)^2).
     *
     * @param other điểm đích
     * @return khoảng cách (double, >= 0)
     */
    public double distance(Point other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Point)) {
            return false;
        }

        Point other = (Point) obj;

        return Math.abs(this.x - other.x) < Constants.General.EPSILON
                && Math.abs(this.y - other.y) < Constants.General.EPSILON;
    }

    /**
     * Trả về mã băm của điểm, sử dụng toạ độ x và y đã được làm tròn theo EPSILON.
     * Cho phép hai điểm gần nhau trong biên EPSILON có cùng mã băm.
     */
    @Override
    public int hashCode() {
        int hx = Double.hashCode(Math.round(x/Constants.General.EPSILON));
        int hy = Double.hashCode(Math.round(y/Constants.General.EPSILON));
        return 31 * hx + hy;
    }

    /**
     * Trả về toạ độ x của điểm.
     *
     * @return giá trị x
     */
    public double getX() {
        return x;
    }

    /**
     * Trả về toạ độ y của điểm.
     *
     * @return giá trị y
     */
    public double getY() {
        return y;
    }
}
