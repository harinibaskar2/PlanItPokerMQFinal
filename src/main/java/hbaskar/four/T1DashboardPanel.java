package hbaskar.four;

import hbaskar.controllers.T1VotingController;
import hbaskar.one.PlanItPokerRepository;
import hbaskar.utils.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Clean main dashboard panel coordinating all voting components
 * Uses dependency injection and clean architecture principles
 * 
 * @author PlanItPoker Team
 * @version 3.0 - Clean Architecture
 */
public class T1DashboardPanel extends JPanel {
    
    // Dependencies
    private final T1VotingController votingController;
    
    // UI Components
    private T1StoriesPanel storiesPanel;
    private T1VotingResultsPanel votingResultsPanel;
    private CardsPanel cardsPanel;
    private T1WestPanel westPanel;
    
    // Layout configuration
    private static final int WEST_PANEL_WIDTH = 250;
    private static final int CARDS_PANEL_WIDTH = 300;
    private static final int RESULTS_PANEL_WIDTH = 350;
    private static final int STORIES_PANEL_HEIGHT = 250;
    
    public T1DashboardPanel() {
        this.votingController = new T1VotingController();
        
        initializeComponents();
        setupLayout();
        connectComponents();
        
        Logger.info("T1DashboardPanel initialized with clean architecture");
    }
    
    /**
     * Initialize all child components
     */
    private void initializeComponents() {
        try {
            // Create UI components with dependency injection
            storiesPanel = new T1StoriesPanel(votingController);
            votingResultsPanel = new T1VotingResultsPanel(votingController);
            cardsPanel = new CardsPanel(votingController);
            westPanel = createWestPanel();
            
            Logger.debug("All dashboard components initialized successfully");
            
        } catch (Exception e) {
            Logger.error("Failed to initialize dashboard components", e);
            throw new RuntimeException("Dashboard initialization failed", e);
        }
    }
    
    /**
     * Create west panel with proper dependencies
     */
    private T1WestPanel createWestPanel() {
        String username = getCurrentUsername();
        
        // Create a minimal dashboard nanny for backward compatibility
        // In a full refactor, this would be replaced with proper controller
        T1DashboardNanny legacyNanny = new T1DashboardNanny(null);
        
        return new T1WestPanel(legacyNanny, username);
    }
    
    /**
     * Setup the main layout structure
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create main horizontal split pane
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setLeftComponent(westPanel);
        mainSplitPane.setRightComponent(createRightPanel());
        mainSplitPane.setResizeWeight(0.25); // West panel gets 25% of space
        mainSplitPane.setDividerLocation(WEST_PANEL_WIDTH);
        
        // Add main content to center
        add(mainSplitPane, BorderLayout.CENTER);
        
        // Add stories panel at bottom
        add(storiesPanel, BorderLayout.SOUTH);
        
        // Set preferred sizes
        setPreferredSizes();
    }
    
    /**
     * Create right panel containing cards and results
     */
    private JSplitPane createRightPanel() {
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightSplitPane.setLeftComponent(cardsPanel);
        rightSplitPane.setRightComponent(votingResultsPanel);
        rightSplitPane.setResizeWeight(0.45); // Cards get 45% of right space
        rightSplitPane.setDividerLocation(CARDS_PANEL_WIDTH);
        
        return rightSplitPane;
    }
    
    /**
     * Set preferred sizes for all components
     */
    private void setPreferredSizes() {
        westPanel.setPreferredSize(new Dimension(WEST_PANEL_WIDTH, 400));
        cardsPanel.setPreferredSize(new Dimension(CARDS_PANEL_WIDTH, 400));
        votingResultsPanel.setPreferredSize(new Dimension(RESULTS_PANEL_WIDTH, 400));
        storiesPanel.setPreferredSize(new Dimension(900, STORIES_PANEL_HEIGHT));
    }
    
    /**
     * Connect components and setup communication
     */
    private void connectComponents() {
        // Setup controller callbacks for UI coordination
        votingController.setUIRefreshCallback(this::refreshAllComponents);
        
        // Connect legacy components if needed
        connectLegacyComponents();
        
        Logger.debug("Dashboard components connected successfully");
    }
    
    /**
     * Connect legacy components for backward compatibility
     */
    private void connectLegacyComponents() {
        // This method handles any legacy component integration
        // In a full refactor, this would be removed
        
        // Example: If westPanel needs to trigger story refresh
        // westPanel.addRoomChangeListener(() -> storiesPanel.refreshStories());
    }
    
    /**
     * Refresh all dashboard components
     */
    public void refreshAllComponents() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (storiesPanel != null) {
                    storiesPanel.refreshStories();
                }
                
                if (votingResultsPanel != null) {
                    votingResultsPanel.refreshDisplay();
                }
                
                if (cardsPanel != null) {
                    cardsPanel.updateVotingState();
                }
                
                if (westPanel != null) {
                    westPanel.updatePlayers();
                }
                
                Logger.debug("All dashboard components refreshed");
                
            } catch (Exception e) {
                Logger.error("Error refreshing dashboard components", e);
            }
        });
    }
    
    /**
     * Get current username from repository
     */
    private String getCurrentUsername() {
        try {
            PlanItPokerRepository repo = PlanItPokerRepository.getInstance();
            
            // Try to get logged in user first
            String username = repo.getLoggedInUser();
            if (username != null) {
                return username;
            }
            
            // Fallback to first player in current room
            String currentRoomCode = repo.getCurrentRoomCode();
            if (currentRoomCode != null) {
                PlanItPokerRepository.Room room = repo.getRoom(currentRoomCode);
                if (room != null && !room.getPlayers().isEmpty()) {
                    return room.getPlayers().get(0);
                }
            }
            
            return "Guest"; // Final fallback
            
        } catch (Exception e) {
            Logger.error("Error getting current username", e);
            return "Guest";
        }
    }
    
    // Public API for external components
    
    /**
     * Get the voting controller
     */
    public T1VotingController getVotingController() {
        return votingController;
    }
    
    /**
     * Get stories panel
     */
    public T1StoriesPanel getStoriesPanel() {
        return storiesPanel;
    }
    
    /**
     * Get voting results panel
     */
    public T1VotingResultsPanel getVotingResultsPanel() {
        return votingResultsPanel;
    }
    
    /**
     * Get cards panel
     */
    public CardsPanel getCardsPanel() {
        return cardsPanel;
    }
    
    /**
     * Get west panel
     */
    public T1WestPanel getWestPanel() {
        return westPanel;
    }
    
    /**
     * Handle room change events
     */
    public void onRoomChanged(String roomCode) {
        Logger.logRoomActivity("Room changed to: " + roomCode);
        
        // Clear current selection
        votingController.clearSelection();
        
        // Refresh all components
        refreshAllComponents();
    }
    
    /**
     * Handle application shutdown
     */
    public void shutdown() {
        Logger.info("Shutting down dashboard...");
        
        // Clean up resources if needed
        // In a full implementation, this might close connections, save state, etc.
    }
    
    /**
     * Get current state for debugging
     */
    public String getDebugInfo() {
        return votingController.getStateSummary();
    }
}