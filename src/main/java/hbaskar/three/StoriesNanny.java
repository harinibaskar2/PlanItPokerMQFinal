package hbaskar.three;

import java.io.File;

import javax.swing.JFileChooser;

import hbaskar.four.DashboardNanny;
import hbaskar.four.DashboardPanel;
import hbaskar.one.Main;
import hbaskar.one.PlanItPokerRepository;
import hbaskar.two.CreateRoomNanny;
import hbaskar.two.ScheduleRoomPanel;

/**
 * Controller responsible for managing the stories and their interactions with the user interface.
 */
public class StoriesNanny {
    
    private Main main;
    private StoriesPanel storiesPanel = new StoriesPanel(this);
    private PlanItPokerRepository repository = PlanItPokerRepository.getInstance();

    public StoriesNanny(Main main) {
        this.main = main;
    }

    public void saveAndAddNew(String text) {
        System.out.println(text);

        String roomCode = repository.getCurrentRoomCode();
        if (roomCode != null) {
            repository.createStory(roomCode, "Untitled", text);
        } else {
            System.err.println("No room selected. Cannot add story.");
        }

        storiesPanel.storyTextArea.setText("");
    }

    public void saveAndClose(String text) {
        System.out.println(text);

        String roomCode = repository.getCurrentRoomCode();
        if (roomCode != null) {
            repository.createStory(roomCode, "Untitled", text);
        } else {
            System.err.println("No room selected. Cannot add story.");
        }

        switchGUI();
    }

    public void importStories() {
        System.out.println("importing stories...");
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(fileChooser);
        File selectedFile = fileChooser.getSelectedFile();
        // Add logic to read from the file and call createStory for each story if needed
    } 

    public void cancel() {
        System.out.println("canceling...");
        switchSchedule();
    }

    private void switchGUI() {
        main.setTitle("dashboard");
        DashboardNanny dashboardNanny = new DashboardNanny(main);
        DashboardPanel dashboardPanel = new DashboardPanel(dashboardNanny);
        main.setContentPane(dashboardPanel);
        main.setSize(800, 600);
        main.setLocationRelativeTo(null);
        main.revalidate();
        main.repaint();
    }

    private void switchSchedule() {
        main.setTitle("Schedule Room");
        CreateRoomNanny roomNanny = new CreateRoomNanny(main);
        ScheduleRoomPanel scheduleRoomPanel = new ScheduleRoomPanel(roomNanny);
        main.setContentPane(scheduleRoomPanel);
        main.setSize(500, 500);
        main.revalidate();
        main.repaint();
    }
}
