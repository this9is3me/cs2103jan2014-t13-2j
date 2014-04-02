package tests;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import todomato.SplitProcessorsHandler;
import todomato.InvalidInputException;

public class IntegrationTest {
	private  File tasks;
	
	@Rule 
	public  TemporaryFolder folder= new TemporaryFolder();
	
	@Before
	public void createTestData() throws IOException {
		System.setProperty("user.dir", folder.getRoot().toString());
		tasks = folder.newFile("tasks.txt");
		BufferedWriter out = new BufferedWriter(new FileWriter(tasks));
        out.write("cs post-lecture quiz#null#null#2014-03-28#null#7#-1997137046#2014-04-01 22:57:15.488000000#LOW#true#null#null#null\r\n");
        out.write("study#12:00#16:00#2014-03-31#utown#7#950768200#2014-04-02 23:17:24.118000000#LOW#true#null#null#null\r\n");
        out.write("es1531 assignment 2#null#null#2014-04-01#null#0#636174328#2014-03-30 16:24:17.877000000#HIGH#true#null#null#null\r\n");
        out.close();
	}

	
	@Test
	public void testAddDelete() throws InvalidInputException, IOException {
		String messageAdd1 = SplitProcessorsHandler.processCommand("add something");
		assertEquals("Added something", messageAdd1);
		String messageUndo1 = SplitProcessorsHandler.processCommand("undo");
		assertEquals("Last action undone", messageUndo1);
		SplitProcessorsHandler.processCommand("add something1");
		String messageAdd2 = SplitProcessorsHandler.processCommand("add Tutorial @ERC-SR2 on 5 Feb at 2pm");
		assertEquals("Added Tutorial at 14:00 on Feb 05 2014 in ERC-SR2", messageAdd2);
		String messageUpdate1 = SplitProcessorsHandler.processCommand("update 4 desc Dinner with Parents\\ location home\\");
		assertEquals("Dinner with Parents in home", messageUpdate1);
		String messageDelete1 = SplitProcessorsHandler.processCommand("delete all");
		assertEquals("Deleted: 5 task(s)", messageDelete1);
	}
	
	
}
