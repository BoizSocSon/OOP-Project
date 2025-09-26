/**
 * @author SteveHoang aka BoizSocSon
 * Student ID: 23020845
 */
package GeometryPrimitives;

/**
 * Tạo 1 điểm trong hệ tọa độ 2D
 */
public class Point {
    private double x;
    private double y;

    /**
     * Hàm khởi tạo một điểm với một tọa độ cho trước
     * @param x tọa độ x của điểm
     * @param y tọa độ y của điểm
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Tính toán từ điểm này tới điểm khác
     * @param other điểm khác mà ta cần tính khoảng cách tới nó
     * @return khoảng cách giữa 2 điểm
     */
    public double distance(Point other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    /**
     * Kiểm tra xem 2 điểm có bằng nhau không (chồng lên nhau)
     * @param other điểm khác mà ta cần so sánh
     * @return trả về đúng khi chúng chồng lên nhau và ngược lại
     */
    public boolean equals(Point other) {
        if (other == null) {
            return false;
        }
        double epsilon = 0.000001;
        return Math.abs(this.x - other.x) < epsilon && Math.abs(this.y - other.y) < epsilon;
    }

    /**
     * Lấy thông tin về tọa độ x của điểm
     * @return trả về tọa độ x
     */
    public double getX() {
        return x;
    }

    /**
     * Lấy thông tin về tọa độ y của điểm
     * @return trả về tọa độ y
     */
    public double getY() {
        return y;
    }
}
