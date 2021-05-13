package com.spookynebula.battleshipgame.ECS;

public interface ISubscriber extends ISystem {
    public void notify(Object data);
}
