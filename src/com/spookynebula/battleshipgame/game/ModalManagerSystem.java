package com.spookynebula.battleshipgame.game;

import com.spookynebula.battleshipgame.Core.CursorSystem;
import com.spookynebula.battleshipgame.Core.InputController;
import com.spookynebula.battleshipgame.Core.components.PositionComponent;
import com.spookynebula.battleshipgame.Core.gfx.Image;
import com.spookynebula.battleshipgame.ECS.*;
import com.spookynebula.battleshipgame.GameContainer;
import com.spookynebula.battleshipgame.game.components.DrawableButtonComponent;
import com.spookynebula.battleshipgame.game.components.ModalComponent;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ModalManagerSystem implements IUpdateSystem, ISubscribeSystem, ISubscriber {
    private GameContainer parentGame;
    private boolean enabled;
    private List<ISubscriber> subscribers;

    private List<IEntity> modalEntityList;

    public ModalManagerSystem(GameContainer gameContainer) {
        parentGame = gameContainer;
        enabled = true;

        modalEntityList = new ArrayList<IEntity>();
        subscribers = new ArrayList<ISubscriber>();
    }

    public void Update(double elapsedTime) {
        double dividedTime = elapsedTime / 100;
        if (!modalEntityList.isEmpty()) {
            IEntity entity = modalEntityList.get(0);
            PositionComponent positionComponent =
                    (PositionComponent) parentGame.ComponentRegister.get(entity, "position_component");

            float offsetY = positionComponent.getY();

            if (offsetY < 0) {
                offsetY = (float) (offsetY*(1-dividedTime)+0*dividedTime);

                positionComponent.setY(offsetY);
            }
        }
    }

    private void dismissModal() {
        if (!modalEntityList.isEmpty()) {
            IEntity entity = modalEntityList.get(0);
            parentGame.getEntityManager().removeEntity(entity);
            notifySubscriber(new ModalDissmisedEvent());
        }
    }

    public void notify(Object data) {
        if (data instanceof EntityManager.EntityModifiedEvent) {
            EntityManager.EntityModifiedEvent eventData = (EntityManager.EntityModifiedEvent) data;
            String componentType = eventData.getComponent().getType();
            if (componentType == "modal_component") updateFilter();
        }
        if (data instanceof EntityManager.EntitiesClearedEvent){
            EntityManager.EntitiesClearedEvent eventData = (EntityManager.EntitiesClearedEvent) data;
            updateFilter();
        }
        if (data instanceof InputController.InputEvent){
            InputController.InputEvent inputEvent = (InputController.InputEvent) data;
            if (inputEvent.isMouseKeyDown(MouseEvent.BUTTON1) || inputEvent.isMouseKeyDown(MouseEvent.BUTTON3))
                dismissModal();
        }
        if (data instanceof EntityManager.EntityRemovedEvent){
            EntityManager.EntityRemovedEvent eventData = (EntityManager.EntityRemovedEvent) data;
            updateFilter();
        }
    }

    protected void updateFilter() {
        modalEntityList = parentGame.getEntityManager().filter("modal_component", "position_component");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void disable() { enabled = false; }
    public void enable() { enabled = true; }

    public void subscribe(ISubscriber newSubscriber) {
        subscribers.add(newSubscriber);
    }

    public void unsubscribe(ISubscriber subscriber){
        subscribers.remove(subscriber);
    }

    public void notifySubscriber(Object eventData){
        for (ISubscriber subscriber : subscribers) {
            subscriber.notify(eventData);
        }
    }

    public class ModalDissmisedEvent {
        public ModalDissmisedEvent() {}
    }
}
