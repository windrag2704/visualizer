package com.bak.visualiser.data;

import java.io.Serializable;

public class Data implements Serializable {
    private final String label;

    public Data(String label) {
        if (label.length() > 20) {
            this.label = label.substring(0,20);
        } else {
            this.label = label;
        }
    }

    public String getLabel() {
        return label;
    }
}
