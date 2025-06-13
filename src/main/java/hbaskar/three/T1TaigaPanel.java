package hbaskar.three;

import hbaskar.T1Card;
import hbaskar.utils.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Complete Taiga Panel for managing imported stories
 * Shows imported stories and provides management functionality
 * 
 * @author PlanItPoker Team
 * @version 3.0 - Complete Integration
 */
public class T1TaigaPanel extends JPanel {
    
    // UI Components
    private JTable storiesTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton importButton;
    private JButton backButton;
    private JButton clearButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JScrollPane tableScrollPane;
    
    // Dependencies
    private T1StoriesNanny nanny;
    private Container parentContainer;
    
    // State
    private boolean isOperationInProgress = false;
    
    // Table columns
    private static final String[] COLUMN_NAMES = {
        "ID", "Title", "Description", "Assigned To", "Story Points", "Status"
    };
    
    public T1TaigaPanel(T1StoriesNanny nanny) {
        this.nanny = nanny;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Set up nanny references
        if (nanny != null) {
            nanny.setCurrentPanel(this);
        }
        
        // Load initial data
        refreshStoriesDisplay();
        
        Logger.info("T1TaigaPanel initialized");
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        // Table setup
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        storiesTable = new JTable(tableModel);
        setupTable();
        
        tableScrollPane = new JScrollPane(storiesTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Buttons
        refreshButton = new JButton("Refresh Stories");
        importButton = new JButton("Import from Taiga");
        backButton = new JButton("Back to Dashboard");
        clearButton = new JButton("Clear Stories");
        
        // Status components
        statusLabel = new JLabel("Ready");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        
        // Styling
        setupComponentStyling();
    }
    
    /**
     * Setup table configuration
     */
    private void setupTable() {
        // Set column widths
        storiesTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        storiesTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Title
        storiesTable.getColumnModel().getColumn(2).setPreferredWidth(300); // Description
        storiesTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Assigned To
        storiesTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Story Points
        storiesTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        
        // Set table properties
        storiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        storiesTable.setRowHeight(25);
        storiesTable.setShowGrid(true);
        storiesTable.setGridColor(Color.LIGHT_GRAY);
        
        // Alternate row colors
        storiesTable.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        
        // Auto-resize columns
        storiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }
    
    /**
     * Setup component styling
     */
    private void setupComponentStyling() {
        // Button styling
        refreshButton.setBackground(new Color(33, 150, 243));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        
        importButton.setBackground(new Color(76, 175, 80));
        importButton.setForeground(Color.WHITE);
        importButton.setFocusPainted(false);
        
        backButton.setBackground(new Color(158, 158, 158));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        
        clearButton.setBackground(new Color(244, 67, 54));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        
        // Status label styling
        statusLabel.setForeground(new Color(100, 100, 100));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        // Tooltips
        refreshButton.setToolTipText("Refresh the stories list");
        importButton.setToolTipText("Import new stories from Taiga");
        backButton.setToolTipText("Return to main dashboard");
        clearButton.setToolTipText("Clear all imported stories");
    }
    
    /**
     * Setup panel layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // Bottom panel
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Imported Taiga Stories", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(63, 81, 181));
        
        JLabel subtitleLabel = new JLabel("Manage your imported stories", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create content panel
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        // Table panel
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);
        
        panel.add(refreshButton);
        panel.add(importButton);
        panel.add(clearButton);
        panel.add(backButton);
        
        return panel;
    }
    
    /**
     * Create bottom panel
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Color.WHITE);
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(progressBar, BorderLayout.SOUTH);
        
        panel.add(statusPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Refresh button
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRefresh();
            }
        });
        
        // Import button
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleImport();
            }
        });
        
        // Back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleBack();
            }
        });
        
        // Clear button
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleClear();
            }
        });
        
        // Table selection listener
        storiesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });
    }
    
    /**
     * Handle refresh button click
     */
    private void handleRefresh() {
        if (isOperationInProgress) {
            return;
        }
        
        Logger.info("Refreshing stories display");
        
        if (nanny != null) {
            nanny.syncStories();
        }
        
        refreshStoriesDisplay();
        updateStatus("Stories refreshed");
    }
    
    /**
     * Handle import button click
     */
    private void handleImport() {
        if (isOperationInProgress) {
            return;
        }
        
        Logger.info("Starting story import from Taiga");
        
        if (nanny == null) {
            showErrorMessage("No stories manager available");
            return;
        }
        
        // Check if authenticated
        if (!nanny.isAuthenticated()) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "You need to login to Taiga first. Would you like to login now?",
                "Authentication Required",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                // Navigate to login panel
                showLoginPanel();
            }
            return;
        }
        
        // Start import process
        startImport();
    }
    
    /**
     * Start the import process
     */
    private void startImport() {
        isOperationInProgress = true;
        setUIOperating(true);
        updateStatus("Importing stories from Taiga...");
        
        SwingWorker<Void, String> importWorker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    publish("Connecting to Taiga...");
                    
                    if (nanny != null) {
                        nanny.importStories();
                    }
                    
                    publish("Processing imported stories...");
                    
                    if (nanny != null) {
                        nanny.processStories();
                    }
                    
                    publish("Import completed successfully!");
                    
                    return null;
                    
                } catch (Exception e) {
                    Logger.error("Import process failed", e);
                    throw e;
                }
            }
            
            @Override
            protected void process(List<String> chunks) {
                if (!chunks.isEmpty()) {
                    updateStatus(chunks.get(chunks.size() - 1));
                }
            }
            
            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    
                    onImportSuccess();
                    
                } catch (Exception e) {
                    Logger.error("Import failed", e);
                    onImportFailure("Import failed: " + e.getMessage());
                } finally {
                    isOperationInProgress = false;
                    setUIOperating(false);
                }
            }
        };
        
        importWorker.execute();
    }
    
    /**
     * Handle successful import
     */
    private void onImportSuccess() {
        Logger.info("Story import completed successfully");
        
        refreshStoriesDisplay();
        updateStatus("Import completed successfully!");
        
        JOptionPane.showMessageDialog(
            this,
            "Stories imported successfully from Taiga!",
            "Import Successful",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Handle import failure
     */
    private void onImportFailure(String errorMessage) {
        Logger.warn("Story import failed: " + errorMessage);
        
        updateStatus("Import failed");
        showErrorMessage(errorMessage);
    }
    
    /**
     * Handle back button click
     */
    private void handleBack() {
        Logger.info("Navigating back to dashboard");
        
        if (nanny != null) {
            nanny.backToStoriesPanel();
        }
    }
    
    /**
     * Handle clear button click
     */
    private void handleClear() {
        if (isOperationInProgress) {
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to clear all imported stories?",
            "Clear Stories",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            Logger.info("Clearing all imported stories");
            
            if (nanny != null) {
                nanny.clearStories();
            }
            
            refreshStoriesDisplay();
            updateStatus("All stories cleared");
        }
    }
    
    /**
     * Handle table selection
     */
    private void handleTableSelection() {
        int selectedRow = storiesTable.getSelectedRow();
        
        if (selectedRow >= 0) {
            String storyId = (String) tableModel.getValueAt(selectedRow, 0);
            String storyTitle = (String) tableModel.getValueAt(selectedRow, 1);
            
            updateStatus("Selected: " + storyTitle);
            Logger.debug("Story selected: " + storyId);
        }
    }
    
    /**
     * Show login panel
     */
    private void showLoginPanel() {
        try {
            T1TaigaLoginPanel loginPanel = new T1TaigaLoginPanel(nanny);
            loginPanel.setParentContainer(getParentContainer());
            
            // Show in dialog
            JDialog loginDialog = new JDialog((Frame) getParentContainer(), "Taiga Login", true);
            loginDialog.setContentPane(loginPanel);
            loginDialog.setSize(400, 300);
            loginDialog.setLocationRelativeTo(this);
            loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            loginDialog.setVisible(true);
            
        } catch (Exception e) {
            Logger.error("Error showing login panel", e);
            showErrorMessage("Could not show login panel: " + e.getMessage());
        }
    }
    
    /**
     * Refresh stories display
     */
    public void refreshStoriesDisplay() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Clear existing rows
                tableModel.setRowCount(0);
                
                // Get stories from nanny
                List<T1Card> stories = null;
                if (nanny != null) {
                    stories = nanny.getStories();
                }
                
                if (stories != null && !stories.isEmpty()) {
                    // Add stories to table
                    for (T1Card story : stories) {
                        addStoryToTable(story);
                    }
                    
                    updateStatus(stories.size() + " stories loaded");
                } else {
                    updateStatus("No stories available");
                }
                
                // Update table display
                tableModel.fireTableDataChanged();
                
            } catch (Exception e) {
                Logger.error("Error refreshing stories display", e);
                updateStatus("Error loading stories");
            }
        });
    }
    
    /**
     * Add story to table
     */
    private void addStoryToTable(T1Card story) {
        if (story == null) return;
        
        String[] row = new String[6];
        row[0] = story.getId();
        row[1] = story.getTitle();
        row[2] = truncateDescription(story.getDescription());
        row[3] = story.getAssignedUser() != null ? story.getAssignedUser() : "Unassigned";
        row[4] = String.format("%.1f", story.getTotalPoints());
        row[5] = getStoryStatus(story);
        
        tableModel.addRow(row);
    }
    
    /**
     * Truncate description for table display
     */
    private String truncateDescription(String description) {
        if (description == null) return "";
        
        if (description.length() > 100) {
            return description.substring(0, 97) + "...";
        }
        
        return description;
    }
    
    /**
     * Get story status
     */
    private String getStoryStatus(T1Card story) {
        if (story.getVoteCount() > 0) {
            if (story.isRevealed()) {
                return "Voted & Revealed";
            } else {
                return "Voted";
            }
        } else {
            return "Not Voted";
        }
    }
    
    /**
     * Set UI operating state
     */
    private void setUIOperating(boolean operating) {
        SwingUtilities.invokeLater(() -> {
            refreshButton.setEnabled(!operating);
            importButton.setEnabled(!operating);
            clearButton.setEnabled(!operating);
            storiesTable.setEnabled(!operating);
            
            progressBar.setVisible(operating);
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
            
            JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
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
     * Get selected story
     */
    public T1Card getSelectedStory() {
        int selectedRow = storiesTable.getSelectedRow();
        
        if (selectedRow >= 0 && nanny != null) {
            String storyId = (String) tableModel.getValueAt(selectedRow, 0);
            
            List<T1Card> stories = nanny.getStories();
            for (T1Card story : stories) {
                if (story.getId().equals(storyId)) {
                    return story;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Alternating row renderer for table
     */
    private static class AlternatingRowRenderer extends JLabel implements TableCellRenderer {
        
        public AlternatingRowRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            setText(value != null ? value.toString() : "");
            
            if (isSelected) {
                setBackground(new Color(184, 207, 229));
                setForeground(Color.BLACK);
            } else {
                if (row % 2 == 0) {
                    setBackground(Color.WHITE);
                } else {
                    setBackground(new Color(245, 245, 245));
                }
                setForeground(Color.BLACK);
            }
            
            return this;
        }
    }
}