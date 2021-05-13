package com.spookynebula.battleshipgame.ECS;

import java.util.List;

public interface IEntity {
    public int getID();
    public void setID(int newID);
    public int getGenome();
    public void setGenome(int newGenome);
    public void addToGenome(int addedGenome);
    public void addComponentID(int componentID);
    public List<Integer> getComponentIDs();
}
