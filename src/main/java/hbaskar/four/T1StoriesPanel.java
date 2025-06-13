package hbaskar.four;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import hbaskar.T1Card;
import hbaskar.one.T1PlanItPokerRepository;
import hbaskar.one.T1PlanItPokerRepository.Room;
import hbaskar.three.T1StoriesNanny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel displaying all active stories as buttons for users to vote on story sizes.
 * <p>
 * The panel dynamically updates to show the current active stories in the
 * selected room and provides UI controls for story interaction.
 * </p>
 * 
 * @author DarienR5
 */

public class T1StoriesPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(T1StoriesPanel.class);
    private static JPanel storyCardsPanel;
    public Object storyTextArea;
    public String id;
    
        public T1StoriesPanel(T1StoriesNanny t1StoriesNanny) {
            setLayout(new BorderLayout());
    
            storyCardsPanel = new JPanel();
            storyCardsPanel.setLayout(new GridLayout(2, 5, 10, 10)); // 2 rows x 5 columns
            add(storyCardsPanel, BorderLayout.CENTER);
    
            updateActiveStories();
        }
    
        public void updateActiveStories() {
            storyCardsPanel.removeAll();

        String currentRoomCode = T1PlanItPokerRepository.getInstance().getCurrentRoomCode();
        if (currentRoomCode == null) return;

        Room room = T1PlanItPokerRepository.getInstance().getRoom(currentRoomCode);
        if (room == null) return;

        List<T1Card> stories = room.getAllStories();

        int count = 0;
        for (T1Card card : stories) {
            JButton storyButton = createStoryButton(card);
            storyCardsPanel.add(storyButton);
            count++;
        }

        // Pad the rest of the grid with empty placeholders if needed
        while (count < 10) {
            storyCardsPanel.add(new JPanel());
            count++;
        }

        revalidate();
        repaint();
    }

    private JButton createStoryButton(T1Card card) {
        String assignedUser = card.getAssignedUser() != null ? card.getAssignedUser() : "Unassigned";
        String totalPoints = String.format("%.2f", card.getTotalPoints());
       

        String label = "<html><b>" + card.getTitle() + "</b><br/>"
                     + card.getDescription() + "<br/>"
                     + "Assigned to: " + assignedUser + "<br/>"
                     + "Total Points: " + totalPoints + "<br/>"
                     + "Size: " + card.getAverageScore()
                     + "</html>";

        JButton button = new JButton(label);
        button.setPreferredSize(new Dimension(150, 100));

        // Highlighted color (light yellow)
        Color highlightColor = new Color(255, 255, 153); // light yellow
        button.setBackground(highlightColor);
        button.setOpaque(true); // ensure background color is painted
        button.setBorder(new LineBorder(Color.GRAY));

        button.addActionListener(e -> {
            logger.info("Clicked story: " + card.getTitle());
            this.id = card.getId();
        });

        return button;
    }
    
}
