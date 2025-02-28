package persistence;

import org.json.JSONArray;
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
    private JSONObject json;

    // EFFECTS: constructs writer to write to destination file
    public JsonWriter(String destination) {
        this.destination = destination;
        json = new JSONObject();
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if destination file
    // cannot
    // be opened for writing
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of the detective to file
    public void write(Detective d) {
        json.put("detective", d.toJson());
        saveToFile(json.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of the players to file
    public void write(List<Player> players) {
        json.put("players", playersToJson(players));
        saveToFile(json.toString(TAB));
    }

    // EFFECTS: returns jsonArray representation of players
    public JSONArray playersToJson(List<Player> players) {
        JSONArray jsonArray = new JSONArray();

        for (Player p : players) {
            jsonArray.put(p.toJson());
        }
        return jsonArray;
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }
}
