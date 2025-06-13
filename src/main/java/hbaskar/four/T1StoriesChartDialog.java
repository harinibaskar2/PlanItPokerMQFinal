package hbaskar.four;

import hbaskar.T1Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Dialog that displays a chart of all stories with their average scores.
 * Shows story titles, average scores, and provides visual representation.
 * 
 * @author Generated for PlanItPoker
 * @version 1.0
 */
public class T1StoriesChartDialog extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(T1StoriesChartDialog.class);
    private List<T1Card> stories;
    private double maxScore;

    public T1StoriesChartDialog(JFrame parent, List<T1Card> stories) {
        super(parent, "Stories Average Scores Chart", true);
        this.stories = stories;
        this.maxScore = calculateMaxScore();
        
        initializeDialog();
    }

    private void initializeDialog() {
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(getParent());

        // Title
        JLabel titleLabel = new JLabel("Stories Average Scores", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Chart panel
        JPanel chartPanel = new StoriesChartPanel();
        JScrollPane scrollPane = new JScrollPane(chartPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with statistics and close button
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        logger.info("Created stories chart dialog with " + stories.size() + " stories");
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Statistics panel
        JPanel statsPanel = new JPanel(new FlowLayout());
        statsPanel.add(new JLabel("Total Stories: " + stories.size()));
        statsPanel.add(new JLabel(" | "));
        statsPanel.add(new JLabel("Overall Average: " + String.format("%.2f", calculateOverallAverage())));
        statsPanel.add(new JLabel(" | "));
        statsPanel.add(new JLabel("Max Score: " + String.format("%.2f", maxScore)));
        
        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        
        bottomPanel.add(statsPanel, BorderLayout.CENTER);
        bottomPanel.add(closeButton, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        return bottomPanel;
    }

    private double calculateMaxScore() {
        return stories.stream()
                .mapToDouble(T1Card::getAverageScore)
                .max()
                .orElse(10.0); // Default max if no scores
    }

    private double calculateOverallAverage() {
        return stories.stream()
                .mapToDouble(T1Card::getAverageScore)
                .average()
                .orElse(0.0);
    }

    /**
     * Custom panel that draws the bar chart
     */
    private class StoriesChartPanel extends JPanel {
        private static final int BAR_HEIGHT = 40;
        private static final int BAR_SPACING = 10;
        private static final int MARGIN = 20;
        private static final int LABEL_WIDTH = 200;

        public StoriesChartPanel() {
            setBackground(Color.WHITE);
            // Calculate preferred size based on number of stories
            int height = stories.size() * (BAR_HEIGHT + BAR_SPACING) + 2 * MARGIN;
            setPreferredSize(new Dimension(750, Math.max(height, 400)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (stories.isEmpty()) {
                g2d.setFont(new Font("Arial", Font.PLAIN, 16));
                g2d.setColor(Color.GRAY);
                g2d.drawString("No stories to display", getWidth() / 2 - 80, getHeight() / 2);
                g2d.dispose();
                return;
            }

            drawChart(g2d);
            g2d.dispose();
        }

        private void drawChart(Graphics2D g2d) {
            int chartWidth = getWidth() - LABEL_WIDTH - 2 * MARGIN;
            int y = MARGIN;

            // Draw title
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.setColor(Color.BLACK);
            g2d.drawString("Story", MARGIN, y - 5);
            g2d.drawString("Average Score", LABEL_WIDTH + MARGIN, y - 5);

            for (int i = 0; i < stories.size(); i++) {
                T1Card story = stories.get(i);
                double score = story.getAverageScore();
                
                // Calculate bar width (proportional to score)
                int barWidth = maxScore > 0 ? (int) ((score / maxScore) * chartWidth) : 0;
                
                // Choose color based on score
                Color barColor = getBarColor(score);
                
                // Draw story title (truncated if too long)
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.setColor(Color.BLACK);
                String title = story.getTitle();
                if (title.length() > 25) {
                    title = title.substring(0, 22) + "...";
                }
                g2d.drawString(title, MARGIN, y + BAR_HEIGHT / 2 + 4);
                
                // Draw bar
                g2d.setColor(barColor);
                g2d.fillRect(LABEL_WIDTH + MARGIN, y, barWidth, BAR_HEIGHT);
                
                // Draw bar outline
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(LABEL_WIDTH + MARGIN, y, barWidth, BAR_HEIGHT);
                
                // Draw score text on the bar
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 11));
                String scoreText = String.format("%.2f", score);
                int textX = LABEL_WIDTH + MARGIN + barWidth + 5;
                if (barWidth < 50) { // If bar is too small, put text outside
                    textX = LABEL_WIDTH + MARGIN + barWidth + 5;
                } else { // Otherwise put text inside the bar
                    textX = LABEL_WIDTH + MARGIN + barWidth - 35;
                    g2d.setColor(Color.WHITE);
                }
                g2d.drawString(scoreText, textX, y + BAR_HEIGHT / 2 + 4);
                
                y += BAR_HEIGHT + BAR_SPACING;
            }
        }

        private Color getBarColor(double score) {
            // Color gradient from red (low) to green (high)
            if (score == 0) return Color.LIGHT_GRAY;
            if (score <= 2) return new Color(255, 102, 102); // Light red
            if (score <= 5) return new Color(255, 178, 102); // Orange
            if (score <= 8) return new Color(255, 255, 102); // Yellow
            return new Color(102, 255, 102); // Light green
        }
    }
}