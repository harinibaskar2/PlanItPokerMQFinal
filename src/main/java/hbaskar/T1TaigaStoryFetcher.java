package hbaskar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import hbaskar.one.PlanItPokerRepository;
import hbaskar.utils.Logger;

/**
 * Fixed Taiga Story Fetcher with improved error handling and debugging
 * 
 * @author PlanItPoker Team
 * @version 2.1 - Enhanced Error Handling
 */
public class T1TaigaStoryFetcher {
    
    private static final String TAIGA_API = "https://api.taiga.io/api/v1";
    private static final int TIMEOUT_MS = 30000; // 30 seconds
    
    // Instance variables for session state
    private String authToken;
    private String username;  
    private String password;
    private String projectSlug;
    private int projectId;
    private boolean isAuthenticated = false;
    
    public T1TaigaStoryFetcher() {
        Logger.info("T1TaigaStoryFetcher initialized");
    }
    
    /**
     * Authenticate with Taiga and store session info
     */
    public boolean authenticate(String username, String password, String projectSlug) {
        try {
            this.username = username;
            this.password = password;
            this.projectSlug = projectSlug;
            
            Logger.info("Starting Taiga authentication for user: " + username + " with project: " + projectSlug);
            
            // Get auth token
            this.authToken = loginAndGetToken(username, password);
            Logger.info("Successfully obtained auth token");
            
            // Get project ID
            this.projectId = getProjectId(authToken, projectSlug);
            Logger.info("Successfully obtained project ID: " + projectId);
            
            // Store in repository
            PlanItPokerRepository repo = PlanItPokerRepository.getInstance();
            repo.setTaigaAuthToken(authToken);
            repo.setTaigaProjectId(projectId);
            
            this.isAuthenticated = true;
            Logger.info("Taiga authentication completed successfully");
            return true;
            
        } catch (Exception e) {
            Logger.error("Taiga authentication failed: " + e.getMessage(), e);
            this.isAuthenticated = false;
            return false;
        }
    }
    
    /**
     * Fetch stories from Taiga (main method called by UI)
     */
    public List<T1Card> fetchStories() throws Exception {
        if (!isAuthenticated) {
            throw new IllegalStateException("Not authenticated with Taiga. Call authenticate() first.");
        }
        
        Logger.info("Fetching stories from Taiga project: " + projectSlug);
        
        try {
            JSONArray storiesJson = fetchUserStoriesFromAPI(authToken, projectId);
            List<T1Card> stories = convertJsonToCards(storiesJson);
            
            Logger.info("Successfully fetched " + stories.size() + " stories from Taiga");
            return stories;
            
        } catch (Exception e) {
            Logger.error("Failed to fetch stories from Taiga", e);
            throw e;
        }
    }
    
    /**
     * Convert JSON stories to T1Card objects
     */
    private List<T1Card> convertJsonToCards(JSONArray storiesJson) {
        List<T1Card> stories = new ArrayList<>();
        
        Logger.info("Converting " + storiesJson.length() + " stories from JSON");
        
        for (int i = 0; i < storiesJson.length(); i++) {
            try {
                JSONObject storyJson = storiesJson.getJSONObject(i);
                
                // Only process backlog stories (no milestone)
                if (!storyJson.isNull("milestone")) {
                    Logger.debug("Skipping story with milestone: " + storyJson.optString("subject"));
                    continue;
                }
                
                // Extract story data
                int id = storyJson.getInt("id");
                String subject = storyJson.optString("subject", "Untitled Story");
                String description = storyJson.optString("description", "");
                
                // Get assigned user
                String assignedUser = "Unassigned";
                if (!storyJson.isNull("assigned_to_extra_info")) {
                    assignedUser = storyJson.getJSONObject("assigned_to_extra_info")
                        .optString("full_name_display", "Unassigned");
                }
                
                // Get story points
                double totalPoints = 0.0;
                if (!storyJson.isNull("total_points")) {
                    totalPoints = storyJson.getDouble("total_points");
                }
                
                // Create T1Card
                T1Card card = new T1Card(
                    "taiga_" + id,
                    subject,
                    description,
                    assignedUser,
                    totalPoints
                );
                
                stories.add(card);
                Logger.debug("Created story card: " + subject + " (ID: " + id + ")");
                
            } catch (Exception e) {
                Logger.error("Error processing story at index " + i, e);
            }
        }
        
        return stories;
    }
    
    /**
     * Enhanced login method with better error handling
     */
    private String loginAndGetToken(String username, String password) throws Exception {
        Logger.info("Attempting to login to Taiga API...");
        
        URL url = new URL(TAIGA_API + "/auth");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            // Configure connection
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            
            // Create request body
            String jsonInput = String.format(
                "{\"type\": \"normal\", \"username\": \"%s\", \"password\": \"%s\"}",
                escapeJson(username), escapeJson(password));
            
            Logger.debug("Sending authentication request to: " + url);
            Logger.debug("Request body: " + jsonInput.replaceAll("\"password\":\"[^\"]*\"", "\"password\":\"***\""));
            
            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes("UTF-8"));
                os.flush();
            }
            
            // Get response
            int responseCode = conn.getResponseCode();
            Logger.info("Taiga API response code: " + responseCode);
            
            // Read response
            String responseBody = readResponse(conn, responseCode);
            Logger.debug("Response body: " + (responseBody.length() > 500 ? 
                responseBody.substring(0, 500) + "..." : responseBody));
            
            // Parse response
            if (responseCode == 200) {
                try {
                    JSONObject json = new JSONObject(responseBody);
                    if (json.has("auth_token")) {
                        String token = json.getString("auth_token");
                        Logger.info("Authentication successful, token obtained");
                        return token;
                    } else {
                        throw new RuntimeException("No auth_token in successful response: " + responseBody);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse successful auth response: " + e.getMessage());
                }
            } else {
                // Handle error response
                String errorMessage = parseErrorResponse(responseBody, responseCode);
                throw new RuntimeException("Authentication failed (HTTP " + responseCode + "): " + errorMessage);
            }
            
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * Parse error response from Taiga API
     */
    private String parseErrorResponse(String responseBody, int responseCode) {
        try {
            JSONObject json = new JSONObject(responseBody);
            
            // Try different error message fields
            if (json.has("_error_message")) {
                return json.getString("_error_message");
            } else if (json.has("detail")) {
                return json.getString("detail");
            } else if (json.has("msg")) {
                return json.getString("msg");
            } else if (json.has("message")) {
                return json.getString("message");
            } else if (json.has("error")) {
                Object error = json.get("error");
                return error.toString();
            } else {
                // Return first string value found
                for (String key : json.keySet()) {
                    Object value = json.get(key);
                    if (value instanceof String) {
                        return key + ": " + value;
                    }
                }
                return "Server error - " + responseBody;
            }
        } catch (Exception e) {
            // If JSON parsing fails, return raw response
            return "Invalid response format: " + responseBody;
        }
    }
    
    /**
     * Read HTTP response with proper error handling
     */
    private String readResponse(HttpURLConnection conn, int responseCode) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                responseCode >= 200 && responseCode < 300 ? 
                conn.getInputStream() : conn.getErrorStream(), "UTF-8"))) {
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
    
    /**
     * Get project ID from slug with better error handling
     */
    private int getProjectId(String token, String projectSlug) throws Exception {
        Logger.info("Getting project ID for slug: " + projectSlug);
        
        URL url = new URL(TAIGA_API + "/projects/by_slug?slug=" + java.net.URLEncoder.encode(projectSlug, "UTF-8"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            
            int responseCode = conn.getResponseCode();
            String responseBody = readResponse(conn, responseCode);
            
            if (responseCode == 200) {
                JSONObject json = new JSONObject(responseBody);
                if (json.has("id")) {
                    int projectId = json.getInt("id");
                    Logger.info("Found project ID: " + projectId + " for slug: " + projectSlug);
                    return projectId;
                } else {
                    throw new RuntimeException("No project ID in response for slug: " + projectSlug);
                }
            } else if (responseCode == 404) {
                throw new RuntimeException("Project not found: " + projectSlug + ". Please check the project name.");
            } else {
                String errorMsg = parseErrorResponse(responseBody, responseCode);
                throw new RuntimeException("Failed to get project (HTTP " + responseCode + "): " + errorMsg);
            }
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * Fetch user stories from Taiga API with better error handling
     */
    private JSONArray fetchUserStoriesFromAPI(String token, int projectId) throws Exception {
        Logger.info("Fetching user stories for project ID: " + projectId);
        
        URL url = new URL(TAIGA_API + "/userstories?project=" + projectId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            
            int responseCode = conn.getResponseCode();
            String responseBody = readResponse(conn, responseCode);
            
            if (responseCode == 200) {
                JSONArray stories = new JSONArray(responseBody);
                Logger.info("Successfully fetched " + stories.length() + " user stories");
                return stories;
            } else {
                String errorMsg = parseErrorResponse(responseBody, responseCode);
                throw new RuntimeException("Failed to fetch stories (HTTP " + responseCode + "): " + errorMsg);
            }
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * Escape JSON string values
     */
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    // Getters for status checking
    public boolean isAuthenticated() {
        return isAuthenticated;
    }
    
    public String getProjectSlug() {
        return projectSlug;
    }
    
    public int getProjectId() {
        return projectId;
    }
    
    public void logout() {
        this.authToken = null;
        this.isAuthenticated = false;
        this.projectId = 0;
        Logger.info("Logged out from Taiga");
    }
    
    /**
     * Test connection to Taiga API
     */
    public static boolean testConnection() {
        try {
            URL url = new URL(TAIGA_API + "/projects");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            
            Logger.info("Taiga API connection test - Response code: " + responseCode);
            return responseCode == 200 || responseCode == 401; // 401 is expected without auth
        } catch (Exception e) {
            Logger.error("Taiga API connection test failed", e);
            return false;
        }
    }
}