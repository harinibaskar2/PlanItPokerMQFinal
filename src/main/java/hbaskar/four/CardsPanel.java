package hbaskar.four;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel that displays the cards used for estimating.
 *
 * @author DarienR
 * ver 1.2 - Added story scoring functionality
 */
public class CardsPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(CardsPanel.class);
    private T1StoriesPanel storiesPanel; // Add reference to stories panel

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
            card.addActionListener(e -> {
                logger.info("Card selected: " + value);
                onCardSelected(value);
            });
        }
    }

    // Add method to set stories panel reference
    public void setStoriesPanel(T1StoriesPanel storiesPanel) {
        this.storiesPanel = storiesPanel;
    }

    // Handle card selection
    private void onCardSelected(String value) {
        if (storiesPanel != null && storiesPanel.id != null) {
            // Call the existing onSizePress method from T1DashboardNanny
            T1DashboardNanny.onSizePress(storiesPanel.id, value);
            logger.info("Score " + value + " assigned to story: " + storiesPanel.id);
        } else {
            logger.warn("No story selected for scoring");
        }
    }
}