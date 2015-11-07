//@@author A0124321Y
package procrastinate.command;

import procrastinate.task.Task;

public abstract class Add extends CleanCommand {
    private Task newTask;
    private String description;

    public Add(CommandType type) {
        super(type);
    }

    public CleanCommand addDescription(String description) {
        this.description = description;
        return this;
    }
}
