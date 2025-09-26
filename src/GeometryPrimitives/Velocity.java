/**
 * @author SteveHoang aka BoizSocSon
 * Student ID: 23020845
 */
package GeometryPrimitives;

/**
 * Lớp Velocity dùng để tính toán gia tốc của một điểm trong hệ tọa độ
 */
public class Velocity {
    private double dx;
    private double dy;

    /**
     * Hàm khởi tạo gia tốc
     * @param dx vi phân trục x
     * @param dy vi phân trục y
     */
    public Velocity(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Tạo gia tốc từ góc và tốc độ
     * @param angle góc độ
     * @param speed tốc độ
     * @return trả về gia tốc mới dựa theo góc dộ và tốc độ
     */
    public static Velocity fromAngleAndSpeed(double angle, double speed) {
        double dx = speed * Math.sin(Math.toRadians(angle));
        double dy = -speed * Math.cos(Math.toRadians(angle));
        return new Velocity(dx, dy);
    }

    /**
     * Lấy vi phân của trục x
     * @return vi phân trục x
     */
    public double getDx() {
        return dx;
    }

    /**
     * Lấy vi phân của trục y
     * @return vi phân trục y
     */
    public double getDy() {
        return dy;
    }

    /**
     * Gắn gia tốc cho một điểm cũ để lấy được một điểm mới
     * @param point điểm cũ sẽ được gắn gia tốc vào
     * @return điểm mới được suy ra từ gia tốc và điểm cũ
     */
    public Point applyToPoint(Point point) {
        return new Point(point.getX() + dx, point.getY() + dy);
    }
}
