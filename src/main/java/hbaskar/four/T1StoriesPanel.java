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
import hbaskar.one.PlanItPokerRepository;
import hbaskar.one.PlanItPokerRepository.Room;
import hbaskar.three.T1StoriesNanny;

/*
 * This Panel Shows all the stories and turns them into buttons for the user to vote on the sizes of the stories.
 * Over the course, it updates the active stories to do this
 * 
 * @Darien
 */

public class T1StoriesPanel extends JPanel {
    private static JPanel storyCardsPanel;

    public T1StoriesPanel(T1StoriesNanny t1StoriesNanny) {
        setLayout(new BorderLayout());

        storyCardsPanel = new JPanel();
        storyCardsPanel.setLayout(new GridLayout(2, 5, 10, 10)); // 2 rows x 5 columns
        add(storyCardsPanel, BorderLayout.CENTER);

        updateActiveStories();
    }

    public void updateActiveStories() {
        storyCardsPanel.removeAll();

        String currentRoomCode = PlanItPokerRepository.getInstance().getCurrentRoomCode();
        if (currentRoomCode == null) return;

        Room room = PlanItPokerRepository.getInstance().getRoom(currentRoomCode);
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

    private static JButton createStoryButton(T1Card card) {
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
        button.setBackground(new Color(220, 240, 255));
        button.setBorder(new LineBorder(Color.GRAY));

        button.addActionListener(e -> {
            System.out.println("Clicked story: " + card.getTitle());
            // You can add further action here like highlighting or editing
        });

        return button;
    }
}
