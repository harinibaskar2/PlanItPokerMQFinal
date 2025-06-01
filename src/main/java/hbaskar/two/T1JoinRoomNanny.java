package hbaskar.two;

import hbaskar.one.Main;
import hbaskar.one.PlanItPokerRepository;
import hbaskar.three.StoriesNanny;
import hbaskar.three.StoriesPanel;



/**
 * This class handles the logic for joining an existing room in the 
 * PlanItPoker application. It acts as the controller that interacts with 
 * the {@code PlanItPokerRepository} to verify and perform the room join 
 * operation, and manages the transition of the user interface upon success.
 * 
 * When a user attempts to join a room, this class fetches the current 
 * logged-in username from the repository and tries to add the user to 
 * the specified room. If successful, it transitions the UI to the 
 * stories panel where gameplay continues.
 * 
 * The actual repository interaction and room state management is handled 
 * by the {@code PlanItPokerRepository}, while this class focuses on 
 * coordinating the join action and GUI updates.
 * 
 * @author hbaskar
 * @version 1.0
 */


public class T1JoinRoomNanny {

    private Main main;

    public T1JoinRoomNanny(Main main) {
        this.main = main;
    }

    public boolean joinRoom(String roomName) {
        System.out.println("Joining room: " + roomName);

        PlanItPokerRepository repo = PlanItPokerRepository.getInstance();
        String username = repo.getLoggedInUser();

        boolean success = repo.joinRoom(roomName, username);

        if (success) {
            switchGUI();
        }
        return success;
    }

    private void switchGUI() {
        main.setTitle("Stories");
        StoriesNanny storiesNanny = new StoriesNanny(main);
        StoriesPanel storiesPanel = new StoriesPanel(storiesNanny);
        main.setContentPane(storiesPanel);
        main.setSize(500, 500);
        main.revalidate();
        main.repaint();
    }
}

