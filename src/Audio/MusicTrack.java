package Audio;

public enum MusicTrack {
    MENU("Main_Menu_background_music.mp3"),
    ROUNDS("Rounds_background_music.mp3"),
    GAME_OVER("Game_Over_background_music.mp3"),
    VICTORY("Victory_background_music.mp3");

    private final String filename;

    MusicTrack(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
