package com.spookynebula.battleshipgame.test;

import com.spookynebula.battleshipgame.Core.CursorSystem;
import com.spookynebula.battleshipgame.Core.DebugSystem;
import com.spookynebula.battleshipgame.Core.DefaultRenderSystem;
import com.spookynebula.battleshipgame.Core.PhysicsSystem;
import com.spookynebula.battleshipgame.Core.components.DrawableComponent;
import com.spookynebula.battleshipgame.Core.components.PhysicsComponent;
import com.spookynebula.battleshipgame.Core.components.PositionComponent;
import com.spookynebula.battleshipgame.ECS.*;
import com.spookynebula.battleshipgame.GameContainer;

public class ECSTest extends GameContainer {
    public static void main(String[] args) {
        ECSTest game = new ECSTest();
        game.start();
    }

    public ECSTest(){
        super();
    }

    @Override
    public void start() {
        // Initialize the window with a different size
        WindowManager.setWindowWidth(200);
        WindowManager.setWindowHeight(200);
        WindowManager.setWindowScale(4.0f);
        WindowManager.updateWindowSize();
        WindowManager.setWindowTitle("ECS Test");
        WindowManager.updateTitle();

        loadSystems();

        testComponents();

        initThread();
    }

    private void testComponents(){
        // Register Test Components
        ComponentRegister.registerComponent(new PositionComponent());
        ComponentRegister.registerComponent(new DrawableComponent());
        ComponentRegister.registerComponent(new PhysicsComponent());
        print(ComponentRegister.getRegisteredComponents().toString());
        // Create the test Entity and add it to the EntityManager
        Entity testEntity = new Entity();
        EntityManager.addEntity(testEntity);
        // Create some components
        PositionComponent testPositionComponent = (PositionComponent) ComponentRegister.newComponent(new PositionComponent());
        PhysicsComponent testPhysicsComponent = (PhysicsComponent) ComponentRegister.newComponent(new PhysicsComponent());
        DrawableComponent testDrawable = (DrawableComponent) ComponentRegister.newComponent(new DrawableComponent());
        print(testPositionComponent.toString());
        print(testPhysicsComponent.toString());
        print(testDrawable.toString());
        // Add the components
        // Print the before and after
        print(testEntity.toString());
        EntityManager.addComponent(testEntity, testPositionComponent);
        EntityManager.addComponent(testEntity, testPhysicsComponent);
        EntityManager.addComponent(testEntity, testDrawable);
        print(testEntity.toString());
        // Check if the Genome is modifying
        try {
            int testGenome = (testEntity.getGenome() & ComponentRegister.getComponentGene(testDrawable));
            print(String.valueOf(testGenome));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Get the entities with a TestTextComponent
        print(EntityManager.filter(new PhysicsComponent()).toString());

        // Clear the entities
        EntityManager.clearEntities();
    }

    private void loadSystems(){
        // Test Systems
        DefaultRenderSystem defaultRenderSystem = new DefaultRenderSystem(this);
        defaultRenderSystem.setClearColour(0xff000000);
        EntityManager.subscribe(defaultRenderSystem);
        systems.add(defaultRenderSystem);

        DebugSystem debugSystem = new DebugSystem(this);
        InputController.subscribe(debugSystem);
        systems.add(debugSystem);

        PhysicsSystem physicsSystem = new PhysicsSystem(this);
        EntityManager.subscribe(physicsSystem);
        systems.add(physicsSystem);

        // Test loading images
        ContentLoader.loadImage("/com/spookynebula/battleshipgame/assets/test.png");
    }

    private void print(){
        System.out.println();
    }

    private void print(String message){
        System.out.println(message);
    }
}
