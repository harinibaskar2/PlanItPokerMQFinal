
package hbaskar.four;
import java.awt.BorderLayout;

import javax.swing.JPanel;

import hbaskar.one.Blackboard;

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

        String username = Blackboard.getLatestPlayer();  // get the current username dynamically
        WestPanel westPanel = new WestPanel(dashboardNanny, username);

        add(new CardsPanel(), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        add(westPanel, BorderLayout.EAST);
    }
}



