package com.spookynebula.battleshipgame.Core.components;

import com.spookynebula.battleshipgame.Core.CursorSystem;
import com.spookynebula.battleshipgame.ECS.Component;

public class HoverComponent extends Component {
    protected CursorSystem.Cursors hoverCursor;
    protected int hitBoxX, hitBoxY;

    public HoverComponent(){
        enabled = true;
        type = "hover_component";
        ID = 0;

        hoverCursor = CursorSystem.Cursors.Arrow;
        hitBoxX = 0;
        hitBoxY = 0;
    }

    public CursorSystem.Cursors getHoverCursor() {
        return hoverCursor;
    }
    public void setHoverCursor(CursorSystem.Cursors newHoverCursor) {
        hoverCursor = newHoverCursor;
    }

    public int getHitBoxX() {
        return hitBoxX;
    }
    public void setHitBoxX(int newHitBoxX) {
        hitBoxX = newHitBoxX;
    }
    public int getHitBoxY() {
        return hitBoxY;
    }
    public void setHitBoxY(int newHitBoxY) {
        hitBoxY = newHitBoxY;
    }
    public void setHitbox(int newX, int newY) { setHitBoxX(newX); setHitBoxY(newY); }

    @Override
    public String toString() {
        return "PositionComponent{" +
                "enabled=" + enabled +
                ", type='" + type + '\'' +
                ", ID=" + ID +
                ", hoverCursor=" + hoverCursor +
                ", hitBoxX=" + hitBoxX +
                ", hitBoxY=" + hitBoxY +
                '}';
    }
}
