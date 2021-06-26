package com.bak.visualiser.parser;

import com.bak.visualiser.data.*;

import java.util.HashMap;
import java.util.Stack;

public class CycleGraphBuilder {
    private final HashMap<String, Graph> graphs = new HashMap<>();
    private final HashMap<String, Stack<Node>> nodeStack = new HashMap<>();
    private final HashMap<String, Position> positions = new HashMap<>();
    private final HashMap<String, Integer> counters = new HashMap<>();
    private final HashMap<String, Stack<Graph>> graphStacks = new HashMap<>();
    private final HashMap<String, Stack<Stack<Node>>> nodeStackStacks = new HashMap<>();
    private final HashMap<String, Stack<Position>> positionsStack = new HashMap<>();
    private final HashMap<String, Stack<Integer>> countersStack = new HashMap<>();
    private final HashMap<String, Integer> graphWidths = new HashMap<>();
    private final HashMap<String, Integer> cyclesCount = new HashMap<>();
    private final HashMap<String, Stack<Integer>> widthStack = new HashMap<>();
    private final HashMap<String, Stack<Integer>> cyclesCountStack = new HashMap<>();
    private final Graph synGraph = new Graph();
    private Edge prevStepNotify = null;

    public void addGraph(String thId) {
        Graph graph = new Graph();
        Position position = new Position(0, 0);
        Node node = new Node("Thread_" + thId, "thread",
                new Data("Thread" + thId), position, false);
        graph.add(node);
        graphs.put(thId, graph);
        Stack<Node> stack = new Stack<>();
        stack.push(node);
        nodeStack.put(thId, stack);
        positions.put(thId, position);
        counters.put(thId, 0);
        graphStacks.put(thId, new Stack<>());
        nodeStackStacks.put(thId, new Stack<>());
        positionsStack.put(thId, new Stack<>());
        countersStack.put(thId, new Stack<>());
        graphWidths.put(thId, 0);
        cyclesCount.put(thId, 0);
        widthStack.put(thId, new Stack<>());
        cyclesCountStack.put(thId, new Stack<>());
    }

    public void deleteGraph(String thId) {
        graphs.remove(thId);
        nodeStack.remove(thId);
        positions.remove(thId);
        counters.remove(thId);
        graphStacks.remove(thId);
        nodeStackStacks.remove(thId);
        positionsStack.remove(thId);
        countersStack.remove(thId);
        graphWidths.remove(thId);
        cyclesCount.remove(thId);
        widthStack.remove(thId);
        cyclesCountStack.remove(thId);
    }

    public void enterCycle(String thId) {
        int current = counters.remove(thId) + 1;
        counters.put(thId, current);
        Stack<Node> stack = nodeStack.get(thId);
        Position position = getPosition(thId, true);
        positions.put(thId, position);
        Node node = new Node("CycleEnter_" + current + "_" + thId, "default",
                new Data("CycleEnter"), position, false);
        Node prevNode = stack.peek();
        Edge edge = new Edge("Edge_" + node.getId() + "_" + prevNode.getId(),
                prevNode.getId(), node.getId(), "");
        Graph curr = graphs.get(thId);
        curr.add(node);
        curr.add(edge);
        stack.push(node);
        int cnt = cyclesCount.get(thId) + 1;
        cyclesCount.put(thId, cnt);
        if (cnt > graphWidths.get(thId)) {
            graphWidths.put(thId, cnt);
        }
    }

    public void exitCycle(String thId) {
        int current = counters.get(thId) + 1;
        counters.put(thId, current);
        Stack<Node> stack = nodeStack.get(thId);
        Node prevNode = stack.peek();
        Position position = getPosition(thId, false);
        positions.put(thId, position);
        Node node = new Node("CycleExit_" + current + "_" + thId, "default",
                new Data("CycleExit"), position, false);
        Edge edge = new Edge("Edge_" + node.getId() + "_" + prevNode.getId(),
                prevNode.getId(), node.getId(), "");
        Graph curr = graphs.get(thId);
        curr.add(node);
        curr.add(edge);
        stack.push(node);
        cyclesCount.put(thId, cyclesCount.get(thId) - 1);
    }

    public void startCycle(String thId) {
        Stack<Node> stack = nodeStack.get(thId);
        int current = counters.get(thId);
        Position position = getPosition(thId, false);
        Graph curr = graphs.get(thId);
        Node node;
        if (stack.peek().getId().split("_")[0].equals("CycleEnter")) {
            node = new Node("CycleStart_" + current + "_" + thId, "default",
                    new Data("CycleStart"), position, false);
        } else {
            do {
                node = stack.pop();
                curr.deleteEdges(node.getId());
                curr.delete(node.getId(), false);
            } while (!node.getId().split("_")[0].equals("CycleStart"));
        }
        positions.put(thId, node.getPosition());
        Edge edge = new Edge("Edge_" + node.getId() + "_" + stack.peek().getId(),
                stack.peek().getId(), node.getId(), "");
        curr.add(node);
        curr.add(edge);
        stack.push(node);
    }

    public void endCycle(String thId) {
        int current = counters.get(thId);
        Stack<Node> stack = nodeStack.get(thId);
        Position position = getPosition(thId, false);
        Graph curr = graphs.get(thId);
        Node node = new Node("CycleEnd_" + current + "_" + thId, "default",
                new Data("CycleEnd"), position, false);
        Edge edge = new Edge("Edge_" + node.getId() + "_" + stack.peek().getId(),
                stack.peek().getId(), node.getId(), "");
        positions.put(thId, node.getPosition());
        curr.add(node);
        curr.add(edge);
        stack.push(node);
    }

    private Position getPosition(String thId, boolean isCycleEnter) {
        Stack<Node> stack = nodeStack.get(thId);
        Position prevPosition = positions.get(thId);
        String prevId = stack.peek().getId();
        String prevNodeName = prevId.split("_")[0];
        int x = prevPosition.getX();
        int y = prevPosition.getY() + 50;
        if (isCycleEnter) {
            x += 50;
        }
        if (prevNodeName.equals("CycleExit")) {
            x -= 50;
        }
        return new Position(x, y);
    }

    public void enterFunc(String thId, String funcName) {
        int current = counters.get(thId) + 1;
        counters.put(thId, current);
        Stack<Node> stack = nodeStack.get(thId);
        Position position = getPosition(thId, false);
        Graph curr = graphs.get(thId);
        Node node = new Node("FuncCall_" + thId + "_" + funcName + "_" + current,
                "default", new Data(funcName), position, false);
        Edge edge = new Edge("Edge_" + node.getId() + "_" + stack.peek().getId(),
                stack.peek().getId(), node.getId(), "");
        curr.add(node);
        curr.add(edge);
        stack.push(node);
        positions.put(thId, node.getPosition());

        // Сохранение состояния выполнения текущего метода
        graphStacks.get(thId).push(curr);
        nodeStackStacks.get(thId).push(stack);
        countersStack.get(thId).push(current);
        positionsStack.get(thId).push(position);
        widthStack.get(thId).push(graphWidths.get(thId));
        cyclesCountStack.get(thId).push(cyclesCount.get(thId));

        // Создание нового графа
        curr = new Graph();
        stack = new Stack<>();
        current = 0;
        position = new Position(0, 0);

        graphs.put(thId, curr);
        nodeStack.put(thId, stack);
        counters.put(thId, current);
        positions.put(thId, position);
        graphWidths.put(thId, 0);
        cyclesCount.put(thId, 0);

        node = new Node("Thread_" + thId, "thread", new Data("Thread" + thId), position, false);
        curr.add(node);
        stack.add(node);
        position = getPosition(thId, false);
        node = new Node("Func_" + thId, "func", new Data(funcName), position, false);
        edge = new Edge("Edge_" + node.getId() + "_" + stack.peek().getId(),
                stack.peek().getId(), node.getId(), "");
        curr.add(node);
        curr.add(edge);
        stack.push(node);
        positions.put(thId, position);
    }

    public void exitFunc(String thId) {
        graphs.put(thId, graphStacks.get(thId).pop());
        nodeStack.put(thId, nodeStackStacks.get(thId).pop());
        counters.put(thId, countersStack.get(thId).pop());
        positions.put(thId, positionsStack.get(thId).pop());
        cyclesCount.put(thId, cyclesCountStack.get(thId).pop());
        graphWidths.put(thId, widthStack.get(thId).pop());
    }

    public void notified(String thId, String nodeId) {
        String threadId = "Thread_" + thId;
        String edgeId = "Edge_" + thId + "_" + nodeId;
        String label = "notify";
        prevStepNotify = new Edge(edgeId, nodeId, threadId, label);
        synGraph.add(prevStepNotify);
    }

    public void endNotify() {
        if (prevStepNotify != null) {
            synGraph.delete(prevStepNotify.getId(), true);
            prevStepNotify = null;
        }
    }

    public void synWait(String thId, String synNode, String nodeName) {
        String threadId = "Thread_" + thId;
        synGraph.delete(synNode, false);
        Node node = new Node(synNode, "input", new Data(nodeName), null);
        Edge edge = new Edge("Edge_" + thId + "_" + synNode, synNode, threadId, "wait");
        synGraph.add(node);
        synGraph.add(edge);
    }

    public void synEnter(String thId, String synNode) {
        String threadId = "Thread_" + thId;
        String edgeId = "Edge_" + thId + "_" + synNode;
        synGraph.delete(edgeId, true);
        Edge edge = new Edge(edgeId, synNode, threadId, "enter");
        synGraph.add(edge);
    }

    public void synExit(String thId, String synNode) {
        String edgeId = "Edge_" + thId + "_" + synNode;
        synGraph.delete(edgeId, true);
        synGraph.deleteIfAlone(synNode);
    }

    public Graph getGraph() {
        Graph result = new Graph();
        int x = 0, y = 100;
        for (Graph graph : graphs.values()) {
            result.addAllEdges(graph.getEdges());
            result.addAllNodes(graph.getNodes(), x, y);
            x = x + 200 + graphWidths.get(graph.getNodes()[0].getId().split("_")[1]) * 50;
        }
        x = 0;
        y = 0;
        for (Node node : synGraph.getNodes()) {
            Node newNode = new Node(node.getId(), node.getType(), node.getData(), new Position(x, y));
            x += 200;
            result.add(newNode);
        }
        result.addAllEdges(synGraph.getEdges());
        return result;
    }

}
