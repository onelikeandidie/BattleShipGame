package com.spookynebula.battleshipgame.Core;

import com.spookynebula.battleshipgame.Core.components.DrawableComponent;
import com.spookynebula.battleshipgame.Core.components.PositionComponent;
import com.spookynebula.battleshipgame.Core.gfx.Image;
import com.spookynebula.battleshipgame.Core.gfx.SpriteFont;
import com.spookynebula.battleshipgame.ECS.*;
import com.spookynebula.battleshipgame.GameContainer;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultRenderSystem implements IInitSystem, IDrawSystem, ISubscriber {
    protected GameContainer parentGame;
    private boolean enabled;

    private int pixelWidth, pixelHeight;
    private int[] pixelData;

    private int clearColour;

    private int debugPixel;

    private List<IEntity> drawEntityList;

    public DefaultRenderSystem(GameContainer gameContainer){
        parentGame = gameContainer;
        enabled = true;

        debugPixel = 0;
        clearColour = 0x000000ff;
    }

    public void Init() {
        pixelWidth = parentGame.getWindowManager().getWindowWidth();
        pixelHeight = parentGame.getWindowManager().getWindowHeight();
        pixelData = ((DataBufferInt) parentGame.getWindowManager().getFrameImage().getRaster().getDataBuffer()).getData();

        updateFilter();
    }

    public void Draw() {
        clear();

        drawEntities();

        //drawDebugPixel();

        sendFramePixelData();
    }

    protected void drawEntities(){
        // Debug, to know how many entities were drawn this frame
        int entityCount = 0;

        for (int i = 0; i < drawEntityList.size(); i++) {
            IEntity entity = drawEntityList.get(i);
            DrawableComponent drawableComponent = (DrawableComponent) parentGame.ComponentRegister.get(entity, "drawable_component");
            PositionComponent positionComponent = (PositionComponent) parentGame.ComponentRegister.get(entity, "position_component");
            Image texture;
            if (drawableComponent.isFromSheet()) {
                texture = parentGame.getContentLoader().getTexture(
                        drawableComponent.getTextureID(),
                        drawableComponent.getTileX(),
                        drawableComponent.getTileY());
            } else {
                texture = parentGame.getContentLoader()
                        .getTexture(drawableComponent.getTextureID());
            }
            drawImage(texture, (int) positionComponent.getX(), (int) positionComponent.getY());
            entityCount++;
        }
    }

    private void drawImage(Image image, int x, int y, float angleInDegrees){
        // Entity Culling
        if (x > pixelWidth) return;
        if (image.getWidth() + x < 0) return;
        if (y > pixelHeight) return;
        if (image.getHeight() + y < 0) return;

        int[] imageData = image.getPixelData();

        for (int pixelDataX = 0; pixelDataX < image.getWidth(); pixelDataX++) {
            for (int pixelDataY = 0; pixelDataY < image.getHeight(); pixelDataY++) {
                int rgbData = imageData[pixelDataX + (pixelDataY * image.getWidth())];
                setPixelAt(pixelDataX + x, pixelDataY + y, rgbData);
            }
        }
    }

    protected void drawImage(Image image, int x, int y){
        drawImage(image, x, y, 0);
    }

    protected void drawImage(Image image){
        drawImage(image, 0, 0, 0);
    }

    private void drawDebugPixel() {
        pixelData[debugPixel * pixelWidth + debugPixel] = 0x00ff00;
        debugPixel++;
        if (debugPixel * pixelWidth + debugPixel >= pixelData.length) debugPixel = 0;
    }

    protected void drawText(String text, SpriteFont spriteFont, int x, int y) {
        char[] chars = text.toUpperCase().toCharArray();

        for (int i = 0; i < chars.length; i++) {
            drawImage(spriteFont.getSymbol(chars[i]), x + (i * spriteFont.getSymbolWidth()),  y);
        }
    }

    protected void sendFramePixelData() {
        BufferedImage b = parentGame.getWindowManager().getFrameImage();
        b.setRGB(0,0, pixelWidth, pixelHeight, pixelData, 0, pixelWidth);
        parentGame.getWindowManager().setFrameImage(b);
    }

    protected void clear() {
        Arrays.fill(pixelData, clearColour);
    }

    private void setPixelAt(int x, int y, int rgba){
        int actualPosition = x + y * pixelWidth;
        if (x < 0) return;
        if (y < 0) return;
        if (x > pixelWidth - 1) return;
        if (y > pixelHeight - 1) return;
        int setAlpha;
        if (actualPosition <= pixelData.length - 1 && actualPosition >= 0) {
            // Blending of transparency
            setAlpha = (rgba >> 24) & 0xff;
            switch (setAlpha) {
                // FULL TRANSPARENT
                case 0:
                    break;
                // FULL OPAQUE
                case 255:
                    pixelData[actualPosition] = rgba;
                    break;
                // Any other case
                default:
                    // This is for dithering
                    // If a int is divisible by 2 then its rightmost bit will be 0 after a right bitshift
                    int pixelIndex = (x+y);
                    if ((pixelIndex & ((1 << 1) - 1)) == 0)
                        pixelData[actualPosition] = rgba;
            }
        }
    }

    public int getClearColour() { return clearColour; }

    public void setClearColour(int newClearColour) { clearColour = newClearColour; }

    public void notify(Object data) {
        if (data instanceof EntityManager.EntityModifiedEvent) {
            EntityManager.EntityModifiedEvent eventData = (EntityManager.EntityModifiedEvent) data;
            String componentType = eventData.getComponent().getType();
            if (componentType == "drawable_component" || componentType == "position_component") updateFilter();
        }
        if (data instanceof WindowManager.WindowResizeEvent){
            WindowManager.WindowResizeEvent eventData = (WindowManager.WindowResizeEvent) data;
            updateWindowSize(eventData.getWindowWidth(), eventData.getWindowHeight());
        }
        if (data instanceof EntityManager.EntitiesClearedEvent){
            EntityManager.EntitiesClearedEvent eventData = (EntityManager.EntitiesClearedEvent) data;
            updateFilter();
        }
        if (data instanceof EntityManager.EntityRemovedEvent){
            EntityManager.EntityRemovedEvent eventData = (EntityManager.EntityRemovedEvent) data;
            updateFilter();
        }
    }

    protected void updateFilter(){
        drawEntityList = parentGame.getEntityManager().filter("drawable_component", "position_component");
        drawEntityList = drawEntityList.stream()
                .sorted((entity1,entity2) -> {
                    DrawableComponent drawableComponent1 =
                            (DrawableComponent) parentGame.getComponentRegister().get(entity1, "drawable_component");
                    DrawableComponent drawableComponent2 =
                            (DrawableComponent) parentGame.getComponentRegister().get(entity2, "drawable_component");
                    int order1 = drawableComponent1.getOrder();
                    int order2 = drawableComponent2.getOrder();
                    return order1 - order2;
                })
                .collect(Collectors.toList());
    }

    private void updateWindowSize(int newWidth, int newHeight){
        pixelWidth = newWidth;
        pixelHeight = newHeight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }
}
