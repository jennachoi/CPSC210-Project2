package persistence;

import model.MoodEntry;
import model.MoodLevel;
import model.MoodLog;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderTest {

    @Test
    void testReadEmptyLog() throws IOException {
        JsonReader reader = new JsonReader("./data/testWriterEmpty.json");
        MoodLog log = reader.read();
        assertEquals(0, log.getTotalEntries());
    }

    @Test
    void testReadSampleLog() throws IOException {
        JsonReader reader = new JsonReader("./data/testWriterGeneral.json");
        MoodLog log = reader.read();

        assertEquals(2, log.getTotalEntries());

        MoodEntry e1 = log.getEntryByDate(LocalDate.of(2025, 10, 1));
        assertNotNull(e1);
        assertEquals(MoodLevel.HAPPY, e1.getMoodLevel());
        assertEquals("Good day", e1.getNote());

        MoodEntry e2 = log.getEntryByDate(LocalDate.of(2025, 10, 2));
        assertNotNull(e2);
        assertEquals(MoodLevel.SAD, e2.getMoodLevel());
        assertEquals("Tired", e2.getNote());
    }

    @Test
    void testReadNonexistentFileThrows() {
        JsonReader reader = new JsonReader("./data/does-not-exist.json");
        assertThrows(IOException.class, reader::read);
    }

    @Test
    void testReadWithDuplicateDatesHonorsAddRule() throws IOException {
        JsonReader reader = new JsonReader("./data/testWriterGeneral.json");
        MoodLog first = reader.read();
        assertEquals(2, first.getTotalEntries());

        MoodLog second = reader.read();
        assertEquals(2, second.getTotalEntries());
    }
}