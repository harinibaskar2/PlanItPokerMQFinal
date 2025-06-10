package hbaskar.three;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
 * Controller for managing Taiga story import and UI transitions.
 * 
 * @author hbaskar
 */
public class T1StoriesNanny {

    private T1StoriesPanel storiesPanel;
    private Main main;
    private PlanItPokerRepository repository = PlanItPokerRepository.getInstance();

    // Primary constructor used in Main
    public T1StoriesNanny(Main main) {
        this.main = main;
        this.storiesPanel = new T1StoriesPanel(this); // Panel that shows "Import from Taiga"
    }

    // Optional constructor (e.g., for testing or specific injection)
    public T1StoriesNanny(T1StoriesPanel panel) {
        this.storiesPanel = panel;
    }

    public T1StoriesPanel getPanel() {
        return storiesPanel;
    }

    // Called when the "Import from Taiga Backlog" button is clicked
    public void importStories() {
        System.out.println("Opening Taiga login panel...");
        main.setTitle("Login to Taiga");

        JPanel loginPanel = new T1TaigaLoginPanel(this); // Show login form
        main.setContentPane(loginPanel);
        main.setSize(400, 250);
        main.setLocationRelativeTo(null);
        main.revalidate();
        main.repaint();
    }

    // Called after submitting username/password/project
    public void importFromTaigaWithCredentials(String username, String password, String projectSlug) {
        // Store credentials centrally
        repository.setTaigaCredentials(username, password);
    
        System.out.println("Importing from Taiga for project: " + projectSlug);
    
        try {
            String authToken = TaigaStoryFetcher.loginAndGetToken(username, password);
            int projectId = TaigaStoryFetcher.getProjectId(authToken, projectSlug);
            JSONArray backlogStories = TaigaStoryFetcher.fetchUserStories(authToken, projectId);
    
            String roomCode = repository.getCurrentRoomCode();
            if (roomCode == null) {
                JOptionPane.showMessageDialog(null, "No room selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            for (int i = 0; i < backlogStories.length(); i++) {
                JSONObject story = backlogStories.getJSONObject(i);
                String title = story.optString("subject", "Untitled");
                String description = story.optString("description", "(no description)");
                repository.createStory(roomCode, title, description);
            }
    
            JOptionPane.showMessageDialog(null, "Imported " + backlogStories.length() + " stories from Taiga.", "Success", JOptionPane.INFORMATION_MESSAGE);
            backToStoriesPanel();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to import stories:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    // Return to the story panel
    public void backToStoriesPanel() {
        main.setTitle("Create New Story");
        main.setContentPane(storiesPanel);
        main.setSize(800, 600);
        main.setLocationRelativeTo(null);
        main.revalidate();
        main.repaint();
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
