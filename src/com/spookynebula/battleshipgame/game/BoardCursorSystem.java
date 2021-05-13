package com.spookynebula.battleshipgame.game;

import com.spookynebula.battleshipgame.Core.CursorSystem;
import com.spookynebula.battleshipgame.Core.components.HoverComponent;
import com.spookynebula.battleshipgame.Core.components.PositionComponent;
import com.spookynebula.battleshipgame.ECS.EntityManager;
import com.spookynebula.battleshipgame.ECS.IEntity;
import com.spookynebula.battleshipgame.GameContainer;
import com.spookynebula.battleshipgame.game.components.DrawableButtonComponent;

public class BoardCursorSystem extends CursorSystem {
    public BoardCursorSystem(GameContainer gameContainer) {
        super(gameContainer);
    }

    @Override
    protected void checkCursorCollisions(float cursorX, float cursorY) {
        boolean hoveringSomething = false;

        for (int i = 0; i < hoverEntityList.size() && !hoveringSomething; i++) {
            IEntity entity = hoverEntityList.get(i);
            PositionComponent positionComponent =
                    (PositionComponent) parentGame.ComponentRegister.get(entity, "position_component");
            DrawableButtonComponent drawableButtonComponent =
                    (DrawableButtonComponent) parentGame.ComponentRegister.get(entity, "drawable_button_component");

            float offsetX, offsetY;
            offsetX = 0;
            offsetY = 0;

            if (positionComponent != null) {
                offsetX = positionComponent.getX();
                offsetY = positionComponent.getY();
            }

            if (offsetX < cursorX && cursorX < offsetX + drawableButtonComponent.getHitBoxX()){
                if (offsetY < cursorY && cursorY < offsetY + drawableButtonComponent.getHitBoxY()){
                    currentCursor = drawableButtonComponent.getHoverCursor();
                    hoveringSomething = true;
                }
            }
        }

        if (!hoveringSomething) {
            currentCursor = getDefault();
        }
    }

    @Override
    protected void updateFilter(){
        hoverEntityList = parentGame.getEntityManager().filter("drawable_button_component");
    }

    @Override
    public void notify(Object data) {
        super.notify(data);
        if (data instanceof EntityManager.EntityModifiedEvent) {
            EntityManager.EntityModifiedEvent eventData = (EntityManager.EntityModifiedEvent) data;
            String componentType = eventData.getComponent().getType();
            if (componentType == "drawable_button_component") updateFilter();
        }
    }
}
