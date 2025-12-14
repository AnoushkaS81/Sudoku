package pages;

import utils.CommonConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class HowToPlayPage extends JFrame {

    public HowToPlayPage() {
        setTitle("How to Play Sudoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(CommonConstants.HOMEPAGE_WIDTH, CommonConstants.HOMEPAGE_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(15, 35, 30, 35));

        content.add(createCard("The Goal", "Fill the 9×9 grid so that every row, column, and 3×3 box contains the digits 1-9 exactly once. " +
                        "Each number must appear only once in each row, column, and box.",
                new GoalIcon(), createGoalGrids()));
        content.add(Box.createVerticalStrut(18));

        content.add(createCard("How to Play", "Click any empty cell to select it (highlighted in blue). Use the number panel on the right or your keyboard (1-9) to enter a digit." +
                        " Press DELETE or BACKSPACE to clear a cell. The timer starts on your first move.",
                new PlayIcon(), createPlayGrid()));
        content.add(Box.createVerticalStrut(18));

        content.add(createCard("Mistakes & Conflicts", "When you enter an invalid number, it turns red and conflicting cells are highlighted." +
                        " You get 3 mistakes before game over. Use the undo button to quickly clear mistakes from selected cells.",
                new MistakeIcon(), createMistakeGrid()));
        content.add(Box.createVerticalStrut(18));

        content.add(createCard("Visual Highlighting", "When you select a cell, all cells in the same row, column, and 3×3 box are automatically highlighted in light blue." +
                        " This helps you spot patterns and avoid conflicts easily.",
                new HighlightIcon(), createHighlightGrid()));
        content.add(Box.createVerticalStrut(18));

        content.add(createCard("Game Features", "Timer tracks your solving speed. Pause/resume anytime. Get hints when stuck (3 per game)." +
                        " Save your progress and continue later." +
                        " Undo clears the selected cell instantly.",
                new FeaturesIcon(), null));
        content.add(Box.createVerticalStrut(18));

        content.add(createCard("Difficulty Levels", "Easy: 40-45 pre-filled cells, perfect for beginners. Medium: 30-35 cells, balanced challenge." +
                        " Hard: 25-30 cells, for experienced players. Select difficulty before starting a new game.",
                new DifficultyIcon(), null));

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(25, 35, 15, 35));

        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        backButton.setForeground(CommonConstants.PRIMARY_COLOR);
        backButton.setBackground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> { new LandingPage(); dispose(); });

        JLabel titleLabel = new JLabel("Sudoku rules");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(50, 60, 80));

        header.add(backButton, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);
        return header;
    }

    private JPanel createCard(String title, String description, JComponent icon, JPanel example) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(224, 237, 250));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(20, 15));
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, example != null ? 480 : 150));
        card.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { if (e.getClickCount() > 1) e.consume(); }
        });

        JPanel topPanel = new JPanel(new BorderLayout(20, 0));
        topPanel.setOpaque(false);

        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconPanel.setOpaque(false);
        iconPanel.add(icon);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(40, 50, 70));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><div style='width:750px'>" + description + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(90, 105, 130));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        topPanel.add(iconPanel, BorderLayout.WEST);
        topPanel.add(textPanel, BorderLayout.CENTER);

        card.add(topPanel, BorderLayout.NORTH);

        if (example != null) {
            JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            centerWrapper.setOpaque(false);
            centerWrapper.add(example);
            card.add(centerWrapper, BorderLayout.CENTER);
        }

        return card;
    }

    private JPanel createGoalGrids() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);

        int[][] incomplete = {{5,3,0,0,7,0,0,0,0},{6,0,0,1,9,5,0,0,0},{0,9,8,0,0,0,0,6,0},
                {8,0,0,0,6,0,0,0,3},{4,0,0,8,0,3,0,0,1},{7,0,0,0,2,0,0,0,6},
                {0,6,0,0,0,0,2,8,0},{0,0,0,4,1,9,0,0,5},{0,0,0,0,8,0,0,7,9}};
        int[][] complete = {{5,3,4,6,7,8,9,1,2},{6,7,2,1,9,5,3,4,8},{1,9,8,3,4,2,5,6,7},
                {8,5,9,7,6,1,4,2,3},{4,2,6,8,5,3,7,9,1},{7,1,3,9,2,4,8,5,6},
                {9,6,1,5,3,7,2,8,4},{2,8,7,4,1,9,6,3,5},{3,4,5,2,8,6,1,7,9}};

        panel.add(createGrid(incomplete, -1, -1, false, "Puzzle"));
        panel.add(createGrid(complete, -1, -1, false, "Solved"));
        return panel;
    }

    private JPanel createPlayGrid() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panel.setOpaque(false);
        int[][] grid = {{0,3,0,0,7,0,0,0,0},{6,0,0,1,9,5,0,0,0},{0,9,8,0,0,0,0,6,0},
                {8,0,0,0,6,0,0,0,3},{4,0,0,8,0,3,0,0,1},{7,0,0,0,2,0,0,0,6},
                {0,6,0,0,0,0,2,8,0},{0,0,0,4,1,9,0,0,5},{0,0,0,0,8,0,0,7,9}};
        panel.add(createGrid(grid, 0, 2, false, "Selected cell"));
        return panel;
    }

    private JPanel createMistakeGrid() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panel.setOpaque(false);
        int[][] grid = {{5,3,4,0,7,0,0,0,0},{6,0,0,1,9,5,0,0,0},{0,9,8,0,0,0,0,6,0},
                {8,0,0,0,6,0,0,0,3},{4,0,0,8,5,3,0,0,1},{7,0,0,0,2,0,0,0,6},
                {0,6,0,0,0,0,2,8,0},{0,0,0,4,1,9,0,0,5},{0,0,0,0,8,0,0,7,9}};
        panel.add(createGrid(grid, 0, 0, true, "Conflict"));
        return panel;
    }

    private JPanel createHighlightGrid() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panel.setOpaque(false);
        int[][] grid = {{5,3,0,0,7,0,0,0,0},{6,0,0,1,9,5,0,0,0},{0,9,8,0,0,0,0,6,0},
                {8,0,0,0,6,0,0,0,3},{4,0,0,8,0,3,0,0,1},{7,0,0,0,2,0,0,0,6},
                {0,6,0,0,0,0,2,8,0},{0,0,0,4,1,9,0,0,5},{0,0,0,0,8,0,0,7,9}};
        panel.add(createGrid(grid, 4, 4, false, "Highlighted"));
        return panel;
    }

    private JPanel createGrid(int[][] numbers, int highlightRow, int highlightCol, boolean conflict, String label) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        JPanel grid = new JPanel(new GridLayout(9, 9, 1, 1));
        grid.setBackground(new Color(100, 120, 150));
        grid.setBorder(BorderFactory.createLineBorder(new Color(100, 120, 150), 3));
        grid.setPreferredSize(new Dimension(270, 270));
        grid.setMaximumSize(new Dimension(270, 270));

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                JLabel cell = new JLabel(numbers[i][j] == 0 ? "" : String.valueOf(numbers[i][j]));
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                cell.setOpaque(true);
                cell.setFont(new Font("Segoe UI", Font.PLAIN, 16));

                boolean sameRow = (i == highlightRow);
                boolean sameCol = (j == highlightCol);
                boolean sameBox = (i/3 == highlightRow/3) && (j/3 == highlightCol/3);

                if (i == highlightRow && j == highlightCol) {
                    cell.setBackground(conflict ? new Color(255, 180, 180) : new Color(100, 149, 237));
                    cell.setForeground(conflict ? new Color(200, 0, 0) : Color.WHITE);
                    cell.setFont(new Font("Segoe UI", Font.BOLD, 16));
                } else if (label.equals("Highlighted") && highlightRow != -1 && (sameRow || sameCol || sameBox)) {
                    cell.setBackground(new Color(200, 220, 245));
                    cell.setForeground(new Color(60, 70, 90));
                } else if (conflict && i == 0 && j == 2) {
                    cell.setBackground(new Color(255, 200, 200));
                    cell.setForeground(new Color(200, 0, 0));
                } else {
                    cell.setBackground(new Color(245, 247, 250));
                    cell.setForeground(new Color(60, 70, 90));
                }

                cell.setBorder(BorderFactory.createMatteBorder(
                        (i % 3 == 0 && i != 0) ? 3 : 1,
                        (j % 3 == 0 && j != 0) ? 3 : 1,
                        0, 0, new Color(100, 120, 150)
                ));
                grid.add(cell);
            }
        }

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        labelText.setForeground(new Color(100, 115, 140));
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);

        container.add(grid);
        container.add(Box.createVerticalStrut(8));
        container.add(labelText);
        return container;
    }

    static class GoalIcon extends JPanel {
        GoalIcon() { setPreferredSize(new Dimension(70, 70)); setOpaque(false); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(76, 175, 80));
            g2.fillOval(10, 10, 50, 50);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(23, 35, 31, 43);
            g2.drawLine(31, 43, 47, 27);
            g2.dispose();
        }
    }

    static class PlayIcon extends JPanel {
        PlayIcon() { setPreferredSize(new Dimension(70, 70)); setOpaque(false); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(33, 150, 243));
            g2.fillOval(10, 10, 50, 50);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawRoundRect(22, 22, 26, 26, 4, 4);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
            g2.drawString("5", 30, 42);
            g2.dispose();
        }
    }

    static class MistakeIcon extends JPanel {
        MistakeIcon() { setPreferredSize(new Dimension(70, 70)); setOpaque(false); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(244, 67, 54));
            g2.fillOval(10, 10, 50, 50);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(26, 26, 44, 44);
            g2.drawLine(44, 26, 26, 44);
            g2.dispose();
        }
    }

    static class HighlightIcon extends JPanel {
        HighlightIcon() { setPreferredSize(new Dimension(70, 70)); setOpaque(false); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(156, 39, 176));
            g2.fillOval(10, 10, 50, 50);
            g2.setColor(Color.WHITE);
            g2.fillRect(28, 23, 14, 14);
            g2.setColor(new Color(200, 200, 255));
            g2.fillRect(28, 41, 14, 6);
            g2.fillRect(22, 29, 6, 12);
            g2.fillRect(42, 29, 6, 12);
            g2.dispose();
        }
    }

    static class FeaturesIcon extends JPanel {
        FeaturesIcon() { setPreferredSize(new Dimension(70, 70)); setOpaque(false); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 193, 7));
            g2.fillOval(10, 10, 50, 50);
            g2.setColor(Color.WHITE);
            g2.fillOval(25, 22, 20, 22);
            g2.fillRect(29, 39, 12, 4);
            g2.fillRect(29, 44, 12, 4);
            g2.fillRect(31, 48, 8, 3);
            g2.dispose();
        }
    }

    static class DifficultyIcon extends JPanel {
        DifficultyIcon() { setPreferredSize(new Dimension(70, 70)); setOpaque(false); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 150, 136));
            g2.fillOval(10, 10, 50, 50);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int[] xPoints = {23, 27, 31, 35, 39, 43};
            int[] yPoints = {45, 35, 45, 30, 45, 25};
            for (int i = 0; i < xPoints.length - 1; i++) {
                g2.drawLine(xPoints[i], yPoints[i], xPoints[i+1], yPoints[i+1]);
            }
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HowToPlayPage());
    }
}