package com.spookynebula.battleshipgame.Core;

import com.spookynebula.battleshipgame.ECS.*;
import com.spookynebula.battleshipgame.GameContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class WindowManager implements ISubscribeSystem{
    private GameContainer parentGame;

    private boolean enabled;
    protected List<ISubscriber> subscribers;

    protected JFrame frame;
    protected BufferedImage frameImage;

    protected Canvas canvas;
    protected BufferStrategy bufferStrategy;
    protected Graphics graphics;

    protected int windowWidth;
    protected int windowHeight;

    protected float windowScale;
    protected String windowTitle;

    private final Cursor BLANK_CURSOR;

    public WindowManager(int width, int height, float scale, String title) {
        enabled = true;
        subscribers = new ArrayList<ISubscriber>();

        windowWidth = width;
        windowHeight = height;
        windowTitle = title;
        windowScale = scale;

        // Create frame buffer
        frameImage = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB);
        canvas = new Canvas();
        Dimension dim = new Dimension((int) (windowWidth * windowScale), (int) (windowHeight * windowScale));
        canvas.setPreferredSize(dim);
        canvas.setMaximumSize(dim);
        canvas.setMinimumSize(dim);
        // Create the window
        frame = new JFrame(windowTitle);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(canvas, BorderLayout.CENTER);
        // pack() automatically resizes the frame to the elements inside (the canvas)
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        // Create the strategy to draw graphics to?
        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();
        graphics = bufferStrategy.getDrawGraphics();

        // Create the invisible cursor
        BufferedImage cursorImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor tempCursor;
        try {
            tempCursor = Toolkit.getDefaultToolkit().createCustomCursor(null, null, null);
        } catch (Exception err) {
            tempCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "blank cursor");
        }
        BLANK_CURSOR = tempCursor;
    }

    // Draw the frame
    public void drawWindow(){
        graphics.drawImage(frameImage, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
        bufferStrategy.show();
    }

    /**
     * Updates the dimensions of the frame.
     * Calculates new dimensions automatically.
     */
    public void updateWindowSize(){
        // Dispose of the current frame
        frameImage.flush();
        frameImage = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB);
        // Create the canvas dimensions to match the new Window size
        Dimension dim = new Dimension((int) (windowWidth * windowScale), (int) (windowHeight * windowScale));
        canvas.setPreferredSize(dim);
        canvas.setMaximumSize(dim);
        canvas.setMinimumSize(dim);
        // Resize frame
        // pack() automatically resizes the frame to the elements inside (the canvas)
        frame.pack();
        // Create the strategy to draw graphics to?
        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();
        graphics = bufferStrategy.getDrawGraphics();
        // Notify Subscribers
        notifySubscriber(new WindowResizeEvent(frame, frameImage, canvas, bufferStrategy, graphics, windowWidth, windowHeight, windowScale));
    }

    /**
     * Updates the title of the Window
     */
    public void updateTitle(){
        frame.setTitle(windowTitle);
    }

    public int getWindowWidth() { return windowWidth; }
    public int getWindowHeight() { return windowHeight; }
    public String getWindowTitle() { return windowTitle; }
    public float getWindowScale() { return windowScale;  }

    public void setWindowWidth(int newWindowWidth) { windowWidth = newWindowWidth; }
    public void setWindowHeight(int newWindowHeight) { windowHeight = newWindowHeight; }
    public void setWindowTitle(String newWindowTitle) { windowTitle = newWindowTitle; }
    public void setWindowScale(float newWindowScale) { windowScale = newWindowScale; }

    public Canvas getCanvas() { return canvas; }

    public BufferedImage getFrameImage() { return frameImage; }

    public void setFrameImage(BufferedImage newFrameImage) { frameImage = newFrameImage; }

    /**
     * Hides system cursor
     */
    public void cursorHide(){
        frame.getContentPane().setCursor(BLANK_CURSOR);
    }

    /**
     * Shows system cursor
     */
    public void cursorShow(){
        frame.setCursor(Cursor.getDefaultCursor());
    }

    private void notifySubscriber(WindowResizeEvent windowResizeEvent){
        for (ISubscriber subscriber : subscribers) {
            subscriber.notify(windowResizeEvent);
        }
    }

    public void subscribe(ISubscriber newSubscriber) {
        subscribers.add(newSubscriber);
    }

    public void unsubscribe(ISubscriber subscriber){
        subscribers.remove(subscriber);
    }

    public boolean isEnabled() { return enabled; }

    public void disable() { enabled = false; }

    public void enable() { enabled = true; }

    /**
     * This event is created whenever the window is resized programmatically
     */
    public class WindowResizeEvent {
        private final JFrame frame;
        private final BufferedImage frameImage;
        private final Canvas canvas;
        private final BufferStrategy bufferStrategy;
        private final Graphics graphics;
        private final int windowWidth;
        private final int windowHeight;
        private final float windowScale;

        public WindowResizeEvent(
                JFrame newFrame,
                BufferedImage newFrameImage,
                Canvas newCanvas,
                BufferStrategy newBufferStrategy,
                Graphics newGraphics,
                int newWindowWidth,
                int newWindowHeight,
                float newWindowScale)
        {
            frame = newFrame;
            frameImage = newFrameImage;
            canvas = newCanvas;
            bufferStrategy = newBufferStrategy;
            graphics = newGraphics;
            windowWidth = newWindowWidth;
            windowHeight = newWindowHeight;
            windowScale = newWindowScale;
        }

        public JFrame getFrame() { return frame; }
        public BufferedImage getFrameImage() { return frameImage; }
        public Canvas getCanvas() { return canvas; }
        public BufferStrategy getBufferStrategy() { return bufferStrategy; }
        public Graphics getGraphics() { return graphics; }

        public int getWindowWidth(){ return windowWidth; }
        public int getWindowHeight(){ return windowHeight; }
        public float getWindowScale(){ return windowScale; }
    }
}
