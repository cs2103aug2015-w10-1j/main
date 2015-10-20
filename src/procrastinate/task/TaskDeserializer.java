package procrastinate.task;

import com.google.gson.*;
import procrastinate.task.Task.TaskType;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class TaskDeserializer implements JsonDeserializer<Task> {

	/**
	 * Task is abstract class so gson is unable to construct a list of Task
	 * Therefore this method is overridden to allow custom deserialization
	 * A new task is constructed depending on the type that is read
	 */
	@Override
	public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		JsonObject jObj = json.getAsJsonObject();

		// type as in the variable, type in Task class
		String type = jObj.get("type").getAsString();
		String description = jObj.get("description").getAsString();
		boolean done = jObj.get("done").getAsBoolean();
		UUID id = UUID.fromString(jObj.get("id").getAsString());
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.getDefault());

		if (type.equals(TaskType.DREAM.toString())) {
			return new Dream(description, done, id);

		} else if (type.equals(TaskType.DEADLINE.toString())) {
	        Date date = null;

            try {
                date = dateFormat.parse(jObj.get("date").getAsString());
            } catch (ParseException e) {
                // TODO Show warning that the file has been modified to an unrecognisable date format
                return new Dream(description, done, id);
            }

			return new Deadline(description, date, done, id);

		} else if (type.equals(TaskType.EVENT.toString())) {
		    Date startDate = null;
		    Date endDate = null;

            try {
                startDate = dateFormat.parse(jObj.get("startDate").getAsString());
                endDate = dateFormat.parse(jObj.get("endDate").getAsString());
            } catch (ParseException e) {
                // TODO Show warning that the file has been modified to an unrecognisable date format
                return new Dream(description, done, id);
            }

            return new Event(description, startDate, endDate, done, id);
		}
		// TODO Show warning that the file has been modified to an unrecognisable format
		// skip adding this task instead of crashing
		return null;
	}
}
