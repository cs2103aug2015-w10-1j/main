package procrastinate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import procrastinate.task.Deadline;
import procrastinate.task.Dream;
import procrastinate.task.Event;
import procrastinate.task.Task;
import procrastinate.task.TaskState;

/*
 * Stub class to mimic TaskState
 * Constructs a hard coded TaskState
 */
public class TaskStateStub extends TaskState{

	public TaskStateStub() {
		super(makeTasks());
	}

	private static List<Task> makeTasks() {
		List<Task> stub = new ArrayList<Task>();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		stub.add(new Dream("foo dream"));
		try {
			stub.add(new Deadline("foo deadline", sdf.parse("30/09/2015")));
			stub.add(new Event("foo event", sdf.parse("30/09/2015"), sdf.parse("02/10/2015")));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Dream doneDream = (new Dream("bar bee"));
		doneDream.setDone();
		stub.add(doneDream);

		return stub;
	}
}
