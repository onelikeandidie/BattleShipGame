package com.spookynebula.battleshipgame;

import com.spookynebula.battleshipgame.Core.CursorSystem;
import com.spookynebula.battleshipgame.Core.components.DrawableComponent;
import com.spookynebula.battleshipgame.Core.components.HoverComponent;
import com.spookynebula.battleshipgame.Core.components.PositionComponent;
import com.spookynebula.battleshipgame.Core.components.TextComponent;
import com.spookynebula.battleshipgame.ECS.Entity;
import com.spookynebula.battleshipgame.game.BoardCursorSystem;
import com.spookynebula.battleshipgame.game.GameStateSystem;
import com.spookynebula.battleshipgame.game.BoardRenderSystem;
import com.spookynebula.battleshipgame.game.ModalManagerSystem;
import com.spookynebula.battleshipgame.game.components.*;

public class BattleShipGame extends GameContainer {
    public static void main(String[] args) {
        BattleShipGame game = new BattleShipGame();
        game.start();
    }

    public BattleShipGame(){
        super();
    }

    @Override
    public void start() {
        // Initialize the window with a different size
        WindowManager.setWindowWidth(256);
        WindowManager.setWindowHeight(128);
        WindowManager.setWindowScale(4.0f);
        WindowManager.updateWindowSize();
        WindowManager.setWindowTitle("Naval Battle");
        WindowManager.updateTitle();

        // Register Components
        ComponentRegister.registerComponent(new DrawableComponent());
        ComponentRegister.registerComponent(new PositionComponent());
        ComponentRegister.registerComponent(new HoverComponent());
        ComponentRegister.registerComponent(new GridComponent());
        ComponentRegister.registerComponent(new DrawableButtonComponent());
        ComponentRegister.registerComponent(new ModalComponent());
        ComponentRegister.registerComponent(new BoatComponent());
        ComponentRegister.registerComponent(new TextDisplayComponent());

        // Create systems
        BoardRenderSystem boardRenderSystem = new BoardRenderSystem(this);
        boardRenderSystem.setClearColour(0xfffef4e2);
        EntityManager.subscribe(boardRenderSystem);
        systems.add(boardRenderSystem);

        ModalManagerSystem modalManagerSystem = new ModalManagerSystem(this);
        InputController.subscribe(modalManagerSystem);
        EntityManager.subscribe(modalManagerSystem);
        systems.add(modalManagerSystem);

        BoardCursorSystem boardCursorSystem = new BoardCursorSystem(this);
        InputController.subscribe(boardCursorSystem);
        EntityManager.subscribe(boardCursorSystem);
        systems.add(boardCursorSystem);

        GameStateSystem gameStateSystem = new GameStateSystem(this);
        InputController.subscribe(gameStateSystem);
        EntityManager.subscribe(gameStateSystem);
        modalManagerSystem.subscribe(gameStateSystem);
        systems.add(gameStateSystem);

        // Load Assets
        ContentLoader.loadSpriteFont("/com/spookynebula/battleshipgame/assets/font.png", 4, 7);
        ContentLoader.loadSpriteSheet("/com/spookynebula/battleshipgame/assets/cursor_sheet.png", 16, 16);
        ContentLoader.loadSpriteSheet("/com/spookynebula/battleshipgame/assets/tile_sheet.png", 8, 8);
        ContentLoader.loadSpriteSheet("/com/spookynebula/battleshipgame/assets/button_sheet.png", 10, 10);
        ContentLoader.loadImage("/com/spookynebula/battleshipgame/assets/fleet_board.png");
        ContentLoader.loadImage("/com/spookynebula/battleshipgame/assets/guess_board.png");
        ContentLoader.loadImage("/com/spookynebula/battleshipgame/assets/modal.png");
        ContentLoader.loadImage("/com/spookynebula/battleshipgame/assets/board_cursor.png");
        ContentLoader.loadImage("/com/spookynebula/battleshipgame/assets/test.png");

        // Create the Entities

        initThread();
    }
}
