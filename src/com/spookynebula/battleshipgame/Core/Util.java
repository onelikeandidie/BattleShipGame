package com.spookynebula.battleshipgame.Core;

/**
 * Util functions used by the Engine
 */
public class Util {
    /**
     * Returns a Position from an index.
     * This is calculated using the array's max width and height
     * @param index Index on the array
     * @param arrayWidth Max X position
     * @param arrayHeight Max Y position
     * @return A final Position object
     */
    public static Position getPositionFromIndexOfArray(int index, int arrayWidth, int arrayHeight) {
        int x = index;
        int y = 0;
        while (x >= arrayWidth){
            x = x - arrayWidth;
            y++;
        }
        if (y >= arrayHeight) y = arrayHeight;
        return new Position(x, y);
    }

    /**
     * Returns true if the x and y with a width and height is
     * within bounds
     * @param x X position
     * @param y Y position
     * @param width Width of the object to check
     * @param height Height of the object to check
     * @param boundsX Width of the bounds
     * @param boundsY Height of the bounds
     * @return True if is within bounds
     */
    public static boolean isInBounds(int x, int y, int width, int height, int boundsX, int boundsY) {
        if (x + width > boundsX) return false;
        if (x < 0) return false;
        if (y + height > boundsY) return false;
        if (y < 0) return false;
        return true;
    }

    /**
     * Returns true if the x and y is within bounds
     * @param x X position
     * @param y Y position
     * @param boundsX Width of the bounds
     * @param boundsY Height of the bounds
     * @return True if is within bounds
     */
    public static boolean isInBounds(int x, int y, int boundsX, int boundsY) {
        if (x >= boundsX) return false;
        if (x < 0) return false;
        if (y >= boundsY) return false;
        if (y < 0) return false;
        return true;
    }

    /**
     * Simple class that describes a X and Y position.
     * These are final and cannot be modified.
     */
    public static class Position {
        private final int x, y;

        public Position(int newX, int newY){
            x = newX;
            y = newY;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        /**
         * Returns true if the X and Y matched with the Object
         * @param o Object to compare
         * @return
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            // Check if class is the same
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            // Check if the position is the same
            return x == position.x && y == position.y;
        }

        @Override
        public String toString() {
            return "Position{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
