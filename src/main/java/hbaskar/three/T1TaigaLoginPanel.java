package hbaskar.three;

import hbaskar.utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



/**
 * Complete Taiga Login Panel with full integration
 * Handles authentication and story import initiation
 * 
 * @author PlanItPoker Team
 * @version 3.0 - Complete Integration
 */
public class T1TaigaLoginPanel extends JPanel {
    
    // UI Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField projectField;
    private JButton loginButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    
    // Dependencies
    private T1StoriesNanny nanny;
    private Container parentContainer;
    
    // State
    private boolean isLoggingIn = false;
    
    public T1TaigaLoginPanel(T1StoriesNanny nanny) {
        this.nanny = nanny;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Set up nanny references
        if (nanny != null) {
            nanny.setCurrentPanel(this);
        }
        
        Logger.info("T1TaigaLoginPanel initialized");
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        // Text fields
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        projectField = new JTextField(20);
        
        // Buttons
        loginButton = new JButton("Login to Taiga");
        cancelButton = new JButton("Cancel");
        
        // Status components
        statusLabel = new JLabel("Enter your Taiga credentials");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        
        // Styling
        setupComponentStyling();
    }
    
    /**
     * Setup component styling
     */
    private void setupComponentStyling() {
        // Set fonts
        Font labelFont = new Font("SansSerif", Font.BOLD, 12);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 12);
        
        usernameField.setFont(fieldFont);
        passwordField.setFont(fieldFont);
        projectField.setFont(fieldFont);
        
        // Set colors
        loginButton.setBackground(new Color(76, 175, 80));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        cancelButton.setBackground(new Color(244, 67, 54));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        
        statusLabel.setForeground(new Color(100, 100, 100));
        
        // Set tooltips
        usernameField.setToolTipText("Enter your Taiga username");
        passwordField.setToolTipText("Enter your Taiga password");
        projectField.setToolTipText("Enter your Taiga project name or ID");
        loginButton.setToolTipText("Click to authenticate with Taiga");
        cancelButton.setToolTipText("Cancel login and return");
    }
    
    /**
     * Setup panel layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Taiga Integration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(63, 81, 181));
        
        JLabel subtitleLabel = new JLabel("Import stories from your Taiga project", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create form panel
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Login Credentials"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(passwordField, gbc);
        
        // Project
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Project:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(projectField, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(statusLabel, gbc);
        
        // Progress bar
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(progressBar, gbc);
        
        return panel;
    }
    
    /**
     * Create button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);
        
        panel.add(loginButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        // Cancel button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCancel();
            }
        });
        
        // Enter key on fields
        ActionListener enterKeyListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loginButton.isEnabled()) {
                    handleLogin();
                }
            }
        };
        
        usernameField.addActionListener(enterKeyListener);
        passwordField.addActionListener(enterKeyListener);
        projectField.addActionListener(enterKeyListener);
    }
    
    /**
     * Handle login button click
     */
    private void handleLogin() {
        if (isLoggingIn) {
            Logger.warn("Login already in progress");
            return;
        }
        
        // Validate input
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String project = projectField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty() || project.isEmpty()) {
            showErrorMessage("Please fill in all fields");
            return;
        }
        
        // Start login process
        startLogin(username, password, project);
    }
    
    /**
     * Start the login process
     */
    private void startLogin(String username, String password, String project) {
        isLoggingIn = true;
        
        // Update UI
        setUILoading(true);
        updateStatus("Connecting to Taiga...");
        
        // Perform authentication in background
        SwingWorker<Boolean, String> loginWorker = new SwingWorker<Boolean, String>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    Logger.info("Starting Taiga authentication for user: " + username);
                    
                    // Update status
                    publish("Authenticating with Taiga...");
                    
                    // Authenticate with Taiga
                    boolean authSuccess = false;
                    if (nanny != null) {
                        authSuccess = nanny.authenticateWithTaiga(username, password, project);
                    }
                    
                    if (authSuccess) {
                        publish("Authentication successful! Loading stories...");
                        
                        // Initialize stories
                        if (nanny != null) {
                            nanny.setParentContainer(getParentContainer());
                            nanny.initializeStories();
                        }
                        
                        publish("Stories loaded successfully!");
                        Thread.sleep(500); // Brief pause to show success message
                        
                        return true;
                    } else {
                        return false;
                    }
                    
                } catch (Exception e) {
                    Logger.error("Login process failed", e);
                    throw e;
                }
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                if (!chunks.isEmpty()) {
                    updateStatus(chunks.get(chunks.size() - 1));
                }
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    
                    if (success) {
                        onLoginSuccess();
                    } else {
                        onLoginFailure("Authentication failed. Please check your credentials.");
                    }
                    
                } catch (Exception e) {
                    Logger.error("Login worker failed", e);
                    onLoginFailure("Login failed: " + e.getMessage());
                } finally {
                    isLoggingIn = false;
                    setUILoading(false);
                }
            }
        };
        
        loginWorker.execute();
    }
    
    /**
     * Handle successful login
     */
    private void onLoginSuccess() {
        Logger.info("Taiga login successful");
        
        updateStatus("Login successful! Redirecting...");
        
        // Navigate back to stories panel
        SwingUtilities.invokeLater(() -> {
            try {
                if (nanny != null) {
                    nanny.backToStoriesPanel();
                }
                
                // Show success message
                JOptionPane.showMessageDialog(
                    this,
                    "Successfully connected to Taiga and imported stories!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
            } catch (Exception e) {
                Logger.error("Error during post-login navigation", e);
            }
        });
    }
    
    /**
     * Handle login failure
     */
    private void onLoginFailure(String errorMessage) {
        Logger.warn("Taiga login failed: " + errorMessage);
        
        updateStatus("Login failed");
        showErrorMessage(errorMessage);
        
        // Clear password field
        passwordField.setText("");
        passwordField.requestFocus();
    }
    
    /**
     * Handle cancel button click
     */
    private void handleCancel() {
        Logger.info("Taiga login cancelled");
        
        if (isLoggingIn) {
            updateStatus("Cancelling...");
            // Note: In a real implementation, you might want to interrupt the login process
        }
        
        // Navigate back
        if (nanny != null) {
            nanny.backToStoriesPanel();
        }
    }
    
    /**
     * Set UI loading state
     */
    private void setUILoading(boolean loading) {
        SwingUtilities.invokeLater(() -> {
            loginButton.setEnabled(!loading);
            cancelButton.setEnabled(!loading);
            usernameField.setEnabled(!loading);
            passwordField.setEnabled(!loading);
            projectField.setEnabled(!loading);
            
            progressBar.setVisible(loading);
            
            if (loading) {
                loginButton.setText("Logging in...");
            } else {
                loginButton.setText("Login to Taiga");
            }
        });
    }
    
    /**
     * Update status message
     */
    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(new Color(100, 100, 100));
        });
    }
    
    /**
     * Show error message
     */
    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(new Color(244, 67, 54));
            
            // Also show dialog for important errors
            if (message.contains("failed") || message.contains("error")) {
                JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
    
    /**
     * Get parent container
     */
    private Container getParentContainer() {
        if (parentContainer != null) {
            return parentContainer;
        }
        
        Container parent = getParent();
        while (parent != null && !(parent instanceof JFrame)) {
            parent = parent.getParent();
        }
        
        return parent;
    }
    
    /**
     * Set parent container
     */
    public void setParentContainer(Container container) {
        this.parentContainer = container;
        
        if (nanny != null) {
            nanny.setParentContainer(container);
        }
    }
    
    /**
     * Set stories nanny
     */
    public void setNanny(T1StoriesNanny nanny) {
        this.nanny = nanny;
        
        if (nanny != null) {
            nanny.setCurrentPanel(this);
            nanny.setParentContainer(getParentContainer());
        }
    }
    
    /**
     * Get stories nanny
     */
    public T1StoriesNanny getNanny() {
        return nanny;
    }
    
    /**
     * Reset form
     */
    public void resetForm() {
        SwingUtilities.invokeLater(() -> {
            usernameField.setText("");
            passwordField.setText("");
            projectField.setText("");
            updateStatus("Enter your Taiga credentials");
            setUILoading(false);
        });
    }
    
    /**
     * Pre-fill form with saved credentials (if available)
     */
    public void prefillCredentials(String username, String project) {
        SwingUtilities.invokeLater(() -> {
            if (username != null && !username.isEmpty()) {
                usernameField.setText(username);
            }
            
            if (project != null && !project.isEmpty()) {
                projectField.setText(project);
            }
            
            // Focus on password field if username is prefilled
            if (username != null && !username.isEmpty()) {
                passwordField.requestFocus();
            } else {
                usernameField.requestFocus();
            }
        });
    }
}