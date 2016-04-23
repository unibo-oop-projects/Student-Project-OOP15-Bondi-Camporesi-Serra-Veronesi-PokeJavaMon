package controller.keyboard;

import controller.parameters.Directions;

public class MenuKeyboardController implements KeyboardController {

    private static MenuKeyboardController SINGLETON;
    
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean isKeyPressed() {
        return false;
    }
    
    public static MenuKeyboardController getController() {
        if (SINGLETON == null) {
            synchronized (MenuKeyboardController.class) {
                if (SINGLETON == null) {
                    SINGLETON = new MenuKeyboardController();
                }
            }
        }
        return SINGLETON;
    }

    @Override
    public void updateSpeed() {
        return;
    }

    @Override
    public Directions getDirection() {
        return Directions.STILL;
    }
}