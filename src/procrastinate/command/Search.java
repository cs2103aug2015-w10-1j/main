//@@author A0124321Y
package procrastinate.command;

import java.util.Date;

public abstract class Search extends CleanCommand implements SearchFeedback {
    protected String description;

    protected String searchTerm,searchString;
    protected Date searchStartDate,
                 searchEndDate;
    protected boolean searchShowDone;

    public Search(CommandType type) {
        super(type);
    }

    public String getSearchStr() {
        return searchString;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public Date getSearchStartDate() {
        return searchStartDate;
    }

    public Date getSearchEndDate() {
        return searchEndDate;
    }

    public boolean getSearchShowDone() {
        return searchShowDone;
    }

}
