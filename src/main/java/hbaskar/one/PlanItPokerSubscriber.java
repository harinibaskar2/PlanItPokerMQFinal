package hbaskar.one;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import com.google.gson.Gson;
import java.util.*;
import java.util.function.Consumer;

public class PlanItPokerSubscriber implements MqttCallback {
    private MqttClient mqttClient;
    private final Gson gson;
    private final String broker = "tcp://localhost:1883"; // Change to your MQTT broker
    private final String clientId;
    private String currentRoomCode;
    private String currentPlayerName;
    
    // Event handlers
    private Consumer<PlanItPokerPublisher.RoomEvent> roomCreatedHandler;
    private Consumer<PlanItPokerPublisher.PlayerEvent> playerJoinedHandler;
    private Consumer<PlanItPokerPublisher.StoryEvent> storyCreatedHandler;
    private Consumer<PlanItPokerPublisher.ScoreEvent> storyStoredHandler;
    private Consumer<PlanItPokerPublisher.RevealEvent> cardsRevealedHandler;
    private Consumer<PlanItPokerPublisher.RoomsUpdatedEvent> roomsUpdatedHandler;
    
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
    
    // Set current context
    public void setCurrentRoom(String roomCode) {
        this.currentRoomCode = roomCode;
    }
    
    public void setCurrentPlayer(String playerName) {
        this.currentPlayerName = playerName;
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
    
    // Subscribe to all events in current room
    public void subscribeToRoomEvents(RoomEventHandler handler) {
        subscribeToPlayerJoined(handler::onPlayerJoined);
        subscribeToStoryCreated(handler::onStoryCreated);
        subscribeToStoryScored(handler::onStoryScored);
        subscribeToCardsRevealed(handler::onCardsRevealed);
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
    public void createRoom(String roomName, String creatorName) {
        PlanItPokerRepository repo = PlanItPokerRepository.getInstance();
        String roomCode = repo.createRoom(roomName, creatorName);
        
        PlanItPokerPublisher publisher = PlanItPokerPublisher.getInstance();
        publisher.publishRoomCreated(roomCode, roomName, creatorName);
        publisher.publishRoomsUpdated();
    }
    
    public boolean joinRoom(String roomCode, String playerName) {
        PlanItPokerRepository repo = PlanItPokerRepository.getInstance();
        boolean success = repo.joinRoom(roomCode, playerName);
        if (success) {
            setCurrentRoom(roomCode);
            setCurrentPlayer(playerName);
            
            PlanItPokerPublisher publisher = PlanItPokerPublisher.getInstance();
            publisher.publishPlayerJoined(roomCode, playerName);
        }
        return success;
    }
    
    public void createStory(String title, String description) {
        if (currentRoomCode != null) {
            PlanItPokerRepository repo = PlanItPokerRepository.getInstance();
            String storyId = repo.createStory(currentRoomCode, title, description);
            if (storyId != null) {
                PlanItPokerPublisher publisher = PlanItPokerPublisher.getInstance();
                publisher.publishStoryCreated(currentRoomCode, storyId, title, description);
            }
        }
    }
    
    public void scoreStory(String storyId, int score) {
        if (currentRoomCode != null && currentPlayerName != null) {
            PlanItPokerRepository repo = PlanItPokerRepository.getInstance();
            repo.updateStoryScore(currentRoomCode, storyId, currentPlayerName, score);
            
            PlanItPokerPublisher publisher = PlanItPokerPublisher.getInstance();
            publisher.publishStoryScored(currentRoomCode, storyId, currentPlayerName, score);
        }
    }
    
    public void revealCards(String storyId) {
        if (currentRoomCode != null) {
            PlanItPokerRepository repo = PlanItPokerRepository.getInstance();
            repo.revealCards(currentRoomCode, storyId);
            
            PlanItPokerRepository.Room room = repo.getRoom(currentRoomCode);
            PlanItPokerRepository.Story story = room.getStory(storyId);
            
            PlanItPokerPublisher publisher = PlanItPokerPublisher.getInstance();
            publisher.publishCardsRevealed(currentRoomCode, storyId, story.getAverageScore());
        }
    }
    
    // Get current data
    public List<String> getAvailableRooms() {
        return PlanItPokerRepository.getInstance().getAvailableRoomCodes();
    }
    
    public PlanItPokerRepository.Room getCurrentRoom() {
        if (currentRoomCode != null) {
            return PlanItPokerRepository.getInstance().getRoom(currentRoomCode);
        }
        return null;
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
                        if (currentRoomCode == null || currentRoomCode.equals(playerEvent.roomCode)) {
                            playerJoinedHandler.accept(playerEvent);
                        }
                    }
                    break;
                    
                case PlanItPokerPublisher.TOPIC_STORY_CREATED:
                    if (storyCreatedHandler != null) {
                        PlanItPokerPublisher.StoryEvent storyEvent = gson.fromJson(messageStr, PlanItPokerPublisher.StoryEvent.class);
                        // Filter by current room
                        if (currentRoomCode == null || currentRoomCode.equals(storyEvent.roomCode)) {
                            storyCreatedHandler.accept(storyEvent);
                        }
                    }
                    break;
                    
                case PlanItPokerPublisher.TOPIC_STORY_SCORED:
                    if (storyStoredHandler != null) {
                        PlanItPokerPublisher.ScoreEvent scoreEvent = gson.fromJson(messageStr, PlanItPokerPublisher.ScoreEvent.class);
                        // Filter by current room
                        if (currentRoomCode == null || currentRoomCode.equals(scoreEvent.roomCode)) {
                            storyStoredHandler.accept(scoreEvent);
                        }
                    }
                    break;
                    
                case PlanItPokerPublisher.TOPIC_CARDS_REVEALED:
                    if (cardsRevealedHandler != null) {
                        PlanItPokerPublisher.RevealEvent revealEvent = gson.fromJson(messageStr, PlanItPokerPublisher.RevealEvent.class);
                        // Filter by current room
                        if (currentRoomCode == null || currentRoomCode.equals(revealEvent.roomCode)) {
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
    }
}