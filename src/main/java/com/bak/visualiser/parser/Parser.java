package com.bak.visualiser.parser;

import com.bak.visualiser.data.*;

import java.io.*;
import java.util.HashMap;

public class Parser implements Serializable {

    private final HashMap<Integer, Graph> graphs = new HashMap<>();
    private final HashMap<Integer, Stacks> stacks = new HashMap<>();
    private final HashMap<Integer, Graph> funcGraphs = new HashMap<>();
    private transient Edge prevStepNotify = null;
    private transient BufferedReader in;
    private int size = 0;
    private final transient CycleGraphBuilder cgb = new CycleGraphBuilder();

    public Parser(File file) {
        try {
            this.in = new BufferedReader(new FileReader(file));
            parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Parser(InputStream input) {
        this.in = new BufferedReader(new InputStreamReader(input));
        parse();
    }

    public void parse() {
        String s;
        int step = 1;
        graphs.put(0, new Graph());
        stacks.put(0, new Stacks());
        try {
            while ((s = in.readLine()) != null) {
                parseLine(s, step);
                step++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        size = step;
    }

    private void parseLine(String s, int step) {
        String[] parts = s.split(" ");
        String threadId = parts[0];
        String action = parts[1];
        Graph prev = graphs.get(step - 1);
        Graph current = prev.clone();
        Stacks prevStack = stacks.get(step - 1);
        Stacks currentStack = prevStack.clone();
        String nodeId, type, edgeId, target, source, label, funcName;
        Data data;
        if (prevStepNotify != null) {
            current.delete(prevStepNotify.getId(), true);
            prevStepNotify = null;
            cgb.endNotify();
        }
        switch (action) {
            case "MAINTHREAD":
                nodeId = "Thread" + threadId;
                type = "input";
                data = new Data("Main Thread");
                current.add(new Node(nodeId, type, data, null));
                currentStack.addStack(threadId);
                cgb.addGraph(threadId);
                break;
            case "THREADRUN":
                nodeId = "Thread" + parts[2];
                type = "default";
                data = new Data(nodeId);
                current.add(new Node(nodeId, type, data, null));
                edgeId = "Thread_e_" + parts[2];
                source = "Thread" + threadId;
                target = nodeId;
                label = "";
                current.add(new Edge(edgeId, source, target, label));
                currentStack.addStack(parts[2]);
                cgb.addGraph(parts[2]);
                break;
            case "THREADEND":
                nodeId = "Thread" + parts[0];
                edgeId = "Thread_e_" + parts[0];
                current.delete(edgeId, true);
                current.delete(nodeId, false);
                currentStack.removeStack(threadId);
                cgb.deleteGraph(threadId);
                break;
            case "NOTIFY":
            case "NOTIFYALL":
                nodeId = "SYN_" + parts[2];
                source = "Thread"+threadId;
                edgeId = "Edge_" + source + "_" + nodeId;
                label = "notify";
                prevStepNotify = new Edge(edgeId, source, nodeId, label);
                current.add(prevStepNotify);
                cgb.notified(threadId, nodeId);
            case "PREWAIT":
            case "SYNWAIT":
                nodeId = "SYN_" + parts[2];
                current.delete(nodeId, false);
                type = "output";
                data = new Data(parts[3]);
                Node synWaitNode = new Node(nodeId, type, data, null);
                current.add(synWaitNode);
                edgeId = "SYN_" + threadId + "_" + parts[2];
                source = "Thread" + threadId;
                target = nodeId;
                label = "wait";
                current.add(new Edge(edgeId, source, target, label));
                cgb.synWait(threadId, synWaitNode.getId(), parts[3]);
                break;
            case "SYNENTER":
                edgeId = "SYN_" + threadId + "_" + parts[2];
                current.delete(edgeId, true);
                source = "Thread" + threadId;
                target = "SYN_" + parts[2];
                label = "enter";
                Edge synEnterEdge = new Edge(edgeId, source, target, label);
                current.add(synEnterEdge);
                cgb.synEnter(threadId, target);
                break;
            case "POSTWAIT":
            case "SYNEXIT":
                edgeId = "SYN_" + threadId + "_" + parts[2];
                current.delete(edgeId, true);
                current.deleteIfAlone("SYN_" + parts[2]);
                cgb.synExit(threadId, "SYN_" + parts[2]);
                break;
            case "READSTART":
                nodeId = "FILE" + parts[2];
                type = "output";
                data = new Data(parts[3]);
                current.add(new Node(nodeId, type, data, null));
                edgeId = "FILE" + threadId + "_" + parts[2];
                source = "Thread" + threadId;
                target = nodeId;
                label = "read";
                current.add(new Edge(edgeId, source, target, label));
                break;
            case "WRITEEND":
            case "READEND":
                edgeId = "FILE" + threadId + "_" + parts[2];
                current.delete(edgeId, true);
                current.deleteIfAlone("FILE" + parts[2]);
                break;
            case "WRITESTART":
                nodeId = "FILE" + parts[2];
                type = "output";
                data = new Data(parts[3]);
                current.add(new Node(nodeId, type, data, null));
                edgeId = "FILE" + threadId + "_" + parts[2];
                source = "Thread" + threadId;
                target = nodeId;
                label = "write";
                current.add(new Edge(edgeId, source, target, label));
                break;
            case "FUNCENTER":
                threadId = parts[0];
                funcName = parts[2];
                currentStack.add(threadId, funcName);
                cgb.enterFunc(threadId, funcName);
                break;
            case "FUNCEND":
                threadId = parts[0];
                currentStack.pop(threadId);
                cgb.exitFunc(threadId);
                break;
            case "CYCLEENTER":
                cgb.enterCycle(threadId);
                break;
            case "CYCLEEXIT":
                cgb.exitCycle(threadId);
                break;
            case "CYCLESTART":
                cgb.startCycle(threadId);
                break;
            case "CYCLEEND":
                cgb.endCycle(threadId);
                break;
            default:
                break;
        }
        funcGraphs.put(step, cgb.getGraph());
        graphs.put(step, current);
        stacks.put(step, currentStack);
    }

    public Graph getGraph(int step) {
        return graphs.get(step);
    }

    public Stacks getStacks(int step) {
        return stacks.get(step);
    }

    public Graph getFunc(Integer step) {
        return funcGraphs.get(step);
    }

    public int getSize() {
        return size;
    }
}
