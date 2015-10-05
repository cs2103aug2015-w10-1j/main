package procrastinate.task;

import java.lang.reflect.Type;
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
		String desc = jObj.get("description").getAsString();

		if (type.equals(TaskType.DREAM.toString())) {
			return new Dream(desc);
		} else if (type.equals(TaskType.DEADLINE.toString())) {
			// TODO: decide on date format
			return null;
		} else if (type.equals(TaskType.EVENT.toString())) {
			// TODO: decide on date format
			return null;
		}
		return null;
	}
}
