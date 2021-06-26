package com.bak.visualiser.data;

import java.io.Serializable;

public class Node extends Element implements Cloneable, Serializable {
    private final String id;
    private final String type;
    private final Data data;
    private Position position;
    private final Boolean draggable;

    public Node(String id, String type, Data data) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.position = null;
        this.draggable = true;
    }

    public Node(String id, String type, Data data, Position position) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.position = position;
        this.draggable = true;
    }

    public Node(String id, String type, Data data, Position position, Boolean draggable) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.position = position;
        this.draggable = draggable;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Data getData() {
        return data;
    }

    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }

    public Node clone() {
        return (Node)super.clone();
    }

    public Boolean getDraggable() {
        return draggable;
    }
}
