package com.spookynebula.battleshipgame.game.components;

import com.spookynebula.battleshipgame.ECS.Component;

import java.util.Arrays;

public class ModalComponent extends Component {
    private String modalMessage;
    private String[] textLines;
    private int lineCount;

    public ModalComponent() {
        enabled = true;
        type = "modal_component";
        ID = 0;

        modalMessage = "";
        textLines = new String[6];
        lineCount = 0;
    }

    public void setModalMessage(String newModalMessage) {
        modalMessage = newModalMessage;
    }

    public void recalculateLines(int maxCharsX, int maxCharsY) {
        //int amountOfLines = Math.min(modalMessage.length() / maxCharsY, textLines.length);
        //int charsLeftInTheMessage = modalMessage.length();
        //lineCount = 0;
        //for (int i = 0; i < amountOfLines; i++) {
        //    int charsOnThisLine = Math.min(charsLeftInTheMessage, maxCharsX);
        //    int lineCharIndexOffset = maxCharsY * i;
        //    setTextLine(i, modalMessage.substring(lineCharIndexOffset, charsOnThisLine + lineCharIndexOffset));
        //    charsLeftInTheMessage -= maxCharsX;
        //    lineCount++;
        //}
        int charsLeftInTheMessage = modalMessage.length();
        int charIndex = 0;
        int lineIndex = 0;
        while (charsLeftInTheMessage > charIndex){
            int charsOnThisLine = Math.min(charsLeftInTheMessage - charIndex, maxCharsX);
            setTextLine(lineIndex, modalMessage.substring(charIndex, charIndex + charsOnThisLine));
            charIndex += charsOnThisLine;
            lineIndex++;
        }
        lineCount = lineIndex;
    }

    public void setTextLine(int index, String lineText) {
        textLines[index] = lineText;
    }
    public String getTextLine(int index) {
        return textLines[index];
    }
    public int getTextLineCount() {
        return lineCount;
    }

    @Override
    public String toString() {
        return "ModalComponent{" +
                "enabled=" + enabled +
                ", type='" + type + '\'' +
                ", ID=" + ID +
                ", modalMessage='" + modalMessage + '\'' +
                ", textLines=" + Arrays.toString(textLines) +
                ", lineCount=" + lineCount +
                '}';
    }
}
