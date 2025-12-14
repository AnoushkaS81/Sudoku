package utils;

import java.awt.*;

public class CommonConstants {
    // Get screen dimensions
    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    // Calculate efficient window size (80% of screen or optimal size, whichever is smaller)
    private static final int OPTIMAL_WIDTH = 1200;
    private static final int OPTIMAL_HEIGHT = 850;

    public static final int HOMEPAGE_WIDTH = Math.min(OPTIMAL_WIDTH, (int)(SCREEN_SIZE.getWidth() * 0.65));
    public static final int HOMEPAGE_HEIGHT = Math.min(OPTIMAL_HEIGHT, (int)(SCREEN_SIZE.getHeight() * 0.85));

    // Based on a 495x495 grid + controls + padding
    public static final int MIN_HOMEPAGE_WIDTH = 1100;
    public static final int MIN_HOMEPAGE_HEIGHT = 750;

    public static final Color BACKGROUND = Color.WHITE;      // Very light blue
    public static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    public static final Color SECONDARY_COLOR = new Color(197, 227, 236);
    public static final Color TEXT_COLOR = new Color(30, 41, 59);
    public static final Color TEXT_COLOR_SEC = new Color(120, 120, 120);

}