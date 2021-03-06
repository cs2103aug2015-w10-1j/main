package procrastinate.test;

import procrastinate.task.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/*
 * Stub class to mimic TaskState
 * Constructs a hard coded TaskState
 */
public class TaskStateStub extends TaskState{

    //@@author A0124321Y
	public TaskStateStub() {
		super(makeTasks());
	}

    //@@author A0124321Y
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

		Dream doneDream = new Dream("bar bee");
		doneDream.setDone(true);
		stub.add(doneDream);

		return stub;
	}
}
