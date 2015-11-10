//@@author A0124321Y
package procrastinate.command;

import java.util.Date;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class SearchDesc extends Search {

    public SearchDesc(String description) {
        super(CommandType.SEARCH);
        this.description = description;
    }
    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        String str = "";
        String term = null;
        Date start = null;
        Date end = null;
        boolean showDone = true;

        String feedback = SEARCH;

        // description setting
        assert (!description.isEmpty());
        feedback += String.format(SEARCH_CONTAINING, description);
        term = description;
        str += String.format(SEARCH_STRING_DESCRIPTION, description);

        searchString = str;
        searchTerm = term;
        searchEndDate = end;
        searchStartDate = start;
        searchShowDone = showDone;
        return feedback;
    }
}
