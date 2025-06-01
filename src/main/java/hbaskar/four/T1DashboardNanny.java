package hbaskar.four;
import hbaskar.one.Main;
public class T1DashboardNanny {

    private T1StoriesPanel T1StoriesPanel;
    private Main main;  // Assuming you want to use Main in this class

    // Modify constructor to accept Main instance
    public T1DashboardNanny(Main main) {
        this.main = main;
    }


    public void setT1StoriesPanel(T1StoriesPanel T1StoriesPanel) {
        this.T1StoriesPanel = T1StoriesPanel;
    }

    public void onRoomSelected(String roomName) {
        // Logic to fetch stories based on the room selected
        if (T1StoriesPanel != null) {
            T1StoriesPanel.updateActiveStories();
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
