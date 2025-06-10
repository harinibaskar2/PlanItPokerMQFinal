package hbaskar.one;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import hbaskar.T1Card;

/**
 * PlanItPokerRepository - Centralized data repository for Planning Poker application
 * 
 * This singleton class manages all room and story data using thread-safe collections.
 * Handles room creation, player management, story creation, and scoring operations.
 * Maintains current room and user state for session management.
 * 
 * @author Daniel Miranda
 * @version 1.0
 * @since 2025
 */

public class PlanItPokerRepository {
    private static PlanItPokerRepository instance;
    private final Map<String, Room> rooms;
    private final AtomicInteger roomCounter;
    private final AtomicInteger storyCounter;

    // Add fields for current room and mode
    private String currentRoomCode;
    private String currentMode;
    private String loggedInUser;


        private String taigaUsername;
    private String taigaPassword;



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


    public String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
    }
    // Room Management
    public String createRoom(String roomName, String creatorName) {
        Room room = new Room(roomName, roomName, creatorName); // use roomName as both code and name
        rooms.put(roomName, room); // store by roomName, not "room 1"
        setCurrentRoomCode(roomName);
        return roomName;
    }
    public void setTaigaCredentials(String username, String password) {
        this.taigaUsername = username;
        this.taigaPassword = password;
    }
    
    public String getTaigaUsername() {
        return taigaUsername;
    }
    
    public String getTaigaPassword() {
        return taigaPassword;
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
            T1Card story = new T1Card(storyId, title, description);
            room.addStory(story);
            return storyId;
        }
        return null;
    }

    public void updateStoryScore(String roomCode, String storyId, String playerName, int score) {
        Room room = rooms.get(roomCode);
        if (room != null) {
            T1Card story = room.getStory(storyId);
            if (story != null) {
                story.addScore(playerName, score);
            }
        }
    }

    public void revealCards(String roomCode, String storyId) {
        Room room = rooms.get(roomCode);
        if (room != null) {
            T1Card story = room.getStory(storyId);
            if (story != null) {
                story.setRevealed(true);
                story.calculateAverageScore();
            }
        }
    }

    // Current room code getter/setter
    public String getCurrentRoomCode() {
        return currentRoomCode;
    }

    public void setCurrentRoomCode(String currentRoomCode) {
        this.currentRoomCode = currentRoomCode;
    }

    // Current mode getter/setter
    public String getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(String currentMode) {
        this.currentMode = currentMode;
    }
   

    // Inner Classes
    public static class Room {
        private String code;
        private String name;
        private String creator;
        private List<String> players;
        private Map<String, T1Card> stories;
        private String scheduledTime;

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

        public void setScheduledTime(String time) {
        this.scheduledTime = time;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }


        public void addStory(T1Card story) {
            stories.put(story.getId(), story);
        }

        public T1Card getStory(String storyId) {
            return stories.get(storyId);
        }

        public List<T1Card> getAllStories() {
            return new ArrayList<>(stories.values());
        }

        // Getters
        public String getCode() { return code; }
        public String getName() { return name; }
        public String getCreator() { return creator; }
        public List<String> getPlayers() { return new ArrayList<>(players); }
    }

}

