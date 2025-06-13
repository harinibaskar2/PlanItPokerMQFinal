package hbaskar.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Model representing a voting session for a story
 * Encapsulates voting state and statistics
 * 
 * @author PlanItPoker Team
 * @version 1.0
 */
public class T1VotingSession {
    
    private final String storyId;
    private final Map<String, Integer> votes;
    private boolean isRevealed;
    private long lastVoteTime;
    
    public T1VotingSession(String storyId) {
        this.storyId = storyId;
        this.votes = new HashMap<>();
        this.isRevealed = false;
        this.lastVoteTime = System.currentTimeMillis();
    }
    
    /**
     * Add or update a player's vote
     */
    public void addVote(String playerName, Integer vote) {
        votes.put(playerName, vote);
        lastVoteTime = System.currentTimeMillis();
    }
    
    /**
     * Remove a player's vote
     */
    public void removeVote(String playerName) {
        votes.remove(playerName);
    }
    
    /**
     * Get all votes (defensive copy)
     */
    public Map<String, Integer> getVotes() {
        return new HashMap<>(votes);
    }
    
    /**
     * Get specific player's vote
     */
    public Integer getPlayerVote(String playerName) {
        return votes.get(playerName);
    }
    
    /**
     * Check if player has voted
     */
    public boolean hasPlayerVoted(String playerName) {
        return votes.containsKey(playerName);
    }
    
    /**
     * Get vote count
     */
    public int getVoteCount() {
        return votes.size();
    }
    
    /**
     * Calculate average score (excluding ? votes)
     */
    public double getAverageScore() {
        return votes.values().stream()
            .filter(vote -> vote >= 0)
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Get minimum score
     */
    public int getMinScore() {
        return votes.values().stream()
            .filter(vote -> vote >= 0)
            .mapToInt(Integer::intValue)
            .min()
            .orElse(0);
    }
    
    /**
     * Get maximum score  
     */
    public int getMaxScore() {
        return votes.values().stream()
            .filter(vote -> vote >= 0)
            .mapToInt(Integer::intValue)
            .max()
            .orElse(0);
    }
    
    /**
     * Check if there's consensus (all votes the same)
     */
    public boolean hasConsensus() {
        Set<Integer> uniqueVotes = new java.util.HashSet<>(votes.values());
        uniqueVotes.remove(-1); // Remove ? votes
        return uniqueVotes.size() <= 1 && !uniqueVotes.isEmpty();
    }
    
    /**
     * Clear all votes
     */
    public void clearVotes() {
        votes.clear();
        isRevealed = false;
    }
    
    // Getters and Setters
    public String getStoryId() {
        return storyId;
    }
    
    public boolean isRevealed() {
        return isRevealed;
    }
    
    public void setRevealed(boolean revealed) {
        isRevealed = revealed;
    }
    
    public long getLastVoteTime() {
        return lastVoteTime;
    }
}