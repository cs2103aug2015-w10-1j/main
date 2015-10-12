package procrastinate.task;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

		JsonObject jObj = json.getAsJsonObject();

		// type as in the variable, type in Task class
		String type = jObj.get("type").getAsString();
		String description = jObj.get("description").getAsString();
		boolean done = jObj.get("done").getAsBoolean();
		UUID id = UUID.fromString(jObj.get("id").getAsString());
        DateFormat defaultDateFormat = new SimpleDateFormat();

		if (type.equals(TaskType.DREAM.toString())) {
			return new Dream(description, done, id);

		} else if (type.equals(TaskType.DEADLINE.toString())) {
	        Date date = null;

            try {
                date = defaultDateFormat.parse(jObj.get("date").getAsString());
            } catch (ParseException e) {
                // TODO Show warning that the file has been modified to an unrecognisable date format
                return new Dream(description, done, id);
            }

			return new Deadline(description, date, done, id);

		} else if (type.equals(TaskType.EVENT.toString())) {
		    Date startDate = null;
		    Date endDate = null;

            try {
                startDate = defaultDateFormat.parse(jObj.get("startDate").getAsString());
                endDate = defaultDateFormat.parse(jObj.get("endDate").getAsString());
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
