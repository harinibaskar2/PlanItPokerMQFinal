package hbaskar.two;
import hbaskar.one.Blackboard;
import hbaskar.one.Main;
import hbaskar.three.StoriesNanny;
import hbaskar.three.StoriesPanel;


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
        System.out.println("Creating room..." + name + ", mode: " + selectedItem);
        Blackboard.addRoom(name);          // Add room to the list of rooms
        Blackboard.addCurrentRoom(name);   // Set as current room
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