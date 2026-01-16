package ui;

import model.Event;
import model.EventLog;
import model.MoodLevel;
import model.MoodLog;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.*;

/**
 * Main Swing GUI frame for the Mood Tracker application.
 *
 * Responsibilities:
 * - Owns a single MoodLog model instance for the entire application.
 * - Creates and wires up the three main tabs: HomeTab, SummaryTab, and
 * ManageTab.
 * - Provides shared helper methods for:
 * * accessing and replacing the MoodLog (e.g., when loading from file),
 * * refreshing all tabs when the model changes,
 * * providing mood-related colors and icons used by child tabs.
 */
public class MoodTrackerGUI extends JFrame {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 650;
    private static final String ICON_BASE_PATH = "/ui/icons/";
    private static final int DEFAULT_ICON_SIZE = 64;

    private MoodLog moodLog;

    private HomeTab homeTab;
    private SummaryTab summaryTab;
    private ManageTab manageTab;
    private JTabbedPane tabbedPane;

    // EFFECTS: launches the Mood Tracker GUI on the Swing event dispatch thread
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MoodTrackerGUI());
    }

    // MODIFIES: this
    // EFFECTS:
    // - constructs the main application window,
    // - initializes the MoodLog model and all tabs,
    // - configures basic frame properties (size, close operation, centering),
    // - makes the window visible
    public MoodTrackerGUI() {
        super("Mood Tracker App");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initModel();
        initUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("\n=== Event Log ===");
                for (Event event : EventLog.getInstance()) {
                    System.out.println(event.toString());
                }
            }
        });

        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: initializes the core model components (creates a new, empty MoodLog)
    private void initModel() {
        moodLog = new MoodLog();
    }

    // MODIFIES: this
    // EFFECTS:
    // - creates the tabbed pane and three tabs (Home, Summary, Manage),
    // - adds each tab to the tabbed pane with its label,
    // - adds the tabbed pane to the frame’s content pane
    private void initUI() {
        tabbedPane = new JTabbedPane();

        homeTab = new HomeTab(this);
        summaryTab = new SummaryTab(this);
        manageTab = new ManageTab(this);

        tabbedPane.addTab("Home", homeTab);
        tabbedPane.addTab("Summary", summaryTab);
        tabbedPane.addTab("Manage", manageTab);

        add(tabbedPane);
    }

    // MODIFIES: this
    // EFFECTS:
    // - calls refresh() on all tabs so that their views stay in sync
    // with the current state of moodLog (e.g., after add/edit/delete/load)
    public void refreshAllTabs() {
        homeTab.refresh();
        summaryTab.refresh();
        manageTab.refresh();
    }

    // EFFECTS: returns the current MoodLog model used by the application
    public MoodLog getMoodLog() {
        return moodLog;
    }

    // MODIFIES: this
    // EFFECTS:
    // - replaces the current MoodLog model with the given one
    // - typically used after loading state from file (ManageTab)
    public void setMoodLog(MoodLog moodLog) {
        this.moodLog = moodLog;
    }

    // EFFECTS:
    // - returns a default-sized icon (DEFAULT_ICON_SIZE x DEFAULT_ICON_SIZE)
    // representing the given mood level, or null if no icon resource is found
    public Icon createMoodIcon(MoodLevel level) {
        return createMoodIcon(level, DEFAULT_ICON_SIZE);
    }

    // EFFECTS:
    // - returns an ImageIcon scaled to (size x size) representing the given mood
    // level,
    // using a PNG file from ICON_BASE_PATH, or null if the resource cannot be found
    // - logs a message to standard error if the icon file is missing
    public Icon createMoodIcon(MoodLevel level, int size) {
        String fileName = getIconFileName(level);
        if (fileName == null) {
            return null;
        }

        String path = ICON_BASE_PATH + fileName;
        URL imageURL = getClass().getResource(path);

        if (imageURL == null) {
            System.err.println("Icon not found: " + path);
            return null;
        }

        ImageIcon rawIcon = new ImageIcon(imageURL);
        Image scaled = rawIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // EFFECTS:
    // - returns the icon file name corresponding to the given mood level
    // - if the mood level is not recognized, returns null
    private String getIconFileName(MoodLevel level) {
        switch (level) {
            case VERY_SAD:
                return "very_sad.png";
            case SAD:
                return "sad.png";
            case NEUTRAL:
                return "neutral.png";
            case HAPPY:
                return "happy.png";
            case VERY_HAPPY:
                return "very_happy.png";
            default:
                return null;
        }
    }

    // EFFECTS:
    // - returns a Color to visually represent the given mood level
    // - if the mood level is not recognized, returns Color.LIGHT_GRAY
    public Color getMoodColor(MoodLevel level) {
        switch (level) {
            case VERY_SAD:
                return new Color(239, 83, 80);
            case SAD:
                return new Color(255, 167, 38);
            case NEUTRAL:
                return new Color(255, 238, 88);
            case HAPPY:
                return new Color(156, 204, 101);
            case VERY_HAPPY:
                return new Color(102, 187, 106);
            default:
                return Color.LIGHT_GRAY;
        }
    }
}