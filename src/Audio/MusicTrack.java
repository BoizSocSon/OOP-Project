package Audio;

/**
 * <p>Định nghĩa các bản nhạc khác nhau được sử dụng trong ứng dụng
 * hoặc trò chơi.</p>
 * <p>Mỗi hằng số (constant) đại diện cho một bản nhạc cụ thể và
 * lưu trữ tên file tương ứng.</p>
 */
public enum MusicTrack {
    // Bản nhạc cho màn hình menu chính
    MENU("Main_Menu_background_music.mp3"),
    // Bản nhạc nền cho các vòng chơi
    ROUNDS("Rounds_background_music.mp3"),
    // Bản nhạc khi trò chơi kết thúc (thua)
    GAME_OVER("Game_Over_background_music.mp3"),
    // Bản nhạc khi người chơi chiến thắng
    VICTORY("Victory_background_music.mp3");

    /** Tên file của bản nhạc (ví dụ: "example.mp3"). */
    private final String filename;

    /**
     * Constructor. Gán tên file cho bản nhạc.
     *
     * @param filename Tên file của bản nhạc (ví dụ: "music.mp3").
     */
    MusicTrack(String filename) {
        this.filename = filename;
    }

    /**
     * Trả về tên file tương ứng với bản nhạc này.
     *
     * @return Tên file (String) của bản nhạc.
     */
    public String getFilename() {
        return filename;
    }
}