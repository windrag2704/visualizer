package com.bak.visualiser.data;

import java.io.Serializable;

public abstract class Element implements Cloneable, Serializable {
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
