package hbaskar.four;
import hbaskar.T1Card;
import hbaskar.one.Main;
import hbaskar.one.T1PlanItPokerRepository;
import hbaskar.one.T1PlanItPokerRepository.Room;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages and coordinates interactions within the PlanItPoker dashboard,
 * particularly handling updates and events related to the stories panel.
 * 
 * This class listens for room selection changes, updates the stories displayed,
 * and processes user actions such as scoring story cards.</p>
 * 
 * It interacts with {@link T1StoriesPanel} to refresh UI elements and uses
 * {@link T1PlanItPokerRepository} to access and modify story data for the current room.</p>
 * 
 * 
 * @author DarienR5
 */
public class T1DashboardNanny {
    private static final Logger logger = LoggerFactory.getLogger(T1DashboardNanny.class);
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
    public static void onSizePress(String id, String value) {
        int score;
        if (value.equals("?")) {
            score = 0;
        } else if (value.equals("½")) {
            score = 0;
        } else {
            score = Integer.parseInt(value);
        }
        
        String currentRoomCode = T1PlanItPokerRepository.getInstance().getCurrentRoomCode();
        Room room = T1PlanItPokerRepository.getInstance().getRoom(currentRoomCode);
        List<T1Card> stories = room.getAllStories();
        T1Card story = room.getStory(id);
        
        if (story != null) {
            story.addScore("guest", score);
            story.calculateAverageScore(); // Calculate average after adding score
            logger.info("This is assigned to the current story: " + value + " for story: " + story.getTitle());
        } else {
            logger.warn("Story not found with id: " + id);
        }
    }
}