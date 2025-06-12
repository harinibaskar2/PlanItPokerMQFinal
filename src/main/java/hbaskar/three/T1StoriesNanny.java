package hbaskar.three;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import hbaskar.TaigaStoryFetcher;
import hbaskar.four.T1DashboardNanny;
import hbaskar.one.Main;
import hbaskar.one.PlanItPokerRepository;
import hbaskar.two.T1CreateRoomNanny;

/**
 * Controller for managing Taiga story import and UI transitions.
 * 
 * @author hbaskar
 */
public class T1StoriesNanny {

    private T1StoriesPanel storiesPanel;
    private Main main;
    private PlanItPokerRepository repository = PlanItPokerRepository.getInstance();

    public T1StoriesNanny(Main main) {
        this.main = main;
        this.storiesPanel = new T1StoriesPanel(this);
    }

    public T1StoriesNanny(T1StoriesPanel panel) {
        this.storiesPanel = panel;
    }

    public T1StoriesPanel getPanel() {
        return storiesPanel;
    }

    public void importStories() {
        System.out.println("Opening Taiga login panel...");
        main.setTitle("Login to Taiga");

        JPanel loginPanel = new T1TaigaLoginPanel(this);
        main.setContentPane(loginPanel);
        main.revalidate();
        main.repaint();
    }

    public void importFromTaigaWithCredentials(String username, String password, String projectSlug) {
        repository.setTaigaCredentials(username, password);

        System.out.println("Importing from Taiga for project: " + projectSlug);

        final JDialog loadingDialog = createLoadingDialog(main, "Importing stories from Taiga...");

        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> loadingDialog.setVisible(true));

                String authToken = TaigaStoryFetcher.loginAndGetToken(username, password);
                int projectId = TaigaStoryFetcher.getProjectId(authToken, projectSlug);
                JSONArray backlogStories = TaigaStoryFetcher.fetchUserStories(authToken, projectId);

                String roomCode = repository.getCurrentRoomCode();
                if (roomCode == null) {
                    SwingUtilities.invokeLater(() -> {
                        loadingDialog.dispose();
                        JOptionPane.showMessageDialog(main, "No room selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    });
                    return;
                }

                for (int i = 0; i < backlogStories.length(); i++) {
                    JSONObject story = backlogStories.getJSONObject(i);
                    String title = story.optString("subject", "Untitled");
                    String description = story.optString("description", "(no description)");
                    repository.createStory(roomCode, title, description);
                }

                SwingUtilities.invokeLater(() -> {
                    loadingDialog.dispose();
                    JOptionPane.showMessageDialog(main, "Imported " + backlogStories.length() + " stories from Taiga.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    switchToDashboard();  // <--- Switch here after import
                });

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    loadingDialog.dispose();
                    JOptionPane.showMessageDialog(main, "Failed to import stories:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    public void backToStoriesPanel() {
        main.setTitle("Create New Story");
        main.setContentPane(storiesPanel);
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
        main.setContentPane(new hbaskar.four.T1DashboardPanel(dashboardNanny));
        main.revalidate();
        main.repaint();
    }

    private void switchToSchedule() {
        main.setTitle("Schedule Room");
        T1CreateRoomNanny roomNanny = new T1CreateRoomNanny(main);
        main.setContentPane(new hbaskar.two.T1ScheduleRoomPanel(roomNanny));
        main.revalidate();
        main.repaint();
    }

    private JDialog createLoadingDialog(JFrame parent, String message) {
        JDialog dialog = new JDialog(parent, true);
        JPanel panel = new JPanel();
        panel.add(new JLabel(message));
        dialog.getContentPane().add(panel);
        dialog.setUndecorated(true);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        return dialog;
    }
}
