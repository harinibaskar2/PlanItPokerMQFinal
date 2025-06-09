package hbaskar.three;

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;

import hbaskar.TaigaStoryFetcher;
import hbaskar.four.T1DashboardNanny;
import hbaskar.four.T1DashboardPanel;
import hbaskar.one.Main;
import hbaskar.one.PlanItPokerRepository;
import hbaskar.two.T1CreateRoomNanny;
import hbaskar.two.T1ScheduleRoomPanel;

/**
 * Controller responsible for managing the stories and their interactions with the user interface.
 * 
 * author @hbaskar
 */
public class T1StoriesNanny {

    private T1StoriesPanel storiesPanel;
    private Main main;
    private PlanItPokerRepository repository = PlanItPokerRepository.getInstance();


    public T1StoriesNanny(Main main) {
        this.main = main;
        this.storiesPanel = new T1StoriesPanel(this); // This is the only panel you need
    }

    public T1StoriesPanel getPanel() {
        return storiesPanel;
    }


    public T1StoriesNanny(T1StoriesPanel panel) {
        this.storiesPanel = panel;
    }
    
    public void saveAndAddNew(String text) {
        System.out.println("Saving and adding new story:\n" + text);

        String roomCode = repository.getCurrentRoomCode();
        if (roomCode != null) {
            repository.createStory(roomCode, "Untitled", text);
        } else {
            System.err.println("No room selected. Cannot add story.");
        }

        storiesPanel.storyTextArea.setText("");
    }

    public void saveAndClose(String text) {
        System.out.println("Saving and closing story:\n" + text);

        String roomCode = repository.getCurrentRoomCode();
        if (roomCode != null) {
            repository.createStory(roomCode, "Untitled", text);
        } else {
            System.err.println("No room selected. Cannot add story.");
        }

        switchToDashboard();
    }

    public void importStories() {
    System.out.println("Importing stories from Taiga...");

    try {
        // Replace with your actual credentials
        String username = "your_username";
        String password = "your_password";
        String projectSlug = "2thesimplexity-pac-man";

        String authToken = TaigaStoryFetcher.loginAndGetToken(username, password);
        int projectId = TaigaStoryFetcher.getProjectId(authToken, projectSlug);
        JSONArray backlogStories = TaigaStoryFetcher.fetchUserStories(authToken, projectId);

        String roomCode = repository.getCurrentRoomCode();
        if (roomCode == null) {
            System.err.println("No room selected. Cannot import stories.");
            return;
        }

        for (int i = 0; i < backlogStories.length(); i++) {
            JSONObject story = backlogStories.getJSONObject(i);
            String title = story.optString("subject", "Untitled");
            String description = story.optString("description", "(no description)");
            repository.createStory(roomCode, title, description);
        }

        JOptionPane.showMessageDialog(null, "Imported " + backlogStories.length() + " stories from Taiga.", "Success", JOptionPane.INFORMATION_MESSAGE);
     
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to import stories:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    public void cancel() {
        System.out.println("Cancelling story creation...");
        switchToSchedule();
    }

    private void switchToDashboard() {
        main.setTitle("Dashboard");
        T1DashboardNanny dashboardNanny = new T1DashboardNanny(main);
        T1DashboardPanel dashboardPanel = new T1DashboardPanel(dashboardNanny);
        main.setContentPane(dashboardPanel);
        main.setSize(800, 600);
        main.setLocationRelativeTo(null);
        main.revalidate();
        main.repaint();
        
    }

    private void switchToSchedule() {
        main.setTitle("Schedule Room");
        T1CreateRoomNanny roomNanny = new T1CreateRoomNanny(main);
        T1ScheduleRoomPanel scheduleRoomPanel = new T1ScheduleRoomPanel(roomNanny);
        main.setContentPane(scheduleRoomPanel);
        main.setSize(500, 500);
        main.revalidate();
        main.repaint();
    }
}
