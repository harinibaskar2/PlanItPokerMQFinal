package hbaskar.four;

import hbaskar.T1Card;
import hbaskar.controllers.T1VotingController;
import hbaskar.models.T1VotingSession;
import hbaskar.utils.Logger;

import javax.swing.*;

import java.awt.*;
import java.util.Map;

/**
 * Clean UI component for displaying voting results
 * Only handles UI concerns, delegates business logic to controller
 * 
 * @author PlanItPoker Team  
 * @version 3.0 - Clean Architecture
 */
public class T1VotingResultsPanel extends JPanel {
    
    // UI Components
    private final JLabel storyTitleLabel;
    private final JLabel voteCountLabel;
    private final JLabel averageLabel;
    private final JLabel statusLabel;
    private final JButton revealButton;
    private final JPanel individualVotesPanel;
    private final JScrollPane votesScrollPane;
    private final JPanel chartContainer;
    
    // Dependencies (injected)
    private T1VotingController votingController;
    
    // State
    private T1Card currentStory;
    private T1VotingSession currentSession;
    
    public T1VotingResultsPanel(T1VotingController votingController) {
        this.votingController = votingController;
        
        // Initialize UI components
        this.storyTitleLabel = new JLabel("No story selected", SwingConstants.CENTER);
        this.voteCountLabel = new JLabel("Votes: 0/0", SwingConstants.CENTER);
        this.averageLabel = new JLabel("Average: --", SwingConstants.CENTER);
        this.statusLabel = new JLabel("Select a story to start voting", SwingConstants.CENTER);
        this.revealButton = new JButton("Reveal Cards");
        this.individualVotesPanel = new JPanel();
        this.votesScrollPane = new JScrollPane(individualVotesPanel);
        this.chartContainer = new JPanel(new BorderLayout());
        
        initializeUI();
        setupEventHandlers();
        
        // Show initial state
        updateDisplay(null, null);
        
        Logger.debug("T1VotingResultsPanel initialized");
    }
    
    /**
     * Initialize UI layout and styling
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);
        
        // Configure components
        setupComponents();
        
        // Layout components
        layoutComponents();
    }
    
    /**
     * Configure individual components
     */
    private void setupComponents() {
        // Story title
        storyTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        storyTitleLabel.setForeground(new Color(50, 50, 50));
        
        // Status label
        statusLabel.setForeground(Color.GRAY);
        
        // Reveal button
        revealButton.setEnabled(false);
        revealButton.setBackground(new Color(240, 173, 78));
        revealButton.setForeground(Color.DARK_GRAY);
        revealButton.setFocusPainted(false);
        
        // Individual votes panel
        individualVotesPanel.setLayout(new BoxLayout(individualVotesPanel, BoxLayout.Y_AXIS));
        individualVotesPanel.setBackground(Color.WHITE);
        
        // Votes scroll pane
        votesScrollPane.setPreferredSize(new Dimension(200, 150));
        votesScrollPane.setBorder(BorderFactory.createTitledBorder("Individual Votes"));
        votesScrollPane.setVisible(false);
        
        // Chart container
        chartContainer.setPreferredSize(new Dimension(300, 200));
        chartContainer.setBorder(BorderFactory.createTitledBorder("Vote Distribution"));
        chartContainer.add(createEmptyChartPanel(), BorderLayout.CENTER);
    }
    
    /**
     * Layout components in the panel
     */
    private void layoutComponents() {
        // Top section - Story info and stats
        JPanel topPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(storyTitleLabel);
        topPanel.add(voteCountLabel);
        topPanel.add(averageLabel);
        topPanel.add(statusLabel);
        
        // Middle section - Reveal button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(revealButton);
        
        // Bottom section - Chart and individual votes
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(chartContainer, BorderLayout.CENTER);
        bottomPanel.add(votesScrollPane, BorderLayout.SOUTH);
        
        // Add to main panel
        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Reveal button handler
        revealButton.addActionListener(e -> handleRevealButtonClick());
        
        // Controller callbacks
        if (votingController != null) {
            votingController.setStorySelectionCallback(this::onStorySelectionChanged);
            votingController.setVotingUpdateCallback(this::onVotingSessionUpdated);
        }
    }
    
    /**
     * Handle reveal button click
     */
    private void handleRevealButtonClick() {
        if (votingController != null) {
            boolean success = votingController.toggleReveal();
            if (success) {
                refreshDisplay();
            }
        }
    }
    
    /**
     * Update display with new story and session data
     */
    public void updateDisplay(T1Card story, T1VotingSession session) {
        SwingUtilities.invokeLater(() -> {
            this.currentStory = story;
            this.currentSession = session;
            
            if (story == null) {
                showNoStorySelected();
            } else {
                showStoryData(story, session);
            }
            
            revalidate();
            repaint();
        });
    }
    
    /**
     * Refresh current display
     */
    public void refreshDisplay() {
        if (votingController != null) {
            T1Card selectedStory = votingController.getSelectedStory();
            T1VotingSession session = votingController.getVotingSession();
            updateDisplay(selectedStory, session);
        }
    }
    
    /**
     * Show no story selected state
     */
    private void showNoStorySelected() {
        storyTitleLabel.setText("No story selected");
        voteCountLabel.setText("Votes: 0/0");
        averageLabel.setText("Average: --");
        statusLabel.setText("Select a story to start voting");
        statusLabel.setForeground(Color.GRAY);
        
        revealButton.setEnabled(false);
        revealButton.setText("Reveal Cards");
        
        votesScrollPane.setVisible(false);
        
        // Update chart
        updateChartDisplay(null);
    }
    
    /**
     * Show story data
     */
    private void showStoryData(T1Card story, T1VotingSession session) {
        // Update story title
        String title = story.getTitle();
        if (title.length() > 25) {
            title = title.substring(0, 22) + "...";
        }
        storyTitleLabel.setText(title);
        
        // Update vote statistics
        updateVoteStatistics(story, session);
        
        // Update reveal state
        updateRevealState(story, session);
        
        // Update chart
        updateChartDisplay(story);
    }
    
    /**
     * Update vote statistics display
     */
    private void updateVoteStatistics(T1Card story, T1VotingSession session) {
        int voteCount = story.getVoteCount();
        int totalPlayers = votingController != null ? votingController.getTotalPlayers() : 1;
        
        voteCountLabel.setText("Votes: " + voteCount + "/" + totalPlayers);
        
        if (voteCount > 0) {
            double average = story.getAverageScore();
            averageLabel.setText(String.format("Average: %.1f", average));
            
            // Update status
            if (voteCount == totalPlayers) {
                statusLabel.setText("All players voted!");
                statusLabel.setForeground(new Color(0, 150, 0));
            } else {
                statusLabel.setText("Waiting for " + (totalPlayers - voteCount) + " more votes");
                statusLabel.setForeground(Color.ORANGE);
            }
            
            revealButton.setEnabled(true);
        } else {
            averageLabel.setText("Average: --");
            statusLabel.setText("No votes yet");
            statusLabel.setForeground(Color.GRAY);
            revealButton.setEnabled(false);
        }
    }
    
    /**
     * Update reveal state display
     */
    private void updateRevealState(T1Card story, T1VotingSession session) {
        boolean isRevealed = story.isRevealed() || (session != null && session.isRevealed());
        
        if (isRevealed) {
            revealButton.setText("Hide Cards");
            showIndividualVotes(story);
            votesScrollPane.setVisible(true);
        } else {
            revealButton.setText("Reveal Cards");
            votesScrollPane.setVisible(false);
        }
    }
    
    /**
     * Show individual player votes
     */
    private void showIndividualVotes(T1Card story) {
        individualVotesPanel.removeAll();
        
        Map<String, Integer> scores = story.getScores();
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            String playerName = entry.getKey();
            Integer score = entry.getValue();
            
            JLabel voteLabel = createVoteLabel(playerName, score);
            individualVotesPanel.add(voteLabel);
        }
        
        individualVotesPanel.revalidate();
        individualVotesPanel.repaint();
    }
    
    /**
     * Create label for individual vote
     */
    private JLabel createVoteLabel(String playerName, Integer score) {
        String scoreText = formatScore(score);
        JLabel label = new JLabel(playerName + ": " + scoreText);
        
        label.setOpaque(true);
        label.setBackground(new Color(240, 248, 255));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        return label;
    }
    
    /**
     * Format score for display
     */
    private String formatScore(Integer score) {
        if (score == null) return "?";
        switch (score) {
            case -1: return "?";
            case -2: return "☕";
            case 0: return "½";
            default: return score.toString();
        }
    }

    private JPanel createEmptyChartPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(message != null ? message : "No data to display", SwingConstants.CENTER);
        label.setForeground(new Color(120, 120, 120));
        panel.add(label, BorderLayout.CENTER);
        panel.setBackground(Color.WHITE);
        return panel;
    }
    
    private void updateChartDisplay(T1Card story) {
        SwingUtilities.invokeLater(() -> {
            try {
                chartContainer.removeAll();
                
                JPanel chartPanel;
                if (votingController != null && story != null && story.getVoteCount() > 0) {
                    // Only create chart if there are votes
                    chartPanel = votingController.createVoteDistributionChart();
                    if (chartPanel == null) {
                        chartPanel = createEmptyChartPanel("No chart data available");
                    }
                } else if (story == null) {
                    chartPanel = createEmptyChartPanel("No story selected");
                } else {
                    chartPanel = createEmptyChartPanel("No votes to display");
                }
                
                chartContainer.add(chartPanel, BorderLayout.CENTER);
                chartContainer.revalidate();
                chartContainer.repaint();
                
                Logger.debug("Chart updated successfully");
                
            } catch (Exception e) {
                Logger.error("Failed to update chart display", e);
                // Fallback to empty chart
                chartContainer.removeAll();
                chartContainer.add(createEmptyChartPanel("Chart error: " + e.getMessage()), BorderLayout.CENTER);
                chartContainer.revalidate();
                chartContainer.repaint();
            }
        });
    }
    

    
    /**
     * Create empty chart panel
     */
    private JPanel createEmptyChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("No data to display", SwingConstants.CENTER), BorderLayout.CENTER);
        panel.setBackground(Color.WHITE);
        return panel;
    }
    
    // Event handlers for controller callbacks
    
    /**
     * Handle story selection change from controller
     */
    private void onStorySelectionChanged(T1Card story) {
        T1VotingSession session = votingController != null ? votingController.getVotingSession() : null;
        updateDisplay(story, session);
    }
    
    /**
     * Handle voting session update from controller
     */
    private void onVotingSessionUpdated(T1VotingSession session) {
        updateDisplay(currentStory, session);
    }
    
    /**
     * Set voting controller (for dependency injection)
     */
    public void setVotingController(T1VotingController votingController) {
        this.votingController = votingController;
        setupEventHandlers();
    }
}