package com.spookynebula.battleshipgame.game.components;

import com.spookynebula.battleshipgame.ECS.Component;

public class BoatComponent extends Component {
    int sizeX, sizeY;
    boolean vertical;

    public BoatComponent() {
        enabled = true;
        type = "boat_component";
        ID = 0;

        sizeX = 0;
        sizeY = 0;
        vertical = true;
    }

    public int getSizeX() { return sizeX; }
    public void setSizeX(int newSizeX) { sizeX = newSizeX; }
    public int getSizeY() { return sizeY; }
    public void setSizeY(int newSizeY) { sizeY = newSizeY; }

    public boolean isVertical() { return vertical; }
    public void setVertical(boolean newState) { vertical = newState; }

    @Override
    public String toString() {
        return "BoatComponent{" +
                "enabled=" + enabled +
                ", type='" + type + '\'' +
                ", ID=" + ID +
                ", sizeX=" + sizeX +
                ", sizeY=" + sizeY +
                ", vertical=" + vertical +
                '}';
    }
}
