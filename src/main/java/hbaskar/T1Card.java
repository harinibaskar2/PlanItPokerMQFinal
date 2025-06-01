package hbaskar;

/*
 * Card Class:
 * The container for the story as well as all associated attributes
 * Ie:
 *  Id, Title, Description (The actual Story), All Votes, Avg Score, revealed status
 * 
 * @author DarienR5
 * */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Need to contain:
public class T1Card {
    private String id;
    private String title;
    private String description;
    private Map<String, Integer> scores;
    private double averageScore;
    private boolean isRevealed;

    public T1Card(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.scores = new ConcurrentHashMap<>();
        this.averageScore = 0.0;
        this.isRevealed = false;
    }

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
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Map<String, Integer> getScores() { return new HashMap<>(scores); }
    public double getAverageScore() { return averageScore; }
    public boolean isRevealed() { return isRevealed; }
    public void setRevealed(boolean revealed) { isRevealed = revealed; }
        
}
