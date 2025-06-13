package hbaskar.interfaces;

import hbaskar.T1Card;
import javax.swing.JPanel;

/**
 * Interface for chart generation operations
 * Defines contract for visualization business logic
 * 
 * @author PlanItPoker Team
 * @version 1.0
 */
public interface T1IChartService {
    
    /**
     * Create vote distribution chart for a story
     */
    JPanel createVoteDistributionChart(T1Card story);
    
    /**
     * Create participation chart showing who voted
     */
    JPanel createParticipationChart(T1Card story, int totalPlayers);
    
    /**
     * Create empty chart with message
     */
    JPanel createEmptyChart(String message);
    
    /**
     * Update existing chart with new data
     */
    void updateChart(JPanel chartPanel, T1Card story);
}