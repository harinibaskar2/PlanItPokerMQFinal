package hbaskar.one;
import hbaskar.two.CreateRoomNanny;
import hbaskar.two.CreateRoomPanel;


/**
 * LoginNanny is responsible for handling the login process.
 *
 * @author javiergs
 */
public class LoginNanny {
	
	private Main main;
	
	public LoginNanny(Main main) {
		this.main = main;
	}
	
	public void enterRoom(String name) {
		System.out.println(name + " Entering a room...");
		login (name);
		switchGUI();
	}
	
	public void login(String name) {
		System.out.println(name + " Logging in...");
		Blackboard.addName(name);
		switchGUI();
	}

	private void switchGUI() {
		main.setTitle("Room");
		CreateRoomNanny createRoomNanny = new CreateRoomNanny(main);
		CreateRoomPanel createRoomPanel = new CreateRoomPanel(createRoomNanny);
		main.setContentPane(createRoomPanel);
		main.setSize(500, 500);
		main.revalidate();
		main.repaint();
	}
	
}