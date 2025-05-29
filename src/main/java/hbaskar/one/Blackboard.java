package hbaskar.one;

import java.util.LinkedList;

/**
 * Shared data structure for the application.
 * Make this into singleton Data repo
 * See players
 * And change name
 *
 * @author javiergs
 */
public class Blackboard {
	
	private static LinkedList<String> names = new LinkedList<>();
	private static LinkedList<String> stories = new LinkedList<>();
	private static LinkedList<String> rooms = new LinkedList<>();  // New list to track all rooms
	
	private static String currentRoom;
	private static String mode;
	private static String currentRoomTime;
	
	public static String getLatestPlayer() {
		return names.isEmpty() ? "Unknown" : names.getLast();
	}

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
	
	public static void setRoomTime(String time) {
	    currentRoomTime = time;
	}

	public static String getRoomTime() {
	    return currentRoomTime;
	}
	
	// New methods for managing all rooms
	public static void addRoom(String roomName) {
		if (!rooms.contains(roomName)) {
			rooms.add(roomName);
		}
	}

	public static LinkedList<String> getRooms() {
		return rooms;
	}
}
