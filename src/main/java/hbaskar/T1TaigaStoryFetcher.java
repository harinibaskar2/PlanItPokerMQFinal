package hbaskar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import hbaskar.one.T1PlanItPokerRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Fetches user stories and related data from the Taiga API for the PlanItPoker application.
 * 
 * This class handles authenticating to Taiga, retrieving the project ID based on a project slug,
 * fetching backlog user stories, creating story cards, and storing them in the application repository.
 * It also extracts and prints unique point and role IDs found in the fetched stories. 
 * 
 * @author hbaskar
 * @version 1.0
 */

public class T1TaigaStoryFetcher {
    private static final Logger logger = LoggerFactory.getLogger(T1TaigaStoryFetcher.class);
    private static final String TAIGA_API = "https://api.taiga.io/api/v1";

    public static void main(String[] args) throws Exception {
        try {
            T1PlanItPokerRepository repo = T1PlanItPokerRepository.getInstance();

            String USERNAME = repo.getTaigaUsername();
            String PASSWORD = repo.getTaigaPassword();
            String projectSlug = repo.getTaigaProjectSlug();

            String authToken = loginAndGetToken(USERNAME, PASSWORD);
            repo.setTaigaAuthToken(authToken);

            int projectId = getProjectId(authToken, projectSlug);
            repo.setTaigaProjectId(projectId);

            logger.trace("Project ID for slug '" + projectSlug + "': " + projectId);

            JSONArray stories = fetchUserStories(authToken, projectId);
            extractUniquePointIds(stories);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String loginAndGetToken(String username, String password) throws Exception {
        URL url = new URL(TAIGA_API + "/auth");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInput = String.format(
            "{\"type\": \"normal\", \"username\": \"%s\", \"password\": \"%s\"}",
            username, password);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes());
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        InputStreamReader streamReader = new InputStreamReader(
            responseCode == 200 ? conn.getInputStream() : conn.getErrorStream()
        );
        BufferedReader in = new BufferedReader(streamReader);

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        try {
            JSONObject json = new JSONObject(response.toString());

            if (responseCode != 200) {
                String errorMessage = json.optString("_error_message",
                                        json.optString("msg", "Unknown login error"));
                throw new RuntimeException("Login failed: " + errorMessage);
            }

            return json.getString("auth_token");
        } catch (Exception e) {
            throw new RuntimeException("Login failed, invalid response: " + response.toString());
        }
    }

    public static int getProjectId(String token, String projectSlug) throws Exception {
        URL url = new URL(TAIGA_API + "/projects/by_slug?slug=" + projectSlug);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + token);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject json = new JSONObject(response.toString());
        return json.getInt("id");
    }

    public static JSONArray fetchUserStories(String token, int projectId) throws Exception {
        URL url = new URL(TAIGA_API + "/userstories?project=" + projectId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + token);
    
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
    
        JSONArray allStories = new JSONArray(response.toString());
        JSONArray backlogStories = new JSONArray();
    
        logger.info("Backlog stories:");
        T1PlanItPokerRepository repo = T1PlanItPokerRepository.getInstance();
    
        for (int i = 0; i < allStories.length(); i++) {
            JSONObject story = allStories.getJSONObject(i);
            if (story.isNull("milestone")) {
                backlogStories.put(story);
    
                int id = story.getInt("id");
                String subject = story.optString("subject", "(no title)");
    
                String responsible = "Unassigned";
                if (!story.isNull("assigned_to_extra_info")) {
                    responsible = story.getJSONObject("assigned_to_extra_info")
                        .optString("full_name_display", "Unassigned");
                }
    
                double totalPoints = 0.0;
                if (!story.isNull("total_points")) {
                    totalPoints = story.getDouble("total_points");
                }
    
                // Create the T1Card with assignedUser and totalPoints
                T1Card card = new T1Card(
                    "story_" + id,
                    subject,
                    "", // Description can be added if available in the JSON
                    responsible,
                    totalPoints
                );
    
                // Add the story card to the current room in the repository
                repo.addStoryToCurrentRoom(card);
    
                logger.info("• #%d - %s\n   Responsible: %s\n   Total Points: %.1f\n",
                    id, subject, responsible, totalPoints);
                
            }
        }
    
        return backlogStories;
    }
    

    /**
     * Extracts and prints all unique roleIds and pointIds from the stories.
     */
    private static void extractUniquePointIds(JSONArray stories) {
        Set<Integer> uniquePointIds = new HashSet<>();
        Set<String> uniqueRoleIds = new HashSet<>();

        for (int i = 0; i < stories.length(); i++) {
            JSONObject story = stories.getJSONObject(i);
            JSONObject pointsObj = story.optJSONObject("points");
            if (pointsObj == null) continue;

            for (String roleIdStr : pointsObj.keySet()) {
                uniqueRoleIds.add(roleIdStr);

                Object point = pointsObj.get(roleIdStr);
                if (point instanceof Integer) {
                    uniquePointIds.add((Integer) point);
                    System.out.printf("Role ID: %s → Point ID: %d%n", roleIdStr, (Integer) point);
                }
            }
        }

        logger.trace("\n📌 Unique pointIds:");
        for (int id : uniquePointIds) {
            logger.trace("pointId: " + id);
        }

        logger.trace("\n👤 Unique roleIds:");
        for (String id : uniqueRoleIds) {
            logger.trace("roleId: " + id);
        }
    }
}
