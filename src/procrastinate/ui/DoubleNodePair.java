//@@author A0121597B
package procrastinate.ui;

import javafx.scene.Node;

public class DoubleNodePair implements Comparable<DoubleNodePair> {

    // ================================================================================
    // Class Variables
    // ================================================================================

    private double height;
    private Node node;

    // ================================================================================
    // DoubleNodePair Constructor
    // ================================================================================

    public DoubleNodePair(double height, Node node) {
        this.height = height;
        this.node = node;
    }

    // ================================================================================
    // DoubleNodePair Methods
    // ================================================================================

    @Override
    public int compareTo(DoubleNodePair o) {
        return Double.valueOf(this.height).compareTo(o.height);
    }

    // ================================================================================
    // Getter Methods
    // ================================================================================

    protected double getHeight() {
        return height;
    }

    protected Node getNode() {
        return node;
    }

}
