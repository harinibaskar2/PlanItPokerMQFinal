package hbaskar.four;

import hbaskar.T1Card;
import hbaskar.controllers.T1VotingController;
import hbaskar.one.PlanItPokerRepository;
import hbaskar.utils.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * Clean UI component for displaying stories
 * Only handles UI concerns, delegates business logic to controller
 * 
 * @author PlanItPoker Team
 * @version 3.0 - Clean Architecture
 */
public class T1StoriesPanel extends JPanel {
    
    private static final int GRID_ROWS = 2;
    private static final int GRID_COLS = 5;
    private static final int BUTTON_WIDTH = 180;
    private static final int BUTTON_HEIGHT = 120;
    
    // UI Components
    private final JPanel storyCardsPanel;
    
    // Dependencies (injected)
    private T1VotingController votingController;
    
    // State
    private List<T1Card> currentStories;
    
    public T1StoriesPanel(T1VotingController votingController) {
        this.votingController = votingController;
        this.storyCardsPanel = new JPanel();
        
        initializeUI();
        setupEventHandlers();
        
        Logger.debug("T1StoriesPanel initialized");
    }
    
    /**
     * Initialize UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Configure story cards panel
        storyCardsPanel.setLayout(new GridLayout(GRID_ROWS, GRID_COLS, 10, 10));
        add(storyCardsPanel, BorderLayout.CENTER);
        
        // Initial load
        refreshStories();
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        if (votingController != null) {
            // Listen for story selection changes to update UI
            votingController.setStorySelectionCallback(this::onStorySelectionChanged);
        }
    }
    
    /**
     * Refresh stories display
     */
    public void refreshStories() {
        SwingUtilities.invokeLater(() -> {
            storyCardsPanel.removeAll();
            
            List<T1Card> stories = loadStoriesFromRepository();
            this.currentStories = stories;
            
            displayStories(stories);
            
            storyCardsPanel.revalidate();
            storyCardsPanel.repaint();
            
            Logger.debug("Refreshed stories display: " + stories.size() + " stories");
        });
    }
    
    /**
     * Load stories from repository
     */
    private List<T1Card> loadStoriesFromRepository() {
        try {
            String currentRoomCode = PlanItPokerRepository.getInstance().getCurrentRoomCode();
            if (currentRoomCode == null) {
                Logger.warn("No current room code available");
                return List.of();
            }
            
            PlanItPokerRepository.Room room = PlanItPokerRepository.getInstance().getRoom(currentRoomCode);
            if (room == null) {
                Logger.warn("Room not found: " + currentRoomCode);
                return List.of();
            }
            
            return room.getAllStories();
            
        } catch (Exception e) {
            Logger.error("Failed to load stories from repository", e);
            return List.of();
        }
    }
    
    /**
     * Display stories in the UI
     */
    private void displayStories(List<T1Card> stories) {
        int count = 0;
        
        for (T1Card story : stories) {
            if (count >= GRID_ROWS * GRID_COLS) {
                break; // Don't exceed grid capacity
            }
            
            JButton storyButton = createStoryButton(story);
            storyCardsPanel.add(storyButton);
            count++;
        }
        
        // Fill remaining slots with empty panels
        while (count < GRID_ROWS * GRID_COLS) {
            storyCardsPanel.add(createEmptyPanel());
            count++;
        }
    }
    
    /**
     * Create button for a story
     */
    private JButton createStoryButton(T1Card story) {
        JButton button = new JButton(formatStoryLabel(story));
        
        // Set appearance
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        updateButtonAppearance(button, story);
        
        // Add click handler
        button.addActionListener(e -> handleStoryClick(story));
        
        return button;
    }
    
    /**
     * Handle story button click
     */
    private void handleStoryClick(T1Card story) {
        if (votingController != null) {
            votingController.selectStory(story);
        }
        
        Logger.debug("Story clicked: " + story.getTitle());
    }
    
    /**
     * Format story information for button label
     */
    private String formatStoryLabel(T1Card story) {
        String assignedUser = story.getAssignedUser() != null ? story.getAssignedUser() : "Unassigned";
        String totalPoints = String.format("%.1f", story.getTotalPoints());
        
        // Voting status
        int voteCount = story.getVoteCount();
        String voteStatus = voteCount > 0 ? " (" + voteCount + " votes)" : " (no votes)";
        String averageScore = voteCount > 0 ? String.format("%.1f", story.getAverageScore()) : "--";
        
        return String.format(
            "<html><b>%s</b><br/>%s<br/>Assigned: %s<br/>Points: %s<br/>Avg: %s%s</html>",
            truncateText(story.getTitle(), 20),
            truncateText(story.getDescription(), 30),
            truncateText(assignedUser, 15),
            totalPoints,
            averageScore,
            voteStatus
        );
    }
    
    /**
     * Update button appearance based on story state
     */
    private void updateButtonAppearance(JButton button, T1Card story) {
        boolean isSelected = votingController != null && votingController.isStorySelected(story);
        boolean hasVotes = story.getVoteCount() > 0;
        boolean isRevealed = story.isRevealed();
        
        // Set colors based on state
        if (isSelected) {
            button.setBackground(new Color(100, 149, 237)); // Cornflower blue
            button.setBorder(new LineBorder(new Color(0, 0, 139), 3));
        } else if (isRevealed && hasVotes) {
            button.setBackground(new Color(144, 238, 144)); // Light green
            button.setBorder(new LineBorder(Color.GRAY));
        } else if (hasVotes) {
            button.setBackground(new Color(255, 255, 224)); // Light yellow
            button.setBorder(new LineBorder(Color.GRAY));
        } else {
            button.setBackground(new Color(220, 240, 255)); // Light blue
            button.setBorder(new LineBorder(Color.GRAY));
        }
    }
    
    /**
     * Create empty panel for grid padding
     */
    private JPanel createEmptyPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(getBackground());
        return panel;
    }
    
    /**
     * Handle story selection change from controller
     */
    private void onStorySelectionChanged(T1Card selectedStory) {
        SwingUtilities.invokeLater(() -> {
            updateAllButtonAppearances();
        });
    }
    
    /**
     * Update all button appearances
     */
    private void updateAllButtonAppearances() {
        if (currentStories == null) {
            return;
        }
        
        Component[] components = storyCardsPanel.getComponents();
        int storyIndex = 0;
        
        for (Component component : components) {
            if (component instanceof JButton && storyIndex < currentStories.size()) {
                JButton button = (JButton) component;
                T1Card story = currentStories.get(storyIndex);
                updateButtonAppearance(button, story);
                storyIndex++;
            }
        }
        
        storyCardsPanel.repaint();
    }
    
    /**
     * Truncate text to fit in button
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }
    
    /**
     * Set voting controller (for dependency injection)
     */
    public void setVotingController(T1VotingController votingController) {
        this.votingController = votingController;
        setupEventHandlers();
    }
    
    /**
     * Get current stories (for testing/debugging)
     */
    public List<T1Card> getCurrentStories() {
        return currentStories != null ? List.copyOf(currentStories) : List.of();
    }
}