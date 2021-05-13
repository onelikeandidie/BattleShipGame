package com.spookynebula.battleshipgame.game;

import com.spookynebula.battleshipgame.Core.DefaultRenderSystem;
import com.spookynebula.battleshipgame.Core.components.PositionComponent;
import com.spookynebula.battleshipgame.Core.gfx.Image;
import com.spookynebula.battleshipgame.Core.gfx.SpriteFont;
import com.spookynebula.battleshipgame.ECS.*;
import com.spookynebula.battleshipgame.GameContainer;
import com.spookynebula.battleshipgame.game.components.DrawableButtonComponent;
import com.spookynebula.battleshipgame.game.components.GridComponent;
import com.spookynebula.battleshipgame.game.components.ModalComponent;
import com.spookynebula.battleshipgame.game.components.TextDisplayComponent;

import java.util.ArrayList;
import java.util.List;

public class BoardRenderSystem extends DefaultRenderSystem implements IInitSystem, IDrawSystem, ISubscriber {
    private List<IEntity> boardEntityList;
    private List<IEntity> drawableButtonEntityList;
    private List<IEntity> textDisplayEntityList;
    private List<IEntity> modalEntityList;

    private Image modalImage;
    private Image selectedTileImage;

    public BoardRenderSystem(GameContainer gameContainer){
        super(gameContainer);
        boardEntityList = new ArrayList<IEntity>();
        drawableButtonEntityList = new ArrayList<IEntity>();
        modalEntityList = new ArrayList<IEntity>();
        textDisplayEntityList = new ArrayList<IEntity>();
    }

    @Override
    public void Init() {
        super.Init();

        modalImage = parentGame.getContentLoader().getTexture(2);
        selectedTileImage = parentGame.getContentLoader().getTexture(3);
    }

    @Override
    public void Draw() {
        clear();
        drawBackgroundBoard();

        drawBoard();
        drawButtons();
        drawModals();
        drawTextDisplayEntities();
        drawEntities();

        sendFramePixelData();
    }

    private void drawModals() {
        for (int i = 0; i < modalEntityList.size(); i++) {
            IEntity entity = modalEntityList.get(i);
            PositionComponent positionComponent =
                    (PositionComponent) parentGame.ComponentRegister.get(entity, "position_component");
            ModalComponent modalComponent =
                    (ModalComponent) parentGame.ComponentRegister.get(entity, "modal_component");

            int offsetX = (int) positionComponent.getX();
            int offsetY = (int) positionComponent.getY();

            drawImage(modalImage, offsetX, offsetY);
            
            drawModalText(modalComponent,
                    parentGame.getContentLoader().getFont(0),
                    offsetX, offsetY,
                    10, 18);
        }
    }

    private void drawModalText(
            ModalComponent modalComponent,
            SpriteFont spriteFont,
            int modalOffsetX, 
            int modalOffsetY, 
            int textStartX, 
            int textStartY) 
    {
        int currentLineOffsetY = textStartY;
        for (int i = 0; i < modalComponent.getTextLineCount(); i++) {
            drawText(modalComponent.getTextLine(i), spriteFont,
                    modalOffsetX + textStartX, modalOffsetY + currentLineOffsetY);
            currentLineOffsetY += spriteFont.getSymbolHeight();
        }
    }

    private void drawTextDisplayEntities() {
        for (int i = 0; i < textDisplayEntityList.size(); i++) {
            IEntity entity = textDisplayEntityList.get(i);
            PositionComponent positionComponent =
                    (PositionComponent) parentGame.ComponentRegister.get(entity, "position_component");
            TextDisplayComponent textDisplayComponent =
                    (TextDisplayComponent) parentGame.ComponentRegister.get(entity, "text_display_component");

            int offsetX = (int) positionComponent.getX();
            int offsetY = (int) positionComponent.getY();

            SpriteFont font = parentGame.getContentLoader().getFont(0);

            drawText(textDisplayComponent.getActualText(), font, offsetX, offsetY);
        }
    }

    private void drawButtons() {
        for (int i = 0; i < drawableButtonEntityList.size(); i++) {
            IEntity entity = drawableButtonEntityList.get(i);
            PositionComponent positionComponent =
                    (PositionComponent) parentGame.ComponentRegister.get(entity, "position_component");
            DrawableButtonComponent drawableButtonComponent =
                    (DrawableButtonComponent) parentGame.ComponentRegister.get(entity, "drawable_button_component");

            int offsetX = (int) positionComponent.getX();
            int offsetY = (int) positionComponent.getY();

            int textureID = drawableButtonComponent.getTextureID();
            Image buttonTexture = parentGame.getContentLoader().getTexture(textureID,
                    drawableButtonComponent.getStateTileX(1),
                    drawableButtonComponent.getStateTileY(1));
            if (drawableButtonComponent.isClickable()) {
                buttonTexture = parentGame.getContentLoader().getTexture(textureID,
                        drawableButtonComponent.getStateTileX(0),
                        drawableButtonComponent.getStateTileY(0));
            }

            drawImage(buttonTexture, offsetX, offsetY);
        }
    }

    private void drawBoard() {
        // Debug, to know how many entities were drawn this frame
        int entityCount = 0;

        // "i < boardEntityList.size() && i < 2" because there can be only 2 boards on screen
        for (int boardIndex = 0; boardIndex < boardEntityList.size() && boardIndex < 2; boardIndex++) {
            IEntity entity = boardEntityList.get(boardIndex);
            PositionComponent positionComponent =
                    (PositionComponent) parentGame.ComponentRegister.get(entity, "position_component");
            GridComponent gridComponent = (GridComponent) parentGame.ComponentRegister.get(entity, "grid_component");

            // Check if it was a hidden grid (for the CPU for example)
            if (!gridComponent.isHidden()){
                // Draw tiles
                drawBoardTiles(boardIndex, positionComponent, gridComponent);
            }
        }
    }

    private void drawBoardTiles(int boardIndex, PositionComponent positionComponent, GridComponent gridComponent) {
        for (int tileIndex = 0; tileIndex < gridComponent.getTiles().length; tileIndex++) {
            GridComponent.TileState tileState = gridComponent.get(tileIndex);
            // Check if the tile has any state that is not null (meaning nothing in the tile)
            if (tileState != GridComponent.TileState.Null || tileIndex == gridComponent.getSelectedTileIndex()) {
                int x = (int) positionComponent.getX();
                int y = 0;
                int rowWidthInPixels = (gridComponent.getGridWidth()) * (gridComponent.getTileWidth() + gridComponent.getTilePaddingX());
                int maxX = (int) (positionComponent.getX() + rowWidthInPixels);
                x += tileIndex * (gridComponent.getTileWidth() + gridComponent.getTilePaddingX());
                while (x >= maxX) {
                    x = x - rowWidthInPixels;
                    y++;
                }
                y = (int) (positionComponent.getY() + y * (gridComponent.getTileHeight() + gridComponent.getTilePaddingY()));
                if (tileState != GridComponent.TileState.Null) {
                    drawTile(tileState, x, y);
                }
                if (tileIndex == gridComponent.getSelectedTileIndex()) {
                    drawSelectedTile(x, y);
                }
            }
        }
    }

    private void drawSelectedTile(int x, int y) {
        drawImage(selectedTileImage, x, y);
    }

    private void drawTile(GridComponent.TileState tileState, int x, int y){
        Image tileTexture;
        switch (tileState){
            case MarkedEmpty:
                tileTexture = parentGame.getContentLoader().getTexture(1, 0, 0);
                break;
            case MarkedSus:
                tileTexture = parentGame.getContentLoader().getTexture(1, 1, 0);
                break;
            case Miss:
                tileTexture = parentGame.getContentLoader().getTexture(1, 0, 1);
                break;
            case Destroyed:
                tileTexture = parentGame.getContentLoader().getTexture(1, 1, 1);
                break;
            case BoatLeft:
                tileTexture = parentGame.getContentLoader().getTexture(1, 0, 3);
                break;
            case BoatMiddleH:
                tileTexture = parentGame.getContentLoader().getTexture(1, 0, 2);
                break;
            case BoatRight:
                tileTexture = parentGame.getContentLoader().getTexture(1, 1, 2);
                break;
            case BoatTop:
                tileTexture = parentGame.getContentLoader().getTexture(1, 0, 4);
                break;
            case BoatMiddleV:
                tileTexture = parentGame.getContentLoader().getTexture(1, 1, 3);
                break;
            case BoatBottom:
                tileTexture = parentGame.getContentLoader().getTexture(1, 1, 4);
                break;
            default:
                return;
        }
        drawImage(tileTexture, x, y);
    }

    @Override
    public void notify(Object data) {
        super.notify(data);

        if (data instanceof EntityManager.EntityModifiedEvent) {
            EntityManager.EntityModifiedEvent eventData = (EntityManager.EntityModifiedEvent) data;
            String componentType = eventData.getComponent().getType();
            if (componentType == "grid_component" ||
                componentType == "drawable_button_component" ||
                componentType == "text_display_component" ||
                componentType == "modal_component")
                updateFilter();
        }
    }

    @Override
    protected void updateFilter() {
        boardEntityList = parentGame.getEntityManager().filter("grid_component");
        drawableButtonEntityList = parentGame.getEntityManager().filter("drawable_button_component", "position_component");
        modalEntityList = parentGame.getEntityManager().filter("modal_component", "position_component");
        textDisplayEntityList = parentGame.getEntityManager().filter("text_display_component", "position_component");
        super.updateFilter();
    }

    private void drawBackgroundBoard(){
        Image fleetBoardTexture = parentGame.getContentLoader().getTexture(0);
        drawImage(fleetBoardTexture);
        Image guessBoardTexture = parentGame.getContentLoader().getTexture(1);
        drawImage(guessBoardTexture, 128, 0);
    }
}
