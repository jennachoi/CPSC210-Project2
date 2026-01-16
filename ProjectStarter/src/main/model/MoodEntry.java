package model;

import java.time.LocalDate;

import org.json.JSONObject;

// Represents a single mood record for a specific date.
// INVARIANTS: moodLevel != null
public class MoodEntry {
    private LocalDate date; // date of the mood record
    private MoodLevel moodLevel; // mood level (enum)
    private String note; // optional note text

    // REQUIRES: moodLevel != null
    // MODIFIES: this
    // EFFECTS: creates a new mood entry with the current date and given mood level
    // and note;
    // if note is null, sets note to an empty string ("")
    public MoodEntry(MoodLevel moodLevel, String note) {
        this.date = LocalDate.now();
        this.moodLevel = moodLevel;
        this.note = (note == null) ? "" : note;
    }

    // REQUIRES: date != null, moodLevel != null
    // MODIFIES: this
    // EFFECTS: creates a new mood entry with the specified date, mood level, and
    // note;
    // if note is null, sets note to an empty string ("")
    public MoodEntry(LocalDate date, MoodLevel moodLevel, String note) {
        this.date = date;
        this.moodLevel = moodLevel;
        this.note = (note == null) ? "" : note;
    }

    public LocalDate getDate() {
        return date;
    }

    public MoodLevel getMoodLevel() {
        return moodLevel;
    }

    public int getMoodScore() {
        return moodLevel.getValue();
    }

    public String getNote() {
        return note;
    }

    public void setDate(LocalDate newDate) {
        this.date = newDate;
    }

    public void setMoodLevel(MoodLevel newLevel) {
        this.moodLevel = newLevel;
    }

    public void setNote(String newNote) {
        this.note = (newNote == null) ? "" : newNote;
    }

    // EFFECTS: returns a string representation of this MoodEntry in the format
    // "YYYY-MM-DD | <MoodLevel label> | Note: <note>",
    // where <MoodLevel label> is the emoji + text provided by MoodLevel.toString()
    @Override
    public String toString() {
        return date + " | " + moodLevel + " | Note: " + note;
    }

    // EFFECTS: returns a string representation of this mood entry in the form:
    // "YYYY-MM-DD | [MoodLevel] | Note: [note]"
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("date", getDate().toString());
        json.put("level", getMoodLevel().name());
        json.put("note", getNote());
        return json;
    }
}