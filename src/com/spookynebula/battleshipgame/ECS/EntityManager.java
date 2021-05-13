package com.spookynebula.battleshipgame.ECS;

import com.spookynebula.battleshipgame.GameContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityManager implements ISubscribeSystem{
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

    public void addEntity(IEntity entity) {
        entity.setID(entityList.size());
        entityList.add(entity);
        notifySubscriber(new NewEntityEvent(entity));
    }

    public void removeEntity(IEntity entity) {
        boolean success = entityList.removeIf(listEntity -> listEntity.getID() == entity.getID());
        if (success) notifySubscriber(new EntityRemovedEvent(entity));
    }

    public void clearEntities(){
        entityList = new ArrayList<IEntity>();
        // TODO: FIX THE CRASH
        //parentGame.ComponentRegister.clearComponents();
        notifySubscriber(new EntitiesClearedEvent());
    }

    public IEntity get(int index){
        return entityList.get(index);
    }

    public List<IEntity> filter(IComponent component){
        List<IEntity> filteredEntities = new ArrayList<IEntity>();
        int filterGene = parentGame.ComponentRegister.getComponentGene(component);
        filteredEntities = entityList.stream()
                .filter(entity -> (entity.getGenome() & filterGene) > 0)
                .collect(Collectors.toList());
        return filteredEntities;
    }

    public List<IEntity> filter(String componentTag){
        List<IEntity> filteredEntities = new ArrayList<IEntity>();
        if (entityList.isEmpty()) return filteredEntities;
        int filterGene = parentGame.ComponentRegister.getComponentGene(componentTag);
        filteredEntities = filter(filterGene, ComponentRegister.ALL_GENES, ComponentRegister.ALL_GENES);
        return filteredEntities;
    }

    public List<IEntity> filter(String componentTag1, String componentTag2){
        List<IEntity> filteredEntities = new ArrayList<IEntity>();
        if (entityList.isEmpty()) return filteredEntities;
        int filterGene1 = parentGame.ComponentRegister.getComponentGene(componentTag1);
        int filterGene2 = parentGame.ComponentRegister.getComponentGene(componentTag2);
        filteredEntities = filter(filterGene1, filterGene2, ComponentRegister.ALL_GENES);
        return filteredEntities;
    }

    public List<IEntity> filter(String componentTag1, String componentTag2, String componentTag3){
        List<IEntity> filteredEntities = new ArrayList<IEntity>();
        if (entityList.isEmpty()) return filteredEntities;
        int filterGene1 = parentGame.ComponentRegister.getComponentGene(componentTag1);
        int filterGene2 = parentGame.ComponentRegister.getComponentGene(componentTag2);
        int filterGene3 = parentGame.ComponentRegister.getComponentGene(componentTag3);
        filteredEntities = filter(filterGene1, filterGene2, filterGene3);
        return filteredEntities;
    }

    private List<IEntity> filter(int gene1, int gene2, int gene3){
        return entityList.stream()
                .filter(entity -> {
                    // This might not seem efficient but at least it's understandable
                    int genome = entity.getGenome();
                    boolean success = true;
                    success = success && (entity.getGenome() & gene1) > 0;
                    success = success && (entity.getGenome() & gene2) > 0;
                    success = success && (entity.getGenome() & gene3) > 0;
                    return success;
                })
                .collect(Collectors.toList());
    }


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

    public class EntityRemovedEvent{
        private IEntity entity;

        public EntityRemovedEvent(IEntity entityRemoved){
            entity = entityRemoved;
        }

        public IEntity getEntity() { return entity; }
    }

    public class NewEntityEvent{
        private IEntity entity;

        public NewEntityEvent(IEntity modifiedEntity){
            entity = modifiedEntity;
        }

        public IEntity getEntity() { return entity; }
    }

    public class EntitiesClearedEvent{

        public EntitiesClearedEvent(){}
    }
}
