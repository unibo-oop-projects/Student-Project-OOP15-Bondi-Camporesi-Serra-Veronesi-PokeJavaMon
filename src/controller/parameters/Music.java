package controller.parameters;

import java.io.File;

/**
 * This enum contains all the songs
 */
public enum Music {
    
    SONG ("NONE", Folder.MAINFOLDER.getAbsolutePath() + File.separator + "music" + File.separator),
    HOME("/music/home.mp3", "/home.mp3"), 
    OPENING("/music/opening.mp3", "/opening.mp3"),
    LAB("/music/lab.mp3", "/lab.mp3"),
    WILD("/music/wild.mp3", "/wild.mp3"),
    TRAINER("/music/trainer.mp3", "/trainer.mp3"),
    CENTER("/music/center.mp3", "/center.mp3"),
    MART("/music/mart.mp3", "/mart.mp3"),
    CAVE("/music/cave.mp3", "/cave.mp3"),
    TOWN("/music/town.mp3", "/town.mp3"),
    ROUTE("/music/route.mp3", "/route.mp3");

    final String resPath;
    final String absPath;
    
    private Music(final String rp, final String absp) {
        this.resPath = rp;
        this.absPath = absp;
    }
    
    /**
     * @return the resource path of the selected song
     */
    public String getResourcePath() {
        return this.resPath;
    }
    
    /**
     * @return the absolute path of the selected song
     */
    public String getAbsolutePath() {
    	return this.absPath;
    }
}