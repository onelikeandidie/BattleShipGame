package com.spookynebula.battleshipgame.game;

import com.spookynebula.battleshipgame.Core.Util;
import com.spookynebula.battleshipgame.ECS.Entity;
import com.spookynebula.battleshipgame.GameContainer;
import com.spookynebula.battleshipgame.game.components.BoatComponent;
import com.spookynebula.battleshipgame.game.components.GridComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AmazingCPU {
    private GameContainer parentGame;

    // CPU boards
    private Entity fleetBoard;
    private Entity guessBoard;

    private List<BoatComponent> boatComponents;
    private Random random;

    // All the lists for the guessing algorithm
    private List<Util.Position> potentialBoats;
    private List<Util.Position> potentialAttacks;
    private List<Util.Position> alreadyGuessed;
    private List<Util.Position> boatsHit;

    public AmazingCPU(GameContainer gameContainer, Entity cpuFleetBoard, Entity cpuGuessBoard) {
        parentGame = gameContainer;

        fleetBoard = cpuFleetBoard;
        guessBoard = cpuGuessBoard;

        boatComponents = new ArrayList<BoatComponent>();
        random = new Random();

        potentialAttacks = new ArrayList<Util.Position>();
        potentialBoats = new ArrayList<Util.Position>();
        alreadyGuessed = new ArrayList<Util.Position>();
        boatsHit = new ArrayList<Util.Position>();
    }

    /**
     * Creates a boatComponent with the size and verticality given
     * @param boatSizeX height
     * @param boatSizeY width
     * @param vertical is vertical
     * @return the Boat Component with the properties given
     */
    private BoatComponent createBoat(int boatSizeX, int boatSizeY, boolean vertical) {
        BoatComponent boatComponent = new BoatComponent();
        boatComponent.setSizeX(boatSizeX);
        boatComponent.setSizeY(boatSizeY);
        boatComponent.setVertical(vertical);
        return boatComponent;
    }

    /**
     * Places the six boats
     */
    public void placeBoats() {
        // Creates the boats with the right sizes
        // randomly choses between vertical and horizontal
        boatComponents.add(createBoat(2, 1, random.nextBoolean()));
        boatComponents.add(createBoat(2, 1, random.nextBoolean()));
        boatComponents.add(createBoat(2, 1, random.nextBoolean()));
        boatComponents.add(createBoat(3, 1, random.nextBoolean()));
        boatComponents.add(createBoat(3, 1, random.nextBoolean()));
        boatComponents.add(createBoat(4, 1, random.nextBoolean()));

        GridComponent gridComponent =
                (GridComponent) parentGame.ComponentRegister.get(fleetBoard, "grid_component");

        // Places each boat until the boat list is empty
        while (!boatComponents.isEmpty()) {
            boolean success =
                    gridComponent.placeBoat((int) (random.nextFloat() * gridComponent.getTiles().length), boatComponents.get(0));

            if (success)
                boatComponents.remove(0);
        }
    }

    /**
     * Calculates possible locations for attacks
     */
    public void think() {
        GridComponent gridComponent =
                (GridComponent) parentGame.ComponentRegister.get(guessBoard, "grid_component");

        int tilesLength = gridComponent.getTiles().length;
        int gridWidth = gridComponent.getGridWidth();
        int gridHeight = gridComponent.getGridHeight();

        // ANALise grid to get the hits and misses
        boatsHit.clear();
        alreadyGuessed.clear();
        for (int i = 0; i < tilesLength; i++) {
            GridComponent.TileState tileState = gridComponent.get(i);

            Util.Position position =
                    Util.getPositionFromIndexOfArray(i, gridWidth, gridHeight);

            if (tileState == GridComponent.TileState.Destroyed) {
                boatsHit.add(position);
                alreadyGuessed.add(position);
            }
            if (tileState == GridComponent.TileState.Miss) {
                alreadyGuessed.add(position);
            }
        }


        // Add where boats could be
        potentialBoats.clear();
        for (int i = 0; i < boatsHit.size(); i++) {
            Util.Position hitPosition = boatsHit.get(i);
            List<Util.Position> aroundList = getAround(hitPosition);

            aroundList.removeIf(newPosition -> alreadyGuessed.stream().
                    anyMatch(newPosition::equals));

            potentialBoats.addAll(aroundList);
        }

        // Calculate where boats could be
        if (!potentialBoats.isEmpty()) {
            potentialAttacks.clear();
            potentialAttacks.addAll(potentialBoats);
            potentialAttacks.removeIf(potentialPosition -> alreadyGuessed.stream().
                    anyMatch(potentialPosition::equals));
            potentialAttacks.removeIf(potentialPosition ->
                    !Util.isInBounds(potentialPosition.getX(), potentialPosition.getY(), gridWidth, gridHeight));
        }

        // If there was no potential attacks, get a random position
        while (potentialAttacks.isEmpty()) {
            potentialAttacks.add(getRandomPosition(tilesLength, gridWidth, gridHeight));
        }

        System.out.println("Potential Attacks: " + potentialAttacks.size());
    }

    /**
     * Returns a random position in the grid with Width and Height given
     * @param tileArrayLength Max array lenght
     * @param gridWidth Width of the grid
     * @param gridHeight Height of the grid
     * @return random position
     */
    private Util.Position getRandomPosition(int tileArrayLength, int gridWidth, int gridHeight) {
        int randomIndex = (int) (random.nextFloat() * tileArrayLength);
        Util.Position potentialPosition =
                Util.getPositionFromIndexOfArray(randomIndex, gridWidth, gridHeight);

        if (alreadyGuessed.stream().noneMatch(position -> position.equals(potentialPosition))) {
            return potentialPosition;
        } else {
            return getRandomPosition(tileArrayLength, gridWidth, gridHeight);
        }
    }

    /**
     * Returns the positions around a position
     * @param position the centre position
     * @return List of positions around the centre
     */
    public List<Util.Position> getAround(Util.Position position) {
        List<Util.Position> positionList = new ArrayList<Util.Position>();
        int x = position.getX();
        int y = position.getY();
        positionList.add(new Util.Position(x + 1, y + 0));
        positionList.add(new Util.Position(x - 1, y + 0));
        positionList.add(new Util.Position(x + 0, y + 1));
        positionList.add(new Util.Position(x + 0, y - 1));
        return positionList;
    }

    /**
     * Selects a random attack position on the grid
     */
    public void attack() {
        GridComponent gridComponent =
                (GridComponent) parentGame.ComponentRegister.get(guessBoard, "grid_component");

        int randomIndex = (int) (random.nextFloat() * potentialAttacks.size() - 1);
        Util.Position randomPotentialPosition = potentialAttacks.get(randomIndex);
        gridComponent.select(randomPotentialPosition.getX(), randomPotentialPosition.getY());
        potentialAttacks.remove(randomIndex);
        System.out.println("Computer Attacks: " + randomPotentialPosition);
    }
}
