package hbaskar.four;


import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Panel that displays the cards used for estimating.
 *
 * @author DarienR
 * ver 1.1
 */
public class CardsPanel extends JPanel {
	
	private static final String[] CARD_VALUES = {
		"0", "½", "1", "2", "3", "5", "8", "20", "40", "10", "0", "?"
	};
	
	public CardsPanel() {
		setLayout(new GridLayout(4, 3, 10, 10));
		for (String value : CARD_VALUES) {
			JButton card = new JButton(value);
			card.setBackground(new Color(172, 248, 199));
			card.setFont(new Font("SansSerif", Font.BOLD, 20));
			add(card);
			card.addActionListener(e->{
				System.out.println(value);
				T1DashboardNanny.onSizePress(value);
			});
		}
	}

}