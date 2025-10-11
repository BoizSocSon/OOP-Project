/**
 * Lớp Line biểu diễn một đoạn thẳng (line segment) xác định bởi hai điểm
 * đầu `start` và `end` trong hệ toạ độ 2D.
 *
 * Lớp cung cấp các phép toán thường dùng cho đoạn thẳng:
 * - Tính độ dài (`length`).
 * - Kiểm tra hai đoạn có giao nhau hay không (`isIntersecting`).
 * - Tìm điểm giao của hai đoạn (`intersectionWith`).
 * - Tìm giao điểm gần điểm bắt đầu nhất giữa một đoạn và một hình chữ nhật
 *   (`closestIntersectionToStartOfLine`).
 *
 * Các thuật toán bên trong sử dụng các khái niệm hình học cơ bản:
 * - Tích chéo (cross product) giữa hai vector để xác định tính collinear (đồng phẳng trên
 *   mặt phẳng 2D) và vị trí tương đối (trái/phải) của một điểm so với hướng của đoạn.
 *   Với hai vector 2D u=(ux,uy), v=(vx,vy) ta có cross(u,v) = ux*vy - uy*vx.
 *   - cross = 0 => hai vector song song/collinear.
 *   - cross > 0 / < 0 cho biết hướng tương đối (theo quy ước trong code: >0 là phải, <0 là trái
 *     theo chiều start->end).
 *
 * - Phương pháp tham số hoá vector (sử dụng r và s) để tìm giao điểm duy nhất của hai
 *   đoạn khi chúng không song song. Nếu r x s != 0, tồn tại t sao cho điểm giao là
 *   p + t * r, trong đó p là start của đoạn này và r = end - start.
 *
 * - Kiểm tra overlap (chồng đoạn) khi hai đoạn collinear: kiểm tra khoảng chiếu trên trục X
 *   và trục Y để xác định phần giao trên từng trục; nếu có giao (khoảng đè) theo ít nhất
 *   một trục thì coi là overlap theo định nghĩa của dự án.
 *
 * Lưu ý: lớp sử dụng một EPSILON nhỏ (1e-6) để so sánh số thực vì sai số làm tròn.
 *
 * @author SteveHoang aka BoizSocSon
 * Student ID: 23020845
 */
package GeometryPrimitives;

import java.util.List;

public class Line {
    private static final double EPSILON = 1e-6;
    private final Point start;
    private final Point end;

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Point closestIntersectionToStartOfLine(Rectangle rect) {
        List<Point> intersections = rect.intersectionPoints(this);
        if (intersections.isEmpty()) {
            return null;
        }

        Point closestPoint = intersections.get(0);
        double minDistance = start.distance(closestPoint);

        // tìm điểm giao gần nhất so với điểm bắt đầu của đoạn
        for (Point point : intersections) {
            double distance = start.distance(point);
            if (distance < minDistance) {
                closestPoint = point;
                minDistance = distance;
            }
        }

        return closestPoint;
    }

    /**
     * Tìm giao điểm của `this` với các cạnh của `rect` rồi trả về giao điểm gần
     * với `start` nhất (theo khoảng cách Euclid).
     *
     * Thuật toán: lấy danh sách giao điểm từ `rect.intersectionPoints(this)`,
     * nếu rỗng trả về null. Ngược lại duyệt và chọn điểm có khoảng cách nhỏ
     * nhất tới `start`.
     *
     * @param "rect" hình chữ nhật cần kiểm tra
     * @return điểm giao gần `start` nhất hoặc null nếu không có giao điểm
     */

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public double length() {
        return start.distance(end);
    }

    /**
     * Xác định vị trí tương đối của `point` so với đường thẳng vô hạn đi qua
     * `start` → `end` bằng tích chéo (cross product) của vector.
     *
     * Toán học:
     * - Gọi r = end - start, v = point - start.
     * - cross = r x v = (r.x * v.y - r.y * v.x).
     *   + Nếu |cross| < EPSILON => point nằm trên đường thẳng (collinear).
     *   + Nếu cross > 0 => point nằm 'bên phải' theo quy ước start→end.
     *   + Nếu cross < 0 => point nằm 'bên trái'.
     *
     * Trả về: 0 nếu nằm trên, 1 nếu bên phải, 2 nếu bên trái.
     *
     * @param point điểm cần kiểm tra vị trí
     * @return 0=on-line, 1=right, 2=left
     */
    private int directionPointRelToLine(Point point) {
        double pointX = point.getX();
        double pointY = point.getY();
        double startX = this.start.getX();
        double startY = this.start.getY();
        double endX = this.end.getX();
        double endY = this.end.getY();

        // Tính toán vị trí tương đối bằng công thức tích chéo
        double slopeCalc = (endY - startY) * (pointX - startX) - (endX - startX) * (pointY - startY);

        if (Math.abs(slopeCalc) < EPSILON) { // slopeCalc == 0
            return 0; // Nằm trên đường thẳng
        }
        if (slopeCalc > 0) {
            return 1; // Nằm bên phải (theo chiều start → end)
        } else {
            return 2; // Nằm bên trái (theo chiều start → end)
        }
    }

    /**
     * Kiểm tra xem `point` có nằm trên đoạn thẳng (segment) hay không.
     *
     * Bước kiểm tra:
     * 1) Nếu point trùng với start hoặc end => nằm trên.
     * 2) Kiểm tra xem point có nằm trong hình chữ nhật bao bởi start và end
     *    (theo cả trục X và Y) - đảm bảo point nằm trong đoạn giới hạn bởi 2 đầu.
     * 3) Sử dụng tích chéo như ở `directionPointRelToLine` để kiểm tra collinear
     *    với sai số EPSILON.
     *
     * @param point điểm cần kiểm tra
     * @return true nếu point nằm trên đoạn (gồm cả 2 endpoint)
     */
    private boolean isPointOnLine(Point point) {
        double pointX = point.getX();
        double pointY = point.getY();
        double startX = this.start.getX();
        double startY = this.start.getY();
        double endX = this.end.getX();
        double endY = this.end.getY();

        // Kiểm tra trùng điểm đầu hoặc điểm cuối
        if (point.equals(this.start) || point.equals(this.end)) {
            return true;
        }

        // Kiểm tra xem điểm có nằm trong hình chữ nhật hay không
        if (pointX > Math.max(startX, endX) + EPSILON || pointX < Math.min(startX, endX) - EPSILON) {
            return false;
        }
        if (pointY > Math.max(startY, endY) + EPSILON || pointY < Math.min(startY, endY) - EPSILON) {
            return false;
        }

        // Tính toán vị trí tương đối
        double slopeCalc = (endY - startY) * (pointX - startX) - (endX - startX) * (pointY - startY);

        return Math.abs(slopeCalc) < EPSILON;
    }

    /**
     * Kiểm tra hai đoạn thẳng collinear (nằm cùng một đường) có thực sự chồng lấp
     * (overlap) hay không. Trường hợp chạm tại một điểm (touch at endpoint)
     * sẽ không được coi là overlap ở đây (overlap yêu cầu giao nhau có độ dài > 0).
     *
     * Thuật toán:
     * - Kiểm tra collinear bằng hai cross product: nếu cả `cross1` và `cross2` xấp xỉ 0
     *   nghĩa là các đầu của đoạn `other` đều nằm trên đường chứa `this`.
     * - Nếu collinear, ta tính khoảng chiếu (projection) của hai đoạn lên trục X và Y:
     *   - overlapX1 = max(min(x1,x2), min(x3,x4))
     *   - overlapX2 = min(max(x1,x2), max(x3,x4))
     *   Tương tự cho Y.
     * - Nếu có khoảng chiếu hợp lệ (overlapOnX hoặc overlapOnY) với kích thước lớn hơn EPSILON,
     *   coi là overlap.
     *
     * @param other đoạn cần kiểm tra
     * @return true nếu hai đoạn collinear và có phần chồng lấp thực sự
     */
    private boolean isOverlapping(Line other) {
        // Lấy tọa độ
        double x1 = this.start.getX();
        double y1 = this.start.getY();
        double x2 = this.end.getX();
        double y2 = this.end.getY();
        double x3 = other.start.getX();
        double y3 = other.start.getY();
        double x4 = other.end.getX();
        double y4 = other.end.getY();

        // 1. Kiểm tra collinear bằng cross product
        double cross1 = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
        double cross2 = (x2 - x1) * (y4 - y1) - (y2 - y1) * (x4 - x1);

        if (!(Math.abs(cross1) < EPSILON && Math.abs(cross2) < EPSILON)) {
            return false; // Không collinear
        }

        // 2. Kiểm tra giao nhau theo trục X và trục Y
        double overlapX1 = Math.max(Math.min(x1, x2), Math.min(x3, x4));
        double overlapX2 = Math.min(Math.max(x1, x2), Math.max(x3, x4));

        double overlapY1 = Math.max(Math.min(y1, y2), Math.min(y3, y4));
        double overlapY2 = Math.min(Math.max(y1, y2), Math.max(y3, y4));

        // Nếu khoảng chiếu giao nhau hợp lệ trên cả 2 trục
        boolean overlapOnX = overlapX1 < overlapX2 - EPSILON;
        boolean overlapOnY = overlapY1 < overlapY2 - EPSILON;

        return overlapOnX || overlapOnY;
    }

    /**
     * Kiểm tra hai đoạn thẳng có giao nhau hay không.
     *
     * Phương pháp:
     * - Tính vị trí tương đối (direction) của các đầu đoạn này so với đường thẳng
     *   của đoạn kia (d1..d4).
     * - Nếu d1 và d2 khác nhau và d3 và d4 khác nhau => hai đoạn cắt nhau theo trường hợp tổng quát.
     * - Xử lý các trường hợp đặc biệt (collinear hoặc chạm endpoint): nếu một đầu nằm trên
     *   đoạn kia và nằm trong giới hạn đoạn (`isPointOnLine`) thì coi là giao nhau.
     *
     * @param other đoạn thẳng khác
     * @return true nếu hai đoạn có ít nhất một điểm giao
     */
    public boolean isIntersecting(Line other) {
        int d1 = this.directionPointRelToLine(other.getStart());
        int d2 = this.directionPointRelToLine(other.getEnd());
        int d3 = other.directionPointRelToLine(this.getStart());
        int d4 = other.directionPointRelToLine(this.getEnd());

        // 1. Trường hợp tổng quát: 2 đoạn cắt nhau (khác phía nhau)
        if (d1 != d2 && d3 != d4) {
            return true;
        }

        // 2. Trường hợp đặc biệt: collinear + nằm trên đoạn
        return (d1 == 0 && this.isPointOnLine(other.getStart())) ||
                (d2 == 0 && this.isPointOnLine(other.getEnd()))   ||
                (d3 == 0 && other.isPointOnLine(this.getStart())) ||
                (d4 == 0 && other.isPointOnLine(this.getEnd()));
    }

    /**
     * Tính điểm giao giữa hai đoạn thẳng (nếu tồn tại duy nhất) và trả về
     * một `Point` tương ứng. Nếu không có giao điểm hoặc có vô số điểm giao
     * (overlap hoàn toàn), theo thiết kế hàm trả về null trong một số trường hợp:
     * - Không cắt nhau -> null.
     * - Collinear và overlap (nhiều điểm giao) -> null.
     * - Collinear nhưng chạm tại một endpoint -> trả endpoint chung.
     *
     * Chi tiết toán học (trường hợp r x s != 0):
     * - Gọi p = this.start, r = this.end - this.start;
     * - Gọi q = other.start, s = other.end - other.start;
     * - Nếu r x s != 0, tồn tại t sao cho p + t*r là điểm giao. Ta tính
     *   t = ((q - p) x s) / (r x s). Sau đó điểm giao = p + t*r.
     * - Ở đây code tính trực tiếp t và dùng nó để xây dựng `Point(interX, interY)`.
     *
     * Các bước biện luận bổ sung:
     * - Nếu một đoạn bị suy giảm thành điểm (start == end) thì kiểm tra trực tiếp
     *   xem điểm đó có nằm trên đoạn kia.
     * - Nếu r x s ≈ 0 và (q - p) x r ≈ 0 => collinear: kiểm tra overlap bằng
     *   `isOverlapping`. Nếu overlap trả null (vô số điểm giao). Nếu không overlap
     *   nhưng chạm endpoint, trả endpoint chung.
     *
     * @param other đoạn thứ hai
     * @return điểm giao nếu có duy nhất, hoặc null theo quy ước nêu trên
     */
    public Point intersectionWith(Line other) {
        // Nếu không intersect (theo isIntersecting) thì nhanh return null
        if (!this.isIntersecting(other)) return null;

        // Nếu một trong hai là điểm
        if (this.start.equals(this.end)) return other.isPointOnLine(this.start) ? this.start : null;
        if (other.start.equals(other.end)) return this.isPointOnLine(other.start) ? other.start : null;

        // Vecto r = this.end - this.start
        double rX = this.end.getX() - this.start.getX();
        double rY = this.end.getY() - this.start.getY();
        // Vecto s = other.end - other.start
        double sX = other.end.getX() - other.start.getX();
        double sY = other.end.getY() - other.start.getY();

        // r x s
        double rxs = rX * sY - rY * sX;
        // (q - p) x r
        double qpx = other.start.getX() - this.start.getX();
        double qpy = other.start.getY() - this.start.getY();
        double qpxr = qpx * rY - qpy * rX;

        if (Math.abs(rxs) < EPSILON) {
            // r x s ≈ 0 => parallel or collinear
            if (Math.abs(qpxr) < EPSILON) {
                // collinear: kiểm tra overlap hoặc chạm endpoint
                // Tính projection đơn giản trên trục X (hoặc Y nếu rX nhỏ)
                boolean overlap = this.isOverlapping(other);
                if (overlap) {
                    return null; // infinite points -> theo thiết kế trả null
                }
                // không overlap nhưng có thể chạm endpoint: trả endpoint chung nếu có
                if (this.start.equals(other.start) || this.start.equals(other.end)) return this.start;
                if (this.end.equals(other.start) || this.end.equals(other.end)) return this.end;
                return null;
            }
            // parallel but not collinear
            return null;
        }

        // Nếu rxs != 0 => compute t, u
        double t = ( (other.start.getX() - this.start.getX()) * sY
                - (other.start.getY() - this.start.getY()) * sX ) / rxs;
        // (không nhất thiết phải tính u nếu isIntersecting đã chắc chắn)
        double interX = this.start.getX() + t * rX;
        double interY = this.start.getY() + t * rY;
        Point inter = new Point(interX, interY);

        // Đảm bảo điểm này nằm trên cả 2 đoạn (biện pháp an toàn)
        if (this.isPointOnLine(inter) && other.isPointOnLine(inter)) return inter;
        return null;
    }
}
