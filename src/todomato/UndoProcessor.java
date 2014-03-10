package todomato;

public class UndoProcessor extends Processor {
	private static final String NO_CHANGES_TO_UNDO_MSG = "No changes to undo";
	
	/**
	 * Undo last action, if possible
	 * @author Joyce
	 * @return String representation of list, or status message if no changes to undo
	 */
	public static String processUndo() {
		if (!oldLists.isEmpty()) {
			list = oldLists.pop();	
			fileHandler.updateFile(list);
			return list.toString();
		} else {
			return NO_CHANGES_TO_UNDO_MSG;
		}
	}

}