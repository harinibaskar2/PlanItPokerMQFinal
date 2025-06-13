package hbaskar.four;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import hbaskar.one.PlanItPokerRepository;
import hbaskar.three.T1StoriesNanny;

/**
 * Integrates a dashboard with the cards, timer, and stories.
 *
 * @author Darien
 * ver 1.1 - Added component connections for story scoring
 */
public class T1DashboardPanel extends JPanel {
    private T1StoriesNanny storiesNanny;

    public T1DashboardPanel(T1DashboardNanny dashboardNanny) {
        setLayout(new BorderLayout());

        // Create stories panel
        T1StoriesPanel storiesPanel = new T1StoriesPanel(storiesNanny);
        dashboardNanny.setT1StoriesPanel(storiesPanel);

        // Create cards panel and connect it to stories panel
        CardsPanel cardsPanel = new CardsPanel();
        cardsPanel.setStoriesPanel(storiesPanel);

        // Get the latest player from PlanItPokerRepository current room
        String currentRoomCode = PlanItPokerRepository.getInstance().getCurrentRoomCode();
        String username = null;
        if (currentRoomCode != null) {
            var room = PlanItPokerRepository.getInstance().getRoom(currentRoomCode);
            if (room != null && !room.getPlayers().isEmpty()) {
                username = room.getPlayers().get(0);
            }
        }
        if (username == null) {
            username = "Guest";  // fallback username
        }

        T1WestPanel westPanel = new T1WestPanel(dashboardNanny, username);

        // Add components to layout
        add(cardsPanel, BorderLayout.CENTER);
        add(storiesPanel, BorderLayout.SOUTH);
        add(westPanel, BorderLayout.EAST);
    }
}