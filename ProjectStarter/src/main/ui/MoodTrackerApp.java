package ui;

import model.MoodEntry;
import model.MoodLevel;
import model.MoodLog;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// Console-based UI for the Mood Tracker application
public class MoodTrackerApp {
    private static final String JSON_STORE = "./data/moodLog.json";

    private Scanner input;
    private MoodLog moodLog;
    private boolean keepRunning;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // EFFECTS: constructs the MoodTracker Application and enters the main interaction loop
    public MoodTrackerApp() {
        runApp();
    }

    // MODIFIES: this
    // EFFECTS:
    //  - initializes input scanner and a new empty MoodLog
    //  - repeatedly displays the menu and processes commands until user quits
    //  - closes the scanner before exiting 
    private void runApp() {
        input = new Scanner(System.in);
        moodLog = new MoodLog();
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        keepRunning = true;

        System.out.println("Welcome to the Mood Tracker!");

        while (keepRunning) {
            displayMenu();
            String command = input.next().toLowerCase();
            input.nextLine(); // clear buffer
            processCommand(command);
        }

        System.out.println("Goodbye! 👋");
        input.close();
    }

    // EFFECTS: prints the list of available commands to standard output
    private void displayMenu() {
        System.out.println("\nSelect an option:");
        System.out.println("a -> Add mood entry");
        System.out.println("v -> View all entries");
        System.out.println("e -> Edit entry");
        System.out.println("d -> Delete entry");
        System.out.println("m -> Show mood summary");
        System.out.println("s -> Save mood log to file");
        System.out.println("l -> Load mood log from file");
        System.out.println("q -> Quit");
        System.out.print("Enter command: ");
    }

    // REQUIRES: command is a non-null string
    // MODIFIES: this, moodLog (indirectly through invoked handlers)
    // EFFECTS:
    // - dispatches to the appropriate handler based on the given command
    // - if command is unrecognized, informs the user and makes no state changes
    private void processCommand(String command) {
        switch (command) {
            case "a": addMoodEntry(); 
            break;
            case "v": viewEntries(); 
            break;
            case "e": editEntry(); 
            break;
            case "d": deleteEntry(); 
            break;
            case "m": showSummary(); 
            break;
            case "s": saveMoodLog(); 
            break;
            case "l": loadMoodLog(); 
            break;
            case "q": keepRunning = false; 
            break;
            default:
                System.out.println("Invalid command. Please try again.");
        }
    }

    // MODIFIES: moodLog
    // EFFECTS:
    //  - prompts user to select a mood (1–5) and enter an optional note
    //  - attempts to add today's MoodEntry to MoodLog
    //  - if mood value is invalid, prints an error and does not modify the log
    //  - if an entry for today already exists, informs the user and does not add
    private void addMoodEntry() {
        displayMoodOptions();
        int score = input.nextInt();
        input.nextLine();
        
        System.out.print("\nEnter a note (optional): ");
        String note = input.nextLine();

        try {
            MoodLevel mood = MoodLevel.fromValue(score);
            if (moodLog.addEntry(mood, note)) {
                System.out.println("\n✅ Mood entry added successfully!");
            } else {
                System.out.println("⚠️ You already have an entry for today!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ " + e.getMessage());
        }
    }

    // EFFECTS: prints the valid mood options (1–5) and their labels
    private void displayMoodOptions() {
        System.out.println("\nSelect your mood (1–5):");
        for (MoodLevel level : MoodLevel.values()) {
            System.out.println(level.getValue() + " -> " + level);
        }
    }

    // EFFECTS: if no entries exist, prints a message and returns
    //          otherwise prints all entries currently in the log
    private void viewEntries() {
        List<MoodEntry> entries = moodLog.getAllEntries();
        if (entries.isEmpty()) {
            System.out.println("\nNo entries yet.");
            return;
        }

        System.out.println("\nYour Mood Entries:");
        for (MoodEntry e : entries) {
            System.out.println(" - " + e);
        }
    }

    // MODIFIES: moodLog
    // EFFECTS:
    // - prompts user for a target date; if no entry exists for that date, prints a warning and returns
    // - otherwise, optionally reads a new date, mood, and note from the user
    // - fields skipped by the user (empty input) are left unchanged
    // - if new date is invalid format or already used, date change is skipped
    // - updates the entry accordingly and prints a confirmation
    private void editEntry() {
        LocalDate date = getDateInput("\nEnter the date to edit (YYYY-MM-DD): ");
        MoodEntry entry = moodLog.getEntryByDate(date);

        if (entry == null) {
            System.out.println("⚠️ No entry found for that date.");
            return;
        }

        System.out.println("\nEditing entry: " + entry);
        LocalDate newDate = getOptionalDateInput("\nEnter new date (YYYY-MM-DD) or press Enter to skip: ");
        MoodLevel newMood = getOptionalMood("\nEnter new mood score (1–5) or press Enter to skip: ");
        String newNote = getOptionalNote("\nEnter new note or press Enter to skip: ");

        moodLog.editEntry(date, newMood, newNote, newDate);
        System.out.println("\n✅ Entry updated!");
    }

    // MODIFIES: moodLog
    // EFFECTS:
    // - prompts user for a date and attempts to delete the entry at that date
    // - prints whether deletion succeeded or failed (when no entry exists)
    private void deleteEntry() {
        LocalDate date = getDateInput("\nEnter the date to delete (YYYY-MM-DD): ");
        if (moodLog.deleteEntry(date)) {
            System.out.println("🗑️ Entry deleted.");
        } else {
            System.out.println("⚠️ No entry found for that date.");
        }
    }

    // EFFECTS: prints a summary string of the log and the mood distribution table
    private void showSummary() {
        System.out.println("\nMood Summary:");
        System.out.println(moodLog.toString());
        System.out.println("Mood distribution:");

        Map<MoodLevel, Integer> dist = moodLog.getMoodDistribution();
        for (MoodLevel level : MoodLevel.values()) {
            System.out.println(" " + level + ": " + dist.get(level));
        }
    }

    // MODIFIES: file system
    // EFFECTS: saves moodLog to JSON file; prints confirmation or error message
    private void saveMoodLog() {
        try {
            jsonWriter.open();
            jsonWriter.write(moodLog);
            jsonWriter.close();
            System.out.println("\n💾 Mood log saved to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("⚠️ Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads moodLog from JSON file; prints confirmation or error message
    private void loadMoodLog() {
        try {
            moodLog = jsonReader.read();
            System.out.println("\n📂 Loaded mood log from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("⚠️ Unable to read from file: " + JSON_STORE);
        }
    }

    // EFFECTS: prompts user for a date and returns parsed LocalDate
    private LocalDate getDateInput(String prompt) {
        System.out.print(prompt);
        return LocalDate.parse(input.nextLine());
    }

    // EFFECTS: prompts user for a date; returns parsed LocalDate or null if skipped/invalid
    private LocalDate getOptionalDateInput(String prompt) {
        System.out.print(prompt);
        String dateInput = input.nextLine();

        if (dateInput.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateInput);
        } catch (Exception e) {
            System.out.println("⚠️ Invalid date format. Skipping date change.");
            return null;
        }
    }

    // EFFECTS: prompts user for a mood (1–5); returns MoodLevel or null if skipped/invalid
    private MoodLevel getOptionalMood(String prompt) {
        System.out.print(prompt);
        String scoreInput = input.nextLine();

        if (scoreInput.isEmpty()) {
            return null;
        }

        try {
            int scoreValue = Integer.parseInt(scoreInput);
            return MoodLevel.fromValue(scoreValue);
        } catch (Exception e) {
            System.out.println("⚠️ " + e.getMessage());
            return null;
        }
    }

    // EFFECTS: prompts user for a note; returns string or null if skipped
    private String getOptionalNote(String prompt) {
        System.out.print(prompt);
        String note = input.nextLine();
        return note.isEmpty() ? null : note;
    }
}