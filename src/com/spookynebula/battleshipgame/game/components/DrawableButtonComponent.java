package com.spookynebula.battleshipgame.game.components;

import com.spookynebula.battleshipgame.Core.components.HoverComponent;

import java.util.Arrays;

public class DrawableButtonComponent extends HoverComponent {
    private int textureID;
    private int[] stateTileX;
    private int[] stateTileY;
    private boolean clickable;

    public DrawableButtonComponent() {
        enabled = true;
        type = "drawable_button_component";
        ID = 0;

        clickable = true;
        textureID = 0;
        stateTileX = new int[2];
        stateTileY = new int[2];
    }

    public boolean isClickable() { return clickable; }
    public void setClickable(boolean newClickability) { clickable = newClickability; }

    public void setSpriteSheetID(int newSpriteSheetID) { textureID = newSpriteSheetID; }
    public int getTextureID() { return textureID; }

    public void setStateTileX(int stateIndex, int newTileX) { stateTileX[stateIndex] = newTileX; }
    public void setStateTileY(int stateIndex, int newTileY) { stateTileY[stateIndex] = newTileY; }
    public void setStateTile(int stateIndex, int newTileX, int newTileY) {
        setStateTileX(stateIndex, newTileX); setStateTileY(stateIndex, newTileY);
    }

    public int getStateTileX(int stateIndex) { return stateTileX[stateIndex]; }
    public int getStateTileY(int stateIndex) { return stateTileY[stateIndex]; }

    @Override
    public String toString() {
        return "DrawableButtonComponent{" +
                "hoverCursor=" + hoverCursor +
                ", hitBoxX=" + hitBoxX +
                ", hitBoxY=" + hitBoxY +
                ", enabled=" + enabled +
                ", type='" + type + '\'' +
                ", ID=" + ID +
                ", textureID=" + textureID +
                ", tileX=" + Arrays.toString(stateTileX) +
                ", tileY=" + Arrays.toString(stateTileY) +
                ", clickable=" + clickable +
                '}';
    }
}
