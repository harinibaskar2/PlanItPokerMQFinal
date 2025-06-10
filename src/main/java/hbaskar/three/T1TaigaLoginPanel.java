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
