package hbaskar.three;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;



/**
 * A Swing JPanel that provides a user interface for importing stories from Taiga.
 * 
 * This panel contains input fields for Taiga username, password, and project slug,
 * along with "Import" and "Cancel" buttons.</p>
 * 
 * When the "Import" button is clicked, the entered credentials and project slug
 * are passed to the {@link T1StoriesNanny} instance to perform the import.</p>
 * 
 * When the "Cancel" button is clicked, the panel requests the {@code T1StoriesNanny}
 * to return to the previous stories panel.

 * 
 * @author hbaskar
 * @version 1.0
 */

public class T1TaigaLoginPanel extends JPanel {

    public T1TaigaLoginPanel(T1StoriesNanny nanny) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Import Stories from Taiga", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField projectSlugField = new JTextField();

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Project Slug:"));
        formPanel.add(projectSlugField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton importButton = new JButton("Import");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(importButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        importButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String slug = projectSlugField.getText();

            nanny.importFromTaigaWithCredentials(username, password, slug);
        });

        cancelButton.addActionListener(e -> nanny.backToStoriesPanel());
    }
}
