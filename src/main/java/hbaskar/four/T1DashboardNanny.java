package hbaskar.four;
import hbaskar.one.Main;

/*
 * Watcher for the stories panel for it to catch all the stories and move around as well as update the stories
 * 
 * author @DarienR5
 * 
 * 
 */
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

   
	// This is the part that I wrote, everything above is a different member's work
	public static void onSizePress(String value){
		System.out.println("This is assigned to the current story" + value);
	}
}
