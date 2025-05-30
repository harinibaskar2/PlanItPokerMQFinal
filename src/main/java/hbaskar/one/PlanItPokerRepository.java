package hbaskar.one;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PlanItPokerRepository {
    private static PlanItPokerRepository instance;
    private final Map<String, Room> rooms;
    private final AtomicInteger roomCounter;
    private final AtomicInteger storyCounter;
    
    private PlanItPokerRepository() {
        this.rooms = new ConcurrentHashMap<>();
        this.roomCounter = new AtomicInteger(1);
        this.storyCounter = new AtomicInteger(1);
    }
    
    public static synchronized PlanItPokerRepository getInstance() {
        if (instance == null) {
            instance = new PlanItPokerRepository();
        }
        return instance;
    }
    
    // Room Management
    public String createRoom(String roomName, String creatorName) {
        String roomCode = "room " + roomCounter.getAndIncrement();
        Room room = new Room(roomCode, roomName, creatorName);
        rooms.put(roomCode, room);
        return roomCode;
    }
    
    public Room getRoom(String roomCode) {
        return rooms.get(roomCode);
    }
    
    public List<String> getAvailableRoomCodes() {
        return new ArrayList<>(rooms.keySet());
    }
    
    public boolean joinRoom(String roomCode, String playerName) {
        Room room = rooms.get(roomCode);
        if (room != null) {
            room.addPlayer(playerName);
            return true;
        }
        return false;
    }
    
    // Story Management
    public String createStory(String roomCode, String title, String description) {
        Room room = rooms.get(roomCode);
        if (room != null) {
            String storyId = "story_" + storyCounter.getAndIncrement();
            Story story = new Story(storyId, title, description);
            room.addStory(story);
            return storyId;
        }
        return null;
    }
    
    public void updateStoryScore(String roomCode, String storyId, String playerName, int score) {
        Room room = rooms.get(roomCode);
        if (room != null) {
            Story story = room.getStory(storyId);
            if (story != null) {
                story.addScore(playerName, score);
            }
        }
    }
    
    public void revealCards(String roomCode, String storyId) {
        Room room = rooms.get(roomCode);
        if (room != null) {
            Story story = room.getStory(storyId);
            if (story != null) {
                story.setRevealed(true);
                story.calculateAverageScore();
            }
        }
    }
    
    // Inner Classes
    public static class Room {
        private String code;
        private String name;
        private String creator;
        private List<String> players;
        private Map<String, Story> stories;
        
        public Room(String code, String name, String creator) {
            this.code = code;
            this.name = name;
            this.creator = creator;
            this.players = new ArrayList<>();
            this.stories = new ConcurrentHashMap<>();
            this.players.add(creator);
        }
        
        public void addPlayer(String playerName) {
            if (!players.contains(playerName)) {
                players.add(playerName);
            }
        }
        
        public void addStory(Story story) {
            stories.put(story.getId(), story);
        }
        
        public Story getStory(String storyId) {
            return stories.get(storyId);
        }
        
        public List<Story> getAllStories() {
            return new ArrayList<>(stories.values());
        }
        
        // Getters
        public String getCode() { return code; }
        public String getName() { return name; }
        public String getCreator() { return creator; }
        public List<String> getPlayers() { return new ArrayList<>(players); }
    }
    
    public static class Story {
        private String id;
        private String title;
        private String description;
        private Map<String, Integer> scores;
        private double averageScore;
        private boolean isRevealed;
        
        public Story(String id, String title, String description) {
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
}