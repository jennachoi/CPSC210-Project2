package persistence;

import model.MoodLog;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

// Represents a writer that writes JSON representation of a MoodLog to file
public class JsonWriter {
    private static final int TAB = 4;
    private PrintWriter writer;
    private String destination;

    // EFFECTS: constructs writer to write data to destination file path
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer for writing to file at destination;
    //          throws FileNotFoundException if the file cannot be opened for writing
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(destination);
    }

    // MODIFIES: this
    // EFFECTS: writes the given MoodLog to file in JSON format with indentation
    public void write(MoodLog moodLog) {
        JSONObject json = moodLog.toJson();
        saveToFile(json.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes the writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes the given JSON string to the file
    private void saveToFile(String json) {
        writer.print(json);
    }
}