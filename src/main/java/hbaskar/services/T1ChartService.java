package hbaskar.services;

import hbaskar.T1Card;
import hbaskar.interfaces.T1IChartService;
import hbaskar.utils.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.util.Map;

/**
 * Service handling chart generation operations
 * Implements business logic for visualization functionality
 * 
 * @author PlanItPoker Team
 * @version 1.0
 */
public class T1ChartService implements T1IChartService {
    
    private static final Color PRIMARY_COLOR = new Color(135, 206, 250);
    private static final Color VOTED_COLOR = new Color(144, 238, 144);
    private static final Color NOT_VOTED_COLOR = new Color(255, 182, 193);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 14);
    
    @Override
    public JPanel createVoteDistributionChart(T1Card story) {
        try {
            if (story == null || story.getScores().isEmpty()) {
                return createEmptyChart("No votes recorded yet");
            }
            
            CategoryDataset dataset = createVoteDistributionDataset(story);
            JFreeChart chart = ChartFactory.createBarChart(
                "Vote Distribution - " + truncateTitle(story.getTitle()),
                "Vote Value",
                "Number of Votes",
                dataset
            );
            
            customizeBarChart(chart);
            
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(300, 200));
            
            Logger.debug("Created vote distribution chart for: " + story.getTitle());
            return chartPanel;
            
        } catch (Exception e) {
            Logger.error("Failed to create vote distribution chart", e);
            return createEmptyChart("Error creating chart");
        }
    }
    
    @Override
    public JPanel createParticipationChart(T1Card story, int totalPlayers) {
        try {
            if (story == null) {
                return createEmptyChart("No story selected");
            }
            
            PieDataset dataset = createParticipationDataset(story, totalPlayers);
            JFreeChart chart = ChartFactory.createPieChart(
                "Voting Participation",
                dataset,
                true,  // legend
                true,  // tooltips  
                false  // URLs
            );
            
            customizePieChart(chart);
            
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(250, 200));
            
            Logger.debug("Created participation chart for: " + story.getTitle());
            return chartPanel;
            
        } catch (Exception e) {
            Logger.error("Failed to create participation chart", e);
            return createEmptyChart("Error creating chart");
        }
    }
    
    @Override
    public JPanel createEmptyChart(String message) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            JFreeChart chart = ChartFactory.createBarChart(
                message,
                "",
                "",
                dataset
            );
            
            chart.setBackgroundPaint(Color.WHITE);
            chart.getTitle().setFont(TITLE_FONT);
            
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(300, 200));
            
            return chartPanel;
            
        } catch (Exception e) {
            Logger.error("Failed to create empty chart", e);
            // Return a simple panel as fallback
            JPanel panel = new JPanel();
            panel.add(new javax.swing.JLabel(message));
            return panel;
        }
    }
    
    @Override
    public void updateChart(JPanel chartPanel, T1Card story) {
        // For now, we'll recreate the chart
        // In a more advanced implementation, we could update existing chart data
        Logger.debug("Updating chart for story: " + (story != null ? story.getTitle() : "null"));
    }
    
    /**
     * Create dataset for vote distribution bar chart
     */
    private CategoryDataset createVoteDistributionDataset(T1Card story) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> scores = story.getScores();
        
        // Count votes for each score value
        Map<Integer, Integer> voteCounts = new java.util.HashMap<>();
        for (Integer score : scores.values()) {
            voteCounts.put(score, voteCounts.getOrDefault(score, 0) + 1);
        }
        
        // Add data to dataset
        for (Map.Entry<Integer, Integer> entry : voteCounts.entrySet()) {
            String scoreLabel = formatScoreLabel(entry.getKey());
            dataset.addValue(entry.getValue(), "Votes", scoreLabel);
        }
        
        return dataset;
    }
    
    /**
     * Create dataset for participation pie chart
     */
    private PieDataset createParticipationDataset(T1Card story, int totalPlayers) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        int votedCount = story.getScores().size();
        int notVotedCount = Math.max(0, totalPlayers - votedCount);
        
        if (votedCount > 0) {
            dataset.setValue("Voted (" + votedCount + ")", votedCount);
        }
        if (notVotedCount > 0) {
            dataset.setValue("Not Voted (" + notVotedCount + ")", notVotedCount);
        }
        
        return dataset;
    }
    
    /**
     * Customize bar chart appearance
     */
    private void customizeBarChart(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        
        // Set colors
        renderer.setSeriesPaint(0, PRIMARY_COLOR);
        
        // Set background
        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // Set fonts
        chart.getTitle().setFont(TITLE_FONT);
    }
    
    /**
     * Customize pie chart appearance
     */
    private void customizePieChart(JFreeChart chart) {
        PiePlot plot = (PiePlot) chart.getPlot();
        
        // Set background first
        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);
        
        // Set fonts
        chart.getTitle().setFont(TITLE_FONT);
        
        // Set colors for known keys
        try {
            @SuppressWarnings("unchecked")
            java.util.List<Comparable> keys = plot.getDataset().getKeys();
            
            for (Comparable key : keys) {
                String keyStr = key.toString();
                if (keyStr.startsWith("Voted")) {
                    plot.setSectionPaint(key, VOTED_COLOR);
                } else if (keyStr.startsWith("Not Voted")) {
                    plot.setSectionPaint(key, NOT_VOTED_COLOR);
                }
            }
        } catch (Exception e) {
            Logger.warn("Could not set pie chart colors: " + e.getMessage());
        }
    }
    
    /**
     * Format score label for display
     */
    private String formatScoreLabel(Integer score) {
        if (score == null) return "?";
        switch (score) {
            case -1: return "?";
            case -2: return "☕";
            case 0: return "½";
            default: return score.toString();
        }
    }
    
    /**
     * Truncate long titles for chart display
     */
    private String truncateTitle(String title) {
        if (title == null) return "Unknown Story";
        return title.length() > 20 ? title.substring(0, 17) + "..." : title;
    }
}