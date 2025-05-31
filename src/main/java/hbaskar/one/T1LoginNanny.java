package hbaskar.one;

import hbaskar.two.T1CreateRoomNanny;
import hbaskar.two.T1JoinRoomNanny;
import hbaskar.two.T1RoomPanel;

public class T1LoginNanny {

    private Main main;
    private PlanItPokerRepository repository = PlanItPokerRepository.getInstance();

    public T1LoginNanny(Main main) {
        this.main = main;
    }

    public void enterRoom(String name) {
        System.out.println(name + " Entering a room...");
        login(name);
        switchGUI();
    }

    public void login(String name) {
        System.out.println(name + " Logging in...");

        // Store user in repository for global access
        repository.setLoggedInUser(name);

   

        // Optional: create default room if none exists
        if (repository.getCurrentRoomCode() == null) {
            String roomCode = repository.createRoom("Default Room", name);
            repository.setCurrentRoomCode(roomCode);
        }

        // Add user to current room's player list
        String currentRoom = repository.getCurrentRoomCode();
        if (currentRoom != null) {
            repository.joinRoom(currentRoom, name);
        }
    }

    private void switchGUI() {
        main.setTitle("Room");

        // Create both nannies
        T1JoinRoomNanny joinRoomNanny = new T1JoinRoomNanny(main);
        T1CreateRoomNanny createRoomNanny = new T1CreateRoomNanny(main);

        // Get logged in user name (if needed)
        String username = PlanItPokerRepository.getInstance().getLoggedInUser();

        // Create the combined panel with both create & join UI
        T1RoomPanel roomPanel = new T1RoomPanel(username, createRoomNanny, joinRoomNanny);

        // Set combined panel as content pane
        main.setContentPane(roomPanel);
        main.setSize(500, 600);  // increased height for both sections
        main.revalidate();
        main.repaint();
    }
} 
