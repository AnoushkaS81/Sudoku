package components;

import pages.InteractiveSudokuController;
import utils.CommonConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class VictoryDialog {

    private JFrame parentFrame;
    private InteractiveSudokuController controller;
    private SudokuBoard9By9 board;

    public VictoryDialog(JFrame parentFrame, InteractiveSudokuController controller, SudokuBoard9By9 board) {
        this.parentFrame = parentFrame;
        this.controller = controller;
        this.board = board;
    }

    /**
     * Check if the puzzle is completely solved
     * @return true if all cells are filled and board is valid
     */
    public boolean isPuzzleSolved() {
        if (board == null || controller == null) {
            return false;
        }

        // Check if all cells are filled
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.getBoard()[row][col] == 0) {
                    return false;
                }
            }
        }

        // Check if board is valid (no conflicts)
        return board.isValidBoard();
    }

    /**
     * Show the victory dialog with statistics
     */
    public void showVictoryDialog() {
        JDialog dialog = new JDialog(parentFrame, "Puzzle Solved!", true);
        dialog.setUndecorated(true);
        dialog.setSize(
                CommonConstants.HOMEPAGE_WIDTH - CommonConstants.HOMEPAGE_WIDTH / 4,
                CommonConstants.HOMEPAGE_HEIGHT - CommonConstants.HOMEPAGE_HEIGHT / 4
        );
        dialog.setLocationRelativeTo(parentFrame);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Victory gradient (green tones)
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(240, 253, 244),
                        0, getHeight(), new Color(220, 252, 231)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Draw decorative stars/sparkles
                drawSparkles(g2);
            }

            private void drawSparkles(Graphics2D g2) {
                g2.setColor(new Color(76, 175, 80, 100));
                int[] xPositions = {50, 120, 200, getWidth() - 80, getWidth() - 150};
                int[] yPositions = {60, 150, 100, 80, 140};
                int[] sizes = {8, 12, 10, 9, 11};

                for (int i = 0; i < xPositions.length; i++) {
                    drawStar(g2, xPositions[i], yPositions[i], sizes[i]);
                }
            }

            private void drawStar(Graphics2D g2, int cx, int cy, int size) {
                Path2D star = new Path2D.Double();
                for (int i = 0; i < 10; i++) {
                    double angle = Math.PI * i / 5.0;
                    double r = (i % 2 == 0) ? size : size / 2.5;
                    double x = cx + r * Math.cos(angle - Math.PI / 2);
                    double y = cy + r * Math.sin(angle - Math.PI / 2);
                    if (i == 0) {
                        star.moveTo(x, y);
                    } else {
                        star.lineTo(x, y);
                    }
                }
                star.closePath();
                g2.fill(star);
            }
        };

        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(76, 175, 80), 3),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Header Panel with trophy icon
        JPanel headerPanel = createHeaderPanel();

        // Statistics Panel
        JPanel statsPanel = createStatisticsPanel();

        // Button Panel
        JPanel buttonPanel = createButtonPanel(dialog);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(statsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 25, 20, 25));

        // Trophy icon panel
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int cx = getWidth() / 2;
                int cy = 40;

                // Draw trophy
                drawTrophy(g2, cx, cy);
            }

            private void drawTrophy(Graphics2D g2, int cx, int cy) {
                // Trophy color - gold
                Color gold = new Color(255, 215, 0);
                Color darkGold = new Color(218, 165, 32);

                // Cup body
                g2.setColor(gold);
                int[] xPoints = {cx - 20, cx + 20, cx + 15, cx - 15};
                int[] yPoints = {cy - 10, cy - 10, cy + 20, cy + 20};
                g2.fillPolygon(xPoints, yPoints, 4);

                // Cup rim
                g2.fillRoundRect(cx - 22, cy - 15, 44, 8, 8, 8);

                // Handles
                g2.setStroke(new BasicStroke(3.5f));
                g2.drawArc(cx - 30, cy - 5, 12, 20, 270, 180);
                g2.drawArc(cx + 18, cy - 5, 12, 20, 90, 180);

                // Base
                g2.setColor(darkGold);
                g2.fillRect(cx - 18, cy + 20, 36, 4);
                g2.fillRoundRect(cx - 22, cy + 24, 44, 8, 6, 6);

                // Shine effect
                g2.setColor(new Color(255, 255, 200, 150));
                g2.fillOval(cx - 8, cy, 8, 12);
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(100, 80));

        // Title text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Congratulations!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(76, 175, 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("You've successfully solved the puzzle!");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(66, 66, 66));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(iconPanel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);

        headerPanel.add(textPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createStatisticsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        // Game statistics header
        JLabel statsTitle = new JLabel("Game Statistics");
        statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statsTitle.setForeground(new Color(33, 33, 33));
        statsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        statsPanel.add(statsTitle);
        statsPanel.add(Box.createVerticalStrut(15));

        // Time stat
        int totalSeconds = controller.getElapsedSeconds();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);

        // Mistakes stat
        int mistakes = controller.getMistakeCount();
        String mistakesStr = mistakes + " mistake" + (mistakes != 1 ? "s" : "");

        // Hints stat
        int hintsUsed = 3 - controller.getHintsRemaining();
        String hintsStr = hintsUsed + " hint" + (hintsUsed != 1 ? "s" : "") + " used";

        // Difficulty stat
        String difficultyStr = board != null && board.getBoardDifficulty() != null
                ? board.getBoardDifficulty().toString()
                : "UNKNOWN";

        // Create 2x2 grid for stats
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        gridPanel.setOpaque(false);

        gridPanel.add(createStatCard(
                "Time Taken",
                timeStr,
                new Color(33, 150, 243),
                "⏱"
        ));

        gridPanel.add(createStatCard(
                "Mistakes Made",
                mistakesStr,
                new Color(244, 67, 54),
                "✗"
        ));

        gridPanel.add(createStatCard(
                "Hints Used",
                hintsStr,
                new Color(255, 152, 0),
                "💡"
        ));

        gridPanel.add(createStatCard(
                "Difficulty",
                difficultyStr,
                new Color(103, 58, 181),
                "⭐"
        ));

        statsPanel.add(gridPanel);

        return statsPanel;
    }

    private JPanel createStatCard(String label, String value, Color accentColor, String icon) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setForeground(accentColor);

        // Text panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelText.setForeground(new Color(120, 120, 120));

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueText.setForeground(accentColor);

        textPanel.add(labelText);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(valueText);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createButtonPanel(JDialog dialog) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 30, 25));

        JButton closeButton = createStyledButton("Close", CommonConstants.PRIMARY_COLOR);
        closeButton.setPreferredSize(new Dimension(160, 45));
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color color = bgColor;
                if (getModel().isPressed()) {
                    color = adjustBrightness(bgColor, 0.85f);
                } else if (getModel().isRollover()) {
                    color = adjustBrightness(bgColor, 1.1f);
                }

                g2.setColor(color);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private Color adjustBrightness(Color color, float factor) {
        int r = Math.min(255, Math.max(0, (int) (color.getRed() * factor)));
        int g = Math.min(255, Math.max(0, (int) (color.getGreen() * factor)));
        int b = Math.min(255, Math.max(0, (int) (color.getBlue() * factor)));
        return new Color(r, g, b);
    }
}