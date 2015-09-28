package procrastinate;

public class Command {
	
	private String commandType;
	private String taskDescription;
	
	//Constructors for Command
	
	public Command(){
		this.commandType = null;
		this.taskDescription = null;
	}
	
	public Command(String commandType){
		this.commandType = commandType;
		this.taskDescription = null;
	}
	
	public Command(String commandType, String taskDescription){
		this.commandType = commandType;
		this.taskDescription = taskDescription;
	}
	
	//Get Methods for Command
	
	public String getCommandType(){
		return this.commandType;
	}
	
	public String getTaskDescription(){
		return this.taskDescription;
	}
	
	//Set Methods for Command
	
	public void setCommandType(String commandType){
		this.commandType = commandType;
	}
	
	public void setTaskDescription(String taskDescription){
		this.taskDescription = taskDescription;
	}
}
