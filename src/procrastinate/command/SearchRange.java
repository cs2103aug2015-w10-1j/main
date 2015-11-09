//@@author A0124321Y
package procrastinate.command;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import procrastinate.task.TaskEngine;
import procrastinate.ui.UI;

public class SearchRange extends Search {
    private Date startDate, endDate;

    public SearchRange(String description, Date startDate, Date endDate) {
        super(CommandType.SEARCH_RANGE);
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
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
        if (startDate != null && endDate != null) {
            // set time to 0000 hrs of the specified day
            startDate = DateUtils.truncate(startDate, Calendar.DATE);
            endDate = DateUtils.truncate(endDate, Calendar.DATE);

            if (endDate.before(startDate)) {
                return String.format(INVALID_RANGE,
                                     Feedback.formatDate(startDate),
                                     Feedback.formatDate(endDate));
            }

            feedback += String.format(SEARCH_FROM_TO,
                                      Feedback.formatDate(startDate),
                                      Feedback.formatDate(endDate));
            start = startDate;
            end = DateUtils.addDays(endDate, 1);;
            str += String.format(SEARCH_STRING_FROM_TO,
                                 Feedback.formatDate(startDate),
                                 Feedback.formatDate(endDate));
        }

        searchString = str;
        searchTerm = term;
        searchEndDate = end;
        searchStartDate = start;
        searchShowDone = showDone;

        return feedback;
    }
}
