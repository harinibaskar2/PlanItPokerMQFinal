package hbaskar.two;

import hbaskar.one.Main;
import hbaskar.one.PlanItPokerRepository;
import hbaskar.three.StoriesNanny;
import hbaskar.three.StoriesPanel;

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
