//@@author A0124321Y
package procrastinate.command;

/**
 * A simple interface to state whether an action should be run
 * @author Gerald
 *
 */
public interface Preview {
    /**
     *
     * @return true if action should not be run
     */
    public boolean isPreview();

    public void setPreview(boolean preview);
}
