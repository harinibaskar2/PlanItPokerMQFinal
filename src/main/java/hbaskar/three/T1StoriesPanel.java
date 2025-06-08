package hbaskar.three;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * Allows the user to create a new story. And implements behavior for the user to submit and save their stories
 *
 * @author hbaskar
 * ver 1.1
 */
public class T1StoriesPanel extends JPanel {

    public JTextArea storyTextArea;
    public JTextArea titlTextArea;

    public T1StoriesPanel(T1StoriesNanny storiesNanny) {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Create New Story", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        add(titleLabel, BorderLayout.NORTH);

        String TitlePlaceHolder = "Put your stories text here. Each line contains new story.";

        titlTextArea = new JTextArea(TitlePlaceHolder);
        JScrollPane titlePane = new JScrollPane(titlTextArea);
        add(titlePane, BorderLayout.CENTER);

        String DescriptionPlaceHolder = "Put your stories text here. Each line contains new story.";

        storyTextArea = new JTextArea(DescriptionPlaceHolder);
        JScrollPane storyPane = new JScrollPane(storyTextArea);
        add(storyPane, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2));
        JButton saveAddNewButton = new JButton("Save & Add New");
        JButton saveCloseButton = new JButton("Save & Close");
        JButton importButton = new JButton("Import");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveAddNewButton);
        buttonPanel.add(saveCloseButton);
        buttonPanel.add(importButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        saveAddNewButton.addActionListener(e -> storiesNanny.saveAndAddNew(storyTextArea.getText()));
        saveCloseButton.addActionListener(e -> storiesNanny.saveAndClose(storyTextArea.getText()));
        importButton.addActionListener(e -> storiesNanny.importStories());
        cancelButton.addActionListener(e -> storiesNanny.cancel());

        // Add FocusListener to clear placeholder on first focus
        storyTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (titlTextArea.getText().equals(TitlePlaceHolder)) {
                    storyTextArea.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (titlTextArea.getText().isEmpty()) {
                    titlTextArea.setText(TitlePlaceHolder);
                }
            }
        });
    }
}
