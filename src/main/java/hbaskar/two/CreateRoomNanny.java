package hbaskar.two;
import hbaskar.one.*;
import hbaskar.three.*;


/**
 * Act as a controller for the CreateRoomPanel.
 *
 * @author javiergs
 */
public class CreateRoomNanny {
	
	private Main main;
	
	public CreateRoomNanny(Main main) {
		this.main = main;
	}
	
	public void createRoom(String name, String selectedItem) {
		System.out.println(" Creating room..." + name + ", mode: " + selectedItem);
		Blackboard.addCurrentRoom(name);
		Blackboard.addCurrentMode(selectedItem);
		switchGUI();
	}

	private void switchGUI() {
    main.setTitle("Schedule Room");
    ScheduleRoomPanel scheduleRoomPanel = new ScheduleRoomPanel(this);
    main.setContentPane(scheduleRoomPanel);
    main.setSize(500, 500);
    main.revalidate();
    main.repaint();
}

// Called after confirming time
public void switchToStoriesPanel() {
    main.setTitle("Stories");
    StoriesNanny storiesNanny = new StoriesNanny(main);
    StoriesPanel storiesPanel = new StoriesPanel(storiesNanny);
    main.setContentPane(storiesPanel);
    main.setSize(500, 500);
    main.revalidate();
    main.repaint();
} 
	
	
}   