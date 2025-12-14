package pages;

import utils.CommonConstants;

import javax.swing.*;
import java.awt.*;

public class LandingPage extends JFrame {

    public LandingPage() {
        setTitle("Sudoku - Landing Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(CommonConstants.HOMEPAGE_WIDTH, CommonConstants.HOMEPAGE_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        JLabel backgroundLabel = new JLabel(new ImageIcon("src/images/landing-page.png"));
        backgroundLabel.setLayout(new GridBagLayout());
        add(backgroundLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        JButton startButton = createPrimaryButton("Start Game");
        JButton customButton = createPrimaryButton("Custom Puzzle");
        JButton historyButton = createPrimaryButton("How to Play");

        buttonPanel.add(startButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(customButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(historyButton);

        backgroundLabel.add(buttonPanel, new GridBagConstraints());

        setVisible(true);
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 22));
        button.setForeground(Color.WHITE);
        button.setBackground(CommonConstants.PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(260, 60));
        button.setMaximumSize(new Dimension(260, 60));
        button.setMinimumSize(new Dimension(260, 60));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addActionListener(e -> {
            if (text.equals("Start Game") || text.equals("Custom Puzzle") || text.equals("History")) {
                new HomePageTest();
                dispose();
            } else if (text.equals("How to Play")) {
                new HowToPlayPage();
                dispose();
            }
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(CommonConstants.PRIMARY_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(CommonConstants.PRIMARY_COLOR);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        new LandingPage();
    }
}
