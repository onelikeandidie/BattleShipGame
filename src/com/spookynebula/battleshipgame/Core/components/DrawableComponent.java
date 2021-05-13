package com.spookynebula.battleshipgame.Core.components;

import com.spookynebula.battleshipgame.ECS.Component;

public class DrawableComponent extends Component {
    private int textureID;
    private int order;
    private boolean isFromSheet;
    private int tileX;
    private int tileY;

    public DrawableComponent(){
        enabled = true;
        type = "drawable_component";
        ID = 0;

        textureID = 0;
        isFromSheet = false;
        tileX = 0;
        tileY = 0;
        order = 0;
    }

    public void setTextureID(int newTextureID) { textureID = newTextureID; }
    public int getTextureID() { return textureID; }

    public void setOrder(int newOrder) { order = newOrder; }
    public int getOrder() { return order; }

    public boolean isFromSheet() { return isFromSheet; }
    public void setFromSheet(boolean fromSheet) { isFromSheet = fromSheet; }

    public int getTileX() { return tileX; }
    public void setTileX(int newTileX) { tileX = newTileX; }

    public int getTileY() { return tileY; }
    public void setTileY(int newTileY) { tileY = newTileY; }

    public void setTile(int newTileX, int newTileY) { tileX = newTileX; tileY = newTileY; }

    @Override
    public String toString() {
        if (isFromSheet){
            return "DrawableComponent{" +
                    "enabled=" + enabled +
                    ", type='" + type + '\'' +
                    ", ID=" + ID +
                    ", sheetID=" + textureID +
                    ", tileX=" + tileX +
                    ", tileY=" + tileY +
                    ", order=" + order +
                    '}';
        } else {
            return "DrawableComponent{" +
                    "enabled=" + enabled +
                    ", type='" + type + '\'' +
                    ", ID=" + ID +
                    ", textureID=" + textureID +
                    ", order=" + order +
                    '}';
        }
    }
}
