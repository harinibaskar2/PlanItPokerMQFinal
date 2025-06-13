package hbaskar.one;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hbaskar.T1Card;

/**
 * T1PlanItPokerRepository - MQTT subscriber for receiving and processing game events
 * 
 * This class receives real-time game events from MQTT broker and updates the UI accordingly.
 * Filters events by current room, handles JSON deserialization, and provides callback mechanisms.
 * Integrates repository operations with event publishing for seamless real-time updates.
 * 
 * @author Daniel Miranda
 * @version 1.0
 * @since 2025
 */

public class T1PlanItPokerSubscriber implements MqttCallback {
    private static final Logger logger = LoggerFactory.getLogger(T1PlanItPokerSubscriber.class);
    private MqttClient mqttClient;
    private final Gson gson;
    private final String broker = "tcp://test.mosquitto.org:1883"; // Change to your MQTT broker
    private final String clientId;
    
    // Event handlers
    private Consumer<T1PlanItPokerPublisher.RoomEvent> roomCreatedHandler;
    private Consumer<T1PlanItPokerPublisher.PlayerEvent> playerJoinedHandler;
    private Consumer<T1PlanItPokerPublisher.StoryEvent> storyCreatedHandler;
    private Consumer<T1PlanItPokerPublisher.ScoreEvent> storyStoredHandler;
    private Consumer<T1PlanItPokerPublisher.RevealEvent> cardsRevealedHandler;
    private Consumer<T1PlanItPokerPublisher.RoomsUpdatedEvent> roomsUpdatedHandler;
    private Consumer<T1PlanItPokerPublisher.ModeEvent> modeChangedHandler;
    
    public T1PlanItPokerSubscriber() {
        this.gson = new Gson();
        this.clientId = "Subscriber_" + UUID.randomUUID().toString();
        connectToBroker();
    }
    
    private void connectToBroker() {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(broker, clientId, persistence);
            mqttClient.setCallback(this);
            
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setAutomaticReconnect(true);
            
            logger.trace("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            logger.trace("Subscriber connected");
            
        } catch (MqttException me) {
            logger.error("Failed to connect to MQTT broker: " + me.getMessage());
            me.printStackTrace();
        }
    }
    
    // Get repository and current room info
    private T1PlanItPokerRepository getRepository() {
        return T1PlanItPokerRepository.getInstance();
    }
    
    private String getCurrentRoomCode() {
        return getRepository().getCurrentRoomCode();
    }
    
    private String getCurrentPlayerName() {
        return getRepository().getLoggedInUser();
    }
    
    // Subscribe to room creation events
    public void subscribeToRoomCreated(Consumer<T1PlanItPokerPublisher.RoomEvent> callback) {
        this.roomCreatedHandler = callback;
        subscribeToTopic(T1PlanItPokerPublisher.TOPIC_ROOM_CREATED);
    }
    
    // Subscribe to player joined events
    public void subscribeToPlayerJoined(Consumer<T1PlanItPokerPublisher.PlayerEvent> callback) {
        this.playerJoinedHandler = callback;
        subscribeToTopic(T1PlanItPokerPublisher.TOPIC_PLAYER_JOINED);
    }
    
    // Subscribe to story creation events
    public void subscribeToStoryCreated(Consumer<T1PlanItPokerPublisher.StoryEvent> callback) {
        this.storyCreatedHandler = callback;
        subscribeToTopic(T1PlanItPokerPublisher.TOPIC_STORY_CREATED);
    }
    
    // Subscribe to scoring events
    public void subscribeToStoryScored(Consumer<T1PlanItPokerPublisher.ScoreEvent> callback) {
        this.storyStoredHandler = callback;
        subscribeToTopic(T1PlanItPokerPublisher.TOPIC_STORY_SCORED);
    }
    
    // Subscribe to cards revealed events
    public void subscribeToCardsRevealed(Consumer<T1PlanItPokerPublisher.RevealEvent> callback) {
        this.cardsRevealedHandler = callback;
        subscribeToTopic(T1PlanItPokerPublisher.TOPIC_CARDS_REVEALED);
    }
    
    // Subscribe to rooms updated events
    public void subscribeToRoomsUpdated(Consumer<T1PlanItPokerPublisher.RoomsUpdatedEvent> callback) {
        this.roomsUpdatedHandler = callback;
        subscribeToTopic(T1PlanItPokerPublisher.TOPIC_ROOMS_UPDATED);
    }
    
    // Subscribe to mode changed events
    public void subscribeToModeChanged(Consumer<T1PlanItPokerPublisher.ModeEvent> callback) {
        this.modeChangedHandler = callback;
        subscribeToTopic(T1PlanItPokerPublisher.TOPIC_MODE_CHANGED);
    }
    
    // Subscribe to all events in current room
    public void subscribeToRoomEvents(RoomEventHandler handler) {
        subscribeToPlayerJoined(handler::onPlayerJoined);
        subscribeToStoryCreated(handler::onStoryCreated);
        subscribeToStoryScored(handler::onStoryScored);
        subscribeToCardsRevealed(handler::onCardsRevealed);
        subscribeToModeChanged(handler::onModeChanged);
    }
    
    private void subscribeToTopic(String topic) {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.subscribe(topic, 1); // QoS 1
                logger.trace("Subscribed to topic: " + topic);
            }
        } catch (MqttException me) {
            logger.error("Failed to subscribe to topic " + topic + ": " + me.getMessage());
            me.printStackTrace();
        }
    }
    
    // Helper methods for common operations
    public String createRoom(String roomName, String creatorName) {
        T1PlanItPokerRepository repo = getRepository();
        String roomCode = repo.createRoom(roomName, creatorName);
        
        T1PlanItPokerPublisher publisher = T1PlanItPokerPublisher.getInstance();
        publisher.publishRoomCreated(roomCode, roomName, creatorName);
        publisher.publishRoomsUpdated();
        
        return roomCode;
    }
    
    public boolean joinRoom(String roomCode, String playerName) {
        T1PlanItPokerRepository repo = getRepository();
        boolean success = repo.joinRoom(roomCode, playerName);
        if (success) {
            repo.setLoggedInUser(playerName);
            
            T1PlanItPokerPublisher publisher = T1PlanItPokerPublisher.getInstance();
            publisher.publishPlayerJoined(roomCode, playerName);
        }
        return success;
    }
    

    
    public void scoreStory(String storyId, int score) {
        String currentRoom = getCurrentRoomCode();
        String currentPlayer = getCurrentPlayerName();
        if (currentRoom != null && currentPlayer != null) {
            T1PlanItPokerRepository repo = getRepository();
            repo.updateStoryScore(currentRoom, storyId, currentPlayer, score);
            
            T1PlanItPokerPublisher publisher = T1PlanItPokerPublisher.getInstance();
            publisher.publishStoryScored(currentRoom, storyId, currentPlayer, score);
        }
    }
    
    public void revealCards(String storyId) {
        String currentRoom = getCurrentRoomCode();
        if (currentRoom != null) {
            T1PlanItPokerRepository repo = getRepository();
            repo.revealCards(currentRoom, storyId);
            
            T1PlanItPokerRepository.Room room = repo.getRoom(currentRoom);
            T1Card story = room.getStory(storyId);
            
            T1PlanItPokerPublisher publisher = T1PlanItPokerPublisher.getInstance();
            publisher.publishCardsRevealed(currentRoom, storyId, story.getAverageScore());
        }
    }
    
    public void changeMode(String newMode) {
        String currentRoom = getCurrentRoomCode();
        if (currentRoom != null) {
            T1PlanItPokerRepository repo = getRepository();
            repo.setCurrentMode(newMode);
            
            T1PlanItPokerPublisher publisher = T1PlanItPokerPublisher.getInstance();
            publisher.publishModeChanged(currentRoom, newMode);
        }
    }



    
    // Get current data
    public List<String> getAvailableRooms() {
        return getRepository().getAvailableRoomCodes();
    }
    
    public T1PlanItPokerRepository.Room getCurrentRoom() {
        String currentRoomCode = getCurrentRoomCode();
        if (currentRoomCode != null) {
            return getRepository().getRoom(currentRoomCode);
        }
        return null;
    }
    
    public List<T1Card> getCurrentRoomStories() {
        T1PlanItPokerRepository.Room room = getCurrentRoom();
        if (room != null) {
            return room.getAllStories();
        }
        return new ArrayList<>();
    }
    
    // MQTT Callback methods
    @Override
    public void connectionLost(Throwable cause) {
        logger.warn("Connection lost: " + cause.getMessage());
        // Attempt to reconnect
        connectToBroker();
    }
    
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String messageStr = new String(message.getPayload());
        logger.trace("Received message on topic " + topic + ": " + messageStr);
        
        try {
            switch (topic) {
                case T1PlanItPokerPublisher.TOPIC_ROOM_CREATED:
                    if (roomCreatedHandler != null) {
                        T1PlanItPokerPublisher.RoomEvent roomEvent = gson.fromJson(messageStr, T1PlanItPokerPublisher.RoomEvent.class);
                        roomCreatedHandler.accept(roomEvent);
                    }
                    break;
                    
                case T1PlanItPokerPublisher.TOPIC_PLAYER_JOINED:
                    if (playerJoinedHandler != null) {
                        T1PlanItPokerPublisher.PlayerEvent playerEvent = gson.fromJson(messageStr, T1PlanItPokerPublisher.PlayerEvent.class);
                        // Filter by current room
                        if (getCurrentRoomCode() == null || getCurrentRoomCode().equals(playerEvent.roomCode)) {
                            playerJoinedHandler.accept(playerEvent);
                        }
                    }
                    break;
                    
                case T1PlanItPokerPublisher.TOPIC_STORY_CREATED:
                    if (storyCreatedHandler != null) {
                        T1PlanItPokerPublisher.StoryEvent storyEvent = gson.fromJson(messageStr, T1PlanItPokerPublisher.StoryEvent.class);
                        // Filter by current room
                        if (getCurrentRoomCode() == null || getCurrentRoomCode().equals(storyEvent.roomCode)) {
                            storyCreatedHandler.accept(storyEvent);
                        }
                    }
                    break;
                    
                case T1PlanItPokerPublisher.TOPIC_STORY_SCORED:
                    if (storyStoredHandler != null) {
                        T1PlanItPokerPublisher.ScoreEvent scoreEvent = gson.fromJson(messageStr, T1PlanItPokerPublisher.ScoreEvent.class);
                        // Filter by current room
                        if (getCurrentRoomCode() == null || getCurrentRoomCode().equals(scoreEvent.roomCode)) {
                            storyStoredHandler.accept(scoreEvent);
                        }
                    }
                    break;
                    
                case T1PlanItPokerPublisher.TOPIC_CARDS_REVEALED:
                    if (cardsRevealedHandler != null) {
                        T1PlanItPokerPublisher.RevealEvent revealEvent = gson.fromJson(messageStr, T1PlanItPokerPublisher.RevealEvent.class);
                        // Filter by current room
                        if (getCurrentRoomCode() == null || getCurrentRoomCode().equals(revealEvent.roomCode)) {
                            cardsRevealedHandler.accept(revealEvent);
                        }
                    }
                    break;
                    
                case T1PlanItPokerPublisher.TOPIC_ROOMS_UPDATED:
                    if (roomsUpdatedHandler != null) {
                        T1PlanItPokerPublisher.RoomsUpdatedEvent roomsEvent = gson.fromJson(messageStr, T1PlanItPokerPublisher.RoomsUpdatedEvent.class);
                        roomsUpdatedHandler.accept(roomsEvent);
                    }
                    break;
                    
                case T1PlanItPokerPublisher.TOPIC_MODE_CHANGED:
                    if (modeChangedHandler != null) {
                        T1PlanItPokerPublisher.ModeEvent modeEvent = gson.fromJson(messageStr, T1PlanItPokerPublisher.ModeEvent.class);
                        // Filter by current room
                        if (getCurrentRoomCode() == null || getCurrentRoomCode().equals(modeEvent.roomCode)) {
                            modeChangedHandler.accept(modeEvent);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            logger.error("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Not used for subscribers
    }
    
    // Disconnect from broker
    public void disconnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                logger.trace("Subscriber disconnected");
            }
        } catch (MqttException me) {
            logger.error("Failed to disconnect: " + me.getMessage());
        }
    }
    
    // Interface for handling room events
    public interface RoomEventHandler {
        default void onPlayerJoined(T1PlanItPokerPublisher.PlayerEvent event) {}
        default void onStoryCreated(T1PlanItPokerPublisher.StoryEvent event) {}
        default void onStoryScored(T1PlanItPokerPublisher.ScoreEvent event) {}
        default void onCardsRevealed(T1PlanItPokerPublisher.RevealEvent event) {}
        default void onModeChanged(T1PlanItPokerPublisher.ModeEvent event) {}
    }
}