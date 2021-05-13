package com.spookynebula.battleshipgame.Core.components;

import com.spookynebula.battleshipgame.ECS.Component;

public class PositionComponent extends Component {
    private float x, y;
    private float rotation;

    public PositionComponent(){
        enabled = true;
        type = "position_component";
        ID = 0;

        x = y = 0;
        rotation = 0.0f;
    }

    public void setRotation(float newRotation) { rotation = newRotation; }

    public void setX(float newX) { x = newX; }
    public void setY(float newY) { y = newY; }

    public void setX(int newX) { x = newX; }
    public void setY(int newY) { y = newY; }

    public void set(float newX, float newY) { set(newX, newY, 0.0f); }
    public void set(int newX, int newY) { set(newX, newY, 0.0f); }

    public void set(float newX, float newY, float newRotation) {
        setX(newX);
        setY(newY);
        setRotation(newRotation);
    }

    public void set(int newX, int newY, float newRotation) {
        setX(newX);
        setY(newY);
        setRotation(newRotation);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getRotation() { return rotation; }

    @Override
    public String toString() {
        return "PositionComponent{" +
                "enabled=" + enabled +
                ", type='" + type + '\'' +
                ", ID=" + ID +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
