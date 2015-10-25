package procrastinate.task;

import com.google.gson.*;
import procrastinate.task.Task.TaskType;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class TaskDeserializer implements JsonDeserializer<Task> {

    private static DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.getDefault());

	/**
	 * Task is abstract class so gson is unable to construct a list of Task
	 * Therefore this method is overridden to allow custom deserialization
	 * A new task is constructed depending on the type that is read
	 */
	@Override
	public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

	    JsonObject jObj = json.getAsJsonObject();

	    String type = null;
	    String description = null;
	    boolean done = false;
	    UUID id = null;

        try {
            id = UUID.fromString(jObj.get("id").getAsString());
        } catch (Exception e) {
            id = UUID.randomUUID(); // generate new UUID instead of crashing
        }

        try {
            done = jObj.get("done").getAsBoolean();
        } catch (Exception e) {
            done = false; // assume not done instead of crashing
        }

        try {
            description = jObj.get("description").getAsString();
        } catch (Exception e) {
            description = ""; // empty description instead of crashing
        }

	    try {
	        // type as in the variable, type in Task class
	        type = jObj.get("type").getAsString();
	    } catch (Exception e) {
	        type = TaskType.DREAM.toString(); // assume Dream instead of crashing
	    }

		if (type.equals(TaskType.DREAM.toString())) {
			return new Dream(description, done, id);

		} else if (type.equals(TaskType.DEADLINE.toString())) {
	        Date date = null;

            try {
                date = dateFormat.parse(jObj.get("date").getAsString());
            } catch (Exception e) {
                return new Dream(description, done, id);
            }

			return new Deadline(description, date, done, id);

		} else if (type.equals(TaskType.EVENT.toString())) {
		    Date startDate = null;
		    Date endDate = null;

            try {
                startDate = dateFormat.parse(jObj.get("startDate").getAsString());
                endDate = dateFormat.parse(jObj.get("endDate").getAsString());

                if (endDate.compareTo(startDate) < 0) { // encountered invalid range
                    endDate = startDate; // use start date as both start and end date
                }
            } catch (Exception e) {
                return new Dream(description, done, id);
            }

            return new Event(description, startDate, endDate, done, id);

		} else {
		    return new Dream(description, done, id); // if unrecognised, default to dream
		}

	}
}
