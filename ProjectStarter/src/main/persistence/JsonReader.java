package persistence;

import model.MoodEntry;
import model.MoodLevel;
import model.MoodLog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Stream;

// Represents a reader that reads MoodLog data from JSON file and converts it to a MoodLog object
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads MoodLog from file and returns it;
    // throws IOException if an error occurs reading data from file
    public MoodLog read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseMoodLog(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses MoodLog from JSON object and returns it
    private MoodLog parseMoodLog(JSONObject jsonObject) {
        MoodLog moodLog = new MoodLog();
        addEntries(moodLog, jsonObject);
        return moodLog;
    }

    // MODIFIES: moodLog
    // EFFECTS: parses entries from JSON array and adds them to moodLog
    private void addEntries(MoodLog moodLog, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("entries");
        for (Object obj : jsonArray) {
            JSONObject entryJson = (JSONObject) obj;
            addEntry(moodLog, entryJson);
        }
    }

    // MODIFIES: moodLog
    // EFFECTS: parses a single entry from JSON object and adds it to moodLog
    private void addEntry(MoodLog moodLog, JSONObject jsonObject) {
        LocalDate date = LocalDate.parse(jsonObject.getString("date"));
        MoodLevel mood = MoodLevel.valueOf(jsonObject.getString("level"));
        String note = jsonObject.getString("note");
        moodLog.addEntry(new MoodEntry(date, mood, note));
    }
}