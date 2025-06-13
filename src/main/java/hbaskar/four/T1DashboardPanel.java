package hbaskar.four;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import hbaskar.one.T1PlanItPokerRepository;
import hbaskar.three.T1StoriesNanny;

/**
 * The main dashboard panel that integrates the cards panel, timer, and stories panel.
 * 
 * This panel uses T1StoriesNanny for story management and initializes
 * sub-panels for stories, cards, and controls for the selected user and room.
 * 
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

        // Get the latest player from T1PlanItPokerRepository current room
        String currentRoomCode = T1PlanItPokerRepository.getInstance().getCurrentRoomCode();
        String username = null;
        if (currentRoomCode != null) {
            var room = T1PlanItPokerRepository.getInstance().getRoom(currentRoomCode);
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