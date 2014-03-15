package todomato;

import hirondelle.date4j.DateTime;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

public class AddProcessor extends Processor {

	private static String[] keywords = new String[] { " at ", " from ",
			" until ", " to ", " in ", " due ",  " on "};
	
	private static int INDEX_OF_WORDS_AFTER_KEYWORDS = 1;
	private static int NOT_FOUND = -1;
	//private static String INVALID_DATE = "Invalid date format";
	//private static String INVALID_TIME = "Invalid time format";
	private static String INVALID_INPUT = "Invalid input format";
	private static int INDEX_OF_DESC = 0;
	private static int INDEX_OF_START_TIME_STRING = 1;
	private static int INDEX_OF_END_TIME_STRING = 2;
	private static int INDEX_OF_DATE_STRING = 3;
	private static int INDEX_OF_LOCATION_STRING = 4;
	private static int NO_OF_TASK_DETAILS = 5;
	private static String[] taskDetails = new String[NO_OF_TASK_DETAILS];

	public static TaskDT parseTask(String input) throws InvalidInputException {
		Arrays.fill(taskDetails,null);
		boolean taskDesExtracted = false;
		int keywordIndex = -1;
		TaskDT userTaskDT = null;
		String[] stringFragments = null;
		
		if (!keywordIsInString(input)) {
			taskDetails[INDEX_OF_DESC] = input;
		}
		
		while (keywordIsInString(input)) {
			keywordIndex = getFirstKeyword(input);
			stringFragments = splitByKeyword(input, keywords[keywordIndex]);
			if (!taskDesExtracted) {
				taskDetails[INDEX_OF_DESC] = stringFragments[0];
				taskDesExtracted = true;
			}
			taskDetails = keywordHandler(keywordIndex, stringFragments[INDEX_OF_WORDS_AFTER_KEYWORDS]);
			input = stringFragments[INDEX_OF_WORDS_AFTER_KEYWORDS];
		}
		
		userTaskDT = setUserTask(taskDetails);
		
		return userTaskDT;
	}

	/**
	 * @author Daryl
	 * Adds Task to the list and writes to file the updated list
	 * @param input
	 * @return Task
	 * @throws InvalidInputException
	 * @throws NumberFormatException
	 * @throws IOException
	 */

	public static String processAdd(String input) throws NumberFormatException {
		TaskDT userTask = null;
		try {
			userTask = parseTask(input);
		} catch (InvalidInputException e) {
			return INVALID_INPUT;
		}
		list.addToList(userTask);
		fileHandler.updateFile(list);
		return userTask.toString();
	}
	
	/**
	 * Creates and sets userTask with all the information stored in
	 * an array
	 * @param taskDetails
	 * @return userTaskDT with all the task details
	 */
	
	private static TaskDT setUserTask(String[] taskDetails) {
		TaskDT userTask = new TaskDT(taskDetails[INDEX_OF_DESC]);
		DateTime startTime = convertStringToDateTime(taskDetails[INDEX_OF_START_TIME_STRING]);
		DateTime endTime = convertStringToDateTime(taskDetails[INDEX_OF_END_TIME_STRING]);
		DateTime date = convertStringToDateTime(taskDetails[INDEX_OF_DATE_STRING]);
		userTask.setStartTime(startTime);
		userTask.setEndTime(endTime);
		userTask.setDate(date);
		userTask.setLocation(taskDetails[INDEX_OF_LOCATION_STRING]);
		return userTask;
	}
	
	/**
	 * Calls helper methods based on what keyword type it is
	 * @param keywordType
	 * @param input
	 * @return String array with all the relevant task details
	 * @throws InvalidInputException
	 */

	private static String[] keywordHandler (int keywordType, String input) throws InvalidInputException {
	
		int spaceIndex = input.indexOf(" ");
		if (spaceIndex == NOT_FOUND) {
			spaceIndex = input.length();
		}
		switch (keywordType) {
			case 0:
			case 1:
				taskDetails[INDEX_OF_START_TIME_STRING] = retrieveStartTime(input, spaceIndex);
				break;
			case 2:
			case 3:
				taskDetails[INDEX_OF_END_TIME_STRING] = retrieveEndTime(input,spaceIndex);
				break;
			case 4:
				taskDetails[INDEX_OF_LOCATION_STRING] = retrieveLocation(input);
				break;
			case 5:
				taskDetails[INDEX_OF_END_TIME_STRING] = retrieveEndTime(input,spaceIndex);
				break;
			case 6:
				taskDetails[INDEX_OF_DATE_STRING] = retrieveDate(input);
				break;
		}

		return taskDetails;
	}
	
	/**
	 * Retrieves the first word in input
	 * @param input
	 * @param spaceIndex
	 * @return startTimeString
	 */
	
	private static String retrieveStartTime (String input, int spaceIndex) {
		String startTimeString = (input.substring(0, spaceIndex));
		startTimeString = parseTimeStringFromInput(startTimeString);
		return startTimeString;
	}
	
	private static String retrieveEndTime (String input, int spaceIndex) {
		String endTimeString = (input.substring(0, spaceIndex));
		endTimeString = parseTimeStringFromInput(endTimeString);
		return endTimeString;
	}
	
	private static String retrieveLocation (String input) {
		String location = null;
		if (getFirstKeyword(input) == NOT_FOUND) {
			location = input;
		} else {
			location = getWordsBeforeNextKeyword(input, keywords[getFirstKeyword(input)]);
		}
		
		return location;
	}
	
	private static String retrieveDate (String input) throws InvalidInputException {
		String dateString = parseDateString(input);
		return dateString;
	}
	


	
	/**
	 * Returns words before the specified keyword
	 * @param input
	 * @param keyword
	 * @return wordsBeforeNextKeyword
	 */

	private static String getWordsBeforeNextKeyword(String input, String keyword) {
		String wordsBeforeNextKeyword;
		wordsBeforeNextKeyword = input.substring(0, input.indexOf(keyword));
		return wordsBeforeNextKeyword;
	}
	
	/**
	 * Checks whether there are any keywords in the string
	 * @param input
	 * @return true for yes/false for no
	 */

	private static Boolean keywordIsInString(String input) {
		for (String keyword : keywords) {
			if (input.contains(keyword)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Retrieves the first keyword in the string
	 * @param input
	 * @return
	 */

	private static int getFirstKeyword(String input) {
		int firstKeywordPos = input.length();
		int firstKeyword = NOT_FOUND;

		for (int i = 0; i < keywords.length; i++) {
			if (input.contains(keywords[i])) {
				if (input.indexOf(keywords[i]) < firstKeywordPos) {
					firstKeywordPos = input.indexOf(keywords[i]);
					firstKeyword = i;
				}
			}
		}
		return firstKeyword;
	}
	
	/**
	 * Splits the input into 2, one portion before the keyword, the other after
	 * @param input
	 * @param keyword
	 * @return
	 */

	private static String[] splitByKeyword(String input, String keyword) {
		String[] splitWords = null;
		Pattern pattern = Pattern.compile(Pattern.quote(keyword));
		splitWords = pattern.split(input);

		return splitWords;
	}
}
