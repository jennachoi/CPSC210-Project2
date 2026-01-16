package ui;

import model.MoodLevel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Represents the main "Home" tab in the mood tracker UI.
 * Users can quickly add a new MoodEntry for today by clicking one of the
 * mood buttons and optionally entering a note in the popup dialog.
 */
public class HomeTab extends JPanel {

    private MoodTrackerGUI parent;

    // =================== Constructor & Initialization ===================

    // REQUIRES: parent != null
    // MODIFIES: this
    // EFFECTS: constructs main tab with quick entry interface
    public HomeTab(MoodTrackerGUI parent) {
        this.parent = parent;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(250, 250, 250));
        initializeComponents();
    }

    // MODIFIES: this
    // EFFECTS: initializes all UI components
    private void initializeComponents() {
        add(createTitleLabel(), BorderLayout.NORTH);
        add(createMoodButtonPanel(), BorderLayout.CENTER);
    }

    // =================== UI Building Helpers ===================

    // EFFECTS: creates and returns the title label
    private JLabel createTitleLabel() {
        JLabel title = new JLabel("How do you feel today?", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(new EmptyBorder(20, 0, 30, 0));
        return title;
    }

    // EFFECTS: creates and returns panel with mood buttons
    private JPanel createMoodButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 15, 15));
        panel.setBorder(new EmptyBorder(30, 40, 100, 40));
        panel.setBackground(new Color(250, 250, 250));

        for (MoodLevel level : MoodLevel.values()) {
            panel.add(createMoodButton(level));
        }

        return panel;
    }

    // EFFECTS: creates a mood button for given level
    private JButton createMoodButton(MoodLevel level) {
        JButton button = createBaseMoodButton(level);

        button.add(Box.createVerticalGlue());
        addMoodIconOrFallback(button, level);
        button.add(Box.createRigidArea(new Dimension(0, 10)));
        addMoodTextLabel(button, level);
        button.add(Box.createVerticalGlue());

        return button;
    }

    // EFFECTS: creates base JButton with layout, size, color, listener set
    private JButton createBaseMoodButton(MoodLevel level) {
        JButton button = new JButton();
        button.setLayout(new BoxLayout(button, BoxLayout.Y_AXIS));
        button.setPreferredSize(new Dimension(140, 180));
        button.setBackground(parent.getMoodColor(level));
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        button.addActionListener(e -> handleMoodButtonClick(level));
        return button;
    }

    // EFFECTS: adds icon label or fallback label to button
    private void addMoodIconOrFallback(JButton button, MoodLevel level) {
        Icon moodIcon = parent.createMoodIcon(level); 

        if (moodIcon != null) {
            JLabel iconLabel = new JLabel(moodIcon);
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.add(iconLabel);
        } else {
            JLabel fallbackLabel = new JLabel(level.getLabel());
            fallbackLabel.setFont(new Font("Arial", Font.BOLD, 18));
            fallbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.add(fallbackLabel);
        }
    }

    // EFFECTS: adds text label (mood name) under icon
    private void addMoodTextLabel(JButton button, MoodLevel level) {
        JLabel nameLabel = new JLabel(level.getLabel());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.add(nameLabel);
    }

    // =================== Interaction Logic ===================

    // MODIFIES: parent.moodLog
    // EFFECTS: handles mood button click and shows note dialog
    private void handleMoodButtonClick(MoodLevel level) {
        String note = showNoteDialog(level);

        if (note != null) {
            addMoodEntry(level, note);
        }
    }

    // EFFECTS: shows dialog for note input and returns note or null if cancelled
    private String showNoteDialog(MoodLevel level) {
        JPanel panel = createNoteDialogPanel();
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Ah, you feel " + level.toString() + " today!",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        JTextArea noteArea = (JTextArea) ((JScrollPane) panel.getComponent(1))
                .getViewport()
                .getView();

        return (result == JOptionPane.OK_OPTION) ? noteArea.getText().trim() : null;
    }

    // EFFECTS: creates and returns panel used in note dialog
    private JPanel createNoteDialogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JLabel("Add more details or save directly:"), BorderLayout.NORTH);

        JTextArea noteArea = new JTextArea(5, 30);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(noteArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // MODIFIES: parent.moodLog
    // EFFECTS: adds mood entry and shows result message
    private void addMoodEntry(MoodLevel level, String note) {
        if (parent.getMoodLog().addEntry(level, note)) {
            showSuccessMessage();
            parent.refreshAllTabs();
        } else {
            showDuplicateEntryWarning();
        }
    }

    // EFFECTS: shows success message dialog
    private void showSuccessMessage() {
        JOptionPane.showMessageDialog(
                this,
                "Saved! 💾",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // EFFECTS: shows duplicate entry warning dialog
    private void showDuplicateEntryWarning() {
        JOptionPane.showMessageDialog(
                this,
                "⚠️ You already have an entry for today!",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
    }

    // EFFECTS: refreshes this tab (currently no action needed)
    public void refresh() {
        // No refresh needed for main tab
    }
}