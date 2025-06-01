package hbaskar.three;

import java.io.File;

import javax.swing.JFileChooser;

import hbaskar.four.T1DashboardNanny;
import hbaskar.four.T1DashboardPanel;
import hbaskar.four.T1StoriesPanel;
import hbaskar.one.Main;
import hbaskar.one.PlanItPokerRepository;
import hbaskar.two.T1CreateRoomNanny;
import hbaskar.two.T1ScheduleRoomPanel;

/**
 * Controller responsible for managing the stories and their interactions with the user interface.
 * 
 * author @DarienR5
 */
public class T1StoriesNanny {

    private T1StoriesPanel t1StoriesPanel;
    private Main main;
    private T1StoriesPanel storiesPanel = new T1StoriesPanel(this);
    private PlanItPokerRepository repository = PlanItPokerRepository.getInstance();

    public T1StoriesNanny(Main main) {
        this.main = main;
        this.t1StoriesPanel = new T1StoriesPanel();
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
        T1DashboardNanny dashboardNanny = new T1DashboardNanny(main);
        T1DashboardPanel dashboardPanel = new T1DashboardPanel(dashboardNanny);
        main.setContentPane(dashboardPanel);
        main.setSize(800, 600);
        main.setLocationRelativeTo(null);
        main.revalidate();
        main.repaint();
        t1StoriesPanel.updateActiveStories();
    }

    private void switchSchedule() {
        main.setTitle("Schedule Room");
        T1CreateRoomNanny roomNanny = new T1CreateRoomNanny(main);
        T1ScheduleRoomPanel scheduleRoomPanel = new T1ScheduleRoomPanel(roomNanny);
        main.setContentPane(scheduleRoomPanel);
        main.setSize(500, 500);
        main.revalidate();
        main.repaint();
    }
}
