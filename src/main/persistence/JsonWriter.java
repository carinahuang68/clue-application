package persistence;
import org.json.JSONObject;

import model.Detective;
import model.Player;

import java.io.*;
import java.util.List;


// Represents a writer that writes JSON representation of Players and Detective to file
public class JsonWriter {
    private static final int TAB = 4;
    private PrintWriter writer;
    private String destination;

    // EFFECTS: constructs writer to write to destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if destination file cannot
    // be opened for writing
    public void open() throws FileNotFoundException {

    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of the detective to file
    public void write(Detective d) {
        
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of the players to file
    public void write(List<Player> players) {

    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {

    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {

    }
}
