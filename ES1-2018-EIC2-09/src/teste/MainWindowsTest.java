package teste;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import gui.MainWindows;

class MainWindowsTest {

	private MainWindows mw = new MainWindows();

	@Test
	void testAddNotification() {
		mw.addNotification("01-12-18", "Facebook", "AE", "Viagem a Madrid");
		mw.addNotification("03-12-18", "Twitter", "AE", "Festa de carnaval");
		assertEquals("Adding 1 row to timeline", 2, mw.getTimelineTable().getRowCount() );
	}
	
	@Test
	void testRemoveNotification() {
		mw.addNotification("01-12-18", "Facebook", "AE", "Viagem a Madrid");
		mw.addNotification("03-12-18", "Twitter", "AE", "Festa de carnaval");
		int row = mw.getTimelineTable().getRowCount() - 1;
		mw.removeNotification(row);
		assertEquals("Removing 1 row to timeline", 1, mw.getTimelineTable().getRowCount() );
	}

}
