package com.spookynebula.battleshipgame.Core.components;

import com.spookynebula.battleshipgame.ECS.Component;

public class PhysicsComponent extends Component {
    private float weight;
    private float vx;
    private float vy;
    private float airDrag;
    private float dragAcceleration;
    private float terminalVelocity;
    
    public PhysicsComponent(){
        enabled = true;
        type = "physics_component";
        ID = 0;
        
        weight = 0;
        vx = 0;
        vy = 0;
        airDrag = 0;
        dragAcceleration = 0;
        terminalVelocity = -1;
    }


    public float getWeight() { return weight; }
    public void setWeight(float newWeight) { weight = newWeight; }

    public float getVx() { return vx; }
    public void setVx(float newVx) { vx = newVx; }
    public float getVy() { return vy; }
    public void setVy(float newVy) { vy = newVy; }

    public float getAirDrag() { return airDrag; }
    public void setAirDrag(float newAirDrag) { airDrag = newAirDrag; }

    public float getDragAcceleration() { return dragAcceleration; }
    public void setDragAcceleration(float newDragAcceleration) { dragAcceleration = newDragAcceleration; }

    public float getTerminalVelocity() { return terminalVelocity; }
    public void setTerminalVelocity(float newTerminalVelocity) { terminalVelocity = newTerminalVelocity; }

    @Override
    public String toString() {
        return "PhysicsComponent{" +
                "enabled=" + enabled +
                ", type='" + type + '\'' +
                ", ID=" + ID +
                ", weight=" + weight +
                ", vx=" + vx +
                ", vy=" + vy +
                ", airDrag=" + airDrag +
                '}';
    }
}
