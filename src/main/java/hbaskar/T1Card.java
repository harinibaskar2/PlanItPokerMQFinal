package hbaskar;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced Card Class representing a user story with voting capabilities
 * Thread-safe implementation following clean code principles
 * 
 * @author DarienR5
 * @version 3.0 - Clean Architecture
 */
public class T1Card {
    private final String id;
    private String title;
    private String description;
    private String assignedUser;
    private double totalPoints;
    private final Map<String, Integer> scores;
    private double averageScore;
    private boolean isRevealed;
    private long lastVoteTime;

    // Constructor with all parameters
    public T1Card(String id, String title, String description, String assignedUser, double totalPoints) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Story ID cannot be null or empty");
        }
        
        this.id = id;
        this.title = title != null ? title : "Untitled Story";
        this.description = description != null ? description : "";
        this.assignedUser = assignedUser;
        this.totalPoints = totalPoints;
        this.scores = new ConcurrentHashMap<>();
        this.averageScore = 0.0;
        this.isRevealed = false;
        this.lastVoteTime = System.currentTimeMillis();
    }

    // Alternative constructor for basic stories
    public T1Card(String id, String title, String description) {
        this(id, title, description, null, 0.0);
    }

    // === Voting Operations ===
    
    /**
     * Add or update a player's vote
     */
    public synchronized void addScore(String playerName, int score) {
        if (playerName == null || playerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        
        scores.put(playerName, score);
        this.lastVoteTime = System.currentTimeMillis();
        calculateAverageScore();
    }

    /**
     * Remove a player's vote
     */
    public synchronized void removeScore(String playerName) {
        if (scores.remove(playerName) != null) {
            calculateAverageScore();
        }
    }

    /**
     * Check if player has voted
     */
    public boolean hasVotedPlayer(String playerName) {
        return scores.containsKey(playerName);
    }

    /**
     * Get specific player's score
     */
    public Integer getPlayerScore(String playerName) {
        return scores.get(playerName);
    }

    /**
     * Get total number of votes
     */
    public int getVoteCount() {
        return scores.size();
    }

    /**
     * Calculate average score (excluding invalid votes)
     */
    public synchronized void calculateAverageScore() {
        if (scores.isEmpty()) {
            averageScore = 0.0;
            return;
        }
        
        // Filter out special votes (? = -1, coffee = -2)
        averageScore = scores.values().stream()
            .filter(score -> score >= 0)
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
    }

    /**
     * Clear all votes and reset state
     */
    public synchronized void clearAllVotes() {
        scores.clear();
        averageScore = 0.0;
        isRevealed = false;
    }

    // === Statistics Methods ===
    
    /**
     * Get minimum score (excluding special votes)
     */
    public int getMinScore() {
        return scores.values().stream()
            .filter(score -> score >= 0)
            .mapToInt(Integer::intValue)
            .min()
            .orElse(0);
    }

    /**
     * Get maximum score (excluding special votes)
     */
    public int getMaxScore() {
        return scores.values().stream()
            .filter(score -> score >= 0)
            .mapToInt(Integer::intValue)
            .max()
            .orElse(0);
    }

    /**
     * Check if all players have consensus (same vote)
     */
    public boolean hasConsensus() {
        if (scores.isEmpty()) {
            return false;
        }
        
        // Get all non-special votes
        java.util.Set<Integer> validVotes = scores.values().stream()
            .filter(score -> score >= 0)
            .collect(java.util.stream.Collectors.toSet());
        
        return validVotes.size() == 1;
    }

    /**
     * Get vote distribution map
     */
    public Map<Integer, Integer> getVoteDistribution() {
        Map<Integer, Integer> distribution = new HashMap<>();
        
        for (Integer score : scores.values()) {
            distribution.put(score, distribution.getOrDefault(score, 0) + 1);
        }
        
        return distribution;
    }

    // === Reveal State Management ===
    
    /**
     * Toggle reveal state
     */
    public synchronized void toggleReveal() {
        isRevealed = !isRevealed;
    }

    // === Getters and Setters ===

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title : "Untitled Story";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public double getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(double totalPoints) {
        this.totalPoints = Math.max(0, totalPoints);
    }

    /**
     * Get defensive copy of scores
     */
    public Map<String, Integer> getScores() {
        return new HashMap<>(scores);
    }

    public double getAverageScore() {
        return averageScore;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public void setRevealed(boolean revealed) {
        this.isRevealed = revealed;
    }

    public long getLastVoteTime() {
        return lastVoteTime;
    }

    // === Utility Methods ===

    /**
     * Get voting progress percentage
     */
    public double getVotingProgress(int totalPlayers) {
        if (totalPlayers <= 0) {
            return 0.0;
        }
        return (double) scores.size() / totalPlayers * 100.0;
    }

    /**
     * Check if voting is complete
     */
    public boolean isVotingComplete(int totalPlayers) {
        return totalPlayers > 0 && scores.size() >= totalPlayers;
    }

    // === Object Overrides ===

    @Override
    public String toString() {
        return String.format("T1Card{id='%s', title='%s', votes=%d, average=%.1f, revealed=%s}", 
                           id, title, scores.size(), averageScore, isRevealed);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        T1Card t1Card = (T1Card) obj;
        return id.equals(t1Card.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    // === Validation Methods ===

    /**
     * Validate story data
     */
    public boolean isValid() {
        return id != null && !id.trim().isEmpty() && 
               title != null && !title.trim().isEmpty();
    }

    /**
     * Get validation errors
     */
    public java.util.List<String> getValidationErrors() {
        java.util.List<String> errors = new java.util.ArrayList<>();
        
        if (id == null || id.trim().isEmpty()) {
            errors.add("Story ID is required");
        }
        
        if (title == null || title.trim().isEmpty()) {
            errors.add("Story title is required");
        }
        
        if (totalPoints < 0) {
            errors.add("Total points cannot be negative");
        }
        
        return errors;
    }
}