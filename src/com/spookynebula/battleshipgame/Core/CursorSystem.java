package com.spookynebula.battleshipgame.Core;

import com.spookynebula.battleshipgame.Core.components.DrawableComponent;
import com.spookynebula.battleshipgame.Core.components.HoverComponent;
import com.spookynebula.battleshipgame.Core.components.PositionComponent;
import com.spookynebula.battleshipgame.ECS.*;
import com.spookynebula.battleshipgame.GameContainer;

import java.util.ArrayList;
import java.util.List;

public class CursorSystem implements IInitSystem, IUpdateSystem, ISubscriber {
    protected GameContainer parentGame;
    private boolean enabled;

    protected Cursors currentCursor;
    private Entity cursorEntity;
    private PositionComponent cursorPosition;
    private DrawableComponent cursorDrawable;
    private Cursors cursorDefault;
    private boolean smoothCursor;
    protected List<IEntity> hoverEntityList;

    public CursorSystem(GameContainer gameContainer){
        parentGame = gameContainer;
        enabled = true;
        smoothCursor = false;
        hoverEntityList = new ArrayList<IEntity>();
        cursorDefault = Cursors.Arrow;
    }

    public void Init() {
        // Init is needed to create the Drawable Cursor Entity
        // and hide the actual system cursor
        createCursor();
        parentGame.getWindowManager().cursorHide();
    }

    public void Update(double elapsedTime) {
        int mouseX = parentGame.getInputController().InputEvent.getMouseX();
        int mouseY = parentGame.getInputController().InputEvent.getMouseY();
        float cursorX = mouseX;
        float cursorY = mouseY;
        // Linear interpolation
        if (smoothCursor) {
            cursorX = (float) (cursorPosition.getX() + (mouseX - cursorX) * elapsedTime);
            cursorY = (float) (cursorPosition.getY() + (mouseY - cursorY) * elapsedTime);
        }
        cursorPosition.set(cursorX, cursorY);

        checkCursorCollisions(cursorX, cursorY);

        updateCursor();
    }

    /**
     * Changes the cursor depending on the HoverComponent bellow
     * @param cursorX The current cursor X
     * @param cursorY The current cursor Y
     */
    protected void checkCursorCollisions(float cursorX, float cursorY) {
        boolean hoveringSomething = false;

        for (int i = 0; i < hoverEntityList.size() && !hoveringSomething; i++) {
            IEntity entity = hoverEntityList.get(i);
            PositionComponent positionComponent = (PositionComponent) parentGame.ComponentRegister.get(entity, "position_component");
            HoverComponent hoverComponent = (HoverComponent) parentGame.ComponentRegister.get(entity, "hover_component");

            float offsetX, offsetY;
            offsetX = 0;
            offsetY = 0;

            // This is in case the whole window has a hover
            // Hitbox instead. Then it doesn't need a position
            // Component
            if (positionComponent != null) {
                offsetX = positionComponent.getX();
                offsetY = positionComponent.getY();
            }

            // Check if the cursor is within bounds
            if (Util.isInBounds((int) cursorX, (int) cursorY, hoverComponent.getHitBoxX(), hoverComponent.getHitBoxY())) {
                currentCursor = hoverComponent.getHoverCursor();
                hoveringSomething = true;
            }
        }

        if (!hoveringSomething) {
            currentCursor = getDefault();
        }
    }

    public Cursors getDefault() { return cursorDefault; }
    public void setDefault(Cursors cursors) { cursorDefault = cursors; }

    public void notify(Object data) {
        // This listens for any Hover Entities added
        if (data instanceof EntityManager.EntityModifiedEvent) {
            EntityManager.EntityModifiedEvent eventData = (EntityManager.EntityModifiedEvent) data;
            String componentType = eventData.getComponent().getType();
            if (componentType == "hover_component") updateFilter();
        }
        // This listens to see if all Entities were cleared,
        // if so, it creates a new cursor drawable Entity
        if (data instanceof EntityManager.EntitiesClearedEvent){
            EntityManager.EntitiesClearedEvent eventData = (EntityManager.EntitiesClearedEvent) data;
            createCursor();
            updateFilter();
        }
    }

    /**
     * Creates a drawable cursor Entity
     */
    private void createCursor(){
        cursorEntity = new Entity();
        parentGame.getEntityManager().addEntity(cursorEntity);

        cursorPosition = (PositionComponent) parentGame.getComponentRegister().newComponent(new PositionComponent());
        cursorDrawable = (DrawableComponent) parentGame.getComponentRegister().newComponent(new DrawableComponent());

        currentCursor = Cursors.Arrow;
        cursorDrawable.setTextureID(0);
        cursorDrawable.setFromSheet(true);
        cursorDrawable.setTileX(0);
        cursorDrawable.setTileY(0);
        cursorDrawable.setOrder(100);

        parentGame.getEntityManager().addComponent(cursorEntity, cursorPosition);
        parentGame.getEntityManager().addComponent(cursorEntity, cursorDrawable);
    }

    /**
     * Updates the cursor's sprite depending on the Hover
     * Component's hover cursor
     */
    public void updateCursor(){
        switch (currentCursor){
            case Arrow:
                cursorDrawable.setTile(0, 0);
                break;
            case Pointer:
                cursorDrawable.setTile(1, 0);
                break;
            case Attack:
                cursorDrawable.setTile(1, 1);
                break;
            // There is no other cursors, don't check the cursor sheet
        }
    }

    protected void updateFilter(){
        hoverEntityList = parentGame.getEntityManager().filter("hover_component");
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

    /**
     * List of possible Cursor types
     */
    public enum Cursors{
        Arrow, Pointer, Attack
    }
}

