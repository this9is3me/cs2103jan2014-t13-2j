package todomato;
import hirondelle.date4j.DateTime;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class reads, writes, and updates a local data file stored
 * on the user's computer. It reads in tasks as Task objects.
 * @author A0099332Y
 *
 */

public class FileHandler {
	
	private static final String LINE_BREAK = "\r\n";
	private String fileLocation;
	private File file;
	
	/**
	 * @param fileLoc
	 */
	public FileHandler (String fileLoc) {
		this.fileLocation = fileLoc;
		this.file = createNewFileWhenNotExist(new File (fileLocation));	
	}
	
	public File createNewFileWhenNotExist (File f) {
		File dataFile = f;
		if(!f.exists()) {
			try {
				FileWriter fw = new FileWriter(f.getAbsoluteFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dataFile;
	}

	/**
	 * @return a task list generated by strings in the data file
	 * @throws IOException
	 */
	public TaskList readFile() {
		
		try {
			Boolean isSyncTimeRetrieved = false;
			Boolean isUserNameRetrieved = false;
			Boolean isPasswordRetrieved = false;
			String currentLine;
			Task currentTask;
			TaskList taskList = new TaskList();
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsoluteFile()));
		
			while ((currentLine = bufferedReader.readLine()) != null) {
				if (!isSyncTimeRetrieved) {
					taskList.setLastSyncTime (new DateTime(currentLine));
					isSyncTimeRetrieved = true;
				} else if (!isUserNameRetrieved) {
					taskList.setUserName(currentLine);
					isUserNameRetrieved = true;
				} else if (!isPasswordRetrieved) {
					taskList.setPassword(currentLine);
					isPasswordRetrieved = true;
				} else {
					currentTask = readTask(currentLine);
					taskList.addToList(currentTask);
				}
			}
			
			bufferedReader.close();
			return taskList;
			
		} catch (IOException e) {
			return null;
			
		}
			
			
	}
	
	/**
	 * @param taskList the updated task list stored in runtime
	 * @return the updated task list generated by strings in the date file
	 * @throws IOException
	 */
	public TaskList updateFile(TaskList taskList) {
		
		try {
		
			String content;
			
			FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
	
			if (!file.exists()) {
				file.createNewFile();
			}
			
			content = taskList.getLastSyncTime() + LINE_BREAK + taskList.getUserName() 
					+ LINE_BREAK + taskList.getPassword() + LINE_BREAK;
			bufferedWriter.write(content);
			
			for (Task task : taskList.getList()) {
				content = task.toFileString() + LINE_BREAK;
				bufferedWriter.write(content);
			}
	
			bufferedWriter.close();
			
			TaskList updatedList = readFile();
			
			
			return updatedList;
		
		} catch (IOException e) {
			
			//TODO
			return null;
			
		}
	}
	
	/**
	 * @param line each line in data file that represents a task
	 * @return task generated by the input line
	 */
	private Task readTask(String line) {
		Task task = null;
		task = Task.createTaskFromFileString(line);
		return task;
	}
	
	
}
