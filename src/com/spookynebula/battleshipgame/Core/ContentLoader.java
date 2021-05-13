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

        textures = new ArrayList<Image>();
        sprites = new ArrayList<Spritesheet>();
        fonts = new ArrayList<SpriteFont>();
    }

    public void loadImage(String path){
        BufferedImage image = loadImageFile(path);

        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
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

        textures.add(imageObject);

        // Memory Management
        image.flush();
    }

    public void loadSpriteSheet(String path, int tileWidth, int tileHeight){
        BufferedImage image = loadImageFile(path);

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

        sprites.add(spritesheet);

        // Memory Management
        image.flush();
    }

    private BufferedImage loadImageFile(String path){
        BufferedImage image = null;

        try {
            image = ImageIO.read(Image.class.getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public Image getTexture(int ID){
        return textures.get(ID);
    }
    public Image getTexture(int spriteSheetID, int x, int y){
        return sprites.get(spriteSheetID).getSprite(x, y);
    }

    public void loadSpriteFont(String path, int tileWidth, int tileHeight) {
        BufferedImage image = loadImageFile(path);

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

        fonts.add(spriteFont);

        // Memory Management
        image.flush();
    }

    public SpriteFont getFont(int index){
        return fonts.get(index);
    }
}
