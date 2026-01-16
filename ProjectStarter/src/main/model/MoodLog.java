package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import persistence.Writable;

// Represents a collection (log) of MoodEntry records.
public class MoodLog implements Writable{
    private List<MoodEntry> entries;

    // MODIFIES: this
    // EFFECTS: constructs an empty MoodLog with no entries
    public MoodLog() {
        entries = new ArrayList<>();
    }

    // REQUIRES: entry != null
    // MODIFIES: this
    // EFFECTS: if no entry exists with the same date as entry.getDate(), adds entry
    // and returns true; otherwise returns false and leaves this unchanged
    // if added, logs "Added Entry: DATE | LEVEL | NOTE"
    public boolean addEntry(MoodEntry entry) {
        if (entry == null || getEntryByDate(entry.getDate()) != null) {
            return false;
        }
        entries.add(entry);

        EventLog.getInstance().logEvent(
                new Event("Added Entry: " + formatEntryForLog(entry)));
        return true;
    }

    // REQUIRES: level != null
    // MODIFIES: this
    // EFFECTS: creates a new MoodEntry for today's date with given level and note
    // (treats null note as ""), attempts to add it;
    // returns true if added, false if an entry for today already exists
    public boolean addEntry(MoodLevel level, String note) {
        MoodEntry entry = new MoodEntry(level, note);
        return addEntry(entry);
    }

    // MODIFIES: this
    // EFFECTS: if an entry exists for the given date, removes it and returns true;
    // otherwise returns false and leaves this unchanged
    // If removed, logs "Deleted Entry: DATE | LEVEL | NOTE"
    public boolean deleteEntry(LocalDate date) {
        MoodEntry entry = getEntryByDate(date);
        if (entry != null) {
            entries.remove(entry);

            EventLog.getInstance().logEvent(
                    new Event("Deleted Entry: " + formatEntryForLog(entry)));

            return true;
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: edits the entry that matches date, if it exists:
    // - if newLevel != null, updates mood level
    // - if newNote != null, updates note (null treated as "")
    // - if newDate != null AND no entry exists for newDate, updates the date
    // if no entry for date exists, does nothing
    // - If an entry is found (regardless of whether fields actually change),
    // logs "Updated Entry: DATE | LEVEL | NOTE" using the final state.
    public void editEntry(LocalDate date, MoodLevel newLevel, String newNote, LocalDate newDate) {
        MoodEntry entry = getEntryByDate(date);
        if (entry == null) {
            return;
        }

        if (newLevel != null) {
            entry.setMoodLevel(newLevel);
        }

        if (newNote != null) {
            entry.setNote(newNote);
        }

        if (newDate != null && getEntryByDate(newDate) == null) {
            entry.setDate(newDate);
        }

        EventLog.getInstance().logEvent(
                new Event("Updated Entry: " + formatEntryForLog(entry)));
    }

    // EFFECTS: returns the entry whose date equals the given date, or null if none
    // exists
    public MoodEntry getEntryByDate(LocalDate date) {
        for (MoodEntry e : entries) {
            if (e.getDate().equals(date)) {
                return e;
            }
        }
        return null;
    }

    // EFFECTS: returns a new list containing all entries (a defensive copy);
    // modifying the returned list will not affect this MoodLog
    public List<MoodEntry> getAllEntries() {
        return new ArrayList<>(entries);
    }

    // REQUIRES: from != null, to != null, and (from.isBefore(to) ||
    // from.isEqual(to))
    // EFFECTS: returns a new list of all entries with dates in [from, to]
    // inclusive;
    // returns an empty list if no matches
    public List<MoodEntry> getEntriesBetween(LocalDate from, LocalDate to) {
        List<MoodEntry> result = new ArrayList<>();
        for (MoodEntry e : entries) {
            LocalDate d = e.getDate();
            if ((d.isEqual(from) || d.isAfter(from))
                    && (d.isEqual(to) || d.isBefore(to))) {
                result.add(e);
            }
        }
        return result;
    }

    // EFFECTS: returns a map counting how many entries exist for each MoodLevel;
    // all levels are present as keys with at least 0 as the count
    public Map<MoodLevel, Integer> getMoodDistribution() {
        Map<MoodLevel, Integer> counts = new HashMap<>();
        for (MoodLevel level : MoodLevel.values()) {
            counts.put(level, 0);
        }
        for (MoodEntry e : entries) {
            MoodLevel level = e.getMoodLevel();
            counts.put(level, counts.get(level) + 1);
        }
        return counts;
    }

    // REQUIRES: from != null, to != null, and (from.isBefore(to) ||
    // from.isEqual(to))
    // EFFECTS: returns a map counting how many entries exist for each MoodLevel
    // among entries with dates in [from, to] inclusive;
    // all levels are present as keys with at least 0 as the count
    public Map<MoodLevel, Integer> getMoodDistribution(LocalDate from, LocalDate to) {
        Map<MoodLevel, Integer> counts = new HashMap<>();
        for (MoodLevel level : MoodLevel.values()) {
            counts.put(level, 0);
        }

        List<MoodEntry> inRange = getEntriesBetween(from, to);
        for (MoodEntry e : inRange) {
            MoodLevel level = e.getMoodLevel();
            counts.put(level, counts.get(level) + 1);
        }
        return counts;
    }

    // EFFECTS: returns how many entries exist between from and to inclusive
    public int countEntriesBetween(LocalDate from, LocalDate to) {
        int count = 0;
        for (MoodEntry e : entries) {
            LocalDate d = e.getDate();
            if ((d.isEqual(from) || d.isAfter(from))
                    && (d.isEqual(to) || d.isBefore(to))) {
                count++;
            }
        }
        return count;
    }

    public int getTotalEntries() {
        return entries.size();
    }

    // EFFECTS: returns a short textual summary of this mood log
    @Override
    public String toString() {
        return "Total Entries: " + getTotalEntries();
    }

    // EFFECTS: returns JSON representation of this log in the form:
    // { "entries": [ <entryJson>, ... ] }
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();
        List<MoodEntry> list = getAllEntries();
        for (int i = 0; i < list.size(); i++) {
            arr.put(list.get(i).toJson());
        }
        json.put("entries", arr);
        return json;
    }

    // REQUIRES: entry is not null
    // EFFECTS: returns a short string representation of the given entry in the
    // format
    // "DATE | LEVEL | NOTE", where DATE is entry.getDate().toString(),
    // LEVEL is entry.getMoodLevel().getLabel(), and NOTE is entry.getNote();
    private String formatEntryForLog(MoodEntry entry) {
        String note = entry.getNote();
        return entry.getDate()
                + " | " + entry.getMoodLevel().getLabel()
                + " | " + note;
    }
}