package com.bak.visualiser.data;

import java.io.Serializable;

public class Position implements Serializable, Cloneable {
    private final int x;
    private final int y;
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
