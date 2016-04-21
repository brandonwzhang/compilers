package com.bwz6jk2227esl89ahj34.assembly.register_allocation;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyPhysicalRegister.Register;

import java.util.LinkedList;
import java.util.List;

public class Node {
    public Register color;
    public List<Node> neighbors;
    public boolean potentialSpill;

    public Node() {
        this.color = null;
        this.potentialSpill = false;
        this.neighbors = new LinkedList<>();
    }

    public void addNeighbor(Node n) {
        this.neighbors.add(n);
    }

    public void removeNeighbor(Node n) {
        this.neighbors.remove(n);
    }

}
