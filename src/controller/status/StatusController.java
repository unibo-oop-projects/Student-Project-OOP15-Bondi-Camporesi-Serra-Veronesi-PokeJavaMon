package controller.status;

import java.util.Optional;

import controller.fight.FightController;
import controller.keyboard.FightingKeyboardController;
import controller.keyboard.FirstMenuKeyboardController;
import controller.keyboard.KeyboardController;
import controller.keyboard.MenuKeyboardController;
import controller.keyboard.SecondMenuKeyboardController;
import controller.keyboard.WalkingKeyboardController;
import controller.music.MainMusicController;
import controller.parameters.Music;
import controller.parameters.State;
import model.fight.FightVsTrainer;
import model.map.Drawable.Direction;
import model.player.PlayerImpl;
import model.map.WalkableZone;
import view.resources.Play;

/**
 * This is the main controller of the game
 */
public final class StatusController implements StatusControllerInterface {
    
    private State state;
    private KeyboardController keyboardController;
    private static StatusControllerInterface singleton; 
    
    /**
     * Private constructor, used by the method getController
     */
    private StatusController() {}
    
    /** 
     * @return the curent {@link StatusController}, or a new {@link StatusController}
     * if this is the first time this method is invoked
     */
    public static StatusControllerInterface getController() {
        if (singleton == null) {
            synchronized (StatusController.class) {
                if (singleton == null) {
                    singleton = new StatusController();
                }
            }
        }
        return singleton;
    }

    @Override
    public void updateStatus(final State s) {
        state = s;
        switch (s) {
            case FIRST_MENU:
                keyboardController = FirstMenuKeyboardController.getController();
                break;
            case SECOND_MENU:
                keyboardController = SecondMenuKeyboardController.getController();
                break;
            case WALKING:
                System.out.println(PlayerImpl.getPlayer().getTileX() + " " + PlayerImpl.getPlayer().getTileY());
                updateMusic();
                keyboardController = WalkingKeyboardController.getController(); 
                Play.updateKeyListener();
                break;
            case MENU:
                keyboardController = MenuKeyboardController.getController();
                Play.updateKeyListener();
                break;
            case FIGHTING:
                if (MainMusicController.getController().playing() == null) {
                    if (FightController.getController().getFight() instanceof FightVsTrainer) {
                        MainMusicController.getController().play(Music.TRAINER);
                    } else {
                        MainMusicController.getController().play(Music.WILD);
                    }
                } else {
                    if (MainMusicController.getController().playing() != Music.TRAINER || MainMusicController.getController().playing() != Music.WILD) {
                        MainMusicController.getController().stop();
                        if (FightController.getController().getFight() instanceof FightVsTrainer) {
                            MainMusicController.getController().play(Music.TRAINER);
                        } else {
                            MainMusicController.getController().play(Music.WILD);
                        }
                    }
                }
                keyboardController = FightingKeyboardController.getController();
                Play.updateKeyListener();
                break;
            case READING:
                keyboardController = MenuKeyboardController.getController();
                Play.updateKeyListener();
                break;
            default:
                break;
        }
    }
    
    @Override
    public State getState() {
        return state;
    }
    
    @Override
    public boolean isKeyPressed() {
        return keyboardController.isKeyPressed();
    }

    @Override
    public KeyboardController getCurrentController() {
        return keyboardController;
    }
    
    @Override
    public void updateSpeed() {
        keyboardController.updateSpeed();
    }
    
    @Override
    public Direction getDirection() {
        return keyboardController.getDirection();
    }
    
    @Override
    public void checkEncounter() {
        keyboardController.checkEncounter();
    }

    @Override
    public void updateMusic() {
        Optional<WalkableZone> zone = Play.getMapImpl().getWalkableZone(PlayerImpl.getPlayer().getTileX(), PlayerImpl.getPlayer().getTileY());
        if (zone.isPresent()) {
            for (Music m : Music.values()) {
                if (m.getAbsolutePath().equals(zone.get().getMusicPath()) && MainMusicController.getController().playing() != m && StatusController.getController().getState() != State.FIGHTING) {
                    if (MainMusicController.getController().playing() != null) {
                        MainMusicController.getController().stop();
                    }
                    MainMusicController.getController().play(m);
                }
            }
        } else {
        	System.out.println("zone not present Player @ " + new Position(PlayerImpl.getPlayer().getTileX(), PlayerImpl.getPlayer().getTileY()));
        }
    }
}