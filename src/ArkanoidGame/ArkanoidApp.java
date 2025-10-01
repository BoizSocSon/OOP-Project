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

public class ArkanoidApp extends Application {
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;

    private GameManager gameManager;
    private Renderer renderer;

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

    public static void main(String[] args) {
        launch(args);
    }
}
