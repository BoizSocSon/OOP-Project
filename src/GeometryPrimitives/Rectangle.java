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

import Utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class Rectangle {
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

        if (line == null) {
            return intersections; // Trả về danh sách rỗng nếu line null
        }

        // Tính trước các điểm gốc để tránh gọi getter lặp lại
        final double x = this.upperLeft.getX();
        final double y = this.upperLeft.getY();

        final Point topLeft = this.upperLeft;
        final Point topRight = new Point(x + this.width, y);
        final Point bottomLeft = new Point(x, y + this.height);
        final Point bottomRight = new Point(x + this.width, y + this.height);

        // Tạo 4 cạnh của hình chữ nhật
        Line[] sides = new Line[] {
                new Line(topLeft, topRight),       // top
                new Line(bottomLeft, bottomRight), // bottom
                new Line(topLeft, bottomLeft),     // left
                new Line(topRight, bottomRight)    // right
        };

        // Kiểm tra giao điểm giữa line và từng cạnh
        for (Line side : sides) {
            Point intersection = line.intersectionWith(side);
            if (intersection != null && !intersections.contains(intersection)) {
                intersections.add(intersection);
            }
        }

        return intersections;
    }
    
    /**
     * Checks if this rectangle intersects with another rectangle.
     * Uses AABB (Axis-Aligned Bounding Box) collision detection.
     * 
     * @param other The other rectangle to check
     * @return true if rectangles overlap, false otherwise
     */
    public boolean intersects(Rectangle other) {
        if (other == null) {
            return false;
        }
        double thisLeft = this.upperLeft.getX();
        double thisRight = thisLeft + this.width;
        double thisTop = this.upperLeft.getY();
        double thisBottom = thisTop + this.height;

        double otherLeft = other.upperLeft.getX();
        double otherRight = otherLeft + other.width;
        double otherTop = other.upperLeft.getY();
        double otherBottom = otherTop + other.height;

        return !(thisLeft >= otherRight
                || thisRight <= otherLeft
                || thisTop >= otherBottom
                || thisBottom <= otherTop);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Rectangle)) {
            return false;
        }

        Rectangle other = (Rectangle) obj;

        return this.upperLeft.equals(other.upperLeft)
                && Math.abs(this.width - other.width) < Constants.General.EPSILON
                && Math.abs(this.height - other.height) < Constants.General.EPSILON;
    }

    @Override
    public int hashCode() {
        // Quantize values by EPSILON to align with equals tolerance
        long ux = Math.round(this.upperLeft.getX() / Constants.General.EPSILON);
        long uy = Math.round(this.upperLeft.getY() / Constants.General.EPSILON);
        long w = Math.round(this.width / Constants.General.EPSILON);
        long h = Math.round(this.height / Constants.General.EPSILON);
        int result = Long.hashCode(ux);
        result = 31 * result + Long.hashCode(uy);
        result = 31 * result + Long.hashCode(w);
        result = 31 * result + Long.hashCode(h);
        return result;
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
}