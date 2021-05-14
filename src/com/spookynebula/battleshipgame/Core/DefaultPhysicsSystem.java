package com.spookynebula.battleshipgame.Core;

import com.spookynebula.battleshipgame.Core.components.PhysicsComponent;
import com.spookynebula.battleshipgame.Core.components.PositionComponent;
import com.spookynebula.battleshipgame.ECS.*;
import com.spookynebula.battleshipgame.GameContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Standard example Physics system
 */
public class DefaultPhysicsSystem implements IUpdateSystem, ISubscriber {
    private boolean enabled;
    private GameContainer parentGame;

    private List<IEntity> physicsEntityList;

    public DefaultPhysicsSystem(GameContainer gameContainer){
        parentGame = gameContainer;
        enabled = true;

        physicsEntityList = new ArrayList<IEntity>();
    }

    public void Update(double elapsedTime) {
        // Debug, to know how many entities were acted on this frame
        int entityCount = 0;
        // This reduces the amount of movement happening between each update
        float dividedTime = (float) (elapsedTime / 10);

        for (int i = 0; i < physicsEntityList.size(); i++) {
            IEntity entity = physicsEntityList.get(i);
            // Get the current position of the entity
            PositionComponent positionComponent = (PositionComponent) parentGame.ComponentRegister.get(entity, "position_component");
            float currentX = positionComponent.getX();
            float currentY = positionComponent.getY();

            // Get the current velocity, weight and drag of the entity
            PhysicsComponent physicsComponent = (PhysicsComponent) parentGame.ComponentRegister.get(entity, "physics_component");
            float currentVx = physicsComponent.getVx();
            float currentVy = physicsComponent.getVy();
            float currentAirDrag = physicsComponent.getDragAcceleration();
            float currentWeight = physicsComponent.getWeight();
            float maxVelocity = physicsComponent.getTerminalVelocity();

            // If the weight is not zero, accelerate with gravity
            if (currentWeight != 0) {
                currentVy = (currentVy + currentWeight * 0.098f * dividedTime);
            }
            // If the drag is not zero, decelerate with air drag
            if (currentAirDrag != 0) {
                // It kinda depends on the direction of the velocity vector
                if (currentVx > 1f) {
                    currentVx = (currentVx + (-currentAirDrag * (dividedTime * dividedTime)) / 2);
                    currentVy = (currentVy + (-currentAirDrag * (dividedTime * dividedTime)) / 2);
                }
                if (currentVx < -1f) {
                    currentVx = (currentVx + (currentAirDrag * (dividedTime * dividedTime)) / 2);
                    currentVy = (currentVy + (currentAirDrag * (dividedTime * dividedTime)) / 2);
                }
            }

            // This limits the velocity to the Terminal Velocity
            if (maxVelocity >= 0){
                if (currentVx > maxVelocity) currentVx = maxVelocity;
                if (currentVx < -maxVelocity) currentVx = -maxVelocity;
                if (currentVy > maxVelocity) currentVy = maxVelocity;
                if (currentVy < -maxVelocity) currentVy = -maxVelocity;
            }

            // Finally calculate the position from the velocity
            currentX = (currentX + currentVx * dividedTime);
            currentY = (currentY + currentVy * dividedTime);

            // Set the new velocity and the new position
            physicsComponent.setVx(currentVx);
            physicsComponent.setVy(currentVy);
            positionComponent.set(currentX, currentY);

            entityCount++;
        }
    }

    /**
     * Recalculates the dragAcceleration and terminalVelocity of the
     * component given.
     * @param component Component to recalculate
     */
    private void recalculateComponent(PhysicsComponent component) {
        float weight = component.getWeight();
        float airDrag = component.getAirDrag();
        float dragAcceleration = 0, terminalVelocity = 0;
        if (weight != 0)
            dragAcceleration = airDrag / weight;
        else
            dragAcceleration = airDrag;
        if (weight != 0)
            terminalVelocity = (float) Math.sqrt((weight*98) / dragAcceleration);
        else terminalVelocity = -1;
        component.setDragAcceleration(dragAcceleration);
        component.setTerminalVelocity(terminalVelocity);
    }

    public void notify(Object data) {
        if (data instanceof EntityManager.EntityModifiedEvent) {
            EntityManager.EntityModifiedEvent eventData = (EntityManager.EntityModifiedEvent) data;
            String componentType = eventData.getComponent().getType();
            if (componentType == "physics_component") {
                recalculateComponent((PhysicsComponent) eventData.getComponent());
                updateFilter();
            }
            if (componentType == "position_component") updateFilter();
        }
        if (data instanceof EntityManager.EntitiesClearedEvent) {
            EntityManager.EntitiesClearedEvent eventData = (EntityManager.EntitiesClearedEvent) data;
            updateFilter();
        }
        if (data instanceof EntityManager.EntityRemovedEvent) {
            EntityManager.EntityRemovedEvent eventData = (EntityManager.EntityRemovedEvent) data;
            updateFilter();
        }
    }

    private void updateFilter() {
        physicsEntityList = parentGame.getEntityManager().filter("physics_component", "position_component");
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
}
