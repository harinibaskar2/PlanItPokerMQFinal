package hbaskar.three;

import java.io.File;

import javax.swing.JFileChooser;

import four.DashboardNanny;
import four.DashboardPanel;
import one.Blackboard;
import one.Main;
import two.ScheduleRoomPanel;
import two.CreateRoomNanny;

/**
 * Controller responsible for managing the stories and their interactions with the user interface.
 *
 * @author javiergs
 */
public class StoriesNanny {
	
	private Main main;
	private StoriesPanel storiesPanel = new StoriesPanel(this);
	
	public StoriesNanny(Main main) {
		this.main = main;
	}
	
	public void saveAndAddNew(String text) {
		System.out.println(text);

		Blackboard.addStory(text);
		storiesPanel.storyTextArea.setText("");
		
	}
	
	public void saveAndClose(String text) {
		System.out.println(text);
		Blackboard.addStory(text);
		switchGUI();
	}
	
	public void importStories() {
		System.out.println("importing stories...");
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(fileChooser);
		File selectedFile = fileChooser.getSelectedFile();
	} 
	
	public void cancel() {
		System.out.println("canceling...");
		switchSchedule();
	}
	
	private void switchGUI() {
		main.setTitle("dashboard");
		four.DashboardNanny dashboardNanny = new DashboardNanny(main);
		DashboardPanel dashboardPanel = new DashboardPanel(dashboardNanny);
		main.setContentPane(dashboardPanel);
		main.setSize(800, 600);
		main.setLocationRelativeTo(null);
		main.revalidate();
		main.repaint();
	}
	
	private void switchSchedule() {
		main.setTitle("Schedule Room");
		two.CreateRoomNanny roomNanny = new two.CreateRoomNanny(main);
		two.ScheduleRoomPanel scheduleRoomPanel = new two.ScheduleRoomPanel(roomNanny);
		main.setContentPane(scheduleRoomPanel);
		main.setSize(500, 500);
		main.revalidate();
		main.repaint();

}
}