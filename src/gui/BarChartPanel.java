package gui;

import javax.swing.*;
import java.awt.*;

public class BarChartPanel extends JPanel {
    private final double[] values;
    private final String[] labels;
    private final String title;

    public BarChartPanel(double[] values, String[] labels, String title) {
        this.values = values;
        this.labels = labels;
        this.title = title;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();
        int barWidth = width / values.length;
        double maxValue = getMaxValue(values);

        // Draw bars
        for (int i = 0; i < values.length; i++) {
            int barHeight = (int) ((values[i] / maxValue) * (height - 50));
            int x = i * barWidth + 10;
            int y = height - barHeight - 20;
            g.setColor(Color.BLUE);
            g.fillRect(x, y, barWidth - 20, barHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, barWidth - 20, barHeight);

            // Draw labels
            g.setColor(Color.BLACK);
            g.drawString(labels[i], x, height - 5);

            // Draw value on top of the bar
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(values[i]), x + 5, y - 5);
        }

        // Draw title
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fontMetrics = g.getFontMetrics();
        int titleWidth = fontMetrics.stringWidth(title);
        int x = (width - titleWidth) / 2;
        g.drawString(title, x, 20);
    }

    private double getMaxValue(double[] values) {
        double maxValue = Double.MIN_VALUE;
        for (double value : values) {
            if (value > maxValue) {
                maxValue = value;
            }
        }
        return maxValue;
    }
}

