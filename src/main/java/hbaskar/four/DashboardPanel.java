
package hbaskar.four;
import java.awt.*;
import javax.swing.*;

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

		
		WestPanel westPanel = new WestPanel(dashboardNanny);

		
		add(new CardsPanel(), BorderLayout.CENTER);
		add(new SouthPanel(), BorderLayout.SOUTH);
		add(new WestPanel(dashboardNanny), BorderLayout.EAST);
	}
	
}