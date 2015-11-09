//@@author A0080485B
package procrastinate.task;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.UUID;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import procrastinate.task.Task.TaskType;

public class TaskDeserializer implements JsonDeserializer<Task> {

	/**
	 * Task is abstract class so gson is unable to construct a list of Task
	 * Therefore this method is overridden to allow custom deserialization
	 * A new task is constructed depending on the type that is read
	 */
	@Override
	public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

	    JsonObject jsonObject = json.getAsJsonObject();

	    String type = null;
	    String description = null;
	    boolean isDone = false;
	    UUID id = null;

        try {
            id = UUID.fromString(jsonObject.get(Task.FIELD_ID).getAsString());
        } catch (Exception e) {
            id = UUID.randomUUID(); // generate new UUID instead of crashing
        }

        try {
            isDone = jsonObject.get(Task.FIELD_DONE).getAsBoolean();
        } catch (Exception e) {
            isDone = false; // assume not done instead of crashing
        }

        try {
            description = jsonObject.get(Task.FIELD_DESCRIPTION).getAsString();
        } catch (Exception e) {
            description = ""; // empty description instead of crashing
        }

	    try {
	        // type as in the variable, type in Task class
	        type = jsonObject.get(Task.FIELD_TYPE).getAsString();
	    } catch (Exception e) {
	        type = TaskType.DREAM.toString(); // assume Dream instead of crashing
	    }

		if (type.equals(TaskType.DREAM.toString())) {
			return new Dream(description, isDone, id);

		} else if (type.equals(TaskType.DEADLINE.toString())) {
	        Date date = null;

            try {
                date = context.deserialize(jsonObject.get(Deadline.FIELD_DATE), Date.class);
            } catch (Exception e) {
                e.printStackTrace();
                return new Dream(description, isDone, id);
            }

			return new Deadline(description, date, isDone, id);

		} else if (type.equals(TaskType.EVENT.toString())) {
		    Date startDate = null;
		    Date endDate = null;

            try {
                startDate = context.deserialize(jsonObject.get(Event.FIELD_START_DATE), Date.class);
                endDate = context.deserialize(jsonObject.get(Event.FIELD_END_DATE), Date.class);

                if (endDate.before(startDate)) { // encountered invalid range
                    endDate = startDate; // use start date as both start and end date
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new Dream(description, isDone, id);
            }

            return new Event(description, startDate, endDate, isDone, id);

		} else {
		    return new Dream(description, isDone, id); // if unrecognised, default to dream
		}

	}
}
