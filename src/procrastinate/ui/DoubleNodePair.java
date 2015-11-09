//@@author A0121597B-reused
package procrastinate.ui;

import javafx.scene.Node;

/**
 * <h1>DoubleNodePair is a class created specially for the SummaryScreen, to allow
 * pairing of a Node and it's height value, a double.</h1>
 *
 * It allows the sorting by the double value (height) to allow the SummaryScreen to
 * make more efficient use of space by handling smaller height Nodes first.
 */
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

    //@@author generated
    protected double getHeight() {
        return height_;
    }

    protected Node getNode() {
        return node_;
    }

}
