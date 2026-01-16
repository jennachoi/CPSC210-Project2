package model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MoodLogTest {

    private MoodLog log;
    private LocalDate d1;
    private LocalDate d2;
    private LocalDate d3;

    private MoodEntry e1;
    private MoodEntry e2;
    private MoodEntry e3;

    @BeforeEach
    void setUp() {
        log = new MoodLog();

        d1 = LocalDate.of(2025, 1, 1);
        d2 = LocalDate.of(2025, 1, 2);
        d3 = LocalDate.of(2025, 1, 3);

        e1 = makeEntry(d1, MoodLevel.VERY_SAD, "bad");
        e2 = makeEntry(d2, MoodLevel.HAPPY, "good");
        e3 = makeEntry(d3, MoodLevel.NEUTRAL, "meh");

        assertTrue(log.addEntry(e1));
        assertTrue(log.addEntry(e2));
        assertTrue(log.addEntry(e3));
    }

    // helper method for test
    // REQUIRES: date != null, level != null
    // EFFECTS: returns a new MoodEntry whose date is set to `date`,
    //          mood level is `level`, and note is `note` (null note becomes "")
    private MoodEntry makeEntry(LocalDate date, MoodLevel level, String note) {
        MoodEntry e = new MoodEntry(level, note);
        e.setDate(date);
        return e;
    }

    // =================== addEntry ===================

    @Test
    void testAddEntryNullRejected() {
        int before = log.getTotalEntries();
        assertFalse(log.addEntry(null));
        assertEquals(before, log.getTotalEntries());
    }

    @Test
    void testAddEntryDuplicateDateRejected() {
        MoodEntry dup = makeEntry(d1, MoodLevel.HAPPY, "dup");
        assertFalse(log.addEntry(dup));
        assertEquals(3, log.getTotalEntries());
    }

    @Test
    void testAddEntryWithLevelAndNoteToday() {
        MoodLog newLog = new MoodLog();
        assertTrue(newLog.addEntry(MoodLevel.SAD, "today"));
        assertFalse(newLog.addEntry(MoodLevel.HAPPY, "again"));
        assertEquals(1, newLog.getTotalEntries());
    }

    // =================== deleteEntry ===================

    @Test
    void testDeleteExistingEntry() {
        assertTrue(log.deleteEntry(d2));
        assertNull(log.getEntryByDate(d2));
        assertEquals(2, log.getTotalEntries());
    }

    @Test
    void testDeleteNonExistingEntry() {
        LocalDate other = LocalDate.of(1999, 1, 1);
        int before = log.getTotalEntries();
        assertFalse(log.deleteEntry(other));
        assertEquals(before, log.getTotalEntries());
    }

    // =================== editEntry ===================

    @Test
    void testEditEntryUpdatesFields() {
        log.editEntry(d1, MoodLevel.HAPPY, "updated", null);

        MoodEntry edited = log.getEntryByDate(d1);
        assertNotNull(edited);
        assertEquals(MoodLevel.HAPPY, edited.getMoodLevel());
        assertEquals("updated", edited.getNote());
    }

    @Test
    void testEditEntryChangesDateIfNewDateFree() {
        LocalDate newDate = LocalDate.of(2025, 1, 10);

        log.editEntry(d1, null, null, newDate);

        assertNull(log.getEntryByDate(d1));
        MoodEntry moved = log.getEntryByDate(newDate);
        assertNotNull(moved);
        assertEquals(MoodLevel.VERY_SAD, moved.getMoodLevel());
    }

    @Test
    void testEditEntryDoesNotChangeDateIfConflict() {
        log.editEntry(d1, null, null, d2);

        MoodEntry stillD1 = log.getEntryByDate(d1);
        MoodEntry stillD2 = log.getEntryByDate(d2);

        assertNotNull(stillD1);
        assertNotNull(stillD2);
        assertEquals(3, log.getTotalEntries());
    }

    @Test
    void testEditEntryDoesNothingIfDateNotFound() {
        MoodLog emptyLog = new MoodLog();
        emptyLog.editEntry(LocalDate.of(2000, 1, 1),
                MoodLevel.HAPPY, "note", LocalDate.of(2000, 1, 2));
        assertEquals(0, emptyLog.getTotalEntries());
    }

    // =================== getters for entries ===================

    @Test
    void testGetEntryByDateFoundAndNotFound() {
        assertEquals(e2, log.getEntryByDate(d2));
        assertNull(log.getEntryByDate(LocalDate.of(2024, 12, 31)));
    }

    @Test
    void testGetAllEntriesDefensiveCopy() {
        List<MoodEntry> copy = log.getAllEntries();
        assertEquals(3, copy.size());

        copy.clear();
        assertEquals(3, log.getTotalEntries());
    }

    @Test
    void testGetEntriesBetweenInclusiveAndEmpty() {
        List<MoodEntry> all = log.getEntriesBetween(d1, d3);
        assertEquals(3, all.size());

        List<MoodEntry> middle = log.getEntriesBetween(d2, d2);
        assertEquals(1, middle.size());
        assertEquals(e2, middle.get(0));

        List<MoodEntry> none = log.getEntriesBetween(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31));
        assertTrue(none.isEmpty());
    }

    // =================== mood distribution ===================

    @Test
    void testGetMoodDistributionOnNonEmptyLog() {
        Map<MoodLevel, Integer> dist = log.getMoodDistribution();

        for (MoodLevel level : MoodLevel.values()) {
            assertTrue(dist.containsKey(level));
        }

        assertEquals(1, dist.get(MoodLevel.VERY_SAD));
        assertEquals(1, dist.get(MoodLevel.HAPPY));
        assertEquals(1, dist.get(MoodLevel.NEUTRAL));
    }

    @Test
    void testGetMoodDistributionOnEmptyLog() {
        MoodLog empty = new MoodLog();
        Map<MoodLevel, Integer> dist = empty.getMoodDistribution();

        for (MoodLevel level : MoodLevel.values()) {
            assertTrue(dist.containsKey(level));
            assertEquals(0, dist.get(level));
        }
    }

    @Test
    void testGetMoodDistributionWithRange() {
        Map<MoodLevel, Integer> dist = log.getMoodDistribution(d1, d2);

        assertEquals(1, dist.get(MoodLevel.VERY_SAD));
        assertEquals(1, dist.get(MoodLevel.HAPPY));
        assertEquals(0, dist.get(MoodLevel.NEUTRAL));
        assertEquals(0, dist.get(MoodLevel.SAD));
        assertEquals(0, dist.get(MoodLevel.VERY_HAPPY));
    }

    // =================== counting & summary ===================

    @Test
    void testCountEntriesBetween() {
        assertEquals(3, log.countEntriesBetween(d1, d3));
        assertEquals(1, log.countEntriesBetween(d2, d2));
        assertEquals(0, log.countEntriesBetween(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31)));
    }

    @Test
    void testGetTotalEntriesAndToString() {
        assertEquals(3, log.getTotalEntries());
        assertEquals("Total Entries: 3", log.toString());
    }

    // =================== toJson ===================

    @Test
    void testToJsonStructureAndSize() {
        JSONObject json = log.toJson();
        assertNotNull(json);
        assertTrue(json.has("entries"));

        JSONArray arr = json.getJSONArray("entries");
        assertEquals(3, arr.length());

        for (int i = 0; i < arr.length(); i++) {
            assertTrue(arr.get(i) instanceof JSONObject);
        }
    }
}