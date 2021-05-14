package com.spookynebula.battleshipgame.ECS;

import com.spookynebula.battleshipgame.GameContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityManager implements ISubscribeSystem{
    /** List of Entities lol */
    private List<IEntity> entityList;
    private GameContainer parentGame;

    protected boolean enabled;
    protected List<ISubscriber> subscribers;

    public EntityManager(GameContainer gameContainer) {
        parentGame = gameContainer;
        entityList = new ArrayList<IEntity>();

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
     * Add component to an Entity. Modifies the Entity's genome and Component list
     * @param entity Entity to add the Component to
     * @param component Component to add
     * @return Entity with the modified genome and Component list
     */
    public IEntity addComponent(IEntity entity, IComponent component){
        entity.addComponentID(component.getID());
        try {
            entity.addToGenome(parentGame.ComponentRegister.getComponentGene(component));
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifySubscriber(new EntityModifiedEvent(entity, component));
        return entity;
    }

    /**
     * Add entity to the entity list
     * @param entity Entity to add
     */
    public void addEntity(IEntity entity) {
        entity.setID(entityList.size());
        entityList.add(entity);
        notifySubscriber(new NewEntityEvent(entity));
    }

    /**
     * Removes the entity from the entity list
     * @param entity Entity to remove
     * @return True if the removal was successful
     */
    public boolean removeEntity(IEntity entity) {
        boolean success = entityList.removeIf(listEntity -> listEntity.getID() == entity.getID());
        if (success) notifySubscriber(new EntityRemovedEvent(entity));
        return success;
    }

    /**
     * Clears all entities
     * WARN: Does not clear Components yet
     */
    public void clearEntities(){
        entityList = new ArrayList<IEntity>();
        // TODO: FIX THE CRASH
        //parentGame.ComponentRegister.clearComponents();
        notifySubscriber(new EntitiesClearedEvent());
    }

    /**
     * Returns Entity by index on the Entity list
     * @param index The index lol
     * @return the IEntity
     */
    public IEntity get(int index){
        return entityList.get(index);
    }

    /**
     * Filter Entities by IComponent, not recommended
     * @param component The Component to filter by
     * @return list of Entities with the same Component Type
     */
    public List<IEntity> filter(IComponent component){
        List<IEntity> filteredEntities = new ArrayList<IEntity>();
        int filterGene = parentGame.ComponentRegister.getComponentGene(component);
        filteredEntities = entityList.stream()
                .filter(entity -> (entity.getGenome() & filterGene) > 0)
                .collect(Collectors.toList());
        return filteredEntities;
    }

    /**
     * Filter Entities by 1 Component Tag
     * @param componentTag First Component Tag
     * @return list of Entities with the Tag
     */
    public List<IEntity> filter(String componentTag){
        List<IEntity> filteredEntities = new ArrayList<IEntity>();
        if (entityList.isEmpty()) return filteredEntities;
        int filterGene = parentGame.ComponentRegister.getComponentGene(componentTag);
        filteredEntities = filter(filterGene, ComponentRegister.ALL_GENES, ComponentRegister.ALL_GENES);
        return filteredEntities;
    }

    /**
     * Filter Entities by 2 Component Tags
     * @param componentTag1 First Component Tag
     * @param componentTag2 Second...
     * @return list of Entities with the Tags
     */
    public List<IEntity> filter(String componentTag1, String componentTag2){
        List<IEntity> filteredEntities = new ArrayList<IEntity>();
        if (entityList.isEmpty()) return filteredEntities;
        int filterGene1 = parentGame.ComponentRegister.getComponentGene(componentTag1);
        int filterGene2 = parentGame.ComponentRegister.getComponentGene(componentTag2);
        filteredEntities = filter(filterGene1, filterGene2, ComponentRegister.ALL_GENES);
        return filteredEntities;
    }
    /**
     * Filter Entities by 3 Component Tags
     * @param componentTag1 First Component Tag
     * @param componentTag2 Second...
     * @param componentTag3 Third...
     * @return list of Entities with the Tags
     */
    public List<IEntity> filter(String componentTag1, String componentTag2, String componentTag3){
        List<IEntity> filteredEntities = new ArrayList<IEntity>();
        if (entityList.isEmpty()) return filteredEntities;
        int filterGene1 = parentGame.ComponentRegister.getComponentGene(componentTag1);
        int filterGene2 = parentGame.ComponentRegister.getComponentGene(componentTag2);
        int filterGene3 = parentGame.ComponentRegister.getComponentGene(componentTag3);
        filteredEntities = filter(filterGene1, filterGene2, filterGene3);
        return filteredEntities;
    }

    /**
     * This compares each Entity with the Genes given and returns a lis of
     * Entities with matching Genomes
     * @param gene1 First Gene to compare
     * @param gene2 Second...
     * @param gene3 Third...
     * @return A list comprised of entities which the genome matches the genes given.
     */
    private List<IEntity> filter(int gene1, int gene2, int gene3){
        return entityList.stream()
                .filter(entity -> {
                    // This might not seem efficient but at least it's understandable
                    boolean success = true;
                    success = success && (entity.getGenome() & gene1) > 0;
                    success = success && (entity.getGenome() & gene2) > 0;
                    success = success && (entity.getGenome() & gene3) > 0;
                    return success;
                })
                .collect(Collectors.toList());
    }

    /**
     * This event is created when a new Component is added to an Entity
     */
    public class EntityModifiedEvent{
        private IEntity entity;
        private IComponent component;

        public EntityModifiedEvent(IEntity modifiedEntity, IComponent newComponent){
            entity = modifiedEntity;
            component = newComponent;
        }

        public IEntity getEntity() { return entity; }
        public IComponent getComponent() { return component; }
    }

    /**
     * This event is created when a Entity is removed from the Entity list
     */
    public class EntityRemovedEvent{
        private IEntity entity;

        public EntityRemovedEvent(IEntity entityRemoved){
            entity = entityRemoved;
        }

        public IEntity getEntity() { return entity; }
    }

    /**
     * This event is created when a new Entity is added to the Entity list
     */
    public class NewEntityEvent{
        private IEntity entity;

        public NewEntityEvent(IEntity modifiedEntity){
            entity = modifiedEntity;
        }

        public IEntity getEntity() { return entity; }
    }

    /**
     * This event is created when the Entities are cleared
     */
    public class EntitiesClearedEvent{

        public EntitiesClearedEvent(){}
    }
}
