package control_flow;

import java.util.HashSet;

public class ConflictGraphNode {
    private String name;
    private HashSet<ConflictGraphNode> neighborhood;
    private int color;

    public ConflictGraphNode(String name) {
        this.name = name;
        neighborhood = new HashSet<>();
        color = 0;
    }

    public int getDegree() {
        return neighborhood.size();
    }

    public void connect(ConflictGraphNode node) {
        neighborhood.add(node);
    }

    public void disconnect(ConflictGraphNode node) {
        neighborhood.remove(node);
    }

    public HashSet<ConflictGraphNode> getNeighborhood() {
        return neighborhood;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConflictGraphNode that = (ConflictGraphNode) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
