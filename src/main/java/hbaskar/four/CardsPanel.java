package hbaskar.four;


import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel that displays cards used for story point estimation.
 * Each card is a button with a predefined value that users can click to submit their estimate.
 * 
 * @author DarienR
 * @version 1.1
 */
public class CardsPanel extends JPanel {
	private static final Logger logger = LoggerFactory.getLogger(CardsPanel.class);

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
				logger.info(value);
			});
		}
	}

}