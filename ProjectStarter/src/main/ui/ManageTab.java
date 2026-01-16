package ui;

import model.MoodEntry;
import model.MoodLevel;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Represents the "Manage" tab in the mood tracker UI.
 * This tab lets the user:
 *   - view all MoodEntry objects in the current MoodLog,
 *   - save/load the MoodLog from/to file,
 *   - edit an existing entry (date, mood level, note),
 *   - delete an existing entry.
 * It delegates all model operations to the parent MoodTrackerGUI.
 */
public class ManageTab extends JPanel {
    private static final String JSON_STORE = "./data/moodlog.json";

    private MoodTrackerGUI parent;
    private JsonReader jsonReader;
    private JsonWriter jsonWriter;
    private DefaultListModel<String> listModel;
    private JList<String> entryList;

    // REQUIRES: parent != null
    // MODIFIES: this
    // EFFECTS: constructs manage tab with entry list and control buttons
    public ManageTab(MoodTrackerGUI parent) {
        this.parent = parent;
        this.jsonReader = new JsonReader(JSON_STORE);
        this.jsonWriter = new JsonWriter(JSON_STORE);

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initializeComponents();
    }

    // MODIFIES: this
    // EFFECTS: initializes all UI components and lays them out
    private void initializeComponents() {
        add(createTitleLabel(), BorderLayout.NORTH);
        add(createEntryListPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    // EFFECTS: creates and returns title label for this tab
    private JLabel createTitleLabel() {
        JLabel title = new JLabel("💾 Manage Your Mood Log", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        return title;
    }

    // MODIFIES: this
    // EFFECTS: creates the scrollable list for entries and returns its scroll pane
    private JScrollPane createEntryListPanel() {
        listModel = new DefaultListModel<>();
        entryList = new JList<>(listModel);
        entryList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        entryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(entryList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Entries"));
        return scrollPane;
    }

    // EFFECTS: creates and returns panel with save/load/edit/delete buttons
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(createButton("Save Data", e -> handleSave()));
        panel.add(createButton("Load Data", e -> handleLoad()));
        panel.add(createButton("Edit Entry", e -> handleEditEntry()));
        panel.add(createButton("Delete Entry", e -> handleDeleteEntry()));

        return panel;
    }

    // EFFECTS: creates a button with given text and attaches given listener
    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    // MODIFIES: file system
    // EFFECTS: saves mood log to JSON_STORE file and shows result message
    private void handleSave() {
        try {
            jsonWriter.open();
            jsonWriter.write(parent.getMoodLog());
            jsonWriter.close();
            showSuccessMessage("💾 Saved successfully!");
        } catch (FileNotFoundException e) {
            showErrorMessage("Error saving file: " + e.getMessage());
        }
    }

    // MODIFIES: parent
    // EFFECTS: loads mood log from JSON_STORE file, sets it on parent,
    //          and refreshes all tabs; shows error dialog if load fails
    private void handleLoad() {
        try {
            parent.setMoodLog(jsonReader.read());
            showSuccessMessage("📂 Loaded successfully!");
            parent.refreshAllTabs();
        } catch (IOException e) {
            showErrorMessage("Error loading file: " + e.getMessage());
        }
    }

    // MODIFIES: parent.moodLog
    // EFFECTS: if an entry is selected, opens dialog to edit that entry;
    //          if no entry is selected, shows warning and does nothing
    private void handleEditEntry() {
        LocalDate date = getSelectedEntryDate();
        if (date == null) {
            showNoSelectionWarning();
            return;
        }

        MoodEntry entry = parent.getMoodLog().getEntryByDate(date);
        if (entry != null) {
            showEditDialog(entry, date);
        }
    }

    // MODIFIES: parent.moodLog
    // EFFECTS: shows edit dialog for given entry; if user confirms,
    //          updates entry using values from the dialog panel
    private void showEditDialog(MoodEntry entry, LocalDate originalDate) {
        JPanel panel = createEditPanel(entry);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit Entry",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            updateEntry(panel, originalDate);
        }
    }

    // REQUIRES: entry != null
    // EFFECTS: creates and returns an edit panel for the given entry;
    //          panel shows editable date field, mood dropdown, and note area,
    //          and stores key components as client properties
    private JPanel createEditPanel(MoodEntry entry) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));

        JTextField dateField = createDateField(entry);
        JComboBox<MoodLevel> moodCombo = createMoodDropdown(entry);
        JScrollPane noteScroll = createNoteArea(entry);

        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Mood:"));
        panel.add(moodCombo);
        panel.add(new JLabel("Note:"));
        panel.add(noteScroll);

        storePanelComponents(panel, dateField, moodCombo, noteScroll);

        return panel;
    }

    // REQUIRES: entry != null
    // EFFECTS: returns a text field initialized with entry's date value
    private JTextField createDateField(MoodEntry entry) {
        return new JTextField(entry.getDate().toString());
    }

    // REQUIRES: entry != null
    // EFFECTS: returns a JComboBox pre-populated with MoodLevel values,
    //          with entry's current level selected; renderer shows label only
    private JComboBox<MoodLevel> createMoodDropdown(MoodEntry entry) {
        JComboBox<MoodLevel> moodCombo = new JComboBox<>(MoodLevel.values());
        moodCombo.setSelectedItem(entry.getMoodLevel());

        moodCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value instanceof MoodLevel) {
                    lbl.setText(((MoodLevel) value).getLabel()); // label only, no emoji
                }
                return lbl;
            }
        });

        return moodCombo;
    }

    // REQUIRES: entry != null
    // EFFECTS: creates and returns a scroll pane containing a text area
    //          initialized with entry's note text
    private JScrollPane createNoteArea(MoodEntry entry) {
        JTextArea noteArea = new JTextArea(entry.getNote(), 3, 20);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        return new JScrollPane(noteArea);
    }

    // MODIFIES: panel
    // EFFECTS: stores dateField, moodCombo and the text area inside noteScroll
    //          as client properties of panel under keys "dateField", "moodCombo", "noteArea"
    private void storePanelComponents(JPanel panel,
                                      JTextField dateField,
                                      JComboBox<MoodLevel> moodCombo,
                                      JScrollPane noteScroll) {
        JTextArea noteArea = (JTextArea) noteScroll.getViewport().getView();
        panel.putClientProperty("dateField", dateField);
        panel.putClientProperty("moodCombo", moodCombo);
        panel.putClientProperty("noteArea", noteArea);
    }

    // MODIFIES: parent.moodLog
    // EFFECTS: updates entry with new values from panel; if the user changes
    //          the date to another day that already has an entry, shows an error
    //          and does not update; if date is unchanged, passes null for newDate
    //          so that the model keeps the original date
    private void updateEntry(JPanel panel, LocalDate originalDate) {
        JTextField dateField = (JTextField) panel.getClientProperty("dateField");
        JComboBox<MoodLevel> moodCombo =
                (JComboBox<MoodLevel>) panel.getClientProperty("moodCombo");
        JTextArea noteArea = (JTextArea) panel.getClientProperty("noteArea");

        LocalDate newDate;
        try {
            newDate = LocalDate.parse(dateField.getText().trim());
        } catch (DateTimeParseException e) {
            showErrorMessage("Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        if (!newDate.equals(originalDate)
                && parent.getMoodLog().getEntryByDate(newDate) != null) {
            showErrorMessage("An entry already exists for that date.");
            return;
        }

        MoodLevel newMood = (MoodLevel) moodCombo.getSelectedItem();
        String newNote = noteArea.getText().trim();

        LocalDate dateToSet = newDate.equals(originalDate) ? null : newDate;

        parent.getMoodLog().editEntry(originalDate, newMood, newNote, dateToSet);
        showSuccessMessage("✅ Entry updated!");
        parent.refreshAllTabs();
    }

    // MODIFIES: parent.moodLog
    // EFFECTS: if an entry is selected, asks user for confirmation and
    //          deletes the entry if confirmed; does nothing if no selection
    private void handleDeleteEntry() {
        LocalDate date = getSelectedEntryDate();
        if (date == null) {
            showNoSelectionWarning();
            return;
        }

        if (confirmDelete()) {
            deleteEntry(date);
        }
    }

    // EFFECTS: shows confirmation dialog; returns true if user chooses YES
    private boolean confirmDelete() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this entry?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        return confirm == JOptionPane.YES_OPTION;
    }

    // MODIFIES: parent.moodLog
    // EFFECTS: deletes entry at given date (if present) and refreshes display
    private void deleteEntry(LocalDate date) {
        if (parent.getMoodLog().deleteEntry(date)) {
            showSuccessMessage("Entry deleted!");
            parent.refreshAllTabs();
        }
    }

    // EFFECTS: returns date of selected entry or null if none selected
    private LocalDate getSelectedEntryDate() {
        String selected = entryList.getSelectedValue();
        if (selected == null || selected.startsWith("No entries")) {
            return null;
        }

        String dateStr = selected.split(" \\| ")[0];
        return LocalDate.parse(dateStr);
    }

    // EFFECTS: shows no selection warning dialog
    private void showNoSelectionWarning() {
        JOptionPane.showMessageDialog(
                this,
                "Please select an entry.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
    }

    // EFFECTS: shows success message dialog with given message
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // EFFECTS: shows error message dialog with given message
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                "⚠️ " + message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // MODIFIES: this
    // EFFECTS: refreshes entry list to reflect current state of parent's MoodLog
    public void refresh() {
        listModel.clear();
        List<MoodEntry> entries = parent.getMoodLog().getAllEntries();

        if (entries.isEmpty()) {
            listModel.addElement("No entries yet 😊");
        } else {
            addEntriesToList(entries);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds all given entries to list model in formatted string form
    private void addEntriesToList(List<MoodEntry> entries) {
        for (MoodEntry entry : entries) {
            String display = formatEntryDisplay(entry);
            listModel.addElement(display);
        }
    }

    // EFFECTS: returns a formatted string representation of entry for display in list
    private String formatEntryDisplay(MoodEntry entry) {
        String note = entry.getNote().isEmpty() ? "(no note)" : entry.getNote();
        return String.format("%s | %s | %s",
                entry.getDate(),
                entry.getMoodLevel().getLabel(),  // label only, no emoji
                note);
    }
}