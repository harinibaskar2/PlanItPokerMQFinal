package hbaskar.three;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Panel with import button, project slug display, and a list to display fetched Taiga stories.
 * 
 * @author hbaskar
 * ver 1.5
 */
public class T1StoriesPanel extends JPanel {

    private DefaultListModel<String> listModel;
    private JList<String> storyList;
    private JLabel statusLabel;
    private JTextField projectSlugField;

    public T1StoriesPanel(T1StoriesNanny storiesNanny) {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title at top
        JLabel titleLabel = new JLabel("Create New Story", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));
        add(titleLabel, BorderLayout.NORTH);

        // Top panel with import button
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);

        JButton importButton = new JButton("Import from Taiga Backlog");
        importButton.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        importButton.setBackground(new Color(240, 173, 78));
        importButton.setForeground(Color.DARK_GRAY);
        importButton.setFocusPainted(false);
        importButton.setPreferredSize(new Dimension(300, 60));
        importButton.setToolTipText("Click to import user stories from your Taiga backlog");
        importButton.addActionListener(e -> storiesNanny.importStories());
        topPanel.add(importButton);

        add(topPanel, BorderLayout.PAGE_START);

        // Project slug panel
        projectSlugField = new JTextField();
        projectSlugField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        projectSlugField.setEditable(false);  // read-only
        projectSlugField.setPreferredSize(new Dimension(300, 30));
        projectSlugField.setToolTipText("Taiga Project Slug");

        JLabel projectLabel = new JLabel("Project Slug:");
        projectLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel projectPanel = new JPanel(new BorderLayout(5, 5));
        projectPanel.setBackground(Color.WHITE);
        projectPanel.add(projectLabel, BorderLayout.WEST);
        projectPanel.add(projectSlugField, BorderLayout.CENTER);

        // Stories list and scroll pane
        listModel = new DefaultListModel<>();
        storyList = new JList<>(listModel);
        storyList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        storyList.setVisibleRowCount(10);

        JScrollPane scrollPane = new JScrollPane(storyList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Taiga Stories"));

        // Center panel to hold project slug and stories list
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(projectPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Status label at bottom
        statusLabel = new JLabel("No stories loaded.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        statusLabel.setForeground(Color.GRAY);
        add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * Update the list with story titles.
     * @param stories List of story titles
     */
    public void updateStoriesList(List<String> stories) {
        listModel.clear();
        if (stories == null || stories.isEmpty()) {
            statusLabel.setText("No stories found.");
        } else {
            for (String title : stories) {
                listModel.addElement(title);
            }
            statusLabel.setText(stories.size() + " stories loaded.");
        }
    }

    /**
     * Set the project slug displayed in the text field.
     * @param slug project slug string
     */
    public void setProjectSlug(String slug) {
        projectSlugField.setText(slug != null ? slug : "");
    }
}
