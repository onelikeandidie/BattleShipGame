package com.spookynebula.battleshipgame.ECS;

import com.spookynebula.battleshipgame.GameContainer;

public interface ISystem {
    public boolean isEnabled();
    public void disable();
    public void enable();
}

