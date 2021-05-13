package com.spookynebula.battleshipgame;

import com.spookynebula.battleshipgame.Core.*;
import com.spookynebula.battleshipgame.ECS.*;
import jdk.internal.util.xml.impl.Input;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GameContainer implements Runnable{
    protected Thread thread;
    protected final double TICK_RATE = 1/30.0;

    protected boolean running;
    protected List<ISystem> systems;

    public WindowManager WindowManager;
    public ComponentRegister ComponentRegister;
    public EntityManager EntityManager;
    public InputController InputController;
    public ContentLoader ContentLoader;

    public GameContainer(){
        systems = new ArrayList<ISystem>();
        running = false;

        initCore();
    }

    public WindowManager getWindowManager() {
        return WindowManager;
    }
    public ComponentRegister getComponentRegister() {
        return ComponentRegister;
    }
    public EntityManager getEntityManager() {
        return EntityManager;
    }
    public InputController getInputController() {
        return InputController;
    }
    public ContentLoader getContentLoader() {
        return ContentLoader;
    }

    // Typically, you would override this method
    public void start() {
        initThread();
    }

    protected void initCore(){
        // Create our Core foundation systems
        ComponentRegister = new ComponentRegister(this);
        EntityManager = new EntityManager(this);
        WindowManager = new WindowManager(300, 300, 2.0f, "Game Container");
        InputController = new InputController(this);
        ContentLoader = new ContentLoader(this);
    }

    protected void initThread(){
        thread = new Thread(this);
        thread.run();
    }

    public void stop() {

    }

    public void run() {
        // Initialize systems that have initialization
        for (int i = 0; i < systems.size(); i++) {
            ISystem system = systems.get(i);
            if (system instanceof IInitSystem){
                ((IInitSystem) system).Init();
            }
        }

        running = true;

        double currentTime = 0;
        double lastUpdateTime = System.currentTimeMillis();
        double gameTime = 0;
        double unprocessedTime = 0;

        boolean requireRender = false;

        while (running){
            currentTime = System.currentTimeMillis();
            gameTime = currentTime - lastUpdateTime;
            lastUpdateTime = currentTime;

            unprocessedTime += gameTime;

            while (unprocessedTime >= TICK_RATE){
                unprocessedTime = 0;
                for (int i = 0; i < systems.size(); i++) {
                    ISystem system = systems.get(i);
                    if (system.isEnabled()) {
                        if (system instanceof IUpdateSystem) {
                            ((IUpdateSystem) system).Update(gameTime);
                        }
                    }
                }
                requireRender = true;
            }

            if (requireRender){
                for (int i = 0; i < systems.size(); i++) {
                    ISystem system = systems.get(i);
                    if (system.isEnabled()) {
                        if (system instanceof IDrawSystem) {
                            ((IDrawSystem) system).Draw();
                        }
                    }
                }
                WindowManager.drawWindow();
                requireRender = false;
            }
        }

        // If the game is not running anymore, dispose
        dispose();
    }

    public void dispose() {

    }
}
