package pages;

import components.*;
import db.GameSaveManager;
import utils.CommonConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Timer;
import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;
import javax.swing.border.Border;

public class HomePageTest extends JFrame {
    public static int[][] sudoku_board = new int[9][9];
    private JPanel gridPanelContainer;
    public SudokuBoard9By9 currentBoard;
    public JButton[][] gridButtons;
    public HintDialog hintDialog;
    private InteractiveSudokuController interactive_controller;
    public JLabel mistake_label;
    public JLabel timer_label;
    public javax.swing.Timer game_timer;
    public JButton hints_btn;
    private JButton pause_resume_btn;
    private GameSaveManager saveManager;
    private Deque<Move> move_history = new ArrayDeque<>();
    private SudokuBoard9By9.Difficulty currentDifficulty = SudokuBoard9By9.Difficulty.EASY;
    private JLabel difficulty_label;
    public VictoryDialog victoryDialog;
    public SolveHandler solveHandler;

    private static class Move {
        final int row;
        final int col;
        final int previousValue;
        final int newValue;
        final Color previousColor;

        Move(int row, int col, int previousValue, int newValue, Color previousColor) {
            this.row = row;
            this.col = col;
            this.previousValue = previousValue;
            this.newValue = newValue;
            this.previousColor = previousColor;
        }
    }


    public HomePageTest() {
        setTitle("GRASS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        interactive_controller = new InteractiveSudokuController();
        saveManager = new GameSaveManager();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key_code = e.getKeyCode();

                if (key_code >= KeyEvent.VK_1 && key_code <= KeyEvent.VK_9) {
                    int digit = key_code - KeyEvent.VK_0;
                    handleNumberInput(digit);
                }
                else if (key_code >= KeyEvent.VK_NUMPAD1 && key_code <= KeyEvent.VK_NUMPAD9) {
                    int digit = key_code - KeyEvent.VK_NUMPAD0;
                    handleNumberInput(digit);
                }
                else if (key_code == KeyEvent.VK_DELETE || key_code == KeyEvent.VK_BACK_SPACE) {
                    handleClearCell();
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();

        setMinimumSize(new Dimension(CommonConstants.MIN_HOMEPAGE_WIDTH, CommonConstants.MIN_HOMEPAGE_HEIGHT));
        setSize(new Dimension(CommonConstants.HOMEPAGE_WIDTH, CommonConstants.HOMEPAGE_HEIGHT));
        setLocationRelativeTo(null);

        JPanel main_panel = new JPanel(new BorderLayout());
        main_panel.setBackground(CommonConstants.BACKGROUND);

        JPanel left_sidebar = new SideBarPanel(this, interactive_controller);
        //JPanel left_sidebar = createLeftSidebar();
        main_panel.add(left_sidebar, BorderLayout.WEST);

        JPanel center_panel = new JPanel();
        center_panel.setBackground(CommonConstants.BACKGROUND);
        center_panel.setLayout(new BorderLayout(15, 15));
        center_panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel top_section = createTopSection();
        center_panel.add(top_section, BorderLayout.NORTH);

        JPanel center_section = createCenterSection();
        center_panel.add(center_section, BorderLayout.CENTER);

        JPanel bottom_section = createBottomSection();
        center_panel.add(bottom_section, BorderLayout.SOUTH);

        main_panel.add(center_panel, BorderLayout.CENTER);

        add(main_panel);
        pack();
        setVisible(true);
        requestFocusInWindow();
    }

    private JPanel createTopSection() {
        JPanel top_panel = new JPanel();
        top_panel.setLayout(new BorderLayout());
        top_panel.setBackground(CommonConstants.BACKGROUND);
        top_panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JPanel difficulty_panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        difficulty_panel.setBackground(CommonConstants.BACKGROUND);

        difficulty_label = new JLabel();
        difficulty_label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        updateDifficultyLabel();                       // set initial text & colour

        difficulty_panel.add(difficulty_label);

        JPanel stats_panel = createStatsPanel();

        top_panel.add(difficulty_panel, BorderLayout.CENTER);
        top_panel.add(stats_panel, BorderLayout.EAST);

        return top_panel;
    }


    private JPanel createStatsPanel() {
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 0));
        stats.setBackground(CommonConstants.BACKGROUND);

        JLabel best_time_icon = new JLabel("Best Time");
        best_time_icon.setFont(new Font("Segoe UI", Font.TRUETYPE_FONT, 15));
        best_time_icon.setForeground(CommonConstants.TEXT_COLOR_SEC);

        JLabel best_value = new JLabel("04:32");
        best_value.setFont(new Font("Segoe UI", Font.ROMAN_BASELINE, 20));
        best_value.setForeground(new Color(255, 152, 0));

        JLabel mistake_title = new JLabel("Mistakes");
        mistake_title.setFont(new Font("Segoe UI", Font.TRUETYPE_FONT, 15));
        mistake_title.setForeground(CommonConstants.TEXT_COLOR_SEC);

        mistake_label = new JLabel("0/3");
        mistake_label.setFont(new Font("Segoe UI", Font.ROMAN_BASELINE, 20));
        mistake_label.setForeground(new Color(244, 67, 54));

        JLabel timer_title = new JLabel("Time");
        timer_title.setFont(new Font("Segoe UI", Font.TRUETYPE_FONT, 15));
        timer_title.setForeground(CommonConstants.TEXT_COLOR_SEC);

        timer_label = new JLabel("00:00");
        timer_label.setFont(new Font("Segoe UI", Font.ROMAN_BASELINE, 20));
        timer_label.setForeground(new Color(33, 150, 243));

        game_timer = new Timer(1000, e -> updateTimer());

        JPanel best_group = new JPanel();
        best_group.setLayout(new BoxLayout(best_group, BoxLayout.Y_AXIS));
        best_group.setBackground(CommonConstants.BACKGROUND);
        best_time_icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        best_value.setAlignmentX(Component.CENTER_ALIGNMENT);
        best_group.add(best_time_icon);
        best_group.add(Box.createVerticalStrut(3));
        best_group.add(best_value);

        JPanel mistake_group = new JPanel();
        mistake_group.setLayout(new BoxLayout(mistake_group, BoxLayout.Y_AXIS));
        mistake_group.setBackground(CommonConstants.BACKGROUND);
        mistake_title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mistake_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        mistake_group.add(mistake_title);
        mistake_group.add(Box.createVerticalStrut(3));
        mistake_group.add(mistake_label);

        JPanel timer_group = new JPanel();
        timer_group.setLayout(new BoxLayout(timer_group, BoxLayout.Y_AXIS));
        timer_group.setBackground(CommonConstants.BACKGROUND);

        timer_title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Timer value and button container
        JPanel timer_with_button = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        timer_with_button.setBackground(CommonConstants.BACKGROUND);

        timer_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create pause/resume button
        pause_resume_btn = createPauseResumeButton();

        timer_with_button.add(timer_label);
        timer_with_button.add(pause_resume_btn);

        timer_group.add(timer_title);
        timer_group.add(Box.createVerticalStrut(3));
        timer_group.add(timer_with_button);

        stats.add(best_group);
        stats.add(mistake_group);
        stats.add(timer_group);

        return stats;
    }

    private JPanel createCenterSection() {
        JPanel center = new JPanel(new BorderLayout(25, 0));
        center.setBackground(CommonConstants.BACKGROUND);

        gridPanelContainer = new JPanel();
        gridPanelContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
        gridPanelContainer.setBackground(CommonConstants.BACKGROUND);
        refreshGridPanel();

        center.add(gridPanelContainer, BorderLayout.CENTER);

        JPanel right_panel = createRightPanel();
        center.add(right_panel, BorderLayout.EAST);

        return center;
    }

    private JPanel createRightPanel() {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(CommonConstants.BACKGROUND);
        right.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel action_buttons = new JPanel(new GridLayout(1, 3, 4, 0));
        action_buttons.setBackground(CommonConstants.BACKGROUND);
        action_buttons.setMaximumSize(new Dimension(210, 70));
        action_buttons.setPreferredSize(new Dimension(210, 70));

        JButton undo_btn = createUndoButton();
        JButton erase_btn = createEraseButton();
        hints_btn = createHintButton();

        action_buttons.add(undo_btn);
        action_buttons.add(erase_btn);
        action_buttons.add(hints_btn);


        JLabel select_label = new JLabel("Select Number");
        select_label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        select_label.setForeground(CommonConstants.TEXT_COLOR);
        select_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel number_panel = createNumberPanel();

        right.add(action_buttons);
        right.add(Box.createVerticalStrut(25));
        right.add(select_label);
        right.add(Box.createVerticalStrut(10));
        right.add(number_panel);


        right.add(Box.createVerticalStrut(16));
        JPanel rightButtons = createRightButtonsPanel();
        rightButtons.setAlignmentX(Component.CENTER_ALIGNMENT);
        right.add(rightButtons);

        right.add(Box.createVerticalGlue());

        return right;
    }


    private JPanel createRightButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(CommonConstants.BACKGROUND);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        buttonsPanel.setMaximumSize(new Dimension(220, 320));
        buttonsPanel.setPreferredSize(new Dimension(220, 320));

        Dimension btnSize = new Dimension(180, 48);


        JButton solveButton = createStyledRightButton("Solve", btnSize);
        JButton newGameButton = createStyledRightButton("New Game", btnSize);
        JButton customPuzzleButton = createStyledRightButton("Custom", btnSize);
        JButton saveGameButton = createStyledRightButton("Save Game", btnSize);


        solveButton.addActionListener(e -> {
            if (solveHandler != null) solveHandler.handleSolve();
        });

        newGameButton.addActionListener(e -> showConfirmationDialog());
        customPuzzleButton.addActionListener(e -> makeUserInputSudokuDialog(HomePageTest.this));
        saveGameButton.addActionListener(e -> saveGameDialog(HomePageTest.this));


        buttonsPanel.add(solveButton);
        buttonsPanel.add(Box.createVerticalStrut(12));
        buttonsPanel.add(newGameButton);
        buttonsPanel.add(Box.createVerticalStrut(12));
        buttonsPanel.add(customPuzzleButton);
        buttonsPanel.add(Box.createVerticalStrut(12));
        buttonsPanel.add(saveGameButton);

        return buttonsPanel;
    }

    private JButton createStyledRightButton(String text, Dimension size) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor;
                if (getModel().isPressed()) {
                    bgColor = new Color(200, 210, 220);
                } else if (getModel().isRollover()) {
                    bgColor = new Color(230, 235, 240);
                } else {
                    bgColor = CommonConstants.SECONDARY_COLOR;
                }


                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);


                g2.setColor(new Color(220, 225, 230));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setForeground(CommonConstants.PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        return button;
    }

    private JPanel createBottomSection() {

        JPanel bottom = new JPanel();
        bottom.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
        bottom.setBackground(CommonConstants.BACKGROUND);
        bottom.setPreferredSize(new Dimension(0, 24));
        return bottom;
    }




    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(CommonConstants.PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }


    private JButton createUndoButton() {
        JButton undoButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                Color bgColor;
                Color textColor;
                if (getModel().isPressed()) {
                    bgColor = new Color(70, 130, 180);
                    textColor = Color.WHITE;
                } else if (getModel().isRollover()) {
                    bgColor = new Color(100, 149, 237);
                    textColor = Color.WHITE;
                } else {
                    bgColor = CommonConstants.SECONDARY_COLOR;
                    textColor = CommonConstants.PRIMARY_COLOR;
                }

                g2.setColor(bgColor);
                g2.fill(new Ellipse2D.Double(2, 2, getWidth() - 4, getHeight() - 4));

                g2.setColor(new Color(0, 0, 0, 30));
                g2.setStroke(new BasicStroke(2));
                g2.draw(new Ellipse2D.Double(2, 2, getWidth() - 4, getHeight() - 4));

                // Draw undo symbol
                g2.setColor(textColor);
                Font font = new Font("Segoe UI Symbol", Font.TRUETYPE_FONT, 45);
                g2.setFont(font);

                String undoSymbol = "⟲";
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(undoSymbol);
                int textHeight = fm.getAscent();

                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + textHeight) / 2 - 10 ;

                g2.drawString(undoSymbol, x, y);

                g2.dispose();
            }
        };

        undoButton.setPreferredSize(new Dimension(60, 60));
        undoButton.setContentAreaFilled(false);
        undoButton.setBorderPainted(false);
        undoButton.setFocusPainted(false);
        undoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        undoButton.setToolTipText("Undo Selected Cell");

        undoButton.addActionListener(e -> handleUndo());

        return undoButton;
    }

    private JButton createEraseButton() {
        JButton eraseButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor;
                if (getModel().isPressed()) {
                    bgColor = new Color(70, 130, 180);
                } else if (getModel().isRollover()) {
                    bgColor = new Color(100, 149, 237);
                } else {
                    bgColor = CommonConstants.SECONDARY_COLOR;
                }

                g2.setColor(bgColor);
                g2.fill(new Ellipse2D.Double(2, 2, getWidth() - 4, getHeight() - 4));

                g2.setColor(new Color(0, 0, 0, 30));
                g2.setStroke(new BasicStroke(2));
                g2.draw(new Ellipse2D.Double(2, 2, getWidth() - 4, getHeight() - 4));

                int w = getWidth();
                int h = getHeight();
                int eraserWidth = (int) (w * 0.5);
                int eraserHeight = (int) (h * 0.28);

                int x = (w - eraserWidth) / 2;
                int y = (h - eraserHeight) / 2 + 2;

                g2.setColor(new Color(250, 250, 250));
                g2.fillRoundRect(x, y, eraserWidth, eraserHeight, 6, 6);

                int tipHeight = eraserHeight / 3;
                g2.setColor(new Color(255, 182, 193));
                g2.fillRoundRect(x, y, eraserWidth, tipHeight, 6, 6);

                g2.setColor(new Color(180, 180, 190));
                g2.drawRoundRect(x, y, eraserWidth, eraserHeight, 6, 6);


                g2.dispose();
            }
        };

        eraseButton.setPreferredSize(new Dimension(60, 60));
        eraseButton.setContentAreaFilled(false);
        eraseButton.setBorderPainted(false);
        eraseButton.setFocusPainted(false);
        eraseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eraseButton.setToolTipText("Erase selected cell");
        eraseButton.addActionListener(e -> handleClearCell());

        return eraseButton;
    }


    private JButton createHintButton() {
        JButton hintButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw circular background
                Color bgColor;
                if (getModel().isPressed()) {
                    bgColor = new Color(255, 179, 0);
                } else if (getModel().isRollover()) {
                    bgColor = new Color(255, 220, 0);
                } else {
                    bgColor = CommonConstants.SECONDARY_COLOR;
                }

                g2.setColor(bgColor);
                g2.fill(new Ellipse2D.Double(2, 2, getWidth() - 4, getHeight() - 4));

                g2.setColor(new Color(0, 0, 0, 30));
                g2.setStroke(new BasicStroke(2));
                g2.draw(new Ellipse2D.Double(2, 2, getWidth() - 4, getHeight() - 4));

                // Draw lightbulb icon
                drawLightbulb(g2, getWidth() / 2, getHeight() / 2);

                // Draw counter badge in top-right corner
                int hints = interactive_controller.getHintsRemaining();
                drawCounterBadge(g2, getWidth(), hints);

                g2.dispose();
            }

            private void drawLightbulb(Graphics2D g2, int cx, int cy) {
                g2.setColor(CommonConstants.PRIMARY_COLOR);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int bulbRadius = 10;
                g2.draw(new Ellipse2D.Double(cx - bulbRadius, cy - bulbRadius - 3, bulbRadius * 2, bulbRadius * 2));

                // Base
                int baseWidth = 8;
                g2.drawLine(cx - baseWidth/2, cy + bulbRadius - 3, cx + baseWidth/2, cy + bulbRadius - 3);
                g2.drawLine(cx - baseWidth/2, cy + bulbRadius, cx + baseWidth/2, cy + bulbRadius);
                g2.drawLine(cx - baseWidth/2, cy + bulbRadius + 3, cx + baseWidth/2, cy + bulbRadius + 3);

                // Filament
                g2.setStroke(new BasicStroke(1.5f));
                Path2D filament = new Path2D.Double();
                filament.moveTo(cx - 3, cy - 5);
                filament.lineTo(cx, cy - 8);
                filament.lineTo(cx + 3, cy - 5);
                g2.draw(filament);
            }

            private void drawCounterBadge(Graphics2D g2, int buttonWidth, int count) {
                int badgeSize = 25;
                int badgeX = buttonWidth - badgeSize - 2;
                int badgeY = 2;

                // Draw badge background
                g2.setColor(CommonConstants.PRIMARY_COLOR);
                g2.fill(new Ellipse2D.Double(badgeX, badgeY, badgeSize, badgeSize));

                // Draw badge border
                g2.setColor(CommonConstants.SECONDARY_COLOR);
                g2.setStroke(new BasicStroke(2));
                g2.draw(new Ellipse2D.Double(badgeX, badgeY, badgeSize, badgeSize));

                // Draw count number
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.TRUETYPE_FONT,18));
                FontMetrics fm = g2.getFontMetrics();
                String countStr = String.valueOf(count);
                int textWidth = fm.stringWidth(countStr);
                int textHeight = fm.getAscent();
                int textX = badgeX + (badgeSize - textWidth) / 2 + 2;
                int textY = badgeY + (badgeSize + textHeight) / 2 - 2;
                g2.drawString(countStr, textX, textY);
            }
        };

        hintButton.setPreferredSize(new Dimension(60, 60));
        hintButton.setContentAreaFilled(false);
        hintButton.setBorderPainted(false);
        hintButton.setFocusPainted(false);
        hintButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hintButton.setToolTipText("Get a hint");
        hintButton.addActionListener(e -> {
            if (hintDialog != null && interactive_controller.hasHintsRemaining()) {
                if (interactive_controller.isTimerPaused()) {
                    interactive_controller.resumeTimer();
                    pause_resume_btn.repaint();
                }
                startTimerIfNeeded();

                hintDialog.showHint();
                interactive_controller.decrementHints();
                hintButton.repaint();
                if (victoryDialog != null && victoryDialog.isPuzzleSolved()) {
                    game_timer.stop();  // Stop the timer
                    // Small delay for better UX
                    Timer delayTimer = new Timer(200, ev -> {
                        victoryDialog.showVictoryDialog();
                    });
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                }
            } else if (!interactive_controller.hasHintsRemaining()) {
                JOptionPane.showMessageDialog(HomePageTest.this,
                        "No hints remaining for this game!",
                        "Hints Exhausted",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return hintButton;
    }

    private JButton createPauseResumeButton() {
        JButton pauseResumeButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor;
                Color textColor;
                if (getModel().isPressed()) {
                    bgColor = new Color(70, 130, 180);
                    textColor = Color.WHITE;
                } else if (getModel().isRollover()) {
                    bgColor = new Color(100, 149, 237);
                    textColor = Color.WHITE;
                } else {
                    bgColor = CommonConstants.SECONDARY_COLOR;
                    textColor = CommonConstants.PRIMARY_COLOR;
                }

                g2.setColor(bgColor);
                g2.fill(new Ellipse2D.Double(2, 2, getWidth() - 4, getHeight() - 4));

                g2.setColor(new Color(0, 0, 0, 30));
                g2.setStroke(new BasicStroke(2));
                g2.draw(new Ellipse2D.Double(2, 2, getWidth() - 4, getHeight() - 4));

                // Draw pause/play icon based on state
                g2.setColor(textColor);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;

                if (interactive_controller.isTimerPaused()) {
                    drawPlayIcon(g2, centerX, centerY);
                } else {
                    drawPauseIcon(g2, centerX, centerY);
                }

                g2.dispose();
            }

            private void drawPauseIcon(Graphics2D g2, int cx, int cy) {
                g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int barWidth = 2;
                int barHeight = 10;
                int spacing = 2;

                // Left bar
                g2.fillRect(cx - spacing - barWidth, cy - barHeight/2, barWidth, barHeight);
                // Right bar
                g2.fillRect(cx + spacing, cy - barHeight/2, barWidth, barHeight);
            }

            private void drawPlayIcon(Graphics2D g2, int cx, int cy) {
                int[] xPoints = {cx - 4, cx - 4, cx + 10};
                int[] yPoints = {cy - 8, cy + 8, cy};
                g2.fillPolygon(xPoints, yPoints, 3);
            }
        };

        pauseResumeButton.setPreferredSize(new Dimension(30, 30));
        pauseResumeButton.setContentAreaFilled(false);
        pauseResumeButton.setBorderPainted(false);
        pauseResumeButton.setFocusPainted(false);
        pauseResumeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pauseResumeButton.setToolTipText("Pause/Resume Timer");

        pauseResumeButton.addActionListener(e -> {
            if (interactive_controller.isTimerStarted()) {
                if (interactive_controller.isTimerPaused()) {
                    interactive_controller.resumeTimer();
                    pauseResumeButton.setToolTipText("Pause Timer");
                } else {
                    interactive_controller.pauseTimer();
                    pauseResumeButton.setToolTipText("Resume Timer");
                }
                pauseResumeButton.repaint();
            }
        });

        return pauseResumeButton;
    }


    private void refreshGridPanel(){
        gridPanelContainer.removeAll();
        gridPanelContainer.add(createGridPanel());
        gridPanelContainer.revalidate();
        gridPanelContainer.repaint();
        interactive_controller.resetTimer();
        timer_label.setText("00:00");
        game_timer.stop();
        if (currentBoard != null && interactive_controller != null) {
            victoryDialog = new VictoryDialog(this, interactive_controller, currentBoard);}
        if (currentBoard != null && interactive_controller != null && solveHandler != null) {
            solveHandler.updateReferences(currentBoard, gridButtons, victoryDialog);
        }
    }

    private JPanel createGridPanel() {
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(9, 9, 1, 1));
        gridPanel.setBackground(CommonConstants.BACKGROUND);
        gridPanel.setBorder(BorderFactory.createLineBorder(CommonConstants.TEXT_COLOR, 3));

        currentBoard = new SudokuBoard9By9();

        interactive_controller.setSudokuBoard(currentBoard);
        interactive_controller.resetGame();

        // Use the difficulty selected in the confirmation dialog
        currentBoard.generatePuzzle(currentDifficulty);
        int[][] board = currentBoard.getBoard();


        interactive_controller.initializeBoard(board);

        gridButtons = new JButton[9][9];

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                JButton button = new JButton();
                button.setFont(new Font("Segoe UI", Font.TRUETYPE_FONT, 20));
                button.setBackground(CommonConstants.BACKGROUND);
                button.setFocusPainted(false);
                button.setPreferredSize(new Dimension(55, 55));

                if (board[row][col] != 0) {
                    button.setText(String.valueOf(board[row][col]));
                    button.setForeground(CommonConstants.TEXT_COLOR);
                    button.setFont(new Font("Segoe UI", Font.TRUETYPE_FONT, 30));
                } else {
                    button.setText("");
                }

                final int finalRow = row;
                final int finalCol = col;
                button.addActionListener(e -> handleCellClick(finalRow, finalCol));

                gridButtons[row][col] = button;

                button.setBorder(BorderFactory.createMatteBorder(
                        (finalRow % 3 == 0) ? 3 : 1,
                        (finalCol % 3 == 0) ? 3 : 1,
                        (finalRow == 8) ? 3 : 0,
                        (finalCol == 8) ? 3 : 0,
                        CommonConstants.TEXT_COLOR
                ));


                gridPanel.add(button);
            }
        }

        gridPanel.setPreferredSize(new Dimension(495, 495));

        hintDialog = new HintDialog(this, currentBoard, gridButtons);
        victoryDialog = new VictoryDialog(this, interactive_controller, currentBoard);
        solveHandler = new SolveHandler(this,interactive_controller,currentBoard,gridButtons,victoryDialog,timer_label,game_timer,pause_resume_btn);

        return gridPanel;
    }

    private void handleCellClick(int row, int col) {
        if (interactive_controller.isTimerPaused() ) {
            interactive_controller.resumeTimer();
            pause_resume_btn.repaint();
        }
        startTimerIfNeeded();

        boolean can_edit = interactive_controller.selectCell(row, col);

        if (!can_edit) {
            interactive_controller.selectCellForViewing(row, col);
        }
        interactive_controller.clearConflictingCells();
        updateCellVisuals();
        requestFocusInWindow();
    }

    public void updateCellVisuals() {
        int selected_row = interactive_controller.getSelectedRow();
        int selected_col = interactive_controller.getSelectedCol();

        int selected_number = -1;
        if (selected_row != -1 && selected_col != -1) {
            selected_number = interactive_controller.getCellValue(selected_row, selected_col);
        }

        List<int[]> conflicts = interactive_controller.getConflictingCells();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                JButton button = gridButtons[row][col];

                Color background = Color.WHITE;

                boolean is_conflicting = false;
                for (int[] conflict : conflicts) {
                    if (conflict[0] == row && conflict[1] == col) {
                        is_conflicting = true;
                        break;
                    }
                }

                if (is_conflicting) {
                    background = CommonConstants.PRIMARY_COLOR;
                }

                if (selected_row != -1 && selected_col != -1) {
                    boolean same_row = (row == selected_row);
                    boolean same_col = (col == selected_col);
                    boolean same_box = (row / 3 == selected_row / 3) && (col / 3 == selected_col / 3);

                    if (selected_number > 0 &&
                            interactive_controller.getCellValue(row, col) == selected_number &&
                            !is_conflicting) {
                        background = CommonConstants.SECONDARY_COLOR;
                    }

                    if ((same_row || same_col || same_box) && !is_conflicting) {
                        background = CommonConstants.SECONDARY_COLOR;
                    }

                    if (row == selected_row && col == selected_col) {
                        background = CommonConstants.SECONDARY_COLOR;
                    }
                }

                button.setBackground(background);

                if (row == selected_row && col == selected_col) {
                    button.setBorder(BorderFactory.createLineBorder(new Color(66, 133, 244), 3));
                } else{
                    button.setBorder(BorderFactory.createMatteBorder(
                            (row % 3 == 0) ? 3 : 1,
                            (col % 3 == 0) ? 3 : 1,
                            (row == 8) ? 3 : 0,
                            (col == 8) ? 3 : 0,
                            CommonConstants.TEXT_COLOR
                    ));
                }
            }
        }
    }

    private JPanel createNumberPanel() {
        JPanel number_panel = new JPanel();
        number_panel.setLayout(new GridLayout(3, 3, 10, 10));
        number_panel.setBackground(CommonConstants.BACKGROUND);
        number_panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        number_panel.setMaximumSize(new Dimension(250, 250));
        number_panel.setPreferredSize(new Dimension(250, 250));

        for (int digit = 1; digit <= 9; digit++) {
            JButton number_button = new JButton(String.valueOf(digit)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    Color bgColor;
                    if (getModel().isPressed()) {
                        bgColor = new Color(200, 210, 220);
                    } else if (getModel().isRollover()) {
                        bgColor = new Color(230, 235, 240);
                    } else {
                        bgColor = CommonConstants.SECONDARY_COLOR;
                    }

                    // Draw rounded background
                    g2.setColor(bgColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                    // Draw border
                    g2.setColor(new Color(220, 225, 230));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                    g2.dispose();
                    super.paintComponent(g);
                }
            };

            number_button.setFont(new Font("Segoe UI", Font.TRUETYPE_FONT, 40));
            number_button.setForeground(CommonConstants.PRIMARY_COLOR);
            number_button.setFocusPainted(false);
            number_button.setBorderPainted(false);
            number_button.setContentAreaFilled(false);
            number_button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            final int final_digit = digit;
            number_button.addActionListener(e -> handleNumberInput(final_digit));

            number_panel.add(number_button);
        }

        return number_panel;
    }

    private void handleNumberInput(int digit) {
        int row = interactive_controller.getSelectedRow();
        int col = interactive_controller.getSelectedCol();
        int previousValue = -1;
        Color previousColor = null;

        if (row != -1 && col != -1) {
            previousValue = interactive_controller.getCellValue(row, col);
            previousColor = gridButtons[row][col].getForeground();
        }

        boolean entered = interactive_controller.enterNumber(digit);

        if (entered) {
            startTimerIfNeeded();
            row = interactive_controller.getSelectedRow();
            col = interactive_controller.getSelectedCol();

            boolean is_valid = interactive_controller.isValidPlacement(row, col, digit);

            // Record move only if we actually changed something
            if (row != -1 && col != -1 && previousValue != digit) {
                move_history.push(new Move(row, col, previousValue, digit, previousColor));
            }

            gridButtons[row][col].setText(String.valueOf(digit));
            currentBoard.getBoard()[row][col] = digit;

            if (!is_valid) {
                interactive_controller.incrementMistakes();
                gridButtons[row][col].setForeground(Color.RED);

                List<int[]> conflicts = interactive_controller.findConflictingCells(row, col, digit);
                interactive_controller.setConflictingCells(conflicts);

                updateMistakeDisplay();
                updateCellVisuals();

                if (interactive_controller.isGameOver()) {
                    showGameOverDialog();
                }
            } else {
                interactive_controller.clearConflictingCells();

                if (victoryDialog != null && victoryDialog.isPuzzleSolved()) {
                    game_timer.stop();  // Stop the timer
                    // Small delay for better UX
                    Timer delayTimer = new Timer(200, e -> {
                        victoryDialog.showVictoryDialog();
                    });
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                }

            }
        } else {
            System.out.println("Cannot enter number: no cell selected");
        }
    }

    private void handleClearCell() {
        int row = interactive_controller.getSelectedRow();
        int col = interactive_controller.getSelectedCol();

        if (row != -1 && col != -1) {
            int original_value = currentBoard.getBoard()[row][col];
            if (original_value == 0) {
                int currentValue = interactive_controller.getCellValue(row, col);
                Color previousColor = gridButtons[row][col].getForeground();

                if (currentValue != 0) {
                    // record this as a move
                    move_history.push(new Move(row, col, currentValue, 0, previousColor));
                }

                interactive_controller.clearSelectedCell();
                gridButtons[row][col].setText("");
                gridButtons[row][col].setForeground(Color.BLACK);
                currentBoard.getBoard()[row][col] = 0;
            }
        }
    }

    private void handleUndo() {
        int[] pos = interactive_controller.undoLastMove();
        if (pos == null) {
            return;
        }

        int row = pos[0];
        int col = pos[1];

        int original_value = currentBoard.getBoard()[row][col];
        int current_value = interactive_controller.getCellValue(row, col);

        if (original_value != 0) {
            gridButtons[row][col].setText(String.valueOf(original_value));
            gridButtons[row][col].setForeground(Color.BLACK);
        } else if (current_value != 0) {
            gridButtons[row][col].setText(String.valueOf(current_value));
            gridButtons[row][col].setForeground(new Color(0, 0, 255));
        } else {
            gridButtons[row][col].setText("");
            gridButtons[row][col].setForeground(Color.BLACK);
        }

        updateCellVisuals();
        currentBoard.getBoard()[row][col] = current_value;
    }


    private void showGameOverDialog() {
        JDialog dialog = new JDialog(this, "Game Over", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel main_panel = new JPanel();
        main_panel.setBackground(new Color(173, 210, 230));
        main_panel.setLayout(new BorderLayout(20, 20));
        main_panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel message_panel = new JPanel();
        message_panel.setBackground(new Color(173, 210, 230));
        message_panel.setLayout(new BoxLayout(message_panel, BoxLayout.Y_AXIS));

        JLabel title_label = new JLabel("Game Over");
        title_label.setFont(new Font("Arial", Font.BOLD, 24));
        title_label.setForeground(Color.RED);
        title_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel message_label = new JLabel("You have made 3 mistakes and lost this game");
        message_label.setFont(new Font("Arial", Font.PLAIN, 14));
        message_label.setForeground(Color.BLACK);
        message_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        message_panel.add(title_label);
        message_panel.add(Box.createVerticalStrut(10));
        message_panel.add(message_label);

        JPanel button_panel = new JPanel();
        button_panel.setBackground(new Color(173, 210, 230));
        button_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton new_game_button = new JButton("Start New Game");
        new_game_button.setFont(new Font("Arial", Font.BOLD, 14));
        new_game_button.setBackground(CommonConstants.PRIMARY_COLOR);
        new_game_button.setForeground(Color.WHITE);
        new_game_button.setFocusPainted(false);
        new_game_button.setPreferredSize(new Dimension(180, 40));
        new_game_button.addActionListener(e -> {
            move_history.clear();
            interactive_controller.resetMistakes();
            refreshGridPanel();
            updateMistakeDisplay();
            dialog.dispose();
        });

        button_panel.add(new_game_button);

        main_panel.add(message_panel, BorderLayout.CENTER);
        main_panel.add(button_panel, BorderLayout.SOUTH);

        dialog.add(main_panel);
        dialog.setVisible(true);
    }

    private void updateTimer() {
        if (interactive_controller.isTimerStarted()) {
            interactive_controller.incrementTimer();
            int seconds = interactive_controller.getElapsedSeconds();
            int minutes = seconds / 60;
            int secs = seconds % 60;
            timer_label.setText(String.format("%02d:%02d", minutes, secs));
        }
    }

    private void startTimerIfNeeded() {
        if (!interactive_controller.isTimerStarted()) {
            interactive_controller.startTimer();
            game_timer.start();
        }
    }

    private void saveGameDialog(JFrame frame) {
        JDialog dialog = new JDialog(frame, "Save Game", true);
        dialog.setBackground(CommonConstants.BACKGROUND);
        dialog.setLayout(new BorderLayout());

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Label
        JLabel label = new JLabel("Enter game name:");
        label.setFont(new Font("Segoe UI", Font.TRUETYPE_FONT, 20));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text field
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setMaximumSize(new Dimension(300, 35));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(300, 40));

        JButton saveButton = createButton("Save");
        JButton cancelButton = createButton("Cancel");
        cancelButton.setBackground(new Color(158, 158, 158));

        // Save button action
        saveButton.addActionListener(e -> {
            String gameName = textField.getText().trim();

            if (gameName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a game name!",
                        "Invalid Name",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            SudokuBoard9By9.Difficulty difficulty = currentBoard.getBoardDifficulty();

            // Check if game already exists
            if (saveManager.gameExists(gameName, difficulty)){
                int choice = JOptionPane.showConfirmDialog(dialog,
                        "A game with this name already exists. Overwrite?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION);

                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            boolean[][] preFilledCells = interactive_controller.getPre_filled_cells();

            // Save the game
            boolean success = saveManager.saveGame(
                    gameName,
                    currentBoard.getBoard(),
                    preFilledCells,
                    interactive_controller.getMistakeCount(),
                    interactive_controller.getElapsedSeconds(),
                    interactive_controller.getHintsRemaining(),
                    difficulty
            );

            if (success) {
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to save game. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Add components with spacing
        mainPanel.add(label);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(textField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel, BorderLayout.CENTER); // Add to the center of the JDialog
        dialog.pack();
        // Use setPreferredSize instead of setMinimumSize when using pack for better results
        // dialog.setPreferredSize(new Dimension(400, 180));
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void makeUserInputSudokuDialog(JFrame frame){
        JDialog dialog = new JDialog(frame, "Enter The Sudoku:", true);
        dialog.setLayout(new BorderLayout());
        JTextField[][] text_fields = new JTextField[9][9];
        JPanel panel = new JPanel(new GridLayout(9, 9));

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                JTextField text_field = new JTextField();
                DigitInputValidation(text_field);
                text_field.setHorizontalAlignment(JTextField.CENTER);
                text_field.setFont(new Font("Arial", Font.BOLD, 20));
                text_field.setPreferredSize(new Dimension(40, 40));
                text_field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                text_field.setBackground(new Color(245, 245, 245));
                int topBorder = (y % 3 == 0 && y != 0) ? 3 : 1;
                int leftBorder = (x % 3 == 0 && x != 0) ? 3 : 1;
                int bottomBorder = 1;
                int rightBorder = 1;
                text_field.setBorder(
                        BorderFactory.createMatteBorder(topBorder, leftBorder, bottomBorder, rightBorder, Color.BLACK)
                );
                text_fields[y][x] = text_field;
                panel.add(text_field);
            }
        }
        dialog.add(panel, BorderLayout.CENTER);

        JButton enter_button = createButton("Enter");
        enter_button.addActionListener(e -> {
            getUserInput(text_fields);
            SudokuBoard9By9 tempBoard = new SudokuBoard9By9();
            tempBoard.setBoard(sudoku_board);
            int numberOfFilledCells = PuzzleDifficultyRater.countClues(tempBoard);

            if (numberOfFilledCells < 15) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "You must enter at least 15 numbers.",
                        "Not Enough Clues",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            if (!tempBoard.isValidBoard()) {
                JOptionPane.showMessageDialog(dialog,
                        "Invalid Sudoku",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            components.PuzzleDifficultyRater.Difficulty customDifficulty = components.PuzzleDifficultyRater.ratePuzzle(tempBoard);
            JOptionPane.showMessageDialog(dialog,
                    "Puzzle Rated: " + customDifficulty.name(),
                    "Difficulty",
                    JOptionPane.INFORMATION_MESSAGE);
            applyCustomBoardToMainGrid();
            currentBoard.setBoard(sudoku_board);
            interactive_controller.resetGame();
            interactive_controller.initializeBoard(sudoku_board);
            updateCellVisuals();
            hintDialog = new HintDialog(HomePageTest.this, currentBoard, gridButtons);
            victoryDialog = new VictoryDialog(HomePageTest.this, interactive_controller, currentBoard);
            if (solveHandler != null) {
                solveHandler.updateReferences(currentBoard, gridButtons, victoryDialog);
            }
            dialog.dispose();
        });

        dialog.add(enter_button, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    public static void getUserInput(JTextField[][] fields) {
        for (int y = 0; y < sudoku_board.length; y++) {
            for (int x = 0; x < sudoku_board[y].length; x++) {
                String text = fields[y][x].getText();
                if (text.isEmpty())
                    sudoku_board[y][x] = 0;
                else
                    sudoku_board[y][x] = Integer.parseInt(text);
            }
        }
    }

    public void DigitInputValidation(JTextField field) {
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (c == '\b') {
                    return;
                }
                if (c < '1' || c > '9') {
                    e.consume();
                }
                if (field.getText().length() >= 1) {
                    e.consume();
                }
            }
        });
    }

    public void applyCustomBoardToMainGrid() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = sudoku_board[row][col];
                JButton cell = gridButtons[row][col];
                if (value == 0) {
                    cell.setText("");
                    cell.setFont(new Font("Segoe UI", Font.TRUETYPE_FONT, 20));
                } else {
                    cell.setText(String.valueOf(value));
                    cell.setForeground(CommonConstants.TEXT_COLOR);
                    cell.setFont(new Font("Segoe UI", Font.TRUETYPE_FONT, 30));
                }
            }
        }
    }

    public void setGridButtonsForTest(JButton[][] buttons) {
        this.gridButtons = buttons;
    }

    private void showConfirmationDialog() {
        JDialog dialog = new JDialog(this, "Generate New Puzzle", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(CommonConstants.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Message + difficulty selection
        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(CommonConstants.BACKGROUND);
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Generate New Puzzle");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(CommonConstants.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel("Are you sure you want to generate a new puzzle?");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(CommonConstants.PRIMARY_COLOR);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel difficultyTextLabel = new JLabel("Select difficulty:");
        difficultyTextLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        difficultyTextLabel.setForeground(CommonConstants.TEXT_COLOR);
        difficultyTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<SudokuBoard9By9.Difficulty> difficultyCombo =
                new JComboBox<>(SudokuBoard9By9.Difficulty.values());
        difficultyCombo.setSelectedItem(currentDifficulty);
        difficultyCombo.setMaximumSize(new Dimension(200, 30));
        difficultyCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

        messagePanel.add(titleLabel);
        messagePanel.add(Box.createVerticalStrut(10));
        messagePanel.add(messageLabel);
        messagePanel.add(Box.createVerticalStrut(15));
        messagePanel.add(difficultyTextLabel);
        messagePanel.add(Box.createVerticalStrut(5));
        messagePanel.add(difficultyCombo);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(CommonConstants.BACKGROUND);

        JButton yesButton = new JButton("YES, Generate New");
        yesButton.setFont(new Font("Arial", Font.BOLD, 12));
        yesButton.setBackground(CommonConstants.PRIMARY_COLOR);
        yesButton.setForeground(CommonConstants.TEXT_COLOR);
        yesButton.setFocusPainted(false);
        yesButton.setPreferredSize(new Dimension(160, 40));
        yesButton.setBorder(BorderFactory.createRaisedBevelBorder());
        yesButton.addActionListener(e -> {
            // Update difficulty from dropdown
            currentDifficulty = (SudokuBoard9By9.Difficulty) difficultyCombo.getSelectedItem();
            updateDifficultyLabel();

            // Existing behaviour
            hints_btn.repaint();
            refreshGridPanel();
            interactive_controller.resetTimer();
            timer_label.setText("00:00");
            game_timer.stop();
            mistake_label.setText("0/3");

            dialog.dispose();
        });

        JButton noButton = new JButton("NO, Cancel");
        noButton.setFont(new Font("Arial", Font.BOLD, 12));
        noButton.setBackground(CommonConstants.SECONDARY_COLOR);
        noButton.setForeground(CommonConstants.TEXT_COLOR);
        noButton.setFocusPainted(false);
        noButton.setPreferredSize(new Dimension(150, 40));
        noButton.setBorder(BorderFactory.createRaisedBevelBorder());
        noButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        mainPanel.add(messagePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private void updateDifficultyLabel() {
        if (difficulty_label == null) {
            return;
        }

        String text;
        Color color;

        switch (currentDifficulty) {
            case EASY -> {
                text = "EASY";
                color = new Color(0, 153, 0);
            }
            case MEDIUM -> {
                text = "MEDIUM";
                color = new Color(0, 102, 204);
            }
            case HARD -> {
                text = "HARD";
                color = new Color(204, 0, 0);
            }
            default -> {
                text = "EASY";
                color = Color.BLACK;
            }
        }

        difficulty_label.setText("Difficulty: " + text);
        difficulty_label.setForeground(color);
    }

    private void updateMistakeDisplay(){
        int mistakes = interactive_controller.getMistakeCount();

        mistake_label.setText(mistakes +"/3");
    }

    public void setSudoku_board(int[][] sudoku_board){
        this.sudoku_board = sudoku_board;
    }
}