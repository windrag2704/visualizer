package com.bak.visualiser.data;

import java.io.Serializable;
import java.util.Stack;

public class ThreadStack implements Cloneable, Serializable {
    private final String threadId;
    private Stack<String> stack = new Stack<>();
    public ThreadStack(String threadId) {
        this.threadId = threadId;
    }
    public String getThreadId() {
        return threadId;
    }
    public void add(String elem) {
        stack.push(elem);
    }

    public void pop() {
        stack.pop();
    }

    public String[] getStack() {
        return stack.toArray(new String[0]);
    }
    @Override
    public ThreadStack clone() {
        try {
            ThreadStack result = (ThreadStack) super.clone();
            result.stack = new Stack<>();
            result.stack.addAll(this.stack);
            return result;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
