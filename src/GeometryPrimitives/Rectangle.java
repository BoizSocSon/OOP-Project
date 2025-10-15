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

        // Tính trước các điểm gốc để tránh gọi getter lặp lại
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
        // Return a defensive copy to avoid external mutation of internal state
        return new Point(this.upperLeft.getX(), this.upperLeft.getY());
    }
    
    /**
     * Checks if this rectangle intersects with another rectangle.
     * Uses AABB (Axis-Aligned Bounding Box) collision detection.
     * 
     * @param other The other rectangle to check
     * @return true if rectangles overlap, false otherwise
     */
    public boolean intersects(Rectangle other) {
        if (other == null) return false;
        
        double thisLeft = this.upperLeft.getX();
        double thisRight = thisLeft + this.width;
        double thisTop = this.upperLeft.getY();
        double thisBottom = thisTop + this.height;
        
        double otherLeft = other.upperLeft.getX();
        double otherRight = otherLeft + other.width;
        double otherTop = other.upperLeft.getY();
        double otherBottom = otherTop + other.height;
        
        // Check if NOT overlapping (then negate)
        return !(thisRight <= otherLeft || 
                 thisLeft >= otherRight || 
                 thisBottom <= otherTop || 
                 thisTop >= otherBottom);
    }

    public boolean equals(Rectangle rectangle) {
        // So sánh hai Rectangle theo toạ độ góc trên trái và kích thước với sai số EPSILON
        // Keep for backward compatibility but delegate to standard equals(Object)
        return this.equals((Object) rectangle);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Rectangle)) return false;
        Rectangle other = (Rectangle) obj;
        if (!this.upperLeft.equals(other.upperLeft)) return false;
        if (Math.abs(this.width - other.width) >= EPSILON) return false;
        if (Math.abs(this.height - other.height) >= EPSILON) return false;
        return true;
    }

    @Override
    public int hashCode() {
        // Quantize values by EPSILON to align with equals tolerance
        long ux = Math.round(this.upperLeft.getX() / EPSILON);
        long uy = Math.round(this.upperLeft.getY() / EPSILON);
        long w = Math.round(this.width / EPSILON);
        long h = Math.round(this.height / EPSILON);
        int result = Long.hashCode(ux);
        result = 31 * result + Long.hashCode(uy);
        result = 31 * result + Long.hashCode(w);
        result = 31 * result + Long.hashCode(h);
        return result;
    }
}