package com.spookynebula.battleshipgame.game;

import com.spookynebula.battleshipgame.Core.CursorSystem;
import com.spookynebula.battleshipgame.Core.InputController;
import com.spookynebula.battleshipgame.Core.components.PositionComponent;
import com.spookynebula.battleshipgame.Core.gfx.Image;
import com.spookynebula.battleshipgame.ECS.*;
import com.spookynebula.battleshipgame.GameContainer;
import com.spookynebula.battleshipgame.game.components.*;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameStateSystem implements IInitSystem, ISubscriber {
    private GameContainer parentGame;
    private boolean enabled;

    private Entity playerFleetBoard;
    private Entity playerGuessBoard;
    private Entity cpuFleetBoard;
    private Entity cpuGuessBoard;
    private Entity[] buttons;
    private Entity textDisplay;

    private List<IEntity> boatEntityList;

    private GameState gameState;
    private AmazingCPU AI;

    private Image modalImage;
    private int windowWidth;

    private boolean tutorialDismissed;

    public GameStateSystem(GameContainer gameContainer) {
        parentGame = gameContainer;
        buttons = new Entity[2];

        enabled = true;
        tutorialDismissed = false;
        gameState = GameState.Tutorial;
        boatEntityList = new ArrayList<IEntity>();
    }

    public void Init() {
        modalImage = parentGame.getContentLoader().getTexture(2);
        windowWidth = parentGame.getWindowManager().getWindowWidth();

        playerFleetBoard = createBoardEntity(0, false, 19, 21);
        playerGuessBoard = createBoardEntity(0, false, 128 + 19, 12);
        cpuFleetBoard = createBoardEntity(1, true);
        cpuGuessBoard = createBoardEntity(1, true);

        buttons[0] = createButtonEntity(
                2,
                new int[]{0,1},
                new int[]{0,0},
                128 + 87, 107,
                CursorSystem.Cursors.Pointer);
        buttons[1] = createButtonEntity(
                2,
                new int[]{0,1},
                new int[]{1,1},
                128 + 97, 107,
                CursorSystem.Cursors.Attack);

        textDisplay = createTextDisplay(128+18, 108);

        displayTutorial();

        createBoats();

        changeGameState(GameState.Tutorial);

        AI = new AmazingCPU(parentGame, cpuFleetBoard, cpuGuessBoard);
    }

    private void displayTutorial() {
        createModalEntity("Welcome to Naval Battle!");
        createModalEntity("The board on the left is your fleet board");
        createModalEntity("The board on the right is your guess board");
        createModalEntity("/ on the guess board to mark tile ] as empty");
        createModalEntity("- on the guess board to mark tile [ as suspicious");
        createModalEntity("But first you must place your fleet!");
        createModalEntity("- to rotate a ship");
        createModalEntity("/ to place a ship");
        createModalEntity("You will be playing vs CPU!");
        createModalEntity("To end your turn press the arrow button");
        createModalEntity("To guess, press the crosshair button");
        createModalEntity("Good Luck!");
    }

    private void createBoats() {
        createBoatEntity(2, 1);
        createBoatEntity(2, 1);
        createBoatEntity(2, 1);
        createBoatEntity(3, 1);
        createBoatEntity(3, 1);
        createBoatEntity(4, 1);
    }

    private Entity createTextDisplay(int x, int y) {
        // Create the test Entity and add it to the EntityManager
        Entity textDisplayEntity = new Entity();
        parentGame.getEntityManager().addEntity(textDisplayEntity);
        // Create some components
        PositionComponent positionComponent = (PositionComponent) parentGame.getComponentRegister().newComponent(new PositionComponent());
        positionComponent.set(x, y);
        TextDisplayComponent textDisplayComponent = (TextDisplayComponent) parentGame.getComponentRegister().newComponent(new TextDisplayComponent());
        textDisplayComponent.setMaxCharX(16);
        textDisplayComponent.setMaxCharY(1);
        textDisplayComponent.setText("textDisplayComponent");
        textDisplayComponent.recalculateText();
        // Add the components
        parentGame.getEntityManager().addComponent(textDisplayEntity, textDisplayComponent);
        parentGame.getEntityManager().addComponent(textDisplayEntity, positionComponent);

        return textDisplayEntity;
    }

    private Entity createBoatEntity(int boatSizeX, int boatSizeY) {
        // Create the test Entity and add it to the EntityManager
        Entity boatEntity = new Entity();
        parentGame.getEntityManager().addEntity(boatEntity);
        // Create some components
        BoatComponent boatComponent = (BoatComponent) parentGame.getComponentRegister().newComponent(new BoatComponent());
        boatComponent.setSizeX(boatSizeX);
        boatComponent.setSizeY(boatSizeY);
        // Add the components
        parentGame.getEntityManager().addComponent(boatEntity, boatComponent);

        return boatEntity;
    }

    private Entity createBoardEntity(int playerID, boolean isHidden) {
        return createBoardEntity(playerID, isHidden, 0, 0);
    }

    private Entity createBoardEntity(int playerID, boolean isHidden, int x, int y) {
        Entity boardEntity = new Entity();
        parentGame.getEntityManager().addEntity(boardEntity);

        PositionComponent positionComponent = (PositionComponent) parentGame.getComponentRegister().newComponent(new PositionComponent());
        positionComponent.set(x, y);
        GridComponent gridComponent =
                (GridComponent) parentGame.getComponentRegister().newComponent(new GridComponent());
        gridComponent.setHidden(isHidden);
        gridComponent.setPlayerID(playerID);
        parentGame.getEntityManager().addComponent(boardEntity, positionComponent);
        parentGame.getEntityManager().addComponent(boardEntity, gridComponent);

        return boardEntity;
    }

    private Entity createModalEntity(String modalText) {
        // Create the test Entity and add it to the EntityManager
        Entity modalEntity = new Entity();
        parentGame.getEntityManager().addEntity(modalEntity);
        // Create some components
        PositionComponent positionComponent = (PositionComponent) parentGame.getComponentRegister().newComponent(new PositionComponent());
        positionComponent.set((windowWidth / 2) - (modalImage.getWidth() / 2), -modalImage.getHeight());
        ModalComponent modalComponent = (ModalComponent) parentGame.getComponentRegister().newComponent(new ModalComponent());
        modalComponent.setModalMessage(modalText);
        modalComponent.recalculateLines(15, 6);
        // Add the components
        parentGame.getEntityManager().addComponent(modalEntity, positionComponent);
        parentGame.getEntityManager().addComponent(modalEntity, modalComponent);

        return modalEntity;
    }

    private Entity createButtonEntity(
            int spriteSheetID,
            int[] tileX, int[] tileY,
            int x, int y,
            CursorSystem.Cursors cursors)
    {
        // Create the test Entity and add it to the EntityManager
        Entity buttonEntity = new Entity();
        parentGame.getEntityManager().addEntity(buttonEntity);
        // Create some components
        PositionComponent positionComponent = (PositionComponent) parentGame.getComponentRegister().newComponent(new PositionComponent());
        positionComponent.set(x, y);
        DrawableButtonComponent drawableButtonComponent = (DrawableButtonComponent) parentGame.getComponentRegister().newComponent(new DrawableButtonComponent());
        drawableButtonComponent.setHitbox(10,10);
        drawableButtonComponent.setHoverCursor(cursors);
        drawableButtonComponent.setSpriteSheetID(spriteSheetID);
        drawableButtonComponent.setStateTile(0, tileX[0], tileY[0]);
        drawableButtonComponent.setStateTile(1, tileX[1], tileY[1]);
        drawableButtonComponent.setClickable(false);
        // Add the components
        parentGame.getEntityManager().addComponent(buttonEntity, positionComponent);
        parentGame.getEntityManager().addComponent(buttonEntity, drawableButtonComponent);

        return buttonEntity;
    }

    private void setTextDisplayText(String newText) {
        TextDisplayComponent textDisplayComponent =
                (TextDisplayComponent) parentGame.ComponentRegister.get(textDisplay, "text_display_component");
        textDisplayComponent.setText(newText);
        textDisplayComponent.recalculateText();
    }

    private void setButtonClickable(int index, boolean newState) {
        Entity entity = buttons[index];
        DrawableButtonComponent buttonComponent =
                (DrawableButtonComponent) parentGame.ComponentRegister.get(entity, "drawable_button_component");

        buttonComponent.setClickable(newState);
    }

    private void controlPlayer(InputController.InputEvent inputEvent) {
        switch (gameState){
            case Tutorial:
                break;
            case PlayerOneFleetPlacement:
                processInputForFleetPlacement(inputEvent);
                break;
            case PlayerOneGuess:
                processInputForPlayerGuessTurn(inputEvent);
                break;
            case PlayerOneTurn:
                processInputForPlayerAttackTurn(inputEvent);
                break;
            case PlayerTwoFleetPlacement:
                AI.placeBoats();
                changeGameState(GameState.PlayerOneGuess);
                break;
            case PlayerTwoGuess:
                AI.think();
                changeGameState(GameState.PlayerOneTurn);
                break;
            case PlayerTwoTurn:
                AI.attack();
                processAIAttackTurn();
                changeGameState(GameState.AttackTurnsComplete);
                break;
            case Tie:
            case PlayerOneLost:
            case PlayerTwoLost:
                processInputForGameOver(inputEvent);
                break;
        }
        updateTextDisplay();
    }

    private void processInputForFleetPlacement(InputController.InputEvent inputEvent) {
        if (boatEntityList.isEmpty()){
            System.out.println("Boat list is empty, proceeding to computer placement.");
            changeGameState(GameState.PlayerTwoFleetPlacement);
            return;
        }

        IEntity boatEntity = boatEntityList.get(0);
        BoatComponent boatComponent =
                (BoatComponent) parentGame.ComponentRegister.get(boatEntity, "boat_component");

        if (inputEvent.isMouseKeyDown(MouseEvent.BUTTON1)){

            int tileIndex = getTileIndexFromMousePos(inputEvent, 0);

            GridComponent gridComponent =
                    (GridComponent) parentGame.ComponentRegister.get(playerFleetBoard, "grid_component");


            // tileIndex is -1 if the cursor was not on a tile
            if (tileIndex >= 0) {
                boolean boatWasPlaced = gridComponent.placeBoat(tileIndex, boatComponent);
                if (boatWasPlaced) parentGame.getEntityManager().removeEntity(boatEntity);
            }
        }

        if (inputEvent.isMouseKeyDown(MouseEvent.BUTTON3)) {
            boatComponent.setVertical(!boatComponent.isVertical());
        }
    }

    private void processInputForPlayerAttackTurn(InputController.InputEvent inputEvent) {
        GridComponent playerGuessGridComponent =
                (GridComponent) parentGame.ComponentRegister.get(playerGuessBoard, "grid_component");
        GridComponent cpuFleetGridComponent =
                (GridComponent) parentGame.ComponentRegister.get(cpuFleetBoard, "grid_component");

        if (inputEvent.isMouseKeyDown(MouseEvent.BUTTON1) || inputEvent.isMouseKeyDown(MouseEvent.BUTTON3)){
            int tileIndex = getTileIndexFromMousePos(inputEvent, 1);

            // tileIndex is -1 if the cursor was not on a tile
            if (tileIndex >= 0) {
                playerGuessGridComponent.select(tileIndex);
                return;
            }
        }

        int attackTileIndex = playerGuessGridComponent.getSelectedTileIndex();
        if (inputEvent.isMouseKeyDown(MouseEvent.BUTTON1) && attackTileIndex > -1) {
            int buttonIndex = getButtonClicked(inputEvent.getMouseX(), inputEvent.getMouseY());

            if (buttonIndex == 1) {
                if (cpuFleetGridComponent.get(attackTileIndex).isBoat()) {
                    playerGuessGridComponent.set(attackTileIndex, GridComponent.TileState.Destroyed);
                    cpuFleetGridComponent.set(attackTileIndex, GridComponent.TileState.Destroyed);
                } else {
                    playerGuessGridComponent.set(attackTileIndex, GridComponent.TileState.Miss);
                    cpuFleetGridComponent.set(attackTileIndex, GridComponent.TileState.Miss);
                }
                playerGuessGridComponent.clearSelectedTile();
                changeGameState(GameState.PlayerTwoTurn);
            }
        }
    }

    private void processInputForPlayerGuessTurn(InputController.InputEvent inputEvent) {
        if (inputEvent.isMouseKeyDown(MouseEvent.BUTTON1) || inputEvent.isMouseKeyDown(MouseEvent.BUTTON3)){
            int tileIndex = getTileIndexFromMousePos(inputEvent, 1);

            GridComponent gridComponent =
                    (GridComponent) parentGame.ComponentRegister.get(playerGuessBoard, "grid_component");

            // tileIndex is -1 if the cursor was not on a tile
            if (tileIndex >= 0) {
                // Check if it was a confirmed hit/not hit
                if (gridComponent.get(tileIndex) == GridComponent.TileState.Destroyed ||
                    gridComponent.get(tileIndex) == GridComponent.TileState.Miss) {
                    return;
                }

                if (inputEvent.isMouseKeyDown(MouseEvent.BUTTON1))
                    gridComponent.set(tileIndex, GridComponent.TileState.MarkedEmpty);
                if (inputEvent.isMouseKeyDown(MouseEvent.BUTTON3))
                    gridComponent.set(tileIndex, GridComponent.TileState.MarkedSus);
                return;
            }
        }

        if (inputEvent.isMouseKeyDown(MouseEvent.BUTTON1)) {
            int buttonIndex = getButtonClicked(inputEvent.getMouseX(), inputEvent.getMouseY());

            if (buttonIndex == 0) {
                changeGameState(GameState.PlayerTwoGuess);
            }
        }
    }

    private void processAIAttackTurn() {
        GridComponent playerFleetGridComponent =
                (GridComponent) parentGame.ComponentRegister.get(playerFleetBoard, "grid_component");
        GridComponent cpuGuessGridComponent =
                (GridComponent) parentGame.ComponentRegister.get(cpuGuessBoard, "grid_component");

        int attackTileIndex = cpuGuessGridComponent.getSelectedTileIndex();

        if (playerFleetGridComponent.get(attackTileIndex).isBoat()) {
            playerFleetGridComponent.set(attackTileIndex, GridComponent.TileState.Destroyed);
            cpuGuessGridComponent.set(attackTileIndex, GridComponent.TileState.Destroyed);
        } else {
            playerFleetGridComponent.set(attackTileIndex, GridComponent.TileState.Miss);
            cpuGuessGridComponent.set(attackTileIndex, GridComponent.TileState.Miss);
        }

        changeGameState(GameState.PlayerTwoTurn);
    }

    private void processInputForGameOver(InputController.InputEvent inputEvent) {
        if (inputEvent.isMouseKeyDown(MouseEvent.BUTTON1)) {
            int buttonIndex = getButtonClicked(inputEvent.getMouseX(), inputEvent.getMouseY());

            if (buttonIndex == 0) {
                System.exit(0);
            }
        }
    }

    private int getButtonClicked(int cursorX, int cursorY) {
        for (int i = 0; i < buttons.length; i++) {
            IEntity entity = buttons[i];
            PositionComponent positionComponent =
                    (PositionComponent) parentGame.ComponentRegister.get(entity, "position_component");
            DrawableButtonComponent buttonComponent =
                    (DrawableButtonComponent) parentGame.ComponentRegister.get(entity, "drawable_button_component");

            float offsetX, offsetY;
            offsetX = 0;
            offsetY = 0;

            if (positionComponent != null) {
                offsetX = positionComponent.getX();
                offsetY = positionComponent.getY();
            }

            if (offsetX < cursorX && cursorX < offsetX + buttonComponent.getHitBoxX()){
                if (offsetY < cursorY && cursorY < offsetY + buttonComponent.getHitBoxY()){
                    return i;
                }
            }
        }
        return -1;
    }

    private int getTileIndexFromMousePos(InputController.InputEvent inputEvent, int requiredGrid) {
        int gridIndex = getGridEntityFromMousePos(inputEvent.getMouseX(), inputEvent.getMouseY());
        if (gridIndex == requiredGrid) {
            Entity entity = playerFleetBoard;
            if (requiredGrid == 1) entity = playerGuessBoard;
            PositionComponent positionComponent =
                    (PositionComponent) parentGame.ComponentRegister.get(entity, "position_component");
            GridComponent gridComponent =
                    (GridComponent) parentGame.ComponentRegister.get(entity, "grid_component");

            int tileIndex = getTileIndexFromMouseOnGrid(
                    inputEvent.getMouseX(), inputEvent.getMouseY(),
                    positionComponent,
                    gridComponent);

            return tileIndex;
        }
        return -1;
    }

    private int getGridEntityFromMousePos(int x, int y) {
        if (x > 128) return 1;
        return 0;
    }

    private int getTileIndexFromMouseOnGrid(int mouseX, int mouseY, PositionComponent positionComponent, GridComponent gridComponent) {
        for (int i = 0; i < gridComponent.getTiles().length; i++) {
            int x = (int) positionComponent.getX();
            int y = 0;
            int rowWidthInPixels = (gridComponent.getGridWidth()) * (gridComponent.getTileWidth() + gridComponent.getTilePaddingX());
            int maxX = (int) (positionComponent.getX() + rowWidthInPixels);
            x += i * (gridComponent.getTileWidth() + gridComponent.getTilePaddingX());
            while (x >= maxX){
                x = x - rowWidthInPixels;
                y++;
            }
            y = (int) (positionComponent.getY() +  y * (gridComponent.getTileHeight() + gridComponent.getTilePaddingY()));

            int hitboxX = x + gridComponent.getTileWidth();
            int hitboxY = y + gridComponent.getTileHeight();

            if (x < mouseX && mouseX < hitboxX){
                if (y < mouseY && mouseY < hitboxY){
                    return i;
                }
            }
        }
        return -1;
    }

    private void changeGameState(GameState newGameState) {
        gameState = newGameState;
        if (gameState == GameState.AttackTurnsComplete) {
            checkIfLost();
        }
        updateTextDisplay();
    }

    private void checkIfLost() {
        GridComponent playerFleetGridComponent =
                (GridComponent) parentGame.ComponentRegister.get(playerFleetBoard, "grid_component");
        GridComponent cpuFleetGridComponent =
                (GridComponent) parentGame.ComponentRegister.get(cpuFleetBoard, "grid_component");

        if (playerFleetGridComponent.hasLost() && cpuFleetGridComponent.hasLost()) {
            gameState = GameState.Tie;
            return;
        }
        if (playerFleetGridComponent.hasLost()) {
            gameState = GameState.PlayerOneLost;
            return;
        }
        if (cpuFleetGridComponent.hasLost()) {
            gameState = GameState.PlayerTwoLost;
            return;
        }

        gameState = GameState.PlayerOneGuess;
    }

    private void updateTextDisplay() {
        switch (gameState) {
            case Tutorial:
                setTextDisplayText("Tutorial");
                break;
            case PlayerOneFleetPlacement:
                setTextDisplayText(getBoatText());
                break;
            case PlayerOneGuess:
                setTextDisplayText("Thinking Turn");
                setButtonClickable(0, true);
                setButtonClickable(1, false);
                break;
            case PlayerOneTurn:
                setTextDisplayText("Attack Turn");
                setButtonClickable(0, false);
                setButtonClickable(1, true);
                break;
            case PlayerTwoFleetPlacement:
                setTextDisplayText("CPU is placing");
                setButtonClickable(0, true);
                setButtonClickable(1, false);
                break;
            case PlayerTwoGuess:
                setTextDisplayText("CPU is thinking");
                setButtonClickable(0, true);
                setButtonClickable(1, false);
                break;
            case PlayerTwoTurn:
                setTextDisplayText("CPU is attacking");
                setButtonClickable(0, true);
                setButtonClickable(1, false);
                break;
            case Tie:
                setTextDisplayText("Draw innit bruv!");
                setButtonClickable(0, true);
                setButtonClickable(1, false);
                break;
            case PlayerOneLost:
                setTextDisplayText("CPU Wins!");
                setButtonClickable(0, true);
                setButtonClickable(1, false);
                break;
            case PlayerTwoLost:
                setTextDisplayText("CPU Lost!");
                setButtonClickable(0, true);
                setButtonClickable(1, false);
                break;
        }
    }

    private String getBoatText() {
        if (boatEntityList.isEmpty()) {
            return "All boats placed!";
        }
        IEntity boatEntity = boatEntityList.get(0);
        BoatComponent boatComponent =
                (BoatComponent) parentGame.ComponentRegister.get(boatEntity, "boat_component");

        int boatSizeX = boatComponent.getSizeX();
        int boatSizeY = boatComponent.getSizeY();

        if (boatComponent.isVertical()) {
            boatSizeX = boatComponent.getSizeY();
            boatSizeY = boatComponent.getSizeX();
        }

        return "Boat:" + boatSizeX + "x" + boatSizeY;
    }

    private void tutorialDismissed() {
        System.out.println("Tutorial Dismissed, proceeding with player fleet placement.");
        changeGameState(GameState.PlayerOneFleetPlacement);
    }

    int tutorialModalDismissCount = 0;

    public void notify(Object data) {
        if (data instanceof InputController.InputEvent){
            InputController.InputEvent inputEvent = (InputController.InputEvent) data;
            controlPlayer(inputEvent);
        }
        if (data instanceof ModalManagerSystem.ModalDissmisedEvent){
            ModalManagerSystem.ModalDissmisedEvent modalDissmisedEvent = (ModalManagerSystem.ModalDissmisedEvent) data;
            if (!tutorialDismissed) {
                tutorialModalDismissCount++;
            }
            if (tutorialModalDismissCount > 11) {
                tutorialDismissed = true;
                tutorialDismissed();
            }
        }
        if (data instanceof EntityManager.EntityRemovedEvent || data instanceof EntityManager.EntitiesClearedEvent) {
            updateFilter();
        }
        if (data instanceof EntityManager.EntityModifiedEvent) {
            EntityManager.EntityModifiedEvent eventData = (EntityManager.EntityModifiedEvent) data;
            String componentType = eventData.getComponent().getType();
            if (componentType == "boat_component") updateFilter();
        }
    }

    private void updateFilter() {
        boatEntityList = parentGame.getEntityManager().filter("boat_component");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void disable() { enabled = false; }
    public void enable() { enabled = true; }

    public class GameStateModifiedEvent{
        private GameState gameState;

        public GameStateModifiedEvent(GameState newGameState){
            gameState = newGameState;
        }

        public GameState getGameState() { return gameState; }
    }

    public enum GameState{
        Tutorial,
        PlayerOneFleetPlacement, PlayerOneTurn, PlayerOneGuess,
        PlayerTwoFleetPlacement, PlayerTwoTurn, PlayerTwoGuess,
        AttackTurnsComplete,
        Tie(true), PlayerOneLost(true), PlayerTwoLost(true);

        private final boolean gameOver;

        GameState() {
            gameOver = false;
        }

        GameState(boolean isGameOver) {
            gameOver = isGameOver;
        }

        public boolean isGameOver() {
            return gameOver;
        }
    }
}
