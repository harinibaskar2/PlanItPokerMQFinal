package hbaskar.one;

import hbaskar.two.CreateRoomNanny;
import hbaskar.two.CreateRoomPanel;

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

        // Pass Main only, since CreateRoomNanny can get the logged in user from repo
        CreateRoomNanny createRoomNanny = new CreateRoomNanny(main);

        CreateRoomPanel createRoomPanel = new CreateRoomPanel(createRoomNanny);
        main.setContentPane(createRoomPanel);
        main.setSize(500, 500);
        main.revalidate();
        main.repaint();
    }
}
