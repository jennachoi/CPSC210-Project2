package persistence;

import model.MoodLevel;
import model.MoodEntry;
import model.MoodLog;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class JsonWriterTest {

    @Test
    void testWriterEmptyMoodLog() {
        try {
            MoodLog log = new MoodLog();
            JsonWriter writer = new JsonWriter("./data/testWriterEmpty.json");
            writer.open();
            writer.write(log);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmpty.json");
            log = reader.read();
            assertEquals(0, log.getTotalEntries());
        } catch (IOException e) {
            fail("Unexpected IOException");
        }
    }

    @Test
    void testWriterGeneralMoodLog() {
        try {
            MoodLog log = new MoodLog();
            log.addEntry(new MoodEntry(LocalDate.of(2025, 10, 1), MoodLevel.HAPPY, "Good day"));
            log.addEntry(new MoodEntry(LocalDate.of(2025, 10, 2), MoodLevel.SAD, "Tired"));

            JsonWriter writer = new JsonWriter("./data/testWriterGeneral.json");
            writer.open();
            writer.write(log);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneral.json");
            MoodLog readLog = reader.read();
            assertEquals(2, readLog.getTotalEntries());

            assertEquals("Good day", readLog.getAllEntries().get(0).getNote());
            assertEquals("Tired", readLog.getAllEntries().get(1).getNote());
        } catch (IOException e) {
            fail("Unexpected IOException");
        }
    }
}