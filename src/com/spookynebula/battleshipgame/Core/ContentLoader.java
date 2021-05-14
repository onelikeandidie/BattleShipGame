package com.spookynebula.battleshipgame.Core;

import com.spookynebula.battleshipgame.Core.gfx.Image;
import com.spookynebula.battleshipgame.Core.gfx.SpriteFont;
import com.spookynebula.battleshipgame.Core.gfx.Spritesheet;
import com.spookynebula.battleshipgame.GameContainer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContentLoader {
    private GameContainer parentGame;

    private List<Image> textures;
    private List<Spritesheet> sprites;
    private List<SpriteFont> fonts;

    public ContentLoader(GameContainer gameContainer){
        parentGame = gameContainer;

        // Initialize lists
        textures = new ArrayList<Image>();
        sprites = new ArrayList<Spritesheet>();
        fonts = new ArrayList<SpriteFont>();
    }

    /**
     * Loads an image to the Image list
     * @param path The Absolute Relative path of the package.
     *             Something like "/com/spookynebula/game/assets/image.png"
     */
    public void loadImage(String path){
        BufferedImage image = loadImageFile(path);
        // Get the width and height
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        // Create the Image object
        Image imageObject = new Image(
                originalWidth,
                originalHeight,
                image.getRGB(
                        0, 0,
                        originalWidth,
                        originalHeight,
                        null,
                        0,
                        originalWidth));
        // Add the Image to the texture list
        textures.add(imageObject);
        // Memory Management
        image.flush();
    }

    /**
     * Loads an image to the Sprites list.
     * The Image is separated into sprites of the tileWidth and tileHeight
     * @param path The Absolute Relative path of the package.
     *             Something like "/com/spookynebula/game/assets/image.png"
     * @param tileWidth The width of each sprite
     * @param tileHeight The height of each sprite
     */
    public void loadSpriteSheet(String path, int tileWidth, int tileHeight){
        BufferedImage image = loadImageFile(path);
        // Get the width and height
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        Spritesheet spritesheet = new Spritesheet(
                originalWidth,
                originalHeight,
                image.getRGB(0,0,
                        originalWidth,
                        originalHeight,
                        null,
                        0,
                        originalWidth),
                tileWidth,
                tileHeight);
        // Add the Spritesheet to the sprite list
        sprites.add(spritesheet);
        // Memory Management
        image.flush();
    }

    /**
     * Loads and Image to the fonts list.
     * The Image is separated into Symbols of "tileWidth" and "tileHeight"
     * To know what order to put your symbols to be loaded, check the
     * SpriteFont.Symbol enum.
     * @param path The Absolute Relative path of the package.
     *             Something like "/com/spookynebula/game/assets/image.png"
     * @param tileWidth The width of each symbol
     * @param tileHeight The height of each symbol
     */
    public void loadSpriteFont(String path, int tileWidth, int tileHeight) {
        BufferedImage image = loadImageFile(path);
        // Get the width and height
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        SpriteFont spriteFont = new SpriteFont(
                originalWidth,
                originalHeight,
                image.getRGB(0,0,
                        originalWidth,
                        originalHeight,
                        null,
                        0,
                        originalWidth),
                tileWidth,
                tileHeight);
        // Add the SpriteFont to the font list
        fonts.add(spriteFont);
        // Memory Management
        image.flush();
    }

    /**
     * Loads and returns an Image file
     * @param path The Absolute Relative path of the package.
     *             Something like "/com/spookynebula/game/assets/image.png"
     * @return A BufferedImage instance
     */
    private BufferedImage loadImageFile(String path){
        BufferedImage image = null;

        try {
            image = ImageIO.read(Image.class.getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
     * Returns a texture. The ID is the order it was loaded in.
     * @param ID Index of the Texture in the textures list
     * @return An Image
     */
    public Image getTexture(int ID){
        return textures.get(ID);
    }

    /**
     * Returns a texture. The ID is the order it was loaded in.
     * This method returns a sprite from a SpriteSheet on the X and Y
     * positions
     * @param spriteSheetID Index of the SpriteSheet in the sprites list
     * @param x Sprite X position
     * @param y Sprite Y position
     * @return An Image of only the sprite
     */
    public Image getTexture(int spriteSheetID, int x, int y){
        return sprites.get(spriteSheetID).getSprite(x, y);
    }

    /**
     * Returns a SpriteFont. The ID is the order it was loaded in.
     * @param index Index of the SpriteFont in the fonts list
     * @return SpriteFont
     */
    public SpriteFont getFont(int index){
        return fonts.get(index);
    }
}
