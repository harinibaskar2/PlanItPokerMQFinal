
package hbaskar.interfaces;

import hbaskar.T1Card;
import java.util.List;

/**
 * Interface for story data access operations
 * Defines contract for story persistence
 * 
 * @author PlanItPoker Team
 * @version 1.0
 */
public interface T1IStoryRepository {
    
    /**
     * Get all stories for current room
     */
    List<T1Card> getCurrentRoomStories();
    
    /**
     * Get story by ID
     */
    T1Card getStoryById(String storyId);
    
    /**
     * Add story to current room
     */
    void addStoryToCurrentRoom(T1Card story);
    
    /**
     * Update existing story
     */
    boolean updateStory(T1Card story);
    
    /**
     * Remove story from current room
     */
    boolean removeStory(String storyId);
    
    /**
     * Get current room code
     */
    String getCurrentRoomCode();
    
    /**
     * Get total players in current room
     */
    int getTotalPlayersInCurrentRoom();
}
