package hbaskar.four;

import hbaskar.T1Card;
import hbaskar.controllers.T1VotingController;
import hbaskar.one.Main;
import hbaskar.utils.Logger;

/**
 * Clean Dashboard Nanny acting as bridge/adapter to new VotingController
 * Maintains backward compatibility while delegating to clean architecture
 * 
 * @author DarienR5
 * @version 4.0 - Clean Architecture Bridge
 */
public class T1DashboardNanny {

    // Legacy UI components (for backward compatibility)
    private T1StoriesPanel storiesPanel;
    private T1VotingResultsPanel votingResultsPanel;
    
    // New architecture dependency
    private final T1VotingController votingController;
    
    // Legacy reference (kept for compatibility)
    private final Main main;

    public T1DashboardNanny(Main main) {
        this.main = main;
        this.votingController = new T1VotingController();
        
        Logger.info("T1DashboardNanny initialized as bridge to VotingController");
    }
    
    /**
     * Constructor with VotingController injection (preferred)
     */
    public T1DashboardNanny(Main main, T1VotingController votingController) {
        this.main = main;
        this.votingController = votingController != null ? votingController : new T1VotingController();
        
        Logger.info("T1DashboardNanny initialized with injected VotingController");
    }

    // === Legacy Component Setters (for backward compatibility) ===
    
    public void setT1StoriesPanel(T1StoriesPanel storiesPanel) {
        this.storiesPanel = storiesPanel;
        
        // Connect to new architecture
        if (storiesPanel != null) {
            storiesPanel.setVotingController(votingController);
        }
        
        Logger.debug("Stories panel connected to dashboard nanny");
    }
    
    public void setVotingResultsPanel(T1VotingResultsPanel votingResultsPanel) {
        this.votingResultsPanel = votingResultsPanel;
        
        // Connect to new architecture
        if (votingResultsPanel != null) {
            votingResultsPanel.setVotingController(votingController);
        }
        
        Logger.debug("Voting results panel connected to dashboard nanny");
    }

    // === Legacy Event Handlers (delegate to new architecture) ===
    
    /**
     * Handle room selection - delegates to controller
     */
    public void onRoomSelected(String roomName) {
        Logger.logRoomActivity("Room selected via legacy nanny: " + roomName);
        
        // Clear selection in new architecture
        votingController.clearSelection();
        
        // Refresh UI components if they exist
        refreshLegacyComponents();
    }
    
    /**
     * Handle story selection - delegates to controller
     */
    public void onStorySelected(T1Card story) {
        if (story != null) {
            votingController.selectStory(story);
            Logger.debug("Story selection delegated to VotingController: " + story.getTitle());
        }
    }
    
    /**
     * Handle vote submission - delegates to controller
     */
    public void onVoteSubmitted(String value) {
        String playerName = getCurrentPlayerName();
        boolean success = votingController.submitVote(playerName, value);
        
        if (!success) {
            Logger.warn("Vote submission failed via legacy nanny");
        }
    }
    
    /**
     * Handle cards revealed - delegates to controller
     */
    public void onCardsRevealed(T1Card story, boolean isRevealed) {
        if (story == null) return;
        
        if (isRevealed) {
            votingController.revealVotes();
        } else {
            votingController.hideVotes();
        }
        
        Logger.debug("Card reveal delegated to VotingController");
    }

    // === Public API (delegates to VotingController) ===
    
    /**
     * Get currently selected story
     */
    public T1Card getSelectedStory() {
        return votingController.getSelectedStory();
    }
    
    /**
     * Clear current selection
     */
    public void clearSelection() {
        votingController.clearSelection();
    }
    
    /**
     * Check if story is selected
     */
    public boolean isStorySelected(T1Card story) {
        return votingController.isStorySelected(story);
    }

    // === Legacy Static Method Support ===
    
    /**
     * Static method for backward compatibility
     * @deprecated Use instance method handleSizePress instead
     */
    @Deprecated
    public static void onSizePress(String value) {
        Logger.warn("Deprecated static method onSizePress called - use instance method");
        Logger.debug("Static onSizePress called with value: " + value);
    }
    
    /**
     * Instance method for handling votes (preferred)
     */
    public void handleSizePress(String value) {
        onVoteSubmitted(value);
    }

    // === Utility Methods ===
    
    /**
     * Get current player name
     */
    private String getCurrentPlayerName() {
        hbaskar.one.PlanItPokerRepository repo = hbaskar.one.PlanItPokerRepository.getInstance();
        String playerName = repo.getLoggedInUser();
        return playerName != null ? playerName : "Anonymous";
    }
    
    /**
     * Refresh legacy UI components
     */
    private void refreshLegacyComponents() {
        if (storiesPanel != null) {
            storiesPanel.refreshStories();
        }
        
        if (votingResultsPanel != null) {
            votingResultsPanel.refreshDisplay();
        }
    }
    
    /**
     * Get the underlying VotingController (for new code)
     */
    public T1VotingController getVotingController() {
        return votingController;
    }
    
    /**
     * Get debug information
     */
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== T1DashboardNanny State ===\n");
        info.append("Has Stories Panel: ").append(storiesPanel != null).append("\n");
        info.append("Has Results Panel: ").append(votingResultsPanel != null).append("\n");
        info.append("VotingController State: \n").append(votingController.getStateSummary());
        return info.toString();
    }
}