package com.spookynebula.battleshipgame.game.components;

import com.spookynebula.battleshipgame.Core.Util;
import com.spookynebula.battleshipgame.ECS.Component;

import java.util.Arrays;

public class GridComponent extends Component {
    private int gridWidth, gridHeight;
    private int tileWidth, tileHeight;
    private int tilePaddingX, tilePaddingY;
    private TileState[] tiles;
    private int selectedTileIndex;
    private boolean hidden;
    private int playerID;

    public GridComponent() {
        enabled = true;
        type = "grid_component";
        ID = 0;

        gridWidth = 9;
        gridHeight = 9;
        tileWidth = 8;
        tileHeight = 8;
        tilePaddingX = 2;
        tilePaddingY = 2;
        selectedTileIndex = -1;
        hidden = true;
        playerID = 0;

        tiles = new TileState[gridWidth * gridHeight];
        clear();
    }

    private void clear() { Arrays.fill(tiles, TileState.Null); }

    public TileState get(int index) {
        return tiles[index];
    }

    public TileState get(int x, int y) {
        return get(x + y * gridWidth);
    }

    public int getGridWidth() { return gridWidth; }
    public void setGridWidth(int newGridWidth) { gridWidth = newGridWidth; }
    public int getGridHeight() {  return gridHeight; }
    public void setGridHeight(int newGridHeight) {  gridHeight = newGridHeight; }

    public int getTileWidth() {  return tileWidth; }
    public void setTileWidth(int newTileWidth) {  tileWidth = newTileWidth; }
    public int getTileHeight() {  return tileHeight; }
    public void setTileHeight(int newTileHeight) {  tileHeight = newTileHeight; }
    public int getTilePaddingX() {  return tilePaddingX; }
    public void setTilePaddingX(int newTilePaddingX) {  tilePaddingX = newTilePaddingX; }
    public int getTilePaddingY() {  return tilePaddingY; }
    public void setTilePaddingY(int newTilePaddingY) {  tilePaddingY = newTilePaddingY; }

    public TileState[] getTiles() {  return tiles; }
    public void setTiles(TileState[] newTiles) {  tiles = newTiles; }

    public void set(int tileIndex, TileState newTileState) { tiles[tileIndex] = newTileState; }
    public void set(int tileX, int tileY, TileState newTileState) { set(tileX + tileY * gridWidth, newTileState); }

    public boolean isHidden() {  return hidden; }
    public void setHidden(boolean newState) {  hidden = newState; }

    public int getPlayerID() { return playerID; }
    public void setPlayerID(int newPlayerID) { playerID = newPlayerID; }

    public int getSelectedTileIndex() { return selectedTileIndex; }
    public void setSelectedTileIndex(int newSelectedTileIndex) { selectedTileIndex = newSelectedTileIndex; }
    public void clearSelectedTile() { selectedTileIndex = -1; }

    public boolean placeBoat(int tileIndex, BoatComponent boatComponent) {
        Util.Position boatPosition =
                Util.getPositionFromIndexOfArray(tileIndex, gridWidth, gridHeight);

        int sizeX = boatComponent.getSizeX();
        int sizeY = boatComponent.getSizeY();

        if (boatComponent.isVertical()) {
            sizeY = boatComponent.getSizeX();
            sizeX = boatComponent.getSizeY();
        }

        if (!Util.isInBounds(
                boatPosition.getX(),
                boatPosition.getY(),
                sizeX,
                sizeY,
                gridWidth,
                gridHeight))
        {
            return false;
        }

        if (collidesWithOtherBoats(boatPosition, boatComponent)) {
            return false;
        }

        for (int i = 0; i < boatComponent.getSizeY() * boatComponent.getSizeX(); i++) {
            int offsetX = 0;
            int offsetY = 0;
            if (boatComponent.isVertical()) {
                offsetY = i;
            } else {
                offsetX = i;
            }

            GridComponent.TileState tileState = getTileStateFromBoatTileIndex(i, boatComponent);;

            set(boatPosition.getX() + offsetX, boatPosition.getY() + offsetY, tileState);
        }

        return true;
    }


    private TileState getTileStateFromBoatTileIndex(int tileIndex, BoatComponent boatComponent) {
        if (boatComponent.isVertical()){
            if (tileIndex == 0) return TileState.BoatTop;
            if (tileIndex == boatComponent.getSizeX() - 1) return TileState.BoatBottom;
            return TileState.BoatMiddleV;
        } else {
            if (tileIndex == 0) return TileState.BoatLeft;
            if (tileIndex == boatComponent.getSizeX() - 1) return TileState.BoatRight;
            return TileState.BoatMiddleH;
        }
    }

    private boolean collidesWithOtherBoats(Util.Position boatPosition, BoatComponent boatComponent) {
        for (int i = 0; i < boatComponent.getSizeY() * boatComponent.getSizeX(); i++) {
            int offsetX = 0;
            int offsetY = 0;
            if (boatComponent.isVertical()) {
                offsetY = i;
            } else {
                offsetX = i;
            }
            GridComponent.TileState tileState = get(boatPosition.getX() + offsetX, boatPosition.getY() + offsetY);
            if (tileState.isBoat()) {
                return true;
            }
        }
        return false;
    }

    public void select(int tileIndex) {
        setSelectedTileIndex(tileIndex);
    }

    public void select(int x, int y) {
        setSelectedTileIndex(x + y * gridWidth);
    }

    public boolean hasLost() {
        return Arrays.stream(tiles).noneMatch(TileState::isBoat);
    }

    @Override
    public String toString() {
        return "GridComponent{" +
                "enabled=" + enabled +
                ", type='" + type + '\'' +
                ", ID=" + ID +
                ", gridWidth=" + gridWidth +
                ", gridHeight=" + gridHeight +
                ", tileWidth=" + tileWidth +
                ", tileHeight=" + tileHeight +
                ", tilePaddingX=" + tilePaddingX +
                ", tilePaddingY=" + tilePaddingY +
                ", tiles=" + Arrays.toString(tiles) +
                ", selectedTileIndex=" + selectedTileIndex +
                ", hidden=" + hidden +
                ", playerID=" + playerID +
                '}';
    }

    public enum TileState{
        // Hehe susus amogus
        Null, Miss, Destroyed, MarkedEmpty, MarkedSus,
        BoatLeft(true), BoatMiddleV(true),
        BoatMiddleH(true), BoatRight(true),
        BoatTop(true), BoatBottom(true);

        private final boolean boat;

        TileState(boolean isBoat){
            boat = isBoat;
        }

        TileState(){
            boat = false;
        }

        public boolean isBoat() {
            return boat;
        }
    }
}
