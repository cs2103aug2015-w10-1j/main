//@@author A0124321Y
package procrastinate.command;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class SearchOn extends Search {
    private Date date;

    public SearchOn(String description, Date date) {
        super(CommandType.SEARCH_ON);
        this.description = description;
        this.date = date;
    }

    @Override
    public String run(UI ui, TaskEngine taskEngine) {
        String str = "";
        String term = null;
        Date start = null;
        Date end = null;

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
        assert (date != null);

        // set time to 0000 hrs of the specified day
        date = DateUtils.truncate(date, Calendar.DATE);

        feedback += String.format(SEARCH_ON, Feedback.formatDate(date));
        start = date;
        end = DateUtils.addDays(date, 3);
        str += SEARCH_STRING_ON + Feedback.formatDate(date);

        searchString = str;
        searchTerm = term;
        searchEndDate = end;
        searchStartDate = start;
        searchShowDone = false;

        return feedback;
    }
}
