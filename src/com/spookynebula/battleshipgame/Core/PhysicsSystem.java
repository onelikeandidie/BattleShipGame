package com.spookynebula.battleshipgame.Core;

import com.spookynebula.battleshipgame.Core.components.DrawableComponent;
import com.spookynebula.battleshipgame.Core.components.PhysicsComponent;
import com.spookynebula.battleshipgame.Core.components.PositionComponent;
import com.spookynebula.battleshipgame.Core.gfx.Image;
import com.spookynebula.battleshipgame.ECS.*;
import com.spookynebula.battleshipgame.GameContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PhysicsSystem implements IUpdateSystem, ISubscriber {
    private boolean enabled;
    private GameContainer parentGame;

    private List<IEntity> physicsEntityList;

    public PhysicsSystem(GameContainer gameContainer){
        parentGame = gameContainer;
        enabled = true;

        physicsEntityList = new ArrayList<IEntity>();
    }

    public void Update(double elapsedTime) {
        // Debug, to know how many entities were acted on this frame
        int entityCount = 0;
        float dividedTime = (float) (elapsedTime / 10);

        for (int i = 0; i < physicsEntityList.size(); i++) {
            IEntity entity = physicsEntityList.get(i);
            PositionComponent positionComponent = (PositionComponent) parentGame.ComponentRegister.get(entity, "position_component");
            float currentX = positionComponent.getX();
            float currentY = positionComponent.getY();

            PhysicsComponent physicsComponent = (PhysicsComponent) parentGame.ComponentRegister.get(entity, "physics_component");
            float currentVx = physicsComponent.getVx();
            float currentVy = physicsComponent.getVy();
            float currentAirDrag = physicsComponent.getDragAcceleration();
            float currentWeight = physicsComponent.getWeight();
            float maxVelocity = physicsComponent.getTerminalVelocity();

            if (currentWeight != 0) {
                currentVy = (currentVy + currentWeight * 0.098f * dividedTime);
            }
            if (currentAirDrag != 0) {
                if (currentVx > 1f) {
                    currentVx = (currentVx + (-currentAirDrag * (dividedTime * dividedTime)) / 2);
                    currentVy = (currentVy + (-currentAirDrag * (dividedTime * dividedTime)) / 2);
                }
                if (currentVx < -1f) {
                    currentVx = (currentVx + (currentAirDrag * (dividedTime * dividedTime)) / 2);
                    currentVy = (currentVy + (currentAirDrag * (dividedTime * dividedTime)) / 2);
                }
            }

            if (maxVelocity >= 0){
                if (currentVx > maxVelocity) currentVx = maxVelocity;
                if (currentVx < -maxVelocity) currentVx = -maxVelocity;
                if (currentVy > maxVelocity) currentVy = maxVelocity;
                if (currentVy < -maxVelocity) currentVy = -maxVelocity;
            }

            currentX = (currentX + currentVx * dividedTime);
            currentY = (currentY + currentVy * dividedTime);

            physicsComponent.setVx(currentVx);
            physicsComponent.setVy(currentVy);
            positionComponent.set(currentX, currentY);

            entityCount++;
        }
    }

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
