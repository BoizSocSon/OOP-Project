/**
 * Lớp Rectangle biểu diễn một hình chữ nhật trong hệ toạ độ 2D.
 *
 * Mỗi Rectangle được xác định bởi điểm góc trên bên trái (`upperLeft`),
 * chiều rộng (`width`) và chiều cao (`height`).
 *
 * Lớp cung cấp các phương thức để tính toán giao điểm giữa một đường thẳng
 * (Line) và các cạnh của hình chữ nhật, cùng các getter cơ bản và phương thức
 * so sánh (equals) sử dụng sai số nhỏ để so sánh số thực.
 *
 * Ghi chú: các điểm trả về bởi `intersectionPoints` sử dụng kiểu `Point`
 * định nghĩa trong cùng package.
 *
 * @author SteveHoang aka BoizSocSon
 * Student ID: 23020845
 */
package GeometryPrimitives;

import java.util.ArrayList;
import java.util.List;

public class Rectangle {
    private static final double EPSILON = 1e-6;
    private Point upperLeft;
    private double width;
    private double height;

    public Rectangle(Point upperLeft, double width, double height) {
        this.upperLeft = upperLeft;
        this.width = width;
        this.height = height;
    }

    /**
     * Tính và trả về danh sách các điểm giao giữa đoạn thẳng `line` và
     * các cạnh của hình chữ nhật này.
     *
     * Quy ước:
     * - Nếu không có giao điểm thì trả về danh sách rỗng.
     * - Nếu đường thẳng cắt tại một đỉnh thì điểm đỉnh đó sẽ xuất hiện một lần.
     * - Nếu có nhiều điểm trùng nhau (do tính toán số thực) phương thức đảm bảo
     *   loại bỏ các điểm trùng bằng so sánh theo `Point.equals(Point)` của package.
     *
     * @param line đoạn thẳng cần kiểm tra giao với các cạnh của hình chữ nhật
     * @return danh sách các điểm giao (có thể rỗng)
     */
    public List<Point> intersectionPoints(Line line) {
        List<Point> intersections = new ArrayList<>();

        // Precompute corner points (fewer repeated calls to getters)
        final double x = this.upperLeft.getX();
        final double y = this.upperLeft.getY();
        final Point topLeft = this.upperLeft;
        final Point topRight = new Point(x + this.width, y);
        final Point bottomLeft = new Point(x, y + this.height);
        final Point bottomRight = new Point(x + this.width, y + this.height);

        Line[] sides = new Line[] {
                new Line(topLeft, topRight),    // top
                new Line(bottomLeft, bottomRight), // bottom
                new Line(topLeft, bottomLeft),  // left
                new Line(topRight, bottomRight) // right
        };

        for (Line side : sides) {
            Point intersection = line.intersectionWith(side);
            if (intersection == null) {
                continue;
            }
            // Sử dụng helper để tránh thêm điểm trùng do Point không override equals(Object)
            if (!pointListContains(intersections, intersection)) {
                intersections.add(intersection);
            }
        }

        return intersections;
    }

    /**
     * Kiểm tra danh sách `list` đã chứa một điểm có toạ độ tương đương với `p`
     * chưa bằng cách gọi `Point.equals(Point)` của kiểu Point.
     *
     * Lý do: lớp `Point` định nghĩa phương thức equals(Point) với EPSILON, nhưng không
     * override `equals(Object)`, nên không thể dùng `List.contains` mặc định để so sánh
     * bằng giá trị toán học. Helper này đảm bảo so sánh đúng bằng toạ độ.
     *
     * @param list danh sách điểm cần kiểm tra
     * @param p điểm cần tìm trong danh sách
     * @return true nếu danh sách đã chứa một điểm tương đương, false otherwise
     */
    private static boolean pointListContains(List<Point> list, Point p) {
        if (p == null) {
            return false;
        }
        for (Point pt : list) {
            if (pt != null && pt.equals(p)) {
                return true;
            }
        }
        return false;
    }

    public double getWidth() {
        // Trả về chiều rộng của hình chữ nhật
        return this.width;
    }

    public double getHeight() {
        // Trả về chiều cao của hình chữ nhật
        return this.height;
    }

    public Point getUpperLeft() {
        // Trả về toạ độ góc trên bên trái của hình chữ nhật
        return this.upperLeft;
    }

    public boolean equals(Rectangle rectangle) {
        // So sánh hai Rectangle theo toạ độ góc trên trái và kích thước với sai số EPSILON
        if (rectangle == null) {
            return false;
        }
        return this.upperLeft.equals(rectangle.upperLeft)
                && Math.abs(this.width - rectangle.width) < EPSILON
                && Math.abs(this.height - rectangle.height) < EPSILON;
    }
}