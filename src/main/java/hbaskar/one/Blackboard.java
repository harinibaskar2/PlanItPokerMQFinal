package hbaskar.one;

import java.util.LinkedList;

/**
 * Shared data structure for the application.
 *
 * @author javiergs
 */
public class Blackboard {
	
	private static LinkedList<String> names = new LinkedList<>();
	private static LinkedList<String> stories = new LinkedList<>();
	private static String currentRoom;
	private static String mode;

	
	public static void addName(String name) {
		names.add(name);
	}
	
	public static void addStory(String story) {
		stories.add(story);
	}
	
	public static void addCurrentRoom(String name) {
		currentRoom = name;
	}
	
	public static void addCurrentMode(String selectedItem) {
		mode = selectedItem;
	}



public static String getCurrentRoom() {
	return currentRoom;
}

public static String getCurrentMode() {
	return mode;
}

public static LinkedList<String> getNames() {
	return names;
}

public static LinkedList<String> getStories() {
	return stories;
}


private static String currentRoomTime;

public static void setRoomTime(String time) {
    currentRoomTime = time;
}

public static String getRoomTime() {
    return currentRoomTime;
}

}


