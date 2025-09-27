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

public class Point {
    private static final double EPSILON = 1e-6;
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
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    /**
     * So sánh hai điểm theo toạ độ với sai số EPSILON để bù trừ cho lỗi làm tròn.
     *
     * Lưu ý: đây không phải là override của `Object.equals(Object)` mà là phương
     * thức đặc thù `equals(Point)`. Hàm trả về false nếu `other` là null.
     *
     * @param other điểm cần so sánh
     * @return true nếu hai toạ độ x và y tương đương trong biên EPSILON
     */
    public boolean equals(Point other) {
        if (other == null) {
            return false;
        }
        return Math.abs(this.x - other.x) < EPSILON && Math.abs(this.y - other.y) < EPSILON;
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
