package hbaskar.one;

import hbaskar.two.CreateRoomNanny;
import hbaskar.two.JoinRoomNanny;
import hbaskar.two.RoomPanel;

public class LoginNanny {

    private Main main;
    private PlanItPokerRepository repository = PlanItPokerRepository.getInstance();

    public LoginNanny(Main main) {
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
        JoinRoomNanny joinRoomNanny = new JoinRoomNanny(main);
        CreateRoomNanny createRoomNanny = new CreateRoomNanny(main);

        // Get logged in user name (if needed)
        String username = PlanItPokerRepository.getInstance().getLoggedInUser();

        // Create the combined panel with both create & join UI
        RoomPanel roomPanel = new RoomPanel(username, createRoomNanny, joinRoomNanny);

        // Set combined panel as content pane
        main.setContentPane(roomPanel);
        main.setSize(500, 600);  // increased height for both sections
        main.revalidate();
        main.repaint();
    }
} 
