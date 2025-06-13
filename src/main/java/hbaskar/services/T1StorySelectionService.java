package hbaskar.services;

import hbaskar.T1Card;
import hbaskar.interfaces.T1IStorySelectionService;
import hbaskar.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service handling story selection operations
 * Implements business logic for story selection functionality
 * 
 * @author PlanItPoker Team
 * @version 1.0
 */
public class T1StorySelectionService implements T1IStorySelectionService {
    
    private T1Card selectedStory;
    private final List<Consumer<T1Card>> selectionListeners;
    
    public T1StorySelectionService() {
        this.selectionListeners = new ArrayList<>();
    }
    
    @Override
    public void selectStory(T1Card story) {
        if (story == null) {
            Logger.warn("Attempted to select null story");
            return;
        }
        
        // Only change selection if it's different
        if (selectedStory != null && selectedStory.getId().equals(story.getId())) {
            Logger.debug("Story already selected: " + story.getTitle());
            return;
        }
        
        T1Card previousStory = selectedStory;
        selectedStory = story;
        
        Logger.logStorySelection(getCurrentPlayerName(), story.getTitle());
        
        // Notify listeners
        notifySelectionListeners(story);
        
        // Log the change
        if (previousStory != null) {
            Logger.debug("Selection changed from '" + previousStory.getTitle() + 
                        "' to '" + story.getTitle() + "'");
        }
    }
    
    @Override
    public T1Card getSelectedStory() {
        return selectedStory;
    }
    
    @Override
    public void clearSelection() {
        if (selectedStory != null) {
            Logger.debug("Clearing selection for: " + selectedStory.getTitle());
            selectedStory = null;
            notifySelectionListeners(null);
        }
    }
    
    @Override
    public boolean isStorySelected(T1Card story) {
        if (story == null || selectedStory == null) {
            return false;
        }
        return selectedStory.getId().equals(story.getId());
    }
    
    @Override
    public boolean hasSelection() {
        return selectedStory != null;
    }
    
    @Override
    public void addSelectionListener(Consumer<T1Card> listener) {
        if (listener != null && !selectionListeners.contains(listener)) {
            selectionListeners.add(listener);
            Logger.debug("Selection listener added. Total listeners: " + selectionListeners.size());
        }
    }
    
    @Override
    public void removeSelectionListener(Consumer<T1Card> listener) {
        if (selectionListeners.remove(listener)) {
            Logger.debug("Selection listener removed. Total listeners: " + selectionListeners.size());
        }
    }
    
    /**
     * Notify all listeners of selection change
     */
    private void notifySelectionListeners(T1Card story) {
        List<Consumer<T1Card>> listenersToNotify = new ArrayList<>(selectionListeners);
        
        for (Consumer<T1Card> listener : listenersToNotify) {
            try {
                listener.accept(story);
            } catch (Exception e) {
                Logger.error("Error notifying selection listener", e);
            }
        }
    }
    
    /**
     * Get current player name from repository
     */
    private String getCurrentPlayerName() {
        hbaskar.one.PlanItPokerRepository repo = hbaskar.one.PlanItPokerRepository.getInstance();
        String playerName = repo.getLoggedInUser();
        return playerName != null ? playerName : "Unknown Player";
    }
    
    /**
     * Get selection summary for debugging
     */
    public String getSelectionSummary() {
        if (selectedStory == null) {
            return "No story selected";
        }
        return String.format("Selected: %s (ID: %s, Votes: %d)", 
                            selectedStory.getTitle(), 
                            selectedStory.getId(), 
                            selectedStory.getVoteCount());
    }
}
