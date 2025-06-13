package hbaskar.services;

import hbaskar.T1Card;
import hbaskar.interfaces.T1IVotingService;
import hbaskar.models.T1VotingSession;
import hbaskar.one.PlanItPokerRepository;
import hbaskar.utils.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Service handling all voting operations
 * Implements business logic for voting functionality
 * 
 * @author PlanItPoker Team
 * @version 1.0
 */
public class T1VotingService implements T1IVotingService {
    
    private final Map<String, T1VotingSession> votingSessions;
    private final PlanItPokerRepository repository;
    
    public T1VotingService() {
        this.votingSessions = new ConcurrentHashMap<>();
        this.repository = PlanItPokerRepository.getInstance();
    }
    
    @Override
    public boolean submitVote(String playerName, String storyId, String voteValue) {
        try {
            // Validate inputs
            if (playerName == null || storyId == null || voteValue == null) {
                Logger.warn("Invalid vote parameters: player=" + playerName + 
                           ", story=" + storyId + ", vote=" + voteValue);
                return false;
            }
            
            // Get or create voting session
            T1VotingSession session = getOrCreateSession(storyId);
            
            // Convert vote value
            Integer voteInt = convertVoteValue(voteValue);
            
            // Submit vote to session
            session.addVote(playerName, voteInt);
            
            // Update the T1Card as well for backward compatibility
            updateStoryCard(storyId, playerName, voteInt);
            
            Logger.logVote(playerName, getStoryTitle(storyId), voteValue);
            return true;
            
        } catch (Exception e) {
            Logger.error("Failed to submit vote", e);
            return false;
        }
    }
    
    @Override
    public T1VotingSession getVotingSession(String storyId) {
        return votingSessions.get(storyId);
    }
    
    @Override
    public boolean revealVotes(String storyId) {
        try {
            T1VotingSession session = getOrCreateSession(storyId);
            session.setRevealed(true);
            
            // Update story card reveal state
            T1Card story = findStoryCard(storyId);
            if (story != null) {
                story.setRevealed(true);
            }
            
            Logger.logReveal(getStoryTitle(storyId), true);
            return true;
            
        } catch (Exception e) {
            Logger.error("Failed to reveal votes for story: " + storyId, e);
            return false;
        }
    }
    
    @Override
    public boolean hideVotes(String storyId) {
        try {
            T1VotingSession session = getOrCreateSession(storyId);
            session.setRevealed(false);
            
            // Update story card reveal state
            T1Card story = findStoryCard(storyId);
            if (story != null) {
                story.setRevealed(false);
            }
            
            Logger.logReveal(getStoryTitle(storyId), false);
            return true;
            
        } catch (Exception e) {
            Logger.error("Failed to hide votes for story: " + storyId, e);
            return false;
        }
    }
    
    @Override
    public boolean hasPlayerVoted(String playerName, String storyId) {
        T1VotingSession session = votingSessions.get(storyId);
        return session != null && session.hasPlayerVoted(playerName);
    }
    
    @Override
    public void clearVotes(String storyId) {
        T1VotingSession session = votingSessions.get(storyId);
        if (session != null) {
            session.clearVotes();
        }
        
        // Clear votes from story card as well
        T1Card story = findStoryCard(storyId);
        if (story != null) {
            story.clearAllVotes();
        }
        
        Logger.info("Cleared votes for story: " + getStoryTitle(storyId));
    }
    
    @Override
    public int getTotalPlayers() {
        String currentRoomCode = repository.getCurrentRoomCode();
        if (currentRoomCode != null) {
            PlanItPokerRepository.Room room = repository.getRoom(currentRoomCode);
            if (room != null) {
                return room.getPlayers().size();
            }
        }
        return 1; // Default minimum
    }
    
    /**
     * Get or create voting session for story
     */
    private T1VotingSession getOrCreateSession(String storyId) {
        return votingSessions.computeIfAbsent(storyId, T1VotingSession::new);
    }
    
    private Integer convertVoteValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return -1; // Unknown vote
        }
        
        switch (value.trim()) {
            case "?": return -1;
            case "½": return 0;  // Changed from 0 to represent half point
            case "☕": return -2;
            case "0": return 0;
            case "1": return 1;
            case "2": return 2;
            case "3": return 3;
            case "5": return 5;
            case "8": return 8;
            case "13": return 13;
            case "21": return 21;
            case "34": return 34;
            case "55": return 55;
            default:
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    Logger.warn("Invalid vote value: " + value);
                    return -1;
                }
        }
    }
    
    /**
     * Update the T1Card with vote (for backward compatibility)
     */
    private void updateStoryCard(String storyId, String playerName, Integer vote) {
        T1Card story = findStoryCard(storyId);
        if (story != null) {
            story.addScore(playerName, vote);
        }
    }
    
    /**
     * Find story card by ID
     */
    private T1Card findStoryCard(String storyId) {
        String currentRoomCode = repository.getCurrentRoomCode();
        if (currentRoomCode != null) {
            PlanItPokerRepository.Room room = repository.getRoom(currentRoomCode);
            if (room != null) {
                return room.getAllStories().stream()
                    .filter(story -> story.getId().equals(storyId))
                    .findFirst()
                    .orElse(null);
            }
        }
        return null;
    }
    
    /**
     * Get story title by ID
     */
    private String getStoryTitle(String storyId) {
        T1Card story = findStoryCard(storyId);
        return story != null ? story.getTitle() : "Unknown Story";
    }
}