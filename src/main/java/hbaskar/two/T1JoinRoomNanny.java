package hbaskar.two;

import hbaskar.one.Main;
import hbaskar.one.PlanItPokerRepository;
import hbaskar.three.StoriesNanny;
import hbaskar.three.StoriesPanel;

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
