
package hbaskar.interfaces;

import hbaskar.T1Card;
import java.util.function.Consumer;

/**
 * Interface for story selection operations
 * Defines contract for story selection business logic
 * 
 * @author PlanItPoker Team
 * @version 1.0
 */
public interface T1IStorySelectionService {
    
    /**
     * Select a story for voting
     */
    void selectStory(T1Card story);
    
    /**
     * Get currently selected story
     */
    T1Card getSelectedStory();
    
    /**
     * Clear current selection
     */
    void clearSelection();
    
    /**
     * Check if a story is selected
     */
    boolean isStorySelected(T1Card story);
    
    /**
     * Check if any story is selected
     */
    boolean hasSelection();
    
    /**
     * Add listener for selection events
     */
    void addSelectionListener(Consumer<T1Card> listener);
    
    /**
     * Remove selection listener
     */
    void removeSelectionListener(Consumer<T1Card> listener);
}
