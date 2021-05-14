package com.spookynebula.battleshipgame.Core.gfx;

public class Image {
    private int width, height;
    private int[] pixelData;

    public Image(int imageWidth, int imageHeight, int[] rgbData){
        width = imageWidth;
        height = imageHeight;
        pixelData = rgbData;
    }

    public int getWidth() { return width; }
    public void setWidth(int newWidth) { width = newWidth; }

    public int getHeight() { return height; }
    public void setHeight(int newHeight) { height = newHeight; }

    public int[] getPixelData() { return pixelData; }
    public void setPixelData(int[] newPixelData) { pixelData = newPixelData; }

    public void setPixel(int x, int y, int pixelColourARGB) {
        pixelData[x + y * width] = pixelColourARGB;
    }
}
