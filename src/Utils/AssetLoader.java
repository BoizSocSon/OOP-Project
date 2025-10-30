package Utils;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Font;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class AssetLoader {

    private AssetLoader() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    // ==================== IMAGE LOADING ====================

    public static Image loadImage(String filename) {
        String path = Constants.Paths.GRAPHICS_PATH + filename;
        return loadImageFromPath(path);
    }

    private static Image loadImageFromPath(String path) {
        try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Error: Image file not found - " + path);
                return createPlaceholderImage();
            }

            Image image = new Image(is);

            if(image.isError()) {
                System.err.println();
                return createPlaceholderImage();
            }

            return image;

        } catch (IOException e) {
            System.err.println("AssetLoader: IOException loading image: " + path);
            e.printStackTrace();
            return createPlaceholderImage();
        } catch (IllegalArgumentException e) {
            System.err.println("AssetLoader: Invalid image format: " + path);
            e.printStackTrace();
            return createPlaceholderImage();
        } catch (Exception e) {
            System.err.println("AssetLoader: Unexpected error loading image: " + path);
            e.printStackTrace();
            return createPlaceholderImage();
        }
    }

    public static List<Image> loadImageSequence(String patternWithPercentD, int from, int to) {
        List<Image> images = new ArrayList<>();
        for (int i = from; i <= to; i++) {
            String filename = String.format(patternWithPercentD, i);
            Image tempImage = loadImage(filename);
            images.add(tempImage);
        }

        return images;
    }

    private static Image createPlaceholderImage() {
        WritableImage placeholder = new WritableImage(50, 50);
        return placeholder;
    }

    // ==================== FONT LOADING ====================

    public static Font loadFont(String filename, int size) {
        String path = Constants.Paths.FONTS_PATH + filename;
        try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("AssetLoader: Font not found: " + path);
                return Font.font("Arial", size);
            }

            Font font = Font.loadFont(is, size);
            if (font == null) {
                System.err.println("AssetLoader: Failed to load font: " + path);
                return Font.font("Arial", size);
            }

            System.out.println("AssetLoader: Loaded font: " + filename + " (" + size + "pt)");
            return font;
        } catch (IOException e) {
            System.err.println("AssetLoader: IOException loading font: " + path);
            e.printStackTrace();
            return Font.font("Arial", size);
        } catch (Exception e) {
            System.err.println("AssetLoader: Unexpected error loading font: " + path);
            e.printStackTrace();
            return Font.font("Arial", size);
        }
    }

    // ==================== AUDIO LOADING ====================

    public static MediaPlayer loadBackgroundMusic(String track, boolean loop, double volume) {
        String path = Constants.Paths.AUDIO_PATH + track;
        try (InputStream is = AssetLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("AssetLoader: Music file not found: " + path);
                return null;
            }

            Media media = new Media(AssetLoader.class.getResource(path).toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volume);
            if (loop) {
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
            return mediaPlayer;
        } catch (Exception e) {
            System.err.println("AssetLoader: Error loading music: " + path);
            e.printStackTrace();
            return null;
        }
    }
}
