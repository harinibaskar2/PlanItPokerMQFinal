package hbaskar;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import hbaskar.one.PlanItPokerRepository;

public class TaigaStoryFetcher {

    private static final String TAIGA_API = "https://api.taiga.io/api/v1";
public static void main(String[] args) throws Exception {
    try {
        PlanItPokerRepository repo = PlanItPokerRepository.getInstance();

        String USERNAME = repo.getTaigaUsername();
        String PASSWORD = repo.getTaigaPassword();
        String projectSlug = repo.getTaigaProjectSlug();

        String authToken = loginAndGetToken(USERNAME, PASSWORD);
        repo.setTaigaAuthToken(authToken);

        int projectId = getProjectId(authToken, projectSlug);
        repo.setTaigaProjectId(projectId);

        System.out.println("Project ID for slug '" + projectSlug + "': " + projectId);

        JSONArray stories = fetchUserStories(authToken, projectId);
        extractUniquePointIds(stories);
        updateBacklogTotalPoints(authToken, stories, 5.0);

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

        Map<String, String> roleIdToName = Map.of(
            "5100817", "UX",
            "5100818", "Design",
            "5100816", "Front",
            "5100815", "Back"
        );

        Map<Integer, Integer> pointIdToValue = Map.of(
            10136072, 8,  // UX
            10136073, 1,  // Design
            10136071, 2,  // Front
            10136075, 3   // Back
        );

        System.out.println("Backlog stories:");
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

                String totalPoints = story.isNull("total_points")
                    ? "—"
                    : String.valueOf(story.getDouble("total_points"));

                System.out.printf("• #%d - %s\n   Responsible: %s\n   Total Points: %s\n",
                    id, subject, responsible, totalPoints);

                if (!story.isNull("points")) {
                    JSONObject pointsObj = story.getJSONObject("points");
                    int sum = 0;
                    for (String roleId : pointsObj.keySet()) {
                        int pointId = pointsObj.getInt(roleId);
                        int value = pointIdToValue.getOrDefault(pointId, -1);
                        String role = roleIdToName.getOrDefault(roleId, "Unknown");

                        System.out.printf("     - %s (roleId: %s) → pointId: %d → value: %s\n",
                            role, roleId, pointId, (value >= 0 ? value : "?"));
                        if (value >= 0) sum += value;
                    }
                    System.out.println("     = Computed Sum: " + sum + "\n");
                } else {
                    System.out.println("     No per-role points assigned.\n");
                }
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

        System.out.println("\n📌 Unique pointIds:");
        for (int id : uniquePointIds) {
            System.out.println("pointId: " + id);
        }

        System.out.println("\n👤 Unique roleIds:");
        for (String id : uniqueRoleIds) {
            System.out.println("roleId: " + id);
        }
    }


    public static void updateBacklogTotalPoints(String token, JSONArray stories, double newPoints) {
        System.out.println("Update backlog total points not implemented yet.");
        // Implement  update logic here if needed
    }
}
