package com.spookynebula.battleshipgame.Core.gfx;

public class Spritesheet {
    private int width, height;
    private int[] pixelData;
    private int spriteWidth, spriteHeight;
    private Image[] sprites;
    private int spriteAmountX, spriteAmountY;

    public Spritesheet(int imageWidth, int imageHeight, int[] rgbData, int tileWidth, int tileHeight){
        width = imageWidth;
        height = imageHeight;
        pixelData = rgbData;
        spriteWidth = tileWidth;
        spriteHeight = tileHeight;
        spriteAmountX = width / spriteWidth;
        spriteAmountY = height / spriteHeight;
        sprites = new Image[spriteAmountX * spriteAmountY];
    }

    public int getWidth() { return width; }

    public void setWidth(int newWidth) { width = newWidth; }

    public int getHeight() { return height; }

    public void setHeight(int newHeight) { height = newHeight; }

    public int[] getPixelData() { return pixelData; }

    public void setPixelData(int[] newPixelData) { pixelData = newPixelData; }

    public int getSpriteWidth() { return spriteWidth; }

    public void setSpriteWidth(int newWidth) { spriteWidth = newWidth; }

    public int getSpriteHeight() { return spriteHeight; }

    public void setSpriteHeight(int newHeight) { spriteHeight = newHeight; }

    public Image getSprite(int x, int y){
        int[] spriteData = new int[spriteWidth * spriteHeight];
        int spriteIndex = x + y * spriteAmountX;
        // Check if the sprite was already made into an image to reduce memory footprint
        if (sprites[spriteIndex] == null){
            int offsetX, offsetY;
            offsetX = x * spriteWidth;
            offsetY = y * spriteHeight;
            // Flip through each pixel starting with the
            int pixelIndex = 0;
            int splitX = 0, splitY = 0;
            // TODO: FIX WHAT THE FUCK
            // This is fixed but I don't know what it's doing
            while (splitY < spriteHeight) {
                while (splitX < spriteWidth) {
                    int pixelX = (splitX + offsetX);
                    int pixelY = (splitY + offsetY) * width;
                    spriteData[pixelIndex] = pixelData[pixelX + pixelY];
                    if (pixelIndex < spriteData.length - 1)
                        pixelIndex++;
                    splitX++;
                }
                splitY++;
                splitX = 0;
            }
            Image sprite = new Image(spriteWidth, spriteHeight, spriteData);
            sprites[spriteIndex] = sprite;
            return sprite;
        } else {
            return sprites[spriteIndex];
        }
    }
}
