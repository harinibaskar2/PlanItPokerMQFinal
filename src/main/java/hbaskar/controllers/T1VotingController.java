
package hbaskar.controllers;

import hbaskar.T1Card;
import hbaskar.interfaces.T1IStorySelectionService;
import hbaskar.interfaces.T1IVotingService;
import hbaskar.interfaces.T1IChartService;
import hbaskar.services.T1StorySelectionService;
import hbaskar.services.T1VotingService;
import hbaskar.services.T1ChartService;
import hbaskar.models.T1VotingSession;
import hbaskar.utils.Logger;

import java.util.function.Consumer;

/**
 * Controller coordinating voting operations
 * Implements clean separation between UI and business logic
 * 
 * @author PlanItPoker Team
 * @version 1.0
 */
public class T1VotingController {
    
    private final T1IStorySelectionService selectionService;
    private final T1IVotingService votingService;
    private final T1IChartService chartService;
    
    // UI update callbacks
    private Consumer<T1Card> storySelectionCallback;
    private Consumer<T1VotingSession> votingUpdateCallback;
    private Runnable uiRefreshCallback;
    
    public T1VotingController() {
        this.selectionService = new T1StorySelectionService();
        this.votingService = new T1VotingService();
        this.chartService = new T1ChartService();
        
        // Set up selection listener
        selectionService.addSelectionListener(this::onStorySelectionChanged);
        
        Logger.info("VotingController initialized");
    }
    
    // === Story Selection Operations ===
    
    /**
     * Select a story for voting
     */
    public void selectStory(T1Card story) {
        if (story == null) {
            Logger.warn("Attempted to select null story");
            return;
        }
        
        selectionService.selectStory(story);
    }
    
    /**
     * Get currently selected story
     */
    public T1Card getSelectedStory() {
        return selectionService.getSelectedStory();
    }
    
    /**
     * Check if story is selected
     */
    public boolean isStorySelected(T1Card story) {
        return selectionService.isStorySelected(story);
    }
    
    /**
     * Clear story selection
     */
    public void clearSelection() {
        selectionService.clearSelection();
    }
    
    // === Voting Operations ===
    
    /**
     * Submit a vote for the selected story
     */
    public boolean submitVote(String playerName, String voteValue) {
        T1Card selectedStory = getSelectedStory();
        
        if (selectedStory == null) {
            Logger.warn("No story selected for voting by player: " + playerName);
            return false;
        }
        
        boolean success = votingService.submitVote(playerName, selectedStory.getId(), voteValue);
        
        if (success) {
            // Trigger UI updates
            triggerVotingUpdate(selectedStory.getId());
            triggerUIRefresh();
        }
        
        return success;
    }
    
    /**
     * Reveal votes for selected story
     */
    public boolean revealVotes() {
        T1Card selectedStory = getSelectedStory();
        
        if (selectedStory == null) {
            Logger.warn("No story selected for revealing votes");
            return false;
        }
        
        boolean success = votingService.revealVotes(selectedStory.getId());
        
        if (success) {
            triggerUIRefresh();
        }
        
        return success;
    }
    
    /**
     * Hide votes for selected story
     */
    public boolean hideVotes() {
        T1Card selectedStory = getSelectedStory();
        
        if (selectedStory == null) {
            Logger.warn("No story selected for hiding votes");
            return false;
        }
        
        boolean success = votingService.hideVotes(selectedStory.getId());
        
        if (success) {
            triggerUIRefresh();
        }
        
        return success;
    }
    
    /**
     * Toggle reveal state for selected story
     */
    public boolean toggleReveal() {
        T1Card selectedStory = getSelectedStory();
        
        if (selectedStory == null) {
            return false;
        }
        
        T1VotingSession session = votingService.getVotingSession(selectedStory.getId());
        
        if (session != null && session.isRevealed()) {
            return hideVotes();
        } else {
            return revealVotes();
        }
    }
    
    /**
     * Check if player has voted for selected story
     */
    public boolean hasPlayerVoted(String playerName) {
        T1Card selectedStory = getSelectedStory();
        
        if (selectedStory == null) {
            return false;
        }
        
        return votingService.hasPlayerVoted(playerName, selectedStory.getId());
    }
    
    // === Chart Operations ===
    
    public javax.swing.JPanel createVoteDistributionChart() {
        T1Card selectedStory = getSelectedStory();
        if (selectedStory == null) {
            Logger.warn("No story selected for chart creation");
            return null;
        }
        
        try {
            return chartService.createVoteDistributionChart(selectedStory);
        } catch (Exception e) {
            Logger.error("Failed to create vote distribution chart", e);
            return chartService.createEmptyChart("Chart creation failed");
        }
    }


    
    /**
     * Create participation chart for selected story
     */
    public javax.swing.JPanel createParticipationChart() {
        T1Card selectedStory = getSelectedStory();
        int totalPlayers = votingService.getTotalPlayers();
        return chartService.createParticipationChart(selectedStory, totalPlayers);
    }
    
    // === Data Access ===
    
    /**
     * Get voting session for selected story
     */
    public T1VotingSession getVotingSession() {
        T1Card selectedStory = getSelectedStory();
        
        if (selectedStory == null) {
            return null;
        }
        
        return votingService.getVotingSession(selectedStory.getId());
    }
    
    /**
     * Get total players in current room
     */
    public int getTotalPlayers() {
        return votingService.getTotalPlayers();
    }
    
    // === UI Callback Registration ===
    
    /**
     * Set callback for story selection changes
     */
    public void setStorySelectionCallback(Consumer<T1Card> callback) {
        this.storySelectionCallback = callback;
    }
    
    /**
     * Set callback for voting updates
     */
    public void setVotingUpdateCallback(Consumer<T1VotingSession> callback) {
        this.votingUpdateCallback = callback;
    }
    
    /**
     * Set callback for general UI refresh
     */
    public void setUIRefreshCallback(Runnable callback) {
        this.uiRefreshCallback = callback;
    }
    
    // === Private Event Handlers ===
    
    /**
     * Handle story selection change
     */
    private void onStorySelectionChanged(T1Card story) {
        if (storySelectionCallback != null) {
            storySelectionCallback.accept(story);
        }
    }
    
    private void triggerVotingUpdate(String storyId) {
        if (votingUpdateCallback != null) {
            T1VotingSession session = votingService.getVotingSession(storyId);
            votingUpdateCallback.accept(session);
        }
        
        // Also trigger chart update
        T1Card story = getSelectedStory();
        if (story != null && story.getId().equals(storyId)) {
            // Chart will be updated via the voting update callback
            Logger.debug("Vote update triggered chart refresh for story: " + story.getTitle());
        }
    }
    
    /**
     * Trigger UI refresh callback
     */
    private void triggerUIRefresh() {
        if (uiRefreshCallback != null) {
            uiRefreshCallback.run();
        }
    }
    
    // === Utility Methods ===
    
    /**
     * Get current state summary for debugging
     */
    public String getStateSummary() {
        T1Card selected = getSelectedStory();
        T1VotingSession session = getVotingSession();
        
        StringBuilder summary = new StringBuilder();
        summary.append("=== Voting Controller State ===\n");
        summary.append("Selected Story: ").append(selected != null ? selected.getTitle() : "None").append("\n");
        summary.append("Total Players: ").append(getTotalPlayers()).append("\n");
        
        if (session != null) {
            summary.append("Votes: ").append(session.getVoteCount()).append("\n");
            summary.append("Revealed: ").append(session.isRevealed()).append("\n");
            summary.append("Average: ").append(String.format("%.1f", session.getAverageScore())).append("\n");
        }
        
        return summary.toString();
    }
}