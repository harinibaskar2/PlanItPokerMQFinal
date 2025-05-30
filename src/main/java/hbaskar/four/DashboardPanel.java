package hbaskar.four;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import hbaskar.one.PlanItPokerRepository;

/**
 * Integrates a dashboard with the cards, timer, and stories.
 *
 * @author javiergs
 */
public class DashboardPanel extends JPanel {

    public DashboardPanel(DashboardNanny dashboardNanny) {

        setLayout(new BorderLayout());

        SouthPanel southPanel = new SouthPanel();
        dashboardNanny.setSouthPanel(southPanel);

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

        WestPanel westPanel = new WestPanel(dashboardNanny, username);

        add(new CardsPanel(), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        add(westPanel, BorderLayout.EAST);
    }
}
