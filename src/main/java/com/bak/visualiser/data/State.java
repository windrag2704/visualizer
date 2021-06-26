package com.bak.visualiser.data;

public class State {
    private final Graph threadGraph;
    private final Graph funcGraph;
    private final Stacks stacks;

    public State(Graph threadGraph, Graph funcGraph, Stacks stacks) {
        this.threadGraph = threadGraph;
        this.funcGraph = funcGraph;
        this.stacks = stacks;
    }

    public Graph getThreadGraph() {
        return threadGraph;
    }

    public Graph getFuncGraph() {
        return funcGraph;
    }

    public Stacks getStacks() {
        return stacks;
    }
}
