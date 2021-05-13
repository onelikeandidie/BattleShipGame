package com.spookynebula.battleshipgame.Core;

import java.util.Objects;

public class Util {
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

    public static boolean isInBounds(int x, int y, int width, int height, int boundsX, int boundsY) {
        if (x + width > boundsX) return false;
        if (x < 0) return false;
        if (y + height > boundsY) return false;
        if (y < 0) return false;
        return true;
    }

    public static boolean isInBounds(int x, int y, int boundsX, int boundsY) {
        if (x >= boundsX) return false;
        if (x < 0) return false;
        if (y >= boundsY) return false;
        if (y < 0) return false;
        return true;
    }

    //public static void setArrayValueFromPosition(Position position, int arrayWidth, int[] array, int value) {
    //    array[position.getX() + position.getY() * arrayWidth] = value;
    //}

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
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
