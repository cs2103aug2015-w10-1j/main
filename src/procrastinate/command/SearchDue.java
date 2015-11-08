//@@author A0124321Y
package procrastinate.command;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class SearchDue extends Search {
    private Date date;

    public SearchDue(String description, Date date) {
        super(CommandType.SEARCH_DUE);
        this.description = description;
        this.date = date;
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
        if (description.isEmpty()) {
            str += SEARCH_STRING_NO_DESCRIPTION;
        } else {
            feedback += String.format(SEARCH_CONTAINING, description);
            term = description;
            str += String.format(SEARCH_STRING_DESCRIPTION, description);
        }

        // date setting
        if (date != null) {
            showDone = false;

            // set time to 0000 hrs of the specified day
            date = DateUtils.truncate(date, Calendar.DATE);

            feedback += String.format(SEARCH_DUE, Feedback.formatDate(date));
            start = new Date(0); // beginning of time
            end = DateUtils.addDays(date, 3);
            str += SEARCH_STRING_DUE + Feedback.formatDate(date);
        }

        // Set the variables only if executing search command
        if (!isPreview()) {
            searchString = str;
            searchTerm = term;
            searchEndDate = end;
            searchStartDate = start;
            searchShowDone = showDone;
        }

        return feedback;
    }
}
