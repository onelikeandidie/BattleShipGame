package com.spookynebula.battleshipgame.game.components;

import com.spookynebula.battleshipgame.Core.components.TextComponent;

public class TextDisplayComponent extends TextComponent {
    private int maxCharX;
    private int maxCharY;
    private String actualText;

    public TextDisplayComponent(){
        enabled = true;
        type = "text_display_component";
        ID = 0;

        text = "";
        maxCharX = 0;
        maxCharY = 0;
        actualText = "";
    }

    public int getMaxCharX() {
        return maxCharX;
    }

    public void setMaxCharX(int newMaxCharX) {
        maxCharX = newMaxCharX;
    }

    public int getMaxCharY() {
        return maxCharY;
    }

    public void setMaxCharY(int newMaxCharY) {
        maxCharY = newMaxCharY;
    }

    public String getActualText() {
        return actualText;
    }

    public void recalculateText() {
        int maxLength = Math.min(text.length(), maxCharX);
        actualText = text.substring(0, maxLength);
    }

    @Override
    public String toString() {
        return "TextDisplayComponent{" +
                "enabled=" + enabled +
                ", type='" + type + '\'' +
                ", ID=" + ID +
                ", text='" + text + '\'' +
                ", actualText='" + actualText + '\'' +
                ", maxCharX=" + maxCharX +
                ", maxCharY=" + maxCharY +
                '}';
    }
}
