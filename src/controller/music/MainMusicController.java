package controller.music;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import controller.parameters.FilePath;
import controller.parameters.Music;

public class MainMusicController implements MusicController {
    
    private Sound s;
    private Music m = null;
    private static MainMusicController SINGLETON;
    
    public void play(Music song) {        
        try {
            s = Gdx.audio.newSound(Gdx.files.absolute(FilePath.SONG.getAbsolutePath() + song.getPath()));
        } catch (Exception e) {
            s = Gdx.audio.newSound(Gdx.files.classpath(song.getPath()));
        }
        s.loop();
        m = song;
    }
    
    public void stop() {
        s.stop();
        s.dispose();
        m = null;
    }
    
    public Music playing() {
        return m;
    }
    
    public static MainMusicController getController() {
        if (SINGLETON == null) {
            synchronized (MainMusicController.class) {
                if (SINGLETON == null) {
                    SINGLETON = new MainMusicController();
                }
            }
        }
        return SINGLETON;
    }
} 