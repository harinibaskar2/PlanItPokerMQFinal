package hbaskar;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a story card in the planning poker system,
 * containing details such as the story ID, title, description,
 * assigned user, total points, votes by players, average score, 
 * and whether the card's score is revealed.
 * 
 * <p>This class provides methods to add scores from players,
 * calculate the average score from all votes, and manage
 * the state of the card.</p>
 * 
 * <p>Thread-safe concurrent map is used to store votes from players.</p>
 * 
 * @author DarienR5
 */

public class T1Card {
    private String id;
    private String title;
    private String description;
    private String assignedUser;
    private double totalPoints;
    private Map<String, Integer> scores;
    private double averageScore;
    private boolean isRevealed;

    // Constructor with assignedUser and totalPoints
    public T1Card(String id, String title, String description, String assignedUser, double totalPoints) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.assignedUser = assignedUser;
        this.totalPoints = totalPoints;
        this.scores = new ConcurrentHashMap<>();
        this.averageScore = 0.0;
        this.isRevealed = false;
    }

    // Existing methods

    public void addScore(String playerName, int score) {
        scores.put(playerName, score);
    }

    public void calculateAverageScore() {
        if (!scores.isEmpty()) {
            averageScore = scores.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
        }
    }

    

    // Getters and Setters

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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
        this.totalPoints = totalPoints;
    }

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
        isRevealed = revealed;
    }
}
