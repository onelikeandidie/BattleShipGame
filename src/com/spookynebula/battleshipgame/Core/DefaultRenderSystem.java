package com.spookynebula.battleshipgame.Core;

import com.spookynebula.battleshipgame.Core.components.DrawableComponent;
import com.spookynebula.battleshipgame.Core.components.PositionComponent;
import com.spookynebula.battleshipgame.Core.gfx.Image;
import com.spookynebula.battleshipgame.Core.gfx.SpriteFont;
import com.spookynebula.battleshipgame.ECS.*;
import com.spookynebula.battleshipgame.GameContainer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultRenderSystem implements IInitSystem, IDrawSystem, ISubscriber {
    protected GameContainer parentGame;
    private boolean enabled;

    // Pixel Width and Height are the size of the Frame in
    // pixels
    private int pixelWidth, pixelHeight;
    // Pixel data is an Array with each colour of each pixel
    // in the frame
    private int[] pixelData;

    private int clearColour;

    private List<IEntity> drawEntityList;

    public DefaultRenderSystem(GameContainer gameContainer){
        parentGame = gameContainer;
        enabled = true;
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

    /**
     * Draws the Image in the X and Y position
     * Angle is currently not supported
     * @param image Image to be drawn
     * @param x X position
     * @param y Y position
     * @param angleInDegrees Not supported, angle in degrees
     */
    private void drawImage(Image image, int x, int y, float angleInDegrees){
        // This checks if the image is within bounds, if not
        // don't draw it
        if (x > pixelWidth) return;
        if (image.getWidth() + x < 0) return;
        if (y > pixelHeight) return;
        if (image.getHeight() + y < 0) return;

        // This retrieves the Image's pixel data array
        int[] imageData = image.getPixelData();

        // Ieterate each pixel in the array and draw it on
        // its respective x and y positions
        for (int pixelDataX = 0; pixelDataX < image.getWidth(); pixelDataX++) {
            for (int pixelDataY = 0; pixelDataY < image.getHeight(); pixelDataY++) {
                // Get the data of that pixel
                int rgbData = imageData[pixelDataX + (pixelDataY * image.getWidth())];
                // Draw it
                setPixelAt(pixelDataX + x, pixelDataY + y, rgbData);
            }
        }
    }

    /**
     * Draws the Image in the X and Y position
     * @param image Image to be drawn
     * @param x X position
     * @param y Y position
     */
    protected void drawImage(Image image, int x, int y){
        drawImage(image, x, y, 0);
    }
    /**
     * Draws the Image in the root position
     * @param image Image to be drawn
     */
    protected void drawImage(Image image){
        drawImage(image, 0, 0, 0);
    }

    /**
     * Draw Text string in the X and Y position
     * @param text Text lol
     * @param spriteFont font to draw with
     * @param x Top left X offset
     * @param y Top left Y offset
     */
    protected void drawText(String text, SpriteFont spriteFont, int x, int y) {
        // The chars must be in UpperCase for now
        // Then convert it to an array
        char[] chars = text.toUpperCase().toCharArray();

        for (int i = 0; i < chars.length; i++) {
            // Draw each char
            drawImage(spriteFont.getSymbol(chars[i]), x + (i * spriteFont.getSymbolWidth()),  y);
        }
    }

    /**
     * Send frame data
     */
    protected void sendFramePixelData() {
        BufferedImage b = parentGame.getWindowManager().getFrameImage();
        b.setRGB(0,0, pixelWidth, pixelHeight, pixelData, 0, pixelWidth);
        parentGame.getWindowManager().setFrameImage(b);
    }

    protected void clear() {
        Arrays.fill(pixelData, clearColour);
    }

    /**
     * Set the colour data of the pixel at the X and Y
     * position to the given ARGB value.
     * @param x X position
     * @param y Y position
     * @param argb new colour value
     */
    private void setPixelAt(int x, int y, int argb){
        // Since the pixelData variable is an array, the index
        // of the x and y pixel needs to be calculated using
        // precise algebra
        int actualPosition = x + y * pixelWidth;
        // This checks if the pixel to be drawn is out of bounds
        if (!Util.isInBounds(x, y, pixelWidth, pixelHeight)) return;
        // setAlpha is the transparency of the colour in argb
        int setAlpha;
        // Then we check again
        if (actualPosition <= pixelData.length - 1 && actualPosition >= 0) {
            // Blending of transparency
            /*
            To obtain this value we must think of the colour
            as a 32 bit number  like so: -1
            The integer "-1" is actually full white because of
            how integers are saved in memory. They are saved
            as a binary number like:
            11111111  11111111  11111111  11111111
            |------|  |------|  |------|  |------|
             alpha      red      green      blue

            Full white is -1 because the left-most bit decides
            if the number is negative. But that doesn't matter
            right now. What matters is how we obtain the alpha
            value.

            Considering that the alpha is on the position
            described above, it would be 24 bits from the
            right-most bit therefore we must shift those bits
            to the right therefore obtaining only the alpha
            bits as a whole number

            11111111  11111111  11111111  11111111
            |------|  |-------------------------->
             alpha              24 bits

            00000000  00000000  00000000  11111111
            |-------------------------->  |------|
                       24 bits             alpha
            */
            setAlpha = (argb >> 24) & 0xff;
            // This switch statement is to decide if the pixel
            // is transparent
            switch (setAlpha) {
                // FULL TRANSPARENT
                case 0:
                    // Since the pixel is fully transparent,
                    // don't draw it
                    break;
                // FULL OPAQUE
                case 255:
                    // Since the pixel is opaque, draw it
                    pixelData[actualPosition] = argb;
                    break;
                // Any other case
                default:
                    // This is for dithering
                    // If a int is divisible by 2 then its
                    // rightmost bit will be 0 after a right
                    // bitshift
                    int pixelIndex = (x+y);
                    if ((pixelIndex & ((1 << 1) - 1)) == 0)
                        pixelData[actualPosition] = argb;
            }
        }
    }

    public int getClearColour() { return clearColour; }
    public void setClearColour(int newClearColour) { clearColour = newClearColour; }

    public void notify(Object data) {
        if (data instanceof EntityManager.EntityModifiedEvent) {
            // Listens for any new drawable entities
            EntityManager.EntityModifiedEvent eventData = (EntityManager.EntityModifiedEvent) data;
            String componentType = eventData.getComponent().getType();
            if (componentType == "drawable_component" || componentType == "position_component") updateFilter();
        }
        if (data instanceof WindowManager.WindowResizeEvent){
            // Listens for resize of the window
            WindowManager.WindowResizeEvent eventData = (WindowManager.WindowResizeEvent) data;
            updateWindowSize(eventData.getWindowWidth(), eventData.getWindowHeight());
        }
        if (data instanceof EntityManager.EntitiesClearedEvent){
            // Listens for any clear event
            EntityManager.EntitiesClearedEvent eventData = (EntityManager.EntitiesClearedEvent) data;
            updateFilter();
        }
        if (data instanceof EntityManager.EntityRemovedEvent){
            // Listens for entity removal events
            EntityManager.EntityRemovedEvent eventData = (EntityManager.EntityRemovedEvent) data;
            updateFilter();
        }
    }

    protected void updateFilter(){
        // The standard filter is only for drawable components
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
