package com.spookynebula.battleshipgame.ECS;

public interface IComponent {
    public boolean isEnabled();
    public void setEnabled(boolean newState);
    public int getID();
    public void setID(int newID);
    public String getType();
}
