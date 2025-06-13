package hbaskar.three;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import hbaskar.T1TaigaStoryFetcher;
import hbaskar.four.T1DashboardNanny;
import hbaskar.one.Main;
import hbaskar.one.T1PlanItPokerRepository;
import hbaskar.two.T1CreateRoomNanny;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class responsible for managing the import of stories from Taiga
 * and handling UI transitions related to story management within the PlanItPoker application.
 * 
 * This class interacts with the Taiga API through {@link T1TaigaStoryFetcher}
 * to authenticate, fetch project and backlog story data, and coordinate updates
 * to the user interface via the main application frame.

 * 
 * @author DarienR5
 * @version 1.0
 */
public class T1StoriesNanny {
    private static final Logger logger = LoggerFactory.getLogger(T1StoriesNanny.class);
    private T1TaigaPanel storiesPanel;
    private Main main;
    private T1PlanItPokerRepository repository = T1PlanItPokerRepository.getInstance();

    public T1StoriesNanny(Main main) {
        this.main = main;
        this.storiesPanel = new T1TaigaPanel(this);
    }

    public T1StoriesNanny(T1TaigaPanel panel) {
        this.storiesPanel = panel;
    }

    public T1TaigaPanel getPanel() {
        return storiesPanel;
    }

    public void importStories() {
        logger.trace("Opening Taiga login panel...");
        main.setTitle("Login to Taiga");

        JPanel loginPanel = new T1TaigaLoginPanel(this);
        main.setContentPane(loginPanel);
        main.revalidate();
        main.repaint();
    }

    public void importFromTaigaWithCredentials(String username, String password, String projectSlug) {
        repository.setTaigaCredentials(username, password);

        logger.trace("Importing from Taiga for project: " + projectSlug);

        final JDialog loadingDialog = createLoadingDialog(main, "Importing stories from Taiga...");

        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> loadingDialog.setVisible(true));

                String authToken = T1TaigaStoryFetcher.loginAndGetToken(username, password);
                int projectId = T1TaigaStoryFetcher.getProjectId(authToken, projectSlug);
                JSONArray backlogStories = T1TaigaStoryFetcher.fetchUserStories(authToken, projectId);

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
        logger.trace("Cancelling story creation...");
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
