package procrastinate.ui;

import javafx.scene.Node;

public class HeightNodePair implements Comparable<HeightNodePair> {

    private double height;
    private Node node;

    public HeightNodePair(double height, Node node) {
        this.height = height;
        this.node = node;
    }

    @Override
    public int compareTo(HeightNodePair o) {
        // TODO Auto-generated method stub
        return Double.valueOf(this.height).compareTo(o.height);
    }

    protected double getHeight() {
        return height;
    }

    protected Node getNode() {
        return node;
    }

}
