package hbaskar.interfaces;

import hbaskar.T1Card;
import hbaskar.models.T1VotingSession;

/**
 * Interface for voting operations
 * Defines contract for voting business logic
 */
public interface T1IVotingService {
    
    /**
     * Submit a vote for the current story
     */
    boolean submitVote(String playerName, String storyId, String voteValue);
    
    /**
     * Get voting session for a story
     */
    T1VotingSession getVotingSession(String storyId);
    
    /**
     * Reveal votes for a story
     */
    boolean revealVotes(String storyId);
    
    /**
     * Hide votes for a story  
     */
    boolean hideVotes(String storyId);
    
    /**
     * Check if player has voted for story
     */
    boolean hasPlayerVoted(String playerName, String storyId);
    
    /**
     * Clear all votes for a story
     */
    void clearVotes(String storyId);
    
    /**
     * Get total players in current room
     */
    int getTotalPlayers();
}