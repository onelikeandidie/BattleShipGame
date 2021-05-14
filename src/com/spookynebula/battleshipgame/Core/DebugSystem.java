package com.spookynebula.battleshipgame.Core;

import com.spookynebula.battleshipgame.Core.components.DrawableComponent;
import com.spookynebula.battleshipgame.Core.components.PhysicsComponent;
import com.spookynebula.battleshipgame.Core.components.PositionComponent;
import com.spookynebula.battleshipgame.ECS.*;
import com.spookynebula.battleshipgame.GameContainer;

import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Debug System has a few purposes:
 * <ul>
 * <li>Print Draw on each draw call</li>
 * <li>Print Update on each Update call</li>
 * <li>Print Init on the init call</li>
 * <li>Create random physics objects when space bar is pressed
 *   (this is for the ECSTest test)</li>
 * </ul>
 */
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
        //System.out.print("Init")
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
            // Listens for input events
            InputController.InputEvent inputEvent = (InputController.InputEvent) data;

            if (inputEvent.isKeyDown(KeyEvent.VK_ESCAPE)){
                // Clears entities on Escape pressed
                parentGame.getEntityManager().clearEntities();
            }

            if (inputEvent.isKeyDown(KeyEvent.VK_SPACE)){
                // Create entity when Space is pressed
                createRandomPhysicsEntity(inputEvent);
            }
        }
    }

    /**
     * Creates a random physics Entity on the cursor position
     * @param inputEvent The input event to get the cursor position
     */
    private void createRandomPhysicsEntity(InputController.InputEvent inputEvent) {
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
