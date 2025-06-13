package hbaskar.three;

import hbaskar.T1Card;
import hbaskar.T1TaigaStoryFetcher;
import hbaskar.one.PlanItPokerRepository;
import hbaskar.utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Complete Stories Nanny handling Taiga integration and story management
 * Bridges between Taiga system and new voting architecture
 * 
 * @author PlanItPoker Team
 * @version 3.0 - Complete Integration
 */
public class T1StoriesNanny {
    
    private final Object context;
    private final PlanItPokerRepository repository;
    private final T1TaigaStoryFetcher taigaFetcher;
    
    // UI References
    private JPanel currentPanel;
    private Container parentContainer;
    
    // Story Management
    private List<T1Card> currentStories;
    private boolean isLoading = false;
    
    public T1StoriesNanny(Object context) {
        this.context = context;
        this.repository = PlanItPokerRepository.getInstance();
        this.taigaFetcher = new T1TaigaStoryFetcher();
        this.currentStories = new ArrayList<>();
        
        Logger.info("T1StoriesNanny initialized with complete Taiga integration");
    }
    
    /**
     * Set parent container for panel navigation
     */
    public void setParentContainer(Container container) {
        this.parentContainer = container;
        Logger.debug("Parent container set for navigation");
    }
    
    /**
     * Set current panel reference
     */
    public void setCurrentPanel(JPanel panel) {
        this.currentPanel = panel;
        Logger.debug("Current panel reference set");
    }
    
    // === Taiga Integration Methods ===
    
    /**
     * Initialize Taiga connection and load stories
     */
    public void initializeStories() {
        Logger.info("Initializing stories from Taiga");
        
        if (isLoading) {
            Logger.warn("Story loading already in progress");
            return;
        }
        
        isLoading = true;
        
        // Run in background thread to avoid UI blocking
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Load stories from Taiga
                    loadStoriesFromTaiga();
                    return null;
                } catch (Exception e) {
                    Logger.error("Background story loading failed", e);
                    throw e;
                }
            }
            
            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    
                    // Update UI on EDT
                    updateStoriesDisplay();
                    Logger.info("Stories initialization completed successfully");
                    
                } catch (Exception e) {
                    Logger.error("Failed to initialize stories from Taiga", e);
                    showErrorDialog("Failed to load stories from Taiga: " + e.getMessage());
                } finally {
                    isLoading = false;
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Load stories from Taiga using the fetcher
     */
    private void loadStoriesFromTaiga() throws Exception {
        try {
            // Get current room to add stories to
            String currentRoomCode = repository.getCurrentRoomCode();
            if (currentRoomCode == null) {
                Logger.warn("No current room - cannot load Taiga stories");
                currentStories = new ArrayList<>();
                return;
            }
            
            // Fetch stories from Taiga
            List<T1Card> taigaStories = taigaFetcher.fetchStories();
            
            if (taigaStories != null && !taigaStories.isEmpty()) {
                // Add stories to current room
                PlanItPokerRepository.Room room = repository.getRoom(currentRoomCode);
                if (room != null) {
                    // Clear existing stories if requested
                    // room.clearStories(); // Uncomment if you want to replace instead of append
                    
                    for (T1Card story : taigaStories) {
                        if (story != null && story.isValid()) {
                            room.addStory(story);
                        }
                    }
                    
                    currentStories = new ArrayList<>(taigaStories);
                    Logger.info("Successfully loaded " + taigaStories.size() + " stories from Taiga");
                } else {
                    Logger.error("Room not found: " + currentRoomCode);
                    throw new RuntimeException("Room not found: " + currentRoomCode);
                }
            } else {
                Logger.warn("No stories retrieved from Taiga");
                currentStories = new ArrayList<>();
            }
            
        } catch (Exception e) {
            Logger.error("Error loading stories from Taiga", e);
            throw e;
        }
    }
    
    /**
     * Refresh stories from Taiga
     */
    public void refreshStories() {
        Logger.info("Refreshing stories from Taiga");
        initializeStories();
    }
    
    /**
     * Update stories display in UI
     */
    public void updateStories() {
        Logger.info("Updating stories display");
        updateStoriesDisplay();
    }
    
    /**
     * Load stories (alias for initialize)
     */
    public void loadStories() {
        initializeStories();
    }
    
    /**
     * Fetch stories (alias for initialize)
     */
    public void fetchStories() {
        initializeStories();
    }
    
    /**
     * Process stories after import
     */
    public void processStories() {
        Logger.info("Processing imported stories");
        
        if (currentStories.isEmpty()) {
            Logger.warn("No stories to process");
            return;
        }
        
        // Validate and clean up stories
        List<T1Card> validStories = new ArrayList<>();
        for (T1Card story : currentStories) {
            if (story != null && story.isValid()) {
                validStories.add(story);
            } else {
                Logger.warn("Invalid story found: " + (story != null ? story.getId() : "null"));
            }
        }
        
        currentStories = validStories;
        Logger.info("Processed " + validStories.size() + " valid stories");
        
        // Update repository
        syncStories();
    }
    
    /**
     * Sync stories with repository
     */
    public void syncStories() {
        Logger.info("Syncing stories with repository");
        
        String currentRoomCode = repository.getCurrentRoomCode();
        if (currentRoomCode != null) {
            PlanItPokerRepository.Room room = repository.getRoom(currentRoomCode);
            if (room != null) {
                // Get stories from room
                currentStories = new ArrayList<>(room.getAllStories());
                Logger.info("Synced " + currentStories.size() + " stories from repository");
            }
        }
    }
    
    /**
     * Import stories from Taiga
     */
    public void importStories() {
        Logger.info("Starting Taiga story import");
        initializeStories();
    }
    
    // === UI Navigation Methods ===
    
    /**
     * Navigate back to stories panel
     */
    public void backToStoriesPanel() {
        Logger.info("Navigating back to stories panel");
        
        SwingUtilities.invokeLater(() -> {
            try {
                if (parentContainer != null) {
                    // Try to find stories panel in parent container
                    Component storiesPanel = findStoriesPanelInContainer(parentContainer);
                    
                    if (storiesPanel != null) {
                        // Show stories panel
                        showPanel(storiesPanel);
                        
                        // Refresh stories display
                        updateStoriesDisplay();
                        
                        Logger.info("Successfully navigated to stories panel");
                    } else {
                        Logger.warn("Could not find stories panel for navigation");
                        // Try alternative navigation
                        navigateToMainDashboard();
                    }
                } else {
                    Logger.warn("No parent container set for navigation");
                    navigateToMainDashboard();
                }
            } catch (Exception e) {
                Logger.error("Error during navigation", e);
            }
        });
    }
    
    /**
     * Navigate to main dashboard as fallback
     */
    private void navigateToMainDashboard() {
        try {
            // Find main window or dashboard
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                if (window.isVisible() && window instanceof JFrame) {
                    JFrame frame = (JFrame) window;
                    Container content = frame.getContentPane();
                    
                    // Look for dashboard panel
                    Component dashboard = findDashboardInContainer(content);
                    if (dashboard != null) {
                        dashboard.setVisible(true);
                        content.revalidate();
                        content.repaint();
                        Logger.info("Navigated to main dashboard");
                        return;
                    }
                }
            }
            
            Logger.warn("Could not find main dashboard for navigation");
            
        } catch (Exception e) {
            Logger.error("Error navigating to main dashboard", e);
        }
    }
    
    /**
     * Find stories panel in container hierarchy
     */
    private Component findStoriesPanelInContainer(Container container) {
        for (Component comp : container.getComponents()) {
            String className = comp.getClass().getSimpleName();
            
            if (className.contains("Stories") || 
                className.contains("Dashboard") ||
                className.equals("T1StoriesPanel") ||
                className.equals("T1DashboardPanel")) {
                return comp;
            }
            
            if (comp instanceof Container) {
                Component found = findStoriesPanelInContainer((Container) comp);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
    
    /**
     * Find dashboard in container hierarchy
     */
    private Component findDashboardInContainer(Container container) {
        for (Component comp : container.getComponents()) {
            String className = comp.getClass().getSimpleName();
            
            if (className.contains("Dashboard") || className.contains("Main")) {
                return comp;
            }
            
            if (comp instanceof Container) {
                Component found = findDashboardInContainer((Container) comp);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
    
    /**
     * Show specific panel
     */
    private void showPanel(Component panel) {
        if (panel != null && parentContainer != null) {
            try {
                // Hide current panel
                if (currentPanel != null) {
                    currentPanel.setVisible(false);
                }
                
                // Show target panel
                panel.setVisible(true);
                
                // Update layout
                parentContainer.revalidate();
                parentContainer.repaint();
                
                Logger.debug("Panel visibility updated successfully");
                
            } catch (Exception e) {
                Logger.error("Error showing panel", e);
            }
        }
    }
    
    // === Data Access Methods ===
    
    /**
     * Get current stories
     */
    public List<T1Card> getStories() {
        return new ArrayList<>(currentStories);
    }
    
    /**
     * Set stories
     */
    public void setStories(List<T1Card> stories) {
        this.currentStories = stories != null ? new ArrayList<>(stories) : new ArrayList<>();
        Logger.info("Stories set: " + currentStories.size() + " stories");
    }
    
    /**
     * Check if has stories
     */
    public boolean hasStories() {
        return !currentStories.isEmpty();
    }
    
    /**
     * Get story count
     */
    public int getStoryCount() {
        return currentStories.size();
    }
    
    /**
     * Add single story
     */
    public void addStory(T1Card story) {
        if (story != null && story.isValid()) {
            currentStories.add(story);
            
            // Add to repository
            String currentRoomCode = repository.getCurrentRoomCode();
            if (currentRoomCode != null) {
                repository.addStoryToCurrentRoom(story);
            }
            
            Logger.info("Story added: " + story.getTitle());
            updateStoriesDisplay();
        }
    }
    
    /**
     * Remove story
     */
    public void removeStory(String storyId) {
        boolean removed = currentStories.removeIf(story -> story.getId().equals(storyId));
        if (removed) {
            Logger.info("Story removed: " + storyId);
            updateStoriesDisplay();
        }
    }
    
    /**
     * Clear all stories
     */
    public void clearStories() {
        currentStories.clear();
        Logger.info("All stories cleared");
        updateStoriesDisplay();
    }
    
    // === Authentication Methods ===
    
    /**
     * Authenticate with Taiga
     */
    public boolean authenticateWithTaiga(String username, String password, String project) {
        try {
            Logger.info("Authenticating with Taiga: " + username + "@" + project);
            
            boolean success = taigaFetcher.authenticate(username, password, project);
            
            if (success) {
                Logger.info("Taiga authentication successful");
                return true;
            } else {
                Logger.warn("Taiga authentication failed");
                return false;
            }
            
        } catch (Exception e) {
            Logger.error("Error during Taiga authentication", e);
            return false;
        }
    }
    
    /**
     * Check if authenticated with Taiga
     */
    public boolean isAuthenticated() {
        return taigaFetcher.isAuthenticated();
    }
    
    /**
     * Logout from Taiga
     */
    public void logout() {
        taigaFetcher.logout();
        clearStories();
        Logger.info("Logged out from Taiga and cleared stories");
    }
    
    // === Private Helper Methods ===
    
    /**
     * Update stories display in UI
     */
    private void updateStoriesDisplay() {
        SwingUtilities.invokeLater(() -> {
            try {
                Logger.debug("Triggering stories display update");
                
                // If we can find the dashboard panel, refresh it
                if (parentContainer != null) {
                    triggerStoriesPanelRefresh(parentContainer);
                }
                
                // Also trigger general UI refresh
                triggerGeneralUIRefresh();
                
            } catch (Exception e) {
                Logger.error("Error updating stories display", e);
            }
        });
    }
    
    /**
     * Recursively find and refresh stories panel
     */
    private void triggerStoriesPanelRefresh(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof hbaskar.four.T1StoriesPanel) {
                hbaskar.four.T1StoriesPanel storiesPanel = (hbaskar.four.T1StoriesPanel) comp;
                storiesPanel.refreshStories();
                Logger.debug("Triggered stories panel refresh");
                return;
            }
            
            if (comp instanceof Container) {
                triggerStoriesPanelRefresh((Container) comp);
            }
        }
    }
    
    /**
     * Trigger general UI refresh
     */
    private void triggerGeneralUIRefresh() {
        if (currentPanel != null) {
            currentPanel.revalidate();
            currentPanel.repaint();
        }
        
        if (parentContainer != null) {
            parentContainer.revalidate();
            parentContainer.repaint();
        }
    }
    
    /**
     * Show error dialog
     */
    private void showErrorDialog(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                Component parent = currentPanel != null ? currentPanel : null;
                JOptionPane.showMessageDialog(
                    parent,
                    message,
                    "Taiga Import Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception e) {
                Logger.error("Error showing error dialog", e);
            }
        });
    }
    
    /**
     * Show success dialog
     */
    private void showSuccessDialog(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                Component parent = currentPanel != null ? currentPanel : null;
                JOptionPane.showMessageDialog(
                    parent,
                    message,
                    "Taiga Import Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception e) {
                Logger.error("Error showing success dialog", e);
            }
        });
    }
    
    // === Status Methods ===
    
    /**
     * Check if currently loading
     */
    public boolean isLoading() {
        return isLoading;
    }
    
    /**
     * Get loading status message
     */
    public String getStatusMessage() {
        if (isLoading) {
            return "Loading stories from Taiga...";
        } else if (currentStories.isEmpty()) {
            return "No stories loaded";
        } else {
            return currentStories.size() + " stories loaded";
        }
    }
    
    /**
     * Get Taiga fetcher instance
     */
    public T1TaigaStoryFetcher getTaigaFetcher() {
        return taigaFetcher;
    }
    
    /**
     * Get context object
     */
    public Object getContext() {
        return context;
    }
}