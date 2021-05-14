package com.spookynebula.battleshipgame.Core.gfx;

public class SpriteFont {
    private int width, height;
    private int[] pixelData;
    private int symbolWidth, symbolHeight;
    private Image[] symbols;
    private int symbolAmountX, symbolAmountY;

    public SpriteFont(int imageWidth, int imageHeight, int[] rgbData, int tileWidth, int tileHeight){
        width = imageWidth;
        height = imageHeight;
        pixelData = rgbData;
        symbolWidth = tileWidth;
        symbolHeight = tileHeight;
        symbolAmountX = width / symbolWidth;
        symbolAmountY = height / symbolHeight;
        symbols = new Image[symbolAmountX * symbolAmountY];
    }

    public int getWidth() { return width; }
    public void setWidth(int newWidth) { width = newWidth; }

    public int getHeight() { return height; }
    public void setHeight(int newHeight) { height = newHeight; }

    public int[] getPixelData() { return pixelData; }
    public void setPixelData(int[] newPixelData) { pixelData = newPixelData; }

    public int getSymbolWidth() { return symbolWidth; }
    public void setSymbolWidth(int newWidth) { symbolWidth = newWidth; }

    public int getSymbolHeight() { return symbolHeight; }
    public void setSymbolHeight(int newHeight) { symbolHeight = newHeight; }

    /**
     * Returns the Image of the symbol of the character given
     * @param symbol 'F' single quote char
     * @return Only the Image of the symbol
     */
    public Image getSymbol(char symbol){
        // Retrieve the Image pixel data
        int[] spriteData = new int[symbolWidth * symbolHeight];
        // Get the symbol
        int index = Symbol.get(symbol);
        // Check if the sprite was already made into an image to reduce memory footprint
        if (symbols[index] == null){
            int offsetX, offsetY;
            int y = 0;
            offsetX = index * symbolWidth;
            // Disgusting way of doing this
            while (offsetX > width){
                offsetX = offsetX - width;
                y++;
            }
            offsetY = y * symbolHeight;
            // Flip through each pixel starting with the first
            int pixelIndex = 0;
            int splitX = 0, splitY = 0;
            while (splitY < symbolHeight) {
                while (splitX < symbolWidth) {
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
            // Create the Image of the Symbol
            Image sprite = new Image(symbolWidth, symbolHeight, spriteData);
            // Add it to the symbols array to use later
            // This kinda saves compute time
            symbols[index] = sprite;
            return sprite;
        } else {
            return symbols[index];
        }
    }

    private enum Symbol {
        // I'm going to hell for this
        A('A'), B('B'), C('C'), D('D'), E('E'), F('F'), G('G'), H('H'), I('I'), J('J'), K('K'), L('L'), M('M'),
        N('N'), O('O'), P('P'), Q('Q'), R('R'), S('S'), T('T'), U('U'), V('V'), W('W'), X('X'), Y('Y'), Z('Z'),
        Zero('0'), One('1'), Two('2'), Three('3'), Four('4'), Five('5'), Six('6'), Seven('7'), Eight('8'),
        Nine('9'), Dot('.'), Exclamation('!'), QuestionMark('?'), OpenParenthesis('('), CloseParenthesis(')'),
        OpenSquare('['), CloseSquare(']'), OpenBracket('{'), CloseBracket('}'), Slash('/'), Dash('-'),
        SingleQuote('\''), Quote('"'), Space(' ');

        private final char hell;

        Symbol(char symbol) {
            hell = symbol;
        }

        public static int get(final char symbol){
            int i;
            for (i = 0; i < Symbol.values().length; i++) {
                char value = Symbol.values()[i].hell;
                if (symbol == value) {
                    return i;
                }
            }
            return i;
        }
    }
}
