package hbaskar.two;

import hbaskar.one.Main;
import hbaskar.one.PlanItPokerRepository;
import hbaskar.three.StoriesNanny;
import hbaskar.three.StoriesPanel;


/**
 * This class manages the creation of a new room in the PlanItPoker application.
 * It serves as the controller that handles user input for room creation, 
 * communicates with the {@code PlanItPokerRepository} to store relevant data,
 * and coordinates transitions between different user interface panels.
 * 
 * Upon room creation, it sets the selected mode and creator name, and 
 * transitions the GUI to the scheduling panel. It also provides a method 
 * to switch the UI to the stories panel once the scheduling step is complete.
 * 
 * This class acts as the intermediary between the UI and the backend repository 
 * during the room creation workflow. The actual UI components involved are 
 * {@code T1ScheduleRoomPanel} and {@code StoriesPanel}.
 * 
 * The logic related to room persistence and user session management is handled 
 * by the {@code PlanItPokerRepository}, while this class focuses on user 
 * interaction and GUI control flow.
 * 
 * @author hbaskar
 * @version 1.0
 */


public class T1CreateRoomNanny {

    private Main main;

    public T1CreateRoomNanny(Main main) {
        this.main = main;
    }

    public void createRoom(String name, String selectedItem) {
        System.out.println("Creating room..." + name + ", mode: " + selectedItem);

        PlanItPokerRepository repo = PlanItPokerRepository.getInstance();

        // Get the actual logged in user from the repository
        String creatorName = repo.getLoggedInUser();

        repo.createRoom(name, creatorName);
        repo.setCurrentMode(selectedItem);

        switchGUI();
    }

    private void switchGUI() {
        main.setTitle("Schedule Room");
        T1ScheduleRoomPanel scheduleRoomPanel = new T1ScheduleRoomPanel(this);
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
