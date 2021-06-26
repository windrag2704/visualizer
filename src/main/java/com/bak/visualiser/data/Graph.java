package com.bak.visualiser.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Graph implements Cloneable, Serializable {
    private ArrayList<Edge> edges = new ArrayList<>();
    private ArrayList<Node> nodes = new ArrayList<>();

    public Graph() {

    }

    public Graph(Edge[] edges, Node[] nodes) {
        this.edges.addAll(Arrays.asList(edges));
        this.nodes.addAll(Arrays.asList(nodes));
    }

    public void add(Element elem) {
        if (elem instanceof Edge) {
            edges.add((Edge) elem);
        } else {
            nodes.add((Node) elem);
        }
    }

    public void delete(String id, Boolean isEdge) {
        if (isEdge) {
            Edge edge = null;
            for (Edge value : edges) {
                if (value.getId().equals(id)) {
                    edge = value;
                }
            }
            if (edge != null) {
                edges.remove(edge);
            }
        } else {
            Node node = null;
            for (Node value : nodes) {
                if (value.getId().equals(id)) {
                    node = value;
                }
            }
            if (node != null) {
                nodes.remove(node);
            }
        }
    }

    public Edge[] getEdges() {
        return edges.toArray(new Edge[0]);
    }

    public Node[] getNodes() {
        return nodes.toArray(new Node[0]);
    }

    public Graph getSynGraph(String synId) {
        Graph graph = new Graph();
        for (Node node: nodes) {
            if (node.getId().equals(synId)) {
                Node newNode = new Node("MUT_" + node.getId(),node.getType(),
                        node.getData(), node.getPosition(),
                        node.getDraggable());
                graph.add(newNode);
            }
        }
        for (Edge edge: edges) {
            if (edge.getTarget().equals(synId)) {
                Edge newEdge = new Edge(edge.getId(), "MUT_" + edge.getSource(),
                        "MUT_" + edge.getTarget(), edge.getLabel());
                graph.add(newEdge);
                for (Node node: nodes) {
                    if (node.getId().equals(edge.getSource())) {
                        Node newNode = new Node("MUT_" + node.getId(),node.getType(),
                                node.getData(), node.getPosition(),
                                node.getDraggable());
                        graph.add(newNode);
                    }
                }
            }
        }
        return graph;
    }

    public Graph clone() {
        try {
            Graph clone = (Graph) super.clone();
            clone.edges = new ArrayList<>(this.edges);
            clone.nodes = new ArrayList<>(this.nodes);
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteIfAlone(String nodeId) {
        boolean alone = true;
        for (Edge edge : edges) {
            if (edge.getTarget().equals(nodeId) || edge.getSource().equals(nodeId)) {
                alone = false;
                break;
            }
        }
        if (alone) {
            delete(nodeId, false);
        }
    }

    public void deleteEdges(String id) {
        ArrayList<Edge> newEdges = new ArrayList<>();
        for (Edge edge : edges) {
            if (!(edge.getTarget().equals(id) || edge.getSource().equals(id))) {
                newEdges.add(edge);
            }
        }
        edges = newEdges;
    }

    public void addAllNodes(Node[] nodes) {
        this.nodes.addAll(Arrays.asList(nodes));
    }

    public void addAllNodes(Node[] nodes, int offX, int offY) {
        for (Node node: nodes) {
            Position position = node.getPosition();
            Position newPosition = new Position(position.getX() + offX, position.getY() + offY);
            Node newNode = node.clone();
            newNode.setPosition(newPosition);
            this.nodes.add(newNode);
        }
    }

    public void addAllEdges(Edge[] edges) {
        this.edges.addAll(Arrays.asList(edges));
    }
}
