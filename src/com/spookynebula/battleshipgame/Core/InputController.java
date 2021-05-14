package com.spookynebula.battleshipgame.Core;

import com.spookynebula.battleshipgame.ECS.ISubscribeSystem;
import com.spookynebula.battleshipgame.ECS.ISubscriber;
import com.spookynebula.battleshipgame.GameContainer;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputController implements ISubscribeSystem, KeyListener, MouseListener, MouseMotionListener {
    private GameContainer parentGame;

    protected boolean enabled;
    protected List<ISubscriber> subscribers;

    protected InputEvent InputEvent;

    public InputController(GameContainer gameContainer){
        enabled = true;
        subscribers = new ArrayList<ISubscriber>();

        parentGame = gameContainer;

        parentGame.getWindowManager().getCanvas().addKeyListener(this);
        parentGame.getWindowManager().getCanvas().addMouseListener(this);
        parentGame.getWindowManager().getCanvas().addMouseMotionListener(this);

        InputEvent = new InputEvent();
    }

    public void subscribe(ISubscriber newSubscriber) {
        subscribers.add(newSubscriber);
    }

    public void unsubscribe(ISubscriber subscriber){
        subscribers.remove(subscriber);
    }

    public boolean isEnabled() { return enabled; }

    public void disable() { enabled = false; }

    public void enable() { enabled = true; }

    private void notifySubscriber(InputEvent eventInputEvent){
        for (ISubscriber subscriber : subscribers) {
            subscriber.notify(eventInputEvent);
        }
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        InputEvent.setKeyDown(e.getKeyCode());
        notifySubscriber(InputEvent);
    }

    public void keyReleased(KeyEvent e) {
        InputEvent.setKeyUp(e.getKeyCode());
        notifySubscriber(InputEvent);
    }

    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {
        InputEvent.setMouseKeyDown(e.getButton());
        notifySubscriber(InputEvent);
    }

    public void mouseReleased(MouseEvent e) {
        InputEvent.setMouseKeyUp(e.getButton());
        notifySubscriber(InputEvent);
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {
        InputEvent.setMouseX((int) (e.getX() / parentGame.getWindowManager().getWindowScale()));
        InputEvent.setMouseY((int) (e.getY() / parentGame.getWindowManager().getWindowScale()));
    }

    /**
     * Describes a more easy to access Input Event
     */
    public class InputEvent {
        private boolean[] keys;
        private boolean[] mouseKeys;

        private int mouseX;
        private int mouseY;

        InputEvent(){
            keys = new boolean[256];
            mouseKeys = new boolean[4];
            mouseX = 0;
            mouseY = 0;
        }

        private void setKeyDown(int keyCode){
            keys[keyCode] = true;
        }

        private void setKeyUp(int keyCode){
            keys[keyCode] = false;
        }

        private void setMouseKeyDown(int keyCode){
            mouseKeys[keyCode] = true;
        }

        private void setMouseKeyUp(int keyCode){
            mouseKeys[keyCode] = false;
        }

        public boolean isKeyDown(int keyCode){
            return keys[keyCode];
        }

        public boolean isKeyUp(int keyCode){
            return !keys[keyCode];
        }

        public boolean isMouseKeyDown(int keyCode){
            return mouseKeys[keyCode];
        }

        public boolean isMouseKeyUp(int keyCode){
            return !mouseKeys[keyCode];
        }

        public int getMouseX() {
            return mouseX;
        }

        public void setMouseX(int newMouseX) {
            mouseX = newMouseX;
        }

        public int getMouseY() {
            return mouseY;
        }

        public void setMouseY(int newMouseY) {
            mouseY = newMouseY;
        }

        public String toString() {
            return "Input{" +
                    "keys=" + Arrays.toString(keys).replaceAll("false", "0").replaceAll("true", "1") +
                    ", mouseKeys=" + Arrays.toString(mouseKeys).replaceAll("false", "0").replaceAll("true", "1")  +
                    ", mouseX=" + mouseX +
                    ", mouseY=" + mouseY +
                    '}';
        }
    }
}
