package com.spookynebula.battleshipgame.Core;

import com.spookynebula.battleshipgame.Core.components.DrawableComponent;
import com.spookynebula.battleshipgame.Core.components.PhysicsComponent;
import com.spookynebula.battleshipgame.Core.components.PositionComponent;
import com.spookynebula.battleshipgame.ECS.*;
import com.spookynebula.battleshipgame.GameContainer;

import java.awt.event.KeyEvent;
import java.util.Random;

public class DebugSystem implements ISystem, IInitSystem, IUpdateSystem, IDrawSystem, ISubscriber {
    private GameContainer parentGame;
    private boolean enabled;

    Random random;

    public DebugSystem(GameContainer gameContainer) {
        parentGame = gameContainer;
        enabled = true;
        random = new Random();
    }

    public void Draw() {
        //System.out.println("Draw");
    }

    public void Init() {

    }

    public void Update(double elapsedTime) {
        //System.out.println("Update");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }

    public void notify(Object data) {
        //System.out.println(data.toString());
        if (data instanceof InputController.InputEvent){
            InputController.InputEvent inputEvent = (InputController.InputEvent) data;

            if (inputEvent.isKeyDown(KeyEvent.VK_ESCAPE)){
                parentGame.getEntityManager().clearEntities();
            }

            if (inputEvent.isKeyDown(KeyEvent.VK_SPACE)){
                // Create the Entities
                Entity testEntity = new Entity();
                parentGame.getEntityManager().addEntity(testEntity);
                DrawableComponent drawableComponent = (DrawableComponent) parentGame.getComponentRegister().newComponent(new DrawableComponent());
                drawableComponent.setTextureID(0);
                parentGame.getEntityManager().addComponent(testEntity, drawableComponent);
                PositionComponent positionComponent = (PositionComponent) parentGame.getComponentRegister().newComponent(new PositionComponent());
                int mouseX = inputEvent.getMouseX();
                int mouseY = inputEvent.getMouseY();
                positionComponent.set(mouseX, mouseY);
                parentGame.getEntityManager().addComponent(testEntity, positionComponent);
                PhysicsComponent physicsComponent = (PhysicsComponent) parentGame.getComponentRegister().newComponent(new PhysicsComponent());
                physicsComponent.setVx(random.nextFloat() * 10 - 5);
                physicsComponent.setVy(random.nextFloat() * 5 - 10);
                physicsComponent.setWeight(random.nextFloat() + 1.0f);
                physicsComponent.setAirDrag(10f);
                parentGame.getEntityManager().addComponent(testEntity, physicsComponent);
            }
        }
    }
}
