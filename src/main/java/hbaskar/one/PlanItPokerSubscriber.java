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

import hbaskar.T1Card;

/**
 * PlanItPokerSubscriber - MQTT subscriber for receiving and processing game events
 * 
 * This class receives real-time game events from MQTT broker and updates the UI accordingly.
 * Filters events by current room, handles JSON deserialization, and provides callback mechanisms.
 * Integrates repository operations with event publishing for seamless real-time updates.
 * 
 * @author Daniel Miranda
 * @version 1.0
 * @since 2025
 */

public class PlanItPokerSubscriber implements MqttCallback {
    private MqttClient mqttClient;
    private final Gson gson;
    private final String broker = "tcp://test.mosquitto.org:1883"; // Change to your MQTT broker
    private final String clientId;
    
    // Event handlers
    private Consumer<PlanItPokerPublisher.RoomEvent> roomCreatedHandler;
    private Consumer<PlanItPokerPublisher.PlayerEvent> playerJoinedHandler;
    private Consumer<PlanItPokerPublisher.StoryEvent> storyCreatedHandler;
    private Consumer<PlanItPokerPublisher.ScoreEvent> storyStoredHandler;
    private Consumer<PlanItPokerPublisher.RevealEvent> cardsRevealedHandler;
    private Consumer<PlanItPokerPublisher.RoomsUpdatedEvent> roomsUpdatedHandler;
    private Consumer<PlanItPokerPublisher.ModeEvent> modeChangedHandler;
    
    public PlanItPokerSubscriber() {
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
            
            System.out.println("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            System.out.println("Subscriber connected");
            
        } catch (MqttException me) {
            System.err.println("Failed to connect to MQTT broker: " + me.getMessage());
            me.printStackTrace();
        }
    }
    
    // Get repository and current room info
    private PlanItPokerRepository getRepository() {
        return PlanItPokerRepository.getInstance();
    }
    
    private String getCurrentRoomCode() {
        return getRepository().getCurrentRoomCode();
    }
    
    private String getCurrentPlayerName() {
        return getRepository().getLoggedInUser();
    }
    
    // Subscribe to room creation events
    public void subscribeToRoomCreated(Consumer<PlanItPokerPublisher.RoomEvent> callback) {
        this.roomCreatedHandler = callback;
        subscribeToTopic(PlanItPokerPublisher.TOPIC_ROOM_CREATED);
    }
    
    // Subscribe to player joined events
    public void subscribeToPlayerJoined(Consumer<PlanItPokerPublisher.PlayerEvent> callback) {
        this.playerJoinedHandler = callback;
        subscribeToTopic(PlanItPokerPublisher.TOPIC_PLAYER_JOINED);
    }
    
    // Subscribe to story creation events
    public void subscribeToStoryCreated(Consumer<PlanItPokerPublisher.StoryEvent> callback) {
        this.storyCreatedHandler = callback;
        subscribeToTopic(PlanItPokerPublisher.TOPIC_STORY_CREATED);
    }
    
    // Subscribe to scoring events
    public void subscribeToStoryScored(Consumer<PlanItPokerPublisher.ScoreEvent> callback) {
        this.storyStoredHandler = callback;
        subscribeToTopic(PlanItPokerPublisher.TOPIC_STORY_SCORED);
    }
    
    // Subscribe to cards revealed events
    public void subscribeToCardsRevealed(Consumer<PlanItPokerPublisher.RevealEvent> callback) {
        this.cardsRevealedHandler = callback;
        subscribeToTopic(PlanItPokerPublisher.TOPIC_CARDS_REVEALED);
    }
    
    // Subscribe to rooms updated events
    public void subscribeToRoomsUpdated(Consumer<PlanItPokerPublisher.RoomsUpdatedEvent> callback) {
        this.roomsUpdatedHandler = callback;
        subscribeToTopic(PlanItPokerPublisher.TOPIC_ROOMS_UPDATED);
    }
    
    // Subscribe to mode changed events
    public void subscribeToModeChanged(Consumer<PlanItPokerPublisher.ModeEvent> callback) {
        this.modeChangedHandler = callback;
        subscribeToTopic(PlanItPokerPublisher.TOPIC_MODE_CHANGED);
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
                System.out.println("Subscribed to topic: " + topic);
            }
        } catch (MqttException me) {
            System.err.println("Failed to subscribe to topic " + topic + ": " + me.getMessage());
            me.printStackTrace();
        }
    }
    
    // Helper methods for common operations
    public String createRoom(String roomName, String creatorName) {
        PlanItPokerRepository repo = getRepository();
        String roomCode = repo.createRoom(roomName, creatorName);
        
        PlanItPokerPublisher publisher = PlanItPokerPublisher.getInstance();
        publisher.publishRoomCreated(roomCode, roomName, creatorName);
        publisher.publishRoomsUpdated();
        
        return roomCode;
    }
    
    public boolean joinRoom(String roomCode, String playerName) {
        PlanItPokerRepository repo = getRepository();
        boolean success = repo.joinRoom(roomCode, playerName);
        if (success) {
            repo.setLoggedInUser(playerName);
            
            PlanItPokerPublisher publisher = PlanItPokerPublisher.getInstance();
            publisher.publishPlayerJoined(roomCode, playerName);
        }
        return success;
    }
    

    
    public void scoreStory(String storyId, int score) {
        String currentRoom = getCurrentRoomCode();
        String currentPlayer = getCurrentPlayerName();
        if (currentRoom != null && currentPlayer != null) {
            PlanItPokerRepository repo = getRepository();
            repo.updateStoryScore(currentRoom, storyId, currentPlayer, score);
            
            PlanItPokerPublisher publisher = PlanItPokerPublisher.getInstance();
            publisher.publishStoryScored(currentRoom, storyId, currentPlayer, score);
        }
    }
    
    public void revealCards(String storyId) {
        String currentRoom = getCurrentRoomCode();
        if (currentRoom != null) {
            PlanItPokerRepository repo = getRepository();
            repo.revealCards(currentRoom, storyId);
            
            PlanItPokerRepository.Room room = repo.getRoom(currentRoom);
            T1Card story = room.getStory(storyId);
            
            PlanItPokerPublisher publisher = PlanItPokerPublisher.getInstance();
            publisher.publishCardsRevealed(currentRoom, storyId, story.getAverageScore());
        }
    }
    
    public void changeMode(String newMode) {
        String currentRoom = getCurrentRoomCode();
        if (currentRoom != null) {
            PlanItPokerRepository repo = getRepository();
            repo.setCurrentMode(newMode);
            
            PlanItPokerPublisher publisher = PlanItPokerPublisher.getInstance();
            publisher.publishModeChanged(currentRoom, newMode);
        }
    }



    
    // Get current data
    public List<String> getAvailableRooms() {
        return getRepository().getAvailableRoomCodes();
    }
    
    public PlanItPokerRepository.Room getCurrentRoom() {
        String currentRoomCode = getCurrentRoomCode();
        if (currentRoomCode != null) {
            return getRepository().getRoom(currentRoomCode);
        }
        return null;
    }
    
    public List<T1Card> getCurrentRoomStories() {
        PlanItPokerRepository.Room room = getCurrentRoom();
        if (room != null) {
            return room.getAllStories();
        }
        return new ArrayList<>();
    }
    
    // MQTT Callback methods
    @Override
    public void connectionLost(Throwable cause) {
        System.err.println("Connection lost: " + cause.getMessage());
        // Attempt to reconnect
        connectToBroker();
    }
    
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String messageStr = new String(message.getPayload());
        System.out.println("Received message on topic " + topic + ": " + messageStr);
        
        try {
            switch (topic) {
                case PlanItPokerPublisher.TOPIC_ROOM_CREATED:
                    if (roomCreatedHandler != null) {
                        PlanItPokerPublisher.RoomEvent roomEvent = gson.fromJson(messageStr, PlanItPokerPublisher.RoomEvent.class);
                        roomCreatedHandler.accept(roomEvent);
                    }
                    break;
                    
                case PlanItPokerPublisher.TOPIC_PLAYER_JOINED:
                    if (playerJoinedHandler != null) {
                        PlanItPokerPublisher.PlayerEvent playerEvent = gson.fromJson(messageStr, PlanItPokerPublisher.PlayerEvent.class);
                        // Filter by current room
                        if (getCurrentRoomCode() == null || getCurrentRoomCode().equals(playerEvent.roomCode)) {
                            playerJoinedHandler.accept(playerEvent);
                        }
                    }
                    break;
                    
                case PlanItPokerPublisher.TOPIC_STORY_CREATED:
                    if (storyCreatedHandler != null) {
                        PlanItPokerPublisher.StoryEvent storyEvent = gson.fromJson(messageStr, PlanItPokerPublisher.StoryEvent.class);
                        // Filter by current room
                        if (getCurrentRoomCode() == null || getCurrentRoomCode().equals(storyEvent.roomCode)) {
                            storyCreatedHandler.accept(storyEvent);
                        }
                    }
                    break;
                    
                case PlanItPokerPublisher.TOPIC_STORY_SCORED:
                    if (storyStoredHandler != null) {
                        PlanItPokerPublisher.ScoreEvent scoreEvent = gson.fromJson(messageStr, PlanItPokerPublisher.ScoreEvent.class);
                        // Filter by current room
                        if (getCurrentRoomCode() == null || getCurrentRoomCode().equals(scoreEvent.roomCode)) {
                            storyStoredHandler.accept(scoreEvent);
                        }
                    }
                    break;
                    
                case PlanItPokerPublisher.TOPIC_CARDS_REVEALED:
                    if (cardsRevealedHandler != null) {
                        PlanItPokerPublisher.RevealEvent revealEvent = gson.fromJson(messageStr, PlanItPokerPublisher.RevealEvent.class);
                        // Filter by current room
                        if (getCurrentRoomCode() == null || getCurrentRoomCode().equals(revealEvent.roomCode)) {
                            cardsRevealedHandler.accept(revealEvent);
                        }
                    }
                    break;
                    
                case PlanItPokerPublisher.TOPIC_ROOMS_UPDATED:
                    if (roomsUpdatedHandler != null) {
                        PlanItPokerPublisher.RoomsUpdatedEvent roomsEvent = gson.fromJson(messageStr, PlanItPokerPublisher.RoomsUpdatedEvent.class);
                        roomsUpdatedHandler.accept(roomsEvent);
                    }
                    break;
                    
                case PlanItPokerPublisher.TOPIC_MODE_CHANGED:
                    if (modeChangedHandler != null) {
                        PlanItPokerPublisher.ModeEvent modeEvent = gson.fromJson(messageStr, PlanItPokerPublisher.ModeEvent.class);
                        // Filter by current room
                        if (getCurrentRoomCode() == null || getCurrentRoomCode().equals(modeEvent.roomCode)) {
                            modeChangedHandler.accept(modeEvent);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
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
                System.out.println("Subscriber disconnected");
            }
        } catch (MqttException me) {
            System.err.println("Failed to disconnect: " + me.getMessage());
        }
    }
    
    // Interface for handling room events
    public interface RoomEventHandler {
        default void onPlayerJoined(PlanItPokerPublisher.PlayerEvent event) {}
        default void onStoryCreated(PlanItPokerPublisher.StoryEvent event) {}
        default void onStoryScored(PlanItPokerPublisher.ScoreEvent event) {}
        default void onCardsRevealed(PlanItPokerPublisher.RevealEvent event) {}
        default void onModeChanged(PlanItPokerPublisher.ModeEvent event) {}
    }
}