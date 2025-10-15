package Audio;

/**
 * Enum định nghĩa các music tracks trong game.
 * 
 * Each track maps to a music file in Resources/Audio/
 * 
 * @author SteveHoang aka BoizSocSon
 */
public enum MusicTrack {
    MENU("menu_theme.mp3"),
    ROUND_1("round1_theme.mp3"),
    ROUND_2("round2_theme.mp3"),
    ROUND_3("round3_theme.mp3"),
    GAME_OVER("gameover_theme.mp3"),
    VICTORY("victory_theme.mp3");
    
    private final String filename;
    
    MusicTrack(String filename) {
        this.filename = filename;
    }
    
    public String getFilename() {
        return filename;
    }
}
