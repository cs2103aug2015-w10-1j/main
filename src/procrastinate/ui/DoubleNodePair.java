//@@author A0121597B
package procrastinate.ui;

import javafx.scene.Node;

public class DoubleNodePair implements Comparable<DoubleNodePair> {

    // ================================================================================
    // Class Variables
    // ================================================================================

    private double height_;
    private Node node_;

    // ================================================================================
    // DoubleNodePair Constructor
    // ================================================================================

    public DoubleNodePair(double height, Node node) {
        this.height_ = height;
        this.node_ = node;
    }

    // ================================================================================
    // DoubleNodePair Methods
    // ================================================================================

    @Override
    public int compareTo(DoubleNodePair o) {
        return Double.valueOf(this.height_).compareTo(o.height_);
    }

    // ================================================================================
    // Getter Methods
    // ================================================================================

    protected double getHeight() {
        return height_;
    }

    protected Node getNode() {
        return node_;
    }

}
