package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MoodEntryTest {
    private MoodEntry entry1;
    private LocalDate today;

    @BeforeEach
    void runBefore() {
        today = LocalDate.now();
        entry1 = new MoodEntry(MoodLevel.HAPPY, "Feeling good");
    }

    @Test
    void testConstructorWithCurrentDate() {
        assertEquals(today, entry1.getDate());
        assertEquals(MoodLevel.HAPPY, entry1.getMoodLevel());
        assertEquals(4, entry1.getMoodScore());
        assertEquals("Feeling good", entry1.getNote());
    }

    @Test
    void testConstructorNullNoteDefaultsToEmptyString() {
        MoodEntry entry = new MoodEntry(MoodLevel.NEUTRAL, null);
        assertEquals("", entry.getNote());
        assertEquals(MoodLevel.NEUTRAL, entry.getMoodLevel());
        assertEquals(3, entry.getMoodScore());
    }

    @Test
    void testConstructorWithCustomDate() {
        LocalDate customDate = LocalDate.of(2025, 10, 1);
        MoodEntry entry = new MoodEntry(customDate, MoodLevel.VERY_HAPPY, "Relaxed");

        assertEquals(customDate, entry.getDate());
        assertEquals(MoodLevel.VERY_HAPPY, entry.getMoodLevel());
        assertEquals(5, entry.getMoodScore());
        assertEquals("Relaxed", entry.getNote());
    }

    @Test
    void testConstructorWithCustomDateNullNoteDefaultsToEmptyString() {
        LocalDate customDate = LocalDate.of(2025, 10, 2);
        MoodEntry entry = new MoodEntry(customDate, MoodLevel.SAD, null);

        assertEquals(customDate, entry.getDate());
        assertEquals(MoodLevel.SAD, entry.getMoodLevel());
        assertEquals(2, entry.getMoodScore());
        assertEquals("", entry.getNote()); 
    }

    @Test
    void testSettersUpdateFields() {
        LocalDate newDate = LocalDate.of(2025, 10, 9);
        entry1.setMoodLevel(MoodLevel.SAD);
        entry1.setNote("Tired");
        entry1.setDate(newDate);

        assertEquals(MoodLevel.SAD, entry1.getMoodLevel());
        assertEquals(2, entry1.getMoodScore());
        assertEquals("Tired", entry1.getNote());
        assertEquals(newDate, entry1.getDate());
    }

    @Test
    void testSetNoteWithNullDefaultsToEmptyString() {
        entry1.setNote(null);
        assertEquals("", entry1.getNote());
    }

    @Test
    void testSetNoteWithNonNullValueUpdatesProperly() {
        entry1.setNote("Updated note");
        assertEquals("Updated note", entry1.getNote());
    }

    @Test
    void testToStringFormat() {
        String result = entry1.toString();

        assertTrue(result.contains(today.toString()));
        assertTrue(result.contains(MoodLevel.HAPPY.toString()));
        assertTrue(result.contains("Feeling good"));
    }
}