package hbaskar.four;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import hbaskar.one.PlanItPokerRepository;
import hbaskar.three.T1StoriesNanny;

/**
 * The main dashboard panel that integrates the cards panel, timer, and stories panel.
 * 
 * This panel uses T1StoriesNanny for story management and initializes
 * sub-panels for stories, cards, and controls for the selected user and room.
 * 
 * 
 * @author Darien
 */
public class T1DashboardPanel extends JPanel {
    private T1StoriesNanny storiesNanny;

    public T1DashboardPanel(T1DashboardNanny dashboardNanny) {

        setLayout(new BorderLayout());

        T1StoriesPanel T1StoriesPanel = new T1StoriesPanel(storiesNanny);
        dashboardNanny.setT1StoriesPanel(T1StoriesPanel);

        // Get the latest player from PlanItPokerRepository current room, for example:
        String currentRoomCode = PlanItPokerRepository.getInstance().getCurrentRoomCode();
        String username = null;
        if (currentRoomCode != null) {
            // Just get the first player in the current room for demo purposes
            var room = PlanItPokerRepository.getInstance().getRoom(currentRoomCode);
            if (room != null && !room.getPlayers().isEmpty()) {
                username = room.getPlayers().get(0);
            }
        }
        if (username == null) {
            username = "Guest";  // fallback username
        }

        T1WestPanel westPanel = new T1WestPanel(dashboardNanny, username);

        add(new CardsPanel(), BorderLayout.CENTER);
        add(T1StoriesPanel, BorderLayout.SOUTH);
        add(westPanel, BorderLayout.EAST);
    }
}

