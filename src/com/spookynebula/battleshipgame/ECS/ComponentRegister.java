package com.spookynebula.battleshipgame.ECS;

import com.spookynebula.battleshipgame.GameContainer;

import java.util.ArrayList;
import java.util.List;

public class ComponentRegister implements ISubscribeSystem{
    /** Components that have been registered to the Register */
    private List<IComponent> registeredComponents;
    /** Components that are in entities */
    private List<IComponent> componentList;

    private GameContainer parentGame;

    protected boolean enabled;
    protected List<ISubscriber> subscribers;

    /** Int with all bits set to 1, describes the perfect entity */
    public static final int ALL_GENES = 0b01111111111111111111111111111111;
    /** Int with all bits set to 0, describes an empty entity */
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

    /**
     * Register a component to the Component register
     * @param component An instance of the IComponent
     */
    public void registerComponent(IComponent component){
        registeredComponents.add(component);
    }

    /**
     * Returns a instance of the component with an ID
     * @param component An instance of the IComponent
     * @return The modified instance of the IComponent
     */
    public IComponent newComponent(IComponent component){
        // The ID is just the index of the new Component
        int componentID = componentList.size();
        component.setID(componentID);
        componentList.add(component);
        notifySubscriber(new ComponentAddedEvent(component));
        return component;
    }

    /**
     * Returns the component by ID.
     * Which incidentally is the index of the Component
     * @param componentID ID of the component
     * @return The component matching the ID
     */
    public IComponent get(int componentID){
        return componentList.get(componentID);
    }

    /**
     * Returns Component associated with the Entity
     * @param entity Entity lol
     * @param componentType ComponentTag usually a String
     * @return Can be null if the component was not found
     */
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

    /**
     * Returns the list of registered Components, used for Tests and Debugging
     * @return list of registered Components
     */
    public List<IComponent> getRegisteredComponents(){
        return registeredComponents;
    }

    /**
     * Returns the gene of a registered Component
     * @param component the Component to retrieve the gene of
     * @return int describing the gene
     */
    public int getComponentGene(IComponent component) {
        for (int i = 0; i < registeredComponents.size(); i++) {
            if (compareType(component, registeredComponents.get(i))){
                return 1 << i;
            }
        }
        System.out.println("Component had not been registered before with type: " + component.getType());
        return ALL_GENES;
    }

    /**
     * Returns the gene of a registered Component by Tag
     * @param componentTag the Tag to find the Component by
     * @return int describing the gene
     */
    public int getComponentGene(String componentTag) {
        for (int i = 0; i < registeredComponents.size(); i++) {
            if (componentTag == registeredComponents.get(i).getType()){
                return 1 << i;
            }
        }
        System.out.println("Component had not been registered before with type: " + componentTag);
        return NO_GENES;
    }

    /**
     * Returns true if the Component types match
     * <p> This can be inefficient since it uses String comparisons </p>
     * @param component The Component
     * @param registeredComponent A Registered Component
     * @return Returns true if the Components matched
     */
    private boolean compareType(IComponent component, IComponent registeredComponent){
        return component.getType() == registeredComponent.getType();
    }

    /**
     * Clears the Component list
     * WARN: Can crash the game
     */
    public void clearComponents() {
        componentList.clear();
        notifySubscriber(new ComponentsClearedEvent());
    }

    /**
     * This event is created when a new Component is added to the list
     */
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

    /**
     * This event is created when the Component list is cleared
     */
    public class ComponentsClearedEvent{

        public ComponentsClearedEvent(){}
    }
}
