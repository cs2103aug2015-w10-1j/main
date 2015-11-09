//@@author A0124321Y
package procrastinate.command;

public abstract class Search extends Command implements FeedbackSearch {
    protected String description;

    public Search(CommandType type) {
        super(type);
    }
}
