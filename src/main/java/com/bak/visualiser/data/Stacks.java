package com.bak.visualiser.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Stacks implements Cloneable, Serializable {
    private HashMap<String, ThreadStack> stacks = new HashMap<>();

    public void addStack(String thId) {
        stacks.put(thId, new ThreadStack(thId));
    }
    public void removeStack(String thId) {
        stacks.remove(thId);
    }
    public void add(String thId, String elem) {
        ThreadStack curr = stacks.get(thId);
        curr.add(elem);
    }
    public void pop(String thId) {
        ThreadStack curr = stacks.get(thId);
        curr.pop();
    }
    public ThreadStack[] getStacks() {
        return stacks.values().toArray(new ThreadStack[0]);
    }
    public Stacks clone() {
        try {
            Stacks result = (Stacks) super.clone();
            result.stacks = new HashMap<>();
            for (Map.Entry<String, ThreadStack> stack : stacks.entrySet()) {
                result.stacks.put(stack.getKey(), stack.getValue().clone());
            }
            return result;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
