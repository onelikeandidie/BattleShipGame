package com.spookynebula.battleshipgame.ECS;

public class Component implements IComponent{
    public boolean enabled;
    public String type;
    public int ID;

    public Component(){
        enabled = true;
        type = "simple_component";
        ID = 0;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean newState) {
        enabled = newState;
    }

    public int getID() {
        return ID;
    }

    public void setID(int newID) {
        ID = newID;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Component{" +
                "enabled=" + enabled +
                ", type='" + type + '\'' +
                ", ID=" + ID +
                '}';
    }
}
