package hbaskar.three;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Panel for importing stories from Taiga.
 * 
 * @author hbaskar
 * ver 1.7 (Modified to remove manual story input)
 */
public class T1StoriesPanel extends JPanel {

    public T1StoriesPanel(T1StoriesNanny storiesNanny) {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title at top
        JLabel titleLabel = new JLabel("Import Stories", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));
        add(titleLabel, BorderLayout.NORTH);

        // Button to import from Taiga
        JButton importButton = new JButton("Import from Taiga");
        importButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        importButton.setBackground(new Color(240, 173, 78));
        importButton.setForeground(Color.DARK_GRAY);
        importButton.setFocusPainted(false);
        importButton.setPreferredSize(new Dimension(300, 50));
        importButton.setToolTipText("Click to import user stories from your Taiga backlog");
        importButton.addActionListener(e -> storiesNanny.importStories());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(importButton);
        add(buttonPanel, BorderLayout.CENTER);  // moved to center since no text field
    }
}
