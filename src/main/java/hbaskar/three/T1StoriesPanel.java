package hbaskar.three;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Simple panel that only shows "Import from Taiga Backlog" button.
 * 
 * @author hbaskar
 * ver 1.2
 */
public class T1StoriesPanel extends JPanel {

    public T1StoriesPanel(T1StoriesNanny storiesNanny) {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Create New Story", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JButton importButton = new JButton("Import from Taiga Backlog");
        importButton.setFont(new Font("Arial", Font.PLAIN, 18));
        importButton.setBackground(new Color(240, 173, 78)); // a soft orange
        importButton.setForeground(Color.DARK_GRAY);
        importButton.setFocusPainted(false);
        importButton.setPreferredSize(new Dimension(300, 200));

        importButton.addActionListener(e -> storiesNanny.importStories());

        JPanel centerPanel = new JPanel();
        centerPanel.add(importButton);
        add(centerPanel, BorderLayout.CENTER);
    }
}
