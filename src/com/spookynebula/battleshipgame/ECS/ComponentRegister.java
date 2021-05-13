package com.spookynebula.battleshipgame.ECS;

import com.spookynebula.battleshipgame.GameContainer;

import java.util.ArrayList;
import java.util.List;

public class ComponentRegister implements ISubscribeSystem{
    private List<IComponent> registeredComponents;
    // Components that are in entities
    private List<IComponent> componentList;

    private GameContainer parentGame;

    protected boolean enabled;
    protected List<ISubscriber> subscribers;

    public static final int ALL_GENES = 0b01111111111111111111111111111111;
    public static final int NO_GENES = 0b00000000000000000000000000000000;

    public ComponentRegister(GameContainer gameContainer) {
        parentGame = gameContainer;

        registeredComponents = new ArrayList<IComponent>();
        componentList = new ArrayList<IComponent>();

        subscribers = new ArrayList<ISubscriber>();
    }

    public void subscribe(ISubscriber newSubscriber) {
        subscribers.add(newSubscriber);
    }

    public void unsubscribe(ISubscriber subscriber){
        subscribers.remove(subscriber);
    }

    public void notifySubscriber(Object eventData){
        for (ISubscriber subscriber : subscribers) {
            subscriber.notify(eventData);
        }
    }

    public boolean isEnabled() { return enabled; }

    public void disable() { enabled = false; }

    public void enable() { enabled = true; }

    public void registerComponent(IComponent component){
        registeredComponents.add(component);
    }

    public IComponent newComponent(IComponent component){
        int componentID = componentList.size();
        component.setID(componentID);
        componentList.add(component);
        notifySubscriber(new ComponentAddedEvent(component));
        return component;
    }

    public IComponent get(int componentID){
        return componentList.get(componentID);
    }

    public IComponent get(IEntity entity, String componentType){
        List<Integer> components = entity.getComponentIDs();
        for (int i = 0; i < components.size(); i++) {
            IComponent component = get(components.get(i));
            if (component.getType() == componentType){
                return component;
            }
        }
        return null;
    }

    public List<IComponent> getRegisteredComponents(){
        return registeredComponents;
    }

    public int getComponentGene(IComponent component) {
        for (int i = 0; i < registeredComponents.size(); i++) {
            if (compareType(component, registeredComponents.get(i))){
                return 1 << i;
            }
        }
        System.out.println("Component had not been registered before with type: " + component.getType());
        return ALL_GENES;
    }

    public int getComponentGene(String componentTag) {
        for (int i = 0; i < registeredComponents.size(); i++) {
            if (componentTag == registeredComponents.get(i).getType()){
                return 1 << i;
            }
        }
        System.out.println("Component had not been registered before with type: " + componentTag);
        return NO_GENES;
    }

    private boolean compareType(IComponent component, IComponent registeredComponent){
        return component.getType() == registeredComponent.getType();
    }

    public void clearComponents() {
        componentList.clear();
        notifySubscriber(new ComponentsClearedEvent());
    }

    public class ComponentAddedEvent{
        private IComponent component;
        private String tag;

        public ComponentAddedEvent(IComponent componentAdded){
            component = componentAdded;
            tag = componentAdded.getType();
        }

        public IComponent getComponent() { return component; }

        public String getTag() { return tag; }
    }

    public class ComponentsClearedEvent{

        public ComponentsClearedEvent(){}
    }
}
