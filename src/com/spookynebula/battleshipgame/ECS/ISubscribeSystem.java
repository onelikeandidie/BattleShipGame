package com.spookynebula.battleshipgame.ECS;

public interface ISubscribeSystem extends ISystem{
    public void subscribe(ISubscriber newSubscriber);
    public void unsubscribe(ISubscriber newSubscriber);
}
