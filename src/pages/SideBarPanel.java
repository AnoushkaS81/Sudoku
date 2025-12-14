package pages;

import components.HintDialog;
import components.SudokuBoard9By9;
import components.VictoryDialog;
import db.GameSaveManager;
import utils.CommonConstants;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SideBarPanel extends JPanel {
    private final JPanel sidebarContainer;
    private final GameSaveManager game_save_manager;
    private final HomePageTest home_page;
    private InteractiveSudokuController interactive_controller;
    private JPanel expandedSidebar;

    public SideBarPanel(HomePageTest home_page, InteractiveSudokuController interactive_controller) {
        game_save_manager = new GameSaveManager();

        this.home_page = home_page;
        this.interactive_controller = interactive_controller;

        setLayout(new BorderLayout());

        sidebarContainer = new JPanel(new CardLayout());

        expandedSidebar = createExpandedSidebar();
        JPanel collapsedSidebar = createCollapsedSidebar();

        sidebarContainer.add(expandedSidebar, "expanded");
        sidebarContainer.add(collapsedSidebar, "collapsed");

        CardLayout cl = (CardLayout) sidebarContainer.getLayout();
        cl.show(sidebarContainer, "collapsed");
        sidebarContainer.setPreferredSize(new Dimension(50, 0));

        add(sidebarContainer, BorderLayout.CENTER);

        addResizeBehavior();
    }

    private JPanel createExpandedSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(CommonConstants.SECONDARY_COLOR);
        sidebar.setPreferredSize(new Dimension(200, 0));

        JButton hamburger_button_close = new JButton();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/cross-icon.png"));
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            hamburger_button_close.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            hamburger_button_close.setText("✖");
        }
        hamburger_button_close.setMaximumSize(new Dimension(40, 40));
        hamburger_button_close.setBackground(CommonConstants.PRIMARY_COLOR);

        hamburger_button_close.addActionListener(e -> collapseSidebar());

        JButton back_button = new JButton("← Back");
        back_button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        back_button.setBackground(CommonConstants.PRIMARY_COLOR);
        back_button.setForeground(CommonConstants.TEXT_COLOR);
        styleButton(back_button);

        back_button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        back_button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back_button.setAlignmentX(Component.LEFT_ALIGNMENT);
        back_button.setMaximumSize(new Dimension(150, 40));

        back_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                back_button.setBackground(new Color(240, 242, 245));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                back_button.setBackground(Color.WHITE);
            }
        });

        back_button.addActionListener(e -> {
            new LandingPage();
            Window window = SwingUtilities.getWindowAncestor(SideBarPanel.this);
            if (window != null) {
                window.dispose();
            }
        });

        JButton how_to_play = createHowToPlayButton();

        addBackExitButtonsToHamburgerMenu(sidebar,back_button,hamburger_button_close);

        addSavedGamesButtons(sidebar);

        sidebar.add(Box.createVerticalStrut(10));

        JPanel howToPlayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        howToPlayPanel.setBackground(CommonConstants.SECONDARY_COLOR);
        howToPlayPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        howToPlayPanel.add(how_to_play);

        sidebar.add(howToPlayPanel);

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }


    private JButton createHowToPlayButton(){
        JButton howToPlay_button = new JButton("How to Play?");
        howToPlay_button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        howToPlay_button.setBackground(Color.WHITE);
        howToPlay_button.setForeground(CommonConstants.PRIMARY_COLOR);
        howToPlay_button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        howToPlay_button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        howToPlay_button.setAlignmentX(Component.LEFT_ALIGNMENT);
        howToPlay_button.setMaximumSize(new Dimension(180, 40));
        howToPlay_button.setOpaque(true);
        howToPlay_button.setBorderPainted(true);
        howToPlay_button.setFocusPainted(false);

        howToPlay_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                howToPlay_button.setBackground(new Color(240, 242, 245));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                howToPlay_button.setBackground(Color.WHITE);
            }
        });

        howToPlay_button.addActionListener(e -> {
            new HowToPlayPage();
            Window window = SwingUtilities.getWindowAncestor(SideBarPanel.this);
            if (window != null) {
                window.dispose();
            }
        });
        return howToPlay_button;
    }

    private void addBackExitButtonsToHamburgerMenu(JPanel sidebar, JButton back_button, JButton hamburger_button_close){
        JPanel topRow = new JPanel();
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        topRow.setBackground(CommonConstants.SECONDARY_COLOR);
        topRow.setAlignmentX(LEFT_ALIGNMENT);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        topRow.add(back_button);
        topRow.add(Box.createHorizontalStrut(10));
        topRow.add(hamburger_button_close);

        sidebar.add(topRow);
    }

    private void addSavedGamesButtons(JPanel sidebar){
        List<String> easy_saved_games = game_save_manager.getGameNames(SudokuBoard9By9.Difficulty.EASY);
        List<String> medium_saved_games = game_save_manager.getGameNames(SudokuBoard9By9.Difficulty.MEDIUM);
        List<String> hard_saved_games = game_save_manager.getGameNames(SudokuBoard9By9.Difficulty.HARD);
        JLabel saved_games_Label = new JLabel("Saved games:");
        sidebar.add(saved_games_Label);
        JLabel easyLabel = new JLabel("Easy:");
        sidebar.add(easyLabel);
        for (int i = 0; i < easy_saved_games.size(); i++) {
            sidebar.add(makeSavedGamesButton(easy_saved_games.get(i),SudokuBoard9By9.Difficulty.EASY));
        }

        JLabel medLabel = new JLabel("Medium:");
        sidebar.add(medLabel);
        for (int i = 0; i < medium_saved_games.size(); i++) {
            sidebar.add(makeSavedGamesButton(medium_saved_games.get(i),SudokuBoard9By9.Difficulty.MEDIUM));
        }

        JLabel hardLabel = new JLabel("Hard:");
        sidebar.add(hardLabel);
        for (int i = 0; i < hard_saved_games.size(); i++) {
            sidebar.add(makeSavedGamesButton(hard_saved_games.get(i),SudokuBoard9By9.Difficulty.HARD));
        }
    }

    private JButton makeSavedGamesButton(String saved_game_name, SudokuBoard9By9.Difficulty difficulty){
        JButton saved_game_button = new JButton(saved_game_name);
        saved_game_button.setBackground(Color.WHITE);
        saved_game_button.setAlignmentX(LEFT_ALIGNMENT);
        saved_game_button.setMaximumSize(new Dimension(180, 30));
        saved_game_button.addActionListener(e -> {
            System.out.println("Loading game : " + saved_game_name);
            GameSaveManager.SavedGameData loaded_game = game_save_manager.loadGame(saved_game_name, difficulty);

            // code to add loaded board to grid
            home_page.setSudoku_board(loaded_game.board);
            home_page.applyCustomBoardToMainGrid();
            home_page.currentBoard.setBoard(loaded_game.board);
            interactive_controller.resetGame();
            interactive_controller.initializeBoard(loaded_game.board);
            interactive_controller.setTimer(loaded_game.elapsedSeconds);
            interactive_controller.setHintsRemaining(loaded_game.hints);
            interactive_controller.setMistake_count(loaded_game.mistakes);
            home_page.updateCellVisuals();
            home_page.hintDialog = new HintDialog(home_page, home_page.currentBoard, home_page.gridButtons);
            home_page.victoryDialog = new VictoryDialog(home_page, interactive_controller, home_page.currentBoard);
            if (home_page.solveHandler != null) {
                home_page.solveHandler.updateReferences(home_page.currentBoard, home_page.gridButtons, home_page.victoryDialog);
            }
            home_page.hints_btn.repaint();
            home_page.mistake_label.setText("0/3");
            home_page.timer_label.setText(loaded_game.getFormattedTime());
            interactive_controller.resetTimer();
            interactive_controller.resetHints();
            interactive_controller.resetMistakes();
        });
        return saved_game_button;
    }


    private JPanel createCollapsedSidebar() {
        JPanel collapsed = new JPanel();
        collapsed.setLayout(new BoxLayout(collapsed, BoxLayout.Y_AXIS));
        collapsed.setBackground(CommonConstants.SECONDARY_COLOR);
        collapsed.setPreferredSize(new Dimension(50, 0));

        JButton hamburger_button = new JButton();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/hamburger-icon.png"));
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            hamburger_button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            hamburger_button.setText("☰");
        }
        styleButton(hamburger_button);

        hamburger_button.addActionListener(e -> expandSidebar());

        collapsed.add(Box.createVerticalStrut(10));
        collapsed.add(hamburger_button);
        collapsed.add(Box.createVerticalGlue());


        return collapsed;
    }

    private void styleButton(JButton button) {
        button.setBackground(CommonConstants.PRIMARY_COLOR);
        button.setMaximumSize(new Dimension(40, 40));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void collapseSidebar() {
        CardLayout cl = (CardLayout) sidebarContainer.getLayout();
        cl.show(sidebarContainer, "collapsed");
        sidebarContainer.setPreferredSize(new Dimension(50, 0));
        revalidate();
        repaint();
    }

    private void expandSidebar() {
        sidebarContainer.remove(expandedSidebar);
        expandedSidebar = createExpandedSidebar();
        sidebarContainer.add(expandedSidebar, "expanded");
        CardLayout cl = (CardLayout) sidebarContainer.getLayout();
        cl.show(sidebarContainer, "expanded");
        sidebarContainer.setPreferredSize(new Dimension(200, 0));
        revalidate();
        repaint();
    }


    private void addResizeBehavior() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    int windowWidth = window.getWidth();
                    if (windowWidth < 1100) {
                        collapseSidebar();
                    } else {
                        expandSidebar();
                    }
                }
            });
        }
    }
}

