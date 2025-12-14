package components;

import utils.CommonConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;


public class HintDialog {

    private JFrame parentFrame;
    private SudokuBoard9By9 board;
    private Solver solver;
    private JButton[][] gridButtons;

    public HintDialog(JFrame parentFrame, SudokuBoard9By9 board, JButton[][] gridButtons) {
        this.parentFrame = parentFrame;
        this.board = board;
        this.gridButtons = gridButtons;
        this.solver = new Solver(board);
    }

    public void showHint() {
        if (board == null || solver == null) {
            showMessageDialog("No Active Game",
                    "Please start a game first!",
                    new Color(244, 67, 54));
            return;
        }

        updateBoardFromUI();
        Solver.SolveStep step = solver.getNextStep();

        if (step == null) {
            if (solver.isSolved()) {
                showMessageDialog("Puzzle Solved!",
                        "Congratulations! The puzzle is already solved!",
                        new Color(76, 175, 80));
            } else {
                showMessageDialog("No Hints Available",
                        "No more logical hints available. The puzzle may require advanced techniques.",
                        new Color(255, 152, 0));
            }
            return;
        }

        showHintDialog(step);
    }

    private void updateBoardFromUI() {
        if (gridButtons != null && board != null) {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    String text = gridButtons[row][col].getText();
                    if (text.isEmpty()) {
                        board.getBoard()[row][col] = 0;
                    } else {
                        try {
                            board.getBoard()[row][col] = Integer.parseInt(text);
                        } catch (NumberFormatException ex) {
                            board.getBoard()[row][col] = 0;
                        }
                    }
                }
            }
            solver = new Solver(board);
        }
    }

    //displays message depending on board state e.g already solved, empty etc
    private void showMessageDialog(String title, String message, Color accentColor) {
        JDialog dialog = new JDialog(parentFrame, title, true);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 3),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(accentColor);

        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(66, 66, 66));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 20, 0));

        JButton okButton = createButton("OK", accentColor);
        okButton.setPreferredSize(new Dimension(100, 36));
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    private void showHintDialog(Solver.SolveStep step) {
        JDialog dialog = new JDialog(parentFrame, "Hint", true);
        dialog.setUndecorated(true);
        dialog.setSize(CommonConstants.HOMEPAGE_WIDTH - CommonConstants.HOMEPAGE_WIDTH/3, CommonConstants.HOMEPAGE_HEIGHT - CommonConstants.HOMEPAGE_HEIGHT/3);
        dialog.setLocationRelativeTo(parentFrame);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(250, 250, 255), 0, getHeight(), new Color(240, 245, 255));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 149, 237), 3),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));

        JLabel titleLabel = new JLabel("Solving Technique");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(100, 100, 100));

        JLabel techniqueLabel = new JLabel(getTechniqueName(step.technique));
        techniqueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        techniqueLabel.setForeground(new Color(33, 33, 33));

        JPanel headerTextPanel = new JPanel();
        headerTextPanel.setLayout(new BoxLayout(headerTextPanel, BoxLayout.Y_AXIS));
        headerTextPanel.setOpaque(false);
        headerTextPanel.add(titleLabel);
        headerTextPanel.add(Box.createVerticalStrut(5));
        headerTextPanel.add(techniqueLabel);

        headerPanel.add(headerTextPanel, BorderLayout.WEST);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 25));

        // Info cards
        if (step.value > 0) {
            contentPanel.add(createInfoCard("Location",
                    "Row " + (step.row + 1) + ", Column " + (step.col + 1),
                    new Color(103, 58, 183)));
            contentPanel.add(Box.createVerticalStrut(10));

            contentPanel.add(createInfoCard("Number",
                    String.valueOf(step.value),
                    new Color(76, 175, 80)));
            contentPanel.add(Box.createVerticalStrut(15));
        }

        // Explanation card
        JPanel explanationCard = new JPanel(new BorderLayout(10, 10));
        explanationCard.setBackground(Color.WHITE);
        explanationCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel explanationTitle = new JLabel("How it works");
        explanationTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        explanationTitle.setForeground(new Color(66, 66, 66));

        JTextArea explanationText = new JTextArea(getDetailedExplanation(step));
        explanationText.setWrapStyleWord(true);
        explanationText.setLineWrap(true);
        explanationText.setEditable(false);
        explanationText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        explanationText.setForeground(new Color(88, 88, 88));
        explanationText.setBackground(Color.WHITE);
        explanationText.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        explanationText.setRows(6);

        JScrollPane scrollPane = new JScrollPane(explanationText);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);

        explanationCard.add(explanationTitle, BorderLayout.NORTH);
        explanationCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(explanationCard);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));

        JButton applyButton = createButton("Apply", new Color(76, 175, 80));
        applyButton.setPreferredSize(new Dimension(140, 42));
        applyButton.addActionListener(e -> {
            applyHint(step);
            dialog.dispose();
        });

        JButton highlightButton = createButton("Show Me", new Color(33, 150, 243));
        highlightButton.setPreferredSize(new Dimension(140, 42));
        highlightButton.addActionListener(e -> {
            highlightHintCell(step);
            dialog.dispose();
        });

        JButton cancelButton = createButton("Close", new Color(158, 158, 158));
        cancelButton.setPreferredSize(new Dimension(140, 42));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(applyButton);
        buttonPanel.add(highlightButton);
        buttonPanel.add(cancelButton);

        // Assemble dialog
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    //info card for modern design, working as component in the dialog shwoing specific info sections
    private JPanel createInfoCard(String label, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelText.setForeground(new Color(120, 120, 120));

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueText.setForeground(accentColor);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(labelText);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(valueText);

        card.add(textPanel, BorderLayout.WEST);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        return card;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color color = bgColor;
                if (getModel().isPressed()) {
                    color = hoverColor(bgColor, 0.85f);
                } else if (getModel().isRollover()) {
                    color = hoverColor(bgColor, 1.1f);
                }

                g2.setColor(color);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private Color hoverColor(Color color, float factor) {
        int r = Math.min(255, Math.max(0, (int)(color.getRed() * factor)));
        int g = Math.min(255, Math.max(0, (int)(color.getGreen() * factor)));
        int b = Math.min(255, Math.max(0, (int)(color.getBlue() * factor)));

        return new Color(r, g, b);
    }


    private String getTechniqueName(Solver.SolveStep.Technique technique) {
        return switch (technique) {
            case NAKED_SINGLE -> "Naked Single";
            case HIDDEN_SINGLE_ROW -> "Hidden Single (Row)";
            case HIDDEN_SINGLE_COL -> "Hidden Single (Column)";
            case HIDDEN_SINGLE_BOX -> "Hidden Single (Box)";
            case NAKED_PAIR -> "Naked Pair";
            case POINTING_PAIR -> "Pointing Pair";
        };
    }

    private String getDetailedExplanation(Solver.SolveStep step) {
        String baseExplanation = step.explanation + "\n\n";

        String techniqueDescription = switch (step.technique) {
            case NAKED_SINGLE ->
                    "A Naked Single occurs when a cell has only one possible candidate number. " +
                            "Since no other number can go in this cell, it must be filled with this candidate.";

            case HIDDEN_SINGLE_ROW ->
                    "A Hidden Single in a row means this number can only be placed in one specific " +
                            "cell within that row, even though the cell might have other candidates.";

            case HIDDEN_SINGLE_COL ->
                    "A Hidden Single in a column means this number can only be placed in one specific " +
                            "cell within that column, even though the cell might have other candidates.";

            case HIDDEN_SINGLE_BOX ->
                    "A Hidden Single in a box means this number can only be placed in one specific " +
                            "cell within that 3×3 box, even though the cell might have other candidates.";

            case NAKED_PAIR ->
                    "A Naked Pair occurs when two cells in the same row, column, or box contain " +
                            "exactly the same two candidates. These candidates can be eliminated from all " +
                            "other cells in that unit.";

            case POINTING_PAIR ->
                    "A Pointing Pair occurs when a candidate number in a box is limited to a single " +
                            "row or column. This means the candidate can be eliminated from that row/column " +
                            "outside the box.";
        };

        return baseExplanation + techniqueDescription;
    }

    private void applyHint(Solver.SolveStep step) {
        if (step.value > 0 && gridButtons != null) {
            solver.applyStep(step);
            gridButtons[step.row][step.col].setText(String.valueOf(step.value));
            gridButtons[step.row][step.col].setForeground(new Color(76, 175, 80));
            gridButtons[step.row][step.col].setFont(new Font("Arial", Font.BOLD, 20));
            highlightHintCell(step);
            board.getBoard()[step.row][step.col] = step.value;
        }
    }

    private void highlightHintCell(Solver.SolveStep step) {
        if (step.value > 0 && gridButtons != null) {
            JButton hintCell = gridButtons[step.row][step.col];
            Color originalBg = hintCell.getBackground();

            javax.swing.Timer timer = new javax.swing.Timer(150, null);
            final int[] count = {0};
            timer.addActionListener(e -> {
                if (count[0] % 2 == 0) {
                    hintCell.setBackground(new Color(255, 235, 59));
                } else {
                    hintCell.setBackground(originalBg);
                }
                count[0]++;
                if (count[0] >= 6) {
                    timer.stop();
                    hintCell.setBackground(new Color(232, 245, 233));
                }
            });
            timer.start();
        }
    }
}