package ArkanoidGame;

import Engine.GameManager;
import Render.CanvasRenderer;
import Render.Renderer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Lớp khởi tạo ứng dụng Arkanoid bằng JavaFX và nối GameManager với Renderer.
 *
 * Mô tả:
 * - Tạo cửa sổ chơi với kích thước cố định (WIDTH x HEIGHT).
 * - Khởi tạo {@code GameManager} để quản lý trạng thái game và {@code Renderer}
 *   để vẽ trạng thái lên {@link javafx.scene.canvas.Canvas}.
 * - Đăng ký sự kiện bàn phím: trái/phải để điều khiển paddle, SPACE để phóng bóng,
 *   R để reset khi game đã kết thúc.
 * - Chạy vòng lặp game bằng {@link javafx.animation.AnimationTimer} để gọi
 *   {@link Engine.GameManager#update()} và vẽ các đối tượng mỗi frame.
 */
public class ArkanoidApp extends Application {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;

    private GameManager gameManager;
    private Renderer renderer;

    /**
     * Điểm vào của JavaFX: khởi tạo giao diện, đăng ký event, và bắt đầu vòng lặp chơi.
     *
     * Các bước chính:
     * - Tạo {@link Canvas} và {@link CanvasRenderer} để vẽ.
     * - Khởi tạo {@link Engine.GameManager} chứa logic game và các đối tượng.
     * - Thiết lập các handler cho sự kiện phím để điều khiển paddle và hành động game.
     * - Khởi động {@link javafx.animation.AnimationTimer} để cập nhật trạng thái và render.
     *
     * @param stage cửa sổ chính của ứng dụng JavaFX
     */
    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        renderer = new CanvasRenderer(canvas);
        gameManager = new GameManager(WIDTH, HEIGHT);

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) gameManager.paddle.moveLeft();
            if (e.getCode() == KeyCode.RIGHT) gameManager.paddle.moveRight();
            if (e.getCode() == KeyCode.SPACE) gameManager.launchBall();
            if (e.getCode() == KeyCode.R) {
                if (gameManager.gameOver) {
                    gameManager.reset();
                }
            }
        });
        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) gameManager.paddle.stop();
        });

        stage.setScene(scene);
        stage.setTitle("Arkanoid Demo");
        stage.show();

        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameManager.update();
                renderer.clear();
                // draw paddle, ball, bricks
                renderer.drawPaddle(gameManager.paddle);
                renderer.drawBall(gameManager.ball);
                for (var b : gameManager.bricks) if (b.isAlive()) renderer.drawBrick(b);
                // HUD: score and lives
                renderer.drawText("Score: " + gameManager.score, 10, 20);
                renderer.drawText("Lives: " + gameManager.lives, 540, 20);
                if (gameManager.gameOver) {
                    if (gameManager.won) {
                        renderer.drawText("You win! Score: " + gameManager.score, 220, 220);
                    } else {
                        renderer.drawText("Game Over. Score: " + gameManager.score, 200, 220);
                    }
                    renderer.drawText("Press 'R' to rerun game", 200, 260);
                }
                renderer.present();
            }
        };
        loop.start();
    }

    /**
     * Hàm main tiêu chuẩn để khởi chạy ứng dụng JavaFX.
     * @param args đối số dòng lệnh (nếu có)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
