package hbaskar.one;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PlanItPokerPublisher - MQTT publisher for real-time game events
 * 
 * This singleton class broadcasts game events to all connected clients via MQTT.
 * Publishes events for room creation, player actions, story management, and game state changes.
 * Uses JSON serialization for structured message format and reliable delivery.
 * 
 * @author Daniel Miranda
 * @version 1.0
 * @since 2025
 */

public class T1PlanItPokerPublisher {
    private static final Logger logger = LoggerFactory.getLogger(T1PlanItPokerPublisher.class);

    private static T1PlanItPokerPublisher instance;
    private MqttClient mqttClient;
    private final Gson gson;
    private final String broker = "tcp://test.mosquitto.org:1883"; // Change to your MQTT broker
    private final String clientId;
    
    // MQTT Topics
    public static final String TOPIC_ROOM_CREATED = "planit/room/created";
    public static final String TOPIC_PLAYER_JOINED = "planit/player/joined";
    public static final String TOPIC_STORY_CREATED = "planit/story/created";
    public static final String TOPIC_STORY_SCORED = "planit/story/scored";
    public static final String TOPIC_CARDS_REVEALED = "planit/cards/revealed";
    public static final String TOPIC_ROOMS_UPDATED = "planit/rooms/updated";
    public static final String TOPIC_MODE_CHANGED = "planit/mode/changed";
    
    private T1PlanItPokerPublisher() {
        this.gson = new Gson();
        this.clientId = "Publisher_" + UUID.randomUUID().toString();
        connectToBroker();
    }
    
    public static synchronized T1PlanItPokerPublisher getInstance() {
        if (instance == null) {
            instance = new T1PlanItPokerPublisher();
        }
        return instance;
    }
    
    private void connectToBroker() {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(broker, clientId, persistence);
            
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setAutomaticReconnect(true);
            
            logger.trace("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            logger.trace("Publisher connected");
            
        } catch (MqttException me) {
            logger.error("Failed to connect to MQTT broker: " + me.getMessage());
            me.printStackTrace();
        }
    }
    
    private void publishMessage(String topic, Object messageObject) {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                String messageJson = gson.toJson(messageObject);
                MqttMessage message = new MqttMessage(messageJson.getBytes());
                message.setQos(1); // At least once delivery
                mqttClient.publish(topic, message);
                System.out.println("Published to " + topic + ": " + messageJson);
            } else {
                System.err.println("MQTT client not connected. Cannot publish message.");
            }
        } catch (MqttException me) {
            System.err.println("Failed to publish message: " + me.getMessage());
            me.printStackTrace();
        }
    }
    
    // Publish room created event
    public void publishRoomCreated(String roomCode, String roomName, String creatorName) {
        RoomEvent event = new RoomEvent(roomCode, roomName, creatorName);
        publishMessage(TOPIC_ROOM_CREATED, event);
    }
    
    // Publish player joined event
    public void publishPlayerJoined(String roomCode, String playerName) {
        PlayerEvent event = new PlayerEvent(roomCode, playerName);
        publishMessage(TOPIC_PLAYER_JOINED, event);
    }
    
    // Publish story created event
    public void publishStoryCreated(String roomCode, String storyId, String title, String description) {
        StoryEvent event = new StoryEvent(roomCode, storyId, title, description);
        publishMessage(TOPIC_STORY_CREATED, event);
    }
    
    // Publish story scored event
    public void publishStoryScored(String roomCode, String storyId, String playerName, int score) {
        ScoreEvent event = new ScoreEvent(roomCode, storyId, playerName, score);
        publishMessage(TOPIC_STORY_SCORED, event);
    }
    
    // Publish cards revealed event
    public void publishCardsRevealed(String roomCode, String storyId, double averageScore) {
        RevealEvent event = new RevealEvent(roomCode, storyId, averageScore);
        publishMessage(TOPIC_CARDS_REVEALED, event);
    }
    
    // Publish rooms updated event
    public void publishRoomsUpdated() {
        T1PlanItPokerRepository repo = T1PlanItPokerRepository.getInstance();
        RoomsUpdatedEvent event = new RoomsUpdatedEvent(repo.getAvailableRoomCodes());
        publishMessage(TOPIC_ROOMS_UPDATED, event);
    }
    
    // Publish mode changed event
    public void publishModeChanged(String roomCode, String newMode) {
        ModeEvent event = new ModeEvent(roomCode, newMode);
        publishMessage(TOPIC_MODE_CHANGED, event);
    }
    
    // Disconnect from broker
    public void disconnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                System.out.println("Publisher disconnected");
            }
        } catch (MqttException me) {
            System.err.println("Failed to disconnect: " + me.getMessage());
        }
    }
    
    // Event Data Classes
    public static class RoomEvent {
        public final String roomCode;
        public final String roomName;
        public final String creatorName;
        public final long timestamp;
        
        public RoomEvent(String roomCode, String roomName, String creatorName) {
            this.roomCode = roomCode;
            this.roomName = roomName;
            this.creatorName = creatorName;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public static class PlayerEvent {
        public final String roomCode;
        public final String playerName;
        public final long timestamp;
        
        public PlayerEvent(String roomCode, String playerName) {
            this.roomCode = roomCode;
            this.playerName = playerName;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public static class StoryEvent {
        public final String roomCode;
        public final String storyId;
        public final String title;
        public final String description;
        public final long timestamp;
        
        public StoryEvent(String roomCode, String storyId, String title, String description) {
            this.roomCode = roomCode;
            this.storyId = storyId;
            this.title = title;
            this.description = description;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public static class ScoreEvent {
        public final String roomCode;
        public final String storyId;
        public final String playerName;
        public final int score;
        public final long timestamp;
        
        public ScoreEvent(String roomCode, String storyId, String playerName, int score) {
            this.roomCode = roomCode;
            this.storyId = storyId;
            this.playerName = playerName;
            this.score = score;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public static class RevealEvent {
        public final String roomCode;
        public final String storyId;
        public final double averageScore;
        public final long timestamp;
        
        public RevealEvent(String roomCode, String storyId, double averageScore) {
            this.roomCode = roomCode;
            this.storyId = storyId;
            this.averageScore = averageScore;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public static class RoomsUpdatedEvent {
        public final java.util.List<String> availableRooms;
        public final long timestamp;
        
        public RoomsUpdatedEvent(java.util.List<String> availableRooms) {
            this.availableRooms = new java.util.ArrayList<>(availableRooms);
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public static class ModeEvent {
        public final String roomCode;
        public final String mode;
        public final long timestamp;
        
        public ModeEvent(String roomCode, String mode) {
            this.roomCode = roomCode;
            this.mode = mode;
            this.timestamp = System.currentTimeMillis();
        }
    }
}