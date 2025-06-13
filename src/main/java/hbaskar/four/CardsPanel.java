package hbaskar.four;

import hbaskar.T1Card;
import hbaskar.controllers.T1VotingController;
import hbaskar.models.T1VotingSession;
import hbaskar.one.PlanItPokerRepository;
import hbaskar.utils.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Clean UI component for voting cards
 * Only handles UI concerns, delegates business logic to controller
 * 
 * @author PlanItPoker Team
 * @version 3.0 - Clean Architecture
 */
public class CardsPanel extends JPanel {
    
    private static final String[] CARD_VALUES = {
        "0", "½", "1", "2", "3", "5", "8", "13", "21", "34", "55", "?"
    };
    
    private static final Color DEFAULT_COLOR = new Color(172, 248, 199);
    private static final Color UNKNOWN_COLOR = new Color(255, 165, 0);
    private static final Color SELECTED_COLOR = new Color(255, 215, 0);
    private static final Font CARD_FONT = new Font("SansSerif", Font.BOLD, 20);
    
    // Dependencies (injected)
    private T1VotingController votingController;
    
    // State
    private String currentPlayerVote;
    
    public CardsPanel(T1VotingController votingController) {
        this.votingController = votingController;
        
        initializeUI();
        setupEventHandlers();
        
        Logger.debug("CardsPanel initialized");
    }
    
    /**
     * Initialize UI components
     */
    private void initializeUI() {
        setLayout(new GridLayout(4, 3, 10, 10));
        
        createVotingCards();
        updateVotingState();
    }
    
    /**
     * Create voting card buttons
     */
    private void createVotingCards() {
        for (String value : CARD_VALUES) {
            JButton card = createVotingCard(value);
            add(card);
        }
    }
    
    /**
     * Create individual voting card
     */
    private JButton createVotingCard(String value) {
        JButton card = new JButton(value);
        
        // Set appearance
        card.setFont(CARD_FONT);
        card.setFocusPainted(false);
        updateCardAppearance(card, value, false);
        
        // Add click handler
        card.addActionListener(e -> handleCardClick(value, card));
        
        // Add tooltip
        card.setToolTipText(getCardTooltip(value));
        
        return card;
    }
    
    /**
     * Handle voting card click
     */
    private void handleCardClick(String value, JButton card) {
        if (votingController == null) {
            showError("Voting system not available");
            return;
        }
        
        // Check if story is selected
        if (votingController.getSelectedStory() == null) {
            showNoStorySelectedMessage();
            return;
        }
        
        String playerName = getCurrentPlayerName();
        
        // Check if player already voted
        if (votingController.hasPlayerVoted(playerName)) {
            if (!confirmVoteChange()) {
                return;
            }
        }
        
        // Provide visual feedback
        provideClickFeedback(card);
        
        // Submit vote through controller
        boolean success = votingController.submitVote(playerName, value);
        
        if (success) {
            currentPlayerVote = value;
            showVoteConfirmation(value);
            updateVotingState();
        } else {
            showError("Failed to submit vote. Please try again.");
        }
    }
    
    /**
     * Update card appearance based on state
     */
    private void updateCardAppearance(JButton card, String value, boolean isPlayerVote) {
        if (isPlayerVote) {
            card.setBackground(SELECTED_COLOR);
        } else if ("?".equals(value)) {
            card.setBackground(UNKNOWN_COLOR);
        } else {
            card.setBackground(DEFAULT_COLOR);
        }
        
        card.setForeground(Color.DARK_GRAY);
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        if (votingController != null) {
            // Listen for story selection changes
            votingController.setStorySelectionCallback(story -> {
                SwingUtilities.invokeLater(this::updateVotingState);
            });
            
            // Listen for voting updates
            votingController.setVotingUpdateCallback(session -> {
                SwingUtilities.invokeLater(this::updateVotingState);
            });
        }
    }
    
    /**
     * Update voting state and card appearances
     */
    public void updateVotingState() {
        SwingUtilities.invokeLater(() -> {
            boolean hasSelectedStory = votingController != null && 
                                    votingController.getSelectedStory() != null;
            
            // Enable/disable cards based on story selection
            setVotingEnabled(hasSelectedStory);
            
            // Highlight player's current vote
            if (hasSelectedStory) {
                updatePlayerVoteHighlight();
            } else {
                clearAllHighlights();
            }
        });
    }
    
    private void updatePlayerVoteHighlight() {
    if (votingController == null) return;
    
    String playerName = getCurrentPlayerName();
    T1Card selectedStory = votingController.getSelectedStory();
    
    if (selectedStory != null) {
        // Get vote from voting session instead of story card
        T1VotingSession session = votingController.getVotingSession();
        if (session != null) {
            Integer playerVote = session.getPlayerVote(playerName);
            if (playerVote != null) {
                String voteValue = formatVoteForDisplay(playerVote);
                highlightPlayerVote(voteValue);
                return;
            }
        }
    }
    
    clearAllHighlights();
}
    
    /**
     * Highlight the card for player's vote
     */
    private void highlightPlayerVote(String voteValue) {
        Component[] components = getComponents();
        
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                String cardValue = button.getText();
                boolean isPlayerVote = cardValue.equals(voteValue);
                
                updateCardAppearance(button, cardValue, isPlayerVote);
            }
        }
        
        repaint();
    }
    
    /**
     * Clear all card highlights
     */
    private void clearAllHighlights() {
        Component[] components = getComponents();
        
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                String cardValue = button.getText();
                updateCardAppearance(button, cardValue, false);
            }
        }
        
        repaint();
    }
    
    /**
     * Enable or disable voting cards
     */
    public void setVotingEnabled(boolean enabled) {
        Component[] components = getComponents();
        
        for (Component component : components) {
            component.setEnabled(enabled);
        }
    }
    
    /**
     * Provide visual feedback for card click
     */
    private void provideClickFeedback(JButton card) {
        Color originalColor = card.getBackground();
        card.setBackground(SELECTED_COLOR);
        
        // Reset color after brief delay
        Timer timer = new Timer(150, e -> {
            if (!isPlayerVoteCard(card)) {
                card.setBackground(originalColor);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Check if card represents player's current vote
     */
    private boolean isPlayerVoteCard(JButton card) {
        return currentPlayerVote != null && currentPlayerVote.equals(card.getText());
    }
    
    private String formatVoteForDisplay(Integer vote) {
		if (vote == null) return "?";
		switch (vote) {
			case -1: return "?";
			case -2: return "☕";
			case 0: return "½";
			default: return vote.toString();
		}
	}
    
    /**
     * Get tooltip text for card
     */
    private String getCardTooltip(String value) {
        switch (value) {
            case "?": return "Unknown/Unsure";
            case "½": return "Half point";
            default: return "Vote " + value + " points";
        }
    }
    
    /**
     * Get current player name
     */
    private String getCurrentPlayerName() {
        PlanItPokerRepository repo = PlanItPokerRepository.getInstance();
        String playerName = repo.getLoggedInUser();
        return playerName != null ? playerName : "Anonymous";
    }
    
    // UI Dialog Methods
    
    /**
     * Show no story selected message
     */
    private void showNoStorySelectedMessage() {
        JOptionPane.showMessageDialog(
            this,
            "Please select a story first before voting.",
            "No Story Selected",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Confirm vote change
     */
    private boolean confirmVoteChange() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "You have already voted for this story. Do you want to change your vote?",
            "Change Vote",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Show vote confirmation
     */
    private void showVoteConfirmation(String value) {
        String storyTitle = votingController != null && votingController.getSelectedStory() != null
            ? votingController.getSelectedStory().getTitle()
            : "selected story";
        
        // Use a timer to show brief confirmation without blocking
        Timer timer = new Timer(1500, e -> {
            // Confirmation automatically disappears
        });
        timer.setRepeats(false);
        timer.start();
        
        Logger.info("Vote confirmed: " + value + " for " + storyTitle);
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Set voting controller (for dependency injection)
     */
    public void setVotingController(T1VotingController votingController) {
        this.votingController = votingController;
        setupEventHandlers();
        updateVotingState();
    }
}