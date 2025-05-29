package hbaskar.four;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Stories organized in tabs.
 * The first tab contains the active stories, and the second one contains the completed stories.
 */
public class SouthPanel extends JPanel {
    private JPanel storyCardsPanel;

    public SouthPanel() {
        setLayout(new BorderLayout());
        storyCardsPanel = new JPanel();
        storyCardsPanel.setLayout(new BoxLayout(storyCardsPanel, BoxLayout.Y_AXIS));
        add(storyCardsPanel, BorderLayout.CENTER);
    }

    public void updateActiveStories(String stories) {
        storyCardsPanel.removeAll();  // Clear previous stories

        String[] storyArray = stories.split("\n");
        for (String story : storyArray) {
            storyCardsPanel.add(createStoryCard(story));
        }

        revalidate();  // Revalidate the layout
        repaint();     // Repaint the panel to reflect changes
    }

    private JPanel createStoryCard(String story) {
        JPanel storyCard = new JPanel();
        storyCard.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        storyCard.add(new JLabel(story));
        return storyCard;
    }
}
