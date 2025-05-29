package hbaskar.four;
import hbaskar.one.*;
public class DashboardNanny {

    private SouthPanel southPanel;
    private Main main;  // Assuming you want to use Main in this class

    // Modify constructor to accept Main instance
    public DashboardNanny(Main main) {
        this.main = main;
    }


    public void setSouthPanel(SouthPanel southPanel) {
        this.southPanel = southPanel;
    }

    public void onRoomSelected(String roomName) {
        // Logic to fetch stories based on the room selected
        String stories = fetchStoriesForRoom(roomName);
        if (southPanel != null) {
            southPanel.updateActiveStories(stories);
        }
    }

    private String fetchStoriesForRoom(String room) {
        switch (room) {
            case "Room 1":
                return "Story 1\nStory 2";
            case "Room 2":
                return "Story A\nStory B";
            case "Room 3":
                return "Story X\nStory Y";
            default:
                return "";
        }
    }
	// This is the part that I wrote, everything above is a different member's work
	public static void onSizePress(String value){
		System.out.println("This is assigned to the current story" + value);
	}
}
