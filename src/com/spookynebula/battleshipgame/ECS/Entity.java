package com.spookynebula.battleshipgame.ECS;

import java.util.ArrayList;
import java.util.List;

public class Entity implements IEntity{
    private int ID;
    private int genome;
    private List<Integer> componentIDList;

    public Entity(){
        // Set the genome to empty bits
        genome = 0;
        // Instance the component ID list
        componentIDList = new ArrayList<Integer>();
    }

    public int getID() {
        return ID;
    }

    public void setID(int newID) {
        ID = newID;
    }

    public int getGenome() {
        return genome;
    }

    public void setGenome(int newGenome) {
        genome = newGenome;
    }

    public void addToGenome(int addedGenome) {
        // Bit Manipulation
        genome |= addedGenome;
    }

    public void addComponentID(int componentID){
        componentIDList.add(componentID);
    }

    public List<Integer> getComponentIDs() {
        return componentIDList;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "genome=" + genome +
                ", componentIDList=" + componentIDList +
                '}';
    }
}
